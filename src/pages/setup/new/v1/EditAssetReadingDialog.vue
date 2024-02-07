<template>
  <el-dialog
    v-if="visibility"
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div
      class="lex-middle fc-empty-white m10 fc-agent-empty-state height-calc200"
      v-if="loading"
    >
      <spinner :show="loading" style="margin-top: 360px"></spinner>
    </div>
    <el-form
      v-else
      ref="newAssetReadingForm"
      :model="module"
      :label-position="'top'"
    >
      <div class="new-header-container">
        <div class="setup-modal-title">Edit Reading</div>
      </div>
      <div
        style="height: calc(100vh - 100px); overflow-y: scroll"
        class="new-body-modal pB100 pR20"
      >
        <div class="">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="label-txt-black">Field Name</div>
              <el-input
                v-model="module.fields[0].displayName"
                class="input-background-remove fc-input-full-border2 width100 pT10"
              ></el-input>
            </el-col>
            <el-col :span="8" v-if="categoryName">
              <div class="label-txt-black">Category</div>
              <div class="label-txt-black">{{ categoryName }}</div>
            </el-col>
            <el-col :span="4" v-if="!isFormulaField">
              <div class="label-txt-black">Type</div>
              <div class="pT20 label-txt-black">
                {{
                  module.fields[0].counterField
                    ? 'Counter'
                    : $constants.dataType[module.fields[0].dataType]
                }}
              </div>
            </el-col>
          </el-row>
          <el-row
            class="pT20 pB10"
            v-if="
              module.fields[0].dataType === 2 || module.fields[0].dataType === 3
            "
            :gutter="20"
          >
            <el-col :span="12">
              <div class="label-txt-black">Metric</div>
              <el-select
                class="fc-radio-btn fc-radio-btn2 fc-input-full-border2 pT10 width100"
                @change="loadUnit(module.fields[0])"
                v-model="metric"
              >
                <el-option
                  v-for="(dtype, index) in metricsUnits.metrics"
                  :key="index"
                  :label="dtype.name"
                  :value="dtype.metricId"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="6">
              <div class="label-txt-black">Unit</div>
              <el-select
                class="fc-radio-btn fc-radio-btn2 fc-input-full-border2 pT10 width100"
                v-model="unitId"
              >
                <el-option
                  v-for="(dtype, index) in unitsForMetric"
                  :key="index"
                  :label="dtype.displayName + ' (' + dtype.symbol + ')'"
                  :value="dtype.unitId"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row v-if="module.fields[0].dataType === 4" class="pT20 pB10">
            <div class="label-txt-black">Boolean Values</div>
            <div class="row pT10">
              <div class="flex-middle">
                <el-input
                  style="width: 285px"
                  v-model="module.fields[0].trueVal"
                  class="input-background-remove fc-input-full-border2"
                ></el-input>
                <div class="pL5">(+ve)</div>
              </div>
            </div>
            <div class="row pT10">
              <div class="flex-middle">
                <el-input
                  style="width: 285px"
                  v-model="module.fields[0].falseVal"
                  class="input-background-remove fc-input-full-border2"
                ></el-input>
                <div class="pL5">(-ve)</div>
              </div>
            </div>
          </el-row>
          <el-row v-else-if="module.fields[0].dataType === 8" class="pT10 pB10">
            <!-- <div class="fc-text-pink2 mT20">Options</div> -->
            <div class="label-txt-black pT10 pB10">Pick List Options</div>
            <div
              :key="index"
              v-for="(value, index) in module.fields[0].values"
              class="pB10"
            >
              <div class="row" v-if="value.visible">
                <div>
                  <el-input
                    class="input-background-remove fc-input-full-border2 width100"
                    type="text"
                    v-model="value.value"
                    placeholder=""
                  ></el-input>
                </div>
                <div
                  style="align-self: center"
                  class="pL10 optionremoveicon pointer"
                  @click="removeOption(value, index)"
                  v-show="module.fields[0].values.length > 2"
                >
                  <img src="~assets/remove-icon.svg" />
                  <!-- <i class="el-icon-delete"></i> -->
                </div>
                <div
                  style="align-self: center"
                  class="pL10 pointer"
                  @click="addOptionsFields(module.fields[0].values, index)"
                  v-show="index + 1 === module.fields[0].values.length"
                >
                  <img src="~assets/add-icon.svg" />
                </div>
                <div class="clearboth"></div>
              </div>
            </div>
          </el-row>
        </div>
        <div v-if="showSensorDetection()" class="mT20 pB20">
          <div class="fc-text-pink2 mT20">
            {{ $t('setup.reading.faulty_sensor_detection') }}
          </div>
          <p class="grey-text2 pT5">
            {{ $t('setup.reading.specify_rules_to_detect_faulty_sensor') }}
          </p>
          <el-checkbox-group
            v-model="selectedValues"
            class="flex flex-direction-column"
          >
            <el-checkbox
              v-for="sensorRule in sensorRules"
              v-model="sensorRule.status"
              class="newradio pT20 f-checkbox fc-sensor-checkbox"
              color="secondary"
              @change="setStatus(sensorRule)"
              :label="sensorRule.sensorRuleType"
              :key="sensorRule.sensorRuleType"
            >
              <div
                v-for="(obj, index) in sensorRule.textArr"
                :key="index"
                class="flex-middle"
              >
                <div>{{ obj.text }}</div>
                <el-input
                  @change="
                    validation(
                      sensorRule,
                      sensorRule.ruleValidatorProps[obj.modelKey]
                    )
                  "
                  v-if="!$validation.isEmpty(obj.modelKey)"
                  v-model="sensorRule.ruleValidatorProps[obj.modelKey]"
                  class="width70px mL10 mR10 fc-input-full-border-h35 fc-input-full-border-bold"
                ></el-input>
              </div>
              <!-- <span>{{ sensorRule.last }}</span> -->
            </el-checkbox>
          </el-checkbox-group>
        </div>
        <!-- safe limit removed -->
        <!-- <f-safe-limit
          v-model="module.fields[0]"
          :edit="true"
          v-if="
            resourceType === 'asset' &&
              (module.fields[0].dataType === 2 ||
                module.fields[0].dataType === 3)
          "
          ref="safelimit"
        /> -->
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="save" class="modal-btn-save"
          >Save</el-button
        >
      </div>
      <!-- </div> -->
    </el-form>
  </el-dialog>
