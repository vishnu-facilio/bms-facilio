package com.facilio.multiImport.context;

import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter@Setter
public class MultiImportField implements Serializable {
    private FacilioField field;
    private ImportRelationshipRequestContext relation;
    private boolean mandatory;
    private boolean isOneLevelSupportedField;
    private boolean isGroupedFieldType;
    private List<FacilioField> lookupModuleFields;
    private List<FacilioField> groupedFields;
}
