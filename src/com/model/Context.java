package com.model;

import java.util.HashMap;
import java.util.Map;

public class Context {
	private static final ThreadLocal<Map<String, Object>> contextHolder = new ThreadLocal<Map<String, Object>>();

	public static final Map<String, Object> getAttributes() {
		Map<String, Object> messages = contextHolder.get();
		if (messages == null) {
			messages = new HashMap<String, Object>();
		}
		return messages;
	}

	public static final Object getAttribute(String key) {
		Map<String, Object> messages = getAttributes();

		if ( key != null) {
			return messages.get(key);
		}

		return null;
	}

	public static final void addAttribute(String key,Object value) {
		Map<String, Object> map = getAttributes();
		map.put(key, value);
		contextHolder.set(map);

	}
	
	public static final void removeAttribute(String key) {
		getAttributes().remove(key);

	}

	public static final void clearAttributes() {
		contextHolder.set(new HashMap<String, Object>());
	}

}
