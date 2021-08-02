package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.wms.constants.WmsEventType;
import com.facilio.wms.endpoints.LiveSession.LiveSessionType;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;

public class DevicesUtil {

public static String generateDevicePasscode(JSONObject additionalInfo) throws Exception {
		
		String code = getRandomPasscode(6);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getDevicePasscodesModule().getTableName())
				.fields(FieldFactory.getDevicePasscodesFields());
		
		long generatedTime = System.currentTimeMillis();
		long expiryTime = generatedTime + 300000;
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("code", code);
		props.put("generatedTime", generatedTime);
		props.put("expiryTime", expiryTime);
		if (additionalInfo != null) {
			props.put("info", additionalInfo.toJSONString());
		}
		
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		return code;
	}


public static Map<String, Object> getDevicePasscodeRow(String code) throws Exception {
	
	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	
	builder.table(ModuleFactory.getDevicePasscodesModule().getTableName()).
	select(FieldFactory.getDevicePasscodesFields()).
	andCondition(CriteriaAPI.getCondition("CODE","code",code,StringOperators.IS));
	
	
	List<Map<String, Object>> props = builder.get();
	
	if (props != null && props.size() > 0) {
		return props.get(0);
	}
	return null;		
}

public static void deleteDevicePasscode(String code) throws Exception {
	
	GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
	
	deleteRecordBuilder.table(ModuleFactory.getDevicePasscodesModule().getTableName()).
	andCondition(CriteriaAPI.getCondition("CODE","code",code,StringOperators.IS));
	deleteRecordBuilder.delete();
}
private static String getRandomPasscode(int noOfLetters) {
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < noOfLetters) { // length of the random string.
        int index = (int) (rnd.nextFloat() * SALTCHARS.length());
        salt.append(SALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();
    return saltStr;
}


public static Map<String, Object> getValidDeviceCodeRow(String code) throws Exception {
	
	
	
	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	builder.table(ModuleFactory.getDevicePasscodesModule().getTableName());
	builder.select(FieldFactory.getDevicePasscodesFields()).
	andCondition(CriteriaAPI.getCondition("CODE", "code", code, StringOperators.IS)).
	andCondition(CriteriaAPI.getCondition("EXPIRY_TIME","expiryTime", System.currentTimeMillis()+"", NumberOperators.GREATER_THAN)).
	andCondition(CriteriaAPI.getCondition("CONNECTED_DEVICE_ID","connectedDeviceId","", CommonOperators.IS_EMPTY));
	
	
	List<Map<String, Object>> props = builder.get();
	
	if (props != null && props.size() > 0) {
		return props.get(0);
	}
	
	return null;
}

public static boolean markCodeAsConnected(String code, long connectedDeviceId, int dc) throws Exception {
	//enter connected device ID in device codes table and create an entry in connected devices 
	
	GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
			.table(ModuleFactory.getDevicePasscodesModule().getTableName())
			.fields(FieldFactory.getDevicePasscodesFields())
			.andCondition(CriteriaAPI.getCondition("CODE", "code",code, StringOperators.IS));
	
			

	Map<String, Object> props = new HashMap<>();
	props.put("connectedDeviceId", connectedDeviceId);
	props.put("dc", dc);
	updateBuilder.update(props);
	
	return true;
}

public static long addConnectedDevice(long deviceID,long orgId) throws Exception
{
	GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
			.table(ModuleFactory.getConnectedDevicesModule().getTableName())
			.fields(FieldFactory.getConnectedDeviceFields());
	
	
	
	Map<String, Object> props = new HashMap<String, Object>();
	props.put("orgId", orgId);
	props.put("sessionStartTime", System.currentTimeMillis());
	props.put("deviceId", deviceID);

	
	return insertBuilder.insert(props);
	

}
public static int disconnectDevice(long deviceId, long orgId) throws Exception {
	
	GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
	
	deleteRecordBuilder.table(ModuleFactory.getConnectedDevicesModule().getTableName())
	.andCondition(CriteriaAPI.getCondition("DEVICE_ID", "deviceId",deviceId+"", NumberOperators.EQUALS))
	.andCondition(CriteriaAPI.getCondition("ORGID", "orgId",  orgId+"", NumberOperators.EQUALS));
	
	WmsApi.sendEventToDevice(deviceId, new WmsEvent().setEventType(WmsEventType.Device.DISCONNECT));
	
	return deleteRecordBuilder.delete();
}

public static void reloadConf(long orgId) throws Exception {
	
	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	
	builder.table(ModuleFactory.getConnectedDevicesModule().getTableName());
	builder.select(FieldFactory.getConnectedDeviceFields());
	builder.andCondition(CriteriaAPI.getOrgIdCondition(orgId, ModuleFactory.getConnectedDevicesModule()));
	
	List<Map<String, Object>> props = builder.get();
	
	
	if(props!=null && props.size()>0)
	{
		for (Map<String, Object> prop : props) {
			ConnectedDeviceContext connectedDeviceContext = FieldUtil.getAsBeanFromMap(prop, ConnectedDeviceContext.class);
			reloadConf(connectedDeviceContext.getDeviceId(), orgId);
		}
	}
}

public static void reloadConf(long deviceId, long orgId) throws Exception {
	
	WmsApi.sendEventToDevice(deviceId, new WmsEvent().setEventType(WmsEventType.Device.RELOAD_CONF));
}

public static ConnectedDeviceContext getConnectedDevice(Long connectedDeviceId) throws Exception {
	
	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	
	builder.table(ModuleFactory.getConnectedDevicesModule().getTableName());
	builder.select(FieldFactory.getConnectedDeviceFields());
	builder.andCondition(CriteriaAPI.getIdCondition(connectedDeviceId+"", ModuleFactory.getConnectedDevicesModule()));
	
	List<Map<String, Object>> props = builder.get();
	
	
	if(props!=null && props.size()>0)
	{
		ConnectedDeviceContext connectedDeviceContext = FieldUtil.getAsBeanFromMap(props.get(0), ConnectedDeviceContext.class);
		return connectedDeviceContext;
	}
	
	
	return null;
}




}
