package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class AddModuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		if(module != null) {
			setModuleName(module);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			long moduleId = modBean.addModule(module);
			module.setModuleId(moduleId);
			
			String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
			if(parentModuleName != null && !parentModuleName.isEmpty()) {
				FacilioModule parentModule = modBean.getModule(parentModuleName);
				modBean.addSubModule(parentModule.getModuleId(), moduleId);
			}
		}
		return false;
	}
	
	private void setModuleName(FacilioModule module) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getModuleIdField());
		fields.add(FieldFactory.getNameField(null));
		FacilioField moduleField = FieldFactory.getModuleIdField();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table("Modules")
					.select(fields)
					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getId())
					.andCustomWhere("NAME = ? OR NAME LIKE ?",module.getName(),module.getName() + "\\_%")
					.orderBy(moduleField.getColumnName() + " desc")
					.limit(1);
		List<Map<String, Object>> props = builder.get();
		String dbModuleName = null;
		if (props != null && !props.isEmpty()) {
			dbModuleName = (String) props.get(0).get("name");
			int count = 0;
			if (dbModuleName.contains("_")) {
				count = Integer.parseInt(dbModuleName.substring(dbModuleName.lastIndexOf('_') + 1));
			}
			module.setName(module.getName() + "_" + ++count);				
		}
	}

}
