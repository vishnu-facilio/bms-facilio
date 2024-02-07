import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import Vue from 'vue'

const timeFormatHash = {
  '24Hour': 1,
  '12Hour': 2,
}

export const getReadableTimeFormat = timeValue => {
  let timeFieldValue = timeValue || {}
  let { timeFormat } = Vue.prototype.$org
  let format = timeFormat === timeFormatHash['12Hour'] ? 'h:mm A' : 'HH:mm'
  return moment(parseInt(timeFieldValue)).format(format)
}

export const getFormatedTime = timeFieldValue => {
  let { timeFormat } = Vue.prototype.$org
  let format = timeFormat === timeFormatHash['12Hour'] ? 'h:mm A' : 'HH:mm'

  if (isEmpty(timeFieldValue)) return

  return moment()
    .startOf('day')
    .milliseconds(timeFieldValue)
    .format(format)
}
