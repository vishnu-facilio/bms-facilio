package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class ReportSpecialHandlingCommand implements Command {
	
	private boolean before;
	
	public ReportSpecialHandlingCommand(boolean b) {
		this.before = b;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportContext != null && reportContext.getDataPoints() != null) {
			long moduleId = reportContext.getModuleId();
			if (moduleId > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				if (before) {
					preHandleSpecialCaseCriteria(context, modBean, module, reportContext.getDataPoints());
				} else {
					postHandleSpecialCaseCriteria(context, modBean, module, reportContext.getDataPoints());
				}
			}
		}
		return false;
	}
	
	private void postHandleSpecialCaseCriteria(Context context, ModuleBean modBean, FacilioModule module, List<ReportDataPointContext> dataPointContexts) throws Exception {
		if (module.getName().equals("workorder")) {
			Map<ReportDataPointContext, Criteria> criteriaMap = (Map<ReportDataPointContext, Criteria>) context.get("workorder_criteriaMap");
			if (criteriaMap != null) {
				for (ReportDataPointContext dataPointContext : dataPointContexts) {
					dataPointContext.setCriteria(criteriaMap.get(dataPointContext));
				}
			}
		}
	}

	private void preHandleSpecialCaseCriteria(Context context, ModuleBean modBean, FacilioModule module, List<ReportDataPointContext> dataPointContexts) throws Exception {
		if (module.getName().equals("workorder")) {
//			Map<ReportDataPointContext, Criteria> criteriaMap = new HashMap<>();
//			FacilioField facilioField = modBean.getField("status", "workorder");
//			Condition specialCondition = CriteriaAPI.getCondition(facilioField, "3", NumberOperators.NOT_EQUALS);
//			for (ReportDataPointContext dataPointContext : dataPointContexts) {
//				Criteria criteria = dataPointContext.getCriteria();
//				criteriaMap.put(dataPointContext, criteria);
//				if (criteria == null) {
//					criteria = new Criteria();
//					dataPointContext.setCriteria(criteria);
//				}
//				criteria.addAndCondition(specialCondition);
//			}
//			context.put("workorder_criteriaMap", criteriaMap);
		}
	}
}
