# Possibile miglioria — payload sugli eventi consegnato agli stati

> **Nota sulla lingua.** Pagina in **italiano**, coerente con il resto del sottosistema
> [enemy-spawn-lifecycle](index.md).
>
> **Stato: proposta, NON pianificata.** Questa pagina congela una discussione di design: non è
> stata implementata e non esiste (ancora) una OpenSpec change. È un promemoria di "cosa
> potremmo migliorare e perché", da trasformare in una change quando sarà il momento. Nulla qui
> descrive il comportamento attuale del codice.

## Indice

1. [In una riga](#1-in-una-riga)
2. [Il problema: `waitTime` viaggia in un canale laterale](#2-il-problema-waittime-viaggia-in-un-canale-laterale)
3. [L'idea: l'Event come corriere del payload](#3-lidea-levent-come-corriere-del-payload)
4. [Rivede una decisione registrata](#4-rivede-una-decisione-registrata)
5. [Spazio di design](#5-spazio-di-design)
6. [Sottigliezze e trabocchetti](#6-sottigliezze-e-trabocchetti)
7. [Superficie d'impatto](#7-superficie-dimpatto)
8. [Inclinazione attuale (da discutere)](#8-inclinazione-attuale-da-discutere)
9. [Prossimi passi](#9-prossimi-passi)

---

## 1. In una riga

Oggi l'unico evento che porta un dato — `hordeTimed`, con il suo `time` in secondi — **non** lo
porta davvero sull'[`Event`](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/statemachine/Event.java):
il valore viaggia in un *side-channel* attraverso il contesto condiviso, legato all'evento solo per
convenzione (un confronto sul nome). L'idea è far sì che sia l'`Event` a portare il payload e che la
macchina lo **consegni allo stato entrante** in `onEnter`, rendendo esplicito un contratto oggi
implicito e aprendo la strada a eventi parametrici futuri.

---

## 2. Il problema: `waitTime` viaggia in un canale laterale

L'evento `hordeTimed` è l'unico dei tre eventi di completamento ondata che trasporta un attributo
(vedi la tabella `GenerateEvent` in [index.md §3.2](index.md#32-generateevent-evento-di-completamento)).
Ma quel valore non è sull'`Event`: percorre una catena di copie.

```
   level-1.xml  time="2"
        |
        v
  GenerateEvent.time  (String)
        |
        v   [StateSpawningHorde.process -> context.spawnNextHorde()]
  HordeSpawner.createHordeEvent()
        |  if name == hordeTimed:  currentWaitTime = parseFloat(time)   <-- stash in un campo
        |  return new Event("hordeTimed")   <-- l'Event porta SOLO il nome
        v
  EnemySpawnContext.spawnNextHorde()
        |  if name == hordeTimed:  waitTime = hordeSpawner.getCurrentWaitTime()  <-- ricopia
        v
  [transizione hordeTimed:  spawningHorde --> awaitingTimer]
        |
        v
  StateAwaitingTimer.internalProcess():  elapsedTime > context.waitTime ?
```

Il punto dolente è l'**accoppiamento implicito**: tre punti diversi devono sapere del payload e
concordare sulla convenzione "vale solo se il nome è `hordeTimed`":

| Chi | File | Cosa fa del payload |
|---|---|---|
| Produttore | [HordeSpawner.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/HordeSpawner.java) | Campo `currentWaitTime`, il getter `getCurrentWaitTime()`, il parse condizionale in `createHordeEvent()`. |
| Ponte | [EnemySpawnContext.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/EnemySpawnContext.java) | Lo stash condizionale in `spawnNextHorde()` (copia dal getter al campo `waitTime`). |
| Consumatore | [StateAwaitingTimer.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/StateAwaitingTimer.java) | Legge `context.waitTime`. |

Lo stato che **emette** l'evento (`spawningHorde`) e lo stato che **consuma** il dato
(`awaitingTimer`) sono diversi: l'evento è il messaggero naturale tra i due, ma oggi il messaggero è
il contesto (blackboard), non l'evento.

---

## 3. L'idea: l'Event come corriere del payload

Far portare all'`Event` i suoi attributi e, quando
[StateMachineImpl.tick()](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/statemachine/StateMachineImpl.java)
cambia stato, passare l'evento scatenante allo stato entrante:

```
  HordeSpawner.createHordeEvent()
        |  Event e = new Event("hordeTimed");  e.<payload> = 2f      <-- payload sull'evento
        v
  StateMachineImpl.tick()
        |  if (next != state)  next.onEnter(context, event)          <-- consegna l'evento
        v
  StateAwaitingTimer.onEnter(ctx, event):  ctx.waitTime = event.<payload>
```

Confronto dell'accoppiamento, prima/dopo:

```
  OGGI                                  PROPOSTO
  ----                                  --------
  time --> HordeSpawner.field           time --> Event.<payload>
        --> [getter] --> Context.waitTime            |  (nasce sull'evento, in 1 punto)
        --> StateAwaitingTimer                        v
  3 punti conoscono il payload          onEnter(ctx, evt): ctx.waitTime = evt.<payload>
  legame evento<->dato = solo il nome   legame evento<->dato = il payload/tipo
```

Beneficio principale: il dato **nasce e si attacca all'evento in un punto solo** (`createHordeEvent`)
e arriva esattamente a chi serve, quando serve. Spariscono il campo/getter su `HordeSpawner` e lo
stash condizionale in `EnemySpawnContext`.

---

## 4. Rivede una decisione registrata

Questa idea **inverte deliberatamente** una scelta presa nel change archiviato
[honor-horde-wait-time/design.md](../../../openspec/changes/archive/2026-08-30-honor-horde-wait-time/design.md),
che aveva valutato esattamente questa alternativa e deciso il contrario:

> *"Carry the delay on the shared context, not on the `Event`. Chosen over adding a payload to
> `Event` because `Event` is game-agnostic engine code and the triggering event is not delivered to
> the next state anyway."*

…mettendo tra i **Non-Goals**: *"No change to the engine `Event` type or the state-machine
contract."*

Ma quello stesso design **nomina la limitazione** che qui vogliamo rimuovere:

> *"`Event` carries only a name, and neither `onEnter` nor `internalProcess` receives the triggering
> event — so a state cannot read the event that activated it."*

Quindi non è una contraddizione: la decisione di allora era razionale *dato che* l'evento non arriva
allo stato. Questa proposta rimuove proprio quel presupposto, aggiungendo un secondo motivo per
rivedere il vincolo.

---

## 5. Spazio di design

Conviene separare **due decisioni indipendenti**.

### Decisione 1 — consegna: `onEnter` riceve l'evento scatenante?

Cambiamento *engine*, piccolo e genuinamente generico ("quale evento mi ha portato qui"). Modo
retro-compatibile via *default method*, così non si toccano tutti gli stati insieme:

```java
// State<C>  (engine)
default void onEnter(C context) { }                 // resta com'è
default void onEnter(C context, Event trigger) {    // nuovo overload
    onEnter(context);                               // default: ignora l'evento
}
```

```java
// StateMachineImpl.tick()
if (next != state) {
    next.onEnter(context, event);   // passa l'evento che ha causato la transizione
}
```

Effetto sul modulo `game`: **solo `StateAwaitingTimer` deve cambiare**; gli altri quattro stati
ereditano il default e restano intatti.

### Decisione 2 — rappresentazione del payload

| Opzione | Cosa | Pro | Contro |
|---|---|---|---|
| **A. Bag sull'`Event` engine** | `Event` con `Map<String,Object>` / `getAttribute(k)` | Dinamico, flessibile; aggiungi parametri senza toccare firme | Non tipizzato (chiave-stringa + cast); aggiunge superficie all'engine agnostico |
| **B. Sottoclasse tipizzata nel `game`** | `HordeTimedEvent extends Event { float waitTime }` | Type-safe; l'`Event` engine resta un puro segnale; estensibile (un evento = una classe) | Serve `instanceof`/cast nello stato; meno "dinamico" |
| **C. Azione sull'arco (sapore Mealy)** | Un'azione `(ctx, evt)` associata alla transizione nella `TransitionTable` | Sia `State` sia (quasi) l'`Event` engine restano invariati; la logica vive sull'arco, dove appartiene | Introduce un concetto nuovo (azioni d'arco) nell'engine; richiede comunque un payload sull'evento (la Decisione 2 non sparisce) |

Nota: le opzioni **A** e **B** condividono la Decisione 1 (evento consegnato a `onEnter`) e
differiscono solo su *tipizzato vs dinamico*. L'opzione **C** consegna il dato all'arco invece che
allo stato, quindi lascia `State`/`onEnter` invariati ma aggiunge il concetto di azione d'arco.

Sketch dell'opzione **B** lato produttore e consumatore:

```java
// HordeSpawner.createHordeEvent()  -- spariscono currentWaitTime, il getter, e lo stash nel context
return new HordeTimedEvent(Float.parseFloat(desc.getTime().trim()));   // name = "hordeTimed"
```

```java
// StateAwaitingTimer
@Override
public void onEnter(EnemySpawnContext ctx, Event trigger) {
    ctx.resetElapsedTime();
    if (trigger instanceof HordeTimedEvent hte) {
        ctx.setWaitTime(hte.getWaitTime());
    }
}
```

Proprietà utile di B: `HordeTimedEvent.getName()` ritorna `"hordeTimed"`, quindi la
[TransitionTable](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/statemachine/TransitionTable.java)
continua a instradare per nome **senza modifiche**. Solo la *consegna* del dato cambia.

---

## 6. Sottigliezze e trabocchetti

- **`onEnter` NON scatta sui self-loop.**
  [StateMachineImpl.tick()](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/statemachine/StateMachineImpl.java)
  salta la notifica quando `next == state`. Oggi `hordeTimed` è una transizione vera
  (`spawningHorde -> awaitingTimer`), quindi ok; ma un futuro evento con payload mappato su un
  self-loop perderebbe silenziosamente il dato. Da documentare nel contratto.
- **Lo stato iniziale non riceve `onEnter`.** In
  [EnemyManager.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/entity/EnemyManager.java)
  la macchina parte con `setState(awaitingTimer)`, e `setState` non chiama `onEnter`. Perciò al
  primo giro `awaitingTimer` gira **senza** un evento scatenante: il default `waitTime = 1` in
  [EnemySpawnContext.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/EnemySpawnContext.java)
  deve **restare**. La proposta non svuota il context: sostituisce solo il *canale* con cui
  `waitTime` viene alimentato dopo la prima ondata.
- **La transition table instrada solo per nome** — il payload è ortogonale al routing. È corretto,
  ma va scritto nero su bianco affinché nessuno pensi che il payload influenzi le transizioni.
- **Gli stati sono singleton stateless riusati** (commento in `EnemyManager.initComponents()`). Il
  payload consegnato in `onEnter` va depositato nel **context** (`ctx.waitTime`), non in un campo
  dello stato, per non introdurre stato mutabile su un singleton.

---

## 7. Superficie d'impatto

Se/quando diventerà una change, l'impatto atteso è:

| Area | Elementi | Nota |
|---|---|---|
| Engine — codice | [State.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/statemachine/State.java), [StateMachineImpl.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/statemachine/StateMachineImpl.java) (+ `Event`/`TransitionTable` secondo l'opzione) | Overload `onEnter(C, Event)`; consegna nell'`tick()`. |
| Game — codice | [StateAwaitingTimer.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/StateAwaitingTimer.java), [HordeSpawner.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/HordeSpawner.java), [EnemySpawnContext.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/EnemySpawnContext.java) (+ eventuale `HordeTimedEvent` in opzione B) | Un solo stato cambia; sparisce il side-channel. |
| OpenSpec — spec | [engine-state-machine/spec.md](../../../openspec/specs/engine-state-machine/spec.md) (requisiti *On-enter lifecycle hook* e *Shared typed context threaded to states*), [enemy-spawn-lifecycle/spec.md](../../../openspec/specs/enemy-spawn-lifecycle/spec.md) | Il contratto dell'hook di ingresso si allarga. |
| Docs | Questa pagina + [index.md](index.md), [motore-macchina-a-stati.md](motore-macchina-a-stati.md), [sequenziamento-horde.md](sequenziamento-horde.md) | Aggiornare quando implementata. |

---

## 8. Inclinazione attuale (da discutere)

- Il guadagno reale **non** è "risparmiare un campo": è rendere **esplicito il contratto** ed
  eliminare il side-channel.
- Tra le rappresentazioni, l'inclinazione è verso **B (sottoclasse tipizzata)**: tiene l'engine
  `Event` puro e game-agnostic, dà type-safety invece di chiavi-stringa + cast, ed è comunque
  estensibile. Il **bag (A)** serve davvero solo se il payload ha forma *dinamica a runtime*; nel
  dominio attuale (eventi di horde a forma nota, scritti nell'XML) siamo nel caso "estensibile a
  design-time", che B copre bene. **C** è concettualmente elegante (Mealy pulito) ma aggiunge un
  concetto nuovo all'engine: allargare un hook esistente pesa meno che introdurne uno nuovo.
- Contrappunto onesto: oggi `hordeTimed` è l'**unico** evento con payload, quindi si sta
  generalizzando un'infrastruttura per un singolo call-site. Il valore cresce solo se si prevedono
  altri eventi parametrici (es. spawn con conteggio, boss con soglie, drop). Da pesare contro il
  ripple sullo spec dell'engine.

---

## 9. Prossimi passi

Quando sarà il momento di eseguire:

1. Trasformare questa pagina in una **OpenSpec change** (proposal + design), registrando la scelta
   A/B/C e il fatto che rivede il non-goal di honor-horde-wait-time.
2. Aggiornare gli spec [engine-state-machine](../../../openspec/specs/engine-state-machine/spec.md)
   ed [enemy-spawn-lifecycle](../../../openspec/specs/enemy-spawn-lifecycle/spec.md).
3. Implementare la Decisione 1 (consegna dell'evento a `onEnter`) e la Decisione 2 (rappresentazione
   scelta), quindi aggiornare le pagine di dettaglio del sottosistema.
