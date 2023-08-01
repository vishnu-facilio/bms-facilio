package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.unitconversion.Unit;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class V3TaskContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private static Logger log = LogManager.getLogger(V3TaskContext.class.getName());
    private Boolean preRequest;

    public Boolean isPreRequest() {
        if (preRequest != null) {
            return preRequest.booleanValue();
        }
        return false;
    }

    public void setPreRequest(Boolean preRequest) {
        this.preRequest = preRequest;
    }
    public void getPreRequest(Boolean preRequest) {
        this.preRequest = preRequest;
    }

    private String subject;
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private ResourceContext resource;
    public ResourceContext getResource() {
        return resource;
    }
    public void setResource(ResourceContext resource) {
        this.resource = resource;
    }

    private AssetContext asset;
    public AssetContext getAsset() {
        return asset;
    }
    public void setAsset(AssetContext asset) {
        this.asset = asset;
    }

    private SpaceContext space;
    public SpaceContext getSpace() {
        return space;
    }
    public void setSpace(SpaceContext space) {
        this.space = space;
    }

    public enum TaskStatus {
        OPEN,
        CLOSED
        ;

        public int getValue() {
            return ordinal() + 1;
        }

        public static V3TaskContext.TaskStatus valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values() [value -1];
            }
            return null;
        }

    }
    private FacilioStatus status;
    public FacilioStatus getStatus() {
        return status;
    }
    public void setStatus(FacilioStatus status) {
        this.status = status;
    }

    private V3TaskContext.TaskStatus statusNew;
    public V3TaskContext.TaskStatus getStatusNewEnum() {
        return statusNew;
    }
    public Integer getStatusNew() {
        if (statusNew != null) {
            return statusNew.getValue();
        }
        return null;
    }
    public void setStatusNew(Integer statusNew) {

        if(statusNew != null) {
            this.statusNew = V3TaskContext.TaskStatus.valueOf(statusNew);
        }
    }

    private Long parentTicketId;
    public Long getParentTicketId() {
        return parentTicketId;
    }
    public void setParentTicketId(Long parentTicketId) {
        this.parentTicketId = parentTicketId;
    }

    private Integer uniqueId;
    public Integer getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    private Long createdTime;
    public Long getCreatedTime() {
        return createdTime;
    }
    @TypeConversion(converter = "java.lang.String", value = "java.lang.String")
    public void setCreatedTime(String createdTime) {

        if(createdTime != null && !createdTime.isEmpty()) {
            if (NumberUtils.isDigits(createdTime)) {
                this.createdTime = FacilioUtil.parseLong(createdTime);
                return;
            }
            // TODO:VR Find cases where textual dates are sent.
            log.info("Textual Date received: " + createdTime);
            try {
                this.createdTime = FacilioConstants.HTML5_DATE_FORMAT.parse(createdTime).getTime();
            } catch (ParseException e) {
                try {
                    this.createdTime = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdTime).getTime();
                } catch (ParseException e1) {
                    log.info("Exception occurred ", e1);
                }
            }
        }
    }
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    private Long modifiedTime;
    public Long getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    private Long readingFieldId ;
    public Long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(Long readingId) {
        this.readingFieldId = readingId;
    }

    private FacilioField readingField;
    public FacilioField getReadingField() {
        return readingField;
    }
    public void setReadingField(FacilioField readingField) {
        this.readingField = readingField;
    }

    private Unit readingFieldUnit;

    public Unit getReadingFieldUnitEnum() {
        return readingFieldUnit;
    }

    public Integer getReadingFieldUnit() {
        if (readingFieldUnit != null) {
            return readingFieldUnit.getUnitId();
        }
        return null;
    }

    public void setReadingFieldUnit(Integer readingFieldUnit) {
        if(readingFieldUnit != null) {
            this.readingFieldUnit = Unit.valueOf(readingFieldUnit);
        }
    }


    private Long readingDataId ;
    public Long getReadingDataId() {
        return readingDataId;
    }
    public void setReadingDataId(Long readingDataId) {
        this.readingDataId = readingDataId;
    }

    private Object lastReading;
    public Object getLastReading() {
        return lastReading;
    }
    public void setLastReading(Object lastReading) {
        this.lastReading = lastReading;
    }

    private Long sectionId;
    public Long getSectionId() {
        return sectionId;
    }
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    private Integer sequence ;
    public Integer getSequence() {
        return sequence;
    }
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    private V3TaskContext.InputType inputType;
    public Integer getInputType() {
        if(inputType != null) {
            return inputType.getVal();
        }
        return null;
    }
    public void setInputType(Integer inputType) {

        if(inputType != null) {
            this.inputType = V3TaskContext.InputType.valueOf(inputType);
        }
    }
    public V3TaskContext.InputType getInputTypeEnum() {
        return inputType;
    }

    private String inputValue;
    public String getInputValue() {
        return inputValue;
    }
    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    private Long inputTime;
    public Long getInputTime() {
        return inputTime;
    }
    public void setInputTime(Long inputTime) {
        this.inputTime = inputTime;
    }

    private List<String> inputValues;
    public List<String> getInputValues() {
        return inputValues;
    }
    public void setInputValues(List<String> inputValues) {
        this.inputValues = inputValues;
    }

    private Boolean attachmentRequired;
    public Boolean getAttachmentRequired() {
        return attachmentRequired;
    }
    public void setAttachmentRequired(boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
    }
    public Boolean isAttachmentRequired() {
        if(attachmentRequired != null) {
            return attachmentRequired.booleanValue();
        }
        return false;
    }

    private Integer noOfAttachments = -1;
    public Integer getNoOfAttachments() {
        return noOfAttachments;
    }
    public void setNoOfAttachments(Integer noOfAttachments) {
        this.noOfAttachments = noOfAttachments;
    }

    private String remarks;
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    private List<String> options;
    public List<String> getOptions() {
        return options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }

    private List<ReadingRuleContext> readingRules;
    public List<ReadingRuleContext> getReadingRules() {
        return readingRules;
    }
    public void setReadingRules(List<ReadingRuleContext> readingRules) {
        this.readingRules = readingRules;
    }

    private List<List<ActionContext>> actionsList;
    public List<List<ActionContext>> getActionsList() {
        return this.actionsList;
    }

    public void setActionsList(List<List<ActionContext>> actionsList) {
        this.actionsList = actionsList;
    }

    public enum InputType {
        NONE,
        READING,
        TEXT,
        NUMBER,
        RADIO,
        //		CHECKBOX,
        BOOLEAN
        ;

        public int getVal() {
            return ordinal()+1;
        }

        public static V3TaskContext.InputType valueOf(int val) {
            if(val > 0 && val <= values().length) {
                return values()[val - 1];
            }
            return null;
        }
    }

    private User createdBy;
    public User getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    private String defaultValue;
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private Long actionId;
    public Long getActionId() {
        return actionId;
    }
    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    private Long woCreateTemplateId;
    public Long getWoCreateTemplateId() {
        return woCreateTemplateId;
    }
    public void setWoCreateTemplateId(Long woCreateTemplateId) {
        this.woCreateTemplateId = woCreateTemplateId;
    }

    private Long woCreateFormId;
    public Long getWoCreateFormId() {
        return woCreateFormId;
    }
    public void setWoCreateFormId(Long woCreateFormId) {
        this.woCreateFormId = woCreateFormId;
    }
    private ActionContext action;
    public ActionContext getAction() {
        return action;
    }
    public void setAction(ActionContext action) {
        this.action = action;
    }

    private Boolean failed;
    public Boolean getFailed() {
        return failed;
    }
    public void setFailed(Boolean failed) {
        this.failed = failed;
    }
    public Boolean isFailed() {
        if(failed != null) {
            return failed.booleanValue();
        }
        return false;
    }

    private String truevalue;

    public String getTruevalue() {
        return truevalue;
    }

    public void setTruevalue(String truevalue) {
        this.truevalue = truevalue;
    }

    private String falsevalue;

    public String getFalsevalue() {
        return falsevalue;
    }

    public void setFalsevalue(String falsevalue) {
        this.falsevalue = falsevalue;
    }
    private String failureValue;
    public String getFailureValue() {
        return failureValue;
    }
    public void setFailureValue(String failureValue) {
        this.failureValue = failureValue;
    }

    private Integer deviationOperatorId;
    public Integer getDeviationOperatorId() {
        if(deviationOperator != null) {
            return deviationOperator.getOperatorId();
        }
        return deviationOperatorId;
    }
    public void setDeviationOperatorId(Integer operatorId) {
        this.deviationOperatorId = operatorId;
        this.deviationOperator = operatorId != null && operatorId > 0 ? Operator.getOperator(operatorId) : null;
    }

    private Operator deviationOperator;
    @JsonIgnore
    public Operator getDeviationOperator() {
        return deviationOperator;
    }
    public void setDeviationOperator(Operator operator) {
        this.deviationOperatorId = operator.getOperatorId();
        this.deviationOperator = operator;
    }

    private Long syncTime;
    public Long getSyncTime() {
        return syncTime;
    }
    public void setSyncTime(Long syncTime) {
        this.syncTime = syncTime;
    }

    private Boolean remarksRequired;

    public Boolean getRemarksRequired() {
        return remarksRequired;
    }
    public void setRemarksRequired(Boolean remarksRequired) {
        this.remarksRequired = remarksRequired;
    }

    public Boolean isRemarksRequired() {
        if(remarksRequired != null) {
            return remarksRequired.booleanValue();
        }
        return false;
    }

    // declaration of remarkOptionValues
    private List<String> remarkOptionValues;

    public List<String> getRemarkOptionValues() throws Exception {

        if (remarkOptionValues == null && remarkOptionValuesString != null && remarkOptionValuesString.length() > 0) {
            List<String> valueList = new ArrayList<String>(Arrays.asList(remarkOptionValuesString.split(",")));
            return valueList;
        }
        return remarkOptionValues;
    }
    public void setRemarkOptionValues(List<String> remarkOptionValues) {
        this.remarkOptionValues = remarkOptionValues;
    }

    // declaration of remarkOptionValuesString
    private String remarkOptionValuesString;

    public String getRemarkOptionValuesString() {
        if (remarkOptionValues != null && remarkOptionValues.size() != 0) {
            String valueString = StringUtils.join(remarkOptionValues, ",");
            return valueString;
        }
        return remarkOptionValuesString;
    }

    public void setRemarkOptionValuesString(String remarkOptionValuesString) {
        this.remarkOptionValuesString = remarkOptionValuesString;
    }

    // declaration of attachmentOptionValuesString
    private String attachmentOptionValuesString;
    public String getAttachmentOptionValuesString() {
        if (CollectionUtils.isNotEmpty(attachmentOptionValues)) {
            return StringUtils.join(attachmentOptionValues, ",");
        }
        return null;
    }
    public void setAttachmentOptionValuesString(String attachmentOptionValuesString) {
        this.attachmentOptionValuesString = attachmentOptionValuesString;
    }

    // declaration of attachmentOptionValues
    private List<String> attachmentOptionValues;
    public List<String> getAttachmentOptionValues() throws Exception {
        if (attachmentOptionValues == null && attachmentOptionValuesString != null && attachmentOptionValuesString.length() > 0) {
            List<String> valueList = new ArrayList<String>(Arrays.asList(attachmentOptionValuesString.split(",")));
            return valueList;
        }
        return attachmentOptionValues;
    }
    public void setAttachmentOptionValues(List<String> attachmentOptionValues) {
        this.attachmentOptionValues = attachmentOptionValues;
    }

    @JsonIgnore
    private V3WorkOrderContext parentWo;

    // declarations for additionalInfoJsonStr

    // should remove all these
    private String additionalInfoJsonStr;
    public String getAdditionalInfoJsonStr() {
        if(getAdditionInfo() != null) {
            return getAdditionInfo().toJSONString();
        }
        return null;
    }
    public void setAdditionalInfoJsonStr(String jsonStr) throws org.json.simple.parser.ParseException {
        if(jsonStr != null) {
            JSONParser parser = new JSONParser();
            setAdditionInfo((JSONObject) parser.parse(jsonStr));
        }
    }

    // declarations for additionInfo
    private JSONObject additionInfo;
    public JSONObject getAdditionInfo() {
        if(additionInfo == null){
            return  new JSONObject();
        }
        return additionInfo;
    }
    public void setAdditionInfo(JSONObject additionInfo) {
        this.additionInfo = additionInfo;
    }

    public void addAdditionInfo(String key, Object value) {
        if(this.additionInfo == null) {
            this.additionInfo =  new JSONObject();
        }
        this.additionInfo.put(key,value);
    }

    // declarations for createWoOnFailure
    private Boolean createWoOnFailure;
    public Boolean getCreateWoOnFailure() {
        if(createWoOnFailure == null && getAdditionInfo().containsKey("createWoOnFailure")){
            return (Boolean) getAdditionInfo().get("createWoOnFailure");
        }
        return createWoOnFailure;
    }
    public void setCreateWoOnFailure(Boolean createWoOnFailure) {
        addAdditionInfo("createWoOnFailure", createWoOnFailure);
        this.createWoOnFailure = createWoOnFailure;
    }

    @JSON(serialize = false)
    public V3WorkOrderContext getParentWo() {
        return parentWo;
    }
    @JSON(serialize = false)
    public void setParentWo(V3WorkOrderContext parentWo) {
        this.parentWo = parentWo;
    }
}
