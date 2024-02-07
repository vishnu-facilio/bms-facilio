<template>
  <div class="height100">
    <div
      class="pm-reading-table-settings row"
      v-if="resources && resources.length"
    >
      <div class="fL col-6 text-left">
        <el-select
          v-model="resourceId"
          class="transparent-select-box"
          @change="getReadingData"
        >
          <el-option
            v-for="(resource, index) in resources"
            :key="index"
            :label="resource.name"
            :value="resource.id"
          >
          </el-option>
        </el-select>
      </div>
      <new-date-picker
        v-if="!hidedatefilter && !config.hidedatefilter"
        ref="newDatePicker"
        class="fR text-right col-6"
        :zone="$timezone"
        :dateObj="dateFilter"
        @date="setDateFilter"
      ></new-date-picker>
    </div>
    <div class="width100 overflow-hidden">
      <spinner v-if="loading" :show="loading" size="80"></spinner>
      <template v-else-if="!loading && tabeldata.length">
        <HotTable
          class="pmhottable"
          :style="styleCss"
          ref="hotTable"
          :root="root"
          :settings="hotSettings"
        ></HotTable>
      </template>
      <div v-else-if="!tabeldata.length" class="no-pm-reading-data">
        <inline-svg
          src="svgs/emptystate/reportlist"
          iconClass="icon text-center icon-xxxlg"
        ></inline-svg>
        <div class="nowo-label f14">
          There are no work orders in this time period.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NewDatePicker from '@/NewDatePicker'
import HotTable from '@handsontable/vue'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import { API } from '@facilio/api'

