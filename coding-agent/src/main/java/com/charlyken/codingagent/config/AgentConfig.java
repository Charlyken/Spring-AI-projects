package com.charlyken.codingagent.config;

import java.nio.file.Path;

import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.GlobTool;
import org.springaicommunity.agent.tools.GrepTool;
import org.springaicommunity.agent.tools.ShellTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AgentConfig {
    private static final String SYSTEM_PROMPT = """
           Tu es un assistant de developpement.
			Tu peux inspecter le projet situé dans : %s

			N'invente jamais le contenu d'un fichier.
			Lis le avant de l'expliquer.
           """;
           
    @Value("${coding-agent.working-dir:${user.dir}}")       
    private String workingDirectory;   

    @Bean
    public ChatMemory chatMemory (){
        return MessageWindowChatMemory.builder()
                        .maxMessages(15)
                        .build();
    }

    @Bean
    public ChatClient client (ChatClient.Builder builder, ChatMemory chatMemory) {
       return builder.defaultSystem(SYSTEM_PROMPT.formatted(workingDirectory))
                    .defaultTools(
                        FileSystemTools.builder()
                                    .allowedDirectories(Path.of(workingDirectory))
                                    .build(),
                        GrepTool.builder().build(),
                        GlobTool.builder().build(),
                        ShellTools.builder().build()            

                    )
                    .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                        .build()
                    )
                    .build();
               
    }
    
}
