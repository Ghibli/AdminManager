# AdminManager

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Minecraft](https://img.shields.io/badge/minecraft-1.18--1.21-green)
![Java](https://img.shields.io/badge/java-8+-orange)
![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red)

Un plugin Spigot/Paper completo e professionale per la gestione amministrativa del server con interfacce grafiche intuitive, supporto economia, statistiche avanzate e molto altro.

## 📋 Descrizione

**AdminManager** è un plugin all-in-one per server Minecraft Spigot/Paper che fornisce agli amministratori una suite completa di strumenti professionali per gestire ogni aspetto del server tramite GUI moderne e intuitive. Include gestione giocatori, economia, configurazioni, mondi, regole di gioco, whitelist, comandi personalizzati e sistema di statistiche integrato.

## ✨ Funzionalità Principali

### 🎮 Server Manager
Centro di controllo completo del server con accesso a tutte le funzionalità amministrative:

- **🔄 Reload Server** - Ricarica tutti i plugin del server
  - ⚠️ Avviso di sicurezza per operazioni con molti plugin e poca RAM
- **🔁 Restart Server** - Riavvia completamente il server (richiede script esterno)
- **🛑 Stop Server** - Arresta il server in sicurezza
- **💎 Economy Provider** - Gestione completa economia Vault
  - Visualizza provider attivo
  - Informazioni su valuta e priorità
  - Accesso rapido a Economy Manager
- **🗑️ Clear Entities** - Rimuovi tutte le entità (mob, item, ecc.)
- **💾 Save Worlds** - Salva tutti i mondi del server
  - ⚠️ Avviso di sicurezza per operazioni con molti plugin e poca RAM
- **📋 Player Data** - Accesso completo ai dati di tutti i giocatori
- **📝 Whitelist Manager** - Gestione whitelist con editor integrato
- **⚙️ Game Rules** - Gestione regole di gioco per ogni mondo
- **📜 Command Registration** - Registra e gestisci comandi personalizzati
- **📁 Config Manager** - Gestione configurazioni del plugin

### 💰 Economy Manager (Integrazione Vault)
Sistema di gestione economia completo con statistiche globali:

- **📊 Statistiche Globali Economy**
  - Totale denaro in circolazione nel server
  - Media denaro per giocatore
  - Giocatore più ricco
  - Giocatore più povero
- **👥 Vista Admin Economia**
  - Lista completa di tutti i giocatori (online + offline)
  - Bilanci di tutti i giocatori del server
  - Ricerca e navigazione facilitata
- **💵 Gestione Bilanci Individuali**
  - Visualizza saldo corrente
  - Aggiungi denaro
  - Rimuovi denaro
  - Imposta saldo specifico
  - Azzera saldo completamente

### 👥 Gestione Giocatori Avanzata

#### Lista Giocatori Interattiva
- Visualizzazione con teste giocatori dinamiche
- Paginazione automatica per server con molti giocatori
- Informazioni dettagliate per ogni giocatore:
  - UUID
  - Ping in tempo reale
  - Mondo corrente
  - Indirizzo IP
  - Coordinate (X, Y, Z)
  - Gamemode
  - Salute e fame
  - Livello esperienza

#### Player Data Manager
- **📚 Database Completo Giocatori**
  - Lista di tutti i giocatori che hanno giocato sul server
  - Dati persistenti anche per giocatori offline
  - Paginazione e navigazione facilitata

- **🔍 Dettagli Giocatore Completi**
  - Informazioni base (UUID, nome, stato)
  - Statistiche di gioco
  - Ultimo accesso con formato leggibile (giorni, ore, minuti, secondi)
  - Stato online/offline in tempo reale

### 🛠️ Azioni Amministrative

- **🌀 Teletrasporto Bidirezionale**
  - Teletrasportati da un giocatore
  - Teletrasporta un giocatore da te
- **👢 Kick** - Espelli giocatori dal server con log automatico
- **🔨 Ban** - Banna permanentemente giocatori con sistema nativo Minecraft
- **🔇 Mute/Unmute** - Sistema di mute persistente con:
  - Blocco automatico della chat
  - Notifiche allo staff
  - Toggle dinamico mute/unmute
  - Salvataggio persistente su file YAML
  - Sincronizzazione thread-safe

### ⚙️ Configurazione Avanzata

#### Config Manager GUI
- **Gestione config.yml**
  - Ricarica configurazione in tempo reale
  - Ripristina valori predefiniti
- **Gestione tools.yml**
  - Configurazione strumenti personalizzati
  - Ricarica e ripristino facilitati

#### Game Rules Manager
- **Selettore Mondi**
  - Lista di tutti i mondi del server
  - Accesso rapido alle regole di ogni mondo
- **Gestione Game Rules**
  - Visualizza e modifica tutte le regole di gioco
  - Toggle rapido per regole booleane
  - Supporto per tutte le game rules di Minecraft

#### Whitelist Editor
- **Gestione Completa Whitelist**
  - Toggle whitelist globale on/off
  - Aggiungi giocatori alla whitelist
  - Rimuovi giocatori dalla whitelist
  - Visualizza lista completa giocatori whitelisted
  - Sincronizzazione con lista whitelist nativa Minecraft

#### Command Registration
- **Registrazione Comandi Personalizzati**
  - Organizzazione per categorie
  - Supporto comandi personalizzati
  - Gestione permessi

### 📝 Sistema di Logging Professionale

Tutti i log vengono salvati in `plugins/AdminManager/LOG/`:
- `player_kick.log` - Registro completo di tutti i kick
- `player_ban.log` - Registro completo di tutti i ban
- `mute_players.yml` - Lista giocatori mutati (persistente tra riavvii)

**Formato log personalizzabile:**
- Timestamp configurabile con timezone
- Formato: `[TIMESTAMP] Admin 'admin_name' -> Action on 'player_name'`
- Supporto per diversi timezone (Europe/Rome, UTC, ecc.)

### 🌍 Sistema Multilingua Completo

Supporto completo per più lingue con traduzione al 100% di tutte le interfacce:
- 🇮🇹 **Italiano** (it_IT) - Lingua predefinita
- 🇬🇧 **Inglese** (en_EN) - Traduzione completa

**Tutte le GUI sono completamente tradotte:**
- PlayerListGui - Lista giocatori
- PlayerManage - Gestione giocatore
- ServerManager - Gestione server
- EconomyManager - Gestione economia
- PlayerData - Dati giocatori
- PlayerDataDetail - Dettagli giocatore
- ConfigManager - Gestione configurazioni
- CommandCategory - Categorie comandi
- GameRules - Regole di gioco
- WhitelistEditor - Editor whitelist
- WorldSelector - Selettore mondi

Tutte le stringhe sono personalizzabili tramite file YAML in `locale/[lingua]/`.

### 📊 Statistiche bStats Integrate

Sistema di metriche anonime con **8 grafici personalizzati**:
- **Plugin Language** - Distribuzione lingue utilizzate
- **Server Software** - Tipologia server (Spigot, Paper, ecc.)
- **Minecraft Version** - Versioni Minecraft utilizzate
- **Economy Provider** - Provider economia installati
- **Java Version** - Versioni Java utilizzate
- **Custom Worlds Range** - Range numero mondi personalizzati
- **Total Custom Worlds** - Numero totale mondi
- **Operating System** - Sistemi operativi server

### 🎨 Banner di Startup Professionale

All'avvio del plugin viene mostrato un banner professionale nella console:
```
=============================================

   Admin Manager (v1.0.0)
   Developed with ♥ by AlessioGTA

   The plugin that helps you manage your server!

   Vault Hook ✓ / ✗

=============================================
```

### 🔐 Sistema Permessi Completo

| Permesso | Descrizione | Default |
|----------|-------------|---------|
| `adminmanager.use` | Accesso base al plugin e tutte le GUI | op |
| `adminmanager.reload` | Permesso per ricaricare il plugin | op |
| `adminmanager.notify.mute` | Ricevi notifiche quando giocatori mutati tentano di parlare | op |

**Sicurezza:**
- Controllo permessi su tutti i comandi
- Doppio controllo per operazioni sensibili (es. reload richiede `adminmanager.use` + `adminmanager.reload`)

## 📦 Requisiti

- **Minecraft Server**: Spigot/Paper 1.18 - 1.21+
- **Java**: 8 o superiore
- **Vault** (opzionale): Per funzionalità economia
  - Richiede un plugin economia (Essentials, CMI, EconomyAPI, ecc.)
- **Maven**: 3.6+ (solo per compilare da sorgente)

## 🔧 Installazione

1. **Scarica** il file `.jar` da [Modrinth](https://modrinth.com/plugin/adminmanager)
2. **Copia** il file in `plugins/` della tua directory server
3. **(Opzionale)** Installa Vault + plugin economia per funzionalità economia
4. **Riavvia** il server
5. **Configura** il file `plugins/AdminManager/config.yml` (opzionale)
6. **Ricarica** con `/adminm reload` (opzionale)

## 🚀 Comandi

| Comando | Descrizione | Permesso |
|---------|-------------|----------|
| `/adminm` | Apre la GUI principale Server Manager | `adminmanager.use` |
| `/adminm reload` | Ricarica configurazione e traduzioni | `adminmanager.use` + `adminmanager.reload` |
| `/adminm info` | Mostra informazioni sul plugin | `adminmanager.use` |

## ⚙️ Configurazione

### Esempio config.yml

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

### Opzioni Disponibili

#### Lingua
- `it_IT` - Italiano (default)
- `en_EN` - Inglese

#### GUI
- `panel_color` - Colore pannelli decorativi (qualsiasi materiale vetro colorato)
- `fill_empty_slots` - Riempi slot vuoti con pannelli (true/false)

#### Log
- `format` - Formato log personalizzato con placeholder
  - `%timestamp%` - Data/ora azione
  - `%admin%` - Nome amministratore
  - `%action%` - Azione eseguita
  - `%player%` - Giocatore target
- `timezone` - Timezone per i timestamp (es. Europe/Rome, UTC, America/New_York)

## 🛠️ Build da Sorgente

### Clona il Repository
```bash
git clone https://github.com/yourusername/AdminManager.git
cd AdminManager
```

### Compila con Maven
```bash
mvn clean package
```

Il file `.jar` compilato sarà disponibile in `target/AdminManager-1.0.0.jar`

### Dipendenze
- Spigot API 1.18-R0.1-SNAPSHOT
- Vault API 1.7 (soft dependency)

## 📂 Struttura File

```
AdminManager/
├── src/main/
│   ├── java/it/alessiogta/adminmanager/
│   │   ├── AdminManager.java              # Classe principale
│   │   ├── commands/
│   │   │   ├── AdminManagerCommand.java
│   │   │   └── AdminManagerTabCompleter.java
│   │   ├── gui/
│   │   │   ├── BaseGui.java               # Classe base GUI
│   │   │   ├── GuiManager.java
│   │   │   ├── PlayerListGui.java         # Lista giocatori
│   │   │   ├── PlayerManage.java          # Gestione giocatore
│   │   │   ├── ServerManagerGui.java      # Gestione server
│   │   │   ├── EconomyManagerGui.java     # Gestione economia
│   │   │   ├── PlayerDataGui.java         # Lista dati giocatori
│   │   │   ├── PlayerDataDetailGui.java   # Dettagli giocatore
│   │   │   ├── ConfigManagerGui.java      # Gestione configurazioni
│   │   │   ├── CommandCategoryGui.java    # Categorie comandi
│   │   │   ├── GameRulesGui.java          # Regole di gioco
│   │   │   ├── WhitelistEditorGui.java    # Editor whitelist
│   │   │   └── WorldSelectorGui.java      # Selettore mondi
│   │   ├── listeners/
│   │   │   ├── ChatListener.java          # Listener chat per mute
│   │   │   └── GuiClickListener.java      # Listener click GUI
│   │   └── utils/
│   │       ├── GuiUtils.java
│   │       ├── MuteManager.java           # Gestione mute
│   │       ├── PlayerLogger.java          # Sistema logging
│   │       ├── TranslationManager.java    # Sistema traduzioni
│   │       ├── EconomyManager.java        # Gestione economia Vault
│   │       └── Metrics.java               # bStats integration
│   └── resources/
│       ├── config.yml
│       ├── plugin.yml
│       └── locale/
│           ├── en_EN/                     # Traduzioni inglesi
│           │   ├── PlayerListGui.yml
│           │   ├── PlayerManage.yml
│           │   ├── ServerManager.yml
│           │   ├── EconomyManager.yml
│           │   ├── PlayerData.yml
│           │   ├── PlayerDataDetail.yml
│           │   ├── ConfigManager.yml
│           │   ├── CommandCategory.yml
│           │   ├── GameRules.yml
│           │   ├── WhitelistEditor.yml
│           │   └── WorldSelector.yml
│           └── it_IT/                     # Traduzioni italiane
│               └── [stessi file di en_EN]
└── pom.xml
```

## 🎯 Guida Rapida

### 1. Gestione Server
- Esegui `/adminm` in-game
- Clicca su qualsiasi icona per accedere alle funzionalità:
  - **Barrier** - Stop server
  - **Redstone** - Restart server
  - **Lime Dye** - Reload server
  - **Emerald** - Economy Provider / Manager
  - **Chest** - Player Data
  - **Comparator** - Game Rules
  - **Grass Block** - Save Worlds
  - **Fire Charge** - Clear Entities
  - **Writable Book** - Config Manager
  - **Command Block** - Command Registration
  - **Dye** - Whitelist toggle/editor

### 2. Gestione Economia
- Da Server Manager, clicca su **Economy Provider**
- Visualizza statistiche globali server
- Clicca su un giocatore per gestire il suo saldo
- Usa i pulsanti per aggiungere, rimuovere o impostare denaro

### 3. Gestione Giocatori
- Da Server Manager, clicca su **Player Data**
- Naviga la lista completa di tutti i giocatori
- Clicca su un giocatore per vedere dettagli completi
- Azioni disponibili su giocatori online:
  - **Ender Pearl** - Teletrasportati dal giocatore
  - **Compass** - Teletrasporta il giocatore da te
  - **Iron Door** - Kick dal server
  - **Red Banner** - Ban permanente
  - **Gray/Green Dye** - Mute/Unmute giocatore

### 4. Sistema Mute
- I giocatori mutati non possono scrivere in chat
- Ricevono un messaggio quando tentano di parlare
- Gli admin con `adminmanager.notify.mute` ricevono notifiche
- Il mute persiste tra riavvii del server
- Toggle facile dalla GUI di gestione giocatore

### 5. Gestione Whitelist
- Clicca sul pulsante Whitelist in Server Manager
- **LEFT CLICK** - Toggle whitelist on/off
- **SHIFT + RIGHT CLICK** - Apri editor whitelist
- Nell'editor: aggiungi o rimuovi giocatori

### 6. Game Rules
- Clicca su Game Rules in Server Manager
- Seleziona il mondo da gestire
- Visualizza e modifica tutte le game rules
- Le modifiche sono applicate immediatamente

## ⚠️ Note Importanti

- **Operazioni Critiche**: Reload Server e Save Worlds mostrano avvisi di sicurezza. Con molti plugin e poca RAM, il server potrebbe riavviarsi inaspettatamente.
- **Vault Opzionale**: Le funzionalità economia richiedono Vault + un plugin economia. Senza Vault, tutte le altre funzionalità rimangono disponibili.
- **Compatibilità**: Testato su Spigot/Paper 1.18-1.21. Dovrebbe funzionare su versioni successive.
- **Backup**: Si consiglia di effettuare backup regolari prima di operazioni critiche.

## 📊 Privacy & Statistiche

Questo plugin raccoglie statistiche anonime tramite bStats per aiutare lo sviluppo. Le statistiche includono:
- Versione plugin e server
- Numero giocatori
- Lingua utilizzata
- Sistema operativo server
- Versione Java

Puoi disabilitare bStats nel file `plugins/bStats/config.yml` (generato automaticamente).

## 🤝 Supporto & Contributi

- **Issues**: Segnala bug e problemi su GitHub Issues
- **Feature Requests**: Suggerisci nuove funzionalità
- **Contributi**: Pull requests sono benvenute!

## 📝 Licenza

Tutti i diritti riservati © 2024 AlessioGTA

Questo plugin è distribuito come freeware per uso privato su server Minecraft. È vietata la redistribuzione, modifica o uso commerciale senza permesso esplicito dell'autore.

## 👤 Autore

**AlessioGTA**
- Website: [mclegacy.it](https://www.mclegacy.it)
- Modrinth: [AdminManager](https://modrinth.com/plugin/adminmanager)

## 🙏 Riconoscimenti

- **Spigot API** - Framework per plugin Bukkit/Spigot
- **Vault API** - Sistema economia unificato
- **bStats** - Sistema metriche per plugin
- **Minecraft Community** - Supporto e feedback

## 📈 Changelog

### Versione 1.0.0 (Release Iniziale)

**✨ Funzionalità Principali:**
- ✅ Server Manager completo con 10+ funzionalità
- ✅ Economy Manager con statistiche globali e gestione bilanci
- ✅ Player Data Manager con database completo giocatori
- ✅ Sistema Mute/Unmute persistente e thread-safe
- ✅ Config Manager per gestione configurazioni
- ✅ Game Rules Manager per tutti i mondi
- ✅ Whitelist Editor integrato
- ✅ Command Registration system
- ✅ Sistema multilingua completo (IT/EN) con 100% traduzioni
- ✅ Sistema logging professionale
- ✅ Integrazione bStats con 8 grafici personalizzati
- ✅ Banner di startup professionale con Vault status
- ✅ Comando /adminm info per informazioni plugin

**🔒 Sicurezza:**
- ✅ Sistema permessi completo su tutti i comandi
- ✅ Doppio controllo su operazioni sensibili
- ✅ Avvisi di sicurezza per operazioni critiche (reload, save)

**🌍 Localizzazione:**
- ✅ Italiano (it_IT) - Completo
- ✅ Inglese (en_EN) - Completo
- ✅ Tutte le GUI 100% tradotte
- ✅ Sistema traduzioni con placeholder dinamici

**🔧 Miglioramenti Tecnici:**
- ✅ Architettura modulare e manutenibile
- ✅ Thread-safe operations per MuteManager
- ✅ Gestione memoria ottimizzata
- ✅ Supporto Paper 1.18-1.21+

---

**AdminManager** - Il plugin completo per la gestione professionale del tuo server Minecraft! 🎮✨
