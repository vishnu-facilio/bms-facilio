<template>
  <div class="height100 relative ">
    <div class="fp-focus">
      <div class="reading-card-header fc-input-label-txt pT20 ">
        Select spaceto focus
      </div>
      <el-select
        v-model="focus.spaceId"
        placeholder="Select"
        class="mR10 fc-input-full-border p10"
      >
        <el-option
          v-for="(space, index) in spaceId"
          :key="index"
          :label="space"
          :value="space"
        >
        </el-option>
      </el-select>
    </div>
    <el-row class="height100">
      <el-col :span="24" class="height100">
        <floorplan-view
          class="floor-map-overview"
          :floorplanId="floorPlanViewMode.floorPlanId"
          :floorId="floorId"
          :hideHeader="true"
          :data="data"
          :focus="focus"
          :viewMode="floorPlanViewMode.viewMode"
          @view="handleView"
        ></floorplan-view>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import floorplanView from 'pages/floorPlan/FloorplanPlay'
import * as d3 from 'd3'
export default {
  data() {
    return {
      floorId: 4,
      floorPlanViewMode: {
        viewMode: 'maintenance',
        floorPlanId: 3,
        scriptModeInt: 1,
      },
      focus: {
        spaceId: 5,
      },
      spaceId: [5, 6],
      data: [],
      viewModes: [
        'default',
        'maintenance',
        'control_points',
        'spacecategory',
        'reservation',
        'asset',
        'readings',
        'people',
        'employee',
      ],
    }
  },
  components: {
    floorplanView,
  },
  mounted() {
    this.getVieMode()
  },
  methods: {
    handleView(mode, data) {
      this.floorPlanViewMode.viewMode = mode
      this.getVieMode(data)
    },
    getVieMode(data) {
      if (this.floorPlanViewMode.viewMode === 'readings') {
        this.$set(this.floorPlanViewMode, 'viewParams', data)
      }
      this.$http
        .post('/v2/floorPlan/viewFloorPlanMode', {
          floorPlanViewMode: this.floorPlanViewMode,
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.data = data.result.data
            this.prepareStyle(this.data)
          }
        })
    },
    getColor() {
      return (
        '#' +
        Math.random()
          .toString(16)
          .substr(2, 6)
      )
    },
    prepareStyle(data) {
      let colors = d3
        .scaleLinear()
        .range(['#1b7f01', '#7fc001', '#ffff01', '#fb8002', '#fa1000'])
      if (data && data.areas) {
        let { areas } = data
        let style = {}
        if (this.floorPlanViewMode.viewMode === 'readings') {
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
              this.$set(rt, 'styles', style)
              this.$set(rt, 'label', label)
              style = {}
            }
          })
        } else {
          areas.forEach(rt => {
            this.$set(style, 'fill', this.getColor())
            this.$set(rt, 'styles', style)
            style = {}
          })
        }
      }
    },
  },
}
</script>

<style>
.fp-focus {
  position: absolute;
  left: 100px;
  z-index: 10;
}
</style>
