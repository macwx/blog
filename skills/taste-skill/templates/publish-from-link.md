# Remix From Link

Use this checklist when a user sends a link and you want to turn it into a Taste skill.

## 1. Find relevant source skills

```bash
taste steal <link>
taste skill @handle/name
```

## 2. Build a new self-contained skill

- Create a new folder.
- Start from `templates/post.md`.
- Copy in the steps you actually need.
- Keep it runnable without opening another skill.

## 3. Mark provenance

Inside `SKILL.md`, add remix comments near the imported sections:

```markdown
<!-- remixed from [[@pnt/firecrawl-mcp]] -->
```

## 4. Publish

```bash
taste publish ./your-skill --tags scraping,automation
```

If this publish came from a previous `taste steal`, include the steal id when you have it:

```bash
taste publish ./your-skill --origin steal --steal-id 42
```

## 5. Offer installation

After publish succeeds:

```bash
taste save @your-handle/your-skill
```
