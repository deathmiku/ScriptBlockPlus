package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemHand extends BaseOption {

	public ItemHand() {
		super("itemhand", "@hand:");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new ItemHand();
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), " ");
		String[] itemData = StringUtils.split(array[0], ":");
		if (Calculation.REALNUMBER_PATTERN.matcher(itemData[0]).matches()) {
			throw new IllegalAccessException("Numerical values can not be used");
		}
		Material type = Material.getMaterial(itemData[0].toUpperCase());
		int damage = itemData.length > 1 ? Integer.parseInt(itemData[1]) : 0;
		int amount = Integer.parseInt(array[1]);
		String create = array.length > 2 ? StringUtils.createString(array, 2) : null;
		String itemName = StringUtils.replaceColor(create, false);

		Player player = getPlayer();
		ItemStack[] items = ItemUtils.getHandItems(player);
		if (!StreamUtils.anyMatch(items, i -> checkItem(i, itemName, type, amount, damage))) {
			String typeName = type == null ? "null" : type.name();
			String itemTypeName = StringUtils.isEmpty(itemName) ? typeName : itemName;
			SBConfig.ERROR_HAND.replace(typeName, amount, damage, itemTypeName).send(player);
			return false;
		}
		return true;
	}

	private boolean checkItem(@Nullable ItemStack item, @NotNull String itemName, @Nullable Material type, int amount, int damage) {
		if (item == null || item.getAmount() < amount || ItemUtils.getDamage(item) != damage) {
			return false;
		}
		return ItemUtils.isItem(item, type, StringUtils.isEmpty(itemName) ? item.getType().name() : itemName);
	}
}