package com.facilio.i18n.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.facilio.accounts.util.AccountUtil;

import lombok.extern.log4j.Log4j;

@Log4j
public class TranslationUtil {

	private static final String BASE_PATH = "conf/translations/messages";
	private static final String DEFAULT_LANG = "en";
	
	private static final Map<String, ResourceBundle> PROPERTIES = new HashMap<>();
	
	public static ResourceBundle getBundle() {
		String lang = AccountUtil.getCurrentOrg().getLanguage();
		if (lang == null) {
			lang = DEFAULT_LANG;
		}
		if (!PROPERTIES.containsKey(lang)) {
			Locale locale = new Locale(lang); // Add country code also if needed later
			ResourceBundle bundle = ResourceBundle.getBundle(BASE_PATH, locale);
			PROPERTIES.put(lang, bundle);
		}
		return PROPERTIES.get(lang);
	}

	public static String getString(String key, String... args) {
		ResourceBundle bundle = getBundle();
		String val = bundle.getString(key);
		if (args.length > 0) {
			val = MessageFormat.format(val, args);
		}
		return val;
	}
}
