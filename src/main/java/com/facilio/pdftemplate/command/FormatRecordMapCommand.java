package com.facilio.pdftemplate.command;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.*;
import com.facilio.pdftemplate.context.PDFTemplate;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.TimeFormat;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormatRecordMapCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PDFTemplate pdfTemplate = (PDFTemplate) context.get(FacilioConstants.ContextNames.PDF_TEMPLATE);
        long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String moduleName = Constants.getModBean().getModule(pdfTemplate.getModuleId()).getName();
        List<Long> recordIds = new ArrayList<>();
        recordIds.add(recordId);
        FacilioContext recordMapContext = V3Util.getSummary(moduleName,recordIds);
        ModuleBaseWithCustomFields recordContext = Constants.getRecordMap(recordMapContext).get(moduleName).get(0);
        Map<String, Object> recordMap = FieldUtil.getAsProperties(recordContext);
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String,Object> formattedRecordMap =  getMapClone(recordMap);
        for(Map.Entry<String,Object> record : recordMap.entrySet()){
            String fieldName = record.getKey();
            Object fieldValue = record.getValue();
            FacilioField field = getField(fields,fieldName);
            if(field != null){
                String formattedValue = getFormattedValueBasedOnFieldDisplayType(field,fieldValue);
                formattedRecordMap.put(fieldName.concat("Formatted"),formattedValue);
            }
        }
        context.put(FacilioConstants.ContextNames.FORMATTED_RECORD_MAP,formattedRecordMap);
        return false;
    }
    private Map<String,Object> getMapClone(Map<String, Object> recordMap){
        Map<String,Object> cloneMap = new HashMap<>();
        for(Map.Entry<String,Object> entry : recordMap.entrySet()){
            cloneMap.put(entry.getKey(), entry.getValue());
        }
        return cloneMap;
    }
    private FacilioField getField(List<FacilioField> fields,String fieldName){
        if(CollectionUtils.isNotEmpty(fields)) {
            for (FacilioField field : fields) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }
    private String getFormattedValueBasedOnFieldDisplayType(FacilioField field, Object value) throws Exception{
        ModuleBean modBean = Constants.getModBean();
        String formattedValue;
        switch (field.getDataTypeEnum()){
            case DATE: {
                String dateFormat = AccountUtil.getCurrentOrg().getDateFormat()!=null ? AccountUtil.getCurrentOrg().getDateFormat().replaceAll("D","d").replaceAll("Y","y") : "dd-MMM-yyyy";
                formattedValue = value!=null ? DateTimeUtil.getFormattedTime((Long) value,dateFormat) : "---";
                break;
            }
            case DATE_TIME:{
                String dateFormat = AccountUtil.getCurrentOrg().getDateFormat()!=null ? AccountUtil.getCurrentOrg().getDateFormat().replaceAll("D","d").replaceAll("Y","y") : "dd-MMM-yyyy";
                TimeFormat timeFormat = AccountUtil.getCurrentOrg().getTimeFormatEnum();
                dateFormat = dateFormat + " " + ( timeFormat!=null && timeFormat == TimeFormat.HOUR_12 ? "hh:mm a" : "HH:mm");
                formattedValue = value!=null ? DateTimeUtil.getFormattedTime((Long) value,dateFormat) : "---";
                break;
            }
            case SYSTEM_ENUM: {
                SystemEnumField enumField = (SystemEnumField) field;
                formattedValue = value!=null ? enumField.getValue((int) value) : "---";
                break;
            }
            case ENUM:{
                EnumField enumField = (EnumField) field;
                formattedValue = value!=null ? enumField.getValue((int) value) : "---";
                break;
            }
            case STRING_SYSTEM_ENUM:{
                StringSystemEnumField enumField = (StringSystemEnumField) field;
                formattedValue = value!=null && enumField.getEnumMap()!=null ? (String) enumField.getEnumMap().get(value) : "---";
                break;
            }
            case LOOKUP:{
                LookupField lookupField = (LookupField) field;
                if(field.getDisplayType().equals(FacilioField.FieldDisplayType.ADDRESS)){
                    formattedValue = getAddress(value);
                    break;
                }
                FacilioField primaryField = modBean.getPrimaryField(lookupField.getLookupModule().getName());
                Map<String,Object> lookupFieldValue = (Map<String,Object>) value;
                if(lookupField.getLookupModule().getName().equals("users")){
                    formattedValue = lookupFieldValue.get("name")!=null ?  (String) lookupFieldValue.get("name") : "---";
                    break;
                }
                formattedValue = lookupFieldValue!=null && primaryField!=null && lookupFieldValue.get(primaryField.getName())!= null ? (String) lookupFieldValue.get(primaryField.getName()) : "---";

                break;
            }
            case BOOLEAN:{
                formattedValue = value!=null && (Boolean) value ? "Yes" : "No";
                break;
            }
            default:{
                formattedValue = value!=null ? value.toString() : "---";
                break;
            }
        }
        return formattedValue;
    }
    private String getAddress(Object value){
        Map<String,String> address = (Map<String,String>) value;
        if(value==null){
            return "---";
        }
        String formattedValue = "";
        if(address.get("street")!=null){
            formattedValue += address.get("street");
        }if ( address.get("city")!=null){
            formattedValue += ", " + address.get("city");
        }if ( address.get("state")!=null){
            formattedValue += ", " + address.get("state");
        }if ( address.get("zip")!=null){
            formattedValue += ", " + address.get("zip");
        }if ( address.get("country")!=null){
            formattedValue += ", " + address.get("country");
        }
        if(formattedValue.isEmpty()){
            return "---";
        }
        return formattedValue;
    }
}
