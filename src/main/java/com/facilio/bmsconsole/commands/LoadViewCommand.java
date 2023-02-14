package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.*;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.SecurityUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.security.SecurityUtil.sanitizeSqlOrderbyParam;

public class LoadViewCommand extends FacilioCommand {

	public static final List<String> HIDDEN_VIEW_NAMES = Arrays.asList("hidden-all", "pendingapproval");
	public static final List<String> ALL_VIEW_NAMES = Arrays.asList("all", "hidden-all");

	private static final Logger LOGGER = LogManager.getLogger(LoadViewCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		long startTime = System.currentTimeMillis();
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);
		boolean isFetchCall = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_FETCH_CALL, false);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		JSONObject tempSortObj = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
		if(tempSortObj!=null) {
			String tempOrderBy = (String) tempSortObj.get("orderBy");
			String tempOrderType = (String) tempSortObj.get("orderType");
			if (!SecurityUtil.isClean(tempOrderBy, tempOrderType)) {
				throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid order clause parameter passed");
			}
		}

		if(viewName != null && !viewName.isEmpty()) {
			String parentViewName = (String) context.get(FacilioConstants.ContextNames.PARENT_VIEW);	// eg: to get default report columns
			FacilioView view = null;
			FacilioModule module = modBean.getModule(moduleName);
			long moduleId = module.getModuleId();
			if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				view = ViewAPI.getView(viewName, -1, moduleName, orgId, appId);
			} else {
				view = ViewAPI.getView(viewName, moduleId, moduleName, orgId, appId);
			}
			
			if(view == null) {
				if (ALL_VIEW_NAMES.contains(viewName)) {
					view = ViewAPI.getAllView(modBean, module, viewName);
				}

				boolean isHiddenView = false;
				if (view == null) {
					if (HIDDEN_VIEW_NAMES.contains(viewName)) {
						viewName = "all";
						isHiddenView = true;
					}

					view = ViewFactory.getView(module, viewName, modBean);
					if (view == null && parentViewName != null) {
						view = ViewFactory.getView(module, parentViewName, modBean);
					} else if (view == null) {
						String extendedModName = module.getExtendModule() == null ? StringUtils.EMPTY : module.getExtendModule().getName();
						if (extendedModName.contains("asset")) {
							view = ViewFactory.getModuleView(module, extendedModName);
						}
					}
				}
				
				if(view != null && view.getFields() != null) {
					ViewAPI.setViewFieldsProp(view.getFields(), moduleName);
					if (isHiddenView) {
						SortField sortField = RecordAPI.getDefaultSortFieldForModule(moduleName);
						if (sortField != null) {
							view.setSortFields(Collections.singletonList(sortField));
						}
					}
				}
			}
			
			if(view != null) {
				// View - Editable access
				if (view.getId() == -1) {
					view.setEditable(true);
				} else {
					try {
						checkViewEditAccess(view, orgId, appId);
					} catch (Exception e) {
						LOGGER.info("ViewEditAccess - LoadViewCommand -- Error occurred in ", e);
					}
				}
				view.setDefaultModuleFields(moduleName, parentViewName);
				Boolean overrideSorting = (Boolean) context.get(ContextNames.OVERRIDE_SORTING);
				if (overrideSorting != null && overrideSorting) {
					JSONObject sortObj = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
					SortField sortField = getQuerySortField(modBean, moduleName, sortObj);
					String sortQuery;
					if (sortField != null) {
						sortQuery = getOrderClauseForLookupTable(sortField, moduleName);			
						view.setSortFields(Collections.singletonList(sortField));
					}
					else {
						String orderByColName = (String) sortObj.get("orderBy");
						String orderType = (String) sortObj.get("orderType");
						sortQuery = orderByColName + " IS NULL," + orderByColName + " " + orderType;
					}
					context.put(FacilioConstants.ContextNames.SORTING_QUERY, sortQuery);
					
				}
				else if (view.getSortFields() != null && !view.getSortFields().isEmpty()) {
					StringBuilder orderBy = new StringBuilder();
					String prefix = "";
					for (SortField sortField : view.getSortFields()) {
						orderBy.append(prefix);
						prefix = ",";
						orderBy.append(getOrderClauseForLookupTable(sortField, moduleName));
						if(!context.containsKey(ContextNames.ORDER_TYPE)) {
							context.put(ContextNames.ORDER_TYPE, (sortField.getSortField() != null && sortField.getIsAscending()) ? "asc" : "desc");
						}
					}
					if(orderBy.length() != 0) {
						context.put(FacilioConstants.ContextNames.SORTING_QUERY, orderBy.toString());
					}
				} else if (!isFetchCall) {
					List<SortField> defaultSortFields = ColumnFactory.getDefaultSortField(moduleName);
					if (defaultSortFields != null && !defaultSortFields.isEmpty()) {
						StringJoiner joiner = new StringJoiner(",");
						for (SortField sortField : defaultSortFields) {
							joiner.add(getOrderClauseForLookupTable(sortField, moduleName));
						}
						context.put(FacilioConstants.ContextNames.SORTING_QUERY, joiner.toString());
						view.setSortFields(defaultSortFields);
					}
				}
				
				Boolean fetchDisplayNames = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_FIELD_DISPLAY_NAMES);
				if (fetchDisplayNames != null && fetchDisplayNames) {
					setFieldDisplayNames(moduleName, view);
				}
				
				context.put(FacilioConstants.ContextNames.CUSTOM_VIEW, view);
				if(view.getViewSharing() == null) {
					view.setViewSharing(SharingAPI.getSharing(view.getId(), ModuleFactory.getViewSharingModule(), SingleSharingContext.class));
				}
				
				if ((view.getFields() == null ||  view.getFields().isEmpty()) && module != null && module.isCustom()) {
					List<ViewField> viewFields = new ArrayList<>();
					if (modBean == null) {
						modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					}
					List<FacilioField> allFields = modBean.getAllFields(moduleName);
					for (FacilioField field : allFields) {
							ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
						    viewField.setField(field);
						    viewFields.add(viewField);	
					}
					if (viewFields != null) {
						List<ViewField> fieldsToRemove = new ArrayList<>();
						for(ViewField column : viewFields) {
							if (column.getName().equals("stateFlowId")) {
								fieldsToRemove.add(column);
							}
							if (module.getStateFlowEnabled() != null && !module.getStateFlowEnabled() && column.getName().equals("moduleState")) {
								fieldsToRemove.add(column);
							}
						}
						viewFields.removeAll(fieldsToRemove);
					}
					view.setFields(viewFields);
				}
			}
		}
		else {
			Boolean overrideSorting = (Boolean) context.get(ContextNames.OVERRIDE_SORTING);
			JSONObject sortObj = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
			if (overrideSorting != null && overrideSorting && sortObj != null) {
				String orderByColName = (String) sortObj.get("orderBy");
				String orderType = (String) sortObj.get("orderType");
				String sortQuery = orderByColName + " IS NULL," + orderByColName + " " + orderType;
				context.put(FacilioConstants.ContextNames.SORTING_QUERY, sortQuery);
			}
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute LoadViewCommand : "+timeTaken);
		return false;
	}

	public static long getMainApplicationId() throws Exception {
		return ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
	}

	public void checkViewEditAccess(FacilioView view, long orgId, long currAppId) throws Exception {
		User currentUser = AccountUtil.getCurrentUser();
		currAppId = currAppId != -1 ? currAppId : getMainApplicationId();
		long currUserAppId = currentUser.getApplicationId() != -1 ? currentUser.getApplicationId() : getMainApplicationId();

		Long currentUserId = currentUser.getId();
		Long currentUserRoleId = currentUser.getRoleId();
		Long superAdminUserId = AccountUtil.getOrgBean().getSuperAdmin(orgId).getOuid();
		Long ownerId = view.getOwnerId() != -1 ? view.getOwnerId() : superAdminUserId;
		Long adminRoleId = AccountUtil.getRoleBean().getRole(orgId, AccountConstants.DefaultSuperAdmin.ADMINISTRATOR).getId();

		// Role Check
		boolean isSuperAdmin = currentUser.isSuperAdmin();
		boolean isAdmin = adminRoleId.equals(currentUserRoleId);
		boolean isPrivileged = currentUser.getRole().isPrevileged() && (currAppId == currUserAppId);
		// Owner Check
		boolean isOwner = ownerId.equals(currentUserId);
		boolean isLocked = view.getIsLocked() != null ? view.getIsLocked() : false;

		view.setEditable(isSuperAdmin || isPrivileged || !isLocked || (isLocked && (isOwner || isAdmin)));
	}
	
	private String getOrderClauseForLookupTable(SortField field, String moduleName) throws Exception {
		List<Long> ids = new ArrayList<>();
		if (field.getSortField().getDataTypeEnum() == FieldType.LOOKUP) {
			long fieldID;
			if (field.getSortField().getFieldId() == -1) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField sfield = modBean.getField(field.getSortField().getName(), moduleName);
				fieldID = sfield.getFieldId();
			} else {
				fieldID = field.getSortField().getFieldId();
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Pair<String, Boolean> fieldOrdering;
			FacilioModule lookupModule = null;
			List<Map<String, Object>> props = null;
			GenericSelectRecordBuilder builder;
			FacilioField sortableField;
			
			if ((((LookupField) field.getSortField()).getSpecialType()) == null) {
			FacilioModule lookupMod = ModuleFactory.getLookupFieldsModule();
			
			
			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			
			FacilioField table = new FacilioField();
			table.setName("tableName");
			table.setDataType(FieldType.STRING);
			table.setColumnName("TABLE_NAME");
			
			FacilioField moduleID = new FacilioField();
			moduleID.setName("moduleID");
			moduleID.setDataType(FieldType.NUMBER);
			moduleID.setColumnName("MODULEID");
			
			builder = new GenericSelectRecordBuilder()
					.select(Arrays.asList(moduleID, name, table))
					.table(ModuleFactory.getLookupFieldsModule().getTableName())
					.innerJoin("Modules")
					.on("Modules.MODULEID = LookupFields.LOOKUP_MODULE_ID")
					.andCustomWhere(lookupMod.getTableName()+".FIELDID = ? AND "+lookupMod.getTableName()+".ORGID = ?", fieldID, AccountUtil.getCurrentOrg().getOrgId());
			
			props = builder.get();
			
			lookupModule = modBean.getModule((long) props.get(0).get("moduleID"));
			fieldOrdering = FieldFactory.getSortableFieldName(lookupModule.getName());
			sortableField = modBean.getField(fieldOrdering.getLeft(), lookupModule.getName());
			String order;
			if (fieldOrdering.getRight()) {
				order = field.getIsAscending() ? "asc" : "desc";
			} else {
				order = field.getIsAscending() ? "desc" : "asc";
			}
			
//			builder = new GenericSelectRecordBuilder()
//					.select(Arrays.asList(FieldFactory.getIdField()))
//					.table((String) props.get(0).get("tableName"))
//					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
//					.orderBy(sortableField.getColumnName() + " " + order);					
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.select(Arrays.asList(FieldFactory.getIdField(modBean.getModule((String) props.get(0).get("name")))))
					.moduleName((String) props.get(0).get("name"))
					.beanClass(ModuleBaseWithCustomFields.class)
					.orderBy(sortableField.getCompleteColumnName() + " " + order);		
			
			props = selectBuilder.getAsProps();
		}
			else {	
				fieldOrdering = FieldFactory.getSortableFieldName(((LookupField) field.getSortField()).getSpecialType());
				sortableField = modBean.getField(fieldOrdering.getLeft(), ((LookupField) field.getSortField()).getSpecialType());
				String order;
				if (fieldOrdering.getRight()) {
					order = field.getIsAscending() ? "asc" : "desc";
				} else {
					order = field.getIsAscending() ? "desc" : "asc";
				}
				
				FacilioModule tableNames;
				tableNames = LookupSpecialTypeUtil.getModule(((LookupField) field.getSortField()).getSpecialType());

			}
			
			if (props != null) {
			
			for (Map<String, Object> prop : props) {
				ids.add((Long) prop.get("id"));
			}
			}
		}
		else if (field.getFieldName() != null && field.getFieldName().equals("siteId")) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(ContextNames.SITE);
			FacilioField nameField = modBean.getField("name", ContextNames.RESOURCE);
			FacilioField idField = FieldFactory.getIdField(module);
			String order = field.getIsAscending() ? " asc" : " desc";
			SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>()
					.moduleName(module.getName()).beanClass(SiteContext.class)
					.select(Collections.singletonList(nameField))
					.orderBy(nameField.getColumnName() + order);
			Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
			if (scopeCriteria != null) {
				builder.andCriteria(scopeCriteria);
			}
			List<Map<String, Object>> sites = builder.getAsProps();
			if (CollectionUtils.isNotEmpty(sites)) {
				ids = sites.stream().map(site -> (long)site.get("id")).collect(Collectors.toList());
			}
		}
		String idString = String.join(",", Lists.transform(ids, Functions.toStringFunction()));
		if (idString.length() > 0) {
			String columnName = field.getSortField().getCompleteColumnName();
			return "FIELD("+columnName + "," + idString+")";
		}
		return field.getSortField().getCompleteColumnName() + " IS NULL," + field.getSortField().getCompleteColumnName() + " " + (field.getIsAscending()? "asc" : "desc");
	}
	
	private SortField getQuerySortField(ModuleBean modBean, String moduleName, JSONObject sortObj) throws Exception {
		String orderByColName = (String) sortObj.get("orderBy");
		String orderType = (String) sortObj.get("orderType");
		FacilioField field = modBean.getField(orderByColName, moduleName);
		if (field != null) {
			SortField newSortField = new SortField();
			newSortField.setFieldId(field.getFieldId());
			newSortField.setSortField(field);
			newSortField.setIsAscending("asc".equalsIgnoreCase(orderType));
			return newSortField;
		}
		return null;
	}
	

	private void setFieldDisplayNames(String moduleName, FacilioView view) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		FacilioModule module = modBean.getModule(moduleName);
		Map<String, ViewField> fieldMap = new HashMap<>();
