<template>
  <div>
    <div class="vm-section-header pB10">
      {{ $t('asset.virtual_meters.reading_details') }}
    </div>
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <Spinner :show="showLoading" size="80"></Spinner>
    </div>
    <div v-else class="p30">
      <ReadingInfo
        v-for="(reading, index) in readings"
        :key="`vm-reading-${index}`"
        :isNew="isNew"
        :vmInfo="getReadingContext(reading)"
        :reading="reading"
        :meterReadings="meterReadings"
        :canShowDelete="canShowDelete"
        :validation="getValidationContext(reading)"
        :ruleCondition="getRuleCondition(reading)"
        @updateReading="updateReading(...arguments, index)"
        @deleteReading="deleteReading"
      >
      </ReadingInfo>
      <div class="pT20" :class="disableReading">
        <el-button @click="addReading()" class="add-new-reading-btn">
          <i class="el-icon-plus"></i>
          {{ $t('asset.virtual_meters.add_new_reading_details') }}
        </el-button>
      </div>
    </div>
  </div>
</template>
<script>
import ReadingInfo from './ReadingInfo.vue'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import { v4 as uuid } from 'uuid'
import { readingModel, validateReadings, vmScopeHash } from './vmUtil'
import isEqual from 'lodash/isEqual'
import { loadMeterReadingFields } from './vmUtil'

export default {
  components: { ReadingInfo, Spinner },
  props: ['isNew', 'vmInfo'],
  data() {
    return {
      readings: [],
      meterReadings: [],
      readingValidations: [],
      showLoading: false,
    }
  },
  created() {
    let { isNew } = this
    if (isNew) {
      this.addReading()
    } else {
      this.initReadings()
    }
  },
  computed: {
    utilityType() {
      let { vmInfo } = this
      return this.$getProperty(vmInfo, 'utilityType', null)
    },
    vmScope() {
      let { vmInfo } = this
      return this.$getProperty(vmInfo, 'scope', null)
    },
    canShowDelete() {
      let { readings } = this
      return (readings || []).length > 1
    },
    relationConfig() {
      let { vmInfo, vmScope } = this
      let lookupModuleName = this.$getProperty(vmScopeHash, `${vmScope}`, null)
      let assetCategoryId = null

      if (['assetCategory', 'spaceCategory'].includes(lookupModuleName)) {
        assetCategoryId = this.$getProperty(vmInfo, `${lookupModuleName}`, null)
      }

      return { assetCategoryId, lookupModuleName }
    },
    disableReading() {
      let { readingValidations } = this
      let inValidReading = (readingValidations || []).filter(
        validation => !validation.valid
      )

      return !isEmpty(inValidReading) ? 'disable-vm-reading-add' : ''
    },
  },
  watch: {
    utilityType: {
      async handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(oldVal, newVal)) {
          this.getAllMeterReadings()
        } else {
          this.meterReadings = []
        }
      },
      immediate: true,
    },
    vmScope: {
      async handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(oldVal, newVal)) {
          if (!isEmpty(oldVal)) {
            this.resetReadings()
          }
        } else {
          this.meterReadings = []
        }
      },
      immediate: true,
    },
  },
  methods: {
    initReadings() {
      let { vmInfo } = this
      let { readings } = vmInfo || {}
      this.syncReadings(readings)
    },
    addReading() {
      let { readings } = this
      let readingObj = cloneDeep(readingModel)
      readingObj = { ...readingObj, uuid: uuid().substring(0, 4) }
      readings.push(readingObj)
      this.syncReadings(readings)
    },
    updateReading(reading, index) {
      let { readings } = this
      readings[index] = reading || {}
      this.syncReadings(readings)
    },
    getReadingContext(reading) {
      let { vmInfo } = this
      return { ...vmInfo, ...reading }
    },
    deleteReading(uuid) {
      let { readings } = this
      readings = (readings || []).filter(reading => reading.uuid !== uuid)
      this.syncReadings(readings)
    },
    syncReadings(readings) {
      this.validateVMReadings(readings)
      this.$emit('updateVirtualMeter', { readings })
      this.readings = readings
    },
    validateVMReadings(readings) {
      return new Promise(resolve => {
        let validatedReadings = validateReadings(readings) || []
        let inValidReading = validatedReadings.filter(reading => !reading.valid)

        this.readingValidations = validatedReadings
        resolve(isEmpty(inValidReading))
      })
    },
    getValidationContext(reading) {
      let { readingValidations } = this
      let { uuid } = reading || {}
      let validation =
        (readingValidations || []).find(
          validation => validation.uuid === uuid
        ) || {}

      return validation
    },
    getRuleCondition(reading) {
      let { relationConfig } = this
      return { ...relationConfig, ...reading }
    },
    async getAllMeterReadings() {
      let { utilityType } = this
      let readings = await loadMeterReadingFields(
        null,
        utilityType,
        false,
        null,
        true
      )
      this.meterReadings = readings || []
    },
    resetReadings() {
      this.showLoading = true
      this.readings = []
      this.addReading()
      this.$nextTick(() => {
        this.showLoading = false
      })
    },
  },
}
</script>
<style scoped lang="scss">
.add-new-reading-btn {
  width: 250px;
  height: 35px;
  border-radius: 4px;
  border-color: transparent;
  background-color: #0074d1;
  color: #fff;
  font-weight: 500;
  font-size: 14px;
  text-transform: capitalize;
  padding-top: 10px;
  &:hover {
    background-color: #0074d1;
    color: #ffffff;
  }
  &:active {
    color: #fff;
    background-color: #0074d1;
    border: transparent;
  }
}
.disable-vm-reading-add {
  opacity: 0.5;
  pointer-events: none;
}
</style>
