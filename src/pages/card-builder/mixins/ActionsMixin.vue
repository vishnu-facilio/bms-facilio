<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

const ACTION_TYPES = {
  FN: 'function',
  CONTROL: 'controlAction',
  LINK: 'hyperLink',
  ANALYTIC: 'showTrend',
  LISTVIEW: 'showListView',
  REPORT: 'showReport',
  NONE: 'none',
}

const CONTROL_TYPES = {
  POINT: 'point',
  GROUP: 'group',
}

const LINK_TYPES = {
  URL: 'url',
}

const LINK_TARGET_TYPES = {
  SELF: 'self',
  BLANK: '_blank',
  POPUP: 'popup',
}

const LIST_TARGET_TYPES = {
  SELF: 'self',
  BLANK: '_blank',
  POPUP: 'popup',
}
const REPORT_TARGET_TYPES = {
  SELF: 'self',
  BLANK: '_blank',
  POPUP: 'popup',
}

const ACTION_CONFIG = {
  // [ACTION_TYPES.FN]: {
  //   actionName: 'Execute Function',
  //   actionType: ACTION_TYPES.FN,
  //   data: {},
  // },
  [ACTION_TYPES.CONTROL]: {
    actionName: 'Control Action',
    actionType: ACTION_TYPES.CONTROL,
    data: {
      buttonLabel: 'Set',
      controlType: CONTROL_TYPES.POINT,
      controlPointId: null,
      controlGroupId: null,
      label: null,
      assetId: null,
      fieldId: null,
    },
  },
  [ACTION_TYPES.LINK]: {
    actionName: 'Link To',
    actionType: ACTION_TYPES.LINK,
    data: {
      linkType: LINK_TYPES.URL,
      target: LINK_TARGET_TYPES.POPUP,
      url: null,
    },
  },
  [ACTION_TYPES.ANALYTIC]: {
    actionName: 'Show Trend',
    actionType: ACTION_TYPES.ANALYTIC,
    data: {},
  },
  [ACTION_TYPES.LISTVIEW]: {
    actionName: 'Show Underlying List',
    actionType: ACTION_TYPES.LISTVIEW,
    data: {
      target: LIST_TARGET_TYPES.POPUP,
      view: 'all',
    },
  },
  [ACTION_TYPES.REPORT]: {
    actionName: 'Show Report',
    actionType: ACTION_TYPES.REPORT,
    data: {
      target: REPORT_TARGET_TYPES.POPUP,
      reportId: null,
      // report: null,
      type: 1,
      moduleName: null,
      dashboardFilters: false,
    },
  },
  [ACTION_TYPES.NONE]: {
    actionName: 'None',
    actionType: ACTION_TYPES.NONE,
    data: {},
  },
}
export default {
  props: [
    'value',
    'defaultType',
    'elements',
    'definedActionTypes',
    'variables',
    'moduleName',
  ],

  data() {
    return {
      ACTION_TYPES,
      ACTION_CONFIG,
      CONTROL_TYPES,
      LINK_TYPES,
      LINK_TARGET_TYPES,
      selectedActionType: this.defaultType || ACTION_TYPES.CONTROL,
      actionData: {},
      controlPoints: [],
      controlGroups: [],
      reportFolders: [],
      Kpiviews: [],
    }
  },
  created() {
    if (!isEmpty(this.value)) {
      this.init()
    }
  },
  computed: {
    FILTERED_CONFIG() {
      if (this.definedActionTypes && this.definedActionTypes.length) {
        let config = {}
        this.definedActionTypes.forEach(rt => {
          this.$set(config, rt, ACTION_CONFIG[rt])
        })
        return config
      } else {
        return null
      }
    },
  },
  methods: {
    init() {
      let { value: actions, elements } = this
      let length = (elements || []).length

      if (length === 0) {
        length = 1
        elements = ['default']
      }

      for (let index = 0; index < length; index++) {
        let element = elements[index].name
        let action = actions[element] || {}
        let params = {}

        if (!isEmpty(action)) {
          params = ACTION_CONFIG[action.actionType]
          this.loadRequiredData(action.actionType, action)
          this.loadpreReportdata(action.actionType, action)
        }

        this.$set(this.actionData, element, {
          ...params,
          ...action,
        })
      }
    },
    onActionTypeChange(element, type) {
      if (isEmpty(element)) return

      let { ACTION_CONFIG } = this
      let params = this.$helpers.cloneObject(ACTION_CONFIG[type])

      this.$set(this.actionData, element, {
        ...this.actionData[element],
        ...params,
      })
      this.loadRequiredData(type)
    },

    getElement(name) {
      return this.elements.find(el => el.name === name)
    },
    loadpreReportdata(actionType, action) {
      if (actionType === ACTION_TYPES.REPORT && action.data.moduleName) {
        let { type, moduleName } = action.data
        if (type === 1) {
          this.loadReports(moduleName)
        } else if (type === 2) {
          this.chooseReport(type)
        }
      }
    },
    getlistview() {
      if (this.moduleName) {
        let url = `/v2/views/viewList?moduleName=${this.moduleName}`
        API.get(url).then(({ data }) => {
          this.Kpiviews = data.groupViews || []
        })
      }
    },
    loadRequiredData(actionType, action) {
      if (
        !isEmpty(action) &&
        !isEmpty(action.data) &&
        !isEmpty(action.data.assetId)
      ) {
        this.loadAssetControlPoints(action.data.assetId)
        this.loadControlGroups()
      } else if (actionType === ACTION_TYPES.CONTROL) this.loadControlPoints()
      else if (actionType === ACTION_TYPES.REPORT) {
        // this.loadReports()
      }
    },

    loadControlPoints() {
      this.controlPoints = []
      this.$http
        .get('/v2/controlAction/getControllablePoints')
        .then(response => {
          if (response.data.result && response.data.result.controllablePoints) {
            this.controlPoints = response.data.result.controllablePoints
          }
        })
        .catch(() => {})

      this.controlGroups = []
      this.$http
        .get('/v2/controlAction/getControlGroups')
        .then(response => {
          if (
            response.data.result &&
            response.data.result.controlActionGroups
          ) {
            this.controlGroups = response.data.result.controlActionGroups
          }
        })
        .catch(() => {})
    },
    loadControlGroups() {
      this.controlGroups = []
      this.$http
        .get('/v2/controlAction/getControlGroups')
        .then(response => {
          if (
            response.data.result &&
            response.data.result.controlActionGroups
          ) {
            this.controlGroups = response.data.result.controlActionGroups
          }
        })
        .catch(() => {})
    },
    setControlPoint(data) {
      let cp = this.controlPoints.find(c => c.id === data.controlPointId)
      if (cp) {
        data.assetId = cp.resourceId
        data.fieldId = cp.fieldId
        data.actionName =
          cp.resourceContext.name + ' (' + cp.field.displayName + ')'
      }
    },
    setControlGroup(data) {
      let cg = this.controlGroups.find(c => c.id === data.groupId)
      if (cg) {
        data.actionName = cg.name
      }
    },
    associateControlPointAsset(data, resource, resourceType) {
      data.label = resource.name
      data.assetId = resource.id
      data.controlPointId = null

      this.loadAssetControlPoints(resource.id)
      this.showControlPointAssetPicker = false
    },
    fetchControlPoints(action) {
      let assetId = action?.data?.assetId ? action.data.assetId : null
      let filters = {}
      if (assetId) {
        this.$set(filters, 'resourceId', {
          operatorId: 9,
          value: [assetId + ''],
        })
      }
      // need to handle the querry data here.
      // if (querry) {
      //   this.$set(filters, 'resourceId', { operatorId: 9, value: [assetId + ''] })
      // }
      this.controlPoints = []
      this.$http
        .get(
          '/v2/controlAction/getControllablePoints?page=1&perPage=200&filters=' +
            encodeURIComponent(JSON.stringify(filters))
        )
        .then(response => {
          if (response.data.result && response.data.result.controllablePoints) {
            this.controlPoints = response.data.result.controllablePoints
          }
        })
    },
    loadAssetControlPoints(assetId) {
      if (!assetId) {
        return
      }
      let filters = {
        resourceId: { operatorId: 9, value: [assetId + ''] },
      }
      this.controlPoints = []
      this.$http
        .get(
          '/v2/controlAction/getControllablePoints?filters=' +
            encodeURIComponent(JSON.stringify(filters))
        )
        .then(response => {
          if (response.data.result && response.data.result.controllablePoints) {
            this.controlPoints = response.data.result.controllablePoints
          }
        })
    },
    chooseReport(type) {
      this.reportFolders = []
      if (type === 2) {
        this.loadReports('energydata')
      }
    },

    async loadReports(moduleName) {
      if (moduleName === 'custommodule') {
        let { data, error } = await API.get(
          `v3/report/folders?moduleName=custommodule`
        )
        if (!error) {
          this.reportFolders = data.reportFolders
        }
      } else {
        let { data, error } = await API.get(
          `v3/report/folders?moduleName=${moduleName}`
        )
        if (!error) {
          this.reportFolders = data.reportFolders
        }
      }
    },
    getReportObj(reportId) {
      // let { data } = this.actionData.default
      // this.reportFolders.forEach(folder => {
      //   folder.reports.forEach(report => {
      //     if (report.id === data.reportId) {
      //       data.report = report
      //     }
      //   })
      // })
    },
  },
}
</script>
