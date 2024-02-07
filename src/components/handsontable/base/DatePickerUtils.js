import moment from 'moment-timezone'
import Constants from 'util/constant'
function isValidDateTime(dateString, isTimeStamp) {
  let m
  if (isTimeStamp) {
    m = moment(Number.parseInt(dateString))
  } else {
    m = moment(dateString, Constants.DEFAULT_DATE_TIME_PARSE_FORMAT)
  }

  return m.isValid()
}

//returns moment object from given string
function parseDateTime(dateString, isTimeStamp) {
  let m
  if (isTimeStamp) {
    m = moment(Number.parseInt(dateString))
  } else {
    m = moment(dateString, Constants.DEFAULT_DATE_TIME_PARSE_FORMAT)
  }
  return m
}

function getFormattedDateTime(mom) {
  return mom.format(Constants.DEFAULT_DATE_TIME_DISPLAY_FORMAT)
}

export { isValidDateTime, parseDateTime, getFormattedDateTime }
