package com.facilio.permission.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
public class PermissionSetContext implements Serializable {
    private long id;
    private String displayName;
    private String description;
    private long orgId;
    private long sysCreatedTime = -1L;
    private long sysModifiedTime = -1L;
    private long sysDeletedTime = -1L;
    private Long sysCreatedBy;
    private Long sysModifiedBy;
    private Long sysDeletedBy;
    private Boolean status;
}