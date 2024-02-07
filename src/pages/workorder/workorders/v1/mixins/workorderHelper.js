import PreviewFile from '@/PreviewFile'
import $helpers from 'util/helpers'
import moment from 'moment-timezone'
import util from 'util/util'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import spaceCardMixin from 'src/components/mixins/SpaceCardMixin'
import transformMixin from './workorderTransform'
import { loadApps, getFilteredApps } from 'util/appUtil'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
import Constants from 'util/constant'
import Vue from 'vue'
import { getCurrencyForCurrencyCode } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  mixins: [spaceCardMixin, transformMixin],
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketType')
    this.availablePortalApps()
  },
  components: {
    PreviewFile,
  },
  data() {
    return {
      portalApps: [],
      selectedPortalApp: [],
    }
  },
  computed: {
    ...mapGetters(['getTicketStatusByLabel', 'getCurrentSiteId']),
    isNotPortal() {
      let {
        appCategory: { PORTALS },
      } = Constants
      let { appCategory } = getApp()

      return appCategory !== PORTALS
    },
    recordCurrency() {
      let { currencyCode } = this.workorder || {}
      return getCurrencyForCurrencyCode(currencyCode)
    },
  },
  methods: {
    async availablePortalApps() {
      let { error, data } = await loadApps()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let applications = getFilteredApps(data)
        this.portalApps = applications.filter(
          app => app.appCategoryEnum === 'PORTALS'
        )
      }
      this.portalApps.forEach(portal => {
        let obj = { appId: portal.id, appSelected: false, appName: portal.name }
        this.selectedPortalApp.push(obj)
      })
    },
    updateStatus(status) {
      let self = this
      if (status === 'On Hold') {
        self.rejectWorkOrderDialog()
        return
      } else if (
        status === 'Resolved' &&
        self.workorder.isWorkDurationChangeAllowed
      ) {
        self.workDurationCallBack = self.setStatusUpdate.bind(self, status)
        self.showWorkDurationDialog = true
        return
      }
      self.setStatusUpdate(status)
    },
    setStatusUpdate(status, actualDuration) {
      let self = this
      let statusField = {
        id: this.getTicketStatusByLabel(status, 'workorder').id,
        status: status,
      }

      let paramObj = { status: statusField }
      let workTimings = []
      let duration = -1
      if (actualDuration) {
        workTimings = actualDuration.workTimings
        duration = actualDuration.duration
      }
      if (status === 'Resolved') {
        self.resolveWorkOrder(
          [this.workorder.id],
          duration,
          workTimings,
          statusField
        )
      } else if (status) {
        self.updateWorkOrder([this.workorder.id], paramObj, workTimings)
        self.$set(self, 'workorder.status', statusField)
      }
      // self.workorder.status = statusField
    },

    rejectWorkOrderDialog() {
      let promptObj = {
        title: this.$t('maintenance._workorder.wo_pause_title'),
        message: this.$t('maintenance._workorder.wo_pause_message'),
        promptType: 'textarea',
        promptPlaceholder: this.$t(
          'maintenance._workorder.wo_pause_message_area'
        ),
        rbDanger: true,
        rbLabel: this.$t('maintenance._workorder.pause'),
      }

      this.$dialog.prompt(promptObj).then(value => {
        if (value !== null) {
          let newComments = {
            parentModuleLinkName: 'ticketnotes',
            parentId: this.workorder.id,
            body: value,
            notifyRequester: false,
          }
          this.setStatusUpdate('On Hold')
          this.$http
            .post('v2/notes/add', {
              note: newComments,
              module: 'ticketnotes',
              ticketModuleName: 'workorder',
            })
            .then(response => {
              if (response.data && response.data.responseCode === 0) {
                this.comments.push(response.data.result.Notes)
                this.reset()
                this.loadactivitie()
              }
            })
        }
      })
    },
    formDate(time, fomat) {
      fomat = fomat || 'DD-MMM-YYYY'
      return moment(time)
        .tz(this.$timezone)
        .format(fomat)
    },
    fetchWo(force) {
      return this.$store.dispatch('workorder/fetchWorkOrder', {
        id: this.openWorkorderId,
        force,
      })
    },
    fetchVendorWo(force) {
      return this.$store.dispatch('workorder/fetchVendorWorkOrder', {
        id: this.openWorkorderId,
        force,
      })
    },
    updateEmptyField() {
      if (
        this.selectedFieldUpdate.dataTypeEnum === 'DATE' ||
        this.selectedFieldUpdate.dataTypeEnum._name === 'DATE'
      ) {
        this.$refs.showcustomdatepicker.focus()
      } else if (
        this.selectedFieldUpdate.dataTypeEnum === 'DATE_TIME' ||
        this.selectedFieldUpdate.dataTypeEnum._name === 'DATE_TIME'
      ) {
        this.$refs.showcustomdatepicker.focus()
      } else {
        this.customFieldUpdate = true
      }
    },
    updateCustomFields() {
      let updateCustomfield = {}
      if (this.selectedFieldUpdate.dataTypeEnum._name === 'ENUM') {
        updateCustomfield[
          this.selectedFieldUpdate.name
        ] = this.selectedFieldUpdate.enumMap[parseInt(this.selectedFieldValue)]
      } else {
        updateCustomfield[
          this.selectedFieldUpdate.name
        ] = this.selectedFieldValue
      }
      this.updateWorkOrder([this.workorder.id], { data: updateCustomfield })
      this.customFieldUpdate = false
    },
    closeUpdateDialog() {
      this.customFieldUpdate = false
    },
    getSiteName(siteId) {
      if (this.sites) {
        let s = this.sites.find(i => i.id === siteId)
        if (!s) {
          return '---'
        }
        return s.name
      }
      return '---'
    },
    openAssetBreakDown(assetid, wosubject) {
      this.assetBDSourceDetails.condition = wosubject
      this.assetBDSourceDetails.assetid = assetid
      this.assetBDSourceDetails.sourceId = this.workorder.id
      this.assetBDSourceDetails.sourceType = 1
      this.showAssetBreakdown = true
    },
    openAsset(id) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                viewname: 'all',
                id,
              },
            })
          }
        } else {
          let url = '/app/at/assets/all/' + id + '/overview'
          this.$router.replace({ path: url })
        }
      }
    },
    openPM() {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('preventivemaintenance', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id: this.workorder.pm.id,
            },
          })
        }
      } else {
        this.$router.replace({
          path: '/app/wo/planned/summary/' + this.workorder.pm.id,
        })
      }
    },
    updateSite(woId, siteId) {
      this.$dialog
        .confirm({
          title: this.$t('maintenance._workorder.change_site'),
          message: this.$t('maintenance._workorder.change_site_message'),
          rbDanger: true,
          rbLabel: this.$t('maintenance._workorder.change_site_message_label'),
        })
        .then(value => {
          if (value) {
            this.updateWorkOrder([woId], { siteId })
          }
        })
    },
    setDueDate(duedate) {
      if (duedate === -1) {
        this.duedate = Date.now() - 3600 * 1000 * 24
      } else {
        this.duedate = new Date(duedate)
      }
    },
    onChangeDueDate(pickVal) {
      this.updateWorkOrderDueDate(
        [this.workorder.id],
        new Date(pickVal).getTime()
      )
    },
    onChangeCustomField(pickVal) {
      let updateCustomfield = {}
      updateCustomfield[this.selectedFieldUpdate.name] = new Date(
        pickVal
      ).getTime()
      this.updateWorkOrder([this.workorder.id], { data: updateCustomfield })
    },
    back() {
      this.$router.go(-1)
    },
    closeAllTask() {
      let self = this
      let taskIds = []
      let ticketId
      for (let idx in this.taskList) {
        for (let idx2 in this.taskList[idx]) {
          let task = this.taskList[idx][idx2]

          if (task.status.type === 'OPEN' && task.isUpdating) {
            this.$message({
              message: this.$t('maintenance._workorder.task_updating'),
              type: 'warning',
            })
            return
          }

          ticketId = task.parentTicketId
          if (
            this.selectedTaskResource === null ||
            (this.selectedTaskResource !== null &&
              task.resource.id === this.selectedTaskResource &&
              task.status.type === 'OPEN')
          ) {
            taskIds.push(this.taskList[idx][idx2].id)
          }
        }
      }
      let data = {
        taskIdList: taskIds,
        parentTicketId: ticketId,
      }
      if (this.selectedTaskResource === null) {
        this.$http.post('v2/tasks/closeAllTask', data).then(response => {
          if (response.data.responseCode === 0) {
            if (this.ticketstatusvalue !== 'Work in Progress') {
              let statusField = this.getTicketStatusByLabel(
                'Work in Progress',
                'workorder'
              )
              let { id: statusId = -1 } = statusField || {}
              this.$set(this, 'workorder.status.id', statusId)
            }
            this.loadTasks()
            //this.fetchWo(true) // commented this out, as we're gonna fetch the workorder obj from API, instead of from store
            this.reloadWorkOrderSummaryToUpdateState(response.data.result)
          } else {
            this.$message.error(response.data.message)
          }
        })
      } else {
        this.$http
          .post(
            '/v2/tasks/closeAllTask?resourceId=' + this.selectedTaskResource,
            data
          )
          .then(response => {
            if (response.data.responseCode === 0) {
              if (this.ticketstatusvalue !== 'Work in Progress') {
                let statusField = this.getTicketStatusByLabel(
                  'Work in Progress'
                )
                let { id: statusId = -1 } = statusField || {}
                this.$set(this, 'workorder.status.id', statusId)
              }
              this.loadTasks()
              // this.fetchWo(true) // commented this out, as we're gonna fetch the workorder obj from API, instead of from store
              this.reloadWorkOrderSummaryToUpdateState(response.data.result)
            } else {
              this.$message.error(response.data.message)
            }
          })
      }
    },
    loadFields() {
      this.loading = true
      this.$util.loadModuleMeta('workorder').then(meta => {
        this.fields = meta.fields
        this.loading = false
      })
    },
    taskStatus(task) {
      let self = this
      let completed = task.status.type === 'OPEN'
      let data = {
        id: [task.id],
        task: {
          id: task.id,
          parentTicketId: task.parentTicketId,
          status: {
            id: completed ? this.closedStatusId : this.openStatusId,
            type: completed ? 'CLOSED' : 'OPEN',
          },
        },
      }

      this.$http
        .post('v2/tasks/updatestatus', data)
        .then(response => {
          if (response.data && response.data.responseCode === 0) {
            if (typeof response.data === 'object') {
              if (completed) {
                task.status = {
                  id: this.closedStatusId,
                  type: 'CLOSED',
                }
                this.$message({
                  message: this.$t('maintenance._workorder.task_close_success'),
                  type: 'success',
                })
              } else {
                task.status = {
                  id: this.openStatusId,
                  type: 'OPEN',
                }
                this.$message({
                  message: this.$t('maintenance._workorder.task_open_success'),
                  type: 'success',
                })
              }
              if (this.ticketstatusvalue !== 'Work in Progress') {
                let statusField = this.getTicketStatusByLabel(
                  'Work in Progress',
                  'workorder'
                )
                let { id: statusId = -1 } = statusField || {}
                this.$set(this, 'workorder.status.id', statusId)
              }
            } else {
              this.$message({
                message: this.$t('maintenance._workorder.operation_failed'),
                type: 'error',
              })
            }
            //this.fetchWo(true) // commented this out, as we're gonna fetch the workorder obj from API, instead of from store
            this.forceReloadIfTaskActionExecuted(response)
            let { isTaskActionExecuted = false } = response.data.result || {}
            if (!isTaskActionExecuted) {
              // Added this check as forceReloadIfTaskActionExecuted() already has onTransitionSuccess() to reload the page.
              this.reloadWorkOrderSummaryToUpdateState(
                response.data.result || {}
              )
            }
            // this.$root.$emit('reloadWO')
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(errorResponse => {
          let defaultMessage = this.$t(
            'maintenance._workorder.operation_failed'
          )
          let { data } = errorResponse || {}
          let { message = defaultMessage } = data || {}
          this.$message.error(message)
        })
    },
    reloadWorkOrderSummaryToUpdateState(response) {
      let { reloadWorkOrder = false } = response || {}
      if (reloadWorkOrder) {
        eventBus.$emit('refreshWorkOrderPage')
      }
    },
    changeTaskStatus(task) {
      let { preRequestStatus = -1 } = this.workorder || {}
      if (preRequestStatus !== 1 || preRequestStatus !== 4) {
        let isOpen = task.status.type === 'OPEN'

        let { id = -1 } = task || {}
        if (isOpen && id === -1) {
          this.$message({
            message: this.$t(
              'maintenance._workorder.please_save_the_task_before_updating_its_status'
            ),
            type: 'warning',
          })
          return
        }

        if (isOpen && task.inputType !== 1 && !task.inputValue) {
          this.$message({
            message: this.$t('maintenance._workorder.task_input'),
            type: 'warning',
          })
          return
        }

        if (isOpen && task.isUpdating) {
          this.$message({
            message: this.$t('maintenance._workorder.task_updating'),
            type: 'warning',
          })
          return
        }

        if (isOpen && this.taskBulkList.length) {
          setTimeout(() => this.taskStatus(task), 500)
        } else {
          this.taskStatus(task)
        }
      }
    },
    showSpaceAssetChooser() {
      this.visibility = true
    },
    associate(selectedObj) {
      this.visibility = false
      this.selectedTask.resource = selectedObj
      this.selectedTask.selectedResourceName = selectedObj.name
    },
    onReadingChange(task, skipValidation) {
      if (!task.inputValue) {
        return
      }
      return this.addTask(task, skipValidation)
    },
    asyncTaskUpdate(doValidation = true, skipValidation = true) {
      let taskListContext = []
      let taskBulkListRef = this.taskBulkList

      if (this.waitBoolean) {
        if (this.taskBulkList) {
          this.taskBulkList.filter(task => {
            let data = {
              id: task.id,
              inputValue: task.inputValue,
              inputValues: task.inputValues,
              remarks: task.remarks,
              readingFieldId: task.readingFieldId,
              attachment: task.attachment,
            }
            if (
              this.$helpers.isLicenseEnabled('READING_FIELD_UNITS_VALIDATION')
            ) {
              data['readingFieldUnit'] = task.readingFieldUnit
            }
            if (task.inputTime > 0) {
              data.inputTime = this.$helpers.getTimeInOrg(task.inputTime)
            }

            if (this.readingData === 3) {
              data.additionalInfo = {
                isMeterReset: true,
                meterResetConsumption: this.consumptionData,
              }
            }

            taskListContext.push(data)
          })
          if (taskListContext) {
            let promise = this.updateTaskList(
              { taskContextList: taskListContext },
              doValidation,
              skipValidation
            )

            Promise.all([promise]).finally(() => {
              /*
                Temporary fix to solve changestatus being called before updatetask command when the input is entered
                and task closed immediately
              */
              taskBulkListRef.forEach(task => {
                task.isUpdating = false
              })
            })
          }
        }
      }
    },
    asyncPreRequestUpdate(doValidation = true) {
      let taskListContext = []
      let taskBulkListRef = this.taskBulkList

      if (this.waitBoolean) {
        if (this.taskBulkList) {
          this.taskBulkList.filter(task => {
            let data = {
              id: task.id,
              inputValue: task.inputValue,
              inputValues: task.inputValues,
              remarks: task.remarks,
              readingFieldId: task.readingFieldId,
              preRequest: true,
            }
            if (task.inputTime > 0) {
              data.inputTime = this.$helpers.getTimeInOrg(task.inputTime)
            }
            taskListContext.push(data)
          })
          if (taskListContext) {
            this.updatePreRequestList(
              { taskContextList: taskListContext },
              doValidation
            ).finally(() => {
              /*
                Temporary fix to solve changestatus being called before updatetask command when the input is entered
                and task closed immediately
              */
              taskBulkListRef.forEach(task => {
                task.isUpdating = false
              })
            })
          }
        }
      }
    },
    updatePreRequestList(data, doValidation = true) {
      this.taskBulkList = []
      this.waitBoolean = false
      this.readingFieldSuggestions = []
      this.readingFieldError = null
      return this.$http
        .post(`v2/tasks/updateAllTask?doValidation=${doValidation}`, data)
        .then(response => {
          if (response.data && response.data.responseCode === 0) {
            if (typeof response.data.result === 'object') {
              this.workorder.preRequestStatus =
                response.data.result.preRequestStatus
              this.waitBoolean = true
              this.noOfUpdatedTAsk =
                this.noOfUpdatedTAsk + response.data.result.rowsUpdated
              if (
                typeof response.data.result.error !== 'undefined' &&
                response.data.result.error
              ) {
                data.taskContextList.forEach(datum => {
                  if (!(datum.id in response.data.result.error)) {
                    this.$set(this.errorMap, datum.id, false)
                    this.$set(this.errorMessage, datum.id, null)
                  }
                })
                Object.assign(this.errorQue, response.data.result.error)
              } else {
                data.taskContextList.forEach(datum => {
                  this.$set(this.errorMap, datum.id, false)
                  this.$set(this.errorMessage, datum.id, null)
                })
              }
              if (this.taskBulkList.length > 0) {
                this.asyncPreRequestUpdate(doValidation)
                return
              } else {
                this.$message({
                  showClose: true,
                  message:
                    this.noOfUpdatedTAsk +
                    this.$t(
                      'maintenance._workorder.prerequisite_updated_success'
                    ),
                  type: 'success',
                })
                this.noOfUpdatedTAsk = 0
              }
              if (Object.keys(this.errorQue).length > 0) {
                this.$dialog
                  .confirm({
                    title: this.$t('_workoder.add_readings'),
                    message: this.$t('_workoder.add_readings_message'),
                    rbDanger: true,
                    rbLabel: this.$t('maintenance._workorder.save'),
                  })
                  .then(function(val) {
                    if (val) {
                      let tasks = this.newTaskList
                        .map(v => v.taskList)
                        .reduce((a, b) => a.concat(b))
                        .filter(v => {
                          return v.id in this.errorQue
                        })
                      let taskContextList = []
                      tasks.forEach(t => {
                        let data = {
                          id: t.id,
                          inputValue: t.inputValue,
                          inputValues: t.inputValues,
                          remarks: t.remarks,
                          readingFieldId: t.readingFieldId,
                        }
                        taskContextList.push(data)
                      })
                      this.errorQue = {}
                      this.updatePreRequestList({ taskContextList }, false)
                    } else {
                      Object.keys(this.errorQue).forEach(key => {
                        this.$set(
                          this.errorMessage,
                          key,
                          this.errorQue[key].message
                        )
                        this.$set(this.errorMap, key, true)
                      })
                    }
                    this.errorQue = {}
                  })
              }
              this.taskBulkList = []
            } else {
              this.$message({
                showClose: true,
                message: this.$t(
                  'maintenance._workorder.prerequisite_updated_failed'
                ),
                type: 'error',
              })
            }
            this.loadactivitie()
          }
        })
        .finally(() => {
          this.waitBoolean = true
        })
    },
    updateTaskList(data, doValidation = true, skipValidation = true) {
      this.taskBulkList = []
      this.waitBoolean = false
      let apiurl = `v2/tasks/updateAllTask?doValidation=${doValidation}`
      if (this.$helpers.isLicenseEnabled('READING_FIELD_UNITS_VALIDATION')) {
        apiurl = `v2/tasks/updateAllTask?doValidation=${doValidation}&skipValidation=${skipValidation}`
        if (data.taskContextList.length > 0) {
          this.currentTaskObj = data.taskContextList[0]
        }
      }
      let formData = data
      // its assumed that there is only one task that is being saved
      if (data.taskContextList[0].attachment) {
        formData = new FormData()
        formData.append('module', data.taskContextList[0].attachment.module)
        formData.append('recordId', data.taskContextList[0].attachment.recordId)
        let attachment = data.taskContextList[0].attachment
        delete data.taskContextList[0].attachment
        this.$helpers.setFormData(
          'taskContextList',
          data.taskContextList,
          formData
        )
        if (attachment.beforeFile) {
          formData.append(
            'beforeAttachment',
            attachment.beforeFile,
            attachment.beforeFileName
          )
        }
        if (attachment.afterFile) {
          formData.append(
            'afterAttachment',
            attachment.afterFile,
            attachment.afterFileName
          )
        }
      }
      return this.$http
        .post(apiurl, formData)
        .then(response => {
          if (response.data && response.data.responseCode === 0) {
            if (typeof response.data === 'object') {
              if (response.data.result.taskErrors) {
                response.data.result.taskErrors.forEach(taskError => {
                  if (taskError.mode == 1 && taskError.failType == 1) {
                    this.readingFieldErrorToggle = true
                    this.readingFieldError = taskError.message
                    this.currentValue = taskError.currentValue
                    this.previousValue = taskError.previousValue
                  } else if (taskError.mode == 2) {
                    this.readingFieldSuggestionToggle = true
                    this.readingFieldSuggestions.push(taskError.message)
                    this.suggestedUnits.push(taskError.suggestedUnit)
                  }
                })
              } else if (
                response.data.result.requiresRemarks ||
                response.data.result.requiresAttachment
              ) {
                if (isArray(data.taskContextList)) {
                  let id = data.taskContextList[0].id
                  let selectedTask
                  this.newTaskList.forEach(section => {
                    if (
                      !isEmpty(section.taskList) &&
                      isArray(section.taskList) &&
                      !selectedTask
                    ) {
                      selectedTask = section.taskList.find(
                        task => task.id === id
                      )
                      if (selectedTask) {
                        this.selectedTask = selectedTask
                        this.addTaskRemarksDialog =
                          response.data.result.requiresRemarks
                        this.addAttachmentRequiredDialog =
                          response.data.result.requiresAttachment
                        this.showMandatoryInputsDialog = true
                        return
                      }
                    }
                  })
                }
              } else {
                this.workorder.preRequestStatus =
                  response.data.result.preRequestStatus
                this.waitBoolean = true
                this.noOfUpdatedTAsk =
                  this.noOfUpdatedTAsk + response.data.result.rowsUpdated
                if (
                  typeof response.data.result.error !== 'undefined' &&
                  response.data.result.error
                ) {
                  data.taskContextList.forEach(datum => {
                    if (!(datum.id in response.data.result.error)) {
                      this.$set(this.errorMap, datum.id, false)
                      this.$set(this.errorMessage, datum.id, null)
                    }
                  })
                  Object.assign(this.errorQue, response.data.result.error)
                } else {
                  data.taskContextList.forEach(datum => {
                    this.$set(this.errorMap, datum.id, false)
                    this.$set(this.errorMessage, datum.id, null)
                  })
                }
                if (this.taskBulkList.length > 0) {
                  this.asyncTaskUpdate(doValidation, skipValidation)
                  return
                } else {
                  this.$message({
                    showClose: true,
                    message:
                      (this.noOfUpdatedTAsk > 1
                        ? this.noOfUpdatedTAsk + ' '
                        : '') +
                      this.$t('maintenance._workorder.task_updated_success'),
                    type: 'success',
                  })
                  this.noOfUpdatedTAsk = 0
                  this.readingFieldErrorToggle = false
                  this.readingFieldSuggestionToggle = false
                  this.readingData = 6
                  this.consumptionData = ''
                }
                if (this.ticketstatusvalue !== 'Work in Progress') {
                  let statusField = this.getTicketStatusByLabel(
                    'Work in Progress',
                    'workorder'
                  )
                  let { id: statusId = -1 } = statusField || {}
                  this.$set(this, 'workorder.status.id', statusId)
                }
                if (Object.keys(this.errorQue).length > 0) {
                  this.$dialog
                    .confirm({
                      title: this.$t('_workoder.add_readings'),
                      message: this.$t('_workoder.add_readings_message'),
                      rbDanger: true,
                      rbLabel: this.$t('maintenance._workorder.save'),
                    })
                    .then(function(val) {
                      if (val) {
                        let tasks = this.newTaskList
                          .map(v => v.taskList)
                          .reduce((a, b) => a.concat(b))
                          .filter(v => {
                            return v.id in this.errorQue
                          })
                        let taskContextList = []
                        tasks.forEach(t => {
                          let data = {
                            id: t.id,
                            inputValue: t.inputValue,
                            inputValues: t.inputValues,
                            remarks: t.remarks,
                            readingFieldId: t.readingFieldId,
                          }
                          taskContextList.push(data)
                        })
                        this.errorQue = {}
                        this.updateTaskList({ taskContextList }, false)
                      } else {
                        Object.keys(this.errorQue).forEach(key => {
                          this.$set(
                            this.errorMessage,
                            key,
                            this.errorQue[key].message
                          )
                          this.$set(this.errorMap, key, true)
                        })
                      }
                      this.errorQue = {}
                    })
                }
                this.taskBulkList = []
              }
            } else {
              this.$message({
                showClose: true,
                message: this.$t('maintenance._workorder.task_updated_failed'),
                type: 'error',
              })
            }
            this.loadactivitie()
            // this.$root.$emit('reloadWO')
            this.forceReloadIfTaskActionExecuted(response)
          } else {
            if (data.taskContextList && data.taskContextList.length === 1) {
              let cl = this.$helpers.cloneObject(this.taskErrorMap)
              cl[data.taskContextList[0].id] = response.data.message
              this.taskErrorMap = cl
            }
            this.$message({
              showClose: true,
              message: response.data.message,
              type: 'error',
            })
          }
        })
        .finally(() => {
          this.waitBoolean = true
        })
    },
    forceReloadIfTaskActionExecuted(response) {
      // force reload the UI, if isTaskActionExecuted, through WorkFlow
      let { isTaskActionExecuted } = response.data.result || {}
      if (isTaskActionExecuted) {
        this.onTransitionSuccess()
      }
    },

    getNextWorkOrderTime() {
      return this.$http.get(
        `/v2/workorders/getNextWorkOrderTime?workOrderId=${this.openWorkorderId}`
      )
    },

    waitFunc: $helpers.debounce(function() {
      if (this.taskBulkList.length) {
        this.asyncTaskUpdate()
      }
    }, 5000),

    addTask(task, skipValidation) {
      let self = this
      if (task.id !== -1) {
        task.isUpdating = true
        this.taskBulkList.push(task)
        this.asyncTaskUpdate(null, skipValidation)
      } else {
        let formData = {}
        if (task.subject.trim() === '') {
          this.$message({
            message: this.$t('maintenance._workorder.task_title_required'),
            type: 'error',
          })
          return
        }
        if (isEmpty(task.resource.id)) {
          if (!isEmpty(self.workorder.resource)) {
            let { id, name } = self.workorder.resource
            task.resource = { id, name }
          } else {
            delete task.resource
          }
        }
        if (!task.assignedTo) {
          delete task.assignedTo
        }
        if (!task.readingFieldId) {
          task.readingFieldId = -1
        }
        if (task.inputType === '5' || task.inputType === '6') {
          let taskOptions = []
          for (let key in task.options) {
            let option = task.options[key]
            taskOptions[key] = option.name
          }
          task.options = taskOptions
        }

        task.parentTicketId = self.workorder.id
        formData.task = task

        self.$http
          .post('/task/add', formData)
          .then(function(response) {
            task.id = response.data.taskId
            self.tasks.push(task)
            self.newTask = {
              subject: '',
              description: '',
              resource: {
                id: -1,
              },
              isReadingTask: false,
              readingFieldId: -1,
              options: [],
              taskType: -1,
              enableInput: false,
              attachmentRequired: false,
              id: -1,
            }
            self.loadactivitie()
            self.$message({
              message: self.$t('maintenance._workorder.task_added_success'),
              type: 'success',
            })
            // show the + button back on screen
            self.isUserAddingTask = false
            self.onTransitionSuccess()
          })
          .catch(res => {
            self.$message({
              message: res.data.message,
              type: 'error',
            })
          })
      }
    },
    addPreRequest(task) {
      let self = this
      if (task.id !== -1) {
        task.inputValue = task.inputValue === task.truevalue ? '1' : '0'
        task.isUpdating = true
        this.taskBulkList.push(task)
        this.asyncPreRequestUpdate()
        task.inputValue =
          task.inputValue === '1' ? task.truevalue : task.falsevalue
      }
    },
    addPreRequesites(taskList) {
      let self = this
      taskList.forEach(task => {
        if (task.id !== -1) {
          task.inputValue = task.inputValue === task.truevalue ? '1' : '0'
          task.isUpdating = true
          this.taskBulkList.push(task)
        }
      })
      this.asyncPreRequestUpdate()
      taskList.forEach(task => {
        task.inputValue =
          task.inputValue === '1' ? task.truevalue : task.falsevalue
      })
    },
    addTaskOption(selectedTask) {
      selectedTask.options.push({ name: '' })
    },
    getPMType(type) {
      return this.$store.getters.getTicketTypePickList()[type]
    },
    addNewSection() {
      //
    },
    // Adding task with section
    addNewTaskToASection(sectionId) {
      if (isEmpty(sectionId)) {
        return
      }

      // disable the + button when user is adding a new task
      this.isUserAddingTask = true

      // Add a new task to the section with ID "sectionId"
      this.newTask = {
        subject: '',
        description: '',
        resource: {
          id: -1,
        },
        isReadingTask: false,
        readingFieldId: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        status: {
          type: 'OPEN',
          typeCode: 1,
          id: -1,
        },
        id: -1,
        sectionId: sectionId,
      }
      if (!this.taskList[sectionId]) {
        this.taskList[sectionId] = []
      }
      this.taskList[sectionId].push(this.newTask)
      this.showTaskDetails(this.newTask)
    },
    // Adding task without section
    addNewTask() {
      // if (this.selectedTask && this.selectedTask.subject !== '') {
      //   this.addTask(this.selectedTask)
      // }

      // disable the + button when user is adding a new task
      this.isUserAddingTask = true

      let lastSection = -1
      for (let key in this.taskList) {
        if (this.taskList.hasOwnProperty(key)) {
          if (
            key !== -1 ||
            (key === -1 && Object.keys(this.taskList).length === 1)
          ) {
            lastSection = key
          }
        }
      }
      this.newTask = {
        subject: '',
        description: '',
        resource: {
          id: -1,
        },
        isReadingTask: false,
        readingFieldId: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        status: {
          type: 'OPEN',
          typeCode: 1,
          id: -1,
        },
        id: -1,
        sectionId: lastSection,
      }
      let taskListCopy = this.taskList
      if (!taskListCopy[lastSection]) {
        taskListCopy[lastSection] = []
      }
      taskListCopy[lastSection].push(this.newTask)
      this.$set(this, 'taskList', {})
      this.$set(this, 'taskList', taskListCopy)
      this.showTaskDetails(this.newTask)
    },
    checkAndShowTask(task) {
      if (this.showAddTask) {
        this.showTaskDetails(task)
      }
    },
    checkAndShowPreRequest(task) {
      if (this.showAddPreRequest) {
        this.showPreRequestDetails(task)
      }
    },
    checkOptionsLength(options) {
      return options.length <= 2 && !options.some(option => option.length > 10)
    },
    onSelectInput() {
      if (this.selectedTask.inputType === '2') {
        this.fillTaskReadings()
      }
    },
    fillTaskReadings() {
      if (
        this.selectedTask.resource &&
        this.selectedTask.resource.resourceType === 2 &&
        this.selectedTask.resource.category
      ) {
        let categoryId = this.$getProperty(
          this,
          'selectedTask.resource.category.id'
        )
        let result = util.loadAssetReadingFields(null, categoryId, false)
        result.then(fields => {
          this.$set(this, 'taskReadingFields', fields)
        })
      } else if (this.selectedTask.resource) {
        let parentCategoryId = -1
        if (this.selectedTask.resource.category) {
          parentCategoryId = this.selectedTask.resource.category.id
        }
        let resourceId = this.$getProperty(this, 'selectedTask.resource.id')
        let result = util.loadSpaceReadingFields(
          resourceId,
          false,
          parentCategoryId,
          null
        )
        result.then(fields => {
          this.$set(this, 'taskReadingFields', fields)
        })
      }
    },
    showTaskDetails(task) {
      this.showAddTask = true
      this.selectedTask = task
    },
    showPreRequestDetails(preRequest) {
      this.showAddPreRequest = true
      this.selectedPreRequest = preRequest
    },
    focusCommentBox() {
      this.$refs.commentBoxRef.focus()
      this.commentFocus = true
    },
    blurCommentBox(e) {
      let itTargetPar = e.path.filter(ele => {
        if (ele.id === 'commentBoxPar') {
          return true
        }
      })
      if (
        (!this.newComment.body || this.newComment.body.trim() === '') &&
        !itTargetPar.length
      ) {
        this.commentFocus = false
      }
    },
    addComment() {
      if (this.newComment.body) {
        let self = this
        self.$http
          .post('v2/notes/add', {
            note: self.newComment,
            module: 'ticketnotes',
            parentModuleName: 'workorder',
          })
          .then(function(response) {
            if (response.data && response.data.responseCode === 0) {
              self.comments.push(response.data.result.Notes)
              self.reset()
              self.commentFocus = false
              if (self.$refs['activitiesDiv']) {
                self.$refs['activitiesDiv'].loadActivities()
              }
            }
          })
      }
    },
    loadactivitie() {
      if (this.$refs['activitiesDiv']) {
        this.$refs['activitiesDiv'].loadActivities()
      }
    },
    reset() {
      this.newComment = {
        parentModuleLinkName: 'ticketnotes',
        parentId: this.openWorkorderId,
        body: null,
        notifyRequester: false,
        selectedPortalApp: this.selectedPortalApp,
      }
    },
    loadComments() {
      let self = this
      self.loading = true
      return this.$http
        .get(
          'v2/notes/parent/noteList?module=ticketnotes&parentId=' +
            this.openWorkorderId
        )
        .then(function(response) {
          if (response.data && response.data.responseCode === 0) {
            self.loading = false
            self.comments = response.data.result.notes
              ? response.data.result.notes
              : []
          }
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.comments = []
          }
        })
    },
    sort(arr) {
      let len = arr.length
      for (let i = len - 1; i >= 0; i--) {
        for (let j = 1; j <= i; j++) {
          if (arr[j - 1].seq > arr[j].seq) {
            let temp = arr[j - 1]
            arr[j - 1] = arr[j]
            arr[j] = temp
          }
        }
      }
      return arr
    },
    loadTasks() {
      let self = this
      self.loading = true
      if (this.openWorkorderId > 0) {
        return this.$http
          .get('/v2/tasks/parent/' + this.openWorkorderId)
          .then(function(response) {
            if (response.data && response.data.responseCode === 0) {
              self.loading = false
              if (
                response.data.result.tasks[
                  Object.keys(response.data.result.tasks)[0]
                ][0].sequence !== -1
              ) {
                let unsorted = []
                Object.keys(response.data.result.tasks).forEach(key => {
                  unsorted.push({
                    secId: key,
                    seq: response.data.result.tasks[key][0].sequence,
                  })
                })
                let sorted = self.sort(unsorted)
                let newTaskList = []
                sorted.forEach(ele => {
                  newTaskList.push({
                    sectionId: ele.secId,
                    taskList: response.data.result.tasks[ele.secId],
                  })
                })
                newTaskList = newTaskList.map(obj => {
                  let tasks = obj.taskList.map(task => {
                    if (!task.status || task.status.id === self.openStatusId) {
                      task.status = {
                        type: 'OPEN',
                        typeCode: 1,
                        id: self.openStatusId,
                      }
                    } else {
                      task.status = {
                        type: 'CLOSED',
                        typeCode: 2,
                        id: self.closedStatusId,
                      }
                    }
                    if (task.inputTime === -1) {
                      task.inputTime = null
                    }
                    self.$set(task, 'hours', null)
                    self.$set(task, 'minutes', null)
                    return task
                  })
                  obj.taskList = tasks
                  return obj
                })
                self.newTaskList = newTaskList
                self.sequencedData = true
              } else {
                self.sequencedData = false
              }
              self.taskList = response.data.result.tasks
                ? response.data.result.tasks
                : {}
              for (let idx in self.taskList) {
                for (let idx2 in self.taskList[idx]) {
                  if (
                    !self.taskList[idx][idx2].status ||
                    self.taskList[idx][idx2].status.id === self.openStatusId
                  ) {
                    self.taskList[idx][idx2].status = {
                      type: 'OPEN',
                      typeCode: 1,
                      id: self.openStatusId,
                    }
                  } else {
                    self.taskList[idx][idx2].status = {
                      type: 'CLOSED',
                      typeCode: 2,
                      id: self.closedStatusId,
                    }
                  }
                  if (self.taskList[idx][idx2].inputTime === -1) {
                    self.taskList[idx][idx2].inputTime = null
                  }
                  let task = self.taskList[idx][idx2]
                  if (
                    task &&
                    task.readingField &&
                    (task.readingField.dataTypeEnum === 'NUMBER' ||
                      task.readingField.dataTypeEnum === 'DECIMAL') &&
                    task.readingField.metric === 5
                  ) {
                    if (task.inputValue) {
                      let hourMinObj = self.$helpers.secToHoursMinu(
                        task.inputValue
                      )
                      self.$set(
                        task,
                        'hours',
                        hourMinObj.hours ? hourMinObj.hours : ''
                      )
                      self.$set(
                        task,
                        'minutes',
                        hourMinObj.minute ? hourMinObj.minute : ''
                      )
                    }
                  }

                  let { id } = self.selectedTask || -1
                  // check to update the readingField property so that task details of the newly added task shows up, properly on UI.
                  if (id === task.id) {
                    self.selectedTask = task
                  }
                }
              }
              self.sections = response.data.result.sections
                ? response.data.result.sections
                : {}
              // prerequisite start
              self.preRequestList = response.data.result.prerequisites
                ? response.data.result.prerequisites
                : {}
              for (let idx in self.preRequestList) {
                for (let idx2 in self.preRequestList[idx]) {
                  self.preRequestList[idx][idx2].inputValue =
                    self.preRequestList[idx][idx2].inputValue === '1'
                      ? self.preRequestList[idx][idx2].truevalue
                      : self.preRequestList[idx][idx2].inputValue === '0'
                      ? self.preRequestList[idx][idx2].falsevalue
                      : null
                  if (
                    !self.preRequestList[idx][idx2].status ||
                    self.preRequestList[idx][idx2].status.id ===
                      self.openStatusId
                  ) {
                    self.preRequestList[idx][idx2].status = {
                      type: 'OPEN',
                      typeCode: 1,
                      id: self.openStatusId,
                    }
                  } else {
                    self.preRequestList[idx][idx2].status = {
                      type: 'CLOSED',
                      typeCode: 2,
                      id: self.closedStatusId,
                    }
                  }
                  if (self.preRequestList[idx][idx2].inputTime === -1) {
                    self.preRequestList[idx][idx2].inputTime = null
                  }
                }
              }
              self.preRequestSections = response.data.result
                .prerequisiteSections
                ? response.data.result.prerequisiteSections
                : {}
            }
          })
          .catch(function(error) {
            if (error) {
              self.loading = false
              self.tasks = []
            }
          })
      }
    },
    // Temp...needs to be sorted in server itself
    getSortedTaskList() {
      let sorted = []
      if (this.taskList[-1]) {
        let list = this.taskList[-1]
        if (this.selectedTaskResource > 0) {
          list = list.filter(
            task =>
              task.resource && task.resource.id === this.selectedTaskResource
          )
        }
        sorted.push({ sectionId: -1, taskList: list })
      }
      let { sections } = this
      if (!isEmpty(sections)) {
        let seqList = Object.keys(sections).map(secId => ({
          secId,
          sequence: this.getSection(secId),
        }))
        sorted.push(
          ...seqList
            .sort((a, b) => a.sequence - b.sequence)
            .map(obj => ({
              sectionId: obj.secId,
              taskList:
                this.selectedTaskResource > 0
                  ? this.taskList[obj.secId].filter(
                      task =>
                        task.resource &&
                        task.resource.id === this.selectedTaskResource
                    )
                  : this.taskList[obj.secId],
            }))
        )
      }
      return sorted.filter(obj => !isEmpty(obj.taskList))
    },
    getSection(secId) {
      let { taskList } = this
      let { [secId]: section } = taskList || {}
      let task = !isEmpty(section) ? section[0] : {}
      return this.$getProperty(task, 'sequence', -1)
    },
    closedisplay() {
      this.showAddTask = false
    },
    closedisplayPreRequest() {
      this.showAddPreRequest = false
    },
    ticketstatusvalue(id) {
      if (id !== null) {
        let status = this.$store.getters.getTicketStatus(id, 'workorder').status
        if (status) {
          return status
        }
      }
      return ''
    },
    assignWorkOrder(assignedTo, assignmentGroup) {
      let assignObj = {}
      assignObj.id = [this.workorder.id]
      if (assignedTo) {
        assignObj.assignedTo = assignedTo
      }
      if (assignmentGroup) {
        assignObj.assignmentGroup = assignmentGroup
      }
      assignObj.status = {
        id: this.getTicketStatusByLabel('Assigned', 'workorder').id,
        status: 'Assigned',
      }

      this.$store.dispatch('workorder/assignWorkOrder', assignObj).then(() => {
        this.$dialog.notify(
          this.$t('maintenance._workorder.wo_assigned_success')
        )
        this.loadactivitie()
        this.fetchWo(true)
      })
    },
    updateWorkOrderDueDate(idList, datetime) {
      let updateObj = {
        id: idList,
        fields: {
          dueDate: datetime,
        },
      }
      this.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(() => {
          this.loading = true
          this.$message.success(
            this.$t('maintenance._workorder.wo_duedate_update_success')
          )
          this.loading = false
          this.fetchWo(true)
        })
        .catch(() => {
          this.$message.error('Workorder Due Date updation failed')
        })
    },
    updateWorkOrder(idList, field, actualTimings) {
      let updateObj = {
        id: idList,
        fields: field,
        actualTimings,
      }
      this.workorder.loadTimer = false
      this.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(() => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_update_success')
          )
          this.fetchWo(true)
          this.loadactivitie()
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_update_failed')
          )
        })
    },
    resolveWorkOrder(idList, actualWorkDuration, actualTimings) {
      if (!this.checkInputTasksCompletion()) {
        return
      }
      let updateObj = {
        id: idList,
      }
      if (actualWorkDuration > 0) {
        updateObj.actualWorkDuration = actualWorkDuration
        updateObj.actualTimings = actualTimings
      }
      this.$store
        .dispatch('workorder/resolve', updateObj)
        .then(() => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_resolved_success')
          )
          this.loading = false
          this.fetchWo(true)
          this.loadactivitie()
          this.updateGAEvent('Resolved', 'Resolve btn')
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_resolved_failed')
          )
        })
    },
    updateGAEvent(action, label) {
      try {
        Vue.prototype.$gtag &&
          Vue.prototype.$gtag.event('custom_event', {
            event_category: 'Workorder',
            event_action: action,
            event_label: label,
            event_value: 1,
          })

        console.log('sent event update status')
      } catch (e) {
        console.log('failed to send event' + e)
      }
    },
    formatTime(date, period, enableFormating) {
      if (date === null) {
        date = 0
      }
      if (date > 0) {
        let dateFormat = period || 'DD-MM-YYY'
        let today = moment()
          .tz(this.$timezone)
          .startOf('day')
          .valueOf()
        let yesterday = moment()
          .tz(this.$timezone)
          .subtract(1, 'day')
          .startOf('day')
          .valueOf()
        if (date > yesterday && enableFormating && date <= today) {
          return 'YESTERDAY'
        } else if (date > today && enableFormating) {
          return 'TODAY'
        } else {
          return moment(date)
            .tz(this.$timezone)
            .format(dateFormat)
        }
      } else {
        return ''
      }
    },
    closeWO(status) {
      if (!this.checkInputTasksCompletion()) {
        return
      }
      if (
        this.workorder.isWorkDurationChangeAllowed &&
        this.ticketStatusValue !== 'Resolved'
      ) {
        this.workDurationCallBack = this.closeWorkOrder.bind(this)
        this.showWorkDurationDialog = true
        return
      } else if (status) {
        let statusField = {
          id: this.getTicketStatusByLabel(status, 'workorder').id,
          status: status,
        }
        this.$set(this, 'workorder.status', statusField)
      }
      this.closeWorkOrder()
    },
    closeWorkOrder(actualDuration) {
      let paramObj = {
        id: [this.workorder.id],
      }
      if (actualDuration) {
        if (actualDuration.duration !== -1) {
          paramObj.actualWorkDuration = actualDuration.duration
        }
      }
      this.$store
        .dispatch('workorder/closeWorkOrder', paramObj)
        .then(() => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_closed_success')
          )
          this.closeSummary()
          this.updateGAEvent('Closed', 'close btn')
          this.fetchWo(true)
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_close_error_failed')
          )
        })
    },
    checkInputTasksCompletion() {
      for (let idx in this.taskList) {
        if (this.taskList.hasOwnProperty(idx)) {
          let check = this.taskList[idx].some(
            task => task.status.type === 'OPEN'
          )
          if (check) {
            this.$message({
              message: this.$t('maintenance._workorder.wo_resolved_failed'),
              type: 'warning',
            })
            return false
          }
        }
      }
      return true
    },
    closeSummary() {
      this.summoryview = false
      let newpath = this.$route.path.substring(
        0,
        this.$route.path.indexOf('/summary/')
      )
      this.$router.replace({ path: newpath })
    },
    download() {
      this.downloadSummary = true
      window.open(
        window.location.protocol +
          '//' +
          window.location.host +
          '/app/pdf/summarydownloadreport?workorderId=' +
          this.openWorkorderId
      )
    },
    updateTaskChartData() {
      if (this.workorder) {
        this.taskChartData = {
          value: this.workorder.noOfTasks > -1 ? this.workorder.noOfTasks : 0,
          currentValue:
            this.workorder.noOfClosedTasks > -1
              ? this.workorder.noOfClosedTasks
              : 0,
          color: '#9c5fb8,#f87a60',
          unit: '',
          centerText: [
            {
              label: this.$t('maintenance._workorder.completed'),
            },
            {
              label:
                this.$t('maintenance._workorder.of') +
                ' ' +
                (this.workorder.noOfTasks > -1 ? this.workorder.noOfTasks : 0) +
                ' ' +
                this.$t('maintenance._workorder.tasks'),
            },
          ],
        }
      }
    },
    selectItem(item, index) {
      this.selectedInventory = item
      this.selectedInventory.index = index
      this.loadindividualTrackingList([item])
    },
    handeler1(item, index) {
      if (item.addedQuantity === 0) {
        this.inventoryList[index].checked = true
      }
    },
    addindIvidualTrackingItem() {
      this.inventoryList[
        this.inventoryList.findIndex(rt => rt.id === this.selectedInventory.id)
      ].invidualList = [
        ...this.individualItemListwrapper.filter(rt => rt.checked),
        ...this.selectedInventory.invidualList,
      ]
      this.selectedInventory = null
      this.individualItemList = []
    },
    selectTool(tool, index) {
      this.selectedTool = tool
      this.selectedTool.index = index
      this.loadindividualToolTrackingList([tool])
    },
    addindIvidualTrackingTool() {
      this.toolsList[
        this.toolsList.findIndex(rt => rt.id === this.selectedTool.id)
      ].invidualList = [
        ...this.individualToolListwrapper.filter(rt => rt.checked),
        ...this.selectedTool.invidualList,
      ]
      this.selectedTool = null
      this.individualToolList = []
    },
    closeTaskRemarksDialog() {
      this.addTaskRemarksDialog = false
    },
    openWoEditForm() {
      this.editFormVisibilty = true
    },
    isLookupSimpleField(field) {
      let { displayType } = field || {}
      return displayType === 'LOOKUP_SIMPLE'
    },
    isLookupLocationField(field) {
      let { displayType } = field || {}
      return displayType === 'GEO_LOCATION'
    },
  },
}
