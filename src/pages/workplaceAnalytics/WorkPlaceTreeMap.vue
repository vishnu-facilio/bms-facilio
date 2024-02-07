<template>
  <div>
    <div v-if="!serverConfig" class="text-center">
      <div
        class="p15 flex-middle height80vh justify-content-center flex-direction-column"
      >
        <div>
          <inline-svg
            src="svgs/emptystate/reportlist"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
        </div>
        <div class="nowo-label">
          {{ $t('common._common.please_select_data_points_analyze') }}
        </div>
      </div>
    </div>
    <el-container class="wp-chart-conatiner" v-else>
      <el-container class="flex relative">
        <div class="wp-loading" v-if="fullLoading">
          <spinner :show="fullLoading" size="80"></spinner>
        </div>
        <WorkplaceAnalyticsLeagend
          :building="{ name: '', id: this.buildingId }"
          :floor="{ name: '', id: this.floorId }"
          :departments="selectedDepartmentIds"
          @dataLoaded="leagendDataLoaded"
        ></WorkplaceAnalyticsLeagend>
        <FNewAnalyticModularReport
          class="text-center width100 treemap-ct-section"
          ref="newAnalyticReport"
          :serverConfig.sync="serverConfig"
          :module="moduleObj"
          :defaultChartType="'treemap'"
          :hideTabs="true"
          :hideHeader="true"
          :hidecharttypechanger="true"
          :chartType="'treemap'"
          :isWidget="false"
          :showPeriodSelect="false"
          :hideUserFilter="true"
          @reportLoaded="reportLoaded"
        ></FNewAnalyticModularReport>
      </el-container>
      <el-aside width="350px" class="h100">
        <WorkplaceAnalyticsSidebar
          @departmentChanged="getDepartmentChanges"
          :departmentData="dataDepartmentList"
          :departmentInfo="departmentInfo"
        ></WorkplaceAnalyticsSidebar>
      </el-aside>
    </el-container>
  </div>
</template>

