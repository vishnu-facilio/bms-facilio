package com.facilio.multiImport.context;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.enums.ImportFieldMappingType;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter@Setter
public class ImportFileSheetsContext implements Serializable {
    private long id = -1l;
    private String name;
    private int sheetIndex = -1;
    private long processedRowCount;
    private long lastRowIdTaken=-1l;
    private long importFileId;
    private long rowCount = -1;
    private FacilioModule module;
    private long moduleId = -1l;
    private String moduleName;
    private long executionOrder=-1L;
    private List<ImportFieldMappingContext> fieldMapping;
    private Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping;
    private Map<Long,String> fieldIdVsSheetColumnName;
    private Map<String,String> fieldNameVsSheetColumnName;
    private String columnHeadingString,insertBy,updateBy,firstRowString,secondRowString;
    private JSONObject firstRow,secondRow;
    private long insertCount,skipCount,updateCount,failCount;
    private MultiImportSetting importSetting;
    private List<ImportFieldMappingContext> relationFieldMapping;
    private Map<ImportFieldMappingType,List<ImportFieldMappingContext>> typeVsFieldMappings;
    public void setImportSetting(int importSetting) {
        this.importSetting = MultiImportSetting.valueOf(importSetting);
    }
    public void setImportSetting(MultiImportSetting importSetting) {
        this.importSetting = importSetting;
    }

    public int getImportSetting() {
        return importSetting!=null ? importSetting.getValue() : -1;
    }
    public MultiImportSetting getImportSettingEnum() {
       return importSetting;
    }

    ImportDataStatus status;
    public void setStatus(int status) {
        this.status = ImportDataStatus.valueOf(status);
    }
    public void setStatus(ImportDataStatus status) {
        this.status = status;
    }
    public int getStatus() {
        return status!=null ? status.getValue() : 1;
    }
    public ImportDataStatus getStatusEnum(){
        return status;
    }

    public JSONObject getFirstRow() throws ParseException {
        if(StringUtils.isNotEmpty(firstRowString)){
            JSONParser parser = new JSONParser();
            firstRow=(JSONObject)parser.parse(firstRowString);
        }
        return firstRow;
    }

    public void setFirstRow(JSONObject firstRow) {
        this.firstRow = firstRow;
    }

    public JSONObject getSecondRow() throws ParseException {
        if(StringUtils.isNotEmpty(secondRowString)){
            JSONParser parser = new JSONParser();
            secondRow=(JSONObject)parser.parse(secondRowString);
        }
        return secondRow;
    }

    public void setSecondRow(JSONObject secondRow) {
        this.secondRow = secondRow;
    }

