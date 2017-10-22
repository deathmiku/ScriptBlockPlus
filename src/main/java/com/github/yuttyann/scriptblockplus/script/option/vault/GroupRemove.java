package com.github.yuttyann.scriptblockplus.script.option.vault;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultPermission;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class GroupRemove extends BaseOption {

	public GroupRemove() {
		super("group_remove", "@groupREMOVE:", 16);
	}

	@Override
	public boolean isValid() {
		VaultPermission vaultPermission = HookPlugins.getVaultPermission();
		if (!vaultPermission.isEnabled() || vaultPermission.isSuperPerms()) {
			throw new UnsupportedOperationException();
		}
		String[] array = StringUtils.split(getOptionValue(), "/");
		String world = array.length > 1 ? array[0] : null;
		String group = array.length > 1 ? array[1] : array[0];
		Player player = getPlayer();
		if ("<world>".equals(world)) {
			world = player.getWorld().getName();
		}
		if (vaultPermission.playerInGroup(player, group)) {
			vaultPermission.playerRemoveGroup(player,  group);
		}
		return true;
	}
}