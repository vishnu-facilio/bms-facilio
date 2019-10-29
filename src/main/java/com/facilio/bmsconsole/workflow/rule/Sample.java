package com.facilio.bmsconsole.workflow.rule;

import com.amazonaws.util.Base64;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class Sample implements Serializable {
    int sample = 12;
    public static void main(String[] args) {
        Sample sample = new Sample();
        sample.sample = 20;
        String serialize = Base64.encodeAsString(SerializationUtils.serialize(sample));
        System.out.println(serialize);

        Sample deserialize = SerializationUtils.deserialize(Base64.decode(serialize));
        System.out.println(deserialize.sample);
    }
}
