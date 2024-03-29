package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ReservationAction extends FacilioAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReservationContext reservation;
    public ReservationContext getReservation() {
        return reservation;
    }
    public void setReservation(ReservationContext reservation) {
        this.reservation = reservation;
    }

    public String addReservation() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.Reservation.RESERVATION, reservation);

        FacilioChain addReservation = TransactionChainFactory.addReservationChain();
        addReservation.execute(context);

        setResult(FacilioConstants.ContextNames.Reservation.RESERVATION, reservation);
        return SUCCESS;
    }

    public String fetchReservationList() throws Exception {
        if (StringUtils.isEmpty(getOrderBy())) {
            setOrderBy("Reservations.SCHEDULED_START_TIME");
            setOrderType("DESC");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioChain fetchList = ReadOnlyChainFactory.fetchModuleDataListChain();
        FacilioContext context = fetchList.getContext();
        constructListContext(context);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.Reservation.RESERVATION);
        context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, Collections.singletonList(modBean.getField("reservedFor", FacilioConstants.ContextNames.Reservation.RESERVATION)));

        fetchList.execute();

        if (isFetchCount()) {
            setResult(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.RECORD_COUNT));
        }
        else {
            setResult(FacilioConstants.ContextNames.Reservation.RESERVATION_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
        }
        return SUCCESS;
    }

    private long recordId = -1;
    public long getRecordId() {
        return recordId;
    }
    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public String fetchReservationDetail() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ID, recordId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.Reservation.RESERVATION);
        FacilioChain fetchDetail = ReadOnlyChainFactory.fetchReservationDetailsChain();
        fetchDetail.execute(context);

        reservation = (ReservationContext) context.get(FacilioConstants.ContextNames.RECORD);
        setResult(FacilioConstants.ContextNames.Reservation.RESERVATION, reservation);
        return SUCCESS;
    }

    private List<Long> id;
    public List<Long> getId() {
        return id;
    }
    public void setId(List<Long> id) {
        this.id = id;
    }

    public String deleteReservation() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.Reservation.RESERVATION);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

        FacilioChain deleteReservation = FacilioChainFactory.deleteModuleDataChain();
        deleteReservation.execute(context);

        setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
        return SUCCESS;
    }

    public String updateReservation() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
        context.put(FacilioConstants.ContextNames.Reservation.RESERVATION, reservation);

        FacilioChain updateReservation = TransactionChainFactory.updateReservationChain();
        updateReservation.execute(context);

        setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
        return SUCCESS;
    }


}
