 package com.facilio.bmsconsole.commands;

 import com.facilio.beans.ModuleBean;
 import com.facilio.command.FacilioCommand;
 import com.facilio.constants.FacilioConstants;
 import com.facilio.fw.BeanFactory;
 import com.facilio.modules.fields.FacilioField;
 import com.facilio.modules.fields.LookupField;
 import com.facilio.util.FacilioUtil;
 import org.apache.commons.chain.Context;
 import org.apache.commons.collections4.CollectionUtils;
 import org.apache.commons.lang3.StringUtils;

 import java.text.MessageFormat;
 import java.util.ArrayList;
 import java.util.List;

 public class LoadMainFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Module Name is not set for the module");
		ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField defaultField = (FacilioField) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD);
		List<FacilioField> selectFields = new ArrayList<>();
		List<LookupField> supplements = new ArrayList<>();

		if(defaultField == null){
			defaultField = modBean.getPrimaryField(moduleName);
			FacilioUtil.throwIllegalArgumentException(defaultField == null, MessageFormat.format("Invalid default field for the given module {0}", moduleName));
			context.put(FacilioConstants.ContextNames.DEFAULT_FIELD, defaultField);
		}
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, defaultField.getModule().getTableName());
		addField(defaultField, selectFields, supplements);

		FacilioField secondaryField = (FacilioField) context.get(FacilioConstants.PickList.SECONDARY_FIELD);
		if(secondaryField != null){
			addField(secondaryField, selectFields, supplements);
		}

		FacilioField subModuleField = (FacilioField) context.get(FacilioConstants.PickList.SUBMODULE_FIELD);
		if(subModuleField != null){
			selectFields.add(subModuleField);
		}

		FacilioField fouthField = (FacilioField) context.get(FacilioConstants.PickList.FOURTH_FIELD);
		if(fouthField != null){
			selectFields.add(fouthField);
		}
		FacilioField colorField = (FacilioField) context.get(FacilioConstants.PickList.COLOR_FIELD);
		if(colorField != null){
			selectFields.add(colorField);
		}

		if (CollectionUtils.isNotEmpty(supplements)) {
			context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
		}
		context.put(FacilioConstants.ContextNames.SELECTABLE_FIELDS, selectFields);

		return false;
		
	}
	 private void addField (FacilioField field, List<FacilioField> selectFields, List<LookupField> supplements) {
		 if (field instanceof LookupField) {
			 supplements.add((LookupField) field);
		 }
		 selectFields.add(field);
	 }

}
