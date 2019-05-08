package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
		List<Long> ids = noteListContext.stream().map(note -> note.getCreatedBy().getId()).collect(Collectors.toList());
		if (ids.size() > 0) {
			List<User> userList = AccountUtil.getUserBean().getUsers(null, ids);
			Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), 
											(prevValue, curValue) -> { return prevValue; }));
			if (userList != null) {
				for (NoteContext notess : noteListContext) {
					notess.setCreatedBy(userMap.get(notess.getCreatedBy().getId()));
				}
			}
			for (NoteContext notess : noteListContext) {
				notess.setCreatedBy(userMap.get(notess.getCreatedBy().getId()));
			}
		}
		
		
		return noteListContext;	
		
	}
	
	private static SelectRecordsBuilder<NoteContext> getListBuilder (long parentId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<NoteContext> selectBuilder = new SelectRecordsBuilder<NoteContext>()
				.select(fields)
				.module(module)
				.beanClass(NoteContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS))
				.maxLevel(0);
		
		long portalID =  AccountUtil.getCurrentUser().getPortalId();
		if (portalID > 0) {
			Criteria cri = new Criteria();
			cri.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("createdBy"), AccountUtil.getCurrentUser().getId() + "", NumberOperators.EQUALS));
			cri.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("notifyRequester"), String.valueOf(true), BooleanOperators.IS));
			
			selectBuilder.andCriteria(cri);
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
	
	public static void updateNotesCount(Collection<Long> parentIds, String ticketModule, String moduleString) throws Exception {
		if (StringUtils.isNoneEmpty(ticketModule) && CollectionUtils.isNotEmpty(parentIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField noOfNotesField = modBean.getField("noOfNotes", ticketModule);
			FacilioModule tModule = modBean.getModule(ticketModule);
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
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(parentIdField, parentIds, NumberOperators.EQUALS));
			
			List<Map<String, Object>> list = select.get();
			for (Map<String, Object> map : list) {
				long id = ((Number) map.get("parentId")).longValue();
				int noOfNotes = ((Number) map.get("count")).intValue();
				
				Map<String, Object> updateMap = new HashMap<>();
				updateMap.put("noOfNotes", noOfNotes);
				
				UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<WorkOrderContext>()
						.module(tModule)
						.fields(Collections.singletonList(noOfNotesField))
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(tModule))
						.andCondition(CriteriaAPI.getIdCondition(id, tModule))
						;
				
				updateRecordBuilder.updateViaMap(updateMap);
			}
		}
	}

}
