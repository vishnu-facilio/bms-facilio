package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.SupplementRecord;

public class AddSuplimentsForEmailBaseMessageCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		 String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
	        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	        List<FacilioField> fields = modBean.getAllFields(moduleName);
	        
	        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
	        List<SupplementRecord> fetchLookupsList = new ArrayList<SupplementRecord>();
	        
	        fetchLookupsList.add((LargeTextField) fieldsAsMap.get("textContent"));
	        fetchLookupsList.add((LargeTextField) fieldsAsMap.get("htmlContent"));
	        
	        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
		
		return false;
	}

}
