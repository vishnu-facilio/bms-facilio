package com.facilio.workflows.functions;

import java.lang.reflect.Field;
import java.util.*;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;

import java.util.stream.Collectors;

import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ResourceAPI;

public enum FacilioSystemFunctions implements FacilioWorkflowFunctionInterface {
	
	/*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated EMails of Role Members 
	 */
	ROLE_EMAILS (1, "getRoleEmails", 2) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			LOGGER.info("Role Mail params : "+Arrays.toString(objects));
			if ( !ROLE_EMAILS.checkParams(objects) ) {
				return "";
			}
			
			List<User> users = getUsersFromRoleAndResource(objects);
			LOGGER.info("Role Mail users : "+users);
			if (users != null && !users.isEmpty()) {
				String emails = users.stream()
								.filter(u -> u.getEmail() != null && !u.getEmail().isEmpty())
								.map(User::getEmail)
								.collect(Collectors.joining(","));
				LOGGER.info("Role Emails : "+emails);
				return emails;
			}
			
			return "";
		}
	},
	/*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated Phone numbers of Role Members 
	 */
	ROLE_PHONE (2, "getRolePhone", 2) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			if ( !ROLE_PHONE.checkParams(objects) ) {
				return "";
			}
			List<User> users = getUsersFromRoleAndResource(objects);
			
			if (users != null && !users.isEmpty()) {
				StringJoiner joiner = new StringJoiner(",");
				for (User user : users) {
					if (user.getMobile() != null && !user.getMobile().isEmpty()) {
						joiner.add(user.getMobile());
					}
					else if (user.getPhone() != null && !user.getPhone().isEmpty()) {
						joiner.add(user.getPhone());
					}
				}
				return joiner.toString();
			}
			
			return "";
		}
	},
	
	/*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated OUIDs of Role Members 
	 */
	ROLE_OUID (3, "getRoleOuids", 2) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			if ( !ROLE_OUID.checkParams(objects) ) {
				return "";
			}
			List<User> users = getUsersFromRoleAndResource(objects);
			
			if (users != null && !users.isEmpty()) {
				return users.stream()
							.map(u -> String.valueOf(u.getOuid()))
							.collect(Collectors.joining(","));
			}
			
			return "";
		}
	},
	GET_AS_PROP_MAP(4, "getAsMap", 1) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			return FieldUtil.getAsProperties(objects[0]);
		}
	},
	AUDIENCE_EMAIL (5, "getAudienceEmail", 3) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			if ( !AUDIENCE_EMAIL.checkParams(objects) ) {
				return "";
			}
			String fieldName = (String) objects[0];
			String moduleName = (String) objects[1];
			Long recordId = Long.parseLong((String) objects[2]);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			List<Long> audienceIds = new ArrayList<>();
			if(CollectionUtils.isNotEmpty(fields)){
				Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
				FacilioField audienceField = fieldsMap.get(fieldName);
				FacilioModule module = modBean.getModule(moduleName);
				if(audienceField instanceof LookupField){
					GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
							.table(module.getTableName())
							.select(Collections.singletonList(audienceField))
							.andCondition(CriteriaAPI.getIdCondition(recordId,module));
					List<Map<String, Object>> props = selectRecordBuilder.get();
					audienceIds.add((Long) props.get(0).get(fieldName));
				}
				else if(audienceField instanceof MultiLookupField){

					String relModuleName = ((MultiLookupField) audienceField).getRelModule().getName();
					List<FacilioField> relModuleFields = modBean.getAllFields(relModuleName);
					Map<String,FacilioField> relModuleFieldsMap = FieldFactory.getAsMap(relModuleFields);

					Criteria criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(relModuleFieldsMap.get("left"),Collections.singletonList(recordId),NumberOperators.EQUALS));
					List<RelRecord> relRecs = V3RecordAPI.getRecordsListWithSupplements(relModuleName, null, RelRecord.class, criteria, null, null, null, true );
					if(CollectionUtils.isNotEmpty(relRecs)){
						for(RelRecord relRecord : relRecs){
							Map<String,Object> audience = (Map<String,Object>) relRecord.getRight();
							audienceIds.add((Long)audience.get("id"));
						}
					}
				}
			}
			if(CollectionUtils.isNotEmpty(audienceIds)){
				List<String> audienceEmails = CommunityFeaturesAPI.getAudiencePeopleEmails(audienceIds);
				if(CollectionUtils.isNotEmpty(audienceEmails)){
					return StringUtils.join(audienceEmails,",");
				}
			}
			return "";
		}
	},
	GET_ANNOUNCEMENT_ATTACHMENT_URLS (5, "getAnnouncementAttachmentUrls", 1) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			if ( !GET_ANNOUNCEMENT_ATTACHMENT_URLS.checkParams(objects) ) {
				return "";
			}
			Long recordId = Long.parseLong((String) objects[0]);
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID","parentId",String.valueOf(recordId),NumberOperators.EQUALS));
			List<AttachmentContext> announcementAttachments = V3RecordAPI.getRecordsListWithSupplements("announcementattachments",null, AttachmentContext.class,criteria,null);
			List<String> fileUrls = new ArrayList<>();
			FileStore fs = FacilioFactory.getFileStore();
			if(CollectionUtils.isNotEmpty(announcementAttachments)){
				for(AttachmentContext file : announcementAttachments){
					fileUrls.add(fs.getOrgiFileUrl(file.getFileId()));
				}
				if(CollectionUtils.isNotEmpty(fileUrls)){
					return StringUtils.join(fileUrls,",");
				}
			}
			return "";
		}
	},
	GET_ANNOUNCEMENT_ATTACHMENT_NAMES (5, "getAnnouncementAttachmentNames", 1) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub

			if ( !GET_ANNOUNCEMENT_ATTACHMENT_NAMES.checkParams(objects) ) {
				return "";
			}
			Long recordId = Long.parseLong((String) objects[0]);
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID","parentId",String.valueOf(recordId),NumberOperators.EQUALS));
			List<AttachmentContext> announcementAttachments = V3RecordAPI.getRecordsListWithSupplements("announcementattachments",null, AttachmentContext.class,criteria,null);
			List<String> fileNames = new ArrayList<>();
			FileStore fs = FacilioFactory.getFileStore();
			if(CollectionUtils.isNotEmpty(announcementAttachments)){
				for(AttachmentContext file : announcementAttachments){
					fileNames.add(fs.getFileInfo(file.getFileId()).getFileName());
				}
				if(CollectionUtils.isNotEmpty(fileNames)){
					return StringUtils.join(fileNames,",");
				}
			}
			return "";
		}
	};

	private int value, requiredParams;
	private String functionName;
	private String namespace = "system";
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.SYSTEM;
	private static final Logger LOGGER = LogManager.getLogger(FacilioSystemFunctions.class.getName());
	
	FacilioSystemFunctions(int value,String functionName, int requiredParams) {
		this.value = value;
		this.functionName = functionName;
		this.requiredParams = requiredParams;
	}
	
	private boolean checkParams (Object... objects) {
		if (requiredParams > 0 && (objects == null || objects.length < requiredParams)) {
//			throw new IllegalArgumentException("Required objects are null for function : "+namespace+"."+functionName);
			return false;
		}
		
		for (int i = 0; i < requiredParams; i++) {
			if (objects[i] == null) {
//				throw new IllegalArgumentException("Required objects are null for function : "+namespace+"."+functionName);
				return false;
			}
		}
		return true;
	}

	@Override
	public abstract Object execute(Map<String, Object> globalParam, Object... objects) throws Exception;
	
	public static Map<String, FacilioSystemFunctions> getAllFunctions() {
		return SYSTEM_FUNCTIONS;
	}
	public static FacilioSystemFunctions getFacilioSystemFunction(String functionName) {
		return SYSTEM_FUNCTIONS.get(functionName);
	}
	private static final Map<String, FacilioSystemFunctions> SYSTEM_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	private static Map<String, FacilioSystemFunctions> initTypeMap() {
		Map<String, FacilioSystemFunctions> typeMap = new HashMap<>();
		for(FacilioSystemFunctions type : values()) {
			typeMap.put(type.functionName, type);
		}
		return typeMap;
	}
	
	private static List<User> getUsersFromRoleAndResource (Object... objects) throws Exception {
		long roleId = Long.parseLong(objects[0].toString());
		long resourceId = Long.parseLong(objects[1].toString());
		
		ResourceContext resource = ResourceAPI.getResource(resourceId);
		if (resource != null) {
			long spaceId = resource.getSpaceId();
			if (spaceId != -1) {
				return AccountUtil.getUserBean().getUsersWithRoleAndAccessibleSpace(roleId, spaceId);
			}
		}
		return null;
	}
}
