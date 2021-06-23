package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DeleteKPICommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long id = (long) context.get(ContextNames.ID);
		
		KPIContext kpi = KPIUtil.getKPI(id, false);
		
		FacilioModule module = ModuleFactory.getKpiModule();
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, module));
				;
		
		deleteRecordBuilder.delete();
		
		KPIUtil.deleteKPIChilds(kpi);
		
		return false;
	}

}
