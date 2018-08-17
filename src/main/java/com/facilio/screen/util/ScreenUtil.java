package com.facilio.screen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.screen.context.ScreenContext;
import com.facilio.screen.context.ScreenDashboardRelContext;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ScreenUtil {

	public void addScreen(ScreenContext screen) throws Exception {
		
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
	
	public void updateScreen(ScreenContext screen) throws Exception {
		
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
	
	public List<ScreenContext> getAllScreen() throws Exception {
		
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
	
	public ScreenContext getScreen(Long screenId) throws Exception {
		
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
	
	public List<ScreenDashboardRelContext> getScreenDashboardRel(ScreenContext screenContext) throws Exception {
		
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
	
	
	
	
	
	
	
	
	
}
