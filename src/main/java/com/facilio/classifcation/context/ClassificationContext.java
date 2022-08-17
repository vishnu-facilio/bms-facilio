package com.facilio.classifcation.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ClassificationContext extends V3Context {

    private String name;
    private String linkName;
    private String description;
    private ClassificationContext parentClassification;
    private long classificationModuleId = -1;
    private Boolean extendAttribute;
    private Set<Long> appliedModuleIds;

    private String classificationPath;

    private List<ClassificationAttributeContext> attributes;
}
