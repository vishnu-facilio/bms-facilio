package com.facilio.telemetry.context;


import com.facilio.ns.context.NameSpaceField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list of NamespaceFields created to hold flat map of a namespace
 */
@NoArgsConstructor
@Getter
@Setter
public class NamespaceFieldsWrapper extends ArrayList<NameSpaceField> {
    private Long primaryResourceId;

    @Override
    public String toString() {
        return "NamespaceFieldsWrapper{" +
                " fields=" + super.toString() +
                " primaryResourceId=" + primaryResourceId +
                '}';
    }

    public NamespaceFieldsWrapper(List<NameSpaceField> flatList, Long primaryResourceId) {
        super(Collections.unmodifiableList(flatList));
        this.primaryResourceId = primaryResourceId;
    }
}
