package com.facilio.bmsconsole.context;

import lombok.Data;

import java.util.List;

@Data
public class ScopingExtendedPropContext extends ScopingContext{
    List<Long> peopleIds;
}
