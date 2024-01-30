package com.facilio.bmsconsole.actions;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.EncodeException;

import com.facilio.auth.actions.PasswordHashUtil;
import com.facilio.bmsconsole.ModuleSettingConfig.impl.PageBuilderConfigUtil;
import com.facilio.bmsconsole.ModuleSettingConfig.impl.PreCommitWorkflowRuleUtil;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.bmsconsole.util.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.ec2.model.Instance;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.LicenseMapping;
import com.facilio.agent.agentcontrol.AgentControl;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.actions.AgentVersionAction;
import com.facilio.agentv2.actions.VersionLogAction;
import com.facilio.agentv2.upgrade.AgentVersionApi;
import com.facilio.aws.util.DescribeInstances;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.cache.LRUCache;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.license.FreshsalesUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.messageQueue.MessageQueueTopic;
import com.facilio.tasker.FacilioTimer;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageType;
import com.facilio.wms.util.WmsApi;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;


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
		newUser.setPassword(PasswordHashUtil.cryptWithMD5(password));
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
		if(!ApplicationApi.isAdminControlAllowed()) {
			return ERROR;
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] selectedFeatures = request.getParameterValues("selected");
		Long orgId = Long.parseLong(request.getParameter("orgid"));

		if (selectedFeatures != null) {
			Map<LicenseMapping, Long> licenseMap = new HashMap<>(LicenseMapping.values().length);
			//initialize license map
			for (LicenseMapping licenseMapping : LicenseMapping.values()) {
				licenseMap.put(licenseMapping, 0L);
			}
			for (String selectedFeature : selectedFeatures) {
				AccountUtil.FeatureLicense license = AccountUtil.FeatureLicense.valueOf(selectedFeature);
				licenseMap.put(license.getGroup(), licenseMap.get(license.getGroup()) + license.getLicense());
			}

			try {
				if (AccountUtil.FeatureLicense.FSM.isEnabled(licenseMap)
				) {
					List<AccountUtil.FeatureLicense> FSMRelatedLicenceList = new ArrayList<>();
					FSMRelatedLicenceList.add(AccountUtil.FeatureLicense.INVENTORY);
					FSMRelatedLicenceList.add(AccountUtil.FeatureLicense.QUOTATION);
					FSMRelatedLicenceList.add(AccountUtil.FeatureLicense.CLIENT);
					FSMRelatedLicenceList.add(AccountUtil.FeatureLicense.VENDOR);
					FSMRelatedLicenceList.add(AccountUtil.FeatureLicense.PAGE_BUILDER);
					FSMRelatedLicenceList.add(AccountUtil.FeatureLicense.TENANTS);

					for(AccountUtil.FeatureLicense featureLicense : FSMRelatedLicenceList){
						if(!featureLicense.isEnabled(licenseMap)) {
							Long sumOfLicense = licenseMap.getOrDefault(featureLicense.getGroup(), 0l);
							sumOfLicense += featureLicense.getLicense();
							licenseMap.put(featureLicense.getGroup(), sumOfLicense);
						}
					}
				}
				if (AccountUtil.FeatureLicense.INVENTORY.isEnabled(licenseMap)
						|| AccountUtil.FeatureLicense.CLIENT.isEnabled(licenseMap)
						|| AccountUtil.FeatureLicense.VENDOR.isEnabled(licenseMap)
						|| AccountUtil.FeatureLicense.TENANTS.isEnabled(licenseMap)
				) {
					if(!AccountUtil.FeatureLicense.SCOPING.isEnabled(licenseMap)) {

						Long sumOfLicense = licenseMap.getOrDefault(AccountUtil.FeatureLicense.SCOPING.getGroup(), 0l);
						
						sumOfLicense += AccountUtil.FeatureLicense.SCOPING.getLicense();

						licenseMap.put(AccountUtil.FeatureLicense.SCOPING.getGroup(), sumOfLicense);
					}
				}

				long licence = AccountUtil.getTransactionalOrgBean(orgId).addLicence(licenseMap);
				System.out.println("##########@@@@@@@@@@@@@" + licence);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}

	public String setApplicationStatus() throws Exception {
		if(!ApplicationApi.isAdminControlAllowed()) {
			return ERROR;
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] selectedApps = request.getParameterValues("selected");
		if(selectedApps != null) {
			Long orgId = Long.parseLong(request.getParameter("orgid"));
			AccountUtil.getTransactionalOrgBean(orgId).setApplicationStatus(selectedApps, orgId);
		}
		return SUCCESS;
	}

	public String addModulePageBuilderConfig() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] selectedModules = request.getParameterValues("selected");
		Long orgId = Long.parseLong(request.getParameter("orgid"));

		if(selectedModules != null) {
			PageBuilderConfigUtil.updatePageBuilderModuleSettings(orgId, selectedModules);
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
		LRUCache.purgeAllCache();

		return SUCCESS;
	}
	
	
	public String pmMetaRefresh() throws IOException {
		
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
		AccountUtil.getTransactionalOrgBean(orgId).runDemoRollup(orgId,timeDuration);
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

		long startTtime = convertDatetoTTimeZone(fromdateTtime);
		long endTtime = convertDatetoTTimeZone(todateendTtime);

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

	public String mlService() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();

		Map<String,Object> mlServiceData = new HashMap<>();
		mlServiceData.put("projectName" ,request.getParameter("projectname"));
		mlServiceData.put("modelName" ,request.getParameter("usecase"));
		mlServiceData.put("serviceType", "default");

		Long fromdateTtime = MLServiceUtil.convertDatetoTTimeZone(request.getParameter("fromTtime"));
		Long todateendTtime = MLServiceUtil.convertDatetoTTimeZone(request.getParameter("toTtime"));

		mlServiceData.put("startTime",fromdateTtime);
		mlServiceData.put("endTime",todateendTtime);

		if(request.getParameter("modelvariables").length() > 2) {
			mlServiceData.put("mlModelVariables", new JSONParser().parse(request.getParameter("modelvariables")));
		}
		if(request.getParameter("workflowinfo").length() > 2) {
			mlServiceData.put("workflowInfo", new JSONParser().parse(request.getParameter("workflowinfo")));
		}

		if(request.getParameter("usecase").equals("multivariateanomaly")) {
			mlServiceData.put("serviceType", "custom");
			if(request.getParameter("model").length() > 2) {
				mlServiceData.put("modelReadings", new JSONParser().parse(request.getParameter("model")));
			}
			if(request.getParameter("groupingmethod").length() > 2) {
				mlServiceData.put("groupingMethod", new JSONParser().parse(request.getParameter("groupingmethod")));
			}
			if(request.getParameter("filtermethod").length() > 2) {
				mlServiceData.put("filteringMethod", new JSONParser().parse(request.getParameter("filtermethod")));
			}
		}else if (request.getParameter("usecase").equals("ahuoptimization")){
			mlServiceData.put("inputType","custom");
			if(request.getParameter("model").length() > 2) {
				mlServiceData.put("modelReadings", new JSONParser().parse(request.getParameter("model")));
			}
		}

		String assetIds = request.getParameter("assetIdList");
		if(assetIds!=null) {
			String []assetArr = assetIds.split(",");
			mlServiceData.put("parentAssetId", Long.valueOf(assetArr[0]));
			if(assetArr.length > 1) {
				JSONArray childAssetIds = new JSONArray();
				for (int i = 1; i < assetArr.length; i++) {
					childAssetIds.add(Long.valueOf(assetArr[i]));
				}
				mlServiceData.put("childAssetIds",childAssetIds);
			}
		}

		Long orgId = Long.parseLong(request.getParameter("orgid"));
		ModuleCRUDBean bean = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", orgId);
		bean.initMLService(mlServiceData);
		return SUCCESS;

	}
	public String updateFeatureLimits() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] featureName = request.getParameterValues("featureName");
		String[] featureLimit=request.getParameterValues("featureLimit");
		Long orgId = Long.parseLong(request.getParameter("orgid"));
		AccountUtil.setCurrentAccount(orgId);
		if(featureLimit !=null && featureName !=null && StringUtils.isNotEmpty(featureName[0]) && StringUtils.isNotEmpty(featureLimit[0])){
			FeatureLimitsUtil.updateFeatureLimits(featureName[0],Long.valueOf(featureLimit[0]));
		}
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

	public String addAgentVersions() throws Exception {
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
		long millis = localDateTime.atZone(timeZone).toInstant().toEpochMilli();
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
			throw new IllegalArgumentException("Facilio alerts param is null or empty " +" bridge URL : "+bridgeUrl + " and  domain is : "+domain);
		}
		bridgeUrl = bridgeUrl + "=" + domain;
		URL url = new URL(bridgeUrl);
		JSONArray jsonArray = new JSONArray();

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		try(Scanner sc = new Scanner(url.openStream());) {
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode:" + responseCode);
			} else {
				StringBuilder inline = new StringBuilder();
				while (sc.hasNext()) {
					inline.append(sc.nextLine());
				}
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

		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() ->getOrgs());
	}

	private static List<Map<String, Object>>  getOrgs() throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("orgId", "Organizations.ORGID", FieldType.ID));
		fields.add(FieldFactory.getField("domain", "Organizations.FACILIODOMAINNAME", FieldType.STRING));

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(fields)
				.table(IAMAccountConstants.getOrgModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(IAMAccountConstants.getOrgDeletedTimeField(), String.valueOf(-1), NumberOperators.EQUALS));
		return builder.get();
	}

	public static List<Map<String, Object>> getAgentOrgs() throws Exception {

		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> getOrgsList());
	}

	public String addAgentVersion() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String version = request.getParameter("version");
		String user = request.getParameter("user");
		String description = request.getParameter("desc");
		String url = request.getParameter("url");
		FacilioService.runAsService(FacilioConstants.Services.AGENT_SERVICE,()->addAgent(version, user, description, url));
		return SUCCESS;
	}

	private void addAgent(String version, String user, String description, String url) {
		AgentVersionAction context = new AgentVersionAction();
		context.setVersion(version);
		context.setDescription(description);
		context.setCreatedBy(user);
		context.setFileName(url);
		context.addAgentVersion();
	}
	
	public String upgradeAgentVersion() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		long versionId = Long.parseLong(request.getParameter("version"));
		long agentId = Long.parseLong(request.getParameter("agentId"));
		FacilioService.runAsService(FacilioConstants.Services.AGENT_SERVICE,()->upgradeAgent(versionId, agentId));
		return SUCCESS;
	}

	private void upgradeAgent(long versionId, long agentId) {
		VersionLogAction context = new VersionLogAction();
		context.setAgentId(agentId);
		context.setVersionId(versionId);
		context.upgradeAgent();
	}
	
	public static List<Map<String,Object>> getAgentVersions() throws Exception{
		Boolean isLatestVersion=false;
		FacilioContext context = new FacilioContext();
    	context.put(AgentConstants.IS_LATEST_VERSION, isLatestVersion);
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE,() -> AgentVersionApi.listAgentVersions(context));
	}
	
	public static List<Map<String, Object>> getAgentList(String orgId) throws Exception{
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", Long.parseLong(orgId));
		return bean.getOrgSpecificAgentList();
	}
	public String updateRulesToPreCommit() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] selectedRuleIds = request.getParameterValues("selectedRules");
		String isPrecommit=request.getParameter("updateRules");
		Long orgId = Long.parseLong(request.getParameter("orgid"));
		AccountUtil.setCurrentAccount(orgId);
		if(selectedRuleIds != null) {
			List<Long> ruleIdsList = Stream.of(selectedRuleIds).map(Long::valueOf).collect(Collectors.toList());
			PreCommitWorkflowRuleUtil.updateWorkflowRulesToPreCommit(ruleIdsList,!Boolean.valueOf(isPrecommit));
		}
		return SUCCESS;
	}
	public String disableAgent() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId = Long.parseLong(request.getParameter("orgId"));
		String level = request.getParameter("level");
		String agentName = request.getParameter("agent");
		boolean action = Boolean.parseBoolean(request.getParameter("action"));
		if(level.equals("topic")) {
			FacilioService.runAsService(FacilioConstants.Services.AGENT_SERVICE,()->MessageQueueTopic.disableOrEnableMessageTopic(action, orgId));
		}else if(level.equals("agent")) {
			AgentControl obj = new AgentControl();
			obj.setAgentName(agentName);
			obj.setOrgId(orgId);
			obj.setAction(action);
			obj.sendToKafka();
		}
		
		return SUCCESS;
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
		List<FileInfo> list =FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,()->FacilioFactory.getFileStore().listSecretFiles());
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
				FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() ->FacilioFactory.getFileStore().addSecretFile(getFileName(),getFile(),getContentType()));
			}
			getSecretFiles();
			return SUCCESS;
		}
		else return "error";
	}
	public String deleteSecretFile() throws Exception {
		LOGGER.info("delete secret file called");
		if(getFileName()!=null){
			FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() ->FacilioFactory.getFileStore().removeSecretFile(getFileName()));
		}
		getSecretFiles();
		return SUCCESS;
	}

	public static String updateAccountUserEmail(String currentEmail, String newEmail) {
		try {
			if(currentEmail == null || newEmail == null){
				return "Please enter a email";
			}
			V3PeopleContext existingUser = V3PeopleAPI.getPeople(newEmail);
			if (existingUser != null) {
				return "Email already exists!";
			}
			Map<String, Object> prop = new HashMap<>();
			prop.put("email", newEmail);
			updateEmailInPeopleTable(currentEmail, prop);
			prop.clear();
			prop.put("userName", newEmail);
			updateUserNameInDc_LoopupTable(currentEmail, prop);
			prop.put("email", newEmail);
			updateEmailInAccount_UsersTable(currentEmail, prop);
			prop.clear();
			return "Email updated successfully";
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while updating user email : " + e.getMessage(), e);
			return "cannot update email";
		}
	}

	public static boolean isEmailInManyOrg(String email) throws Exception {
		try {
			Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());

			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(new HashSet<>())
					.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldsMap.get("email"))
					.table("Account_Users")
					.innerJoin("Account_ORG_Users")
					.on("Account_ORG_Users.USERID = Account_Users.USERID");

			selectBuilder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", email, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
			selectBuilder.groupBy("Account_ORG_Users.ORGID");

			List<Map<String, Object>> props = selectBuilder.get();
			int emailInOrgs = org.apache.commons.collections.CollectionUtils.isNotEmpty(props) ? props.size() : 0;

			return emailInOrgs != 1;
		}catch (Exception e){
			LOGGER.log(Level.INFO, "Exception while seaching for email in orgs : " + e.getMessage(), e);
			return false;
		}
	}

	private static void updateUserNameInDc_LoopupTable(String currentEmail, Map<String, Object> prop) throws SQLException {
		try {
			GenericUpdateRecordBuilder updateDc_LookupTable = new GenericUpdateRecordBuilder()
					.table(IAMAccountConstants.getDCLookupModule().getTableName())
					.fields(IAMAccountConstants.getDCFields())
					.andCondition(CriteriaAPI.getCondition("USERNAME", "userName", currentEmail, StringOperators.IS));
			updateDc_LookupTable.update(prop);
		}catch (Exception e){
			LOGGER.log(Level.INFO, "Exception while updating email in DC_Lookup table : " + e.getMessage(), e);
		}
	}

	private static void updateEmailInPeopleTable(String currentEmail, Map<String, Object> prop) throws Exception {
		try {
			List<FacilioField> peopleFields = Constants.getModBean().getAllFields(ContextNames.PEOPLE);

			GenericUpdateRecordBuilder updatePeopleTable = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getPeopleModule().getTableName())
					.fields(peopleFields)
					.andCondition(CriteriaAPI.getCondition("EMAIL", "email", currentEmail, StringOperators.IS));
			updatePeopleTable.update(prop);
		}catch (Exception e){
			LOGGER.log(Level.INFO, "Exception while updating email in People table : " + e.getMessage(), e);
		}
	}

	private static void updateEmailInAccount_UsersTable(String currentEmail, Map<String, Object> prop) throws SQLException {
		try {
			GenericUpdateRecordBuilder updateAccount_UsersTable = new GenericUpdateRecordBuilder()
					.table(IAMAccountConstants.getAccountsUserModule().getTableName())
					.fields(IAMAccountConstants.getAccountsUserFields())
					.andCondition(CriteriaAPI.getCondition("EMAIL", "email", currentEmail, StringOperators.IS))
					.andCondition(CriteriaAPI.getCondition("USERNAME", "UserName", currentEmail, StringOperators.IS));
			updateAccount_UsersTable.update(prop);
		}catch (Exception e){
			LOGGER.log(Level.INFO, "Exception while updating email in Account_Users table : " + e.getMessage(), e);
		}
	}
}
