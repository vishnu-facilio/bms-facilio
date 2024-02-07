<template>
  <div class=" height100 p0">
    <card-loading v-if="isLoading"></card-loading>
    <div v-else class="fp-widget-container" :style="{ height: height }">
      <div class="card-header-block f15 bold fc-widget-header">
        {{ cardData.title }}
      </div>
      <div class="dashboard-web-widget">
        <template>
          <floorplan-view-play
            ref="floorplanWidget"
            class="floor-map-overview height100"
            v-if="cardData && cardData.value"
            :floorplanId="cardData.value.id"
            :floorId="cardData.value.floorId"
            :hideHeader="true"
            :data="data"
            :viewMode="cardData.viewMode"
            @view="handleView"
            :viewParams="viewParams"
            :hideSettings="true"
          ></floorplan-view-play>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import Card from '../base/Card'
import * as d3 from 'd3'
import floorplanViewPlay from 'pages/floorPlan/FloorplanPlay'

export default {
  props: ['cardDataObj'],
  extends: Card,
  components: {
    floorplanViewPlay,
  },
  data() {
    return {
      height: 300,
      params: {},
      floorplanData: null,
    }
  },
  mounted() {
    this.getClientProps()
  },
  computed: {
    data() {
      if (this.floorplanData) {
        return this.floorplanData
      } else {
        return this.cardData.data
      }
    },
    viewParams() {
      if (this.cardParams && this.cardParams.viewParams) {
        return this.cardParams.viewParams
      } else if (this.cardDataObj && this.cardDataObj.viewParams) {
        return this.cardDataObj.viewParams
      }
      return null
    },
    cardParam() {
      if (this.cardParams) {
        return this.cardParams
      } else if (this.cardDataObj) {
        return this.cardDataObj
      } else {
        return null
      }
    },
  },
  watch: {
    '$attrs.widgetConfig.w': {
      handler() {
        setTimeout(() => {
          this.getClientProps()
        }, 200)
      },
    },
    '$attrs.widgetConfig.h': {
      handler() {
        setTimeout(() => {
          this.getClientProps()
        }, 200)
      },
    },
  },
  methods: {
    handleView(mode, data) {
      this.$set(this, 'params', this.cardParam)
      this.cardData.viewMode = mode
      this.params.viewMode = mode
      this.getVieMode(data)
    },
    getVieMode(data) {
      this.floorplanData = null
      if (this.params.viewMode === 'readings') {
        this.$set(this.params, 'viewParams', data)
      }
      this.$http
        .post('/v2/floorPlan/viewFloorPlanMode', {
          floorPlanViewMode: this.params,
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.floorplanData = data.result.data
            this.prepareStyle(data.result.data)
            if (this.$refs['floorplanWidget']) {
              this.$refs['floorplanWidget'].reInit()
            }
          }
        })
    },
    prepareStyle(data) {
      let colors = d3
        .scaleLinear()
        .range(['#1b7f01', '#7fc001', '#ffff01', '#fb8002', '#fa1000'])
      if (data && data.areas) {
        let { areas } = data
        let style = {}
        if (this.cardData.viewMode === 'readings') {
          let domaindata = []
          areas.forEach(rt => {
            if (rt.value && rt.value.value) {
              domaindata.push(rt.value.value)
            }
          })
          colors.domain(domaindata)
          areas.forEach(rt => {
            if (rt.value && rt.value.value) {
              let label = `${rt.value.value} ${rt.value.unit || ''}`
              this.$set(style, 'fill', colors(rt.value.value))
              if (!rt.style) {
                this.$set(rt, 'styles', style)
              }
              this.$set(rt, 'label', label)
              style = {}
            } else {
              this.$set(style, 'fill', '#b0b0b0')
              if (!rt.style) {
                this.$set(rt, 'styles', style)
              }
            }
          })
        }
      }
    },
    getClientProps() {
      if (this.data) {
        this.prepareStyle(this.data)
      }
      this.$nextTick(() => {
        if (this.$el) {
          this.height = this.$el.clientHeight - 67
        } else {
          this.height = 300
        }
        if (this.$refs['floorplanWidget']) {
          this.$refs['floorplanWidget'].reInit()
        }
      })
    },
  },
}
</script>

<style scoped>
.fp-widget-container {
  height: calc(100% - 50px);
}
.formbuilder-fullscreen-popup {
  position: relative;
  z-index: 0;
}
.floor-plan-viewer-container {
  padding: 0px !important;
}
</style>
