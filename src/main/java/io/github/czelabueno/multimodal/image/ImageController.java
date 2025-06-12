package io.github.czelabueno.multimodal.image;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.*;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    private final ChatClient chatClient;
    @Value("classpath:images/java-developer.png")
    Resource image;

    private final ImageModel imageModel;

    public ImageController(ChatClient.Builder builder, OpenAiImageModel imageModel) {
        this.chatClient = builder.build();
        this.imageModel = imageModel;
    }

    @GetMapping("/image-to-text")
    public String detection() {
        return chatClient.prompt()
                .user(u -> u
                        .text("Explain me what you see in the following image?")
                        .media(MimeTypeUtils.IMAGE_PNG, image)
                )
                .call()
                .content();
    }

    @GetMapping("/text-to-image")
    public Image generation(@RequestParam(defaultValue = "A cute cat") String prompt) {
        ImageOptions options = OpenAiImageOptions.builder()
                .model("dall-e-3")
                .width(1024)
                .height(1024)
                .quality("hd")
                .style("natural")
                .build();
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);

        ImageResponse imageResponse = imageModel.call(imagePrompt);
        return imageResponse.getResult().getOutput();
    }
}
