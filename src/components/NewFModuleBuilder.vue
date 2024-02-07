<template>
  <el-form :inline="true" :model="value" :label-position="'top'">
    <table class="setup-dialog-table">
      <thead class="setup-dialog-thead">
        <tr>
          <th class="setup-dialog-th" style="width: 140px;">Field Name</th>
          <th class="setup-dialog-th" style="width: 140px;">Field Type</th>
          <th
            v-if="isNumberFields"
            class="setup-dialog-th"
            style="width: 100px;"
          >
            Metric
          </th>
          <th v-else class="setup-dialog-th" style="width: 100px;"></th>
          <th
            v-if="isNumberFields"
            class="setup-dialog-th"
            style="width: 100px;"
          >
            Unit
          </th>
          <th v-else class="setup-dialog-th" style="width: 100px;"></th>
          <th></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(field, index) in value.fields" :key="index">
          <td class="module-builder-td" style="max-width: 170px;width:170px">
            <el-input
              v-model="field.displayName"
              placeholder="Field name"
              style="width: 140px;"
              class="fc-input-full-border2"
            ></el-input>
          </td>
          <td class="module-builder-td">
            <el-select
              @change="showVisible(field, index)"
              :disabled="value.disableUneditable"
              v-model="field.dataType"
              style="width: 120px;"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="(dtype, index) in dataTypes"
                :key="index"
                :label="dtype.label"
                :value="dtype.value"
              ></el-option>
            </el-select>
          </td>
          <td
            class="module-builder-td"
            v-if="field.dataType === 2 || field.dataType === 3"
          >
            <el-select
              @change="loadUnit(field)"
              :disabled="value.disableUneditable"
              v-model="field.metric"
              style="width: 150px;"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="(dtype, index) in metricsUnits.metrics"
                :key="index"
                :label="dtype.name"
                :value="dtype.metricId"
              ></el-option>
            </el-select>
          </td>
          <td class="module-builder-td" v-else-if="field.dataType === 4">
            <el-input
              v-model="field.trueVal"
              placeholder="+ve Value"
              style="width: 140px;"
              class="fc-input-full-border2"
            ></el-input>
          </td>
          <td
            class="module-builder-td"
            v-else-if="field.dataType === 8"
            style="width: 140px;"
          >
            <div
              class="row pB10 export-dropdown-menu"
              :key="index"
              v-for="(value, index) in field.values"
            >
              <!-- <div style="padding-left:3px;" class="row"> -->
              <div>
                <el-input
                  type="text"
                  v-model="value.value"
                  placeholder=""
                  style="width:140px;"
                ></el-input>
              </div>
              <div class="creteria-delete-icon inline-flex  mL20">
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
              <!-- <div >
                    <img src="~assets/add.svg" style="height:14px" />
                  </div>
                  <div class="optionremoveicon" @click="removeOption(field.options, index)" v-show="field.options.length > 2">
                    <i class="el-icon-delete"></i>
                  </div>
                  -->
            </div>
            <!-- <div v-for="field.options">
                <el-input v-model="field.trueVal" placeholder="True Value" style="width: 140px;" class="fc-input-full-border2"></el-input>
            </div> -->
          </td>
          <td class="module-builder-td" v-else></td>
          <td
            class="module-builder-td"
            v-if="field.dataType === 2 || field.dataType === 3"
          >
            <el-select
              :disabled="value.disableUneditable"
              v-model="field.unitId"
              style="width: 140px;"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="(dtype, index) in metricsUnits.metricWithUnits[
                  field.metricName
                ]"
                :key="index"
                :label="dtype.displayName + ' (' + dtype.symbol + ')'"
                :value="dtype.unitId"
              ></el-option>
            </el-select>
          </td>
          <td class="module-builder-td" v-else-if="field.dataType === 4">
            <el-input
              v-model="field.falseVal"
              placeholder="-ve Value"
              style="width: 140px;"
              class="fc-input-full-border2"
            ></el-input>
          </td>
          <!-- <td class="module-builder-td" v-else-if="field.dataType === 8">
            <div class="row" :key="option" v-for="(option, index) in field.options" style="padding-bottom:10px;">
             <div class="creteria-delete-icon">
                <img src="~assets/add-icon.svg" style="height:18px;width:18px;" class="delete-icon" @click="addOptionsFields(field.options, index)"/>
                <img src="~assets/remove-icon.svg" v-show="field.options.length > 2" style="height:18px;width:18px;margin-right: 3px;" class="delete-icon" @click="removeOption(field.options, index)"/>
              </div>
            </div>
          </td> -->
          <td class="module-builder-td" v-else></td>
          <!-- </div> -->
          <td>
            <el-dialog
              :visible.sync="dialogVisible"
              width="35%"
              :append-to-body="true"
              class="builder-dialog"
            >
              <f-safe-limit
                v-model="value.fields[index]"
                v-if="field.dataType === 2 || field.dataType === 3"
                :categoryId="value.categoryId"
                class="pL40"
              />
              <div class="modal-dialog-footer">
                <el-button
                  @click="dialogVisible = false"
                  class="modal-btn-cancel"
                  >Cancel</el-button
                >
                <el-button
                  type="primary"
                  class="modal-btn-save"
                  @click="dialogVisible = false"
                  >Save</el-button
                >
              </div>
            </el-dialog>
            <div class="hover-actions mL30 export-dropdown-menu">
              <img
                v-show="field.dataType === 2 || field.dataType === 3"
                slot="reference"
                src="~assets/settings2.svg"
                @click="dialogVisible = true"
                style="height:18px;"
                class="mR10"
              />
              <!-- settings icon -->
              &nbsp;&nbsp;
              <span @click="addRow" title="Add" v-tippy>
                <img src="~assets/add.svg" style="height:14px" class="mR10" />
              </span>
              &nbsp;&nbsp;
              <span
                v-if="value.fields.length > 1"
                @click="deleteRow(index)"
                title="Remove"
                v-tippy
              >
                <img src="~assets/remove.svg" style="height:14px;width:14px;" />
              </span>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
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
          label: 'Text',
          value: 1,
        },
        {
          label: 'Number',
          value: 2,
        },
        {
          label: 'Decimal',
          value: 3,
        },
        {
          label: 'Boolean',
          value: 4,
        },
        {
          label: 'Pick List',
          value: 8,
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
      values.splice(index + 1, 0, { value: '' })
      // this.$forceUpdate()
    },
    showVisible(field, index) {
      if (index === 0) {
        this.isNumberFields = false
      }
      if (field.dataType === 2 || field.dataType === 3) {
        this.isNumberFields = true
      } else if (field.dataType === 8) {
        this.$set(field, 'values', [])
        field.values.push(
          { value: 'Option 1', visible: true },
          { value: 'Option 2', visible: true }
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
        dataType: 1,
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
.builder-dialog {
}
</style>
