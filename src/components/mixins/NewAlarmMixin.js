// mixin for NewFaultsList, FaultsSummary and BMSAlarmsList, BMSAlarmsSummary independent of store

import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { mapGetters } from 'vuex'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData.js'

export default {
  data() {
    return {
      occurrenceList: null,
      eventList: null,
      occurrence: null,
      dialogVisible: false,
      createWoIds: [],
    }
  },
  computed: {
    modelDataClass() {
      return CustomModuleData
    },
  },
  methods: {
    //data fetching methods
    //used in both list and summary
    isActiveAlarm(alarm) {
      let severity = this.$getProperty(alarm, 'severity.severity')
      return severity !== 'Clear'
    },
    getAlarmColor(alarm) {
      return this.$getProperty(alarm, 'severity.color')
    },
    getAlarmDisplayName(alarm) {
      return this.$getProperty(alarm, 'severity.displayName', '---')
    },
    getSensorDisplayName(alarm) {
      let { readingField } = alarm || {}
      return this.$getProperty(readingField, 'displayName', '---')
    },
    ...mapGetters(['getTicketCategory', 'getTicketPriority', 'getUser']),

    //used only in list
    getNoOfOccurrences(record) {
      let { noOfOccurrences } = record
      return noOfOccurrences && noOfOccurrences > -1 ? noOfOccurrences : '0'
    },
    getAcknowledgedTime(record) {
      let { acknowledgedTime } = record
      return acknowledgedTime > 0
        ? this.$options.filters.fromNow(acknowledgedTime)
        : this.$options.filters.fromNow(new Date())
    },
    // update and acknowledge from summary
    async updateAlarmStatus(alarm) {
      //load severity
      let url = 'v2/module/data/list?moduleName=alarmseverity'
      let alarmSeverity = null
      await API.get(url).then(({ data, error }) => {
        if (!isEmpty(error)) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          alarmSeverity = data.moduleDatas
        }
      })
      //update clear status
      let clearedStatus = (alarmSeverity || []).find(
        status => status.severity === 'Clear'
      )
      let { id } = clearedStatus || {}
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
        moduleName: this.currentModuleName || this.moduleName,
        id: alarm.id,
      }
      let { error } = await API.post('v3/modules/data/patch', params)
      if (isEmpty(error)) {
        await this.loadRecords(true)
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
        moduleName: this.currentModuleName || this.moduleName,
        id: this.alarm.id,
      }
      let { error } = await API.post('v3/modules/data/patch', params)
      if (!error) {
        this.currentAlarm.acknowledged = true
        await this.loadRecords(true)
      } else {
        this.$message.error(error)
      }
    },

    //occurrence and event list
    async getOccurrenceFromId(alarmId, force = false) {
      try {
        let moduleName = 'alarmoccurrence'
        let filters = {
          alarm: { operatorId: 9, value: [alarmId.toString()] },
        }
        let { page } = this
        let params = {
          moduleName,
          filters,
          page,
          force,
          orderBy: 'CREATED_TIME',
          orderType: 'desc',
        }

        this.occurrenceList = await this.modelDataClass.fetchAll(params)
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    async getEventsFromId(alarmId, force = false) {
      try {
        let moduleName = 'baseevent'
        let filters = {
          baseAlarm: { operatorId: 9, value: [alarmId.toString()] },
        }
        let { page } = this
        let params = {
          moduleName,
          filters,
          page,
          force,
          orderBy: 'CREATED_TIME',
          orderType: 'desc',
        }

        this.eventList = await this.modelDataClass.fetchAll(params)
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    // work order stuff
    createWoDialog(idList) {
      this.createWoIds = idList
      this.dialogVisible = true
    },
    closeWoDialog() {
      this.createWoIds = []
      this.dialogVisible = false
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
    async createWO(workObj) {
      let { category, priority, assignedTo, assignmentGroup, siteId } = workObj
      let fields = {}
      if (category) {
        let tktCategory = this.getTicketCategory(workObj.category)
        let { name } = tktCategory || {}
        fields.category = {
          id: category,
          name: name,
        }
      }
      if (priority) {
        fields.priority = {
          id: priority,
          name: this.getTicketPriority(priority),
        }
      }
      if (assignedTo) {
        fields.assignedTo = assignedTo
      }
      if (assignmentGroup) {
        fields.assignmentGroup = assignmentGroup
      }
      if (siteId > 0) {
        fields.siteId = siteId
      }

      fields.isWoCreated = true
      let updateObj = {
        id: this.createWoIds,
        workorder: fields,
      }
      this.$message('Creating Workorder ...')
      let { error } = await API.post('/v2/alarmOccurrence/createWO', updateObj)
      if (isEmpty(error)) {
        this.$message.success('Workorder created successfully!')
        this.$refs['confirmWoModel'].reset()
        this.dialogVisible = false
      } else {
        this.$message.error(error.message)
        this.$refs['confirmWoModel'].reset()
        this.dialogVisible = false
      }
    },

    //export stuff
    async exportEvent(selectedFields) {
      let array = {
        alarmId: this.alarm.id,
        fieldId: selectedFields,
        type: this.exportType,
        parentId: this.alarm.resource.id,
      }
      let url = 'event/eventExport'
      this.$message({
        showClose: true,
        message: this.$t('common._common.downloading'),
        type: 'success',
      })
      let { error, data } = await API.post(url, array)
      if (isEmpty(error)) {
        this.exportDownloadUrl = data.fileUrl
        this.showAssetExport = false
      }
    },
  },
}
