<template>
  <div class="p30">
    <div class="header">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'KPI Layout' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20">
      <div class="section config-panel panel-scroll">
        <el-form
          :model="cardDataObj"
          :ref="`${this.cardLayout}_form`"
          :rules="validationRules"
          :label-position="'top'"
        >
          <el-tabs v-model="activeTab" class="card-tab-fixed">
            <el-tab-pane label="Config" name="config">
              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="title" class="mB10">
                    <p class="fc-input-label-txt pB5">Title</p>
                    <el-input
                      :autofocus="isNew"
                      v-model="cardDataObj.title"
                      class="width100 fc-input-full-border2"
                      @change="cardStateObj.expression.name = cardDataObj.title"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <!-- <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="reading" class="mB10">
                    <p class="fc-input-label-txt pB5">Type</p>
                    <el-select
                      v-model="cardDataObj.kpiType"
                      placeholder="Please select a period"
                      class="width100 el-input-textbox-full-border"
                      @change="setDateRange"
                    >
                      <template v-for="(value, key) in kpiTypes">
                        <el-option
                          :label="key"
                          :value="value"
                          :key="key"
                        ></el-option>
                      </template>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row> -->
              <template v-if="cardDataObj.kpiType === 'reading'">
                <kpi-reading-picker
                  :initialReading="cardDataObj.kpi"
                  @onReadingSelect="reading => setReading('kpi', reading)"
                ></kpi-reading-picker>

                <el-row class="mB10">
                  <el-col :span="24">
                    <el-form-item prop="dateRange" class="mB10">
                      <p class="fc-input-label-txt pB5">Period</p>
                      <el-select
                        v-model="cardDataObj.dateRange"
                        placeholder="Please select a period"
                        class="width100 el-input-textbox-full-border"
                      >
                        <template v-for="(dateRange, index) in dateOperators">
                          <el-option
                            :label="dateRange.label"
                            :value="dateRange.value"
                            :key="index"
                          ></el-option>
                        </template>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>
              <template v-else>
                <el-row class="mB10">
                  <el-col :span="24">
                    <kpi-reading-picker
                      :type="'moduleKPI'"
                      :initialReading="cardDataObj.kpi"
                      @onModuleKpiSelect="
                        moduleKpi => handleModuleKpi(moduleKpi)
                      "
                      @onReadingSelect="reading => setReading('kpi', reading)"
                    ></kpi-reading-picker>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item prop="dateRange" class="mB10">
                      <p class="fc-input-label-txt pB5">Period</p>
                      <el-select
                        v-model="cardDataObj.dateRange"
                        placeholder="Please select a period"
                        class="width100 el-input-textbox-full-border"
                      >
                        <template v-for="(dateRange, index) in dateOperators">
                          <el-option
                            :label="dateRange.label"
                            :value="dateRange.value"
                            :key="index"
                          ></el-option>
                        </template>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="22">
                    <el-form-item class="mB10">
                      <p class="fc-input-label-txt pB5">Sub Text</p>
                      <el-input
                        :autofocus="isNew"
                        v-model="cardDataObj.subText"
                        class="width100 fc-input-full-border2"
                        clearable
                      ></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="2">
                    <el-form-item class="mB10 mT25">
                      <p class="fc-input-label-txt pB5"></p>
                      <el-popover
                        v-model="showInsertVariablePopover"
                        placement="right"
                        width="250"
                        trigger="click"
                        :popper-class="'f-popover'"
                      >
                        <div class="graphics-insert-variable-container">
                          <div class="graphics-insert-variable-title">
                            Insert Variable
                          </div>
                          <div class="graphics-insert-variable-filter">
                            <el-input
                              placeholder="Filter variables"
                              v-model="filterVariablesInput"
                              class="fc-input-full-border2"
                            ></el-input>
                          </div>
                          <div
                            class="graphics-insert-variable-list"
                            v-if="previewData.value.fields"
                          >
                            <ul>
                              <li
                                v-for="(v,
                                index) in previewData.value.fields.filter(
                                  v =>
                                    !filterVariablesInput ||
                                    v.name
                                      .toLowerCase()
                                      .indexOf(
                                        filterVariablesInput.toLowerCase()
                                      ) >= 0
                                )"
                                :key="index"
                                @click="
                                  cardDataObj.subText =
                                    cardDataObj.subText + ' ${' + v.name + '}'
                                  showInsertVariablePopover = false
                                "
                              >
                                {{ v.displayName }}
                              </li>
                            </ul>
                          </div>
                        </div>
                        <el-tooltip
                          class="item"
                          effect="dark"
                          content="Insert Variable"
                          placement="top"
                          slot="reference"
                        >
                          <el-button
                            icon="el-icon-s-order"
                            style="padding: 2px 8px; font-size: 24px;"
                            type="text"
                          ></el-button>
                        </el-tooltip>
                      </el-popover>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row class="mB10">
                  <el-col :span="24">
                    <el-form-item prop="baseline" class="mB10">
                      <p class="fc-input-label-txt pB5">Baseline</p>
                      <el-select
                        v-model="cardDataObj.baseline"
                        placeholder="Please select a period"
                        class="width100 el-input-textbox-full-border"
                      >
                        <template v-for="(dateRange, index) in baselines">
                          <el-option
                            :label="dateRange.name"
                            :value="dateRange.name"
                            :key="index"
                          ></el-option>
                        </template>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>
            </el-tab-pane>

            <!-- style props-->

            <el-tab-pane label="Styles" name="styles">
              <el-row class="mB10 mT20">
                <el-col :span="8">
                  <el-form-item prop="primaryColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Primary Text</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.primaryColor"
                        :key="'primaryColor' + cardStateObj.styles.primaryColor"
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item prop="secondaryColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Secondary Text</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.secondaryColor"
                        :key="
                          'secondaryColor' + cardStateObj.styles.secondaryColor
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item prop="backgroundColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Background Color</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.backgroundColor"
                        :key="
                          'backgroundColor' +
                            cardStateObj.styles.backgroundColor
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10 mT20">
                <el-col :span="8">
                  <el-form-item prop="arrowUpColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Arrow up</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.arrowUpColor"
                        :key="'arrowUpColor' + cardStateObj.styles.arrowUpColor"
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item prop="arrowDownColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Arrow down</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-color-picker
                        v-model="cardStateObj.styles.arrowDownColor"
                        :key="
                          'arrowDownColor' + cardStateObj.styles.arrowDownColor
                        "
                        :predefine="predefinedColors"
                        size="small"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10 mT20">
                <el-col :span="8">
                  <el-form-item prop="backgroundColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Decimal Place</p>
                    <el-select
                      v-model="cardStateObj.styles.decimalPlace"
                      placeholder="Please select a decimal Place"
                      class="width100 el-input-textbox-full-border"
                    >
                      <template v-for="(value, key) in decimalPlaces">
                        <el-option
                          :label="key"
                          :value="value"
                          :key="key"
                        ></el-option>
                      </template>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="16">
                  <el-form-item prop="backgroundColor" class="mB10">
                    <unit-picker
                      class="pL10"
                      :unitConfig="cardStateObj.styles.unitConfig"
                    ></unit-picker>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-tab-pane>

            <el-tab-pane label="Actions" name="actions">
              <ActionPicker
                v-if="cardActionsMount"
                ref="action-picker"
                :definedActionTypes="definedActionTypes"
                :variables="variables"
                v-model="cardActions"
                :elements="[{ name: 'default', displayName: 'None' }]"
              >
                <template slot="element-title">
                  <div class="pT15"></div>
                </template>
              </ActionPicker>
            </el-tab-pane>
            <el-tab-pane label="FORMATTING" name="conditionalformatting">
              <div class="card-builder-criteria-block">
                <ConditionalFormating
                  ref="conditional-formatting"
                  :variables="conditionalVariables"
                  :cardData="previewData"
                  v-model="conditionalFormatting"
                  :cardStyles="cardStateObj.styles"
                ></ConditionalFormating>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :cardData="previewData"
            :cardState="previewState"
            :isLoading="isPreviewLoading"
            :cardParams="cardDataObj"
          ></Card>
        </div>
        <!-- card tools -->
      </div>
    </div>
    <div class="d-flex mT-auto form-action-btn">
      <el-button
        class="form-btn f13 bold secondary text-center text-uppercase"
        @click="onGoBack()"
        >Cancel</el-button
      >
      <el-button
        type="primary"
        class="form-btn f13 bold primary m0 text-center text-uppercase"
        @click="save()"
        >Save</el-button
      >
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import NewDateHelper from '@/mixins/NewDateHelper'
import Config from '../base/Config'
import Card from './Card'
import { isEmpty } from '@facilio/utils/validation'

