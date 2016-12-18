package com.ly.utils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.ly.linq.Enm;
import com.ly.linq.FuncT;
import com.ly.linq.Pair;
import com.ly.linq.Pre;

/**
 * 类相关的工具类
 */
public class Cls {
	/**
	 * 获取类的同包中的所有子类
	 */
	public static ArrayList<Class<?>> getAllSubClass(Class<?> self) {
		ArrayList<Class<?>> all = getClasses(self.getPackage().getName(), true);
		if (all == null) {
			return null;
		}
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		for (Class<?> cls : all) {
			if (!self.isAssignableFrom(cls) || self.equals(cls)) {
				continue;
			}
			result.add(cls);
		}
		return result;
	}

	public static ArrayList<Class<?>> getClasses(String packageName, boolean recursive) {
		String dir = packageName.replace('.', '/');
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> dirs;
		try {
			dirs = loader.getResources(dir);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				String filePath;
				try {
					filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				result.addAll(fromFile(packageName, filePath, recursive));
				continue;
			}
			if ("jar".equals(protocol)) {
				JarFile jar;
				try {
					jar = ((JarURLConnection) url.openConnection()).getJarFile();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (!name.startsWith(dir) || !userDefined(name)) {
						continue;
					}
					Class<?> cls;
					try {
						cls = Class.forName(name.replace('/', '.').substring(0, name.indexOf('.')));
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					result.add(cls);
				}
			}
		}
		return result;
	}

	private static ArrayList<Class<?>> fromFile(String packageName, String packagePath, final boolean recursive) {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		File[] files = new File(packagePath).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || userDefined(file.getName());
			}
		});
		for (File f : files) {
			String n = f.getName();
			if (f.isDirectory()) {
				result.addAll(fromFile(packageName + "." + n, f.getAbsolutePath(), recursive));
				continue;
			}
			Class<?> c;
			try {
				c = Class.forName(packageName + '.' + n.replace(".class", ""));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			result.add(c);
		}
		return result;
	}

	private static boolean userDefined(String name) {
		if (!name.endsWith(".class")) {
			return false;
		}
		for (int i = 0; i < name.length() - 1; i++) {
			if (name.charAt(i) == '$') {
				int c = name.charAt(i + 1);
				if (c < 58 && c > 47) {
					return false;
				}

			}
		}
		return true;
	}

	public static ArrayList<String> getFieldNames(Class<?> cls) {
		FuncT<Field, String> mapper = new FuncT<Field, String>() {
			@Override
			public String get(Field ti) {
				return ti.getName();
			}
		};
		return Enm.select(cls.getDeclaredFields(), mapper);
	}

	public static HashMap<String, Object> toHashMap(final Object t, Pre<Field> filter) {
		FuncT<Field, Pair<String, Object>> pair = new FuncT<Field, Pair<String, Object>>() {
			@Override
			public Pair<String, Object> get(Field ti) {
				Object value;
				try {
					value = ti.get(t);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				return new Pair<String, Object>(ti.getName(), value);
			}
		};
		return Enm.toHashMap(Enm.where(t.getClass().getDeclaredFields(), filter), pair);
	}

}