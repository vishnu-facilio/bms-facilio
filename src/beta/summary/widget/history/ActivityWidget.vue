<template>
  <div ref="activities-container">
    <portal :to="`header-${widget.id}-${widget.name}`">
      <div class="flex-middle justify-content-start">
        <FContainer padding="containerXLarge">
          <!-- class="pT20 pR30" -->
          <el-dropdown
            class="fc-dropdown-menu-border"
            trigger="click"
            placement="bottom-start"
            @command="setActivity"
            :disbaled="disableHistorySwitch"
          >
            <el-button type="primary" class="fc-btn-group-white">
              <div>
                {{ fixedFilter }}
                <i class="el-icon-arrow-down"></i>
              </div>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="actual">
                <div>
                  {{ $t('setup.decommission.actual_log') }}
                </div>
              </el-dropdown-item>
              <el-dropdown-item
                v-if="showCommisitioningOption"
                command="decommisson"
              >
                <div>
                  {{ $t('setup.decommission.commissioning_log') }}
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </FContainer>
      </div>
    </portal>

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
import Activities from './Activities'
import LogActivities from './LogActivities.vue'
import { FContainer } from '@facilio/design-system'

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
    FContainer,
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
    disableHistorySwitch() {
      return !this.$helpers.isLicenseEnabled('COMMISSIONING')
    },
    showCommisitioningOption() {
      return this.$helpers.isLicenseEnabled('COMMISSIONING')
    },
  },
  methods: {
    setDefaultData() {
      if (this.moduleName === 'newreadingalarm') {
        this.record.id = this.details.alarm.id
        this.record.occurrencId = this.details.occurrence.id
      }
    },
    setActivity(command) {
      if (command === 'actual') {
        this.isActualActivity = true
        this.fixedFilter = this.$t('setup.decommission.actual_log')
      } else {
        this.isActualActivity = false
        this.fixedFilter = this.$t('setup.decommission.commissioning_log')
      }
      this.$nextTick(() => {
        this.resizeWidget({ h: this.widget.height })
      })
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
        let height = this.$refs['activities-container'].scrollHeight + 20
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
