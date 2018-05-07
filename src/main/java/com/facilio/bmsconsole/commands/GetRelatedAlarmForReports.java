package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetRelatedAlarmForReports implements Command{

	private static final Logger LOGGER = Logger.getLogger(GetRelatedAlarmForReports.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		
		String energyMeterValue = null; 
		
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		String dateFilter = (String) context.get(FacilioConstants.ContextNames.REPORT_DATE_FILTER);
		
		if (reportContext.getCriteria() != null) {
			Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getCriteria().getCriteriaId());
			if(module.getName().equals("energydata") && criteria != null) {
				Map<Integer, Condition> conditions = criteria.getConditions();
				for(Condition condition:conditions.values()) {
					if(condition.getColumnName().equals("Energy_Data.PARENT_METER_ID")) {
						energyMeterValue = energyMeterValue + condition.getValue() +",";
					}
				}
			}
		}
		ReportFieldContext reportXAxisField = reportContext.getxAxisField();
		FacilioField xAxisField = reportXAxisField.getField();
		
		boolean isEnergyDataWithTimeFrame = false;
		if(xAxisField.getColumnName().contains("TTIME") && module.getName().equals("energydata")) {
			isEnergyDataWithTimeFrame = true;
		}
		
		if(energyMeterValue != null && !"".equalsIgnoreCase(energyMeterValue.trim()) && isEnergyDataWithTimeFrame && !reportContext.getIsComparisionReport()) {
			
			List<FacilioField> alarmVsEnergyFields = new ArrayList<>();
			
			FacilioField subject = new FacilioField();
			subject.setName("subject");
			subject.setDataType(FieldType.STRING);
			subject.setColumnName("SUBJECT");
			subject.setModule(ModuleFactory.getTicketsModule());
			alarmVsEnergyFields.add(subject);
			
			FacilioField modTime = new FacilioField();
			modTime.setName("modifiedTime");
			modTime.setDataType(FieldType.NUMBER);
			modTime.setColumnName("MODIFIED_TIME");
			modTime.setModule(ModuleFactory.getAlarmsModule());
			alarmVsEnergyFields.add(modTime);
			
			FacilioField severity = new FacilioField();
			severity.setName("severity");
			severity.setDataType(FieldType.NUMBER);
			severity.setColumnName("SEVERITY");
			severity.setModule(ModuleFactory.getAlarmsModule());
			alarmVsEnergyFields.add(severity);
			
			FacilioField serialNumber = new FacilioField();
			serialNumber.setName("serialNumber");
			serialNumber.setDataType(FieldType.NUMBER);
			serialNumber.setColumnName("SERIAL_NUMBER");
			serialNumber.setModule(ModuleFactory.getTicketsModule());
			alarmVsEnergyFields.add(serialNumber);
			
			FacilioField alarmId = new FacilioField();
			alarmId.setName("alarmid");
			alarmId.setDataType(FieldType.NUMBER);
			alarmId.setColumnName("ALARM_ID");
			alarmId.setModule(ModuleFactory.getAlarmVsEnergyData());
			alarmVsEnergyFields.add(alarmId);
			
			if (energyMeterValue.endsWith(",")) {
				energyMeterValue = energyMeterValue.substring(0, energyMeterValue.length()-1);
			}
			
			GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getAlarmVsEnergyData().getTableName())
					.innerJoin(ModuleFactory.getAlarmsModule().getTableName())
					.on(ModuleFactory.getAlarmVsEnergyData().getTableName()+".ALARM_ID="+ModuleFactory.getAlarmsModule().getTableName()+".ID")
					.innerJoin(ModuleFactory.getTicketsModule().getTableName())
					.on(ModuleFactory.getTicketsModule().getTableName()+".ID="+ModuleFactory.getAlarmsModule().getTableName()+".ID")
					.andCustomWhere(ModuleFactory.getTicketsModule().getTableName()+".RESOURCE_ID in ("+energyMeterValue+")")
					.andCustomWhere(ModuleFactory.getAlarmsModule().getTableName()+".ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.select(alarmVsEnergyFields);
			
			Condition dateCondition = null;
			if (reportContext.getDateFilter() != null) {
				dateCondition = new Condition();
				dateCondition.setField(reportContext.getDateFilter().getField());
				
				if (dateFilter != null) {
					if (dateFilter.split(",").length > 1) {
						dateCondition.setOperator(DateOperators.BETWEEN);
						dateCondition.setValue(dateFilter);
					}
					else {
						dateCondition.setOperatorId(Integer.parseInt(dateFilter));
					}
				}
			}
			
			if (dateCondition != null) {
				dateCondition.setField(modTime);
				builder1.andCondition(dateCondition);
			}
			
			List<Map<String, Object>> alarmVsEnergyProps = builder1.get();
			
			LOGGER.log(Level.INFO, "builder1 query -- "+builder1);
			LOGGER.log(Level.INFO, "alarmVsEnergyProps -- "+alarmVsEnergyProps);
//			Map<Object ,JSONArray> alarmProps=  getAlarmProps(alarmVsEnergyProps);
//			JSONArray relatedAlarms = getAlarmReturnFormat(alarmProps);
//			setRelatedAlarms(relatedAlarms);
		}
		return false;
	}

}
