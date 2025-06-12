package io.github.czelabueno.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @Mock
    ChatModel  chatModel;

    @Captor
    ArgumentCaptor<Prompt> promptCaptor;

    @Test
    void promptChat() {
        // Default ChatResponseMetadata
        ChatResponseMetadata chatResponseMetadata = ChatResponseMetadata.builder().build();

        given(this.chatModel.call(this.promptCaptor.capture()))
                .willReturn(
                        new ChatResponse(List.of(new Generation(new AssistantMessage("Hello Carlos!"))), chatResponseMetadata)
                );

        var chatClient = ChatClient.builder(this.chatModel).build();

        // Simulate a chat response from Mock
        ChatResponse chatResponse = chatClient.prompt().user("My name is Carlos").call().chatResponse();
        // Assert that the response content matches the expected output
        String content = chatResponse.getResult().getOutput().getText();
        // Verify the prompt was captured
        assertThat(content).isEqualTo("Hello Carlos!");
    }

    @Test
    void streamingPromptChat() {
        given(this.chatModel.stream(this.promptCaptor.capture()))
                .willReturn(Flux.generate(
                        () -> new ChatResponse(List.of(new Generation(new AssistantMessage("Hello Carlos!")))), (state, sink) -> {
                            sink.next(state);
                            sink.complete();
                            return state;
                        }));

        var chatClient = ChatClient.builder(this.chatModel).build();
        // Simulate a chat response from Mock
        Flux<ChatResponse> chatResponseFlux = chatClient.prompt().user("My name is Carlo").stream().chatResponse();
        // Assert that the response content matches the expected output
        chatResponseFlux
                .doOnNext(chatResponse -> {
                    String content = chatResponse.getResult().getOutput().getText();
                    // Verify the prompt was captured
                    assertThat(content).isEqualTo("Hello Carlos!");
                })
                .blockLast(); // Block until the Flux completes to ensure all messages are processed
    }

    @Test
    void responseEntityTest() {
        // With ChatResponseMetadata
        ChatResponseMetadata chatResponseMetadata = ChatResponseMetadata.builder().keyValue("key1", "value1").build();

        var ChatResponse = new ChatResponse(List.of(new Generation(new AssistantMessage("""
				{"name":"Carlos", "age":35}
				"""))), chatResponseMetadata);

        given(this.chatModel.call(this.promptCaptor.capture()))
                .willReturn(ChatResponse);

        var chatClient = ChatClient.builder(this.chatModel).build();

        // Simulate a chat response from Mock
        Person person = chatClient.prompt()
                .user("My name is Carlos")
                .call()
                .entity(Person.class);

        assertThat(person).isNotNull();
        assertThat(person.name()).isEqualTo("Carlos");
        assertThat(person.age()).isEqualTo(35);
    }

    record Person(String name, int age){}
}
