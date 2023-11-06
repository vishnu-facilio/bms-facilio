package com.facilio.fields.fieldBuilder;

import org.json.simple.JSONObject;

import java.util.List;

public interface ViewFieldListBuilder extends FieldListBuilder<ViewFieldListBuilder>{

    ViewFieldListBuilder addFixedFields(List<String> fieldNames);

    ViewFieldListBuilder addFixedSelectableFields(List<String> fieldNames);

    ViewFieldListBuilder addConfigForField(String fieldName, int width, String testWrap);

}
