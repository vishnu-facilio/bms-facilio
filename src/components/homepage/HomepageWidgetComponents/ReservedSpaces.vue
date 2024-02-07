<template>
  <el-card
    class="box-card fc-recently-received-card fc-recently-received-card2"
  >
    <div class="flex-middle">
      <inline-svg
        src="svgs/employeePortal/recent"
        iconClass="text-center vertical-bottom icon-sm-md"
      ></inline-svg>
      <div class="fc-portal-title-txt-14 pL5 fw5">
        Recently Reserved Space
      </div>
    </div>
    <div v-if="formatedWidgetData.length" class="overflow-auto">
      <div class="pT10 inline-flex overflow-auto" style="display:inline-flex">
        <div
          class="fc-hot-desk-cards ellipsis"
          v-for="(space, index) in formatedWidgetData"
          :key="index"
          @click="redirectToFloorplan(space.id, space.floorId)"
        >
          <div class="fc-txt12-grey">
            {{ $getProperty(space, 'spaceCategory.name', 'Space') }}
          </div>
          <div class="flex-middle pT10 pointer">
            <div v-if="space.spaceCategory">
              <inline-svg
                v-if="space.spaceCategory.name === 'Office '"
                :src="iconMap.Office"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>

              <inline-svg
                v-else-if="space.spaceCategory.name === 'Parking Stall'"
                :src="iconMap.ParkingStall"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
              <inline-svg
                v-else-if="space.spaceCategory.name === 'Desk'"
                :src="iconMap.Desks"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
            </div>
            <div v-else>
              <inline-svg
                :src="iconMap.Office"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
            </div>
            <el-tooltip :content="space.name" placement="bottom" effect="dark">
              <div class="fc-portal-sub-txt-16 pL5 ellipsis">
                {{ space.name }}
              </div>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="flex-center">
      <inline-svg
        src="svgs/employeePortal/EmptyState/Emptystate-recentlyreserved"
        iconClass="icon text-center icon-md vertical-bottom empty-state"
      ></inline-svg>
    </div>
  </el-card>
</template>
<script>
import { findRouteForTab, tabTypes } from '@facilio/router'
import { API } from '@facilio/api'
import baseWidget from 'src/components/homepage/HomepageWidgetComponents/BaseWidget.vue'
export default {
  extends: baseWidget,
  props: ['widgetData'],
  data() {
    return {
      iconMap: {
        Desks: `svgs/employeePortal/hot-desk2`,
        ParkingStall: `svgs/employeePortal/parking2`,
        Office: `svgs/employeePortal/meeting-room4`,
        ps: `svgs/employeePortal/announcement`, // need deliveries icon
        Announcement: `svgs/employeePortal/announcement`,
      },
      floorId: null,
      floorplanId: null,
      recordIdVsFloorplanId: {},
    }
  },
  computed: {
    formatedWidgetData() {
      let data = []
      if (this.widgetData?.reservedSpaces) {
        let { reservedSpaces = [] } = this.widgetData
        data = reservedSpaces
      }
      return data
    },
  },
  methods: {
    widgetInit() {
      this.formatedWidgetData.forEach(space => {
        if (space.floorId) {
          this.getFirstActivePlanFromFloorList({
            spaceId: space.id,
            floorId: space.floorId,
          })
        }
      })
    },
    redirectToFloorplan(recordId, floorId) {
      if (this.recordIdVsFloorplanId[recordId] && floorId) {
        let floorplanId = this.recordIdVsFloorplanId[recordId]
        let { path } = findRouteForTab(tabTypes.INDOOR_FLOORPLAN)
        // 'floorplan/floor-map/indoorfloorplan/:floorId?/:floorplanid?'
        if (path) {
          path = path.replaceAll(':floorId?', floorId)
          path = path.replaceAll(':floorplanid?', floorplanId)
        }
        this.$router.push({
          path: `/employee/${path}`,
          query: {
            featureRecordId: recordId,
            viewMode: 'BOOKING',
          },
        })
      }
    },
    async getFirstActivePlanFromFloorList({ spaceId, floorId }) {
      //get first floor with an  active floor plan
      let params = {
        viewName: 'all',
        page: 1,
        perPage: 1,
        moduleName: 'floor',
        filters: JSON.stringify({
          indoorFloorPlanId: {
            operatorId: 2,
            value: [],
          },
          id: {
            operatorId: 9,
            value: [`${floorId}`],
          },
        }),
      }
      let { data, error } = await API.get('v2/module/data/list', params)

      if (!error) {
        let floorPlanIdFromList = data.moduleDatas[0].indoorFloorPlanId
        this.$set(this.recordIdVsFloorplanId, spaceId, floorPlanIdFromList)
      } else {
        this.$error.message('Error loading all floors', error)
      }
      this.loading = false
    },
    getRandomColor() {
      return (
        '#' + ((Math.random() * 0xffffff) << 0).toString(16).padStart(6, '0')
      )
    },
  },
}
</script>
<style>
.fc-hot-desk-cards {
  min-width: 115px;
  height: auto;
  margin-right: 10px;
  cursor: pointer;
  border-radius: 4px;
  border: solid 1px #e6e6e6;
  background-color: #fff;
  padding: 8px;
  max-width: 300px;
}
.fc-hot-desk-cards:hover {
  border-color: #0053cc;
}
.fc-recently-received-card {
  /* height: 100%; */
}
.flex-center {
  display: flex;
  justify-content: center;
}
svg.icon.text-center.icon-md.vertical-bottom.empty-state {
  width: 100%;
  height: 100%;
  margin-top: 15px;
}
</style>
