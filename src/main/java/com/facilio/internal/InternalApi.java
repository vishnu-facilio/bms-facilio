package com.facilio.internal;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;

public class InternalApi {
	private static final Logger LOGGER = Logger.getLogger(InternalApi.class.getName());
	
	private static JSONObject mobileDetails;
	
	public static boolean checkMinClientVersion(String type, double version) throws Exception {
		if (mobileDetails == null) {
			mobileDetails = fetchMobileDetailsAsService();
		}
		if (mobileDetails != null && mobileDetails.containsKey(type)) {
			double minVersion =  (double) mobileDetails.get(type);
			return version >= minVersion;
		}
		return true;
	}
	
	public static JSONObject fetchMobileDetailsAsService() throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> fetchMobileDetails());
	}
	
	private static JSONObject fetchMobileDetails() throws Exception {
		FacilioModule module = ModuleFactory.getMobileDetailsModule();
		List<FacilioField> fields = FieldFactory.getMobileDetailFields();
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				;
		
		List<Map<String, Object>> details = builder.get();
		JSONObject obj = null;
		if (CollectionUtils.isNotEmpty(details)) {
			obj = new JSONObject();
			for(Map<String, Object> detail: details) {
				obj.put(detail.get("type"), detail.get("minVersion"));
			}
		}
		return obj;
	}

}
