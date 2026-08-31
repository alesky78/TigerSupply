# Ricetta: aggiungere ondate, nemici, algoritmi o stati

> **Indice correlato**: [Ciclo di vita dello spawn dei nemici](index.md)

Questa pagina raccoglie le procedure **additive** per estendere il sottosistema. Ogni scenario
distingue esplicitamente ciò che si tocca nel modulo **game** (contenuto/esempio) da ciò che si
tocca nel modulo **engine** (framework): nella pratica quasi tutto è **solo `game`**, perché
l'engine è già completo e riusabile.

## Indice

1. [Aggiungere una nuova ondata](#1-aggiungere-una-nuova-ondata)
2. [Aggiungere un nuovo tipo di nemico](#2-aggiungere-un-nuovo-tipo-di-nemico)
3. [Aggiungere un nuovo algoritmo di movimento](#3-aggiungere-un-nuovo-algoritmo-di-movimento)
4. [Aggiungere un nuovo stato alla macchina (avanzato)](#4-aggiungere-un-nuovo-stato-alla-macchina-avanzato)
5. [Checklist](#5-checklist)

---

## 1. Aggiungere una nuova ondata

**Obiettivo:** inserire una nuova ondata nel livello. **Modulo toccato: solo `game` (XML).**

### Prerequisiti
- I prototipi nemico e algoritmo che intendi usare esistono già in `level-1.xml` (altrimenti vedi
  §2 e §3).

### Passi
1. Apri [level-1.xml](../../../game/src/main/resources/level/level-1.xml), sezione `<hordes>`.
2. Aggiungi un blocco `<horde>` nel punto della sequenza desiderato (l'ordine di dichiarazione è
   l'ordine di spawn):

   ```xml
   <horde>
       <generateEvent name="hordeTimed" time="1.5" />
       <enemy enemyPrototype="standard" posX="1350" posY="200" posZ="20" algorithmPrototype="default" />
       <enemy enemyPrototype="standard" posX="1350" posY="500" posZ="20" algorithmPrototype="default" />
   </horde>
   ```
3. Scegli il `generateEvent`:
   - `hordeTimed` con `time` (secondi, anche frazionari) → attesa temporizzata **obbligatoria**;
   - `hordeClearable` → attende lo schermo pulito (nessun `time`);
   - `bossSpawned` → l'ondata è il boss (passa allo stato di attesa boss).

> **Coordinate.** `posX/posY` assumono la risoluzione fissa **1360×660**. Gli spawn a destra usano
> tipicamente `posX="1350"`; a sinistra `posX="1"`.

### Verifica
- Avvia il gioco; l'ondata deve comparire al proprio turno. Un'ondata `hordeTimed` senza `time`
  valido **impedisce l'avvio del livello** con un errore che ne nomina l'indice.

---

## 2. Aggiungere un nuovo tipo di nemico

**Obiettivo:** introdurre un nemico con comportamento nuovo. **Moduli toccati: `game` (classe +
XML), eventualmente il catalogo immagini.**

### Prerequisiti
- Un'immagine (o alias già presente nell'`image-catalog.txt`) per lo sprite.

### Passi
1. **Classe nemico** — `game/src/main/java/it/spaghettisource/tigersupply/game/entity/`:
   crea una sottoclasse di [`Enemy`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/entity/Enemy.java)
   (prendi a modello `EnemyStandard`), implementandone il comportamento (arma, punti vita, ecc.).
2. **Alias immagine** (se nuovo) — registra l'immagine nell'`image-catalog.txt` del modulo `game`.
3. **Prototipo XML** — in `level-1.xml`, sezione `<enemiesPrototype>`, aggiungi:

   ```xml
   <enemyPrototype name="mioNemico" type="imageSingleSprite"
                   class="it.spaghettisource.tigersupply.game.entity.MioNemico">
       <speed x="-120" y="0" />
       <image alias="enemy1" />
       <scale value="1.1" />
   </enemyPrototype>
   ```
4. **Usalo** in una `<horde>` con `enemyPrototype="mioNemico"` (vedi §1).

> **Reflection.** L'attributo `class` deve essere l'**FQN esatto** della tua classe: viene
> istanziata da `EntityFactory` (modulo `engine`) via reflection. Un typo qui fallisce solo a
> runtime, quando l'ondata viene generata.

### Verifica
- L'ondata che usa il nuovo prototipo deve spawnare il nemico con lo sprite e la velocità attesi.

---

## 3. Aggiungere un nuovo algoritmo di movimento

**Obiettivo:** un nuovo pattern di movimento. **Moduli toccati: `engine` (classe algoritmo) + `game`
(XML).**

### Prerequisiti
- Nessuno.

### Passi
1. **Classe algoritmo** — `engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/`:
   crea una sottoclasse di [`UpdateAlgorithm`](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgorithm.java)
   (modello: `UpdateAlgorithmSinusoidal`). Leggi i parametri dal `DynaProperties` che ricevi.

   > Il movimento è logica **framework** riusabile: appartiene a `engine.entity.logic`, non a
   > `game`.
2. **Prototipo XML** — in `level-1.xml`, sezione `<algorithmsPrototype>`, aggiungi:

   ```xml
   <algorithmPrototype name="mioAlgo"
                       class="it.spaghettisource.tigersupply.engine.entity.logic.MioAlgo">
       <algorithmProperties>
           <property name="delta" value="30" />
           <property name="increment" value="45.0" />
       </algorithmProperties>
   </algorithmPrototype>
   ```
   Per algoritmi a percorso usa `<listPoints>` con `<point posX="…" posY="…"/>`.
3. **Usalo** in una `<enemy … algorithmPrototype="mioAlgo" />`.

### Verifica
- Il nemico che usa il nuovo algoritmo deve muoversi secondo il pattern implementato.

---

## 4. Aggiungere un nuovo stato alla macchina (avanzato)

**Obiettivo:** un nuovo tipo di completamento/comportamento di sequenziamento (es. un'attesa
condizionata diversa). **Moduli toccati: `game` (stato + cablaggio + costanti); l'`engine` **non**
si tocca.**

### Prerequisiti
- Comprendere il [framework a stati dell'engine](motore-macchina-a-stati.md) e il
  [sequenziamento del gioco](sequenziamento-horde.md).

### Passi
1. **Costanti** — in [EnemySpawnStateMachineFactory](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/EnemySpawnStateMachineFactory.java)
   aggiungi la costante di stato `STATE_*` e, se serve, l'evento `EVENT_*` (più l'eventuale `Event`
   condiviso). Se lo stato è raggiunto da un `generateEvent`, il valore dell'evento deve coincidere
   con il `name` usato nell'XML.
2. **Classe stato** — `game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/`:
   estendi [`AbstractState<EnemySpawnContext>`](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/statemachine/AbstractState.java),
   passa il nome al costruttore con `super(stateName)` e implementa solo `internalProcess(context)`
   (ritorna un `Event`, tipicamente un singleton della factory). Mantieni lo stato **senza campi
   mutabili**: la memoria vive in `EnemySpawnContext`.
3. **Contesto** (se serve) — aggiungi a
   [`EnemySpawnContext`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/EnemySpawnContext.java)
   i dati/deleghe necessari.
4. **Cablaggio** — in `EnemySpawnStateMachineFactory.build()`
   ([EnemySpawnStateMachineFactory.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/EnemySpawnStateMachineFactory.java)):
   costruisci lo stato (passandogli il nome) e dichiara le sue transizioni sulla `TransitionTable`
   (`add` / `selfLoop`). Se è terminale, fai in modo che `isFinal()` ritorni `true` (come
   `StateLevelCleared`) e **non** aggiungerlo come sorgente nella tabella.

### Verifica
- Compila; il grafo dichiarato deve coprire ogni coppia `(stato,evento)` che può prodursi. Una
  transizione mancante solleva `StateMachineUnsupportedEvent`/`StateMachineUnsupportedState` al tick.

---

## 5. Checklist

- [ ] **Ondata** aggiunta in `<hordes>` con un `generateEvent` valido (`time` presente se `hordeTimed`).
- [ ] **Nemico**: nuova sottoclasse di `Enemy` (`game`) + `<enemyPrototype>` con FQN corretto (+ alias immagine).
- [ ] **Algoritmo**: nuova sottoclasse di `UpdateAlgorithm` (`engine`) + `<algorithmPrototype>` con FQN e proprietà.
- [ ] **Stato**: costanti `STATE_*`/`EVENT_*` (`game`) + classe `AbstractState<EnemySpawnContext>` + transizioni cablate in `EnemyManager.initComponents()`.
- [ ] Nessuna transizione mancante nella `TransitionTable`.
- [ ] `mvn -DskipTests clean package` verde e smoke launch del `launcher/target/tigersupply.jar` senza errori in console.
