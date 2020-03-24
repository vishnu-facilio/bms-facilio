package com.facilio.bmsconsole.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerAPI {
	
	private static final Configuration initConfig() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
		return cfg;
	}
	private static final Configuration DEFAULT_CONFIG = initConfig(); 
	
	public static String processTemplate (String template, Map<String, Object> params) throws Exception {
		try(StringReader sr = new StringReader(template); StringWriter output = new StringWriter();) {
			Template temp = new Template("Temp", sr, DEFAULT_CONFIG);
			temp.process(params, output);
			return output.toString();
		}
	}
}
