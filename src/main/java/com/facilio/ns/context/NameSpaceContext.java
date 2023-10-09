package com.facilio.ns.context;

import com.facilio.annotations.ImmutableChildClass;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@ImmutableChildClass(className = "NameSpaceCacheContext")
public class NameSpaceContext implements Serializable {

    public NameSpaceContext(NameSpaceContext ns) {
        this.id = ns.id;
        this.orgId = ns.orgId;
        this.parentRuleId = ns.parentRuleId;
        this.execInterval = ns.execInterval;
        this.includedAssetIds = ns.includedAssetIds;
        this.type = ns.type;
        this.assetCategoryId = ns.assetCategoryId;
        this.workflowId = ns.workflowId;
        this.categoryId = ns.categoryId;
        this.workflowContext = ns.workflowContext;
        this.status = ns.status;
        this.fields = ns.fields;
        this.loggerLevel = ns.loggerLevel;
        this.assetCategoryContext = ns.assetCategoryContext;
        this.execMode = ns.execMode;
    }

    /**
     * this constructor is used in storm to clone NameSpaceContext
     */
    public NameSpaceContext(NameSpaceCacheContext ns) {
        this.id = ns.id;
        this.orgId = ns.orgId;
        this.parentRuleId = ns.parentRuleId;
        this.execInterval = ns.execInterval;
        this.includedAssetIds = ns.includedAssetIds;
        this.type = ns.type;
        this.assetCategoryId = ns.assetCategoryId;
        this.workflowId = ns.workflowId;
        this.categoryId = ns.categoryId;
        this.workflowContext = ns.workflowContext;
        this.status = ns.status;
        this.fields = ns.fields;
        this.loggerLevel = ns.loggerLevel;
        this.assetCategoryContext = ns.assetCategoryContext;
        this.execMode = ns.execMode;
    }

    Long id;

    Long orgId;

    Long parentRuleId;

    Long execInterval;

    List<Long> includedAssetIds;

    NSType type;

    Long assetCategoryId;

    Long categoryId;

    Integer execMode;

    public void setExecMode(int mode) {
        this.execMode = mode;
        this.execModeEnum = NSExecMode.valueOf(mode);
    }

    NSExecMode execModeEnum;
    public void setExecModeEnum(NSExecMode mode) {
        this.execMode = mode.getValue();
        this.execModeEnum = mode;
    }

    Long executorId;

    Long workflowId;

    WorkflowContext workflowContext;

    Boolean status;

    List<NameSpaceField> fields;

    int loggerLevel;

    V3AssetCategoryContext assetCategoryContext;

    {
        fields = new ArrayList<>();
    }

    public NameSpaceContext(NSType type, Long parentRuleId, Long execInterval, Long workflowId, Long categoryId) {
        this.type = type;
        this.parentRuleId = parentRuleId;
        this.execInterval = execInterval;
        this.workflowId = workflowId;
        this.categoryId = categoryId;
    }

    public void addField(NameSpaceField... fs) {
        Collections.addAll(fields, fs);
        fields.sort(Comparator.comparing(NameSpaceField::getDataInterval)); //mutate with ascending order
    }

    /**
     * @param resourceId - Asset Id
     * @param fieldId    - Field Id
     * @return Returns Group Fields which has match with resource id and field id.
     */
    public List<NameSpaceField> getFields(long resourceId, long fieldId) {
        Map<String, NameSpaceField> flds = new HashMap<>();
        for (NameSpaceField f : fields) {
            String key = f.fieldKey();
            if (!flds.containsKey(key) && f.getResourceId() == resourceId && f.getFieldId() == fieldId) {
                flds.put(key, f);
            }
        }
        return new ArrayList<>(flds.values());
    }

    /**
     * @param resourceId - Resource Id
     * @param fieldId    - Field Id
     * @return
     */
    public NameSpaceField getField(long resourceId, long fieldId, AggregationType typ) {
        for (NameSpaceField f : fields) {
            if (f.getResourceId() == resourceId && f.getFieldId() == fieldId && f.getAggregationType() == typ) {
                return f;
            }
        }
        return null;
    }

    public NSType getTypeEnum() {
        return type;
    }

    public int getType() {
        return (type != null) ? type.getIndex() : -1;
    }

    public void setType(Integer type) {
        this.type = NSType.valueOf(type);
    }

    public WorkflowContext getWorkflowContext() throws Exception {
        if (workflowContext != null) {
            return this.workflowContext;
        }
        if (workflowId != null) {
            return this.workflowContext = WorkflowUtil.getWorkflowContext(workflowId);
        }
        return null;
    }
}
