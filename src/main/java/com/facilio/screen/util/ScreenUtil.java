package com.facilio.screen.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.context.ScreenContext;
import com.facilio.screen.context.ScreenDashboardRelContext;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

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
		
	}
	
	public static void deleteScreen(ScreenContext screen) throws Exception {
		
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
			
			ScreenContext screenContext = (ScreenContext) FieldUtil.getAsBeanFromMap(prop, ScreenContext.class);
			getScreenDashboardRel(screenContext);
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
			
			ScreenContext screenContext = (ScreenContext) FieldUtil.getAsBeanFromMap(prop, ScreenContext.class);
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
		
		List<Map<String, Object>> props = builder.get();
		
		List<ScreenDashboardRelContext> screenDashboardRels = new ArrayList<>();
		for(Map<String, Object> prop :props) {
			
			ScreenDashboardRelContext screenDashboardContext = (ScreenDashboardRelContext) FieldUtil.getAsBeanFromMap(prop, ScreenDashboardRelContext.class);
			screenDashboardContext.setDashboard(DashboardUtil.getDashboardWithWidgets(screenDashboardContext.getDashboardId()));
			screenContext.addScreenDashboard(screenDashboardContext);
			screenDashboardRels.add(screenDashboardContext);
		}
		return screenDashboardRels;
	}
	
	
	public static List<RemoteScreenContext> getAllRemoteScreen() throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getRemoteScreenModule().getTableName());
		builder.select(FieldFactory.getRemoteScreenModuleFields());
		builder.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getRemoteScreenModule()));
		
		List<Map<String, Object>> props = builder.get();
		
		List<RemoteScreenContext> remoteScreens = new ArrayList<>();
		for(Map<String, Object> prop :props) {
			
			RemoteScreenContext remoteSreenContext = (RemoteScreenContext) FieldUtil.getAsBeanFromMap(prop, RemoteScreenContext.class);
			
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
			
			RemoteScreenContext remoteScreenContext = (RemoteScreenContext) FieldUtil.getAsBeanFromMap(prop, RemoteScreenContext.class);
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
		
	}
	
	public static void deleteRemoteScreen(RemoteScreenContext remoteScreenContext) throws Exception {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getRemoteScreenModule().getTableName())
		.andCustomWhere("ID = ?", remoteScreenContext.getId());
		deleteRecordBuilder.delete();
	}
}
