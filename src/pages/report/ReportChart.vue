<template>
  <div id="showChart">
    <f-report :config="{ id: reportId }" :showChartOnly="true"></f-report>
  </div>
</template>
<script>
import FReport from './components/FReport'
export default {
  components: { FReport },
  computed: {
    reportId() {
      return parseInt(this.$route.params.reportid)
    },
  },
}
</script>
<style>
#sideBar,
.fc-layout-aside,
.layout-header,
.title-actions {
  display: none;
}

.layout-page {
  background: #fff !important;
}

#showChart {
  text-align: center;
}

#showChart .self-center.fchart-section {
  margin-right: 100px !important;
  width: 100% !important;
}

#showChart svg.fchart {
  /* width: 900px !important; */
  zoom: 80%;
}
</style>
