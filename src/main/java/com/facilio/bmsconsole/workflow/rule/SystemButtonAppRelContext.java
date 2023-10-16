package com.facilio.bmsconsole.workflow.rule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SystemButtonAppRelContext implements Serializable {
    long id;
    long appId;
    long systemButtonId;
}
