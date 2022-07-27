package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApplicationRelatedAppsContext implements Serializable {
    private long id=-1;
    private long applicationId=-1;
    private long relatedApplicationId=-1;
}
