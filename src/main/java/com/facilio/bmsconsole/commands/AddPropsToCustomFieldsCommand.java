package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.customfields.CFType;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;

public class AddPropsToCustomFieldsCommand implements Command{
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioCustomField> fields = (List<FacilioCustomField>) context.get("CustomFields");
		
		for(FacilioCustomField field : fields) {
			field.setTableName(field.getModuleName()+"_Fields");
			field.setDataType(CFType.getCFType(field.getDataTypeCode()));
			
			long objId = CFUtil.getObjId(field.getModuleName()+"_Objects", field.getOrgId());
			
			if(objId == -1) {
				throw new IllegalArgumentException("Invalid Org Id");
			}
			else {
				field.setObjId(objId);
			}
			
			field.setColumnNum(CFUtil.getColumnNum(field));
			
		}
		
		return false;
	}
	
}
