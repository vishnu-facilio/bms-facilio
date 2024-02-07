<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog45 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form ref="newAssetReadingForm" :model="module" :label-position="'top'">
      <div class="new-header-container border-none">
        <div class="setup-modal-title">Edit {{ resourceType }} Reading</div>
      </div>
      <!-- <div class="container-scroll"> -->
      <div
        class="setup-formula-block2"
        style="background: #fafcfe;border-bottom: 1px solid #e2e8ee;border-top: 1px solid #e2e8ee;"
      >
        <el-row>
          <el-col :span="10">
            <span class="setup-formula-txt MT20"
              >Is this a formula field ?</span
            >
          </el-col>
          <el-col :span="2">
            <div>{{ isFormulaField ? 'Yes' : 'No' }}</div>
          </el-col>
          <el-col :span="3">
            <el-switch
              :disabled="true"
              v-model="isFormulaField"
              active-color="#ef4f8f"
            ></el-switch>
          </el-col>
        </el-row>
      </div>
      <div style="height: calc(100vh - 220px); overflow-y:scroll">
        <div
          class="setup-formula-block2"
          style="border-top: none;background: #f5f8fb;border-bottom: 1px solid #e2e8ee;padding-top: 20px;padding-bottom: 27px;"
        >
          <el-row>
            <el-col :span="12">
              <p class="label-txt2">Field Name</p>
              <el-input
                style="width: 240px;;background: none;"
                v-model="module.fields[0].displayName"
                class="input-background-remove fc-input-full-border2"
              ></el-input>
            </el-col>
            <el-col :span="8" v-if="categoryName">
              <p class="label-txt-blue">Category</p>
              <p class="label-txt-black">{{ categoryName }}</p>
            </el-col>
            <el-col :span="4" v-if="isFormulaField">
              <p class="label-txt-blue">Unit</p>
              <el-input
                style="width: 240px;background: none;"
                v-model="formulaFieldUnit"
                class="input-background-remove"
              ></el-input>
            </el-col>
            <el-col :span="4" v-if="!isFormulaField">
              <p class="label-txt-blue">Type</p>
              <p class="label-txt-black">{{ module.fields[0].dataTypeEnum }}</p>
            </el-col>
          </el-row>
          <el-row
            class="pT10 pB10"
            v-if="
              (module.fields[0].dataType === 2 ||
                module.fields[0].dataType === 3) &&
                !isFormulaField
            "
          >
            <el-col :span="12">
              <p class="label-txt-blue">Metric</p>
              <el-select
                class="fc-radio-btn fc-radio-btn2 fc-input-full-border2"
                @change="loadUnit(module.fields[0])"
                v-model="module.fields[0].metric"
                style="width: 240px;"
              >
                <el-option
                  v-for="(dtype, index) in metricsUnits.metrics"
                  :key="index"
                  :label="dtype.name"
                  :value="dtype.metricId"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              <p class="label-txt-blue">Unit</p>
              <el-select
                class="fc-radio-btn fc-radio-btn2 fc-input-full-border2"
                v-model="unitId"
                style="width: 240px;"
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
          <el-row v-if="module.fields[0].dataType === 4" class="pT10 pB10">
            <p class="label-txt-blue">Boolean Values</p>
            <div class="row">
              <div style="display: flex;align-items: center;">
                <el-input
                  style="width: 240px;;background: none;"
                  v-model="module.fields[0].trueVal"
                  class="input-background-remove fc-input-full-border2"
                ></el-input>
                <div class="pL5">(+ve)</div>
              </div>
            </div>
            <div class="row pT10">
              <div style="display: flex;align-items: center;">
                <el-input
                  style="width: 240px;;background: none;"
                  v-model="module.fields[0].falseVal"
                  class="input-background-remove fc-input-full-border2"
                ></el-input>
                <div class="pL5">(-ve)</div>
              </div>
            </div>
            <!-- <div>
            <el-input  style="width: 240px;;background: none;" v-model="module.fields[0].trueVal" class="input-background-remove fc-input-full-border2"></el-input>
          </div>
          <el-col :span="12">
            <p class="label-txt-blue">-ve value</p>
            <el-input  style="width: 240px;;background: none;" v-model="module.fields[0].falseVal" class="input-background-remove fc-input-full-border2"></el-input>
          </el-col> -->
          </el-row>
          <el-row v-else-if="module.fields[0].dataType === 8" class="pT10 pB10">
            <!-- <div class="fc-text-pink2 mT20">Options</div> -->
            <p class="label-txt-blue">Pick List Options</p>
            <div
              :key="index"
              v-for="(value, index) in module.fields[0].values"
              style="padding-bottom:10px;"
            >
              <div class="row" v-if="value.visible">
                <div style="padding-left:3px;">
                  <el-input
                    class="input-background-remove fc-input-full-border2"
                    style="width:100%;"
                    type="text"
                    v-model="value.value"
                    placeholder=""
                  ></el-input>
                </div>
                <div
                  style="align-self: center;"
                  class=" pL10 optionremoveicon"
                  @click="removeOption(value)"
                  v-show="module.fields[0].values.length > 2"
                >
                  <img src="~assets/remove-icon.svg" style="height:14px" />
                  <!-- <i class="el-icon-delete"></i> -->
                </div>
                <div
                  style="align-self: center;"
                  class="pL10"
                  @click="addOptionsFields(module.fields[0].values, index)"
                  v-show="index + 1 === module.fields[0].values.length"
                >
                  <img src="~assets/add-icon.svg" style="height:14px" />
                </div>
                <div class="clearboth"></div>
              </div>
            </div>
          </el-row>
        </div>
        <f-formula-builder
          class="reading-formula-builder"
          style="padding: 35px 40px;"
          v-if="isFormulaField"
          v-model="formula.workflow"
          module="formulaField"
          :assetCategory="
            resourceType === 'asset' ? { id: module.categoryId } : null
          "
        ></f-formula-builder>

        <f-safe-limit
          v-model="module.fields[0]"
          :edit="true"
          v-if="
            module.fields[0].dataType === 2 ||
              module.fields[0].dataType === 3 ||
              isFormulaField
          "
          ref="safelimit"
          class="pL40"
        />
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
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import FSafeLimit from '@/FSafeLimit'
import { mapState } from 'vuex'

