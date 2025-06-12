package io.github.czelabueno.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class IngestionPipeline {

    private final VectorStore vectorStore;

    public IngestionPipeline(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    void run() {
        // Bring my own data
        MarkdownDocumentReader reader = new MarkdownDocumentReader(
                "classpath:documents/star-wars-movies.md",
                MarkdownDocumentReaderConfig.builder()
                        .withAdditionalMetadata("movies", "star-wars")
                        .build());
        List<Document> documents = reader.get();
        vectorStore.add(new TokenTextSplitter().split(documents));
    }

}
