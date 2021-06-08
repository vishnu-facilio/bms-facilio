package com.facilio.qa.context;

import com.facilio.v3.context.V3Context;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class PageContext extends V3Context {

    public PageContext(Long id) {
        super(id);
    }

    private QAndATemplateContext parent;
    private String name;
    private String description;

    public Integer getPosition() {
        return position;
    }
    public void setPosition(Integer position) {
        this.position = position;
    }
    private Integer position;

    private List<QuestionContext> questions;
}
