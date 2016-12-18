package com.ly.linq;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.ly.linq.FuncT;

public class Function {
	public static <TIn, TOut> FuncT<TIn, TOut> getField(final Class<TIn> clsIn, Class<TOut> clsOut, final String name) {
		return new FuncT<TIn, TOut>() {
			@SuppressWarnings("unchecked")
			@Override
			public TOut get(TIn ti) {
				try {
					Field field = clsIn.getDeclaredField(name);
					field.setAccessible(true);
					return (TOut) field.get(ti);
				} catch (Exception e) {
					return null;
				}
			}
		};
	}

	public static <TIn, TOut> FuncT<TIn, TOut> invoke(final Class<TIn> clsIn, final Class<TOut> clsOut, final String name) {
		return invoke(clsIn, clsOut, name, null);
	}

	public static <TIn, TOut> FuncT<TIn, TOut> invoke(final Class<TIn> clsIn, final Class<TOut> clsOut, final String name, final FuncT<TIn, Object[]> ps) {
		return new FuncT<TIn, TOut>() {
			@SuppressWarnings("unchecked")
			@Override
			public TOut get(TIn ti) {
				try {
					Method method = clsIn.getDeclaredMethod(name);
					method.setAccessible(true);
					Object[] objects = ps == null ? new Object[0] : ps.get(ti);
					return (TOut) method.invoke(ti, objects);
				} catch (Exception e) {
					return null;
				}
			}
		};
	}
}
