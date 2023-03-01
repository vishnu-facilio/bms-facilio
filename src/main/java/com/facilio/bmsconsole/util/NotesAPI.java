package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.CommentSharingContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NotesAPI {
	
	public static List<NoteContext> fetchNotes(long parentId, String moduleName) throws Exception {
		List<NoteContext> noteListContext = getListBuilder(parentId, moduleName).get();
		return getNotes(Collections.singletonList(parentId), moduleName, noteListContext);
	}
	
	
	public static long fetchNotesCount(long parentId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<NoteContext> builder = getListBuilder(parentId, moduleName);
		builder.select(FieldFactory.getCountField(modBean.getModule(moduleName)));
		List<Map<String, Object>> props = builder.getAsProps();
		long count = 0;
		if (props != null && !props.isEmpty()) {
			count = (long) props.get(0).get("count");
		}
		return count;
	}
	
	public static List<NoteContext> getNotes (List<Long> parentIds, String moduleName, List<NoteContext> noteListContext) throws Exception {
		List<Long> ids = noteListContext.stream().filter(note -> note.getCreatedBy() != null).map(note -> note.getCreatedBy().getId()).collect(Collectors.toList());
		if (ids.size() > 0) {
			List<User> userList = AccountUtil.getUserBean().getUsers(null, false, true, ids);
			Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), 
											(prevValue, curValue) -> { return prevValue; }));
			if (userList != null) {
				for (NoteContext notes : noteListContext) {
					if (notes.getCreatedBy() != null) {
						notes.setCreatedBy(userMap.get(notes.getCreatedBy().getId()));
					}
				}
			}
			for (NoteContext notess : noteListContext) {
				if (notess.getCreatedBy() != null) {
					notess.setCreatedBy(userMap.get(notess.getCreatedBy().getId()));
				}
			}
		}
		
		
		return noteListContext;	
		
	}
	
	private static SelectRecordsBuilder<NoteContext> getListBuilder (long parentId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		FacilioModule commentsSharingModule = ModuleFactory.getCommentsSharingModule();
		ApplicationContext currentApp = AccountUtil.getCurrentApp();

		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<NoteContext> selectBuilder = new SelectRecordsBuilder<NoteContext>()
				.select(fields)
				.module(module)
				.beanClass(NoteContext.class)
				.maxLevel(0);

		if (currentApp != null && currentApp.getAppCategoryEnum().equals(ApplicationContext.AppCategory.PORTALS)) {
			selectBuilder.leftJoin(commentsSharingModule.getTableName())
					.on(module.getTableName() + ".ID=" + commentsSharingModule.getTableName() + ".PARENT_ID AND "
							+ module.getTableName() + ".MODULEID=" + commentsSharingModule.getTableName() + ".PARENT_MODULE_ID"
					+" AND " +module.getTableName() + ".ORGID=" + commentsSharingModule.getTableName() + ".ORGID");
		}

		if (parentId > 0) {
			Criteria criteria = new Criteria();
			if (fieldMap.containsKey("parentId")) {
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS));
			}

			if (fieldMap.containsKey("parent")) {
				criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), String.valueOf(parentId), NumberOperators.EQUALS));
			}

			if (!criteria.isEmpty()) {
				selectBuilder.andCriteria(criteria);
			}
		}

		Criteria notifyRequesterCriteria = null;
		Condition portalSharingCondition = null;
		if (AccountUtil.getCurrentUser().isPortalUser() && currentApp != null && !currentApp.getLinkName().equals(ApplicationLinkNames.VENDOR_PORTAL_APP)) {
			notifyRequesterCriteria = new Criteria();
			notifyRequesterCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("createdBy"), AccountUtil.getCurrentUser().getId() + "", NumberOperators.EQUALS));
			if (!moduleName.equalsIgnoreCase("insurancenotes") && fieldMap.get("notifyRequester") != null) {
				notifyRequesterCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("notifyRequester"), String.valueOf(true), BooleanOperators.IS));
			}
		}
		if (currentApp != null && currentApp.getAppCategoryEnum().equals(ApplicationContext.AppCategory.PORTALS)) {
			List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(commentsSharingModule);
			Map<String, FacilioField> sharingFieldMap = FieldFactory.getAsMap(allFields);
			portalSharingCondition = CriteriaAPI.getCondition(sharingFieldMap.get("appId"), String.valueOf(AccountUtil.getCurrentApp().getId()), NumberOperators.EQUALS);
		}
		if (notifyRequesterCriteria != null && portalSharingCondition != null) {
			Criteria criteria = new Criteria();
			criteria.andCriteria(notifyRequesterCriteria);
			criteria.addOrCondition(portalSharingCondition);
			selectBuilder.andCriteria(criteria);
		} else if (notifyRequesterCriteria != null) {
			selectBuilder.andCriteria(notifyRequesterCriteria);
		} else if (portalSharingCondition != null) {
			selectBuilder.andCondition(portalSharingCondition);
		}
		return selectBuilder;
	}

	public static List<NoteContext> fetchNote (List<Long> parentIds, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		StringJoiner ids = new StringJoiner(",");
		parentIds.stream().forEach(f -> ids.add(String.valueOf(f)));
		
		SelectRecordsBuilder<NoteContext> selectBuilder = new SelectRecordsBuilder<NoteContext>()
				.select(fields)
				.module(module)
				.beanClass(NoteContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), ids.toString(), NumberOperators.EQUALS));
		
		List<NoteContext> props = selectBuilder.get();

		return getNotes(parentIds, moduleName, props);
	}
	public static NoteContext getNoteContext (long noteId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<NoteContext> selectBuilder = new SelectRecordsBuilder<NoteContext>()
				.select(fields)
				.module(module)
				.beanClass(NoteContext.class)
				.maxLevel(0);
		selectBuilder.andCondition(CriteriaAPI.getIdCondition(noteId, module));
		NoteContext props = selectBuilder.fetchFirst();
		if(props!=null)
		{
		return props;
		}
		return null;
	}
	
	public static void updateNotesCount(Collection<Long> parentIds, String parentModule, String moduleString) throws Exception {
		if (StringUtils.isNoneEmpty(parentModule) && CollectionUtils.isNotEmpty(parentIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField noOfNotesField = modBean.getField("noOfNotes", parentModule);
			if (noOfNotesField == null) {
				return;
			}
			FacilioModule pModule = modBean.getModule(parentModule);
			FacilioModule module = modBean.getModule(moduleString);
			
			FacilioField parentIdField = modBean.getField("parentId", moduleString);
	
			List<FacilioField> fields = new ArrayList<>();
			fields.add(parentIdField);
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.groupBy(parentIdField.getCompleteColumnName())
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(parentIdField, parentIds, NumberOperators.EQUALS));
			
			List<Map<String, Object>> list = select.get();
			for (Map<String, Object> map : list) {
				long id = ((Number) map.get("parentId")).longValue();
				int noOfNotes = ((Number) map.get("count")).intValue();
				
				Map<String, Object> updateMap = new HashMap<>();
				updateMap.put("noOfNotes", noOfNotes);
				
				UpdateRecordBuilder<ModuleBaseWithCustomFields> updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
						.module(pModule)
						.fields(Collections.singletonList(noOfNotesField))
//						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(tModule))
						.andCondition(CriteriaAPI.getIdCondition(id, pModule))
						;
				
				updateRecordBuilder.updateViaMap(updateMap);
			}
		}
	}

	public static List<CommentSharingContext> getNoteSharing(long noteId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		FacilioModule commentsSharingModule = ModuleFactory.getCommentsSharingModule();
		List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(commentsSharingModule);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(allFields)
				.table(commentsSharingModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(noteId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		List<CommentSharingContext> commentsSharingContexts = null;
		if (props != null && !props.isEmpty()) {
			commentsSharingContexts = FieldUtil.getAsBeanListFromMapList(props, CommentSharingContext.class);
		}
		return commentsSharingContexts;
	}

	public static void addCommentSharing(String moduleName,long parentId, List<String> linkNames) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule notesModule = modBean.getModule(moduleName);
		FacilioModule commentsSharingModule = ModuleFactory.getCommentsSharingModule();
		List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(commentsSharingModule);
		Map<String, FacilioField> sharingFieldMap = FieldFactory.getAsMap(allFields);
		List<CommentSharingContext> addCommentSharingList = new ArrayList<>();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(commentsSharingModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(sharingFieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(sharingFieldMap.get("parentModuleId"), String.valueOf(notesModule.getModuleId()), NumberOperators.EQUALS));
		builder.delete();
			for (String link : linkNames) {
				CommentSharingContext commentSharing = new CommentSharingContext();
				commentSharing.setParentId(parentId);
				commentSharing.setParentModuleId(notesModule.getModuleId());
				commentSharing.setAppId(ApplicationApi.getApplicationIdForLinkName(link));
				commentSharing.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				if(commentSharing.getAppId()>0) {
					addCommentSharingList.add(commentSharing);
				}
			}
			if(addCommentSharingList != null && !addCommentSharingList.isEmpty()) {
				InsertRecordBuilder<CommentSharingContext> commentsSharingBuilder = new InsertRecordBuilder<CommentSharingContext>()
						.module(commentsSharingModule)
						.fields(allFields);
				commentsSharingBuilder.addRecords(addCommentSharingList);
				commentsSharingBuilder.save();
			}
	}
}
