package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.NoteAction;
import com.facilio.bmsconsole.commands.FetchCommentAttachmentsCommand;
import com.facilio.bmsconsole.commands.FetchCommentMentionsCommand;
import com.facilio.bmsconsole.commands.FetchCommentSharingCommand;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.CommentMentionContext;
import com.facilio.bmsconsole.context.CommentSharingContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NotesAPI {
	
	public static List<NoteContext> fetchNotes(long parentId,long parentNoteId, String moduleName,Boolean onlyFetchParentNotes) throws Exception {
		List<NoteContext> noteListContext = getListBuilder(parentId,parentNoteId, moduleName, onlyFetchParentNotes).get();
		return getNotes(Collections.singletonList(parentId), moduleName, noteListContext);
	}

	public static NoteContext getLastReply(long parentId, long parentNoteId, String moduleName) throws Exception {
		NoteContext lastReply = fetchLastReply(parentId,parentNoteId, moduleName);
		if(lastReply == null){
			return null;
		}
		NoteContext note = fetchNoteSubordinates(lastReply,moduleName);
		return note;
	}

	private static NoteContext fetchLastReply(long parentId, long parentNoteId, String moduleName) throws Exception {
		SelectRecordsBuilder<NoteContext> selectBuilder = getListBuilder(parentId, parentNoteId, moduleName, false);
		ModuleBean modBean = Constants.getModBean();
		SupplementRecord createdByField = (SupplementRecord) modBean.getField("createdBy", moduleName);
		if(createdByField != null){
			selectBuilder.fetchSupplement(createdByField);
		}
		FacilioField createdTimeField = modBean.getField("createdTime", moduleName);
		if(createdTimeField == null){
			return null;
		}
		selectBuilder.orderBy(createdTimeField.getColumnName() + " DESC");
		return selectBuilder.fetchFirst();
	}
	private static NoteContext fetchNoteSubordinates(NoteContext note, String moduleName) throws Exception {
		FacilioChain chain = new FacilioChain(true);
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.NOTE_LIST,Collections.singletonList(note));
		context.put(FacilioConstants.ContextNames.NEED_COMMENT_SHARING,false);
		context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
		chain.addCommand(new FetchCommentMentionsCommand());
		chain.addCommand(new FetchCommentSharingCommand());
		chain.addCommand(new FetchCommentAttachmentsCommand());
		chain.execute();
		List<NoteContext> result = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
		return result.get(0);
	}
	
	
	public static long fetchNotesCount(long parentId,long parentNoteId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<NoteContext> builder = getListBuilder(parentId,parentNoteId, moduleName,false);
		builder.select(FieldFactory.getCountField(modBean.getModule(moduleName)));
		if(parentNoteId > 0){
			builder.groupBy("PARENT_NOTE");
		}
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
	
	private static SelectRecordsBuilder<NoteContext> getListBuilder (long parentId,long parentNoteId, String moduleName,Boolean onlyFetchParentNotes) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		FacilioModule commentsSharingModule = ModuleFactory.getCommentsSharingModule();
		ApplicationContext currentApp = AccountUtil.getCurrentApp();
		Boolean isReply = false;
		if(parentNoteId > 0){
			isReply = true;
		}

		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<NoteContext> selectBuilder = new SelectRecordsBuilder<NoteContext>()
				.select(fields)
				.module(module)
				.beanClass(NoteContext.class)
				.maxLevel(0);

		if (currentApp != null && currentApp.getAppCategoryEnum().equals(ApplicationContext.AppCategory.PORTALS) && !isReply) {
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

		if (isReply) {
			Criteria criteria = new Criteria();
			if(fieldMap.containsKey("parentNote")) {
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentNote"), String.valueOf(parentNoteId), NumberOperators.EQUALS));
			}
			if (!criteria.isEmpty()) {
				selectBuilder.andCriteria(criteria);
			}
		}
		if(onlyFetchParentNotes){
			Criteria criteria = new Criteria();
			if(fieldMap.containsKey("parentNote")) {
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentNote"), "0" ,NumberOperators.LESS_THAN_EQUAL));
				criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("parentNote"), CommonOperators.IS_EMPTY));
			}
			if (!criteria.isEmpty()) {
				selectBuilder.andCriteria(criteria);
			}
		}

		Criteria notifyRequesterCriteria = null;
		Condition portalSharingCondition = null;
		if (AccountUtil.getCurrentUser().isPortalUser() && currentApp != null && !currentApp.getLinkName().equals(ApplicationLinkNames.VENDOR_PORTAL_APP) && !isReply) {
			notifyRequesterCriteria = new Criteria();
			notifyRequesterCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("createdBy"), AccountUtil.getCurrentUser().getId() + "", NumberOperators.EQUALS));
			if (!moduleName.equalsIgnoreCase("insurancenotes") && fieldMap.get("notifyRequester") != null) {
				notifyRequesterCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("notifyRequester"), String.valueOf(true), BooleanOperators.IS));
			}
		}
		if (currentApp != null && currentApp.getAppCategoryEnum().equals(ApplicationContext.AppCategory.PORTALS) && !isReply) {
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
	public static List<CommentMentionContext> getNoteMentions(long noteId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		FacilioModule commentMentionsModule = ModuleFactory.getCommentMentionsModule();
		List<FacilioField> allFields = FieldFactory.getCommentMentionsFields(commentMentionsModule);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(allFields)
				.table(commentMentionsModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(noteId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		List<CommentMentionContext> commentsMentionsContexts = null;
		if (props != null && !props.isEmpty()) {
			commentsMentionsContexts = FieldUtil.getAsBeanListFromMapList(props, CommentMentionContext.class);
		}
		return commentsMentionsContexts;

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
	public static void deleteCommentSharing(NoteContext note, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule module = modBean.getModule(moduleName);
		FacilioModule commentsSharingModule = ModuleFactory.getCommentsSharingModule();

		List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(commentsSharingModule);
		Map<String, FacilioField> sharingFieldMap = FieldFactory.getAsMap(allFields);

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(commentsSharingModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(sharingFieldMap.get("parentId"), String.valueOf(note.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(sharingFieldMap.get("parentModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		builder.delete();
	}

	public static void deleteCommentMentions(NoteContext note, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule module = modBean.getModule(moduleName);
		FacilioModule commentMentions = ModuleFactory.getCommentMentionsModule();

		List<FacilioField> allFields = FieldFactory.getCommentMentionsFields(commentMentions);
		Map<String, FacilioField> mentionsFieldMap = FieldFactory.getAsMap(allFields);

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(commentMentions.getTableName())
				.andCondition(CriteriaAPI.getCondition(mentionsFieldMap.get("parentId"), String.valueOf(note.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(mentionsFieldMap.get("parentModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		builder.delete();
	}

	public static void deleteCommentAttachments(NoteContext note, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule module = modBean.getModule(moduleName);
		FacilioModule commentAttachments = modBean.getModule(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS);

		List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS);
		Map<String, FacilioField> attachmentsFieldMap = FieldFactory.getAsMap(allFields);

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(commentAttachments.getTableName())
				.andCondition(CriteriaAPI.getCondition(attachmentsFieldMap.get("parent"), String.valueOf(note.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(attachmentsFieldMap.get("commentModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		builder.delete();
	}

	public static ArrayList<CommentSharingContext> fetchDefaultCommentSharingApps() throws Exception {
		ArrayList<CommentSharingContext> defaultSharingApps = new ArrayList<>();
		FacilioModule sharingPreferenceModule = ModuleFactory.getCommentsSharingPreferenceModule();
		List<FacilioField> fields = FieldFactory.getCommentsSharingPreferenceFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(sharingPreferenceModule.getTableName());
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()){
			for (Map<String, Object> prop:props) {
				Object appId = prop.get(FacilioConstants.ContextNames.APP_ID);
				if(appId != null){
					CommentSharingContext sharingPreference = new CommentSharingContext();
					sharingPreference.setAppId( (Long) appId);
					defaultSharingApps.add(sharingPreference);
				}
			}
		}
		return defaultSharingApps;
	}

	public static void deleteDefaultCommentSharingApps() throws Exception {
		FacilioModule sharingPreferenceModule = ModuleFactory.getCommentsSharingPreferenceModule();
		FacilioField orgIdField = AccountConstants.getOrgIdField();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(sharingPreferenceModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(orgIdField, Collections.singleton(orgId), NumberOperators.EQUALS));
		builder.delete();
	}

	public static void AddDefaultCommentSharingApps( List<CommentSharingContext> sharingInfos) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, SQLException {
		FacilioModule sharingModule = ModuleFactory.getCommentsSharingPreferenceModule();
		List<FacilioField> fields = FieldFactory.getCommentsSharingPreferenceFields();
		if(sharingInfos == null || sharingInfos.isEmpty()){
			return;
		}
		List<CommentSharingContext> props = sharingInfos.stream().collect(Collectors.toList());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(sharingModule.getTableName())
				.fields(fields);
		insertBuilder.addRecords(FieldUtil.getAsMapList(props,CommentSharingContext.class));
		insertBuilder.save();
	}


	public static List<NoteContext> getAllNotes(String notesModuleName, Long recordId, String parentModuleName) throws Exception {
		NoteAction noteAction = new NoteAction();
		noteAction.setModule(notesModuleName);
		noteAction.setParentId(recordId);
		noteAction.setParentModuleName(parentModuleName);
		noteAction.getNotesList();
		return noteAction.getNotes();
	}
}
