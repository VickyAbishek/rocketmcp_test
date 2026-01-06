#!/usr/bin/env python3
"""
Python MCP Server with utility tools.
This server exposes several tools that can be discovered and executed
by any MCP client (including our Spring AI application).

Uses FastMCP from the official MCP Python SDK.
"""

from datetime import datetime
from typing import Annotated
import json

from mcp.server.fastmcp import FastMCP

# Create an MCP server instance
mcp = FastMCP("python-tools")


# =============================================================================
# Tool: Calculator
# =============================================================================
@mcp.tool()
def calculator(
    operation: Annotated[str, "The operation to perform: add, subtract, multiply, divide"],
    a: Annotated[float, "The first number"],
    b: Annotated[float, "The second number"]
) -> str:
    """
    Perform basic arithmetic operations.
    Supports: add, subtract, multiply, divide
    """
    operations = {
        "add": lambda x, y: x + y,
        "subtract": lambda x, y: x - y,
        "multiply": lambda x, y: x * y,
        "divide": lambda x, y: x / y if y != 0 else "Error: Division by zero"
    }
    
    if operation not in operations:
        return json.dumps({
            "error": f"Unknown operation: {operation}. Supported: add, subtract, multiply, divide"
        })
    
    result = operations[operation](a, b)
    return json.dumps({
        "operation": operation,
        "a": a,
        "b": b,
        "result": result
    })


# =============================================================================
# Tool: Weather (Mock)
# =============================================================================
@mcp.tool()
def get_weather(
    city: Annotated[str, "The city to get weather for"]
) -> str:
    """
    Get current weather information for a city.
    Note: This is a mock implementation for demonstration purposes.
    """
    # Mock weather data
    mock_weather = {
        "new york": {"temp": 22, "condition": "Partly Cloudy", "humidity": 65},
        "london": {"temp": 15, "condition": "Rainy", "humidity": 80},
        "tokyo": {"temp": 28, "condition": "Sunny", "humidity": 55},
        "paris": {"temp": 18, "condition": "Overcast", "humidity": 70},
        "sydney": {"temp": 25, "condition": "Clear", "humidity": 50},
    }
    
    city_lower = city.lower()
    
    if city_lower in mock_weather:
        weather = mock_weather[city_lower]
        return json.dumps({
            "city": city,
            "temperature_celsius": weather["temp"],
            "condition": weather["condition"],
            "humidity_percent": weather["humidity"],
            "note": "This is mock data for demonstration"
        })
    else:
        return json.dumps({
            "city": city,
            "temperature_celsius": 20,
            "condition": "Unknown",
            "humidity_percent": 60,
            "note": "City not found in mock data, returning defaults"
        })


# =============================================================================
# Tool: Greeting Generator
# =============================================================================
@mcp.tool()
def generate_greeting(
    name: Annotated[str, "The name of the person to greet"],
    style: Annotated[str, "The greeting style: formal, casual, enthusiastic"] = "casual"
) -> str:
    """
    Generate a personalized greeting message.
    Supports different styles: formal, casual, enthusiastic
    """
    greetings = {
        "formal": f"Dear {name}, I hope this message finds you well.",
        "casual": f"Hey {name}! How's it going?",
        "enthusiastic": f"🎉 Hello {name}! Great to see you! 🎉"
    }
    
    greeting = greetings.get(style, greetings["casual"])
    
    return json.dumps({
        "name": name,
        "style": style,
        "greeting": greeting
    })


# =============================================================================
# Tool: Date/Time Information
# =============================================================================
@mcp.tool()
def get_datetime(
    timezone: Annotated[str, "Timezone identifier (e.g., UTC, local)"] = "local"
) -> str:
    """
    Get current date and time information.
    Returns formatted date, time, and additional info.
    """
    now = datetime.now()
    
    return json.dumps({
        "date": now.strftime("%Y-%m-%d"),
        "time": now.strftime("%H:%M:%S"),
        "day_of_week": now.strftime("%A"),
        "iso_format": now.isoformat(),
        "timestamp": now.timestamp(),
        "timezone": timezone
    })


# =============================================================================
# Tool: Text Utilities
# =============================================================================
@mcp.tool()
def text_utils(
    text: Annotated[str, "The text to process"],
    operation: Annotated[str, "Operation: uppercase, lowercase, reverse, word_count, char_count"]
) -> str:
    """
    Perform various text utility operations.
    Supports: uppercase, lowercase, reverse, word_count, char_count
    """
    operations = {
        "uppercase": lambda t: {"result": t.upper()},
        "lowercase": lambda t: {"result": t.lower()},
        "reverse": lambda t: {"result": t[::-1]},
        "word_count": lambda t: {"result": len(t.split())},
        "char_count": lambda t: {"result": len(t)}
    }
    
    if operation not in operations:
        return json.dumps({
            "error": f"Unknown operation: {operation}",
            "supported": list(operations.keys())
        })
    
    result = operations[operation](text)
    result["operation"] = operation
    result["original_text"] = text
    return json.dumps(result)


# =============================================================================
# Resource: Server Info
# =============================================================================
@mcp.resource("server://info")
def get_server_info() -> str:
    """
    Get information about this MCP server.
    """
    return json.dumps({
        "name": "Python Tools MCP Server",
        "version": "1.0.0",
        "description": "A demo MCP server with utility tools",
        "tools": [
            "calculator",
            "get_weather",
            "generate_greeting",
            "get_datetime",
            "text_utils"
        ]
    })


# =============================================================================
# Main Entry Point
# =============================================================================
if __name__ == "__main__":
    # Run the server using stdio transport
    # This allows the Spring AI MCP client to communicate with this server
    mcp.run()
