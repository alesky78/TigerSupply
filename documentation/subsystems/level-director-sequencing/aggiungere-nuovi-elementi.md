# Ricetta: aggiungere passi, azioni, nemici, algoritmi o stati

> **Indice correlato**: [Sequenziamento del livello tramite il Level Director](index.md)

Questa pagina raccoglie le procedure **additive** per estendere il sottosistema. Ogni scenario
distingue esplicitamente ciò che si tocca nel modulo **game** (contenuto/esempio) da ciò che si
tocca nel modulo **engine** (framework): nella pratica quasi tutto è **solo `game`**, perché
l'engine è già completo e riusabile.

## Indice

1. [Aggiungere un nuovo passo](#1-aggiungere-un-nuovo-passo)
2. [Aggiungere un nuovo tipo di azione](#2-aggiungere-un-nuovo-tipo-di-azione)
3. [Aggiungere un nuovo tipo di nemico](#3-aggiungere-un-nuovo-tipo-di-nemico)
4. [Aggiungere un nuovo algoritmo di movimento](#4-aggiungere-un-nuovo-algoritmo-di-movimento)
5. [Aggiungere un nuovo stato alla macchina (avanzato)](#5-aggiungere-un-nuovo-stato-alla-macchina-avanzato)
6. [Checklist](#6-checklist)

---

## 1. Aggiungere un nuovo passo

**Obiettivo:** inserire un nuovo passo nel livello. **Modulo toccato: solo `game` (XML).**

### Prerequisiti
- I tipi di azione, i prototipi nemico e algoritmo che intendi usare esistono già (altrimenti vedi
  §2, §3 e §4).

### Passi
1. Apri [level-1.xml](../../../game/src/main/resources/level/level-1.xml), sezione `<steps>`.
2. Aggiungi un blocco `<step>` nel punto della sequenza desiderato (l'ordine di dichiarazione è
   l'ordine di esecuzione). Un passo si scrive **in ordine di esecuzione**: prima le `<actions>`,
   poi il `<completionEvent>`:

   ```xml
   <step>
       <actions>
           <action type="spawnHorde">
               <enemy enemyPrototype="standard-2" posX="1350" posY="200" posZ="20" algorithmPrototype="default" />
               <enemy enemyPrototype="standard-2" posX="1350" posY="500" posZ="20" algorithmPrototype="default" />
           </action>
       </actions>
       <completionEvent name="timed" time="1.5" />
   </step>
   ```
3. Scegli il `completionEvent`:
   - `timed` con `time` (secondi, anche frazionari) → attesa temporizzata **obbligatoria**;
   - `cleared` → attende lo schermo pulito (nessun `time`);
   - `bossSpawned` → il passo ha introdotto il boss (passa allo stato di attesa boss).

> **Coordinate.** `posX/posY` assumono la risoluzione fissa **1360×660**. Gli spawn a destra usano
> tipicamente `posX="1350"`; a sinistra `posX="1"`.

### Verifica
- Avvia il gioco; il passo deve eseguirsi al proprio turno. Un passo `timed` senza `time` valido
  **impedisce l'avvio del livello** con un errore che ne nomina l'indice.

---

## 2. Aggiungere un nuovo tipo di azione

**Obiettivo:** introdurre un nuovo comando eseguibile in un passo (es. muovere lo sfondo, comandare
la base, lanciare una traccia audio). **Modulo toccato: `game` (classe azione + registro), più
eventualmente il `DirectorContext` se serve un sottosistema nuovo.**

### Prerequisiti
- Il sottosistema che l'azione deve comandare è raggiungibile dal
  [`DirectorContext`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/director/DirectorContext.java)
  (l'enemy manager c'è già; per un sottosistema nuovo aggiungi un campo + getter/setter e cablalo in
  [`LevelDirector.init()`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/director/LevelDirector.java)
  e in [`LevelScene`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/LevelScene.java)).

### Passi
1. **Classe azione** — `game/src/main/java/it/spaghettisource/tigersupply/game/scene/action/`:
   implementa [`LevelAction`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/action/LevelAction.java)
   (modello: [`SpawnHordeAction`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/action/SpawnHordeAction.java)).
   In `init(ActionDefinition)` leggi i dati dell'azione (il sacchetto `properties` per gli attributi,
   o gli `<enemy>` per uno spawn); in `execute(DirectorContext)` comanda il sottosistema. Mantieni
   l'azione **fire-and-forget**: prende effetto subito, ogni comportamento durativo è del sottosistema
   che comanda.

   ```java
   public class SetBaseMotionAction implements LevelAction {
       private String state;

       @Override
       public void init(ActionDefinition definition) {
           this.state = definition.getProperty("state"); // es. "halt" | "advance"
       }

       @Override
       public void execute(DirectorContext context) {
           // context.getBaseManager().setMotion(state);  // quando la base esisterà
       }
   }
   ```
2. **Registra il tipo** — in [`LevelActionFactory`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/action/LevelActionFactory.java)
   aggiungi una riga nella mappa `TYPE_TO_CLASS`:

   ```java
   TYPE_TO_CLASS.put("setBaseMotion", "it.spaghettisource.tigersupply.game.scene.action.SetBaseMotionAction");
   ```
3. **Usala** in un passo, accanto alle altre azioni:

   ```xml
   <step>
       <actions>
           <action type="spawnHorde"> … </action>
           <action type="setBaseMotion" state="halt" />
       </actions>
       <completionEvent name="cleared" />
   </step>
   ```

> **Additivo e isolato.** Un nuovo tipo di azione = **una nuova classe** + **una riga** nel registro.
> Non si toccano gli stati della macchina né il vocabolario di completamento: le azioni sono il punto
> di estensione **aperto**.

### Verifica
- Il passo che usa la nuova azione deve produrne l'effetto. Un `type` non registrato fa fallire il
  passo con "unknown level action type '…'".

---

## 3. Aggiungere un nuovo tipo di nemico

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
4. **Usalo** in un'`<action type="spawnHorde">` con `enemyPrototype="mioNemico"` (vedi §1).

> **Reflection.** L'attributo `class` deve essere l'**FQN esatto** della tua classe: viene
> istanziata da `EntityFactory` (modulo `engine`) via reflection. Un typo qui fallisce solo a
> runtime, quando il passo viene eseguito.

### Verifica
- Il passo che usa il nuovo prototipo deve spawnare il nemico con lo sprite e la velocità attesi.

---

## 4. Aggiungere un nuovo algoritmo di movimento

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

## 5. Aggiungere un nuovo stato alla macchina (avanzato)

**Obiettivo:** un nuovo tipo di completamento/comportamento di sequenziamento (es. un'attesa
condizionata diversa). **Moduli toccati: `game` (stato + cablaggio + costanti); l'`engine` **non**
si tocca.**

> **Prima di aggiungere uno stato, chiediti se ti serve davvero.** Se vuoi solo un nuovo *effetto* in
> un passo, aggiungi un'**azione** (§2), non uno stato. Uno stato nuovo serve solo per un nuovo modo
> di **attendere/completare** un passo (il vocabolario di completamento chiuso).

### Prerequisiti
- Comprendere il [framework a stati dell'engine](motore-macchina-a-stati.md) e il
  [sequenziamento del gioco](sequenziamento-step.md).

### Passi
1. **Costanti** — in [LevelDirectorStateMachineFactory](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/LevelDirectorStateMachineFactory.java)
   aggiungi la costante di stato `STATE_*` e, se serve, l'evento `EVENT_*` (più l'eventuale `Event`
   condiviso). Se lo stato è raggiunto da un `completionEvent`, il valore dell'evento deve coincidere
   con il `name` usato nell'XML.
2. **Classe stato** — `game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/`:
   estendi [`AbstractState<DirectorContext>`](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/statemachine/AbstractState.java),
   passa il nome al costruttore con `super(stateName)` e implementa solo `internalProcess(context)`
   (ritorna un `Event`, tipicamente un singleton della factory). Mantieni lo stato **senza campi
   mutabili**: la memoria vive in `DirectorContext`.
3. **Contesto** (se serve) — aggiungi a
   [`DirectorContext`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/director/DirectorContext.java)
   i dati/deleghe necessari.
4. **Cablaggio** — in `LevelDirectorStateMachineFactory.build()`
   ([LevelDirectorStateMachineFactory.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/LevelDirectorStateMachineFactory.java)):
   costruisci lo stato (passandogli il nome) e dichiara le sue transizioni sulla `TransitionTable`
   (`add` / `selfLoop`). Se è terminale, fai in modo che `isFinal()` ritorni `true` (come
   `StateLevelCleared`) e **non** aggiungerlo come sorgente nella tabella.

### Verifica
- Compila; il grafo dichiarato deve coprire ogni coppia `(stato,evento)` che può prodursi. Una
  transizione mancante solleva `StateMachineUnsupportedEvent`/`StateMachineUnsupportedState` al tick.

---

## 6. Checklist

- [ ] **Passo** aggiunto in `<steps>` con `<actions>` seguite da un `<completionEvent>` valido (`time` presente se `timed`).
- [ ] **Azione**: nuova classe `LevelAction` (`game`) + riga in `LevelActionFactory.TYPE_TO_CLASS` (+ eventuale sottosistema nel `DirectorContext`).
- [ ] **Nemico**: nuova sottoclasse di `Enemy` (`game`) + `<enemyPrototype>` con FQN corretto (+ alias immagine).
- [ ] **Algoritmo**: nuova sottoclasse di `UpdateAlgorithm` (`engine`) + `<algorithmPrototype>` con FQN e proprietà.
- [ ] **Stato**: costanti `STATE_*`/`EVENT_*` (`game`) + classe `AbstractState<DirectorContext>` + transizioni cablate in `LevelDirectorStateMachineFactory.build()`.
- [ ] Nessuna transizione mancante nella `TransitionTable`.
- [ ] `mvn -DskipTests clean package` verde e smoke launch del `launcher/target/tigersupply.jar` senza errori in console.
