package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class RequestForQuotationModule extends BaseModuleConfig{
    public RequestForQuotationModule(){
        setModuleName(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> requestForQuotation = new ArrayList<FacilioView>();
        requestForQuotation.add(getAllRequestForQuotationView().setOrder(order++));
        requestForQuotation.add(getQuotesReceivedRfqView("quotesReceived", "Quotes Received" ,true).setOrder(order++));
        requestForQuotation.add(getAwardedRfqView("awarded", "Awarded" ,true).setOrder(order++));
        requestForQuotation.add(getPoCreatedRfqView("poCreated", "PO Created" ,true).setOrder(order++));
        requestForQuotation.add(getDiscardedRfqView("discarded", "Discarded" ,true).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        groupDetails.put("views", requestForQuotation);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllRequestForQuotationView() {

        FacilioModule module = ModuleFactory.getRequestForQuotationModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Request For Quotations");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getQuotesReceivedRfqView(String viewName, String viewDisplayName, boolean quotesReceived) {
        FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(rfqModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getQuotesReceivedRfqCondition(quotesReceived);
        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getQuotesReceivedRfqCondition(boolean quotesReceived) {
        FacilioField quotesReceivedField = new FacilioField();
        quotesReceivedField.setName("isQuoteReceived");
        quotesReceivedField.setColumnName("QUOTE_RECEIVED");
        quotesReceivedField.setDataType(FieldType.BOOLEAN);
        quotesReceivedField.setModule(ModuleFactory.getRequestForQuotationModule());

        Condition quotesReceivedCondition = new Condition();
        quotesReceivedCondition.setField(quotesReceivedField);
        quotesReceivedCondition.setOperator(BooleanOperators.IS);
        quotesReceivedCondition.setValue(String.valueOf(quotesReceived));

        Criteria rfqStatusCriteria = new Criteria();
        rfqStatusCriteria.addAndCondition(quotesReceivedCondition);
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("AWARDED","isAwarded",String.valueOf(false),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("PO_CREATED","isPoCreated",String.valueOf(false),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));

        return rfqStatusCriteria;
    }

    private static FacilioView getAwardedRfqView(String viewName, String viewDisplayName, boolean awarded) {
        FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(rfqModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getAwardedRfqCondition(awarded);
        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getAwardedRfqCondition(boolean awarded) {
        FacilioField awardedField = new FacilioField();
        awardedField.setName("isAwarded");
        awardedField.setColumnName("AWARDED");
        awardedField.setDataType(FieldType.BOOLEAN);
        awardedField.setModule(ModuleFactory.getRequestForQuotationModule());

        Condition awardedCondition = new Condition();
        awardedCondition.setField(awardedField);
        awardedCondition.setOperator(BooleanOperators.IS);
        awardedCondition.setValue(String.valueOf(awarded));

        Criteria rfqStatusCriteria = new Criteria();
        rfqStatusCriteria.addAndCondition(awardedCondition);
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("QUOTE_RECEIVED","isQuoteReceived",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("PO_CREATED","isPoCreated",String.valueOf(false),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));

        return rfqStatusCriteria;
    }
    private static FacilioView getPoCreatedRfqView(String viewName, String viewDisplayName, boolean poCreated) {
        FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(rfqModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getPoCreatedRfqCondition(poCreated);
        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getPoCreatedRfqCondition(boolean poCreated) {
        FacilioField poCreatedField = new FacilioField();
        poCreatedField.setName("isPoCreated");
        poCreatedField.setColumnName("PO_CREATED");
        poCreatedField.setDataType(FieldType.BOOLEAN);
        poCreatedField.setModule(ModuleFactory.getRequestForQuotationModule());

        Condition poCreatedCondition = new Condition();
        poCreatedCondition.setField(poCreatedField);
        poCreatedCondition.setOperator(BooleanOperators.IS);
        poCreatedCondition.setValue(String.valueOf(poCreated));

        Criteria rfqStatusCriteria = new Criteria();
        rfqStatusCriteria.addAndCondition(poCreatedCondition);
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("QUOTE_RECEIVED","isQuoteReceived",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("AWARDED","isAwarded",String.valueOf(true),BooleanOperators.IS));
        rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));
        return rfqStatusCriteria;
    }

    private static FacilioView getDiscardedRfqView(String viewName, String viewDisplayName, boolean discarded) {
        FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(rfqModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getDiscardedRfqCondition(discarded);
        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getDiscardedRfqCondition(boolean discarded) {
        FacilioField discardedField = new FacilioField();
        discardedField.setName("isDiscarded");
        discardedField.setColumnName("DISCARDED");
        discardedField.setDataType(FieldType.BOOLEAN);
        discardedField.setModule(ModuleFactory.getRequestForQuotationModule());

        Condition discardedCondition = new Condition();
        discardedCondition.setField(discardedField);
        discardedCondition.setOperator(BooleanOperators.IS);
        discardedCondition.setValue(String.valueOf(discarded));

        Criteria rfqStatusCriteria = new Criteria();
        rfqStatusCriteria.addAndCondition(discardedCondition);
        return rfqStatusCriteria;
    }
}
