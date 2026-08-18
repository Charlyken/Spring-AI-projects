package com.charlyken.codingagent.cli;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.charlyken.codingagent.service.CodingAgentService;

@Component 
public class CodingAgentRunner {
    private final CodingAgentService codingAgentService;

    CodingAgentRunner(CodingAgentService codingAgentService){
        this.codingAgentService =  codingAgentService;
    }

    public void run(){
        Scanner sc = new Scanner(System.in);
        System.out.println("\n Agent prêt. Ecris exit pour quitter.\n");

        while(true){
            System.out.println("> ");
            String input = sc.nextLine();

            if("exit".equalsIgnoreCase(input)) {
                break;
            }

            try {

            } catch (Exception e){
                System.err.println("Erreur: "+e.getMessage());
            }
        }
    }


}
