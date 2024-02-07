<script>
import { isEmpty } from '@facilio/utils/validation'
import CardHelpers from 'pages/card-builder/card-helpers'
import { dateOperators } from 'pages/card-builder/card-constants'
import { getBaseURL } from 'util/baseUrl'
import { API } from '@facilio/api'

export default {
  props: {
    widget: Object,
    componentKey: [Number, String],
    cardData: Object,
    cardState: Object,
    cardParams: Object,
    cardDrilldown: [Object],
    dbTimelineFilter: Object,
    dbCustomScriptFilter: Object,
    conditionalFormatting: [Array],
    isLoading: Boolean,
    triggerAction: {
      type: Function,
      default: function() {},
    },
    customScriptId: {
      type: Number,
      default: null,
    },
    scriptModeInt: {
      type: Number,
      default: null,
    },
  },
  mixins: [CardHelpers],
  data() {
    return {
      now: null,
      intervalIdForTimeStamp: null,
      activeTab: 'Config',
      report: null,
    }
  },
  computed: {
    cardStyle() {
      let { cardState } = this
      if (isEmpty(cardState)) return {}

      let { styles } = cardState
      return isEmpty(styles) ? {} : styles
    },
    cardPeriod() {
      let { cardData } = this
      if (isEmpty(cardData)) return null

      let { period } = cardData
      if (period) {
        let operator = dateOperators.find(opr => opr.value === period)
        return operator ? operator.label : period
      } else return null
    },
    variables() {
      let { variables } = this.cardData
      if (variables) {
        return variables
      }
      return []
    },
    subscriptionParams() {
      // Implement in components for automatically refreshing card when
      // its readings change
      return {}
    },
    isDashboardEdit() {
      let createQuery = this.$getProperty(this, '$route.query.create', null)
      if (['edit', 'new'].includes(createQuery)) {
        return true
      } else {
        return false
      }
    },
  },
  mounted() {
    this.createTimeStampManager()
    this.setReadingObj()
  },
  destroyed() {
    this.destroyTimeStampManager()
  },
  methods: {
    setReadingObj() {
      let data =
        this.cardDrilldown &&
        this.cardDrilldown.default &&
        this.cardDrilldown.default.data
          ? this.cardDrilldown.default.data
          : null
      if (data && data.reportId) {
        this.loadReport(data.reportId)
      }
    },
    async loadReport(reportId) {
      let resp = await API.put(`v3/report/execute`, {
        reportId: reportId,
      })
      let result = resp.data
      this.report = result.report
    },
    createTimeStampManager() {
      this.now = Date.now()

      // [Internal] Call this fn every 60 seconds
      this.intervalIdForTimeStamp = setTimeout(() => {
        this.createTimeStampManager()
      }, 1 * 60 * 1000)
    },
    destroyTimeStampManager() {
      clearInterval(this.intervalIdForTimeStamp)
    },
    getCardDrillDown() {
      return this.cardDrilldown
    },
    getAdditionalStyle(cardStyle) {
      let styles = {}
      if (cardStyle.hasOwnProperty('blink') && cardStyle.blink) {
        this.$set(styles, 'animation', 'blinker 1s linear infinite')
      }
      return styles
    },
    getPreviewUrl(fileId) {
      return getBaseURL() + `/v2/files/preview/` + fileId
    },
  },
}
</script>
