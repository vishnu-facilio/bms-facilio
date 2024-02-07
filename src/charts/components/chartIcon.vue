<template>
  <div>
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/piechart.svg"
      v-if="icon === 'pie'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/doughnut.svg"
      v-else-if="icon === 'doughnut' || icon === 'donut'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/barchart.svg"
      v-else-if="icon === 'bar'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/stackedbar.svg"
      v-else-if="icon === 'stackedbar'"
    />
    <fc-icon
      class="chart-icon-new filter-theme-color"
      v-else-if="icon === 'histogram'"
      group="chart-data"
      name="histogram"
      color="#ef508f"
      size="17"
      style="border: 1.5px solid #ef508f; border-radius:2px;display:flex;justify-content:center;align-items:center"
    ></fc-icon>
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/groupedbar.svg"
      v-else-if="icon === 'groupedbar'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/line_chart.svg"
      v-else-if="icon === 'line'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/area.svg"
      v-else-if="icon === 'area'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/area.svg"
      v-else-if="icon === 'area-spline'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/area.svg"
      v-else-if="icon === 'area-step'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/progressbar.svg"
      v-else-if="icon === 'progress'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/heatmap.svg"
      v-else-if="icon === 'heatMap'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/cubism.svg"
      v-else-if="icon === 'cubsim'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/table.svg"
      v-else-if="icon === 'tabular'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/gauge.svg"
      v-else-if="icon === 'gauge'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/line_chart.svg"
      v-else
    />
  </div>
</template>
<script>
export default {
  props: ['icon'],
}
</script>
<style>
.chart-icon-new {
  width: 1.4vw;
  height: 1.3vw;
  z-index: 2;
  position: relative;
  display: block;
}
.filter-theme-color {
  filter: var(--fc-theme-filter);
}
</style>
