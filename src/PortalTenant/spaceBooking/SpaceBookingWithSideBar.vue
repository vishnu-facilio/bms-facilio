<template>
  <div style="display:flex">
    <SpaceBookingForm class="h100"></SpaceBookingForm>
    <div class="spacebooking-card h100">
      <SpaceBookingCard class="h100"></SpaceBookingCard>
    </div>
  </div>
</template>

<script>
import SpaceBookingForm from 'src/PortalTenant/spaceBooking/SpaceBookingForm.vue'
import SpaceBookingCard from 'src/pages/spacebooking/FormSIdeBarBookingCard.vue'

export default {
  components: { SpaceBookingForm, SpaceBookingCard },
}
</script>
<style scoped>
.h100 {
  height: 100%;
}
.spacebooking-card {
  margin: 20px 20px 0px;
  width: 40%;
  height: calc(100vh - 100px);
}
</style>
