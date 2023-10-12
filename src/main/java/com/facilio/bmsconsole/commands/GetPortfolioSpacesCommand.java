package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPortfolioSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String searchText = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        List<SiteContext> sites = getSites(searchText,FacilioConstants.ContextNames.SITE);
        List<BuildingContext> buildings = getBuildings(searchText,FacilioConstants.ContextNames.BUILDING);
        List<FloorContext> floors = getFloors(searchText,FacilioConstants.ContextNames.FLOOR);
        List<SpaceContext> spaces = getSpaces(searchText,FacilioConstants.ContextNames.SPACE);
        Map<String,Object> portfolioList = new HashMap<>();
        if(!sites.isEmpty()) {
            portfolioList.put("sites", sites);
        }
        if(!buildings.isEmpty()) {
            portfolioList.put("buildings", buildings);
        }
        if(!floors.isEmpty()) {
            portfolioList.put("floors", floors);
        }
        if(!spaces.isEmpty()) {
            portfolioList.put("spaces", spaces);
        }
        context.put(FacilioConstants.ContextNames.SEARCH_RESULT,portfolioList);
        return false;
    }
    public List<SiteContext> getSites(String  search,String moduleName) throws Exception{
        FacilioModule spaceModule = Constants.getModBean().getModule(moduleName);
        List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
                .select(fields)
                .table(spaceModule.getTableName())
                .moduleName(spaceModule.getName())
                .beanClass(SiteContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),search, StringOperators.CONTAINS))
                .limit(5);
        List<SiteContext> props = selectBuilder.get();
        return props;
    }

    public List<BuildingContext> getBuildings(String  search, String moduleName) throws Exception{
        FacilioModule spaceModule = Constants.getModBean().getModule(moduleName);
        List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
                .select(fields)
                .table(spaceModule.getTableName())
                .moduleName(spaceModule.getName())
                .beanClass(BuildingContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),search, StringOperators.CONTAINS))
                .limit(5);
        List<BuildingContext> props = selectBuilder.get();
        return props;
    }

    public List<SpaceContext> getSpaces(String  search, String moduleName) throws Exception{
        FacilioModule spaceModule = Constants.getModBean().getModule(moduleName);
        List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
                .select(fields)
                .table(spaceModule.getTableName())
                .moduleName(spaceModule.getName())
                .beanClass(SpaceContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),search, StringOperators.CONTAINS))
                .limit(5);
        List<SpaceContext> props = selectBuilder.get();
        return props;
    }

    public List<FloorContext> getFloors(String  search,String moduleName) throws Exception{
        FacilioModule spaceModule = Constants.getModBean().getModule(moduleName);
        List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
                .select(fields)
                .table(spaceModule.getTableName())
                .moduleName(spaceModule.getName())
                .beanClass(FloorContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),search, StringOperators.CONTAINS))
                .limit(5);
        List<FloorContext> props = selectBuilder.get();
        return props;
    }

    }
