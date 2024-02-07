<template>
  <div
    class="dragabale-card height100 kpi-card-1"
    v-if="domRerender"
    :style="getScale"
  >
    <div v-if="loading"></div>
    <template v-else>
      <div class="settings-over" v-if="mode">
        <div
          class="reading-card-conditional-formater"
          @click="visibility = true"
        >
          <img src="~statics/cardIcon/formula.svg" />
        </div>
        <el-popover
          placement="right"
          width="300"
          popper-class="reading-color-picker"
          trigger="click"
        >
          <div>
            <div class="color-picker-section">
              <div class="c-picker-label">
                {{ $t('panel.layout.back_colour') }} :
              </div>
              <div
                v-for="(color, index) in predefineColors"
                :key="index"
                class="color-picker-conatiner"
              >
                <div
                  class="color-box"
                  :style="'background:' + color + ';'"
                  @click="setColor('bg', color)"
                  v-bind:class="{ active: data.style.bgcolor === color }"
                ></div>
              </div>
            </div>
            <div class="color-picker-section mT20">
              <div class="c-picker-label">Text color :</div>
              <div
                v-for="(color, index) in predefineColors"
                :key="index"
                class="color-picker-conatiner"
              >
                <div
                  class="color-box"
                  :style="'background:' + color + ';'"
                  @click="setColor('color', color)"
                  v-bind:class="{ active: data.style.color === color }"
                ></div>
              </div>
            </div>
          </div>
          <div slot="reference" v-if="mode" class="color-choose-icon">
            <div class="reading-card-color-picker">
              <img src="~statics/cardIcon/colorpicker.svg" />
            </div>
          </div>
        </el-popover>
      </div>
      <div class="kpi-container" :style="getStyle()">
        <div class="kpi-sections">
          {{
            data.expressions && data.expressions.length
              ? data.expressions[0].actualName
              : ''
          }}
        </div>
        <div
          class="kpi-data"
          v-if="this.data.hasOwnProperty('unitString')"
          @click="drilldown"
          :style="getDisplayStyleObj()"
        >
          <div class="kpi-count pointer">
            {{
              this.result && this.result.hasOwnProperty('result')
                ? formattedResult() + ' '
                : '---' + ' '
            }}
            <span
              v-html="data.unitString"
              v-if="data && data.unitString"
            ></span>
          </div>
        </div>
        <div
          class="kpi-data"
          v-else-if="this.result.hasOwnProperty('unitString')"
          @click="drilldown"
          :style="getDisplayStyleObj()"
        >
          <div class="kpi-count pointer">
            {{
              this.result && this.result.hasOwnProperty('result')
                ? formattedResult()
                : '---' + ' '
            }}
            <span
              v-html="result.unitString"
              v-if="result && result.unitString"
            ></span>
          </div>
        </div>
        <div
          class="kpi-data"
          v-else
          @click="drilldown"
          :style="getDisplayStyleObj()"
        >
          <div class="kpi-count pointer">
            {{
              this.result && this.result.hasOwnProperty('result')
                ? formattedResult()
                : '---' + ' '
            }}
            <!-- <span v-html="result.unit" v-if="result && result.unit"></span> -->
          </div>
        </div>
        <div class="kpi-label" v-if="!enable[widget.id]">
          <div class="kpi-period">
            {{
              getdateOperators().find(rt => rt.value === data.operatorId)
                ? getdateOperators().find(rt => rt.value === data.operatorId)
                    .label
                : ''
            }}
          </div>
        </div>
      </div>
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
          <span class="kpi-card-header">{{ $t('panel.tyre.settings') }}</span>
        </span>
        <div style="height:500px;">
          <smart-table-settings
            :data="data"
            :column="data"
            :settings="settings"
          ></smart-table-settings>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="closedialog">{{
            $t('panel.tyre.clos')
          }}</el-button>
          <el-button type="primary" class="modal-btn-save" @click="save()">{{
            $t('panel.tyre.ok')
          }}</el-button>
        </div>
      </el-dialog>
    </template>
  </div>
</template>

