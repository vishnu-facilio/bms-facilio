package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class SaveSortFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		JSONObject sortObj = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		long moduleId = module.getModuleId();
		FacilioView view = ViewAPI.getView(viewName, moduleId, AccountUtil.getCurrentOrg().getOrgId());
		if (view == null) {
			view = ViewFactory.getView(module, viewName, modBean);
		}
		List<SortField> sortField = view.getSortFields();

		// TODO Handle for multiple sort columns when implemented
		if (sortObj != null && !sortObj.isEmpty()) {
			String orderByColName = (String) sortObj.get("orderBy");
			String orderType = (String) sortObj.get("orderType");
			
			boolean columnChanged = false;
			boolean orderChanged = false;
			if (sortField == null || sortField.isEmpty()) {
				columnChanged = true;
			} else {
				columnChanged = false;
				if (!sortField.get(0).getSortField().getName().equals(orderByColName)) {
					columnChanged = true;
				}
				orderChanged = false;
				if (sortField.get(0).getIsAscending()) {
					if (!"asc".equalsIgnoreCase(orderType)) { 
						orderChanged = true;
					}
				} else {
					if (!"desc".equalsIgnoreCase(orderType)) {
						orderChanged = true;
					}
				}
			}

			if (orderChanged || columnChanged) {
				if (view.getId() == -1) {
					long viewId = ViewAPI.checkAndAddView(view.getName(), moduleName, null);
					view.setId(viewId);
				}
				FacilioField field = modBean.getField(orderByColName, moduleName);
				if (field != null) {
					SortField newSortField = new SortField();
					newSortField.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
					newSortField.setFieldId(field.getFieldId());
					newSortField.setViewId(view.getId());
					newSortField.setFieldName(field.getName());
					newSortField.setIsAscending("asc".equalsIgnoreCase(orderType));
					ViewAPI.customizeViewSortColumns(view.getId(), Arrays.asList(newSortField));
					List<SortField> savedFields = ViewAPI.getSortFields(view.getId(), moduleName);
					context.put(FacilioConstants.ContextNames.SORT_FIELDS_OBJECT, savedFields);
				}
			}
		}
		return false;
	}

}
