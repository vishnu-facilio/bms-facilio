package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;

public class LoadViewCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(viewName != null && !viewName.isEmpty()) {
			
			FacilioView view = ViewFactory.getView(moduleName + "-" +viewName);
			
			if(view != null) {
				context.put(FacilioConstants.ContextNames.CUSTOM_VIEW, view);
			}
			
//			PreparedStatement pstmt = null;
//			ResultSet rs = null;
//			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
//			try {
//				Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
//				pstmt = conn.prepareStatement("SELECT * FROM Views WHERE ORGID = ? AND NAME = ?");
//				pstmt.setLong(1, orgId);
//				pstmt.setString(2, viewName);
//				
//				rs = pstmt.executeQuery();
//				
//				if(rs.next()) {
//					FacilioView view = CommonCommandUtil.getViewFromRS(rs);
//					Criteria criteria = CriteriaUtil.getCriteria(view.getCriteriaId(),conn);
//					view.setCriteria(criteria);
//					context.put(FacilioConstants.ContextNames.CUSTOM_VIEW, view);
//				}
//			}
//			catch(SQLException e) {
//				e.printStackTrace();
//				throw e;
//			}
//			finally {
//				DBUtil.closeAll(pstmt, rs);
//			}
		}
		
		return false;
	}

}
