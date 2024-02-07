<template>
  <el-dialog
    :visible="visibility"
    class="pivot-add-data-dialog"
    :show-close="false"
    :append-to-body="true"
    :title="$t('pivot.cellVisualization')"
    width="40%"
  >
    <div>
      <div class="body">
        <div class="pivot-cell-visual-dialog-wrapper">
          <div class="pivot-visual-config-section width60">
            <div class="visual-data-column">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.visualType') }}
              </div>
              <el-select
                v-model="visualConfig.visualType"
                filterable
                :default-first-option="true"
                placeholder="Select"
                class="fc-input-full-border-select2  mT5 module-date-field-select width90"
              >
                <el-option
                  :key="'visual-type-option-1'"
                  label="Text"
                  value="text"
                ></el-option>
                <el-option
                  :key="'visual-type-option-2'"
                  label="Data bar"
                  value="data-bar"
                ></el-option>
                <el-option
                  :key="'visual-type-option-3'"
                  label="Compare"
                  value="compare"
                ></el-option>
                <el-option
                  :key="'visual-type-option-4'"
                  label="Circle"
                  value="circle"
                ></el-option>
              </el-select>
            </div>
            <div
              v-if="
                visualConfig.visualType && visualConfig.visualType != 'text'
              "
              class="progress-bar-settings"
            >
              <div class="reference-value-difference  mT10">
                <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                  {{ $t('pivot.compareWith') }}
                </div>
                <el-radio-group
                  v-model="compareWithModel"
                  @change="updateCompareWithValue"
                  class="fc-input-full-border-select2 mT10 module-select"
                >
                  <el-radio-button :label="$t('pivot.maxValue')">
                  </el-radio-button>
                  <el-radio-button :label="$t('pivot.constant')">
                  </el-radio-button>
                  <el-radio-button :label="$t('pivot.referenceColumn')">
                  </el-radio-button>
                </el-radio-group>
              </div>
              <div
                class="reference-data-column"
                v-if="visualConfig.compareWith == 'referenceColumn'"
              >
                <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                  {{ $t('pivot.referenceColumn') }}
                </div>
                <el-select
                  v-model="visualConfig.referenceAlias"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2  mT5 module-date-field-select width90"
                >
                  <el-option
                    v-for="(dataColumn, index) in referenceOptions"
                    :key="'reference-data-field-option' + index"
                    :label="dataColumn.label"
                    :value="dataColumn.alias"
                  ></el-option>
                </el-select>
              </div>
              <div
                v-if="visualConfig.compareWith == 'constant'"
                class="reference-data-column "
              >
                <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                  {{ $t('pivot.constantValue') }}
                </div>
                <el-input
                  v-model="visualConfig.constantValue"
                  placeholder="Constant Value"
                  class="fc-input-full-border2 mT10 width90"
                >
                </el-input>
              </div>
              <el-row class="color-picker-section-fill-color  mT10">
                <el-col v-if="showFillColor" :span="12">
                  <div class="fill-color-wrapper">
                    <div class="fc-grey7-12 f14 text-left line-height25 mb5">
                      {{ $t('pivot.fillColor') }}
                    </div>
                    <div class="fc-color-picker card-color-container mT5">
                      <el-color-picker
                        v-model="visualConfig.fillColor"
                        :predefine="getPredefinedColors()"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col v-if="showTextColor" :span="12">
                  <div class="text-color">
                    <div class="fc-grey7-12 f14 text-left line-height25  mb5">
                      {{ $t('pivot.textColor') }}
                    </div>
                    <div class="fc-color-picker card-color-container mT5">
                      <el-color-picker
                        v-model="visualConfig.textColor"
                        :predefine="getPredefinedColors()"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
              </el-row>
              <el-row
                v-if="showArrowColor"
                class="color-picker-section-arrow-color  mT10"
              >
                <el-col :span="12">
                  <div class="fill-color-wrapper">
                    <div class="fc-grey7-12 f14 text-left line-height25 mb5">
                      {{ $t('pivot.upArrowColor') }}
                    </div>
                    <div class="fc-color-picker card-color-container mT5">
                      <el-color-picker
                        v-model="visualConfig.upArrowColor"
                        :predefine="getPredefinedColors()"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="text-color">
                    <div class="fc-grey7-12 f14 text-left line-height25  mb5">
                      {{ $t('pivot.downArrowColor') }}
                    </div>
                    <div class="fc-color-picker card-color-container mT5">
                      <el-color-picker
                        v-model="visualConfig.downArrowColor"
                        :predefine="getPredefinedColors()"
                      ></el-color-picker>
                    </div>
                  </div>
                </el-col>
              </el-row>
              <div v-if="showValueOption" class="display-value mT10">
                <el-checkbox
                  v-model="visualConfig.showValue"
                  class="fc-grey7-12 f14 text-left line-height25  mT15"
                  >{{ $t('pivot.showValue') }}</el-checkbox
                >
              </div>
            </div>
          </div>
          <div class="pivot-visual-preview-section width40 mT10 mL20">
            <div class="fc-grey7-12 f14 text-left line-height25  mb5">
              {{ $t('pivot.preview') }}
            </div>
            <data-bar-view
              v-if="previewVisualType == 'data-bar'"
              :value="previewValue"
              :referenceValue="previewReferenceValue"
              :visualConfig="visualConfig"
            ></data-bar-view>
            <compare-view
              v-else-if="previewVisualType == 'compare'"
              :value="previewValue"
              :referenceValue="previewReferenceValue"
              :visualConfig="visualConfig"
            />
            <circle-view
              v-else-if="previewVisualType == 'circle'"
              :value="previewValue"
              :referenceValue="previewReferenceValue"
              :visualConfig="visualConfig"
            >
            </circle-view>
            <div v-else class="text-preview">
              {{ previewValue }}
            </div>
          </div>
        </div>
      </div>
      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          {{ $t('pivot.cancel') }}</el-button
        >
        <el-button type="primary" class="modal-btn-save mL0" @click="save">{{
          $t('pivot.done')
        }}</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import colors from 'charts/helpers/colors'
