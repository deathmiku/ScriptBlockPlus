package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractConstructor<T> {

	private T cacheInstance;
	private List<T> cacheList;

	public abstract void registerDefaults();

	public int size() {
		return getConstructors().size();
	}

	public boolean isEmpty() {
		return getConstructors().isEmpty();
	}

	public int indexOf(Class<? extends T> clazz) {
		return getConstructors().indexOf(getConstructor(clazz));
	}

	public void add(Class<? extends T> clazz) {
		getConstructors().add(getConstructor(clazz));
	}

	public void add(int index, Class<? extends T> clazz) {
		getConstructors().add(index, getConstructor(clazz));
	}

	public void remove(Class<? extends T> clazz) {
		getConstructors().remove(getConstructor(clazz));
	}

	public void remove(int index) {
		getConstructors().remove(index);
	}

	public abstract List<Constructor<? extends T>> getConstructors();

	public final T getCacheInstance() {
		return cacheInstance;
	}

	public final List<T> getCacheList() {
		if (cacheList == null) {
			newInstances();
		}
		return Collections.unmodifiableList(cacheList);
	}

	public T[] newInstances() {
		return newInstances(getGenericArray());
	}

	public final T[] newInstances(T[] array) {
		clearCacheList();
		T[] instances = array;
		try {
			int i = 0;
			for (Constructor<? extends T> constructor : getConstructors()) {
				cacheList.add(instances[i++] = constructor.newInstance());
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return instances;
	}

	public final T newInstance(T t) {
		return newInstance(t.getClass());
	}

	public final T newInstance(Class<?> clazz) {
		clearCacheInstance();
		try {
			for (Constructor<? extends T> constructor : getConstructors()) {
				if (clazz == constructor.getDeclaringClass()) {
					return cacheInstance = constructor.newInstance();
				}
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected final void clearCacheInstance() {
		cacheInstance = null;
	}

	protected final void clearCacheList() {
		if (cacheList == null) {
			cacheList = new ArrayList<T>(getConstructors().size());
		} else {
			cacheList.clear();
		}
		clearCacheInstance();
	}

	private T[] getGenericArray() {
		try {
			Type type = getClass().getGenericSuperclass();
			String className = ((ParameterizedType) type).getActualTypeArguments()[0].getTypeName();
			@SuppressWarnings("unchecked")
			T[] array = (T[]) Array.newInstance(Class.forName(className), getConstructors().size());
			return array;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private <R extends T> Constructor<R> getConstructor(Class<R> clazz) {
		try {
			Constructor<R> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
}