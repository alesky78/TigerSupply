---
agent: 'agent'
description: Build or maintain a project's data dictionary based on the current conversation.
tools: ['vscode', 'execute', 'read', 'edit', 'search', 'agent', 'todo', 'web']
---


# Data Dictionary
  
Build and sharpen the project's data dictionary as you design based on the current session.

## Goal
The objective is to be able to map business concepts to code artifacts, UI, DB, config, and relationships in a way that reduces inference during agent research.

## Rules
- **Purpose:** the data dictionary is only a reduction map that links business concepts to code artifacts.
- **Inclusion:** an entry may enter the dictionary only when the business concept is hard to map to code by its name.
- **Exclusion:** when the mapping between the business concept and its code artifact, UI, DB, config, or relationship is 1-to-1 and obvious, it must not be included.
- **Document only the WHAT (the mapping), never the HOW (the behavior).**
  - **WHAT (allowed):** the term → its concrete artifact (Code / DB / UI / Config) and cardinality. This is the sole purpose of an entry.
  - **HOW (forbidden in an entry):** derivation logic, computation, routing, validation, orchestration, or "used to / used for" purpose. These are *rules/behavior* — they belong in documentation, not in the dictionary.
  - **Test:** if removing the "how/why" text still leaves a usable term→artifact pointer, the entry is correct. If the entry has *no* value once the "how/why" is removed, it was a rule masquerading as a mapping → drop it.

## File structure

The dictionary lives in `documentation/data-dictionary/`. The repo has multiple dictionaries organized by context:

```
/
├── documentation/
│   └── data-dictionary/
│        ├── dictionary-<context>.md
│        └── dictionary-<context>.md
```

`dictionary-map.md` exists at the root, the map helps to point to where each one lives:

```
/
├── documentation/
│   └── data-dictionary/
│        ├── dictionary-map.md
│        ├── dictionary-<context>.md
│        └── dictionary-<context>.md
```

## Format and rules

There are two kinds of files, each with its own format and rules:

1. **`dictionary-map.md`** — the single index that lists every context dictionary and their cross-context relationships.
2. **`dictionary-<context>.md`** — one file per context, holding the actual term-to-artifact entries.

---

### dictionary-map.md

The root index. One per repo. Points to each context dictionary and describes how the contexts relate.

#### rules
- **Two sections only.** `## Dictionary` lists the context files; `## Relationships` describes how contexts connect. Nothing else.
- **`## Dictionary`.** One bullet per context: a link to its `dictionary-<context>.md` file followed by ` — ` and a short business description of what the context covers.
- **`## Relationships`.** One bullet per cross-context link. State the direction with an arrow (`→` one-way, `↔` shared) and describe how the contexts connect (events consumed/emitted, shared types).
- **Keep it thin.** The map only routes to the right file; term-to-artifact detail lives in each `dictionary-<context>.md`, never here.

#### file format
```md
## Dictionary

- [Ordering](./documentation/data-dictionary/dictionary-ordering.md) — receives and tracks customer orders
- [Billing](./documentation/data-dictionary/dictionary-billing.md) — generates invoices and processes payments
- [Fulfillment](./documentation/data-dictionary/dictionary-fulfillment.md) — manages warehouse picking and shipping

## Relationships

- **Ordering → Fulfillment**: Ordering emits `OrderPlaced` events; Fulfillment consumes them to start picking
- **Fulfillment → Billing**: Fulfillment emits `ShipmentDispatched` events; Billing consumes them to generate invoices
- **Ordering ↔ Billing**: Shared types for `CustomerId` and `Money`
```


---

### dictionary-{context}.md

One file per context. Holds the term-to-artifact entries for that context.

#### rules
- Each entry is a block of ordered fields. Only the fields relevant to the term are present, always in this order:
  - **Code:** Java artifacts — class, interface, service, method, controller, handler.
  - **UI:** front-end artifacts — JSP page, JS file.
  - **DB:** database tables and columns.
  - **Config:** configuration artifacts — Spring Batch XML file, SQL procedure, properties key.
  - **Relationship:** cardinality between entities.
  - **Doc:** link to related documentation.
  - **Not:** the wrong, misleading, or deprecated name for *this same* concept — a negative alias. If an agent meets it, it still points here, but it must never be used as the term (e.g. Purchase, transaction).
- **Be opinionated.** When several names exist for one concept, lead the header with the best one. Keep genuine synonyms an agent may meet in the code/UI/DB as aliases in the header (separated by `/`) so any of them routes to this entry; put under `**Not:**` only the names that are wrong, misleading, or deprecated.
- **Keep definitions tight.** One or two sentences max. Define what it IS (the mapping), not what it does (the behavior). The description is a **noun phrase** naming what the term *is as a mapping target*. **Ban behavior verbs**: *derived from, used to, resolves, filters, routes, drives, triggers, validates, computes*. Their presence signals HOW → drop that text from the entry.
- **Group entries into subsections.** Under `## Language`, cluster related entries beneath `###` subsection headings that follow the context's natural structure (e.g. a lifecycle stage, a functional area, or a screen group). Order the subsections to follow that structure so an agent can scan the flow top to bottom. Keep the subsection titles short and business-oriented.

#### file format
```md

## Title
{One or two sentence description of what this context is and why it exists.}

## Language

### {subsection -> a lifecycle stage or functional area that groups the entries below it}

**{definition -> the preferred term first, then genuine synonyms separated by `/`}**
{the description -> one or two sentence noun phrase}
- **Code:** ...
- **UI:** ...
- **DB:** ...
- **Config:** ...
- **Relationship:** ...
- **Doc:** ...
- **Not:** ...

### {next subsection -> the following stage/area in the context's natural order}

**{next definition}**
{the description}
- **Code:** ...
```



