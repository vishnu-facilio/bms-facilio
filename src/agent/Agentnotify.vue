<template>
  <div class="agent-notification-page">
    <portal to="automation-actions" slim>
      <div class="white-bg mL10 mR10 mT10">
        <el-tabs
          v-model="selectedTab"
          @tab-click="updateCurrentAction"
          class="agent-tabs"
        >
          <el-tab-pane
            v-for="tab in tabs"
            :key="tab.key"
            :label="tab.label"
            :name="tab.key"
          >
          </el-tab-pane>
        </el-tabs>
      </div>
    </portal>
    <div v-if="module != null">
      <AlarmAutomation
        :key="selectedTab"
        :modules="module"
        :moduleName="moduleName"
      ></AlarmAutomation>
    </div>
  </div>
</template>
<script>
import AlarmAutomation from 'src/agent/AlarmAutomation'
export default {
  title() {
    return 'Alarm Automation'
  },
  data() {
    return {
      moduleName: 'agentAlarm',
      module: null,
      selectedTab: 'Notification',
      tabs: [
        { key: 'Notification', label: 'Notification' },
        { key: 'WorkFlow', label: 'Workflow' },
      ],
    }
  },
  components: {
    AlarmAutomation,
  },
  mounted() {
    this.loadModuleMeta()
    this.addCurrentActionInMeta()
  },

  methods: {
    loadModuleMeta() {
      this.$util
        .loadModuleMeta(this.moduleName)
        .then(response => (this.module = response.module))
    },
    updateCurrentAction() {
      let { $route, selectedTab } = this
      let { meta } = $route
      meta.currentAction = selectedTab

      let page = this.$route.query.page
      const query = Object.assign({}, this.$route.query)
      if (page) delete query.page
      query.action = selectedTab
      this.$router.replace({ query })
    },
    addCurrentActionInMeta() {
      let { $route, selectedTab } = this
      let { meta } = $route
      meta['currentAction'] = selectedTab
    },
  },
}
</script>
<style lang="scss">
.agent-notification-page {
  width: calc(100% - 250px);
}
</style>
