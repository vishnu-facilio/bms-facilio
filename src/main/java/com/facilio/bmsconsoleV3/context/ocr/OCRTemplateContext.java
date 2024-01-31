package com.facilio.bmsconsoleV3.context.ocr;

import java.io.File;
import java.util.List;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OCRTemplateContext extends V3Context {

	
	private String name;
    private String description;
    private Integer pageBreak;
    
    private File sampleBill;
    private File parsedForm;
    private File parsedRawText;
    private long sampleBillId;
    private String sampleBillUrl;
    private String sampleBillFileName;
    private  String sampleBillContentType;
    private long parsedFormId;
    private String parsedFormUrl;
    private String parsedFormFileName;
    private  String parsedFormContentType;
    private long parsedRawTextId;
    private String parsedRawTextUrl;
    private String parsedRawTextFileName;
    private  String parsedRawTextContentType;

    private templateStatusEnum status;
    
    List<OCRTemplateTableContext> tables;
    
    List<OCRTemplateVariableContext> variables;
    
    List<OCRTemplateOutputModuleContext> outputConfig;
    
    public Integer getStatus() {
        if (status == null) {
            return null;
        }
        return status.getIndex();
    }
    public void setStatus(Integer status) {
        if (status != null) {
            this.status = templateStatusEnum.valueOf(status);
        } else {
            this.status = null;
        }
    }
    
    public void setStatusEnum(templateStatusEnum status) {
        this.status = status;
    }
    @AllArgsConstructor
    public static enum templateStatusEnum implements FacilioIntEnum {
        BILL_UPLOADED ("Bill Uploaded"),
        PARSING_IN_PROGRESS ("Parsing In Progress"),
        BILL_PARSED ("Bill Parsed"),
        PARSING_FAILED("Parsing Field")
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
        private static final templateStatusEnum[] STATUS = templateStatusEnum.values();
        public static templateStatusEnum valueOf(int type) {
            if (type > 0 && type <= STATUS.length) {
                return STATUS[type - 1];
            }
            return null;
        }
    }
}
