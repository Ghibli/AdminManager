package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldGeneratorGui extends BaseGui {

    private final Player admin;

    // Static tracking for players awaiting chat input
    public static final Map<UUID, InputData> awaitingInput = new HashMap<>();

    public enum InputType {
        WORLD_NAME,
        SEED
    }

    public static class InputData {
        public InputType type;
        public WorldGeneratorGui gui;

        public InputData(InputType type, WorldGeneratorGui gui) {
            this.type = type;
            this.gui = gui;
        }
    }

    // Current configuration
    private String worldName = "custom_world";
    private World.Environment environment = World.Environment.NORMAL;
    private WorldType worldType = WorldType.NORMAL;
    private String seed = "";
    private boolean generateStructures = true;
    private Difficulty difficulty = Difficulty.NORMAL;
    private boolean keepSpawnLoaded = true;

    public WorldGeneratorGui(Player admin) {
        super(admin, TranslationManager.translate("WorldGenerator", "title", "&6&lWorld Generator"), 1);
        this.admin = admin;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable automatic navigation
    }

    private void setupGuiItems() {
        // Row 1-2: Info panel
        setItem(4, createInfoButton());

        // Row 3: Main configuration
        setItem(20, createWorldNameButton());
        setItem(21, createEnvironmentButton());
        setItem(22, createWorldTypeButton());
        setItem(23, createSeedButton());

        // Row 4: Secondary configuration
        setItem(29, createStructuresButton());
        setItem(30, createDifficultyButton());
        setItem(31, createKeepSpawnButton());

        // Row 5: Actions
        setItem(40, createGenerateButton());

        // Row 6: Navigation
        setItem(49, createBackButton());
    }

    private ItemStack createInfoButton() {
        String title = TranslationManager.translate("WorldGenerator", "info_title", "&e&lConfigurazione Corrente");
        String loreText = TranslationManager.translate("WorldGenerator", "info_lore",
            "&7Nome: &f{name}\n&7Environment: &f{env}\n&7Tipo: &f{type}\n&7Seed: &f{seed}\n&7Strutture: &f{struct}\n&7Difficoltà: &f{diff}\n&7Keep Spawn: &f{spawn}")
            .replace("{name}", worldName)
            .replace("{env}", getEnvironmentName(environment))
            .replace("{type}", getWorldTypeName(worldType))
            .replace("{seed}", seed.isEmpty() ? "Random" : seed)
            .replace("{struct}", generateStructures ? "Sì" : "No")
            .replace("{diff}", getDifficultyName(difficulty))
            .replace("{spawn}", keepSpawnLoaded ? "Sì" : "No");

        return createItem(Material.PAPER, title, loreText.split("\n"));
    }

    private ItemStack createWorldNameButton() {
        String title = TranslationManager.translate("WorldGenerator", "name_title", "&eNome Mondo");
        String loreText = TranslationManager.translate("WorldGenerator", "name_lore",
            "&7Corrente: &f{name}\n\n&e&lClick: &7Inserisci nome in chat")
            .replace("{name}", worldName);
        return createItem(Material.NAME_TAG, title, loreText.split("\n"));
    }

    private ItemStack createEnvironmentButton() {
        String title = TranslationManager.translate("WorldGenerator", "env_title", "&eEnvironment");
        String loreText = TranslationManager.translate("WorldGenerator", "env_lore",
            "&7Corrente: &f{env}\n\n&7• Normal (Overworld)\n&7• Nether\n&7• The End\n\n&e&lClick: &7Cambia environment")
            .replace("{env}", getEnvironmentName(environment));

        Material material = environment == World.Environment.NETHER ? Material.NETHERRACK :
                          environment == World.Environment.THE_END ? Material.END_STONE :
                          Material.GRASS_BLOCK;

        return createItem(material, title, loreText.split("\n"));
    }

    private ItemStack createWorldTypeButton() {
        String title = TranslationManager.translate("WorldGenerator", "type_title", "&eTipo Mondo");
        String loreText = TranslationManager.translate("WorldGenerator", "type_lore",
            "&7Corrente: &f{type}\n\n&7• Normal\n&7• Flat\n&7• Large Biomes\n&7• Amplified\n\n&e&lClick: &7Cambia tipo")
            .replace("{type}", getWorldTypeName(worldType));

        return createItem(Material.FILLED_MAP, title, loreText.split("\n"));
    }

    private ItemStack createSeedButton() {
        String title = TranslationManager.translate("WorldGenerator", "seed_title", "&eSeed");
        String loreText = TranslationManager.translate("WorldGenerator", "seed_lore",
            "&7Corrente: &f{seed}\n\n&e&lClick: &7Inserisci seed in chat\n&e&lShift+Click: &7Reset (Random)")
            .replace("{seed}", seed.isEmpty() ? "Random" : seed);

        return createItem(Material.WHEAT_SEEDS, title, loreText.split("\n"));
    }

    private ItemStack createStructuresButton() {
        String title = TranslationManager.translate("WorldGenerator", "struct_title", "&eGenera Strutture");
        String status = generateStructures ? "&aON" : "&cOFF";
        String loreText = TranslationManager.translate("WorldGenerator", "struct_lore",
            "&7Status: " + status + "\n\n&7Villaggi, templi, fortezze, ecc.\n\n&e&lClick: &7Toggle")
            .replace("{status}", status);

        Material material = generateStructures ? Material.BRICK : Material.COBBLESTONE;
        return createItem(material, title, loreText.split("\n"));
    }

    private ItemStack createDifficultyButton() {
        String title = TranslationManager.translate("WorldGenerator", "diff_title", "&eDifficoltà");
        String loreText = TranslationManager.translate("WorldGenerator", "diff_lore",
            "&7Corrente: &f{diff}\n\n&7• Peaceful\n&7• Easy\n&7• Normal\n&7• Hard\n\n&e&lClick: &7Cambia difficoltà")
            .replace("{diff}", getDifficultyName(difficulty));

        return createItem(Material.DIAMOND_SWORD, title, loreText.split("\n"));
    }

    private ItemStack createKeepSpawnButton() {
        String title = TranslationManager.translate("WorldGenerator", "spawn_title", "&eKeep Spawn Loaded");
        String status = keepSpawnLoaded ? "&aON" : "&cOFF";
        String loreText = TranslationManager.translate("WorldGenerator", "spawn_lore",
            "&7Status: " + status + "\n\n&7Mantieni spawn caricato in memoria\n\n&e&lClick: &7Toggle")
            .replace("{status}", status);

        Material material = keepSpawnLoaded ? Material.RESPAWN_ANCHOR : Material.BEDROCK;
        return createItem(material, title, loreText.split("\n"));
    }

    private ItemStack createGenerateButton() {
        String title = TranslationManager.translate("WorldGenerator", "generate_title", "&a&l⚡ GENERA MONDO");
        String loreText = TranslationManager.translate("WorldGenerator", "generate_lore",
            "&7Crea il mondo con la\n&7configurazione corrente\n\n&c&lWARNING: &7Verifica che il nome\n&7non esista già!\n\n&e&lClick: &7Genera!");

        return createItem(Material.NETHER_STAR, title, loreText.split("\n"));
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("WorldGenerator", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("WorldGenerator", "back_button_lore", "&7Torna a World Selector");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();
        boolean isShiftClick = event.isShiftClick();

        switch (slot) {
            case 20: // World Name
                handleWorldNameInput(clicker);
                break;
            case 21: // Environment
                handleEnvironmentToggle(clicker);
                break;
            case 22: // World Type
                handleWorldTypeToggle(clicker);
                break;
            case 23: // Seed
                if (isShiftClick) {
                    handleSeedReset(clicker);
                } else {
                    handleSeedInput(clicker);
                }
                break;
            case 29: // Structures
                handleStructuresToggle(clicker);
                break;
            case 30: // Difficulty
                handleDifficultyToggle(clicker);
                break;
            case 31: // Keep Spawn
                handleKeepSpawnToggle(clicker);
                break;
            case 40: // Generate World
                handleGenerateWorld(clicker);
                break;
            case 49: // Back
                handleBack(clicker);
                break;
        }
    }

    private void handleWorldNameInput(Player clicker) {
        clicker.closeInventory();

        String message = TranslationManager.translate("WorldGenerator", "name_input",
            "&eInserisci il nome del mondo in chat &7(senza spazi):");
        clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        String cancelMessage = TranslationManager.translate("WorldGenerator", "cancel_input",
            "&7Scrivi &c'annulla' &7per annullare");
        clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', cancelMessage));

        // Register player as awaiting input
        awaitingInput.put(clicker.getUniqueId(), new InputData(InputType.WORLD_NAME, this));
    }

    private void handleEnvironmentToggle(Player clicker) {
        // Cycle: NORMAL → NETHER → THE_END → NORMAL
        if (environment == World.Environment.NORMAL) {
            environment = World.Environment.NETHER;
        } else if (environment == World.Environment.NETHER) {
            environment = World.Environment.THE_END;
        } else {
            environment = World.Environment.NORMAL;
        }

        refreshAllSlots();
    }

    private void handleWorldTypeToggle(Player clicker) {
        // Cycle: NORMAL → FLAT → LARGE_BIOMES → AMPLIFIED → NORMAL
        if (worldType == WorldType.NORMAL) {
            worldType = WorldType.FLAT;
        } else if (worldType == WorldType.FLAT) {
            worldType = WorldType.LARGE_BIOMES;
        } else if (worldType == WorldType.LARGE_BIOMES) {
            worldType = WorldType.AMPLIFIED;
        } else {
            worldType = WorldType.NORMAL;
        }

        refreshAllSlots();
    }

    private void handleSeedInput(Player clicker) {
        clicker.closeInventory();

        String message = TranslationManager.translate("WorldGenerator", "seed_input",
            "&eInserisci il seed in chat &7(numero o testo):");
        clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        String cancelMessage = TranslationManager.translate("WorldGenerator", "cancel_input",
            "&7Scrivi &c'annulla' &7per annullare");
        clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', cancelMessage));

        // Register player as awaiting input
        awaitingInput.put(clicker.getUniqueId(), new InputData(InputType.SEED, this));
    }

    private void handleSeedReset(Player clicker) {
        seed = "";
        String message = TranslationManager.translate("WorldGenerator", "seed_reset",
            "&aSeed resettato! &7Verrà generato un mondo random.");
        clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        refreshAllSlots();
    }

    private void handleStructuresToggle(Player clicker) {
        generateStructures = !generateStructures;
        refreshAllSlots();
    }

    private void handleDifficultyToggle(Player clicker) {
        // Cycle: PEACEFUL → EASY → NORMAL → HARD → PEACEFUL
        if (difficulty == Difficulty.PEACEFUL) {
            difficulty = Difficulty.EASY;
        } else if (difficulty == Difficulty.EASY) {
            difficulty = Difficulty.NORMAL;
        } else if (difficulty == Difficulty.NORMAL) {
            difficulty = Difficulty.HARD;
        } else {
            difficulty = Difficulty.PEACEFUL;
        }

        refreshAllSlots();
    }

    private void handleKeepSpawnToggle(Player clicker) {
        keepSpawnLoaded = !keepSpawnLoaded;
        refreshAllSlots();
    }

    private void handleGenerateWorld(Player clicker) {
        clicker.closeInventory();

        // Check if world already exists
        if (Bukkit.getWorld(worldName) != null) {
            String error = TranslationManager.translate("WorldGenerator", "world_exists",
                "&c&lERRORE: &eEsiste già un mondo con questo nome!");
            clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', error));
            return;
        }

        String startMessage = TranslationManager.translate("WorldGenerator", "generating",
            "&a&lGENERAZIONE MONDO: &eCreazione di &6{name}&e...")
            .replace("{name}", worldName);
        clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', startMessage));

        // Create world asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> {
                try {
                    WorldCreator creator = new WorldCreator(worldName);
                    creator.environment(environment);
                    creator.type(worldType);

                    if (!seed.isEmpty()) {
                        try {
                            creator.seed(Long.parseLong(seed));
                        } catch (NumberFormatException e) {
                            creator.seed(seed.hashCode());
                        }
                    }

                    creator.generateStructures(generateStructures);

                    // Create world on main thread
                    Bukkit.getScheduler().runTask(
                        Bukkit.getPluginManager().getPlugin("AdminManager"),
                        () -> {
                            World newWorld = creator.createWorld();
                            if (newWorld != null) {
                                newWorld.setDifficulty(difficulty);
                                newWorld.setKeepSpawnInMemory(keepSpawnLoaded);

                                String success = TranslationManager.translate("WorldGenerator", "success",
                                    "&a&lSUCCESS: &eMondo &6{name} &ecreato con successo!")
                                    .replace("{name}", worldName);
                                clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', success));

                                // Teleport player to new world
                                Location spawn = newWorld.getSpawnLocation();
                                clicker.teleport(spawn);
                            } else {
                                String error = TranslationManager.translate("WorldGenerator", "error",
                                    "&c&lERRORE: &eImpossibile creare il mondo!");
                                clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', error));
                            }
                        }
                    );
                } catch (Exception e) {
                    String error = TranslationManager.translate("WorldGenerator", "error",
                        "&c&lERRORE: &e" + e.getMessage());
                    clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', error));
                    e.printStackTrace();
                }
            }
        );
    }

    private void handleBack(Player clicker) {
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new WorldSelectorGui(clicker).open()
        );
    }

    private void refreshAllSlots() {
        setItem(4, createInfoButton());
        setItem(20, createWorldNameButton());
        setItem(21, createEnvironmentButton());
        setItem(22, createWorldTypeButton());
        setItem(23, createSeedButton());
        setItem(29, createStructuresButton());
        setItem(30, createDifficultyButton());
        setItem(31, createKeepSpawnButton());

        if (inventory != null) {
            inventory.setItem(4, createInfoButton());
            inventory.setItem(20, createWorldNameButton());
            inventory.setItem(21, createEnvironmentButton());
            inventory.setItem(22, createWorldTypeButton());
            inventory.setItem(23, createSeedButton());
            inventory.setItem(29, createStructuresButton());
            inventory.setItem(30, createDifficultyButton());
            inventory.setItem(31, createKeepSpawnButton());
        }
    }

    // Helper methods
    private String getEnvironmentName(World.Environment env) {
        switch (env) {
            case NETHER: return "Nether";
            case THE_END: return "The End";
            default: return "Overworld";
        }
    }

    private String getWorldTypeName(WorldType type) {
        if (type == WorldType.FLAT) return "Flat";
        if (type == WorldType.LARGE_BIOMES) return "Large Biomes";
        if (type == WorldType.AMPLIFIED) return "Amplified";
        return "Normal";
    }

    private String getDifficultyName(Difficulty diff) {
        switch (diff) {
            case PEACEFUL: return "Peaceful";
            case EASY: return "Easy";
            case HARD: return "Hard";
            default: return "Normal";
        }
    }

    // Public setters for chat input
    public void setWorldName(String name) {
        this.worldName = name;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }
}
