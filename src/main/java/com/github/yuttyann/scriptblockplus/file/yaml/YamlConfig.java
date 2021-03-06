package com.github.yuttyann.scriptblockplus.file.yaml;

import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * ScriptBlockPlus YamlConfig クラス
 * @author yuttyann44581
 */
public class YamlConfig {

	private static final Pattern UUID_PATTERN = Pattern.compile(
			"[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"
	);

	private final Plugin plugin;
	private final File file;

	private UTF8Config yaml;
	private boolean isCopyFile;

	protected YamlConfig(@NotNull Plugin plugin, @NotNull File file, boolean isCopyFile) {
		this.plugin = plugin;
		this.file = file;
		this.isCopyFile = isCopyFile;
		reload();
	}

	@NotNull
	public static YamlConfig load(@NotNull Plugin plugin, @NotNull File file) {
		return load(plugin, file, true);
	}

	@NotNull
	public static YamlConfig load(@NotNull Plugin plugin, @NotNull File file, boolean isCopyFile) {
		return new YamlConfig(plugin, file, isCopyFile);
	}

	@NotNull
	public static YamlConfig load(@NotNull Plugin plugin, @NotNull String filePath) {
		return load(plugin, filePath, true);
	}

	@NotNull
	public static YamlConfig load(@NotNull Plugin plugin, @NotNull String filePath, boolean isCopyFile) {
		return load(plugin, new File(plugin.getDataFolder(), filePath), isCopyFile);
	}

	@NotNull
	public final YamlConfig setCopyFile(boolean isCopyFile) {
		this.isCopyFile = isCopyFile;
		return this;
	}

	@NotNull
	public final Plugin getPlugin() {
		return plugin;
	}

	@NotNull
	public final File getDataFolder() {
		return plugin.getDataFolder();
	}

	@NotNull
	public final File getFile() {
		return file;
	}

	@NotNull
	public final String getFileName() {
		return file.getName();
	}

	@NotNull
	public final String getPath() {
		return file.getPath();
	}

	@NotNull
	public final String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	@NotNull
	public final String getFolderPath() {
		String path = StringUtils.removeStart(getPath(), getDataFolder().getPath());
		return path.startsWith(File.separator) ? path.substring(1) : path;
	}

	public final boolean exists() {
		return file.exists();
	}

	public final long length() {
		return file.length();
	}

