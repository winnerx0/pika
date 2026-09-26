package com.winnerx0.pika.taxact;

import com.winnerx0.pika.shared.dto.ApiResponse;
import com.winnerx0.pika.taxact.dto.QuestionRequest;
import com.winnerx0.pika.taxact.dto.RecordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxActServiceImpl implements TaxActService{

    private final EmbeddingModel embeddingModel;
    private final TaxActRepository taxActRepository;
    private final TaxActIndexer taxActIndexer;
    private final PgVectorStore vectorStore;
    private final ChatClient chatClient;


    @Override
    public ApiResponse<?> uploadDocument(RecordRequest recordRequest) throws IOException {

//        String text = new String(recordRequest.getDocument().getBytes(), StandardCharsets.UTF_8);

        Resource resource = new InputStreamResource(recordRequest.getDocument().getInputStream());

        // read tax law pdfs
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMaxNumChunks(1000)
                .withMinChunkLengthToEmbed(5)
                .withMinChunkSizeChars(350)
                .build();

        List<Document> chunks = splitter.apply(reader.get());

        taxActIndexer.index(chunks);
        return new ApiResponse<>("Record uploaded successfully", null);
    }

    @Override
    public ApiResponse<String> ask(QuestionRequest questionRequest) {

        vectorStore.similaritySearch(questionRequest.getQuestion());

        String response = chatClient.prompt()
                .user(questionRequest.getQuestion())
                .call()
                .content();
        return new ApiResponse<>("Response retrieved successfully", response);
    }
}
