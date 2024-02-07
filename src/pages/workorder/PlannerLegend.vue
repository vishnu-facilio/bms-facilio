<template>
  <el-popover
    placement="bottom-end"
    popper-class="planner-legend-popover"
    v-bind:visible-arrow="false"
  >
    <div class="planner-legend-container fc-black-color p30 pB15 pT15">
      <div
        class="legend-title fc-black-color f14 bold text-left mB20 text-uppercase"
      >
        {{ legendName }}
      </div>
      <div class="d-flex legend-2col-cont">
        <div class="legend-cont-col1 mR40">
          <div
            v-if="(index + 1) % 2"
            v-for="(legend, key, index) in legendMap"
            :key="key"
            class="planner-legend-item mB15"
          >
            <div
              :class="[
                ['wo-legend-' + legend.colorIndex],
                'planner-legend-dot',
              ]"
            ></div>
            <div class="planner-legend-text">{{ legend.name }}</div>
          </div>
        </div>

        <div class="`legend`-cont-col2">
          <div
            v-if="!((index + 1) % 2)"
            v-for="(legend, key, index) in legendMap"
            :key="key"
            class="planner-legend-item mB15"
          >
            <div
              :class="[
                ['wo-legend-' + legend.colorIndex],
                'planner-legend-dot',
              ]"
            ></div>
            <div class="planner-legend-text">{{ legend.name }}</div>
          </div>
        </div>
      </div>
    </div>
    <el-button
      slot="reference"
      class="mR20 vertical-middle text-left height35 fc-btn-grey-border40"
    >
      <div class="planner-legend-icon-cont">
        <div class="planner-legend-icon-dot " style="background:#5a6aea"></div>
        <div class="planner-legend-icon-dot " style="background:#a85aea"></div>
        <div class="planner-legend-icon-dot " style="background:#ea5ad2"></div>
      </div>
    </el-button>
  </el-popover>
</template>
<script>
export default {
  props: ['legendMap', 'legendName'],
}
</script>

<style lang="scss">
//no  scoped classed as popover appends in end of body
.planner-legend-container {
  border-radius: 3px;
  //box-shadow: 0 5px 25px 0 rgba(31, 23, 63, 0.09);
  background-color: #ffffff;
  // padding:30px;
  // padding-bottom:15px;
}
.planner-legend-item {
  display: flex;
}
.planner-legend-text {
  max-width: 200px;
}
.planner-legend-dot {
  border-radius: 50%;
  margin-top: 5px;
  margin-right: 10px;
  width: 8px;
  height: 8px;
}
.planner-legend-popover {
  padding: 0px !important;
}

.planner-legend-icon-cont {
  width: 30px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}
.planner-legend-icon-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
}
.legend-title {
  letter-spacing: 1px;
}
</style>
