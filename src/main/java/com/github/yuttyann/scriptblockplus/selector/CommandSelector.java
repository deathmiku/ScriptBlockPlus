package com.github.yuttyann.scriptblockplus.selector;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.selector.versions.Vx_x_Rx;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * ScriptBlockPlus CommandSelector クラス
 * @author yuttyann44581
 */
public final class CommandSelector {

	@Deprecated
	private static final Pattern COMMAND_PATTERN = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)])?$");

	private static final CommandListener COMMAND_LISTENER;

	static {
		Vx_x_Rx nmsVx_x_Rx;
		try {
			String packageName = Vx_x_Rx.class.getPackage().getName();
			String className = packageName + "." + PackageType.getVersionName();
			nmsVx_x_Rx = (Vx_x_Rx) Class.forName(className).newInstance();
		} catch (Exception e) {
			nmsVx_x_Rx = new Vx_x_Rx();
		}
		COMMAND_LISTENER = nmsVx_x_Rx.getCommandBlock();
	}

	@NotNull
	public static CommandListener getListener() {
		return COMMAND_LISTENER;
	}

	@Deprecated
	public static boolean isCommandPattern(@NotNull String command) {
		String[] args = StringUtils.split(command, " ");
		for (int i = 1; i < args.length; i++) {
			if (isPattern(args[i])) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public static boolean isPattern(@NotNull String s) {
		return COMMAND_PATTERN.matcher(s).matches();
	}
}