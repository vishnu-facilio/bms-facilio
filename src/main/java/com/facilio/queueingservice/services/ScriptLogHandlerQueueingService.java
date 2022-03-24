package com.facilio.queueingservice.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.impl.OrgBeanCacheImpl;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.license.LicenseContext.FacilioLicense;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.queueingservice.FacilioQueueingServiceAnnotation;
import com.facilio.queueingservice.QueueingServiceBean;
import com.facilio.queueingservice.QueueingServiceBeanImpl;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.v3.util.V3Util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@FacilioQueueingServiceAnnotation (
	consumerGroup = ScriptLogHandlerQueueingService.CONSUMER_GROUP, 
	topic = ScriptLogHandlerQueueingService.TOPIC
)
@Getter
@Setter
public class ScriptLogHandlerQueueingService extends QueueingService {
    private static final Logger LOGGER = LogManager.getLogger(OrgBeanCacheImpl.class.getName());
	public static final String TOPIC = "script-log";
	public static final String CONSUMER_GROUP = "script-log-consumer";
	
	@Override
	public void processRecords(List<FacilioRecord> records){
		Map<Long,List<Map<String,Object>>> orgVsRecordsMap=new HashMap<>();
			
			for (FacilioRecord record : records) {
				Map<String, Object> value = record.getData();
				Long orgId = (Long) value.get("orgId");
				if(orgId!=null) {
					if (!orgVsRecordsMap.containsKey(orgId)) {
						List<Map<String,Object>> OrgRecords = new ArrayList();
						OrgRecords.add(value);
						orgVsRecordsMap.put(orgId, OrgRecords);
					} else {
						orgVsRecordsMap.get(orgId).add(value);
					}
				}
			}
			for (Map.Entry<Long,List<Map<String,Object>>> value : orgVsRecordsMap.entrySet()) {        
				QueueingServiceBean bean;
				try {
					bean = (QueueingServiceBean) BeanFactory.lookup("QueueingServiceBean", value.getKey());
					bean.addScriptLogs(value.getValue());
				} catch (Exception e) {
					LOGGER.info(e);
				}
			}
	}

}
