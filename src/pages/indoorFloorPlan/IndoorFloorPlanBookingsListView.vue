<template>
  <div class="p25 floorplan-object-list-view">
    <template v-if="searchResult && searchResult.length">
      <div
        class="mB30"
        v-for="(list, listIndex) in searchResult"
        :key="listIndex"
      >
        <div class="list-header" v-if="list.children.length > 0">
          <span class="header-icon">
            <InlineSvg
              v-if="list.title == 'Parkings'"
              src="svgs/employeePortal/parking-list"
              iconClass="icon-xxxs"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Space'"
              src="svgs/employeePortal/meeting-room4"
              iconClass="icon-xxxs"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Assignable Desk'"
              src="svgs/employeePortal/hot-desk-list"
              iconClass="icon-xxxs"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Hot Desk'"
              src="svgs/employeePortal/hot-desk-list"
              iconClass="icon-xxxs"
            ></InlineSvg>
            <InlineSvg
              v-else-if="list.title == 'Hotel Desk'"
              src="svgs/employeePortal/icon-hoteling"
              iconClass="icon-xxxs"
            ></InlineSvg></span
          ><span>{{ list.title }}</span
          ><span class="header-desc"> {{ list.children.length }}results</span>
        </div>
        <div
          v-for="(feature, index) in list.children"
          :key="index"
          class="list-catalog-items pL20 pR20 d-flex "
          @mouseover="selectFeatureId(feature)"
          @mouseleave="deSelectFeatureID(feature)"
        >
          <div>
            <div class="list-catalog-name mL20">
              {{ feature.label }}
            </div>
            <div class="list-sub-text mL20">
              {{ feature.secondaryLabel }}
            </div>
          </div>
          <div
            class="locate-button-container"
            @click="changed(feature.objectId)"
            v-if="hoverFeature === feature.objectId"
          >
            <div class="locate-button">
              <span class="mR5 h12">
                <inline-svg
                  :key="`parkingIdx-${index}`"
                  src="svgs/ic-my-location"
                  iconClass="icon-xs"
                ></inline-svg> </span
              >Locate in Map
            </div>
          </div>
        </div>
      </div>
    </template>
    <template v-else>
      <div class="mB30">
        <div class="list-header">
          <div>No data</div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import InlineSvg from '../../components/InlineSvg.vue'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'

export default {
  props: ['searchText', 'showSearchList'],
  data() {
    return {
      hoverFeature: null,
      desksType: {
        1: 'Assignable Desk',
        2: 'Hot Desk',
        3: 'Hotel Desk',
      },
      zones: null,
      markers: null,
    }
  },
  mounted() {
    eventBus.$on('VIEWER_API_LOADED', ({ zones, markers }) => {
      if (zones) {
        this.zones = zones
      }
      if (markers) {
        this.markers = markers
      }
    })
  },
  computed: {
    listData() {
      let { zones, desksType, markers } = this
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
      return listData
    },
    searchResult() {
      let { searchText, listData } = this
      if (
        listData &&
        listData.length &&
        searchText !== null &&
        searchText !== ''
      ) {
        let result = []
        listData.forEach(rt => {
          let data = {}
          data.title = rt.title
          data.children = []
          if (rt.children) {
            rt.children.forEach(rl => {
              if (
                rl.label.toLowerCase().indexOf(searchText.toLowerCase()) > -1
              ) {
                data.children.push(rl)
              }
              return null
            })
          }
          if (data.children.length) {
            result.push(data)
          }
        })
        return result
      }
      return listData
    },
  },
  methods: {
    selectFeatureId(feature) {
      let { objectId } = feature
      this.hoverFeature = objectId
    },
    deSelectFeatureID() {
      this.hoverFeature = null
    },
    changed(id) {
      this.$emit('changed')
      this.$router.replace({ query: { featureId: Number(id) } })
    },
  },
  components: { InlineSvg },
}
</script>
<style lang="scss" scoped>
.h12 {
  height: 12px;
}
.floorplan-object-list-view {
  overflow: auto;
  height: calc(100% - 120px);
}
.list-catalog-items {
  background-color: #fff;
  height: 60px;
  margin-bottom: 5px;
  cursor: pointer;
  align-items: center;
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #464646;
  justify-content: space-between;
}
.list-catalog-items:hover {
  background-color: #fff;
  height: 60px;
  margin-bottom: 5px;
  cursor: pointer;
  align-items: center;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #464646;
}
// .list-catalog-items:hover .locate-button-container {
//   display: block;
// }
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
  line-height: 1.33;
  letter-spacing: 0.5px;
  text-align: justify;
  color: #979797;
}
.locate-button {
  font-size: 12px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.43px;
  color: #fff;
  height: inherit;
  display: flex;
  align-items: center;
}
.locate-button-container {
  width: 125px;
  height: 28px;
  padding: 0 12px;
  border-radius: 3px;
  background-color: #0053cc;
  color: #fff;
}
</style>
