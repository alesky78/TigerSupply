# Catalogo degli algoritmi di movimento attuali

> **Related index**: [Algoritmi di movimento delle entità](index.md)

Questa pagina documenta **uno per uno** gli 8 algoritmi di movimento esistenti nel package
[engine.entity.logic](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic).
Per ognuno: comportamento, formula/passo, chiavi di configurazione, se rispetta il frame-rate, come
crearlo e note.

## Indice

1. [UpdateAlgorithmDefault](#1-updatealgorithmdefault)
2. [UpdateAlgorithmSinusoidal](#2-updatealgorithmsinusoidal)
3. [UpdateAlgorithmLinearPath](#3-updatealgorithmlinearpath)
4. [UpdateAlgorithmBspline](#4-updatealgorithmbspline)
5. [UpdateAlgoritmGoToPoint](#5-updatealgoritmgotopoint)
6. [UpdateAlgoritmGoToPointIncreasingSpeed](#6-updatealgoritmgotopointincreasingspeed)
7. [UpdateAlgoritmFollowSprite](#7-updatealgoritmfollowsprite)
8. [UpdateAlgoritmCopyPosition](#8-updatealgoritmcopyposition)
9. [Quadro riassuntivo](#9-quadro-riassuntivo)

---

## 1. UpdateAlgorithmDefault

**Forma del moto:** retta a velocità costante.

- **Sorgente:** [UpdateAlgorithmDefault.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgorithmDefault.java)
- **Rispetta il frame-rate:** **sì** (integra `Speed` su `deltaSeconds`).
- **Chiavi di configurazione:** nessuna.

**Comportamento.** A ogni frame incrementa la posizione di `speed * deltaSeconds` su entrambi gli
assi. È il moto di default: nessuna configurazione, direzione e verso determinati interamente dalla
`Speed` di riferimento dell'entità.

```
X += speedX * dt
Y += speedY * dt
```

```
--------------------------->   (retta, verso e pendenza dati da Speed)
```

**Creazione (codice):** `UpdateAlgorithmFactoryWrapper.newDefault()`.

**XML (livello 1):**

```xml
<algorithmPrototype name="default"
    class="it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmDefault" />
```

---

## 2. UpdateAlgorithmSinusoidal

**Forma del moto:** avanza in X a velocità costante mentre oscilla in Y con un'onda sinusoidale.

- **Sorgente:** [UpdateAlgorithmSinusoidal.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgorithmSinusoidal.java)
- **Rispetta il frame-rate:** **sì**.
- **Chiavi:** `delta` (ampiezza), `increment` (velocità angolare in °/s), `start` (angolo iniziale, opzionale, default `0`).

**Comportamento.** La X avanza come nel Default; la Y aggiunge un offset sinusoidale. L'angolo
interno cresce di `increment * dt` ogni frame.

```
X += speedX * dt
Y += speedY * dt + delta * ( sin(angle + increment*dt) - sin(angle) )
angle += increment * dt
```

```
~~~~~~~~~~~~~>   (onda: ampiezza = delta, frequenza = increment)
```

**Creazione (codice):** `newSinusoidal(delta, angleIncrement)` oppure
`newSinusoidal(delta, angleIncrement, angleStart)`.

**XML (livello 1):**

```xml
<algorithmPrototype name="sinusoidal"
    class="it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmSinusoidal">
    <algorithmProperties>
        <property name="delta" value="30" />
        <property name="increment" value="45.0" />
    </algorithmProperties>
</algorithmPrototype>
```

---

## 3. UpdateAlgorithmLinearPath

**Forma del moto:** percorre in linea retta una sequenza di waypoint, uno dopo l'altro, a velocità
costante.

- **Sorgente:** [UpdateAlgorithmLinearPath.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgorithmLinearPath.java)
- **Rispetta il frame-rate:** **sì**.
- **Chiavi:** `listpoints` (lista ordinata di `Point`).

**Comportamento.** Al primo frame ricava la velocità di crociera dal **modulo** della `Speed` di
riferimento (`sqrt(speedX² + speedY²)`). Ogni frame avanza verso il waypoint corrente di
`velocità * dt`; quando arriva entro un passo dal waypoint ci si aggancia e punta al successivo.
Esaurita la lista, **l'entità si ferma**.

```
step = |Speed| * dt
avanza verso il waypoint corrente di step; raggiunto -> prossimo waypoint
```

```
   *__            (segmenti retti tra i waypoint)
      \__*___*
```

> **Differenza da Bspline:** qui i segmenti restano **spigolosi** e la velocità è costante e
> rispettosa del frame-rate; Bspline invece **arrotonda** i punti in una curva e ignora la velocità.

**Creazione (codice):** `newLinearPath(List<Point> points)`.

**XML (livello 1, es. `pathUp`):**

```xml
<algorithmPrototype name="pathUp"
    class="it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmLinearPath">
    <algorithmProperties>
        <listPoints name="listpoints">
            <point posX="685" posY="0"/>
            <point posX="1300" posY="300"/>
            <point posX="-100" posY="300"/>
        </listPoints>
    </algorithmProperties>
</algorithmPrototype>
```

---

## 4. UpdateAlgorithmBspline

**Forma del moto:** percorso liscio campionato da una **spline cubica naturale** che passa per i
punti di controllo.

- **Sorgente:** [UpdateAlgorithmBspline.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgorithmBspline.java)
- **Rispetta il frame-rate:** **no** — avanza di **un punto precalcolato per frame**, ignorando
  `Speed` e `deltaSeconds`.
- **Chiavi:** `listpoints` (punti di controllo).

**Comportamento.** `init` costruisce una [`NatCubicSpline`](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/path/NatCubicSpline.java)
dai punti di controllo e precalcola l'elenco dei punti interpolati; ogni frame l'entità **salta** al
punto successivo. Esaurito il percorso, l'entità si ferma.

```
per ogni frame: position = prossimo punto precalcolato della spline
```

```
      .-'""'-.__
     /         "-._      (curva liscia attraverso i punti di controllo)
```

> **Nota frame-rate.** Poiché avanza di un punto per frame, a FPS diversi la velocità percepita
> cambia (a 60 FPS è il doppio che a 30). È l'algoritmo giusto quando conta la **forma** della
> traiettoria più della velocità precisa.

**Creazione (codice):** `newBspline(List<Point> points)`.

**XML (livello 1, `pathAlfa`):**

```xml
<algorithmPrototype name="pathAlfa"
    class="it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmBspline">
    <algorithmProperties>
        <listPoints name="listpoints">
            <point posX="1350" posY="325"/>
            <point posX="700" posY="100"/>
            <point posX="400" posY="450"/>
            <point posX="1100" posY="250"/>
            <point posX="700" posY="200"/>
            <point posX="-100" posY="100"/>
        </listPoints>
    </algorithmProperties>
</algorithmPrototype>
```

---

## 5. UpdateAlgoritmGoToPoint

> Il nome della classe è `UpdateAlgoritmGoToPoint` (refuso storico: manca la `h`).

**Forma del moto:** si muove in linea retta verso un **punto bersaglio fisso** a velocità costante.

- **Sorgente:** [UpdateAlgoritmGoToPoint.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgoritmGoToPoint.java)
- **Rispetta il frame-rate:** **sì**.
- **Chiavi:** `speedx`/`speedy` (velocità massime per asse), `point` (`Position` bersaglio).

**Comportamento.** Al primo frame calcola le velocità per asse in modo che i due assi raggiungano il
bersaglio **insieme**, limitando la componente verticale alla `speedy` massima; dopodiché avanza a
quelle velocità fisse fino al punto.

```
al 1° frame: calcola newXspeed, newYspeed (arrivo simultaneo sui due assi, clamp su max)
X += newXspeed * dt ;  Y += newYspeed * dt
```

```
[start] ------------> (X)   bersaglio
```

**Creazione (codice):** `newGoToPoint(maxSpeedx, maxSpeedy, Position target)`.

---

## 6. UpdateAlgoritmGoToPointIncreasingSpeed

> Nome classe `UpdateAlgoritmGoToPointIncreasingSpeed` (refuso storico: manca la `h`).

**Forma del moto:** come [GoToPoint](#5-updatealgoritmgotopoint), ma **accelerando** durante il
tragitto.

- **Sorgente:** [UpdateAlgoritmGoToPointIncreasingSpeed.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgoritmGoToPointIncreasingSpeed.java)
- **Rispetta il frame-rate:** **sì** per l'integrazione, ma l'accelerazione è **per-frame**
  (vedi nota).
- **Chiavi:** `speedx`/`speedy` (velocità iniziali), `point` (bersaglio).

**Comportamento.** Identico a GoToPoint per il calcolo iniziale, ma ogni frame moltiplica le
velocità per una piccola percentuale fissa (`increasingPercentage = 0.003`) prima di integrarle,
così l'entità accelera progressivamente.

```
newXspeed += newXspeed * 0.003 ;  newYspeed += newYspeed * 0.003
X += newXspeed * dt ;  Y += newYspeed * dt
```

> **Nota frame-rate.** L'incremento `* 0.003` è applicato **per frame**, non per secondo: la curva
> di accelerazione dipende dagli FPS anche se lo spostamento integra `dt`.

**Creazione (codice):** `newGoToPointIncr(maxSpeedx, maxSpeedy, Position target)`.

---

## 7. UpdateAlgoritmFollowSprite

> Nome classe `UpdateAlgoritmFollowSprite` (refuso storico: manca la `h`).

**Forma del moto:** insegue un'**entità bersaglio**, spingendosi verso la sua posizione corrente a
velocità per-asse fisse.

- **Sorgente:** [UpdateAlgoritmFollowSprite.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgoritmFollowSprite.java)
- **Rispetta il frame-rate:** **sì**.
- **Chiavi:** `sprite` (l'`Entity` da seguire).

**Comportamento.** Ogni frame confronta la propria posizione con quella del bersaglio e si sposta
nella sua direzione di `xSpeed`/`ySpeed` (fissi: `40`/`20` px/s) scalati per `dt`, un asse alla
volta.

```
se X > targetX -> X -= xSpeed*dt   altrimenti X += xSpeed*dt
se Y > targetY -> Y -= ySpeed*dt   altrimenti Y += ySpeed*dt
```

```
 o ~~~> @   (insegue il bersaglio)
```

> **Limite noto.** Non frena mai: essendo le velocità fisse e cambiando solo il segno, **oscilla**
> attorno al bersaglio invece di convergere. Va bene per un nemico "che ronza", non per un missile a
> ricerca (vedi la proposta *homing con turn-rate* in [algoritmi-proposti-e-composizione.md](algoritmi-proposti-e-composizione.md)).

**Creazione (codice):** `newFollowSprite(Sprite target)`.

---

## 8. UpdateAlgoritmCopyPosition

> Nome classe `UpdateAlgoritmCopyPosition` (refuso storico: manca la `h`).

**Forma del moto:** resta **agganciata** a un altro punto di riferimento, con un offset fisso
`(deltaX, deltaY)`.

- **Sorgente:** [UpdateAlgoritmCopyPosition.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/logic/UpdateAlgoritmCopyPosition.java)
- **Rispetta il frame-rate:** **ignora** `dt` e `Speed` (è un aggancio, non un moto).
- **Chiavi:** `deltax`/`deltay` (offset), `point` (`Position` di riferimento da copiare).

**Comportamento.** Ogni frame scatta la propria posizione a `riferimento + offset`. Serve a tenere
un'entità incollata a un punto mobile (es. un'appendice/parte che segue un corpo principale).

```
X = copyPoint.X + deltaX
Y = copyPoint.Y + deltaY
```

```
   [ref] +offset-> [entità]   (sempre a distanza fissa dal riferimento)
```

**Creazione (codice):** `newCopyPosition(deltax, deltay, Position copyPoint)`.

---

## 9. Quadro riassuntivo

| # | Algoritmo | Categoria | Usa `dt`? | Si ferma a fine? | Chiavi |
|---|---|---|---|---|---|
| 1 | `UpdateAlgorithmDefault` | moto libero | Sì | no (moto infinito) | — |
| 2 | `UpdateAlgorithmSinusoidal` | moto libero | Sì | no (moto infinito) | `delta`, `increment`, `start`(opz.) |
| 3 | `UpdateAlgorithmLinearPath` | percorso scriptato | Sì | sì (fine waypoint) | `listpoints` |
| 4 | `UpdateAlgorithmBspline` | percorso scriptato | **No** | sì (fine spline) | `listpoints` |
| 5 | `UpdateAlgoritmGoToPoint` | mira one-shot | Sì | no* (prosegue oltre il punto) | `speedx`, `speedy`, `point` |
| 6 | `UpdateAlgoritmGoToPointIncreasingSpeed` | mira one-shot | Sì | no* (prosegue accelerando) | `speedx`, `speedy`, `point` |
| 7 | `UpdateAlgoritmFollowSprite` | inseguimento | Sì | no (insegue sempre) | `sprite` |
| 8 | `UpdateAlgoritmCopyPosition` | aggancio | Ignora | no (segue sempre) | `deltax`, `deltay`, `point` |

> \* GoToPoint / GoToPointIncreasingSpeed non hanno una condizione esplicita di arresto sul
> bersaglio: raggiunto il punto, l'entità **continua** oltre alle velocità calcolate.
