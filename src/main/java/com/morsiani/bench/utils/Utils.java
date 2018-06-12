package com.morsiani.bench.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Random;
import java.util.Date;

import java.sql.Timestamp;

public class Utils {

	public static Timestamp getCurrentTimeStamp() {

		Date now = new Date();
		return new java.sql.Timestamp(now.getTime());
	}

	public static String getRandomString(int size) {

		char[] chars = "QWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	public static int getRandomInt(int maxInt) {
		
		Random r = new Random();
		return r.nextInt(maxInt);
	}

	public static BigDecimal getRandomBigDecimal(int maxValue, int scale) {
		Random r = new Random();
		double d = (double) r.nextInt(maxValue) + r.nextDouble();
		return new BigDecimal(d).setScale(scale, RoundingMode.FLOOR);

	}

}
