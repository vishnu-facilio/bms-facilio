package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class ServiceCatalogGroupContext implements Serializable {

    private long id = -1;
    private String name;
    private String description;
    private String linkName;
}
