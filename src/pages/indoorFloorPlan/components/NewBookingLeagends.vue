<template>
  <div>
    <div class="assignment-viwe-leagends booking-leagend-width">
      <el-tabs
        v-model="activeTab"
        class="card-tab-fixed aignment-leagends"
        v-if="!hideLeagend"
      >
        <el-tab-pane
          label="Availability"
          name="availability"
          v-if="deskList.length + spaceList.length > 0"
        >
          <template v-for="(data, index) in availability">
            <!-- <div
              class="inline-flex pT10 pB10 border-bottom15"
              v-if="data.name === 'Non reservable'"
              :key="index"
            >
              <inline-svg
                src="svgs/floorplan/desk"
                iconClass="icon text-center icon-sm inactive-desk"
              ></inline-svg>
              <div class="pL8 fc-black-13">
                {{ `${data.name} (${data.value})` }}
              </div>
            </div> -->
            <div class="inline-flex pT10 pB10 border-bottom15" :key="index">
              <div :style="{ color: data.color, opacity: data.opacity || 1 }">
                <i class="fa fa-circle f14" aria-hidden="true"></i>
              </div>
              <div class="pL8 fc-black-13">
                {{ `${data.name} (${data.value}) ` }}
              </div>
            </div>
          </template>
        </el-tab-pane>
      </el-tabs>
      <div class="sidebarhide" @click="hideLeagend = true" v-if="!hideLeagend">
        <div class="sidebarhide-bar">
          <i class="el-icon-arrow-right" v-if="hideLeagend"></i>
          <i class="el-icon-d-arrow-left" v-else></i>
        </div>
      </div>
    </div>
    <div
      class="show-leadends-text"
      v-if="hideLeagend"
      @click="hideLeagend = false"
    >
      Show Legend <i class="el-icon-d-arrow-left"></i>
    </div>
  </div>
</template>
<script>
import BookingViewLeagends from 'src/pages/indoorFloorPlan/components/BookingViewLeagends.vue'
export default {
  extends: BookingViewLeagends,
  computed: {
    availability() {
      let availableColor = this.settings.bookingState.availableColor
      let bookedColor = this.settings.bookingState.notAvailableColor
      let nonReservableColor = this.settings.bookingState.nonReservableColor
      let availabilityMap = [
        { name: 'Available', value: 0, color: availableColor },
        // { name: 'Partially available', value: 0, color: '#ffe4a6' },
        //partial state pendings
        { name: 'Booked', value: 0, color: bookedColor },
        {
          name: 'Non reservable',
          value: this.deskVsType.assign,
          color: nonReservableColor,
          opacity: 0.4,
        },
      ]
      //get available and unavailable count ,check if desk has booking entry
      this.deskList.forEach(desk => {
        if (desk.deskType == 3 || desk.deskType == 2) {
          if (desk.isBooked) {
            availabilityMap[1].value++
          } else {
            availabilityMap[0].value++
          }
        }
      })
      this.spaceList.forEach(space => {
        if (space.isReservable) {
          if (space.isBooked) {
            availabilityMap[1].value++
          } else {
            availabilityMap[0].value++
          }
        } else {
          availabilityMap[2].value++
        }
      })
      return availabilityMap
    },
  },
}
</script>
<style>
.booking-leagend-width {
  min-width: 200px;
}
</style>