    public List<String> getInsertByFieldsList(){
        if(StringUtils.isNotEmpty(insertBy)){
            JSONParser parser = new JSONParser();
            try{
                return (List<String>) parser.parse(insertBy);
            }catch (Exception e){
                return null;
            }

        }
        return null;
    }
    public List<String> getUpdateByFieldsList(){
        if(StringUtils.isNotEmpty(updateBy)){
            JSONParser parser = new JSONParser();
            try {
                return (List<String>) parser.parse(updateBy);
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }
    public FacilioModule getModule() throws Exception{
        if(module==null && moduleId!=-1l){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            module=modBean.getModule(moduleId);
        }
        return module;
    }
    public String getModuleName() throws Exception {
        this.getModule();
        if(module!=null){
            moduleName=module.getName();
        }
        return moduleName;
    }


    @JsonIgnore
    public Map<String, ImportFieldMappingContext> getSheetColumnNameVsFieldMapping() {
        if(CollectionUtils.isNotEmpty(fieldMapping)&& MapUtils.isEmpty(sheetColumnNameVsFieldMapping)){
            sheetColumnNameVsFieldMapping=new HashMap<>();
            for(ImportFieldMappingContext fieldMapping:fieldMapping){
                String sheetColumnName=fieldMapping.getSheetColumnName();
                sheetColumnNameVsFieldMapping.put(sheetColumnName,fieldMapping);
            }
        }
        return sheetColumnNameVsFieldMapping;
    }
    @JsonIgnore
    public Map<Long,String> getFieldIdVsSheetColumnNameMap(){
        if(CollectionUtils.isNotEmpty(fieldMapping)&& MapUtils.isEmpty(fieldIdVsSheetColumnName)){
            fieldIdVsSheetColumnName=new HashMap<>();
            for(ImportFieldMappingContext fieldMapping:fieldMapping){
                Long fieldId=fieldMapping.getFieldId();
                if(fieldId == -1l){ //skip if fieldId is empty
                    continue;
                }
                String sheetColumnName=fieldMapping.getSheetColumnName();
                fieldIdVsSheetColumnName.put(fieldId,sheetColumnName);
            }
        }
        return fieldIdVsSheetColumnName;
    }
    @JsonIgnore
    @JSON(serialize = false)
    public Map<ImportFieldMappingType, List<ImportFieldMappingContext>> getTypeVsFieldMappings() {
        if(CollectionUtils.isNotEmpty(fieldMapping) && MapUtils.isEmpty(typeVsFieldMappings)){
            typeVsFieldMappings = fieldMapping.stream().collect(Collectors.groupingBy(ImportFieldMappingContext::getTypeEnum));
        }
        return typeVsFieldMappings;
    }

    @JsonIgnore
    public Map<String,String> getFieldNameVsSheetColumnNameMap(){
        if(CollectionUtils.isNotEmpty(fieldMapping)&& MapUtils.isEmpty(fieldNameVsSheetColumnName)){
            fieldNameVsSheetColumnName=new HashMap<>();
            for(ImportFieldMappingContext fieldMapping:fieldMapping){
                String fieldName=fieldMapping.getFieldName();
                if(StringUtils.isEmpty(fieldName)){ //skip if fieldName is empty
                    continue;
                }
                String sheetColumnName=fieldMapping.getSheetColumnName();
                fieldNameVsSheetColumnName.put(fieldName,sheetColumnName);
            }
        }
        return fieldNameVsSheetColumnName;
    }

    public JSONArray getColumnHeadings() throws Exception
    {
        if(StringUtils.isNotEmpty(columnHeadingString)) {
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(getColumnHeadingString());
        }
        return null;
    }
    public boolean isHasFieldMappingDependencies() {
        if(CollectionUtils.isNotEmpty(fieldMapping)){
            return true;
        }else {
            return false;
        }
    }
    @JsonIgnore
    @JSON(serialize = false)
    public List<ImportFieldMappingContext> getRelationFieldMapping() {
        if(CollectionUtils.isNotEmpty(fieldMapping)&& CollectionUtils.isEmpty(relationFieldMapping)){
            relationFieldMapping=fieldMapping.stream().filter(p->p.getTypeEnum()==ImportFieldMappingType.RELATIONSHIP).collect(Collectors.toList());
        }
        return relationFieldMapping;
    }

    @Override
    public String toString() {
        return "ImportFileSheetsContext{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sheetIndex=" + sheetIndex +
                ", status=" + status +
                ", processedRowCount=" + processedRowCount +
                ", lastRowIdTaken=" + lastRowIdTaken +
                ", importFileId=" + importFileId +
                ", rowCount=" + rowCount +
                ", module=" + module +
                ", moduleId=" + moduleId +
                ", moduleName='" + moduleName + '\'' +
                ", executionOrder=" + executionOrder +
                ", fieldMapping=" + fieldMapping +
                ", sheetColumnNameVsFieldMapping=" + sheetColumnNameVsFieldMapping +
                ", fieldIdVsSheetColumnName=" + fieldIdVsSheetColumnName +
                ", fieldNameVsSheetColumnName=" + fieldNameVsSheetColumnName +
                ", columnHeadingString='" + columnHeadingString + '\'' +
                ", insertBy='" + insertBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", firstRowString='" + firstRowString + '\'' +
                ", secondRowString='" + secondRowString + '\'' +
                ", firstRow=" + firstRow +
                ", secondRow=" + secondRow +
                ", insertCount=" + insertCount +
                ", skipCount=" + skipCount +
                ", updateCount=" + updateCount +
                ", importSetting=" + importSetting +
                '}';
    }
}
