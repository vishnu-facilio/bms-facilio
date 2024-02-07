<script>
import AlarmOverview from 'src/pages/firealarm/alarms/v1/AlarmOverview.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData.js'

export default {
  extends: AlarmOverview,
  data() {
    return {
      currentAlarm: null,
    }
  },
  computed: {
    alarm() {
      return this.currentAlarm
    },
    currentModuleName() {
      return 'newreadingalarm'
    },
    id() {
      let paramId = this.$attrs.id || this.$route.params.id
      return !isEmpty(paramId) ? parseInt(this.$route.params.id) : ''
    },
  },
  watch: {
    id: {
      handler() {
        this.loadRecord()
      },
      immediate: true,
    },
  },
  methods: {
    async loadRecord(force = true) {
      try {
        let { currentModuleName } = this
        let { newreadingalarm } = await API.fetchRecord(
          currentModuleName,
          {
            id: this.id,
          },
          { force }
        )
        this.currentAlarm = newreadingalarm
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },

    async acknowledgeAlarms() {
      let dataObj = {
        alarm: this.alarm,
        occurrence: this.occurrence,
        acknowledged: true,
        acknowledgedBy: this.$account.user,
        acknowledgedTime: Date.now(),
      }
      let params = {
        data: dataObj,
        moduleName: this.currentModuleName,
        id: this.alarm.id,
      }
      let { error } = await API.post('v3/modules/data/patch', params)
      if (!error) {
        this.alarm.acknowledged = true
      }
      await this.loadRecord()
    },
    async updateFaultStatus(fault) {
      let { alarmSeverity = [] } = this
      let clearedAlarm = (alarmSeverity || []).find(
        status => status.severity === 'Clear'
      )
      let { id } = clearedAlarm || {}
      let dataObj = {
        alarmOccurrence: {
          severity: {
            id: id,
          },
          clearedTime: Date.now(),
        },
        severity: {
          id: id,
        },
      }
      let params = {
        data: dataObj,
        moduleName: this.currentModuleName,
        id: fault.id,
      }
      let { error } = await API.post('v3/modules/data/patch', params)
      if (isEmpty(error)) {
        this.alarm.clearedBy = this.$account.user
        await this.loadRecord()
      }
    },
  },
}
</script>
