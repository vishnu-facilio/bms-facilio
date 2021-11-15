package com.facilio.util;

import java.util.Map;

import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.AttributeProvider;

public class ImageAttributeProvider implements AttributeProvider {
    @Override
    public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
        if (node instanceof Image) {
            attributes.put("referrerpolicy", "no-referrer");
        }
        if(node instanceof Link)
        {
        	attributes.put("referrerpolicy", "no-referrer");
        	attributes.put("target", "_blank");
        }
    }
}
