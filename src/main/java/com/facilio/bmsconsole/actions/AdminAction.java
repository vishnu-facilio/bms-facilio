package com.facilio.bmsconsole.actions;

import com.amazonaws.services.ec2.model.Instance;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.auth.actions.FacilioAuthAction;
import com.facilio.aws.util.DescribeInstances;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.AdminAPI;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.LRUCache;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.license.FreshsalesUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.tasker.FacilioTimer;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageType;
import com.facilio.wms.util.WmsApi;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.EncodeException;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.facilio.fw.TransactionBeanFactory;


public class AdminAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(AdminAction.class.getName());
	private org.json.simple.JSONArray secretFilesList = new org.json.simple.JSONArray();
	public String show() {
		return SUCCESS;
	}

	public String jobs() {
		try {
			ValueStack stack = ActionContext.getContext().getValueStack();
			Map<String, Object> context = new HashMap<String, Object>();

			context.put("JOBS", AdminAPI.getSystemJobs());
			stack.push(context);
		} catch (SQLException e) {
			LOGGER.log(Level.INFO, "Exception while showing admin job details" + e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}

	public String updateUser() {
		System.out.println("did not work");
		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong(request.getParameter("orgid"));
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		long roleId = Long.parseLong(request.getParameter("roleId"));
		User newUser = new User();

		/*
		 * if(!email.contains("@")) { newUser.setMobile(email); }
		 */
		newUser.setName(name);
		newUser.setEmail(email);
		newUser.setRoleId(roleId);
		newUser.setPassword(FacilioAuthAction.cryptWithMD5(password));
		newUser.setUserVerified(true);
		newUser.setInviteAcceptStatus(true);
		newUser.setInvitedTime(System.currentTimeMillis());
		newUser.setUserStatus(true);
		try {
			AccountUtil.setCurrentAccount(orgId);
			long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
			AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
				//for now add main app users only from admin console
			newUser.setApplicationId(appId);
			newUser.setAppDomain(appDomain);
			
			AccountUtil.getTransactionalUserBean(orgId).createUser(orgId, newUser, appDomain.getIdentifier(), false, false);
			PeopleAPI.addPeopleForUser(newUser, false);
			AccountUtil.cleanCurrentAccount();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return SUCCESS;
	}

	@SuppressWarnings("unused")
	public String addLicense() throws SQLException {
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] selectedfeatures = request.getParameterValues("selected");
		if (selectedfeatures != null) {
			long flicensevalue = 0, summodule = 0;
			String orgidstring = request.getParameter("orgid");
			if (selectedfeatures != null) {
				for (int i = 0; i < selectedfeatures.length; i++) {
					flicensevalue = Long.parseLong(selectedfeatures[i]);
					summodule += flicensevalue;
				}
			}

			try {
				long licence = AccountUtil.getTransactionalOrgBean(Long.parseLong(orgidstring)).addLicence(summodule);
				System.out.println("##########@@@@@@@@@@@@@" + licence);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}

	public String updateCRM() {
		// System.out.println("it works");
		HttpServletRequest request = ServletActionContext.getRequest();
		String orgid = request.getParameter("orgid");
		String domainame = ((String[]) getFreshsales().get("faciliodomainname"))[0];
		// System.out.println(doma);
		String amount = ((String[]) getFreshsales().get("amount"))[0];

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

		System.out.println("Final data to freshsales" + data);

		try {
			FreshsalesUtil.createLead("deals", "deal", data);
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

	public void addJob() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String name = request.getParameter("name");
		Integer period = Integer.parseInt(request.getParameter("period"));
		try {
			FacilioTimer.schedulePeriodicJob(1, name, 15, period, "facilio");
			AdminAPI.addSystemJob(1l);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while adding job" + e.getMessage(), e);
		}
	}

	public void deleteJob() {
		HttpServletRequest request = ServletActionContext.getRequest();

		Long jobId = Long.parseLong(request.getParameter("jobId"));

		try {
			AdminAPI.deleteSystemJob(jobId);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while adding job" + e.getMessage(), e);
		}
	}

	public String clearCache() throws IOException {
		LRUCache.getFieldsCache().purgeCache();
		LRUCache.getFieldNameCache().purgeCache();
		LRUCache.getModuleFieldsCache().purgeCache();
		LRUCache.getUserSessionCache().purgeCache();
		LRUCache.getResponseCache().purgeCache();

		return SUCCESS;
	}

	public  String reloadBrowser() throws IOException {
		Message message = new Message(MessageType.BROADCAST);

		message.setNamespace("system");
		message.setAction("reload");
		// message.setEventType(WmsEvent.WmsEventType.RECORD_UPDATE);
		message.addData("time", System.currentTimeMillis());
		message.addData("sound", false);

		// JSONObject messagejson = new JSONObject();
		// message.setContent(messagejson);
		// WmsApi.sendChatMessage(to, message);
		try {
			WmsApi.broadCastMessage(message);
		} catch (IOException | EncodeException e) {
			// TODO Auto-generated catch block
			LOGGER.info("Exception occurred ", e);
		}

		return SUCCESS;
	}

	public String demoRollUp() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong(request.getParameter("orgId"));
		long timeDuration = Long.parseLong(request.getParameter("durations"));

		try {
			FacilioChain demoRollupChain = TransactionChainFactory.demoRollUpChain();
			demoRollupChain.getContext().put(ContextNames.DEMO_ROLLUP_EXECUTION_TIME, timeDuration);
			demoRollupChain.getContext().put(ContextNames.DEMO_ROLLUP_JOB_ORG, orgId);
			demoRollupChain.execute();
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while executing Demojob" + e.getMessage(), e);
		}

		return SUCCESS;

	}
	
	public String demoRollUpYearly() {

		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong(request.getParameter("orgId"));
		String lastYearStartTime = request.getParameter("lastYearStartTime");
		lastYearStartTime = lastYearStartTime.replace('T', ' ');
		long time = convertDatetoTTime(lastYearStartTime);
		ZonedDateTime currentZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
		try {

			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
			bean.demoOneTimeJob(orgId, currentZdt);

		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while executing Demojob" + e.getMessage(), e);
		}

		return SUCCESS;
	}

	public static List<Instance> getAwsInstance()throws Exception{

		List<Instance> instanceInfo = DescribeInstances.getAwsvalue();
		if(CollectionUtils.isNotEmpty(instanceInfo)) {
			return instanceInfo;
		}
		return Collections.emptyList();
	}
	
	public String deleteMessageQueue() {
		HttpServletRequest request = ServletActionContext.getRequest();
		int daysToDelete = Integer.parseInt(request.getParameter("days"));
		String tableName = request.getParameter("tableName");
		try {
			FacilioChain deleteMessageChain = TransactionChainFactory.deleteMessageQueueChain();
			deleteMessageChain.getContext().put("NO_OF_DAYS", daysToDelete);
			deleteMessageChain.getContext().put("TABLE_NAME", tableName);
			deleteMessageChain.execute();
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred in Delelt Message queue" + e.getMessage(), e);
		}

		return SUCCESS;
	}

	public String adminReadingTools() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong(request.getParameter("orgid"));
		long assetCategoryId = Long.parseLong(request.getParameter("assetcategory"));
		long fieldId = Long.parseLong(request.getParameter("fieldId"));
		long assetId = Long.parseLong(request.getParameter("assetId"));
		long selectfields = Long.parseLong(request.getParameter("selectfields"));
		String fromdateTtime = request.getParameter("fromTtime");
		fromdateTtime = fromdateTtime.replace('T', ' ');
		String todateendTtime = request.getParameter("toTtime");
		todateendTtime = todateendTtime.replace('T', ' ');
		String email = request.getParameter("email");

		long startTtime = convertDatetoTTime(fromdateTtime);
		long endTtime = convertDatetoTTime(todateendTtime);

		long TtimeLimit = TimeUnit.DAYS.convert(endTtime - startTtime, TimeUnit.MILLISECONDS);

		if (TtimeLimit < 61) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
			bean.readingTools(orgId, fieldId, assetId, startTtime, endTtime, email, selectfields);
		} else {
			throw new IllegalArgumentException(
					"Number of Days Should not be more than 60 days. Your Calculated days : " + TtimeLimit);
		}

		return SUCCESS;
	}

	public String deleteReadings() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		long orgId = Long.parseLong(request.getParameter("orgid"));
		long assetCategoryId = Long.parseLong(request.getParameter("assetcategory"));
		long moduleId = Long.parseLong(request.getParameter("moduleId"));
		if (request.getParameterMap().containsKey("fieldId")) {
			long fieldId = Long.parseLong(request.getParameter("fieldId"));
		}
		long assetId = Long.parseLong(request.getParameter("assetId"));
		String fromdateTtime = request.getParameter("fromTtime");
		fromdateTtime = fromdateTtime.replace('T', ' ');
		String todateendTtime = request.getParameter("toTtime");
		todateendTtime = todateendTtime.replace('T', ' ');

		long startTtime = convertDatetoTTimeZone(fromdateTtime);
		long endTtime = convertDatetoTTimeZone(todateendTtime);

		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		bean.deleteReadings(orgId, fieldId, assetId, startTtime, endTtime, assetCategoryId, moduleId);
		request.removeAttribute("orgid");
		request.removeAttribute("assetcategory");
		request.removeAttribute("assetId");
		response.setHeader("result", "deleted");
		response.sendRedirect("deleteReadings");

//		request.getRequestDispatcher("/WEB-INF/deleteReading");
		return SUCCESS;

	}

	public String moveReadings() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong(request.getParameter("orgid"));
		long assetCategoryId = Long.parseLong(request.getParameter("assetcategory"));
		long fieldId = Long.parseLong(request.getParameter("fieldId"));
		long assetId = -1;
		if (request.getParameterMap().containsKey("assetId")) {
			assetId = Long.parseLong(request.getParameter("assetId"));
		}
		String fromdateTtime = request.getParameter("fromTtime");
		fromdateTtime = fromdateTtime.replace('T', ' ');
		String todateendTtime = request.getParameter("toTtime");
		todateendTtime = todateendTtime.replace('T', ' ');

		long startTtime = convertDatetoTTime(fromdateTtime);
		long endTtime = convertDatetoTTime(todateendTtime);
		long type = Long.parseLong(request.getParameter("shiftType"));


		ModuleCRUDBean bean = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", orgId);
		bean.moveReadings(orgId, fieldId, assetId, startTtime, endTtime, assetCategoryId, durations, type);

		return SUCCESS;

	}

	public String fieldMigration() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		long orgId = Long.parseLong(request.getParameter("orgid"));
		long assetCategoryId = Long.parseLong(request.getParameter("assetcategory"));
		long sourceFieldId = Long.parseLong(request.getParameter("sourceField"));
		long targetFieldId = Long.parseLong(request.getParameter("targetField"));
		long assetId = Long.parseLong(request.getParameter("assetId"));

		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		bean.readingFieldsMigration(orgId, sourceFieldId, assetId,  assetCategoryId, targetFieldId);
		request.removeAttribute("orgid");
		request.removeAttribute("assetcategory");
		request.removeAttribute("assetId");
		response.sendRedirect("fieldMigration");
		return SUCCESS;
	}

	public String addAgentVersion() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String agentVersion = request.getParameter("version");
		String user = request.getParameter("user");
		String description = request.getParameter("desc");
		if (((agentVersion != null) && (!agentVersion.isEmpty())) && ((user != null) && (!user.isEmpty()))
				&& ((description != null) && (!description.isEmpty()))) {

			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
			Context context = new FacilioContext();
			context.put(ContextNames.TABLE_NAME, "Agent_Version");
			context.put(ContextNames.FIELDS, FieldFactory.getAgentVersionFields());
			Map<String, Object> toInsertMap = new HashMap<>();
			toInsertMap.put(AgentConstants.VERSION_ID, agentVersion);
			toInsertMap.put(AgentConstants.CREATED_BY, user);
			toInsertMap.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
			toInsertMap.put(AgentConstants.DESCRIPTION, description);
			context.put(ContextNames.TO_INSERT_MAP, toInsertMap);
			bean.genericInsert(context);
			return SUCCESS;
		}
		// log fail case
		return ERROR;
	}

	public long convertDatetoTTime(String time) {

		LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		long millis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return millis;
	}

	public long convertDatetoTTimeZone (String time) {
		ZoneId timeZone = ZoneId.of(AccountUtil.getCurrentOrg().getTimezone());
		LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		long millis = localDateTime.atZone(timeZone.systemDefault()).toInstant().toEpochMilli();
		return millis;
	}

	public String statusLog() throws IOException {

		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong((request.getParameter("orgid")));
		int statusLevel = Integer.parseInt((request.getParameter("loggerlevel")));
		try {
			AccountUtil.getTransactionalOrgBean(orgId).updateLoggerLevel(statusLevel, orgId);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public static JSONArray getAlertsPointsData(String domain) throws Exception {

		String bridgeUrl = FacilioProperties.getBridgeUrl();
		if (bridgeUrl == null || domain == null || domain.isEmpty() ) {
			throw new IllegalArgumentException("Facilio ALerts URL is null  ");
		}
		bridgeUrl = bridgeUrl + "=" + domain;
		URL url = new URL(bridgeUrl);
		JSONArray jsonArray = new JSONArray();

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		try {
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode:" + responseCode);
			} else {
				Scanner sc = new Scanner(url.openStream());
				StringBuilder inline = new StringBuilder();
				while (sc.hasNext()) {
					inline.append(sc.nextLine());
				}
				sc.close();
				JSONParser parser = new JSONParser();
				jsonArray = (JSONArray) parser.parse(inline.toString());
			}

		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Alters points Exception" + e.getMessage(), e);
		} finally {
			conn.disconnect();
		}
		return jsonArray;
	}

	public static List<Map<String, Object>> getOrgsList() throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("orgId", "Organizations.ORGID", FieldType.ID));
		fields.add(FieldFactory.getField("domain", "Organizations.FACILIODOMAINNAME", FieldType.STRING));

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(fields)
				.table(IAMAccountConstants.getOrgModule().getTableName())
				.innerJoin(ModuleFactory.getAgentDataModule().getTableName()).on("Agent_Data.ORGID=Organizations.ORGID")
				.groupBy("Agent_Data.ORGID");
		return builder.get();
	}

	public static List<Map<String, Object>> getAgentOrgs() throws Exception {

		return FacilioService.runAsServiceWihReturn(() -> getOrgsList());
	}

	private String orgDomain;

	public String getOrgDomain() {
		return orgDomain;
	}

	public void setOrgDomain(String orgDomain) {
		this.orgDomain = orgDomain;
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
	private long type;

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
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

	public String clearSession() throws Exception {
		String email = getEmail();
		long uid = getUserId();
		IAMUserUtil.clearUserSessions(uid);
		return SUCCESS;

	}
	private File file;
	private String fileName;
	private String contentType;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getSecretFiles() throws Exception {
		List<FileInfo> list =FacilioService.runAsServiceWihReturn(()->FacilioFactory.getFileStore().listSecretFiles());
		org.json.simple.JSONArray jsonArray = new org.json.simple.JSONArray();
		for (FileInfo fileInfo: list){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("fileId",fileInfo.getFileId());
			jsonObject.put("fileName",fileInfo.getFileName());
			jsonObject.put("fileSize",fileInfo.getFileSize());
			jsonObject.put("contentType",fileInfo.getContentType());
			jsonArray.add(jsonObject);
		}

		secretFilesList=jsonArray;

		return SUCCESS;
	}

	public org.json.simple.JSONArray getSecretFilesList() {
		return secretFilesList;
	}

	public void setSecretFilesList(org.json.simple.JSONArray secretFilesList) {
		this.secretFilesList = secretFilesList;
	}
	public String addSecretFile() throws Exception {
		LOGGER.info("Add Secret file called .. ");
		LOGGER.info(getFileName() + " : " + getFile() + " : "+ getContentType());
		if (getFileName()!=null && getFile()!=null && getContentType() !=null){

			FileStore fs = FacilioFactory.getFileStore() ;
			if(!fs.isSecretFileExists(getFileName())){
				LOGGER.info("adding secretFile");
				FacilioService.runAsService(() ->FacilioFactory.getFileStore().addSecretFile(getFileName(),getFile(),getContentType()));
			}
			getSecretFiles();
			return SUCCESS;
		}
		else return "error";
	}
	public String deleteSecretFile() throws Exception {
		LOGGER.info("delete secret file called");
		if(getFileName()!=null){
			FacilioService.runAsService(() ->FacilioFactory.getFileStore().removeSecretFile(getFileName()));
		}
		getSecretFiles();
		return SUCCESS;
	}
}
