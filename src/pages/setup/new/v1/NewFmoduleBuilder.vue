<template>
  <el-form :inline="true" :model="value" :label-position="'top'">
    <div class="flex-middle justify-content-space pT20">
      <div class="fc-text-pink2">{{ $t('common.tabs.reading') }}</div>
    </div>
    <div
      class="mT20 fc-field-values-block"
      v-for="(field, index) in value.fields"
      :key="index"
    >
      <el-row class="flex-middle">
        <el-col :span="6">
          <div class="label-txt-black">
            {{ $t('common.header.reading_name') }}
          </div>
        </el-col>
        <el-col :span="14" class="">
          <el-input
            v-model="field.displayName"
            :placeholder="$t('common.wo_report.field_name')"
            class="fc-input-full-border2 width100"
          >
          </el-input>
        </el-col>
      </el-row>
      <el-row class="pT20 flex-middle">
        <el-col :span="6">
          <div class="label-txt-black">
            {{ $t('common._common.reading_type') }}
          </div>
        </el-col>
        <el-col :span="14" class="">
          <el-select
            @change="showVisible(field, index)"
            :disabled="value.disableUneditable"
            v-model="field.dataTypeTemp"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="(dtype, index) in dataTypes"
              :key="index"
              :label="dtype.label"
              :value="dtype.value"
            >
            </el-option>
          </el-select>
        </el-col>
        <el-col
          :span="2"
          class="text-left pL10"
          v-if="field.dataType === 3 && value.includeValidations"
        >
          <!-- <i class="el-icon-setting fc-blue-txt-14 pointer" @click="dialogVisible = true" v-if="field.dataType === 2 || field.dataType === 3"></i> -->
          <img
            src="~assets/settings2.svg"
            @click="dialogVisible = true"
            style="height:18px;"
          />
        </el-col>
      </el-row>
      <el-row
        class="pT20 flex-middle"
        v-if="field.dataType === 3 || field.dataType === '3_1'"
      >
        <el-col :span="6"> </el-col>
        <el-col :span="14" class="">
          <el-col :span="12">
            <el-select
              @change="loadUnit(field)"
              :disabled="value.disableUneditable"
              v-model="field.metric"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(dtype, index) in metricsUnits.metrics"
                :key="index"
                :label="dtype.name"
                :value="dtype.metricId"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="12" class="pL10">
            <el-select
              :disabled="value.disableUneditable"
              v-model="field.unitId"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(dtype, index) in metricsUnits.metricWithUnits[
                  field.metricName
                ]"
                :key="index"
                :label="dtype.displayName + ' (' + dtype.symbol + ')'"
                :value="dtype.unitId"
              >
              </el-option>
            </el-select>
          </el-col>
        </el-col>
      </el-row>
      <el-row v-else-if="field.dataType === 4" class="pT20 flex-middle">
        <el-col :span="6"></el-col>
        <el-col :span="14" class="">
          <el-col :span="12" class="">
            <el-input
              v-model="field.trueVal"
              :placeholder="$t('common._common.+ve_value')"
              class="fc-input-full-border2 width100"
            ></el-input>
          </el-col>
          <el-col :span="12" class="pL10">
            <el-input
              v-model="field.falseVal"
              :placeholder="$t('common._common.-ve_value')"
              class="fc-input-full-border2 width100"
            >
            </el-input>
          </el-col>
        </el-col>
      </el-row>
      <el-row v-else-if="field.dataType === 8" class="pT20 flex-middle">
        <el-col :span="6"> </el-col>
        <el-col :span="14" class="position-relative">
          <div :key="index" v-for="(value, index) in field.values">
            <div class="flex-middle">
              <div>
                <el-input
                  type="text"
                  v-model="value.value"
                  placeholder=""
                  class="mB10 width280px"
                ></el-input>
              </div>
              <div
                class="creteria-delete-icon inline-flex fmodulevalue-Edit mL10"
              >
                <img
                  src="~assets/remove-icon.svg"
                  v-show="field.values.length > 2"
                  style="height:18px;width:18px;"
                  @click="removeOption(field.values, index)"
                />
                <img
                  src="~assets/add-icon.svg"
                  v-show="index + 1 === field.values.length"
                  style="height:18px;width:18px;"
                  class="mL10"
                  @click="addOptionsFields(field.values, index)"
                />
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row v-else></el-row>
      <el-row class="pT20 flex-middle">
        <el-col :span="6"></el-col>
        <el-col :span="12">
          <el-dialog
            :visible.sync="dialogVisible"
            width="35%"
            :append-to-body="true"
            class="fc-dialog-center-container"
            :title="$t('common.products.add_safe_limit')"
          >
            <f-safe-limit
              v-model="value.fields[index]"
              v-if="field.dataType === 2 || field.dataType === 3"
              :categoryId="value.categoryId"
              class="module-safe-limit-dialog"
            />
            <div class="modal-dialog-footer">
              <el-button
                @click="dialogVisible = false"
                class="modal-btn-cancel"
                >{{ $t('common._common.cancel') }}</el-button
              >
              <el-button
                type="primary"
                class="modal-btn-save"
                @click="dialogVisible = false"
                >{{ $t('common._common._save') }}</el-button
              >
            </div>
          </el-dialog>
        </el-col>
      </el-row>
      <div
        v-if="value.fields.length > 1"
        @click="deleteRow(index)"
        :title="$t('common._common.remove')"
        v-tippy
        class="fc-remove-icon pointer"
      >
        <img src="~assets/remove-icon.svg" style="height:16px;width:16px;" />
      </div>
    </div>
    <el-button @click="addRow" class="fc-add-border-green-btn mT20">
      <i class="el-icon-plus fwBold"></i>
      {{ $t('common._common.add_reading') }}
    </el-button>
  </el-form>
