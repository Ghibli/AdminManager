# AdminManager - Modrinth Release Guide

## Project Basic Information

### Title
**AdminManager**

### Slug
`adminmanager`

### Short Description (max 256 characters)
```
All-in-one server management plugin with modern GUIs: players, Vault economy, configs, worlds, game rules, whitelist & stats. Full IT/EN support.
```

### Suggested Categories/Tags
- `management`
- `administration`
- `economy`
- `utility`
- `server-tools`
- `gui`
- `vault`
- `moderation`

### Supported Loaders
- ✅ **Spigot** (1.18 - 1.21+)
- ✅ **Paper** (1.18 - 1.21+)

### Supported Minecraft Versions
```
1.18, 1.18.1, 1.18.2
1.19, 1.19.1, 1.19.2, 1.19.3, 1.19.4
1.20, 1.20.1, 1.20.2, 1.20.3, 1.20.4, 1.20.5, 1.20.6
1.21, 1.21.1
```

### License
**All Rights Reserved**

---

## Full Description for Modrinth

```markdown
# AdminManager

Professional all-in-one plugin for complete Minecraft server management! 🎮✨

## 🌟 Main Features

**AdminManager** provides a complete suite of administrative tools through modern and intuitive graphical interfaces. Everything you need to manage your server in one plugin!

### 🎮 Server Manager - Control Center
Central hub with access to all administrative features:
- 🔄 **Reload/Restart/Stop Server** - Complete server control
- 💎 **Economy Provider** - Vault integration with global statistics
- 🗑️ **Clear Entities** - Entity cleanup in all worlds
- 💾 **Save Worlds** - Safe saving of all worlds
- 📋 **Player Data** - Complete database of all players
- 📝 **Whitelist Manager** - Integrated whitelist editor
- ⚙️ **Game Rules** - Rule management for each world
- 📜 **Command Registration** - Custom commands system
- 📁 **Config Manager** - Centralized configuration management

⚠️ **Safety**: Automatic warnings for critical operations (reload/save) with many plugins and low RAM

### 💰 Economy Manager (Vault)
Professional economy system with:
- 📊 **Global Statistics** - Total in circulation, average, top/bottom players
- 👥 **Complete Admin View** - All players (online + offline)
- 💵 **Balance Management** - Add, remove, set, reset money
- 🔄 **Vault Support** - Compatible with Essentials, CMI, EconomyAPI, etc.

### 👥 Advanced Player Management
- **Dynamic List** - Display with player heads and pagination
- **Player Data Manager** - Persistent database of all players
- **Complete Details** - UUID, ping, world, IP, coordinates, statistics
- **Admin Actions** - Teleport, kick, ban, mute with automatic logging

### 🛠️ Moderation Actions
- 🌀 **Bidirectional Teleport** - Admin → Player and vice versa
- 👢 **Kick** - Expulsion with automatic log
- 🔨 **Ban** - Native Minecraft permanent ban
- 🔇 **Mute/Unmute** - Thread-safe persistent system with staff notifications

### ⚙️ Advanced Configuration
- **Config Manager** - Reload and restore config.yml / tools.yml
- **Game Rules Manager** - Modify game rules for each world
- **Whitelist Editor** - Toggle and complete whitelist management
- **Command Registration** - Custom commands system by categories

### 🌍 100% Multilingual System
- 🇮🇹 **Italian** (it_IT) - Complete
- 🇬🇧 **English** (en_EN) - Complete
- ✅ **All GUIs translated** - 11 complete translation files
- 🔄 **Instant language change** - Without server restart

### 📊 bStats Statistics
8 integrated custom charts:
- Plugin language, server software, Minecraft version
- Economy provider, Java version
- Number of custom worlds
- Server operating system

### 📝 Professional Logging
- **Automatic Logs** - Kick, ban, mute with configurable timestamp
- **Customizable Format** - Dynamic placeholders (timestamp, admin, action, player)
- **Timezone Support** - Timezone configuration for accurate logs
- **Persistence** - YAML files for mute and configurations

### 🎨 Professional Experience
- **Startup Banner** - Professional console banner with Vault status
- **Info Command** - `/adminm info` for plugin information
- **Permission System** - Complete controls with double verification for sensitive operations
- **Modern GUIs** - Intuitive interfaces with meaningful icons

## 📦 Requirements

- **Server**: Spigot or Paper 1.18-1.21+
- **Java**: 8 or higher
- **Vault** (optional): For economy features

## 🚀 Quick Start

1. Download the `.jar` file
2. Copy to `plugins/`
3. (Optional) Install Vault + economy plugin
4. Restart the server
5. Use `/adminm` to open Server Manager!

## 🔐 Permissions

- `adminmanager.use` - Base access to plugin
- `adminmanager.reload` - Reload configuration
- `adminmanager.notify.mute` - Mute notifications

## 📚 Complete Documentation

Check the [README on GitHub](https://github.com/yourusername/AdminManager) for:
- Detailed guide of all features
- Configuration examples
- File structure and API
- Quick guide for each feature

## 🤝 Support

- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/yourusername/AdminManager/issues)
- 💡 **Feature Requests**: [GitHub Discussions](https://github.com/yourusername/AdminManager/discussions)
- 🌐 **Website**: [mclegacy.it](https://www.mclegacy.it)

## 📊 Privacy

The plugin collects anonymous statistics via bStats to improve development. You can disable them in `plugins/bStats/config.yml`.

---

**AdminManager** - The complete solution for professional administrators! 🎮✨

Developed with ❤️ by **AlessioGTA**
```

