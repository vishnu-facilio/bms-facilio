<template>
  <div
    class="dragabale-card height100 graphics-widget"
    id="graphics-widget"
    v-bind:class="{ mobileoverlay: $mobile }"
  >
    <f-graphics-builder
      ref="graphicsWidget"
      v-if="config.widget.graphicsId || config.widget.dataOptions.graphicsId"
      :showFilters="showFilters"
      :zoomLevel="zoomLevel"
      :defaultAsset="defaultAsset"
      :graphicsContext="{
        id: config.widget.graphicsId
          ? config.widget.graphicsId
          : config.widget.dataOptions.graphicsId,
      }"
      readonly="true"
      :drilldown="!$mobile"
      :width="config.width"
      :height="config.height"
    ></f-graphics-builder>
  </div>
</template>
<script>
import JumpToHelper from '@/mixins/JumpToHelper'
export default {
  props: ['config'],
  mixins: [JumpToHelper],
  components: {
    FGraphicsBuilder: () => import('pages/assets/graphics/FGraphicsBuilder'),
  },
  data() {
    return {}
  },
  computed: {
    mode() {
      if (
        this.$route.query &&
        this.$route.query.create &&
        this.$route.query.create === 'edit'
      ) {
        return true
      } else if (
        this.$route.query &&
        this.$route.query.create &&
        this.$route.query.create === 'new'
      ) {
        return true
      } else {
        return false
      }
    },
    showFilters() {
      if (
        this.config.widget.dataOptions &&
        this.config.widget.dataOptions.graphicsOptions
      ) {
        let obj = JSON.parse(this.config.widget.dataOptions.graphicsOptions)
        if (obj.showFilter) {
          return true
        }
      }
      return false
    },
    zoomLevel() {
      if (
        this.config.widget.dataOptions &&
        this.config.widget.dataOptions.graphicsOptions
      ) {
        let obj = JSON.parse(this.config.widget.dataOptions.graphicsOptions)
        if (obj.zoomLevel && obj.zoomLevel > 0.1) {
          return obj.zoomLevel
        }
      }
      return null
    },
    defaultAsset() {
      if (this.showFilters && this.$route.query && this.$route.query.assetId) {
        return parseInt(this.$route.query.assetId)
      } else if (
        this.config.widget.dataOptions &&
        this.config.widget.dataOptions.graphicsOptions
      ) {
        let obj = JSON.parse(this.config.widget.dataOptions.graphicsOptions)
        if (obj.defaultAsset) {
          return obj.defaultAsset
        }
      }
      return null
    },
  },
  mounted() {},
  methods: {
    openInAnalytics() {
      if (this.$refs['graphicsWidget']) {
        this.$refs['graphicsWidget'].openInAnalytics()
      }
    },
    exportAsImage() {
      if (this.$refs['graphicsWidget']) {
        this.$refs['graphicsWidget'].exportAsImage()
      }
    },
  },
}
</script>
<style>
.dragabale-card .canvas-container {
  margin: 0 auto;
}

/* for mobile dashboard scroll { */
.mobileoverlay {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 10;
}
</style>
