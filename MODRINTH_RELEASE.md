# AdminManager - Guida Pubblicazione Modrinth

## Informazioni Base Progetto

### Titolo
**AdminManager**

### Slug
`adminmanager`

### Descrizione Breve (max 256 caratteri)
```
Plugin all-in-one per gestione server Minecraft con GUI moderne: giocatori, economia Vault, configurazioni, mondi, game rules, whitelist e statistiche. Supporto IT/EN completo.
```

### Categorie/Tags Suggerite
- `management`
- `administration`
- `economy`
- `utility`
- `server-tools`
- `gui`
- `vault`
- `moderation`

### Loaders Supportati
- ✅ **Spigot** (1.18 - 1.21+)
- ✅ **Paper** (1.18 - 1.21+)

### Versioni Minecraft Supportate
```
1.18, 1.18.1, 1.18.2
1.19, 1.19.1, 1.19.2, 1.19.3, 1.19.4
1.20, 1.20.1, 1.20.2, 1.20.3, 1.20.4, 1.20.5, 1.20.6
1.21, 1.21.1
```

### Licenza
**All Rights Reserved**

---

## Descrizione Completa per Modrinth

```markdown
# AdminManager

Plugin professionale all-in-one per la gestione completa del tuo server Minecraft! 🎮✨

## 🌟 Caratteristiche Principali

**AdminManager** fornisce una suite completa di strumenti amministrativi tramite interfacce grafiche moderne e intuitive. Tutto ciò di cui hai bisogno per gestire il tuo server in un unico plugin!

### 🎮 Server Manager - Centro di Controllo
Hub centrale con accesso a tutte le funzionalità amministrative:
- 🔄 **Reload/Restart/Stop Server** - Controllo completo del server
- 💎 **Economy Provider** - Integrazione Vault con statistiche globali
- 🗑️ **Clear Entities** - Pulizia entità in tutti i mondi
- 💾 **Save Worlds** - Salvataggio sicuro di tutti i mondi
- 📋 **Player Data** - Database completo di tutti i giocatori
- 📝 **Whitelist Manager** - Editor whitelist integrato
- ⚙️ **Game Rules** - Gestione regole per ogni mondo
- 📜 **Command Registration** - Sistema comandi personalizzati
- 📁 **Config Manager** - Gestione configurazioni centralizzata

⚠️ **Sicurezza**: Avvisi automatici per operazioni critiche (reload/save) con molti plugin e poca RAM

### 💰 Economy Manager (Vault)
Sistema economia professionale con:
- 📊 **Statistiche Globali** - Totale in circolazione, media, top/bottom giocatori
- 👥 **Vista Admin Completa** - Tutti i giocatori (online + offline)
- 💵 **Gestione Bilanci** - Aggiungi, rimuovi, imposta, azzera denaro
- 🔄 **Supporto Vault** - Compatibile con Essentials, CMI, EconomyAPI, ecc.

### 👥 Gestione Giocatori Avanzata
- **Lista Dinamica** - Visualizzazione con teste giocatori e paginazione
- **Player Data Manager** - Database persistente di tutti i giocatori
- **Dettagli Completi** - UUID, ping, mondo, IP, coordinate, statistiche
- **Azioni Admin** - Teleport, kick, ban, mute con logging automatico

### 🛠️ Azioni di Moderazione
- 🌀 **Teletrasporto Bidirezionale** - Admin → Giocatore e viceversa
- 👢 **Kick** - Espulsione con log automatico
- 🔨 **Ban** - Ban permanente nativo Minecraft
- 🔇 **Mute/Unmute** - Sistema persistente thread-safe con notifiche staff

### ⚙️ Configurazione Avanzata
- **Config Manager** - Ricarica e ripristino config.yml / tools.yml
- **Game Rules Manager** - Modifica game rules per ogni mondo
- **Whitelist Editor** - Toggle e gestione completa whitelist
- **Command Registration** - Sistema di comandi personalizzati per categorie

### 🌍 Sistema Multilingua 100%
- 🇮🇹 **Italiano** (it_IT) - Completo
- 🇬🇧 **Inglese** (en_EN) - Completo
- ✅ **Tutte le GUI tradotte** - 11 file di traduzione completi
- 🔄 **Cambio lingua istantaneo** - Senza riavvio server

### 📊 Statistiche bStats
8 grafici personalizzati integrati:
- Lingua plugin, server software, versione Minecraft
- Provider economia, versione Java
- Numero mondi personalizzati
- Sistema operativo server

### 📝 Logging Professionale
- **Log Automatici** - Kick, ban, mute con timestamp configurabile
- **Formato Personalizzabile** - Placeholder dinamici (timestamp, admin, azione, giocatore)
- **Timezone Support** - Configurazione timezone per log accurati
- **Persistenza** - File YAML per mute e configurazioni

### 🎨 Esperienza Professionale
- **Banner Startup** - Banner console professionale con status Vault
- **Comando Info** - `/adminm info` per informazioni plugin
- **Sistema Permessi** - Controlli completi con doppia verifica per operazioni sensibili
- **GUI Moderne** - Interfacce intuitive con icone significative

## 📦 Requisiti

- **Server**: Spigot o Paper 1.18-1.21+
- **Java**: 8 o superiore
- **Vault** (opzionale): Per funzionalità economia

## 🚀 Quick Start

1. Scarica il file `.jar`
2. Copia in `plugins/`
3. (Opzionale) Installa Vault + plugin economia
4. Riavvia il server
5. Usa `/adminm` per aprire il Server Manager!

## 🔐 Permessi

- `adminmanager.use` - Accesso base al plugin
- `adminmanager.reload` - Ricarica configurazione
- `adminmanager.notify.mute` - Notifiche mute

## 📚 Documentazione Completa

Consulta il [README su GitHub](https://github.com/yourusername/AdminManager) per:
- Guida dettagliata di tutte le funzionalità
- Esempi di configurazione
- Struttura file e API
- Guida rapida per ogni feature

## 🤝 Supporto

- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/yourusername/AdminManager/issues)
- 💡 **Feature Requests**: [GitHub Discussions](https://github.com/yourusername/AdminManager/discussions)
- 🌐 **Website**: [mclegacy.it](https://www.mclegacy.it)

## 📊 Privacy

Il plugin raccoglie statistiche anonime tramite bStats per migliorare lo sviluppo. Puoi disabilitarle in `plugins/bStats/config.yml`.

---

**AdminManager** - La soluzione completa per amministratori professionali! 🎮✨

Sviluppato con ❤️ da **AlessioGTA**
```

