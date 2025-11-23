# AdminManager

![Version](https://img.shields.io/badge/version-1.0--SNAPSHOT-blue)
![Minecraft](https://img.shields.io/badge/minecraft-1.18-green)
![Java](https://img.shields.io/badge/java-8-orange)

Un plugin Spigot completo per la gestione amministrativa dei giocatori con interfaccia grafica intuitiva.

## 📋 Descrizione

**AdminManager** è un plugin per server Minecraft Spigot 1.18 che fornisce agli amministratori strumenti potenti e facili da usare per gestire i giocatori tramite GUI interattive. Il plugin include sistema di logging, supporto multilingua e funzionalità complete di moderazione.

## ✨ Funzionalità

### 🎮 Gestione Giocatori
- **Lista Giocatori Interattiva** - Visualizza tutti i giocatori online con le loro teste
- **Informazioni Dettagliate** per ogni giocatore:
  - UUID
  - Ping in tempo reale
  - Mondo corrente
  - Indirizzo IP
  - Coordinate (X, Y, Z)

### 🛠️ Azioni Amministrative
- **Teletrasporto**
  - Teletrasportati da un giocatore
  - Teletrasporta un giocatore da te
- **Kick** - Espelli giocatori dal server con log automatico
- **Ban** - Banna permanentemente giocatori con sistema nativo Minecraft
- **Mute/Unmute** - Sistema di mute persistente con:
  - Blocco automatico della chat
  - Notifiche allo staff
  - Toggle dinamico mute/unmute
  - Salvataggio su file YAML

### 📝 Sistema di Logging
Tutti i log vengono salvati in `plugins/AdminManager/LOG/`:
- `player_kick.log` - Registro di tutti i kick
- `player_ban.log` - Registro di tutti i ban
- `mute_players.yml` - Lista giocatori mutati (persistente)

Formato log personalizzabile con:
- Timestamp configurabile
- Timezone selezionabile
- Formato: `[TIMESTAMP] Admin 'admin_name' -> Action on 'player_name'`

### 🌍 Multilingua
Supporto completo per più lingue:
- 🇮🇹 **Italiano** (it_IT) - Default
- 🇬🇧 **Inglese** (en_EN)

Tutte le stringhe sono tradotte e personalizzabili tramite file YAML in `locale/`.

### ⚙️ Configurazione
Sistema di configurazione completo in `config.yml`:
- Selezione lingua
- Colori GUI personalizzabili
- Formato log configurabile
- Timezone per i timestamp
- Permessi personalizzabili

## 📦 Requisiti

- **Minecraft Server**: Spigot/Paper 1.18+
- **Java**: 8 o superiore
- **Maven**: 3.6+ (per compilare)

## 🔧 Installazione

1. **Scarica** il file `.jar` compilato
2. **Copia** il file in `plugins/` della tua directory server
3. **Riavvia** il server
4. **Configura** il file `plugins/AdminManager/config.yml` (opzionale)
5. **Ricarica** con `/adminm reload`

## 🚀 Comandi

| Comando | Descrizione | Permesso |
|---------|-------------|----------|
| `/adminm` | Apre la GUI principale con lista giocatori | `adminmanager.use` |
| `/adminm reload` | Ricarica configurazione e traduzioni | `adminmanager.reload` |

## 🔐 Permessi

| Permesso | Descrizione | Default |
|----------|-------------|---------|
| `adminmanager.use` | Accesso al plugin e alle GUI | op |
| `adminmanager.reload` | Permesso per ricaricare il plugin | op |
| `adminmanager.notify.mute` | Ricevi notifiche quando giocatori mutati tentano di parlare | op |

## ⚙️ Configurazione

Esempio di `config.yml`:

```yaml
# Lingua del plugin (it_IT o en_EN)
language: it_IT

# Configurazione GUI
gui:
  panel_color: BLACK_STAINED_GLASS_PANE
  fill_empty_slots: true

# Configurazione Log
log:
  format: "[%timestamp%] Admin '%admin%' -> %action% on '%player%'"
  timezone: Europe/Rome

# Permessi personalizzati
permissions:
  use: adminmanager.use
  reload: adminmanager.reload
```

## 🛠️ Build da Sorgente

### Clona il Repository
```bash
git clone <repository-url>
cd AdminManager
```

### Compila con Maven
```bash
mvn clean package
```

Il file `.jar` compilato sarà disponibile in `target/AdminManager-1.0-SNAPSHOT.jar`

### Dipendenze
- Spigot API 1.18-R0.1-SNAPSHOT

## 📂 Struttura File

```
AdminManager/
├── src/main/
│   ├── java/it/alessiogta/adminmanager/
│   │   ├── AdminManager.java          # Classe principale
│   │   ├── commands/
│   │   │   ├── AdminManagerCommand.java
│   │   │   └── AdminManagerTabCompleter.java
│   │   ├── gui/
│   │   │   ├── BaseGui.java           # Classe base GUI
│   │   │   ├── GuiManager.java
│   │   │   ├── PlayerListGui.java     # Lista giocatori
│   │   │   └── PlayerManage.java      # Gestione giocatore
│   │   ├── listeners/
│   │   │   └── ChatListener.java      # Listener chat per mute
│   │   └── utils/
│   │       ├── GuiUtils.java
│   │       ├── MuteManager.java       # Gestione mute
│   │       ├── PlayerLogger.java      # Sistema logging
│   │       └── TranslationManager.java # Sistema traduzioni
│   └── resources/
│       ├── config.yml
│       ├── plugin.yml
│       └── locale/
│           ├── en_EN/
│           │   ├── PlayerListGui.yml
│           │   └── PlayerManage.yml
│           └── it_IT/
│               ├── PlayerListGui.yml
│               └── PlayerManage.yml
└── pom.xml
```

## 🔄 Changelog Recente

### Versione 1.0-SNAPSHOT (Ultima)

**🐛 Bug Fixes:**
- ✅ Fixato bug critico di compilazione - MuteManager mancante
- ✅ Fixato bug visualizzazione mondo - ora mostra il mondo corretto del giocatore
- ✅ Rimossa variabile inutilizzata `muteCheckCounter`

**✨ Nuove Funzionalità:**
- ✅ Sistema Mute/Unmute completo e funzionante
  - Persistenza su file YAML
  - Blocco automatico chat
  - Toggle dinamico nella GUI
  - Notifiche allo staff
  - Thread-safe con ReadWriteLock
- ✅ Bottone Mute/Unmute nella GUI di gestione giocatore
- ✅ Traduzioni inglesi complete
- ✅ Sistema di reload per MuteManager

**🌍 Traduzioni:**
- ✅ Completate tutte le traduzioni inglesi mancanti
- ✅ Aggiornate stringhe per sistema mute

**📚 Documentazione:**
- ✅ README completo con tutte le funzionalità
- ✅ Documentazione API inline

## 🎯 Utilizzo

### Aprire la GUI
1. Esegui `/adminm` in-game
2. Clicca sulla testa di un giocatore per gestirlo
3. Seleziona l'azione desiderata:
   - **Ender Pearl** - Teletrasportati dal giocatore
   - **Bussola** - Teletrasporta il giocatore da te
   - **Porta di Ferro** - Kick dal server
   - **Stendardo Rosso** - Ban permanente
   - **Colorante Grigio/Verde** - Mute/Unmute giocatore
   - **Porta di Quercia Scura** - Torna indietro

### Sistema Mute
- I giocatori mutati non possono scrivere in chat
- Ricevono un messaggio quando tentano di parlare
- Gli admin con permesso `adminmanager.notify.mute` ricevono notifiche
- Il mute persiste anche dopo il riavvio del server
- Toggle facile: clicca il bottone per mutare/smutare

## 🤝 Contribuire

Contributi, issues e feature requests sono benvenuti!

## 📝 Licenza

Questo progetto è un progetto personale sviluppato per server Minecraft privati.

## 👤 Autore

**alessiogta**

## 🙏 Riconoscimenti

- Spigot API per il framework
- Minecraft community

---

**Nota**: Questo plugin è stato sviluppato e testato su Spigot 1.18. La compatibilità con altre versioni non è garantita ma probabile per versioni successive.
