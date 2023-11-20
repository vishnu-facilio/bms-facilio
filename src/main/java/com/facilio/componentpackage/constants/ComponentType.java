package com.facilio.componentpackage.constants;

import com.facilio.componentpackage.implementation.*;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.modules.FacilioIntEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ComponentType implements FacilioIntEnum {

    MODULE(ModulePackageBeanImpl.class, null, false),
    FIELD(FieldPackageBeanImpl.class, MODULE, false),
    APP(AppPackageBeanImpl.class, null, false),
    ORG_INFO(OrgInfoPackageBeanImpl.class, null, false),
    ROLE(RolePackageBeanImpl.class, null, true),
    APP_LAYOUT(AppLayoutPackageBeanImpl.class, APP, true),
    WEBTAB_GROUP(WebTabGroupPackageBeanImpl.class, APP_LAYOUT, false),
    WEBTAB(WebTabPackageBeanImpl.class, APP, false),
    FORM(FormPackageBeanImpl.class, MODULE, true),
    FORM_SECTION(FormSectionPackageBeanImpl.class, FORM, false),
    FORM_FIELDS(FormFieldPackageBeanImpl.class, FORM, false),
    VIEW(ViewPackageBeanImpl.class, MODULE, false),
    ASSET_CATEGORY(AssetCategoryPackageBeanImpl.class,MODULE,true),
    ASSET_DEPARTMENT(AssetDepartmentPackageBeanImpl.class, MODULE, false),
    ASSET_TYPE(AssetTypePackageBeanImpl.class, MODULE, false),
    TICKET_CATEGORY(TicketCategoryPackageBeanImpl.class, MODULE, false),
    TICKET_TYPE(TicketTypePackageBeanImpl.class, MODULE, false),
    TICKET_PRIORITY(TicketPriorityPackageBeanImpl.class, MODULE, false),
    SPACE_CATEGORY(SpaceCategoryPackageBeanImpl.class, MODULE, false),
    PEOPLE(PeoplePackageBeanImpl.class, APP, false),
    USER(UserPackageBeanImpl.class, APP, true),
    TEAM(GroupPackageBeanImpl.class, APP, false),
    SITE(null, null, false),
    TICKET_STATUS(TicketStatePackageBeanImpl.class, MODULE, false),
    FUNCTION_NAMESPACE(FunctionNameSpacePackageBeanImpl.class, null, false),
    FUNCTION(FunctionPackageBeanImpl.class, FUNCTION_NAMESPACE, true),
    EMAIL_TEMPLATE(EmailTemplatePackageBeanImpl.class, MODULE, false),
    WORKFLOW_RULE(WorkflowRulePackageBeanImpl.class, MODULE, false),
    SCHEDULE(SchedulerPackageBeanImpl.class, null, false),
    CONNECTED_APP(null, null, false),
    CONNECTED_APP_WIDGETS(null, CONNECTED_APP, false),
    CONNECTED_APP_VARIABLES(null, CONNECTED_APP, false),
    CONNECTED_APP_CONNECTORS(null, CONNECTED_APP, false),
    FORM_RULE(FormRulePackageBeanImpl.class, FORM, false),
    SUB_FORM_RULE(SubFormRulePackageBeanImpl.class, FORM, false),
    VALIDATION_RULE(ValidationRulePackageBeanImpl.class, FORM, false),
    NAMED_CRITERIA(NamedCriteriaPackageBeanImpl.class, MODULE, false),
    CUSTOM_BUTTON(CustomButtonPackageBeanImpl.class, MODULE, false),
    RELATIONSHIP(RelationshipPackageBeanImpl.class, MODULE, false),
    STATE_FLOW(StateFlowPackageBeanImpl.class,MODULE,true),
    STATE_TRANSITION(StateTransitionPackageBeanImpl.class,MODULE,false),
    NOTIFICATION(NotificationPackageBeanImpl.class,MODULE,false),
    APPROVAL_STATE_FLOW(ApprovalStateFlowPackageBeanImpl.class,MODULE,false),
    GLOBAL_GROUP_VARIABLE(GlobalVariableGroupPackageBeanImpl.class,MODULE,false),
    GLOBAL_VARIABLE(GlobalVariablePackageBeanImpl.class,MODULE,false),
    SLA_ENTITY(SLAEntityPackageBeanImpl.class,MODULE,false),
    SLA_POLICY(SLAPackageBeanImpl.class,MODULE,false),
    FAULT_IMPACT(FaultImpactPackageBeanImpl.class,MODULE,Boolean.FALSE),
    READING_RULE(ReadingRulePackageBeanImpl.class,MODULE,Boolean.TRUE),
    READING_KPI(ReadingKpiPackageBeanImpl.class, MODULE,Boolean.FALSE),
    ASSET_READINGS(ReadingsPackageBeanImpl.class,MODULE,Boolean.FALSE),
    CONNECTOR(ConnectorPackageBeanImpl.class,null,false),
    GLOBAL_SCOPE_VARIABLE(GlobalScopingPackageImpl.class,MODULE,false),
    USER_SCOPING(UserScopingPackageImpl.class,MODULE, false),
    USER_SCOPING_CONFIG(UserScopingConfigPackageBeanImpl.class,MODULE, false),
    PEOPLE_USER_SCOPING_CONFIG(PeopleUserScopingConfigPackageBeanImpl.class,MODULE, false),
    PERMISSION_SET(PermissionSetPackageImpl.class,MODULE,false),
    PERMISSION_SET_CONFIG(PermissionSetConfigPackageImpl.class,MODULE,false),
    PEOPLE_PERMISSION_SET_CONFIG(PeoplePermissionSetConfigPackageImpl.class,MODULE,false),
    SERVICE_CATALOG_GROUP(ServiceCatalogGroupPackageBeanImpl.class,null,false),
    SERVICE_CATALOG_ITEM(ServiceCatalogItemPackageImpl.class,null,false),
    VENDOR(null, null, false),
    TENANT(null, null, false),
    CLIENT(null, null, false);

    public static final List<ComponentType> ORDERED_COMPONENT_TYPE_LIST = Collections.unmodifiableList(initOrderedList());
    public static List<ComponentType> initOrderedList() {
        List<ComponentType> componentOrder = new ArrayList<ComponentType>(){{
            add(ORG_INFO);
            add(APP);
            add(ROLE);
            add(PEOPLE);
            add(VENDOR);
            add(TENANT);
            add(CLIENT);
            add(USER);
            add(TEAM);
            add(MODULE);
            add(TICKET_STATUS);
            add(ASSET_CATEGORY);
            add(ASSET_DEPARTMENT);
            add(ASSET_TYPE);
            add(TICKET_CATEGORY);
            add(TICKET_TYPE);
            add(TICKET_PRIORITY);
            add(SPACE_CATEGORY);
            add(FIELD);
            add(SITE);
            add(ASSET_READINGS);
            add(NAMED_CRITERIA);
            add(FUNCTION_NAMESPACE);
            add(FUNCTION);
            add(CONNECTED_APP);
            add(FORM);
            add(FORM_SECTION);
            add(FORM_FIELDS);
            add(VIEW);
            add(RELATIONSHIP);
            add(CONNECTED_APP_WIDGETS);
            add(CONNECTED_APP_VARIABLES);
            add(CONNECTED_APP_CONNECTORS);
            add(EMAIL_TEMPLATE);
            add(SCHEDULE);
            add(FORM_RULE);
            add(SUB_FORM_RULE);
            add(VALIDATION_RULE);
            add(CUSTOM_BUTTON);
            add(WORKFLOW_RULE);
            add(APP_LAYOUT);
            add(WEBTAB_GROUP);
            add(WEBTAB);
            add(STATE_FLOW);
            add(STATE_TRANSITION);
            add(NOTIFICATION);
            add(APPROVAL_STATE_FLOW);
            add(GLOBAL_GROUP_VARIABLE);
            add(GLOBAL_VARIABLE);
            add(SLA_ENTITY);
            add(SLA_POLICY);
            add(FAULT_IMPACT);
            add(READING_RULE);
            add(READING_KPI);
            add(CONNECTOR);
            add(GLOBAL_SCOPE_VARIABLE);
            add(USER_SCOPING);
            add(USER_SCOPING_CONFIG);
            add(PEOPLE_USER_SCOPING_CONFIG);
            add(PERMISSION_SET);
            add(PERMISSION_SET_CONFIG);
            add(PEOPLE_PERMISSION_SET_CONFIG);
            add(SERVICE_CATALOG_GROUP);
            add(SERVICE_CATALOG_ITEM);
        }};
        return componentOrder;
    }

    public static final String ORDER_BY_COMPONENT_TYPE_STR = getOrderByComponentTypeString();

    private static String getOrderByComponentTypeString() {
        String orderedTypes = ORDERED_COMPONENT_TYPE_LIST.stream().map(a -> String.valueOf(a.getIndex()))
                .collect(Collectors.joining(","));
        return "FIELD(COMPONENT_TYPE,"+orderedTypes+")" ;
    }

    Class<? extends PackageBean> componentClass;
    ComponentType parentComponentType;
    boolean isPostTransactionRequired;

    @Override
    public String getValue() {
        return name();
    }

    public static ComponentType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

    public PackageBean getPackageComponentClassInstance() throws Exception {
        PackageBean packageClass = componentClass.getDeclaredConstructor().newInstance();
        return packageClass;
    }

    public static final List<ComponentType> PICKLIST_COMPONENTS = new ArrayList<ComponentType>(){{
        add(PEOPLE);
        add(USER);
        add(TEAM);
        add(ROLE);
        add(TICKET_STATUS);
        add(ASSET_CATEGORY);
        add(ASSET_DEPARTMENT);
        add(ASSET_TYPE);
        add(TICKET_CATEGORY);
        add(TICKET_TYPE);
        add(TICKET_PRIORITY);
        add(SPACE_CATEGORY);
    }};
}
