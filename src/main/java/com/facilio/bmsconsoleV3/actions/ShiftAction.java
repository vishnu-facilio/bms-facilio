package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

@Getter
@Setter
public class ShiftAction extends V3Action {
    private static Logger LOGGER = LogManager.getLogger(ShiftAction.class.getName());

    private Long rangeFrom;
    private Long rangeTo;
    private Long peopleId;
    private List<Long> people;
    private Long shiftID;
    private Long shiftStart;
    private Long shiftEnd;
    private String format;
    public String list() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getShiftPlannerListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Shift.RANGE_FROM, rangeFrom);
        context.put(FacilioConstants.Shift.RANGE_TO, rangeTo);
        context.put(FacilioConstants.ContextNames.PEOPLE_ID, peopleId);
        chain.execute();

        setData("shifts", context.get("shifts"));
        return SUCCESS;
    }

    public String calendar() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getShiftPlannerCalendarChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Shift.RANGE_FROM, rangeFrom);
        context.put(FacilioConstants.Shift.RANGE_TO, rangeTo);
        context.put(FacilioConstants.ContextNames.PAGE, getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, getOrderType());
        context.put(FacilioConstants.ContextNames.FILTERS, getFilters());
        context.put(FacilioConstants.ContextNames.REPORT_ORDER_BY, getOrderBy());
        chain.execute();

        setData(FacilioConstants.Shift.RANGE,
                context.get(FacilioConstants.Shift.RANGE));
        setData(FacilioConstants.ContextNames.PEOPLE,
                context.get(FacilioConstants.ContextNames.PEOPLE));
        setData(FacilioConstants.ContextNames.SHIFTS,
                context.get(FacilioConstants.ContextNames.SHIFTS));
        setData(FacilioConstants.ContextNames.COUNT,
                context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

    public String update() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getShiftPlannerUpdateChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Shift.SHIFT_START, shiftStart);
        context.put(FacilioConstants.Shift.SHIFT_END, shiftEnd);
        context.put(FacilioConstants.Shift.SHIFT_ID, shiftID);
        context.put(FacilioConstants.ContextNames.PEOPLE, people);
        chain.execute();

        setData("shifts", context.get("shifts"));
        return SUCCESS;
    }

    public String export() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getExportShiftPlannerChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Shift.RANGE_FROM, rangeFrom);
        context.put(FacilioConstants.Shift.RANGE_TO, rangeTo);
        context.put(FacilioConstants.ContextNames.PAGE, getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        context.put(FacilioConstants.ContextNames.PEOPLE_ID,getPeopleId() );
        context.put(FacilioConstants.Shift.FORMAT, format);
        context.put(FacilioConstants.ContextNames.FILTERS, getFilters());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, getOrderType());
        context.put(FacilioConstants.ContextNames.REPORT_ORDER_BY, getOrderBy());
        chain.execute();

        setData(FacilioConstants.Shift.URL, context.get(FacilioConstants.Shift.URL));

        return SUCCESS;
    }
}