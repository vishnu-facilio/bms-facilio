package com.facilio.remotemonitoring.wms;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ControllerOfflineJobInfoContext implements Serializable {
    private Long timeStamp;
}
