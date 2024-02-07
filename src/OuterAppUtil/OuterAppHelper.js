import Vue from 'vue'
import Spinner from '@/Spinner'
import http from '../util/http'
import helpers from '../util/helpers'
import * as filters from '../util/filters'
import moment from 'moment-timezone'
import constants from 'util/constant'
import util from '../util/util'
import style from '../util/style'
import common from '../util/common'
import delve from 'dlv'
import dset from 'dset'
import convert from '../util/convert'
import time from '../util/time'
import { isEmpty, isBoolean } from '@facilio/utils/validation'
import {
  isBooleanField,
  isNumberField,
  isDecimalField,
  isEnumField,
  isDateField,
  isDateTimeField,
  isDateTypeField,
  isLookupField,
  getDisplayValue,
  isLookupDropDownField,
} from '../util/field-utils'
import { htmlToText } from '@facilio/utils/filters'
import { mapState } from 'vuex'
import ConnectedAppEventChannel from 'pages/connectedapps/event-channel/ConnectedAppEventChannel'
export default {
  data() {
    return {
      loading: false,
      isMobilePage: true,
      outerData: null,
      rawdata: null,
      localloading: true,
    }
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
  },
  methods: {
    appInit() {
      // appInit
      let CONNECTED_APP_EVENT_CHANNEL = null
      CONNECTED_APP_EVENT_CHANNEL = new ConnectedAppEventChannel({
        user: this.account.user,
        org: this.account.org,
      })

      Vue.prototype.$connectedAppEventChannel =
        CONNECTED_APP_EVENT_CHANNEL || null
    },
    loadQData() {
      this.outerData = this.$route.query

      if (this.outerData.timezone) {
        Vue.prototype.$timezone = JSON.parse(this.outerData.timezone)
      }

      Vue.prototype.$mobile = this.isMobilePage

      if (this.outerData.account) {
        Vue.prototype.$account = JSON.parse(this.outerData.account)
        Vue.prototype.$org = JSON.parse(this.outerData.account).org
      }
      if (this.outerData.currency) {
        Vue.prototype.$currency = JSON.parse(this.outerData.currency)
      }
      let filterList = filters
      Object.keys(filterList).forEach(key => {
        Vue.filter(key, filterList[key])
      })
      Vue.filter('htmlToText', htmlToText)
      Vue.prototype.$dateformat = this.outerData.dateformat || 'DD-MMM-YYYY'
      Vue.prototype.$timeformat =
        this.outerData.timeFormat === 2 ? 'hh:mm A' : 'HH:mm'

      const hasPermission = () => {
        return true
      }

      Vue.component('spinner', Spinner)
      Vue.prototype.$http = http
      Vue.prototype.$constants = constants
      Vue.prototype.$helpers = helpers
      Vue.prototype.$util = util
      Vue.prototype.$style = style
      Vue.prototype.$common = common
      Vue.prototype.$getProperty = delve
      Vue.prototype.$setProperty = dset
      Vue.prototype.$convert = convert
      Vue.prototype.$time = time
      Vue.prototype.$hasPermission = hasPermission
      ;(Vue.prototype.$dateformat = 'DD-MMM-YYYY'),
        (Vue.prototype.$validation = {
          isEmpty,
          isBoolean,
        })
      Vue.prototype.$fieldUtils = {
        isBooleanField,
        isNumberField,
        isDecimalField,
        isEnumField,
        isDateField,
        isDateTimeField,
        isDateTypeField,
        isLookupField,
        getDisplayValue,
        isLookupDropDownField,
      }
      moment.getDateInOrg = date => {
        return moment.tz(
          moment(date).format('YYYY-MM-DD HH:mm:ss'),
          this.outerData.timezone
        )
      }
      this.loading = true

      Promise.all([
        this.$store.dispatch('getCurrentAccount'),
        this.$store.dispatch('getFeatureLicenses'),
      ]).then(() => {
        this.loading = false
        this.appInit()
      }),
        (this.localloading = false)
    },
  },
}
