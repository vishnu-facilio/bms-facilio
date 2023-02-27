package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.RoleApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrUpdateApplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ApplicationContext application = (ApplicationContext) context.get(FacilioConstants.ContextNames.APPLICATION);
		Boolean addLayout = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.ADD_APPLICATION_LAYOUT, false);

		if (application != null) {
			application.setIsDefault(false);
			if (application.getId() > 0) {
				update(FieldUtil.getAsProperties(application), ModuleFactory.getApplicationModule(), FieldFactory.getApplicationFields(), application.getId());
				context.put(FacilioConstants.ContextNames.APPLICATION_ID,application.getId());
			} else {
				add(FieldUtil.getAsProperties(application), ModuleFactory.getApplicationModule(), FieldFactory.getApplicationFields());
				ApplicationContext app = ApplicationApi.getApplicationForLinkName(application.getLinkName());
				if(addLayout != null && addLayout) {
					ApplicationLayoutContext layout = new ApplicationLayoutContext(app.getId(), ApplicationLayoutContext.AppLayoutType.DUAL, ApplicationLayoutContext.LayoutDeviceType.WEB, app.getLinkName());
					ApplicationApi.addApplicationLayout(layout);

					ApplicationLayoutContext customAppLayoutMobile = new ApplicationLayoutContext(app.getId(),
							ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.MOBILE,app.getLinkName());
					ApplicationApi.addApplicationLayout(customAppLayoutMobile);
				}

				if(app.getAppCategory() == ApplicationContext.AppCategory.FEATURE_GROUPING.getIndex() &&  !app.getIsDefault() &&
						app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex() && !app.getLinkName().equals("newapp")){
					addCustomSetupLayout(app);
				}
				addAdminRoleForApp(app);
				ApplicationApi.addDefaultScoping(app.getId());
				context.put(FacilioConstants.ContextNames.APPLICATION_ID,app.getId());
			}

		}

		return false;
	}

	private void add(Map<String, Object> map, FacilioModule module, List<FacilioField> fields) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields);
		builder.insert(map);
	}

	private void addAdminRoleForApp(ApplicationContext app) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getOrgId();

		RoleBean roleBean = AccountUtil.getRoleBean();
		Role role = new Role();
		role.setName(app.getName() + " Admin");
		role.setIsPrevileged(true);
		long roleId = roleBean.createRole(orgId, role);
		List<RoleApp> roleApps = new ArrayList<>();
		roleApps.add(new RoleApp(app.getId(), roleId));
		roleBean.addRolesAppsMapping(roleApps);
	}

	private void update(Map<String, Object> map, FacilioModule module, List<FacilioField> fields, long id) throws Exception {
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields).andCondition(
						CriteriaAPI.getIdCondition(id, module));
		builder.update(map);
	}

	public static void addCustomSetupLayout(ApplicationContext app) throws Exception{
		ApplicationLayoutContext customAppLayoutSetup = new ApplicationLayoutContext(app.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.SETUP, app.getLinkName());
		ApplicationApi.addApplicationLayout(customAppLayoutSetup);
		ApplicationApi.addCustomAppSetupLayoutWebGroups(customAppLayoutSetup);
	}

}
