<template>
  <div>
    <div class="assignment-viwe-leagends">
      <el-tabs
        v-model="activeTab"
        class="card-tab-fixed aignment-leagends"
        v-if="!hideLeagend"
      >
        <el-tab-pane
          label="Availability"
          name="availability"
          v-if="deskList.length"
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
        <el-tab-pane label="Desk Types" name="desktype" v-if="deskList.length">
          <div class="inline-flex pT10 pB10 border-bottom15">
            <inline-svg
              src="svgs/floorplan/hotDesk"
              iconClass="icon text-center icon-sm"
            ></inline-svg>
            <div class="pL8 fc-black-13">
              {{ ` Hot Desk (${deskVsType.hot})` }}
            </div>
          </div>
          <div class="inline-flex pT10 pB10 border-bottom15">
            <inline-svg
              src="svgs/floorplan/hotelDesk"
              iconClass="icon text-center icon-sm"
            ></inline-svg>
            <div class="pL8 fc-black-13">
              {{ ` Hotel Desk (${deskVsType.hotel})` }}
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="Locker" name="locker" v-if="lockers.length">
          <div class="inline-flex pT10 pB10 border-bottom15">
            <div :style="{ color: '#22ae5c' }">
              <i class="fa fa-circle f14" aria-hidden="true"></i>
            </div>
            <div class="pL8 fc-black-13">
              {{ `Vacant Lockers (${unAssignedLockers.length || 0}) ` }}
            </div>
          </div>
          <div class="inline-flex pT10 pB10 border-bottom15">
            <div :style="{ color: '#dc4a4c' }">
              <i class="fa fa-circle f14" aria-hidden="true"></i>
            </div>
            <div class="pL8 fc-black-13">
              {{ `Occupied Lockers (${assignedLockers.length || 0}) ` }}
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Parking" name="parking" v-if="parkingList.length">
          <div class="inline-flex pT10 pB10 border-bottom15">
            <div :style="{ color: getParrkingUnassignedColor }">
              <i class="fa fa-circle f14" aria-hidden="true"></i>
            </div>
            <div class="pL8 fc-black-13">
              {{ `Vacant Parking (${getVacantList.length || 0}) ` }}
            </div>
          </div>
          <div class="inline-flex pT10 pB10 border-bottom15">
            <div :style="{ color: getParrkingAssignedColor }">
              <i class="fa fa-circle f14" aria-hidden="true"></i>
            </div>
            <div class="pL8 fc-black-13">
              {{ `Occupied Parking (${getBookedList.length || 0}) ` }}
            </div>
          </div>
          <div class="inline-flex pT10 pB10 border-bottom15">
            <div :style="{ color: getParkingReservableColor }">
              <i class="fa fa-circle f14" aria-hidden="true"></i>
            </div>
            <div class="pL8 fc-black-13">
              {{
                `Non Reservable Parking (${getNonReservalbleList.length || 0}) `
              }}
            </div>
          </div>
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
import {
  getUnassignedList,
  getAssignedList,
  getNonReservalbleList,
  getVacantList,
  getBookedList,
} from 'src/pages/indoorFloorPlan/util.js'
export default {
  props: [
    'departments',
    'deskList',
    'bookings',
    'lockers',
    'parkingList',
    'settings',
    'spaceList',
  ],
  data() {
    return {
      activeTab: 'availability',
      hideLeagend: true,
    }
  },
  computed: {
    getParrkingUnassignedColor() {
      let { availableColor } = this.settings.bookingState
      return availableColor || '#22ae5c'
    },
    getParrkingAssignedColor() {
      let { notAvailableColor } = this.settings.bookingState
      return notAvailableColor || '#dc4a4c'
    },
    getParkingReservableColor() {
      let { nonReservableColor } = this.settings.bookingState
      return nonReservableColor || '#dc4a4c'
    },
    getNonReservalbleList() {
      return getNonReservalbleList(this.parkingList)
    },
    unAssignedParking() {
      return getUnassignedList(this.parkingList)
    },
    getVacantList() {
      return getVacantList(this.parkingList)
    },
    getBookedList() {
      return getBookedList(this.parkingList)
    },
    assignedParking() {
      return getAssignedList(this.parkingList)
    },
    unAssignedLockers() {
      return getUnassignedList(this.lockers)
    },
    assignedLockers() {
      return getAssignedList(this.lockers)
    },
    departmentVsDesk() {
      let departmentMap = {}
      this.deskList.forEach(desk => {
        if (desk.department) {
          if (departmentMap[desk.department.name]) {
            let count = departmentMap[desk.department.name] + 1
            this.$set(departmentMap, desk.department.name, count)
          } else {
            this.$set(departmentMap, desk.department.name, 1)
          }
        }
      })
      return departmentMap
    },
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

      return availabilityMap
    },
    deskVsType() {
      let statusCount = {
        hot: 0,
        hotel: 0,
        assign: 0,
      }
      this.deskList.forEach(desk => {
        if (desk.deskType === 3) {
          let count = statusCount['hot'] + 1
          statusCount['hot'] = count
        } else if (desk.deskType === 2) {
          let count = statusCount['hotel'] + 1
          statusCount['hotel'] = count
        } else if (desk.deskType === 1) {
          let count = statusCount['assign'] + 1
          statusCount['assign'] = count
        }
      })
      return statusCount
    },
  },
  watch: {
    lockers: {
      handler() {
        this.setView()
      },
      immediate: true,
    },
  },
  mounted() {
    this.setView()
  },
  methods: {
    setView() {
      if (this.lockers.length && !this.deskList.length) {
        this.activeTab = 'locker'
      } else if (this.parkingList.length && !this.deskList.length) {
        this.activeTab = 'parking'
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.assignment-viwe-leagends {
  position: absolute;
  bottom: 100px;
  right: 20px;
  border: 1px solid rgba(222, 231, 239, 0.6);
  background: #fff;
  box-shadow: 1px 1px 7px 0 rgb(0 0 0 / 10%);
  .el-tabs__nav {
    padding-left: 15px;
    padding-right: 15px;
    border-bottom: 1px solid #eee;
  }
  .el-tabs__active-bar {
    height: 2px;
  }
  .el-tabs__active-bar {
    left: 20px;
  }
  .el-tabs__content {
    padding-left: 15px;
    padding-right: 15px;
    padding-bottom: 15px;
  }
  .el-tabs__header {
    margin-bottom: 10px;
  }
}
.aignment-leagends {
  padding: 0;
  max-height: 400px;
}
.show-leadends-text {
  position: absolute;
  bottom: 100px;
  right: 20px;
  padding: 8px 15px;
  background-color: #3ab2c1;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.4px;
  &:hover {
    box-shadow: 0px 1px 4px rgb(0 0 0 / 30%);
  }
  .el-icon-d-arrow-left {
    transform: rotate(90deg);
    margin-left: 5px;
    font-weight: 600;
  }
}
.inactive-desk {
  opacity: 0.4;
}
</style>
<style scoped>
.card-tab-fixed .el-tabs__header {
  position: sticky;
  top: 0;
  background: #fff;
  z-index: 1;
  padding: 10px;
  margin-bottom: 0;
}
.card-tab-fixed .el-tabs__content {
  padding: 10px;
}
.sidebarhide {
  width: 22px;
  height: 39px;
  position: absolute;
  right: 9px;
  top: -30px;
  transform: rotate(-90deg);
  cursor: pointer;
}
.sidebarhide-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  height: 100%;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
  box-shadow: 1px 1px 7px 0 rgb(0 0 0 / 10%);
  border: 1px solid rgb(222 231 239 / 60%);
}
.sidebarhide-bar i {
  font-size: 12px;
  font-weight: bold;
  color: rgb(50 64 86 / 40%);
}
</style>
