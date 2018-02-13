package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReportUnderlyingDataCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dateFilter = (String)context.get(FacilioConstants.ContextNames.DATE_FILTER);

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
		
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT_CONTEXT);
		 
		if (reportContext.getReportCriteriaContexts() != null) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), reportContext.getReportCriteriaContexts().get(0).getCriteriaId());
			builder.andCriteria(criteria);
		}
		if (reportContext.getDateFilter() != null) {
			Condition dateCondition = new Condition();
			dateCondition.setField(reportContext.getDateFilter().getField());
			
			if (dateFilter != null) {
				if (dateFilter.split(",").length > 1) {
					// between
					dateCondition.setOperator(DateOperators.BETWEEN);
					dateCondition.setValue(dateFilter);
				}
				else {
					dateCondition.setOperatorId(Integer.parseInt(dateFilter));
				}
			}
			else {
				if (reportContext.getDateFilter().getReportId() == 20) {
					// between
					dateCondition.setOperator(DateOperators.BETWEEN);
					dateCondition.setValue(reportContext.getDateFilter().getVal());
				}
				else {
					dateCondition.setOperatorId(reportContext.getDateFilter().getOperatorId());
				}
			}
			builder.andCondition(dateCondition);
		}
		if (reportContext.getEnergyMeter() != null) {
			if (reportContext.getEnergyMeter().getSubMeterId() != null) {
				builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", reportContext.getEnergyMeter().getSubMeterId()+"", NumberOperators.EQUALS));
			}
			else if (reportContext.getEnergyMeter().getBuildingId() != null) {
				List<EnergyMeterContext> meters = DeviceAPI.getMainEnergyMeter(reportContext.getEnergyMeter().getBuildingId()+"");
				if (meters != null && meters.size() > 0) {
					List<Long> meterIds = new ArrayList<Long>();
					for (EnergyMeterContext meter : meters) {
						meterIds.add(meter.getId());
					}
					
					String meterIdStr = StringUtils.join(meterIds, ",");
					builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS));
				}
			}
		}
		
		builder.limit(200); // 200 records max
		
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
