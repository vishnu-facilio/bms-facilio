<template>
  <div
    class="fplan-space-info-card floorplan-tooltip"
    v-if="selectedSpaceInfo && selectedSpaceInfo.space"
    :style="{
      top: selectedSpaceInfo.top + 'px',
      left: selectedSpaceInfo.left + 'px',
    }"
  >
    <div class="position-relative">
      <el-row>
        <template v-if="tooltip">
          <el-col :span="24" class="space-title">{{ tooltip.label }}</el-col>
          <el-col :span="24" v-if="tooltip.value" class="space-subtitle">
            {{ tooltip.value }} {{ tooltip.unit ? ' ' + tooltip.unit : '' }}
          </el-col>
          <el-col :span="24" class="mT20">
            <template>
              <button
                :style="{ background: color, color: color }"
                type="button"
                class="footer-btn  el-col el-col-24"
              ></button>
            </template>
          </el-col>
        </template>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  props: ['spaceList', 'visibility', 'element', 'floorPlan', 'area'],
  data() {
    return {
      selectedSpaceInfo: null,
    }
  },
  mounted() {
    this.initData()
  },
  watch: {
    'element.target': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.initData()
        }
      },
      deep: true,
    },
  },
  computed: {
    tooltip() {
      if (this.area && this.area[0] && this.area[0].tooltip) {
        return this.area[0].tooltip
      }
      return null
    },
    color() {
      if (
        this.area &&
        this.area[0] &&
        this.area[0].styles &&
        this.area[0].styles.fill
      ) {
        return this.area[0].styles.fill
      }
      return '#8f8f8f'
    },
  },
  methods: {
    initData() {
      if (
        this.element &&
        this.element.target &&
        this.element.target.floorplan &&
        this.element.target.floorplan.spaceId
      ) {
        let obj = this.element.target
        let event = this.element.e
        let { floorplan } = this.element.target
        let { spaceId } = floorplan
        let spaceObj = this.spaceList.find(rt => rt.id === spaceId)
        let metaObj = {
          top: event.offsetY - 20,
          left: event.offsetX - 170,
        }
        this.selectedSpaceInfo = { ...spaceObj, ...metaObj }
      } else {
        this.selectedSpaceInfo = null
      }
    },
    close() {
      this.$emit('close')
    },
  },
}
</script>
<style>
.floor-plan-builder .floorplan-tooltip .space-title {
  padding-left: 20px;
  padding-top: 20px;
}
.floor-plan-builder .floorplan-tooltip .space-subtitle {
  padding-left: 20px;
  padding-top: 10px;
}
.floorplan-tooltip .footer-btn {
  padding: 3px;
}
.floor-plan-builder .floorplan-tooltip {
  text-align: left !important;
}
</style>
