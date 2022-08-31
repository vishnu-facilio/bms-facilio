package com.facilio.classification.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ClassificationContext extends V3Context {

    private String name;
    private String linkName;
    private String description;
    private ClassificationContext parentClassification;
    private Boolean extendAttribute;
    private Set<Long> appliedModuleIds;

    private String classificationPath;
    public void setClassificationPath(String classificationPath) {
        if (StringUtils.isNotEmpty(classificationPath)) {
            this.classificationPath = "All / " + classificationPath;
        } else {
            this.classificationPath = classificationPath;
        }
    }

}
