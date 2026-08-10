package com.charlyken.codingagent;

import java.util.Map;

import org.springaicommunity.agent.tools.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class CodingAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodingAgentApplication.class, args);
	}

    @Bean
	CommandLineRunner commandLineRunner (ChatClient.Builder builder) {
		return args -> {
			String workingDirectory = System.getProperty("user.dir");

			ChatClient client = builder
			.defaultSystem("""
				Tu es un assistant de developpement.
				Tu peux inspecter le projet situé dans : %s

				N'invente jamais le contenu d'un fichier.
				Lis le avant de l'expliquer.
			""".formatted(workingDirectory))
			.defaultTools(
				FileSystemTools.builder().build(),
				GrepTool.builder().build(),
				GlobTool.builder().build(),
				ShellTools.builder().build()

			)
			.build();

			Flux<String> response = client.prompt()
			    .system("Tu es un expert du developpement logiciel")
				.user("Analyse la structure actuelle du projet et fais moi un feedback detaillé max 800 Tokens")
				.toolContext(Map.of("workingDirectory", workingDirectory))
				.stream()
				.content();
			
			response
			    .doOnNext(System.out::print)
				.blockLast();
			
				System.out.println();
				
		};
	}

}
