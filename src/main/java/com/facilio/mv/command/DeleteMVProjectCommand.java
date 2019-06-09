package com.facilio.mv.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;

public class DeleteMVProjectCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProject mvProject = (MVProject) context.get(MVUtil.MV_PROJECT);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		
		DeleteRecordBuilder<MVProject> delete = new DeleteRecordBuilder<MVProject>()
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(mvProject.getId(), module));
		
		delete.delete();
		return false;
	}


}
