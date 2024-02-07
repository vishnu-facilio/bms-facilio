<template>
  <div ref="activities-container">
    <div class="flex-middle justify-content-space">
      <div class="pL30 pT30">
        <div class="fc-black-com f14 bold pL10">
          {{ $t('asset.history.history') }}
        </div>
      </div>
      <div v-if="$helpers.isLicenseEnabled('COMMISSIONING')" class="pT20 pR30">
        <el-dropdown class="fc-dropdown-menu-border" trigger="click">
          <el-button type="primary" class="fc-btn-group-white">
            <div>
              {{ fixedFilter }}
              <i class="el-icon-arrow-down"></i>
            </div>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <div @click="setActualActivity()">
                {{ $t('setup.decommission.actual_log') }}
              </div>
            </el-dropdown-item>
            <el-dropdown-item>
              <div @click="setLogActivity()">
                {{ $t('setup.decommission.commissioning_log') }}
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <activities
      v-if="isActualActivity"
      :record="details"
      :module="moduleName"
      :activityModule="activityModule"
      @autoResizeWidget="autoResize"
      ref="activitiesDiv"
    ></activities>
    <LogActivities
      v-else
      :recordId="recordId"
      @autoResizeWidget="autoResize"
      :selectedModule="moduleName"
      :details="details"
    ></LogActivities>
  </div>
</template>
<script>
import Activities from '@/widget/Activities'
import LogActivities from '@/widget/decommission/LogActivities.vue'

export default {
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'moduleName',
    'resizeWidget',
    'widget',
  ],
  components: {
    Activities,
    LogActivities,
  },
  data() {
    return {
      record: {},
      fixedFilter: 'All History',
      isActualActivity: true,
    }
  },
  mounted() {
    this.setDefaultData()
    this.autoResize()
  },
  computed: {
    activityModule() {
      return this.getActivityModuleName()
    },
    recordId() {
      return this.$getProperty(this, 'details.id', null)
    },
  },
  methods: {
    setDefaultData() {
      if (this.moduleName === 'newreadingalarm') {
        this.record.id = this.details.alarm.id
        this.record.occurrencId = this.details.occurrence.id
      }
    },
    setActualActivity() {
      this.isActualActivity = true
      this.fixedFilter = this.$t('setup.decommission.actual_log')
    },
    setLogActivity() {
      this.isActualActivity = false
      this.fixedFilter = this.$t('setup.decommission.commissioning_log')
    },
    getActivityModuleName() {
      if (this.moduleName === 'newreadingalarm') {
        return 'alarmActivity'
      } else {
        return this.$getProperty(this, 'widget.widgetParams.activityModuleName')
      }
    },
    autoResize() {
      this.$nextTick(() => {
        let height = this.$refs['activities-container'].scrollHeight + 50
        let width = this.$refs['activities-container'].scrollWidth
        if (this.resizeWidget) {
          this.resizeWidget({ height, width })
        }
      })
    },
  },
}
</script>
<style lang="scss">
.activity-widget-page {
  .vue-portal-target {
    margin-top: 25px;
  }
  .el-table th.is-leaf {
    padding-top: 10px;
    padding-bottom: 10px;
    padding-left: 20px;
  }
}
</style>
