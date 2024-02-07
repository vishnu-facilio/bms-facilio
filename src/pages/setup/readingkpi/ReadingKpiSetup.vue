<template>
  <div class="kpi-setup-page fc-setup-page height100">
    <SetupHeader>
      <template #heading>
        {{ title }}
      </template>
      <template #description>
        {{ description }}
      </template>
      <template #actions>
        <div class="flex-middle">
          <div class="action-btn setting-page-btn mL20">
            <el-button
              v-if="currentTab === 'readingkpi.list'"
              type="primary"
              @click="addNewKpi"
              class="setup-el-btn"
            >
              {{ $t('kpi.kpi.add_kpi_templates') }}
            </el-button>
            <el-button
              v-else
              type="primary"
              @click="showHistoricalDialog = true"
              class="setup-el-btn"
            >
              {{ $t('kpi.historical.run_historical') }}
            </el-button>
          </div>
        </div>
      </template>
      <template #tabs>
        <el-tabs class="fc-setup-list-tab" v-model="currentTab">
          <el-tab-pane
            :label="$t('kpi.kpi.kpi_template')"
            name="readingkpi.list"
          >
            <div v-if="currentTab === 'readingkpi.list'" class="mT10 mB20">
              <KPITemplatesList />
            </div>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('kpi.historical.kpi_history_logs')"
            name="readingkpi.logs"
          >
            <div v-if="currentTab === 'readingkpi.logs'" class="mT10 mB20">
              <KPIHistoryLogs
                :showHistoricalDialog="showHistoricalDialog"
                @closeCreateDialog="showHistoricalDialog = false"
                @openCreateDialog="showHistoricalDialog = true"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
        <div class="kpi-setup-tab-actions">
          <portal-target name="kpi-setup-tab-actions"></portal-target>
        </div>
      </template>
    </SetupHeader>
  </div>
</template>

<script>
import KPITemplatesList from 'src/pages/setup/readingkpi/KPITemplatesList.vue'
import KPIHistoryLogs from 'src/pages/setup/readingkpi/KPIHistoryLogs.vue'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'

export default {
  components: {
    SetupHeader,
    KPITemplatesList,
    KPIHistoryLogs,
  },
  data() {
    return {
      showHistoricalDialog: false,
    }
  },

  computed: {
    currentTab: {
      get() {
        return this.$route.name
      },
      set(selectedTabName) {
        this.handleTabChange(selectedTabName)
      },
      currentTabName() {
        if (this.$route.name == 'readingkpi.list') {
          return this.$t('kpi.kpi.kpi_template')
        }
        return this.$t('kpi.historical.kpi_history')
      },
    },
    title() {
      return this.currentTab === 'readingkpi.list'
        ? this.$t('kpi.kpi.kpi_template')
        : this.$t('kpi.historical.kpi_history')
    },
    description() {
      return this.currentTab === 'readingkpi.list'
        ? this.$t('kpi.kpi.list_of_all_kpis')
        : this.$t('kpi.historical.history_description')
    },
  },
  methods: {
    changeRouteForSelectedTab(selectedTabName) {
      if (this.$route.name !== selectedTabName)
        this.$router.push({ name: selectedTabName })
    },
    handleTabChange(selectedTabName) {
      this.changeRouteForSelectedTab(selectedTabName)
    },
    addNewKpi() {
      this.$router.push({ name: 'create-new-kpi' })
    },
  },
}
</script>
<style lang="scss" scoped>
.kpi-setup-page {
  .kpi-setup-tab-actions {
    display: flex;
    align-items: center;
    justify-content: right;
    position: absolute;
    top: 90px;
    right: 50px;
    height: 55px;
    width: 35%;
  }
}
</style>
