import helpers from 'src/util/helpers.js'
const { getOrgMoment: moment } = helpers

export const getFifteenMinSplit = hour => {
  let startTime = moment(hour, 'hh:mm A')

  return [
    startTime.add(15, 'minutes').format('hh:mm a'),
    startTime.add(15, 'minutes').format('hh:mm a'),
    startTime.add(15, 'minutes').format('hh:mm a'),
    startTime.add(15, 'minutes').format('hh:mm a'),
  ]
}

export const get15SplitFor24Hour = (start, end, isArray) => {
  let startTime = moment(start, 'HH:mm')
  let endTime = moment(end, 'HH:mm')

  if (endTime.isBefore(startTime)) {
    endTime.add(1, 'day')
  }

  let timeStops
  if (isArray) timeStops = []
  else timeStops = {}

  while (startTime < endTime) {
    startTime.add(15, 'minutes')
    if (isArray) timeStops.push(new moment(startTime).format('hh:mm a'))
    else timeStops[new moment(startTime).format('hh:mm a')] = {}
  }
  return timeStops
}

export const getHour = (timeString, format) => {
  let hour = moment(timeString, 'hh:mm A').format(format)
  if (format === 'H') return parseInt(hour)
  else return hour
}
