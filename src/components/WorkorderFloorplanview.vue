<template>
  <el-dialog
    custom-class="f-image-editor fc-dialog-fp-container fp-adder"
    v-if="visible"
    :visible.sync="visible"
    :modal-append-to-body="false"
    :width="'50%'"
    :title="'Floor Plan'"
    :before-close="closeDialog"
  >
    <template v-if="loading">
      <spinner
        style="margin-top: 150px;"
        v-if="true"
        :show="true"
        :size="80"
      ></spinner>
    </template>
    <template v-else>
      <div class="empty-container" v-if="floorPlanViewMode.floorPlanId === -1">
        <div class="wo-empty-notes">
          NO FLOOR PLAN ASSOCIATED
        </div>
      </div>
      <floorplan-view
        v-else
        class="floor-wo-overview"
        @floorModeData="getData"
        :floorplanId="floorPlanViewMode.floorPlanId"
        :floorId="floorId"
        :hideHeader="true"
        :data="data"
        :focus="focus"
        :viewMode="'maintenance'"
        :loadViewMode="true"
        :hideSettings="true"
      ></floorplan-view>
    </template>
  </el-dialog>
</template>

<script>
import * as d3 from 'd3'
export default {
  props: ['visible', 'floorplandata'],
  data() {
    return {
      loading: false,
      floorId: null,
      floorPlanViewMode: {
        viewMode: 'maintenance',
        floorPlanId: null,
        scriptModeInt: 1,
      },
      data: [],
      focus: null,
    }
  },
  components: {
    floorplanView: () => import('pages/floorPlan/FloorplanPlay'),
  },
  mounted() {
    if (this.floorplandata) {
      this.floorId = this.floorplandata.floorId
      this.focus = this.floorplandata.focus
      this.fetchFloorDetails()
      if (this.floorplandata.floorPlanId) {
        this.getVieMode()
      }
    }
  },
  methods: {
    getData(data) {
      this.data = data
    },
    closeDialog() {
      this.$emit('update:visible', false)
      this.$emit('close')
    },
    handleView(mode, data) {
      this.floorPlanViewMode.viewMode = mode
      this.getVieMode(data)
    },
    getVieMode() {
      this.$http
        .post('/v2/floorPlan/viewFloorPlanMode', {
          floorPlanViewMode: this.floorPlanViewMode,
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.data = data.result.data
            this.prepareStyle(this.data)
            this.loading = false
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
    fetchFloorDetails() {
      this.loading = true
      if (this.floorId) {
        return this.$http
          .get(`/v2/pages/floor?id=${this.floorId}`)
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.floorPlanViewMode.floorPlanId =
                data.result.record.defaultFloorPlanId
              this.getVieMode()
            }
          })
      }
    },
  },
}
</script>

<style>
.floor-wo-overview {
  height: 60vh;
}
.fc-dialog-fp-container .el-dialog__body {
  padding: 0px !important;
}
.fc-dialog-fp-container .el-dialog__header {
  border-bottom: 1px solid #d6d3d3;
}
.empty-container {
  width: 100%;
  height: 60vh;
  text-align: center;
  align-items: center;
  margin: auto;
  font-size: 15px;
  font-weight: 400;
}
.wo-empty-notes {
  padding-top: 25vh;
}
</style>
