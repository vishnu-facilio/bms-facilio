package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.GatePassLineItemsContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddGatePassLineItemsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		List<GatePassLineItemsContext> lineItems = (List<GatePassLineItemsContext>) context.get(FacilioConstants.ContextNames.GATE_PASS_LINE_ITEMS);
		if(lineItems!=null && !lineItems.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.GATE_PASS_LINE_ITEMS);
			List<FacilioField> fields = modBean
					.getAllFields(FacilioConstants.ContextNames.GATE_PASS_LINE_ITEMS);
			addItem(module, fields, lineItems);
		}
		return false;
	}

	private void addItem(FacilioModule module, List<FacilioField> fields, List<GatePassLineItemsContext> parts) throws Exception {
		InsertRecordBuilder<GatePassLineItemsContext> readingBuilder = new InsertRecordBuilder<GatePassLineItemsContext>().module(module)
				.fields(fields).addRecords(parts);
		readingBuilder.save();
	}
	
}
