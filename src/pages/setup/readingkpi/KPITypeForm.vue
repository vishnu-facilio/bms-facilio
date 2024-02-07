<template>
  <div>
    <div id="kpitype-header" class="section-header">
      {{ $t('kpi.kpi.kpi_type') }}
    </div>
    <el-form
      ref="kpiTypeForm"
      :model="kpiTypeObj"
      label-width="150px"
      label-position="left"
      class="p50 pT10 pB30"
    >
      <div class="section-contaner flex-container">
        <div class="reading-kpi-form-item">
          <el-form-item :label="`${this.$t('kpi.kpi.kpi_type')}`">
            <el-radio-group
              :disabled="!isNew"
              v-model="kpiTypeObj.kpiTypeLabel"
              @change="handleKpiTypeChange"
            >
              <el-radio label="computed" class="fc-radio-btn">{{
                $t('kpi.kpi.computed_kpi')
              }}</el-radio>
              <el-radio label="realtime" class="fc-radio-btn">{{
                $t('kpi.kpi.real_time_kpi')
              }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <div
          v-if="kpiTypeObj.kpiTypeLabel !== 'realtime'"
          class="reading-kpi-form-item fc-label-required"
        >
          <el-form-item
            prop="frequency"
            :label="`${this.$t('common._common.frequency')}`"
          >
            <el-select
              v-model="kpiTypeObj.frequency"
              class="fc-input-full-border2 width100"
              :placeholder="$t('kpi.kpi.select_frequency')"
              @change="handleFreqChange"
            >
              <el-option
                v-for="(key, label) in frequencies"
                :key="key"
                :label="label"
                :value="key"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="reading-kpi-form-item">
          <el-form-item :label="`${this.$t('kpi.kpi.metric')}`">
            <el-select
              @change="loadUnit(metricsUnits)"
              v-model="kpiTypeObj.metricId"
              filterable
              clearable
              :placeholder="$t('common.products.select_metric')"
              class="fc-input-full-border2 width100"
              :disabled="!isNew"
            >
              <el-option
                v-for="(metric, index) in metricsList"
                :key="index"
                :label="metric.name"
                :value="metric.metricId"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="reading-kpi-form-item" v-if="kpiTypeObj.metricId !== 0">
          <el-form-item :label="`${this.$t('common.header.unit')}`">
            <el-select
              v-model="kpiTypeObj.unitId"
              :disabled="!kpiTypeObj.metricId || !isNew"
              :placeholder="$t('common.products.select_unit')"
              class="fc-input-full-border2 width100"
              filterable
              clearable
            >
              <el-option
                v-for="(unit, index) in unitOptions"
                :key="index"
                :label="unit.displayName + ' (' + unit.symbol + ')'"
                :value="unit.unitId"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="reading-kpi-form-item" v-else>
          <el-form-item :label="`${this.$t('common.header.unit')}`">
            <el-input
              v-model="kpiTypeObj.customUnit"
              class="fc-input-full-border2"
              :placeholder="`${this.$t('common._common.enter_unit')}`"
              :autofocus="true"
              :disabled="!isNew"
            ></el-input>
          </el-form-item>
        </div>
      </div>
    </el-form>
  </div>
</template>
<script>
import { isEmpty } from 'util/validation'
import { API } from '@facilio/api'

const frequencies = {
  '1 Min': 1,
  '2 Mins': 2,
  '3 Mins': 3,
  '4 Mins': 4,
  '5 Mins': 5,
  '10 Mins': 6,
  '15 Mins': 7,
  '20 Mins': 8,
  '30 Mins': 9,
  '1 Hr': 10,
  '2 Hr': 11,
  '3 Hr': 12,
  '4 Hr': 13,
  '8 Hr': 14,
  '12 Hr': 15,
  '1 Day': 16,
  'Weekly ': 17,
  'Monthly ': 18,
  'Quarterly ': 19,
  'Half Yearly': 20,
  'Annually ': 21,
}

export default {
  components: {},
  props: {
    isNew: {
      type: Boolean,
    },
    kpiInfo: {
      type: Object,
    },
  },
  data() {
    return {
      metricsUnits: {},
      metricName: '',
      unitId: null,
      metricId: null,
      kpiTypeObj: {
        kpiTypeLabel: 'computed',
        kpiType: 1,
        frequency: null,
        newOrExistingReading: 'new',
        metricId: null,
        unitId: null,
        customUnit: null,
      },
      prefillPhase: true,
      frequencies: frequencies,
    }
  },
  async created() {
    await this.loadDefaultMetricUnits()
    if (!this.isNew) this.prefillKpiDetails()
  },
  computed: {
    metricsList() {
      let metricList = this.$getProperty(this, 'metricsUnits.metrics')
      if (!isEmpty(metricList)) {
        let others = {
          _name: 'OTHER',
          metricId: 0,
          name: 'Others',
          siUnitId: 0,
        }
        metricList.push(others)
      }
      return metricList
    },
    unitOptions() {
      if (
        !isEmpty(this.metricName) &&
        !isEmpty(this.metricsUnits.metricWithUnits)
      )
        return this.metricsUnits.metricWithUnits[this.metricName]
      else return []
    },
  },
  watch: {
    kpiTypeObj: {
      handler(newVal) {
        let { frequency } = newVal || {}

        if (!isEmpty(frequency)) {
          this.kpiTypeObj.kpiType = frequency > 16 ? 2 : 1
        }
        this.$emit('kpiTypeChange', newVal)
      },
      deep: true,
    },
  },
  methods: {
    async prefillKpiDetails() {
      let { kpiInfo } = this
      if (!isEmpty(kpiInfo)) {
        let { kpiType, frequency, metricId, unitId, customUnit } = kpiInfo || {}
        this.kpiTypeObj = {
          metricId: metricId,
          unitId: unitId,
          customUnit: customUnit,
        }
        if (kpiType === 1 || kpiType === 2) {
          this.$set(this.kpiTypeObj, 'kpiTypeLabel', 'computed')
          this.$set(this.kpiTypeObj, 'frequency', frequency)
        } else {
          this.$set(this.kpiTypeObj, 'kpiTypeLabel', 'realtime')
          this.$set(this.kpiTypeObj, 'frequency', null)
        }

        let metricType = this.getMetricType(this.kpiTypeObj.metricId)
        if (metricType === 0) {
          this.$set(this.kpiTypeObj, 'metricId', null)
        } else if (metricType === 2) {
          this.$set(this.kpiTypeObj, 'metricId', 0)
          this.setMetricName(this.metricsUnits)
        } else {
          this.setMetricName(this.metricsUnits)
        }
      }
      this.prefillPhase = false
    },
    handleKpiTypeChange() {
      let { kpiTypeObj } = this
      let { kpiTypeLabel } = kpiTypeObj || {}
      if (kpiTypeLabel === 'realtime') this.kpiTypeObj.kpiType = 3
      else {
        this.kpiTypeObj.kpiType = 1
      }
      this.kpiTypeObj.frequency = null
    },
    handleFreqChange() {
      let { kpiTypeObj } = this
      let { frequency } = kpiTypeObj || {}
      if (!isEmpty(frequency)) {
        this.kpiTypeObj.kpiType = frequency > 16 ? 2 : 1
      }
    },
    getMetricType(metricId) {
      //0 - doesnt exist
      //1 - standard
      //2 - custom
      let customUnit = this.$getProperty(this.kpiTypeObj, 'customUnit')

      if (!isEmpty(metricId)) {
        if (metricId === 0 && !isEmpty(customUnit)) return 2
        return 1
      }
      return 0
    },
    setLookupFieldValue(props) {
      let { kpiTypeObj } = this
      let { field } = props
      let { options = [], selectedItems = [] } = field
      let selectedItemIds = []
      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.map(item => item.value)
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          // Have to push only new options that doesnt exists in field options
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )
          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }
      this.$set(kpiTypeObj, `assets`, selectedItemIds)
      this.$set(this.selectedLookupField, 'options', options)
    },
    constructCategoryFilter() {
      let { kpiTypeObj } = this
      let { assetCategoryId } = kpiTypeObj || {}
      let filters = {}
      if (!isEmpty(assetCategoryId)) {
        filters = {
          category: {
            operator: 'is',
            value: [`${assetCategoryId}`],
          },
        }
      }
      return filters
    },
    onAssetCategorySelected(selectedValue) {
      let { label } = selectedValue || {}
      let { assetField, kpiTypeObj } = this
      let { selectedItems } = assetField || {}
      if (!isEmpty(label)) {
        this.selectedCategoryLabel = label
      }
      if (!isEmpty(selectedItems)) {
        assetField.selectedItems = []
        this.$set(kpiTypeObj, 'assets', null)
      }
    },
    async loadDefaultMetricUnits() {
      let { data } = await API.get('/units/getDefaultMetricUnits')
      if (!isEmpty(data)) {
        this.metricsUnits = data
      }
    },
    setMetricName(metricsUnits) {
      if (this.kpiTypeObj.metricId !== 0) {
        this.metricName = metricsUnits.metrics.find(
          metric => this.kpiTypeObj.metricId === metric.metricId
        )
        this.metricName = this.metricName._name
      }
    },
    loadUnit(metricsUnits) {
      if (this.kpiTypeObj.metricId !== 0) {
        this.setMetricName(metricsUnits)
        this.unitId = metricsUnits.orgUnitsList.find(
          metric => metric.metric === this.kpiTypeObj.metricId
        )
        if (!isEmpty(this.kpiTypeObj.unitId)) {
          this.$set(this.kpiTypeObj, 'unitId', this.unitId.unit)
        }
      } else {
        this.$set(this.kpiTypeObj, 'unitId', 0)
      }
    },
  },
}
</script>

<style scoped lang="scss">
.kpi-form-item {
  flex: 1 1 100%;
  width: 100%;
  border-top: 1px solid #c7c1c14f;
}
.add-kpi-category-container {
  border: none;
  width: 100%;
  text-align: left;
  color: #3ab2c2;
  font-weight: 400;
}
.plus-icon {
  font-weight: bold;
}
</style>
<style lang="scss">
.reading-kpi-form-item {
  flex: 1 1 100%;
  width: 100%;
  .el-form-item {
    display: flex;
    flex-direction: column;
  }
  .el-form-item__content {
    margin-left: 0px !important;
  }
}
.min-width730 {
  min-width: 730px;
}
</style>
