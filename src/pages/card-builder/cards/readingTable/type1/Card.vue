<template>
  <div class=" height100 reading-card-table">
    <card-loading v-if="isLoading"></card-loading>
    <div
      v-else
      class="height100 reading-table fc-list-card-table-container fc-list-view fc-list-table-container fc-table-td-height fc-table-viewchooser pB100"
    >
      <div
        class=" fc-widget-header card-header-block f15 bold d-flex"
        v-if="showHeader"
      >
        {{ cardData.title || 'Reading Table' }}
      </div>
      <div :class="cardStyle.theme">
        <el-table
          :data="invtableData"
          border
          :fit="false"
          class=""
          width="100%"
          height="100%"
          @header-dragend="setColWidth"
          v-if="invert"
        >
          <template v-for="(col, index) in invertColumns">
            <el-table-column
              :prop="col.prop"
              :label="col.label"
              width="200"
              :key="index"
            >
              <template v-slot="scope">
                <div
                  class="reading-table-row"
                  v-bind:class="{ setVisibleAction: setVisibleIv(scope) }"
                >
                  <el-tooltip
                    v-if="getTooltipDataIv(scope) && !isDashboardEdit"
                    class="item"
                    effect="dark"
                    :content="getTooltipDataIv(scope)"
                    placement="bottom"
                  >
                    <div
                      class="jumptocell"
                      @click.stop="jumpToModulesIv(scope, 'trend')"
                    >
                      {{ scope.row[col.prop] }}
                      <el-button
                        size="mini"
                        class="mg-20 set-btn visibilityHideAction"
                        @click.stop="jumpToModulesIv(scope, 'set')"
                        >Set</el-button
                      >
                    </div>
                  </el-tooltip>
                  <div v-else>{{ scope.row[col.prop] }}</div>
                </div>
              </template>
            </el-table-column>
          </template>
        </el-table>
        <el-table
          :id="`${getUniqueId}`"
          border
          :data="tableData"
          :fit="false"
          class=""
          width="100%"
          height="100%"
          :cell-style="cellStyle"
          @header-dragend="setColWidth"
          :span-method="objectSpanMethod"
          v-else
        >
          <template v-for="(column, index) in cardData.columns">
            <el-table-column
              :key="index"
              :prop="column.fieldName"
              :label="column.label"
              :width="column.width || 200"
            >
              <template v-slot="scope">
                <div
                  class="reading-table-row"
                  v-bind:class="{ setVisibleAction: setVisible(scope) }"
                >
                  <div v-if="column.type === 'ACTION'">
                    <!-- {{ scope }} -->
                    <template v-if="scope.row[column.fieldName].length === 1">
                      <el-button size="mini">{{
                        scope.row[column.fieldName][0].message || 'Open URL'
                      }}</el-button>
                    </template>
                    <template v-else>
                      <el-dropdown>
                        <el-button size="mini">
                          Go to<i class="el-icon-arrow-down el-icon--right"></i>
                        </el-button>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item
                            v-for="(action, index) in scope.row[
                              column.fieldName
                            ]"
                            :key="index"
                          >
                            <div @click="actionHandle(action)">
                              {{ action.message || 'open URL' }}
                            </div>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </el-dropdown>
                    </template>
                  </div>
                  <el-tooltip
                    v-else-if="getTooltipData(scope) && !isDashboardEdit"
                    class="item"
                    effect="dark"
                    :content="getTooltipData(scope)"
                    placement="bottom"
                  >
                    <div
                      class="jumptocell"
                      @click.stop="jumpToModules(scope, 'trend')"
                    >
                      {{ scope.row[column.fieldName] }}
                      <el-button
                        size="mini"
                        class="mg-20 set-btn visibilityHideAction"
                        @click.stop="jumpToModules(scope, 'set')"
                        >Set</el-button
                      >
                    </div>
                  </el-tooltip>

                  <div v-else>
                    {{ scope.row[column.fieldName] }}
                  </div>
                </div>
              </template>
            </el-table-column>
          </template>
        </el-table>
      </div>
    </div>
    <CardTrend
      :visible.sync="showTableTrend"
      :closeAction="closeTrendPop"
      :trendData="trendData"
      v-if="showTableTrend"
    ></CardTrend>
    <SetReadingPopup
      v-if="controlPopup.visible"
      :saveAction="resetControlValue"
      :recordId="controlPopup.reading.recordId"
      :fieldId="controlPopup.reading.fieldId"
      :closeAction="resetControlValue"
      class="setReadingDialog"
    ></SetReadingPopup>
  </div>