</template>
<script>
import FSafeLimit from '@/FSafeLimit'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['visibility', 'model', 'unitDetails', 'resourceType', 'categoryId'],
  data() {
    return {
      sensorRules: [],
      selectedValues: [],
      selectedValuesFromDb: [],
      loading: false,
      metricsUnits: this.unitDetails,
      numberFields: null,
      unitsForMetric: null,
      unitId: null,
      metric: null,
      sensorId: null,
      module: {
        categoryId: null,
        includeValidations: false,
        fields: [
          {
            safeLimitId: -1,
            raiseSafeLimitAlarm: false,
            displayName: '',
            dataType: 1,
            lesserThan: null,
            greaterThan: null,
            betweenTo: null,
            betweenFrom: null,
            safeLimitPattern: 'none',
            safeLimitSeverity: 'Minor',
          },
        ],
      },
      isFormulaField: false,
      formula: {
        name: '',
        workflow: null,
        includedResources: [],
      },
      formulaFieldUnit: '',
      alarmSeverity: ['Critical', 'Major', 'Minor'],
    }
  },
  components: {
    FSafeLimit,
    Spinner,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  mounted() {
    this.loading = true
    this.loadFormulaField()
    if (this.model) {
      this.unitId =
        this.model.fields[0].unitId > 0
          ? parseInt(this.model.fields[0].unitId)
          : null
      this.model.fields[0].unitId =
        this.model.fields[0].unitId > 0
          ? parseInt(this.model.fields[0].unitId)
          : null
      this.model.fields[0].metric =
        this.model.fields[0].metric > 0
          ? parseInt(this.model.fields[0].metric)
          : null
      this.metric = this.model.fields[0].metric
      if (this.model.fields[0].metric > 0) {
        if (this.metricsUnits.metrics) {
          let metric = this.metricsUnits.metrics.filter(d => {
            if (d.metricId === this.model.fields[0].metric) {
              return d
            }
          })
          this.unitsForMetric = this.metricsUnits.metricWithUnits[
            metric[0]._name
          ]
        }
      }
      if (this.model.fields[0].dataType === 4) {
        if (!this.model.fields[0].trueVal) {
          this.model.fields[0].trueVal = 'True'
        }
        if (!this.model.fields[0].falseVal) {
          this.model.fields[0].falseVal = 'False'
        }
      }
      Object.assign(this.module, this.$helpers.cloneObject(this.model))
    }
    this.formulaFieldUnit = this.model.fields[0].unit
    this.fetchSensorRules()
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    categoryName() {
      if (this.module.categoryId) {
        let category = this.assetCategory.find(
          category => category.id === this.module.categoryId
        )
        if (category) {
          return category.displayName
        }
      }
      return ''
    },
  },
  methods: {
    validation(sensorRule, value) {
      let { sensorRuleType } = sensorRule
      if (!this.selectedValues.includes(sensorRuleType) && !isEmpty(value)) {
        this.selectedValues.push(sensorRule.sensorRuleType)
      } else if (isEmpty(value)) {
        this.selectedValues = this.selectedValues.filter(
          val => val !== sensorRuleType
        )
      }
    },
    fetchSensorRules() {
      let { categoryId, model } = this
      let fieldObj = model.fields[0]
      let { id } = fieldObj
      let params = {
        categoryId,
        readingFieldId: id,
      }
      API.get(`/v2/alarm/fetchSensorRules`, params).then(({ error, data }) => {
        if (!error) {
          let { sensorRuleTypes, id: sensorRuleId } = data || []
          this.sensorRules = sensorRuleTypes
          this.sensorId = sensorRuleId
          this.constructPlaceHolders()
        } else {
          this.$message.error(error.message || 'Error Occured')
          this.loading = false
        }
      })
    },
    setStatus(sensorRule) {
      let { status } = sensorRule
      this.$set(sensorRule, 'status', isEmpty(status) || !status)
    },
    constructPlaceHolders() {
      this.sensorRules.forEach(sensorRule => {
        if (sensorRule.id > 0 && sensorRule.status) {
          this.selectedValues.push(sensorRule.sensorRuleType)
          this.selectedValuesFromDb.push(sensorRule)
        }
        let placeHolder = sensorRule.subject.match(/[^{\}]+(?=})/g)
        let reg = /\{.*?\}/g
        let textArr = []
        let text = sensorRule.subject.split(reg)
        let last = ''
        let gaps = []
        text.forEach((part, i) => {
          let obj = {
            text: part || '',
            modelKey:
              !isEmpty(placeHolder) && !isEmpty(placeHolder[i])
                ? placeHolder[i]
                : '',
          }
          textArr.push(obj)
          gaps.push(i + 1)
        })

        last = text.pop()
        this.$set(sensorRule, 'textArr', textArr)
        this.$set(sensorRule, 'gaps', gaps)
        this.$set(sensorRule, 'last', last)
      })
      this.loading = false
    },
    isSensorUpdateOrAdd() {
      let { sensorId } = this
      if (!isEmpty(sensorId)) {
        this.update(sensorId)
      } else {
        this.addSensorRecord()
      }
    },
    async addSensorRecord() {
      let {
        categoryId,
        model,
        sensorRules,
        selectedValues,
        assetCategory,
      } = this
      let fieldObj = this.$getProperty(model, 'fields.0')
      let category = assetCategory.find(category => category.id === categoryId)
      let { id } = fieldObj || {}
      let sensorRulesList = sensorRules.filter(sensorRule =>
        selectedValues.includes(sensorRule.sensorRuleType)
      )
      sensorRulesList.forEach(ruleType => {
        if (!isEmpty(ruleType.rulePropStr)) {
          delete ruleType.rulePropStr
        }
      })
      let params = {
        assetCategory: { id: categoryId },
        sensorFieldId: id,
        sensorRuleTypes: sensorRulesList,
      }
      let { resourceType } = this
      if (resourceType === 'asset')
        params = { ...params, sensorModuleId: category.assetModuleID }
      else {
        let { moduleId } = fieldObj
        params = { ...params, sensorModuleId: moduleId }
      }
      let { error } = await API.createRecord('sensorrule', { data: params })
      if (error) {
        this.$message.error('Error Occured ')
      }
    },
    async update(sensorRuleId) {
      let {
        categoryId,
        model,
        sensorRules,
        selectedValues,
        assetCategory,
        selectedValuesFromDb,
      } = this
      let fieldObj = this.$getProperty(model, 'fields.0')
      let category = assetCategory.find(category => category.id === categoryId)
      let { id } = fieldObj || {}
      let sensorRulesList = sensorRules.filter(sensorRule =>
        selectedValues.includes(sensorRule.sensorRuleType)
      )
      selectedValuesFromDb.forEach(val => {
        if (!selectedValues.includes(val.sensorRuleType)) {
          sensorRulesList.push(val)
        }
      })
      sensorRulesList.forEach(ruleType => {
        if (!isEmpty(ruleType.rulePropStr)) {
          delete ruleType.rulePropStr
        }
      })
      let params = {
        assetCategoryId: categoryId,
        sensorFieldId: id,
        id: sensorRuleId,
        sensorRuleTypes: sensorRulesList,
      }
      let { resourceType } = this
      if (resourceType === 'asset')
        params = { ...params, sensorModuleId: category.assetModuleID }
      else {
        let { moduleId } = fieldObj
        params = { ...params, sensorModuleId: moduleId }
      }
      let { error } = await API.updateRecord('sensorrule', {
        data: params,
        id: sensorRuleId,
      })
      if (error) {
        this.$message.error('Error Occured while updating sensor rules')
      }
    },
    showSensorDetection() {
      if (!this.$helpers.isLicenseEnabled('SENSOR_RULE')) {
        return false
      }
      let dataTypeEnumList = ['DECIMAL', 'NUMBER', 'COUNTER']
      let { model } = this
      let { fields } = model || {}
      let fieldObj = fields[0] || {}
      let { dataTypeEnum } = fieldObj || {}
      let dataTypeEnumName = dataTypeEnum._name
      return dataTypeEnumList.includes(dataTypeEnumName)
    },
    removeOption(value, idx) {
      // if (value.id > 0) {
      //   value.visible = false
      // }
      // else {
      //   this.module.fields[0].values.splice(idx, 1)
      // }
      this.module.fields[0].values.splice(idx, 1)
    },
    addOptionsFields(values, index) {
      values.splice(index + 1, 0, {
        value: '',
        visible: true,
      })
    },
    loadUnit(field) {
      if (this.metric > 0) {
        let metric = this.metricsUnits.metrics.filter(d => {
          if (d.metricId === this.metric) {
            return d
          }
        })
        if (metric[0]._name === 'PERCENTAGE') {
          this.metricsUnits.metricWithUnits[metric[0]._name].splice(1, 1)
        }
        this.unitsForMetric = this.metricsUnits.metricWithUnits[metric[0]._name]
      }
      this.module.fields[0].unitId = null
      this.unitId = null
    },
    cancel() {
      this.$emit('canceled')
    },
    loadFormulaField() {
      if (this.model.fields[0].moduleType === 8) {
        // LIVE_FORMULA
        this.isFormulaField = true
        this.$http
          .get('/v2/reading/formula/field/' + this.model.fields[0].id)
          .then(response => {
            if (response.data.responseCode === 0) {
              Object.assign(this.formula, response.data.result.formulaField)
            } else {
              this.$message.error(response.data.message)
            }
          })
      }
    },
    save() {
      this.isSensorUpdateOrAdd()
      if (this.module.fields) {
        let url, params
        let fieldReadingRules = []
        fieldReadingRules.push(
          this.$common.getSafeLimitRules(
            this.module.fields[0],
            this.module.categoryId
          )
        )
        let delReadingRulesIds = []
        if (
          this.module.fields[0].safeLimitPattern === 'none' &&
          this.module.fields[0].safeLimitId &&
          this.module.fields[0].safeLimitId !== -1
        ) {
          delReadingRulesIds.push(this.module.fields[0].safeLimitId)
        }

        let moduleField = this.module.fields[0]
        let field = {
          displayName: moduleField.displayName,
          dataType: moduleField.dataType,
          id: moduleField.id,
        }
        if (moduleField.dataType === 8) {
          field.values = moduleField.values
        } else if (moduleField.dataType === 4) {
          field.trueVal = moduleField.trueVal
          field.falseVal = moduleField.falseVal
        }

        if (this.unitId > 0) {
          field.unitId = this.unitId
        }
        if (this.metric > 0) {
          field.metric = this.metric
        }
        url = '/reading/updatesetupreading'
        params = {
          fieldJson: field,
          moduleId: this.module.fields[0].moduleId,
          delReadingRulesIds,
          fieldReadingRules,
        }

        this.$http.post(url, params).then(response => {
          if (typeof response.data === 'object') {
            this.$message.success('Reading Updated successfully.')
            this.$emit('saved')
            this.closeDialog()
          } else {
            this.$message.error('Reading updation failed.')
          }
        })
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    forceUpdate() {
      this.$forceUpdate()
    },
  },
}
</script>
