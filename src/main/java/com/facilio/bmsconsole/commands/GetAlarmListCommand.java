package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class GetAlarmListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
														.connection(conn)
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(AlarmContext.class)
														.select(fields)
														.orderBy("ID");

		if(view != null) {
			Criteria criteria = view.getCriteria();
			builder.andCriteria(criteria);
		}
		
		List<Condition> conditionList = (List<Condition>) context.get(FacilioConstants.ContextNames.FILTER_CONDITIONS);
		if(conditionList != null && !conditionList.isEmpty()) {
			for(Condition condition : conditionList) {
				builder.andCondition(condition);
			}
		}
		
		List<AlarmContext> alarms = builder.get();
		context.put(FacilioConstants.ContextNames.ALARM_LIST, alarms);
		
		return false;
	}

}
