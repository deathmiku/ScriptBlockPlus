package com.github.yuttyann.scriptblockplus.utils;

import com.google.common.base.Charsets;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * ScriptBlockPlus FileUtils クラス
 * @author yuttyann44581
 */
public final class FileUtils {

	@Nullable
	public static InputStream getResource(@NotNull Plugin plugin, @NotNull String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		try {
			URL url = plugin.getClass().getClassLoader().getResource(filePath);
			if (url == null) {
				return null;
			}
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (IOException e) {
			return null;
		}
	}

	public static void copyFileFromPlugin(@NotNull Plugin plugin, @NotNull File targetFile, @NotNull String sourceFilePath) {
		if (StringUtils.isEmpty(sourceFilePath)) {
			return;
		}
		File parent = targetFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		InputStream is = getResource(plugin, sourceFilePath);
		if (is == null) {
			return;
		}
		try (
			FileOutputStream os = new FileOutputStream(targetFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charsets.UTF_8))
		) {
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void copyDirectory(@NotNull File sourceFile, @NotNull File targetFile, @NotNull Predicate<File> filter) {
		if (isEmpty(sourceFile)) {
			return;
		}
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		for (File file : Objects.requireNonNull(sourceFile.listFiles())) {
			if (filter.test(file)) {
				continue;
			}
			File copy = new File(targetFile, file.getName());
			try (
				InputStream is = new FileInputStream(file);
				FileOutputStream fos = new FileOutputStream(copy);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, Charsets.UTF_8))
			) {
				String line;
				while ((line = reader.readLine()) != null) {
					writer.write(line);
					writer.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void fileDownload(@NotNull String url, @NotNull File file) throws IOException {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try (
			InputStream is = getWebFile(url);
			FileOutputStream fos = new FileOutputStream(file)
		) {
			if (is == null) {
				return;
			}
			byte[] bytes = new byte[1024];
			int length;
			while ((length = is.read(bytes)) != -1) {
				fos.write(bytes, 0, length);
			}
		}
	}

	public static void saveFile(@NotNull File file, @NotNull Object value) throws IOException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
			os.writeObject(value);
		}
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static <T> T loadFile(@NotNull File file) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			return (T) ois.readObject();
		}
	}

	@Nullable
	public static InputStream getWebFile(@NotNull String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("GET");
		connection.setAllowUserInteraction(false);
		connection.setInstanceFollowRedirects(true);
		connection.connect();
		int httpStatusCode = connection.getResponseCode();
		if (httpStatusCode == HttpURLConnection.HTTP_OK) {
			return connection.getInputStream();
		}
		connection.disconnect();
		return null;
	}

	public static boolean isEmpty(@NotNull File file) {
		String[] array = file.list();
		return array == null || array.length == 0;
	}
}