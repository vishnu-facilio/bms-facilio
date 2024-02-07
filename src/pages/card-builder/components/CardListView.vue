<template>
  <div v-if="data && data.target === 'popup'">
    <el-dialog
      :title="data.title || 'VIEW'"
      v-if="visible"
      :visible.sync="visible"
      :append-to-body="true"
      custom-class="f-popup-view fc-dialog-center-body-p0 fc-dialog-center-body-m0 fc-card-popup-list-view"
      top="0%"
      :before-close="beforeClose"
    >
      <template slot="title">
        <div class="flex-middle justify-content-space" v-if="!isOperationsApp">
          <div class="label-txt-black fwBold">
            {{ data.title || 'VIEW' }}
          </div>
          <div
            class="content analytics-txt mR30 pointer active-txt bold f14"
            @click="openListView"
          >
            Open in List view
            <img
              style="width:13px; height: 9px;"
              src="~statics/icons/right-arrow.svg"
            />
          </div>
        </div>
      </template>
      <div v-if="loading" class="mT40">
        <Spinner :show="loading"></Spinner>
      </div>
      <CustomTable
        v-else
        class="list-view-widget custom-view-table"
        :moduleName="moduleName"
        :viewname="data.view"
        :viewMode="true"
        :filterJSON="filters"
        @setCount="setCount"
        :targetDetail="data.target"
      >
      </CustomTable>
    </el-dialog>
  </div>
  <div v-else></div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import CustomTable from 'src/components/CustomViewTableComponent'
import Spinner from '@/Spinner'
import JumpToHelper from '@/mixins/JumpToHelper'
import { dateOperators } from 'pages/card-builder/card-constants'
import cloneDeep from 'lodash/cloneDeep'
export default {
  props: ['visible', 'closeAction', 'data'],
  components: { CustomTable, Spinner },
  mixins: [JumpToHelper],
  data() {
    return {
      showPopup: false,
      listCount: 0,
      loading: false,
      moduleKPI: [],
      kpiObject: null,
      criteria: null,
      moduleName: 'workorder',
      currentView: 'all',
      filters: {},
      dateOperators,
    }
  },
  mounted() {
    this.executeAction()
  },
  computed: {
    isOperationsApp() {
      let appNameFromUrl = window.location.pathname.slice(1).split('/')[0]
      if (appNameFromUrl === 'operations') {
        return true
      }
      return false
    },
  },
  methods: {
    executeAction() {
      let { data } = this
      this.kpiObject = data.kpi
      let { cardParams } = data
      this.setListViewData(cardParams)
      if (!isEmpty(this.kpiObject.siteId) && this.kpiObject.siteId != -1) {
        this.filters.siteId = {
          operatorId: 36,
          value: [this.kpiObject.siteId.toString()],
        }
      }
      if (!isEmpty(data.cardUserFilters)) {
        let dbFilters = data.cardUserFilters
        if (this.filters) {
          for (let fieldName of Object.keys(dbFilters)) {
            this.$set(this.filters, fieldName, dbFilters[fieldName])
          }
        } else {
          this.filters = dbFilters
        }
      }
      if (data.target === 'self') {
        this.closeAction()
        this.openListView()
      } else if (data.target === 'popup') {
        this.showPopup = true
      } else {
        this.closeAction()
        this.jumpToViewList(this.filters, this.moduleName, true, this.data.view)
      }
    },
    setListViewData(cardParams) {
      this.moduleName = this.kpiObject.moduleName
      this.criteria = this.kpiObject.criteria
      let filterJSON = this.$helpers.criteriaToFilters(this.criteria)
      if (!isEmpty(filterJSON) && typeof filterJSON == 'object') {
        let cloneFilterJson = {}
        for (let key in filterJSON) {
          if (!isEmpty(key) && key.includes('.')) {
            cloneFilterJson[key.split('.')[1]] = filterJSON[key]
          } else {
            cloneFilterJson[key] = filterJSON[key]
          }
        }
        filterJSON = cloneDeep(cloneFilterJson)
      }
      if (cardParams && cardParams.dateRange) {
        //if  kpi card  has date range
        //when dashboard timeline filters present ,use timeline filter to generate date Range criteria in filter json
        if (!isEmpty(this.data.cardFilters)) {
          let cardFilters = this.data.cardFilters
          if (!isEmpty(cardFilters)) {
            let dateFilter = {
              //Cant send offset for MONTH=AUGUST ,JULY kinda cases as ,newdate pickers doesn't emit the offset->only gives ,start and end TS+operator , so use date-operator=20(CUSTOM) always
              operatorId: 20,
              value: [
                String(cardFilters.startTime),
                String(cardFilters.endTime),
              ],
            }

            let dateFieldName = cardFilters.dateField
            this.$set(filterJSON, dateFieldName, dateFilter)
          }
        } else {
          // find date field and generate date Range criteria in filter json to apply to list view
          let operatorId = this.dateOperators.find(
            rt => rt.value === cardParams.dateRange
          ).enumValue
          let fieldName = 'createdTime'
          if (
            this.kpiObject.dateFieldId &&
            this.kpiObject.dateFieldId > 0 &&
            this.kpiObject.dateFieldName
          ) {
            fieldName = this.kpiObject.dateFieldName
            this.$set(filterJSON, fieldName, { operatorId: operatorId })
          }
        }
      }
      if (filterJSON) {
        this.filters = filterJSON
      }
    },
    setCount(listCount) {
      this.listCount = listCount
    },
    beforeClose() {
      this.closeAction()
    },
    openListView() {
      this.jumpToViewList(this.filters, this.moduleName, false, this.data.view)
    },
  },
}
</script>
