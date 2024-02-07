<template>
  <div class="fc-mobile-tenant-home-con widht100 p0 flex-direction-column">
    <div
      class="fc-black2-50 p20"
      style="font-size: 22px; padding-bottom: 10px; padding-top: 30px; font-weight: 500;"
    >
      Hi {{ $portaluser.name }} , here is your overview.
    </div>
    <el-row>
      <el-col :span="12">
        <portalHeader
          :employeeData="employee"
          :loading="loading"
        ></portalHeader>
      </el-col>
      <el-col :span="12" class="pR10">
        <desk :deskData="desks" :loading="loading"></desk>
      </el-col>
    </el-row>
    <service-request
      :serviceData="serviceRequestData"
      :loading="loading"
    ></service-request>
    <visitor
      :visitorData="visitorData"
      :loading="loading"
      :getFormattedDay="getFormattedDay"
    ></visitor>
    <booking
      :bookingData="bookingData"
      :loading="loading"
      :getFormattedDay="getFormattedDay"
    ></booking>
  </div>
</template>
<script>
import portalHeader from 'src/PortalTenant/servicePortalOverview/serviceOverviewHeader'
import booking from 'src/PortalTenant/servicePortalOverview/serviceOverviewBooking'
import serviceRequest from 'src/PortalTenant/servicePortalOverview/serviceOverviewServiceRequest'
import desk from 'src/PortalTenant/servicePortalOverview/serviceOverviewDesk'
import visitor from 'src/PortalTenant/servicePortalOverview/serviceOverviewVisitor'
import serviceMixin from 'src/PortalTenant/servicePortalOverview/mixin/serviceMixin'
export default {
  mixins: [serviceMixin],
  components: {
    booking,
    serviceRequest,
    desk,
    visitor,
    portalHeader,
  },
}
</script>
