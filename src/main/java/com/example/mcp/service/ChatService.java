package com.example.mcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for AI chat functionality using GPT-4o.
 * Integrates with MCP tools based on enabled/disabled status.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient.Builder chatClientBuilder;
    private final ToolManagementService toolManagementService;

    public ChatService(ChatClient.Builder chatClientBuilder,
            ToolManagementService toolManagementService) {
        this.chatClientBuilder = chatClientBuilder;
        this.toolManagementService = toolManagementService;
    }

    /**
     * Process a chat message and return AI response.
     * Only enabled tools are made available to the AI.
     */
    public ChatResult chat(String userMessage) {
        log.info("Processing chat message: {}", userMessage);

        // Get only enabled tools
        ToolCallback[] enabledTools = toolManagementService.getEnabledToolCallbacks();
        log.info("Available enabled tools: {}", enabledTools.length);

        try {
            // Build chat client with enabled tools
            ChatClient chatClient = chatClientBuilder.build();

            String response;
            List<String> toolsUsed = new ArrayList<>();

            if (enabledTools.length > 0) {
                // Call with tools - use toolCallbacks() for ToolCallback objects
                response = chatClient.prompt()
                        .user(userMessage)
                        .toolCallbacks(enabledTools)
                        .call()
                        .content();

                // Note: In a more complete implementation, we'd track which tools were called
                // For now, we indicate tools were available
            } else {
                // No tools enabled, just chat
                response = chatClient.prompt()
                        .user(userMessage)
                        .call()
                        .content();
            }

            log.info("Chat response generated successfully");
            return new ChatResult(true, response, toolsUsed, null);

        } catch (Exception e) {
            log.error("Chat error: {}", e.getMessage(), e);
            return new ChatResult(false, null, List.of(), e.getMessage());
        }
    }

    /**
     * Record representing chat result.
     */
    public record ChatResult(
            boolean success,
            String response,
            List<String> toolsUsed,
            String error) {
    }
}
