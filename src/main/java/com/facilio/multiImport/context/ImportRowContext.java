package com.facilio.multiImport.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class ImportRowContext implements Serializable {
    private long id;
    private long importSheetId;
    private long rowNumber;
    private String rowContent;
    private String errorMessage;

    public void setErrorMessage(String errorMessage) {
        if(StringUtils.isNotEmpty(errorMessage) && errorMessage.length() > 255){
           errorMessage = errorMessage.substring(0,255).trim();
        }
        this.errorMessage = errorMessage;
    }

    private RowStatus rowStatus;
    private boolean errorOccuredRow;
    public void setRowStatus(int rowStatus) {
        this.rowStatus = RowStatus.valueOf(rowStatus);
    }

    public void setRowStatus(RowStatus rowStatus) {
        this.rowStatus = rowStatus;
    }

    public int getRowStatus() {
        return rowStatus!=null? rowStatus.getValue() : -1;
    }
    public RowStatus getStatusTypeEnum(){
        return rowStatus;
    }

    @JsonIgnore
    public Map<String, Object> getRawRecordMap() {
        if(MapUtils.isEmpty(rawRecordMap) && StringUtils.isNotEmpty(rowContent)){
            rawRecordMap=new HashMap<>();
            JSONParser parser = new JSONParser();
            try{
                rawRecordMap = (JSONObject) parser.parse(rowContent);
            }catch (Exception ex){

            }
        }
        return rawRecordMap;
    }

    @Getter(AccessLevel.NONE)
    private Map<String,Object> rawRecordMap;
    private Map<String,Object> processedRawRecordMap;

    public enum RowStatus{
        ADDED,
        UPDATED,
        SKIPPED;

        public int getValue(){
            return ordinal()+1;
        }
        public static RowStatus valueOf(Integer status){
            return ROW_STATUS.get(status);
        }
        private static final Map<Integer,RowStatus> ROW_STATUS = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, RowStatus> initTypeMap() {
            Map<Integer, RowStatus> typeMap = new HashMap<>();
            for(RowStatus status : values()) {
                typeMap.put(status.getValue(), status);
            }
            return typeMap;
        }
    }


}