</template>

<script>
import Card from '../base/Card'
import cardHelper from 'pages/card-builder/card-helpers.js'
import CardTrend from 'pages/card-builder/components/TableTrend'
import JumpToHelper from '@/mixins/JumpToHelper'
import SetReadingPopup from '@/readings/SetReadingValue'
import {
  dateOperators,
  aggregateFunctions,
} from 'pages/card-builder/card-constants'
import { isEmpty, isUndefined } from '@facilio/utils/validation'
export default {
  mixins: [JumpToHelper, cardHelper],
  components: { CardTrend, SetReadingPopup },
  extends: Card,
  data() {
    return {
      invert: false,
      invertRows: [],
      tableConfig: {},
      invertColumns: [],
      invtableData: [],
      tableData: [],
      trendData: null,
      showTableTrend: false,
      controlpointsload: [],
      controlPopup: {
        visible: false,
        reading: {
          fieldId: null,
          recordId: null,
        },
      },
    }
  },
  watch: {
    cardData: {
      handler: function() {
        this.getTableData()
      },
      deep: true,
    },
  },
  computed: {
    showHeader() {
      let { hideHeader } = this.cardData
      if (!isUndefined(hideHeader)) {
        return !hideHeader
      }
      return true
    },
    getUniqueId() {
      return (
        Date.now().toString(36) +
        Math.random()
          .toString(36)
          .substr(2, 5)
      ).toLowerCase()
    },
    cardParam() {
      if (this.cardParams) {
        return this.cardParams
      } else if (this.cardDataObj) {
        return this.cardDataObj
      } else {
        return null
      }
    },
    groupRowFieldName() {
      let { group_row_fieldName } = this.cardStyle
      let { result_group_row_fieldName } = this.cardData
      if (!isEmpty(result_group_row_fieldName)) {
        return result_group_row_fieldName
      } else if (!isEmpty(group_row_fieldName)) {
        return group_row_fieldName
      }
      return null
    },
  },
  mounted() {
    this.loadControlPoints()
    if (this.cardParams.displayMode) {
      this.inveTable()
    } else {
      this.getTableData()
    }
  },
  methods: {
    cellStyle({ rowIndex, columnIndex }) {
      let cellData = this.getCellData(rowIndex, columnIndex)
      if (cellData.styles) {
        return cellData.styles
      }
      return {}
    },
    getStyles(scope, colIndex) {
      let { $index } = scope
      let cellData = this.getCellData($index, colIndex)
      if (cellData.styles) {
        return cellData
      }
      return {}
    },
    getCellData(rowIndex, colIndex) {
      let { rows } = this.cardData
      return rows[rowIndex][colIndex]
    },
    applyGroupingOrder(rows, groupingOrder) {
      let { list, value, field } = groupingOrder
      let { columns } = this.cardData
      let colIndex = columns.findIndex(rt => rt.fieldName === field)
      let newRows = []
      let mergedList = []
      let listOfIds = [...new Set(rows.map(rt => rt[colIndex][value]))]
      mergedList = [...new Set([...list, ...listOfIds])] // merge the list and rows list
      mergedList.forEach(id => {
        let lst = []
        lst = rows.filter(data => {
          if (data[colIndex][value] === id) {
            return data
          }
        })
        newRows = [...newRows, ...lst]
      })
      return newRows.length === rows.length ? newRows : rows
    },
    applyTableGrouping(originalKey) {
      // to be removed urgent demo work
      let { tableData, columns, tableConfig } = this
      // tableConfig = {}
      if (!isEmpty(tableData)) {
        let key = originalKey || 'problem'
        let list = [...new Set(tableData.map(rt => rt[key]))]
        list.forEach(data => {
          let index = tableData.findIndex(rt => rt[key] === data)
          let length = tableData.filter(rt => rt[key] === data).length
          // this.$set(d, index, { rowspan: length, colspan: 1 })
          this.$set(tableConfig, index, { rowspan: length, colspan: 1 })
        })
      }
    },
    transpose() {
      let original = this.cardData.rows
      let copy = []
      for (let i = 0; i < original.length; ++i) {
        for (let j = 0; j < original[i].length; ++j) {
          if (original[i][j] === undefined) continue
          if (copy[j] === undefined) copy[j] = []
          copy[j][i] = original[i][j]
        }
      }
      return copy
    },
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      let { tableConfig } = this
      if (!isEmpty(tableConfig) && Object.keys(tableConfig).length) {
        if (columnIndex === 0) {
          if (tableConfig[rowIndex]) {
            return tableConfig[rowIndex]
          } else {
            return {
              rowspan: 0,
              colspan: 0,
            }
          }
        }
      }
    },
    inveTable() {
      let columns = []
      let rows = []
      let label
      if (this.cardData.columns[0].label) {
        label = this.cardData.columns[0].label
      } else if (this.cardData.columns[0].value) {
        label = this.cardData.columns[0].value
      }
      let firstIndex = {
        prop: 'fr',
        label: label,
      }
      columns.push(firstIndex)
      this.cardData.rows.forEach(rt => {
        let col = {
          prop: rt[0].parentId + '',
          label: rt[0].value,
        }
        columns.push(col)
      })
      rows = this.transpose()
      rows.splice(0, 1)
      rows.forEach((rt, index) => {
        if (rt.length) {
          let col = {
            ...this.cardData.columns[index + 1],
            ...{
              parentId: 'fr',
              value: this.cardData.columns[index + 1].label,
            },
          }
          rt.splice(0, 0, col)
        }
      })
      this.prepareInvTableData(rows, columns)
      this.invert = true
    },
    prepareInvTableData(rows, columns) {
      this.invertRows = []
      this.invertColumns = []
      this.invertColumns = columns
      this.invertRows = rows
      this.invtableData = []
      if (rows && rows.length) {
        rows.forEach(rt => {
          let data = {}
          columns.forEach((rl, idx) => {
            if (
              rt[idx].hasOwnProperty('value') &&
              rt[0].type == 'reading' &&
              rt[idx].value !== null
            ) {
              let val = this.formatDecimal(rt[idx].value)
              let unit = rt[idx].unit || ''
              this.$set(data, rl.prop, val + ' ' + unit)
            } else {
              this.$set(data, rl.prop, '---')
            }
          })
          this.invtableData.push(data)
        })
      }
    },
    setColWidth(currentWidth, preWidth, celldata) {
      let { property } = celldata
      let { columns } = this.widget.dataOptions.cardParams
      let colIndex = columns.findIndex(
        rt =>
          rt.fieldName === property ||
          (rt.type == 'reading' && rt.fieldObj === property)
      )
      this.$set(columns[colIndex], 'width', currentWidth)
    },
    loadControlPoints() {
      let { filters } = this
      let self = this
      self.loading = true
      let url = `v2/controlAction/getControllablePoints?page=1&perPage=50`
      if (filters) {
        url += `&filters=${encodeURIComponent(
          JSON.stringify(filters)
        )}&includeParentFilter=true`
      }
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          this.controlpointsload = response.data.result.controllablePoints
            ? response.data.result.controllablePoints
            : []
          this.loading = false
        }
      })
    },
    isControllable(id) {
      let obj = this.controlpointsload.filter(function(point) {
        return point.resourceId == id
      })
      if (obj.length > 0) {
        return true
      }
    },
    setVisibleIv(data) {
      let { property } = data.column
      let { $index } = data
      let colIndex = this.invertColumns.findIndex(rt => rt.prop === property)
      let rowIndex = $index
      let cellData = this.invertRows[rowIndex][colIndex]
      let { linkTo } = cellData
      if (linkTo && linkTo.linkType) {
        if (linkTo.linkType === 'SUMMARY') {
          return false
        } else if (
          linkTo.linkType === 'ANALYTICS' &&
          this.cardParams.columns[rowIndex + 1].controlAction &&
          this.isControllable(linkTo.readings[0].parentId)
        ) {
          return true
        }
        return false
      } else {
        return false
      }
    },
    setVisible(data) {
      let { columns, rows } = this.cardData
      let { property } = data.column
      let { $index } = data
      let colIndex = columns.findIndex(rt => rt.fieldName === property)
      let rowIndex = $index
      let cellData = rows[rowIndex][colIndex]
      let { linkTo } = cellData
      if (linkTo && linkTo.linkType) {
        if (linkTo.linkType === 'SUMMARY') {
          return false
        } else if (
          linkTo.linkType === 'ANALYTICS' &&
          columns[colIndex].controlAction &&
          columns[colIndex].type == 'reading' &&
          this.isControllable(linkTo.readings[0].parentId)
        ) {
          return true
        }
        return false
      } else {
        return false
      }
    },
    getTooltipDataIv(data) {
      let { columnAction } = this.cardStyle
      let { property } = data.column
      let { $index } = data
      let colIndex = this.invertColumns.findIndex(rt => rt.prop === property)
      let rowIndex = $index
      let cellData = this.invertRows[rowIndex][colIndex]
      let { linkTo } = cellData
      if (linkTo && linkTo.linkType) {
        if (linkTo.linkType === 'SUMMARY' && columnAction === 2) {
          return 'Open Graphics'
        } else if (linkTo.linkType === 'SUMMARY' && columnAction === 3) {
          return ''
        } else if (linkTo.linkType === 'SUMMARY') {
          return 'Open Asset'
        } else if (linkTo.linkType === 'ANALYTICS') {
          return 'Show Trend'
        }
        return false
      } else {
        return false
      }
    },
    getTooltipData(data) {
      let { columns, rows } = this.cardData
      let { columnAction } = this.cardStyle
      let { property } = data.column
      let { $index } = data
      let colIndex = columns.findIndex(rt => rt.fieldName === property)
      let rowIndex = $index
      let cellData = rows[rowIndex][colIndex]
      let { linkTo } = cellData

      if (linkTo && linkTo.linkType) {
        if (linkTo.linkType === 'SUMMARY' && columnAction === 2) {
          return 'Open Graphics'
        } else if (linkTo.linkType === 'SUMMARY' && columnAction === 3) {
          return ''
        } else if (linkTo.linkType === 'SUMMARY') {
          return 'Open Asset'
        } else if (linkTo.linkType === 'ANALYTICS') {
          return 'Show Trend'
        } else if (linkTo.linkType === 'URL') {
          return linkTo.message || 'Open URL'
        }
        return false
      } else {
        return false
      }
    },
    jumpToModulesIv(data, type) {
      let { columnAction } = this.cardStyle
      let { buildingId, period } = this.cardData
      let { assetCategoryId } = this.cardParams
      let { property } = data.column
      let { $index } = data
      let colIndex = this.invertColumns.findIndex(rt => rt.prop === property)
      let rowIndex = $index
      let cellData = this.invertRows[rowIndex][colIndex]
      let { linkTo } = cellData
      let jumptoData = {}
      jumptoData = { ...jumptoData, ...linkTo }
      if (buildingId) {
        jumptoData = { ...jumptoData, ...{ buildingId: buildingId } }
      }
      if (this.invertColumns[colIndex].yAggr) {
        let yAggObj = aggregateFunctions.find(
          rt => rt.value === this.invertColumns[colIndex].yAggr
        )
        let yAggr = null
        if (yAggObj.enumValue) {
          yAggr = yAggObj.enumValue
        }
        jumptoData = { ...jumptoData, ...{ yAggr: yAggr } }
      }
      if (period) {
        let operatorId = dateOperators.find(rt => rt.value === period).enumValue
        jumptoData = {
          ...jumptoData,
          ...{
            dateOperator: {
              operatorId: operatorId,
              value: null,
            },
          },
        }
      }
      if (!isEmpty(linkTo) && linkTo.linkType === 'URL') {
        this.executeAction(linkTo)
        return
      }
      if (
        linkTo.linkType === 'ANALYTICS' &&
        this.isControllable(linkTo.readings[0].parentId) &&
        type == 'set'
      ) {
        this.controlPopup.reading.fieldId = linkTo.readings[0].fieldId
        this.controlPopup.reading.recordId = linkTo.readings[0].parentId
        this.setdialogShow(this.controlPopup.reading)
      } else if (linkTo && linkTo.linkType === 'ANALYTICS') {
        this.openTrend(jumptoData)
      } else if (
        linkTo &&
        linkTo.linkType === 'SUMMARY' &&
        columnAction === 2
      ) {
        this.$popupView.openPopup({
          type: 'graphics',
          url: null,
          alt: '',
          dashboardId: '',
          reportId: '',
          graphicsId: null,
          assetId: jumptoData.id,
          target: null,
          graphicsContext: {
            assetId: jumptoData.id,
            assetCategoryId: assetCategoryId,
          },
        })
      } else if (
        linkTo &&
        linkTo.linkType === 'SUMMARY' &&
        columnAction === 3
      ) {
        // ..
      } else {
        this.cardBuilderJumpToHelper(jumptoData)
      }
    },
    actionHandle(linkTo) {
      if (!isEmpty(linkTo) && linkTo.linkType === 'URL') {
        this.executeAction(linkTo)
        return
      }
      return
    },
    jumpToModules(data, type) {
      let { columnAction } = this.cardStyle
      let { columns, rows, period } = this.cardData
      let { sorting } = this.cardParam
      rows = this.sortingRows(rows, sorting, columns)
      let { assetCategoryId, buildingId } = this.cardParams
      let { property } = data.column
      let { $index } = data
      let colIndex = columns.findIndex(rt => rt.fieldName === property)
      let rowIndex = $index
      let cellData = rows[rowIndex][colIndex]
      let { linkTo } = cellData
      let jumptoData = {}
      jumptoData = { ...jumptoData, ...linkTo }
      if (buildingId) {
        jumptoData = { ...jumptoData, ...{ buildingId: buildingId } }
      }
      if (!isEmpty(linkTo) && linkTo.linkType === 'URL') {
        this.executeAction(linkTo)
        return
      }
      if (columns[colIndex].yAggr) {
        let yAggObj = aggregateFunctions.find(
          rt => rt.value === columns[colIndex].yAggr
        )
        let yAggr = null
        if (yAggObj.enumValue) {
          yAggr = yAggObj.enumValue
        }
        jumptoData = { ...jumptoData, ...{ yAggr: yAggr } }
      }
      if (period) {
        let operatorId = dateOperators.find(rt => rt.value === period).enumValue
        jumptoData = {
          ...jumptoData,
          ...{
            dateOperator: {
              operatorId: operatorId,
              value: null,
            },
          },
        }
      }
      if (
        linkTo.linkType === 'ANALYTICS' &&
        columns[colIndex].controlAction &&
        columns[colIndex].type == 'reading' &&
        this.isControllable(linkTo.readings[0].parentId) &&
        type == 'set'
      ) {
        this.controlPopup.reading.fieldId = linkTo.readings[0].fieldId
        this.controlPopup.reading.recordId = linkTo.readings[0].parentId
        this.setdialogShow(this.controlPopup.reading)
      } else if (linkTo && linkTo.linkType === 'ANALYTICS') {
        this.openTrend(jumptoData)
      } else if (
        linkTo &&
        linkTo.linkType === 'SUMMARY' &&
        columnAction === 2
      ) {
        this.$popupView.openPopup({
          type: 'graphics',
          url: null,
          alt: '',
          dashboardId: '',
          reportId: '',
          graphicsId: null,
          assetId: jumptoData.id,
          target: null,
          graphicsContext: {
            assetId: jumptoData.id,
            assetCategoryId: assetCategoryId,
          },
        })
      } else if (
        linkTo &&
        linkTo.linkType === 'SUMMARY' &&
        columnAction === 3
      ) {
        // ..
      } else {
        this.cardBuilderJumpToHelper(jumptoData)
      }
    },
    executeAction(urlAction) {
      let selfHost = window.location.protocol + '//' + window.location.host
      if (urlAction.url) {
        let url = this.applyLiveVariables(urlAction.url, true)
        if (urlAction.target === 'self') {
          if (url.startsWith(selfHost)) {
            if (
              this.$helpers.isLicenseEnabled('NEW_LAYOUT') &&
              url.startsWith(selfHost + '/app/em/newdashboard/')
            ) {
              url = url.replace(
                selfHost + '/app/em/newdashboard/',
                selfHost + '/app/home/dashboard/'
              )
            }
            let appUrl = url.replace(selfHost, '')
            this.$router.push({ path: appUrl })
          } else {
            window.location.href = url
          }
        } else if (urlAction.target === 'popup') {
          this.$popupView.openPopup({
            type: urlAction.linkType,
            url: urlAction.url,
            alt: '',
            dashboardId: '',
            reportId: '',
            target: urlAction.target,
          })
        } else {
          window.open(url, '_blank')
          this.$emit('showUrl', false)
        }
      }
    },
    closeTrendPop() {
      this.showTableTrend = false
    },
    resetControlValue() {
      this.controlPopup = {
        visible: false,
        reading: {
          fieldId: null,
          recordId: null,
        },
      }
    },
    openTrend(data) {
      this.trendData = data
      this.showTableTrend = true
    },
    setdialogShow(value) {
      this.controlPopup.visible = true
      this.controlPopup.reading = value
    },
    sortingRows(rows, sorting, columns) {
      let { fieldName, order } = sorting
      if (!isEmpty(fieldName) && !isEmpty(order) && !isEmpty(rows)) {
        let rowIndex = columns.findIndex(rt => rt.fieldName === fieldName)
        const rows_map= {}
        rows.forEach((row, index) =>{
          rows_map[row[rowIndex].value] = row
        })
        if(!isEmpty(rows_map))
        {
          let sorted_rows_map = Object.keys(rows_map).sort().reduce(function (result, key) {
            result[key] = rows_map[key]
            return result
          }, {})
          if(!isEmpty(sorted_rows_map)){
            if(order == 'dsc'){
              let asc_sorted_vals =  Object.values(sorted_rows_map)
              return asc_sorted_vals.reverse()
            }
            return Object.values(sorted_rows_map)
          }
        }
      }
      return rows
    },
    getTableData() {
      let { columns, rows, groupingOrder } = this.cardData
      let { sorting } = this.cardParam
      this.tableData = []
      this.tableConfig = {}
      if (!isUndefined(sorting)) {
        rows = this.sortingRows(rows, sorting, columns)
      }
      if (
        !isUndefined(groupingOrder) &&
        groupingOrder &&
        groupingOrder.list &&
        groupingOrder.field &&
        groupingOrder.value
      ) {
        rows = this.applyGroupingOrder(rows, groupingOrder)
      }
      if (rows && rows.length) {
        rows.forEach(rt => {
          let data = {}
          columns.forEach((rl, idx) => {
            if (rl.type === 'reading') {
              if (!isEmpty(rl.fieldObj) && rl.fieldName != rl.fieldObj) {
                rl.fieldName = rl.fieldObj
              }
              if (rt[idx].hasOwnProperty('value') && rt[idx].value !== null) {
                let readings = this.formatDecimal(rt[idx].value)
                let unit = rt[idx].unit || ''
                this.$set(data, rl.fieldName, readings + ' ' + unit)
              } else {
                this.$set(data, rl.fieldName, '---')
              }
            } else {
              if (rt[idx].dataType == 'DATE_TIME' && !isNaN(rt[idx].value)) {
                rt[idx].value = this.getUserDateTime(rt[idx].value)
              }
              this.$set(data, rl.fieldName, rt[idx].value)
            }
          })
          this.tableData.push(data)
        })
      }
      if (
        !isUndefined(sorting) &&
        sorting &&
        sorting.sortingType &&
        sorting.sortingType === 'relativeSort'
      ) {
        this.applyTableGrouping()
      }
    },
    applyLiveVariables(str, applyRawValue) {
      if (!str) {
        return str
      }
      let readingVariables = []
      let matched = str.match(/[^\\${}]+(?=\})/g)
      if (matched && matched.length) {
        readingVariables.push(...matched)
      }

      if (readingVariables && readingVariables.length) {
        for (let rv of readingVariables) {
          let liveVal = null
          let replaceVal = ''
          if (liveVal) {
            if (applyRawValue) {
              replaceVal = liveVal.value
            } else {
              replaceVal = liveVal.label
            }
          }
          if (!replaceVal) {
            replaceVal = ''
          }
          str = str.replace('${' + rv + '}', replaceVal)
        }
      }
      return str
    },
  },
}
</script>
<style scoped>
.setReadingDialog .f-dialog-content .control-action-reading-field {
  width: 100% !important;
}
.mg-20 {
  margin-left: 20px;
}
.set-btn {
  background: #39b2c2;
  color: #fff;
  border-radius: 3px;
}
.set-btn:hover {
  background: #33a6b5;
}
.setVisibleAction:hover .visibilityHideAction {
  visibility: visible;
}

.visibilityHideAction {
  visibility: hidden;
}
</style>
<style lang="scss">
/* reading table css*/
.reading-table {
  td {
    height: 0 !important;
    padding: 0;
  }

  td .cell {
    height: 100% !important;
  }

  // .el-table td,
  // .el-table th {
  //   padding: 0px !important;
  // }

  .smart-td-cell {
    padding-top: 12px;
    padding-bottom: 12px;
    padding-left: 20px;
    padding-right: 20px;
    height: 100%;
  }
}
</style>
