<template>
  <div v-if="visibility" class="analytics-sidebar-bg">
    <div class="left-sidebar-position-setup">
      <div
        class="sidebar-close-btn pointer"
        style="left:-37 px;"
        @click="cancel"
      >
        <div class="">
          <i class="el-icon-close"></i>
        </div>
      </div>
      <div class="building-analysis-sidebar-conatainer">
        <div class="fc-text-pink fw6 pL20 pT20">SAVED REPORTS</div>
        <div v-if="report !== null">
          <div class="mT20">
            <div
              v-for="folder in report"
              :to="getReportLink(folder.id)"
              :key="folder.id"
            >
              <div
                class="label-txt-black pointer pT15 pB15 pL20 pR20 folder-hover"
                @click="openreport(folder.id)"
              >
                {{ folder.name }}
              </div>
            </div>
          </div>
        </div>
        <div v-else class="mT20 mL20">
          No saved regression reports to display.
        </div>
      </div>
      <div class="col-9 reports-summary" id="printable-area">
        <router-view />
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['visibility'],
  data() {
    return {
      report: null,
    }
  },
  mounted() {
    let self = this
    let params = {}
    params['reportType'] = 3
    API.get('/v3/report/reading/all', params).then(response => {
      if (!response.error) {
        self.report = response.data.regressionReport
      }
    })
  },
  methods: {
    cancel() {
      this.$emit('update:visibility', false)
    },
    getReportLink(id) {
      return '/app/em/reportview/' + id
    },
    openreport(id) {
      this.$router.push({
        path: '/app/em/analytics/regression',
        query: { reportId: id },
      })
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style>
.folder-hover:hover {
  background: #f1f8fa;
  color: #324056;
  cursor: pointer;
}
</style>