<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FNewAnalyticModularReport from 'src/pages/energy/analytics/components/FNewAnalyticModularReport'
import WorkplaceAnalyticsSidebar from 'src/pages/workplaceAnalytics/WorkplaceAnalyticsSidebar.vue'
import WorkplaceAnalyticsLeagend from 'src/pages/workplaceAnalytics/WorkplaceAnalyticsLeagend.vue'
import isEqual from 'lodash/isEqual'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  mixins: [AnalyticsMixin],
  components: {
    FNewAnalyticModularReport,
    WorkplaceAnalyticsSidebar,
    WorkplaceAnalyticsLeagend,
  },
  props: ['floorId', 'buildingId'],
  data() {
    let buildingUserFilter = {
      fieldId: 1614157,
      dataType: 7,
      chooseValue: {
        type: 1,
        values: [],
        otherEnabled: false,
      },
      name: 'Building',
      component: {
        componentType: 1,
        availableComponents: [1, 2],
        dataType: 7,
      },
      defaultValues: [],
      values: this.buildingId ? [this.buildingId] : [],
      allValues: [],
    }
    let floorUserFilter = {
      fieldId: 1614158,
      dataType: 7,
      chooseValue: {
        type: 1,
        values: [],
        otherEnabled: false,
      },
      name: 'Floor',
      component: {
        componentType: 1,
        availableComponents: [1, 2],
        dataType: 7,
      },
      defaultValues: [],
      values: this.floorId ? [this.floorId] : [],
      allValues: [],
    }
    return {
      departmentInfo: {},
      fullLoading: true,
      childCallbackSyanc: {
        leagendCallback: false,
        reportCallback: false,
      },
      selectedDepartmentIds: [],
      leagendData: {},
      dataDepartmentList: {},
      reportObject: null,
      resultObject: null,
      chartDimensions: {
        width: 980,
        height: 600,
      },
      serverConfig: {
        xField: {
          field_id: 1614158,
          module_id: 137767,
        },
        yField: [null],
        dateField: null,
        groupBy: [
          { field_id: 1682506, module_id: 137767 },
          { field_id: 1614593, module_id: 137767 },
        ],
        criteria: {
          pattern: '(1)',
          conditions: {
            '1': {
              fieldName: 'deskType',
              value: '1',
              columnName: 'Desks.DESK_TYPE',
              operatorId: 54,
              isResourceOperator: false,
              parseLabel: null,
              valueArray: ['1'],
              operatorLabel: 'is',
              active: true,
              operatorsDataType: {
                dataType: 'ENUM',
                displayType: 'SELECTBOX',
              },
              isSpacePicker: false,
            },
          },
          resourceOperator: false,
        },
        having: null,
        sortFields: null,
        sortOrder: null,
        limit: null,
        userFilters: this.floorId ? [floorUserFilter] : [buildingUserFilter],
        moduleType: null,
        moduleName: 'desks',
      },
      moduleObj: {
        moduleName: 'desks',
        moduleDisplayName: 'Desks',
        moduleId: 137767,
      },
    }
  },
  mounted() {
    eventBus.$on('heatmapLoaded', ({ ...departmentInfo }) => {
      this.$nextTick(() => {
        this.departmentInfo = departmentInfo?.departmentInfo
          ? departmentInfo.departmentInfo
          : null
        this.childCallbackSyanc.reportCallback = true
      })
    })
  },
  beforeDestroy() {
    eventBus.$off('heatmapLoaded')
  },
  watch: {
    'reportObject.treeMapData': function(rawData, oldVal) {
      if (!isEqual(rawData, oldVal)) {
        this.prepareDepartment()
      }
    },
    childCallbackSyanc: {
      handler(newVal) {
        if (newVal.leagendCallback && newVal.reportCallback) {
          this.fullLoading = false
        }
      },
      deep: true,
    },
  },
  methods: {
    leagendDataLoaded() {
      this.$nextTick(() => {
        this.childCallbackSyanc.leagendCallback = true
      })
    },
    getDepartmentChanges(ids) {
      this.childCallbackSyanc = {
        leagendCallback: false,
        reportCallback: false,
      }
      this.fullLoading = true
      this.selectedDepartmentIds = ids
      this.setDepartmentCriteria(ids)
    },
    setDepartmentCriteria(departments) {
      let criteria = {
        fieldName: 'department',
        operatorLabel: "isn't",
        active: true,
        value: departments.join(),
        valueArray: departments,
        columnName: 'Desks.DEPARTMENT_ID',
        operatorId: 37,
        operatorsDataType: {
          dataType: 'LOOKUP',
          displayType: 'LOOKUP_POPUP',
        },
        isSpacePicker: false,
      }
      if (departments.length) {
        let pattern = '(1 and 2)'
        this.$set(this.serverConfig.criteria.conditions, '2', criteria)
        this.$set(this.serverConfig.criteria, 'pattern', pattern)
      } else {
        let pattern = '(1)'
        delete this.serverConfig.criteria.conditions['2']
        this.$set(this.serverConfig.criteria, 'pattern', pattern)
      }
    },
    reportLoaded(report, result) {
      this.reportObject = report
      this.resultObject = result
      this.prepareDepartment()
      if (result?.reportData?.data && !result.reportData.data.length) {
        this.$nextTick(() => {
          this.childCallbackSyanc.reportCallback = true
        })
      }
    },

    prepareDepartment() {
      let { reportObject } = this
      this.dataDepartmentList = {}
      if (reportObject?.treeMapData?.children) {
        reportObject.treeMapData.children.forEach(floor => {
          if (floor?.children) {
            floor.children.forEach(department => {
              this.setDepartmentData(department, department.key)
            })
          }
        })
      }
    },
    setDepartmentData(data, departmentId) {
      let { dataDepartmentList } = this
      if (dataDepartmentList[departmentId]?.value) {
        let value = dataDepartmentList[departmentId].value
        this.$set(dataDepartmentList, departmentId, {
          value: data.value + value,
          name: data.name,
          color: data.color,
        })
      } else {
        this.$set(dataDepartmentList, departmentId, {
          value: data.value,
          name: data.name,
          disabled: false,
          color: data.color,
        })
      }
    },
  },
}
</script>

<style lang="scss">
.wp-chart-conatiner {
  width: 100%;
  height: calc(100vh - 100px);
  overflow: hidden;
  background: #f0f0f0;
}
.wp-loading {
  position: absolute;
  width: 100%;
  background: #fff;
  height: 100%;
  z-index: 3;
}
.treemap-ct-section {
  padding-left: 90px;
  padding-right: 90px;
}
</style>
