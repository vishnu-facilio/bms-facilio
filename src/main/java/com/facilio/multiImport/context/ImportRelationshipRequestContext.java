package com.facilio.multiImport.context;

import com.facilio.relation.context.RelationMappingContext;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
@Getter@Setter
public class ImportRelationshipRequestContext implements Serializable {
    private long id=-1l;
    private String name;
    private List<RelationMappingContext> mappings;
}
