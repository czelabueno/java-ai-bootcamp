package io.github.czelabueno.vector;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class VectorStoreController {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    public VectorStoreController(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        List<Document> documents = List.of(
                new Document("Java AI rocks!! Java AI rocks!! Java AI rocks!! Java AI rocks!! Java AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));

        // Add the documents to PGVector
        vectorStore.add(documents);
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/vector-store")
    public List<Document> getVectorStore(@RequestParam(defaultValue = "Spring AI") String message) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(3) // similar number of docs
                        .build()
        );
    }

    @GetMapping("/embedding")
    public Map embedMessage(@RequestParam(defaultValue = "Spring AI") String message) {
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }

}