	public final void reload() {
		Validate.notNull(file, "File cannot be null");
		if (isCopyFile && !file.exists()) {
			FileUtils.copyFileFromPlugin(plugin, file, getFolderPath());
		}
		yaml = new UTF8Config();
		try {
			yaml.load(file);
		} catch (FileNotFoundException ignored) {
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save(@NotNull File file) throws IOException {
		yaml.save(file);
	}

	public void save(@NotNull String file) throws IOException {
		yaml.save(file);
	}

	public void addDefault(@NotNull String path, @Nullable Object value) {
		yaml.addDefault(path, value);
	}

	public void addDefault(@NotNull Configuration defaults) {
		yaml.addDefaults(defaults);
	}

	public void addDefault(@NotNull Map<String, Object> defaults) {
		yaml.addDefaults(defaults);
	}

	public void set(@NotNull String path, @Nullable Object value) {
		yaml.set(path, value);
	}

	public void setDefaults(@NotNull Configuration defaults) {
		yaml.setDefaults(defaults);
	}

	@NotNull
	public ConfigurationSection createSection(@NotNull String path) {
		return yaml.createSection(path);
	}

	@NotNull
	public ConfigurationSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
		return yaml.createSection(path, map);
	}

	@Nullable
	public ConfigurationSection getConfigurationSection(@NotNull String path) {
		return yaml.getConfigurationSection(path);
	}

	@Nullable
	public Configuration getDefaults() {
		return yaml.getDefaults();
	}

	@Nullable
	public ConfigurationSection getDefaultSection() {
		return yaml.getDefaultSection();
	}

	@Nullable
	public ConfigurationSection getParent() {
		return yaml.getParent();
	}

	@Nullable
	public Configuration getRoot() {
		return yaml.getRoot();
	}

	@NotNull
	public String getCurrentPath() {
		return yaml.getCurrentPath();
	}

	@NotNull
	public String getName() {
		return yaml.getName();
	}

	@Nullable
	public String getString(@NotNull String path) {
		return yaml.getString(path);
	}

	@NotNull
	public String getString(@NotNull String path, @NotNull String def) {
		return Objects.requireNonNull(yaml.getString(path, def));
	}

	@Nullable
	public Object get(@NotNull String path) {
		return yaml.get(path);
	}

	@NotNull
	public Object get(@NotNull String path, @NotNull Object def) {
		return Objects.requireNonNull(yaml.get(path, def));
	}

	@Nullable
	public UUID getUUID(@NotNull String path) {
		String val = getString(path);
		return val == null ? null : UUID.fromString(val);
	}

	@NotNull
	public UUID getUUID(@NotNull String path, @NotNull UUID def) {
		Object val = yaml.get(path, def);
		return val == null ? def : UUID.fromString(val.toString());
	}

	@Nullable
	public Color getColor(@NotNull String path) {
		return yaml.getColor(path);
	}

	@NotNull
	public Color getColor(@NotNull String path, @NotNull Color def) {
		return Objects.requireNonNull(yaml.getColor(path, def));
	}

	@Nullable
	public ItemStack getItemStack(@NotNull String path) {
		return yaml.getItemStack(path);
	}

	@NotNull
	public ItemStack getItemStack(@NotNull String path, @NotNull ItemStack def) {
		return Objects.requireNonNull(yaml.getItemStack(path, def));
	}

	@Nullable
	public Vector getVector(@NotNull String path) {
		return yaml.getVector(path);
	}

	@NotNull
	public Vector getVector(@NotNull String path, @NotNull Vector def) {
		return Objects.requireNonNull(yaml.getVector(path, def));
	}

	@NotNull
	public FileConfigurationOptions options() {
		return yaml.options();
	}

	public boolean contains(@NotNull String path) {
		return yaml.contains(path);
	}

	public boolean getBoolean(@NotNull String path) {
		return yaml.getBoolean(path);
	}

	public boolean getBoolean(@NotNull String path, boolean def) {
		return yaml.getBoolean(path, def);
	}

	public boolean isString(@NotNull String path) {
		return yaml.isString(path);
	}

	public boolean isUUID(@NotNull String path) {
		String val = getString(path);
		return val != null && UUID_PATTERN.matcher(val).matches();
	}

	public boolean isColor(@NotNull String path) {
		return yaml.isColor(path);
	}

	public boolean isItemStack(@NotNull String path) {
		return yaml.isItemStack(path);
	}

	public boolean isVector(@NotNull String path) {
		return yaml.isVector(path);
	}

	public boolean isBoolean(@NotNull String path) {
		return yaml.isBoolean(path);
	}

	public boolean isOfflinePlayer(@NotNull String path) {
		return yaml.isOfflinePlayer(path);
	}

	public boolean isConfigurationSection(@NotNull String path) {
		return yaml.isConfigurationSection(path);
	}

	public boolean isInt(@NotNull String path) {
		return yaml.isInt(path);
	}

	public boolean isDouble(@NotNull String path) {
		return yaml.isDouble(path);
	}

	public boolean isFloat(@NotNull String path) {
		Object val = get(path);
		return val instanceof Float;
	}

	public boolean isLong(@NotNull String path) {
		return yaml.isLong(path);
	}

	public boolean isSet(@NotNull String path) {
		return yaml.isSet(path);
	}

	public boolean isList(@NotNull String path) {
		return yaml.isList(path);
	}

	public int getInt(@NotNull String path) {
		return yaml.getInt(path);
	}

	public int getInt(@NotNull String path, int def) {
		return yaml.getInt(path, def);
	}

	public double getDouble(@NotNull String path) {
		return yaml.getDouble(path);
	}

	public double getDouble(@NotNull String path, double def) {
		return yaml.getDouble(path, def);
	}

	public float getFloat(@NotNull String path) {
		Object def = getDefault(path);
		return getFloat(path, def instanceof Number ? NumberConversions.toFloat(def) : 0.0F);
	}

	public float getFloat(@NotNull String path, float def) {
		Object val = get(path, def);
		return val instanceof Number ? NumberConversions.toFloat(val) : def;
	}

	public long getLong(@NotNull String path) {
		return yaml.getLong(path);
	}

	public long getLong(@NotNull String path, long def) {
		return yaml.getLong(path, def);
	}

	@NotNull
	public Set<String> getKeys() {
		return yaml.getKeys(false);
	}

	@NotNull
	public Set<String> getKeys(boolean deep) {
		return yaml.getKeys(deep);
	}

	@NotNull
	public Set<String> getKeys(String path) {
		return getConfigurationSection(path).getKeys(false);
	}

	@NotNull
	public Set<String> getKeys(String path, boolean deep) {
		return getConfigurationSection(path).getKeys(deep);
	}

	@NotNull
	public Map<String, Object> getValues(boolean deep) {
		return yaml.getValues(deep);
	}

	@Nullable
	public List<?> getList(@NotNull String path) {
		return yaml.getList(path);
	}

	@Nullable
	public List<?> getList(@NotNull String path, List<?> def) {
		return yaml.getList(path, def);
	}

	@NotNull
	public List<Map<?, ?>> getMapList(@NotNull String path) {
		return yaml.getMapList(path);
	}

	@NotNull
	public List<String> getStringList(@NotNull String path) {
		return yaml.getStringList(path);
	}

	@NotNull
	public List<UUID> getUUIDList(@NotNull String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<>();
		}
		List<UUID> result = new ArrayList<>(list.size());
		for (Object object : list) {
			if (object instanceof String) {
				result.add(UUID.fromString(object.toString()));
			}
		}
		return result;
	}

	@NotNull
	public List<Boolean> getBooleanList(@NotNull String path) {
		return yaml.getBooleanList(path);
	}

	@NotNull
	public List<Character> getCharacterList(@NotNull String path) {
		return yaml.getCharacterList(path);
	}

	@NotNull
	public List<Integer> getIntegerList(@NotNull String path) {
		return yaml.getIntegerList(path);
	}

	@NotNull
	public List<Double> getDoubleList(@NotNull String path) {
		return yaml.getDoubleList(path);
	}

	@NotNull
	public List<Float> getFloatList(@NotNull String path) {
		return yaml.getFloatList(path);
	}

	@NotNull
	public List<Long> getLongList(@NotNull String path) {
		return yaml.getLongList(path);
	}

	@NotNull
	public List<Short> getShortList(@NotNull String path) {
		return yaml.getShortList(path);
	}

	@NotNull
	public List<Byte> getByteList(@NotNull String path) {
		return yaml.getByteList(path);
	}

	@NotNull
	@Override
	public String toString() {
		return yaml.toString();
	}

	@Nullable
	protected Object getDefault(@NotNull String path) {
		Validate.notNull(path, "Path cannot be null");
		Configuration root = getRoot();
		Configuration defaults = root == null ? null : root.getDefaults();
		return defaults == null ? null : defaults.get(MemorySection.createPath(yaml, path));
	}
}