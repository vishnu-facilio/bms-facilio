package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import java.util.Map;
import com.facilio.bmsconsole.util.DashboardUtil;

public class AddOrUpdateKPICommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		KPIContext kpi = (KPIContext)context.get(FacilioConstants.ContextNames.KPI);
		kpi.setActive(true);
		
		FacilioModule module = ModuleFactory.getKpiModule();
		List<FacilioField> fields = FieldFactory.getKPIFields();
		
		if (kpi.getId() > 0) {
			
			validateUpdateKPI(kpi);
			KPIUtil.updateChildIds(kpi);
			kpi.setModifiedTime(System.currentTimeMillis());
			kpi.setModuleId(-1);
			
			if (kpi.getDateOperatorEnum() != null && !kpi.getDateOperatorEnum().isValueNeeded()) {
				kpi.setDateValue("");
			}
			if (kpi.getMetricId() > 0) {
				kpi.setMetricName("");
			}
			else if (StringUtils.isNotEmpty(kpi.getMetricName())) {
				kpi.setMetricId(-99);
			}
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(kpi.getId(), module));
			updateBuilder.update(FieldUtil.getAsProperties(kpi));
		}
		else {
			
			validateAddKPI(kpi);
			KPIUtil.updateChildIds(kpi);
			kpi.setCreatedTime(System.currentTimeMillis());
			getModuleKPILinkName(kpi);
			if (kpi.getModuleId() == -1) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				kpi.setModuleId(modBean.getModule(kpi.getModuleName()).getModuleId());
			}
			if (StringUtils.isNotEmpty(kpi.getMetricName())) {
				if (kpi.getMetricName().equals("count")) {
					kpi.setAggr(null);
				}
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(fields)
					;
			long id = insertBuilder.insert(FieldUtil.getAsProperties(kpi));
			kpi.setId(id);
		}
		
		return false;
	}
	
	private void validateAddKPI(KPIContext kpi) throws Exception {
		if (kpi.getWorkflow() == null && kpi.getCriteria() == null) {
			throw new IllegalArgumentException("Workflow or Criteria is mandatory");
		}
		if (kpi.getModuleId() == -1 && kpi.getModuleName() == null) {
			throw new IllegalArgumentException("Module is mandatory");
		}
		if (kpi.getAggr() == -1 && (kpi.getMetricId() != -1 || !kpi.getMetricName().equals("count")) ) {
			throw new IllegalArgumentException("Aggregation is mandatory for this metric");
		}
	}

	private void validateUpdateKPI(KPIContext kpi) throws Exception {
		if (kpi.getAggr() == -1 && (kpi.getMetricId() != -1 || (StringUtils.isNotEmpty(kpi.getMetricName()) && !kpi.getMetricName().equals("count")) ) ) {
			throw new IllegalArgumentException("Aggregation is mandatory for this metric");
		}
	}
	private void getModuleKPILinkName(KPIContext kpi) throws Exception {
		Map<String, FacilioField> moduleKpiFields = FieldFactory.getAsMap(FieldFactory.getKPIFields());
		FacilioField kpiLinkName = moduleKpiFields.get(FacilioConstants.ContextNames.LINK_NAME);
		FacilioModule module = ModuleFactory.getKpiModule();
		List<String> linkNames = DashboardUtil.getExistingLinkNames(module.getTableName(),kpiLinkName);
		if(kpi.getLinkName() == null){
			String name = kpi.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
			String linkName = DashboardUtil.getLinkName(name,linkNames);
			kpi.setLinkName(linkName);
		}
	}

}
