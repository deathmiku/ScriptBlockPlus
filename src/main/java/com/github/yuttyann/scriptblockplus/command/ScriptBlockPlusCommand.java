package com.github.yuttyann.scriptblockplus.command;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.*;
import com.google.common.base.Charsets;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * ScriptBlockPlus ScriptBlockPlusCommand コマンドクラス
 * @author yuttyann44581
 */
public final class ScriptBlockPlusCommand extends BaseCommand {

	public ScriptBlockPlusCommand(@NotNull ScriptBlock plugin) {
		super(plugin);
	}

	@NotNull
	@Override
	public String getCommandName() {
		return "ScriptBlockPlus";
	}

	@Override
	public boolean isAliases() {
		return true;
	}

	@NotNull
	@Override
	public CommandData[] getUsages() {
		String[] typeNodes = Permission.getTypeNodes(true);
		return new CommandData[] {
				new CommandData(SBConfig.TOOL_COMMAND.getValue(), Permission.COMMAND_TOOL.getNode()),
				new CommandData(SBConfig.RELOAD_COMMAND.getValue(), Permission.COMMAND_RELOAD.getNode()),
				new CommandData(SBConfig.BACKUP_COMMAND.getValue(), Permission.COMMAND_BACKUP.getNode()),
				new CommandData(SBConfig.CHECKVER_COMMAND.getValue(), Permission.COMMAND_CHECKVER.getNode()),
				new CommandData(SBConfig.DATAMIGR_COMMAND.getValue(), Permission.COMMAND_DATAMIGR.getNode()),
				new CommandData(SBConfig.EXPORT_COMMAND.getValue(), Permission.COMMAND_EXPORT.getNode()),
				new CommandData(SBConfig.CREATE_COMMAND.getValue(), typeNodes),
				new CommandData(SBConfig.ADD_COMMAND.getValue(), typeNodes),
				new CommandData(SBConfig.REMOVE_COMMAND.getValue(), typeNodes),
				new CommandData(SBConfig.VIEW_COMMAND.getValue(), typeNodes),
				new CommandData(SBConfig.RUN_COMMAND.getValue(), typeNodes),
				new CommandData(SBConfig.SELECTOR_PASTE_COMMAND.getValue(), Permission.COMMAND_SELECTOR.getNode()),
				new CommandData(SBConfig.SELECTOR_REMOVE_COMMAND.getValue(), Permission.COMMAND_SELECTOR.getNode())
		};
	}

