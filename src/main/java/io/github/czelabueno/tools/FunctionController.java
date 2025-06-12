package io.github.czelabueno.tools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunctionController {

    private final ChatClient chatClient;

    public FunctionController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/date")
    public String dater() {
        return chatClient.prompt("What day is tomorrow?")
                .call()
                .content();
    }

    @GetMapping("/date/tool")
    public String daterTool() {
        return chatClient.prompt("What day is tomorrow?")
                .tools(new DateTools())
                .call()
                .content();
    }
}
