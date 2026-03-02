# AgentVerse

## Overview

AgentVerse is an Android application designed to integrate and orchestrate multiple AI agents within a unified architecture. This project provides a scalable foundation for building AI-powered mobile experiences using modular clean architecture.

---

## Architecture & Modules

AgentVerse is structured for production-level scalability and maintainability. The architecture separates UI, orchestration, agents, tools, and model connectors into distinct modules:

```
app
ui-common
core
agent
api-integration
api-integration-common
data
```

### Module Responsibilities

- **app**: Android entry layer. Handles navigation, dependency injection (Hilt), configuration, and ViewModels. Connects UI to core logic. No business or orchestration logic.
- **ui-common**: Reusable UI components and design system (themes, buttons, chat bubbles, etc.).
- **core**: The brain. Contains the agent orchestrator, context manager, tool registry, and base interfaces for extensibility.
- **agent**: Individual agent implementations (e.g., ChatAgent, CodeAgent). Each agent is modular and uses core and API integrations.
- **api-integration**: Model connectors for external AI services (OpenAI, Gemini, Claude, local LLMs). Handles networking and authentication.
- **api-integration-common**: Shared DTOs, base models, error handling, and API contracts.
- **data**: Storage layer for chat history, caching, tokens, and user sessions. Organized into local, remote, and repository submodules.

---