//		if (view.getFields() != null) {
//			fieldMap = view.getFields().stream().collect(Collectors.toMap(vf -> vf.getField().getName(), Function.identity()));
//		}
		if (view.getFields() != null) {
			for(int i=0;i<view.getFields().size();i++) {
			String modulesName = null;
			String fieldsName = null;
			if (view.getFields().get(i).getField() == null && view.getFields().get(i).getName() == null) {
//				Temporary handling for Deyari
			if (view.getFields().get(i).getFieldName() != null && view.getFields().get(i).getFieldName().equals("siteId") && (moduleName.equals("workorder") || moduleName.equals("asset") || moduleName.equals("tenant") || moduleName.equals("safetyPlan"))) {
				modulesName = moduleName;
				fieldsName	= view.getFields().get(i).getFieldName();
			}
			else if (view.getFields().get(i).getFieldName() != null) {
				System.out.println("view.getFields().get(i).getFieldName()" + view.getFields().get(i));
				FacilioField systemField = FieldFactory.getSystemField(view.getFields().get(i).getFieldName(), module);
				if (systemField != null) {
					modulesName = systemField.getModule().getName();
				} else if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
					modulesName = moduleName;
				}
				fieldsName = view.getFields().get(i).getFieldName();
			}
			}
			else {
 			modulesName = view.getFields().get(i).getField().getModule().getName();
			fieldsName = view.getFields().get(i).getName();
			}
			fieldMap.put(fieldsName + modulesName, view.getFields().get(i));
			}
		}
		Map<String, String> fieldNames = null;
		if (fields != null) {
			fieldNames = new HashMap<> ();
			for(FacilioField field : fields) {
				if(field.getName().equals("tenant")) {
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
						continue;
					}
				}
				else if(field.getName().equals("safetyPlan")) {
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
						continue;
					}
				}
				String displayName;
				if (fieldMap != null && fieldMap.get(field.getName()) != null && fieldMap.get(field.getName()).getColumnDisplayName() != null) {
					displayName = fieldMap.get(field.getName()).getColumnDisplayName();
				}
				else if (view.getDefaultModuleFields() != null && view.getDefaultModuleFields().get(field.getName()) != null && view.getDefaultModuleFields().get(field.getName()).getColumnDisplayName() != null) {
					displayName = view.getDefaultModuleFields().get(field.getName()).getColumnDisplayName();
				}
				else {
					displayName = field.getDisplayName();
				}
				fieldNames.put(field.getName(), displayName);
			}
		}
		
//		Temporary handling for Deyari
		if (moduleName.equals("workorder") || moduleName.equals("safetyPlan") || moduleName.equals("asset") || moduleName.equals("tenant")) {
			FacilioField siteField = FieldFactory.getSiteIdField(module);
				fieldNames.put(siteField.getName(), siteField.getDisplayName());
				
		}
		view.setFieldDisplayNames(fieldNames);
	}

}
