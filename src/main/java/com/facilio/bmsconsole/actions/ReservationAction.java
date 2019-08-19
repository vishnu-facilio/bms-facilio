package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class ReservationAction extends FacilioAction {
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

        Chain addReservation = TransactionChainFactory.addReservationChain();
        addReservation.execute(context);

        setResult(FacilioConstants.ContextNames.Reservation.RESERVATION, reservation);
        return SUCCESS;
    }

    private List<ReservationContext> reservationList;
    public List<ReservationContext> getReservationList() {
        return reservationList;
    }
    public void setReservationList(List<ReservationContext> reservationList) {
        this.reservationList = reservationList;
    }
    public String fetchReservationList() throws Exception {
        if (StringUtils.isEmpty(getOrderBy())) {
            setOrderBy("Reservations.LOCAL_ID");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioContext context = constructListContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.Reservation.RESERVATION);
        context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, Collections.singletonList(modBean.getField("reservedFor", FacilioConstants.ContextNames.Reservation.RESERVATION)));

        Chain fetchList = ReadOnlyChainFactory.fetchModuleDataListChain();
        fetchList.execute(context);

        if (isFetchCount()) {
            setResult(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.RECORD_COUNT));
        }
        else {
            reservationList = (List<ReservationContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            setResult(FacilioConstants.ContextNames.Reservation.RESERVATION_LIST, reservationList);
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
        Chain fetchDetail = ReadOnlyChainFactory.fetchReservationDetailsChain();
        fetchDetail.execute(context);

        reservation = (ReservationContext) context.get(FacilioConstants.ContextNames.RECORD);
        setResult(FacilioConstants.ContextNames.Reservation.RESERVATION, reservation);
        return SUCCESS;
    }

}
