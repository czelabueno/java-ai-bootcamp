package io.github.czelabueno.rag.advanced;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdvancedRagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public AdvancedRagController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/advanced-rag/rewrite")
    public String advancedRag(@RequestParam(defaultValue = "in which movie Anakin fall in the dark side?") String question) {
        return chatClient.prompt()
                .advisors(RetrievalAugmentationAdvisor.builder()
                        .queryTransformers(RewriteQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate())
                                .targetSearchSystem("vector store")
                                .build())
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .similarityThreshold(0.50)
                                .vectorStore(vectorStore)
                                .topK(3)
                                .build())
                        .build())
                .user(u -> u.text(question))
                .call()
                .content();
    }

    @GetMapping("/advanced-rag/multiquery")
    public String advancedRagMultiQuery(@RequestParam(defaultValue = "in which movie Anakin fall in the dark side?") String question) {
        return chatClient.prompt()
                .advisors(RetrievalAugmentationAdvisor.builder()
                        .queryExpander(MultiQueryExpander.builder()
                                .chatClientBuilder(chatClient.mutate())
                                .numberOfQueries(3)
                                .build())
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .similarityThreshold(0.50)
                                .vectorStore(vectorStore)
                                .topK(3)
                                .build())
                        .documentJoiner(new ConcatenationDocumentJoiner())
                        .build())
                .user(u -> u.text(question))
                .call()
                .content();
    }

    @GetMapping("/advanced-rag/translation")
    public String advancedRagTranslation(@RequestParam(defaultValue = "in which movie Anakin fall in the dark side?") String question) {
        return chatClient.prompt()
                .advisors(RetrievalAugmentationAdvisor.builder()
                        .queryTransformers(TranslationQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate())
                                .targetLanguage("english")
                                .build())
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .similarityThreshold(0.50)
                                .vectorStore(vectorStore)
                                .topK(3)
                                .build())
                        .documentJoiner(new ConcatenationDocumentJoiner())
                        .build())
                .user(u -> u.text(question))
                .call()
                .content();
    }



}