<script>
import DashboardFilterMixin from 'pages/dashboard/mixins/DashboardFilters'
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
import JumpToHelper from '@/mixins/JumpToHelper'
import SmartTableSettings from '@/SmartTableSettings'
import criteriaHelper from 'src/util/criteriaHelper.js'
export default {
  props: ['widget', 'config'],
  mixins: [DateHelper, DashboardFilterMixin, JumpToHelper, criteriaHelper],
  data() {
    return {
      visibility: false,
      enable: {
        10227: 10227,
        10228: 10228,
        10229: 10229,
        10230: 10230,
        10231: 10231,
        10232: 10232,
        10233: 10233,
        10234: 10234,
        10235: 10235,
      },
      settings: {
        critera: {
          key: null,
          operatorId: null,
          value: null,
        },
        display: {
          displayValue: null,
        },
        action: {
          blinkValue: false,
        },
        style: {
          color: '#000',
          bgcolor: '#fff',
        },
      },
      rerender: false,
      data: {
        style: {
          color: 'red',
          bgcolor: 'pink',
        },
        unitString: '',
      },
      style: {
        bgcolor: '#fff',
        color: '#000',
      },
      result: null,
      domRerender: true,
      predefineColors: colors.readingcardColors,
      loading: false,
    }
  },
  components: {
    SmartTableSettings,
  },
  mounted() {
    this.loadCardData()
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
    getScale() {
      return this.$style.responsiveScale(
        200,
        200,
        1,
        this.currentWidth,
        this.currentHeight
      )
    },
    currentWidth() {
      if (this.$el && this.$el.clientWidth) {
        return this.$el.clientWidth
      }
      return 200
    },
    currentHeight() {
      if (this.$el && this.$el.clientHeight) {
        return this.$el.clientHeight
      }
      return 200
    },
  },
  watch: {
    dashboardDateFilter: {
      handler(newData, oldData) {
        if (newData) {
          console.log('KFI  card ,Dashboard filter changed')
          this.refresh()
        }
      },
    },
  },
  methods: {
    getStyle() {
      if (
        this.data.conditionalFormatting &&
        this.data.conditionalFormatting.length &&
        this.data
      ) {
        let conditionalFormatting = this.data.conditionalFormatting
        if (
          conditionalFormatting[0].critera &&
          conditionalFormatting[0].critera.operatorId
        ) {
          return this.getStyleObj(
            this.result.result,
            this.data.conditionalFormatting,
            this.data
          )
        } else {
          return { background: this.style.bgcolor, color: this.style.color }
        }
      }
      return { background: this.style.bgcolor, color: this.style.color }
    },
    getDisplayStyleObj() {
      if (
        this.data.conditionalFormatting &&
        this.data.conditionalFormatting.length &&
        this.data
      ) {
        let conditionalFormatting = this.data.conditionalFormatting
        if (
          conditionalFormatting[0].critera &&
          conditionalFormatting[0].critera.operatorId
        ) {
          return this.getDisStyleObj(
            this.result.result,
            this.data.conditionalFormatting,
            this.data
          )
        }
      }
    },
    getDisStyleObj(value, conditionalFormatting, config) {
      let self = this
      if (conditionalFormatting && conditionalFormatting.length) {
        let style = {}
        conditionalFormatting.forEach(rt => {
          let operator = null
          operator = Object.values(
            self.$constants.OPERATORS[config.datatype]
          ).find(rl => rl.operatorId === rt.critera.operatorId)
          let val = null
          if (Object.keys(value).length > 1) {
            val = value.value
          } else {
            val = value
          }
          if (
            operator &&
            this.criteriaValidate(
              val,
              rt.critera.value,
              operator._name,
              config.datatype
            )
          ) {
            if (rt.action.blinkValue) {
              style = {
                animation: 'blinker 1s linear infinite',
              }
            }
          }
        })
        return style
      }
      return {}
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
          let val = null
          if (Object.keys(value).length > 1) {
            val = value.value
          } else {
            val = value
          }
          if (
            operator &&
            this.criteriaValidate(
              val,
              rt.critera.value,
              operator._name,
              config.datatype
            )
          ) {
            style = {
              color: rt.style.color,
              'background-color': rt.style.bgcolor,
            }
          }
        })
        return style
      }
      return {}
    },
    getDisplayObj(value, conditionalFormatting, config) {
      let self = this
      if (conditionalFormatting && conditionalFormatting.length) {
        let displayValue = this.normalValue()
        conditionalFormatting.forEach(rt => {
          let operator = null
          operator = Object.values(
            self.$constants.OPERATORS[config.datatype]
          ).find(rl => rl.operatorId === rt.critera.operatorId)
          let val = null
          if (Object.keys(value).length > 1) {
            val = value.value
          } else {
            val = value
          }
          if (
            operator &&
            this.criteriaValidate(
              val,
              rt.critera.value,
              operator._name,
              config.datatype
            )
          ) {
            if (rt.display && rt.display.displayValue) {
              displayValue = rt.display.displayValue
            }
          }
        })
        return displayValue
      }
      return {}
    },
    closedialog() {
      this.visibility = false
      this.selectcolumn = null
    },
    save() {
      this.setParams()
      this.closedialog()
      this.rerender = true
      setTimeout(() => {
        this.rerender = false
      }, 150)
    },
    formattedResult() {
      if (
        this.data.conditionalFormatting &&
        this.data.conditionalFormatting.length &&
        this.data
      ) {
        let conditionalFormatting = this.data.conditionalFormatting
        if (
          conditionalFormatting[0].critera &&
          conditionalFormatting[0].critera.operatorId
        ) {
          return this.getDisplayObj(
            this.result.result,
            this.data.conditionalFormatting,
            this.data
          )
        } else {
          return this.normalValue()
        }
      } else {
        return this.normalValue()
      }
    },
    normalValue() {
      if (
        this.result.result &&
        this.result.result.hasOwnProperty('unit') &&
        this.result.result.hasOwnProperty('value')
      ) {
        if (
          this.$convert()
            .possibilities()
            .find(rt => rt === this.result.result.unit)
        ) {
          let obj = this.$convert(Number(this.result.result.value))
            .from(this.result.result.unit)
            .toBest()
          return `${Number(obj.val).toFixed(2)} ${obj.unit}`
        } else {
          return `${Number(this.result.result.value).toFixed(2)} ${
            this.result.result.unit
          }`
        }
      } else if (
        this.result.result &&
        this.result.result.hasOwnProperty('value')
      ) {
        return this.result.result
      } else {
        return this.result.result || '---'
      }
    },
    drilldown() {
      if (!this.mode && !this.$mobile) {
        if (
          this.data &&
          this.data.expressions &&
          this.data.expressions.length
        ) {
          let data = this.$helpers.expressionsToFilters(this.data.expressions)
          if (data.filters && data.moduleName) {
            this.jumpToViewList(data.filters, data.moduleName)
          }
        }
      }
    },
    refresh() {
      this.updateCard()
    },
    loadCardData() {
      let self = this
      let params = null
      if (this.widget && this.widget.id > -1) {
        params = {
          widgetId: self.widget.id,
        }
        if (this.dashboardDateFilter) {
          params.startTime = this.getDateFilterStartTime()
          params.endTime = this.getDateFilterEndTime()
          //  params = this.getDateFilterParams(params)
        } else if (
          this.dashboardDateFilter &&
          (this.widget.widgetSettingsJson
            ? this.widget.widgetSettingsJson.useDashboardFilter
            : true)
        ) {
          params.startTime = this.getDateFilterStartTime()
          params.endTime = this.getDateFilterEndTime()
        }
      } else {
        params = {
          workflow: {
            expressions:
              this.widget.dataOptions.data &&
              this.widget.dataOptions.data.expressions
                ? this.widget.dataOptions.data.expressions
                : [],
            workflowUIMode: 1,
          },
          staticKey: 'kpiCard',
        }
      }

      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getParams()
          self.getData(response.data.cardResult)
          self.loading = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          self.loading = false
        })
    },
    updateCard() {
      let self = this
      let params = null
      if (
        this.dashboardDateFilter &&
        (this.widget.widgetSettingsJson
          ? this.widget.widgetSettingsJson.useDashboardFilter
          : true)
      ) {
        if (this.widget && this.widget.id > -1) {
          params = {
            widgetId: self.widget.id,
          }
          params.startTime = this.getDateFilterStartTime()
          params.endTime = this.getDateFilterEndTime()
        } else {
          params = {
            workflow: {
              expressions:
                this.widget.dataOptions.data &&
                this.widget.dataOptions.data.expressions
                  ? this.widget.dataOptions.data.expressions
                  : [],
              workflowUIMode: 1,
            },
            staticKey: 'kpiCard',
          }
          params.startTime = this.getDateFilterStartTime()
          params.endTime = this.getDateFilterEndTime()
        }
      } else {
        params = {
          workflow: {
            expressions:
              this.widget.dataOptions.data &&
              this.widget.dataOptions.data.expressions
                ? this.widget.dataOptions.data.expressions
                : [],
            workflowUIMode: 1,
          },
          staticKey: 'kpiCard',
        }
      }
      this.loading = true
      self.getParams()
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getData(response.data.cardResult)
          self.loading = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          self.loading = false
        })
    },
    setColor(mode, color) {
      if (mode === 'bg') {
        this.style.bgcolor = color
      } else if (mode === 'color') {
        this.style.color = color
      }
      this.domRerender = false
      this.domRerender = true
      this.setParams()
    },
    getParams() {
      if (this.widget.dataOptions.data) {
        this.widget.dataOptions.metaJson = JSON.stringify(
          this.widget.dataOptions.data
        )
        this.data = this.widget.dataOptions.data
      } else if (this.widget.id > -1 && this.widget.dataOptions.metaJson) {
        this.data = JSON.parse(this.widget.dataOptions.metaJson)
      }
      if (!this.data.conditionalFormatting) {
        let condition = [
          {
            critera: {
              key: null,
              operatorId: null,
              value: null,
            },
            display: {
              displayValue: null,
            },
            action: {
              blinkValue: false,
            },
            style: {
              color: '#000',
              bgcolor: '#fff',
            },
          },
        ]
        this.$set(this.data, 'conditionalFormatting', condition)
        this.data.datatype = 'NUMBER'
      }
      if (
        this.widget.hasOwnProperty('widgetVsWorkflowContexts') &&
        this.widget.widgetVsWorkflowContexts.length &&
        this.widget.widgetVsWorkflowContexts[0].workflow
      ) {
        this.data[
          'v2Script'
        ] = this.widget.widgetVsWorkflowContexts[0].workflow['v2Script']
        this.data[
          'workflowV2String'
        ] = this.widget.widgetVsWorkflowContexts[0].workflow['workflowV2String']
      }
      this.setParams()
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
    getData(data) {
      if (data.hasOwnProperty('result')) {
        this.result = data
      }
    },
  },
}
</script>

