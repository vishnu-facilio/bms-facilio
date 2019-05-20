package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.context.GatePassLineItemsContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.GatePassAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetGatePassDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		GatePassContext gatePassContext = (GatePassContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(gatePassContext!=null) {
			GatePassAPI.setLineItems(gatePassContext);
			context.put(FacilioConstants.ContextNames.GATE_PASS, gatePassContext);
		}
		return false;
	}

}
