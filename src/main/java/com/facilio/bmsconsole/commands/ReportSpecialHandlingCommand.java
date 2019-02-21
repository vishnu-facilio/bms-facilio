package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportGroupByField;

public class ReportSpecialHandlingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportContext != null && reportContext.getDataPoints() != null) {
			long moduleId = reportContext.getModuleId();
			if (moduleId > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				handleSpecialCaseCriteria(modBean, module, reportContext.getDataPoints());
			}
		}
		return false;
	}
	
	private Condition handleSpecialCaseCriteria(ModuleBean modBean, FacilioModule module, List<ReportDataPointContext> dataPointContexts) throws Exception {
		if (module.getName().equals("workorder")) {
			FacilioField facilioField = modBean.getField("status", "workorder");
			Condition specialCondition = CriteriaAPI.getCondition(facilioField, "3", NumberOperators.NOT_EQUALS);
			for (ReportDataPointContext dataPointContext : dataPointContexts) {
				Criteria criteria = dataPointContext.getCriteria();
				if (criteria == null) {
					criteria = new Criteria();
					dataPointContext.setCriteria(criteria);
				}
				criteria.addAndCondition(specialCondition);
			}
		}
		return null;
	}
}
