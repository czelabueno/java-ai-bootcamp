package io.github.czelabueno.vector;

import io.github.czelabueno.TestApp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestApp.class)
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".*")

public class PGVectorStoreIT {

    @Autowired
    VectorStore vectorStore;

    List<Document> documents = List.of(
            Document.builder()
                    .id(UUID.randomUUID().toString())
                    .text("""
                        Episode II: Attack of clones: it centers on an older Anakin (played now by Hayden Christensen) 
                        and Obi-Wan (played once again by Ewan McGregor) as they discover new and terrifying threats in the galaxy, 
                        which in turn leads up to the Clone Wars
                        """)
                    .metadata("trilogy", "1")
                    .metadata("starring", "3")
                    .build(),
            Document.builder()
                    .id(UUID.randomUUID().toString())
                    .text("""
                        Episode III: Revenge of the Sith: it centers on Anakin's fall to the dark side, 
                        Obi-Wan's attempt to stop him, and the rise of the Galactic Empire.
                        """)
                    .metadata("trilogy", "1")
                    .metadata("starring", "4")
                    .build(),
            Document.builder()
                    .id(UUID.randomUUID().toString())
                    .text("""
                        Episode IV: A New Hope: it centers on Luke Skywalker, Princess Leia, and Han Solo as they fight against the Galactic Empire.
                        """)
                    .metadata("trilogy", "2")
                    .metadata("starring", "1")
                    .build()
    );

    @BeforeEach
    void setUp() {
        this.vectorStore.add(documents);
    }

    @AfterEach
    void tearDown() {
        this.vectorStore.delete(documents.stream().map(Document::getId).toList());
    }

    @Test
    void testVectorStore() {
        // Retrieve relevant documents
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("Anakin's fall to the dark side")
                        .topK(1)
                        .build());

        // Assert that the results are not empty and contain the expected document
        assertThat(results).hasSize(1);
        Document result = results.get(0);
        assertThat(result.getText()).contains("Episode III: Revenge of the Sit");
        assertThat(result.getMetadata()).containsEntry("trilogy", "1");
    }
}
