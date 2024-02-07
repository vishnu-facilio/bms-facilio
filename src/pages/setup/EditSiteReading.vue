<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog45 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form ref="newAssetReadingForm" :model="module" :label-position="'top'">
      <div class="new-header-container border-none">
        <div class="setup-modal-title">Edit {{ resourceType }} Reading</div>
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
            <el-col :span="4">
              <p class="label-txt-blue">Type</p>
              <p class="label-txt-black">
                {{
                  module.fields[0].counterField
                    ? 'Counter'
                    : $constants.dataType[module.fields[0].dataType]
                }}
              </p>
            </el-col>
          </el-row>
          <el-row
            class="pT10 pB10"
            v-if="
              module.fields[0].dataType === 2 || module.fields[0].dataType === 3
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
                v-model="module.fields[0].unitId"
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
          </el-row>
          <el-row v-else-if="module.fields[0].dataType === 8" class="pT10 pB10">
            <!-- <div class="fc-text-pink2 mT20">Options</div> -->
            <p class="label-txt-blue">Pick List Options</p>
            <div
              :key="option"
              v-for="(option, index) in module.fields[0].options"
              style="padding-bottom:10px;"
            >
              <div class="row">
                <div style="padding-left:3px;">
                  <el-input
                    class="input-background-remove fc-input-full-border2"
                    style="width:100%;"
                    type="text"
                    v-model="option.name"
                    placeholder=""
                  ></el-input>
                </div>
                <div
                  style="align-self: center;"
                  class=" pL10 optionremoveicon"
                  @click="removeOption(module.fields[0].options, index)"
                  v-show="module.fields[0].options.length > 2"
                >
                  <img src="~assets/remove-icon.svg" style="height:14px" />
                  <!-- <i class="el-icon-delete"></i> -->
                </div>
                <div
                  style="align-self: center;"
                  class="pL10"
                  @click="addOptionsFields(module.fields[0].options, index)"
                  v-show="index + 1 === module.fields[0].options.length"
                >
                  <img src="~assets/add-icon.svg" style="height:14px" />
                </div>
                <div class="clearboth"></div>
              </div>
            </div>
          </el-row>
        </div>
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
import { mapState } from 'vuex'
export default {
  props: ['visibility', 'model', 'resourceType', 'unitDetails'],
  data() {
    return {
      metricsUnits: this.unitDetails,
      numberFields: null,
      unitsForMetric: null,
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
    }
  },

  created() {
    this.$store.dispatch('loadAssetCategory')
  },

  mounted() {
    if (this.model) {
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
      if (this.model.fields[0].dataType === 8) {
        this.model.fields[0].options = []
        this.model.fields[0].values.forEach(d => {
          this.model.fields[0].options.push({ name: d })
        })
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
          return category.name
        }
      }
      return ''
    },
  },
  methods: {
    removeOption(list, index) {
      list.splice(index, 1)
      this.$forceUpdate()
    },
    addOptionsFields(field) {
      field.push({ name: '' })
      this.$forceUpdate()
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
    save() {
      if (this.module.fields) {
        let url, params
        if (this.module.fields[0].dataType === 8) {
          if (this.module.fields[0].dataType === 8) {
            this.module.fields[0].values = []
            if (this.module.fields[0].options) {
              this.module.fields[0].options.forEach(d => {
                if (d.name) {
                  this.module.fields[0].values.push(d.name ? d.name : null)
                }
              })
            }
          }
        }
        this.module.fields[0].readingRules = []
        url = '/reading/updatesetupreading'
        params = {
          fieldJson: this.module.fields[0],
          moduleId: this.module.fields[0]
            ? this.module.fields[0].moduleId
            : this.module.numberFields[0].moduleId,
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
