package com.bupt.memes.util;

import java.util.Set;

public class NewsUtil {

	public static Set<String> convertTag(String tag) {
		String[] split = tag.split(",");
		return Set.of(split);
	}
}
