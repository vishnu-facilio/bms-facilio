package com.facilio.wmsv2.bean;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.command.PackageChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.commands.SandboxDataMigrationChainFactory;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.LongRunningTaskHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.relation.util.RelationshipDataUtil;
import com.facilio.sandbox.command.SandboxTransactionChainFactory;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.weather.commands.WeatherTransactionChainFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.formula.functions.T;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Log4j
public class LongTasksBeanImpl implements LongTasksBean {

    @Override
    public void addBulkWeatherStationMigration(JSONObject data) throws Exception {
        FacilioChain chain = WeatherTransactionChainFactory.addWeatherStationMigratinChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        //Send email
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject json = new JSONObject();
        json.put("sender", EmailClient.getFromEmail("info"));
        json.put("to", data.get("emailAddress"));
        json.put("_tracking", false);
        json.put("subject" , String.valueOf(orgId) + " - Bulk weather station migration status");
        StringBuilder body = new StringBuilder();
        body.append("Details : \nStart Time : ").append(data.get("startTime"))
                .append("\nCompleted Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
                .append("\nStatus : ")
                .append(context.get("message"));
        json.put("message", body.toString());
        FacilioFactory.getEmailClient().sendMailWithoutTracking(json, null);
    }

	@Override
	public void populateVMData(JSONObject data) throws Exception {
		// TODO Auto-generated method stub
		
		Long vmTemplateId = FacilioUtil.parseLong(data.get(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_ID));
		
		VirtualMeterTemplateContext vmTemplate = (VirtualMeterTemplateContext) V3Util.getRecord(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE, vmTemplateId, null);
		
		RelationContext relation = RelationUtil.getRelation(vmTemplate.getRelationShipId(),true);

		List<Object> resourceIdobjList = (List<Object>) data.get(FacilioConstants.ContextNames.RESOURCE_LIST);

		List<Long> resourceIds = new ArrayList<>();

		for (Object resourceIdobj : resourceIdobjList) {
			resourceIds.add(FacilioUtil.parseLong(resourceIdobj));
		}

		Map<Long,V3MeterContext> vmParentMap = vmTemplate.constructParentVsVirtualMeters(resourceIds);
		
		for(Long parentId : vmParentMap.keySet()) {
			
			V3MeterContext vmMeter = vmParentMap.get(parentId);
			
			Map<String, Object>  meterProp = FieldUtil.getAsProperties(vmMeter);
			
			FacilioContext context = V3Util.createRecord(Constants.getModBean().getModule(FacilioConstants.Meter.METER), meterProp);
			
			List<ModuleBaseWithCustomFields> records = Constants.getRecordList(context);
			
			vmMeter.setId(records.get(0).getId());
			
			String parentModuleName = vmTemplate.getScopeEnum().getModuleName();
			String relationLinkName = relation.getMapping1().getMappingLinkName();

			Map<String, List<Object>> queryParameters = new HashMap<>();
			queryParameters.put("relationName", new ArrayList(){{add(relationLinkName);}});
			queryParameters.put("parentId", new ArrayList(){{add(parentId);}});
			
			List<Object> dataList = new ArrayList<>();
			
			dataList.add(vmMeter.getId());
			
			JSONObject dataMap = new JSONObject();

			List<V3MeterContext> meterContext = Constants.getRecordList(context);
			long meterModuleId = meterContext.get(0).getUtilityType().getMeterModuleID();
			FacilioModule module = Constants.getModBean().getModule(meterModuleId);
			dataMap.put(module.getName(), dataList);

			RelationshipDataUtil.associateRelation(parentModuleName, dataMap, queryParameters, null);
			
		}
	}
	@Override
	public void addDefaultSignupDataToSandbox(JSONObject data) throws Exception {
		long productionOrgId = ((Number)data.get(PackageConstants.SOURCE_ORG_ID)).longValue();
		long sandboxId = ((Number)data.get(SandboxConstants.SANDBOX_ID)).longValue();
		SandboxConfigContext sandboxConfigContext = SandboxAPI.getSandboxById(sandboxId);
		try {
			JSONObject content = new JSONObject();
			SandboxAPI.sendSandboxProgress(PackageUtil.SandboxProgressCheckPointType.ORG_SIGNUP_STARTED.getIntVal(), sandboxId, "Org-Creation started for sandbox", productionOrgId);
			LOGGER.info("####Sandbox - Initiating Sandbox Org Creation");

			FacilioChain sandboxOrgChain = SandboxTransactionChainFactory.getSandboxOrgSignupChain();
			FacilioContext sandboxDataContext = sandboxOrgChain.getContext();
			sandboxDataContext.put(SandboxConstants.SANDBOX_ID, sandboxId);
			sandboxOrgChain.execute();

			long sandboxOrgId = (long)sandboxDataContext.get(PackageConstants.TARGET_ORG_ID);

			sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.META_UPGRADE_IN_PROGRESS);
			SandboxAPI.changeSandboxStatus(sandboxConfigContext);
			SandboxAPI.sendSandboxProgress(PackageUtil.SandboxProgressCheckPointType.ORG_SIGNUP_FINISHED.getIntVal(), sandboxId, "Org-Creation done for sandbox", productionOrgId);

			LOGGER.info("####Sandbox - Completed Sandbox Org creation");
			content.put(SandboxConstants.SANDBOX_ID, sandboxId);
			content.put(PackageConstants.SOURCE_ORG_ID, productionOrgId);
			content.put(PackageConstants.TARGET_ORG_ID, sandboxOrgId);
			content.put(PackageConstants.FILE_ID, SandboxAPI.getRecentPackageId(sandboxConfigContext.getSubDomain()));
			content.put(SandboxConstants.SANDBOX_PROGRESS, PackageUtil.SandboxProgressCheckPointType.ORG_SIGNUP_FINISHED.getIntVal());
			content.put("methodName", "installPackageForSandboxData");
			content.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")) + "");
			Messenger.getMessenger().sendMessage(new Message()
					.setKey(LongRunningTaskHandler.KEY + "/" + DateTimeUtil.getCurrenTime())
					.setOrgId(sandboxOrgId)
					.setContent(content));
		}catch (Exception e){
			LOGGER.error("####Sandbox - Error While Creating Sandbox Org",e);
			sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.SANDBOX_ORG_CREATION_FAILED);
			SandboxAPI.changeSandboxStatus(sandboxConfigContext, productionOrgId);
			SandboxAPI.sendSandboxProgress(PackageUtil.SandboxProgressCheckPointType.ORG_SIGNUP_FAILED.getIntVal(), sandboxId, "Org-Creation Failed for sandbox", productionOrgId);
		}
	}
	@Override
	public void createPackageForSandboxData(JSONObject data) throws Exception {
		long sandboxId = ((Number)data.get(SandboxConstants.SANDBOX_ID)).longValue();
		Integer sandboxProgress = (Integer)data.get(SandboxConstants.SANDBOX_PROGRESS);
		String methodName;
		FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();
		FacilioContext context = createPackageChain.getContext();
		long orgId;
		SandboxConfigContext sandboxConfigContext = SandboxAPI.getSandboxById(sandboxId);
		boolean fromAdminTool = (Boolean) data.getOrDefault(PackageConstants.FROM_ADMIN_TOOL, false);
		if (sandboxConfigContext == null) {
			LOGGER.info("####Sandbox --context is null, returning without creating and installing package");
			return;
		}
		List<Integer> skipComponents = (List<Integer>) data.getOrDefault(PackageConstants.SKIP_COMPONENTS,new ArrayList<>());
		JSONObject content = new JSONObject();
		try {
			LOGGER.info("####Sandbox - Initiating Package creation");
			context.put(PackageConstants.DISPLAY_NAME, "package_" + sandboxConfigContext.getOrgId() + "_" + sandboxConfigContext.getSubDomain()+ "_" + System.currentTimeMillis());
			context.put(PackageConstants.SOURCE_ORG_ID, sandboxConfigContext.getOrgId());
			context.put(PackageConstants.SANDBOX_DOMAIN_NAME, sandboxConfigContext.getSubDomain());
			context.put(PackageConstants.SKIP_COMPONENTS, skipComponents);
			context.put(PackageConstants.PACKAGE_TYPE, PackageContext.PackageType.SANDBOX);
			context.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
			context.put(SandboxConstants.SANDBOX_ID, sandboxId);
			context.put(SandboxConstants.SANDBOX_PROGRESS, sandboxProgress);
			createPackageChain.execute();
			content.put(PackageConstants.FILE_ID, SandboxAPI.getRecentPackageId(sandboxConfigContext.getSubDomain()));
			LOGGER.info("####Sandbox - Completed Package creation");
			sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.META_UPGRADE_IN_PROGRESS);

			SandboxAPI.changeSandboxStatus(sandboxConfigContext);
			if(fromAdminTool){
				//For sandbox rerun flow from admin-tool
				methodName = "installPackageForSandboxData";
				content.put(SandboxConstants.SANDBOX_PROGRESS, PackageUtil.SandboxProgressCheckPointType.PACKAGE_CREATION_FINISHED_ON_RERUN.getIntVal());
				SandboxAPI.sendSandboxProgress(PackageUtil.SandboxProgressCheckPointType.PACKAGE_CREATION_FINISHED_ON_RERUN.getIntVal(), sandboxId, "Package creation done for sandbox(RERUN)");
				orgId = sandboxConfigContext.getSandboxOrgId();
			}else{
				//For sandbox addition flow from setup-page
				methodName = "addDefaultSignupDataToSandbox";
				content.put(SandboxConstants.SANDBOX_PROGRESS, PackageUtil.SandboxProgressCheckPointType.PACKAGE_CREATION_FINISHED_ON_SETUP.getIntVal());
				SandboxAPI.sendSandboxProgress(PackageUtil.SandboxProgressCheckPointType.PACKAGE_CREATION_FINISHED_ON_SETUP.getIntVal(), sandboxId, "Package creation done for sandbox");
				orgId = sandboxConfigContext.getOrgId();
			}
			content.put("methodName", methodName);
			content.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")) + "");
			content.put(PackageConstants.SOURCE_ORG_ID, sandboxConfigContext.getOrgId());
			content.put(PackageConstants.TARGET_ORG_ID, sandboxConfigContext.getSandboxOrgId());
			content.put(SandboxConstants.SANDBOX_ID, sandboxConfigContext.getId());
			content.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
			Messenger.getMessenger().sendMessage(new Message()
					.setKey(LongRunningTaskHandler.KEY + "/" + DateTimeUtil.getCurrenTime())
					.setOrgId(orgId)
					.setContent(content));
		} catch (Exception e) {
			LOGGER.info("####Sandbox - Error while Creating Package");
			sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.META_PACKAGE_FAILED);

			SandboxAPI.changeSandboxStatus(sandboxConfigContext);
			if(fromAdminTool){
				//For sandbox rerun flow from admin-tool
				SandboxAPI.sendSandboxProgress(PackageUtil.SandboxProgressCheckPointType.PACKAGE_CREATION_FAILED_ON_RERUN.getIntVal(), sandboxId, "Sandbox failed at package creation(RERUN)");
			}else{
				//For sandbox addition flow from setup-page
				SandboxAPI.sendSandboxProgress(PackageUtil.SandboxProgressCheckPointType.PACKAGE_CREATION_FAILED_ON_SETUP.getIntVal(), sandboxId, "Sandbox failed at package creation");
			}
		}
	}

	@Override
	public void installPackageForSandboxData(JSONObject data) throws Exception {
		long productionOrgId = ((Number)data.get(PackageConstants.SOURCE_ORG_ID)).longValue();
		long sandboxOrgId = ((Number)data.get(PackageConstants.TARGET_ORG_ID)).longValue();
		long sandboxId = ((Number)data.get(SandboxConstants.SANDBOX_ID)).longValue();
		Integer sandboxProgress = (Integer)data.get(SandboxConstants.SANDBOX_PROGRESS);
		List<Integer> skipComponents = (List<Integer>) data.getOrDefault(PackageConstants.SKIP_COMPONENTS,new ArrayList<>());
		SandboxConfigContext sandboxConfigContext = null;
		FacilioChain deployPackageChain = PackageChainFactory.getDeployPackageChain();
		FacilioContext deployContext = deployPackageChain.getContext();
		boolean fromAdminTool = (Boolean) data.getOrDefault(PackageConstants.FROM_ADMIN_TOOL, false);
		try {
			LOGGER.info("####Sandbox - Initiating Package Deployment");
			deployContext.put(PackageConstants.FILE_ID, ((Number)data.get(PackageConstants.FILE_ID)).longValue());
			deployContext.put(PackageConstants.SOURCE_ORG_ID, productionOrgId);
			deployContext.put(PackageConstants.TARGET_ORG_ID, sandboxOrgId);
			deployContext.put(PackageConstants.SKIP_COMPONENTS, skipComponents);
			deployContext.put(SandboxConstants.SANDBOX_ID, sandboxId);
			deployContext.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
			deployContext.put(SandboxConstants.SANDBOX_PROGRESS, sandboxProgress);
			deployPackageChain.execute();
			LOGGER.info("####Sandbox - Completed Package Deployment");
			try {
				sandboxConfigContext = SandboxAPI.getSandboxById(sandboxId, productionOrgId);
				sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.ACTIVE);
				SandboxAPI.changeSandboxStatus(sandboxConfigContext, productionOrgId);
				SandboxAPI.sendSandboxProgress( PackageUtil.SandboxProgressCheckPointType.PACKAGE_INSTALLATION_FINISHED.getIntVal(), sandboxId, "Data Installation done for sandbox", productionOrgId);
			}catch(Exception e){
				LOGGER.error("####Sandbox - Error occurred while changing status ",e);
			}
		}catch(Exception e){
			try {
				LOGGER.info("####Sandbox - Error while  Package Deployment");
				sandboxConfigContext = SandboxAPI.getSandboxById(sandboxId, productionOrgId);
				sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.META_INSTALL_FAILED);
				SandboxAPI.changeSandboxStatus( sandboxConfigContext, productionOrgId);
				SandboxAPI.sendSandboxProgress(PackageUtil.SandboxProgressCheckPointType.PACKAGE_INSTALLATION_FAILED.getIntVal(), sandboxId, "Data Installation Failed for sandbox", productionOrgId);
			} catch(Exception ex){
				LOGGER.error("####Sandbox - Error occurred while changing status", ex);
			}
		}finally {
			PackageFolderContext rootFolder = (PackageFolderContext) deployContext.getOrDefault(PackageConstants.PACKAGE_ROOT_FOLDER, null);
			if(rootFolder != null) {
				File file = new File(rootFolder.getPath());
				FileUtils.deleteDirectory(file);
			}
		}
	}

	@Override
	public void createDataCSVPackageForSandbox(JSONObject data) throws Exception {
		try {
			int limit = parseValue(data, DataMigrationConstants.LIMIT, FacilioUtil::parseInt, 0);
			int offset = parseValue(data, DataMigrationConstants.OFFSET, FacilioUtil::parseInt, 0);
			long sourceOrgId = parseValue(data, DataMigrationConstants.SOURCE_ORG_ID, FacilioUtil::parseLong, -1L);
			long dataMigrationId = parseValue(data, DataMigrationConstants.DATA_MIGRATION_ID, FacilioUtil::parseLong, -1L);
			boolean createFullPackage = parseValue(data, DataMigrationConstants.CREATE_FULL_PACKAGE, FacilioUtil::parseBoolean, false);
			boolean fetchDeletedRecords = parseValue(data, DataMigrationConstants.FETCH_DELETED_RECORDS, FacilioUtil::parseBoolean, false);

			List<String> runOnlyModuleNames = (List<String>) data.get(DataMigrationConstants.RUN_ONLY_FOR_MODULES);
			List<String> migrationModuleNames = (List<String>) data.get(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES);
			List<String> skipModuleNames = (List<String>) data.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);
			Map<String, Map<String, Object>> filters = (Map<String, Map<String, Object>>) data.get(DataMigrationConstants.FILTERS);

			LOGGER.info("####Sandbox - Initiating Data Package creation");

			FacilioChain dataPackageChain = SandboxDataMigrationChainFactory.createDataPackageChain();

			FacilioContext dataMigrationChainContext = dataPackageChain.getContext();
			dataMigrationChainContext.put(DataMigrationConstants.LIMIT, limit);
			dataMigrationChainContext.put(DataMigrationConstants.OFFSET, offset);
			dataMigrationChainContext.put(DataMigrationConstants.SOURCE_ORG_ID, sourceOrgId);
			dataMigrationChainContext.put(DataMigrationConstants.FILTERS, getFilterJSON(filters));
			dataMigrationChainContext.put(DataMigrationConstants.CREATE_FULL_PACKAGE, createFullPackage);
			dataMigrationChainContext.put(DataMigrationConstants.RUN_ONLY_FOR_MODULES, runOnlyModuleNames);
			dataMigrationChainContext.put(DataMigrationConstants.DATA_MIGRATION_ID, dataMigrationId);
			dataMigrationChainContext.put(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, fetchDeletedRecords);
			dataMigrationChainContext.put(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES, migrationModuleNames);
			dataMigrationChainContext.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, skipModuleNames);
			dataMigrationChainContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, DataMigrationConstants.MAX_THREAD_TIME);
			dataPackageChain.execute();

			dataMigrationId = (long) dataMigrationChainContext.get(DataMigrationConstants.DATA_MIGRATION_ID);
			boolean errorOccurred = (boolean)  dataMigrationChainContext.getOrDefault(DataMigrationConstants.ERROR_OCCURRED, false);
			DataMigrationStatusContext dataMigrationObj = DataMigrationConstants.getDataMigrationBean(sourceOrgId).getDataMigrationStatus(dataMigrationId);

			if (!errorOccurred && dataMigrationObj != null && !dataMigrationObj.getStatusEnum().equals(DataMigrationStatusContext.DataMigrationStatus.CREATION_COMPLETED)) {
				data.put(DataMigrationConstants.DATA_MIGRATION_ID, dataMigrationId);
				data.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")) + "");
				data.put("methodName", DataMigrationConstants.LongTaskBeanMethodNames.CREATE_DATA_PACKAGE);

				// for reference
				data.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, FacilioUtil.getAsJSON(dataMigrationObj));

				Message message = new Message().setKey(LongRunningTaskHandler.KEY + "/" + DateTimeUtil.getCurrenTime())
						.setOrgId(sourceOrgId)
						.setContent(data);

				Messenger.getMessenger().sendMessage(message);
			}

			LOGGER.info("####Sandbox - Completed Data Package creation");

		} catch (Exception e) {
			LOGGER.error("####Sandbox - Data Package Creation- Error occurred - " + e);
			throw e;
		}
	}

	@Override
	public void installDataCSVPackageForSandbox(JSONObject data) throws Exception {
		try {
			int queryLimit = parseValue(data, DataMigrationConstants.QUERY_LIMIT, FacilioUtil::parseInt, 0);
			long sourceOrgId = parseValue(data, DataMigrationConstants.SOURCE_ORG_ID, FacilioUtil::parseLong, -1L);
			long targetOrgId = parseValue(data, DataMigrationConstants.TARGET_ORG_ID, FacilioUtil::parseLong, -1L);
			long metaPackageId = parseValue(data, DataMigrationConstants.PACKAGE_ID, FacilioUtil::parseLong, -1L);
			long dataMigrationId = parseValue(data, DataMigrationConstants.DATA_MIGRATION_ID, FacilioUtil::parseLong, -1L);

			String bucketName = (String) data.get(DataMigrationConstants.BUCKET_NAME);
			String bucketRegion = (String) data.get(DataMigrationConstants.BUCKET_REGION);
			String packageFileURL = (String) data.get(DataMigrationConstants.PACKAGE_FILE_URL);
			List<String> moduleSequence = (List<String>) data.get(DataMigrationConstants.MODULE_SEQUENCE);
			List<String> logModuleNames = (List<String>) data.get(DataMigrationConstants.LOG_MODULES_LIST);
			List<String> skipModuleNames = (List<String>) data.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);
			Map<String, Map<String, Object>> filters = (Map<String, Map<String, Object>>) data.get(DataMigrationConstants.FILTERS);

			LOGGER.info("####Sandbox - Initiating Data Package installation");

			FacilioChain dataPackageChain = SandboxDataMigrationChainFactory.getInstallDataMigrationChain();

			FacilioContext dataMigrationChainContext = dataPackageChain.getContext();
			dataMigrationChainContext.put(DataMigrationConstants.LIMIT, queryLimit);
			dataMigrationChainContext.put(DataMigrationConstants.FILTERS, getFilterJSON(filters));
			dataMigrationChainContext.put(DataMigrationConstants.BUCKET_NAME, bucketName);
			dataMigrationChainContext.put(DataMigrationConstants.BUCKET_REGION, bucketRegion);
			dataMigrationChainContext.put(DataMigrationConstants.SOURCE_ORG_ID, sourceOrgId);
			dataMigrationChainContext.put(DataMigrationConstants.TARGET_ORG_ID, targetOrgId);
			dataMigrationChainContext.put(DataMigrationConstants.PACKAGE_ID, metaPackageId);
			dataMigrationChainContext.put(DataMigrationConstants.MODULE_SEQUENCE, moduleSequence);
			dataMigrationChainContext.put(DataMigrationConstants.LOG_MODULES_LIST, logModuleNames);
			dataMigrationChainContext.put(DataMigrationConstants.PACKAGE_FILE_URL, packageFileURL);
			dataMigrationChainContext.put(DataMigrationConstants.DATA_MIGRATION_ID, dataMigrationId);
			dataMigrationChainContext.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, skipModuleNames);
			dataMigrationChainContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, DataMigrationConstants.MAX_THREAD_TIME);
			dataPackageChain.execute();

			dataMigrationId = (long) dataMigrationChainContext.get(DataMigrationConstants.DATA_MIGRATION_ID);
			boolean errorOccurred = (boolean)  dataMigrationChainContext.getOrDefault(DataMigrationConstants.ERROR_OCCURRED, false);
			DataMigrationStatusContext dataMigrationObj = DataMigrationConstants.getDataMigrationBean(targetOrgId).getDataMigrationStatus(dataMigrationId);

			if (!errorOccurred && dataMigrationObj != null && !dataMigrationObj.getStatusEnum().equals(DataMigrationStatusContext.DataMigrationStatus.INSTALLATION_COMPLETED)) {
				data.put(DataMigrationConstants.DATA_MIGRATION_ID, dataMigrationId);
				data.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")) + "");
				data.put("methodName", DataMigrationConstants.LongTaskBeanMethodNames.INSTALL_DATA_PACKAGE);

				// for reference
				data.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, FacilioUtil.getAsJSON(dataMigrationObj));

				Message message = new Message().setKey(LongRunningTaskHandler.KEY + "/" + DateTimeUtil.getCurrenTime())
						.setOrgId(targetOrgId)
						.setContent(data);

				Messenger.getMessenger().sendMessage(message);
			}

			LOGGER.info("####Sandbox - Completed Data Package installation");

		} catch (Exception e) {
			LOGGER.error("####Sandbox - Data Package Installation - Error occurred - " + e);
			throw e;
		}
	}

	private static <T> T parseValue(JSONObject data, String key, Function<Object, T> parser, T defaultValue) {
		Object value = data.get(key);
		if (value != null) {
			return parser.apply(value);
		}
		return defaultValue;
	}

	private static Map<String, JSONObject> getFilterJSON(Map<String, Map<String, Object>> filterMap) {
		Map<String, JSONObject> filterJson = new HashMap<>();
		try {
			if (MapUtils.isNotEmpty(filterMap)) {
				JSONObject jsonObject = FieldUtil.getAsJSON(filterMap);
				if (jsonObject != null) {
					String jsonString = jsonObject.toJSONString();
					filterJson = (Map<String, JSONObject>) new JSONParser().parse(jsonString);
				}
			}
		} catch (Exception e) {
			LOGGER.error("####Sandbox - Exception while parsing filter criteria - " + e);
		}
		return filterJson;
	}
}
