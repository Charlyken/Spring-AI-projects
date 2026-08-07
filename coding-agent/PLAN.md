# Réalisation guidée de coding-agent

## Résumé

Construire progressivement un agent CLI inspiré du tutoriel, mais avec des versions stables actuelles : Spring Boot 4.0.7, Java 25, Spring AI 2.0.0, Agent Utils 0.10.0 et Claude Haiku 4.5 (claude-haiku-4-5-20251001). Références :
Agent Utils (https://github.com/spring-ai-community/spring-ai-agent-utils/releases/tag/v0.10.0), modèles Anthropic (https://platform.claude.com/docs/en/about-claude/models/overview).

À chaque étape : explication → implémentation par l’utilisateur → revue → test → validation avant de continuer.

## Étapes de réalisation

1. Préparer Git
    - Retirer uniquement coding-agent de .gitignore.
    - Vérifier que le dossier est vide et que les autres changements existants restent intacts.

2. Initialiser le projet
    - Maven, Spring Boot 4.0.7, Java 25.
    - Groupe dev.learning, artefact coding-agent, package dev.learning.codingagent.
    - Dépendances initiales : Anthropic Claude et Spring Boot Test.
    - Valider avec ./mvnw test.

3. Configurer le modèle
    - Ajouter Agent Utils 0.10.0 au pom.xml.
    - Créer application.yaml.
    - Lire la clé depuis ${ANTHROPIC_API_KEY} uniquement.
    - Configurer Claude Haiku 4.5 sans enregistrer le secret.
    - Vérifier le démarrage de l’application.

4. Créer un chatbot CLI minimal
    - Construire un ChatClient.
    - Ajouter un prompt système simple.
    - Créer une boucle Scanner avec la commande exit.
    - Premier test manuel : question simple sans outil.

5. Ajouter les outils progressivement
    - Commencer par GlobTool et GrepTool.
    - Ajouter ensuite FileSystemTools.
    - Limiter impérativement FileSystemTools au chemin canonique de coding-agent/ avec allowedDirectory.
    - Tester lecture autorisée et accès extérieur refusé.

6. Comprendre la boucle agentique
    - Activer les journaux de tool calling.
    - Tester une demande nécessitant plusieurs actions : rechercher une classe, la lire, puis l’expliquer.
    - Observer la séquence décision → outil → résultat → nouvelle décision.

7. Ajouter la mémoire
    - Utiliser MessageWindowChatMemory avec une fenêtre de 50 messages.
    - Fixer un identifiant de conversation CLI.
    - Vérifier qu’une question de suivi comprend une référence au message précédent.

8. Ajouter un skill
    - Créer .claude/skills/spring-boot-java/SKILL.md.
    - Utiliser un frontmatter valide : name, description, allowed-tools.
    - Enregistrer SkillsTool.
    - Comparer un prompt sans déclenchement et un prompt créant un contrôleur Spring.

9. Refactorer après le MVP
    - Garder Application comme point d’entrée.
    - Extraire la configuration du ChatClient et des outils.
    - Extraire un service CodingAgentService exposant String ask(String prompt).
    - Extraire la boucle dans CodingAgentRunner.
    - Aucun endpoint HTTP : l’interface publique reste la CLI.

10. Ajouter ShellTools en dernier
    - Le rendre désactivé par défaut et activable par un profil shell.
    - Tester uniquement dans un conteneur Docker jetable contenant une copie non inscriptible depuis l’hôte.
    - N’y transmettre qu’une clé Anthropic temporaire ; aucun token Git ou autre secret.
    - Commencer par pwd et ./mvnw test, jamais par une commande destructive.

## Tests et critères de réussite

- ./mvnw test fonctionne sans appel réel au LLM.
- L’application refuse de démarrer proprement si la clé requise manque.
- Les outils fichiers ne peuvent pas sortir de coding-agent/, y compris via .. ou un lien symbolique.
- La CLI répond, conserve le contexte et quitte avec exit.
- Le skill est chargé uniquement pour une demande pertinente.
- Le test réel final permet à l’agent d’inspecter le projet et de créer un contrôleur conforme.
- ShellTools reste absent du profil par défaut.

## Hypothèses et limites

- Le projet sera versionné dans le dépôt.
- La clé n’est actuellement pas présente dans le shell ; elle devra être exportée avant les essais réels.
- RAG, MCP, sous-agents, mémoire persistante, interface Web et déploiement sont hors de ce premier projet.
- Les réponses du LLM sont non déterministes : les tests automatisés couvriront la configuration, la CLI et les frontières des outils ; les scénarios LLM resteront des tests manuels.
