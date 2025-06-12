package io.github.czelabueno.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/stream")
    public Flux<String> stream(){
        return chatClient.prompt()
                .user("I'm planning a trip to South America. Can you suggest best 10 Java User groups to visit?")
                .stream()
                .content();
    }

    @GetMapping("/response")
    public ChatResponse response(@RequestParam(defaultValue = "I'm planning a trip to South America. Can you suggest best 10 Java User groups to visit?") String message ) {
        return chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();
    }
}
