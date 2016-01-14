package com.model;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {
	
	public static String populateTemplate(String template){

		Map<String, Object> map = MethodContext.getAttributes();
	Pattern p = Pattern.compile("\\{\\{([a-zA-Z0-9]+)\\}\\}");
	Matcher m = p.matcher(template);
	StringBuffer sb = new StringBuffer();
	while (m.find()) {
	 
	    m.appendReplacement(sb, (String)map.get(m.group(1)));
	}
	m.appendTail(sb);
	return  sb.toString();
	}
	public static void main(String[] args) {
		Context.addAttribute("abc", "m2");

		Map<String, Object> map = Context.getAttributes();
	
		Pattern p = Pattern.compile("\\{\\{([a-zA-Z0-9]+)\\}\\}");
		Matcher m = p.matcher("public static void {{abc}}(String[] args){}");
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
		 
		    m.appendReplacement(sb, (String)map.get(m.group(1)));
		}
		m.appendTail(sb);
		System.out.println(sb.toString());
	}
}
