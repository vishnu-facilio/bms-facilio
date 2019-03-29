package com.facilio.bmsconsole.util;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class FreeMarkerAPI {
	
	private static final Configuration initConfig() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
		return cfg;
	}
	private static final Configuration DEFAULT_CONFIG = initConfig(); 
	
	public static String processTemplate (String template, Map<String, Object> params) throws Exception {
		Template temp = new Template("Temp", new StringReader(template), DEFAULT_CONFIG);
		StringWriter output = new StringWriter();
		temp.process(params, output);
		return output.toString();
	}
}
