package com.facilio.qa.context;

import com.facilio.util.FacilioEnumClassTypeIdResolverBase;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QAndATemplateContext extends V3Context {
    private String name;
    private String description;
    private QAndAType typeEnum;
    private Long responseModuleId;

    private List<PageContext> pages;

    public QAndATemplateContext (Long id) {
        super(id);
    }

    public Integer getType() {
        return typeEnum == null ? null : typeEnum.getIndex();
    }
    public void setType(Integer type) {
        typeEnum = type == null ? null : QAndAType.valueOf(type);
    }

    public static class QAndATypeIdResolver extends FacilioEnumClassTypeIdResolverBase<QAndATemplateContext> {
        @Override
        protected Class<? extends QAndATemplateContext> getSubClass(int index) {
            QAndAType typeEnum = QAndAType.valueOf(index);
            FacilioUtil.throwIllegalArgumentException(typeEnum == null, "Invalid type for Q And A Template");
            return typeEnum.getSubClass();
        }
    }
}