import DataBarView from './TableCell/DataBarView'
import CompareView from './TableCell/CompareView'
import CircleView from './TableCell/CircleView'

export default {
  props: ['alias', 'editConfig', 'visibility', 'referenceDataColumnOptions'],
  components: {
    DataBarView,
    CompareView,
    CircleView,
  },
  data() {
    return {
      visualConfig: {
        visualType: 'text',
        compareWith: 'maxValue',
        referenceAlias: null,
        constantValue: null,
        showValue: false,
        upArrowColor: null,
        downArrowColor: null,
      },
      compareWithModel: 'Max value',
    }
  },
  computed: {
    referenceOptions() {
      return this.referenceDataColumnOptions.filter(
        option => option.alias != this.alias
      )
    },
    showFillColor() {
      return (
        this.visualConfig.visualType &&
        this.visualConfig.visualType != 'text' &&
        this.visualConfig.visualType != 'compare'
      )
    },
    showValueOption() {
      return (
        this.visualConfig.visualType &&
        this.visualConfig.visualType != 'text' &&
        this.visualConfig.visualType != 'compare'
      )
    },
    showTextColor() {
      return this.visualConfig && this.visualConfig.visualType == 'data-bar'
    },
    showArrowColor() {
      return (
        this.visualConfig.visualType &&
        this.visualConfig.visualType == 'compare'
      )
    },
    previewVisualType() {
      return this.visualConfig.visualType
    },
    previewValue() {
      return 7
    },
    previewReferenceValue() {
      return 10
    },
  },
  mounted() {
    if (this.editConfig && this.editConfig.visualType) {
      this.visualConfig = JSON.parse(JSON.stringify(this.editConfig))

      let compareWith = this.visualConfig.compareWith
      if (compareWith == 'maxValue') {
        this.compareWithModel = 'Max value'
      } else if (compareWith == 'constant') {
        this.compareWithModel = 'Constant'
      } else if (compareWith == 'referenceColumn') {
        this.compareWithModel = 'Reference column'
      }
    }
  },
  methods: {
    closeDialog() {
      this.$emit('closeDialog')
    },
    save() {
      this.$emit('cellVisualizationConfigAdded', {
        alias: this.alias,
        visualConfig: { ...this.visualConfig },
      })
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    updateCompareWithValue(label) {
      if (label == 'Max value') {
        this.visualConfig.compareWith = 'maxValue'
      } else if (label == 'Constant') {
        this.visualConfig.compareWith = 'constant'
      } else if (label == 'Reference column') {
        this.visualConfig.compareWith = 'referenceColumn'
      }
    },
  },
}
</script>

<style scoped>
.cfd-select-box {
  display: flex;
  align-items: space-between;
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

.pivot-cell-visual-dialog-wrapper {
  display: flex;
  justify-content: space-around;
}

.pivot-visual-preview-section {
  font-size: 13px;
}
</style>
