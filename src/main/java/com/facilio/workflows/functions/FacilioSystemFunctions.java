package com.facilio.workflows.functions;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.RelRecord;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
@Log4j
@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.SYSTEM_FUNCTION)
public class FacilioSystemFunctions {
	private int requiredParams;
	/*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated EMails of Role Members
	 */
	public Object getRoleEmails(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		LOGGER.debug("Role Mail params : "+Arrays.toString(objects));
		if (!checkParams(objects) ) {
			return "";
		}

		List<User> users = getUsersFromRoleAndResource(objects);
		LOGGER.debug("Role Mail users : "+users);
		if (users != null && !users.isEmpty()) {
			String emails = users.stream()
					.filter(u -> u.getEmail() != null && !u.getEmail().isEmpty())
					.map(User::getEmail)
					.collect(Collectors.joining(","));
			LOGGER.debug("Role Emails : "+emails);
			return emails;
		}

		return "";
	}

    /*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated Phone numbers of Role Members
	 */
	public Object getRolePhone(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		if ( !checkParams(objects) ) {
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

	/*
	 * @param roleId Role ID
	 * @param resourceId Resource ID
	 * @return Comma separated OUIDs of Role Members
	 */
	public Object getRoleOuids(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub
		if ( !checkParams(objects) ) {
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

	public Object getAsMap(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		return FieldUtil.getAsProperties(objects[0]);
	}

	public Object getAudienceEmail(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		if ( !checkParams(objects) ) {
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

	public Object getAnnouncementAttachmentUrls(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		if ( !checkParams(objects) ) {
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

	public Object getAnnouncementAttachmentNames(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		if ( !checkParams(objects) ) {
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
}
