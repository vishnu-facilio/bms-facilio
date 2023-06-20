package com.facilio.workflows.functions;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.facilio.accounts.sso.SSOUtil;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.cards.util.CardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.pdf.PdfUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import com.facilio.service.FacilioHttpUtilsFW;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

;@Log4j
@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.DEFAULT_FUNCTION)
public class FacilioDefaultFunction  {
		public Object allMatch(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);

			if(objects[0] == null) {
				return false;
			}

			List<Object> list = (List<Object>) objects[0];
			boolean allEqual = list.isEmpty() || list.stream().allMatch(list.get(0)::equals);

			return allEqual;
		}

		public Object getMainEnergyMeter(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
			if(objects[0] == null) {
				throw new FunctionParamException("Required Object is null");
			}
			else if(!(objects[0] instanceof Long)) {
				throw new FunctionParamException("Required Object is not of type List");
			}

			Long spaceId = (Long) objects[0];
			List<EnergyMeterContext> energyMeterContexts = DashboardUtil.getMainEnergyMeter(spaceId+"");

			if(energyMeterContexts.size() < 0 || energyMeterContexts.get(0) == null) {
				return null;
			}

			System.out.println("spaceId --  "+spaceId+"  allEqual --- "+energyMeterContexts.get(0).getId());

			return energyMeterContexts.get(0).getId();
		};

		public Object stringEquals(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 2) {
				return false;
			}
			return (objects[0] == null ? objects[1] == null : objects[0].toString().equals(objects[1].toString()));
		}

		public Object stringContains(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 2) {
				return false;
			}
			return (objects[0] == null ? objects[1] == null : objects[0].toString().contains(objects[1].toString()));
		}

		public Object convertUnit(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 3) {
				return null;
			}
			Double value = null;
			Integer fromUnit = null;
			Integer toUnit = null;
			if(objects[0] != null) {
				value = Double.parseDouble(objects[0].toString());
			}
			if(objects[1] != null) {
				fromUnit = Integer.parseInt(objects[1].toString());
			}
			if(objects[2] != null) {
				toUnit = Integer.parseInt(objects[2].toString());
			}
			LOGGER.debug( "value -- "+value);
			LOGGER.debug( "fromUnit -- "+fromUnit);
			LOGGER.debug( "toUnit -- "+toUnit);
			if(value != null && fromUnit != null && toUnit != null) {
				return UnitsUtil.convert(value, Unit.valueOf(fromUnit), Unit.valueOf(toUnit));
			}
			return null;
		}

		public Object getFilePrivateUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			FileStore fs = FacilioFactory.getFileStore();

			Long fileId = null;
			if(objects[0] != null) {
				fileId = (long) Double.parseDouble(objects[0].toString());
			}
			String url = null;
			if (AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.THROW_403_WEBTAB)) {

				url = fs.getUrl(FacilioConstants.ContextNames.DASHBOARD, fileId,false);
			} else {
				url = fs.getPrivateUrl(fileId);
			}
			return url;
		}

		public Object getFileName(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			FileStore fs = FacilioFactory.getFileStore();

			Long fileId = null;
			if(objects[0] != null) {
				fileId = (long) Double.parseDouble(objects[0].toString());
			}

			return fs.getFileInfo(fileId).getFileName();
		}

		public Object fetchData(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 2) {
				return false;
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = (String) objects[0];
			String criteriaString = (String) objects[1];
			Criteria criteria = WorkflowUtil.parseCriteriaString(moduleName, criteriaString);
			String fieldName = null, aggregateCondition = null;

			if(objects[2] != null) {
				fieldName = (String) objects[2];
			}
			if(objects[3] != null) {
				aggregateCondition = (String) objects[3];
			}

			FacilioModule module = modBean.getModule(moduleName);

			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>();
			builder.module(module);
			builder.andCriteria(criteria);

			if(fieldName != null) {

				if(aggregateCondition != null) {

				}
			}
			return null;
		}

		public Object getUnit(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects.length < 1) {
				return false;
			}
			if(objects[0] == null) {
				return null;
			}

			String fieldRefObject = objects[0].toString();
			String moduleRefObject = null;

			if(objects.length > 1 && objects[1] != null) {
				moduleRefObject = objects[1].toString();
			}

			int metric = -1;
			if(FacilioUtil.isNumeric(fieldRefObject)) {
				Long fieldId = Long.parseLong(fieldRefObject);
				metric = CardUtil.getMetic(fieldId);
			}
			else if(moduleRefObject != null) {
				metric = CardUtil.getMetic(moduleRefObject, fieldRefObject);
			}
			if(metric > 0) {
				return  AccountUtil.getOrgBean().getOrgDisplayUnit(metric);
			}
			return null;
		}

		public Object sumData(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 2) {
				return false;
			}
			int counter = 0;
			FacilioModule module = null;
			FacilioField field = null;

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			String arg1 = objects[counter++].toString();

			if(FacilioUtil.isNumeric(arg1)) {
				field = modBean.getField(Long.parseLong(arg1));
				module = field.getModule();
			}
			else {
				String moduleName =  arg1;
				String fieldName = (String) objects[counter++];

				module = modBean.getModule(moduleName);
				field = modBean.getField(fieldName, moduleName);
			}

			String startTime = null,endtime = null;

			if(counter < objects.length && objects[counter] != null) {
				startTime = objects[counter++].toString();
			}
			if(counter < objects.length && objects[counter] != null) {
				endtime = objects[counter++].toString();
			}
			String parentIds = null;			//comma seperated values
			if(counter < objects.length && objects[counter] != null) {
				parentIds = objects[counter++].toString();
			}
			FacilioField dateField = modBean.getField("ttime", module.getName());
			FacilioField parentField = modBean.getField("parentId", module.getName());

			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
			builder.table(module.getTableName());

			if(field != null) {

				List<FacilioField> selectFields = new ArrayList<>();
				String selectFieldString = "sum("+field.getColumnName()+")";
				FacilioField selectField = null;
				if(field instanceof NumberField) {
					NumberField numberField =  (NumberField)field;
					NumberField selectFieldNumber = new NumberField();
					selectFieldNumber.setMetric(numberField.getMetric());
					selectField = selectFieldNumber;
				}
				else {
					selectField = new FacilioField();
				}
				selectField.setName(field.getName());
				selectField.setDisplayName(field.getDisplayName());
				selectField.setColumnName(selectFieldString);
				selectField.setFieldId(field.getFieldId());

				selectFields.add(selectField);
				builder.select(selectFields);
			}
			LOGGER.debug( "startTime -- "+startTime);
			LOGGER.debug( "endtime -- "+endtime);
			if(startTime != null && endtime != null) {
				Condition condition = CriteriaAPI.getCondition(dateField, startTime+","+endtime, DateOperators.BETWEEN);
				builder.andCondition(condition);
			}
			if(parentIds != null) {
				Condition condition = CriteriaAPI.getCondition(parentField, parentIds, StringOperators.IS);
				builder.andCondition(condition);
			}
			builder.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module));
			builder.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleid", module.getModuleId()+"", NumberOperators.EQUALS));
			List<Map<String, Object>> props = builder.get();
			LOGGER.debug( "builder -- "+builder);
			if(props != null && !props.isEmpty()) {
				Object sum = props.get(0).get(field.getName());
				LOGGER.debug( "res -- "+sum);
				return Double.parseDouble(sum.toString());
			}
			return null;
		}

		public Object picklist(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 1) {
				return null;
			}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			String moduleName = objects[0].toString();
			if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				return LookupSpecialTypeUtil.getPickList(moduleName);
			}

			FacilioModule module = modBean.getModule(moduleName);
			FacilioField field = objects.length > 1 && objects[1] != null ? modBean.getField(objects[1].toString(), moduleName) : modBean.getPrimaryField(moduleName);

			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.select(Collections.singletonList(field))
					.orderBy("ID");

			List<Map<String, Object>> records = builder.getAsProps();
			if(records != null && records.size() > 0) {
				Map<String, String> pickList = new HashMap<>();
				for(Map<String, Object> record : records) {
					pickList.put(((Long) record.get("id")).toString(), record.get(field.getName()).toString());
				}
				return pickList;
			}

			return null;
		}

		public Object getPermaLinkUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 1) {
				return null;
			}

			AppDomain appDomain = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());

			User user = AccountUtil.getUserBean().getUser((String) objects[5], appDomain.getIdentifier());
			String token = AccountUtil.getUserBean().generatePermalinkForURL(objects[1].toString(), user);
			String permalLinkURL = objects[0].toString()+objects[1].toString()+"?token="+token+"&startDate="+Long.valueOf(objects[2].toString())+"&endDate="+Long.valueOf(objects[3].toString());

			return permalLinkURL;
		}

		public Object getMaintenancePermaLinkUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 1) {
				return null;
			}
			AppDomain appDomain = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());
			User user = AccountUtil.getUserBean().getUser((String) objects[5], appDomain.getIdentifier());
			String token = AccountUtil.getUserBean().generatePermalinkForURL(objects[1].toString(), user);
			DashboardContext dashboard = DashboardUtil.getDashboard(Long.valueOf(objects[6].toString()));
			if(dashboard == null) {
				return objects[0].toString()+"/app";
			}
			org.json.simple.JSONObject dateJson = new org.json.simple.JSONObject();
			dateJson.put("startTime", Long.valueOf(objects[2].toString()));
			dateJson.put("endTime", Long.valueOf(objects[3].toString()));
			dateJson.put("operatorId", Long.valueOf(objects[4].toString()));

			Long siteId = Long.valueOf(objects[7].toString());


			String permalLinkURL = objects[0].toString()+"/app/maintenanceReport?token="+token+"&id="+dashboard.getId()+"&linkName="+dashboard.getLinkName()+"&siteId="+siteId+"&moduleName=workorder"+"&name="+URLEncoder.encode(dashboard.getDashboardName())+"&daterange="+URLEncoder.encode(dateJson.toString());
			if(siteId > 0) {
				SiteContext site = SpaceAPI.getSiteSpace(siteId);
				if(site != null) {
					permalLinkURL = permalLinkURL + "&siteName=" + URLEncoder.encode(site.getName());
				}
			}
			return permalLinkURL;
		}
		public Object getUserName(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			User user = AccountUtil.getCurrentUser();
			return user.getName();
		}
		public Object getUserEmail(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			User user = AccountUtil.getCurrentUser();
			return user.getEmail();
		}
		public Object getRecord(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(objects[0].toString());
			List<FacilioField> fields = modBean.getAllFields(objects[0].toString());

			Class beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(objects[0].toString());
			if (beanClassName == null) {
				beanClassName = ModuleBaseWithCustomFields.class;
			}
			SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.beanClass(beanClassName)
					.select(fields)
					.andCondition(CriteriaAPI.getIdCondition(Long.valueOf(objects[1].toString()), module))
					;

			List<? extends ModuleBaseWithCustomFields> records = builder.get();
			if(CollectionUtils.isNotEmpty(records)) {
				return records.get(0);
			}
			else {
				return null;
			}

		}

		public Object mergeJson(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			Map json1 = (Map) objects[0];
			Map json2 = (Map) objects[1];

			if(json1 != null && json2 != null) {
				for(Object key :json2.keySet()) {
					if(json1.get(key) instanceof Map && json2.get(key) instanceof Map) {

						Map json3 = (Map) json1.get(key);
						Map json4 = (Map) json2.get(key);

						for(Object key1 :json4.keySet()) {
							json3.put(key1, json4.get(key1));
						}
					}
					else {
						json1.put(key, json2.get(key));
					}
				}
			}
			return null;
		}
		public Object exportURL(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			String url = (String) objects[0];
			String fileName = PdfUtil.exportUrlAsPdf(url.toString(), true, null, FileFormat.PDF);
			return fileName;
		}

		public Object exportURLWithFileReturn(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			String url = (String) objects[0];
			Long fileId = PdfUtil.exportUrlAsPDF(url.toString(), null,null, FileFormat.PDF);
			return fileId;
		}

		public Object getOrgFileUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			FileStore fs = FacilioFactory.getFileStore();
			long fileId = (long) Double.parseDouble(objects[0].toString());
			return fs.getOrgiFileUrl(fileId);
		}

		public Object getOrgDownloadUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			FileStore fs = FacilioFactory.getFileStore();
			long fileId = (long) Double.parseDouble(objects[0].toString());
			return fs.getOrgiDownloadUrl(fileId);
		}

		public Object encodeFileToBase64Binary(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			FileStore fs = FacilioFactory.getFileStore();

			long fileId = (long) Double.parseDouble(objects[0].toString());

			return fs.encodeFileToBase64Binary(fileId);

		}


		public Object getTinyUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			String longUrl = objects[0].toString();

			return BitlyUtil.getSmallUrl(longUrl);

		}

		public Object getTransitionPermaLink(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 1) {
				return null;
			}

			LOGGER.debug( "recordId -- "+(long)objects[1]);

			User user = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
			JSONObject jObj = new JSONObject();
			jObj.put("recordId", (long)objects[1]);
			jObj.put("allowUrls",(String)objects[2]);

			String moduleName = (String)objects[3];
			boolean getTransitionDetails = false;
			if (objects.length > 4) {
				getTransitionDetails =  (boolean)objects[4];
			}

			List<Map<String, Object>> details = new ArrayList<>();

			ModuleBaseWithCustomFields record = RecordAPI.getRecord(moduleName, (long)objects[1]);

			if(record.getStateFlowId() > 0 && record.getModuleState() != null) {

				LOGGER.debug( "record.getStateFlowId() -- "+record.getStateFlowId());

				FacilioContext facilioContext = new FacilioContext();
				facilioContext.put(FacilioConstants.ContextNames.STATE_TRANSITION_GET_PERMALINK_ONLY, true);
				List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(record.getStateFlowId(), record.getModuleState().getId(), moduleName, record, facilioContext);
				jObj.put("moduleId", record.getModuleId());
				jObj.put("moduleName", moduleName);
				ArrayList<String> permalinks = new ArrayList<String>();

				if(CollectionUtils.isNotEmpty(nextStateRule)){

					LOGGER.debug( "nextStateRule -- "+nextStateRule);

					for(WorkflowRuleContext rule : nextStateRule) {
						long transitionId = rule.getId();
						jObj.put("transitionId", transitionId);
						String token = AccountUtil.getUserBean().generatePermalink(user, jObj);
						String permalLinkURL = objects[0].toString()+objects[2].toString()+"?token=" + token;

						if (getTransitionDetails) {
							Map<String, Object> detail = new HashMap<>();
							detail.put("permalink", permalLinkURL);
							detail.put("transitionId", transitionId);
							detail.put("name", rule.getName());
							details.add(detail);
						}

						permalinks.add(permalLinkURL);
					}
					return getTransitionDetails ? details : permalinks;
				}
			}
			return null;


		}

		public Object getApprovalPermaLink(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if (objects == null || objects.length < 1) {
				return null;
			}

			LOGGER.debug("approval recordId -- "+(long)objects[1]);

			User user = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
			JSONObject jObj = new JSONObject();
			jObj.put("recordId", (long)objects[1]);
			jObj.put("allowUrls",(String)objects[2]);

			String moduleName = (String)objects[3];
			boolean getTransitionDetails = false;
			if (objects.length > 4) {
				getTransitionDetails =  (boolean)objects[4];
			}

			List<Map<String, Object>> details = new ArrayList<>();

			ModuleBaseWithCustomFields record = RecordAPI.getRecord(moduleName, (long)objects[1]);

			if(record.getStateFlowId() > 0 && record.getModuleState() != null) {

				LOGGER.debug( "approval record.getStateFlowId() -- "+record.getStateFlowId());

				List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(record.getApprovalFlowId(), record.getApprovalStatus().getId(), moduleName, record, new FacilioContext());
				jObj.put("moduleId", record.getModuleId());
				jObj.put("moduleName", moduleName);
				ArrayList<String> permalinks = new ArrayList<String>();

				if(CollectionUtils.isNotEmpty(nextStateRule)){

					LOGGER.debug( "approval nextStateRule -- "+nextStateRule);

					for(WorkflowRuleContext rule : nextStateRule) {
						long transitionId = rule.getId();
						jObj.put("transitionId", transitionId);
						String token = AccountUtil.getUserBean().generatePermalink(user, jObj);
						String permalLinkURL = objects[0].toString()+objects[2].toString()+"?token=" + token;

						if (getTransitionDetails) {
							Map<String, Object> detail = new HashMap<>();
							detail.put("permalink", permalLinkURL);
							detail.put("transitionId", transitionId);
							detail.put("name", rule.getName());
							details.add(detail);
						}

						permalinks.add(permalLinkURL);
					}
					return getTransitionDetails ? details : permalinks;
				}
			}
			return null;


		}

		public Object getTimeIntervals(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			long startTime = (long) Double.parseDouble(objects[0].toString());
			long endTime = (long) Double.parseDouble(objects[1].toString());
			int interval = (int) Double.parseDouble(objects[2].toString());

			return DateTimeUtil.getTimeIntervals(startTime, endTime, interval);
		}

		public Object getPermaLinkToken(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			FacilioChain chain = FacilioChainFactory.getPermaLinkTokenChain();
			AppDomain appDomain = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());

			FacilioContext context = chain.getContext();

			String url = objects[0].toString();
			Map<String,Object> sessionObjectMap = (Map<String,Object>) objects[1];

			JSONObject sessionObjectJson = WorkflowV2Util.getAsJSONObject(sessionObjectMap);
			String email = objects[2].toString();

			context.put(FacilioConstants.ContextNames.PERMALINK_FOR_URL,url);
			context.put(FacilioConstants.ContextNames.SESSION,sessionObjectJson);
			context.put(FacilioConstants.ContextNames.USER_EMAIL,email);
			context.put(FacilioConstants.ContextNames.IDENTIFIER, appDomain.getIdentifier());

			chain.execute();

			String permaLink = (String) context.get(FacilioConstants.ContextNames.PERMALINK_TOKEN_FOR_URL);
			return permaLink;

		}

		public Object getOrgInfo(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			String[] names = objects[0].toString().split(",");
			return CommonCommandUtil.getOrgInfo(names);

		}

		public Object getPublicFileUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			if(objects[0] != null) {
//				FileStore fs = FacilioFactory.getFileStore();
//		    	FileInfo fileInfo = fs.getFileInfo((long)objects[0]);
//		    	return PublicFileUtil.createPublicFile(fileInfo.getFileId(), fileInfo.getFileName(), fileInfo.getContentType(), fileInfo.getContentType());
//				PublicFileContext publicFileContext = PublicFileUtil.createPublicFile(fileInfo.getFileId(), fileInfo.getFileName(), fileInfo.getContentType(), fileInfo.getContentType());
//				if(publicFileContext != null) {
//					return publicFileContext.getPublicUrl();
//				}
			}
			return null;
		}

		public Object getUserNameForId(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			User user = AccountUtil.getUserBean().getUser((Long) objects[0], false);
			if (user != null) {
				return user.getName();
			}
			return null;
		}

		public Object isUserInSiteScope(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			Long siteId = (Long) objects[0];
			Long assignedToId = (Long) objects[1];
			User assignedTo = AccountUtil.getUserBean().getUser(assignedToId, true);
			List<Long> accessibleSpace = assignedTo.getAccessibleSpace();
			Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
			Set<Long> userSiteIds = new HashSet<>();
			if (accessibleSpace != null && !accessibleSpace.isEmpty()) {
				for (long id : accessibleSpace) {
					BaseSpaceContext space = idVsBaseSpace.get(id);
					if (space.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
						userSiteIds.add(space.getId());
					} else {
						userSiteIds.add(space.getSiteId());
					}
				}
			}
			if (!userSiteIds.isEmpty() && !userSiteIds.contains(siteId)) {
				return false;
			}
			return  true;
		}

		public Object getFromObject(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			if(objects[0] != null) {
				Map<String, Object> map = FieldUtil.getAsProperties(objects[0]);
				return map.get(objects[1]);
			}
			return null;
		}

		public Object getLatLngForSite(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			if(objects[0] != null) {
				ResourceContext resource = ResourceAPI.getResource((long)objects[0]);
				if(resource != null) {
					SiteContext site = SpaceAPI.getSiteSpace(resource.getSiteId());
					if(site != null && site.getLocation() != null) {
						String mapsLocation = "https://www.google.com/maps?saddr=My+Location&daddr="+ site.getLocation().getLat() + "," + site.getLocation().getLng();
						return mapsLocation;
					}
				}
			}
			return null;
		}


		public Object encodeUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			if(objects[0] != null) {
				return (URLEncoder.encode((String)objects[0], "UTF-8").replace("+", "%20"));
			}
			return null;
		}

		public Object exportURLAsFile(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			String url = (String) objects[0];
			long fileId = PdfUtil.exportUrlAsFileId(url.toString(), null, FileFormat.PDF);
			return fileId;
		}

		public Object updateStatus(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			Map<String,Object> record = (Map<String,Object>) objects[0];
			String moduleName = (String) objects[1];
			String statusName = (String) objects[2];
			FacilioModule module = modBean.getModule(moduleName);
			FacilioStatus status = TicketAPI.getStatus(module, statusName);

			FacilioChain chain = TransactionChainFactory.getHistoryUpdateChain();
			FacilioContext facilioContext = chain.getContext();
			facilioContext.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);

			if (module.isCustom()) {
				String activityContext = FacilioConstants.ContextNames.CUSTOM_ACTIVITY;
				Constants.setActivityContext(facilioContext, activityContext);
			}

			StateFlowRulesAPI.updateState(FieldUtil.getAsBeanFromMap(record, ModuleBaseWithCustomFields.class), modBean.getModule(moduleName), status, false, facilioContext);
			chain.execute();
			return null;
		}

		public Object updateOrgInfo(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			String name = (String) objects[0];
			String value = String.valueOf(objects[1]);
			CommonCommandUtil.updateOrgInfo(name, value);

			return null;

		}

		public Object updateVendorPortalAccess(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			VendorContactContext contact = (VendorContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, Long.valueOf(objects[0].toString()));
			contact.setIsVendorPortalAccess(true);
			Map<String, Long> rolesMap = new HashMap<String, Long>();
			rolesMap.put("vendor", Long.valueOf(objects[1].toString()));
			contact.setRolesMap(rolesMap);
			FacilioChain c = TransactionChainFactory.updateVendorContactAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(contact));

			c.execute();

			return null;
		}

		public Object revokeVendorPortalAccess(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {


			VendorContactContext contact = (VendorContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, Long.valueOf(objects[0].toString()));
			contact.setIsVendorPortalAccess(false);
			Map<String, Long> rolesMap = new HashMap<String, Long>();
			rolesMap.put("vendor", Long.valueOf(objects[1].toString()));
			contact.setRolesMap(rolesMap);
			FacilioChain c = TransactionChainFactory.updateVendorContactAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(contact));

			c.execute();

			return null;
		}

		public Object updateTenantPortalAccess(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			TenantContactContext contact = (TenantContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, Long.valueOf(objects[0].toString()));
			contact.setIsTenantPortalAccess(true);
			Map<String, Long> rolesMap = new HashMap<String, Long>();
			rolesMap.put("tenant", Long.valueOf(objects[1].toString()));
			contact.setRolesMap(rolesMap);
			FacilioChain c = TransactionChainFactory.updateTenantContactAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(contact));

			c.execute();

			return null;
		}

		public Object revokeTenantPortalAccess(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {


			TenantContactContext contact = (TenantContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, Long.valueOf(objects[0].toString()));
			contact.setIsTenantPortalAccess(false);
			Map<String, Long> rolesMap = new HashMap<String, Long>();
			rolesMap.put("tenant", Long.valueOf(objects[1].toString()));
			contact.setRolesMap(rolesMap);
			FacilioChain c = TransactionChainFactory.updateTenantContactAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(contact));

			c.execute();

			return null;
		}

		public Object addInviteRecordViaV3Chain(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
			List records = (List) objects[1];
			List<InviteVisitorContextV3> inviteRecords = new ArrayList<>();
			for(Object rec:records) {
				HashMap record = (HashMap<String, Object>)rec;
				InviteVisitorContextV3 invite = new InviteVisitorContextV3();
				if(record.get("expectedCheckInTime") != null) {
					invite.setExpectedCheckInTime((Long)record.get("expectedCheckInTime"));
				}
				if(record.get("formId") != null) {
					invite.setFormId((Long)record.get("formId"));
				}
				if(record.get("host") != null) {
					HashMap hostmap = (HashMap)record.get("host");
					V3PeopleContext host = new V3PeopleContext();
					if(hostmap.get("id") != null) {
						host.setId((Long)hostmap.get("id"));
						invite.setHost(host);
					}
				}
				if(record.get("visitedSpace") != null) {
					HashMap hostmap = (HashMap)record.get("visitedSpace");
					if(hostmap.get("id") != null) {
						BaseSpaceContext space = new BaseSpaceContext((Long)hostmap.get("id"));
						space.setId((Long)hostmap.get("id"));
						invite.setVisitedSpace(space);

					}
				}
				if(record.get("visitorEmail") != null) {
					invite.setVisitorEmail(record.get("visitorEmail").toString());
				}
				if(record.get("visitorName") != null) {
					invite.setVisitorName(record.get("visitorName").toString());
				}
				if(record.get("visitorPhone") != null) {
					invite.setVisitorPhone(record.get("visitorPhone").toString());
				}
				if(record.get("visitorType") != null) {
					HashMap hostmap = (HashMap)record.get("visitorType");
					VisitorTypeContext visitor = new VisitorTypeContext();
					if(hostmap.get("id") != null) {
						visitor.setId((Long)hostmap.get("id"));
						invite.setVisitorType(visitor);
					}
				}
				if(record.get("purposeOfVisit") != null) {
					Long pov = (Long)record.get("purposeOfVisit");
					invite.setPurposeOfVisit(pov.intValue());
				}
				if(record.get("vendor") != null) {
					Long vendorId = (Long)record.get("vendor");
					VendorContext vendor = VendorsAPI.getVendors(Collections.singletonList(vendorId)).get(0);
					invite.setVendor( vendor);
				}
				if(record.get("expectedCheckOutTime") != null) {
					invite.setExpectedCheckOutTime((Long)record.get("expectedCheckOutTime"));
				}
				if(record.get("workorders") != null) {
					Map woMap = new HashMap<String, Object>();
					woMap.put("workorders",record.get("workorders"));
					invite.setData(woMap);
				}
				inviteRecords.add(invite);
			}
			FacilioModule module = ChainUtil.getModule(objects[0].toString());
			V3Config v3Config = ChainUtil.getV3Config(module);
			FacilioChain createRecordChain = ChainUtil.getCreateChain(module.getName());
			FacilioContext addAnswerContext = createRecordChain.getContext();
			Class beanClass = ChainUtil.getBeanClass(v3Config, module);

			Constants.setV3config(addAnswerContext, v3Config);
			Constants.setModuleName(addAnswerContext, module.getName());
			addAnswerContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
			addAnswerContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
			addAnswerContext.put(Constants.BEAN_CLASS, beanClass);
			Map<String, List> recordMap = new HashMap<>();
			recordMap.put(module.getName(), inviteRecords);
			addAnswerContext.put(Constants.RECORD_MAP, recordMap);

			createRecordChain.execute();

			return null;
		}

		public Object tagAssetAsRotating(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			V3AssetContext asset =  V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET, Long.valueOf(objects[0].toString()),V3AssetContext.class);
			List<V3AssetContext> assets = new ArrayList<>();
			assets.add(asset);
			FacilioChain c = TransactionChainFactoryV3.getTagAssetASRotatingChainV3();
			c.getContext().put(FacilioConstants.ContextNames.RECORD, assets);
			c.execute();

			return null;
		}

		public Object getBlobFile(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}

			String url = (String) objects[0];

			Map<String,String> params = (Map<String, String>) objects[1];
			Map<String,String> headers = (Map<String, String>) objects[2];

			String fileName = (String) objects[3];
			String type = (String) objects[4];

			return FacilioHttpUtils.doHttpGetWithFileResponse(url, headers, params, fileName, type);
		}

		public Object getDelegations(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {


			FacilioChain chain = ReadOnlyChainFactory.getAllMyDelegationChain();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.ONLY_MY_DELEGATION, false);
//		        context.put(FacilioConstants.ContextNames.APP_ID, appId);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.USER_DELEGATION);

			if(objects.length > 0) {
				Map<String,Object> filterJSON = (Map<String, Object>) objects[0];
				context.put(FacilioConstants.ContextNames.FILTERS, filterJSON);
			}

			chain.execute();

			return FieldUtil.getAsMapList((List<DelegationContext>) context.get(FacilioConstants.ContextNames.DELEGATION_LIST) ,DelegationContext.class);

		}

		public Object postWithFiles(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);

			String url = (String) objects[0];

			Map<String,String> params = null;
			Map<String,String> headers = null;
			Map<String,File> files = null;
			if(objects.length > 1) {
				headers = (Map<String, String>) objects[1];
				if(objects.length > 2) {
					params = (Map<String, String>) objects[2];
					if(objects.length > 3) {
						Map<String,Long> fileIdMap = (Map<String,Long>) objects[3];

						if(fileIdMap != null) {
							files = new HashMap<String, File>();

							FileStore fs = FacilioFactory.getFileStore();

							for(Entry<String, Long> set : fileIdMap.entrySet()) {
								FileInfo fileInfo = fs.getFileInfo(set.getValue());
								InputStream ipStream = fs.readFile(set.getValue());
								File file = new File(fileInfo.getFileName());
								FileUtils.copyInputStreamToFile(ipStream, file);
								files.put(set.getKey(), file);
							}
						}
					}
				}

			}

			String res = FacilioHttpUtilsFW.doHttpPost(url, headers, params, null,files,-1);

			return res;
		};


		public Object calculateHistoryForFormula(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);

			FacilioChain historicalCalculation = TransactionChainFactory.historicalFormulaCalculationChain();
			FacilioContext context = historicalCalculation.getContext();
			context.put(FacilioConstants.ContextNames.FORMULA_FIELD, (Long) objects[0]);
			context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange((Long) objects[1], (Long) objects[2]));
			context.put(FacilioConstants.ContextNames.RESOURCE_LIST, (List<Long>) objects[3]);
			context.put(FacilioConstants.ContextNames.IS_INCLUDE,(Boolean) objects[4]);
			context.put(FacilioConstants.OrgInfoKeys.CALCULATE_VM_THROUGH_FORMULA,(Boolean) objects[5]);
			context.put(FacilioConstants.ContextNames.SKIP_OPTIMISED_WF, (Boolean) objects[6]);

			historicalCalculation.execute();

			return "Formula calculation scheduled";
		};

		public Object getAudiencePeople(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);
			Object ids = objects[0];
			List<Long> audienceIds = new ArrayList<>();
			if(ids instanceof Long) {
				audienceIds.add((Long) ids);
			}
			else{
				audienceIds = (List<Long>) objects[0];
			}
			Long page = (Long) objects[1];
			Long perPage = (Long) objects[2];

			if(CollectionUtils.isNotEmpty(audienceIds)){
				List<V3PeopleContext> peopleList = CommunityFeaturesAPI.getAudienceSharing(audienceIds,page,perPage);
				if(CollectionUtils.isNotEmpty(peopleList)) {
					List<Map<String, Object>> recMapList = FieldUtil.getAsMapList(peopleList, V3PeopleContext.class);
					if (CollectionUtils.isNotEmpty(recMapList)) {
						return recMapList;
					}
				}
			}
			return "No People";
		}

		public Object getPortalDomainUrl(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);

			Integer id= Integer.parseInt((objects[0])+"");
			AppDomain.AppDomainType type=AppDomain.AppDomainType.valueOf(id);

			List<AppDomain> appDomains = null;

			switch (type) {
				case TENANT_PORTAL: {
					appDomains = IAMAppUtil.getAppDomain(AppDomain.AppDomainType.TENANT_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}
				case VENDOR_PORTAL: {
					appDomains = IAMAppUtil.getAppDomain(AppDomain.AppDomainType.VENDOR_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}
				case CLIENT_PORTAL: {
					appDomains = IAMAppUtil.getAppDomain(AppDomain.AppDomainType.CLIENT_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}
				case SERVICE_PORTAL:{
					appDomains = IAMAppUtil.getAppDomain(AppDomain.AppDomainType.SERVICE_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}
				case EMPLOYEE_PORTAL:{
					appDomains = IAMAppUtil.getAppDomain(AppDomain.AppDomainType.EMPLOYEE_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}

				default:
					break;
			}

			StringBuilder appUrl;
			if(appDomains!=null && !appDomains.isEmpty()){
				appUrl = new StringBuilder(SSOUtil.getProtocol())
						.append("://")
						.append(appDomains.get(0).getDomain());
			}
			else{
				appUrl=new StringBuilder(SSOUtil.getProtocol())
						.append("://")
						.append("maintenance");
			}
			return appUrl;
		}

	private void checkParam(Object... objects) throws Exception {
		if (objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
