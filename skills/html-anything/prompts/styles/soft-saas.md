# Soft SaaS Style

Use this style for customer-facing work data: support mailboxes, email
campaigns, onboarding programs, customer success queues, lightweight product
analytics, and workstreams that should feel operational without becoming a
heavy admin dashboard.

## Underlying System: Soft SaaS Console

This is a calm product console made of floating panels on a pale app canvas.
It should feel like a modern SaaS performance screen: precise, friendly,
low-friction, and quietly alive.

Reference-derived invariants:

1. **Pale gray app canvas** with generous gutters and no marketing hero.
2. **White rounded panels** with subtle borders, soft shadows, and almost no
   saturated backgrounds.
3. **Asymmetric dashboard collage**: left identity/source panel, large central
   visual metric surface, right campaign/status panels, lower activity strip.
4. **Small blue pill labels** and tiny status dots instead of loud badges.
5. **Blue / lavender / cyan accents** with one warm yellow highlight for the
   central visual; avoid Clockless orange as the dominant color here.
6. **Dense mini-metrics**: tabular percentages, sparkline cards, micro heatmaps,
   leaderboards, and short rows.
7. **Rounded but disciplined geometry**: 14-20px cards, 8-12px pills, compact
   44px touch controls.
8. **Motion is product-like**: hover lift, linked selection, chart reveal, and
   quiet tooltip transitions. No theatrical scrolling or decorative effects.

## Base Scaffold

The first viewport must be a full app canvas, not a report:

1. `.soft-saas-shell` — whole-page app canvas with a fixed max-width board.
2. `.profile-card` — source identity / dataset owner / inbox summary, with
   small channel chips or participant icons.
3. `.metric-bloom` — the central visual surface: honeycomb, radial heatmap,
   clustered dots, or delivery grid, backed by real data.
4. `.campaign-panel` — right-side status/insight panels with 2x2 metric tiles,
   top action hint, and trend sparklines.
5. `.activity-strip` — bottom strip of mini bars, recent messages/events, or
   journey metrics.
6. `.leaderboard-panel` or `.thread-list` — ranked senders, issues, campaigns,
   queues, or threads.

For narrow screens, collapse into a single column while preserving the app-panel
vocabulary. The central visual should appear before raw rows.

## Component Vocabulary

Use these class names in generated HTML:

- `.soft-saas-shell`
- `.soft-board`
- `.profile-card`
- `.soft-alert`
- `.metric-bloom`
- `.campaign-panel`
- `.metric-tile`
- `.leaderboard-panel`
- `.activity-strip`
- `.delivery-grid`
- `.soft-row`
- `.soft-tooltip`

## Source Translation

Translate source modules into the soft SaaS product language:

- Email archives → inbox health, thread response time, open-loop questions,
  sender/channel mix, busiest hours, support handoffs.
- Campaign or onboarding CSVs → delivered/opened/clicked/converted metrics,
  stage cards, delivery heatmap, cohort leaderboard.
- Issue trackers → queue health, response/aging tiles, owner leaderboard,
  stale-item callouts, weekly activity strip.
- Calendars → meeting load as journey metrics, focus-time strips, collaborator
  cards, busiest-day delivery grid.

The page may include raw records, but raw data belongs in a compact searchable
drawer or list after the product console.

## Interaction Model

- Clicking a sender/thread/campaign highlights the related cells in the bloom,
  updates an inspector, and reveals evidence rows.
- Hovering a cell or mini-bar shows `.soft-tooltip` with readable values.
- Search filters thread/event rows without changing the overall layout.
- Copy/export actions should be compact text buttons or icon buttons, not a
  primary hero CTA.
- All controls need visible focus states and accessible names.

## Visual Language

Suggested token override:

- Canvas: `#f5f7fb`
- Panel: `#ffffff`
- Panel border: `rgba(106, 119, 150, 0.13)`
- Text: `#172033`
- Muted text: `#6b7288`
- Blue: `#5b7cf6`
- Lavender: `#aebcff`
- Cyan: `#65d6ce`
- Pink: `#e978ae`
- Yellow: `#f6c445`

Use Space Grotesk for display and Plus Jakarta Sans for body unless a host
style sheet already defines better product UI fonts.

## Required Modules

- Source identity / profile panel.
- Central data bloom or delivery grid.
- 2x2 metric tile block with trend indicators.
- Leaderboard or thread queue.
- Bottom activity strip or journey metric rail.
- Compact searchable record/evidence area.

## Avoid

- Do not generate a generic `hero + KPI cards + chart cards + table` layout.
- Do not use a dark app, terminal surface, or saturated gradient hero.
- Do not make every section a same-sized dashboard card grid.
- Do not use emoji as primary icons; use small inline SVG or text labels.
- Do not hide chart values behind hover-only interactions.
- Do not claim business performance meaning that is not supported by the data.

## Style Compliance Gate

Before returning HTML, verify:

- Root is `<html ... data-ha-style="soft-saas">`.
- First viewport contains `.soft-saas-shell`, `.profile-card`,
  `.metric-bloom`, `.campaign-panel`, and `.activity-strip`.
- The page visually reads as an airy SaaS app screen with floating white panels,
  blue/lavender accents, compact metrics, and asymmetrical composition.
- At least two interactions are implemented and keyboard accessible.
- Source-required analysis is translated into SaaS panel language rather than
  pasted as generic report sections.
