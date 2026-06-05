# Performance Optimization

## Render Performance

- Vue tracks dependencies automatically—only re-renders what changes
- Keys on `v-for` are critical—use stable, unique ID, not array index
- `v-if` vs `v-show`: if unmounts, show toggles CSS—show better for frequent toggle
- `v-once` for static content—renders once, never updates

## Computed Optimization

- Computed values are cached—use for expensive derivations
- Computed only recalculates when dependencies change
- Avoid side effects in computed—can't rely on when they run
- Chain computed values—each caches independently

## Component Optimization

- Define components outside `<script setup>`—or use `defineComponent`
- Avoid inline handlers creating new functions: `:click="() => handle(item)"`
- For large lists, use virtual scrolling—vue-virtual-scroller
- Code split with `defineAsyncComponent`—load on demand

## Props Stability

- Object/array props create new reference each render
- Use `computed` or `toRef` for derived props—stable references
- `shallowRef` for large objects not deeply reactive
- `v-memo` for memoizing parts of template

## v-memo

- `v-memo="[dependency]"` caches template fragment
- Re-renders only when dependency changes
- Useful for large lists with selectable items
- More granular than component-level memoization

## List Optimization

- `key` must be stable and unique—not array index if list reorders
- `key` change triggers full component recreation—use for forced re-render
- Virtual scrolling for 1000+ items—don't render off-screen
- Paginate if possible—show 50, load more on demand

## Async Components

- `defineAsyncComponent` for lazy loading—reduces initial bundle
- Route-level code splitting—each route loads its components
- `<Suspense>` for coordinated async loading—show loading state
- Prefetch likely-needed components—on hover or visibility

## DevTools Profiling

- Vue DevTools has component inspector—see props, state
- Performance timeline shows render time
- Highlight updates option—visualize what re-renders
- Production build for accurate performance—dev mode adds overhead

## Build Optimization

- Tree-shaking removes unused code—don't import entire libraries
- `vue` runtime only build—smaller if not using template compiler
- `vite build` handles most optimizations automatically
- Analyze bundle: `vite-plugin-visualizer`—find heavy dependencies
