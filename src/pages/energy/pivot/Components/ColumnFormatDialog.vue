<template>
  <BaseDialogBox
    :visibility.sync="visibility"
    :title="$t('pivot.columnFormat')"
    :onConfirm="save"
    :onCancel="closeDialog"
    cancelText="Cancel"
    confirmText="Save"
    width="46%"
  >
    <div slot="body" class="dialog-content-body pB20">
      <spinner v-if="loading" :show="loading" size="80" class=""></spinner>
      <div class="body" v-if="!loading">
        <div class="main-layout-cft">
          <div style="display:flex;justify-content:space-between;">
            <el-row>
              <el-row class="pivot-fc text-left line-height25 mT10">
                {{ $t('pivot.columnName') }}
              </el-row>
              <el-row class="text-left line-height25 mT5">
                <el-input
                  v-model="fieldName"
                  disabled
                  class="fc-input-full-border2 width60"
                >
                </el-input>
              </el-row>
            </el-row>
            <el-row>
              <el-row class="pivot-fc text-left line-height25 mT10">
                {{ $t('pivot.dataType') }}
              </el-row>
              <el-row class="text-left line-height25 mT5">
                <el-input
                  v-model="fieldDataType"
                  disabled
                  class="fc-input-full-border2 width60"
                >
                </el-input>
              </el-row>
            </el-row>
          </div>
          <div style="justify-content:space-between;">
            <el-row v-if="showUnitSelectorOption">
              <el-row class="pivot-fc text-left line-height25 mT10">
                {{ $t('pivot.unit') }}
              </el-row>
              <el-row class="text-left line-height25 mT10">
                <el-input
                  v-model="field.unit"
                  disabled
                  class="fc-input-full-border2"
                >
                </el-input>
              </el-row>
            </el-row>
            <el-row>
              <el-row>
                <div class="pivot-fc text-left line-height25 mT10">
                  {{ $t('pivot.alignment') }}
                </div>
              </el-row>
              <el-row>
                <el-select
                  v-if="reRender"
                  v-model="config.textAlign"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2 mT5 module-select"
                  popper-class="fc-group-select"
                  style="width:275px;"
                  @change="reRenderDropdown()"
                >
                  <el-option
                    v-for="(textAlign, index) in textAlignOptions"
                    :key="'textAlign-options' + index"
                    :label="textAlign.label"
                    :value="textAlign.value"
                  ></el-option>
                </el-select>
              </el-row>
            </el-row>
            <div class="decimal-points-input" v-if="showDecimalPoints">
              <el-row>
                <el-row>
                  <div class="pivot-fc text-left line-height25 mT10">
                    {{ $t('pivot.decimalPoints') }}
                  </div>
                </el-row>
                <el-row>
                  <el-input
                    v-model="config.decimalPoints"
                    type="number"
                    class="fc-input-full-border-select2 width60 mT5"
                  ></el-input>
                </el-row>
              </el-row>
            </div>
          </div>
        </div>
        <div class="metric-block mT20 " v-if="showUnitOption">
          <label class="fc-modal-sub-title">
            {{ $t('pivot.unit') }}
          </label>

          <div
            class="metric-display-unit-selecter"
            v-if="showUnitSelectorOption"
          >
            <el-row>
              <el-row>
                <div class="pivot-fc text-left line-height25 mT10">
                  {{ $t('pivot.displayUnit') }}
                </div>
              </el-row>
              <el-row>
                <el-select
                  v-model="config.displayUnit"
                  filterable
                  placeholder="Select"
                  style="width:275px;"
                  class="fc-input-full-border-select2 mT5 module-select"
                  popper-class="fc-group-select"
                  @change="converToUnitChanged"
                >
                  <el-option
                    v-for="(metric, index) in metricUnitOptions"
                    :key="'displayUnit-options' + index"
                    :label="metric.displayName"
                    :value="metric.unitId"
                  ></el-option>
                </el-select>
              </el-row>
            </el-row>
          </div>
          <div class="metric-custom-unit-input-box mT10" v-else>
            <el-row>
              <el-row>
                <div class="pivot-fc text-left line-height25 mT5">
                  {{ $t('pivot.customUnit') }}
                </div>
              </el-row>
              <el-row>
                <el-input
                  v-model="config.customUnit"
                  placeholder="Custom unit"
                  class="fc-input-full-border-select2 mT5 module-select"
                >
                </el-input>
              </el-row>
            </el-row>
          </div>
          <div class="show-unit-at-selecter mT10">
            <el-row>
              <el-row>
                <div class="pivot-fc text-left line-height25">
                  {{ $t('pivot.showUnitAt') }}
                </div>
              </el-row>
              <el-col :span="12">
                <el-select
                  v-model="unitDisplayLocation"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2 mT5 module-select"
                  popper-class="fc-group-select"
                  style="width:275px;"
                  @change="unitShowAtChanged"
                >
                  <el-option
                    v-for="(unitDisplay, index) in unitDisplayOptions"
                    :key="'unitDisplay-options' + index"
                    :label="unitDisplay.label"
                    :value="unitDisplay.value"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col :span="12" class="pL10">
                <el-radio-group
                  v-model="config.prefix"
                  class="fc-input-full-border-select2 mT5 mL5 module-select cf-radio-group"
                  style="width:275px;display:flex;"
                >
                  <el-radio-button :label="true" class="cf-btn">{{
                    $t('pivot.prefix')
                  }}</el-radio-button>
                  <el-radio-button :label="false" class="cf-btn">{{
                    $t('pivot.suffix')
                  }}</el-radio-button>
                </el-radio-group>
              </el-col>
            </el-row>
          </div>
        </div>
        <div class="separator-block mT20" v-if="showNumberFormatBlock">
          <label class="fc-modal-sub-title">
            {{ $t('pivot.separator') }}
          </label>
          <div class="show-unit-at-selecter mT10">
            <el-row>
              <el-row>
                <div class="pivot-fc text-left line-height25 mT5">
                  {{ $t('pivot.localeSeparator') }}
                </div>
              </el-row>
              <el-row>
                <el-radio-group
                  v-model="config.applyLocaleSeparator"
                  class="fc-input-full-border-select2  width60 mT5 module-select"
                >
                  <el-radio-button :label="true">{{
                    $t('pivot.default')
                  }}</el-radio-button>
                  <el-radio-button :label="false">{{
                    $t('pivot.custom')
                  }}</el-radio-button>
                </el-radio-group>
              </el-row>
            </el-row>
          </div>
          <div class="decimal-separetor-selecter" v-if="showDecimalOptions">
            <el-row>
              <el-row>
                <div class="pivot-fc text-left line-height25 mT10">
                  {{ $t('pivot.decimalSeparator') }}
                </div>
              </el-row>
              <el-row>
                <el-select
                  v-model="config.decimalSeparator"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2 width60 mT5 module-select"
                  popper-class="fc-group-select"
                >
                  <el-option
                    v-for="(decimalSeparator, index) in decimalSeparatorOptions"
                    :key="'decimalSeparator-options' + index"
                    :label="decimalSeparator.label"
                    :value="decimalSeparator.value"
                  ></el-option>
                </el-select>
              </el-row>
            </el-row>
          </div>
          <div class="thousand-separetor-selecter" v-if="showThousandSeparator">
            <el-row>
              <el-row>
                <div class="pivot-fc text-left line-height25 mT10">
                  {{ $t('pivot.thousandSeparator') }}
                </div>
              </el-row>
              <el-row>
                <el-select
                  v-model="config.thousandSeparator"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2 width60 mT5 module-select"
                  popper-class="fc-group-select"
                >
                  <el-option
                    v-for="(thousandSeparator,
                    index) in thousandSeparatorOptions"
                    :key="'thousandSeparator-options' + index"
                    :label="thousandSeparator.label"
                    :value="thousandSeparator.value"
                  ></el-option>
                </el-select>
              </el-row>
            </el-row>
          </div>
        </div>
        <div
          class="alias-block mT10 "
          v-if="showEnumBlocKFormatValue || showBooleanBlocKFormatValue"
        >
          <label class="fc-modal-sub-title">
            {{ $t('pivot.formatValues') }}
          </label>
          <div class="boolean-block" v-if="showBooleanBlocKFormatValue">
            <div class="true-alias">
              <div class="pivot-fc text-left line-height25 mT10">
                {{ $t('pivot.true') }}
              </div>
              <el-input
                v-model="config.trueValue"
                placeholder="True"
                class="fc-input-full-border-select2  mT5 module-select"
              >
              </el-input>
            </div>
            <div class="false-alias">
              <div class="pivot-fc text-left line-height25 mT10">
                {{ $t('pivot.false') }}
              </div>
              <el-input
                v-model="config.falseValue"
                placeholder="False"
                class="fc-input-full-border-select2  mT5 module-select"
              >
              </el-input>
            </div>
          </div>
          <div class="enum-block mT10 " v-if="showEnumBlocKFormatValue">
            <div
              class="enum-alias"
              v-for="(enumObj, index) in enumAliasList"
              :key="index"
            >
              <el-row>
                <el-row>
                  <div class="pivot-fc text-left line-height25 mT10">
                    {{ enumObj.value }}
                  </div>
                </el-row>
                <el-row>
                  <el-input
                    v-model="config.enumValues[enumObj.key]"
                    :placeholder="enumObj.value"
                    class="fc-input-full-border-select2 mT5 module-select"
                  >
                  </el-input>
                </el-row>
              </el-row>
            </div>
          </div>
        </div>
      </div>
    </div>
  </BaseDialogBox>
