import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { mapState, mapActions, mapGetters } from 'vuex'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: [],
  computed: {
    ...mapState({
      alarms: state => state.newAlarm.alarms,
      users: state => state.users,
      devices: state => state.devices,
      severityStatus: state => state.alarmSeverity,
      occurrenceList: state => state.newAlarm.occurrence,
      eventList: state => state.newAlarm.events,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters(['getTicketCategory', 'getTicketPriority']),
    alarmId() {
      return parseInt(this.$route.params.id)
    },
    alarm() {
      return this.$store.state.newAlarm.currentAlarm
    },
    occurrence() {
      return this.$store.state.newAlarm.currentOccurrence
    },
  },
  methods: {
    ...mapActions({
      updateAlarmStatus: 'newAlarm/updateAlarmStatus',
      acknowledgeAlarm: 'newAlarm/acknowledgeAlarm',
    }),
    openRuleSummary(id, isNewReadingRule) {
      if (id) {
        if (isNewReadingRule) {
          this.$router.push({
            name: 'newRulesSummary',
            params: { id, viewname: 'all' },
            query: this.$route.query,
          })
        } else {
          this.$router.push({
            name: 'ruleOverview',
            params: { id },
          })
        }
      }
    },
    loadAlarmDetails() {
      let self = this
      this.alarmOverviewLoading = true
      let params = { alarmId: this.alarmId, isSummary: true }
      self.$http
        .post('/v2/alarm/rules/fetchRule', params)
        .then(function(response) {
          self.rule = response.data.result
          self.alarmOverviewLoading = false
        })
    },
    exportEvent(type) {
      let array = {
        alarmId: this.occurrence.id,
        fieldId: this.selectedFields,
        type: this.exportType,
        parentId: this.alarm.resource.id,
      }
      let self = this
      let url = 'event/eventExport'
      self.$message({
        showClose: true,
        message: 'Downloading...',
        type: 'success',
      })
      this.$http.post(url, array).then(function(response) {
        self.exportDownloadUrl = response.data.fileUrl
        self.showAssetExport = false
      })
    },
    exportSummary(type) {
      this.exportType = type
      this.showAssetExport = true
      this.$util.loadAssetReadingFields(this.alarm.resource.id).then(fields => {
        this.assetFields = fields
      })
    },
    createWO(data) {
      let self = this
      let fields = {}
      if (data.category) {
        fields.category = {
          id: data.category,
          name: this.getTicketCategory(data.category).name,
        }
      }
      if (data.priority) {
        fields.priority = {
          id: data.priority,
          name: this.getTicketPriority(data.priority),
        }
      }
      if (data.assignedTo) {
        fields.assignedTo = data.assignedTo
      }
      if (data.assignmentGroup) {
        fields.assignmentGroup = data.assignmentGroup
      }
      self.$store
        .dispatch('newAlarm/createWoFromAlarm', {
          id: [this.alarm.id],
          fields: fields,
        })
        .then(function() {
          self.$dialog.notify('Workorder created successfully!')
          self.showCreateWo = false
        })
    },
    openWorkorder(id) {
      if (id > 0) {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('workorder', pageTypes.OVERVIEW) || {}
          if (name) {
            this.$router.push({ name, params: { viewname: 'all', id } })
          }
        } else {
          this.$router.push({ path: '/app/wo/orders/summary/' + id })
        }
      } else {
        return false
      }
    },
    updateAlarmsStatus() {
      this.updateAlarmStatus({
        occurrence: this.occurrence,
        alarm: this.alarm,
        clearedTime: Date.now(),
        severity: this.severityStatus.find(
          status => status.severity === 'Clear'
        ),
      })
      this.alarm.clearedBy = this.$account.user
    },
    loadFieldDetails: function() {
      let self = this
      let url = '/field/' + this.alarm.readingFieldId
      self.$http.get(url).then(function(response) {
        self.readingField = response.data.field
      })
    },
    acknowledgeAlarms() {
      let self = this
      this.acknowledgeAlarm({
        alarm: this.alarm,
        occurrence: this.occurrence,
        acknowledged: true,
        acknowledgedBy: this.$account.user,
      })
      this.acknowledged = true
    },
    loadSpaceDetails() {
      if (this.resourceDetails) {
        this.$util
          .loadBaseSpaceDetails(
            this.resourceDetails.spaceId
              ? this.resourceDetails.spaceId
              : this.resourceDetails.buildingId
              ? this.resourceDetails.buildingId
              : this.resourceDetails.siteId
          )
          .then(result => {
            this.spaceDetails = result
          })
          .catch(err => {})
      }
    },
  },
}
