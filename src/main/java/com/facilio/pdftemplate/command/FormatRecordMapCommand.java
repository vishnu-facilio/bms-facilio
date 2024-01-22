package com.facilio.pdftemplate.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.*;
import com.facilio.pdftemplate.context.PDFTemplate;
import com.facilio.report.formatter.NumberFormatter;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.TimeFormat;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormatRecordMapCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PDFTemplate pdfTemplate = (PDFTemplate) context.get(FacilioConstants.ContextNames.PDF_TEMPLATE);
        long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        if(pdfTemplate==null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR,"There is no PDF Template mapped to the page layout");
        }
        String moduleName = Constants.getModBean().getModule(pdfTemplate.getModuleId()).getName();
        List<Long> recordIds = new ArrayList<>();
        recordIds.add(recordId);
        FacilioContext recordMapContext = V3Util.getSummary(moduleName,recordIds);
        ModuleBaseWithCustomFields recordContext = Constants.getRecordMap(recordMapContext).get(moduleName).get(0);
        Map<String, Object> recordMap = FieldUtil.getAsProperties(recordContext);
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String,Object> formattedRecordMap =  getMapClone(recordMap);
        for(FacilioField field :  fields){
            String fieldName = field.getName();
            Object fieldValue = getFieldValue(recordMap,field,context);
            String formattedValue = getFormattedValueBasedOnFieldDisplayType(field,fieldValue,context);
            formattedRecordMap.put(fieldName.concat("Formatted"),formattedValue);
        }
        context.put(FacilioConstants.ContextNames.FORMATTED_RECORD_MAP,formattedRecordMap);
        return false;
    }
    private Object getFieldValue(Map<String, Object> recordMap,FacilioField field,Context context) throws Exception{
        String fieldName = field.getName();
        Object fieldValue;
        switch (field.getDataTypeEnum()){
            case FILE:{
                fieldValue = recordMap.get(fieldName + "FileName");
                break;
            }
            case MULTI_CURRENCY_FIELD:{
                CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
                double currencyValue = (double) recordMap.get(fieldName);
                double exchangeRate = (double) recordMap.get("exchangeRate");
                double baseCurrencyValue = CurrencyUtil.getConvertedBaseCurrencyValue(currencyValue, exchangeRate);
                Map<String,Object> currencyValueMap = new HashMap<>();
                currencyValueMap.put("currencyCode",recordMap.get("currencyCode"));
                currencyValueMap.put("currencyValue",currencyValue);
                currencyValueMap.put("baseCurrencyCode",baseCurrency.getCurrencyCode());
                currencyValueMap.put("baseCurrencyValue",baseCurrencyValue);
                fieldValue = currencyValueMap;
                break;
            }
            default:{
                fieldValue = recordMap.get(fieldName);
                break;
            }
        }
        return fieldValue;
    }
    private Map<String,Object> getMapClone(Map<String, Object> recordMap){
        Map<String,Object> cloneMap = new HashMap<>();
        for(Map.Entry<String,Object> entry : recordMap.entrySet()){
            cloneMap.put(entry.getKey(), entry.getValue());
        }
        return cloneMap;
    }
    private String getFormattedValueBasedOnFieldDisplayType(FacilioField field, Object value, Context context) throws Exception{
        ModuleBean modBean = Constants.getModBean();
        String formattedValue;
        switch (field.getDataTypeEnum()){
            case NUMBER:{
                if(field.getDisplayType().equals(FacilioField.FieldDisplayType.DURATION) && value!=null){
                    formattedValue = NumberFormatter.relativeTime(Long.parseLong((((Long)value)*1000)+""));
                }else{
                    formattedValue = value!=null ? value.toString() : "---";
                }
                break;
            }
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
            case MULTI_ENUM:{
                MultiEnumField multiEnumField = (MultiEnumField) field;
                List<Integer> values = (List<Integer>) value;
                formattedValue = CollectionUtils.isNotEmpty(values) ? values.stream().map(val -> multiEnumField.getValue(val)).collect(Collectors.joining(", ")) : "---";
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
                    formattedValue = lookupFieldValue!=null && lookupFieldValue.get("name")!=null ?  (String) lookupFieldValue.get("name") : "---";
                    break;
                }
                formattedValue = lookupFieldValue!=null && primaryField!=null && lookupFieldValue.get(primaryField.getName())!= null ? (String) lookupFieldValue.get(primaryField.getName()) : "---";
                break;
            }
            case MULTI_LOOKUP:{
                MultiLookupField multiLookupField = (MultiLookupField) field;
                FacilioField primaryField = modBean.getPrimaryField(multiLookupField.getLookupModule().getName());
                List<Map<String,String>> values = (List<Map<String,String>>) value;
                formattedValue = CollectionUtils.isNotEmpty(values) &&  primaryField!=null ? values.stream().map(val -> val.get(primaryField.getName())).collect(Collectors.joining(", ")) : "---";
                break;
            }
            case BOOLEAN:{
                formattedValue = value!=null && (Boolean) value ? "Yes" : "No";
                break;
            }
            case URL_FIELD:{
                Map<String,String> url = (Map<String, String>) value;
                formattedValue = url!=null &&  url.get("href")!=null ? url.get("href") : "---";
                break;
            }
            case FILE:{
                if(field.getDisplayType().equals(FacilioField.FieldDisplayType.SIGNATURE)){
                    formattedValue = value!=null ? "Signed" : "Not Signed";
                    break;
                }
                formattedValue = value!=null ? value.toString() : "---";
                break;
            }
            case MULTI_CURRENCY_FIELD:{
                Map<String, String> currency = (Map<String, String>) value;
                if(currency!=null){
                    formattedValue =  currency.get("currencyCode") + " " + currency.get("currencyValue");
                    if(!currency.get("currencyCode").equals(currency.get("baseCurrencyCode"))){
                        formattedValue += " (" + currency.get("baseCurrencyCode") + " " + currency.get("baseCurrencyValue") + ") ";
                    }
                    break;
                }
                formattedValue = "---";
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
