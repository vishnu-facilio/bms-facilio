<template>
  <div>
    <div class="emp-floor-swicther" @click="handleClick">
      <!-- <div class="emp-building-name" v-if="building">
        {{ building.name }}
      </div> -->
      <div class="emp-floor-name">
        <span class="pR5" style="position: relative;top: 3px;">
          <inline-svg
            src="svgs/employeePortal/floormap"
            iconClass="icon"
            style="width:16px;opacity:0.8"
          ></inline-svg>
        </span>
        <span v-if="isBuildingLoading"><ShimmerDiv></ShimmerDiv></span>
        <span class="pR5" v-else-if="building"> {{ building.name }}</span>
        <span class="mR5"> / </span>

        <span v-if="isFloorLoading"><ShimmerDiv></ShimmerDiv></span>

        <span class="pR5" v-else-if="floor">{{ floor.name }}</span>
        <span v-if="!diableSwitcherDialog && !isFloorLoading"
          ><i
            class="el-icon-arrow-down"
            style="
    font-weight: 600;
    font-size: 14px;
"
          ></i
        ></span>
      </div>
    </div>
    <portal to="employee-portal-floor-swicther" v-if="!diableSwitcherDialog">
      <IndoorFloorPlanSwitcher
        v-if="showFilter"
        :visibility.sync="showFilter"
        :floorplanId="floorplanId"
        :floorId="floorId"
        :buildingId="buildingId"
        @close="handleCloseFilterdilaog"
      ></IndoorFloorPlanSwitcher>
    </portal>
  </div>
</template>
<script>
import IndoorFloorPlanSwitcher from 'pages/indoorFloorPlan/components/IndoorFloorPlanSwitcher2'
import ShimmerDiv from '@/ShimmerDiv'
export default {
  props: [
    'floorplanId',
    'floorId',
    'buildingId',
    'building',
    'floor',
    'diableSwitcherDialog',
  ],
  components: { IndoorFloorPlanSwitcher, ShimmerDiv },
  data() {
    return {
      showFilter: false,
    }
  },
  computed: {
    isBuildingLoading() {
      //temp method need proper fix
      if (this.building?.name) {
        if (this.building.name.indexOf('loading') > -1) {
          return true
        }
      }
      return false
    },
    isFloorLoading() {
      //temp method need proper fix
      if (this.floor?.name) {
        if (this.floor.name.indexOf('loading') > -1) {
          return true
        }
      }
      return false
    },
  },
  methods: {
    handleClick() {
      this.showFilter = !this.showFilter
    },
    handleCloseFilterdilaog() {
      this.showFilter = false
    },
  },
}
</script>
<style scoped>
.emp-floor-swicther {
  line-height: normal;
  cursor: pointer;
}
.emp-building-name {
  font-size: 11px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #b1b1b1;
  padding-bottom: 4px;
}
.emp-floor-name {
  font-size: 16px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #12324a;
  display: flex;
}
</style>
