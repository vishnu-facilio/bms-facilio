<template>
  <div class=" height100 fc-smart-table-page">
    <div class="fc-widget-header p10" v-if="mode"></div>
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
export default {
  props: ['widget', 'config'],
  mixins: [criteriaHelper],
  data() {
    return {
      tablererender: false,
      visibility: false,
      selectcolumn: null,
      height: '',
      loading: true,
      result: null,
      elTableResult: [],
      data: null,
      tableConfig: {},
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
  },
  mounted() {
    this.getClientProps()
    this.getParams()
    this.loadCardData()
  },
  components: {
    shimmerLoading,
    SmartTableSettings,
    ConditionalValue,
  },
  methods: {
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
          this.height = this.$el.clientHeight - (this.mode ? 28 : 0)
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
          self.getData(response.data.cardResult)
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
        })
    },
  },
}
</script>
<style>
.smart-table-wrapper {
  overflow: auto;
}
.smart-table-wrapper .el-table td {
  padding: 0px !important;
}
/* .condition-icon {
  position: absolute;
  right: 0;
  top: 15px;
} */
.smart-table-actions {
  position: absolute;
  right: 0px;
  top: 15px;
}
.smart-table-print {
  font-size: 18px;
  position: absolute;
  z-index: 10;
  color: #646f7f;
  top: 10px;
  cursor: pointer;
}
.smart-table-wrapper .el-table__row td:first-child .cell {
  padding-left: 0;
}
</style>
