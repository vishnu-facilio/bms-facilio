package com.facilio.relation.context;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.util.RelationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import java.io.Serializable;

@Getter
@Setter
public class RelationMappingContext implements Serializable {

    private long id = -1;
    private long relationId = -1;
    private long fromModuleId = -1;
    private long toModuleId = -1;
    private String relationName;

    private RelationRequestContext.RelationType relationType;
    public int getRelationType() {
        if (relationType != null) {
            return relationType.getIndex();
        }
        return -1;
    }
    public RelationRequestContext.RelationType getRelationTypeEnum() {
        return relationType;
    }
    public void setRelationType(int relationType) {
        this.relationType = RelationRequestContext.RelationType.valueOf(relationType);
    }
    public void setRelationType(RelationRequestContext.RelationType relationType) {
        this.relationType = relationType;
    }

    private Position position;
    public int getPosition() {
        if (position != null) {
            return position.getIndex();
        }
        return -1;
    }
    public Position getPositionEnum() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    public void setPosition(int position) {
        this.position = Position.valueOf(position);
    }

    public Position getReversePosition(Position position) {
        return (position == Position.LEFT) ? Position.RIGHT : Position.LEFT;
    }

    private RelationContext relationContext;
    @JsonIgnore
    @JSON(serialize = false)
    public RelationContext getRelationContext() throws Exception {
        if (relationContext == null) {
            relationContext = RelationUtil.getRelation(relationId, false);
        }
        return relationContext;
    }

    private FacilioModule fromModule;
    @JsonIgnore
    @JSON(serialize = false)
    public FacilioModule getFromModule() throws Exception {
        if (fromModule == null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            fromModule = modBean.getModule(fromModuleId);
        }
        return fromModule;
    }

    private FacilioModule toModule;
    @JsonIgnore
    @JSON(serialize = false)
    public FacilioModule getToModule() throws Exception {
        if (toModule == null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            toModule = modBean.getModule(toModuleId);
        }
        return toModule;
    }

    public enum Position implements FacilioIntEnum {
        LEFT("left"),
        RIGHT("right");

        String name;

        Position(String name) {
            this.name = name;
        }

        public String getFieldName() {
            return name;
        }
        public String getColumnName() {
            return StringUtils.upperCase(name) + "_ID";
        }

        public static Position valueOf(int position) {
            if (position > 0 && position <= values().length) {
                return values()[position - 1];
            }
            return null;
        }
    }
}
