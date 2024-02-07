<template>
  <div class="dragabale-card height100">
    <shimmer-loading v-if="isLoading" class="map-shimmer"></shimmer-loading>
    <div v-else class="cb-card-container d-flex flex-direction-column">
      <div
        class="card-header-block f15 bold"
        :style="{ backgroundColor: cardStyle.headerColor, color: '#fff' }"
      >
        {{ cardData.title }}
      </div>
      <div class="height100 d-flex flex-direction-column pT10">
        <div
          v-for="(value, index) in cardData.values"
          :key="index"
          class="d-flex flex-direction-row reading pT15 pB15 pL25 pR25"
        >
          <div v-if="canShowIcons" style="min-width: 30px;">
            <inline-svg
              v-if="getIconPath(value.icon)"
              :key="value.icon"
              :src="getActivePath(value)"
              :class="['vertical-middle']"
              :iconClass="`icon icon-lg ${getActiveClass(value)}`"
            ></inline-svg>
          </div>
          <div v-if="canShowLabels" class="flex-grow f13 bold">
            {{ value ? value.label : '' }}
          </div>
          <div class="mL-auto vertical-middle">
            <template v-if="value && !$validation.isEmpty(value.value)">
              <span
                v-if="value.dataType === 'BOOLEAN'"
                :class="['f14', 'bold', value.value ? 'text-on' : 'text-off']"
              >
                {{
                  typeof value.value === 'string'
                    ? value.value
                    : value.value
                    ? 'ON'
                    : 'OFF'
                }}
              </span>
              <span
                v-else-if="value.dataType === 'ENUM'"
                :class="['f14 bold text-uppercase']"
              >
                {{ value.value }}
              </span>
              <span
                v-else-if="['DECIMAL', 'NUMBER'].includes(value.dataType)"
                class="f16"
              >
                <!-- {{ value.value }}
                {{ value.unit ? value.unit : '' }} -->
                {{
                  value.hasOwnProperty('value') && isValue(value.value)
                    ? formatDecimal(value.value)
                    : ''
                }}
                {{ value.unit ? value.unit : '' }}
              </span>
              <span v-else class="f15">
                {{ value.value }}
                {{ value.unit ? value.unit : '' }}
              </span>
            </template>
            <span v-else class="f13" style="color: #bfbfbf;">
              No Data
            </span>
          </div>
        </div>
      </div>
      <div class="pB5 text-center f12 fc-grey4" v-if="cardData.lastUpdated">
        *Last updated {{ lastUpdatedTime }}
      </div>
      <div class="pB5 text-center f12 fc-grey4" v-else>
        *No data found for the last one hour.
      </div>
    </div>
  </div>
</template>

<script>
import shimmerLoading from '@/ShimmerLoading'
import Card from '../base/Card'
import { graphicIcons } from 'pages/card-builder/card-constants'
import { isEmpty } from '@facilio/utils/validation'
import Vue from 'vue'
import moment from 'moment-timezone'

export default {
  extends: Card,
  components: {
    shimmerLoading,
  },
  computed: {
    canShowLabels() {
      return this.cardState.canShowLabels
    },
    canShowIcons() {
      return this.cardState.canShowIcons
    },
    lastUpdatedTime() {
      let { lastUpdated } = this.cardData
      // eslint-disable-next-line no-unused-vars
      let { now } = this

      return moment(lastUpdated)
        .tz(Vue.prototype.$timezone)
        .fromNow()
    },
    subscriptionParams() {
      let { cardParams, formatReadingsForPubSub: format } = this
      if (isEmpty(cardParams)) return []
      return {
        readings: format(cardParams.readings.map(({ reading }) => reading)),
      }
    },
  },
  methods: {
    getIconPath(icon) {
      return graphicIcons.find(i => i.name === icon)
    },
    getActivePath(value) {
      let isBoolean = value.dataType === 'BOOLEAN'
      let isTruthy =
        !isEmpty(value.value) &&
        (value.actualValue === true || value.value === true)
      let needsActiveIcon =
        !isEmpty(value.icon) &&
        [
          'bulb',
          'bell',
          'motor',
          'energy',
          'power',
          'pressure',
          'battery',
          'carbon',
          'carbon_alt',
          'humidity',
          'humidity_alt',
        ].includes(value.icon)

      if (isBoolean && isTruthy && needsActiveIcon) {
        return this.getIconPath(value.icon).activePath
      } else {
        return this.getIconPath(value.icon).path
      }
    },
    getActiveClass(value) {
      let isBoolean = value.dataType === 'BOOLEAN'
      let isNumber = ['DECIMAL', 'NUMBER'].includes(value.dataType)
      let isTruthy = false

      if (isBoolean) {
        isTruthy =
          !isEmpty(value.value) &&
          (value.actualValue === true || value.value === true)
      } else if (isNumber) {
        isTruthy = !isEmpty(value.value) && value.value > 0
      }

      let needsActiveClass =
        !isEmpty(value.icon) &&
        ['supplyfan', 'fan', 'bulb'].includes(value.icon)

      let { activeClass, className = null } = this.getIconPath(value.icon)

      if (isTruthy && needsActiveClass) {
        return activeClass
      } else return className ? className : ''
    },
  },
}
</script>

<style scoped>
.reading {
  align-items: center;
  height: 55px;
}
.reading:not(:last-of-type) {
  border-bottom: solid 1px rgba(0, 0, 0, 0.04);
}
.text-on {
  color: #39b2c2;
}
.text-off {
  color: #e74240;
}
</style>
