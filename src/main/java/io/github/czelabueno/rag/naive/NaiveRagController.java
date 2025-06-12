package io.github.czelabueno.rag.naive;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NaiveRagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public NaiveRagController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/rag")
    public String rag(@RequestParam(defaultValue = "in which movie Anakin fall in the dark side?") String question) {
        return chatClient.prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(3).build())
                        .build())
                .user(u -> u.text(question))
                .call()
                .content();
    }

    @GetMapping("/naive-rag")
    public String naive(@RequestParam(defaultValue = "in which movie Anakin fall in the dark side?") String question) {
        return chatClient.prompt()
                .advisors(RetrievalAugmentationAdvisor.builder()
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .similarityThreshold(0.5d)
                                .topK(3)
                                .build())
                        .build())
                .user(u -> u.text(question))
                .call()
                .content();
    }
}
