# Skill Publishing Guide

Use this before writing or remixing a skill for Taste.

## A Taste Skill Must Be Self-Contained

Do not publish a skill that says “go read another skill”.

If you remix from an existing skill:

- copy the required steps into the new skill
- keep the new skill runnable on its own
- mark provenance with remix comments only

Example:

```markdown
<!-- remixed from [[@pnt/firecrawl-mcp]] -->
```

## Required File

Every published skill folder must contain `SKILL.md` with YAML frontmatter:

```markdown
---
name: firecrawl-mcp
description: Install and configure Firecrawl MCP for web extraction.
---

# Firecrawl MCP
```

Requirements:

- `name` must be lowercase kebab-case
- `description` must say what the skill does and when to use it
- the body should contain the real steps, not just a summary

## Good Skill Structure

```text
my-skill/
├── SKILL.md
├── references/
├── templates/
├── scripts/
└── examples/
```

Use extra files when they help execution:

- `references/` for deeper docs or caveats
- `templates/` for reusable files
- `scripts/` for helper automation
- `examples/` for worked examples

## Publish Checklist

- the frontmatter is valid
- the skill works without opening another skill
- secrets are described but not hard-coded
- file paths are relative and portable
- remix markers exist when you borrowed material
- tags are short and useful

## After Publish

Publishing does not install the skill locally by itself.

If the user wants to use it immediately:

```bash
taste save @your-handle/your-skill
```
