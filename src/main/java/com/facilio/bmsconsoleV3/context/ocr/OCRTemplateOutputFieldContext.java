package com.facilio.bmsconsoleV3.context.ocr;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.enums.FieldTypeOcrValueProcessor;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OCRTemplateOutputFieldContext extends V3Context{
	
	
	OCRTemplateOutputModuleContext ocrOutputModule; 
	OCRTemplateContext template;
	OCRTemplateVariableContext variable;
	
	 private String regex;
	    
	    private String tableName;
		private Integer row;
		private Integer column;
	    
	    private Integer pageNumber;
	    private String fieldKey;
	    
	    private Long outputFieldId;
	    
	    private String uniqueKey;
	    
	    private String dateFormat;

	    private TemplateOutputFieldExtractTypeEnum type;
	    
	    public TemplateOutputFieldExtractTypeEnum getTypeEnum() {
	    	return type;
	    }
	
	    public Integer getType() {
	        if (type == null) {
	            return null;
	        }
	        return type.getIndex();
	    }
	    public void setType(Integer type) {
	        if (type != null) {
	            this.type = TemplateOutputFieldExtractTypeEnum.valueOf(type);
	        } else {
	            this.type = null;
	        }
	    }
	    @AllArgsConstructor
	    public static enum TemplateOutputFieldExtractTypeEnum implements FacilioIntEnum {
	        KEYVALUE("Key Value") {
				@Override
				public Object execute(OCRTemplateOutputModuleContext opModule, OCRTemplateOutputFieldContext opField,Map<String, Object> parsedResult) throws Exception {
					
					Map<Integer, Map<String, String>> keyValueSearchMap = (Map<Integer, Map<String, String>>) parsedResult.get("formsSearchMap");
					
					String value = keyValueSearchMap.get(opField.getPageNumber()).get(opField.getFieldKey());
					
					value = extractRegexValue(value, opField.getRegex());
					
					return parseAsPerFieldType(opField, value);
				}
			},
	        TABLE("Tables") {
				@Override
				public Object execute(OCRTemplateOutputModuleContext opModule, OCRTemplateOutputFieldContext opField,Map<String, Object> parsedResult) throws Exception {
					
					Map<String, Map<Integer, Map<Integer, String>>> tableSearchMap = (Map<String, Map<Integer, Map<Integer, String>>>) parsedResult.get("tablesSearchMap");
					
					Map<Integer, Map<Integer, String>> table = tableSearchMap.get(opField.getTableName());

					String value = table.get(opField.getRow()).get(opField.getColumn());

					value = extractRegexValue(value, opField.getRegex());
					
					return parseAsPerFieldType(opField, value);
				}
			},
	        RAWTEXT("Raw Text") {
				@Override
				public Object execute(OCRTemplateOutputModuleContext opModule, OCRTemplateOutputFieldContext opField,Map<String, Object> parsedResult) throws Exception {
					// TODO Auto-generated method stub
					
					String rawText = (String) parsedResult.get("rawText");
					
					String value = extractRegexValue(rawText, opField.getRegex());
					
					return parseAsPerFieldType(opField, value);
					
				}
			},
	        VARIABLE("Variable") {
				@Override
				public Object execute(OCRTemplateOutputModuleContext opModule, OCRTemplateOutputFieldContext opField,Map<String, Object> parsedResult) throws Exception {
					
					Map<Long, Object> variablesOp = (Map<Long, Object>) parsedResult.get("variables");
					
					Object obj = variablesOp.get(opField.getVariable().getId());
					
					return parseAsPerFieldType(opField, obj.toString());
				}
			},
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
	        private static final TemplateOutputFieldExtractTypeEnum[] RULE_TYPES = TemplateOutputFieldExtractTypeEnum.values();
	        public static TemplateOutputFieldExtractTypeEnum valueOf(int type) {
	            if (type > 0 && type <= RULE_TYPES.length) {
	                return RULE_TYPES[type - 1];
	            }
	            return null;
	        }
	        
	        public Object parseAsPerFieldType(OCRTemplateOutputFieldContext opField,String value) throws Exception {
	        	
	        	ModuleBean moduleBean = Constants.getModBean();
				FacilioField field = moduleBean.getField(opField.getOutputFieldId());
				
				FieldTypeOcrValueProcessor fieldTypeOcrValueProcessor = FieldTypeOcrValueProcessor.getFieldTypeOcrValueProcessor(field.getDataTypeEnum());
				return fieldTypeOcrValueProcessor.process(field, value, opField.getDateFormat());
	        }

			public String extractRegexValue(String text,String regex) {
	        	
	        	if(regex == null) {
	        		return text;
	        	}
	        	Pattern pattern = Pattern.compile(regex);
				
				Matcher matcher = pattern.matcher(text);
				
				if(matcher.find()) {
					return matcher.group(1);
				}
				return null;
	        }

			public abstract Object execute(OCRTemplateOutputModuleContext opModule, OCRTemplateOutputFieldContext opField,Map<String, Object> parsedResult) throws Exception;
	    }

}
