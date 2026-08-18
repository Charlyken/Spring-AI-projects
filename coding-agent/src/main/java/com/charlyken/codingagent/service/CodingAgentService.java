package com.charlyken.codingagent.service;

import reactor.core.publisher.Flux;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CodingAgentService {
    private final ChatClient chatClient;
    private final String workingDirectory;

    public CodingAgentService(ChatClient chatClient, 
        @Value("${coding-agent.working-dir:${user.dir}}") String workingDirectory
    ) {
        this.chatClient = chatClient;
        this.workingDirectory = workingDirectory;
    }

    public Flux<String> streamResponse(String input) {
        return chatClient.prompt()
                    .user(input)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "conversation-id"))
                    .toolContext(Map.of("workingDirectory", workingDirectory))
                    .stream()
                    .content();
    }
}