</template>

<script>
import {
  isNumberField,
  isDecimalField,
  isIdField,
  isBooleanField,
  isEnumField,
} from '@facilio/utils/field'
import { API } from '@facilio/api'
import BaseDialogBox from './BaseDialogBox.vue'

export default {
  props: [
    'field',
    'alias',
    'editConfig',
    'visibility',
    'isDataColumn',
    'fieldName',
  ],
  components: {
    BaseDialogBox,
  },
  computed: {
    enumAliasList() {
      let enumList = []
      let keys = Object.keys(this.field.enumMap)
      keys.forEach(key => {
        enumList.push({ key: key, value: this.field.enumMap[key] })
      })
      return enumList
    },
    fieldDataType() {
      let dataType = this.field.dataTypeEnum
      return dataType[0] + dataType.slice(1).toLowerCase()
    },
    showUnitOption() {
      return (
        isDecimalField(this.field) ||
        isNumberField(this.field) ||
        this.field.unit
      )
    },
    showUnitSelectorOption() {
      return this.field.metricEnum
    },
    showThousandSeparator() {
      return (
        (this.isDataColumn ||
          isDecimalField(this.field) ||
          isNumberField(this.field)) &&
        !this.config.applyLocaleSeparator
      )
    },
    showDecimalOptions() {
      return isDecimalField(this.field) && !this.config.applyLocaleSeparator
    },
    showDecimalPoints() {
      return isDecimalField(this.field)
    },
    showBooleanBlocKFormatValue() {
      return this.isBooleanField(this.field)
    },
    showEnumBlocKFormatValue() {
      return this.isEnumField(this.field)
    },
    showNumberFormatBlock() {
      return (
        this.isDataColumn ||
        isDecimalField(this.field) ||
        isNumberField(this.field)
      )
    },
  },
  data() {
    return {
      config: {
        textAlign: null,
        unitId: -1,
        metricId: null,
        appendUnit: false,
        displayUnit: -1,
        prefix: false,
        headerUnit: false,
        unit: null,
        decimalSeparator: '.',
        decimalPoints: 2,
        thousandSeparator: ',',
        enumValues: {},
        applyLocaleSeparator: true,
      },
      reRender: true,
      unitDisplayLocation: 0,
      textAlignOptions: [
        { label: 'Center', value: 'center' },
        { label: 'Left', value: 'left' },
        { label: 'Right', value: 'right' },
      ],
      decimalSeparatorOptions: [
        { label: 'Dot (.)', value: '.' },
        { label: 'Comma (,)', value: ',' },
        { label: "Single quote (')", value: "'" },
      ],
      thousandSeparatorOptions: [
        { label: 'Comma (,)', value: ',' },
        { label: 'Dot (.)', value: '.' },
      ],
      unitDisplayOptions: [
        { label: 'None', value: 0 },
        { label: 'Header', value: 1 },
        { label: 'Each row', value: 2 },
      ],
      metricUnits: null,
      metricUnitOptions: null,
      metricOptions: null,
      loading: false,
    }
  },
  mounted() {
    // let nodes = document.getElementsByClassName('cf-btn').childNodes
    // for (let i = 0; i < nodes.length; i++) {
    //   if (nodes[i].nodeName.toLowerCase() == 'span') {
    //     nodes[i].style.width = '138px'
    //   }
    // }
    if (this.editConfig) {
      this.config = JSON.parse(JSON.stringify(this.editConfig))
    }

    if (!this.editConfig?.textAlign) {
      if (
        isNumberField(this.field) ||
        isDecimalField(this.field) ||
        isIdField(this.field)
      ) {
        this.config.textAlign = 'right'
      } else if (isBooleanField(this.field) || isEnumField(this.field)) {
        this.config.textAlign = 'center'
      } else {
        this.config.textAlign = 'left'
      }
    }

    if (!this.config.appendUnit && !this.config.headerUnit) {
      this.unitDisplayLocation = 0
    } else if (!this.config.appendUnit && this.config.headerUnit) {
      this.unitDisplayLocation = 1
    } else if (this.config.appendUnit && !this.config.headerUnit) {
      this.unitDisplayLocation = 2
    }
    this.getDefaultMetricUnits()
  },
  methods: {
    isNumberField,
    isDecimalField,
    isIdField,
    isBooleanField,
    isEnumField,
    changeUnitId() {},
    closeDialog() {
      this.$emit('close')
    },
    reRenderDropdown() {
      this.reRender = false
      this.$nextTick(() => {
        this.reRender = true
      })
    },
    save() {
      this.$emit('columnFormatConfig', {
        alias: this.alias,
        config: { ...this.config },
      })
    },
    metricUnitChanged(initial) {
      let metric = this.metricOptions.find(
        mt => mt.metricId == this.config.metricId
      )
      if (metric) {
        this.metricUnitOptions = this.metricUnits[metric._name]
      }
      if (this.field.unitId > 0) {
        this.config.unitId = this.field.unitId
        this.config.displayUnit =
          this.editConfig && this.config.metricId == this.editConfig.metricId
            ? this.editConfig.displayUnit
            : this.field.unitId
        this.converToUnitChanged(this.config.convertToUnitId)
      } else if (initial) {
        this.config.unitId = this.metricUnitOptions[0].unitId
        this.config.displayUnit = this.metricUnitOptions[0].unitId
        this.converToUnitChanged(this.config.convertToUnitId)
      }
    },
    getDefaultMetricUnits() {
      API.get('/units/getDefaultMetricUnits').then(({ data }) => {
        let { metricWithUnits, metrics } = data
        this.metricUnits = metricWithUnits
        this.metricOptions = metrics
        if (this.field.metric > 0) {
          this.config.metricId = this.field.metric
        }
        this.metricUnitChanged(false)
      })
    },
    unitShowAtChanged(value) {
      if (value == 0) {
        this.config.appendUnit = false
        this.config.headerUnit = false
      } else if (value == 1) {
        this.config.appendUnit = false
        this.config.headerUnit = true
      } else if (value == 2) {
        this.config.appendUnit = true
        this.config.headerUnit = false
      }
    },
    converToUnitChanged(optn) {
      let unit = this.metricUnitOptions.find(ut => ut?.unitId == optn)
      this.config.unit = unit?.symbol
    },
  },
}
</script>

<style scoped lang="scss">
.cfd-select-box {
  display: flex;
  //align-items: space-between;
}

.cf-btn {
  .el-radio-button__inner {
    // width: 138px !important;
    padding-right: 40px;
    padding-left: 40px;
  }
}

.dialog-content-body {
  /* padding: 40px 0px; */
  font-size: 13px !important;
}
.main-layout-cft {
  .el-input {
    width: 275px !important;
  }
}
.fc-modal-sub-title {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 1.6px;
  color: #ef4f8f;
  /* color: var(--fc-theme-color); */
  text-transform: uppercase;
  margin: 0;
}
.pivot-fc {
  color: #324056;
  letter-spacing: 0.5px;
  font-weight: normal;
  font-stretch: normal;
}
label {
  span {
    width: 138px;
  }
}
</style>
