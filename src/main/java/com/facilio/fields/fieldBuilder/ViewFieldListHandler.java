package com.facilio.fields.fieldBuilder;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioChain;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class ViewFieldListHandler extends FieldListHandler<ViewFieldListBuilder> implements ViewFieldListBuilder{

    public ViewFieldListHandler(FieldConfig parent) {
        super(parent);
    }

    public List<String> getFixedFields() {
        return CollectionUtils.isNotEmpty(fixedFields)?Collections.unmodifiableList(fixedFields):null;
    }

    public List<String> getFixedSelectableFields() {
        return CollectionUtils.isNotEmpty(fixedSelectableFields)?Collections.unmodifiableList(fixedSelectableFields):null;
    }

    public Map<String, JSONObject> getCustomization() {
        return MapUtils.isNotEmpty(customization)?Collections.unmodifiableMap(customization):null;
    }

    private List<String> fixedFields;

    private List<String> fixedSelectableFields;

    private Map<String, JSONObject> customization;


    @Override
    public ViewFieldListBuilder addFixedFields(List<String> fieldNames) {
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            if (fixedFields == null) {
                fixedFields = new ArrayList<>();
            }
            fixedFields.addAll(fieldNames);
        }
        return this;
    }

    @Override
    public ViewFieldListBuilder addFixedSelectableFields(List<String> fieldNames) {
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            if (fixedSelectableFields == null) {
                fixedSelectableFields = new ArrayList<>();
            }
            fixedSelectableFields.addAll(fieldNames);
        }
        return this;
    }

    @Override
    public ViewFieldListBuilder addConfigForField(String fieldName, int width, String textWrap) {
        if(customization == null) {
            customization = new HashMap<>();
        }
        JSONObject columnCustomization = new JSONObject();
        columnCustomization.put("columnWidth", width);
        columnCustomization.put("text-wrap", textWrap);

        customization.put(fieldName, columnCustomization);

        return this;
    }
}
