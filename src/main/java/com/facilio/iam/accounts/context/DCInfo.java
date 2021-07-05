package com.facilio.iam.accounts.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DCInfo {
    private int dc;
    private String domain;
    private int appType;
}
