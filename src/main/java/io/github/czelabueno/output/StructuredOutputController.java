package io.github.czelabueno.output;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StructuredOutputController {

    private final ChatClient chatClient;

    public StructuredOutputController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/output/unstructured")
    public String unstructured() {
        return chatClient.prompt()
                .user("What is the good plan to visit Lima for 5 days?")
                .call()
                .content();
    }

    @GetMapping("/output/structured")
    public Itinerary structured() {
        return chatClient.prompt()
                .user("What is the good plan to visit Lima for 5 days?")
                .call()
                .entity(Itinerary.class);
    }

    record Activity(String activity, String location, String day, String time) {}
    record Itinerary(List<Activity> activities) {}
}