---

## Changelog Versione 1.0.0

```markdown
# AdminManager v1.0.0 - Release Iniziale

Prima release pubblica di AdminManager! 🎉

## ✨ Funzionalità Principali

### 🎮 Server Manager
- Centro di controllo completo del server
- Reload, restart, stop server con conferme
- Clear entities per tutti i mondi
- Save worlds con avvisi di sicurezza
- Accesso rapido a tutte le funzionalità admin

### 💰 Economy Manager (Vault Integration)
- Statistiche globali economia (totale, media, top/bottom)
- Vista admin con tutti i giocatori (online + offline)
- Gestione bilanci: aggiungi, rimuovi, imposta, azzera
- Supporto completo Vault API
- Compatibile con Essentials, CMI, EconomyAPI

### 👥 Player Management
- Lista giocatori dinamica con teste e paginazione
- Player Data Manager con database completo
- Dettagli giocatore: UUID, ping, mondo, IP, coordinate
- Azioni admin: teleport, kick, ban, mute
- Sistema mute persistente thread-safe

### ⚙️ Configuration & Tools
- Config Manager per config.yml e tools.yml
- Game Rules Manager per tutti i mondi
- Whitelist Editor integrato
- Command Registration system
- Ricarica configurazioni in tempo reale

### 🌍 Localizzazione
- Sistema multilingua completo (IT/EN)
- 100% GUI tradotte (11 file di traduzione)
- Cambio lingua istantaneo
- Supporto placeholder dinamici

### 📊 Analytics & Logging
- Integrazione bStats con 8 grafici personalizzati
- Sistema logging professionale (kick, ban, mute)
- Timestamp configurabili con timezone
- Persistenza dati su file YAML

### 🎨 User Experience
- Banner startup professionale con Vault status
- Comando `/adminm info` per info plugin
- GUI moderne e intuitive
- Avvisi di sicurezza per operazioni critiche

## 🔒 Sicurezza
- Sistema permessi completo
- Doppio controllo per operazioni sensibili
- Avvisi automatici per operazioni rischiose (reload/save con molti plugin)
- Thread-safe operations

## 🔧 Miglioramenti Tecnici
- Architettura modulare e manutenibile
- Thread-safe MuteManager
- Gestione memoria ottimizzata
- Supporto Paper 1.18-1.21+

## 📦 Requisiti
- Minecraft Server: Spigot/Paper 1.18-1.21+
- Java: 8+
- Vault (opzionale): Per funzionalità economia

## 🚀 Installazione
1. Scarica il JAR
2. Copia in `plugins/`
3. Opzionale: Installa Vault + economia plugin
4. Riavvia server
5. Usa `/adminm` in-game!

## 🐛 Bug Fixes
Nessuno - Prima release!

## 📝 Note
- Testato su Spigot/Paper 1.18-1.21
- Supporto completo Vault opzionale
- Tutte le funzionalità non-economy disponibili senza Vault

---

**Grazie per aver scelto AdminManager!** 🎮✨

Per supporto, documentazione completa e aggiornamenti:
- 📚 README: https://github.com/yourusername/AdminManager
- 🐛 Issues: https://github.com/yourusername/AdminManager/issues
- 🌐 Website: https://www.mclegacy.it
```

