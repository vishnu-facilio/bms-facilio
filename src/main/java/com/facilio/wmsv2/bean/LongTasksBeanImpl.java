package com.facilio.wmsv2.bean;

import com.facilio.accounts.dto.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.command.PackageChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.LongRunningTaskHandler;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.relation.util.RelationshipDataUtil;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.sandbox.utils.SandboxUtil;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.weather.commands.WeatherTransactionChainFactory;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		Map<Long,V3MeterContext> vmParentMap = vmTemplate.constructParentVsVirtualMeters();
		
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
			
			dataMap.put(FacilioConstants.Meter.METER, dataList);

			RelationshipDataUtil.associateRelation(parentModuleName, dataMap, queryParameters, null);
			
		}
	}
	@Override
	public void addDefaultSignupDataToSandbox(JSONObject data){
		try {
			JSONObject content = new JSONObject();
			long productionOrgId = ((Number)data.get(PackageConstants.SOURCE_ORG_ID)).longValue();
			long sandboxOrgId = ((Number)data.get(PackageConstants.TARGET_ORG_ID)).longValue();
			long sandboxId = ((Number)data.get(SandboxConstants.SANDBOX_ID)).longValue();
			String productionDomainName = String.valueOf(data.get(SandboxConstants.PRODUCTION_DOMAIN_NAME));
			Map<String, Object> sandboxOrgData = (Map<String, Object>)data.get(SandboxConstants.SANDBOX_ORG);
			Map<String, Object> signupData = (Map<String, Object>)data.get(FacilioConstants.ContextNames.SIGNUP_INFO);
			Map<String, Object> userData = (Map<String, Object>)data.get(SandboxConstants.SANDBOX_ORG_USER);
			JSONObject signupDataJson = SandboxUtil.getSandboxSignUpDataParser(signupData);
			Organization sandBoxOrg = SandboxUtil.constructSandboxOrganizationFromMap(sandboxOrgData);
			IAMUser iamUser = SandboxUtil.constructSandboxIAMUserFromMap(userData);
			Account account = new Account(sandBoxOrg, new User(iamUser));
			AccountUtil.setCurrentAccount(account);

			LOGGER.info("####Sandbox - Initiating Sandbox Domain Creation");

			AppDomain sandboxDomain = new AppDomain(
					productionDomainName + "." + FacilioProperties.getSandboxSubDomain(),
					AppDomain.AppDomainType.FACILIO.getIndex(), AppDomain.GroupType.FACILIO.getIndex(), sandboxOrgId,
					AppDomain.DomainType.DEFAULT.getIndex());

			List<AppDomain> appDomains = new ArrayList<>();
			appDomains.add(sandboxDomain);

			IAMAppUtil.addAppDomains(appDomains);

			LOGGER.info("####Sandbox - Completed Sandbox Domain Creation");

			LOGGER.info("####Sandbox - Initiating Sandbox Org Creation");
			FacilioChain signupChain = TransactionChainFactory.getOrgSignupChain();
			FacilioContext signupContext = signupChain.getContext();
			signupContext.put("orgId", sandboxOrgId);
			signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupDataJson);
			signupChain.execute();

			LOGGER.info("####Sandbox - Completed Sandbox Org creation");
			content.put(SandboxConstants.SANDBOX_ID, sandboxId);
			content.put(PackageConstants.SOURCE_ORG_ID, productionOrgId);
			content.put("methodName", "createPackageForSandboxData");
			content.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")) + "");
			Messenger.getMessenger().sendMessage(new Message()
					.setKey(LongRunningTaskHandler.KEY + "/" + DateTimeUtil.getCurrenTime())
					.setOrgId(productionOrgId)
					.setContent(content));
		}catch (Exception e){
			LOGGER.error("####Sandbox - Error While Creating Sandbox Org",e);
		}
	}
	@Override
	public void createPackageForSandboxData(JSONObject data) throws Exception {
		PackageFileUtil.accountSwitch(((Number)data.get(PackageConstants.SOURCE_ORG_ID)).longValue());
		try {
			SandboxConfigContext sandboxConfigContext = SandboxAPI.getSandboxById(((Number)data.get(SandboxConstants.SANDBOX_ID)).longValue());
			boolean fromAdminTool = (Boolean) data.getOrDefault(PackageConstants.FROM_ADMIN_TOOL, false);
			if (sandboxConfigContext == null) {
				LOGGER.info("####Sandbox --context is null, returning without creating and installing package");
				return;
			}
			List<Integer> skipComponents = (List<Integer>) data.getOrDefault(PackageConstants.SKIP_COMPONENTS,new ArrayList<>());
			JSONObject content = new JSONObject();
			try {
				LOGGER.info("####Sandbox - Initiating Package creation");
				FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();
				FacilioContext context = createPackageChain.getContext();
				context.put(PackageConstants.DISPLAY_NAME, "package_" + sandboxConfigContext.getOrgId() + "_" + sandboxConfigContext.getSubDomain()+ "_" + System.currentTimeMillis());
				context.put(PackageConstants.SOURCE_ORG_ID, sandboxConfigContext.getOrgId());
				context.put(PackageConstants.TARGET_ORG_ID, sandboxConfigContext.getSandboxOrgId());
				context.put(PackageConstants.SANDBOX_DOMAIN_NAME, sandboxConfigContext.getSubDomain());
				context.put(PackageConstants.SKIP_COMPONENTS, skipComponents);
				context.put(PackageConstants.PACKAGE_TYPE, PackageContext.PackageType.SANDBOX);
				context.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
				createPackageChain.execute();
				content.put(PackageConstants.FILE_ID, SandboxAPI.getRecentPackageId(sandboxConfigContext.getSubDomain()));
				LOGGER.info("####Sandbox - Completed Package creation");
				sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.UPGRADE_IN_PROGRESS);
				SandboxAPI.changeSandboxStatus(sandboxConfigContext);
				SandboxAPI.sendSandboxProgress(sandboxConfigContext);
			} catch (Exception e) {
				sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.PACKAGE_FAILED);
				SandboxAPI.changeSandboxStatus(sandboxConfigContext);
				SandboxAPI.sendSandboxProgress(sandboxConfigContext);
				return;
			}
			content.put("methodName", "installPackageForSandboxData");
			content.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")) + "");
			content.put(PackageConstants.SOURCE_ORG_ID, sandboxConfigContext.getOrgId());
			content.put(PackageConstants.TARGET_ORG_ID, sandboxConfigContext.getSandboxOrgId());
			content.put(SandboxConstants.SANDBOX_ID, sandboxConfigContext.getId());
			Messenger.getMessenger().sendMessage(new Message()
					.setKey(LongRunningTaskHandler.KEY + "/" + DateTimeUtil.getCurrenTime())
					.setOrgId(sandboxConfigContext.getSandboxOrgId())
					.setContent(content));
		}catch(Exception e){
			LOGGER.error("####Sandbox - Error while Creating Package",e);
		}
	}

	@Override
	public void installPackageForSandboxData(JSONObject data) throws Exception {
		PackageFileUtil.accountSwitch(((Number)data.get(PackageConstants.TARGET_ORG_ID)).longValue());
		SandboxConfigContext sandboxConfigContext = null;
		List<Integer> skipComponents = (List<Integer>) data.getOrDefault(PackageConstants.SKIP_COMPONENTS,new ArrayList<>());
		try {
			LOGGER.info("####Sandbox - Initiating Package Deployment");
			FacilioChain deployPackageChain = PackageChainFactory.getDeployPackageChain();
			FacilioContext deployContext = deployPackageChain.getContext();
			deployContext.put(PackageConstants.FILE_ID, ((Number)data.get(PackageConstants.FILE_ID)).longValue());
			deployContext.put(PackageConstants.SOURCE_ORG_ID, ((Number)data.get(PackageConstants.SOURCE_ORG_ID)).longValue());
			deployContext.put(PackageConstants.TARGET_ORG_ID, ((Number)data.get(PackageConstants.TARGET_ORG_ID)).longValue());
			deployContext.put(PackageConstants.SKIP_COMPONENTS, skipComponents);
			deployPackageChain.execute();
			LOGGER.info("####Sandbox - Completed Package Deployment");
			AccountUtil.cleanCurrentAccount();
			try {
				PackageFileUtil.accountSwitch(((Number)data.get(PackageConstants.SOURCE_ORG_ID)).longValue());
				sandboxConfigContext = SandboxAPI.getSandboxById(((Number)data.get(SandboxConstants.SANDBOX_ID)).longValue());
				if(sandboxConfigContext == null){
					LOGGER.error("####Sandbox - Error occurred while changing status to Success");
					return;
				}
				sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.ACTIVE);
				SandboxAPI.changeSandboxStatus(sandboxConfigContext);
				SandboxAPI.sendSandboxProgress(sandboxConfigContext);
			}catch(Exception e){
				LOGGER.error("####Sandbox - Error occurred while changing status ",e);
			}
		}catch(Exception e){
			try {
				LOGGER.info("####Sandbox - Error while  Package Deployment");
				PackageFileUtil.accountSwitch(((Number) data.get(PackageConstants.SOURCE_ORG_ID)).longValue());
				sandboxConfigContext = SandboxAPI.getSandboxById(((Number) data.get(SandboxConstants.SANDBOX_ID)).longValue());
				if (sandboxConfigContext == null) {
					LOGGER.error("####Sandbox - Error occurred while changing status to Failed");
					return;
				}
				sandboxConfigContext.setStatus(SandboxConfigContext.SandboxStatus.INSTALL_FAILED);
				SandboxAPI.changeSandboxStatus(sandboxConfigContext);
				SandboxAPI.sendSandboxProgress(sandboxConfigContext);
			} catch(Exception ex){
				LOGGER.error("####Sandbox - Error occurred while changing status", ex);
			}
		}
	}
}