</template>
<script>
import FSafeLimit from '@/FSafeLimit'
export default {
  props: ['value'],
  data() {
    return {
      metricsUnits: {},
      isNumberFields: false,
      selectedMetric: null,
      alarmSeverity: ['Critical', 'Major', 'Minor'],
      dataTypes: [
        {
          label: this.$t('common._common.text'),
          value: 1,
        },
        // {
        //   label: 'Number',
        //   value: 2,
        // },
        {
          label: this.$t('common.products.decimal'),
          value: 3,
        },
        {
          label: this.$t('common.products.boolean'),
          value: 4,
        },
        {
          label: this.$t('common.products.pick_list'),
          value: 8,
        },
        {
          label: this.$t('common.products.counter'),
          value: '3_1',
        },
      ],
      dialogVisible: false,
    }
  },
  components: {
    FSafeLimit,
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
  },
  mounted() {
    this.loadDefaultMetricUnits()
  },
  methods: {
    removeOption(values, index) {
      values.splice(index, 1)
      // this.$forceUpdate()
    },
    addOptionsFields(values, index) {
      values.splice(index + 1, 0, {
        value: '',
      })
      // this.$forceUpdate()
    },
    showVisible(field, index) {
      if (index === 0) {
        this.isNumberFields = false
      }
      if (field.dataTypeTemp === '3_1') {
        field.dataType = 3
        field.counterField = true
      } else {
        field.dataType = field.dataTypeTemp
      }
      if (field.dataType === 2 || field.dataType === 3) {
        this.isNumberFields = true
      } else if (field.dataType === 8) {
        this.$set(field, 'values', [])
        field.values.push(
          {
            value: 'Option 1',
            visible: true,
          },
          {
            value: 'Option 2',
            visible: true,
          }
        )
      }
      if (field.dataType === 1) {
        field.displayType = 'TEXTBOX'
        field.displayTypeInt = 1
      } else if (field.dataType === 2) {
        field.displayType = 'NUMBER'
        field.displayTypeInt = 9
      } else if (field.dataType === 3) {
        field.displayType = 'DECIMAL'
        field.displayTypeInt = 13
      } else if (field.dataType === 4) {
        field.displayType = 'DECISION_BOX'
        field.displayTypeInt = 5
      } else if (field.dataType === 8) {
        field.displayType = 'SELECTBOX'
        field.displayTypeInt = 3
      }
    },
    loadUnit(field) {
      if (field.metric > 0) {
        let metric = this.metricsUnits.metrics.filter(d => {
          if (d.metricId === field.metric) {
            return d
          }
        })
        field.metricName = metric[0]._name
        if (field.metricName === 'PERCENTAGE') {
          this.metricsUnits.metricWithUnits[metric[0]._name].splice(1, 1)
        }
        this.$set(field, 'unitId', this.setOrgUnit(field.metric))
      }
    },
    setOrgUnit(metricId) {
      if (this.metricsUnits.orgUnitsList) {
        let unitId = null
        this.metricsUnits.orgUnitsList.forEach(d => {
          if (d.metric === metricId) {
            unitId = d.unit
          }
        })
        return unitId > 0 ? unitId : null
      }
    },
    addRow() {
      this.value.fields.push({
        safeLimitPattern: 'none',
        raiseSafeLimitAlarm: false,
        displayName: '',
        dataType: 3,
        safeLimitSeverity: 'Minor',
        lesserThan: null,
        greaterThan: null,
        betweenTo: null,
        betweenFrom: null,
      })
    },
    loadDefaultMetricUnits() {
      let self = this
      self.$http.get('/units/getDefaultMetricUnits').then(response => {
        self.metricsUnits = response.data
      })
    },
    deleteRow(index) {
      this.value.fields.splice(index, 1)
    },
  },
}
</script>
<style>
.module-builder-td .el-input .el-input__inner,
.el-textarea .el-textarea__inner {
  border-bottom: solid 1px #e2e8ee;
}

.module-builder-td .el-input__inner {
  background-color: none !important;
  background: none !important;
}

.builder-dialog .el-dialog__header {
  display: none;
}

.builder-dialog .el-dialog__body {
  min-height: 400px;
  padding: 0px 20px 60px;
}
</style>
