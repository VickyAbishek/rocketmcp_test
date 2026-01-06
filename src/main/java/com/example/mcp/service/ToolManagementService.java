package com.example.mcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing MCP tool availability.
 * Tracks which tools are enabled/disabled and provides filtered tool lists.
 */
@Service
public class ToolManagementService {

    private static final Logger log = LoggerFactory.getLogger(ToolManagementService.class);

    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    // Stores disabled tool names (by default all tools are enabled)
    private final Set<String> disabledTools = ConcurrentHashMap.newKeySet();

    public ToolManagementService(SyncMcpToolCallbackProvider toolCallbackProvider) {
        this.toolCallbackProvider = toolCallbackProvider;
    }

    @PostConstruct
    public void init() {
        log.info("ToolManagementService initialized with {} tools",
                toolCallbackProvider.getToolCallbacks().length);
    }

    /**
     * Get all tools with their enabled/disabled status.
     */
    public List<ToolStatus> getAllToolsStatus() {
        return Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .map(tool -> {
                    String name = tool.getToolDefinition().name();
                    String description = tool.getToolDefinition().description();
                    boolean enabled = !disabledTools.contains(name);
                    return new ToolStatus(name, description, enabled);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get only enabled tool callbacks (for passing to ChatClient).
     */
    public ToolCallback[] getEnabledToolCallbacks() {
        return Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .filter(tool -> !disabledTools.contains(tool.getToolDefinition().name()))
                .toArray(ToolCallback[]::new);
    }

    /**
     * Enable a tool by name (or suffix).
     */
    public boolean enableTool(String toolName) {
        String fullName = findToolFullName(toolName);
        if (fullName != null) {
            disabledTools.remove(fullName);
            log.info("Enabled tool: {}", fullName);
            return true;
        }
        return false;
    }

    /**
     * Disable a tool by name (or suffix).
     */
    public boolean disableTool(String toolName) {
        String fullName = findToolFullName(toolName);
        if (fullName != null) {
            disabledTools.add(fullName);
            log.info("Disabled tool: {}", fullName);
            return true;
        }
        return false;
    }

    /**
     * Find the full tool name by exact match or suffix match.
     */
    private String findToolFullName(String toolName) {
        for (ToolCallback tool : toolCallbackProvider.getToolCallbacks()) {
            String name = tool.getToolDefinition().name();
            if (name.equals(toolName) || name.endsWith("_" + toolName)) {
                return name;
            }
        }
        return null;
    }

    /**
     * Record representing tool status.
     */
    public record ToolStatus(
            String name,
            String description,
            boolean enabled) {
    }
}
