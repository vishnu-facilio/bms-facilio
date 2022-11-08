package com.facilio.bmsconsoleV3.commands.jobplanSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;

public class AddSupplementForJobPlanSectionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> supplementList = new ArrayList<SupplementRecord>();
        
        supplementList.add((LookupField) fieldsAsMap.get("jobPlan"));

	    context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementList);
		
		return false;
	}

}
