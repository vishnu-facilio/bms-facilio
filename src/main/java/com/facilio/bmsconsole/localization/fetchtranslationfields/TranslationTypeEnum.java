package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.WebTabContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum TranslationTypeEnum {

    DETAILS(WebTabContext.Type.MODULE,new GetTranslationDetailFields()),
    VIEWS(WebTabContext.Type.MODULE,new GetViewTranslationFields()),
    VIEW_FOLDER(WebTabContext.Type.MODULE,new GetViewFolderTranslationFields()),
    BUTTONS(WebTabContext.Type.MODULE, new GetButtonTranslationFields()),
    STATES(WebTabContext.Type.MODULE,new GetStateTranslationFields()),
    FORMS(WebTabContext.Type.MODULE,new GetFormTranslationFields()),
    FORM_FIELDS(WebTabContext.Type.MODULE, new GetFormFieldTranslationFields()),
    FIELDS(WebTabContext.Type.MODULE,new GetFieldTranslationFileds()),
    WEB_TAB(WebTabContext.Type.MODULE,new GetWebTabTranslationFields()),
    STATE_TRANSITION(WebTabContext.Type.MODULE,new GetStateTransitionTranslationFields()),
    DASHBOARD_FOLDER(WebTabContext.Type.DASHBOARD,new GetDashboardFolderTranslationFields()),
    DASHBOARD(WebTabContext.Type.DASHBOARD,new GetDashboardTabAndWidgetTransFields()),
    REPORT_FOLDER(WebTabContext.Type.REPORT,new GetReportFolderTranslationFields())
    ;

    private static final Map<String, TranslationTypeEnum> TRANSLATION_TYPE_ENUM_MAP = initTranslationWiseType();
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
}
