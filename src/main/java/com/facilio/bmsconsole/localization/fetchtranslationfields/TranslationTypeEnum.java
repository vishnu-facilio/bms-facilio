package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.WebTabContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

@Getter
@AllArgsConstructor
public enum TranslationTypeEnum {

    DETAILS("DETAILS",WebTabContext.Type.MODULE,new GetTranslationDetailFields()),
    FIELDS("FIELDS",WebTabContext.Type.MODULE,new GetFieldTranslationFileds()),
    FORMS("FORMS",WebTabContext.Type.MODULE,new GetFormTranslationFields()),
    VIEWS("VIEWS",WebTabContext.Type.MODULE,new GetViewTranslationFields()),
    VIEW_FOLDER("VIEW FOLDER",WebTabContext.Type.MODULE,new GetViewFolderTranslationFields()),
    STATES("STATES",WebTabContext.Type.MODULE,new GetStateTranslationFields()),
    STATE_TRANSITION("STATE TRANSITION",WebTabContext.Type.MODULE,new GetStateTransitionTranslationFields()),
    STATE_TRANSITION_FORM("STATE TRANSITION FORM",WebTabContext.Type.MODULE,new GetFormTranslationFields()),
    BUTTONS("BUTTONS",WebTabContext.Type.MODULE, new GetButtonTranslationFields()),
    DASHBOARD_DETAILS("DASHBOARD DETAILS", WebTabContext.Type.DASHBOARD,new GetTranslationDetailFields()),
    DASHBOARD_FOLDER("DASHBOARD FOLDER",WebTabContext.Type.DASHBOARD,new GetDashboardFolderTranslationFields()),
    DASHBOARD("DASHBOARD",WebTabContext.Type.DASHBOARD,new GetDashboardTabAndWidgetTransFields()),
    REPORT_FOLDER("REPORT FOLDER",WebTabContext.Type.REPORT,new GetReportFolderTranslationFields()),
    REPORT("REPORT",WebTabContext.Type.REPORT,new GetReportTranslationFields()),
    WORKORDER_FIELDS("WORKORDER FIELDS",WebTabContext.Type.MODULE,new GetWorkOrderTranslationFields()),
    ASSET_FIELDS("ASSET FIELDS",WebTabContext.Type.MODULE,new GetAssetTranslationFields()),
    PORTFOLIO("WEB TAB DETAILS", WebTabContext.Type.CUSTOM,new GetTranslationDetailFields())
    ;

    private static final Map<String, TranslationTypeEnum> TRANSLATION_TYPE_ENUM_MAP = initTranslationWiseType();
    public static final Map<WebTabContext.Type, List<ClientColumnTypeEnum>> CLIENT_TRANSLATION_TYPE_ENUM= init();

    private String clientColumnName;
    private WebTabContext.Type type;
    private TranslationTypeInterface handler;

    private static Map<String, TranslationTypeEnum> initTranslationWiseType () {
        Map<String, TranslationTypeEnum> translationWiseType = new HashMap<>();
        for (TranslationTypeEnum type : values()) {
            translationWiseType.put(type.name(),type);
        }
        return Collections.unmodifiableMap(translationWiseType);
    }

    public static TranslationTypeEnum getTranslationTypeModule ( @NonNull String type ) {
        return TRANSLATION_TYPE_ENUM_MAP.get(type);
    }

    private static Map<WebTabContext.Type, List<ClientColumnTypeEnum>> init () {
        Map<WebTabContext.Type, List<ClientColumnTypeEnum>> columnVsType = new LinkedHashMap<>();
        List<ClientColumnTypeEnum> moduleType = new ArrayList<>();
        List<ClientColumnTypeEnum> dashboardType = new ArrayList<>();
        List<ClientColumnTypeEnum> reportType = new ArrayList<>();
        List<ClientColumnTypeEnum> portfolioType = new ArrayList<>();
        for (TranslationTypeEnum type : values()) {
            switch (type.getType()) {
                case MODULE:
                    moduleType.add(new ClientColumnTypeEnum(type.name(),type.getClientColumnName()));
                    break;
                case DASHBOARD:
                    dashboardType.add(new ClientColumnTypeEnum(type.name(),type.getClientColumnName()));
                    break;
                case REPORT:
                    reportType.add(new ClientColumnTypeEnum(type.name(),type.getClientColumnName()));
                    break;
                case CUSTOM:
                    portfolioType.add(new ClientColumnTypeEnum(type.name(), type.getClientColumnName()));
                    break;
            }
        }
        columnVsType.put(WebTabContext.Type.MODULE,moduleType);
        columnVsType.put(WebTabContext.Type.DASHBOARD,dashboardType);
        columnVsType.put(WebTabContext.Type.REPORT,reportType);
        columnVsType.put(WebTabContext.Type.CUSTOM, portfolioType);
        return Collections.unmodifiableMap(columnVsType);
    }

    @Getter
    @AllArgsConstructor
    public static class ClientColumnTypeEnum {
        private String type, label;
    }
}

