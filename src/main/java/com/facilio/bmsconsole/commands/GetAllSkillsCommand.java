package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetAllSkillsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		//Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		SelectRecordsBuilder<SkillContext> builder = new SelectRecordsBuilder<SkillContext>()
				.table(module.getTableName())
				.moduleName(moduleName)
				.beanClass(SkillContext.class)
				.select(fields)
				.orderBy(module.getTableName()+".ID");

		List<SkillContext> skills = builder.get();
		context.put(FacilioConstants.ContextNames.SKILL_LIST, skills);
		
		return false;
	}

}
