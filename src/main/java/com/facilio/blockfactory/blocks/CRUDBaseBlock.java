package com.facilio.blockfactory.blocks;

import com.facilio.beans.ModuleBean;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CRUDBaseBlock extends BaseBlock{
    public CRUDBaseBlock(Map<String, Object> config) {
        super(config);
    }
    protected void validateRecordData(JSONObject recordData, String moduleName) throws Exception {
        ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Set<String> keySet = recordData.keySet();
        for (String fieldName : keySet){
            FacilioField field = fieldMap.get(fieldName);
            Object data = recordData.get(fieldName);
            if(field == null || data==null){
                continue;
            }
            switch (field.getDataTypeEnum()) {
                case DATE:
                case DATE_TIME: {
                    if(!NumberUtils.isNumber(data.toString())){
                        throw new FlowException(data +" is not a number for date time field:"+field);
                    }
                    long millis = (long) Double.parseDouble(data.toString());
                    recordData.put(field.getName(),millis);
                    break;
                }
                case SYSTEM_ENUM: {
                    if(!NumberUtils.isNumber(data.toString())){
                        throw new FlowException(data +" is not a number for system enum field:"+field);
                    }
                    int index = (int) Double.parseDouble(data.toString());

                    SystemEnumField enumField = (SystemEnumField) field;
                    String enumString = enumField.getValue(index);

                    if(enumString == null){
                        throw new FlowException(data +" is a unsupported system enum field:"+field);
                    }

                    recordData.put(field.getName(),index);
                    break;
                }
                case ENUM: {
                    if(!NumberUtils.isNumber(data.toString())){
                        throw new FlowException(data +" is not a number for system enum field:"+field);
                    }
                    int index = (int) Double.parseDouble(data.toString());

                    EnumField enumField = (EnumField) field;
                    String enumString = enumField.getValue(index);

                    if(enumString == null){
                        throw new FlowException(data +" is a unsupported enum for field:"+field);
                    }

                    recordData.put(field.getName(),index);
                    break;
                }
                case LOOKUP: {
                    if(!(data instanceof Map)){
                        throw new FlowException(data + " is not a map for lookup field:"+field);
                    }
                    break;
                }
                case NUMBER:{
                    if(!NumberUtils.isNumber(data.toString())){
                        throw new FlowException(data +" is not a number for field:"+field);
                    }
                    long number = (long) Double.parseDouble(data.toString());
                    recordData.put(field.getName(),number);
                    break;
                }
                case DECIMAL: {
                    if(!NumberUtils.isNumber(data.toString())){
                        throw new FlowException(data +" is not a decimal for field:"+field);
                    }
                    double decimal = Double.parseDouble(data.toString());
                    recordData.put(field.getName(),decimal);
                    break;
                }
                case BOOLEAN: {
                    boolean booleanValue = FacilioUtil.parseBoolean(data.toString());
                    recordData.put(field.getName(), booleanValue);
                    break;
                }
            }

        }
    }
}
