package com.facilio.bmsconsole.commands.data;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;

public class EventInfo {

	private static Logger log = LogManager.getLogger(EventInfo.class.getName());

	public String source;
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAdditionInfo() {
		return additionInfo;
	}

	public void setAdditionInfo(String additionInfo) {
		this.additionInfo = additionInfo;
	}

	public String type;
	public String node;
	public String resource;
	public String severity;
	public String description;
	public String additionInfo;
	
	public static void save(EventInfo[] events) throws Exception
	{
		java.sql.Connection con = FacilioConnectionPool.getInstance().getConnection();
		
		PreparedStatement ps=null;
		try {
			String pre_query = "insert into Event (ORGID,SOURCE,NODE,EVENT_TYPE,SEVERITY,DESCRIPTION,ADDITIONAL_INFO) values ("+AccountUtil.getCurrentOrg().getOrgId()+" ,?,?,?,?,?,? ) ";
			ps = con.prepareStatement(pre_query);
			for(int i=0;i<events.length;i++)
			{
				EventInfo event = events[i];
				ps.setString(1, event.getSource());
				ps.setString(2, event.getNode());

				ps.setString(3, event.getType());

				ps.setString(4, event.getSeverity());

				ps.setString(5, event.getDescription());

				ps.setString(6, event.getAdditionInfo());
				ps.addBatch();
				

			}
			ps.executeBatch();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		finally
		{
			ps.close();
			con.close();
		}
		
		
	}
	
}
