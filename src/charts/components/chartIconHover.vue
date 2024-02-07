<template>
  <div>
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/piechart-white.svg"
      v-if="icon === 'pie'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/doughnut-white.svg"
      v-else-if="icon === 'doughnut'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/barchart-white.svg"
      v-else-if="icon === 'bar'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/stackedbar-white.svg"
      v-else-if="icon === 'stackedbar'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/groupedbar-white.svg"
      v-else-if="icon === 'groupedbar'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/line_chart-white.svg"
      v-else-if="icon === 'line'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/area-white.svg"
      v-else-if="icon === 'area'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/progressbar-white.svg"
      v-else-if="icon === 'progress'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/heatmap-white.svg"
      v-else-if="icon === 'heatMap'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/cubism-white.svg"
      v-else-if="icon === 'cubsim'"
    />
    <img
      class="chart-icon-new filter-theme-color"
      src="~statics/report/line_chart-white.svg"
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
  width: 20px;
  height: 17px;
}
.filter-theme-color {
  filter: var(--fc-theme-filter);
}
</style>
