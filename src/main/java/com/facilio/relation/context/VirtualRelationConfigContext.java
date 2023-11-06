package com.facilio.relation.context;

import com.facilio.modules.FacilioModule;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class VirtualRelationConfigContext implements Serializable {
    FacilioModule fromModule;
    FacilioModule toModule;
    private long relationId;
    List<RelationContext> relationList;
}
