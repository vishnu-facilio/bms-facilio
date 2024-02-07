<template>
  <div>
    <div class="assignment-viwe-leagends">
      <el-tabs
        v-model="activeTab"
        class="card-tab-fixed aignment-leagends"
        v-if="!hideLeagend"
      >
        <el-tab-pane label="Occupancy" name="occupancy" v-if="deskList.length">
          <div class="inline-flex pT10 pB10 border-bottom15">
            <inline-svg
              src="svgs/floorplan/desk"
              iconClass="icon text-center icon-sm"
            ></inline-svg>
            <div class="pL8 fc-black-13">
              {{ ` Vacant Desks (${statusVsDesk.vacant})` }}
            </div>
          </div>
          <div class="inline-flex pT10 pB10 border-bottom15">
            <inline-svg
              src="svgs/floorplan/circle"
              iconClass="icon text-center icon-sm"
            ></inline-svg>
            <div class="pL8 fc-black-13">
              {{ ` Occupied Desks (${statusVsDesk.occupied})` }}
            </div>
          </div>
          <div class="inline-flex pT10 pB10 border-bottom15">
            <inline-svg
              src="svgs/floorplan/desk"
              iconClass="icon text-center icon-sm inactive-desk"
            ></inline-svg>
            <div class="pL8 fc-black-13">
              {{ `Reservable Desks (${statusVsDesk.reservable})` }}
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane
          label="Departments"
          name="department"
          v-if="deskList.length"
        >
          <template v-for="(department, index) in departments">
            <div
              class="inline-flex pT10 pB10 border-bottom15"
              :key="index"
              v-if="departmentVsDesk[department.id]"
            >
              <div :style="{ color: department.color }" v-if="department.color">
                <i class="fa fa-circle f14" aria-hidden="true"></i>
              </div>
              <div :style="{ color: '#ddd' }" v-else>
                <i class="fa fa-circle f14" aria-hidden="true"></i>
              </div>
              <div class="pL8 fc-black-13">
                {{
                  `${department.name} (${departmentVsDesk[department.id] ||
                    0}) `
                }}
              </div>
            </div>
          </template>
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
              {{ `Vacant Parking (${unAssignedParking.length || 0}) ` }}
            </div>
          </div>
          <div class="inline-flex pT10 pB10 border-bottom15">
            <div :style="{ color: getParrkingAssignedColor }">
              <i class="fa fa-circle f14" aria-hidden="true"></i>
            </div>
            <div class="pL8 fc-black-13">
              {{ `Occupied Parking (${assignedParking.length || 0}) ` }}
            </div>
          </div>
          <div class="inline-flex pT10 pB10 border-bottom15">
            <div :style="{ color: getParrkingReservableColor }">
              <i class="fa fa-circle f14" aria-hidden="true"></i>
            </div>
            <div class="pL8 fc-black-13">
              {{ `Reservable Parking (${getReservalbleList.length || 0}) ` }}
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
  getReservalbleList,
} from 'src/pages/indoorFloorPlan/util.js'
export default {
  props: ['departments', 'deskList', 'lockers', 'parkingList', 'settings'],
  data() {
    return {
      hideLeagend: false,
      activeTab: 'occupancy',
    }
  },
  computed: {
    getParrkingUnassignedColor() {
      let { unAssignedColor } = this.settings.assignmentState
      return unAssignedColor || '#22ae5c'
    },
    getParrkingAssignedColor() {
      let { assignedColor } = this.settings.assignmentState
      return assignedColor || '#dc4a4c'
    },
    getParrkingReservableColor() {
      let { nonReservableColor } = this.settings.bookingState
      return nonReservableColor || '#dc4a4c'
    },
    unAssignedParking() {
      return getUnassignedList(this.parkingList)
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
    getReservalbleList() {
      return getReservalbleList(this.parkingList)
    },
    departmentVsDesk() {
      let departmentMap = {}
      this.deskList.forEach(desk => {
        if (desk.department) {
          if (departmentMap[desk.department.id]) {
            let count = departmentMap[desk.department.id] + 1
            this.$set(departmentMap, desk.department.id, count)
          } else {
            this.$set(departmentMap, desk.department.id, 1)
          }
        }
      })
      return departmentMap
    },
    statusVsDesk() {
      let statusCount = {
        vacant: 0,
        occupied: 0,
        reservable: 0,
      }
      this.deskList.forEach(desk => {
        if (desk.deskType > 1) {
          let count = statusCount['reservable'] + 1
          statusCount['reservable'] = count
        } else if (desk.employee) {
          let count = statusCount['occupied'] + 1
          statusCount['occupied'] = count
        } else if (desk.deskType === 1 && !desk.employee) {
          let count = statusCount['vacant'] + 1
          statusCount['vacant'] = count
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
<style lang="scss">
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
    overflow: auto;
    max-height: 300px;
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
