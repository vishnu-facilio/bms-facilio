package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.google.common.collect.ArrayListMultimap;
import nl.basjes.shaded.org.springframework.util.StringUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UpdateBaseAndResourceCommand implements Command,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(UpdateBaseAndResourceCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		try(Connection con = FacilioConnectionPool.INSTANCE.getConnection();) {
				//ConnectionPool does not return the same connection (Caution)	
			ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			Organization org = AccountUtil.getCurrentOrg();
			for(String module : recordsList.keySet()) {
				ModuleBean modBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
				FacilioModule facilioModule = modBean.getModule(module);
				List<Long> readingsList = recordsList.get(module);
				String updateBaseQueryColumn = null;
				switch(module) {
					case "site":
						{
							updateBaseQueryColumn ="SITE_ID";
							break;
						}
					case "building":{
						updateBaseQueryColumn ="BUILDING_ID";
						break;
					}
					case "floor":{
						updateBaseQueryColumn = "FLOOR_ID";
						break;
					}
					default:{
						break;
					}
				}
				if(facilioModule.getExtendModule().getName().equals(FacilioConstants.ContextNames.BASE_SPACE)) {
					for(int done= 0 ;done< readingsList.size();) {
						String updateResourceQuery = "UPDATE Resources SET SPACE_ID = CASE ID";
						String updateBaseSpaceQuery = "UPDATE BaseSpace SET "+ updateBaseQueryColumn +" = CASE ID";
						
						List<Long> tempList = new ArrayList<>();
						int remaining  = readingsList.size() - done;
						if(remaining > 1000) {
							tempList = readingsList.subList(done,done+1000);
						}
						else {
							tempList = readingsList.subList(done, done+ remaining);
						}
						for(int temp1=0; temp1 < tempList.size() ; temp1++) {
							updateResourceQuery = updateResourceQuery + " WHEN " + readingsList.get(temp1) + " THEN " + readingsList.get(temp1);
							updateBaseSpaceQuery = updateBaseSpaceQuery + " WHEN " + readingsList.get(temp1) + " THEN " + readingsList.get(temp1);
						}
						
						updateResourceQuery = updateResourceQuery + " else SPACE_ID end WHERE ORGID = " + org.getId() + " AND ID IN (" + StringUtils.arrayToCommaDelimitedString(readingsList.toArray()) +");";
						PreparedStatement pstmt = con.prepareStatement(updateResourceQuery);
						pstmt.executeUpdate();
						if(updateBaseQueryColumn != null) {
							updateBaseSpaceQuery = updateBaseSpaceQuery + " else " + updateBaseQueryColumn + " end WHERE ORGID = " + org.getId() + " AND ID IN (" + StringUtils.arrayToCommaDelimitedString(readingsList.toArray()) +");";
							PreparedStatement basepstmt = con.prepareStatement(updateBaseSpaceQuery);
							basepstmt.executeUpdate();
						}		
						done = done + tempList.size();
					}
					}
			}
			LOGGER.severe("UPDATED BASE and RESOURCE");
		}
		catch(Exception e) {
			LOGGER.severe(e.toString());
			throw e;
		}
		return false;
	}

}
