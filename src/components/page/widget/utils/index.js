import moment from 'moment-timezone'

export const getFormattedDuration = (value, format) => {
  if (!value) return '00:00 <span class="period">Hrs</span>'

  let duration = moment.duration(parseInt(value, 10), format)

  let days = parseInt(duration.asDays(), 10)
  let hours = duration.hours()
  let minutes = duration.minutes()
  let seconds = duration.seconds()

  const pad = val => String(val).padStart(2, '0')

  if (days > 0) {
    return hours
      ? `${pad(days)} <span class="period">Days</span> ${pad(
          hours
        )} <span class="period">Hrs</span>`
      : `${pad(days)} <span class="period">Days</span>`
  } else if (hours > 0) {
    return minutes
      ? `${pad(hours)} <span class="period">Hrs</span> ${pad(
          minutes
        )} <span class="period">Mins</span>`
      : `${pad(hours)} <span class="period">Hrs</span>`
  } else if (minutes > 0) {
    return `${pad(minutes)}:${pad(seconds)} <span class="period">Mins</span>`
  } else {
    return `${pad(seconds)} <span class="period">Secs</span>`
  }
}
