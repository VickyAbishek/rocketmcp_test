# MCP Integration Project

A Spring AI MCP client that connects to Python MCP servers using the **built-in** `SyncMcpToolCallbackProvider`.

## Project Structure

```
├── pom.xml                           # Spring AI 1.0.0 dependencies
├── src/main/java/com/example/mcp/
│   └── McpApplication.java           # Uses built-in SyncMcpToolCallbackProvider
├── src/main/resources/
│   └── application.yml               # MCP server connections
└── mcp_server/
    ├── requirements.txt              # Python MCP dependencies
    └── server.py                     # Python MCP server with 5 tools
```

## How It Works

Spring AI auto-configures `SyncMcpToolCallbackProvider` which gives you access to all MCP tools:

```java
@Autowired
private SyncMcpToolCallbackProvider toolCallbackProvider;

ToolCallback[] tools = toolCallbackProvider.getToolCallbacks();
```

## Run

```bash
mvn spring-boot:run
```

On startup, you'll see all available tools from connected MCP servers.

## Available Python Tools

- **calculator** - Basic arithmetic (add, subtract, multiply, divide)
- **get_weather** - Mock weather data
- **generate_greeting** - Personalized greetings
- **get_datetime** - Current date/time info
- **text_utils** - Text operations (uppercase, reverse, etc.)

## Add More MCP Servers

Edit `application.yml`:

```yaml
spring.ai.mcp.client.stdio.connections:
  another-server:
    command: node
    args: ["/path/to/server.js"]
```