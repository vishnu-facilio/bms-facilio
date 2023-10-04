package com.facilio.pdftemplate.context;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Setter
@Getter
public class PDFTemplate {

    private long id;
    private long orgId;
    private String name;
    private String description;
    private long moduleId;
    private String headerTemplate;
    private String footerTemplate;
    private long htmlContentId;
    private String htmlContent;
    private String htmlContentCss;
    private long htmlContentCssId;
    private String templateSettings;
    private long sysCreatedTime;
    private long sysCreatedBy;
    private long sysModifiedTime;
    private long sysModifiedBy;

    public JSONArray getTemplateSettingsJSON() throws Exception {
        JSONArray templateSettingsJSON = new JSONArray();
        if (StringUtils.isNotEmpty(templateSettings)) {
            templateSettingsJSON = (JSONArray) new JSONParser().parse(templateSettings);
        }
        return templateSettingsJSON;
    }

    public void setTemplateSettingsJSON(JSONArray templateSettingsJSON) {
        if (templateSettingsJSON != null) {
            this.templateSettings = templateSettingsJSON.toJSONString();
        }
    }
}
