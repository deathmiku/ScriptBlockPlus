package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ScriptBlockPlus ScriptMap クラス
 * @author yuttyann44581
 */
abstract class ScriptMap extends ScriptListener implements ObjectMap {

	private static final Map<UUID, Map<String, Object>> OBJECT_MAP = new HashMap<>();

	private final UUID ramdomId = UUID.randomUUID();

	ScriptMap(@NotNull ScriptListener listener) {
		super(listener);
	}

	@Override
	public void put(@NotNull String key, @Nullable Object value) {
		OBJECT_MAP.computeIfAbsent(ramdomId, k -> new HashMap<>()).put(key, value);
	}

	@Override
	@Nullable
	public <T> T get(@NotNull String key) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map == null ? null : (T) map.get(key);
	}

	@Override
	public void remove(@NotNull String key) {
		OBJECT_MAP.get(ramdomId).remove(key);
	}

	@Override
	public void clear() {
		OBJECT_MAP.remove(ramdomId);
	}
}