---

## Changelog Version 1.5.1

```markdown
# AdminManager v1.5.1 - Update Checker Release

New release with automatic update checking! 🎉

## 🔄 New Features (v1.5.1)

### Update Checker System
- **Automatic Update Check** - Checks for updates on server startup
- **Scheduled Checks** - Automatic check every 48 hours
- **Modrinth API Integration** - Uses official Modrinth API
- **Console Notifications** - Update status in startup banner
- **In-Game Notifications** - Alerts admins with `adminmanager.use` permission
- **Direct Download Link** - Provides Modrinth download link when update available

### Startup Banner Enhanced
- Shows update status: `✓ Plugin is Up to Date` (green) or `✗ Update Available: v1.X.X` (red)
- Direct link to Modrinth if update available

## ✨ Main Features

### 🎮 Server Manager
- Complete server control center
- Reload, restart, stop server with confirmations
- Clear entities for all worlds
- Save worlds with safety warnings
- Quick access to all admin features

### 💰 Economy Manager (Vault Integration)
- Global economy statistics (total, average, top/bottom)
- Admin view with all players (online + offline)
- Balance management: add, remove, set, reset
- Full Vault API support
- Compatible with Essentials, CMI, EconomyAPI

### 👥 Player Management
- Dynamic player list with heads and pagination
- Player Data Manager with complete database
- Player details: UUID, ping, world, IP, coordinates
- Admin actions: teleport, kick, ban, mute
- Thread-safe persistent mute system

### ⚙️ Configuration & Tools
- Config Manager for config.yml and tools.yml
- Game Rules Manager for all worlds
- Integrated Whitelist Editor
- Command Registration system
- Real-time configuration reload

### 🌍 Localization
- Complete multilingual system (IT/EN)
- 100% translated GUIs (11 translation files)
- Instant language change
- Dynamic placeholder support

### 📊 Analytics & Logging
- bStats integration with 8 custom charts
- Professional logging system (kick, ban, mute)
- Configurable timestamps with timezone
- Data persistence on YAML files

### 🎨 User Experience
- Professional startup banner with Vault status
- `/adminm info` command for plugin information
- Modern and intuitive GUIs
- Safety warnings for critical operations

## 🔒 Security
- Complete permission system
- Double check for sensitive operations
- Automatic warnings for risky operations (reload/save with many plugins)
- Thread-safe operations

## 🔧 Technical Improvements
- Modular and maintainable architecture
- Thread-safe MuteManager
- Optimized memory management
- Paper 1.18-1.21+ support

## 📦 Requirements
- Minecraft Server: Spigot/Paper 1.18-1.21+
- Java: 8+
- Vault (optional): For economy features

## 🚀 Installation
1. Download the JAR
2. Copy to `plugins/`
3. Optional: Install Vault + economy plugin
4. Restart server
5. Use `/adminm` in-game!

## 🐛 Bug Fixes
- Fixed all hardcoded strings with translation system
- Fixed Economy Provider placeholder display
- Fixed player data translation issues
- Fixed permission checks on all commands
- Added security warnings for critical operations

## 📝 Notes
- Tested on Spigot/Paper 1.18-1.21
- Full Vault support (optional)
- All non-economy features available without Vault

---

**Thank you for choosing AdminManager!** 🎮✨

For support, complete documentation and updates:
- 📚 README: https://github.com/yourusername/AdminManager
- 🐛 Issues: https://github.com/yourusername/AdminManager/issues
- 🌐 Website: https://www.mclegacy.it
```

