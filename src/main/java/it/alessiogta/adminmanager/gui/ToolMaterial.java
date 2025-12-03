package it.alessiogta.adminmanager.gui;

import org.bukkit.Material;

/**
 * Represents the different materials tools can be made from
 */
public enum ToolMaterial {
    WOOD("§6Wood", "WOODEN", Material.OAK_PLANKS),
    STONE("§7Stone", "STONE", Material.COBBLESTONE),
    IRON("§fIron", "IRON", Material.IRON_INGOT),
    GOLD("§eGold", "GOLDEN", Material.GOLD_INGOT),
    DIAMOND("§bDiamond", "DIAMOND", Material.DIAMOND),
    NETHERITE("§8Netherite", "NETHERITE", Material.NETHERITE_INGOT);

    private final String displayName;
    private final String prefix;
    private final Material icon;

    ToolMaterial(String displayName, String prefix, Material icon) {
        this.displayName = displayName;
        this.prefix = prefix;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public Material getIcon() {
        return icon;
    }

    /**
     * Get the durability for this material
     */
    public int getBaseDurability(ToolType toolType) {
        switch (this) {
            case WOOD:
                switch (toolType) {
                    case SWORD: return 59;
                    case PICKAXE: return 59;
                    case SHOVEL: return 59;
                    case HOE: return 59;
                    case AXE: return 59;
                }
                break;
            case STONE:
                switch (toolType) {
                    case SWORD: return 131;
                    case PICKAXE: return 131;
                    case SHOVEL: return 131;
                    case HOE: return 131;
                    case AXE: return 131;
                }
                break;
            case IRON:
                switch (toolType) {
                    case SWORD: return 250;
                    case PICKAXE: return 250;
                    case SHOVEL: return 250;
                    case HOE: return 250;
                    case AXE: return 250;
                }
                break;
            case GOLD:
                switch (toolType) {
                    case SWORD: return 32;
                    case PICKAXE: return 32;
                    case SHOVEL: return 32;
                    case HOE: return 32;
                    case AXE: return 32;
                }
                break;
            case DIAMOND:
                switch (toolType) {
                    case SWORD: return 1561;
                    case PICKAXE: return 1561;
                    case SHOVEL: return 1561;
                    case HOE: return 1561;
                    case AXE: return 1561;
                }
                break;
            case NETHERITE:
                switch (toolType) {
                    case SWORD: return 2031;
                    case PICKAXE: return 2031;
                    case SHOVEL: return 2031;
                    case HOE: return 2031;
                    case AXE: return 2031;
                }
                break;
        }
        return 100; // fallback
    }

    /**
     * Get the attack damage for this material (for swords)
     */
    public double getAttackDamage() {
        switch (this) {
            case WOOD: return 4.0;
            case STONE: return 5.0;
            case IRON: return 6.0;
            case GOLD: return 4.0;
            case DIAMOND: return 7.0;
            case NETHERITE: return 8.0;
            default: return 1.0;
        }
    }

    /**
     * Get the mining speed multiplier for this material
     */
    public double getMiningSpeed() {
        switch (this) {
            case WOOD: return 2.0;
            case STONE: return 4.0;
            case IRON: return 6.0;
            case GOLD: return 12.0;
            case DIAMOND: return 8.0;
            case NETHERITE: return 9.0;
            default: return 1.0;
        }
    }
}
