package com.example.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application that acts as an MCP client with AI chat capabilities.
 * - Connects to MCP servers to access tools
 * - Provides GPT-4o powered chat with tool integration
 * - Web UI for chat and tool management
 */
@SpringBootApplication
public class McpApplication {

    private static final Logger log = LoggerFactory.getLogger(McpApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(McpApplication.class, args);
        log.info("=".repeat(60));
        log.info("MCP AI Chat Application Started!");
        log.info("Open http://localhost:8080 in your browser");
        log.info("=".repeat(60));
    }
}
