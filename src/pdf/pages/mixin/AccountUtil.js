import Vue from 'vue'
import Spinner from '@/Spinner'
import http from 'src/util/http'
import helpers from 'src/util/helpers'
// import * as filters from '../util/filters'
import moment from 'moment-timezone'
import constants from 'util/constant'
import util from 'src/util/util'
import style from 'src/util/style'
import common from 'src/util/common'
import delve from 'dlv'
import dset from 'dset'
import convert from 'src/util/convert'
import time from 'src/util/time'
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
} from 'src/util/field-utils'
import { htmlToText } from '@facilio/utils/filters'
import { mapState } from 'vuex'
import{ formatPeriod } from 'src/util/filters'
import Util from 'util/index'

export default {
  data() {
    return {
      loading: false,
      outerData: null,
      localloading: true,
    }
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
  },
  methods: {
    async loadQData() {
      await this.$store.dispatch('getCurrentAccount')
      await this.$store.dispatch('getFeatureLicenses')
      this.outerData = this.$route.query

      let { account } = this
      let { org } = account

      if (org?.timezone) {
        let { timezone } = org
        Vue.prototype.$timezone = timezone
        moment.getDateInOrg = date => {
          return moment.tz(moment(date).format('YYYY-MM-DD HH:mm:ss'), timezone)
        }
      }

      if (account) {
        Vue.prototype.$account = account
        Vue.prototype.$org = org
      }
      if (org?.currency) {
        Vue.prototype.$currency = org.currency
      }
      if (org?.dateformat) {
        Vue.prototype.$dateformat = org.dateformat || 'DD-MMM-YYYY'
      }
      if (org?.timeformat) {
        Vue.prototype.$timeformat = org.timeformat === 2 ? 'hh:mm A' : 'HH:mm'
      }
      else{
        Vue.prototype.$timeformat= 'HH:mm'
      }
      Vue.prototype.$mobile = false

      Vue.filter('htmlToText', htmlToText)
      Vue.filter('formatPeriod',formatPeriod)

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
      Vue.use(Util, {account,})
      this.localloading = false
    },
  },
}
