package com.facilio.ns.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class NameSpaceContext {

    Long id;
    Long orgId;
    Long parentRuleId;
    Long execInterval;
    NSType type;
    List<NameSpaceField> fields;

    {
        fields = new ArrayList<>();
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

    private NameSpaceField getMaxIntervalField() {
        return fields.get(fields.size() - 1);
    }

    @JsonIgnore
    public Long intervalTimeWithMaxDuration() {
        NameSpaceField maxIntervalField = getMaxIntervalField();
        return maxIntervalField.getDataInterval() > getExecInterval() ?
                maxIntervalField.getDataInterval() : getExecInterval();
    }

    @JsonIgnore
    public String nsKey() {
        return "NS_" + id + "_LETS";
    }

    public NSType getTypeEnum() {
        return type;
    }

    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }

    public void setType(Integer type) {
        this.type = NSType.valueOf(type);
    }

    public void setNullForResponse() {

    }
}
