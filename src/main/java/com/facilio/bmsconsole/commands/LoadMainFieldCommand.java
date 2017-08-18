 package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleBeanImpl;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class LoadMainFieldCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name is not set for the module");
		}
 		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			ModuleBeanImpl modBean = new ModuleBeanImpl();// (ModuleBean) BeanFactory.lookup("ModuleBean", orgId, conn);
		//	modBean.
			FacilioField defaultField = modBean.getPrimaryField(moduleName);
			
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, defaultField.getModuleTableName());
			context.put(FacilioConstants.ContextNames.DEFAULT_FIELD, defaultField);
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
		
		return false;
		
	}

}
