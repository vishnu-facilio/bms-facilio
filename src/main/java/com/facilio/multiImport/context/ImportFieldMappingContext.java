package com.facilio.multiImport.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ImportFieldMappingContext implements Serializable {

    private long id = -1L;
    private long importSheetId = -1L;
    private long fieldId = -1L;
    private String fieldName;
    private String sheetColumnName;
    private int unitId;
    private boolean isMandatory;
    private String dateFormat;
}
