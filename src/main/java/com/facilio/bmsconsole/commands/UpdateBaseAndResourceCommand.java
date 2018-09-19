package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.sql.Connection;
import java.util.logging.Logger;
import org.apache.log4j.LogManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.transaction.FacilioConnectionPool;
import com.google.common.collect.ArrayListMultimap;

public class UpdateBaseAndResourceCommand implements Command,Serializable {
	private static Logger LOGGER = Logger.getLogger(UpdateBaseAndResourceCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		ArrayListMultimap<String, ReadingContext> groupedContext = (ArrayListMultimap<String, ReadingContext>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		for(String module : groupedContext.keySet()) {
			List<ReadingContext> readingsList = groupedContext.get(module);
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
			if(updateBaseQueryColumn != null) {
				try { Connection con = FacilioConnectionPool.INSTANCE.getConnection();
				
				for(int done= 0 ;done< readingsList.size();) {
					String updateResourceQuery = "UPDATE Resources SET SPACE_ID = CASE ID";
					String updateBaseSpaceQuery = "UPDATE BaseSpace SET "+ updateBaseQueryColumn +" = CASE ID";
					
					List<ReadingContext> tempList = new ArrayList<>();
					int remaining  = readingsList.size() - done;
					if(remaining > 1000) {
						tempList = readingsList.subList(done,done+1000);
					}
					else {
						tempList = readingsList.subList(done, done+ remaining);
					}
					for(int temp1=0; temp1 < tempList.size() ; temp1++) {
						updateResourceQuery = updateResourceQuery + " WHEN " + readingsList.get(temp1).getId() + " THEN " + readingsList.get(temp1).getId();
						updateBaseSpaceQuery = updateBaseSpaceQuery + " WHEN " + readingsList.get(temp1).getId() + " THEN " + readingsList.get(temp1).getId();
					}
					
					updateResourceQuery = updateResourceQuery + " else SPACE_ID end;";
					PreparedStatement pstmt = con.prepareStatement(updateResourceQuery, Statement.RETURN_GENERATED_KEYS);
					pstmt.executeUpdate();
					updateBaseSpaceQuery = updateBaseSpaceQuery + " else " + updateBaseQueryColumn + " end;";
					PreparedStatement basepstmt = con.prepareStatement(updateBaseSpaceQuery, Statement.RETURN_GENERATED_KEYS);
					basepstmt.executeUpdate();
					
					done = done + tempList.size();
					LOGGER.severe("UPDATED BASE and RESOURCE");
				}
				}catch(Exception e) {
					LOGGER.severe(e.toString());
				}
			}
		}
		return false;
	}

}
