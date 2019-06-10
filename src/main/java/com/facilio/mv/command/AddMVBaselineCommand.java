package com.facilio.mv.command;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;

public class AddMVBaselineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		MVProject mvProject = (MVProject) context.get(MVUtil.MV_PROJECT);
		
		List<MVBaseline> baseLines = mvProject.getBaselines();
		
		for(MVBaseline baseLine :baseLines) {
			context.put(FacilioConstants.ContextNames.FORMULA_FIELD, baseLine.getFormulaField());

			if (baseLine.getFormulaField().getInterval() == -1) {
				int interval = mvProject.getFrequency();
				baseLine.getFormulaField().setInterval(interval);
			}

			Chain addEnpiChain = TransactionChainFactory.addFormulaFieldChain();
			addEnpiChain.execute(context);
		}
		
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		
		InsertRecordBuilder<MVBaseline> insert = new InsertRecordBuilder<MVBaseline>()
				.module(module)
				.fields(fields)
				.addRecords(baseLines);
		
		insert.save();
		
		return false;
	}

}
