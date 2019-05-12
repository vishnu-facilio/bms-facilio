package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetReportUnderlyingDataCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT_CONTEXT);
		if (reportContext.getReportChartType() == ReportContext.ReportChartType.TABULAR) {
			return false;
		}
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		JSONArray dateFilter = (JSONArray)context.get(FacilioConstants.ContextNames.DATE_FILTER);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.table(module.getTableName())
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
				.select(fields)
				.maxLevel(0);
		
		builder.select(fields);
		Criteria criteria = null;
		
		if (reportContext.getCriteria() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getCriteria().getCriteriaId());
			builder.andCriteria(criteria);
		}
		if (reportContext.getDateFilter() != null) {
			Condition dateCondition = DashboardUtil.getDateCondition(reportContext, dateFilter, module);
			builder.andCondition(dateCondition);
		}
		if (reportContext.getReportSpaceFilterContext() != null && reportContext.getReportSpaceFilterContext().getBuildingId() != null) {
			List<EnergyMeterContext> meters = DeviceAPI.getMainEnergyMeter(reportContext.getReportSpaceFilterContext().getBuildingId()+"");
			if (meters != null && meters.size() > 0) {
				List<Long> meterIds = new ArrayList<Long>();
				for (EnergyMeterContext meter : meters) {
					meterIds.add(meter.getId());
				}
				
				String meterIdStr = StringUtils.join(meterIds, ",");
				builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS));
			}
		}
		
		int limit = 200;
		if (context.containsKey(FacilioConstants.ContextNames.LIMIT_VALUE)) {
			limit = (int) context.get(FacilioConstants.ContextNames.LIMIT_VALUE);
		}
		
		builder.limit(limit);
		
		List<? extends ModuleBaseWithCustomFields> list = builder.get();
		if (module.getExtendModule() != null && module.getExtendModule().getName().equals(FacilioConstants.ContextNames.TICKET)) {
			TicketAPI.loadTicketLookups((Collection<? extends TicketContext>) list);
		}
		else if(moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
			ReadingsAPI.loadReadingParent((List<ReadingContext>) list);
		}
		
		context.put(FacilioConstants.ContextNames.RECORD_LIST, list);
		
		return false;
	}

}
