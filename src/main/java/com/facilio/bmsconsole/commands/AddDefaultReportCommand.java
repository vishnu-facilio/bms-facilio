package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.SQLScriptRunner;
import org.apache.log4j.LogManager;

public class AddDefaultReportCommand implements Command {

	private static org.apache.log4j.Logger log = LogManager.getLogger(AddDefaultReportCommand.class.getName());

	private static final File INSERT_REPORTS_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/defaultReports.sql").getFile());
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		long orgId = (long) context.get("orgId");
		
		Map<String, String> paramValues = new HashMap<>(); 
		paramValues.put("orgId", String.valueOf(orgId));
		
		try {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean",orgId);
			FacilioModule module = modBean.getModule(ContextNames.WORK_ORDER);
			
			paramValues.put("workOrderModuleId", String.valueOf(module.getModuleId()));
			
			ArrayList<FacilioField> fields = modBean.getAllFields(module.getName());
			if(fields != null) {
				for(FacilioField field:fields) {
					paramValues.put(module.getName()+"_"+field.getName(), String.valueOf(field.getId()));
				}
			}
			
			module = modBean.getModule(ContextNames.ALARM);
			
			paramValues.put("alarmModuleId", String.valueOf(module.getModuleId()));
			
			fields = modBean.getAllFields(module.getName());
			if(fields != null) {
				for(FacilioField field:fields) {
					paramValues.put(module.getName()+"_"+field.getName(), String.valueOf(field.getId()));
				}
			}
			
			module = modBean.getModule(ContextNames.ENERGY_DATA_READING);
			paramValues.put("energyDataModuleId", String.valueOf(module.getModuleId()));
			
			fields = modBean.getAllFields(module.getName());
			if(fields != null) {
				for(FacilioField field:fields) {
					paramValues.put(module.getName()+"_"+field.getName(), String.valueOf(field.getId()));
				}
			}
			
			module = modBean.getModule(ContextNames.ENERGY_METER);
			paramValues.put("energyMeterModuleId", String.valueOf(module.getModuleId()));
			
			fields = modBean.getAllFields(module.getName());
			if(fields != null) {
				for(FacilioField field:fields) {
					paramValues.put(module.getName()+"_"+field.getName(), String.valueOf(field.getId()));
				}
			}
			
			List<BaseLineContext> baselines = BaseLineAPI.getAllBaseLines();
			
			for(BaseLineContext baseline : baselines) {
				
				String name = baseline.getName();
				name = name.replaceAll("\\s","");
				paramValues.put("baseline_"+name, String.valueOf(baseline.getId()));
			}
			
			System.out.println(paramValues);
			SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_REPORTS_SQL, true, paramValues);
			scriptRunner.runScript();
			
//			CommonCommandUtil.insertOrgInfo(orgId, "newdashboard", "wo,fa");
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return false;
	}

}
