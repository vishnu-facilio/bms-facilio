package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddResetAssetActivityCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY,FacilioConstants.ContextNames.ASSET_ACTIVITY);
		for(ResetCounterMetaContext resetCounter:resetCounterMetaList){
			
			if(resetCounter.getEndvalue() > 0){
				JSONObject info = new JSONObject();
				info.put("Fieldname",resetCounter.getField().getDisplayName());
				info.put("Endvalue", resetCounter.getEndvalue());
				if(resetCounter.getStartvalue() > 0){
					info.put("Startvalue", resetCounter.getStartvalue());
				}
				CommonCommandUtil.addActivityToContext(resetCounter.getResourceId(), -1, AssetActivityType.RESET_READING, info, (FacilioContext) context);
				
			}
		}
		
		return false;
	}

}
