# Sequenziamento delle ondate (modulo game)

> **Indice correlato**: [Ciclo di vita dello spawn dei nemici](index.md)

> **Modulo:** `game` — **esempio implementativo** costruito sul framework
> [`engine.statemachine`](motore-macchina-a-stati.md). Questa pagina mostra come i cinque stati
> concreti di TigerSupply usano quella macchina a stati generica per sequenziare le ondate di un
> livello.

## Indice

1. [Contesto](#1-contesto)
2. [Descrizione dei componenti](#2-descrizione-dei-componenti)
3. [Flusso dati](#3-flusso-dati)
4. [Punti di integrazione](#4-punti-di-integrazione)
5. [Stato dell'engine toccato](#5-stato-dellengine-toccato)

---

## 1. Contesto

### Scopo
Sequenziare le ondate del livello: decidere **quando** generare l'ondata successiva (dopo un tempo
o dopo aver ripulito lo schermo), gestire il boss e riconoscere la **fine del livello**. Tutto
questo istanziando la macchina a stati generica dell'engine come
`StateMachine<EnemySpawnContext>`.

### Obiettivo
Portare la macchina da `waitTime` (attesa iniziale) fino allo stato finale `bossKilledFinal`,
attraversando le ondate dichiarate in [level-1.xml](../../../game/src/main/resources/level/level-1.xml).

### Trigger
Il **frame update** della scena di gioco. `LevelScene.update(...)` chiama
`enemyManager.updateEntity(deltaTimeSeconds)`, che a sua volta fa un tick della macchina a stati.

### Concetti locali
- **`elapsedTime`**: secondi accumulati nello stato di attesa corrente (`float`, stessa base
  temporale del frame update).
- **`waitTime`**: ritardo da rispettare in `StateWaitTime`. Vale `1` prima della primissima ondata,
  poi è sovrascritto dal `time` dichiarato da ogni ondata `waitTime`.
- **Ondata "time-gated"**: un'ondata il cui `generateEvent` è `waitTime`; il suo `time` è
  obbligatorio e validato al caricamento (vedi [caricamento-dati-livello.md](caricamento-dati-livello.md)).

---

## 2. Descrizione dei componenti

### Il cablaggio (dove engine e game si incontrano)

`EnemyManager.initComponents()` è il **punto di composizione**: costruisce gli stati concreti,
dichiara la tabella delle transizioni e avvia la macchina generica dell'engine.

| Componente | Modulo | Classe | Responsabilità |
|---|---|---|---|
| Cablaggio | `game` | `EnemyManager` | Costruisce stati + `TransitionTable`, imposta contesto e stato iniziale, fa il tick. |
| Contesto | `game` | `EnemySpawnContext` | Il `C` della macchina: tempo, ritardo, delega a `HordeSpawner`. |
| Coordinatore | `game` | `HordeSpawner` | Genera l'ondata corrente e ne restituisce l'`Event`. |

Estratto da [EnemyManager.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/entity/EnemyManager.java):

```java
// costruisci gli stati una volta (senza stato interno, riusati come singleton)
StateWaitTime      waitTime      = new StateWaitTime();
StateWaitKill      waitKill      = new StateWaitKill();
StateGenerateHorde generateHorde = new StateGenerateHorde();
StateKillBoss      killBoss      = new StateKillBoss();
StateBossKilled    bossKilled    = new StateBossKilled();

TransitionTable<EnemySpawnContext> table = new TransitionTable<EnemySpawnContext>();
table.selfLoop(waitTime, GameResources.EVENT_WAIT);
table.add(waitTime, GameResources.EVENT_NEW_HORDE, generateHorde);
table.selfLoop(waitKill, GameResources.EVENT_WAIT);
table.add(waitKill, GameResources.EVENT_NEW_HORDE, generateHorde);
table.add(generateHorde, GameResources.EVENT_WAIT_TIME,      waitTime);
table.add(generateHorde, GameResources.EVENT_WAIT_KILL,      waitKill);
table.add(generateHorde, GameResources.EVENT_BOSS_GENERATED, killBoss);
table.selfLoop(killBoss, GameResources.EVENT_WAIT);
table.add(killBoss, GameResources.EVENT_BOSS_KILLED, bossKilled);

stateMachine = new StateMachineImpl<EnemySpawnContext>();
stateMachine.setTransitionTable(table);
stateMachine.setContext(dataModel);   // dataModel = EnemySpawnContext
stateMachine.setState(waitTime);      // stato iniziale
```

### I cinque stati concreti

| Stato | Nome (`GameResources`) | Evento prodotto | Comportamento |
|---|---|---|---|
| [`StateWaitTime`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/StateWaitTime.java) | `waitTime` | `newHorde` se `elapsedTime > waitTime`, altrimenti `wait` | In `onEnter` azzera il timer; conta i secondi. |
| [`StateWaitKill`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/StateWaitKill.java) | `waitKill` | `newHorde` se lo schermo è ripulito, altrimenti `wait` | Attende che tutti i nemici siano morti. |
| [`StateGenerateHorde`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/StateGenerateHorde.java) | `generateHorde` | l'evento dell'ondata: `waitTime` \| `waitKill` \| `bossGenerated` | Genera l'ondata e ne emette l'evento di completamento. |
| [`StateKillBoss`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/StateKillBoss.java) | `killBoss` | `bossKilled` se il boss è morto, altrimenti `wait` | Attende l'uccisione del boss. |
| [`StateBossKilled`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/StateBossKilled.java) | `bossKilledFinal` | *(nessuno: `isFinal()` → `true`)* | Terminale: livello vinto, la macchina si ferma. |

> **Perché due stati di attesa distinti?** `waitTime` è temporizzato (ideale per ondate ravvicinate
> e coreografate), `waitKill` è a "schermo pulito" (obbligatorio per il boss e per i colli di
> bottiglia). Lo stato `generateHorde` sceglie a quale tornare in base al `generateEvent` **della
> stessa ondata appena generata**.

---

## 3. Flusso dati

```mermaid
sequenceDiagram
    participant LS as LevelScene (game)
    participant EM as EnemyManager (game)
    participant SM as StateMachine (engine)
    participant CTX as EnemySpawnContext (game)
    participant HS as HordeSpawner (game)

    LS->>EM: updateEntity(delta)
    EM->>CTX: increaseElapsedTime(delta)
    EM->>SM: tick()  (un tick)
    SM->>SM: state.process(context)
    alt stato = StateWaitTime e elapsedTime > waitTime
        SM->>SM: Event(newHorde) -> next = generateHorde
    else stato = StateGenerateHorde
        SM->>CTX: spawnNextHorde()
        CTX->>HS: spawnNextHorde()
        HS-->>CTX: Event(waitTime|waitKill|bossGenerated)
        Note over CTX: se evento = waitTime, aggiorna waitTime col time dell'ondata
        CTX-->>SM: Event
        SM->>SM: next = waitTime | waitKill | killBoss
    end
    SM-->>EM: (tick concluso)
    EM->>EM: super.updateEntity(delta) + scanTargetInRange()
```

Narrazione dell'esempio di riferimento (Livello 1):

1. **Avvio.** La macchina parte in `StateWaitTime` con `waitTime = 1`: attende ~1 secondo.
2. **Prima ondata.** Superato il secondo, `StateWaitTime` emette `newHorde` → `generateHorde`.
3. **Generazione.** `StateGenerateHorde` chiama `context.spawnNextHorde()`: `HordeSpawner` istanzia
   i nemici della prima ondata e restituisce l'`Event` `waitTime` (la prima ondata dichiara
   `<generateEvent name="waitTime" time="1" />`). Il contesto legge `getCurrentWaitTime()` e imposta
   `waitTime = 1`.
4. **Ritorno all'attesa.** `generateHorde --waitTime--> waitTime`; `onEnter` azzera `elapsedTime` e
   il ciclo ricomincia per l'ondata successiva.
5. **Ondate `waitKill`.** Quando un'ondata dichiara `waitKill`, si passa a `StateWaitKill`, che
   attende finché `areAllEnemiesKilled()` è `true`.
6. **Boss.** L'ultima ondata dichiara `bossGenerated` → `killBoss`. Quando il boss muore,
   `killBoss` emette `bossKilled` → `bossKilledFinal` (finale).
7. **Fine livello.** `EnemyManager.isBossDead()` diventa `true`; `LevelScene` avvia il livello
   successivo.

---

## 4. Punti di integrazione

| Punto | Formato / dettaglio |
|---|---|
| Nomi di stato ed evento | Costanti `STATE_*` / `EVENT_*` in [GameResources](../../../game/src/main/java/it/spaghettisource/tigersupply/game/utils/GameResources.java). I nomi degli **eventi** coincidono con i valori `name` del tag `<generateEvent>` dell'XML. |
| `<generateEvent name="…" time="…">` | L'XML pilota indirettamente le transizioni: l'evento restituito da `HordeSpawner.spawnNextHorde()` porta lo stesso nome del `generateEvent` dell'ondata. |
| Attesa iniziale | `EnemySpawnContext.waitTime` è inizializzato a `1` per introdurre una pausa prima della primissima ondata. |

> **Corrispondenza nome evento ↔ XML.** `EVENT_WAIT_TIME = "waitTime"`,
> `EVENT_WAIT_KILL = "waitKill"`, `EVENT_BOSS_GENERATED = "bossGenerated"`: sono esattamente i
> valori ammessi per `<generateEvent name="…">`. Aggiungere un nuovo tipo di completamento richiede
> quindi sia una nuova costante/transizione sia il valore corrispondente nell'XML.

---

## 5. Stato dell'engine toccato

| Elemento | Modulo | Letto / scritto | Note |
|---|---|---|---|
| `EnemySpawnContext.elapsedTime` | `game` | scritto ogni frame, azzerato in `onEnter` di `StateWaitTime` | Base temporale del sequenziamento. |
| `EnemySpawnContext.waitTime` | `game` | scritto quando l'evento generato è `waitTime` | Sovrascrive il default `1`. |
| `EnemyManager` (entità) | `game` | scritto da `HordeSpawner.addRequest(...)` | I nemici generati entrano nel gruppo gestito. |
| `StateMachineImpl.state` | `engine` | scritto a ogni tick non finale | Lo stato corrente. |

### Casi limite e sicurezza

| Situazione | Comportamento |
|---|---|
| Un tick su `bossKilledFinal` | No-op (stato finale); `internalProcess` non viene mai invocato. |
| Ondata `waitTime` con `time` mancante o non numerico | Rifiutata **al caricamento** da `HordeSpawner.validateWaitTimeHordes` (fail-fast) — vedi [caricamento-dati-livello.md](caricamento-dati-livello.md). |
| `generateEvent` con `name` non fra quelli dichiarati nella tabella | `TransitionTable.next` solleva `StateMachineUnsupportedEvent` al tick. |
| Nemici ancora vivi in `waitKill` / `killBoss` | Lo stato emette `wait` e resta su sé stesso (self-loop). |

> **Attenzione.** Gli stati concreti sono **senza stato interno** e riusati come singleton: tutta la
> memoria vive in `EnemySpawnContext`. Non aggiungere campi mutabili agli stati.
