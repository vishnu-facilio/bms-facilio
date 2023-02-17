package com.facilio.datamigration.util;

import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataMigrationUtil {

    public static List<SupplementRecord> getSupplementFields(Collection<FacilioField> fields) {
        List<SupplementRecord> supplementFields = new ArrayList<>();
        for (FacilioField f : fields) {
            if ((!f.isDefault() && (f.getDataTypeEnum() == FieldType.LOOKUP || f.getDataTypeEnum() == FieldType.MULTI_LOOKUP
                        || f.getDataTypeEnum() == FieldType.MULTI_ENUM))
                    || (f.getDataTypeEnum().isRelRecordField())) {
                supplementFields.add((SupplementRecord) f);
            }
        }

        return supplementFields;
    }
}
