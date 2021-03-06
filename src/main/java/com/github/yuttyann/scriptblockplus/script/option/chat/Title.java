package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Title オプションクラス
 * @author yuttyann44581
 */
public class Title extends BaseOption {

	public Title() {
		super("title", "@title:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new Title();
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		String title = StringUtils.setColor(array[0] + "", true);
		String subtitle = StringUtils.setColor(array.length > 1 ? array[1] : "", true);
		int fadeIn = 10, stay = 40, fadeOut = 10;
		if (array.length == 3) {
			String[] times = StringUtils.split(array[2], "-");
			if (times.length == 3) {
				fadeIn = Integer.parseInt(times[0]);
				stay = Integer.parseInt(times[1]);
				fadeOut = Integer.parseInt(times[2]);
			}
		}
		sendTitle(getPlayer(), title, subtitle, fadeIn, stay, fadeOut);
		return true;
	}

	private void sendTitle(@NotNull Player player, @Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.12")) {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		} else {
			String prefix = "title " + player.getName();
			CommandSender sender = Bukkit.getConsoleSender();
			Bukkit.dispatchCommand(sender, prefix + " times " + fadeIn + " " + stay + " " + fadeOut);
			Bukkit.dispatchCommand(sender, prefix + " subtitle {\"text\":\"" + subtitle + "\"}");
			Bukkit.dispatchCommand(sender, prefix + " title {\"text\":\"" + title + "\"}");
		}
	}
}