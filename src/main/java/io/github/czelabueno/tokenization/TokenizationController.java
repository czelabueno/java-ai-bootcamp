package io.github.czelabueno.tokenization;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenizationController {

    private final ChatClient chatClient;

    public TokenizationController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/tokenization")
    public String tokenize(@RequestParam(defaultValue = "Tell me a fun fact about Java") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/tokenization/my-response")
    public MyResponse tokenizationResponse(@RequestParam(defaultValue = "Tell me a fun fact about Java") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .entity(MyResponse.class);
    }

    record MyResponse(String prompt, String completion, Usage usage) {
        record Usage(int promptTokens, int completionTokens, int totalTokens) {
        }
    }

}
