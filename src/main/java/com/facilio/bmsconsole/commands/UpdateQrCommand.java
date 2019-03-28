package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateQrCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList<Long> assetQr =(ArrayList<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		Map<Long,String> mappedqr = new HashMap<Long, String>();
		for(int i=0;i<assetQr.size();i++) {
			AssetContext assetcontext=new AssetContext();	
			assetcontext.setId(assetQr.get(i));
			assetcontext.setQrVal("facilio_" +  assetcontext.getId());
			context.put(FacilioConstants.ContextNames.RECORD, assetcontext);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule("asset");
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields("asset");
			}
			
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(assetQr.get(i), module));
			 updateBuilder.update(assetcontext);
			
		    mappedqr.put(assetcontext.getId(), assetcontext.getQrVal());
			}
		context.put(FacilioConstants.ContextNames.MAP_QR,mappedqr);
		return false;
	}

}
