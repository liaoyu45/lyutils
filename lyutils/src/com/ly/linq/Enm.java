package com.ly.linq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Enm {
	public static <T> ArrayList<T> where(T[] ts, Pre<T> mapper) {
		return where(toList(ts), mapper);
	}

	public static <T> ArrayList<T> where(Iterable<T> source, Pre<T> mapper) {
		ArrayList<T> r = new ArrayList<T>();
		for (T t : source) {
			if (mapper.check(t)) {
				r.add(t);
			}
		}
		return r;
	}

	public static <T> String toString(T[] source, Object s) {
		return toString(toList(source), s);
	}

	public static <T> String toString(T[] source, Object s, FuncT<T, String> mapper) {
		return toString(toList(source), s, mapper);
	}

	public static <T> String toString(Iterable<T> source, Object s) {
		return toString(source, s, null);
	}

	public static <T> String toString(Iterable<T> source, Object spliter, FuncT<T, String> mapper) {
		String s = spliter == null ? "" : spliter.toString();
		StringBuilder sb = new StringBuilder();
		for (T ti : source) {
			String str = mapper == null ? ti.toString() : mapper.get(ti);
			sb.append(str + s);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - s.length());
			return sb.toString();
		} else {
			return "";
		}
	}

	public static <T, TKey, TValue> HashMap<TKey, TValue> toHashMap(T[] ts, FuncT<T, Pair<TKey, TValue>> pair) {
		return toHashMap(toList(ts), pair);
	}

	public static <T, TKey, TValue> HashMap<TKey, TValue> toHashMap(Iterable<T> source, FuncT<T, Pair<TKey, TValue>> pair) {
		HashMap<TKey, TValue> r = new HashMap<TKey, TValue>();
		for (T t : source) {
			Pair<TKey, TValue> p = pair.get(t);
			r.put(p.getKey(), p.getValue());
		}
		return r;
	}

	public static <T> ArrayList<T> toList(Iterable<T> source) {
		ArrayList<T> r = new ArrayList<T>();
		for (T t : source) {
			r.add(t);
		}
		return r;
	}

	public static <T> ArrayList<T> toList(T[] ts) {
		ArrayList<T> r = new ArrayList<T>();
		for (T t : ts) {
			r.add(t);
		}
		return r;
	}

	public static <T> ArrayList<T> except(Iterable<T> it0, Iterable<T> it1) {
		ArrayList<T> r = new ArrayList<T>();
		for (T t : it0) {
			if (!r.contains(t)) {
				r.add(t);
			}
		}
		for (int i = r.size() - 1; i > -1; i--) {
			T t = r.get(i);
			if (contains(it1, t)) {
				r.remove(t);
			}
		}
		return r;
	}

	public static <T> ArrayList<T> union(Iterable<T> it0, Iterable<T> it1) {
		ArrayList<T> r = new ArrayList<T>();
		for (T t : it0) {
			if (!r.contains(t)) {
				r.add(t);
			}
		}
		for (T t : it1) {
			if (!r.contains(t)) {
				r.add(t);
			}
		}
		return r;
	}

	public static <T> ArrayList<T> intersect(Iterable<T> it0, Iterable<T> it1) {
		ArrayList<T> r = new ArrayList<T>();
		for (T t0 : it0) {
			for (T t1 : it1) {
				if (t0 == null) {
					if (t1 == null) {
						if (!r.contains(t0)) {
							r.add(t0);
						}
					}
				} else {
					if (t0.equals(t1)) {
						if (!r.contains(t0)) {
							r.add(t0);
						}
					}
				}
			}
		}
		return r;
	}

	public static <T0, T1, TOut> ArrayList<TOut> intersect(Class<TOut> outCls, Iterable<T0> it0, Iterable<T1> it1, FuncT<T0, TOut> mapper0, FuncT<T1, TOut> mapper1) {
		ArrayList<TOut> r = new ArrayList<TOut>();
		for (T0 t0 : it0) {
			TOut t0out = mapper0.get(t0);
			for (T1 t1 : it1) {
				TOut t1out = mapper1.get(t1);
				if (t0out == null) {
					if (t1out == null) {
						if (!r.contains(t0out)) {
							r.add(null);
						}
					}
				} else {
					if (t0out.equals(t1out)) {
						if (!r.contains(t0out)) {
							r.add(t0out);
						}
					}
				}
			}
		}
		return r;
	}

	public static <T> boolean existsIn(T t, T... ts) {
		for (T t2 : ts) {
			if (t.equals(t2)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean contains(Iterable<T> source, T target) {
		for (T t : source) {
			if (t == null) {
				if (target == null) {
					return true;
				}
			} else {
				if (t.equals(target)) {
					return true;
				}
			}
		}
		return false;
	}

	public static <T> boolean any(Pre<T> pre, T t, T... ts) {
		if (pre.check(t)) {
			return true;
		}
		return any(toList(ts), pre);
	}

	public static <T> boolean any(T[] ts, Pre<T> pre) {
		return any(toList(ts), pre);
	}

	public static <T> boolean any(Iterable<T> source, Pre<T> pre) {
		for (T i : source) {
			if (pre.check(i)) {
				return true;
			}
		}
		return false;
	}

	public static <T> ArrayList<T> sort(ArrayList<T> source, FuncT<T, Integer> mapper) {
		int[] indexes = sortIndexes(source, mapper);
		ArrayList<T> r = new ArrayList<T>();
		for (int i : indexes) {
			r.add(source.get(i));
		}
		return r;
	}

	public static <T> int[] sortIndexes(Iterable<T> source, FuncT<T, Integer> mapper) {
		ArrayList<Integer> ints = new ArrayList<Integer>();
		Iterator<T> it = source.iterator();
		while (it.hasNext()) {
			Integer integer = mapper.get(it.next());
			ints.add(integer);
		}
		int[] r = new int[ints.size()];
		for (int i = 0; i < r.length; i++) {
			r[i] = ints.get(i);
		}
		return sortIndexes(r);
	}

	public static <T> int[] sortIndexes(T[] source, FuncT<T, Integer> mapper) {
		int[] ints = new int[source.length];
		for (int i = 0; i < source.length; i++) {
			ints[i] = mapper.get(source[i]);
		}
		return sortIndexes(ints);
	}

	public static int[] sortIndexes(int[] source) {
		ArrayList<Integer> decre = new ArrayList<Integer>();
		for (int i : source) {
			decre.add(i);
		}
		ArrayList<Integer> incre = new ArrayList<Integer>();
		while (incre.size() < source.length) {
			int s = 0;
			int v = decre.get(0);
			for (int i = 1; i < decre.size(); i++) {
				int e = decre.get(i);
				if (e < v) {
					s = i;
					v = e;
				}
			}
			decre.remove(s);
			for (int i = 0; i < source.length; i++) {
				if (source[i] != v || incre.contains(i)) {
					continue;
				}
				incre.add(i);
				break;
			}
		}
		int[] r = new int[incre.size()];
		for (int i = 0; i < incre.size(); i++) {
			r[i] = incre.get(i);
		}
		return r;
	}

	public static <TIn, TOut> ArrayList<TOut> select(TIn[] source, FuncT<TIn, TOut> mapper) {
		return select(toList(source), mapper);
	}

	public static <TIn, TOut> ArrayList<TOut> select(Iterable<TIn> source, FuncT<TIn, TOut> mapper) {
		ArrayList<TOut> r = new ArrayList<TOut>();
		for (TIn ti : source) {
			r.add(mapper.get(ti));
		}
		return r;
	}
}