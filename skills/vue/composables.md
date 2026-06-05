# Composables Design

## Composable Philosophy

- Composables are functions that encapsulate stateful logic
- Name with `use` prefix—`useMouse`, `useAuth`, `useFetch`
- Return refs/reactive, not plain values—caller needs reactivity
- Composables are like custom hooks in React

## Basic Structure

```javascript
export function useCounter(initial = 0) {
  const count = ref(initial)
  const increment = () => count.value++
  const decrement = () => count.value--
  return { count, increment, decrement }
}
```

## Input Flexibility

- Accept ref OR plain value as input—support both use cases
- `toValue()` (3.3+) unwraps ref or returns value—flexible API
- For reactive objects, consider accepting getter function
- Document what inputs are accepted

## Return Value Patterns

- Return object with named properties—allows destructuring what's needed
- Return refs, not reactive—easier to destructure
- Return methods along with state—complete API
- Consider returning readonly for state—prevent external mutation

## Lifecycle in Composables

- `onMounted`, `onUnmounted` work inside composables—attached to calling component
- Cleanup with `onUnmounted`—unsubscribe, clear timers
- Composable must be called synchronously in setup—not in callbacks
- Multiple components using same composable = separate instances

## Side Effects

- Start effects in composable—they run when component mounts
- Clean up in `onUnmounted`—or use cleanup function in watchEffect
- Consider `onScopeDispose` for effect scope cleanup
- Return stop functions for manual control

## Async Composables

- Return reactive state that updates when async completes
- Include loading and error states—`{data, isLoading, error}`
- Consider `useFetch` pattern from VueUse
- For SSR, handle hydration mismatch

## Composition Patterns

- Composables can use other composables—build on abstractions
- Avoid deep nesting—keep composables focused
- Share types between composables—consistent API
- Consider extracting to separate packages—if truly reusable

## VueUse Reference

- Rich composable library—200+ utilities
- Study patterns in VueUse—excellent examples
- Don't reinvent—check if VueUse has it
- Extend VueUse composables rather than replacing
