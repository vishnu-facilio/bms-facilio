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

    USER(null, APP, false),
    TEAM(null, APP, false),
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
    VALIDATION_RULE(null, MODULE, false),
    NAMED_CRITERIA(NamedCriteriaPackageBeanImpl.class, MODULE, false),
    CUSTOM_BUTTON(null, MODULE, false),
    RELATIONSHIP(RelationshipPackageBeanImpl.class, MODULE, false),
    STATE_FLOW(StateFlowPackageBeanImpl.class,MODULE,false),
    STATE_TRANSITION(StateTransitionPackageBeanImpl.class,MODULE,false)
    ;

    public static final List<ComponentType> ORDERED_COMPONENT_TYPE_LIST = Collections.unmodifiableList(initOrderedList());
    public static List<ComponentType> initOrderedList() {
        List<ComponentType> componentOrder = new ArrayList<ComponentType>(){{
            add(ORG_INFO);
            add(APP);
            add(ROLE);
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
            add(VALIDATION_RULE);
            add(CUSTOM_BUTTON);
            add(WORKFLOW_RULE);
            add(APP_LAYOUT);
            add(WEBTAB_GROUP);
            add(WEBTAB);
            add(STATE_FLOW);
            add(STATE_TRANSITION);
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

}
