package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

public class FetchAlarmInsightCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long assetId = (long) context.get(ContextNames.ASSET_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.ALARM);
		FacilioModule ticketModule = modBean.getModule(ContextNames.TICKET);
		List<FacilioField> fields = modBean.getAllFields(ContextNames.ALARM);
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		List<FacilioField> selectFields = new ArrayList<>();
		FacilioField entityField = fieldMap.get("entityId");
		FacilioField subjectField = fieldMap.get("subject");
		FacilioField clearedTimeField = fieldMap.get("clearedTime");
		FacilioField durationField = FieldFactory.getField("duration", "SUM(" + clearedTimeField.getColumnName() + "-" + fieldMap.get("createdTime").getColumnName() + ")", FieldType.NUMBER);
		selectFields.add(entityField);
		selectFields.add(subjectField);
		selectFields.add(durationField);
		selectFields.addAll(FieldFactory.getCountField(module));
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.innerJoin(ticketModule.getTableName())
				.on(module.getTableName()+".ID="+ticketModule.getTableName()+".ID")
				.select(selectFields)
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(assetId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(clearedTimeField, CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(SourceType.ANOMALY_ALARM.getIntVal()), NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), DateOperators.CURRENT_YEAR))
				.groupBy(module.getTableName() + ".ORGID, " + entityField.getCompleteColumnName()  + "," + subjectField.getCompleteColumnName())
				;
		
		List<Map<String, Object>> props = builder.get();
		context.put(ContextNames.ALARM_LIST, props);
		
		return false;
	}

}
