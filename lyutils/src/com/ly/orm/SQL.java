package com.ly.orm;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import com.ly.linq.ActionT;
import com.ly.linq.Enm;
import com.ly.linq.FuncT;
import com.ly.linq.FuncTT;
import com.ly.linq.Pair;
import com.ly.linq.Pre;
import com.ly.utils.Cls;
import com.ly.utils.Text;

public class SQL {

	private static Pre<Field> isColumn = new Pre<Field>() {
		@Override
		public boolean check(Field ti) {
			return ti.getAnnotation(Insert.class) != null;
		}
	};
	private static FuncT<Field, Integer> byIndex = new FuncT<Field, Integer>() {
		@Override
		public Integer get(Field ti) {
			return ti.getAnnotation(Insert.class).index();
		}
	};
	private static FuncT<Field, String> getColumnWithData = new FuncT<Field, String>() {
		@Override
		public String get(Field ti) {
			String desc = ti.getAnnotation(Insert.class).desc();
			if (desc.charAt(0) == ' ') {
				return desc.substring(1);
			}
			return ti.getName() + ' ' + desc;
		}
	};

	public interface QueryLoader<T> extends FuncT<ValueGetter, T> {
	}

	public interface ValueGetter extends FuncTT<String, Class<?>, Object> {
	}

	public static <T> QueryLoader<T> load(final Class<T> cls) {
		return new QueryLoader<T>() {
			@Override
			public T get(ValueGetter nameToValue) {
				T r;
				try {
					r = cls.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				Field[] fs = cls.getDeclaredFields();
				for (Field f : fs) {
					Query anno = f.getAnnotation(Query.class);
					String name = anno == null ? f.getName() : anno.name();
					Object val = nameToValue.get(name, f.getType());
					f.setAccessible(true);
					try {
						f.set(r, val);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
				return r;
			}
		};
	}

	public static <T> T query(Class<T> cls, FuncTT<String, Class<?>, Object> nameToValue) {
		T r;
		try {
			r = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs) {
			Query anno = f.getAnnotation(Query.class);
			String name = anno == null ? f.getName() : anno.name();
			Object val = nameToValue.get(name, f.getClass());
			f.setAccessible(true);
			try {
				f.set(r, val);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return r;
	}

	public static HashMap<String, Object> getKey(final Object obj) {
		return Enm.toHashMap(Enm.where(obj.getClass().getDeclaredFields(), new Pre<Field>() {
			@Override
			public boolean check(Field t) {
				Insert insert = t.getAnnotation(Insert.class);
				return insert != null && insert.desc().contains("key");
			}
		}), new FuncT<Field, Pair<String, Object>>() {
			@Override
			public Pair<String, Object> get(Field ti) {
				Object v;
				ti.setAccessible(true);
				try {
					v = ti.get(obj);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				String k;
				Insert insert = ti.getAnnotation(Insert.class);
				if (insert.desc().startsWith(" ")) {
					String sub = insert.desc().substring(1);
					k = sub.substring(0, sub.indexOf(" "));
				} else {
					k = ti.getName();
				}
				return new Pair<String, Object>(k, v);
			}
		});
	}

	public static <T> String getUpdateSQL(T t, ActionT<T> action) {
		HashMap<String, Object> oldOne = Cls.toHashMap(t, isColumn);
		action.doo(t);
		HashMap<String, Object> newOne = Cls.toHashMap(t, isColumn);
		HashMap<String, Object> changes = new HashMap<String, Object>();
		for (String k : oldOne.keySet()) {
			Object ov = oldOne.get(k);
			Object nv = newOne.get(k);
			if (ov == null) {
				if (nv == null) {
					continue;
				}
			} else {
				if (ov.equals(nv)) {
					continue;
				}
			}
			changes.put(k, nv);
		}
		return String.format("update %s set %s", getSqlTableName(t.getClass()), toEqualsSQL(changes, ',') + " where " + toEqualsSQL(getKey(t), " and "));
	}

	public static String toEqualsSQL(HashMap<String, Object> map, Object spliter) {
		StringBuilder sb = new StringBuilder();
		for (String k : map.keySet()) {
			sb.append(k + '=' + toSqlValue(map.get(k)) + spliter);
		}
		if (sb.length() == 0) {
			return null;
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public static String toSqlValue(Object v) {
		if (v == null) {
			v = "null";
		} else {
			if (v instanceof Boolean) {
				v = (Boolean) v ? 1 : 0;
			} else if (v instanceof Date) {
				v = "'" + DateFormat.getDateTimeInstance().format(v) + "'";
			} else if (v instanceof byte[]) {
				v = Text.toHex((byte[]) v);
			} else {
				v = "'" + v + "'";
			}
		}
		return v.toString();
	}

	public static <T> String getCreateSQL(Class<T> cls) {
		String cols = Enm.toString(Enm.select(Enm.sort(Enm.where(
				cls.getFields(),
				isColumn), byIndex), getColumnWithData), ',');
		if (cols.length() == 0) {
			return cols;
		}
		return String.format("create table %s(%s);", getSqlTableName(cls), cols);
	}

	private static <T> String getSqlTableName(Class<T> cls) {
		String table;
		Table tAnno = cls.getAnnotation(Table.class);
		if (tAnno != null) {
			table = tAnno.name();
		} else {
			table = cls.getSimpleName();
		}
		return table;
	}
}