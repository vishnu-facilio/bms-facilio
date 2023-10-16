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
    private int unitId=-1;
    private boolean isMandatory;
    private String dateFormat;
    private LookupIdentifierEnum lookupIdentifier;
    private long relMappingId = -1l;

    public int getLookupIdentifier() {
        if(lookupIdentifier!=null){
            return lookupIdentifier.getIndex();
        }
        return 1;
    }
    public LookupIdentifierEnum getLookupIdentifierEnum(){
        if(lookupIdentifier!=null){
            return lookupIdentifier;
        }
        return LookupIdentifierEnum.PRIMARY_FIELD;
    }
    public void setLookupIdentifier(LookupIdentifierEnum lookupIdentifier) {
        this.lookupIdentifier = lookupIdentifier;
    }
    public void setLookupIdentifier(int lookupIdentifier) {
        this.lookupIdentifier =LookupIdentifierEnum.getLookupIdentifier(lookupIdentifier);
        if( this.lookupIdentifier==null){
           throw new IllegalArgumentException("Invalid lookupIdentifier:"+lookupIdentifier);
        }
    }
}
