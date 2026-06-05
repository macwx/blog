# Digital E-Guide Style

Use this style when the brief asks for an **e-guide**, digital guide, PDF
guide, lead magnet, creator guide, playbook, ebook, lookbook, downloadable
course preview, or "电子指南 / 电子书". It is especially good for turning a
long report, article, tutorial, or strategy memo into a polished guide preview
that feels shareable.

This style is modeled on a two-page digital guide spread. It must feel like an
elegant PDF preview on a warm desk: paper pages, serif display type, a compact
table of contents, a lesson spread, pull quote, steps, and exercise strip. It
is not a dashboard, app shell, or generic document review.

## Underlying System: Digital E-Guide Spread

The first viewport is the product. Build the page around a two-page guide
preview, then layer utility controls around it.

Base scaffold:

1. **Guide desk** - `<body>` or the main shell is a warm tinted backdrop with
   two paper pages centered in the first viewport. Use `.eguide-desk` and a
   two-column `.spread-pair` when there is room; stack pages on mobile.
2. **Cover page** - the first `.guide-page.cover-page` has an uppercase mono
   eyebrow, oversized serif title, italic accent word, author/source byline,
   3-cell stats row, "What's inside" section, compact 2-column TOC with
   leader dots, a subtle sticker/dot, and footer folio.
3. **Inside spread** - the second `.guide-page.inside-spread` has chapter
   eyebrow, editorial chapter title, deck paragraph, 2-column body or content
   + step list, pinned pull quote, exercise / action strip, and footer folio.
4. **Source utility layer** - search, copy, evidence, raw source, or chapter
   controls are allowed, but they must be small: a mono `TOOLS` tab, bottom
   tray, or side drawer. They must never replace the two-page preview.
5. **Chapter interaction** - TOC rows and page controls update the inside
   spread. Use small vanilla JS; keep the cover stable while the right page
   changes.

Component vocabulary:

- Required primitives: `.eguide-desk`, `.spread-pair`, `.guide-page`,
  `.cover-page`, `.inside-spread`, `.eguide-eyebrow`, `.guide-title`,
  `.stat-row`, `.toc`, `.toc-item`, `.guide-sticker`, `.chapter-title`,
  `.guide-columns`, `.lesson-steps`, `.pullquote`, `.exercise-strip`,
  `.guide-footer`, `.guide-tools`, `.source-drawer`.
- Use mono only for labels, page numbers, stats, TOC numbers, and tool chrome.
- Use serif display for the cover title, chapter title, pull quote, and
  editorial section headings.

## Visual Language

Match the reference closely:

- Backdrop: warm rose / taupe, around `#d8c8c0`, with very soft radial light.
- Paper: `#faf3ea` and `#f4ecdf`; ink: `#1f1c14`; muted text: `#837964`;
  rules: `#d3c9b3`; accent red/orange: `#c44a47` and `#e07d52`.
- Pages: paper-tone cards, small radius, page-sized proportions, 6px-ish
  soft shadow, slight opposing rotations on desktop (about `-0.6deg` and
  `0.6deg`). Keep pages flat and printed, not glassy.
- Typography: large serif display hierarchy with one italic flourish per page.
  Use Georgia / Iowan Old Style / Cormorant-like stacks for display; use a
  quieter serif for body; use system mono for metadata.
- Rules and leader dots carry structure. Avoid colorful chart blocks unless
  the source truly needs them; then render them as printed mini-figures.

## Required Modules

- Cover title and subtitle derived from the source or brief.
- Three concise stats such as pages, reading time, chapters, records, or
  lessons.
- A 5-8 item table of contents.
- One active inside spread that changes when the TOC is clicked.
- Pull quote or short evidence excerpt with source label.
- Numbered steps, principles, or actions distilled from the source.
- Exercise / prompt / "try this" strip that makes the guide useful.
- Search or evidence drawer backed by the inlined `DATA`.
- Copy summary or copy current chapter action when useful.

## Interaction Model

- Clicking TOC rows changes the inside spread and active folio.
- Previous / next controls may page through chapters.
- Search highlights matching TOC entries or opens the evidence drawer.
- Evidence buttons reveal short source excerpts, not a full raw dump as the
  main experience.
- Respect `prefers-reduced-motion`; page changes can gently fade/slide but
  must remain usable without motion.

## Avoid

- Top navigation bars, app headers, command palettes, dashboard KPI grids, or
  a generic `hero + cards + charts + table` layout.
- Long continuous article pages as the primary surface.
- Dense raw-data tables on the first viewport.
- Overly glossy glassmorphism, bright gradients, neon palettes, or dark-only
  surfaces.
- Treating the reference as just a beige palette. The two-page spread,
  cover/inside contrast, serif/mono hierarchy, TOC, pull quote, and exercise
  strip are the style.

## Implementation Notes

- Use a complete `<!doctype html>` document with inline CSS and JS.
- Root must include `data-ha-style="digital-eguide"`.
- Preserve a stable paper aspect ratio with responsive constraints. On mobile,
  stack pages but keep the cover first and the chapter controls visible.
- If source-specific prompts require charts, citations, or drill-down, place
  them inside printed mini-figures, side notes, or the source drawer rather
  than converting the page into a dashboard.
