import moment from 'moment-timezone'
import Vue from 'vue'

export default function botTime(timeStamp) {
  let currentTime = moment()
    .tz(Vue.prototype.$timezone)
    .valueOf()
  let msPerMinute = 60 * 1000
  let msPer5Minutes = msPerMinute * 5
  let msPerHour = msPerMinute * 60

  let msPerDay = msPerHour * 24
  let msPerWeek = msPerDay * 7

  let elapsed = currentTime - timeStamp

  if (elapsed < msPerMinute) {
    return 'now'
  } else if (elapsed < msPer5Minutes) {
    let minutesAgo = Math.floor(elapsed / msPerMinute)
    return minutesAgo == 1 ? minutesAgo + ' min ago' : minutesAgo + ' min ago'
  } else if (elapsed < msPerDay) {
    let formattedTime = moment(timeStamp)
      .tz(Vue.prototype.$timezone)
      .format('h:mm a')
    return formattedTime
  } else if (elapsed < msPerWeek) {
    let formattedTime = moment(timeStamp)
      .tz(Vue.prototype.$timezone)
      .format('ddd h:mm a')
    return formattedTime
  } else {
    let formattedTime = moment(timeStamp)
      .tz(Vue.prototype.$timezone)
      .format('MMM-DD h:mm a')
    return formattedTime
  }
}
