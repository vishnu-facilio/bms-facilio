package com.facilio.report.formatter;

import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiEnumField;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.StringJoiner;

public class MultiEnumFormatter extends Formatter{
    protected MultiEnumFormatter(FacilioField field) {
        super(field);
    }

    @Override
    public Object format(Object value) {

        MultiEnumField multiEnumField = (MultiEnumField) getField();
        List<Integer> values = (List<Integer>) value;
        StringJoiner valueString = new StringJoiner(", ");
        for(int index : values){
            valueString.add(multiEnumField.getValue(index));
        }
        return valueString.toString();
    }

    @Override
    public JSONObject serialize() {
        return new JSONObject();
    }

    @Override
    public void deserialize(JSONObject formatJSON) {

    }
}
