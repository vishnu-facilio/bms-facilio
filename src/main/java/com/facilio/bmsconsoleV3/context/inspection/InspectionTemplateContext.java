package com.facilio.bmsconsoleV3.context.inspection;

import java.util.List;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.qa.context.QAndATemplateContext;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@NoArgsConstructor
public class InspectionTemplateContext extends QAndATemplateContext {
    private SiteContext site;

    List<InspectionTriggerContext> triggers;
    public InspectionTemplateContext(long id) {
        super(id);
    }
    
    
}
