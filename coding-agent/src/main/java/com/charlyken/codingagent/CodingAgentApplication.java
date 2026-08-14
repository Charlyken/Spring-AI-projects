package com.charlyken.codingagent;

import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.GlobTool;
import org.springaicommunity.agent.tools.GrepTool;
import org.springaicommunity.agent.tools.ShellTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
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
	CommandLineRunner commandLineRunner(ChatClient.Builder builder) {
		return (var args) -> {
			String workingDirectory = System.getProperty("user.dir");

			ChatMemory chatMemory = MessageWindowChatMemory.builder()
					.maxMessages(15)
					.build(); 

			ChatClient client = builder
					.defaultSystem("""
								Tu es un assistant de developpement.
								Tu peux inspecter le projet situé dans : %s

								N'invente jamais le contenu d'un fichier.
								Lis le avant de l'expliquer.
							""".formatted(workingDirectory)
					)
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

			try (Scanner sc = new Scanner(System.in)) {
				System.out.println("\nAgent prêt. Ecris exit pour quitter.\n");

				while (true) {
					System.out.println("> ");
					String input = sc.nextLine();

					if ("exit".equalsIgnoreCase(input)) {
						break;
					}

					try {
						Flux<String> response = client.prompt()
								.user(input)
								.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "conversation-id"))
								.toolContext(Map.of("workingDirectory", workingDirectory))
								.stream()
								.content();
						System.out.println("\n");
						response
								.doOnNext(System.out::print)
								.blockLast();

						System.out.println();
					} catch (Exception e) {
						System.err.println("Erreur: " + e.getMessage());
					}
				}

			}

		};
	}

}
