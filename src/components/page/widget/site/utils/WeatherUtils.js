import { isEmpty } from '@facilio/utils/validation'
export const ICON_TYPES = {
  CLEAR_DAY: 1,
  CLEAR_NIGHT: 2,
  RAIN: 3,
  SNOW: 4,
  SLEET: 5,
  WIND: 6,
  FOG: 7,
  CLOUDY: 8,
  PARTLY_CLOUDY_DAY: 9,
  PARTLY_CLOUDY_NIGHT: 10,
  HAIL: 11,
  THUNDERSTORM: 12,
  TORNADO: 13,
}

export const WEATHER_ICONS = {
  default: 'cloudy',
  [ICON_TYPES.CLEAR_DAY]: 'clear-day',
  [ICON_TYPES.CLEAR_NIGHT]: 'clear-night',
  [ICON_TYPES.RAIN]: 'rain',
  [ICON_TYPES.SNOW]: 'snow',
  [ICON_TYPES.SLEET]: 'sleet',
  [ICON_TYPES.WIND]: 'wind',
  [ICON_TYPES.FOG]: 'fog',
  [ICON_TYPES.CLOUDY]: 'cloudy',
  [ICON_TYPES.PARTLY_CLOUDY_DAY]: 'partly-cloudy-day',
  [ICON_TYPES.PARTLY_CLOUDY_NIGHT]: 'partly-cloudy-night',
  [ICON_TYPES.HAIL]: 'hail',
  [ICON_TYPES.THUNDERSTORM]: 'thunderstorm',
  [ICON_TYPES.TORNADO]: 'tornado',
}

export function getIconSrc(iconNum) {
  if (!isEmpty(iconNum) && iconNum < 14 && iconNum > 0)
    return WEATHER_ICONS[iconNum]
  return WEATHER_ICONS['default']
}
