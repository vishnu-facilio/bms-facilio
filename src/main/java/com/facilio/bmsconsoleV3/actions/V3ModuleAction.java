package com.facilio.bmsconsoleV3.actions;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.fields.FacilioField.AccessType;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class V3ModuleAction extends V3Action {

	public String moduleList() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.geAllModulesChain();
		FacilioContext context = chain.getContext();
		chain.execute();

		setData("systemModules", context.get("systemModules"));
		setData("customModules", context.get("customModules"));

		return SUCCESS;
	}
	
	private String moduleName;
	
	public String sortableFields() throws Exception {
		FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Module Name is mandatory");

		FacilioChain chain = ReadOnlyChainFactoryV3.getSortableFieldsCommand();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.MODULE_NAME, moduleName);
		context.put(ContextNames.FIELD_ACCESS_TYPE, AccessType.SORT.getVal());
		chain.execute();
		
		setData(ContextNames.FIELDS, context.get(ContextNames.SORT_FIELDS));
		return SUCCESS;
	}
}
