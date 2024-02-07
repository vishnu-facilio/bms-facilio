<template>
  <div class="asset-cost-page">
    <div class="d-flex flex-direction-row f14">
      <div class="border-right width30">
        <el-row
          v-bind:class="{ selectedrow: selectedCost === 'maintenance' }"
          class="cost-breakup-unselected"
        >
          <div @click="costSelection('maintenance')">
            <el-col :span="24" class="p30">
              <div
                class="fc-black-13 text-uppercase text-left bold letter-spacing09"
              >
                {{ $t('asset.assets.maintenance_cost') }}
              </div>
              <div class="pT20">
                <el-row>
                  <el-col :span="12">
                    <div class="flex-middle">
                      <inline-svg
                        src="svgs/arrow"
                        :iconClass="
                          `${getArrowDesign(
                            maintenanceCostThisYear,
                            maintenanceCostLastYear
                          )} icon arrow`
                        "
                        class="vertical-middle mR5"
                      ></inline-svg>
                      <currency
                        :value="maintenanceCostThisYear"
                        valueSize="mmd"
                        symbolSize="mmd"
                      ></currency>
                    </div>
                    <div class="common-state bold pT10 pL23">
                      {{ $t('common.reports.rangeCategory.THIS_YEAR') }}
                    </div>
                  </el-col>
                  <el-col :span="12" class="pL40">
                    <currency
                      :value="maintenanceCostLastYear"
                      valueSize="mmd"
                      symbolSize="mmd"
                    ></currency>
                    <div class="fc-grey2 f12 bold pT10">
                      {{ $t('common.reports.rangeCategory.LAST_YEAR') }}
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-col>
          </div>
        </el-row>

        <el-row
          v-bind:class="{ selectedrow: selectedCost === 'plannedmaintenance' }"
          class="cost-breakup-unselected"
        >
          <div @click="costSelection('plannedmaintenance')">
            <el-col :span="24" class="p30">
              <div
                class="fc-black-13 text-uppercase text-left bold letter-spacing09"
              >
                {{ $t('common.header.planned_maintenance') }}
              </div>
              <el-row class="pT20">
                <el-col :span="12">
                  <div class="flex-middle">
                    <inline-svg
                      src="svgs/arrow"
                      :iconClass="
                        `${getArrowDesign(
                          plannedMaintenanceCostThisYear,
                          plannedMaintenanceCostLastYear
                        )} icon arrow`
                      "
                      class="vertical-middle mR5"
                    ></inline-svg>
                    <currency
                      :value="plannedMaintenanceCostThisYear"
                      valueSize="mmd"
                      symbolSize="mmd"
                    ></currency>
                  </div>
                  <div class="common-state bold pT10 pL23">
                    {{ $t('common.reports.rangeCategory.THIS_YEAR') }}
                  </div>
                </el-col>
                <el-col :span="12" class="pL40">
                  <currency
                    :value="plannedMaintenanceCostLastYear"
                    valueSize="mmd"
                    symbolSize="mmd"
                  ></currency>
                  <div class="fc-grey2 f12 bold pT10">
                    {{ $t('common.reports.rangeCategory.LAST_YEAR') }}
                  </div>
                </el-col>
              </el-row>
            </el-col>
          </div>
        </el-row>

        <el-row
          v-bind:class="{
            selectedrow: selectedCost === 'unplannedmaintenance',
          }"
          class="cost-breakup-unselected"
        >
          <div @click="costSelection('unplannedmaintenance')">
            <el-col :span="24" class="p30">
              <div
                class="fc-black-13 text-uppercase text-left bold letter-spacing09"
              >
                {{ $t('asset.assets.asset_upm') }}
              </div>
              <el-row class="pT20">
                <el-col :span="12">
                  <div class="flex-middle">
                    <inline-svg
                      src="svgs/arrow"
                      :iconClass="
                        `${getArrowDesign(
                          unplannedMaintenanceCostThisYear,
                          unplannedMaintenanceCostLastYear
                        )} icon arrow`
                      "
                      class="vertical-middle mR5"
                    ></inline-svg>
                    <currency
                      :value="unplannedMaintenanceCostThisYear"
                      valueSize="mmd"
                      symbolSize="mmd"
                    ></currency>
                  </div>
                  <div class="common-state bold pT10 pL23">
                    {{ $t('common.reports.rangeCategory.THIS_YEAR') }}
                  </div>
                </el-col>
                <el-col :span="12" class="pL40">
                  <currency
                    :value="unplannedMaintenanceCostLastYear"
                    valueSize="mmd"
                    symbolSize="mmd"
                  ></currency>
                  <div class="fc-grey2 f12 bold pT10">
                    {{ $t('common.reports.rangeCategory.LAST_YEAR') }}
                  </div>
                </el-col>
              </el-row>
            </el-col>
          </div>
        </el-row>
      </div>
      <div class="width70">
        <div class="height100">
          <common-widget-chart
            ref="cost-breakup-chart"
            v-bind="$props"
            key="costbreakup"
            moduleName="workorderCost"
            type="donut"
            :refresh="refreshChart"
            :customizeChartOptions="customizeChartOptions"
            datePickerHide="true"
            isWidget="true"
          >
          </common-widget-chart>
        </div>
      </div>
    </div>
    <!-- action area for widget-title-section-->
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="widget-header-name">
          {{ $t('asset.assets.cost_brakup') }}
        </div>
      </div>
    </portal>
    <!-- action area -->
  </div>
