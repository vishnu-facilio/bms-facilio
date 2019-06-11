package com.facilio.mv.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;

public class UpdateMVPojectCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProject mvProject = (MVProject) context.get(MVUtil.MV_PROJECT);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		UpdateRecordBuilder<MVProject> update = new UpdateRecordBuilder<MVProject>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(mvProject.getId(), module));
		
		update.update(mvProject);
		
		return false;
	}


}