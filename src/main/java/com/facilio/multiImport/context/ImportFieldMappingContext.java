package com.facilio.multiImport.context;

import com.facilio.multiImport.enums.ImportFieldMappingType;
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
    private long parentLookupFieldId = -1l;
    private ImportFieldMappingType type;
    private String propName;

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
    public ImportFieldMappingType getTypeEnum() {
       return type;
    }
    public int getType() {
        if(type!=null){
            return type.getIndex();
        }
        return -1;
    }

    public void setType(int type) {
        this.type = ImportFieldMappingType.getImportFieldType(type);
        if(this.type==null){
            throw new IllegalArgumentException("Unsupported import field mapping type:"+type);
        }
    }
    public void setType(ImportFieldMappingType type) {
        this.type = type;
    }

}
