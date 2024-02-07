import Attachments from '@/relatedlist/SummaryAttachment'
import Activities from '@/relatedlist/Activities2'
import Steps from '@/relatedlist/Steps'
import Comments from '@/relatedlist/Comments2'
import { mapState, mapGetters } from 'vuex'
import Avatar from '@/Avatar'
import UserAvatar from '@/avatar/User'
import FSelect from '@/fields/FSelect'
import Subheader from '@/Subheader'
import AssetAvatar from '@/avatar/Asset'
import FDialog from '@/FDialogNew'
import TaskSpaceAssetChooser from '@/SpaceAssetChooser'
import MobileAttachment from '@/MobileAttachment2'
import FAssignment from '@/FAssignment'
import WorkHours from 'pages/workorder/widgets/dialogs/WorkHours'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import WorkorderSummaryMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import clone from 'lodash/clone'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  Toast,
  Ripple,
  QBtn,
  QIcon,
  QModal,
  QModalLayout,
  QToolbar,
  QToolbarTitle,
  QSearch,
  QList,
  QItem,
  QItemSide,
  QItemMain,
  QSelect,
  QField,
  QDatetime,
  QItemSeparator,
  QChip,
  QTabs,
  QTab,
  QOptionGroup,
  QPopover,
  Alert,
} from 'quasar'
import { getFieldOptions } from 'util/picklist'
// import { Promise } from 'q'

