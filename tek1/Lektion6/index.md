# 🧠 Assembly – Første Skridt (x86-64 Linux)

Små øvelser, der bygger direkte videre på det, vi har gennemgået i undervisningen.

Hver øvelse er kun **ét lille skridt videre** end demoen.

---

# 🔹 Demo 1 – Hello World

Vi lærte at:

- Bruge `write` (systemkald 1)
- Skrive bytes til stdout
- Afslutte programmet korrekt med `exit` (systemkald 60)
- Forstå at kernen printer præcis det antal bytes, vi angiver

---

## 🧩 Øvelse 1 – Print To Linjer

Ret programmet, så det printer:

```
Hello world!
Assembly er sjovt!
```

### Krav

- Du må kun bruge `write`
- Du skal justere byte-længden korrekt
- Output skal se pænt ud i terminalen

### Tænk over

- Hvor mange bytes printer du?
- Tæller newline (`0ah`) med?
- Vil du bruge ét eller to `write`-kald?

---

# 🔹 Demo 2 – Læg To Tal Sammen

Vi lavede et program, der:

- Lægger to tal sammen
- Konverterer resultatet til ASCII
- Printer resultatet

Eksempel:

```
3 + 5 = 8
```

---

## 🧩 Øvelse 2 – Print Hele Udtrykket

I stedet for kun at printe resultatet, skal programmet printe:

```
3 + 5 = 8
```

### Krav

- Tallene skal stadig beregnes (må ikke hardcodes)
- Du skal konvertere tal til ASCII
- Du må gerne bruge flere `write`-kald

### Hint

ASCII-cifre starter ved:

```
'0' = 48
```

Så:

```
5 + '0' → ASCII-tegnet '5'
```

---

# 🔹 Demo 3 – Løkke

Vi lavede en løkke, der printede tal i rækkefølge.

---

## 🧩 Øvelse 3 – Print Kun Lige Tal

Ret løkken, så den printer:

```
0 2 4 6 8
```

### Krav

- Du skal stadig bruge en løkke
- Du må ikke hardcode tallene
- Programmet skal stoppe korrekt

### Tænk over

- Kan du øge tælleren med 2?
- Eller kan du teste, om et tal er lige?
- Hvad bestemmer, hvornår løkken stopper?

---

# 🔹 Demo 4 – Funktion

Vi lavede en funktion:

```
add_five
```

og kaldte den med:

```
call add_five
```

Vi så, at returværdien ligger i `rax`.

---

## 🧩 Øvelse 4 – Kald Funktionen To Gange

Ret programmet, så det:

1. Starter med tallet `2`
2. Kalder `add_five`
3. Kalder `add_five` igen
4. Printer det endelige resultat

Forventet resultat:

```
12
```

### Tænk over

- Hvor ligger returværdien?
- Hvad sker der med `rax` efter første kald?
- Kan du genbruge værdien direkte?

---

# ✅ Mål

Efter disse øvelser bør du forstå:

- Systemkald (`write`, `exit`)
- Registre som databeholdere
- ASCII-konvertering
- Løkker
- Funktionskald (`call` / `ret`)
- Returværdier i `rax`

---

Hold det simpelt.  
Tænk i registre.  
Alt er bare bytes.
