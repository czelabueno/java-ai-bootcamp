package io.github.czelabueno.multimodal.audio;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioController {

    private final SpeechModel speechModel;
    private final ChatClient chatClient;

    public AudioController(SpeechModel speechModel, ChatClient.Builder builder) {
        this.speechModel = speechModel;
        this.chatClient = builder.build();
    }

    @GetMapping("/speech")
    public ResponseEntity<byte[]> generateSpeech(@RequestParam(defaultValue = "It's a good time to learn about AI") String text) {

        var options = OpenAiAudioSpeechOptions.builder()
                .model("tts-1-hd")
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f)
                .build();

        SpeechPrompt prompt = new SpeechPrompt(text, options);
        SpeechResponse response = speechModel.call(prompt);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.mp3\"")
                .body(response.getResult().getOutput());
    }

    @GetMapping("/polyglot")
    public ResponseEntity<byte[]> polyglot(@RequestParam(defaultValue = "It's a good time to learn about AI") String text, @RequestParam(defaultValue = "en") String language) {
        var system = """
                You are a multilingual AI assistant.
                You can speak multiple languages fluently.
                
                Translate the user's message into following language: {language}
                """;

        String translated = chatClient.prompt()
                .system(s -> {
                    s.text(system);
                    s.param("language", language);
                })
                .user(text)
                .call()
                .content();

        var options = OpenAiAudioSpeechOptions.builder()
                .model("tts-1-hd")
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f)
                .build();

        SpeechPrompt prompt = new SpeechPrompt(translated, options);
        SpeechResponse response = speechModel.call(prompt);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech-" + language + ".mp3\"")
                .body(response.getResult().getOutput());
    }
}
