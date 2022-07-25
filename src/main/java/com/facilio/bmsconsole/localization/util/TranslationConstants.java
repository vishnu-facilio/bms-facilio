package com.facilio.bmsconsole.localization.util;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.List;

public class TranslationConstants {

    public static final String LANG_CODE = "langCode";
    public static final String FILE_ID = "fileId";
    public static final String TRANSLATION_DATA = "Translation_Data";
    public static final String TRANSLATION_LIST="translationFinalList";
    public static final String LABEL = "label";
    public static final String PREFIX = "prefix";
    public static final String SUFFIX = "suffix";
    public static final String VALUE = "value";
    public static final String MODULE = "module";
    public static final String FIELDS = "fields";
    public static final String DISPLAY_NAME = "displayName";
    public static final String NAME = "name";
    public static final String KEY ="key";
    public static final String TAB_ID="tabId";
    public static final String TRANSLATION_FIELDS="translationFields";
    public static final String TRANSLATION_TYPE="translationType";
    public static final String FILTERS = "filters";

    public static final FacilioModule getTranslationModule () {
        return new FacilioModule("translationData","Translation Data",TRANSLATION_DATA);
    }
    public static final List<FacilioField> getTranslationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module =getTranslationModule();
        fields.add(FieldFactory.getIdField(module));
        fields.add(FieldFactory.getField(LANG_CODE, "LANG_CODE", module, FieldType.STRING));
        fields.add(FieldFactory.getField(FILE_ID, "FILE_ID", module, FieldType.NUMBER));
        fields.add(FieldFactory.getField("status","STATUS",module,FieldType.BOOLEAN));
		fields.add(FieldFactory.getField("createdTime","CREATED_TIME",FieldType.DATE_TIME));
		fields.add(FieldFactory.getField("modifiedTime","MODIFIED_TIME",FieldType.DATE_TIME));

        return fields;
    }
}
