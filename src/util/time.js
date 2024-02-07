// this file used to get the time related properties
import moment from 'moment-timezone'
import Vue from 'vue'
export default {
  format(timestamp, formatStr) {
    if (formatStr) {
      return moment(timestamp)
        .tz(Vue.prototype.$timezone)
        .format(formatStr)
    } else {
      return moment(timestamp)
        .tz(Vue.prototype.$timezone)
        .format('DD MMM YYYY hh:mm a')
    }
  },
}
