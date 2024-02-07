<script>
import { isEmpty } from '@facilio/utils/validation'
import { isMarkerElement, isSpaceZoneElement } from '../elements/Common'
export default {
  methods: {
    applyFilters(obj) {
      this.applySpaceFilter(obj)
    },
    applyStyles(obj) {
      let spaceId =
        obj.floorplan && obj.floorplan.spaceId ? obj.floorplan.spaceId : null
      let { areas } = this
      if (spaceId) {
        let area = areas.find(rt => rt.spaceId === spaceId)
        if (area && area.styles && isSpaceZoneElement(obj)) {
          let { styles } = area
          this.setSpaceZoneStyles(obj, 'polygon', styles)
          if (area.label) {
            this.setSpaceZoneLabel(obj, 'text', area.label)
          }
          // if (area.icons && area.icons.length) {
          //   area.icons.forEach(icon => {
          //     this.addMarker(icon.type, obj)
          //   })
          // }
        }
      }
    },
    applyPointerStyles(obj) {
      let { employeeId } = obj.floorplan
      // this.areas = [
      //   {
      //     employeeId: 3137,
      //     styles: {
      //       pointer_active_circle: { fill: 'blue' },
      //       pointer_bg_circle: { fill: 'green' },
      //       pointer_center_circle: { fill: 'pink' },
      //     },
      //   },
      // ]
      let { areas } = this
      let employee = areas.find(rt => rt.employeeId === employeeId)
      if (employee && employee.styles) {
        let { styles } = employee
        Object.keys(styles).forEach(key => {
          this.setSpaceZoneFloorProps(obj, key, styles[key])
        })
      }
    },
    getAsset() {},
    applySpaceFilter(obj) {
      if (obj && obj.floorplan && obj.floorplan.spaceId) {
        let { spaceId } = obj.floorplan
        let { spaces } = this
        let { markerType } = obj.floorplan
        if (this.areas && this.areas.length) {
          let area = this.areas.find(rt => rt.spaceId === spaceId)
          if (area) {
            if (isMarkerElement(obj)) {
              if ([343, 410].includes(window.orgId)) {
                if (area.markerReadings && area.markerReadings.OCCUPANCY) {
                  let OCCUPANCY = area.markerReadings.OCCUPANCY
                  if (!OCCUPANCY.value) {
                    this.disableObject(obj)
                  }
                } else if (area.assets && area.assets.length) {
                  let assetId = obj.floorplan.assetId
                  if (
                    (assetId || area.assets[0][assetId]) &&
                    area.markerReading &&
                    area.markerReading.OCCUPANCY
                  ) {
                    let OCCUPANCY = area.markerReading.OCCUPANCY
                    if (!OCCUPANCY.value) {
                      this.disableObject(obj)
                    }
                  } else if (!assetId || !area.assets[0][assetId]) {
                    this.disableObject(obj)
                  }
                } else {
                  this.disableObject(obj)
                }
              } else {
                if (!area.markers) {
                  this.disableObject(obj)
                } else {
                  if (
                    markerType &&
                    area.markers.findIndex(rt => rt.Enum === markerType) === -1
                  ) {
                    this.disableObject(obj)
                  }
                }
              }
            }
          } else {
            this.disableObject(obj)
          }
        }
        if (!isEmpty(spaces)) {
          let spaceIdList = spaces.map(rt => rt.id)
          if (spaceIdList.indexOf(spaceId) === -1) {
            this.disableObject(obj)
          }
        }
      }
    },
    disableObject(obj) {
      this.$set(obj.floorplan, 'disable', true)
      obj.visible = 0
    },
  },
}
</script>
