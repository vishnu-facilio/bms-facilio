package com.facilio.bmsconsole.workflow.rule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class CustomButtonAppRelContext implements Serializable {

    long id;
    long appId;
    long customButtonId;
}
