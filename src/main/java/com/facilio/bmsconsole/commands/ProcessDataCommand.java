package com.facilio.bmsconsole.commands;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;


public class ProcessDataCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(ProcessDataCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		JSONObject payLoad =(JSONObject)context.get(FacilioConstants.ContextNames.PAY_LOAD);
		if (AccountUtil.getCurrentOrg().getId() == 146 ) {
			LOGGER.info("Payload : "+payLoad);
		}
		Iterator<String> keyList = payLoad.keySet().iterator();
		List<Map<String,Object>> pointsStat=null;
		Criteria criteriaList = new Criteria();
		
		Map<String, Map<String,String>> deviceData= new HashMap<String, Map<String,String>>();
		LOGGER.debug("Inside ProcessDataCommand####### incoming JSON: "+payLoad);
		while(keyList.hasNext())
		{
			String actualKey = keyList.next();

			Object recordObj= payLoad.get(actualKey);
			if(!(recordObj instanceof JSONObject)) {
				continue;
			}
			JSONObject record =(JSONObject)recordObj;
			String keyName=actualKey;

			if(actualKey.startsWith("DEVICE_") || actualKey.startsWith("POINT_")) {
				int firstIndex= actualKey.indexOf("_");
				keyName=actualKey.substring(firstIndex+1);
			}
			Iterator<String> innerKeyList = record.keySet().iterator();
			StringJoiner instanceList=new StringJoiner(",");
			while(innerKeyList.hasNext()) {
				String iKeyName=innerKeyList.next();
				Object instanceObj = record.get(iKeyName);
				if (instanceObj != null) {
					String instanceVal = instanceObj.toString();
					String deviceName=iKeyName;//incase of POINT_ inner keyName is deviceName
					String instanceName=keyName;//incase of POINT_ keyName is instanceName
	
					if(!actualKey.startsWith("POINT_")) {
						deviceName=keyName;//incase of DEVICE_ & others keyName is deviceName
						instanceName=iKeyName;//incase of DEVICE_ & others inner KeyName is instanceName
					}
					Map<String,String> data= deviceData.get(deviceName);
					if(data==null) {
						data= new HashMap<String,String> ();
						deviceData.put(deviceName, data);
					}
					data.put(instanceName,instanceVal);	
					instanceList.add(instanceName.replace(",", StringOperators.DELIMITED_COMMA));
				}
			}	
//		if(TimeSeriesAPI.isStage()) {
				
				if(instanceList.length()>0) { //if innerKeyList isEmpty,.. so the length will be 0
					FacilioModule module=ModuleFactory.getPointsModule();
					Criteria deviceAndInstanceCriteria = new Criteria();
					//here taking the keyName as deviceName in the assumption that POINT_ will not come hereafter...
					//so not handling the deviceName as list scenario below..
					deviceAndInstanceCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getDeviceField(module), keyName, StringOperators.IS));
					deviceAndInstanceCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getInstanceField(module), instanceList.toString(), StringOperators.IS));
					criteriaList.orCriteria(deviceAndInstanceCriteria);
				}	
//		}
		}
//	if(TimeSeriesAPI.isStage()) {
			
			pointsStat= getDataPoints(criteriaList);
			LOGGER.debug("########### Insert Points Data: "+pointsStat);
			context.put("DATA_POINTS",pointsStat );
//	}
		LOGGER.debug("Finished ProcessDataCommand####### : ");
		context.put(FacilioConstants.ContextNames.DEVICE_DATA, deviceData);
		if (TimeSeriesAPI.isStage() ) {
			LOGGER.debug("Device Data : "+deviceData);
		}
		return false;
	}

	private List<Map<String,Object>> getDataPoints(Criteria criteriaList) throws Exception{
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		fields.add(FieldFactory.getIdField(module));
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getPointsModule()))
				.andCriteria(criteriaList);

		List<Map<String, Object>> props = builder.get();
		LOGGER.debug("###### DataPoints Query ########"+builder.toString());
		return props;
	}
}
