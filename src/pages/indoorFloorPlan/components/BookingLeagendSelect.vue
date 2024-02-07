<template>
  <component
    :ref="componentName"
    v-if="componentName"
    :is="componentName"
    :key="componentName"
    v-bind="$attrs"
    :departments="departments"
    :deskList="deskList"
    :spaceList="spaceList"
    :bookings="bookings"
    :lockers="lockers"
    :parkingList="parkingList"
    :settings="settings"
  ></component>
</template>
<script>
/* eslint-disable vue/no-unused-components */

export default {
  props: [
    'departments',
    'deskList',
    'spaceList',
    'bookings',
    'lockers',
    'parkingList',
    'settings',
  ],
  components: {
    newbookingleagend: () =>
      import('src/pages/indoorFloorPlan/components/NewBookingLeagends.vue'),
    bookingleagend: () =>
      import('src/pages/indoorFloorPlan/components/BookingViewLeagends.vue'),
  },
  computed: {
    componentName() {
      if (this.$helpers.isLicenseEnabled('NEW_FLOORPLAN')) {
        return 'newbookingleagend'
      }
      return 'bookingleagend'
    },
  },
}
</script>
