<template
  ><div class="h100">
    <SpaceDetailsCard
      :widget="widget"
      :moduleName="moduleName"
      :details="details"
      :resizeWidget="resizeWidget"
      :primaryFields="primaryFields"
      :approvalMeta="approvalMeta"
      :hideTitleSection="hideTitleSection"
      :goToBooking="false"
    ></SpaceDetailsCard></div
></template>
<script>
import SpaceBookingCard from 'src/pages/spacebooking/SpaceBookingCard.vue'
export default {
  extends: SpaceBookingCard,
}
</script>
