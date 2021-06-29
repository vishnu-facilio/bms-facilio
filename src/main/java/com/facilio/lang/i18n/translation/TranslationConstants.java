package com.facilio.lang.i18n.translation;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TranslationConstants {

    public static final String LANG_CODE = "langCode";
    public static final String FILE_ID = "fileId";
    public static final String TRANSLATION_DATA = "Translation_Data";
    public static final String ORGID = "orgId";
    public static final String NAME_SPACE = "Translation";

    public static final FacilioModule getTranslationModule () {
        return new FacilioModule("translationData","Translation Data",TRANSLATION_DATA);
    }
    public static final List<FacilioField> getTranslationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module =getTranslationModule();
        fields.add(FieldFactory.getIdField(module));
        fields.add(FieldFactory.getField(LANG_CODE, "LANG_CODE", module, FieldType.STRING));
        fields.add(FieldFactory.getField(FILE_ID, "FILE_ID", module, FieldType.NUMBER));

        return fields;
    }

}
