package com.facilio.modules;

import com.facilio.modules.fields.UrlField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlRecord extends BaseSystemLookupRecord {
    private String href, name, alt;
    private UrlField.UrlTarget target;
}
