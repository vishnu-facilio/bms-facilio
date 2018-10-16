package com.facilio.screen.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.context.ScreenContext;
import com.facilio.screen.context.ScreenDashboardRelContext;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.wms.message.WmsRemoteScreenMessage;
import com.facilio.wms.util.WmsApi;

public class ScreenUtil {

	public static void addScreen(ScreenContext screen) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getScreenModule().getTableName())
				.fields(FieldFactory.getScreenModuleFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(screen);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		screen.setId((Long) props.get("id"));
		
		insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getScreenDashboardRelModule().getTableName())
				.fields(FieldFactory.getScreenDashboardRelModuleFields());
		
		
		for(ScreenDashboardRelContext screenDashboard :screen.getScreenDashboards()) {
			
			screenDashboard.setScreenId(screen.getId());
			props = FieldUtil.getAsProperties(screenDashboard);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
	}
	
	public static void updateScreen(ScreenContext screen) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getScreenModule().getTableName())
				.fields(FieldFactory.getScreenModuleFields())
				.andCustomWhere("ID = ?", screen.getId());

		Map<String, Object> props = FieldUtil.getAsProperties(screen);
		updateBuilder.update(props);
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getScreenDashboardRelModule().getTableName())
		.andCustomWhere("SCREEN_ID = ?", screen.getId());
		deleteRecordBuilder.delete();
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getScreenDashboardRelModule().getTableName())
				.fields(FieldFactory.getScreenDashboardRelModuleFields());
		
		for(ScreenDashboardRelContext screenDashboard :screen.getScreenDashboards()) {
			
			screenDashboard.setScreenId(screen.getId());
			props = FieldUtil.getAsProperties(screenDashboard);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
		
		List<RemoteScreenContext> remoteScreens = getAllRemoteScreen(screen.getId());
		if (remoteScreens != null && remoteScreens.size() > 0) {
			for (RemoteScreenContext remoteScreen : remoteScreens) {
				WmsApi.sendRemoteMessage(remoteScreen.getId(), new WmsRemoteScreenMessage().setAction(WmsRemoteScreenMessage.RemoteScreenAction.REFRESH));
			}
		}
	}
	
	public static void deleteScreen(ScreenContext screen) throws Exception {
		
		List<RemoteScreenContext> remoteScreens = getAllRemoteScreen(screen.getId());
		if (remoteScreens != null && remoteScreens.size() > 0) {
			for (RemoteScreenContext remoteScreen : remoteScreens) {
				WmsApi.sendRemoteMessage(remoteScreen.getId(), new WmsRemoteScreenMessage().setAction(WmsRemoteScreenMessage.RemoteScreenAction.REFRESH));
			}
		}
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getScreenDashboardRelModule().getTableName())
		.andCustomWhere("SCREEN_ID = ?", screen.getId());
		deleteRecordBuilder.delete();
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getScreenModule().getTableName())
		.andCustomWhere("ID = ?", screen.getId());
		deleteRecordBuilder.delete();
		
	}
	
	public static List<ScreenContext> getAllScreen() throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getScreenModule().getTableName());
		builder.select(FieldFactory.getScreenModuleFields());
		builder.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getScreenModule()));
		
		List<Map<String, Object>> props = builder.get();
		
		List<ScreenContext> screens = new ArrayList<>();
		for(Map<String, Object> prop :props) {
			
			ScreenContext screenContext = FieldUtil.getAsBeanFromMap(prop, ScreenContext.class);
			getScreenDashboardRel(screenContext);
			screenContext.setRemoteScreens(getAllRemoteScreen(screenContext.getId()));
			
			screens.add(screenContext);
		}
		return screens;
	}
	
	public static ScreenContext getScreen(Long screenId) throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getScreenModule().getTableName());
		builder.select(FieldFactory.getScreenModuleFields());
		builder.andCondition(CriteriaAPI.getIdCondition(screenId+"", ModuleFactory.getScreenModule()));
		
		List<Map<String, Object>> props = builder.get();
		
		for(Map<String, Object> prop :props) {
			
			ScreenContext screenContext = FieldUtil.getAsBeanFromMap(prop, ScreenContext.class);
			getScreenDashboardRel(screenContext);
			
			return screenContext;
		}
		return null;
	}
	
	public static List<ScreenDashboardRelContext> getScreenDashboardRel(ScreenContext screenContext) throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getScreenDashboardRelModule().getTableName());
		builder.select(FieldFactory.getScreenDashboardRelModuleFields());
		builder.andCustomWhere("SCREEN_ID = ?", screenContext.getId());
		builder.orderBy("SEQUENCE_NO");
		
		List<Map<String, Object>> props = builder.get();
		
		List<ScreenDashboardRelContext> screenDashboardRels = new ArrayList<>();
		for(Map<String, Object> prop :props) {
			
			ScreenDashboardRelContext screenDashboardContext = FieldUtil.getAsBeanFromMap(prop, ScreenDashboardRelContext.class);
			
			DashboardContext db = DashboardUtil.getDashboardWithWidgets(screenDashboardContext.getDashboardId());
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", db.getOrgId());
			db.setModuleName(modBean.getModule(db.getModuleId()).getName());
			
			if (db.getLinkName().indexOf("buildingdashboard") != -1 && screenDashboardContext.getSpaceId() != null && screenDashboardContext.getSpaceId() > 0) {
				db.setDashboardName(SpaceAPI.getBaseSpace(screenDashboardContext.getSpaceId()).getName());
			}
			else if (db.getLinkName().indexOf("sitedashboard") != -1 && screenDashboardContext.getSpaceId() != null && screenDashboardContext.getSpaceId() > 0) {
				db.setDashboardName(SpaceAPI.getBaseSpace(screenDashboardContext.getSpaceId()).getName());
			}
			
			screenDashboardContext.setDashboard(db);
			screenContext.addScreenDashboard(screenDashboardContext);
			screenDashboardRels.add(screenDashboardContext);
		}
		return screenDashboardRels;
	}
	
	
	public static List<RemoteScreenContext> getAllRemoteScreen(long screenId) throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getRemoteScreenModule().getTableName());
		builder.select(FieldFactory.getRemoteScreenModuleFields());
		builder.andCustomWhere("SCREEN_ID = ?", screenId);
		
		List<Map<String, Object>> props = builder.get();
		
		List<RemoteScreenContext> remoteScreens = new ArrayList<>();
		for(Map<String, Object> prop :props) {
			
			RemoteScreenContext remoteSreenContext = FieldUtil.getAsBeanFromMap(prop, RemoteScreenContext.class);
			
			if (remoteSreenContext.getScreenId() != null && remoteSreenContext.getScreenId() > 0) {
				remoteSreenContext.setScreenContext(getScreen(remoteSreenContext.getScreenId()));
			}
			
			remoteScreens.add(remoteSreenContext);
		}
		return remoteScreens;
	}
	
	public static List<RemoteScreenContext> getAllRemoteScreen() throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getRemoteScreenModule().getTableName());
		builder.select(FieldFactory.getRemoteScreenModuleFields());
		builder.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getRemoteScreenModule()));
		
		List<Map<String, Object>> props = builder.get();
		
		List<RemoteScreenContext> remoteScreens = new ArrayList<>();
		for(Map<String, Object> prop :props) {
			
			RemoteScreenContext remoteSreenContext = FieldUtil.getAsBeanFromMap(prop, RemoteScreenContext.class);
			
			if (remoteSreenContext.getScreenId() != null && remoteSreenContext.getScreenId() > 0) {
				remoteSreenContext.setScreenContext(getScreen(remoteSreenContext.getScreenId()));
			}
			
			remoteScreens.add(remoteSreenContext);
		}
		return remoteScreens;
	}
	
	public static RemoteScreenContext getRemoteScreen(Long remoteScreenId) throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getRemoteScreenModule().getTableName());
		builder.select(FieldFactory.getRemoteScreenModuleFields());
		builder.andCondition(CriteriaAPI.getIdCondition(remoteScreenId+"", ModuleFactory.getRemoteScreenModule()));
		
		List<Map<String, Object>> props = builder.get();
		
		for(Map<String, Object> prop :props) {
			
			RemoteScreenContext remoteScreenContext = FieldUtil.getAsBeanFromMap(prop, RemoteScreenContext.class);
			return remoteScreenContext;
		}
		return null;
	}
	
	public static void addRemoteScreen(RemoteScreenContext remoteScreenContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getRemoteScreenModule().getTableName())
				.fields(FieldFactory.getRemoteScreenModuleFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(remoteScreenContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		remoteScreenContext.setId((Long) props.get("id"));
	}
	
	public static void updateRemoteScreen(RemoteScreenContext remoteScreenContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getRemoteScreenModule().getTableName())
				.fields(FieldFactory.getRemoteScreenModuleFields())
				.andCustomWhere("ID = ?", remoteScreenContext.getId());

		Map<String, Object> props = FieldUtil.getAsProperties(remoteScreenContext);
		updateBuilder.update(props);
		
		WmsApi.sendRemoteMessage(remoteScreenContext.getId(), new WmsRemoteScreenMessage().setAction(WmsRemoteScreenMessage.RemoteScreenAction.REFRESH));
	}
	
	public static void deleteRemoteScreen(RemoteScreenContext remoteScreenContext) throws Exception {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getRemoteScreenModule().getTableName())
		.andCustomWhere("ID = ?", remoteScreenContext.getId());
		deleteRecordBuilder.delete();
		
		WmsApi.sendRemoteMessage(remoteScreenContext.getId(), new WmsRemoteScreenMessage().setAction(WmsRemoteScreenMessage.RemoteScreenAction.REFRESH));
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
	
	public static String generateTVPasscode(JSONObject additionalInfo) throws Exception {
		
		String code = getRandomPasscode(6);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTVPasscodeModule().getTableName())
				.fields(FieldFactory.getTVPasscodeFields());
		
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
	
	public static boolean updateConnectedScreen(String code, long remoteScreenId) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getTVPasscodeModule().getTableName())
				.fields(FieldFactory.getTVPasscodeFields())
				.andCustomWhere("CODE = ?", code);

		Map<String, Object> props = new HashMap<>();
		props.put("connectedScreenId", remoteScreenId);
		updateBuilder.update(props);
		
		return true;
	}
	
	public static Map<String, Object> getTVPasscode(String code) throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getTVPasscodeModule().getTableName());
		builder.select(FieldFactory.getTVPasscodeFields());
		builder.andCustomWhere("CODE = ?", code);
		
		List<Map<String, Object>> props = builder.get();
		
		if (props != null && props.size() > 0) {
			return props.get(0);
		}
		return null;		
	}
	
	public static boolean validateTVPasscode(String code) throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getTVPasscodeModule().getTableName());
		builder.select(FieldFactory.getTVPasscodeFields());
		builder.andCustomWhere("CODE = ? AND EXPIRY_TIME > ? AND CONNECTED_SCREEN_ID IS NULL", code, System.currentTimeMillis());
		
		List<Map<String, Object>> props = builder.get();
		
		if (props != null && props.size() > 0) {
			return props.get(0).containsKey("code");
		}
		return false;
	}
	
	public static void deleteTVPasscode(String code) throws Exception {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getTVPasscodeModule().getTableName())
		.andCustomWhere("CODE = ?", code);
		deleteRecordBuilder.delete();
	}
}
