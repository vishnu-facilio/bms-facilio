package com.facilio.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.DigestMailTemplateMapContext;

public class DigestTemplateFactory {

	private static final Map<Integer, DigestMailTemplateMapContext> DIGEST_TEMPLATE_MAP = Collections.unmodifiableMap(initMap());
	
	private static Map<Integer, DigestMailTemplateMapContext> initMap() {
		Map<Integer, DigestMailTemplateMapContext> digest_template_map = new HashMap<>();
		digest_template_map.put(84, new DigestMailTemplateMapContext(84, 3, 1, "Monthly maintenance digest", 84));
		digest_template_map.put(83, new DigestMailTemplateMapContext(83, 2, 1, "Weekly maintenance digest", 83));
		
		return digest_template_map;
	}
	
	public static DigestMailTemplateMapContext getDigestTemplate(Integer defaultTemplateId) {
		return DIGEST_TEMPLATE_MAP.get(defaultTemplateId);
	}
	
	public static List<DigestMailTemplateMapContext> getAllDigestTemplates() {
		 List<DigestMailTemplateMapContext> list = new ArrayList<DigestMailTemplateMapContext>();
		 list.addAll(DIGEST_TEMPLATE_MAP.values());
		 return list;
	}
	
}
