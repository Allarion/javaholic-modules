# Fluent API Conventions

## Builders

- .of() → manual mode
- .auto() → meta-based mode

All builders must:
- return stage interface
- expose build()
- never return partially constructed component

## Testing

Each fluent component must have:
- Shape test (method chaining)
- Contract test (build result not null)