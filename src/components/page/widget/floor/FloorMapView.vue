<template>
  <div v-if="$org.id === 343">
    <floorplan-play-view
      v-if="!rerender"
      :floorId="floorId"
      ref="floorplanWidget"
      class="floor-map-overview overflow-hidden"
      :floorplanId="details.defaultFloorPlanId"
      :hideHeader="true"
      :data="floorplanData"
      :viewMode="viewmode"
      @view="handleView"
      :viewParams="viewParams"
      :hideSettings="true"
    ></floorplan-play-view>
  </div>
  <div v-else>
    <floorplan-view
      class="floor-map-overview overflow-hidden"
      v-if="!rerender"
      :floorId="floorId"
    ></floorplan-view>
  </div>
</template>
<script>
export default {
  props: ['details'],
  components: {
    floorplanView: () => import('pages/floorPlan/FloorPlanViewer'),
    floorplanPlayView: () => import('pages/floorPlan/FloorplanPlay'),
  },
  data() {
    return {
      rerender: false,
      floorplanData: null,
      viewParams: {
        readingModule: null,
        assetCategoryId: null,
        readingFieldName: null,
      },
    }
  },
  mounted() {
    if (this.$org.id === 343) {
      this.rerender = true
      this.getVieMode()
    }
  },
  computed: {
    floorId() {
      let { id } = this.details
      if (id) {
        return id
      }
      return null
    },
    viewmode() {
      if (this.$org.id === 343) {
        return 'vavreadings'
      }
      return 'default'
    },
  },
  watch: {
    layout: {
      floorplanId() {
        this.rerender = true
        setTimeout(() => {
          this.rerender = false
        }, 250)
      },
      deep: true,
    },
    'details.defaultFloorPlanId': {
      handler() {
        this.rerender = true
        setTimeout(() => {
          this.rerender = false
        }, 250)
      },
      deep: true,
    },
  },
  methods: {
    handleView(mode, data) {
      this.getVieMode(data)
    },
    getVieMode() {
      this.floorplanData = null
      let params = {
        cardContext: {
          cardLayout: 'floorplan_layout_1',
          cardParams: {
            title: '',
            floorPlanId: this.details.defaultFloorPlanId,
            floorId: this.floorId,
            viewMode: 'vavreadings',
            viewParams: this.viewParams,
            scriptModeInt: 1,
          },
          cardState: { styles: { hideHeader: true }, canResize: true },
        },
      }
      this.$http
        .post('/v2/dashboard/cards/getCardData', params)
        .then(({ data }) => {
          this.rerender = false
          if (data.responseCode === 0) {
            this.floorplanData = data.result.data.data
            if (this.$refs['floorplanWidget']) {
              this.$refs['floorplanWidget'].reInit()
            }
          }
        })
    },
  },
}
</script>
<style lang="scss" scoped>
.floor-map-overview.formbuilder-fullscreen-popup {
  position: revert;
  z-index: 0;
}
</style>
