package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.field.expression.FieldExpression;
import com.cronutils.model.field.expression.FieldExpressionFactory;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.CronUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class AddPreventiveMaintenanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			pm.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			pm.setTemplateId(templateId);
			
			Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
			
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
																.connection(conn)
																.table("Preventive_Maintenance")
																.fields(FieldFactory.getPreventiveMaintenanceFields())
																.addRecord(pmProps);
			
			builder.save();
			
			List<FieldExpression> expressions = new ArrayList<>();		//TODO
			expressions.add(FieldExpressionFactory.on(7));
			expressions.add(FieldExpressionFactory.on(8));
			Cron cron = CronBuilder.cron(CronUtil.DEFAULT_CRON_DEFN)
					.withYear(FieldExpressionFactory.always())
					.withMinute(FieldExpressionFactory.every(2))
					.withHour(FieldExpressionFactory.on(15))
					.withDoM(FieldExpressionFactory.and(expressions))
					.withMonth(FieldExpressionFactory.always())
					.withDoW(FieldExpressionFactory.always())
					.instance();
			FacilioTimer.scheduleCalendarJob(1, "test", 30, cron, "priority", 1507638900l);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return false;
	}
	
}
