package com.winnerx0.pika.taxact;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxActIndexer {

    private final VectorStore vectorStore;

    public void index(List<Document> documents){
        vectorStore.add(documents);
    }
}
