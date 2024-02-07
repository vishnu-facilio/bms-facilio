<script>
import { isEmpty } from '@facilio/utils/validation'
import { fabric } from 'fabric'
import { getElement } from '../elements/Common'
const OMITTED_OBJECTS = ['space_zone_group', 'floor_plan_image']
import { isElement, getUniqueId } from '../elements/Common'
export default {
  methods: {
    handleLeagend(data) {
      if (data.type === 'CONTROL') {
        this.showControlType(data.name)
      } else if (data === 'CLEAR') {
        this.clearType()
      }
    },
    handleLeagendActive(data) {
      if (data.type === 'CONTROL') {
        this.visibleMarkerTypes(data)
      } else if (data === 'CLEAR') {
        this.clearFilter()
      }
    },
    handleleagendType(type) {
      if (type === 'CONTROL') {
        this.showControlMarkers()
      } else if (type === 'NONE') {
        this.clearFilter()
      } else if (type === 'SPACE_TYPE') {
        this.showSpaceTypes()
      }
    },
    clearFilter() {
      this.canvas.getObjects().forEach(obj => {
        if (
          getElement(obj) &&
          OMITTED_OBJECTS.indexOf(obj.floorplan.type) === -1
        ) {
          obj.opacity = 1
          obj.visible = 1
        }
      })
      this.canvas.renderAll()
    },
    showSpaceTypes() {
      this.canvas.getObjects().forEach(obj => {
        if (getElement(obj) && isElement(obj, 'space_zone_group')) {
          let { floorplan } = obj
        }
      })
      this.canvas.renderAll()
    },
    visibleMarkerTypes(data) {
      let { name } = data
      let { active } = data
      this.canvas.getObjects().forEach(obj => {
        if (getElement(obj)) {
          let { floorplan } = obj
          if (
            floorplan.markerType &&
            floorplan.markerType === name &&
            !floorplan.disable
          ) {
            obj.visible = active
          }
        }
      })
      this.canvas.renderAll()
    },
    showControlType(name) {
      this.canvas.getObjects().forEach(obj => {
        if (getElement(obj)) {
          let { floorplan } = obj
          if (floorplan.markerType && floorplan.markerType === name) {
            obj.opacity = 1
          } else {
            if (OMITTED_OBJECTS.indexOf(floorplan.type) === -1) {
              obj.opacity = 0.2
            }
          }
        }
      })
      this.canvas.renderAll()
    },
    showControlMarkers() {
      this.canvas.getObjects().forEach(obj => {
        if (getElement(obj)) {
          let { floorplan } = obj
          if (
            floorplan.markerType &&
            floorplan.markerType !== null &&
            !floorplan.disable
          ) {
            obj.visible = 1
          } else {
            if (
              OMITTED_OBJECTS.indexOf(floorplan.type) === -1 &&
              !floorplan.disable
            ) {
              obj.visible = 0
            }
          }
        }
      })
      this.canvas.renderAll()
    },
    clearType() {
      this.canvas.getObjects().forEach(obj => {
        if (
          getElement(obj) &&
          OMITTED_OBJECTS.indexOf(obj.floorplan.type) === -1
        ) {
          obj.opacity = 1
        }
      })
      this.canvas.renderAll()
    },
  },
}
</script>
