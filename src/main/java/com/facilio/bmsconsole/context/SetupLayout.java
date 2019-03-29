package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class SetupLayout<T> {
	private T t;
	private static Logger log = LogManager.getLogger(SetupLayout.class.getName());

	public void setData(T t) {
        this.t = t;
    }
	private  Class<T> type;

	public SetupLayout(Class<T> type) {
        this.type = type;
   }

    public Class<T> getDataType() {
        return this.type;
    }
	
    public T getData() {
    	if(t==null)
    	{
    		try {
				return type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
    	}
    	
        return t;
    }
	
	String settingTitle;
	String settingGroupTitle;
	boolean isForm;
	String settingViewTitle;
	String settingFormTitle;
	List<ActionButton> actionButtons;
	
	public SetupLayout() {
		actionButtons = new ArrayList<>();
	}

	public String getSettingTitle() {
		return settingTitle;
	}

	public void setSettingTitle(String settingTitle) {
		this.settingTitle = settingTitle;
	}

	public String getSettingGroupTitle() {
		return settingGroupTitle;
	}
	
	public boolean isForm() {
		return isForm;
	}
	public void setForm(boolean isForm) {
		this.isForm = isForm;
	}

	public void setSettingGroupTitle(String settingGroupTitle) {
		this.settingGroupTitle = settingGroupTitle;
	}

	public String getSettingViewTitle() {
		return settingViewTitle;
	}

	public void setSettingViewTitle(String settingViewTitle) {
		this.settingViewTitle = settingViewTitle;
	}

	public String getSettingFormTitle() {
		return settingFormTitle;
	}

	public void setSettingFormTitle(String settingFormTitle) {
		this.settingFormTitle = settingFormTitle;
	}

	public List<ActionButton> getActionButtons() {
		return actionButtons;
	}

	public void setActionButtons(List<ActionButton> actionButtons) {
		this.actionButtons = actionButtons;
	}
	
	public SetupLayout addActionButton(ActionButton ab) {
		this.actionButtons.add(ab);
		return this;
	}
	
	public static SetupLayout getPersonalSettingsLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("Personal Settings");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	public static SetupLayout getImportLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Data Administration");
		sl.setSettingViewTitle("Import");
		
		return sl;
	}
	public static SetupLayout getAssignmentRules() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Automation");
		sl.setSettingViewTitle("Assignment Rules");
		
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		
		return sl;
	}
	public static SetupLayout getNewAssignmentRules() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Automation");
		sl.setSettingViewTitle("New AssignmentRules");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		
		return sl;
	}
	
	public static SetupLayout<ServicePortalInfo> getservicePortal() {
		
		SetupLayout<ServicePortalInfo> sl = new SetupLayout<ServicePortalInfo>();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("Service Portal");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		
		
		return sl;
	}
	
	public static SetupLayout getCompanySettingsLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("Company Settings");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getEmailNotificationsLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Work Order");
		sl.setSettingViewTitle("Notifications");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
	
	public static SetupLayout getEmailSettingsLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Work Order");
		sl.setSettingViewTitle("Email Settings");
		sl.addActionButton(new ActionButton().setName("New Helpdesk Email").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
	
	
   public static SetupLayout getControllerSettingsLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Controller");
		sl.setSettingViewTitle("Add Controller");
		sl.addActionButton(new ActionButton().setName("New Add Controller").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		return sl;
	}
	
   public static SetupLayout getAddControllerSettingsLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Controller");
		sl.setSettingViewTitle("New Add Controller");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		return sl;
	}

	public static SetupLayout getNewEmailSettingLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Work Order");
		sl.setSettingViewTitle("New Helpdesk Email");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getEditEmailSettingLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Work Order");
		sl.setSettingViewTitle("Edit Helpdesk Email");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getUsersListLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("Users");
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
	
	public static SetupLayout getNewUserLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("New User");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getEditUserLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("Edit User");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getGroupsListLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("Groups");
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
	
	public static SetupLayout getNewGroupLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("New Group");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getEditGroupLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("Edit Group");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getRolesListLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("Roles");
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
	
	public static SetupLayout getSkillsListLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("Skills");
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
	
	public static SetupLayout getNewSkillLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("New Skill");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getEditSkillLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("Edit Skill");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}


	public static SetupLayout getLocationsListLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("Locations");
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
		
	public static SetupLayout getNewLocationLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("New Location");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getEditLocationLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("General");
		sl.setSettingViewTitle("Edit Location");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}

	public static SetupLayout getNewRoleLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("New Role");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getEditRoleLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Users & Groups");
		sl.setSettingViewTitle("Edit Role");
		sl.addActionButton(new ActionButton().setName("Save").setIconClass("fa-check").setClassName("save-btn").setType(ActionButton.Type.SAVE));
		sl.addActionButton(new ActionButton().setName("Cancel").setIconClass("fa-times").setClassName("cancel-btn").setType(ActionButton.Type.CANCEL));
		
		return sl;
	}
	
	public static SetupLayout getTicketStatusListLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Work Order");
		sl.setSettingViewTitle("Status");
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
	
	public static SetupLayout getTicketPriorityListLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Work Order");
		sl.setSettingViewTitle("Priority");
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
	
	public static SetupLayout getTicketCategoryListLayout() {
		
		SetupLayout sl = new SetupLayout();
		sl.setSettingTitle("Settings");
		sl.setSettingGroupTitle("Work Order");
		sl.setSettingViewTitle("Category");
		sl.addActionButton(new ActionButton().setName("New").setIconClass("fa-plus").setClassName("new-btn").setType(ActionButton.Type.SAVE));
		
		return sl;
	}
}

class ActionButton
{
	String name;
	String className;
	String iconClass;
	Type type;
	private static Logger log = LogManager.getLogger(ActionButton.class.getName());

	public enum Type {
		
		SAVE,
		CANCEL,
		NORMAL;
	}
	
	public String getName() {
		return name;
	}
	public ActionButton setName(String name) {
		this.name = name;
		return this;
	}
	public String getIconClass() {
		return iconClass;
	}
	public ActionButton setIconClass(String iconClass) {
		this.iconClass = iconClass;
		return this;
	}
	public String getClassName() {
		return className;
	}
	public ActionButton setClassName(String className) {
		this.className = className;
		return this;
	}
	public Type getType() {
		return type;
	}
	public ActionButton setType(Type type) {
		this.type = type;
		return this;
	}
	
	
	
}