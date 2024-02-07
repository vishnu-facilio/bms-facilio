<template>
  <div>
    <Maintenancepopup
      v-if="Maintenance"
      @close="close"
      :visibility.sync="Maintenance"
      :floorPlan="floorPlan"
      :element="element"
      :spaceList="spaceList"
      :area="area"
      :data="data"
      :canvasMeta="canvasMeta"
    ></Maintenancepopup>
    <readings-popup
      v-if="readingsVisible"
      @close="close"
      :visibility.sync="readingsVisible"
      :floorPlan="floorPlan"
      :element="element"
      :spaceList="spaceList"
      :area="area"
      :data="data"
      :canvasMeta="canvasMeta"
    ></readings-popup>
  </div>
</template>

<script>
import Maintenancepopup from 'pages/floorPlan/components/MarkerActions/MaintenancePopup'
import ReadingsPopup from 'pages/floorPlan/components/MarkerActions/ReadingsPopup'
export default {
  components: { Maintenancepopup, ReadingsPopup },
  data() {
    return {
      Maintenance: false,
      readingsVisible: false,
      controlPopup: false,
      floorPlan: null,
      element: null,
      spaceList: null,
      data: null,
      area: null,
      canvasMeta: null,
    }
  },
  methods: {
    executeAction(markertype, data) {
      this.close()
      let { floorPlan, element, spaceList, area, icons, canvasMeta } = data
      this.floorPlan = floorPlan
      this.element = element
      this.spaceList = spaceList
      this.area = area
      this.canvasMeta = canvasMeta
      switch (markertype) {
        case 'default':
          break
        case 'maintenance':
          if (icons) {
            this.data = icons.find(rt => rt.type === 'MAINTENANCE')
            this.Maintenance = true
          }
          break
        case 'control_points':
          this.controlPopup = true
          break
        case 'reservation':
          break
        case 'spacecategory':
          break
        case 'asset':
          break
        case 'readings':
          if (icons) {
            this.data = icons.find(rt => rt.type === 'READINGS')
            this.readingsVisible = true
          }
          break
      }
    },
    close() {
      this.controlPopup = false
      this.Maintenance = false
      this.readingsVisible = false
    },
  },
}
</script>

<style></style>
