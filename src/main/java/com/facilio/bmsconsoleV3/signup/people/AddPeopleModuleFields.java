package com.facilio.bmsconsoleV3.signup.people;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddPeopleModuleFields extends SignUpData{
	@Override
	public void addData() throws Exception{

		ModuleBean bean = Constants.getModBean();
		FacilioModule module = bean.getModule(FacilioConstants.ContextNames.PEOPLE);

		List<FacilioField> fields = new ArrayList<>();

		fields.add(FieldFactory.getDefaultField("user","Is User","IS_USER", FieldType.BOOLEAN));
		fields.add(FieldFactory.getDefaultField("labour","Is Labour","IS_LABOUR", FieldType.BOOLEAN));

		FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
		chain.execute();
	}
}
