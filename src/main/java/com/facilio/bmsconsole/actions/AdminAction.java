package com.facilio.bmsconsole.actions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.EncodeException;

import org.apache.commons.chain.Chain;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.actions.FacilioAuthAction;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.AdminAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.LRUCache;
import com.facilio.license.FreshsalesUtil;
import com.facilio.tasker.FacilioTimer;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageType;
import com.facilio.wms.util.WmsApi;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class AdminAction extends ActionSupport
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AdminAction.class.getName());
	private static org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(AdminAction.class.getName());
	
	public String show()
	{
		return SUCCESS;
	}
	
	public String jobs()
	{
		try 
		{
			ValueStack stack = ActionContext.getContext().getValueStack();
		    Map<String, Object> context = new HashMap<String, Object>();

		    context.put("JOBS", AdminAPI.getSystemJobs()); 
		    stack.push(context);
		} 
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while showing admin job details" +e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String updateUser()
	{
		System.out.println("did not work");
		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId =  Long.parseLong(request.getParameter("orgid"));
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		long roleId =  Long.parseLong(request.getParameter("roleId"));
		User newUser = new User();
		
		/*if(!email.contains("@"))
		{
			newUser.setMobile(email);
		}*/
		newUser.setName(name);
		newUser.setEmail(email);
		newUser.setRoleId(roleId);
		newUser.setPassword(FacilioAuthAction.cryptWithMD5(password));
		newUser.setUserVerified(true);
		newUser.setUserStatus(true);
		try {
			AccountUtil.getTransactionalUserBean(orgId).inviteAdminConsoleUser(orgId, newUser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return SUCCESS;
	}
	
	@SuppressWarnings("unused")
	public String addLicense() throws SQLException 
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] selectedfeatures = request.getParameterValues("selected");
		if(selectedfeatures!=null)
		{
			long flicensevalue = 0, summodule = 0;
			String orgidstring = request.getParameter("orgid");
			if (selectedfeatures != null) 
			{
				for (int i = 0; i < selectedfeatures.length; i++)
				{
					flicensevalue = Long.parseLong(selectedfeatures[i]);
					summodule += flicensevalue;
				}
			}

			try {
				long licence = AccountUtil.getTransactionalOrgBean(Long.parseLong(orgidstring)).addLicence(summodule);
				System.out.println("##########@@@@@@@@@@@@@"+licence);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}
	
	
	

	public String updateCRM()
	{
		//System.out.println("it works");
		HttpServletRequest request = ServletActionContext.getRequest();
		String orgid = request.getParameter("orgid");
		String domainame = ((String[])getFreshsales().get("faciliodomainname"))[0];
		//System.out.println(doma);
		String amount = ((String[])getFreshsales().get("amount"))[0];

		JSONObject data = new JSONObject();
		data.put("name", domainame);
		data.put("amount", amount);
		
		JSONObject salesacct = new JSONObject();
		salesacct.put("name", domainame);
		data.put("sales_account", salesacct);
		
		JSONObject customfield = new JSONObject();
		customfield.put("ORGID", orgid);
		customfield.put("faciliodomainname", domainame);

		data.put("custom_field", customfield);
		
		System.out.println("Final data to freshsales"+data);
		
			try {
				FreshsalesUtil.createLead("deals","deal", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return SUCCESS;
	}
	public JSONObject getFreshsales() {
		return freshsales;
	}

	public void setFreshsales(JSONObject freshsales) {
		this.freshsales = freshsales;
	}
	private JSONObject freshsales = new JSONObject();
	private String email;
	private long userid;
	
	
	public void addJob()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		String name = request.getParameter("name");
		Integer period = Integer.parseInt(request.getParameter("period"));
		try 
		{
			FacilioTimer.schedulePeriodicJob(1, name, 15, period, "facilio");
			AdminAPI.addSystemJob(1l);
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while adding job" +e.getMessage(), e);
		}
	}
	
	public void deleteJob()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		
		Long jobId = Long.parseLong(request.getParameter("jobId"));
		
		try 
		{
			AdminAPI.deleteSystemJob(jobId);
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while adding job" +e.getMessage(), e);
		}
	}
	
	public String clearCache() throws IOException
	{
		LRUCache.getFieldsCache().purgeCache();
		LRUCache.getModuleFieldsCache().purgeCache();
		LRUCache.getUserSessionCache().purgeCache();
		System.out.println("Clear cache called");
		
		
		return SUCCESS ;
	}
	
	public static String reloadBrowser() throws IOException
	{
		Message message = new Message(MessageType.BROADCAST);
		
		
		message.setNamespace("system");
		message.setAction("reload");
		//message.setEventType(WmsEvent.WmsEventType.RECORD_UPDATE);
		message.addData("time", System.currentTimeMillis());
		message.addData("sound", false);
		
	//	JSONObject messagejson = new JSONObject();
	//	message.setContent(messagejson);
		//WmsApi.sendChatMessage(to, message);
		try {
			WmsApi.broadCastMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		} catch (EncodeException e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		
		
		return SUCCESS;
	}

	public String demoRollUp() throws IOException{

		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId=Long.parseLong(request.getParameter("orgId"));
		long timeDuration = Long.parseLong(request.getParameter("durations"));

		try {
			FacilioContext context=new FacilioContext();
			context.put(ContextNames.DEMO_ROLLUP_EXECUTION_TIME,timeDuration);
			context.put(ContextNames.DEMO_ROLLUP_JOB_ORG,orgId);
			Chain demoRollupChain = TransactionChainFactory.demoRollUpChain();
				demoRollupChain.execute(context);
		}		
		catch(Exception e) {
			logger.log(Level.SEVERE, "Exception while executing Demojob" +e.getMessage(), e);
		}

		return SUCCESS;
		
	}

	public String deltaCalculation() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong(request.getParameter("orgid"));
		long assetCategoryId = Long.parseLong(request.getParameter("assetcategory"));
		long fieldId = Long.parseLong(request.getParameter("fieldId"));
		long assetId = Long.parseLong(request.getParameter("assetId"));
		long selectfields = Long.parseLong(request.getParameter("selectfields"));
		long startTtime = Long.parseLong(request.getParameter("fromTtime"));
		long endTtime = Long.parseLong(request.getParameter("toTtime"));
		String email = request.getParameter("email");

		if(selectfields != 0 && selectfields == 1) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
			bean.updateAdminDeltaCalculation(orgId,fieldId,assetId,startTtime,endTtime,email);
		}
		else {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
			bean.removeDuplicates(orgId,fieldId,assetId,startTtime,endTtime,email);
		}
		

		return SUCCESS;
	}

	public String statusLog() throws IOException {

		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong((request.getParameter("orgid"))) ;
		int statusLevel =Integer.parseInt((request.getParameter("loggerlevel")));
		try {
			AccountUtil.getTransactionalOrgBean(orgId).updateLoggerLevel(statusLevel, orgId);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("####loggerlevel  :"+statusLevel);
		
		
		
		return SUCCESS;
	}
	
	private long statusLevel;
	public long getStatusLevel() {
		return statusLevel;
	}

	public void setStatusLevel(long statusLevel) {
		this.statusLevel = statusLevel;
	}
	private long fieldId;
	
	
	public long getFieldId() {
		return fieldId;
	}

	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	private long assetId;
	
	public long getAssetId() {
		return assetId;
	}

	private long assetCategoryId = -1;
	
	public long getAssetCategoryId() {
		return assetCategoryId;
	}

	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	private long startTtime;
	
	public long getStartTtime() {
		return startTtime;
	}

	public void setStartTtime(long startTtime) {
		this.startTtime = startTtime;
	}

	private long endTtime;
	
	public long getEndTtime() {
		return endTtime;
	}

	public void setEndTtime(long endTtime) {
		this.endTtime = endTtime;
	}
	private long orgId;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long durations;
	public long getDurations() {
		return durations;
	}

	public void setDurations(long durations) {
		this.durations = durations;
	}
	
	
	
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public long getUserId() {
		return this.userid;
	}

	public void setUserId(long userId) {
		this.userid = userId;
	}
	
	
	public String clearSession() throws Exception 
	{
		
		String email =getEmail();
		
		long uid = getUserId();
		 //System.out.println("session id :"+email+" "+uid);
		 AccountUtil.getUserBean().clearAllUserSessions(uid, email);
		
		 return SUCCESS;
		
}
	
}	


