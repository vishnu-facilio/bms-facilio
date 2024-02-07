<template>
  <div class="pivot-add-data">
    <el-dialog
      :visible="visibility"
      class="pivot-add-data-dialog"
      :show-close="false"
      :append-to-body="true"
      :title="$t('pivot.addData')"
      width="50%"
    >
      <div>
        <spinner v-if="loading" :show="loading" size="80" class=""></spinner>
        <div
          class="body"
          v-if="!loading && pivotBaseModule && pivotBaseModule.displayName"
        >
          <div v-if="canShowReadingDataOptions">
            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              {{ $t('pivot.dataModuleType') }}
            </div>

            <div class="choose-data-module-type mT5">
              <el-radio
                v-model="dataModuleType"
                class="fc-radio-btn"
                :label="PIVOT_DATA_MODULE_TYPE.MODULE"
                @change="changeDataModuleType"
                :disabled="isEditing"
                >{{ $t('pivot.module') }}</el-radio
              >
              <el-radio
                v-model="dataModuleType"
                class="fc-radio-btn"
                :label="PIVOT_DATA_MODULE_TYPE.READING"
                @change="changeDataModuleType"
                :disabled="isEditing"
                >{{ $t('pivot.reading') }}</el-radio
              >
            </div>
          </div>
          <div
            v-if="dataModuleType == PIVOT_DATA_MODULE_TYPE.MODULE"
            :key="'module-section-' + refreshKey"
          >
            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              {{ $t('pivot.module') }}
            </div>

            <el-select
              v-model="nonReadingModule"
              filterable
              placeholder="Select"
              class="fc-input-full-border-select2 width60 mT5 module-select"
              popper-class="fc-group-select"
              value-key="name"
              :disabled="isEditing"
              @change="changeNonReadingModule"
            >
              <el-option-group :label="$t('pivot.mainModule')">
                <el-option
                  :label="pivotBaseModule.displayName"
                  :value="pivotBaseModule"
                ></el-option>
              </el-option-group>
              <el-option-group :label="$t('pivot.subModules')">
                <el-option
                  v-for="(submodule, index) in submoduleOptions"
                  :key="'non-reading-module-options' + index"
                  :label="submodule.displayName"
                  :value="submodule"
                ></el-option>
              </el-option-group>
            </el-select>
            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              {{ $t('pivot.metric') }}
            </div>

            <div class="width60 align-center metric-aggr-section">
              <el-row>
                <el-col :span="18">
                  <el-select
                    v-model="metricField"
                    filterable
                    placeholder="Select"
                    class="fc-input-full-border-select2 width100  mT5  flex-grow metric-select"
                    value-key="completeColumnName"
                    :disabled="isEditing"
                    @change="initColumnName"
                  >
                    <el-option
                      v-for="(metric, index) in metricOptions"
                      :key="'metric-option' + index"
                      :label="metric.displayName"
                      :value="metric"
                    ></el-option>
                  </el-select>
                </el-col>
                <el-col :span="6"
                  ><el-select
                    v-model="metricAggregation"
                    :disabled="metricAggregationDisabled"
                    placeholder="Select"
                    class="fc-input-full-border-select2 width100 mL5 mT5 reading-field-select"
                  >
                    <el-option
                      v-for="(option, index) in aggrOptions"
                      :key="'aggr-option' + index"
                      :label="option.label"
                      :value="option.value"
                    ></el-option> </el-select
                ></el-col>
              </el-row>
            </div>
            <div v-if="!isEmpty(metricField)">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.columnName') }}
              </div>

              <el-input
                v-model="columnName"
                placeholder="Rename"
                class="fc-input-full-border2 width60 mT5 "
              >
              </el-input>
            </div>
            <div v-if="moduleSelected">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.dateField') }}
              </div>

              <el-select
                v-model="dateFieldId"
                filterable
                placeholder="Select"
                class="fc-input-full-border-select2 width60 mT5 module-date-field-select"
              >
                <el-option
                  v-for="(dateField, index) in dateFieldOptions"
                  :key="'module-date-field-option' + index"
                  :label="dateField.displayName"
                  :value="dateField.id"
                ></el-option>
              </el-select>
              <el-row class="width60">
                <el-col :span="12">
                  <div v-if="dateFieldId != -99">
                    <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                      {{ $t('pivot.datePeriod') }}
                    </div>

                    <el-select
                      v-model="moduleDatePeriod"
                      filterable
                      placeholder="Select"
                      class="fc-input-full-border-select2 mT5 module-date-period-select"
                      style="width: 95%;"
                    >
                      <el-option
                        v-for="(datePeriod, index) in datePeriodOptions"
                        :key="'module-date-period' + index"
                        :label="datePeriod.label"
                        :value="datePeriod.dateOperator"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
                <el-col :span="12" style="float: right;">
                  <div v-if="dateFieldId != -99">
                    <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                      {{ $t('common.header.base_line') }}
                    </div>
                    <el-select
                      v-model="baselinePeriod"
                      filterable
                      clearable
                      placeholder="Baseline"
                      class="fc-input-full-border-select2 width100 mT5 module-date-period-select"
                      style="width: 95%;"
                    >
                      <el-option
                        v-for="(datePeriod, index) in baselinePeriodOptions"
                        :key="'module-base-period' + index"
                        :label="datePeriod.name"
                        :value="datePeriod.name"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
              </el-row>
              <div v-if="moduleDatePeriod == 20">
                <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                  {{ $t('pivot.customRange') }}
                </div>
                <el-date-picker
                  v-model="customDateRange"
                  type="datetimerange"
                  placeholder="Select start date and time"
                  class="width60 fc-input-full-border2 mT5"
                  value-format="timestamp"
                >
                </el-date-picker>
              </div>
              <div v-if="dateFieldId != -99">
                <el-checkbox
                  v-model="excludeFromTimelineFilter"
                  class="width60 mT15"
                  >{{ $t('pivot.excludeFromTLF') }}</el-checkbox
                >
              </div>
              <div class="data-criteria mT30">
                <new-criteria-builder
                  :lookupModuleFieldsList="nonReadingModuleFields"
                  :lookupFieldModuleName="nonReadingModule.name"
                  :key="refreshKey"
                  title="Show options that match"
                  class="stateflow-criteria"
                  ref="criteriaBuilder"
                  v-model="criteria"
                  :exrule="criteria"
                  :module="nonReadingModule.name"
                  @condition="updateCriteria"
                ></new-criteria-builder>
              </div>
            </div>
          </div>
          <div
            v-else-if="dataModuleType == PIVOT_DATA_MODULE_TYPE.READING"
            :key="'reading-section' + refreshKey"
          >
            <div
              class="asset-cat-reading-field-section"
              v-if="showAssetReadings"
            >
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.assetCategory') }}
              </div>

              <el-select
                v-model="assetCategoryIndex"
                filterable
                placeholder="Select"
                class="fc-input-full-border-select2 width60 mT5 asset-cat-select"
                value-key="name"
                :disabled="isEditing"
                @change="changeAssetCategoryEvent"
              >
                <el-option
                  v-for="(option, index) in assetCategoryOptions || []"
                  :key="'asset-category-option' + index"
                  :label="option.displayName"
                  :value="index"
                ></el-option>
              </el-select>
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.readingField') }}
              </div>
              <div class="width60 align-center metric-aggr-section">
                <el-row>
                  <el-col :span="18">
                    <el-select
                      @change="initColumnName"
                      v-model="readingField"
                      filterable
                      placeholder="Select"
                      class="fc-input-full-border-select2 width100 mT5 reading-field-select"
                      :disabled="isEditing"
                      value-key="name"
                    >
                      <el-option
                        v-for="(option, index) in readingFieldOptions || []"
                        :key="'reading-field-option' + index"
                        :label="option.displayName"
                        :value="option"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="6">
                    <el-select
                      v-model="metricAggregation"
                      placeholder="Select"
                      class="fc-input-full-border-select2 width100 mL5 mT5 reading-field-select"
                    >
                      <el-option
                        v-for="(option, index) in aggrOptions"
                        :key="'aggr-option' + index"
                        :label="option.label"
                        :value="option.value"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div
              class="site-floor-buinding-reading-section"
              v-if="showSiteBuildingFloorReadings"
            >
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.readingField') }}
              </div>
              <div class="width60 align-center metric-aggr-section">
                <el-row>
                  <el-col :span="18">
                    <el-select
                      @change="initColumnName"
                      v-model="readingField"
                      filterable
                      placeholder="Select"
                      class="fc-input-full-border-select2 width100 mT5 reading-field-select"
                      :disabled="isEditing"
                      value-key="name"
                    >
                      <el-option
                        v-for="(option, index) in readingFieldOptions || []"
                        :key="'reading-field-option' + index"
                        :label="option.displayName"
                        :value="option"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="6">
                    <el-select
                      v-model="metricAggregation"
                      placeholder="Select"
                      class="fc-input-full-border-select2 width100 mL5 mT5 reading-field-select"
                    >
                      <el-option
                        v-for="(option, index) in aggrOptions"
                        :key="'aggr-option' + index"
                        :label="option.label"
                        :value="option.value"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div class="space-reading-field-section" v-if="showSpaceReadings">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.assetCategory') }}
              </div>
              <el-select
                v-model="spaceCategoryId"
                filterable
                placeholder="Select"
                class="fc-input-full-border-select2 width60 mT5 asset-cat-select"
                value-key="name"
                :disabled="isEditing"
                @change="changeSpaceCategoryEvent"
              >
                <el-option label="All Categories" :value="-1"></el-option>
                <el-option
                  v-for="(option, index) in spaceCategoryOptions || []"
                  :key="'space-category-option' + index"
                  :label="option.name"
                  :value="option.id"
                ></el-option>
              </el-select>
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.readingField') }}
              </div>
              <div class="width60 align-center metric-aggr-section">
                <el-row>
                  <el-col :span="18">
                    <el-select
                      @change="initColumnName"
                      v-model="readingField"
                      filterable
                      placeholder="Select"
                      class="fc-input-full-border-select2 width100 mT5 reading-field-select"
                      :disabled="isEditing"
                      value-key="name"
                    >
                      <el-option
                        v-for="(option, index) in readingFieldOptions || []"
                        :key="'reading-field-option' + index"
                        :label="option.displayName"
                        :value="option"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="6">
                    <el-select
                      v-model="metricAggregation"
                      placeholder="Select"
                      class="fc-input-full-border-select2 width100 mL5 mT5 reading-field-select"
                    >
                      <el-option
                        v-for="(option, index) in aggrOptions"
                        :key="'aggr-option' + index"
                        :label="option.label"
                        :value="option.value"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div v-if="!isEmpty(readingField)">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.columnName') }}
              </div>

              <el-input
                v-model="columnName"
                placeholder="Rename"
                class="fc-input-full-border2 width60 mT5 "
              >
              </el-input>
            </div>
            <el-row class="width60">
              <el-col :span="12">
                <div>
                  <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                    {{ $t('pivot.datePeriod') }}
                  </div>
                  <el-select
                    v-model="readingDatePeriod"
                    filterable
                    placeholder="Select"
                    class="fc-input-full-border-select2 mT5 reading-date-period-select"
                    style="width: 95%;"
                  >
                    <el-option
                      v-for="(readingDatePeriod, index) in datePeriodOptions"
                      :key="'reading-date-period' + index"
                      :label="readingDatePeriod.label"
                      :value="readingDatePeriod.dateOperator"
                    ></el-option>
                  </el-select>
                </div>
              </el-col>
              <el-col :span="12" style="float: right;">
                <div>
                  <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                    {{ $t('common.header.base_line') }}
                  </div>
                  <el-select
                    v-model="baselinePeriod"
                    filterable
                    clearable
                    placeholder="Baseline"
                    class="fc-input-full-border-select2 width100 mT5 module-date-period-select"
                    style="width: 95%;"
                  >
                    <el-option
                      v-for="(datePeriod, index) in baselinePeriodOptions"
                      :key="'module-base-period' + index"
                      :label="datePeriod.name"
                      :value="datePeriod.name"
                    ></el-option>
                  </el-select>
                </div>
              </el-col>
            </el-row>
            <div v-if="readingDatePeriod == 20">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.customRange') }}
              </div>
              <el-date-picker
                v-model="customDateRange"
                type="datetimerange"
                placeholder="Select start date and time"
                class="width60 fc-input-full-border2 mT5"
                style="width: 60% !important; border-radius: 5px !important;"
                value-format="timestamp"
              >
              </el-date-picker>
            </div>
            <div>
              <el-checkbox
                v-model="excludeFromTimelineFilter"
                class="width60 mT15"
                >{{ $t('pivot.excludeFromTLF') }}</el-checkbox
              >
            </div>
          </div>
        </div>
        <div class="dialog-save-cancel">
          <el-button class="modal-btn-cancel" @click="closeDialog">
            {{ $t('pivot.cancel') }}
          </el-button>
          <el-button type="primary" class="modal-btn-save mL0" @click="save">
            {{ $t('pivot.done') }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { isDateTypeField } from '@facilio/utils/field'
import { mapActions, mapState } from 'vuex'
import { defaultColFormat, datePeriodOptions } from './PivotDefaults'
const PIVOT_DATA_MODULE_TYPE = {
  MODULE: 1,
  READING: 2,
}
const readingSupportedModules = ['asset', 'site', 'building', 'floor', 'space']
export default {
  props: [
    'visibility',
    'pivotBaseModuleName',
    'editConfig',
    'index',
    'formatConfig',
    'referenceDataColumnOptions',
  ],
  components: {
    NewCriteriaBuilder,
  },
  computed: {
    ...mapState({ assetCategoryOptions: 'assetCategory' }),
    ...mapState({ spaceCategoryOptions: 'spaceCategory' }),
    dateFieldOptions() {
      let options = []
      options.push({ id: -99, displayName: 'NONE' })
      let dateFields = this.nonReadingModuleFields.filter(field => {
        return isDateTypeField(field)
      })
      options.push(...dateFields)
      return options
    },
    moduleSelected() {
      return !isEmpty(this.nonReadingModule)
    },
    canShowReadingDataOptions() {
      return readingSupportedModules.includes(this.pivotBaseModuleName)
    },
    metricAggregationDisabled() {
      if (
        this.metricField &&
        this.metricField.name &&
        this.metricField.name != 'id'
      ) {
        return false
      } else {
        return true
      }
    },
    isEditing() {
      return !isEmpty(this.editConfig)
    },
    showAssetReadings() {
      return this.pivotBaseModuleName == 'asset'
    },
    showSiteBuildingFloorReadings() {
      return ['site', 'building', 'floor'].includes(this.pivotBaseModuleName)
    },
    showSpaceReadings() {
      return this.pivotBaseModuleName == 'space'
    },
  },

  data() {
    return {
      PIVOT_DATA_MODULE_TYPE: PIVOT_DATA_MODULE_TYPE,
      refreshKey: 0,
      nonReadingModule: {}, //moduleType->module
      nonReadingModuleFields: [],
      readingModule: {},
      baselinePeriodOptions: {},
      baselinePeriod: null,
      dataModuleType: PIVOT_DATA_MODULE_TYPE.MODULE,
      pivotBaseModule: null,
      submoduleOptions: [],
      metricField: {},
      metricAggregation: 3,
      metricOptions: [],
      criteria: null,
      excludeFromTimelineFilter: false,
      dateFieldId: -99,
      customDateRange: [],
      columnName: null,
      //Readings options
      assetCategory: {},
      spaceCategoryId: -1,
      readingFieldOptions: [],
      readingField: {},
      datePeriodOptions,
      loading: true,
      assetCategoryIndex: 0,
      moduleDatePeriod: 28,
      readingDatePeriod: 28,
      aggrOptions: [
        {
          label: 'COUNT',
          value: 1,
        },
        {
          label: 'AVG',
          value: 2,
        },
        {
          label: 'SUM',
          value: 3,
        },
        {
          label: 'MIN',
          value: 4,
        },
        {
          label: 'MAX',
          value: 5,
        },
      ],
    }
  },

  created() {
    this.initSelectStatements()
  },
  methods: {
    isEmpty,
    ...mapActions(['loadAssetCategory', 'loadSpaceCategory']),
    initColumnName(field) {
      this.columnName = field.displayName
      if (this.metricField.name == 'id' && !this.editConfig) {
        //for id fields only COUNT is allowed
        this.metricAggregation = 1
      }
    },
    initProps() {
      this.dataModuleType = parseInt(this.editConfig.moduleType)
      this.baselinePeriod = this.editConfig.baselineLabel
      this.excludeFromTimelineFilter = this.editConfig.excludeFromTimelineFilter
      this.customDateRange = [
        this.editConfig.startTime,
        this.editConfig.endTime,
      ]

      if (this.dataModuleType == 2) {
        if (this.pivotBaseModuleName == 'asset') {
          this.assetCategoryIndex = this.assetCategoryOptions.findIndex(
            at => at.assetModuleID == this.editConfig.parentModuleId
          )
          this.assetCategory = this.assetCategoryOptions[
            this.assetCategoryIndex
          ]
          this.changeAssetCategory().then(() => {
            this.readingField = this.readingFieldOptions.find(
              rf => rf.id == this.editConfig.readingField.id
            )
          })
        } else if (
          ['site', 'floor', 'building'].includes(this.pivotBaseModuleName)
        ) {
          this.readingField = this.readingFieldOptions.find(
            rf => rf.id == this.editConfig.readingField.id
          )
        } else if (this.pivotBaseModuleName == 'space') {
          this.loadSpaceCategory().then(() => {
            let spaceCategory = this.spaceCategoryOptions.find(
              sp => sp.id == this.editConfig.parentModuleId
            )
            this.spaceCategoryId = spaceCategory?.id ? spaceCategory?.id : -1
            this.loadSpaceReadingFields(this.spaceCategoryId).then(() => {
              this.readingField = this.readingFieldOptions.find(
                rf => rf.id == this.editConfig.readingField.id
              )
            })
          })
        }

        this.readingDatePeriod = this.editConfig.datePeriod
        this.columnName = this.formatConfig.label
          ? this.formatConfig.label
          : this.editConfig.field.displayName
      } else {
        if (this.editConfig.moduleName != this.pivotBaseModuleName) {
          this.nonReadingModule = this.submoduleOptions.find(
            submodule => submodule.name == this.editConfig.moduleName
          )
        } else {
          this.nonReadingModule = this.pivotBaseModule.module
        }
        this.changeNonReadingModule().then(() => {
          if (this.editConfig.field.id == -1) {
            this.metricField = this.metricOptions.find(
              fd => fd.name == this.editConfig.field.name
            )
          } else {
            this.metricField = this.nonReadingModuleFields.find(
              fd => fd.id == this.editConfig.field.id
            )
          }
          this.dateFieldId = this.editConfig.dateFieldId
          this.moduleDatePeriod = this.editConfig.datePeriod
          this.columnName = this.formatConfig.label
            ? this.formatConfig.label
            : this.editConfig.field.displayName
          this.criteria = this.editConfig.criteria
        })
      }
      this.metricAggregation = this.editConfig.aggr
    },
    async initSelectStatements() {
      await this.loadNonReadingModuleOptions()
      await this.loadPivotBaseModuleMeta()
      await this.loadMetricOptions()
      await this.loadNonReadingModuleFields()
      await this.loadBaselineOptions()

      if (this.pivotBaseModuleName == 'asset') {
        await this.loadAssetCategory()
      } else if (
        ['site', 'building', 'floor'].includes(this.pivotBaseModuleName)
      ) {
        await this.loadSiteBuildingFloorReadings(this.pivotBaseModuleName)
      } else if (this.pivotBaseModuleName == 'space') {
        await this.loadSpaceCategory()
      }

      if (this.editConfig) {
        this.initProps()
      }
    },
    async loadNonReadingModuleFields() {
      let { data, error } = await API.get(
        `v2/modules/meta/${this.nonReadingModule.name}`
      )

      if (error) {
        this.$message.error('Error loading all fields for module')
      } else {
        this.nonReadingModuleFields = data.meta.fields
      }
    },

    async loadAssetReadingFields() {
      let data1 = await API.get('v2/readings/assetcategory', {
        id: this.assetCategory.id,
        excludeEmptyFields: true,
        fetchValidationRules: false,
        readingType: 'connected',
      })
      let data2 = await API.get('v2/readings/assetcategory', {
        id: this.assetCategory.id,
        excludeEmptyFields: true,
        fetchValidationRules: false,
        readingType: 'available',
      })
      if (data1.data.error || data2.data.error) {
        this.$message.error('error loading reading fields for asset category')
      } else {
        this.readingFieldOptions = [
          ...data1.data.readings,
          ...data2.data.readings,
        ]
      }
    },
    //not used
    async loadSiteBuildingFloorReadings(moduleName) {
      let { data, error } = await API.post('reading/getallspacetypereadings', {
        spaceType: this.getSpaceType(moduleName),
      })
      if (error) {
        this.$message.error(`error loading reading fields for ${moduleName}`)
      } else {
        this.readingFieldOptions = this.getSiteReadingFields(data)
      }
    },
    getSiteReadingFields(data) {
      let fields = []
      let keys = Object.keys(data.moduleMap)
      keys.forEach(key => {
        fields.push(...data.moduleMap[key])
      })
      return fields
    },
    getSpaceType(moduleName) {
      switch (moduleName) {
        case 'site':
          return 'Sites'

        case 'building':
          return 'Buildings'

        case 'floor':
          return 'Floors'
      }
    },
    changeSpaceCategoryEvent(value) {
      this.spaceCategoryId = value
      this.loadSpaceReadingFields(value)
    },
    async loadSpaceReadingFields(id) {
      this.readingFieldOptions = []
      let data1 = await API.get('v2/readings/spacecategory', {
        id: id,
        readingType: 'available',
      })
      let data2 = await API.get('v2/readings/spacecategory', {
        id: id,
        readingType: 'connected',
      })
      this.readingFieldOptions = [
        ...data1.data.readings,
        ...data2.data.readings,
      ]
    },
    async changeNonReadingModule() {
      if (
        this.dataModuleType == PIVOT_DATA_MODULE_TYPE.MODULE &&
        !isEmpty(this.nonReadingModule)
      ) {
        this.metricOptions = []
        this.nonReadingModuleFields = []
        await this.loadMetricOptions()
        await this.loadNonReadingModuleFields()
      }
    },
    async loadBaselineOptions() {
      let { data } = await API.get('/baseline/all')
      if (data) {
        this.baselinePeriodOptions = JSON.parse(JSON.stringify(data))
      }
    },
    changeAssetCategoryEvent(value) {
      this.assetCategory = this.assetCategoryOptions[value]
      this.changeAssetCategory()
    },
    async changeAssetCategory() {
      if (!isEmpty(this.assetCategory)) {
        this.readingField = {}
        await this.loadAssetReadingFields()
      }
    },
    updateCriteria(newValue) {
      if (this.isValidCriteria(newValue)) {
        this.criteria = newValue
      }
    },
    isValidCriteria(criteria) {
      if (!criteria.conditions) {
        return false
      } //all conditions empty ,invalid criteria
      let areAllConditionsEmpty = false

      areAllConditionsEmpty = Object.keys(criteria.conditions).every(
        conditionKey => {
          let condition = criteria.conditions[conditionKey]
          return isEmpty(condition)
        }
      )

      return !areAllConditionsEmpty
    },
    async loadMetricOptions() {
      let resp = await API.get('v2/report/getMetricsList', {
        moduleName: this.nonReadingModule.name,
      })
      let { data, error } = resp
      if (error) {
        this.$message.error('Error loading metric list')
      } else {
        this.metricOptions = data.Fields

        let idMetric = this.metricOptions.find(
          metricField => metricField.dataTypeEnum == 'ID'
        )
        if (idMetric) {
          this.metricField = idMetric
          this.initColumnName(this.metricField)
        }
        //initialize metric dropdown to ID metric
      }
    },
    async loadPivotBaseModuleMeta() {
      let moduleMetaResp = await API.get(
        `v2/modules/meta/${this.pivotBaseModuleName}`
      )
      let { data, error } = moduleMetaResp
      if (error) {
        this.$message.error('Error fetching base module meta')
      } else {
        this.pivotBaseModule = JSON.parse(JSON.stringify(data.meta))
        this.nonReadingModule = this.pivotBaseModule
      }
    },
    changeDataModuleType() {
      if (!this.isEditing) this.resetDefauts()
      if (this.dataModuleType == PIVOT_DATA_MODULE_TYPE.MODULE) {
        this.loadNonReadingModuleOptions()
      } else if (this.pivotBaseModuleName == 'asset') {
        this.changeAssetCategoryEvent(0)
      } else if (
        ['site', 'building', 'floor'].includes(this.pivotBaseModuleName)
      ) {
        this.loadSiteBuildingFloorReadings(this.pivotBaseModuleName)
      } else if (this.pivotBaseModuleName == 'space') {
        this.loadSpaceCategory()
      }
    },
    async loadNonReadingModuleOptions() {
      let resp = await API.get('/v2/report/getDataModuleList', {
        moduleName: this.pivotBaseModuleName,
      })

      let { data, error } = resp
      if (error) {
        this.$message.error('Error loading  module list')
      } else {
        let submodules = data.modules
        //module submodule up to itself case, wo -parent wo , remove the submodule here

        this.submoduleOptions = submodules.filter(
          module => module.name != this.pivotBaseModuleName
        )
      }
      this.loading = false
    },
    resetDefauts() {
      this.refreshKey++
      this.nonReadingModule = {}
      this.submoduleOptions = []
      this.metricField = {}
      this.metricAggregation = 3
      this.metricOptions = []
      this.criteria = null
      this.dateFieldId = -99
      this.nonReadingModuleFields = []
      this.moduleDatePeriod = 28
      this.readingDatePeriod = 28
      this.readingModule = {}
      this.readingField = {}
      this.readingFieldOptions = []
      this.assetCategory = {}
    },
    closeDialog() {
      this.$emit('updateCancel')
      this.$emit('update:visibility', false)
    },
    getParentModuleId() {
      if (this.pivotBaseModuleName == 'asset') {
        return this.assetCategory.assetModuleID
      } else if (this.pivotBaseModuleName == 'space') {
        return this.spaceCategoryId
      }
    },
    save() {
      let params = {
        moduleType: this.dataModuleType,
        aggr: this.metricAggregation,
        baselineLabel: this.baselinePeriod,
        excludeFromTimelineFilter: this.excludeFromTimelineFilter,
      }
      if (this.customDateRange.length > 0) {
        params = {
          ...params,
          startTime: this.customDateRange[0],
          endTime: this.customDateRange[1],
        }
      }
      if (this.dataModuleType == PIVOT_DATA_MODULE_TYPE.MODULE) {
        params = {
          ...params,

          ...{
            field: this.metricField,
            moduleName: this.nonReadingModule.name,
            dateFieldId: this.dateFieldId,
            datePeriod: this.moduleDatePeriod,
            criteria: this.criteria,
          },
        }
      } else if (this.dataModuleType == PIVOT_DATA_MODULE_TYPE.READING) {
        params = {
          ...params,

          ...{
            readingField: this.readingField,
            datePeriod: this.readingDatePeriod,
            parentModuleId: this.getParentModuleId(),
          },
        }
      }

      if (
        this.pivotBaseModule.name !== this.nonReadingModule.name &&
        this.dataModuleType == PIVOT_DATA_MODULE_TYPE.MODULE
      ) {
        params = {
          ...params,
          subModuleFieldId: this.metricField.id,
        }
      }
      let formatting = {
        ...defaultColFormat.dataColumn,
        label: this.columnName,
      }

      if (this.editConfig) {
        params.alias = this.editConfig.alias
        this.$emit('update', {
          params: { data: params, formatting },
          index: this.index,
        })
      } else {
        this.$emit('save', { data: params, formatting })
      }
    },
  },
}
</script>
<style lang="scss">
.pivot-add-data-dialog {
  .body {
    height: 450px;
    overflow: scroll;
  }
  .el-date-editor--datetimerange.el-input__inner {
    width: 60% !important;
    background: none !important;
    border-radius: 3px !important;
    height: 40px;
    line-height: 40px;
    padding-left: 15px;
    padding-right: 15px;
    font-size: 14px;
    letter-spacing: 0.4px;
    color: #333333;
    text-overflow: ellipsis;
    font-weight: 400;
    white-space: nowrap;
  }
}
</style>
