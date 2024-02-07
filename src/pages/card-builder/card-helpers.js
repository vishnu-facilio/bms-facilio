import {
  isEmpty,
  isNumber,
  isFloat,
  isInteger,
} from '@facilio/utils/validation'
import {
  formatCurrency,
  formatInterger,
  formatCardDecimal,
} from 'charts/helpers/formatter'
import moment from 'moment-timezone'
import Vue from 'vue'
import { mapGetters } from 'vuex'
export default {
  computed: {
    ...mapGetters(['getCurrentUser']),
  },
  data() {
    return {
      timeFormat: Vue.prototype.$timeformat || 'HH:mm a',
      dateFormat: Vue.prototype.$dateformat,
    }
  },
  methods: {
    getUserDateTime(timestamp) {
      let timeZone = this.getCurrentUser?.timezone
      if (timeZone == null) {
        timeZone = Vue.prototype.$timezone
      }
      let format = `${this.dateFormat} ${this.timeFormat}`
      return moment(timestamp)
        .tz(timeZone)
        .format(format)
    },
    getUserTime(timestamp) {
      return moment(timestamp).format(this.timeFormat)
    },
    getUserDate(timestamp) {
      return moment(timestamp).format(this.dateFormat)
    },

    formatReadingsForPubSub(readingArray) {
      return readingArray.map(({ moduleName, fieldName, parentId }) => ({
        moduleName,
        fieldName,
        parentId,
      }))
    },
    isValue(value) {
      if (value === undefined) {
        return false
      } else if (value === null) {
        return false
      }
      return true
    },
    getValidData(value) {
      if (value === undefined || isNaN(value) || value === null) {
        return '--'
      } else {
        return value
      }
    },
    getFormatedValue(value, decimalPlace) {
      let validData = this.getValidData(value)
      if (typeof validData === 'string') {
        return validData
      } else if (decimalPlace > 0) {
        return this.formatDecimal(validData, decimalPlace)
      } else if (decimalPlace > -1) {
        return this.formatDecimal(validData, 0)
      } else {
        return this.formatDecimal(validData)
      }
    },
    formatCardDate(time, formatter) {
      if (time) {
        let format = formatter || 'DD MMM YYYY'
        return moment(Number(time))
          .tz(this.$timezone)
          .format(format)
      }
      return ''
    },
    formatValueByFieldType(value, fieldObj) {
      if (value && fieldObj) {
        let dataTypeEnum = fieldObj.dataTypeEnum
          ? fieldObj.dataTypeEnum
          : fieldObj.dataType || ''
        if (dataTypeEnum === 'DATE') {
          return this.formatCardDate(value)
        } else if (dataTypeEnum === 'DATE_TIME') {
          return this.formatCardDate(value, 'DD MMM YYYY hh:mm')
        } else if (dataTypeEnum === 'ENUM') {
          let { enumMap } = fieldObj
          return enumMap[value]
        } else if (dataTypeEnum === 'NUMBER') {
          return this.formatDecimal(value)
        } else if (dataTypeEnum === 'BOOLEAN') {
          if (typeof value.value === 'string') {
            return value.value
          } else {
            if (value.value === true) {
              return fieldObj.trueVal
            } else if (value.value === false) {
              return fieldObj.falseVal
            }
          }
        }
      }
      return typeof value !== 'undefined' ? value : ''
    },
    formatDecimal(value, decimalPoints) {
      if (this.isValue(value)) {
        if (window.orgId === 349) {
          decimalPoints = 2
        }
        if (!isEmpty(value) && isNumber(value) && isFloat(value)) {
          if (decimalPoints === 0) {
            return formatInterger(value)
          }
          if (!isEmpty(decimalPoints)) {
            return formatCurrency(Number(value), decimalPoints)
          } else {
            return formatCurrency(Number(value))
          }
        } else if (!isEmpty(value) && isNumber(value) && isInteger(value)) {
          if (decimalPoints === 0) {
            return formatInterger(value)
          }
          return formatCurrency(Number(value), 0)
        }
        return typeof value === 'boolean' ? String(value) : value
      }
      return value
    },
    formatIntergerDecimal(value, decimalPoints = 2) {
      if (!isEmpty(value) && isNumber(value) && isFloat(value)) {
        if (decimalPoints === 0) {
          return formatInterger(value)
        }
        return formatCardDecimal(Number(value))
      }
      return typeof value === 'boolean' ? String(value) : value
    },
    getMapMarkerIcon(data, style) {
      let { icon } = data
      let { value } = data
      let color =
        data && data.color
          ? data.color
          : style && style.color
          ? style.color
          : 'red'
      let x = value.hasOwnProperty('value')
        ? 25 - (value.value + '').length
        : 25
      let markerValue =
        value && value.hasOwnProperty('value') ? value.value : ''
      let markerSvg = ``
      if (icon === 1) {
        markerSvg = `  <svg width="62px" height="80px" viewBox="0 0 62 80" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
                        <g id="Map_1-new.svg" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                          <g id="map_2-new-01" transform="translate(8.000000, 1.000000)" fill-rule="nonzero">
                            <path d="M33.05,2.95 C25.22,-0.65 16.17,0.32 9.15,5.32 C1.51,10.76 -1.61,20.22 1.14,29.12 C2.74,34.28 5.42,39 8.16,43.63 C11.18,48.71 14.17,53.84 16.62,59.23 C18.79,64.01 20.89,69.28 21.4,74.55 C21.45,75.06 21.42,75.6 21.64,76.08 C21.83,76.49 22.19,76.81 22.62,76.94 C23.05,77.07 23.55,77.01 23.89,76.72 C24.47,76.24 24.43,75.4 24.52,74.71 C24.59,74.17 24.68,73.64 24.77,73.11 C24.88,72.43 25.01,71.76 25.14,71.09 C26.14,66.08 27.72,61.02 29.92,56.36 C32.2,51.53 35.29,47.15 38.45,42.86 C41.51,38.7 43.86,33.72 45.07,28.68 C45.82,25.57 46.08,22.32 45.59,19.14 C45.18,16.48 44.27,13.9 42.88,11.6 C40.95,8.39 37.89,5.17 33.05,2.95 Z" id="Path" stroke="#FFFFFF" fill="${color}"></path>
                            <circle id="Oval" fill="#000" cx="22.88" cy="24.11" r="8.56" opacity="0.5"></circle>
                          </g>
                        </g>
                      </svg>`
      } else if (icon === 2) {
        x = 25 - (markerValue + '').length * 3
        markerSvg = `<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="62px" height="80px" viewBox="0 0 62 80" version="1.1">
                  <title>Group 2 Copy</title>
                  <desc>Created with Sketch.</desc>
                  <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                      <g id="Group-2-Copy" transform="translate(0.200000, 0.200000)">
                          <g id="Group">
                              <path d="M30.7526226,55.4684967 C17.1645388,55.4684967 6.14942237,44.4533803 6.14942237,30.8652965 C6.14942237,17.2772127 17.1645388,6.2684967 30.7526226,6.2684967 C44.3407064,6.2684967 55.3558228,17.2772127 55.3558228,30.8652965 C55.3558228,44.4533803 44.3407064,55.4684967 30.7526226,55.4684967" id="Path" fill="#FFFFFF"/>
                              <path d="M30.7526226,53.0684967 C18.4902055,53.0684967 8.54973459,43.1280258 8.54973459,30.8656087 C8.54973459,18.6031916 18.4902055,8.6684967 30.7526226,8.6684967 C43.0150396,8.6684967 52.9555106,18.6031916 52.9555106,30.8656087 C52.9555106,43.1280258 43.0150396,53.0684967 30.7526226,53.0684967 M30.7526226,0 C13.7687878,0 -2.84217094e-14,13.7687878 -2.84217094e-14,30.7526226 C-2.84217094e-14,54.2033366 24.8890244,79.6 30.8519433,79.6 C36.8148621,79.6 61.5052452,54.2033366 61.5052452,30.7526226 C61.5052452,13.7687878 47.7364573,0 30.7526226,0" id="Fill-1" fill="${color}"/>
                              <path d="M30.7526226,53.0684967 C18.4902055,53.0684967 8.54973459,43.1280258 8.54973459,30.8656087 C8.54973459,18.6031916 18.4902055,8.6684967 30.7526226,8.6684967 C43.0150396,8.6684967 52.9555106,18.6031916 52.9555106,30.8656087 C52.9555106,43.1280258 43.0150396,53.0684967 30.7526226,53.0684967" id="Path" fill="#FFFFFF"/>
                              <path d="M29.724641,2.1193783 C38.1787772,2.1193783 45.7455395,5.9161022 50.8109907,11.8980598 C45.9002953,7.41174931 39.3624732,4.67517793 32.1850571,4.67517793 C16.9337201,4.67517793 4.5702952,17.031419 4.5702952,32.282756 C4.5702952,39.0818397 7.0274003,45.3069802 11.1020923,50.1186593 C5.57644711,45.068923 2.10987904,37.802809 2.10987904,29.7269564 C2.10987904,14.4756193 14.4733039,2.1193783 29.724641,2.1193783 Z" id="Combined-Shape" fill="${color}"/>
                              <path d="M35.8098164,15.4051386 C42.2018576,15.4051386 47.922767,18.2766474 51.7515365,22.8005079 C48.0397747,19.4068702 43.0965477,17.3369571 37.6695387,17.3369571 C26.1417121,17.3369571 16.7967335,26.6765057 16.7967335,38.2043323 C16.7967335,43.3410644 18.6522178,48.044383 21.7295199,51.6806217 C17.5551836,47.8645317 14.9370113,42.3743058 14.9370113,36.2725138 C14.9370113,24.7446872 24.2819898,15.4051386 35.8098164,15.4051386 Z" id="Combined-Shape" fill="#F1F3F4" transform="translate(33.344274, 33.542880) rotate(-180.000000) translate(-33.344274, -33.542880) "/>
                          </g>
                          <text id="75" font-family="ProximaNova-Semibold, Proxima Nova" font-size="25" font-weight="500" fill="#000" style="font-family: sans-serif;font-weight: 700;">
                              <tspan x="${x}" y="40">${markerValue}</tspan>
                          </text>
                      </g>
                  </g>
              </svg>`
      } else if (icon === 3) {
        x = 40 - (markerValue + '').length * 3
        markerSvg = `<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="85px" height="85px" viewBox="0 0 85 85" version="1.1">
    <!-- Generator: Sketch 55.2 (78181) - https://sketchapp.com -->
    <title>Group 2</title>
    <desc>Created with Sketch.</desc>
    <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
        <g id="Artboard-Copy" transform="translate(-160.000000, -111.000000)">
            <g id="Group-2" transform="translate(160.000000, 111.000000)" style="opacity:1">
                <circle id="Oval-Copy-4" fill="${color}" opacity="0.699999988" cx="42.5" cy="42.5" r="22.5"/>
                <circle id="Oval-Copy-5" fill="${color}" opacity="0.300000012" cx="42.5" cy="42.5" r="32.5"/>
                <circle id="Oval" fill="${color}" cx="42.5" cy="42.5" r="14.5"/>
                <text id="24" font-family="ProximaNova-Bold, Proxima Nova" font-size="12" font-weight="bold" fill="#4c4949">
                    <tspan x="${x}" y="46">${markerValue}</tspan>
                </text>
            </g>
        </g>
    </g>
</svg>`
      } else if (icon === 4) {
        markerSvg = `<?xml version="1.0" encoding="UTF-8"?>
        <svg width="62px" height="80px" viewBox="0 0 62 80" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
            <!-- Generator: Sketch 59 (86127) - https://sketch.com -->
            <title>Map_1 new.svg</title>
            <desc>Created with Sketch.</desc>
            <defs>
                <linearGradient x1="34.1142507%" y1="34.0182685%" x2="60.3501072%" y2="58.4804525%" id="linearGradient-1">
                    <stop stop-color="#FFFFFF" stop-opacity="0.2" offset="0%"></stop>
                    <stop stop-color="#FFFFFF" stop-opacity="0" offset="100%"></stop>
                </linearGradient>
            </defs>
            <g id="Map_1-new.svg" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                <g id="Map_1-new" transform="translate(11.000000, 1.000000)" fill-rule="nonzero">
                    <path d="M39.5,19.5 C39.5,8.78 30.72,0 20,0 C9.28,0 0.5,8.78 0.5,19.5 C0.5,29.71 8.36,38.08 18.37,38.9 L18.37,76.37 C18.37,77.29 19.08,78 20,78 C20.92,78 21.63,77.29 21.63,76.37 L21.63,38.9 C31.64,38.08 39.5,29.71 39.5,19.5 Z" id="Path" fill="#303C42"></path>
                    <circle id="Oval" fill="${color}" cx="20" cy="19.5" r="16.23"></circle>
                    <path d="M31.03,7.76 C31.13,8.37 31.23,9.09 31.23,9.8 C31.23,18.78 23.98,26.03 15,26.03 C10.71,26.03 6.73,24.29 3.87,21.54 C4.89,29.5 11.63,35.83 19.9,35.83 C28.88,35.83 36.13,28.58 36.13,19.6 C36.13,14.91 34.19,10.72 31.03,7.76 Z" id="Path" fill="#000000" opacity="0.1"></path>
                    <path d="M39.5,19.5 C39.5,8.78 30.72,0 20,0 C9.28,0 0.5,8.78 0.5,19.5 C0.5,29.71 8.36,38.08 18.37,38.9 L18.37,76.37 C18.37,77.29 19.08,78 20,78 C20.92,78 21.63,77.29 21.63,76.37 L21.63,38.9 C31.64,38.08 39.5,29.71 39.5,19.5 Z" id="Path" fill="url(#linearGradient-1)"></path>
                </g>
            </g>
        </svg>`
      } else {
        markerSvg = `<svg width="62px" height="80px" viewBox="0 0 62 80" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
        <!-- Generator: Sketch 59 (86127) - https://sketch.com -->
        <title>Map_1 new.svg</title>
        <desc>Created with Sketch.</desc>
        <g id="Map_1-new.svg" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
            <g id="map_2-new-01" transform="translate(8.000000, 1.000000)" fill-rule="nonzero">
                <path d="M33.05,2.95 C25.22,-0.65 16.17,0.32 9.15,5.32 C1.51,10.76 -1.61,20.22 1.14,29.12 C2.74,34.28 5.42,39 8.16,43.63 C11.18,48.71 14.17,53.84 16.62,59.23 C18.79,64.01 20.89,69.28 21.4,74.55 C21.45,75.06 21.42,75.6 21.64,76.08 C21.83,76.49 22.19,76.81 22.62,76.94 C23.05,77.07 23.55,77.01 23.89,76.72 C24.47,76.24 24.43,75.4 24.52,74.71 C24.59,74.17 24.68,73.64 24.77,73.11 C24.88,72.43 25.01,71.76 25.14,71.09 C26.14,66.08 27.72,61.02 29.92,56.36 C32.2,51.53 35.29,47.15 38.45,42.86 C41.51,38.7 43.86,33.72 45.07,28.68 C45.82,25.57 46.08,22.32 45.59,19.14 C45.18,16.48 44.27,13.9 42.88,11.6 C40.95,8.39 37.89,5.17 33.05,2.95 Z" id="Path" stroke="#FFFFFF" fill="${color}"></path>
                <circle id="Oval" fill="#000" cx="22.88" cy="24.11" r="8.56" opacity="0.5"></circle>
            </g>
        </g>
    </svg>`
      }
      let base64 = btoa(unescape(encodeURIComponent(markerSvg)))
      return `data:image/svg+xml;base64,${base64}`
    },
  },
}