import {
  kpiTypes,
  dateOperators,
  aggregateFunctions,
} from 'pages/card-builder/card-constants'
import KpiReadingPicker from 'pages/card-builder/cards/common/KpiReadingPicker'
import { API } from '@facilio/api'
export default {
  name: 'KpiCard2',
  extends: Config,
  components: { Card, KpiReadingPicker },
  data() {
    return {
      cardLayout: `kpicard_layout_2`,
      kpiTypes,
      baselines: [],
      showInsertVariablePopover: false,
      filterVariablesInput: '',
      cardActionsMount: true,
      moduleKpi: [],
      resourceProps: [
        'title',
        'kpiType',
        {
          prop: 'kpi',
          resourceProps: ['kpiId', 'yAggr'],
        },
        'dateRange',
        'subText',
        'baseline',
      ],
      readingTypes: {
        Asset: 'asset',
        Space: 'space',
      },
      modules: [
        {
          label: 'Maintenance',
          moduleName: 'workorder',
          license: 'MAINTENANCE',
        },
        {
          label: 'FDD',
          moduleName: this.$helpers.isLicenseEnabled('NEW_ALARMS')
            ? 'newreadingalarm'
            : 'alarm',
          license: 'ALARMS',
        },
        {
          label: 'Building performance',
          moduleName: 'energydata',
          license: 'ENERGY',
        },
        {
          label: 'Asset',
          moduleName: 'asset',
          license: 'SPACE_ASSET',
        },
        {
          label: 'Inventory Request',
          moduleName: 'inventoryrequest',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Item',
          moduleName: 'item',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Contracts',
          moduleName: 'contracts',
          list: [],
          expand: false,
          license: 'CONTRACT',
        },
        {
          label: 'Purchaseorder',
          moduleName: 'purchaseorder',
          list: [],
          expand: false,
          license: 'PURCHASE',
        },
        {
          label: 'Purchaserequest',
          moduleName: 'purchaserequest',
          list: [],
          expand: false,
          license: 'PURCHASE',
        },
        {
          label: 'Visit',
          moduleName: 'visitorlog',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'contact',
          moduleName: 'contact',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Watchlist',
          moduleName: 'watchlist',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Visitor',
          moduleName: 'visitor',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
      ],
      metrics: [],
      kpiList: [],
      cardStateObj: {
        canResize: true,
        styles: {
          primaryColor: '#110d24',
          secondaryColor: '#969caf',
          backgroundColor: '#FFF',
          arrowUpColor: '#008000',
          arrowDownColor: '#ff0100',
          decimalPlace: -1,
          unitConfig: {
            unit: null,
            position: 2,
          },
        },
        modulekpi: {
          moduleName: null,
          kpiId: null,
        },
        expression: {
          name: 'test',
          aggr: 3,
          metricId: -1,
          moduleName: 'workorder',
          siteId: null,
          dateOperator: 22,
          dateValue: `${NewDateHelper.getDatePickerObject(22).value[0]},${
            NewDateHelper.getDatePickerObject(22).value[1]
          } `,
          dateFieldId: null,
          criteria: {
            pattern: '(1)',
            conditions: {
              '1': {
                fieldName: '__vue_devtool_undefined__',
                value: '__vue_devtool_undefined__',
                columnName: '__vue_devtool_undefined__',
                operatorId: -1,
              },
            },
            resourceOperator: false,
          },
        },
      },
      cardActions: {
        default: {
          actionType: 'none',
        },
      },
      cardDataObj: {
        title: '',
        kpiType: 'module',
        reading: {
          parentType: 'asset',
          assetCategory: null,
          kpiId: null,
        },
        dateRange: 'Today',
        aggr: 'avg',
        subText: '',
        baseline: 'Previous Day',
      },

      kpiModule: {
        moduleName: null,
        expression: {},
        aggregateString: 'count',
      },
      toggleMetricAggregation: false,
      timeFields: [],
      aggregateFunctions,
      dateOperators: dateOperators,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    API.get('/baseline/all').then(({ data, error }) => {
      if (!error) {
        this.baselines = data
      }
    })
  },
  mounted() {
    this.validationCheck()
    this.getKpiList()
    this.loadModuleFields()
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: 'Kpi card 2',
            period: 'Today',
            baselinePeriod: 'Previous Day',
            value: {
              value: '',
            },
            baselineValue: {
              value: '',
            },
          }
    },
    ...mapState({
      assetCategory: state => state.assetCategory,
      sites: state => state.sites,
    }),
    definedActionTypes() {
      let type = this.cardDataObj.kpiType
      if (type === 'reading') {
        return ['hyperLink', 'controlAction', 'none', 'showTrend', 'showReport']
      } else {
        return [
          'hyperLink',
          'controlAction',
          'none',
          'showListView',
          'showReport',
        ]
      }
    },
    AssetFilteredKpiList() {
      if (this.cardDataObj.reading.parentType === 'asset') {
        return this.kpiList.filter(
          rt => rt.assetCategoryId === this.cardDataObj.reading.assetCategory
        )
      }
      return this.kpiList
    },
    metric() {
      if (this.data && this.cardStateObj.expression.aggr) {
        return this.metrics.find(
          rt => rt.id === this.cardStateObj.expression.metricId
        )
      } else {
        return this.metrics.length ? this.metrics[0] : null
      }
    },
    variables() {
      let { variables } = this.previewData
      if (variables) {
        return variables
      }
      return []
    },
  },
  methods: {
    validationCheck() {
      const operatorId = this.$getProperty(
        this,
        'cardStateObj.expression.criteria.conditions.1.operatorId',
        null
      )
      if (operatorId === '__vue_devtool_undefined__') {
        this.cardStateObj.expression.criteria.conditions[1].operatorId = -1
      }
      const decimalPlace = this.$getProperty(
        this,
        'cardStateObj.styles.decimalPlace',
        null
      )
      if (decimalPlace === 'Auto') {
        this.cardStateObj.styles.decimalPlace = -1
      }
    },
    getCardActions() {},
    setDateRange() {
      let type = this.cardDataObj.kpiType
      if (type === 'reading') {
        this.cardDataObj.dateRange = 'Today'
        this.cardActions = {
          default: {
            actionType: 'showTrend',
          },
        }
      } else {
        this.cardActions = {
          default: {
            actionType: 'showListView',
            data: {
              target: 'popup',
            },
          },
        }
      }
      this.cardActionsMount = false
      setTimeout(() => {
        this.cardActionsMount = true
      }, 500)
    },
    serializeProperty(prop, data) {
      if (prop === 'kpi') {
        return data.kpi
      }
    },
    validateProperty() {
      return {
        kpi: data => {
          if (data) {
            return this.validateKpi(data)
          }
          return false
        },
        dateRange: () => false,
        subText: () => false,
      }
    },
    validateKpi(data) {
      if (data?.kpi?.kpiId) {
        return false
      }
      return true
    },
    handleModuleKpi(moduleKpiObj) {
      if (moduleKpiObj) {
        if (moduleKpiObj.id) {
          if (moduleKpiObj.dateOperator && moduleKpiObj.dateOperator > 0) {
            this.cardDataObj.dateRange = this.dateOperators.find(
              rl => rl.enumValue === moduleKpiObj.dateOperator
            ).value
          } else {
            this.cardDataObj.dateRange = null
          }
        }
      }
    },
    addKpi() {
      API.post('/v2/kpi/module/addOrUpdate', {
        kpi: this.cardStateObj.expression,
      }).then(response => {
        this.setKpiResult(response.data.result)
      })
    },
    setKpiResult(result) {
      if (result.kpi) {
        let { kpi } = result
        let obj = {
          kpiId: kpi.id,
          yAggr: this.aggregateFunctions.find(rt => rt.enumValue === kpi.aggr)
            .value,
        }
        this.$set(this.cardDataObj, 'kpi', obj)
        this.getData()
      }
    },
    setDateValue() {
      this.cardStateObj.expression.dateValue = `${
        NewDateHelper.getDatePickerObject(
          this.cardStateObj.expression.dateOperator
        ).value[0]
      },${
        NewDateHelper.getDatePickerObject(
          this.cardStateObj.expression.dateOperator
        ).value[1]
      } `
    },
    setMetricAggregation(agg) {
      this.cardStateObj.expression.aggr = agg.enumValue
      this.toggleMetricAggregation = false
    },
    getModuleObject(moduleMetaObject) {
      this.moduleMetaObject = moduleMetaObject
    },
    getCriteria(criteria) {
      this.cardStateObj.expression.criteria = criteria
    },
    showMetricAggregation(metric) {
      if (metric && metric.defaultMetric) {
        this.toggleMetricAggregation = false
        return false
      } else {
        return true
      }
    },
    loadModuleFields() {
      this.loading = true
      API.post('/v2/report/getReportFields', {
        moduleName: this.cardStateObj.expression.moduleName,
      }).then(response => {
        this.prepareMetrics(response.data.result)
        this.getTimeFields(response.data.result)
        this.loading = false
      })
    },
    getTimeFields(result) {
      if (result?.meta?.dimension?.time) {
        this.timeFields = result.meta.dimension.time
      }
    },
    prepareMetrics(result) {
      this.metrics = []
      if (result && result.meta && result.meta.metrics) {
        for (let metric of result.meta.metrics) {
          if (metric.name === 'actualWorkDuration') {
            metric.displayName = 'Work Duration'
          }
          this.metrics.push(metric)
        }
        let defaultMetric = {}
        defaultMetric['displayName'] =
          'Number of ' +
          this.modules.find(
            rt => rt.moduleName === this.cardStateObj.expression.moduleName
          ).label +
          's'
        defaultMetric['defaultMetric'] = true
        defaultMetric['fieldId'] = -1
        defaultMetric['name'] = 'count'
        defaultMetric['id'] = -1
        this.metrics.push(defaultMetric)
      }
    },
    getKpiList() {
      let url = `/v2/kpi/list?type=1`
      API.get(url).then(({ data }) => {
        if (data.responseCode === 0) {
          this.kpiList = data.result.formulaList
        }
      })
    },
    setReading(prop, reading) {
      let { cardDataObj } = this
      let { kpiId } = reading
      this.$set(this, 'cardDataObj', { ...cardDataObj, [prop]: reading })
      this.$set(reading, 'kpiId', kpiId)
      this.setConditionalVariables(reading)
    },
  },
}
</script>
<style lang="scss" scoped>
.card-wrapper {
  height: 220px;
  width: 300px;
}
.header {
  font-size: 22px;
  font-weight: 400;
  color: #324056;
}
</style>
