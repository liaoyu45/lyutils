package com.ly.utils;


public class Text {

	public static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			String h = Integer.toHexString(b & 0xff);
			if (h.length() == 1) {
				sb.append('0' + h);
			} else {
				sb.append(h);
			}
		}
		return sb.toString();
	}
}
