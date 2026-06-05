# Component Patterns

## Props Best Practices

- Type your props: `defineProps<{msg: string, count?: number}>()`
- Required props have no `?`—TypeScript enforces
- Default values: `withDefaults(defineProps<Props>(), {count: 0})`
- Props are readonly—never mutate, emit event to parent instead

## Emit Patterns

- Type your emits: `defineEmits<{(e: 'update', value: string): void}>()`
- Emit returns boolean if event handler returns false
- Vue 3.3+ shorthand: `defineEmits<{update: [value: string]}>()`
- Always emit for changes—maintain one-way data flow

## v-model Patterns

- `v-model` = `:modelValue` + `@update:modelValue`
- Multiple v-model: `v-model:title`, `v-model:content`—different props
- `defineModel()` (3.4+) creates ref that syncs automatically—much simpler
- v-model modifiers: `.trim`, `.number`, `.lazy`—access via `modelModifiers`

## Slots Patterns

- Default slot: `<slot></slot>`—or `<slot>fallback content</slot>`
- Named slots: `<slot name="header">` + `<template #header>`
- Scoped slots: `<slot :item="item">` + `<template #default="{item}">`
- `$slots.header?.()` to check if slot provided

## Dynamic Components

- `<component :is="componentName">`—switch between components
- `<KeepAlive>` preserves state when switching—cached in memory
- `include`/`exclude` props on KeepAlive—control which are cached
- `max` prop limits cache size—LRU eviction

## Async Components

- `defineAsyncComponent(() => import('./Comp.vue'))`—lazy loading
- Loading component: `loadingComponent` option—shown while loading
- Error component: `errorComponent` option—shown on load failure
- Combine with `<Suspense>` for coordinated loading states

## Teleport

- `<Teleport to="body">`—render content outside component tree
- Useful for modals, dropdowns, tooltips—need to escape overflow/z-index
- `disabled` prop for conditional teleporting
- Events still bubble in Vue component tree—not DOM tree

## Provide/Inject

- `provide('key', value)` in ancestor—available to all descendants
- `inject('key', defaultValue)` to receive
- Provide reactive value for reactive inject—`provide('count', ref(0))`
- Symbol keys for type safety—avoid string collisions
