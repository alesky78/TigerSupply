## Purpose

Defines the engine's reusable, game-agnostic finite-state-machine framework: how a machine advances
between named states in response to computed events, threads a shared context to its states, halts on
final states, and notifies states when they are entered — usable by any game feature without the
engine referencing a concrete game type.

## ADDED Requirements

### Requirement: One transition per tick

The state machine SHALL, on each tick, run the current state exactly once to compute an event, then
select the next state from that event, advancing at most one transition per tick.

#### Scenario: A single tick advances one edge

- **WHEN** the machine is ticked while in a non-final state
- **THEN** the current state computes exactly one event and the machine adopts the single next state
  selected for that event

#### Scenario: Self-transition keeps the machine in place

- **WHEN** a state computes an event whose configured target is the same state
- **THEN** the machine remains in that state after the tick

### Requirement: Declarative transition table

The state machine SHALL resolve the next state from a declarative table keyed on the pair of the
current state and the computed event, configured by declaring transitions rather than hand-written
branching. Resolving a pair that has no configured target SHALL raise a distinct error, and resolving
from a state unknown to the table SHALL raise a distinct error.

#### Scenario: Declared transition resolves the next state

- **WHEN** a transition from a state for a given event has been declared to a target state
- **AND** the machine, in that state, computes that event
- **THEN** the machine advances to the declared target state

#### Scenario: Unknown event for the current state is rejected

- **WHEN** the current state computes an event for which no transition has been declared from that state
- **THEN** the machine raises an unsupported-event error

#### Scenario: Unknown state is rejected

- **WHEN** the machine is asked to resolve a transition from a state the table does not recognize
- **THEN** the machine raises an unsupported-state error

### Requirement: Final states halt the machine

A state SHALL be declarable as final. When the machine's current state is final, ticking the machine
SHALL have no effect, and the machine SHALL report that it has reached a final state.

#### Scenario: Reaching a final state stops progression

- **WHEN** the machine transitions into a state declared final
- **THEN** subsequent ticks make no further transitions

#### Scenario: Final state is observable

- **WHEN** the machine's current state is final
- **THEN** a query for whether the machine is in a final state returns true

#### Scenario: Non-final state is observable

- **WHEN** the machine's current state is not final
- **THEN** a query for whether the machine is in a final state returns false

### Requirement: On-enter lifecycle hook

The state machine SHALL notify a state that it has been entered whenever a tick changes the current
state to that state, so a state can initialize per-entry data. Remaining in the same state across a
tick SHALL NOT re-trigger the entered notification.

#### Scenario: Entering a state triggers its on-enter hook

- **WHEN** a tick changes the current state from one state to a different state
- **THEN** the newly current state's on-enter hook is invoked with the shared context

#### Scenario: Staying in a state does not re-trigger on-enter

- **WHEN** a tick leaves the machine in the same state it was already in
- **THEN** that state's on-enter hook is not invoked

### Requirement: Shared typed context threaded to states

The state machine SHALL hold a single shared context and provide it to each state when the state
computes its event and when the state is entered, so states read and update shared data through that
context rather than each holding its own injected copy.

#### Scenario: States receive the shared context

- **WHEN** the machine runs the current state to compute an event
- **THEN** the state is given the machine's shared context to read and update

### Requirement: Framework is game-agnostic

The state-machine framework SHALL NOT reference any concrete game type, so any game feature can reuse
it by supplying its own context type, states, and transition table.

#### Scenario: No concrete game reference in the framework

- **WHEN** the state-machine framework is inspected for game-specific types
- **THEN** no concrete game type is referenced by the framework
