package io.github.czelabueno.prompt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JournalistController {

    private final ChatClient chatClient;

    public JournalistController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/post/new")
    public String newPost(@RequestParam String topic) {
        var system = """
                You are a journalist. This is your post generator guidelines:
                
                1. Length & Purpose: Generate 100-word blog posts that inform and engage general audiences.
                2. Structure:
                   - Introduction: Hook readers and establish the topic's relevance
                   - Body: Develop 3 main points with supporting evidence and examples
                   - Conclusion: Summarize key takeaways and include a call-to-action
                3. Tone & Style: Use a friendly, conversational tone with a touch of humor.
                4. Response Format: Deliver complete, ready-to-publish posts with a suggested title.
                """;
        return chatClient.prompt()
                .system(system)
                .user(user -> {
                    user.text("Generate a blog post about {topic}");
                    user.param("topic", topic);
                })
                .call()
                .content();
    }
}
