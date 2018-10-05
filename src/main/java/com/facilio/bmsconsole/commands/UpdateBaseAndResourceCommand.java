package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.transaction.FacilioConnectionPool;
import com.google.common.collect.ArrayListMultimap;

public class UpdateBaseAndResourceCommand implements Command,Serializable {
	private static Logger LOGGER = Logger.getLogger(UpdateBaseAndResourceCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		ArrayListMultimap<String, ReadingContext> groupedContext = (ArrayListMultimap<String, ReadingContext>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
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
				try { Connection con = FacilioConnectionPool.INSTANCE.getConnection();
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
					
					updateResourceQuery = updateResourceQuery + " else SPACE_ID end;";
					PreparedStatement pstmt = con.prepareStatement(updateResourceQuery, Statement.RETURN_GENERATED_KEYS);
					pstmt.executeUpdate();
					if(updateBaseQueryColumn != null) {
						updateBaseSpaceQuery = updateBaseSpaceQuery + " else " + updateBaseQueryColumn + " end;";
						PreparedStatement basepstmt = con.prepareStatement(updateBaseSpaceQuery, Statement.RETURN_GENERATED_KEYS);
						basepstmt.executeUpdate();
					}
					
					
					done = done + tempList.size();
				}
				}catch(Exception e) {
					LOGGER.severe(e.toString());
				}
				LOGGER.severe("UPDATED BASE and RESOURCE");
			}
			}
				
		return false;
	}

}
