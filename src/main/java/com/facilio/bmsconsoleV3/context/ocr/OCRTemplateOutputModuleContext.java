package com.facilio.bmsconsoleV3.context.ocr;

import java.util.List;

import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputFieldContext.TemplateOutputFieldExtractTypeEnum;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OCRTemplateOutputModuleContext extends V3Context{
	
	public OCRTemplateOutputModuleContext(Long id) {
		this.setId(id);
	}

	OCRTemplateContext template;
	Long outputModuleId;
	Long parentRecordId;
	
	OCROutputModuleType type;
	
	TemplateOutputFieldExtractTypeEnum fieldType; // for client purpose
	
	public TemplateOutputFieldExtractTypeEnum getFieldTypeEnum() {
        return fieldType;
    }
	
	public Integer getFieldType() {
        if (fieldType == null) {
            return null;
        }
        return fieldType.getIndex();
    }
    public void setFieldType(Integer type) {
        if (type != null) {
            this.fieldType = TemplateOutputFieldExtractTypeEnum.valueOf(type);
        } else {
            this.fieldType = null;
        }
    }
	
	public Integer getType() {
        if (type == null) {
            return null;
        }
        return type.getIndex();
    }
    public void setType(Integer type) {
        if (type != null) {
            this.type = OCROutputModuleType.valueOf(type);
        } else {
            this.type = null;
        }
    }
    
    
	
	List<OCRTemplateOutputFieldContext> outputFields;
	
	@AllArgsConstructor
    public static enum OCROutputModuleType implements FacilioIntEnum {
        TABLE("Table"),
        EACH_FIELD("Each Field")
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final OCROutputModuleType[] RULE_TYPES = OCROutputModuleType.values();
        public static OCROutputModuleType valueOf(int type) {
            if (type > 0 && type <= RULE_TYPES.length) {
                return RULE_TYPES[type - 1];
            }
            return null;
        }
        
        //public abstract List<PreUtilityIntegerationLineItems> execute(Context context,LineItemRuleContext lineItemRule) throws Exception;
    }
	
	OCROutputRecordType recordType;
	
	public Integer getRecordType() {
        if (recordType == null) {
            return null;
        }
        return recordType.getIndex();
    }
    public void setRecordType(Integer recordType) {
        if (recordType != null) {
            this.recordType = OCROutputRecordType.valueOf(recordType);
        } else {
            this.recordType = null;
        }
    }
	
	@AllArgsConstructor
    public static enum OCROutputRecordType implements FacilioIntEnum {
        ACCOUNT("Account"),
        ELECTRICITY("ELECTRICITY"),
        WATER("WATER"),
        SEWAGE("SEWAGE"),
        LINE_ITEMS("LINE_ITEMS"),
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final OCROutputRecordType[] RULE_TYPES = OCROutputRecordType.values();
        public static OCROutputRecordType valueOf(int type) {
            if (type > 0 && type <= RULE_TYPES.length) {
                return RULE_TYPES[type - 1];
            }
            return null;
        }
    }
}