export default {
  props: [],
  mixins: [workorderMixin, WorkorderSummaryMixin],
  data() {
    return {
      reportLoading: true,
      pm: null,
      tasksList: '',
      notesList: '',
      workordersList: '',
      woObject: '',
      sequencedData: false,
      comments: [],
      pmMap: {},
      preRequestList: {},
      preRequestSections: {},
      taskList: {},
      tl: {},
      newTaskList: [],
      taskL: [],
      sec: {},
      sections: {},
      workorder: null,
      newTask: {
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
        inputValidation: null,
        minSafeLimit: null,
        maxSafeLimit: null,
        id: -1,
      },
      building: null,
      showAddTask: false,
      selectedTask: {
        inputValidationRuleId: -1,
        inputValidation: null,
        safeLimitRuleId: -1,
        minSafeLimit: null,
        maxSafeLimit: null,
        options: [],
        taskType: -1,
        previousExecution: null,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        subject: '',
        selectedResourceName: '',
        inputValues: [],
      },
      taskAttachments: {},
      woAttachments: {},
      history: {},
      siteNameCollection: {},
      logoCollection: {},
      buildingsList: [],
      metricWithUnits: {},
    }
  },
  async mounted() {
    await this.loadReadingFieldUnits()
    await this.loadReport()
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
    this.isEmpty = isEmpty
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
      ticketstatus: state => state.ticketStatus.workorder,
    }),
    ...mapGetters([
      'getTicketTypePickList',
      'getTicketPriority',
      'getTicketCategory',
    ]),
    tickettype() {
      return this.getTicketTypePickList()
    },
    openStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel(
        'Submitted',
        'workorder'
      ).id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    workorderObject() {
      if (this.workordersList) {
        for (let i = 0; i < this.workordersList.length; i++) {
          if (this.tasksList && this.tasksList[this.workordersList[i].id]) {
            this.workordersList[i].taskLists = this.tasksList[
              this.workordersList[i].id
            ]
            if (
              this.getTaskAndSectionLists(
                this.tasksList[this.workordersList[i].id]
              )
            ) {
              this.workordersList[i].sec = this.sec
              this.workordersList[i].taskL = this.taskL
            }
          }

          if (this.notesList && this.notesList[this.workordersList[i].id]) {
            this.workordersList[i].notesLists = this.notesList[
              this.workordersList[i].id
            ]
          }
          if (
            this.workordersList[i].pm &&
            this.workordersList[i].pm.id &&
            this.pmMap[this.workordersList[i].pm.id] &&
            this.pmMap[this.workordersList[i].pm.id].workorders &&
            this.pmMap[this.workordersList[i].pm.id].workorders.length > 1
          ) {
            this.workordersList[
              i
            ].previousExecution = this.toGetPreviousExecutedWo(
              this.pmMap[this.workordersList[i].pm.id].workorders,
              this.workordersList[i].createdTime,
              this.workordersList[i]
            )
          }
          if (
            this.workordersList[i].trigger &&
            this.workordersList[i].trigger.id > 0 &&
            this.workordersList[i].pm &&
            this.workordersList[i].pm.id &&
            this.pmMap[this.workordersList[i].pm.id]
          ) {
            this.workordersList[i].triggerEnum = this.pmMap[
              this.workordersList[i].pm.id
            ].triggers.find(
              trig => trig.id === this.workordersList[i].trigger.id
            ).scheduleMsg
          } else {
            this.workordersList[i].previousExecution = 0
          }
        }
        return this.workordersList
      }
      return ''
    },

    closedStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel('Closed', 'workorder')
        .id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    workorderId() {
      return this.$route.query.workorderId
    },
    ticketStatusValue() {
      if (this.workorder && this.workorder.status && this.workorder.status.id) {
        return (
          this.$store.getters.getTicketStatus(
            this.workorder.status.id,
            'workorder'
          ) || ''
        )
      }
      return ''
    },
    showImage() {
      return (
        this.$route.query.showImage === true ||
        this.$route.query.showImage === 'true'
      )
    },
    showAttachements() {
      return (
        this.$route.query.showAttachements === true ||
        this.$route.query.showAttachements === 'true'
      )
    },
    showComments() {
      return (
        this.$route.query.showComments === true ||
        this.$route.query.showComments === 'true'
      )
    },
    showHistory() {
      let checkHistory = this.$getProperty(
        this,
        '$route.query.showHistory',
        null
      )
      return checkHistory === true || checkHistory === 'true'
    },
    showSignature() {
      let toShowSign = this.$getProperty(
        this.$route,
        'query.showSignature',
        null
      )
      return toShowSign === true || toShowSign === 'true'
    },
    useSiteLogo() {
      let toUseSiteLogo = this.$getProperty(
        this.$route,
        'query.useSiteLogo',
        null
      )
      return toUseSiteLogo === true || toUseSiteLogo === 'true'
    },
    getFacilioLogoURL() {
      let { brandConfig } = this
      let logo = this.$getProperty(brandConfig, 'logo', null)
      return logo
    },
    showLogo() {
      let { brandConfig } = this
      let showLogoInPDF = this.$getProperty(brandConfig, 'showLogoInPDF', true)
      return showLogoInPDF
    },
    brandConfig() {
      return window.brandConfig
    },
  },

  methods: {
    async loadBuildings(wolist) {
      let buildingIds = []
      buildingIds = wolist
        .filter(
          wo =>
            (wo.resource?.resourceType === 2 || wo.resource?.spaceType !== 2) &&
            wo.resource?.buildingId > 0
        )
        .map(wo => String(wo.resource.buildingId))
      if (!isEmpty(buildingIds)) {
        let { options } = await getFieldOptions({
          field: { lookupModuleName: 'building', skipDeserialize: true },
          defaultIds: buildingIds,
          perPage: buildingIds.length,
        })
        this.buildingsList = options
      }
    },
    toGetPreviousExecutedWo(wos, currentWoCreatedTime, currentWo) {
      let workOrders = wos.filter(i => i.resource.id === currentWo.resource.id)

      if (workOrders && workOrders.length > 1) {
        let a = []
        workOrders.forEach(d => {
          a.push(d.createdTime)
        })
        let result = 0,
          lastDelta
        a.some(function(item) {
          if (currentWoCreatedTime !== item && currentWoCreatedTime > item) {
            let delta = Math.abs(currentWoCreatedTime - item)
            if (delta > lastDelta) {
              return 0
            }
            result = item
            lastDelta = delta
          }
        })
        return result
      } else {
        return 0
      }
    },
    isPmAvail(wo) {
      let { pmMap } = this
      return (
        !isEmpty(pmMap) &&
        wo?.pm?.id &&
        pmMap[wo.pm.id]?.workorders?.length > 1 &&
        wo?.previousExecution > 0
      )
    },
    isScheduleMsgAvail(wo) {
      let { pmMap } = this
      return !isEmpty(pmMap) && !isEmpty(pmMap[wo.pm?.id]?.scheduleMsg)
    },
    loadTaskAttachments() {
      let promises = []
      if (this.showImage && this.tasksList) {
        for (let taskObj in this.tasksList) {
          for (let sectionId in this.tasksList[taskObj].tasks) {
            this.tasksList[taskObj].tasks[sectionId].forEach(task => {
              if (task?.noOfAttachments > 0) {
                promises.push(this.loadAttachments(task))
              }
            })
          }
        }
      }
      if (promises.length) {
        Promise.all([...promises]).then(() => {
          this.reportLoading = false
          setTimeout(() => {
            this.print()
          }, 2000)
        })
      } else {
        this.reportLoading = false
        setTimeout(() => {
          this.print()
        }, 1000)
      }
    },
    loadAttachments(task) {
      let self = this
      self.loading = true
      return this.$http
        .get('/attachment?module=taskattachments&recordId=' + task.id)
        .then(function(response) {
          if (response.status === 200) {
            self.loading = false
            let attachments = response.data.attachments
              ? response.data.attachments
              : []

            if (attachments.length > 0) {
              if (!self.taskAttachments[task.parentTicketId]) {
                self.taskAttachments[task.parentTicketId] = {
                  attachments: [],
                }
              }
              let taskAttachment = {}
              for (let attachment of attachments) {
                attachment.previewUrl =
                  attachment.previewUrl + `?width=360&height=360`
                if (attachment.type === 1) {
                  taskAttachment.before = attachment.previewUrl
                } else if (attachment.type === 2) {
                  taskAttachment.after = attachment.previewUrl
                }
                taskAttachment.uploadedTime = attachment.uploadedTime
              }
              taskAttachment.subject = task.subject
              self.taskAttachments[task.parentTicketId].attachments.push(
                taskAttachment
              )
            }
          }
        })
    },
    loadWoAttachments(woId) {
      let self = this
      return this.$http
        .get('/attachment?module=ticketattachments&recordId=' + woId)
        .then(function(response) {
          let attachments = response.data.attachments
            ? response.data.attachments
            : []
          if (attachments.length) {
            self.woAttachments[woId] = {
              images: [],
              docs: [],
            }
            attachments.forEach(attachment => {
              if (attachment.contentType.startsWith('image/')) {
                attachment.previewUrl =
                  attachment.previewUrl + `?width=360&height=360`
                self.woAttachments[woId].images.push(attachment)
              } else {
                self.woAttachments[woId].docs.push(attachment)
              }
            })
          }
        })
        .catch(function(error) {
          if (error) {
            self.woAttachments = {}
          }
        })
    },
    updateWoActions() {
      this.loadactivitie()

      if (this.isStateFlowEnabled) {
        this.fetchAvailableStates()
      }
    },
    async loadReport() {
      let self = this
      let workorderIds = this.$route.query.workorderId.split(',')
      let ids = workorderIds.map(String)
      let data = {
        id: ids,
      }
      let queryObj = {
        viewname: 'all',
        page: '1',
        id: data.id,
      }
      let promise2 = self.$http.post('/v2/tasks/parents', {
        id: ids,
      })
      let promise3 = self.$http.post('/v2/notes/parent/noteList', {
        notesIds: ids,
      })

      let promises = []
      if (this.showAttachements) {
        workorderIds.forEach(id => {
          // promises.push()
          this.loadWoAttachments(id)
        })
      }
      if (this.showHistory) {
        workorderIds.forEach(id => {
          this.loadHistory(id)
        })
      }
      let { $route } = self
      let viewName = this.$getProperty($route, 'query.viewName', null)
      let params = {
        moduleName: 'workorder',
        viewName,
        filters: JSON.stringify({ id: { operatorId: 9, value: ids } }),
        page: 1,
        perPage: 50,
        withoutCustomButtons: true,
      }
      let { list } = await API.fetchAll('workorder', params)
      if (isEmpty(list)) {
        this.$message.error('No Workorders to print')
      } else {
        this.workordersList = clone(list)
        let siteIds = []
        list.forEach(wo => siteIds.push(String(wo.siteId)))
        let { list: siteList } = await API.fetchAll('site', {
          page: 1,
          filters: JSON.stringify({ id: { operatorId: 9, value: siteIds } }),
        })
        siteList.forEach(site => {
          let { siteId } = site || {}
          if (siteId) {
            this.logoCollection[siteId] =
              this.useSiteLogo && !isEmpty(site.avatarUrl)
                ? site.avatarUrl
                : this.$org.logoUrl
            this.siteNameCollection[siteId] = !isEmpty(site.name)
              ? site.name
              : '---'
          }
        })
        this.loadBuildings(list)
        let workorders = this.workordersList.filter(b => b.pm && b.pm !== null)
        if (workorders && workorders.length) {
          let pmIDs = new Set()
          for (let i = 0; i < workorders.length; i++) {
            if (workorders[i].pm && workorders[i].pm.id) {
              pmIDs.add(workorders[i].pm.id)
            }
          }
          if (pmIDs.size > 0) {
            pmIDs.forEach(pmId => {
              promises.push(
                this.$http.get(
                  `/workorder/preventiveMaintenanceSummary?id=` + parseInt(pmId)
                )
              )
            })
          }
        }
      }
      if (promises.length > 0) {
        Promise.all([...promises]).then(function(value) {
          value.forEach(d => {
            self.pmMap[d.data.preventivemaintenance.id] =
              d.data.preventivemaintenance
          })

          if (Object.keys(self.pmMap).length) {
            Promise.all([promise3, promise2]).then(function(values) {
              self.notesList = values[0].data.result.notesList
              self.tasksList = values[1].data.result.tasks
              self.setPrerequisites(values[1])
              self.loadTaskAttachments()
            })
          }
        })
      } else {
        Promise.all([promise3, promise2]).then(function(values) {
          self.notesList = values[0].data.result.notesList
          self.tasksList = values[1].data.result.tasks
          self.setPrerequisites(values[1])
          self.loadTaskAttachments()
        })
      }
    },
    setPrerequisites(response) {
      let self = this

      self.preRequestList = response.data.result.preRequests
      if (self.preRequestList) {
        for (let woId in self.preRequestList) {
          let preRequestList = self.preRequestList[woId].tasks
          if (preRequestList) {
            for (let idx in preRequestList) {
              for (let idx2 in preRequestList[idx]) {
                preRequestList[idx][idx2].inputValue =
                  preRequestList[idx][idx2].inputValue === '1'
                    ? preRequestList[idx][idx2].truevalue
                    : preRequestList[idx][idx2].inputValue === '0'
                    ? preRequestList[idx][idx2].falsevalue
                    : null
                if (
                  !preRequestList[idx][idx2].status ||
                  preRequestList[idx][idx2].status.id === self.openStatusId
                ) {
                  preRequestList[idx][idx2].status = {
                    type: 'OPEN',
                    typeCode: 1,
                    id: self.openStatusId,
                  }
                } else {
                  preRequestList[idx][idx2].status = {
                    type: 'CLOSED',
                    typeCode: 2,
                    id: self.closedStatusId,
                  }
                }
                if (preRequestList[idx][idx2].inputTime === -1) {
                  preRequestList[idx][idx2].inputTime = null
                }
              }
            }
          }
        }
      }
    },
    canShowTaskResource(wo) {
      return wo.resource && wo.resource.resourceType === 1
    },

    print() {
      this.$nextTick(() => {
        // this.$emit('printed')
        window.print()
      })
    },
    fetchWo(force) {
      this.$store.dispatch('workorder/fetchWorkOrder', {
        id: this.openWorkorderId,
        force,
      })
    },
    getPMType(type) {
      return this.tickettype[type]
    },
    checkOptionsLength(options) {
      return options.length <= 2 && !options.some(option => option.length > 10)
    },
    workorderstatus(id) {
      let priority = this.getTicketPriority(id).displayName
      if (priority) {
        return priority
      } else {
        return ''
      }
    },
    ticketstatusvalue(id) {
      if (id !== null) {
        let status = this.$store.getters.getTicketStatus(id, 'workorder').status
        if (status) {
          return status
        } else {
          return ''
        }
      }
      return ''
    },
    loadComments() {
      let self = this
      self.loading = true
      if (this.showComments && this.comments) {
        return this.$http
          .get('/note/get?module=ticketnotes&parentId=' + this.workorderId)
          .then(function(response) {
            self.loading = false
            self.comments =
              response.data && response.data.notes ? response.data.notes : []
          })
          .catch(function(error) {
            if (error) {
              self.loading = false
              self.comments = []
            }
          })
      }
    },
    async loadHistory(woId) {
      this.loading = true
      this.modulename = 'workorders'
      if (this.showHistory) {
        let { data } = await this.$http.get(
          '/v2/workorders/activity?workOrderId=' + woId
        )

        let activity = this.$getProperty(data, 'result.activity', null)
        if (!isEmpty(activity)) {
          this.history[woId] = activity
        }
        this.loading = false
      }
    },
    loadFields() {
      let self = this
      self.loading = true
      self.$http
        .get('/module/fields?moduleName=workorder')
        .then(function(response) {
          self.fields = response.data.fields
          self.loading = false
        })
    },
    getTaskAndSectionLists(woTasks) {
      let self = this
      self.tl = {}
      self.taskL = []
      self.sec = {}
      if (woTasks && woTasks.tasks && Object.keys(woTasks.tasks).length > 0) {
        if (woTasks.tasks[Object.keys(woTasks.tasks)[0]][0].sequence !== -1) {
          let unsorted = []
          Object.keys(woTasks.tasks).forEach(key => {
            unsorted.push({
              secId: key,
              seq: woTasks.tasks[key][0].sequence,
            })
          })
          let sorted = self.sort(unsorted)
          let newTaskList = []
          sorted.forEach(ele => {
            newTaskList.push({
              sectionId: ele.secId,
              taskList: woTasks.tasks[ele.secId],
            })
          })
          newTaskList = newTaskList.map((obj, objInx) => {
            let tasks = obj.taskList.map((task, index) => {
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
              return task
            })
            obj.taskList = tasks
            return obj
          })
          // self.newTaskList = newTaskList
          self.taskL = newTaskList
          self.sequencedData = true
        } else {
          self.sequencedData = false
        }
        // self.taskList = woTasks.tasks ? woTasks.tasks : {}
        self.tl = woTasks.tasks ? woTasks.tasks : {}
        for (let idx in self.tl) {
          for (let idx2 in self.tl[idx]) {
            if (
              !self.tl[idx][idx2].status ||
              self.tl[idx][idx2].status.id === self.openStatusId
            ) {
              self.tl[idx][idx2].status = {
                type: 'OPEN',
                typeCode: 1,
                id: self.openStatusId,
              }
            } else {
              self.tl[idx][idx2].status = {
                type: 'CLOSED',
                typeCode: 2,
                id: self.closedStatusId,
              }
            }
            if (self.tl[idx][idx2].inputTime === -1) {
              self.tl[idx][idx2].inputTime = null
            }
          }
        }
        self.sec = woTasks.sections ? woTasks.sections : {}
        return true
      }
      return false
    },
    loadBuilding(workorder) {
      if (
        this.workorder.resource &&
        (this.workorder.resource.resourceType === 2 ||
          this.workorder.resource.spaceType !== 2) &&
        this.workorder.resource.buildingId > 0
      ) {
        let self = this
        self.loading = true
        self.$http
          .get('/building/' + this.workorder.resource.buildingId)
          .then(function(response) {
            self.building = response.data.building
            self.loading = false
          })
      }
    },
    resourceLabel({ pmCreationType, pmResourceLabel }) {
      if (pmCreationType === 1) {
        return pmResourceLabel
      }
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
    async loadReadingFieldUnits() {
      API.get('v2/getReadingFieldUnits').then(response => {
        if (isEmpty(response.error) && response.data) {
          let { readingFieldUnits } = response.data || {}
          let { MetricsWithUnits } = readingFieldUnits || {}
          this.metricWithUnits = MetricsWithUnits
        }
      })
    },
    // Helper function to return the unit (in short form) with unitId 'readingFieldUnit'.
    getReadingFieldUnit(readingFieldMetricId, readingFieldUnit) {
      if (isEmpty(readingFieldMetricId) || isEmpty(readingFieldUnit)) {
        return '--'
      }

      let readingFieldUnitSymbol = ''
      if (!isEmpty(this.metricWithUnits)) {
        let currentMetricId = Object.keys(this.metricWithUnits).find(
          metricUnitKey => metricUnitKey == readingFieldMetricId
        )
        let readingFieldUnits = this.metricWithUnits[currentMetricId]
        if (Array.isArray(readingFieldUnits)) {
          readingFieldUnits.forEach(item => {
            if (item.unitId === readingFieldUnit) {
              readingFieldUnitSymbol = item.symbol
            }
          })
        }
      }
      return readingFieldUnitSymbol
    },
    // Helper function to convert the inputValue (in SI Unit) to the format toUnit_1 : touUnit_2.
    // Eg. inputValue (in seconds) = 3660 to "1 h 1 min", where unit_1 is hours, unit_2 is minutes.
    convertSIUnitValueToUnit1Unit2Format(
      readingFieldMetricId, // metric ID
      convertedToUnitId_1, // unitId required for conversion
      convertedToUnitId_2, // unitId required for conversion
      inputValue // in terms of siUnit
    ) {
      if (
        isEmpty(readingFieldMetricId) ||
        isEmpty(convertedToUnitId_1) ||
        isEmpty(convertedToUnitId_2)
      ) {
        return ''
      }

      let toUnit_1 = {}
      let toUnit_2 = {}

      if (!isEmpty(this.metricWithUnits)) {
        let currentMetricId = Object.keys(this.metricWithUnits).find(
          metricUnitKey => metricUnitKey == readingFieldMetricId
        )
        let readingFieldUnits = this.metricWithUnits[currentMetricId]
        if (Array.isArray(readingFieldUnits)) {
          readingFieldUnits.forEach(item => {
            if (item.unitId === convertedToUnitId_1) {
              toUnit_1 = item
            }
            if (item.unitId === convertedToUnitId_2) {
              toUnit_2 = item
            }
          })
        }

        // conversion starts here
        // get the fromSiUnit, toSiUnit expressions of unit_1
        let unit_1_expression_fromSiUnit = toUnit_1.fromSiUnit
          ? toUnit_1.fromSiUnit
          : ''
        let unit_1_expression_toSiUnit = toUnit_1.toSiUnit
          ? toUnit_1.toSiUnit
          : ''

        // replace the occurrences of 'si' with inputValue
        unit_1_expression_fromSiUnit = unit_1_expression_fromSiUnit.replaceAll(
          'si',
          inputValue
        )
        // evaluate the expression
        let unit_1_value = Math.floor(eval(unit_1_expression_fromSiUnit))

        // replace the occurrences of 'this' with unit_1_value
        unit_1_expression_toSiUnit = unit_1_expression_toSiUnit.replaceAll(
          'this',
          unit_1_value
        )

        // get the fromSiUnit expression of unit_2
        let unit_2_expression = toUnit_2.fromSiUnit ? toUnit_2.fromSiUnit : ''
        // evaluate the expression - subtract value of unit_1_expression_toSiUnit from inputValue
        let unit_2_val = Math.abs(
          inputValue - Math.floor(eval(unit_1_expression_toSiUnit))
        )
        // replace the occurrences of 'si' with unit_2_val
        unit_2_expression = unit_2_expression.replaceAll('si', unit_2_val)
        // evaluate the expression
        let unit_2_value = Math.floor(eval(unit_2_expression))
        return (
          unit_1_value +
          ' ' +
          toUnit_1.symbol +
          ' ' +
          unit_2_value +
          ' ' +
          toUnit_2.symbol
        )
      }
    },
  },
  components: {
    Avatar,
    UserAvatar,
    Attachments,
    Activities,
    Comments,
    Toast,
    Ripple,
    QBtn,
    QIcon,
    QModal,
    QModalLayout,
    QToolbar,
    QToolbarTitle,
    QSearch,
    QList,
    QItem,
    QItemSide,
    QItemMain,
    QSelect,
    QField,
    QDatetime,
    QItemSeparator,
    QChip,
    FSelect,
    QTabs,
    QTab,
    QOptionGroup,
    QPopover,
    Steps,
    Subheader,
    Alert,
    AssetAvatar,
    FDialog,
    TaskSpaceAssetChooser,
    MobileAttachment,
    FAssignment,
    WorkHours,
  },
  filters: {
    getUser: function(id, users) {
      let userObj = users.find(user => user.id === id)
      if (userObj) {
        return userObj
      } else {
        return {
          name: 'System',
        }
      }
    },
    options: function(jsonobj, type) {
      let array = []
      if (type === 'category' || type === 'priority') {
        for (let jsonkey in jsonobj) {
          let val = jsonobj[jsonkey]
          array.push({
            label: val,
            value: parseInt(jsonkey),
          })
        }
      } else if (type === 'user' || type === 'space') {
        for (let key in jsonobj) {
          array.push({
            label: jsonobj[key].name,
            value: jsonobj[key].id,
          })
        }
      }
      return array
    },
  },
}
