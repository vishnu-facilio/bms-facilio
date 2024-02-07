<template>
  <div
    v-if="searchText"
    class="floorplan-object-list-view"
    v-show="showSearchList"
  >
    <div v-if="isLoading">
      <spinner :show="true" size="40"></spinner>
    </div>

    <div v-else-if="searchResult.length" class="floorplan-object-list">
      <div
        class="mB15"
        v-for="(list, listIndex) in searchResult"
        :key="listIndex"
      >
        <div class="list-header">
          <span class="header-icon">
            <InlineSvg
              v-if="list.title == 'Parkings'"
              src="svgs/employeePortal/parking-list"
              iconClass="icon-xxxs"
              class="flex-middle"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Space'"
              src="svgs/employeePortal/meeting-room4"
              iconClass="icon-xxxs"
              class="flex-middle"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Assignable Desk'"
              src="svgs/employeePortal/hot-desk-list"
              iconClass="icon-xxxs"
              class="flex-middle"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Hot Desk'"
              src="svgs/employeePortal/hot-desk-list"
              iconClass="icon-xxxs"
              class="flex-middle"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Hotel Desk'"
              src="svgs/employeePortal/icon-hoteling"
              iconClass="icon-xxxs"
              class="flex-middle"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Employees'"
              src="svgs/employeePortal/employee"
              iconClass="icon-xxxs"
              class="flex-middle"
            ></InlineSvg></span
          ><span>{{ list.title }}</span
          ><span class="header-desc" v-if="list.title !== 'Employees'">
            {{ list.children.length }} results</span
          >
        </div>
        <div
          v-for="(feature, index) in list.children"
          :key="index"
          class="list-catalog-items d-flex "
          @click="handleClick(feature)"
          v-bind:class="{ disabled: !isSpaceAssociated(feature) }"
        >
          <div>
            <div class="list-catalog-name mL20">
              {{ feature.label }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="floorplan-object-list">
      <div class="mB15">
        <div class="fp-s-no-data">No data</div>
      </div>
    </div>
  </div>
</template>
<script>
import IndoorFloorPlanBookingsListView from 'src/pages/indoorFloorPlan/IndoorFloorPlanBookingsListView.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: IndoorFloorPlanBookingsListView,
  props: ['employeesList', 'isLoading', 'showSearchList', 'searchText'],
  data() {
    return {
      openfloorplanData: null,
    }
  },
  // watch: {
  //   listData: {
  //     handler(newVal, oldVal) {
  //       if (this.listData.length) {

  //       }
  //     },
  //   },
  // },
  computed: {
    listData() {
      let { zones, desksType, markers, employeesList } = this
      let zoneFeatures = zones?.features
      let markersFeatures = markers?.features
      let features = [].concat(zoneFeatures, markersFeatures)
      let listData = []
      let parkings = []
      let space = []
      let AssignableDesk = []
      let hotdesk = []
      let hoteldesk = []
      if (features && features.length) {
        features.forEach(feature => {
          if (feature?.properties?.markerModuleName) {
            if (feature.properties.markerModuleName === 'parkingstall') {
              parkings.push(feature.properties)
            } else if (feature.properties.markerModuleName === 'space') {
              space.push(feature.properties)
            } else if (feature.properties.markerModuleName === 'desks') {
              if (feature?.properties?.deskType) {
                let type = desksType[feature.properties.deskType]
                if (type == 'Assignable Desk') {
                  AssignableDesk.push(feature.properties)
                } else if (type == 'Hot Desk') {
                  hotdesk.push(feature.properties)
                } else if (type == 'Hotel Desk') {
                  hoteldesk.push(feature.properties)
                }
              }
            }
          }
        })

        if (!isEmpty(parkings)) {
          listData.push({ children: parkings, title: 'Parkings' })
        }
        if (!isEmpty(space)) {
          listData.push({ children: space, title: 'Space' })
        }
        if (!isEmpty(AssignableDesk)) {
          listData.push({ children: AssignableDesk, title: 'Assignable Desk' })
        }
        if (!isEmpty(hotdesk)) {
          listData.push({ children: hotdesk, title: 'Hot Desk' })
        }
        if (!isEmpty(hoteldesk)) {
          listData.push({ children: hoteldesk, title: 'Hotel Desk' })
        }
      }
      if (employeesList.length) {
        employeesList.forEach(element => {
          element.label = element.name
        })
        let obj = {}
        obj = {
          children: this.employeesList,
          title: 'Employees',
        }
        listData.push(obj)
      }
      return listData
    },
  },
  methods: {
    isSpaceAssociated(feature) {
      if (feature.space) {
        return true
      } else {
        if (feature?.objectId) {
          return true
        }
      }
      return false
    },
    handleClick(feature) {
      if (feature.space) {
        this.$emit('actionEmployee', feature)
      } else {
        if (feature?.objectId) {
          this.changed(feature.objectId)
        }
      }
    },
  },
}
</script>
<style>
.floorplan-object-list {
  overflow: auto;
  padding: 20px 0;
  max-height: 350px;
}
.floorplan-object-list-view {
  min-height: 50px;
  background: #fff;
  max-height: 350px;
}
.search-result-component {
  background: #fff;
  position: absolute;
  width: 300px;
  right: -5;
  border-radius: 0 0 3px 3px;
  box-shadow: 0px 2px 7px 0 rgb(0 0 0 / 10%);
}
</style>
<style lang="scss" scoped>
.h12 {
  height: 12px;
}

.list-catalog-items {
  background-color: #fff;
  height: 35px;
  cursor: pointer;
  align-items: center;
  font-size: 12px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  padding: 0 25px;
  color: #70757a;
}
.list-catalog-items:hover {
  background-color: rgba(60, 64, 67, 0.04);
}

.list-sub-text {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.33;
  letter-spacing: normal;
  text-align: justify;
  color: #979797;
  margin-top: 5px;
}
.list-header {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #334056;
  margin-bottom: 10px;
  padding: 0 20px;
  display: flex;
  align-items: center;
}
.header-icon {
  margin-right: 8px;
}
.header-desc {
  margin-left: 8px;
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 2px;
  letter-spacing: 0.5px;
  text-align: justify;
  color: #979797;
}
.list-catalog-name {
  display: flex;
  align-items: center;
}
.list-sub-text {
  display: flex;
  align-items: center;
}
.fp-s-no-data {
  text-align: center;
  font-size: 12px;
  color: #a3a1a1;
}
</style>
