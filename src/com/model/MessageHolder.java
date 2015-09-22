package com.model;

import java.util.HashMap;
import java.util.Map;

public class MessageHolder {
	private static final ThreadLocal<Map<String, String>> contextHolder = new ThreadLocal<Map<String, String>>();

	public static final Map<String, String> getMessages() {
		Map<String, String> messages = contextHolder.get();
		if (messages == null) {
			messages = new HashMap<String, String>();
		}
		return messages;
	}

	public static final String getMessage(String key) {
		Map<String, String> messages = getMessages();

		if ( key != null) {
			return messages.get(key);
		}

		return null;
	}

	public static final void addMessage(String key,String value) {
		Map<String, String> map = getMessages();
		map.put(key, value);
		contextHolder.set(map);

	}
	
	public static final void removeMessage(String key) {
		getMessages().remove(key);

	}

	public static final void clearMessages() {
		contextHolder.set(new HashMap<String, String>());
	}

}