---

## Suggested Images/Screenshots

For a professional publication on Modrinth, it is recommended to include screenshots of:

1. **Server Manager GUI** - Main screen
2. **Economy Manager** - Global statistics view
3. **Economy Manager** - Admin view with player list
4. **Player Data Manager** - Complete player list
5. **Player Management** - Single player details with actions
6. **Whitelist Editor** - Whitelist management
7. **Game Rules Manager** - Game rules management
8. **Config Manager** - Configuration management
9. **Multilingual Support** - IT/EN comparison of same GUI
10. **Startup Banner** - Console with professional banner

---

## Pre-Publication Checklist

### Files to Prepare
- [ ] `AdminManager-1.5.1.jar` - Compiled plugin file
- [ ] Screenshots (at least 3-5 images)
- [ ] Banner/Logo (optional, suggested size: 1280x640px)
- [ ] Project icon (optional, size: 256x256px)

### Information to Complete
- [ ] GitHub repository URL (if public)
- [ ] Issues tracker URL
- [ ] Discord server (optional)
- [ ] Additional documentation links

### Modrinth Configuration
- [ ] Create project on Modrinth
- [ ] Set slug: `adminmanager`
- [ ] Select appropriate categories
- [ ] Configure loaders: Spigot, Paper
- [ ] Select supported Minecraft versions
- [ ] Set license: All Rights Reserved
- [ ] Upload complete description
- [ ] Upload screenshots
- [ ] Upload banner/logo (if available)

### Release Upload
- [ ] Upload file `AdminManager-1.5.1.jar`
- [ ] Set version number: `1.5.1`
- [ ] Set version name: `v1.5.1 - Update Checker Release`
- [ ] Release type: `release`
- [ ] Paste version 1.5.1 changelog
- [ ] Select compatible loaders
- [ ] Select all supported Minecraft versions
- [ ] Add optional dependencies: Vault
- [ ] Publish!

---

## Additional Notes

### Optional Dependencies
On Modrinth, make sure to indicate **Vault** as an optional dependency:
- **Name**: Vault
- **Type**: Optional
- **Description**: "Required only for economy features. Without Vault all other features remain available."

### Suggested Tags for SEO
Besides categories, use keywords in the description body like:
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

### Useful Modrinth Links
- **Dashboard**: https://modrinth.com/dashboard/projects
- **Upload Docs**: https://docs.modrinth.com/docs/tutorials/uploading/
- **Formatting Guide**: https://docs.modrinth.com/docs/tutorials/markdown/

---

**Happy publishing!** 🚀