</template>
<script>
import CommonWidgetChart from '@/page/widget/performance/charts/CommonWidgetChart'

export default {
  props: [
    'details',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
  ],
  components: { CommonWidgetChart },
  data() {
    return {
      ids: '',
      selectedCost: 'maintenance',
      refreshChart: false,
      plannedMaintenanceCostThisYear: 0,
      unplannedMaintenanceCostThisYear: 0,
      maintenanceCostThisYear: 0,
      plannedMaintenanceCostLastYear: 0,
      unplannedMaintenanceCostLastYear: 0,
      maintenanceCostLastYear: 0,
      typeVsDisplayNameMap: {
        maintenance: 'MAINTENANCE COST',
        plannedmaintenance: 'PLANNED MAINTENANCE',
        unplannedmaintenance: 'UNPLANNED MAINTENANCE',
      },
      customizeChartOptions: {
        donut: {
          centerText: {
            primaryText: '_sum',
            secondaryText: 'Total Cost',
          },
        },
        legend: {
          position: 'bottom',
        },
      },
    }
  },
  mounted() {
    this.costSelection('maintenance')
    this.loadMaintenanceCost()
  },
  methods: {
    costSelection(type) {
      // this.customizeChartOptions.donut.centerText.secondaryText = this.typeVsDisplayNameMap[
      //   type
      // ]
      this.selectedCost = type
      this.refreshChart = false
      this.workorderIdsForSelectedCostAndsetChartParams()
    },
    workorderIdsForSelectedCostAndsetChartParams() {
      this.$util
        .getWorkFlowResult(106, [
          this.details.id,
          this.selectedCost === 'plannedmaintenance'
            ? 2
            : this.selectedCost === 'unplannedmaintenance'
            ? 3
            : 1,
        ])
        .then(data => {
          this.ids = data.workorderIds
        })
        .finally(() => {
          Object.values(
            this.widget.widgetParams.chartParams.criteria.conditions
          ).forEach(a => {
            if (a.columnName == 'Workorder_cost.PARENT_ID') {
              a.value = this.ids
                ? this.ids.substring(0, this.ids.length - 1)
                : ''
            }
          })
          this.$refs['cost-breakup-chart'].initializeReport()
        })
    },
    loadMaintenanceCost() {
      let url = '/v2/workflow/getDefaultWorkflowResult'
      let params = 'defaultWorkflowId=' + 105 + '&paramList=' + this.details.id
      url = url + '?' + params
      return this.$http.get(url).then(response => {
        if (
          response.data.result &&
          response.data.result.workflow &&
          response.data.result.workflow.returnValue
        ) {
          this.plannedMaintenanceCostThisYear =
            response.data.result.workflow.returnValue.plannedMaintenanceCostThisYear
          this.unplannedMaintenanceCostThisYear =
            response.data.result.workflow.returnValue.unplannedMaintenanceCostThisYear
          this.maintenanceCostThisYear =
            response.data.result.workflow.returnValue.maintenanceCostThisYear
          this.plannedMaintenanceCostLastYear =
            response.data.result.workflow.returnValue.plannedMaintenanceCostLastYear
          this.unplannedMaintenanceCostLastYear =
            response.data.result.workflow.returnValue.unplannedMaintenanceCostLastYear
          this.maintenanceCostLastYear =
            response.data.result.workflow.returnValue.maintenanceCostLastYear
        }
      })
    },
    getArrowDesign(costThisYear, costLastYear) {
      if (costThisYear === costLastYear) return 'hide-v'
      return costThisYear < costLastYear
        ? 'fill-green rotate-bottom'
        : 'fill-red'
    },
  },
}
</script>
<style lang="scss">
.asset-cost-page {
  .selectedrow {
    background-color: #effdff !important;
    border: 1px solid #ccf7fc;
    border-right: 3px solid #9ed6dd;
  }
  .symbol-mmd,
  .curreny-mmd {
    font-size: 18px;
    font-weight: 500;
    color: #324056;
  }
}

.cost-breakup-unselected {
  height: 146px;
  border: 1px solid transparent;
  border-right: 3px solid transparent;
}
.cost-widget {
  min-height: 300px;
  padding-top: 10px;
}
.cost-widget .fc-newchart-container {
}
// .cost-widget .fc-newchart-container svg{
//   height: 250px;
// }
</style>
