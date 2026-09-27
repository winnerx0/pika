package com.winnerx0.pika.config;

import com.winnerx0.pika.messages.HybridDocumentRetriever;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

    @Bean
    public DocumentRetriever documentRetriever(VectorStore vectorStore){

        return VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .build();
    }

    @Bean
    public Advisor ragAdvisor(HybridDocumentRetriever hybridDocumentRetriever){
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(hybridDocumentRetriever)
                .build();
    }
}
