package com.example.mcp.controller;

import com.example.mcp.service.ChatService;
import com.example.mcp.service.ChatService.ChatResult;
import com.example.mcp.service.ToolManagementService;
import com.example.mcp.service.ToolManagementService.ToolStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for chat and tool management.
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;
    private final ToolManagementService toolManagementService;

    public ChatController(ChatService chatService, ToolManagementService toolManagementService) {
        this.chatService = chatService;
        this.toolManagementService = toolManagementService;
    }

    // ==================== Chat Endpoints ====================

    /**
     * Send a chat message and get AI response.
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResult> chat(@RequestBody ChatRequest request) {
        log.info("Chat request: {}", request.message());
        ChatResult result = chatService.chat(request.message());

        if (result.success()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ==================== Tool Management Endpoints ====================

    /**
     * Get all available tools with their enabled/disabled status.
     */
    @GetMapping("/tools")
    public ResponseEntity<List<ToolStatus>> getTools() {
        return ResponseEntity.ok(toolManagementService.getAllToolsStatus());
    }

    /**
     * Enable a tool.
     */
    @PostMapping("/tools/{toolName}/enable")
    public ResponseEntity<Map<String, Object>> enableTool(@PathVariable String toolName) {
        boolean success = toolManagementService.enableTool(toolName);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "tool", toolName,
                "action", "enabled"));
    }

    /**
     * Disable a tool.
     */
    @PostMapping("/tools/{toolName}/disable")
    public ResponseEntity<Map<String, Object>> disableTool(@PathVariable String toolName) {
        boolean success = toolManagementService.disableTool(toolName);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "tool", toolName,
                "action", "disabled"));
    }

    // ==================== Request/Response Records ====================

    public record ChatRequest(String message) {
    }
}
