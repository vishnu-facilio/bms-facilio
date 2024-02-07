<template>
  <div>
    <WorkplaceAnalyticsTopbar
      :building="resource.building"
      :floor="resource.floor"
      @goback="goBack()"
    ></WorkplaceAnalyticsTopbar>

    <div class="wp-analytics-body">
      <WorkPlaceTreeMap
        v-if="render"
        class="workplace-treemap"
        :buildingId="buildingId"
        :floorId="floorId"
      >
      </WorkPlaceTreeMap>
    </div>
  </div>
</template>
<script>
import WorkPlaceTreeMap from 'src/pages/workplaceAnalytics/WorkPlaceTreeMap.vue'
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'
import WorkplaceAnalyticsTopbar from 'src/pages/workplaceAnalytics/WorkplaceAnalyticsTopbar.vue'
export default {
  components: { WorkPlaceTreeMap, WorkplaceAnalyticsTopbar },
  props: ['building'],
  data() {
    return {
      render: true,
      headerDetails: {
        header: null,
        subHeader: null,
      },
      resource: {
        floor: null,
        building: null,
      },
    }
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    buildingId() {
      if (this.building?.id) {
        return this.building.id
      }
      if (this.$route?.params?.buildingId) {
        return Number(this.$route.params.buildingId)
      }
      return null
    },
    floorId() {
      if (this.$route?.params?.floorId) {
        return Number(this.$route.params.floorId)
      }
      return null
    },
  },
  watch: {
    floorId(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.remount()
      }
    },
    buildingId(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.remount()
      }
    },
  },
  mounted() {
    if (this.floorId !== null) {
      this.getFloor()
    } else {
      this.getBuilding()
    }
  },
  methods: {
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'workplace-analytics' }
        let { name } = findRouteForTab(tabType, { config }) || {}

        return name ? this.$router.resolve({ name }).href : null
      } else {
        return '/app/wp/workplacetreemap'
      }
    },
    goBack() {
      // window.history.go(-1)
      this.remount()

      let parentPath = this.findRoute()

      if (parentPath) {
        if (this.floorId) {
          this.$router.push({
            path: `${parentPath}/${this.buildingId}`,
          })
        } else {
          this.$router.push({
            path: `${parentPath}`,
          })
        }
      }
    },
    remount() {
      if (this.floorId !== null) {
        this.getFloor()
      } else {
        this.getBuilding()
      }
      this.render = false
      this.$nextTick(() => {
        this.render = true
      })
    },
    async getFloor() {
      let params = {
        floorId: this.floorId,
      }
      let { data } = await API.post(`v2/floor/details`, params)
      if (data?.floor?.id) {
        if (data.floor.building?.name) {
          this.$set(this.resource, 'building', data.floor.building)
          this.$set(this.headerDetails, 'header', data.floor.building.name)
        }
        this.$set(this.resource, 'floor', data.floor)
        this.$set(this.headerDetails, 'subHeader', data.floor.name)
      }
    },
    async getBuilding() {
      let params = {
        buildingId: this.buildingId,
      }
      let { data } = await API.post(`v2/building/details`, params)
      if (data?.building?.id) {
        if (data.building.site?.id) {
          let site = this.sites.find(rt => rt.id === data.building.site.id)
          if (site?.name) {
            this.$set(this.headerDetails, 'header', site.name)
          }
        }
        this.$set(this.resource, 'building', data.building)
        this.$set(this.resource, 'floor', null)

        this.$set(this.headerDetails, 'subHeader', data.building.name)
      }
    },
  },
}
</script>
<style>
.wp-analytics-header {
  box-shadow: 0px -1px 5px 0 rgb(0 0 0 / 20%);
}
.wp-analytics-body {
  height: calc(100vh - 120px);
}
</style>
