package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class AddDefaultSystemFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		 List<FacilioModule> modules = CommonCommandUtil.getModules(context);
		 
		 boolean putBundleChangeSetEntry = (boolean) context.getOrDefault(BundleConstants.PUT_DEFAULT_BUNDLE_CHANGE_SET_ENTRY, false);
		 
         if (CollectionUtils.isNotEmpty(modules)) {
             for (FacilioModule module : modules) {
                 List<FacilioField> sysFields = FieldFactory.getSystemPointFields(module);
                 sysFields.forEach(f -> f.setDefault(true));
                 module.setFields(sysFields);
                 insertSystemFields(module,putBundleChangeSetEntry);
             }
         }
		return false;
	}
	
	private void insertSystemFields(FacilioModule module,boolean putBundleChangeSetEntry) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(FacilioField field : module.getFields()) {
       	 	modBean.addField(field);
       	 	if(putBundleChangeSetEntry) {
       	 		BundleUtil.addBundleChangeSetForSystemComponents(BundleComponentsEnum.FIELD, field.getId(), field.getDisplayName());
       	 	}
        }
	}
}