<style>
.kpi-card-1 .reading-card-color-picker {
  color: #fff;
}
.kpi-sections {
  font-size: 14px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.36;
  letter-spacing: 1.5px;
  text-align: center;
}

.kpi-period {
  font-size: 12px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.3px;
  text-align: center;
  /* color: #ffffff; */
  text-transform: uppercase;
  opacity: 0.6;
}

.dragabale-card:hover .color-choose-icon,
.dragabale-card:hover .reading-card-conditional-formater,
.dragabale-card:hover .reading-card-color-picker {
  display: block !important;
}
.settings-over {
  width: 100%;
  position: absolute;
  height: 0px;
  transition: height 0.2s;
  -webkit-transition: height 0.2s;
  border-radius: 3px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0px;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
  background-color: rgba(0, 0, 0, 0.4);
}
.dragabale-card:hover .settings-over {
  height: 50px;
}
.reading-card-color-picker {
  position: absolute;
  top: 16px;
  right: 40px;
  color: #fff;
  font-weight: 500;
  font-size: 18px;
  cursor: pointer;
  z-index: 10;
  display: none;
  opacity: 1;
}
.reading-card-conditional-formater {
  position: absolute;
  top: 16px;
  right: 74px;
  color: #fff;
  font-weight: 500;
  font-size: 18px;
  cursor: pointer;
  z-index: 10;
  display: none;
  opacity: 1;
}
</style>
