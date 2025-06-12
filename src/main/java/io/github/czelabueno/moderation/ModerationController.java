package io.github.czelabueno.moderation;

import org.springframework.ai.moderation.ModerationModel;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.ai.moderation.ModerationResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ModerationController {

    private final ModerationModel moderationModel;


    public ModerationController(ModerationModel moderationModel) {
        this.moderationModel = moderationModel;
    }

    @GetMapping("/moderation")
    public ModerationResponse moderation(@RequestParam(defaultValue = "I want to kill them..") String message) {
        ModerationPrompt prompt = new ModerationPrompt(message);
        return moderationModel.call(prompt);
    }

}
