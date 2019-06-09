package com.facilio.mv.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;

public class AddMVAjustmentCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		MVProject mvProject = (MVProject) context.get(MVUtil.MV_PROJECT);
		
		List<MVAdjustment> adjustments = mvProject.getAjustments();
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		
		InsertRecordBuilder<MVAdjustment> insert = new InsertRecordBuilder<MVAdjustment>()
				.module(module)
				.fields(fields)
				.addRecords(adjustments);
		
		insert.save();
		
		return false;
	}

}