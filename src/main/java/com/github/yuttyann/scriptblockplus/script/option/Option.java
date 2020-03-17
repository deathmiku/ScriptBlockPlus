package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.OptionList;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * オプション クラス
 * @author yuttyann44581
 */
public abstract class Option implements SBInstance<Option> {

	private static final String PERMISSION_PREFIX = "scriptblockplus.option.";

	private final String name;
	private final String syntax;

	/**
	 * コンストラクタ
	 * @param name オプション名　[（例） example]
	 * @param syntax オプション構文　[（例） @example: ]
	 */
	protected Option(@NotNull String name, @NotNull String syntax) {
		this.name = Objects.requireNonNull(name);
		this.syntax = Objects.requireNonNull(syntax);
	}

	/**
	 * インスタンスを生成する
	 * @return Option
	 */
	@NotNull
	@Override
	public Option newInstance() {
		return OptionList.getManager().newInstance(this, InstanceType.REFLECTION);
	}

	/**
	 * オプション名を取得する
	 * @return オプション名
	 */
	@NotNull
	public final String getName() {
		return name;
	}

	/**
	 * 構文を取得する
	 * @return 構文
	 */
	@NotNull
	public final String getSyntax() {
		return syntax;
	}

	/**
	 * パーミッションノードを取得する
	 * @return パーミッション
	 */
	@NotNull
	public final String getPermissionNode() {
		return PERMISSION_PREFIX + name;
	}

	/**
	 * スクリプトからオプションの値を取得
	 * @return 値
	 */
	@NotNull
	public final String getValue(@NotNull String script) {
		return StringUtils.removeStart(script, syntax);
	}

	/**
	 * スクリプトがオプションなのかどうかチェックする
	 * @return オプションなのかどうか
	 */
	public final boolean isOption(@NotNull String script) {
		return StringUtils.isNotEmpty(script) && script.startsWith(syntax);
	}

	/**
	 * 失敗時に終了処理を無視するかどうか</br>
	 * 戻り値が true の場合は無視します
	 * @return 無視するかどうか
	 */
	public boolean isFailedIgnore() {
		return false;
	}

	/**
	 * オプションを実行する</br>
	 * @param sbRead
	 * @return 実行が成功したかどうか
	 */
	public abstract boolean callOption(@NotNull SBRead sbRead);

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Option) {
			Option option = (Option) obj;
			return name.equals(option.getName()) && syntax.equals(option.getSyntax());
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		int prime = 31;
		hash = prime * hash + name.hashCode();
		hash = prime * hash + syntax.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return "Option{name: " + name + ", syntax: " + syntax + "}";
	}
}