package it.alessiogta.adminmanager.gui;

import org.bukkit.Material;

/**
 * Represents the different types of tools that can be created
 */
public enum ToolType {
    SWORD("Sword", Material.DIAMOND_SWORD),
    PICKAXE("Pickaxe", Material.DIAMOND_PICKAXE),
    SHOVEL("Shovel", Material.DIAMOND_SHOVEL),
    HOE("Hoe", Material.DIAMOND_HOE),
    AXE("Axe", Material.DIAMOND_AXE);

    private final String displayName;
    private final Material icon;

    ToolType(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    /**
     * Get the Material for this tool type with the specified material
     */
    public Material getMaterial(ToolMaterial material) {
        String prefix = material.getPrefix();

        switch (this) {
            case SWORD:
                switch (material) {
                    case WOOD: return Material.WOODEN_SWORD;
                    case STONE: return Material.STONE_SWORD;
                    case IRON: return Material.IRON_SWORD;
                    case GOLD: return Material.GOLDEN_SWORD;
                    case DIAMOND: return Material.DIAMOND_SWORD;
                    case NETHERITE: return Material.NETHERITE_SWORD;
                }
                break;
            case PICKAXE:
                switch (material) {
                    case WOOD: return Material.WOODEN_PICKAXE;
                    case STONE: return Material.STONE_PICKAXE;
                    case IRON: return Material.IRON_PICKAXE;
                    case GOLD: return Material.GOLDEN_PICKAXE;
                    case DIAMOND: return Material.DIAMOND_PICKAXE;
                    case NETHERITE: return Material.NETHERITE_PICKAXE;
                }
                break;
            case SHOVEL:
                switch (material) {
                    case WOOD: return Material.WOODEN_SHOVEL;
                    case STONE: return Material.STONE_SHOVEL;
                    case IRON: return Material.IRON_SHOVEL;
                    case GOLD: return Material.GOLDEN_SHOVEL;
                    case DIAMOND: return Material.DIAMOND_SHOVEL;
                    case NETHERITE: return Material.NETHERITE_SHOVEL;
                }
                break;
            case HOE:
                switch (material) {
                    case WOOD: return Material.WOODEN_HOE;
                    case STONE: return Material.STONE_HOE;
                    case IRON: return Material.IRON_HOE;
                    case GOLD: return Material.GOLDEN_HOE;
                    case DIAMOND: return Material.DIAMOND_HOE;
                    case NETHERITE: return Material.NETHERITE_HOE;
                }
                break;
            case AXE:
                switch (material) {
                    case WOOD: return Material.WOODEN_AXE;
                    case STONE: return Material.STONE_AXE;
                    case IRON: return Material.IRON_AXE;
                    case GOLD: return Material.GOLDEN_AXE;
                    case DIAMOND: return Material.DIAMOND_AXE;
                    case NETHERITE: return Material.NETHERITE_AXE;
                }
                break;
        }

        // Fallback (shouldn't happen)
        return Material.STICK;
    }
}
