<template>
  <component
    v-if="module && metaData.visibility"
    :is="componentMap[module]"
    :visibility.sync="metaData.visibility"
    :data="metaData.data"
    :isNew="metaData.isNew"
    :buildingObj="metaData.buildingObj"
    :site="metaData.site"
    :building="metaData.building"
    :floorObj="metaData.floorObj"
    :spaceobj="metaData.spaceobj"
    :floor="metaData.floor"
    :spaceParent="metaData.spaceParent"
    :isSummaryEdit="metaData.isSummaryEdit"
    @refreshlist="saved"
    @closed="toggleFormVisibilty"
  ></component>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  data() {
    return {
      metaData: {
        site: null,
        building: null,
        floorObj: null,
        floor: null,
        spaceobj: null,
        spaceParent: null,
        buildingObj: null,
        visibility: false,
        isNew: false,
        data: null,
        isSummaryEdit: false,
      },
      module: null,
    }
  },
  created() {
    let { metaData = {} } = this
    eventBus.$on(
      'openSpaceManagementForm',
      ({
        site = null,
        building = null,
        floorObj = null,
        floor = null,
        spaceobj = null,
        spaceParent = null,
        buildingObj = null,
        visibility = false,
        isNew = false,
        data = null,
        module = null,
        isSummaryEdit = false,
      }) => {
        metaData.site = site
        metaData.building = building
        metaData.floorObj = floorObj
        metaData.floor = floor
        metaData.spaceobj = spaceobj
        metaData.spaceParent = spaceParent
        metaData.buildingObj = buildingObj
        metaData.visibility = visibility
        metaData.isNew = isNew
        metaData.data = data
        metaData.isSummaryEdit = isSummaryEdit
        this.$set(this, 'metaData', metaData)
        this.$set(this, 'module', module)
      }
    )
  },
  computed: {
    componentMap() {
      return {
        site: () => import('pages/spacemanagement/forms/SiteForm'),
        building: () => import('pages/spacemanagement/forms/BuildingForm'),
        floor: () => import('pages/spacemanagement/forms/FloorForm'),
        space: () => import('pages/spacemanagement/forms/SpaceForm'),
      }
    },
  },
  methods: {
    toggleFormVisibilty() {
      this.metaData.visibility = !this.metaData.visibility
    },
    saved() {
      this.$emit('saved', this.module)
      this.module = null
    },
  },
}
</script>
