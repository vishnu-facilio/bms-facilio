package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.WebTabContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum TranslationTypeEnum {

    DETAILS(WebTabContext.Type.MODULE,new GetTranslationDetailFieldsCommand()),
    VIEWS(WebTabContext.Type.MODULE,new GetViewTranslationFields()),
    VIEW_COLUMNS(WebTabContext.Type.MODULE,new GetViewColumnTranslationFields()),
    BUTTONS(WebTabContext.Type.MODULE, new GetButtonTranslationFields()),
    STATE_FLOWS(WebTabContext.Type.MODULE,new GetStateFlowTranslationFields()),
    FORMS(WebTabContext.Type.MODULE,new GetFormTranslationFields()),
    FORM_FIELDS(WebTabContext.Type.MODULE, new GetFormFieldTranslationFields()),
    FIELDS(WebTabContext.Type.MODULE,new GetFieldTranslationFileds())
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
