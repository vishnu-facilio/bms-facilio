<template>
  <div class="sr-summary-parent">
    <ServiceRequestEmailThread
      :details="details"
      v-bind="$attrs"
    ></ServiceRequestEmailThread>
    <ServiceRequestDetails
      :details="details"
      v-bind="$attrs"
    ></ServiceRequestDetails>
  </div>
</template>
<script>
import ServiceRequestEmailThread from './ServiceRequestEmailThread'
import ServiceRequestDetails from './ServiceRequestDetails'
export default {
  components: {
    ServiceRequestEmailThread,
    ServiceRequestDetails,
  },
  props: ['details'],
}
</script>