export default {
  components: { FFormulaBuilder, FSafeLimit },
  props: ['visibility', 'model', 'resourceType', 'unitDetails'],
  data() {
    return {
      metricsUnits: this.unitDetails,
      numberFields: null,
      unitsForMetric: null,
      unitId: null,
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
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  mounted() {
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
      this.model.fields[0].metricEnum = this.model.fields[0].metricEnum
        ? this.model.fields[0].metricEnum
        : null
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
    removeOption(value) {
      value.visible = false
    },
    addOptionsFields(values, index) {
      values.splice(index + 1, 0, { value: '', visible: true })
    },
    loadUnit(field) {
      if (field.metric > 0) {
        let metric = this.metricsUnits.metrics.filter(d => {
          if (d.metricId === field.metric) {
            return d
          }
        })
        if (metric[0]._name === 'PERCENTAGE') {
          this.metricsUnits.metricWithUnits[metric[0]._name].splice(1, 1)
        }
        this.unitsForMetric = this.metricsUnits.metricWithUnits[metric[0]._name]
      }
      this.module.fields[0].unitId = null
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
      if (this.module.fields) {
        let url, params
        let fieldReadingRules = []
        if (this.isFormulaField) {
          this.module.fields.forEach(i => {
            fieldReadingRules.push(
              this.$common.getSafeLimitRules(i, this.module.categoryId)
            )
          })
          let delReadingRulesIds = []
          if (this.module.fields[0]) {
            if (
              this.module.fields[0] &&
              this.module.fields[0].safeLimitPattern === 'none' &&
              this.module.fields[0].safeLimitId &&
              this.module.fields[0].safeLimitId !== -1
            ) {
              delReadingRulesIds.push(this.module.fields[0].safeLimitId)
            }
          }
          url = '/reading/updateformula'
          params = {
            formula: { id: this.formula.id, workflow: this.formula.workflow },
            moduleId: this.module.fields[0]
              ? this.module.fields[0].moduleId
              : this.module.numberFields[0].moduleId,
            formulaFieldUnit: this.formulaFieldUnit,
            delReadingRulesIds,
            fieldReadingRules,
          }
          if (
            this.module.fields[0].displayName !==
            this.model.fields[0].displayName
          ) {
            params.formula.name = this.module.fields[0].displayName
          }
        } else {
          this.module.fields.forEach(i => {
            fieldReadingRules.push(
              this.$common.getSafeLimitRules(i, this.module.categoryId)
            )
          })
          let delReadingRulesIds = []
          if (this.module.fields[0]) {
            if (
              this.module.fields[0] &&
              this.module.fields[0].safeLimitPattern === 'none' &&
              this.module.fields[0].safeLimitId &&
              this.module.fields[0].safeLimitId !== -1
            ) {
              delReadingRulesIds.push(this.module.fields[0].safeLimitId)
            }
            // if (this.module.fields[0].dataType === 2 || this.module.fields[0].dataType === 3) {
            //   this.numberFields = this.module.fields[0]
            // }
          }

          for (let i = 0; i < this.module.fields.length; i++) {
            delete this.module.fields[i].readingRules
          }
          if (this.unitId > 0) {
            this.module.fields[0].unitId = this.unitId
          }
          url = '/reading/updatesetupreading'
          params = {
            fieldJson: this.module.fields[0],
            moduleId: this.module.fields[0]
              ? this.module.fields[0].moduleId
              : this.module.numberFields[0].moduleId,
            delReadingRulesIds,
            fieldReadingRules,
          }
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
    clearOthers(safeLimitPattern) {
      if (safeLimitPattern === 'none') {
        this.module.fields[0].lesserThan = null
        this.module.fields[0].greaterThan = null
        this.module.fields[0].betweenFrom = null
        this.module.fields[0].betweenTo = null
      } else if (safeLimitPattern === 'lesser') {
        this.module.fields[0].greaterThan = null
        this.module.fields[0].betweenFrom = null
        this.module.fields[0].betweenTo = null
      } else if (safeLimitPattern === 'greater') {
        this.module.fields[0].lesserThan = null
        this.module.fields[0].betweenFrom = null
        this.module.fields[0].betweenTo = null
      } else if (safeLimitPattern === 'between') {
        this.module.fields[0].lesserThan = null
        this.module.fields[0].greaterThan = null
      }
      this.$forceUpdate()
    },
  },
}
</script>
<style>
.new-header-container {
  margin-top: 0 !important;
}
.input-background-remove .el-input__inner {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #333333;
}
.reading-formula-builder {
  overflow-y: scroll !important;
  height: calc(100vh - 200px) !important;
  padding-bottom: 100px !important;
}
</style>
