<template>
  <div class=" height100">
    <div class="fc-widget-header fc-datatable-header-1">
      <div class="fc-header fL">
        {{ data && data.tableName ? data.tableName : 'TABLE' }}
      </div>
    </div>

    <div class="table-filters-section pT5">
      <new-date-picker
        v-if="!rerenderdatefilter"
        :zone="$timezone"
        class="filter-field fR"
        style="margin-left: auto;"
        :dateObj.sync="dateObj"
        @date="changeDateFilter"
      ></new-date-picker>
      <template v-if="data && data.filters">
        <div
          class="fR fc-table-filters-section-select"
          v-if="data.filters.building"
        >
          <el-select
            v-model="updateParams.buildingId"
            filterable
            clearable
            collapse-tags
            @change="applyFilter"
            placeholder="Select Building"
            class="db-filter fc-tag"
            v-tippy
          >
            <el-option
              v-for="(building, index) in buildings"
              :key="index"
              :label="building.name"
              :value="building.id"
            >
            </el-option>
          </el-select>
        </div>
        <div
          class="fR fc-table-filters-section-select"
          v-if="data.filters && data.filters.Assetcategory"
        >
          <el-select
            v-model="updateParams.Assetcategory"
            filterable
            clearable
            collapse-tags
            @change="applyFilter"
            placeholder="Select Asset category"
            class="db-filter fc-tag"
            v-tippy
          >
            <el-option
              v-for="(cat, index) in category"
              :key="index"
              :label="cat.label"
              :value="cat.value"
            >
            </el-option>
          </el-select>
        </div>
      </template>
    </div>
    <shimmer-loading v-if="loading" class="map-shimmer"> </shimmer-loading>
    <div v-else class="height100 smart-table-wrapper">
      <template v-if="!tablererender">
        <el-table
          :id="`smart-table${widget && widget.id ? '-' + widget.id : '-1'}`"
          border
          :data="elTableResult"
          :fit="false"
          class="smart-table"
          :span-method="objectSpanMethod"
          @header-dragend="setColWidth"
          :height="height"
        >
          <template v-for="(config, index) in data.columns">
            <el-table-column
              :key="index"
              :prop="config.key"
              :label="config.label"
              :width="config.width"
            >
              <template v-slot:header>
                <div
                  class="smart-table-header-th row p0 inline width100 align-center relative visibility-visible-actions position-relative"
                >
                  <div class="width100 p0 flex-middle line-height-normal">
                    <div class="headerlabel width100">{{ config.label }}</div>
                  </div>
                  <div
                    class="smart-table-actions pointer"
                    @click="
                      visibility = true
                      selectcolumn = config
                    "
                  >
                    <i
                      class="el-icon-caret-bottom fR condition-icon visibility-hide-actions"
                      v-if="
                        mode &&
                          (config.datatype === 'TEXT' ||
                            config.datatype === 'NUMBER')
                      "
                    ></i>
                  </div>
                </div>
              </template>
              <template v-slot="scope">
                <conditional-value
                  :style="
                    getStyleObj(
                      formatLabel(scope.row[config.key], config),
                      config.conditionalFormatting,
                      config
                    )
                  "
                  class="smart-td-cell"
                  :value="formatLabel(scope.row[config.key], config)"
                  :conditionalFormatting="config.conditionalFormatting || []"
                ></conditional-value>
              </template>
            </el-table-column>
          </template>
        </el-table>
      </template>

      <el-dialog
        custom-class="f-kfi-card-builder fc-dialog-center-container kpi-map-setup-dilaog"
        :append-to-body="true"
        :visible.sync="visibility"
        :width="'50%'"
        title="KPI CARD"
        :before-close="closedialog"
        v-if="visibility"
      >
        <span slot="title" class="dialog-footer">
          <span class="kpi-card-header">SMART TABLE SETTINGS</span>
        </span>
        <div style="height:500px;">
          <smart-table-settings
            :data="data"
            :column="selectcolumn"
          ></smart-table-settings>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="closedialog"
            >Close</el-button
          >
          <el-button type="primary" class="modal-btn-save" @click="save()"
            >OK</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import shimmerLoading from '@/ShimmerLoading'
