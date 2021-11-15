package com.facilio.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.ins.InsExtension;

import java.util.Arrays;
import java.util.List;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;

public class MarkDownUtil {
	public static List<Extension> extensions = Arrays.asList(AutolinkExtension.create() , TaskListItemsExtension.create() , StrikethroughExtension.create() , TablesExtension.create() , InsExtension.create());
	public static Parser parser = Parser.builder().extensions(extensions).build();
	public static String getHTMLRender(String note)
	{
		try {
		Node document = parser.parse(note);
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).attributeProviderFactory(new AttributeProviderFactory() {
            public AttributeProvider create(AttributeProviderContext context) {
                return new ImageAttributeProvider();
            }
        }).escapeHtml(true).build();
		return (renderer.render(document));
		}
		catch(Exception e) {
			return note;
		}
	}
	public static String getTextRender(String note)
	{
		try
		{
		Node document = parser.parse(note);
		TextContentRenderer renderer = TextContentRenderer.builder().extensions(extensions).build();
		return (renderer.render(document));
		}
		catch(Exception e) {
			return note;
			
		}
	}

}
