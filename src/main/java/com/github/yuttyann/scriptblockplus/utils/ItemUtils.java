package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * ScriptBlockPlus ItemUtils クラス
 * @author yuttyann44581
 */
public class ItemUtils {

	@SuppressWarnings("deprecation")
	public static void setDamage(@NotNull ItemStack item, int damage) {
		Validate.notNull(item, "Item cannot be null");
		if (Utils.isCBXXXorLater("1.13")) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				((org.bukkit.inventory.meta.Damageable) meta).setDamage(damage);
				item.setItemMeta(meta);
			}
		} else {
			item.setDurability((short) damage);
		}
	}

	@SuppressWarnings("deprecation")
	public static int getDamage(@NotNull ItemStack item) {
		Validate.notNull(item, "Item cannot be null");
		if (Utils.isCBXXXorLater("1.13")) {
			ItemMeta meta = item.getItemMeta();
			return meta == null ? 0 : ((org.bukkit.inventory.meta.Damageable) meta).getDamage();
		}
		return item.getDurability();
	}

	@NotNull
	public static Material getMaterial(@NotNull String name) {
		Material type = Material.getMaterial(name.toUpperCase());
		return type == null ? Material.AIR : type;
	}

	@NotNull
	public static ItemStack getBlockSelector() {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		meta.setDisplayName("§dBlock Selector");
		meta.setLore(setListColor(SBConfig.BLOCK_SELECTOR.getValue()));
		item.setItemMeta(meta);
		return item;
	}

	@NotNull
	public static ItemStack getScriptEditor(@NotNull ScriptType scriptType) {
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		meta.setDisplayName("§dScript Editor§6[Mode: " + scriptType.name() + "]");
		meta.setLore(setListColor(SBConfig.SCRIPT_EDITOR.getValue()));
		item.setItemMeta(meta);
		return item;
	}

	@NotNull
	public static ItemStack getScriptViewer() {
		ItemStack item = new ItemStack(Material.valueOf(Utils.isCBXXXorLater("1.13") ? "CLOCK" : "WATCH"));
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		meta.setDisplayName("§dScript Viewer");
		meta.setLore(setListColor(SBConfig.SCRIPT_VIEWER.getValue()));
		item.setItemMeta(meta);
		return item;
	}

	@NotNull
	private static List<String> setListColor(List<String> list) {
		list = new ArrayList<>(list);
		for (int i = 0; i < list.size(); i++) {
			list.set(i, StringUtils.setColor(list.get(i), true));
		}
		return list;
	}

	public static boolean isBlockSelector(@Nullable ItemStack item) {
		return isItem(item, Material.STICK, s -> s.equals("§dBlock Selector"));
	}

	public static boolean isScriptEditor(@Nullable ItemStack item) {
		return isItem(item, Material.BLAZE_ROD, s -> s.startsWith("§dScript Editor§6[Mode: ") && s.endsWith("]"));
	}

	public static boolean isScriptViewer(@Nullable ItemStack item) {
		return isItem(item, Material.valueOf(Utils.isCBXXXorLater("1.13") ? "CLOCK" : "WATCH"), s -> s.startsWith("§dScript Viewer"));
	}

	@NotNull
	public static ScriptType getScriptType(@Nullable ItemStack item) {
		if (isScriptEditor(item)) {
			String name = StringUtils.removeStart(ItemUtils.getName(item, ""), "§dScript Editor§6[Mode: ");
			return ScriptType.valueOf(name.substring(0, name.length() - 1));
		}
		return ScriptType.valueOf(0);
	}

	@NotNull
	public static ItemStack[] getHandItems(Player player) {
		PlayerInventory inventory = player.getInventory();
		return new ItemStack[] { inventory.getItemInMainHand(), inventory.getItemInOffHand() };
	}

	public static void setName(@NotNull ItemStack item, @NotNull String name) {
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}

	@NotNull
	public static String getName(@NotNull ItemStack item, @Nullable String def) {
		def = def == null ? item.getType().name() : def;
		if (item.getType() == Material.AIR) {
			return def;
		}
		ItemMeta meta = item.getItemMeta();
		return meta == null ? def : meta.hasDisplayName() ? meta.getDisplayName() : def;
	}

	@NotNull
	public static String getName(@NotNull ItemStack item) {
		return getName(item, item.getType().name());
	}

	public static boolean isItem(@Nullable ItemStack item, @Nullable Material type, @NotNull String name) {
		return isItem(item, type, name::equals);
	}

	public static boolean isItem(@Nullable ItemStack item, @Nullable Material type, @NotNull Predicate<String> name) {
		return item != null && type != null && item.getType() == type && name.test(getName(item));
	}
}