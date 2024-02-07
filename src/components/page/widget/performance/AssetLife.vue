<template>
  <div>
    <div class="p30" ref="asset-life-description" v-if="description">
      <div class="fc-blue-label f11 text-uppercase letter-spacing07">
        {{ $t('asset.assets.asset_description') }}
      </div>
      <div class="label-txt-black pT10 line-height20 break-word">
        {{ description }}
      </div>
      <div class="f14 pT20">
        <el-row>
          <el-col :span="8">
            <div class="pR15 mR15">
              <div
                class="bold mR15 fc-blue-label f11 text-uppercase letter-spacing07"
              >
                {{ field1Name }}
              </div>
              <div
                class="label-txt-black pT10 line-height20 break-word"
                v-if="field1"
              >
                {{ field1 }}
              </div>
              <div v-else class="f13">{{ $t('asset.assets.unknown') }}</div>
            </div>
          </el-col>
          <el-col :span="12">
            <div>
              <div
                class="bold mR15 fc-blue-label f11 text-uppercase letter-spacing07"
              >
                {{ field2Name }}
              </div>
              <div
                v-if="field2"
                class="label-txt-black pT10 line-height20 break-word"
              >
                {{ field2 }}
              </div>
              <div v-else class="label-txt-black pT10 line-height20 break-word">
                {{ $t('asset.assets.unknown') }}
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <!--no description  -->
    <div class="pL30" v-else>
      <div class="d-flex flex-direction-row f14 pT25">
        <inline-svg
          src="calendar"
          class="icon icon-md mR20 text-pelorous"
        ></inline-svg>
        <div class="pR15 mR15 border-right">
          <span class="bold mR15">{{ field1Name }}</span>
          <span v-if="field1">{{ field1 }}</span>
          <span v-else class="f13">{{ $t('asset.assets.unknown') }}</span>
        </div>
        <div class="pR15 mR15">
          <span class="bold mR15">{{ field2Name }}</span>
          <span v-if="field2">{{ field2 }}</span>
          <span v-else class="f13">{{ $t('asset.assets.unknown') }}</span>
        </div>
        <div class="pR15 mR15 mL-auto" v-if="field3Name">
          <span class="bold mR15">{{ field3Name }}</span>
          <span v-if="field3">{{ field3 }}</span>
          <span v-else class="f13">{{ $t('asset.assets.unknown') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'

export default {
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
  ],
  data() {
    return {
      field1: null,
      field2: null,
      field3: null,
      description: null,
    }
  },
  mounted() {
    if (this.moduleName === 'mlAnomalyAlarm') {
      this.field1 = this.$options.filters.formatDate(
        this.details.occurrence.createdTime,
        false,
        false
      )

      if (
        this.getAlarmSeverity(this.details.alarm.severity.id).severity ===
        'Clear'
      ) {
        this.field2 = this.$options.filters.formatDate(
          this.details.alarm.lastClearedTime,
          false,
          false
        )
      } else {
        // this.description = this.details.alarm.description
        this.field2 = this.$options.filters.formatDate(
          this.details.alarm.lastOccurredTime,
          false,
          false
        )
      }
    } else if (this.moduleName === 'formulaField') {
      let { createdTime, modifiedTime, minTarget, target } = this.details

      if (!isEmpty(createdTime))
        this.field1 = this.$options.filters.formatDate(
          createdTime,
          false,
          false
        )

      if (!isEmpty(modifiedTime))
        this.field2 = this.$options.filters.formatDate(
          modifiedTime,
          false,
          false
        )

      if (!isEmpty(minTarget) && !isEmpty(target)) {
        this.field3 = minTarget.toString() + ' - ' + target.toString()
      } else if (!isEmpty(minTarget)) {
        this.field3 = ' > ' + minTarget.toString()
      } else if (!isEmpty(target)) {
        this.field3 = ' < ' + target.toString()
      }
    } else if (
      this.moduleName === 'fahu' &&
      this.$org.id === 210 &&
      this.details.id === 1145442
    ) {
      this.field1 = '15 Years'
      this.field2 = '6 Years'
    } else if (this.$org.id === 321) {
      if (this.details.id === 1293825) {
        this.field1 = '25 Years'
        this.field2 = '18 Years'
      } else if (this.details.id === 1272810) {
        this.field1 = '18 Years'
        this.field2 = '6 Years'
      } else if (this.details.id === 1271044) {
        this.field1 = '20 Years'
        this.field2 = '10 Years'
      }
    }
    if (!isEmpty(this.description)) {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['asset-life-description'])) {
          if (this.resizeWidget) {
            this.resizeWidget({ h: this.layoutParams.h + 2 })
          }
        }
      })
    }
  },
  computed: {
    ...mapGetters(['getAlarmSeverity']),
    field1Name() {
      let { moduleName } = this

      switch (moduleName) {
        case 'mlAnomalyAlarm': {
          return this.$t('alarm.alarm.last_Occurred_Time')
        }
        case 'formulaField': {
          return this.$t('alarm.alarm.created_on')
        }
        default: {
          return this.$t('asset.performance.expected_life')
        }
      }
    },
    field2Name() {
      let { moduleName } = this

      switch (moduleName) {
        case 'mlAnomalyAlarm': {
          return this.$t('alarm.alarm.last_reported_on')
        }
        case 'formulaField': {
          return this.$t('alarm.alarm.last_updated')
        }
        default: {
          return this.$t('asset.performance.est_time_rem')
        }
      }
    },
    field3Name() {
      if (this.moduleName === 'formulaField') {
        return 'Safe Limit : '
      }
      return null
    },
  },
}
</script>
