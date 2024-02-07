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
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
export default {
  props: {
    item: {
      type: Object,
      required: true,
    },
  },
  mixins: [JumpToHelper, BaseWidgetMixin],
  components: {
    FGraphicsBuilder: () => import('pages/assets/graphics/FGraphicsBuilder'),
  },
  data() {
    return {
      showGraphicOptions: false,
    }
  },
  computed: {
    id() {
      const {
        item: { id },
      } = this
      return id
    },
    widgetConfig() {
      const { id } = this
      const self = this
      return {
        id: id,
        minW: 25,
        maxW: 96,
        minH: 10,
        maxH: 50,
        showHeader: true,
        showExpand: false,
        noResize: false,
        showDropDown: true,
        editMenu: [
          {
            label: 'Graphics Options',
            action: () => {
              self.showGraphicOptions = true
            },
          },
        ],
        borderAroundWidget: true,
        viewMenu: [
          {
            label: 'Go to Graphics',
            action: this.gotoGraphics,
          },
          {
            label: 'Explore in Analytics',
            action: this.exploreInAnalytics,
          },
          {
            label: 'Export as Image',
            action: this.exportAsImage,
          },
        ],
      }
    },
    widget() {
      const {
        item: { widget },
      } = this
      return widget
    },
    config() {
      const { widget } = this
      return {
        widget: widget,
      }
    },
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
  created() {
    this.initWidget(this.widgetConfig)
  },
  mounted() {},
  methods: {
    exploreInAnalytics() {},
    gotoGraphics() {},
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
