package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GenericDeleteForSpaces implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long building = (long) context.get(FacilioConstants.ContextNames.ID);
		context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		DeleteRecordBuilder<BaseSpaceContext> deleteAs = new DeleteRecordBuilder<BaseSpaceContext>()
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(building, module));
		deleteAs.markAsDelete();
		return false;
	}

}
