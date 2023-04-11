package com.facilio.bmsconsoleV3.context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PeopleTypeField {
    private Long id;
    private String name;
    private List<String> fields;

    public PeopleTypeField(Long peopleId, String name, List<String> fields) {
        this.id = peopleId;
        this.name = name;
        this.fields = fields;
    }
}