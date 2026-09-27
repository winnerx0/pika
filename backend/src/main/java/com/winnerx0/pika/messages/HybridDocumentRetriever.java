package com.winnerx0.pika.messages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HybridDocumentRetriever implements DocumentRetriever {

    private static final int RRF_K = 10;

    private final KeywordSearchRepository keywordSearchRepository;

    private final VectorStore vectorStore;

    @Override
    public List<Document> retrieve(Query query) {

        String text = query.text();

        List<Document> vectorResults =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(text)
                                .topK(20)
                                .build()
                );

        List<Document> keywordResults =
                keywordSearchRepository.search(text, 20);

        return reciprocalRankFusion(
                vectorResults,
                keywordResults,
                10
        );
    }

    private List<Document> reciprocalRankFusion(
            List<Document> vectorResults,
            List<Document> keywordResults,
            int finalLimit
    ) {

        Map<String, Double> scores = new HashMap<>();
        Map<String, Document> documents = new HashMap<>();

        addRanking(vectorResults, scores, documents);
        addRanking(keywordResults, scores, documents);

        return scores.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Double>comparingByValue()
                                .reversed()
                )
                .limit(finalLimit)
                .map(entry -> {
                    Document document =
                            documents.get(entry.getKey());

                    return document.mutate()
                            .score(entry.getValue())
                            .build();
                })
                .toList();
    }

    private void addRanking(
            List<Document> results,
            Map<String, Double> scores,
            Map<String, Document> documents
    ) {

        for (int i = 0; i < results.size(); i++) {

            Document document = results.get(i);

            int rank = i + 1;

            double rrfScore = 1.0 / (RRF_K + rank);

            scores.merge(
                    document.getId(),
                    rrfScore,
                    Double::sum
            );

            documents.putIfAbsent(
                    document.getId(),
                    document
            );
        }
    }
}
