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
                <el-col :span="24" class="pR10">
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

              <el-row class="mB10">
                <el-col :span="24" class="pR10">
                  <el-form-item prop="reading" class="mB10">
                    <p class="fc-input-label-txt pB5">Type</p>
                    <el-select
                      v-model="cardDataObj.kpiType"
                      placeholder="Please select a period"
                      class="width100 el-input-textbox-full-border"
                      @change="setDateRange, (typeChange = true)"
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
              </el-row>
              <template v-if="cardDataObj.kpiType === 'reading'">
                <kpi-reading-picker
                  :initialReading="cardDataObj.kpis"
                  :clearData="typeChange"
                  @onReadingSelect="reading => setReading('kpis', reading)"
                ></kpi-reading-picker>
              </template>
              <template v-else>
                <el-row class="mB10">
                  <el-col :span="24">
                    <kpi-reading-picker
                      :type="'moduleKPI'"
                      :initialReading="cardDataObj.kpis"
                      :clearData="typeChange"
                      @onModuleKpiSelect="
                        moduleKpi => handleModuleKpi(moduleKpi)
                      "
                      @onReadingSelect="reading => setReading('kpis', reading)"
                    ></kpi-reading-picker>
                  </el-col>
                </el-row>
              </template>

              <el-row class="mB20" :gutter="20">
                <el-col
                  :span="24"
                  v-if="
                    [safeLimitTypes.MAX, safeLimitTypes.BETWEEN].includes(
                      cardDataObj.safeLimitType
                    )
                  "
                >
                  <el-form-item prop="maxSafeLimitType" class="mB10">
                    <div class="fc-text-pink2 pB5">
                      Maximum
                    </div>
                    <div class="">
                      <div class="width100 pR10">
                        <el-select
                          v-model="cardDataObj.maxSafeLimitType"
                          placeholder="Select the option"
                          class="el-input-textbox-full-border mB10 width100"
                        >
                          <el-option label="KPI" value="kpi"></el-option>
                          <el-option
                            label="Constant"
                            value="constant"
                          ></el-option>
                        </el-select>
                      </div>
                      <div
                        v-if="cardDataObj.maxSafeLimitType === 'constant'"
                        class="width100 pR10"
                      >
                        <el-input
                          :autofocus="true"
                          class="addReading-title el-input-textbox-full-border width100"
                          v-model="cardDataObj.maxSafeLimitConstant"
                          :placeholder="'Enter the maximum value'"
                        ></el-input>
                      </div>
                      <div v-else class="width100">
                        <template v-if="cardDataObj.kpiType === 'reading'">
                          <kpi-single-reading-picker
                            :initialReading="cardDataObj.maxSafeLimitKpi"
                            :key="cardDataObj.kpiType"
                            :clearData="typeChange"
                            @onReadingSelect="
                              reading => setReading('maxSafeLimitKpi', reading)
                            "
                          ></kpi-single-reading-picker>
                        </template>
                        <template v-else>
                          <kpi-single-reading-picker
                            :type="'moduleKPI'"
                            :key="cardDataObj.kpiType"
                            :initialReading="cardDataObj.maxSafeLimitKpi"
                            :clearData="typeChange"
                            @onReadingSelect="
                              reading => setReading('maxSafeLimitKpi', reading)
                            "
                          ></kpi-single-reading-picker>
                        </template>
                      </div>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB20 pR10" :gutter="20">
                <el-col :span="24">
                  <el-form-item class="mB10">
                    <div class="fc-text-pink2 pB5">
                      Center Text
                    </div>
                    <div class="">
                      <div class="width100">
                        <el-select
                          v-model="cardDataObj.centerTextType"
                          placeholder="Select the option"
                          class="el-input-textbox-full-border mB10 width100"
                        >
                          <el-option label="KPI" value="kpi"></el-option>
                          <el-option label="Text" value="text"></el-option>
                        </el-select>
                      </div>
                      <div
                        v-if="cardDataObj.centerTextType === 'text'"
                        class="width100 pT10"
                      >
                        <el-input
                          :autofocus="true"
                          class="addReading-title el-input-textbox-full-border width100"
                          v-model="cardDataObj.centerText"
                          :placeholder="'Enter the center text'"
                        ></el-input>
                      </div>
                      <div v-else class="width100">
                        <template v-if="cardDataObj.kpiType === 'reading'">
                          <kpi-single-reading-picker
                            :key="cardDataObj.kpiType"
                            :initialReading="cardDataObj.centerTextKpi"
                            :clearData="typeChange"
                            @onReadingSelect="
                              reading => setReading('centerTextKpi', reading)
                            "
                          ></kpi-single-reading-picker>
                        </template>
                        <template v-else>
                          <kpi-single-reading-picker
                            :type="'moduleKPI'"
                            :key="cardDataObj.kpiType"
                            :initialReading="cardDataObj.centerTextKpi"
                            :clearData="typeChange"
                            @onReadingSelect="
                              reading => setReading('centerTextKpi', reading)
                            "
                          ></kpi-single-reading-picker>
                        </template>
                      </div>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-tab-pane>

            <!-- style props-->
            <el-tab-pane
              label="Styles"
              name="styles"
              v-if="showstyle"
              class="styleScroll"
            >
              <el-row class="mB30 pT20">
                <el-col :span="10" class="flex-middle pT5 pL5">
                  <div>
                    <el-checkbox
                      class=""
                      v-model="cardStateObj.styles.showPathtext"
                    >
                    </el-checkbox>
                  </div>
                  <div class="fc-input-label-txt pB0 pL10">Show data label</div>
                </el-col>
                <el-col :span="14" class="flex-middle dashboard-color-picker">
                  <el-color-picker
                    v-model="cardStateObj.styles.gaugeCenterColors.pathColor"
                    :key="'centercolor'"
                    :predefine="predefinedColors"
                    size="small"
                    :popper-class="'chart-custom-color-picker'"
                  ></el-color-picker>
                  <div class="fc-input-label-txt pB0 pL10">
                    Center background color
                  </div>
                </el-col>
                <el-col
                  :span="14"
                  class="flex-middle dashboard-color-picker pT20"
                >
                  <el-color-picker
                    v-model="cardStateObj.styles.gaugeCenterColors.textColor"
                    :key="'centerText'"
                    :predefine="predefinedColors"
                    size="small"
                    :popper-class="'chart-custom-color-picker'"
                  ></el-color-picker>
                  <div class="fc-input-label-txt pB0 pL5">
                    Center Text color
                  </div>
                </el-col>
              </el-row>
              <el-row class="mB30" :gutter="20" v-if="cardDataObj.kpis.length">
                <el-col :span="8" class="mB10">
                  <p class="fc-input-label-txt pB10">Name</p>
                </el-col>
                <el-col :span="4" class="mB10">
                  <p class="fc-input-label-txt pB10">Colors</p>
                </el-col>
                <el-col :span="5" class="mB10">
                  <p class="fc-input-label-txt pB10">Text color</p>
                </el-col>
              </el-row>
              <el-row
                class="mB30"
                :gutter="20"
                v-for="(color, index) in cardDataObj.kpis"
                :key="index"
              >
                <el-col :span="8" class="mB10">
                  {{ color.label }}
                </el-col>
                <el-col :span="4" class="mB10 dashboard-color-picker">
                  <el-color-picker
                    v-model="color.pathColor"
                    :key="'' + index + '-color'"
                    :predefine="predefinedColors"
                    size="small"
                    :popper-class="'chart-custom-color-picker'"
                  ></el-color-picker>
                </el-col>
                <el-col :span="5" class="mB10 dashboard-color-picker">
                  <el-color-picker
                    v-model="color.textColor"
                    :key="'' + index + '-textColor'"
                    :predefine="predefinedColors"
                    size="small"
                    :popper-class="'chart-custom-color-picker'"
                  ></el-color-picker>
                </el-col>
              </el-row>
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
            :editMode="true"
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
import Config from 'pages/card-builder/cards/kpi/type1/Config'
import Card from './Card'
import KpiSingleReadingPicker from 'pages/card-builder/cards/common/KpiReadingPicker'
import {
  kpiTypes,
  dateOperators,
  aggregateFunctions,
} from 'pages/card-builder/card-constants'
import KpiReadingPicker from 'pages/card-builder/cards/common/KpiMultiReadingPicker'
import { deepCloneObject } from 'util/utility-methods'
import isString from 'lodash/isString'
export default {
  name: 'GuageCard7',
  extends: Config,
  components: {
    Card,
    KpiReadingPicker,
    KpiSingleReadingPicker,
  },
  data() {
    const safeLimitTypes = {
      MIN: 0,
      MAX: 1,
      BETWEEN: 2,
    }

    return {
      typeChange: false,
      cardLayout: `gauge_layout_7`,
      kpiTypes,
      showstyle: true,
      showInsertVariablePopover: false,
      filterVariablesInput: '',
      validationRules: {},
      cardActionsMount: true,
      moduleKpi: [],
      resourceProps: [
        'title',
        'kpiType',
        'kpis',
        'dateRange',
        'subText',
        'safeLimitType',
        'maxSafeLimitType',
        'maxSafeLimitKpi',
        'maxSafeLimitConstant',
        'centerTextType',
        'centerTextKpi',
        'centerText',
      ],
      readingTypes: {
        Asset: 'asset',
        Space: 'space',
      },
      cardDataObj: {
        title: '',
        kpiType: 'reading',
        reading: {
          parentType: 'asset',
          assetCategory: null,
          kpiId: null,
        },
        safeLimitType: 1,
        maxSafeLimitType: 'constant',
        centerTextType: 'text',
        maxSafeLimitConstant: null,
        maxSafeLimitKpi: {},
        centerTextKpi: {},
        centerText: null,
        kpis: [],
        dateRange: 'Today',
        aggr: 'avg',
        subText: '',
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
      readingPickerOptions: {
        parentId: {
          type: 'single',
        },
        fieldName: {
          type: 'single',
        },
        fieldId: {
          type: 'single',
        },
        dataType: {
          type: 'single',
        },
        moduleName: {
          type: 'single',
        },
        parentName: {
          type: 'single',
        },
        parentType: {
          type: 'single',
        },
        yAggr: {
          type: 'single',
        },
      },
      metrics: [],
      kpiList: [],
      cardStateObj: {
        canResize: true,
        styles: {
          primaryColor: '#110d24',
          secondaryColor: '#969caf',
          backgroundColor: '#FFF',
          gaugeColors: [],
          showPathText: true,
          textColor: '#000000',
          gaugeCenterColors: {
            pathColor: '#969caf',
            textColor: '#000000',
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
      safeLimitTypes,
      cardActions: {
        default: {
          actionType: 'none',
        },
      },
      emptykpi: {
        kpiId: null,
        yAggr: null,
        dateRange: 'Today',
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
  },
  mounted() {
    this.getKpiList()
    this.loadModuleFields()
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
      sites: state => state.sites,
    }),
    unit() {
      return ''
    },
    definedActionTypes() {
      return ['none']
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
      let variables =
        this.previewData && this.previewData.variables
          ? this.previewData.variables
          : null
      if (variables) {
        return variables
      }
      return []
    },
  },
  methods: {
    addEmpty() {
      let data = deepCloneObject(this.emptykpi)
      this.cardDataObj.kpis.push(data)
    },
    serializeProperty(prop, data) {
      if (prop === 'kpis') {
        return data.kpis
      }
    },
    validateProperty() {
      return {
        kpis: () => false,
        dateRange: () => false,
        subText: () => false,
        safeLimitType: () => false,
        maxSafeLimitConstant: () => false,
        maxSafeLimitKpi: () => false,
        centerTextType: () => false,
        centerTextKpi: () => false,
        centerText: () => false,
      }
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
      this.$http
        .post('/v2/kpi/module/addOrUpdate', {
          kpi: this.cardStateObj.expression,
        })
        .then(response => {
          // this.setKpiResult(response.data.result)
        })
    },
    // setKpiResult(result) {
    //   if (result.kpi) {
    //     let { kpi } = result
    //     let obj = {
    //       kpiId: kpi.id,
    //       yAggr: this.aggregateFunctions.find(rt => rt.enumValue === kpi.aggr)
    //         .value,
    //     }
    //     this.$set(this.cardDataObj, 'kpi', obj)
    //     this.getData()
    //   }
    // },
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
      this.$http
        .post('/v2/report/getReportFields', {
          moduleName: this.cardStateObj.expression.moduleName,
        })
        .then(response => {
          this.prepareMetrics(response.data.result)
          this.getTimeFields(response.data.result)
          this.loading = false
        })
    },
    getTimeFields(result) {
      if (result.meta && result.meta.dimension && result.meta.dimension.time) {
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
      let url = `/v2/kpi/list?type=1&`
      this.$http.get(url).then(({ data }) => {
        if (data.responseCode === 0) {
          this.kpiList = data.result.formulaList
        }
      })
    },
    setReading(prop, reading) {
      let { cardDataObj } = this
      let { kpiId } = reading
      if (cardDataObj?.kpiType === 'reading') {
        reading =
          prop === 'centerTextKpi' || prop === 'maxSafeLimitKpi'
            ? [reading]
            : reading
        let readingNew = this.$helpers.cloneObject(reading)

        readingNew = readingNew.map(kpi => {
          let { kpiId } = kpi
          if (isString(kpiId)) {
            kpi.kpiId = parseInt(kpiId.split('_')[0])
          }
          return kpi
        })

        readingNew =
          prop === 'centerTextKpi' || prop === 'maxSafeLimitKpi'
            ? readingNew[0]
            : readingNew
        this.$set(this, 'cardDataObj', { ...cardDataObj, [prop]: readingNew })
      } else {
        this.$set(this, 'cardDataObj', { ...cardDataObj, [prop]: reading })
      }
      this.$set(reading, 'kpiId', kpiId)
      if (prop === 'kpis') {
        this.showstyle = false
        setTimeout(() => {
          this.showstyle = true
        }, 100)
      }
    },
  },
}
</script>
<style scoped lang="scss">
.card-wrapper {
  height: 400px;
}
.header {
  font-size: 22px;
  font-weight: 400;
  color: #324056;
}
</style>
<style>
.kpi-criteria .report-criteria-buuilder .criteria-container {
  height: auto;
  padding: 0;
}
.graphics-insert-variable-title {
  padding: 12px;
  border-bottom: 1px solid #fafafa;
  text-transform: uppercase;
  font-weight: 500;
  font-size: 13px;
}
.graphics-insert-variable-filter {
  padding: 12px;
}
.graphics-insert-variable-list {
  max-height: 300px;
  overflow: scroll;
}
.graphics-insert-variable-list ul {
  padding: 0;
  margin: 0;
}
.graphics-insert-variable-list ul li {
  list-style: none;
  padding: 12px;
  cursor: pointer;
}
.graphics-insert-variable-list ul li:hover {
  background: #fafafa;
}
.styleScroll {
  overflow: auto;
  height: 350px;
}
</style>
