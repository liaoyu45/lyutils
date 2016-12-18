package com.ly.linq;

public class Pair<TKey, TValue> {
	private TValue value;
	private TKey key;

	public TValue getValue() {
		return value;
	}

	public TKey getKey() {
		return key;
	}

	public Pair(TKey key, TValue value) {
		this.key = key;
		this.value = value;
	}
}