export default {
  props: ['pmObject', 'pm', 'workorder', 'config', 'settings'],
  components: {
    HotTable,
    NewDatePicker,
  },
  data() {
    return {
      root: 'tabular-pm-hot',
      triggerFrequencyInt: 1,
      hidedatefilter: true,
      units: [],
      frequencyMap: {
        DAILY: 'hh:mm a',
        WEEKLY: 'ddd',
        MONTHLY: 'MMM YYYY',
        QUARTERTLY: 'MMM YYYY',
        HALF_YEARLY: 'MMM YYYY',
        ANNUALLY: 'YYYY',
      },
      frequencyToDateForamterMap: {
        DAILY: {
          day: 'hh:mm a',
          week: 'ddd',
          month: 'DD MMM YYYY',
          quarter: 'DD MMM YYYY',
          year: 'DD MMM YYYY',
        },
        WEEKLY: {
          day: 'hh:mm a',
          week: 'ddd',
          month: 'MMM YYYY',
          quarter: 'MMM YYYY',
          year: 'YYYY',
        },
        MONTHLY: {
          day: 'hh:mm a',
          week: 'ddd',
          month: 'MMM YYYY',
          quarter: 'MMM YYYY',
          year: 'YYYY',
        },
        QUARTERTLY: {
          day: 'hh:mm a',
          week: 'ddd',
          month: 'MMM YYYY',
          quarter: 'MMM YYYY',
          year: 'YYYY',
        },
        HALF_YEARLY: {
          day: 'hh:mm a',
          week: 'ddd',
          month: 'MMM YYYY',
          quarter: 'MMM YYYY',
          year: 'YYYY',
        },
        ANNUALLY: {
          day: 'hh:mm a',
          week: 'ddd',
          month: 'MMM YYYY',
          quarter: 'MMM YYYY',
          year: 'YYYY',
        },
      },
      loading: false,
      resourceId: null,
      tabeldata: [],
      updateData: [],
      dateFilter: NewDateHelper.getDatePickerObject(22),
      sectionRows: [],
      taskRows: [],
      hotSettings: {
        data: [],
        columns: [],
        colHeaders: false,
        rowHeaders: false,
        rowHeaderWidth: 200,
        width: '100%',
        height: 200,
        renderAllRows: true,
        readOnly: true,
        columnSorting: false,
        stretchH: 'all',
        fillHandle: false,
        autoWrapRow: true,
        colWidths: 180,
      },
      hotTable: null,
      tableState: {
        // table config to save
        columns: [],
      },
      initialRendered: false,
    }
  },
  computed: {
    styleCss() {
      return 'width: 100% !important;'
    },
    resources() {
      if (
        this.pm &&
        this.pm.pmCreationType === 2 &&
        this.pmObject &&
        this.pmObject.preventivemaintenance &&
        this.pmObject.preventivemaintenance &&
        this.pmObject.preventivemaintenance.resourcePlanners
      ) {
        let data = []
        this.pmObject.preventivemaintenance.resourcePlanners.forEach(rt => {
          data.push(rt.resource)
        })
        return data
      } else if (
        this.pm &&
        this.pm.pmCreationType === 1 &&
        this.workorder &&
        this.workorder.resource
      ) {
        return [this.workorder.resource]
      } else {
        return []
      }
    },
    triggers() {
      if (this.pm && this.pm.triggers && this.pm.triggers.length) {
        return this.pm.triggers
      }
      return []
    },
    mergedTaskList() {
      if (this.pmObject && this.pmObject.taskList) {
        let list = []
        Object.values(this.pmObject.taskList).forEach(rt => {
          Object.values(rt).forEach(rl => {
            list.push(rl)
          })
        })
        return list
      } else {
        return []
      }
    },
    sectionHierarchy() {
      let sections = []
      if (this.pmObject && this.pmObject.taskList) {
        let keys = Object.keys(this.pmObject.taskList)
        keys.forEach(i =>
          sections.push({ section: i, tasks: this.pmObject.taskList[i] })
        )
      }
      return sections
    },
  },
  created() {
    API.get('/v2/getReadingFieldUnits').then(({ data, error }) => {
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.units = this.$getProperty(
          data,
          'readingFieldUnits.orgUnitsList',
          null
        )
      }
    })
  },
  mounted() {
    let self = this
    if (this.config) {
      if (this.config.operatorId) {
        this.dateFilter = NewDateHelper.getDatePickerObject(
          this.config.operatorId,
          this.config.dateValueString
        )
      }

      if (this.config.resourceId) {
        this.resourceId = this.config.resourceId
      }
    }

    this.$nextTick(rt => {
      if (this.settings) {
        Object.keys(this.settings).forEach(column => {
          self.hotSettings[column] = self.settings[column]
        })

        if (this.$mobile && this.settings.height < 100) {
          this.hotSettings.height = 600
        }
      }
      this.hidedatefilter = false
    })
    if (this.triggers && this.triggers.length) {
      this.triggerFrequencyInt = Math.min(
        ...this.triggers.map(rt => rt.frequency)
      )
    }
    this.init()
  },
  methods: {
    init() {
      this.setDefaultValues()
      if (this.pm) {
        this.getReadingData()
      }
    },
    setDefaultValues() {
      this.resourceId =
        this.resources && this.resources.length ? this.resources[0].id : null
    },
    getReadingData() {
      let self = this
      self.loading = true
      this.tabeldata = []
      let params = {
        pmId: this.pm.id,
        resourceId: this.resourceId,
        startTime:
          this.dateFilter.value && this.dateFilter.value.length
            ? this.dateFilter.value[0]
            : null,
        endTime:
          this.dateFilter.value && this.dateFilter.value.length
            ? this.dateFilter.value[1]
            : null,
      }
      let url = '/v2/workorders/getPreventiveMaintenanceReadings'
      self.$http.post(url, params).then(function(response) {
        if (
          response.data &&
          response.data.result &&
          response.data.result.workorders &&
          response.data.result.workorders.length
        ) {
          self.prepareTableData(response.data.result.workorders)
        }
        self.loading = false
      })
    },
    prepareTableData(data) {
      if (!data) {
        return
      }

      this.tabeldata = data
      data.sort(function(a, b) {
        return a.createdTime - b.createdTime
      })
      this.hotSettings.columns = this.getColData2(data, this.pmObject)
      this.hotSettings.data = this.getData2(data, this.pmObject)
      this.setTableData()
    },
    getColData2(data, pmObject) {
      let d = []
      let self = this
      d.push({ data: 'Task' })
      data.forEach(rt => {
        d.push({
          data: rt.id,
          renderer: function displayFunction(
            instance,
            td,
            row,
            col,
            prop,
            value,
            cellProperties
          ) {
            while (td.firstChild) {
              td.removeChild(td.firstChild)
            }
            let textNode = document.createTextNode(value || '')
            td.appendChild(textNode)
          },
        })
      })
      return d
    },
    displayValue(value, obj) {
      if (value && obj) {
        if (obj.readingFieldUnit > -1) {
          let val =
            this.units.find(rt => rt.unit === obj.readingFieldUnit) &&
            this.units.find(rt => rt.unit === obj.readingFieldUnit).unitEnum &&
            this.units.find(rt => rt.unit === obj.readingFieldUnit).unitEnum
              .symbol
              ? this.units.find(rt => rt.unit === obj.readingFieldUnit).isLeft
                ? this.units.find(rt => rt.unit === obj.readingFieldUnit)
                    .unitEnum.symbol +
                  ' ' +
                  value
                : value +
                  ' ' +
                  this.units.find(rt => rt.unit === obj.readingFieldUnit)
                    .unitEnum.symbol
              : value
          return val
        }
        return value
      }
      return ''
    },
    getColData(data, pmObject) {
      let d = []
      data.forEach(rt => {
        d.push({ type: 'numeric' })
      })
      return d
    },
    mergeTasks(task) {
      if (task && task) {
        let list = []
        Object.values(task).forEach(rt => {
          Object.values(rt).forEach(rl => {
            list.push(rl)
          })
        })
        return list
      } else {
        return []
      }
    },
    getData2(data, pmObject) {
      let allRows = []
      let headerRow = { Task: 'Tasks' }

      data.forEach(workorder => {
        headerRow[workorder.id] = this.$time.format(
          workorder.createdTime,
          'DD MMM YYYY hh:mm a'
        )
      })
      allRows.push(headerRow)
      let row = 0
      this.sectionHierarchy.forEach(s => {
        let sectionRow = { Task: s.section }
        allRows.push(sectionRow)
        row++
        this.sectionRows.push(row)
        s.tasks.forEach(task => {
          let taskRow = { Task: task.subject }
          data.forEach(workorder => {
            let mergedTasks = this.mergeTasks(workorder.tasks)
            for (let i = 0; i < mergedTasks.length; i++) {
              let t = mergedTasks[i]
              if (
                this.$account.org.id === 263 ||
                this.$account.org.id === 146
              ) {
                if (t.uniqueId === task.uniqueId) {
                  taskRow[workorder.id] = t.inputValue || null
                }
              } else {
                if (t.readingFieldId === task.readingFieldId) {
                  taskRow[workorder.id] = t.inputValue || null
                }
              }
            }
          })
          allRows.push(taskRow)
          row++
          this.taskRows.push(row)
        })
      })
      return allRows
    },
    getData(data, pmObject) {
      let d = []
      let self = this
      self.mergedTaskList.forEach(rt => {
        let d1 = []
        data.forEach(rl => {
          let tasks = []
          tasks = self.mergeTasks(rl.tasks)
          if (tasks.length) {
            let dat = tasks.find(rx => rx.readingFieldId === rt.readingFieldId)
              ? tasks.find(rx => rx.readingFieldId === rt.readingFieldId)
                  .inputValue
              : null
            d1.push(dat)
          }
        })
        d.push(d1)
      })
      return d
    },
    getColHeader(data, pmObject) {
      let self = this
      let d = []
      if (this.triggers && this.triggers.length) {
        data.forEach(rt => {
          d.push(
            self.$time.format(
              rt.createdTime,
              this.getDateFormater(
                this.triggers.find(
                  r => r.frequency === this.triggerFrequencyInt
                ).frequencyEnum
              )
            )
          )
        })
      } else {
        data.forEach(rt => {
          d.push(self.$time.format(rt.createdTime, 'DD MMM YYYY hh:mm a'))
        })
      }
      return d
    },
    getDateFormater(FreqEnum) {
      if (
        this.frequencyToDateForamterMap[FreqEnum] &&
        this.frequencyToDateForamterMap[FreqEnum][this.dateFilter.operationOn]
      ) {
        return this.frequencyToDateForamterMap[FreqEnum][
          this.dateFilter.operationOn
        ]
      }
      return this.frequencyMap[FreqEnum]
    },
    getRowHeader(data, pmObject) {
      let rowHeaders = []

      for (let i = 0; i < this.sectionHierarchy.length; i++) {
        rowHeaders.push(this.sectionHierarchy[i].section)
        let noOfTasks = this.sectionHierarchy[i].tasks.length
        for (let j = 0; j < noOfTasks; j++) {
          rowHeaders.push(this.sectionHierarchy[i].tasks[j].subject)
        }
      }

      return rowHeaders
    },
    setTableData() {
      let self = this

      this.hotSettings.afterChange = function(from, to) {
        // self.updatedata(from, to)
        self.updatedata2(from, to)
      }
      this.hotSettings.afterColumnMove = function(from, to) {
        self.saveCurrentState()
      }

      this.hotSettings.afterColumnResize = function(col, width) {
        self.saveCurrentState()
        self.setTableHeight()
      }

      this.hotSettings.afterRender = function() {
        if (!self.hotTable && self.$refs['hotTable']) {
          self.hotTable = self.$refs['hotTable'].table
          self.setTableHeight()
        }
        if (!self.initialRendered) {
          self.initialRendered = true
        }
      }

      this.hotSettings.afterRenderer = function(
        td,
        row,
        col,
        prop,
        value,
        cellProperties
      ) {
        if (row === 0) {
          td.classList.add('fhot-table-header')
        } else if (self.sectionRows.includes(row)) {
          td.classList.add('fhot-section-row')
          if (col === 0) {
            td.classList.add('fhot-section-cell')
          }
        } else if (col === 0 && !self.sectionRows.includes(row)) {
          td.classList.add('fhot-task-cell')
        } else {
          td.classList.add('fhot-data-cell')
        }
      }

      this.hotSettings.mergeCells = []
      this.sectionRows.forEach(row =>
        this.hotSettings.mergeCells.push({
          row: row,
          col: 0,
          colspan: 4,
          rowspan: 1,
        })
      )
    },
    convertreadableObj(data) {
      return {
        row: data[0],
        col: data[1],
        pre: data[2],
        val: data[3],
      }
    },
    validData(editObj, tableData) {
      if (editObj.pre == null && editObj.val) {
        if (!isNaN(Number(editObj.val))) {
          return true
        }
      } else if (editObj.pre !== editObj.val) {
        if (
          !isNaN(Number(editObj.val) && tableData.inputTypeEnum === 'READING')
        ) {
          return true
        } else {
          return false
        }
      }
      return false
    },
    updatedata2(data, source) {
      let self = this
      if (source === 'edit' && data) {
        if (data[0] && data[0].length) {
          let editObj = this.convertreadableObj(data[0])
          let tableSourceData = {}
          this.tabeldata.find(rt => {
            if (rt.id === editObj.col) {
              tableSourceData = self.mergeTasks(rt.tasks)[editObj.row]
                ? self.mergeTasks(rt.tasks)[editObj.row]
                : null
            }
          })
          if (this.validData(editObj, tableSourceData)) {
            tableSourceData['inputValue'] = editObj.val + ''
            if (this.updateData.find(rt => rt.id === tableSourceData.id)) {
              let index = this.updateData.findIndex(
                rt => rt.id === tableSourceData.id
              )
              this.updateData.splice(index, 1, tableSourceData)
            } else {
              this.updateData.push(tableSourceData)
            }
            this.$emit('readingData', this.updateData) // need to enable after the bulk update fix
          }
        }
      }
    },
    updatedata(data, source) {
      if (source === 'edit' && data) {
        if (data[0] && data[0].length) {
          let editObj = this.convertreadableObj(data[0])
          let tableSourceData = Object.values(this.tabeldata[editObj.col].tasks)
            ? this.mergeTasks(Object.values(this.tabeldata[editObj.col].tasks))[
                editObj.row
              ]
            : null
          if (this.validData(editObj, tableSourceData)) {
            tableSourceData['inputValue'] = editObj.val + ''
            if (this.updateData.find(rt => rt.id === tableSourceData.id)) {
              let index = this.updateData.findIndex(
                rt => rt.id === tableSourceData.id
              )
              this.updateData.splice(index, 1, tableSourceData)
            } else {
              this.updateData.push(tableSourceData)
            }
            this.$emit('readingData', this.updateData) // need to enable after the bulk update fix
          }
        }
      }
    },
    setTableHeight() {},
    saveCurrentState() {
      let stateColumns = []
      for (let i = 0; i < this.hotTable.getColHeader().length; i++) {
        let colWidth = this.hotTable.getColWidth(i)

        let idx = this.hotTable.toPhysicalColumn(i)
        let name = this.hotSettings.columns[idx].name

        let stateColumn = this.tableState.columns.find(col => col.name === name)
        stateColumn.width = colWidth
        stateColumns.push(stateColumn)
      }
      this.tableState.columns = stateColumns

      this.reportConfig.params.tabularState = JSON.stringify(this.tableState)
    },
    setDateFilter(data) {
      this.dateFilter = data
      this.getReadingData()
    },
  },
}
</script>
<style>
.pmhottable th {
  font-size: 12px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
  text-transform: unset !important;
}
.pmhottable td {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
}
.transparent-select-box .el-input .el-input__inner {
  border: 0;
  background: transparent;
  height: 30px;
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.32px;
  color: #324056;
  padding-left: 3px;
}
.transparent-select-box .el-input__suffix {
  height: 35px;
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.32px;
  color: #324056;
  right: -23px;
}
#tabular-pm-hot .handsontableInputHolder {
  width: 145px;
  height: 43px;
}
#tabular-pm-hot textarea.handsontableInput {
  max-width: -webkit-fill-available !important;
  max-height: -webkit-fill-available;
  padding-right: 0px;
}
.no-pm-reading-data {
  clear: both;
  text-align: center;
  align-items: center;
  background: #fff;
  min-height: 230px;
  padding-top: 50px;
}
.fhot-table-header {
  font-weight: 700 !important;
  text-transform: uppercase;
}
</style>
