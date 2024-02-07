<template>
  <div class="layout-padding">
    <div class="fc-form">
      <div class="fc-form-title setting-form-title">
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="importNewFile()"
          >{{ 'IMPORT NEW FILE' }}
        </el-button>
        <table class=" pT15 fc-list-view-table border-right-table">
          <thead>
            <tr
              class="tablerow pT30"
              style="background-color:#ffffff;padding:15px"
            >
              <td class="headerrow">FILE NAME</td>
              <td class="headerrow">SITE NAME</td>
              <td class="headerrow ">STATUS</td>
              <td class="headerrow ">IMPORTED TIME</td>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="bim in bimIntegrationLogs"
              :key="bim.id"
              class="tablerow"
            >
              <td>
                {{ bim.fileName }}
              </td>
              <td>
                {{ bim.siteName }}
              </td>
              <td class="contentrow">
                <img
                  v-if="bim.status == 1"
                  class="svg-icon"
                  src="~assets/bim/progress.svg"
                />
                <img
                  v-else-if="bim.status == 2"
                  class="svg-icon"
                  src="~assets/bim/success.svg"
                />
                <img v-else class="svg-icon" src="~assets/bim/failed.svg" />
              </td>
              <td>
                <span v-if="bim.importedTime == 0"> --- </span>
                <span v-else>{{ bim.importedTime | formatDate() }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      bimIntegrationLogs: [],
    }
  },
  mounted() {
    this.getBimIntegrationLogs()
  },
  methods: {
    getBimIntegrationLogs() {
      this.$http
        .get('/v2/bimIntegration/get')
        .then(response => {
          if (response.data) {
            if (response.data.responseCode !== 0) {
              this.$message.error(response.data.message)
            } else {
              this.bimIntegrationLogs = response.data.result.bimIntegrationList
            }
          }
        })
        .catch(error => {
          this.$message.error(response.data.message)
        })
    },
    importNewFile() {
      this.$router.push({
        path: `/app/setup/bim/bimintegration`,
      })
    },
  },
}
</script>
