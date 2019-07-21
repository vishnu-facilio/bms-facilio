package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class DeletePhotosCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		String modulename = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (id > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(modulename);
			DeleteRecordBuilder<PhotosContext> deleteBuilder = new DeleteRecordBuilder<PhotosContext>()
					.module(module)
					.andCondition(CriteriaAPI.getIdCondition(id, module));
			deleteBuilder.delete();
		}
		return false;
	}

}