	@Override
	public boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (args.length == 1) {
			if (equals(args[0], "tool")) {
				return doTool(sender);
			} else if (equals(args[0], "reload")) {
				return doReload(sender);
			} else if (equals(args[0], "backup")) {
				return doBackup(sender);
			} else if (equals(args[0], "checkver")) {
				return doCheckVer(sender);
			} else if (equals(args[0], "datamigr")) {
				return doDataMigr(sender);
			}
		} else if (args.length == 2) {
			if (equals(args[0], "export") && equals(args[1], "sound", "material")) {
				return doExport(sender, args);
			} if (equals(args[0], ScriptType.types()) && equals(args[1], "remove", "view")) {
				return setAction(sender, args);
			} else if (equals(args[0], "selector") && equals(args[1], "remove")) {
				return doSelectorRemove(sender);
			} else if (equals(args[0], "selector") && equals(args[1], "paste")) {
				return doSelectorPaste(sender, args);
			}
		} else if (args.length > 2) {
			if (args.length < 5 && equals(args[0], "selector") && equals(args[1], "paste")) {
				return doSelectorPaste(sender, args);
			} else if (equals(args[0], ScriptType.types())) {
				if (args.length == 6 && equals(args[1], "run")) {
					return doRun(sender, args);
				} else if (equals(args[1], "create", "add")) {
					return setAction(sender, args);
				}
			}
		}
		return false;
	}

	private boolean doExport(@NotNull CommandSender sender, @NotNull String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_EXPORT, false)) {
			return false;
		}
		boolean isSound = args[1].equalsIgnoreCase("sound");
		String type = isSound ? "Sound" : "Material";
		SBConfig.EXPORT_START.replace(type).send(sender);
		String path = "export/" + type.toLowerCase() + "_v" + Utils.getServerVersion() + "_.txt";
		File file = new File(getPlugin().getDataFolder(), path);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		new Thread(() -> {
			try (
				OutputStream os = new FileOutputStream(file);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charsets.UTF_8))
			) {
				for (Enum<?> t : isSound ? Sound.values() : Material.values()) {
					writer.write(t.name());
					writer.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				SBConfig.EXPORT_END.replace(type).send(sender);
			}
		}).start();
		return true;
	}

	private boolean doTool(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_TOOL)) {
			return false;
		}
		Player player = (Player) sender;
		player.getInventory().addItem(ItemUtils.getBlockSelector());
		player.getInventory().addItem(ItemUtils.getScriptEditor(ScriptType.INTERACT));
		player.getInventory().addItem(ItemUtils.getScriptViewer());
		Utils.updateInventory(player);
		SBConfig.GIVE_TOOL.send(player);
		return true;
	}

	private boolean doReload(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_RELOAD, false)) {
			return false;
		}
		Files.reload();
		NameFetcher.clear();
		PackageType.clear();
		setUsage(getUsages());
		ScriptBlock.getInstance().getMapManager().loadAllScripts();
		SBConfig.ALL_FILE_RELOAD.send(sender);
		return true;
	}

	private boolean doBackup(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_BACKUP, false)) {
			return false;
		}
		File dataFolder = Files.getConfig().getDataFolder();
		File scripts = new File(dataFolder, "scripts");
		if (!scripts.exists() || FileUtils.isEmpty(scripts)) {
			SBConfig.ERROR_SCRIPTS_BACKUP.send(sender);
			return true;
		}
		File backup = new File(scripts, "backup");
		String formatTime = Utils.getFormatTime("yyyy-MM-dd HH-mm-ss");
		FileUtils.copyDirectory(scripts, new File(backup, "scripts " + formatTime), File::isDirectory);
		SBConfig.SCRIPTS_BACKUP.send(sender);
		return true;
	}

	private boolean doCheckVer(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_CHECKVER, false)) {
			return false;
		}
		ScriptBlock.getInstance().checkUpdate(sender, true);
		return true;
	}

	private boolean doDataMigr(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_DATAMIGR)) {
			return false;
		}
		String path = "plugins/ScriptBlock/BlocksData/";
		File interactFile = new File(path + "interact_Scripts.yml");
		File walkFile = new File(path + "walk_Scripts.yml");
		Player player = (Player) sender;
		if (!walkFile.exists() && !interactFile.exists()) {
			SBConfig.NOT_SCRIPT_BLOCK_FILE.send(sender);
		} else {
			String time = Utils.getFormatTime();
			String uuid = player.getUniqueId().toString();
			SBConfig.DATAMIGR_START.send(sender);
			new Thread(() -> {
				if (interactFile.exists()) {
					saveScript(uuid, time, YamlConfig.load(getPlugin(), interactFile, false), ScriptType.INTERACT);
				}
				if (walkFile.exists()) {
					saveScript(uuid, time, YamlConfig.load(getPlugin(), walkFile, false), ScriptType.WALK);
				}
				SBConfig.DATAMIGR_END.send(sender);
			}).start();
		}
		return true;
	}

	private void saveScript(@NotNull String uuid, @NotNull String time, @NotNull YamlConfig scriptFile, @NotNull ScriptType scriptType) {
		ScriptData scriptData = new ScriptData(scriptType);
		for (String world : scriptFile.getKeys()) {
			World tWorld = Objects.requireNonNull(Utils.getWorld(world));
			for (String coords : scriptFile.getKeys(world)) {
				List<String> scripts = scriptFile.getStringList(world + "." + coords);
				if (scripts.size() > 0 && scripts.get(0).startsWith("Author:")) {
					scripts.remove(0);
				}
				for (int i = 0; i < scripts.size(); i++) {
					if (scripts.get(i).contains("@cooldown:")) {
						scripts.set(i, StringUtils.replace(scripts.get(i), "@cooldown:", "@oldcooldown:"));
					}
				}
				scriptData.setLocation(BlockCoords.fromString(tWorld, coords));
				scriptData.setAuthor(uuid);
				scriptData.setLastEdit(time);
				scriptData.setScripts(scripts);
			}
		}
		scriptData.save();
		scriptData.reload();
	}

	private boolean doRun(@NotNull CommandSender sender, @NotNull String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!isPlayer(sender) || !Permission.has(sender, scriptType, true)) {
			return false;
		}
		Player player = (Player) sender;
		World world = Utils.getWorld(args[2]);
		int x = Integer.parseInt(args[3]);
		int y = Integer.parseInt(args[4]);
		int z = Integer.parseInt(args[5]);
		Location location = new Location(world, x, y, z);
		ScriptBlock.getInstance().getAPI().scriptRead(player, location, scriptType, 0);
		return true;
	}

	private boolean setAction(@NotNull CommandSender sender, @NotNull String[] args) {
		ScriptType scriptType = ScriptType.valueOf(args[0].toUpperCase());
		if (!isPlayer(sender) || !Permission.has(sender, scriptType, true)) {
			return false;
		}
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (sbPlayer.getScriptLine().isPresent() || sbPlayer.getActionType().isPresent()) {
			SBConfig.ERROR_ACTION_DATA.send(sbPlayer);
			return true;
		}
		if (args.length > 2) {
			String script = StringUtils.createString(args, 2).trim();
			if (!isScripts(script)) {
				SBConfig.ERROR_SCRIPT_CHECK.send(sbPlayer);
				return true;
			}
			sbPlayer.setScriptLine(script);
		}
		ActionType actionType = ActionType.valueOf(args[1].toUpperCase());
		sbPlayer.setActionType(actionType.getKey(scriptType));
		SBConfig.SUCCESS_ACTION_DATA.replace(scriptType.type() + "-" + actionType.name().toLowerCase()).send(sbPlayer);
		return true;
	}

	private boolean doSelectorRemove(@NotNull CommandSender sender) {
		if (!hasPermission(sender, Permission.COMMAND_SELECTOR)) {
			return false;
		}
		Player player = (Player) sender;
		Region region = SBPlayer.fromPlayer(player).getRegion();
		if (!region.hasPositions()) {
			SBConfig.NOT_SELECTION.send(sender);
			return true;
		}
		CuboidRegionBlocks regionBlocks = new CuboidRegionBlocks(region);
		Set<Block> blocks = regionBlocks.getBlocks();
		StrBuilder builder = new StrBuilder();
		for (ScriptType scriptType : ScriptType.values()) {
			if (!Files.getScriptFile(scriptType).exists()) {
				continue;
			}
			ScriptEdit scriptEdit = new ScriptEdit(scriptType);
			for (Block block : blocks) {
				if (scriptEdit.lightRemove(block.getLocation()) && builder.indexOf(scriptType.type()) == -1) {
					builder.append(builder.length() == 0 ? "" : ", ").append(scriptType.type());
				}
			}
			scriptEdit.save();
		}
		if (builder.length() == 0) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sender);
		} else {
			String types = builder.toString();
			SBConfig.SELECTOR_REMOVE.replace(types, regionBlocks.getCount()).send(player);
			SBConfig.CONSOLE_SELECTOR_REMOVE.replace(types, regionBlocks).console();
		}
		return true;
	}

	private boolean doSelectorPaste(@NotNull CommandSender sender, @NotNull String[] args) {
		if (!hasPermission(sender, Permission.COMMAND_SELECTOR)) {
			return false;
		}
		boolean pasteonair = args.length > 2 && Boolean.parseBoolean(args[2]);
		boolean overwrite = args.length > 3 && Boolean.parseBoolean(args[3]);
		SBPlayer sbPlayer = SBPlayer.fromPlayer((Player) sender);
		if (!sbPlayer.getClipboard().isPresent()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sender);
			return true;
		}
		Region region = sbPlayer.getRegion();
		if (!region.hasPositions()) {
			SBConfig.NOT_SELECTION.send(sender);
			return true;
		}
		CuboidRegionBlocks regionBlocks = new CuboidRegionBlocks(region);
		SBClipboard clipboard = sbPlayer.getClipboard().get();
		sbPlayer.setClipboard(null);
		for (Block block : regionBlocks.getBlocks()) {
			if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
				continue;
			}
			clipboard.lightPaste(block.getLocation(), overwrite);
		}
		clipboard.save();
		String scriptType = clipboard.getScriptType().type();
		SBConfig.SELECTOR_PASTE.replace(scriptType, regionBlocks.getCount()).send(sbPlayer);
		SBConfig.CONSOLE_SELECTOR_PASTE.replace(scriptType, regionBlocks).console();
		return true;
	}

	@Override
	public void tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull List<String> empty) {
		if (args.length == 1) {
			String prefix = args[0].toLowerCase();
			Set<String> set = setCommandPermissions(sender, new LinkedHashSet<>());
			StreamUtils.fForEach(set, s -> StringUtils.startsWith(s, prefix), empty::add);
		} else if (args.length == 2) {
			if (equals(args[0], "export")) {
				if (Permission.COMMAND_EXPORT.has(sender)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[] { "sound", "material" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			} else if (equals(args[0], "selector")) {
				if (Permission.COMMAND_SELECTOR.has(sender)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[] { "paste", "remove" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			} else if (equals(args[0], ScriptType.types())) {
				if (Permission.has(sender, ScriptType.valueOf(args[0].toUpperCase()), true)) {
					String prefix = args[1].toLowerCase();
					String[] answers = new String[] { "create", "add", "remove", "view", "run" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			}
		} else if (args.length > 2) {
			if (args.length == 3 && equals(args[0], "selector") && equals(args[1], "paste")) {
				if (Permission.COMMAND_SELECTOR.has(sender)) {
					String prefix = args[2].toLowerCase();
					String[] answers = new String[] { "true", "false" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			} else if (args.length == 4 && equals(args[0], "selector") && equals(args[1], "paste")) {
				if (Permission.COMMAND_SELECTOR.has(sender)) {
					String prefix = args[3].toLowerCase();
					String[] answers = new String[] { "true", "false" };
					StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
				}
			} else if (equals(args[0], ScriptType.types())) {
				if (Permission.has(sender, ScriptType.valueOf(args[0].toUpperCase()), true)) {
					if (args.length == 3 && equals(args[1], "run")) {
						List<World> worlds = Bukkit.getWorlds();
						String prefix = args[args.length - 1].toLowerCase();
						String[] answers = StreamUtils.toArray(worlds, World::getName, new String[worlds.size()]);
						StreamUtils.fForEach(answers, s -> s.startsWith(prefix), empty::add);
					} else if (equals(args[1], "create", "add")) {
						String prefix = args[args.length - 1].toLowerCase();
						String[] answers = OptionManager.getSyntaxs();
						Arrays.sort(answers);
						StreamUtils.fForEach(answers, s -> s.startsWith(prefix), s -> empty.add(s.trim()));
					}
				}
			}
		}
	}

	private Set<String> setCommandPermissions(CommandSender sender, Set<String> set) {
		set.add(hasPermission(sender, Permission.COMMAND_TOOL, "tool"));
		set.add(hasPermission(sender, Permission.COMMAND_RELOAD, "reload"));
		set.add(hasPermission(sender, Permission.COMMAND_BACKUP, "backup"));
		set.add(hasPermission(sender, Permission.COMMAND_CHECKVER, "checkver"));
		set.add(hasPermission(sender, Permission.COMMAND_DATAMIGR, "datamigr"));
		set.add(hasPermission(sender, Permission.COMMAND_EXPORT, "export"));

		StreamUtils.fForEach(ScriptType.values(), s -> Permission.has(sender, s, true), s -> set.add(s.type()));

		set.add(hasPermission(sender, Permission.COMMAND_SELECTOR, "selector"));
		return set;
	}

	private String hasPermission(CommandSender sender, Permission permission, String name) {
		return StringUtils.isNotEmpty(permission.getNode()) && permission.has(sender) ? name : null;
	}

	private boolean isScripts(String scriptLine) {
		try {
			int[] success = { 0 };
			List<String> scripts = StringUtils.getScripts(scriptLine);
			StreamUtils.fForEach(scripts, OptionManager::has, o -> success[0]++);
			if (success[0] == 0 || success[0] != scripts.size()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}