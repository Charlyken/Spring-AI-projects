package com.charlyken.codingagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CodingAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodingAgentApplication.class, args);
	}

    @Bean
	CommandLineRunner commandLineRunner (ChatClient.Builder builder) {
		return args -> {
			ChatClient client = builder.build();

			String response = client.prompt()
		           .system("Tu es un expert Java")
			       .user("Eplique simplement ce qu'est une injection de dependance, je veux une réponse courte")
				   .call()
				   .content();
			
			System.out.println(response);	   

		};
	}

}
