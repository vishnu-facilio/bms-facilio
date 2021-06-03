package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class AddDefaultSystemFieldsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {

		 List<FacilioModule> modules = CommonCommandUtil.getModules(context);
         if (CollectionUtils.isNotEmpty(modules)) {
             for (FacilioModule module : modules) {
                 List<FacilioField> sysFields = FieldFactory.getSystemPointFields(module);
                 sysFields.forEach(f -> f.setDefault(true));
                 module.setFields(sysFields);
                 insertSystemFields(module);
             }
         }
		return false;
	}
	
	private void insertSystemFields(FacilioModule module) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(FacilioField field : module.getFields()) {
       	 modBean.addField(field);
        }
	}
}