import SmartTableSettings from '@/SmartTableSettings'
import ConditionalValue from '@/NewConditionalValue'
import criteriaHelper from 'src/util/criteriaHelper.js'
import NewDateHelper from '@/mixins/NewDateHelper'
import NewDatePicker from '@/NewDatePicker'
import moment from 'moment-timezone'
export default {
  props: ['widget', 'config'],
  mixins: [criteriaHelper],
  data() {
    return {
      rerenderdatefilter: false,
      dateObj: NewDateHelper.getDatePickerObject(22),
      category: [
        {
          label: 'All Assets',
          value: 'ALL',
        },
        {
          label: 'Chiller',
          value: 'Chiller',
        },
        {
          label: 'AHU',
          value: 'AHU',
        },
        {
          label: 'Primary Pump',
          value: 'Primary Pump',
        },
        {
          label: 'Secondary Pump',
          value: 'Secondary Pump',
        },
        {
          label: 'FAHU',
          value: 'FAHU',
        },
        {
          label: 'Cooling Tower',
          value: 'Cooling Tower',
        },
        {
          label: 'Condenser Pump',
          value: 'Condenser Pump',
        },
      ],
      updateParams: {
        buildingId: null,
        Assetcategory: null,
        startTime: NewDateHelper.getDatePickerObject(22).value[0],
        endTime: NewDateHelper.getDatePickerObject(22).value[1],
      },
      nameSpace: null,
      tablererender: false,
      visibility: false,
      selectcolumn: null,
      selectedfunction: null,
      height: '',
      loading: true,
      result: null,
      elTableResult: [],
      data: null,
      tableConfig: {},
      categoryReading: {
        Chiller: 'chillerreading',
        AHU: 'ahureading',
        FAHU: 'fahureading',
        'Cooling Tower': 'coolingtowerreading',
        'Primary Pump': 'chillerprimarypumpreading',
        'Secondary Pump': 'chillersecondarypumpreading',
        'Condenser Pump': 'chillercondenserpumpreading',
        ALL: null,
      },
    }
  },
  watch: {
    'config.height': {
      handler(newData, oldData) {
        this.getClientProps()
      },
    },
  },
  computed: {
    mode() {
      if (
        this.$route.query &&
        this.$route.query.create &&
        this.$route.query.create === 'edit'
      ) {
        return true
      } else if (
        this.$route.query &&
        this.$route.query.create &&
        this.$route.query.create === 'new'
      ) {
        return true
      } else {
        return false
      }
    },
    buildings() {
      if (
        this.data &&
        this.data.filters &&
        this.data.filters.outOfScopeBuilding
      ) {
        return [
          {
            name: 'Key Tower A',
            id: 1271170,
          },
          {
            name: 'Key Tower B',
            id: 1271989,
          },
          {
            name: 'Scotia Plaza Office',
            id: 1270886,
          },
          {
            name: 'Scotia Plaza Residence',
            id: 1271017,
          },
          {
            name: 'YLS Towers',
            id: 1270140,
          },
        ]
      } else {
        return [
          {
            name: 'Heard Building',
            id: 1269536,
          },
          {
            name: 'Luhrs Tower',
            id: 1270227,
          },
          {
            name: 'Key Tower A',
            id: 1271170,
          },
          {
            name: 'Key Tower B',
            id: 1271989,
          },
          {
            name: 'Scotia Plaza Office',
            id: 1270886,
          },
          {
            name: 'Scotia Plaza Residence',
            id: 1271017,
          },
          {
            name: 'YLS Towers',
            id: 1270140,
          },
          {
            name: 'Azure Building',
            id: 1270641,
          },
          {
            name: 'Whiteacre Tower',
            id: 1270464,
          },
          {
            name: 'Bridgestone Tower',
            id: 1270843,
          },
          {
            name: 'Monroe Tower',
            id: 1269918,
          },
          {
            name: 'Heron Building',
            id: 1270943,
          },
        ]
      }
    },
  },
  created() {
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    this.loadNameSpaceList()
    this.getClientProps()
    this.getParams()
    this.loadCardData()
    this.rerenderdatefilter = true
    if (this.data.datefilterId) {
      this.dateObj = NewDateHelper.getDatePickerObject(this.data.datefilterId)
      this.updateParams = {
        buildingId: null,
        startTime: NewDateHelper.getDatePickerObject(this.data.datefilterId)
          .value[0],
        endTime: NewDateHelper.getDatePickerObject(this.data.datefilterId)
          .value[1],
        Assetcategory: null,
      }
    }
    setTimeout(() => {
      this.rerenderdatefilter = false
    }, 500)
  },
  components: {
    shimmerLoading,
    SmartTableSettings,
    ConditionalValue,
    NewDatePicker,
  },
  methods: {
    applyFilter() {
      let { updateParams } = this
      this.updateScript(updateParams)
    },
    changeDateFilter(datefilter) {
      this.updateParams.startTime = datefilter.value[0]
      this.updateParams.endTime = datefilter.value[1]
      this.updateScript(this.updateParams)
    },
    updateScript(params) {
      if (this.data.functionId) {
        this.selectedfunction = [].concat
          .apply(
            [],
            this.nameSpace.map(rt => rt.functions)
          )
          .find(rt => rt.id === this.data.functionId)
      }
      if (
        this.data &&
        this.data.filters &&
        this.data.filters.Assetcategory &&
        this.updateParams.Assetcategory
      ) {
        this.data.workflowV2String = `Map test () {
            params = {};
            params["startTime"] = ${params.startTime};
            params["endTime"] = ${params.endTime};
            params["resourceId"] = ${params.buildingId || null};
            params["category"] = "${params.Assetcategory || null}";
            params["categoryReading"] = "${this.categoryReading[
              params.Assetcategory
            ] || null}";

            table = new NameSpace("tabledata");
            result = table.${this.selectedfunction.name}(params);
            log "" + result;
            return result;
          }`
      } else if (this.data.functionId && this.selectedfunction) {
        this.data.workflowV2String = `Map test () {
        params = {};
        params["startTime"] = ${params.startTime};
        params["endTime"] = ${params.endTime};
        params["resourceId"] = ${params.buildingId || null};

        table = new NameSpace("tabledata");
        result = table.${this.selectedfunction.name}(params);
        log "" + result;
        return result;
      }`
      } else {
        this.data.workflowV2String = `Map test () {
        params = {};
        params["startTime"] = ${params.startTime};
        params["endTime"] = ${params.endTime};
        params["resourceId"] = ${params.buildingId || null};

        table = new NameSpace("tabledata");
        result = table.pmScore(params);
        log "" + result;
        return result;
      }`
      }
      this.loadCardData()
    },
    getStyleObj(value, conditionalFormatting, config) {
      let self = this
      if (conditionalFormatting && conditionalFormatting.length) {
        let style = {}
        conditionalFormatting.forEach(rt => {
          let operator = null
          operator = Object.values(
            self.$constants.OPERATORS[config.datatype]
          ).find(rl => rl.operatorId === rt.critera.operatorId)
          if (
            operator &&
            this.criteriaValidate(
              value,
              rt.critera.value,
              operator._name,
              config.datatype
            )
          ) {
            style = {
              color: rt.style.color,
              'background-color': rt.style.bgcolor,
            }
            if (rt.action.blink) {
              style.animation = 'blinker 1s linear infinite'
            }
          }
        })
        return style
      }
      return {}
    },
    save() {
      this.setParams()
      this.closedialog()
      this.tablererender = true
      setTimeout(() => {
        this.tablererender = false
      }, 150)
    },
    closedialog() {
      this.visibility = false
      this.selectcolumn = null
    },
    getClientProps() {
      this.$nextTick(() => {
        if (this.$el) {
          this.height = this.$el.clientHeight - 110
        } else {
          this.height = ''
        }
      })
    },
    setColWidth(currentWidth, preWidth, celldata) {
      if (celldata && celldata.label) {
        let index = this.data.columns.findIndex(
          rt => rt.label === celldata.label
        )
        this.data.columns[index].width = currentWidth
      }
      this.setParams()
    },
    refresh() {
      this.getParams()
      this.loadCardData()
    },
    formatLabel(label, config) {
      if (config && config.datatype === 'DATE') {
        let sec = Number(label)
        return this.toHHMMSS(sec)
      }
      return label
    },
    toHHMMSS(sec) {
      let sec_num = parseInt(sec, 10) // don't forget the second param
      let hours = Math.floor(sec_num / 3600)
      let minutes = Math.floor((sec_num - hours * 3600) / 60)
      let seconds = sec_num - hours * 3600 - minutes * 60

      if (hours < 10) {
        hours = '0' + hours
      }
      if (minutes < 10) {
        minutes = '0' + minutes
      }
      if (seconds < 10) {
        seconds = '0' + seconds
      }
      if (hours > 1) {
        if (minutes !== '00') {
          return hours + ' hrs ' + minutes + ' mins'
        }
        return hours + ' hrs '
      } else {
        if (seconds !== '00') {
          return minutes + ' mins' + seconds + ' secs'
        }
        return minutes + ' mins'
      }
    },
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      return this.tableConfig[columnIndex][rowIndex]
    },
    perpareElTableResult(result) {
      if (result.data) {
        return result.data
      }
      return []
    },
    getData(result) {
      this.result = result.result
      this.elTableResult = this.perpareElTableResult(result.result)
      this.getTableOptions(this.elTableResult)
      this.elTableResult = this.tempFormatingData(
        this.elTableResult,
        this.tableConfig
      )
    },
    tempFormatingData(result, config) {
      if (config[4]) {
        console.log('table config', config[4])
      }
      return result
    },
    getTableOptions(result) {
      let config = {}
      let self = this
      let res = result
      let mergeRuleList = {}
      this.data.columns.forEach((rt, index) => {
        config[index] = {}
        let list = []
        if (rt.merge) {
          if (rt.mergeKey && mergeRuleList[rt.mergeKey]) {
            list = mergeRuleList[rt.mergeKey]
          } else {
            result.forEach((rl, idx) => {
              let cou = {}
              cou['key'] = rl[rt.key]
              cou['value'] = res.filter(rx => rx[rt.key] === rl[rt.key]).length
              if (list.findIndex(r => r.key === cou.key) > -1) {
                console.log('empty', cou.key)
                list.push({ key: rl[rt.key], value: 0 })
              } else {
                console.log('full', cou.key)
                list.push(cou)
              }
            })
          }
          result.forEach((rl, idx) => {
            let d = {
              rowspan: list[idx].value,
              colspan: 1,
            }
            config[index][idx] = d
          })
        } else {
          result.forEach((rl, idx) => {
            let d = {
              rowspan: 1,
              colspan: 1,
            }
            config[index][idx] = d
          })
        }
        mergeRuleList[rt.key] = list
      })
      this.tableConfig = config
    },
    getParams() {
      if (this.widget.dataOptions.data) {
        this.widget.dataOptions.metaJson = JSON.stringify(
          this.widget.dataOptions.data
        )
        this.data = this.widget.dataOptions.data
        this.setParams()
      } else if (this.widget.id > -1 && this.widget.dataOptions.metaJson) {
        this.data = JSON.parse(this.widget.dataOptions.metaJson)
        this.setParams()
      }
    },
    setParams() {
      if (this.data && !this.data.style) {
        this.data.style = {
          color: this.color,
          bgcolor: this.bgcolor,
        }
      } else {
        this.style = this.data.style
      }
      this.widget.dataOptions.metaJson = JSON.stringify(this.data)
    },
    loadCardData() {
      let self = this
      let params = null
      params = {
        workflow: {
          isV2Script: true,
          workflowV2String: this.data.workflowV2String,
        },
        staticKey: 'kpiCard',
      }
      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          if (self.data.columns.find(rt => rt.key === 'outOfSchedule')) {
            let result = self.specialHandling(response.data.cardResult)
            self.getData(result)
          } else if (
            self.data.columns.find(rt => rt.key === 'outOfSchedulecat')
          ) {
            let result = self.specialHandling2(response.data.cardResult)
            self.getData(result)
          } else {
            self.getData(response.data.cardResult)
          }
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
        })
    },
    specialHandling2(result) {
      let data = result.result.data
      let actualReadingList = null

      data.forEach(rt => {
        let readings = []
        let operatingHours = rt.outOfScheduleRaw.flat()
        operatingHours.forEach(op => {
          readings = rt.readings.flat().filter(rl => {
            if (
              rl.day === op.dayOfWeek &&
              op.startTimeAsLocalTime &&
              op.endTimeAsLocalTime
            ) {
              if (
                op.startTimeAsLocalTime.hour > new Date(rl.ttime).getHours() ||
                new Date(rl.ttime).getHours() > op.endTimeAsLocalTime.hour
              ) {
                return rl
              }
            }
          })
        })
        actualReadingList = this.calaculateDuration(readings)
        this.$set(rt, 'outOfSchedulecat', actualReadingList)
      })
      data = data.filter(rt => rt.outOfSchedulecat !== null)
      data.forEach((rt, index) => {
        rt.a = index + 1
      })
      result.data = data
      this.$set(result.result, 'data', data)
      return result
    },
    specialHandling(result) {
      let data = result.result.data
      let actualReadingList = null

      data.forEach(rt => {
        let readings = []
        let operatingHours = rt.outOfScheduleRaw
        operatingHours.forEach(op => {
          readings = readings.concat(rt.readings).filter(rl => {
            if (
              rl.day === op.dayOfWeek &&
              op.startTimeAsLocalTime &&
              op.endTimeAsLocalTime
            ) {
              if (
                op.startTimeAsLocalTime.hour > new Date(rl.ttime).getHours() ||
                new Date(rl.ttime).getHours() > op.endTimeAsLocalTime.hour
              ) {
                return rl
              }
            }
          })
        })
        actualReadingList = this.calaculateDuration(readings)
        this.$set(rt, 'outOfSchedule', actualReadingList)
      })
      data = data.filter(rt => rt.outOfSchedule !== null)
      data.forEach((rt, index) => {
        rt.a = index + 1
      })
      result.data = data
      this.$set(result.result, 'data', data)
      return result
    },
    calaculateDuration(readings) {
      if (readings && readings.length) {
        let durationMins = 10
        let durationMins1 = 15
        let durationMins2 = 20
        let firstIndex = readings[0].ttime
        let firstIndexDuration = Number(moment(firstIndex).format('HHmm'))
        let duration = 0
        readings.forEach((rt, index) => {
          let currentTime = Number(moment(rt.ttime).format('HHmm'))
          if (currentTime !== firstIndexDuration) {
            if (
              (currentTime - firstIndexDuration === durationMins ||
                currentTime - firstIndexDuration === durationMins1 ||
                currentTime - firstIndexDuration === durationMins2) &&
              (rt.runStatus || rt.pumpRunStatus || rt.runstatus)
            ) {
              duration = duration + (currentTime - firstIndexDuration)
            }
            firstIndexDuration = Number(
              moment(readings[index].ttime).format('HHmm')
            )
          }
        })
        let hours = duration / 60
        let rhours = Math.floor(hours)
        let minutes = (hours - rhours) * 60
        let rminutes = Math.round(minutes)
        return duration !== 0
          ? rhours + ' hour(s) ' + rminutes + ' minute(s)'
          : null
      }
      return null
    },
    loadNameSpaceList() {
      this.$http
        .get('/v2/workflow/getNameSpaceListWithFunctions')
        .then(response => {
          this.nameSpace = response.data.result.workflowNameSpaceList
            ? response.data.result.workflowNameSpaceList
            : []
        })
        .catch(() => {
          this.functions = []
        })
    },
  },
}
</script>
<style>
.table-filters-section,
.fc-datatable-header-1 {
  height: 50px;
}
.fc-datatable-header-1 {
  border-bottom: 1px solid #ebeef5;
  font-size: 13px;
  padding: 14px 15px;
  white-space: nowrap;
  padding-top: 15px;
  padding-bottom: 15px;
  color: #333;
  font-weight: 600;
  letter-spacing: 1px;
}
</style>
