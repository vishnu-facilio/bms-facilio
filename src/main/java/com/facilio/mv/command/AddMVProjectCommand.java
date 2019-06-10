package com.facilio.mv.command;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;

public class AddMVProjectCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProject mvProject = (MVProject) context.get(MVUtil.MV_PROJECT);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(mvProject.getEnergyMeter().getId() == -1l) {
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			context.put(FacilioConstants.ContextNames.RECORD, mvProject.getEnergyMeter());
			Chain addAssetChain = FacilioChainFactory.getAddEnergyMeterChain();
			addAssetChain.execute(context);
			
			mvProject.setAutoGenVmMeter(true);
		}
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		InsertRecordBuilder<MVProject> insert = new InsertRecordBuilder<MVProject>()
				.module(module)
				.fields(fields)
				.addRecord(mvProject);
		
		insert.save();
		return false;
	}


}
