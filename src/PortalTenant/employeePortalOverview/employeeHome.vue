<template>
  <div class="fc-mobile-tenant-home-con widht100 p0 flex-direction-column">
    <div
      class="fc-black2-50 p20"
      style="font-size: 22px; padding-bottom: 10px; padding-top: 30px; font-weight: 500;"
    >
      {{ $portaluser.name }}
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
import portalHeader from 'src/PortalTenant/employeePortalOverview/employeeOverviewHeader'
import booking from 'src/PortalTenant/employeePortalOverview/employeeOverviewBooking'
import serviceRequest from 'src/PortalTenant/employeePortalOverview/employeeOverviewServiceRequest'
import desk from 'src/PortalTenant/employeePortalOverview/employeeOverviewDesk'
import visitor from 'src/PortalTenant/employeePortalOverview/employeeOverviewVisitor'
import serviceMixin from 'src/PortalTenant/employeePortalOverview/employeeMixin'
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
