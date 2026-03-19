# Seen-Software-Engineering-project
"Seen - Film & TV Series Tracker" - un'app Android per gestire e recensire film e serie TV - Progetto Software completo - Requisiti - Analisi - Progettazione

## Documentazione

Questo progetto include la documentazione completa del processo di sviluppo software:

- **Requisiti**: Analisi dei requisiti funzionali e non funzionali
- **Analisi**: Studio del dominio e dei casi d'uso
- **Progettazione**: Architettura e design dell'applicazione

[Progettazione Software.pdf](./Progettazione%20Software.pdf)

## 🔗 Repository

[GitHub Repository](https://github.com/andreapassini03-ctrl/Seen-Software-Engineering-project)

## Funzionalità

- Registrazione e autenticazione utenti
- Catalogo completo di film e serie TV
- Area personale per gestire i contenuti
- Tracciamento dello stato di visione (visto/non visto)
- Sistema di recensioni con valutazione e commenti
- Recensioni per film, serie TV ed episodi singoli
- Segnalazione di recensioni inappropriate
- Organizzazione per sezioni (Film/Serie TV)

## Tecnologie

- **Kotlin** - Linguaggio di programmazione
- **Jetpack Compose** - UI moderna e dichiarativa
- **Room Database** - Persistenza locale dei dati
- **MVVM Pattern** - Architettura con ViewModel
- **Navigation Compose** - Gestione della navigazione
- **Coroutines** - Programmazione asincrona

## How to Run

### Prerequisiti

- Android Studio (versione Hedgehog o successiva)
- JDK 11 o superiore
- Android SDK (API 25 o superiore)
- Dispositivo Android o Emulatore (API 25+)

### Passi per l'esecuzione

1. **Clone del repository**
   ```bash
   git clone https://github.com/andreapassini03-ctrl/Seen-Software-Engineering-project.git
   cd Seen-Software-Engineering-project
   ```

2. **Apri il progetto in Android Studio**
   - Apri Android Studio
   - Seleziona "Open an existing project"
   - Naviga alla cartella del progetto clonato

3. **Sync delle dipendenze**
   - Android Studio sincronizzerà automaticamente le dipendenze Gradle
   - Attendi il completamento del processo

4. **Configura un dispositivo**
   - Collega un dispositivo Android fisico con USB debugging abilitato, oppure
   - Crea un emulatore Android (AVD) tramite Device Manager

5. **Run dell'applicazione**
   - Clicca sul pulsante "Run" (▶️) in Android Studio
   - Seleziona il dispositivo target
   - L'app verrà installata e avviata automaticamente

### Note

- Al primo avvio, il database viene popolato automaticamente con contenuti di esempio
- Credenziali di test disponibili dopo il popolamento del database
- L'app richiede Android 7.0 (API 25) o superiore

## 📂 Struttura del Progetto

```
app/src/main/java/com/example/progetto_software/
├── components/      # Componenti UI riutilizzabili
├── controller/      # ViewModel e logica di presentazione
├── data/           # Entità del dominio
├── database/       # Room Database, DAO e popolamento
├── screen/         # Schermate dell'applicazione
└── theme/          # Temi e stili UI
```

## 👥 Autori

Passini Andrea
Binni Francesco
Manoni Andrea

## 📄 Licenza

Progetto Progetto accademico di Software Engineering T
