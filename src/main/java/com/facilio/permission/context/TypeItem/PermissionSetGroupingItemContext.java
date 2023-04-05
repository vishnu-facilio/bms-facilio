package com.facilio.permission.context.TypeItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
public class PermissionSetGroupingItemContext implements Serializable {
    long id;
    String displayName;

    public PermissionSetGroupingItemContext(long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
}