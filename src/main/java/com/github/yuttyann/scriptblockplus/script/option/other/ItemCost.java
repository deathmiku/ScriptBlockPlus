package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ItemCost オプションクラス
 * @author yuttyann44581
 */
public class ItemCost extends BaseOption {

	public static final String KEY_OPTION = Utils.randomUUID();
	public static final String KEY_PLAYER = Utils.randomUUID();

	public ItemCost() {
		super("itemcost", "$item:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new ItemCost();
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), " ");
		String[] itemData = StringUtils.split(array[0], ":");
		if (Calculation.REALNUMBER_PATTERN.matcher(itemData[0]).matches()) {
			throw new IllegalAccessException("Numerical values can not be used");
		}
		Material type = ItemUtils.getMaterial(itemData[0]);
		int damage = itemData.length > 1 ? Integer.parseInt(itemData[1]) : 0;
		int amount = Integer.parseInt(array[1]);
		String create = array.length > 2 ? StringUtils.createString(array, 2) : null;
		String itemName = StringUtils.setColor(create, false);

		Player player = getPlayer();
		PlayerInventory inventory = player.getInventory();
		if (!getSBRead().has(KEY_OPTION)) {
			getSBRead().put(KEY_OPTION, copyItems(inventory.getContents()));
		}
		ItemStack[] items = inventory.getContents();
		int result = amount;
		for (ItemStack item : items) {
			if (equalItems(item, itemName, type, damage)) {
				result -= result > 0 ? setAmount(item, item.getAmount() - result) : 0;
			}
		}
		if (result > 0) {
			SBConfig.ERROR_ITEM.replace(type, amount, damage, itemName).send(player);
			return false;
		}
		inventory.setContents(items);
		return true;
	}

	private int setAmount(@NotNull ItemStack item, int amount) {
		int oldAmount = item.getAmount();
		item.setAmount(amount);
		return oldAmount;
	}

	private boolean equalItems(@Nullable ItemStack item, @NotNull String itemName, @Nullable Material type, int damage) {
		if (item == null || ItemUtils.getDamage(item) != damage) {
			return false;
		}
		return ItemUtils.isItem(item, type, StringUtils.isEmpty(itemName) ? item.getType().name() : itemName);
	}

	@NotNull
	private ItemStack[] copyItems(ItemStack[] items) {
		ItemStack[] copy = new ItemStack[items.length];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = items[i] == null ? new ItemStack(Material.AIR) : items[i].clone();
		}
		return copy;
	}
}