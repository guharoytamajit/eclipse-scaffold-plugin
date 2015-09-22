package com.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.ui.dialogs.MainTypeSelectionDialog;

public class Template {
	
	public static String populateTemplate(String template){

		Map<String, String> map = MessageHolder.getMessages();
	Pattern p = Pattern.compile("\\{\\{([a-zA-Z0-9]+)\\}\\}");
	Matcher m = p.matcher(template);
	StringBuffer sb = new StringBuffer();
	while (m.find()) {
	 
	    m.appendReplacement(sb, map.get(m.group(1)));
	}
	m.appendTail(sb);
	return  sb.toString();
	}
	public static void main(String[] args) {
		MessageHolder.addMessage("abc", "m2");

		Map<String, String> map = MessageHolder.getMessages();
	
		Pattern p = Pattern.compile("\\{\\{([a-zA-Z0-9]+)\\}\\}");
		Matcher m = p.matcher("public static void {{abc}}(String[] args){}");
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
		 
		    m.appendReplacement(sb, map.get(m.group(1)));
		}
		m.appendTail(sb);
		System.out.println(sb.toString());
	}
}