---

## Immagini/Screenshot Suggeriti

Per una pubblicazione professionale su Modrinth, si consiglia di includere screenshot di:

1. **Server Manager GUI** - Schermata principale
2. **Economy Manager** - Vista statistiche globali
3. **Economy Manager** - Vista admin con lista giocatori
4. **Player Data Manager** - Lista completa giocatori
5. **Player Management** - Dettagli singolo giocatore con azioni
6. **Whitelist Editor** - Gestione whitelist
7. **Game Rules Manager** - Gestione regole di gioco
8. **Config Manager** - Gestione configurazioni
9. **Multilingual Support** - Confronto IT/EN della stessa GUI
10. **Startup Banner** - Console con banner professionale

---

## Checklist Pre-Pubblicazione

### File da Preparare
- [ ] `AdminManager-1.0.0.jar` - File plugin compilato
- [ ] Screenshot (almeno 3-5 immagini)
- [ ] Banner/Logo (opzionale, dimensioni suggerite: 1280x640px)
- [ ] Icon progetto (opzionale, dimensioni: 256x256px)

### Informazioni da Completare
- [ ] URL GitHub repository (se pubblico)
- [ ] URL Issues tracker
- [ ] Discord server (opzionale)
- [ ] Link documentazione aggiuntiva

### Configurazione Modrinth
- [ ] Crea progetto su Modrinth
- [ ] Imposta slug: `adminmanager`
- [ ] Seleziona categorie appropriate
- [ ] Configura loaders: Spigot, Paper
- [ ] Seleziona versioni Minecraft supportate
- [ ] Imposta licenza: All Rights Reserved
- [ ] Carica descrizione completa
- [ ] Carica screenshot
- [ ] Carica banner/logo (se disponibili)

### Upload Release
- [ ] Carica file `AdminManager-1.0.0.jar`
- [ ] Imposta version number: `1.0.0`
- [ ] Imposta version name: `v1.0.0 - Release Iniziale`
- [ ] Tipo release: `release`
- [ ] Incolla changelog versione 1.0.0
- [ ] Seleziona loaders compatibili
- [ ] Seleziona tutte le versioni Minecraft supportate
- [ ] Aggiungi dipendenze opzionali: Vault
- [ ] Pubblica!

---

## Note Aggiuntive

### Dipendenze Opzionali
Su Modrinth, assicurati di indicare **Vault** come dipendenza opzionale:
- **Nome**: Vault
- **Tipo**: Opzionale
- **Descrizione**: "Richiesto solo per funzionalità economia. Senza Vault tutte le altre funzionalità rimangono disponibili."

### Tags Suggeriti per SEO
Oltre alle categorie, nel corpo della descrizione usa keywords come:
- Server management
- Player administration
- Economy plugin
- Vault integration
- GUI-based
- Multilingual
- Italian support
- Server tools
- Moderation tools
- Whitelist management

### Link Utili Modrinth
- **Dashboard**: https://modrinth.com/dashboard/projects
- **Docs Upload**: https://docs.modrinth.com/docs/tutorials/uploading/
- **Formatting Guide**: https://docs.modrinth.com/docs/tutorials/markdown/

---

**Buona pubblicazione!** 🚀
