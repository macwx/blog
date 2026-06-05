# Reactivity Patterns

## ref vs reactive Decision

- `ref` for primitives (string, number, boolean)—`.value` access
- `ref` also works for objects—sometimes clearer than reactive
- `reactive` for objects when you want direct property access
- Consistency wins: pick one style for your team

## ref Patterns

- Forgot `.value` in script—logs Ref object instead of value
- `.value` in template—unnecessary, auto-unwrapped
- `ref` with object value—still reactive, but `.value` for whole object
- `unref()` helper—returns value if ref, value itself if not

## reactive Patterns

- Reassigning whole object breaks reactivity—`state = newObj` doesn't work
- Destructuring loses reactivity—`const {count} = state` is not reactive
- `toRefs(state)` converts each property to ref—preserves reactivity
- `toRef(state, 'prop')` for single property to ref

## Shallow Reactivity

- `shallowRef` only tracks `.value` changes—nested changes not reactive
- `shallowReactive` only tracks root-level properties
- Use for performance with large objects—when deep reactivity not needed
- `triggerRef(ref)` manually triggers update for shallowRef

## readonly Protection

- `readonly(state)` creates read-only proxy—mutations silently fail
- Props are already readonly—no need to wrap
- Good for exposing state from composables—prevent external mutation
- `isReadonly()` to check if value is readonly

## Raw Values

- `toRaw(proxy)` gets original object from reactive proxy
- Useful for third-party libraries that don't handle Proxy
- Mutations on raw object won't trigger updates
- `markRaw(obj)` prevents object from ever becoming reactive

## Computed Patterns

- Computed with setter: `computed({ get, set })`—for two-way derived state
- Computed is cached—expensive calculations only run when dependencies change
- Avoid side effects in computed—use `watch` or `watchEffect` instead
- Computed ref unwraps in template—same as regular ref

## Effect Cleanup

- `watchEffect` receives cleanup function: `watchEffect((onCleanup) => {})`
- Clean up timers, subscriptions, event listeners
- Cleanup runs before effect re-runs AND on unmount
- Same pattern in `watch` callback—third argument
