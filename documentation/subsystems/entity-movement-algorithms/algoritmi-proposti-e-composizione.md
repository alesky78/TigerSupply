# Algoritmi di movimento proposti e composizione

> **Related index**: [Algoritmi di movimento delle entità](index.md)

> **Stato: proposta, non pianificata.** Questa pagina raccoglie idee di **potenziali migliorie** al
> catalogo di [`UpdateAlgorithm`](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgorithm.java)
> per uno shoot 'em up in stile anni '90 (Raiden, 1942, Gradius, DoDonPachi…). **Nessuna** di queste
> è implementata: la documentazione dell'esistente sta in
> [catalogo-algoritmi-attuali.md](catalogo-algoritmi-attuali.md). Prima di realizzarne una, aprire
> una change OpenSpec.

## Indice

1. [Perché questi algoritmi](#1-perché-questi-algoritmi)
2. [Algoritmi proposti per ruolo](#2-algoritmi-proposti-per-ruolo)
   - [A. Nemici popcorn](#a-nemici-popcorn)
   - [B. Boss / mid-boss](#b-boss--mid-boss)
   - [C. Proiettili / oggetti](#c-proiettili--oggetti)
3. [L'idea chiave: un algoritmo composito](#3-lidea-chiave-un-algoritmo-composito)
4. [Quali sono i più utili (priorità)](#4-quali-sono-i-più-utili-priorità)
   - [4.1 Due significati di "utile"](#41-due-significati-di-utile)
   - [4.2 Matrice valore/costo](#42-matrice-valorecosto)
   - [4.3 Classifica per tier](#43-classifica-per-tier)
   - [4.4 Il genere sposta la classifica](#44-il-genere-sposta-la-classifica)
5. [Note trasversali](#5-note-trasversali)

---

## 1. Perché questi algoritmi

Il catalogo attuale copre bene il **moto libero** (Default, Sinusoidal), il **percorso scriptato**
(LinearPath, SmoothPath), la **mira one-shot** (GoToPoint, GoToPointIncreasingSpeed) e
l'**aggancio/inseguimento** (FollowSprite, CopyPosition). Mancano però diversi archetipi di
movimento che sono *firme riconoscibili* del genere anni '90. Li raggruppo per **ruolo di gioco**,
che è il modo in cui si pensano quando si scrive l'XML del livello.

```
   ATTUALE                                     MANCANTE (proposto)
  +-------------------+                        +--------------------------+
  | moto libero       | Default, Sinusoidal    | zig-zag, orbita, fig.8   |
  | percorso          | LinearPath, SmoothPath | swoop / picchiata        |
  | mira one-shot     | GoToPoint(+Incr)       | ease-in-out, boomerang   |
  | aggancio/inseguo  | FollowSprite, CopyPos  | homing con turn-rate     |
  | -- (nessuno) --   |                        | arco balistico, spirale  |
  | -- (nessuno) --   |                        | enter-hold-exit (compos.)|
  +-------------------+                        +--------------------------+
```

---

## 2. Algoritmi proposti per ruolo

### A. Nemici popcorn

| Proposta | Forma | Idea di comportamento | Chiavi ipotizzate |
|---|---|---|---|
| **ZigZag** (onda triangolare) | inversioni nette anziché seno morbido | come Sinusoidal ma con onda triangolare: la Y sale/scende a velocità costante e inverte al raggiungimento dell'ampiezza | `amplitude`, `period` |
| **Enter-Hold-Exit** | entra, sosta, esce | va a una posizione di "station keeping", **attende** N secondi (sparando), poi esce; oggi inesprimibile perché nessun algoritmo ha uno stato di attesa | `entryPoint`, `holdSeconds`, `exitSpeed` — vedi [§3](#3-lidea-chiave-un-algoritmo-composito) |
| **Swoop / picchiata** | entra e curva verso il giocatore | traiettoria d'attacco parametrica (Bezier/arco) che punta il giocatore ed esce, tipica dei "divers" alla Galaga | `entryPoint`, `targetRef`, `curvature` |

```
 ZigZag:  /\  /\  /\        Enter-Hold-Exit:  --->[ sosta N s ]--->
          \/  \/  \/                          entra   spara     esce

 Swoop:   \
          _\        (picchiata verso il giocatore, poi uscita)
          /  `-.__
```

### B. Boss / mid-boss

| Proposta | Forma | Idea di comportamento | Chiavi ipotizzate |
|---|---|---|---|
| **Ping-pong in bounding box** | spazza a sinistra/destra rimbalzando | si muove entro un rettangolo e inverte la direzione ai bordi | `minX`, `maxX`, `speed` |
| **Figure-8 / Lissajous** | otto orizzontale/verticale | seno su **entrambi** gli assi con fasi/frequenze diverse (oggi il seno è solo su Y) | `ampX`, `ampY`, `freqX`, `freqY`, `phase` |
| **Orbita / cerchio** | ruota attorno a un centro | posizione parametrizzata su un cerchio (o spirale) attorno a un punto | `center`, `radius`, `angularSpeed` |

```
 Ping-pong: [<====  ====>]     Figure-8:  ( ∞ )      Orbita:  ( o ) attorno a un centro
```

### C. Proiettili / oggetti

| Proposta | Forma | Idea di comportamento | Chiavi ipotizzate |
|---|---|---|---|
| **Homing con turn-rate** | missile a ricerca | punta il giocatore ma **sterza** con una velocità di virata massima (converge, non oscilla come FollowSprite) | `targetRef`, `speed`, `maxTurnDegPerSec` |
| **Arco balistico (gravità)** | parabola | velocità iniziale + gravità costante, per bombe lanciate/detriti | `speedX`, `speedY`, `gravity` |
| **Spirale** | girandola | raggio crescente/decrescente + rotazione, tipico bullet-hell di fine anni '90 | `center`, `angularSpeed`, `radialSpeed` |

```
 Homing:  o~->.            Arco:   *            Spirale:  ((( o
             `-._>@                / \                     (raggio che cresce)
          sterza max °/s          (parabola)
```

> **Homing vs FollowSprite.** [`UpdateAlgoritmFollowSprite`](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgoritmFollowSprite.java)
> usa velocità per-asse fisse e cambia solo il segno: **oscilla** attorno al bersaglio. Un homing
> "vero" ruota il vettore velocità verso il bersaglio con un limite di virata, e per questo può
> convergere e mancare il colpo se il giocatore schiva — che è esattamente ciò che serve.

---

## 3. L'idea chiave: un algoritmo composito

Molti pattern qui sopra (**Enter-Hold-Exit**, swoop-and-return, boomerang) **non sono nuove
formule**: sono *sequenze* di algoritmi che **esistono già**. Invece di scrivere una nuova classe
per ognuno, la leva a più alto valore è un unico `UpdateAlgorithm` **composito** (pattern
**Composite/Decorator**) che concatena strategie esistenti, passando dall'una alla successiva quando
una **condizione di completamento** è soddisfatta (tempo trascorso, punto raggiunto).

```
  SequenceUpdateAlgorithm  (implements UpdateAlgorithm)
  +--------------------------------------------------------------+
  |  [ GoToPoint(entryPoint) ] --(arrivato)-->                    |
  |  [ Default(speed=0) per holdSeconds ] --(timeout)-->          |
  |  [ Default(exitSpeed) ]                                       |
  +--------------------------------------------------------------+
     riusa i mattoni esistenti: zero nuova matematica di traiettoria
```

**Come funzionerebbe (schizzo, non implementazione):**

- tiene una lista ordinata di *segmenti*, ciascuno = un `UpdateAlgorithm` + una condizione di fine
  (`durata in secondi` oppure `punto raggiunto`);
- `updateLogic` delega al segmento corrente; quando la condizione scatta, passa al successivo;
- esaurita la lista, l'entità resta ferma (o si applica una policy di loop).

**Cosa sblocca gratis** riusando `GoToPoint`, `Default`, `Sinusoidal`, `LinearPath`:

| Pattern | Composizione |
|---|---|
| Enter-Hold-Exit | `GoToPoint` → `Default(v=0)`+timer → `Default(uscita)` |
| Boomerang / ritorno | `Default(entra)` → `Default(v=0)`+timer → `Default(-entra)` |
| Swoop-and-return | `SmoothPath(entra)` → `GoToPoint(uscita)` |
| Pattugliamento a segmenti con soste | `LinearPath` → `Default(v=0)` → `LinearPath` … |

> **Perché conviene.** Un solo algoritmo composito, ben fatto e riutilizzabile dall'XML, copre metà
> della lista dei nemici popcorn e diversi pattern boss **senza** aggiungere una classe per ogni
> variante. È probabilmente l'aggiunta singola a più alto ritorno di tutta questa pagina.

> **Non è "il primo in classifica": è quello che *cancella* le altre voci.** Costruito il composito,
> **Enter-Hold-Exit, boomerang e swoop-and-return spariscono come classi dedicate** — diventano sue
> composizioni (vedi la tabella qui sopra). La domanda giusta non è "quali algoritmi costruisco", ma:
> *"costruisco il composito, e quali **mattoni atomici** mi mancano per comporre tutto il resto?"*

**Prerequisito abilitante (il vero lavoro).** Serve un concetto di **condizione di completamento del
segmento** (durata scaduta o punto raggiunto). Attenzione: i mattoni attuali **non sanno segnalare la
fine** — `GoToPoint` *prosegue oltre il punto* (nessuna condizione di arresto sul bersaglio, vedi il
[catalogo](catalogo-algoritmi-attuali.md#9-quadro-riassuntivo)), mentre `Default` e `Sinusoidal` non
finiscono mai. Quindi il composito deve **possedere lui stesso** il check (durata/arrivo), oppure gli
algoritmi devono esporre un `isComplete()`: questa è la vera decisione di design, più della
traiettoria.

```
  Il vero lavoro del composito non e' la geometria:
  +--------------------+     +-------------------------------+
  | concatenare        |  +  | definire QUANDO un segmento   |
  | strategie (facile) |     | e' "finito"  (il nodo vero)   |
  +--------------------+     +-------------------------------+
```

Nel `game` esiste già un vocabolario simile per gli step del livello
(vedi [level-director-sequencing](../level-director-sequencing/index.md)); qui però va tenuto nel
modulo `engine`, generico e senza dipendenze dal gioco, coerente con la collocazione di
`UpdateAlgorithm`.

---

## 4. Quali sono i più utili (priorità)

> **In breve:** i due più utili in assoluto sono quelli che **sbloccano capacità oggi impossibili** —
> l'algoritmo **composito** ([§3](#3-lidea-chiave-un-algoritmo-composito)) e l'**homing con
> turn-rate**. Il resto sono forme nuove per capacità che in parte esistono già, quindi con ritorno
> marginale più basso. L'ordine fine dipende dal titolo di riferimento.

### 4.1 Due significati di "utile"

Le proposte non sono sullo stesso piano. Alcune **sbloccano un comportamento oggi inesprimibile**;
altre aggiungono solo una **forma nuova a una capacità che esiste già** — e quindi hanno un ritorno
marginale più basso.

```
  SBLOCCA una capacita' oggi IMPOSSIBILE   (ritorno massimo)
   *  Composite         -> introduce lo stato di ATTESA
   *  Homing turn-rate  -> CONVERGE sul bersaglio

  AGGIUNGE una forma a capacita' che ESISTONO gia'   (ritorno minore)
   *  ZigZag      ~ Sinusoidal (onda triangolare)
   *  Figure-8    ~ Sinusoidal applicata a 2 assi
   *  Ping-pong   ~ LinearPath ma in LOOP (rimbalza)
   *  Orbita / Spirale / Arco balistico
```

- **Composite** — nessun algoritmo attuale ha uno *stato di attesa*: Enter-Hold-Exit è letteralmente
  inesprimibile.
- **Homing con turn-rate** — [`UpdateAlgoritmFollowSprite`](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgoritmFollowSprite.java)
  **oscilla** attorno al bersaglio, non converge mai; un vero missile a ricerca oggi non esiste.

### 4.2 Matrice valore/costo

```
  valore
 (pattern sbloccati x frequenza d'uso)
   ALTO |  [Homing]                  [Composite] *    <- miglior ROI
        |
  MEDIO |  [Ping-pong]   [Figure-8/Orbita]            [Spirale] **
        |
  BASSO |  [ZigZag]      [Arco balistico]
        +----------------------------------------------------------
          BASSO                 costo impl.                    ALTO

  *  Composite: il costo NON e' la matematica (zero) ma il
     "contratto di completamento" del segmento (vedi §3).
  ** Spirale: il valore dipende dal sottogenere (alto se bullet-hell).
```

### 4.3 Classifica per tier

| Tier | Proposte | Perché |
|---|---|---|
| **1 — costruisci questi** | **`SequenceUpdateAlgorithm` (composito)**, **Homing con turn-rate** | Sbloccano l'impossibile; il composito **elimina** 3+ altre proposte (Enter-Hold-Exit, boomerang, swoop-and-return diventano sue composizioni). |
| **2 — miglior valore/costo** | **Ping-pong** (il *loop* è la vera novità: `LinearPath` si ferma a fine lista), **Figure-8 / Orbita** | Carattere ai boss con matematica minima. |
| **3 — solo se il genere lo chiede** | **Spirale** (bullet-hell), **Arco balistico** (nemici che lanciano bombe/detriti) | Nicchia; dipendono dal titolo di riferimento. |
| **declassato** | **ZigZag** | Quasi già esprimibile con `Sinusoidal` (stesso inviluppo, onda triangolare): una classe propria rende poco. |

> **Meglio pochi parametri di molte classi.** `ZigZag` e `Figure-8` sono entrambi *"`Sinusoidal`
> generalizzato"* (forma d'onda diversa; seno anche sul secondo asse). Un singolo oscillatore più
> ricco li assorbirebbe con qualche parametro invece di tre classi sorelle — a costo di allontanarsi
> dalla convenzione "una classe per comportamento, referenziata per nome nell'XML". È un compromesso
> da decidere consapevolmente.

### 4.4 Il genere sposta la classifica

Uno shmup "pulito" alla Raiden/Gradius premia **composito + ping-pong + figure-8**; un bullet-hell di
fine anni '90 (DoDonPachi) fa salire **spirale** e **homing**. La domanda aperta che decide i primi
due posti è quindi: **qual è il titolo di riferimento?**

---

## 5. Note trasversali

- **Frame-rate.** Progettare i nuovi algoritmi in modo **frame-rate independent** (moltiplicare per
  `deltaSeconds`), evitando il difetto ancora presente in `GoToPointIncreasingSpeed` (accelerazione
  per-frame). Vedi [concetto 3.4](index.md#3-concetti-chiave).
- **Riferimenti a entità.** Homing e Swoop hanno bisogno di un riferimento al giocatore: passarlo
  via `DynaProperties` come oggetto (come già fa `FollowSprite` con la chiave `sprite`), senza
  introdurre dipendenze del modulo `engine` verso il `game`.
- **Registrazione.** Ogni nuovo algoritmo segue il pattern esistente: sottoclasse di
  `AbstractUpdateAlgorithm`, eventuale helper in
  [UpdateAlgorithmFactoryWrapper](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgorithmFactoryWrapper.java),
  nuove costanti `ALGPRO_*` in
  [StaticResources](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/utils/StaticResources.java),
  e riferimento per nome di classe dall'XML del livello. La procedura completa è nella
  [ricetta del Level Director](../level-director-sequencing/aggiungere-nuovi-elementi.md).
