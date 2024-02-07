<template>
  <shimmer-loading v-if="loading" class="card-shimmer"> </shimmer-loading>
  <div class="dragabale-card readingcard h100" :style="getbackground()" v-else>
    <el-popover
      placement="right"
      width="300"
      popper-class="reading-color-picker"
      v-model="colorpicker"
      v-if="layout === 3"
    >
      <div>
        <div class="color-picker-section mT0">
          <div class="c-picker-label">{{ $t('panel.card.chart_colour') }}</div>
          <div
            v-for="(color, index) in predefineColors"
            :key="index"
            class="color-picker-conatiner"
          >
            <div
              class="color-box"
              :style="'background:' + color + ';'"
              @click="choosechartcolor(color)"
              v-bind:class="{ active: color === chartcolor }"
            ></div>
          </div>
          <div class="color-picker-conatiner">
            <div
              class="color-box"
              :style="'background:' + '#000' + ';'"
              @click="choosechartcolor('#000')"
              v-bind:class="{ active: '#000' === chartcolor }"
            ></div>
          </div>
        </div>
      </div>
      <div slot="reference" v-if="mode" class="color-choose-icon">
        <i class="fa fa-font reading-card-color-picker"></i>
      </div>
    </el-popover>
    <el-popover
      placement="right"
      width="300"
      popper-class="reading-color-picker"
      v-model="colorpicker"
      v-else-if="layout === 4"
    >
      <div>
        <div class="color-picker-section">
          <div class="c-picker-label">{{ $t('panel.card.colour') }}</div>
          <div
            v-for="(color, index) in predefineColors"
            :key="index"
            class="color-picker-conatiner"
          >
            <div
              class="color-box"
              :style="'background:' + color + ';'"
              @click="chooseStartColor(color)"
              v-bind:class="{ active: color === gaugeLayout.startColor }"
            ></div>
          </div>
        </div>
      </div>
      <div slot="reference" v-if="mode" class="color-choose-icon">
        <i class="fa fa-font reading-card-color-picker"></i>
      </div>
    </el-popover>
    <el-popover
      placement="right"
      width="300"
      popper-class="reading-color-picker"
      v-model="colorpicker"
      v-else
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
              @click="choosecolor(color)"
              v-bind:class="{ active: color === bgcolor }"
            ></div>
          </div>
        </div>
        <div class="color-picker-section mT20">
          <div class="c-picker-label">{{ $t('panel.card.text') }}</div>
          <div
            v-for="(color, index) in predefineColors"
            :key="index"
            class="color-picker-conatiner"
          >
            <div
              class="color-box"
              :style="'background:' + color + ';'"
              @click="choosefontcolor(color)"
              v-bind:class="{ active: color === fontcolor }"
            ></div>
          </div>
          <div class="color-picker-conatiner">
            <div
              class="color-box"
              :style="'background:' + '#000' + ';'"
              @click="choosefontcolor('#000')"
              v-bind:class="{ active: '#000' === fontcolor }"
            ></div>
          </div>
        </div>
      </div>
      <div slot="reference" v-if="mode" class="color-choose-icon">
        <i class="fa fa-font reading-card-color-picker"></i>
      </div>
    </el-popover>
    <div
      class="readingcard-section"
      :class="'layout' + layout + ' height' + widget.layout.height"
      v-if="layout === 1 && rawdata"
    >
      <div
        class="readingcard-header"
        v-if="title"
        :style="getFontcolor(title, 'title')"
      >
        {{ title }}
      </div>
      <div
        class="readingcard-data"
        v-bind:class="{ pointer: url }"
        @click="getRedirectUrl()"
      >
        <span
          class="readingcard-unit unitleft"
          :style="getFontcolor()"
          v-html="unit"
          v-if="unitLeft"
        ></span>
        <span
          class="no-value"
          :style="getFontcolor(value, 'value')"
          v-if="value === null"
          >{{ setDefaultMeta('NO DATA') }}</span
        >
        <span
          class="readingcard-value"
          :style="getFontcolor(value, 'value')"
          v-tippy
          :title="rawdata.value + ' ' + rawdata.unit"
          v-else
          >{{ formatValue(value) }}</span
        >
        <span
          class="readingcard-unit"
          :style="getFontcolor()"
          v-html="unit"
          v-if="!unitLeft"
        ></span>
      </div>
      <div class="readingcard-time" :style="getFontcolor()" v-if="!set">
        {{ formatPeriod(period) }}
      </div>
      <div
        class="readingcard-time"
        :style="getFontcolor()"
        v-if="set && typeof newReadingField === 'undefined'"
      >
        {{ formatPeriod(period) }}
      </div>

      <el-dialog
        title="Set Reading"
        :visible.sync="readingSetDialog"
        width="60%"
        custom-class="mobile-set-dialog"
        v-if="newReadingField && $mobile"
      >
        <div v-if="newReadingField">
          <div class="readingcard-setlayout">
            <div class="reading-setcard-body">
              <div style="margin-top: 7px;" class="pB10">
                <div
                  v-if="newReadingField.field.dataTypeEnum === 'BOOLEAN'"
                  class="pT10 mobile-set-switch"
                >
                  {{ newReadingField.field.falseVal || 'False'
                  }}<el-switch
                    v-model="newReadingField.value"
                    class="pL10 pR10"
                  ></el-switch
                  >{{ newReadingField.field.trueVal || 'True' }}
                </div>
                <div v-else>
                  <el-input
                    :type="
                      newReadingField.field.dataTypeEnum === 'NUMBER' ||
                      newReadingField.field.dataTypeEnum === 'DECIMAL'
                        ? 'number'
                        : 'text'
                    "
                    v-model="newReadingField.value"
                  ></el-input>
                </div>
              </div>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="cancelReadingSetDialog()">{{
            $t('panel.card.cancel')
          }}</el-button>
          <el-button type="primary" @click="setFieldValue">{{
            $t('panel.card.set')
          }}</el-button>
        </span>
      </el-dialog>
      <div
        class="readingcard-time reading-set-button mobileset-btn"
        :style="getFontcolor()"
        slot="reference"
        v-if="
          newReadingField &&
            aggregationFuncName === 6 &&
            set &&
            $mobile &&
            value !== null
        "
        @click="readingSetCard()"
      >
        {{ $t('panel.card.st') }}
      </div>
      <el-popover
        placement="right"
        width="200"
        v-model="readingSetDialog"
        v-if="!$mobile"
        popper-class="readingcard-setcard-popup"
      >
        <div v-if="newReadingField">
          <div class="readingcard-setlayout">
            <div class="reading-setcard-body">
              <div style="margin-top: 7px;" class="pB10">
                <div style="margin-bottom: 5px;" class="readingset-card-header">
                  {{ $t('panel.card.reading') }}
                </div>
                <div
                  v-if="newReadingField.field.dataTypeEnum === 'BOOLEAN'"
                  class="pT10"
                >
                  {{ newReadingField.field.falseVal || 'False'
                  }}<el-switch
                    v-model="newReadingField.value"
                    class="pL10 pR10"
                  ></el-switch
                  >{{ newReadingField.field.trueVal || 'True' }}
                </div>
                <div v-else>
                  <el-input
                    :type="
                      newReadingField.field.dataTypeEnum === 'NUMBER' ||
                      newReadingField.field.dataTypeEnum === 'DECIMAL'
                        ? 'number'
                        : 'text'
                    "
                    v-model="newReadingField.value"
                  ></el-input>
                </div>
              </div>
            </div>
            <div class="readingcard-btn-set row">
              <el-button
                class="col-6 s-cancel uppercase"
                @click="cancelReadingSetDialog()"
                >{{ $t('panel.card.cancel_') }}</el-button
              >
              <el-button
                class="col-6 s-set uppercase"
                @click="setFieldValue()"
                >{{ $t('panel.card.st') }}</el-button
              >
            </div>
          </div>
        </div>
        <div
          class="readingcard-time reading-set-button"
          :style="getFontcolor()"
          slot="reference"
          v-if="
            newReadingField &&
              aggregationFuncName === 6 &&
              set &&
              !$mobile &&
              value !== null
          "
          @click="readingSetCard()"
        >
          {{ $t('panel.card.st') }}
        </div>
      </el-popover>
    </div>
    <div
      class="readingcard-section"
      :class="'layout' + layout + ' height' + widget.layout.height"
      v-else-if="layout === 2 && rawdata"
    >
      <div
        class="readingcard-header"
        v-if="title"
        :style="getFontcolor(title, 'title')"
      >
        {{ title }}
      </div>
      <div
        class="readingcard-data"
        v-bind:class="{ pointer: url }"
        @click="readingSetCard()"
      >
        <span
          class="readingcard-unit unitleft"
          :style="getFontcolor()"
          v-html="unit"
          v-if="unitLeft"
        ></span>
        <span
          class="no-value"
          :style="getFontcolor(value, 'value')"
          v-if="value === null"
          >{{ setDefaultMeta('NO DATA') }}</span
        >
        <span
          class="readingcard-value"
          :style="getFontcolor(value, 'value')"
          v-tippy
          :title="rawdata.value + ' ' + rawdata.unit"
          @click="getRedirectUrl()"
          v-else
          >{{ formatValue(value) }}</span
        >
        <span
          class="readingcard-unit"
          :style="getFontcolor()"
          v-html="unit"
          v-if="!unitLeft"
        ></span>
      </div>
      <div class="readingcard-time" :style="getFontcolor()" v-if="!set">
        {{ formatPeriod(period) }}
      </div>
      <div
        class="readingcard-time"
        :style="getFontcolor()"
        v-if="set && typeof newReadingField === 'undefined'"
      >
        {{ formatPeriod(period) }}
      </div>

      <el-dialog
        title="Set Reading"
        :visible.sync="readingSetDialog"
        width="60%"
        custom-class="mobile-set-dialog"
        v-if="newReadingField && $mobile"
      >
        <div v-if="newReadingField">
          <div class="readingcard-setlayout">
            <div class="reading-setcard-body">
              <div style="margin-top: 7px;" class="pB10">
                <div
                  v-if="newReadingField.field.dataTypeEnum === 'BOOLEAN'"
                  class="pT10 mobile-set-switch"
                >
                  {{ newReadingField.field.falseVal || 'False'
                  }}<el-switch
                    v-model="newReadingField.value"
                    class="pL10 pR10"
                  ></el-switch
                  >{{ newReadingField.field.trueVal || 'True' }}
                </div>
                <div v-else>
                  <el-input
                    :type="
                      newReadingField.field.dataTypeEnum === 'NUMBER' ||
                      newReadingField.field.dataTypeEnum === 'DECIMAL'
                        ? 'number'
                        : 'text'
                    "
                    v-model="newReadingField.value"
                  ></el-input>
                </div>
              </div>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="cancelReadingSetDialog()">{{
            $t('panel.card.cancel')
          }}</el-button>
          <el-button type="primary" @click="setFieldValue">{{
            $t('panel.card.set')
          }}</el-button>
        </span>
      </el-dialog>
      <div
        class="readingcard-time reading-set-button mobileset-btn"
        :style="getFontcolor()"
        slot="reference"
        v-if="
          newReadingField &&
            aggregationFuncName === 6 &&
            set &&
            $mobile &&
            value !== null
        "
        @click="readingSetCard()"
      >
        {{ $t('panel.card.st') }}
      </div>
      <el-popover
        placement="right"
        width="200"
        v-model="readingSetDialog"
        v-if="!$mobile"
        popper-class="readingcard-setcard-popup"
      >
        <div v-if="newReadingField">
          <div class="readingcard-setlayout">
            <div class="reading-setcard-body">
              <div style="margin-top: 7px;" class="pB10">
                <div style="margin-bottom: 5px;" class="readingset-card-header">
                  {{ $t('panel.card.reading') }}
                </div>
                <div
                  v-if="newReadingField.field.dataTypeEnum === 'BOOLEAN'"
                  class="pT10"
                >
                  {{ newReadingField.field.falseVal || 'False'
                  }}<el-switch
                    v-model="newReadingField.value"
                    class="pL10 pR10"
                  ></el-switch
                  >{{ newReadingField.field.trueVal || 'True' }}
                </div>
                <div v-else>
                  <el-input
                    :type="
                      newReadingField.field.dataTypeEnum === 'NUMBER' ||
                      newReadingField.field.dataTypeEnum === 'DECIMAL'
                        ? 'number'
                        : 'text'
                    "
                    v-model="newReadingField.value"
                  ></el-input>
                </div>
              </div>
            </div>
            <div class="readingcard-btn-set row">
              <el-button
                class="col-6 s-cancel uppercase"
                @click="cancelReadingSetDialog()"
                >{{ $t('panel.card.cancel_') }}</el-button
              >
              <el-button
                class="col-6 s-set uppercase"
                @click="setFieldValue()"
                >{{ $t('panel.card.st') }}</el-button
              >
            </div>
          </div>
        </div>
        <div
          class="readingcard-time reading-set-button"
          :style="getFontcolor()"
          slot="reference"
          v-if="
            newReadingField &&
              aggregationFuncName === 6 &&
              set &&
              !$mobile &&
              value !== null
          "
          @click="readingSetCard()"
        >
          {{ $t('panel.card.st') }}
        </div>
      </el-popover>
    </div>
    <div
      class="readingcard-section"
      :class="'layout' + layout"
      v-else-if="layout === 3"
    >
      <div
        class="reading-card-border"
        :style="'background:' + alarmSeverity[alarm] + ';'"
        v-if="alarmSeverity[alarm]"
      ></div>
      <div
        class="readingcard-header"
        v-if="title"
        :style="getFontcolor(title, 'title')"
      >
        {{ title }}
      </div>
      <div class="readingcard-data" v-bind:class="{ pointer: url }">
        <span
          class="readingcard-unit unitleft pR5"
          :style="getFontcolor()"
          v-html="unit"
          v-if="unitLeft"
        ></span>
        <span
          class="readingcard-value no-value"
          :style="getFontcolor(value, 'value')"
          v-if="value === null"
          >{{ setDefaultMeta('NO DATA') }}</span
        >
        <span
          class="readingcard-value"
          :style="getFontcolor(value, 'value')"
          @click="getRedirectUrl()"
          v-else
          >{{
            newResult === 0 ? newResult : tempFormate(value, newResult)
          }}</span
        >
        <span
          class="readingcard-unit pL5"
          :style="getFontcolor()"
          v-html="unit"
          v-if="!unitLeft"
        ></span>
      </div>
      <div
        class="readingcard-time card3-measure-txt reading-card-time3"
        :style="getFontcolor()"
      >
        {{ getPeriod(legendFunctions[aggregateFunction]) }}
      </div>
      <div
        class="readingcard-time text-uppercase reading-card-time2"
        :style="getFontcolor()"
      >
        {{ formatPeriod(period) }}
      </div>
      <sparkline
        :height="sparkilneSize.height"
        :width="sparkilneSize.width"
        v-if="chartrender"
        :tooltipProps="sparkline.label"
      >
        <sparklineCurve
          :data="sparkline.data"
          :limit="sparkline.data.length"
          :styles="sparkline.style"
          :textStyles="sparkline.label"
        />
      </sparkline>
    </div>
    <div
      class="readingcard-section"
      :class="'layout' + layout"
      v-else-if="layout === 4"
    >
      <div
        class="readingcard-header"
        v-if="title"
        :style="getFontcolor(title, 'title')"
      >
        {{ title }}
      </div>
      <reading-gauge-chart
        :widget="widget"
        :config="config"
        :gaugeData="gaugeData"
        v-if="!loading && !error"
      ></reading-gauge-chart>
      <div style="display: flex;height: 160px;" v-else>
        <div class="nodata" style="display: flex;margin: auto;">
          {{ $t('panel.card.no_data') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import colors from 'charts/helpers/colors'
import shimmerLoading from '@/ShimmerLoading'
import DateUtil from '@/mixins/DateHelper'
import DashboardFilterMixin from 'pages/dashboard/mixins/DashboardFilters'
import formatter from 'charts/helpers/formatter'
import Sparkline from 'newcharts/sparklines/Sparkline'
import JumpToHelper from '@/mixins/JumpToHelper'
import readingGaugeChart from '@/ReadingGaugeChart'

export default {
  mixins: [DateUtil, JumpToHelper, DashboardFilterMixin],
  props: ['widget', 'config'],
  data() {
    return {
      loading: false,
      readings: null,
      colorpicker: false,
      newResult: null,
      backupFontcolor: '#000',
      backupbgcolor: '#fff',
      newReadingField: null,
      set: false,
      unitLeft: false,
      readingSetDialog: false,
      aggregationFuncName: 3,
      aggParam: 3,
      assetId: null,
      fieldId: null,
      error: false,
      bgcolor: '#fff',
      fontcolor: '#000',
      chartcolor: '#54a5ff',
      gaugeLayout: {
        needMax: 'usetarget',
        centerText: [
          {
            label: '',
          },
          {
            label: 'This month',
          },
        ],
        startColor: '#f866a0',
        endColor: '#ecb0c8',
        maxColor: '#532d80',
        enableCenterText1: true,
      },
      layoutkey: {
        1: 'readingcard',
        2: 'readingcard',
        3: 'readingWithGraphCard',
        4: 'readingGaugeCard',
      },
      alarm: '',
      gaugeData: null,
      chartrender: true,
      graphUnit: null,
      url: null,
      rawdata: null,
      title: '',
      value: null,
      aggregateFunction: '',
      layout: 1,
      unit: '',
      result: null,
      paramsJson: null,
      alarmSeverity: {
        Critical: '#f33333',
        Major: '#e69958',
        Minor: '#e2c820',
        Warning: '#D0C43A',
        Clear: '#6cbc85',
        Info: '#509AAF',
        Fire: '#E74141',
        none: '#fff',
      },
      legendFunctions: {
        sum: 'Total',
        avg: 'Avg',
        min: 'Min',
        max: 'Max',
        lastValue: 'Last value',
      },
      legendEnamMap: {
        sum: 3,
        avg: 2,
        min: 4,
        max: 5,
        lastValue: 6,
      },
      aggregateFunctions: [
        {
          label: 'Sum',
          value: 3,
          name: 'sum',
        },
        {
          label: 'Avg',
          value: 2,
          name: 'avg',
        },
        {
          label: 'Min',
          value: 4,
          name: 'min',
        },
        {
          label: 'Max',
          value: 5,
          name: 'max',
        },
        {
          label: 'Current Value',
          value: 6,
          name: 'lastValue',
        },
      ],
      predefineColors: colors.readingcardColors,
      period: '',
      sparkline: {
        data: [],
        style: {
          stroke: '#54a5ff',
          fill: '#54a5ff',
        },
        label: {
          formatter(val) {
            return `<label style="color:#fff;font-weight:bold;">${val.value}</label>`
          },
        },
      },
      colorMap: false,
      colorMapConfig: {},
    }
  },
  components: {
    shimmerLoading,
    Sparkline,
    readingGaugeChart,
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
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
    sparkilneSize() {
      if (this.$el) {
        return {
          width: this.$el.offsetWidth + 6,
          height: (this.$el.offsetHeight * 3) / 15,
        }
      } else {
        return {
          width: 100,
          height: 30,
        }
      }
    },
  },
  watch: {
    'config.height': {
      handler(newData, oldData) {
        this.chartrender = false
        let self = this
        setTimeout(function() {
          self.render()
        }, 400)
      },
    },
    'config.editwidget': {
      // need to be removed
      handler(newData, oldData) {
        if (
          this.widget.id > -1 &&
          this.config.selectedWidgetId === this.widget.id &&
          newData !== oldData
        ) {
          this.updateReadingcard()
        }
      },
      deep: true,
    },
    widget: {
      handler(newData, oldData) {
        if (
          this.config.selectedWidgetId &&
          this.config.selectedWidgetId === this.widget.id
        ) {
          this.updateReadingcard()
        }
      },
      deep: true,
    },
    dashboardDateFilter: {
      handler(newData, oldData) {
        if (newData) {
          console.log('Reading card ,Dashboard filter changed')
          if (
            this.widget.widgetSettingsJson
              ? this.widget.widgetSettingsJson.useDashboardFilter
              : true
          ) {
            this.refresh(false)
          }
        }
      },
    },
  },
  mounted() {
    this.getCardData()
  },
  methods: {
    refresh() {
      this.getCardData()
    },
    getPeriod(period) {
      if (period === 'Last value') {
        let per = this.period.toLowerCase()
        if (
          per === 'today' ||
          per === 'this week' ||
          per === 'this year' ||
          per === 'this month'
        ) {
          return 'Current value'
        } else {
          return period
        }
      } else {
        return period
      }
    },
    getGaugePeriod(period) {
      if (period === 'Last value') {
        let per = this.period.toLowerCase()
        if (
          per === 'today' ||
          per === 'this week' ||
          per === 'this year' ||
          per === 'this month'
        ) {
          return 'Current value'
        } else {
          return this.period
        }
      } else {
        return this.period
      }
    },
    getbackground() {
      return 'background:' + this.bgcolor + ''
    },
    readingSetCard() {
      this.readingSetDialog = true
      if (
        this.newReadingField &&
        this.newReadingField.readingType === 2 &&
        this.$helpers.isLicenseEnabled('CONTROL_ACTIONS')
      ) {
        this.readingSetDialog = true
      } else {
        this.newReadingField = null
        this.readingSetDialog = false
      }
    },
    cancelReadingSetDialog() {
      this.readingSetDialog = false
    },
    render() {
      this.sparkilneSize.width = this.$el.offsetWidth + 6
      this.chartrender = true
    },
    chooseStartColor(color) {
      this.gaugeLayout.startColor = color
      this.updateMeta()
    },
    chooseEndColor(color) {
      this.gaugeLayout.endColor = color
      this.updateMeta()
    },
    chooseMaxColor(color) {
      this.gaugeLayout.maxColor = color
      this.updateMeta()
    },
    choosecolor(color) {
      this.bgcolor = color
      this.updateMeta()
    },
    choosechartcolor(color) {
      this.chartcolor = color
      this.sparkline.style.stroke = color
      this.sparkline.style.fill = color
      this.updateMeta()
    },
    updateReadingcard() {
      this.error = false
      let paramsJson = {
        xAggr: this.$util.getCardPeriodfromOperatorId(
          this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.operatorId
            ? this.widget.dataOptions.reading.operatorId
            : -1
        ),
        dateOperator:
          this.widget.dataOptions.reading &&
          this.widget.dataOptions.reading.operatorId
            ? this.widget.dataOptions.reading.operatorId
            : -1,
        moduleName:
          this.widget.dataOptions.reading &&
          this.widget.dataOptions.reading.readings &&
          this.widget.dataOptions.reading.readings &&
          this.widget.dataOptions.reading.readings.readingField &&
          this.widget.dataOptions.reading.readings.readingField.module
            ? this.widget.dataOptions.reading.readings.readingField.module.name
            : 'energydata',
        parentId:
          this.widget.dataOptions.reading &&
          this.widget.dataOptions.reading.parentId
            ? this.widget.dataOptions.reading.parentId
            : -1,
        fieldName:
          this.widget.dataOptions.reading &&
          this.widget.dataOptions.reading.readings &&
          this.widget.dataOptions.reading.readings.readingField
            ? this.widget.dataOptions.reading.readings.readingField.name
            : null,
        aggregateOpperator: this.widget.dataOptions.reading
          ? this.aggregateFunctions.find(
              rt => rt.value === this.widget.dataOptions.reading.aggregationFunc
            ).name
          : 3,
        aggregateFunc: this.widget.dataOptions.reading
          ? this.widget.dataOptions.reading.aggregationFunc
          : 3,
        write:
          (this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.write) ||
          false,
        set:
          this.widget.dataOptions.reading &&
          this.widget.dataOptions.reading.write
            ? this.widget.dataOptions.reading.write
            : false,
        fieldId:
          this.widget.dataOptions.reading &&
          this.widget.dataOptions.reading.readings &&
          this.widget.dataOptions.reading.readings.readingField
            ? this.widget.dataOptions.reading.readings.readingField.fieldId
            : -1,
        dateFilter: this.getDateOperatorFromId(
          this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.operatorId
            ? this.widget.dataOptions.reading.operatorId
            : -1
        ),
      }
      if (this.widget.id > -1) {
        if (paramsJson !== this.widget.dataOptions.paramsJson) {
          this.updategetCardData(false)
        } else {
          this.updategetCardData(true, 'update')
        }
      } else {
        if (paramsJson !== this.widget.dataOptions.paramsJson) {
          this.getCardData(false)
        } else {
          this.getCardData(true)
        }
      }
    },
    loadNewReadingFileds() {
      let self = this
      if (this.widget && this.widget.id && this.widget.id > -1) {
        if (
          this.widget.dataOptions &&
          this.widget.dataOptions.paramsJson &&
          this.widget.dataOptions.paramsJson.parentId &&
          this.widget.dataOptions.paramsJson.parentId
        ) {
          this.$util
            .loadLatestReading(this.widget.dataOptions.paramsJson.parentId)
            .then(fields => {
              if (
                self.widget.dataOptions.paramsJson &&
                self.widget.dataOptions.paramsJson.fieldId
              ) {
                self.newReadingField = fields.find(
                  rt =>
                    rt.fieldId === self.widget.dataOptions.paramsJson.fieldId
                )
              } else {
                let self2 = self
                self.newReadingField = fields.find(function(rt) {
                  if (
                    rt.field &&
                    rt.field.name ===
                      self2.widget.dataOptions.paramsJson.fieldName
                  ) {
                    return rt
                  }
                })
              }
              self.assetId = this.widget.dataOptions.paramsJson.parentId
              self.fieldId = this.widget.dataOptions.paramsJson.fieldId
              if (
                self.newReadingField &&
                self.newReadingField.readingType !== 2 &&
                self.$helpers.isLicenseEnabled('CONTROL_ACTIONS')
              ) {
                self.newReadingField = null
              }
              self.loading = false
            })
        }
      } else if (
        this.widget &&
        this.widget.dataOptions &&
        this.widget.dataOptions.params &&
        this.widget.dataOptions.params.paramsJson &&
        this.widget.dataOptions.params.paramsJson.parentId &&
        this.widget.dataOptions.params.paramsJson.fieldId
      ) {
        this.$util
          .loadLatestReading(this.widget.dataOptions.params.paramsJson.parentId)
          .then(fields => {
            self.newReadingField = fields.find(
              rt =>
                rt.fieldId === this.widget.dataOptions.params.paramsJson.fieldId
            )
            self.newReadingField.value =
              self.rawdata && self.rawdata.value
                ? Number(self.rawdata.value)
                : null
            self.assetId = this.widget.dataOptions.params.paramsJson.parentId
            self.fieldId = this.widget.dataOptions.params.paramsJson.fieldId
            if (
              self.newReadingField &&
              self.newReadingField.readingType !== 2 &&
              self.$helpers.isLicenseEnabled('CONTROL_ACTIONS')
            ) {
              self.newReadingField = null
            }
            self.loading = false
          })
      }
    },
    setFieldValue() {
      if (this.newReadingField.value === '') {
        this.$message.error('Please enter a value')
      }
      let self = this
      this.$util
        .setReadingValue(
          this.assetId,
          this.newReadingField.field.fieldId,
          this.newReadingField.value.toString()
        )
        .then(() => {
          if (
            !this.newReadingField.readingType === 2 &&
            !this.$helpers.isLicenseEnabled('CONTROL_ACTIONS')
          ) {
            this.newReadingField = null
          }
        })
        .catch(function(e) {
          if (e) {
            self.$message.success('Error..')
          }
        })
      self.cancelReadingSetDialog()
    },
    choosefontcolor(color) {
      this.fontcolor = color
      this.updateMeta()
    },
    getFontcolor(title, mode) {
      if (title) {
        if (this.layout === 1) {
          if (mode && mode === 'title') {
            let font = 14
            if (title.length < 10) {
              font = 14
            } else if (title.length < 15) {
              font = 13
            } else if (title.length < 25) {
              font = 12
            } else if (title.length < 35) {
              font = 11
            } else if (title.length < 45) {
              font = 10
            } else if (title.length > 45) {
              font = 10
            }
            return 'color:' + this.fontcolor + ';' + 'font-size:' + font + 'px'
          } else if (mode && mode === 'value') {
            let font = 2
            title = String(title)
            if (title.length < 7) {
              font = 2
            } else if (title.length < 8) {
              font = 1.8
            } else if (title.length < 12) {
              font = 1.7
            } else if (title.length > 12) {
              font = 1.4
            }
            return 'color:' + this.fontcolor + ';' + 'font-size:' + font + 'vw'
          }
        } else if (this.layout === 3) {
          if (mode && mode === 'title') {
            let font = 0.9
            if (title.length < 10) {
              font = 1
            } else if (title.length < 15) {
              font = 0.9
            } else if (title.length < 25) {
              font = 0.8
            } else if (title.length < 35) {
              font = 0.8
            } else if (title.length > 45) {
              font = 0.7
            } else if (title.length > 45) {
              font = 0.7
            }
            return 'color:' + this.fontcolor + ';' + 'font-size:' + font + 'vw'
          } else if (mode && mode === 'value') {
            let font = 2
            title = String(title)
            if (title.length < 7) {
              font = 2
            } else if (title.length < 8) {
              font = 1.8
            } else if (title.length < 12) {
              font = 1.7
            } else if (title.length > 12) {
              font = 1.4
            }
            return 'color:' + this.fontcolor + ';' + 'font-size:' + font + 'vw'
          }
        } else if (this.layout === 4) {
          if (mode && mode === 'title') {
            let font = 0.6
            if (title.length < 10) {
              font = 1
            } else if (title.length < 15) {
              font = 0.9
            } else if (title.length < 25) {
              font = 0.8
            } else if (title.length < 35) {
              font = 0.8
            } else if (title.length > 45) {
              font = 0.7
            } else if (title.length > 45) {
              font = 0.6
            }
            return 'color:' + this.fontcolor + ';' + 'font-size:' + font + 'vw'
          } else if (mode && mode === 'value') {
            let font = 2
            title = String(title)
            if (title.length < 7) {
              font = 2
            } else if (title.length < 8) {
              font = 1.8
            } else if (title.length < 12) {
              font = 1.7
            } else if (title.length > 12) {
              font = 1.4
            }
            return 'color:' + this.fontcolor + ';' + 'font-size:' + font + 'vw'
          }
        } else {
          if (mode && mode === 'title') {
            let font = 12
            if (title.length < 10) {
              font = 12
            } else if (title.length < 15) {
              font = 11
            } else if (title.length < 25) {
              font = 10
            } else if (title.length < 35) {
              font = 10
            } else if (title.length < 45) {
              font = 10
            } else if (title.length > 45) {
              font = 10
            }
            return 'color:' + this.fontcolor + ';' + 'font-size:' + font + 'px'
          } else if (mode && mode === 'value') {
            let font = 2
            title = String(title)
            if (title.length < 7) {
              font = 1.3
            } else if (title.length < 8) {
              font = 1
            } else if (title.length < 12) {
              font = 1
            } else if (title.length > 12) {
              font = 1
            }
            return 'color:' + this.fontcolor + ';' + 'font-size:' + font + 'vw'
          }
        }
      } else {
        return 'color:' + this.fontcolor + ''
      }
    },
    updateMeta() {
      let metaJson = {
        background: this.bgcolor,
        fontcolor: this.fontcolor,
        chartcolor: this.chartcolor,
        title: this.title,
        mode: this.layout,
        aggregateFunction: this.aggregateFunction,
        startColor: this.gaugeLayout.startColor,
        endColor: this.gaugeLayout.endColor,
        maxColor: this.gaugeLayout.maxColor,
        needGaugeMax: this.gaugeLayout.needMax,
        colorMap: this.colorMap,
        colorMapConfig: this.colorMapConfig,
        readings: this.readings,
      }
      let paramsJson = null
      if (this.widget.dataOptions.reading) {
        paramsJson = {
          dateOperator:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.operatorId
              ? this.widget.dataOptions.reading.operatorId
              : -1,
          moduleName:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings.readingField &&
            this.widget.dataOptions.reading.readings.readingField.module
              ? this.widget.dataOptions.reading.readings.readingField.module
                  .name
              : 'energydata',
          parentId:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.parentId
              ? this.widget.dataOptions.reading.parentId
              : -1,
          fieldName:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings.readingField
              ? this.widget.dataOptions.reading.readings.readingField.name
              : null,
          aggregateOpperator: this.widget.dataOptions.reading
            ? this.aggregateFunctions.find(
                rt =>
                  rt.value === this.widget.dataOptions.reading.aggregationFunc
              ).name
            : '',
          aggregateFunc: this.widget.dataOptions.reading.aggregationFunc,
          set:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.write
              ? this.widget.dataOptions.reading.write
              : false,
          dateFilter: this.getDateOperatorFromId(
            this.widget.dataOptions.reading &&
              this.widget.dataOptions.reading.operatorId
              ? this.widget.dataOptions.reading.operatorId
              : -1
          ),
        }
        if (this.layout === 4) {
          paramsJson.dateOperator1 =
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.operatorId
              ? this.widget.dataOptions.reading.operatorId
              : -1
          paramsJson.moduleName1 =
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings.readingField &&
            this.widget.dataOptions.reading.readings.readingField.module
              ? this.widget.dataOptions.reading.readings.readingField.module
                  .name
              : 'energydata'
          paramsJson.aggregateOpperator1 = this.widget.dataOptions.reading
            ? this.aggregateFunctions.find(
                rt =>
                  rt.value === this.widget.dataOptions.reading.aggregationFunc
              ).name
            : ''
          paramsJson.aggregateFunc1 = this.widget.dataOptions.reading
            ? this.widget.dataOptions.reading.aggregationFunc
            : ''
          metaJson.parentName1 = this.gaugeLayout.parentName1
          metaJson.parentName2 = this.gaugeLayout.parentName2
          metaJson.enableCenterText1 = this.gaugeLayout.enableCenterText1
          if (
            this.widget.dataOptions.reading.targetreading &&
            this.widget.dataOptions.reading.targetreading.targetmode ===
              'reading'
          ) {
            paramsJson.parentId1 =
              this.widget.dataOptions.reading &&
              this.widget.dataOptions.reading.targetreading &&
              this.widget.dataOptions.reading.targetreading.parentId
                ? this.widget.dataOptions.reading.targetreading.parentId
                : -1
            paramsJson.fieldName1 =
              this.widget.dataOptions.reading &&
              this.widget.dataOptions.reading.readings &&
              this.widget.dataOptions.reading.targetreading.readings
                .readingField &&
              this.widget.dataOptions.reading.targetreading.readings
                .readingField
                ? this.widget.dataOptions.reading.targetreading.readings
                    .readingField.name
                : null
            paramsJson.fieldId1 =
              this.widget.dataOptions.reading &&
              this.widget.dataOptions.reading.readings &&
              this.widget.dataOptions.reading.targetreading.readings
                .readingField &&
              this.widget.dataOptions.reading.targetreading.readings
                .readingField
                ? this.widget.dataOptions.reading.targetreading.readings
                    .readingField.fieldId
                : -1
            paramsJson.constant = -1
          } else {
            paramsJson.parentId1 = -1
            paramsJson.fieldName1 = -1
            paramsJson.fieldId1 = -1
            paramsJson.constant = parseInt(
              this.widget.dataOptions.reading.targetreading.count
            )
          }
          if (
            this.widget.dataOptions.reading.targetreading &&
            this.widget.dataOptions.reading.targetreading.maxmode &&
            this.widget.dataOptions.reading.targetreading.maxmode !==
              'usetarget'
          ) {
            this.gaugeLayout.needMax = this.widget.dataOptions.reading.targetreading.maxmode
            if (
              this.widget.dataOptions.reading.targetreading &&
              this.widget.dataOptions.reading.targetreading.maxmode ===
                'constant'
            ) {
              paramsJson.maxConstant = parseInt(
                this.widget.dataOptions.reading.targetreading.maxValue
              )
              paramsJson.maxPercentage = -1
            } else {
              paramsJson.maxPercentage = parseInt(
                this.widget.dataOptions.reading.targetreading.maxpercentage
              )
              paramsJson.maxConstant = -1
            }
          } else {
            paramsJson.maxConstant = -1
            paramsJson.maxPercentage = -1
          }
        }
      } else {
        paramsJson = this.paramsJson
      }
      this.widget.dataOptions.params = {
        metaJson: metaJson,
        paramsJson: paramsJson,
      }
    },
    updategetCardData(rejectCall, update) {
      let self = this
      this.title =
        this.widget.dataOptions.reading && this.widget.dataOptions.reading.title
          ? this.widget.dataOptions.reading.title
          : ''
      this.layout =
        this.widget.dataOptions.reading && this.widget.dataOptions.reading.mode
          ? this.widget.dataOptions.reading.mode
          : 2
      this.period =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.operatorId
          ? this.getdateOperators().find(
              rt => rt.value === this.widget.dataOptions.reading.operatorId
            ).label
          : ''
      this.aggregateFunction =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.legend
          ? this.widget.dataOptions.reading.legend
          : ''
      this.aggregationFuncName =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.aggregationFunc
          ? this.widget.dataOptions.reading.aggregationFunc
          : 6
      this.set =
        self.widget.dataOptions.reading && self.widget.dataOptions.reading.write
          ? self.widget.dataOptions.reading.write
          : false
      this.gaugeLayout.parentName1 =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.parentName1
          ? this.widget.dataOptions.reading.parentName1
          : ''
      this.gaugeLayout.parentName2 =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.targetreading &&
        this.widget.dataOptions.reading.targetreading.parentName2
          ? this.widget.dataOptions.reading.targetreading.parentName2
          : ''
      this.gaugeLayout.needMax =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.targetreading &&
        this.widget.dataOptions.reading.targetreading.maxmode &&
        this.widget.dataOptions.reading.targetreading.maxmode === 'usetarget'
          ? this.widget.dataOptions.reading.targetreading.maxmode
          : 'usetarget'
      this.gaugeLayout.enableCenterText1 =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.targetreading &&
        this.widget.dataOptions.reading.targetreading['enableCenterText1'] !==
          undefined
          ? this.widget.dataOptions.reading.targetreading.enableCenterText1
          : true
      this.colorMap =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.colorMap
          ? this.widget.dataOptions.reading.colorMap
          : false
      this.colorMapConfig =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.colorMapConfig
          ? this.widget.dataOptions.reading.colorMapConfig
          : {}
      this.readings =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.readings
          ? this.widget.dataOptions.reading.readings
          : null
      let params = null
      params = this.setParams()
      if (!rejectCall) {
        this.getReadingData(params)
      }
      if (update) {
        this.updateMeta()
      }
    },
    getCardData(rejectCall, update) {
      let self = this
      this.title =
        this.widget.dataOptions.reading && this.widget.dataOptions.reading.title
          ? this.widget.dataOptions.reading.title
          : ''
      this.layout =
        this.widget.dataOptions.reading && this.widget.dataOptions.reading.mode
          ? this.widget.dataOptions.reading.mode
          : 2
      this.period =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.operatorId
          ? this.getdateOperators().find(
              rt => rt.value === this.widget.dataOptions.reading.operatorId
            ).label
          : ''
      this.aggregateFunction =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.legend
          ? this.widget.dataOptions.reading.legend
          : ''
      this.aggregationFuncName =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.aggregationFunc
          ? this.widget.dataOptions.reading.aggregationFunc
          : 6
      this.set =
        self.widget.dataOptions.reading && self.widget.dataOptions.reading.write
          ? self.widget.dataOptions.reading.write
          : false
      this.gaugeLayout.parentName1 =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.parentName1
          ? this.widget.dataOptions.reading.parentName1
          : ''
      this.gaugeLayout.parentName2 =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.targetreading &&
        this.widget.dataOptions.reading.targetreading.parentName2
          ? this.widget.dataOptions.reading.targetreading.parentName2
          : ''
      this.gaugeLayout.needMax =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.targetreading &&
        this.widget.dataOptions.reading.targetreading.maxmode &&
        this.widget.dataOptions.reading.targetreading.maxmode === 'usetarget'
          ? this.widget.dataOptions.reading.targetreading.maxmode
          : 'usetarget'
      this.gaugeLayout.enableCenterText1 =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.targetreading &&
        this.widget.dataOptions.reading.targetreading['enableCenterText1'] !==
          undefined
          ? this.widget.dataOptions.reading.targetreading.enableCenterText1
          : true
      this.colorMap =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.colorMap
          ? this.widget.dataOptions.reading.colorMap
          : false
      this.colorMapConfig =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.colorMapConfig
          ? this.widget.dataOptions.reading.colorMapConfig
          : {}
      this.readings =
        this.widget.dataOptions.reading &&
        this.widget.dataOptions.reading.readings
          ? this.widget.dataOptions.reading.readings
          : null
      let params = null
      if (this.widget.id === -1) {
        params = this.setParams()
      } else {
        params = null
        params = {
          widgetId: self.widget.id,
        }
      }
      if (!rejectCall) {
        this.getReadingData(params)
      }
      if (update) {
        this.updateMeta()
      }
    },
    setParams() {
      let params = null
      params = {
        staticKey: this.layoutkey[this.layout],
        paramsJson: {
          xAggr: this.$util.getCardPeriodfromOperatorId(
            this.widget.dataOptions.reading &&
              this.widget.dataOptions.reading.operatorId
              ? this.widget.dataOptions.reading.operatorId
              : -1
          ),
          dateOperator:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.operatorId
              ? this.widget.dataOptions.reading.operatorId
              : -1,
          moduleName:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings.readingField &&
            this.widget.dataOptions.reading.readings.readingField.module
              ? this.widget.dataOptions.reading.readings.readingField.module
                  .name
              : 'energydata',
          parentId:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.parentId
              ? this.widget.dataOptions.reading.parentId
              : -1,
          fieldName:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings.readingField
              ? this.widget.dataOptions.reading.readings.readingField.name
              : null,
          aggregateOpperator: this.widget.dataOptions.reading
            ? this.aggregateFunctions.find(
                rt =>
                  rt.value === this.widget.dataOptions.reading.aggregationFunc
              ).name
            : 'sum',
          aggregateFunc: this.widget.dataOptions.reading
            ? this.widget.dataOptions.reading.aggregationFunc
            : 3,
          write:
            (this.widget.dataOptions.reading &&
              this.widget.dataOptions.reading.write) ||
            false,
          set:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.write
              ? this.widget.dataOptions.reading.write
              : false,
          fieldId:
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.readings.readingField
              ? this.widget.dataOptions.reading.readings.readingField.fieldId
              : -1,
          dateFilter: this.getDateOperatorFromId(
            this.widget.dataOptions.reading &&
              this.widget.dataOptions.reading.operatorId
              ? this.widget.dataOptions.reading.operatorId
              : -1
          ),
        },
        metaJson: {
          background: this.bgcolor,
          fontcolor: this.fontcolor,
          chartcolor: this.chartcolor,
          title: this.title,
          mode: this.layout,
          aggregateFunction: this.aggregateFunction,
          colorMap: this.colorMap,
          colorMapConfig: this.colorMapConfig,
          readings: this.readings,
        },
      }
      if (this.layout === 4) {
        this.aggParam = params.paramsJson.aggregateFunc
        params.metaJson.startColor = this.gaugeLayout.startColor
        params.metaJson.endColor = this.gaugeLayout.endColor
        params.metaJson.maxColor = this.gaugeLayout.maxColor
        params.metaJson.parentName1 = this.gaugeLayout.parentName1
        params.metaJson.parentName2 = this.gaugeLayout.parentName2
        params.metaJson.enableCenterText1 = this.gaugeLayout.enableCenterText1
        params.paramsJson.dateOperator1 =
          this.widget.dataOptions.reading &&
          this.widget.dataOptions.reading.operatorId
            ? this.widget.dataOptions.reading.operatorId
            : -1
        params.paramsJson.moduleName1 =
          this.widget.dataOptions.reading &&
          this.widget.dataOptions.reading.readings &&
          this.widget.dataOptions.reading.readings &&
          this.widget.dataOptions.reading.readings.readingField &&
          this.widget.dataOptions.reading.readings.readingField.module
            ? this.widget.dataOptions.reading.readings.readingField.module.name
            : 'energydata'
        params.paramsJson.aggregateOpperator1 = this.aggregateFunctions.find(
          rt => rt.value === this.widget.dataOptions.reading.aggregationFunc
        ).name
        params.paramsJson.aggregateFunc1 = this.widget.dataOptions.reading.aggregationFunc
        if (
          this.widget.dataOptions.reading.targetreading &&
          this.widget.dataOptions.reading.targetreading.targetmode === 'reading'
        ) {
          params.paramsJson.parentId1 =
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.targetreading &&
            this.widget.dataOptions.reading.targetreading.parentId
              ? this.widget.dataOptions.reading.targetreading.parentId
              : -1
          params.paramsJson.fieldName1 =
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.targetreading.readings
              .readingField &&
            this.widget.dataOptions.reading.targetreading.readings.readingField
              ? this.widget.dataOptions.reading.targetreading.readings
                  .readingField.name
              : null
          params.paramsJson.fieldId1 =
            this.widget.dataOptions.reading &&
            this.widget.dataOptions.reading.readings &&
            this.widget.dataOptions.reading.targetreading.readings
              .readingField &&
            this.widget.dataOptions.reading.targetreading.readings.readingField
              ? this.widget.dataOptions.reading.targetreading.readings
                  .readingField.fieldId
              : -1
          params.paramsJson.constant = -1
        } else {
          params.paramsJson.parentId1 = -1
          params.paramsJson.fieldName1 = -1
          params.paramsJson.fieldId1 = -1
          params.paramsJson.constant = parseInt(
            this.widget.dataOptions.reading.targetreading.count
          )
        }
        if (
          this.widget.dataOptions.reading.targetreading &&
          this.widget.dataOptions.reading.targetreading.maxmode &&
          this.widget.dataOptions.reading.targetreading.maxmode !== 'usetarget'
        ) {
          this.gaugeLayout.needMax = this.widget.dataOptions.reading.targetreading.maxmode
          params.metaJson.needGaugeMax = this.gaugeLayout.needMax
          if (
            this.widget.dataOptions.reading.targetreading &&
            this.widget.dataOptions.reading.targetreading.maxmode === 'constant'
          ) {
            params.paramsJson.maxConstant = parseInt(
              this.widget.dataOptions.reading.targetreading.maxValue
            )
            params.paramsJson.maxPercentage = -1
          } else {
            params.paramsJson.maxPercentage = parseInt(
              this.widget.dataOptions.reading.targetreading.maxpercentage
            )
            params.paramsJson.maxConstant = -1
          }
        } else {
          params.paramsJson.maxConstant = -1
          params.paramsJson.maxPercentage = -1
        }
      }
      this.widget.dataOptions.staticKey = params.staticKey
      this.widget.dataOptions.params = params
      return params
    },

    getReadingData(params) {
      //use dashboard filter if enabled in dashboard and widget is configured to use dashboard filter
      if (
        this.dashboardDateFilter &&
        (this.widget.widgetSettingsJson
          ? this.widget.widgetSettingsJson.useDashboardFilter
          : true)
      ) {
        params.startTime = this.getDateFilterStartTime()
        params.endTime = this.getDateFilterEndTime()
        //  params = this.getDateFilterParams(params)
      }
      let self = this
      self.loading = true
      let orgDetails = [1, 146, 191]
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.result = response.data.cardResult
          if (self.widget.dataOptions.staticKey === 'readingWithGraphCard') {
            self.prepareDataGraphData(response.data.cardResult)
          } else if (self.widget.dataOptions.staticKey === 'readingGaugeCard') {
            self.preapareGaugeData(response.data.cardResult)
          } else {
            self.prepareData(response.data.cardResult)
          }
          if (
            self.$helpers.isLicenseEnabled('CONTROL_ACTIONS') &&
            (self.layout === 1 || self.layout === 2)
          ) {
            self.loadNewReadingFileds()
          } else {
            self.loading = false
          }
          self.setColorMapData(response.data.cardResult)
        })
        .catch(function(error) {
          console.log(error)
          self.error = true
          self.loading = false
        })
    },
    formatPeriod(period) {
      if (this.dashboardDateFilter) {
        return this.dashboardDateFilter.label
      }
      if (this.aggregationFuncName === 6 && this.layout !== 3) {
        return 'CURRENT VALUE'
      } else {
        return period
      }
    },
    setDefaultMeta(data) {
      if (data === 'NO DATA') {
        if (this.result && Object.keys(this.colorMapConfig).length) {
          this.fontcolor = this.backupFontcolor ? this.backupFontcolor : '#000'
          this.bgcolor = this.backupbgcolor ? this.backupbgcolor : '#fff'
        }
        return data
      } else {
        return data
      }
    },
    formatValue(value) {
      this.backupbgcolor = this.$helpers.cloneObject(this.bgcolor)
      this.backupFontcolor = this.$helpers.cloneObject(this.fontcolor)
      if (
        this.result &&
        this.result.result &&
        Object.keys(this.colorMapConfig).length
      ) {
        if (this.colorMapConfig['BOOLEAN']) {
          if (
            this.readings &&
            this.readings.readingField &&
            this.readings.readingField.hasOwnProperty('trueVal') &&
            this.result.result === this.readings.readingField.trueVal
          ) {
            this.fontcolor = this.colorMapConfig['BOOLEAN'].max.textColor
            this.bgcolor = this.colorMapConfig['BOOLEAN'].max.bgColor
            this.unit = this.colorMapConfig.BOOLEAN.max.label ? '' : this.unit
            return this.colorMapConfig.BOOLEAN.max.label
              ? this.colorMapConfig.BOOLEAN.max.label
              : value
          } else if (
            this.readings &&
            this.readings.readingField &&
            this.readings.readingField.hasOwnProperty('falseVal') &&
            this.result.result === this.readings.readingField.falseVal
          ) {
            this.fontcolor = this.colorMapConfig['BOOLEAN'].min.textColor
            this.bgcolor = this.colorMapConfig['BOOLEAN'].min.bgColor
            this.unit = this.colorMapConfig.BOOLEAN.min.label ? '' : this.unit
            return this.colorMapConfig.BOOLEAN.min.label
              ? this.colorMapConfig.BOOLEAN.min.label
              : value
          } else if (
            Number(this.result.result) === 0 ||
            this.result.result === false
          ) {
            this.fontcolor = this.colorMapConfig['BOOLEAN'].min.textColor
            this.bgcolor = this.colorMapConfig['BOOLEAN'].min.bgColor
            this.unit = this.colorMapConfig.BOOLEAN.min.label ? '' : this.unit
            return this.colorMapConfig.BOOLEAN.min.label
              ? this.colorMapConfig.BOOLEAN.min.label
              : value
          } else if (
            Number(this.result.result) === 1 ||
            this.result.result === true
          ) {
            this.fontcolor = this.colorMapConfig['BOOLEAN'].max.textColor
            this.bgcolor = this.colorMapConfig['BOOLEAN'].max.bgColor
            this.unit = this.colorMapConfig.BOOLEAN.max.label ? '' : this.unit
            return this.colorMapConfig.BOOLEAN.max.label
              ? this.colorMapConfig.BOOLEAN.max.label
              : value
          } else {
            this.fontcolor = this.backupFontcolor
              ? this.backupFontcolor
              : '#000'
            this.bgcolor = this.backupbgcolor ? this.backupbgcolor : '#fff'
            return value
          }
        } else if (this.colorMapConfig['ENUM']) {
          if (this.colorMapConfig['ENUM'][Number(this.result.result)]) {
            this.fontcolor = this.colorMapConfig['ENUM'][
              Number(this.result.result)
            ].textColor
            this.bgcolor = this.colorMapConfig['ENUM'][
              Number(this.result.result)
            ].bgColor
            this.unit = this.colorMapConfig['ENUM'][Number(this.result.result)]
              .label
              ? ''
              : this.unit
            return this.colorMapConfig['ENUM'][Number(this.result.result)].label
              ? this.colorMapConfig['ENUM'][Number(this.result.result)].label
              : value
          } else {
            this.fontcolor = this.backupFontcolor
              ? this.backupFontcolor
              : '#000'
            this.bgcolor = this.backupbgcolor ? this.backupbgcolor : '#fff'
            return value
          }
        } else if (this.colorMapConfig['NUMBER']) {
          if (this.result.hasOwnProperty('result')) {
            let self = this
            let i = 0
            this.colorMapConfig['NUMBER'].forEach(function(rt) {
              Number.prototype.between = function(a, b) {
                let min = Math.min.apply(Math, [a, b]),
                  max = Math.max.apply(Math, [a, b])
                return this > min && this < max
              }
              if (Number(self.result.result).between(rt.min, rt.max)) {
                i++
                self.fontcolor = rt.textColor
                self.bgcolor = rt.bgColor
                value = rt.label || value
                self.unit = rt.label ? '' : self.unit
              }
            })
            if (!i) {
              this.bgcolor = '#fff'
              this.fontcolor = '#000'
            }
          }
          return value
        } else if (this.colorMapConfig['DECIMAL']) {
          if (this.result.hasOwnProperty('result')) {
            let self = this
            let i = 0
            this.colorMapConfig['DECIMAL'].forEach(function(rt) {
              Number.prototype.between = function(a, b) {
                let min = Math.min.apply(Math, [a, b]),
                  max = Math.max.apply(Math, [a, b])
                return this > min && this < max
              }
              if (Number(self.result.result).between(rt.min, rt.max)) {
                i++
                self.fontcolor = rt.textColor
                self.bgcolor = rt.bgColor
                value = rt.label || value
                self.unit = rt.label ? '' : self.unit
              }
            })
            if (!i) {
              this.bgcolor = '#fff'
              this.fontcolor = '#000'
            }
          }
          return value
        } else {
          this.fontcolor = this.backupFontcolor ? this.backupFontcolor : '#000'
          this.bgcolor = this.backupbgcolor ? this.backupbgcolor : '#fff'
          return value
        }
      } else {
        this.fontcolor = this.backupFontcolor ? this.backupFontcolor : '#000'
        this.bgcolor = this.backupbgcolor ? this.backupbgcolor : '#fff'
        return value
      }
    },
    setColorMapData(data) {},
    getRedirectUrl() {
      if (!this.$mobile) {
        let datePicker = {
          operatorId:
            this.result.widget &&
            this.result.widget.paramsJson &&
            this.result.widget.paramsJson.dateOperator
              ? this.result.widget.paramsJson.dateOperator
              : this.widget &&
                this.widget.dataOptions &&
                this.widget.dataOptions.params &&
                this.widget.dataOptions.params &&
                this.widget.dataOptions.params.paramsJson &&
                this.widget.dataOptions.params.paramsJson &&
                this.widget.dataOptions.params.paramsJson.dateOperator
              ? this.widget.dataOptions.params.paramsJson.dateOperator
              : 28,
          value: null,
        }
        if (this.mode) {
          let self = this
          self.$dialog
            .confirm({
              title: 'Redirect to Analytics',
              message: 'Are you sure you want to redirect?',
              rbDanger: true,
              rbLabel: 'OK',
            })
            .then(function(value) {
              if (value) {
                self.jumpCardToAnalytics(
                  self.widget.id,
                  datePicker,
                  self.widget
                )
              }
            })
        } else {
          this.jumpCardToAnalytics(this.widget.id, datePicker, this.widget)
        }
      }
    },
    tempFormate(value, newResult) {
      if (isNaN(value) && !isNaN(newResult)) {
        return isNaN(Number(newResult))
          ? newResult
          : Number(newResult).toFixed(2)
      } else {
        return value
      }
    },
    setNormalMeta() {
      if (this.widget && data.widget.metaJson) {
        let meta = JSON.parse(data.widget.metaJson)
        this.bgcolor = meta.background
        this.fontcolor = meta.fontcolor
      }
    },
    prepareData(data) {
      if (data.widget && data.widget.metaJson) {
        let meta = JSON.parse(data.widget.metaJson)
        this.bgcolor = meta.background
        this.fontcolor = meta.fontcolor
        this.sparkline.style.stroke = meta.chartcolor
        this.sparkline.style.fill = meta.chartcolor
        this.title = meta.title
        this.layout = meta.mode ? meta.mode : 2
        this.aggregateFunction = meta.aggregateFunction
        this.colorMap = meta.colorMap ? meta.colorMap : false
        this.colorMapConfig = meta.colorMapConfig ? meta.colorMapConfig : {}
        this.readings = meta.readings ? meta.readings : null
      }
      if (data.widget && data.widget.paramsJson) {
        this.paramsJson = data.widget.paramsJson
        this.period = this.getdateOperators().find(
          rt => rt.value === data.widget.paramsJson.dateOperator
        ).label
        this.set =
          data.widget.paramsJson && data.widget.paramsJson.set
            ? data.widget.paramsJson.set
            : false
        this.aggregationFuncName =
          data.widget.paramsJson && data.widget.paramsJson.aggregateFunc
            ? data.widget.paramsJson.aggregateFunc
            : 3
      }
      if (data.result && data.unitString) {
        this.rawdata = {
          value: data.result,
          unit: ' ' + data.unitString,
          lowerUnit: this.$helpers.cloneObject(data.unitString),
        }
        this.unitLeft = false
      } else if (data.result && data.unit && data.unit.symbol) {
        this.rawdata = {
          value: data.result,
          unit: data.unit.symbol,
          lowerUnit: this.$helpers.cloneObject(data.unit.symbol),
        }
        this.unitLeft = data.unit.isLeft || false
      } else {
        this.rawdata = {
          value: data.result,
          unit: '',
          lowerUnit: '',
        }
      }
      let formatedJson = formatter.formatCardValue(
        this.rawdata.value,
        this.rawdata.unit,
        this.rawdata.lowerUnit
      )
      this.value =
        this.rawdata.value === 0
          ? 0 + ' ' + this.rawdata.lowerUnit
          : formatedJson.value
      this.unit = formatedJson.unit
    },
    prepareDataGraphData(data) {
      let self = this
      if (data.widget && data.widget.metaJson) {
        let meta = JSON.parse(data.widget.metaJson)
        this.bgcolor = meta.background
        this.fontcolor = meta.fontcolor
        this.sparkline.style.stroke = meta.chartcolor
        this.sparkline.style.fill = meta.chartcolor
        this.title = meta.title
        this.layout = meta.mode ? meta.mode : 2
        this.aggregateFunction =
          meta.aggregateFunction === 'lastValue'
            ? meta.aggregateFunction
            : meta.aggregateFunction.toLowerCase()
        this.colorMap = meta.colorMap ? meta.colorMap : false
        this.colorMapConfig = meta.colorMapConfig ? meta.colorMapConfig : {}
        this.readings = meta.readings ? meta.readings : null
      }
      if (data.widget && data.widget.paramsJson) {
        this.paramsJson = data.widget.paramsJson
        this.period = this.getdateOperators().find(
          rt => rt.value === data.widget.paramsJson.dateOperator
        ).label
      }
      if (data.alarmSeverity && data.alarmSeverity.severity) {
        this.alarm = data.alarmSeverity.severity
      }
      if (Object.keys(data.result.reportData.aggr).length) {
        let result =
          data.result.reportData.aggr['A.' + this.aggregateFunction] || '---'
        this.newResult =
          data.result.reportData.aggr['A.' + this.aggregateFunction]
        result =
          result !== null
            ? Number(result) === isNaN
              ? result.toFixed(2)
              : result
            : result
        this.rawdata = {
          value: result,
          unit: '',
          lowerUnit: '',
        }
        if (data.result && data.unitString) {
          this.rawdata = {
            value: data.result,
            unit: ' ' + data.unitString,
            lowerUnit: this.$helpers.cloneObject(data.unitString),
          }
          this.unitLeft = false
        } else if (data.unit && data.unit.symbol) {
          this.rawdata = {
            value: result,
            unit: data.unit.symbol,
            lowerUnit: this.$helpers.cloneObject(data.unit.symbol),
          }
          this.unitLeft = data.unit.isLeft || false
        } else {
          this.rawdata = {
            value: result,
            unit: '',
            lowerUnit: '',
          }
        }
      } else {
        this.value = '---'
        this.unit = ''
      }
      let formatedJson = null
      if (this.rawdata && this.rawdata.value) {
        formatedJson = formatter.formatCardValue(
          this.rawdata.value,
          this.rawdata.unit,
          this.rawdata.lowerUnit
        )
      } else {
        formatedJson = {
          value: '---',
          unit: '',
        }
      }
      this.value = formatedJson.value
      this.unit = formatedJson.unit
      // if (Object.keys(data.result.reportData).length && data.result.reportData[Object.keys(data.result.reportData)[0]] && data.result.reportData[Object.keys(data.result.reportData)[0]].actual) {
      //   let dataraw = (data.result.reportData[Object.keys(data.result.reportData)[0]] && data.result.reportData[Object.keys(data.result.reportData)[0]].actual) ? data.result.reportData[Object.keys(data.result.reportData)[0]].actual : {}
      //   let data3 = []
      //   data3 = this.$helpers.cloneObject(dataraw)
      //   this.sparkline.data = Object.keys(data3).map(function (key) {
      //     return data3[key]
      //   })
      //   this.sparkline.labelArray = Object.keys(data3).map(function (key) {
      //     return Number(key)
      //   })
      //   if (this.rawdata) {
      //     this.sparkline.label = {
      //       formatter (val) {
      //         // let data = `<label>${formatter.formatCardTime(self.sparkline.labelArray[val.index], self.paramsJson.xAggr, self.paramsJson.dateOperator)}</label> : <label style="color:#fff;font-weight:bold;">${val.value}</label>&nbsp;<label>` + self.rawdata.unit + `</label>`
      //         let data2 = `<div style="padding:3px;"><div><label>${formatter.formatCardTime(self.sparkline.labelArray[val.index], self.paramsJson.xAggr, self.paramsJson.dateOperator)}</label></div>`
      //         data2 += `<div><label style="color:#fff;font-weight:bold;">${val.value}</label>&nbsp;<label>` + self.rawdata.unit + `</label></div></div>`
      //         return data2
      //       }
      //     }
      //   }
      // }

      if (data.result.reportData.data.length) {
        this.sparkline.data = data.result.reportData.data.map(rt =>
          Number(rt['A'])
        )
        this.sparkline.labelArray = data.result.reportData.data.map(
          rt => rt['X']
        )
        if (this.rawdata) {
          this.sparkline.label = {
            formatter(val) {
              // let data = `<label>${formatter.formatCardTime(self.sparkline.labelArray[val.index], self.paramsJson.xAggr, self.paramsJson.dateOperator)}</label> : <label style="color:#fff;font-weight:bold;">${val.value}</label>&nbsp;<label>` + self.rawdata.unit + `</label>`
              let data2 = `<div style="padding:3px;"><div><label>${formatter.formatCardTime(
                self.sparkline.labelArray[val.index],
                self.paramsJson.xAggr,
                self.paramsJson.dateOperator
              )}</label></div>`
              data2 +=
                `<div><label style="color:#fff;font-weight:bold;">${val.value}</label>&nbsp;<label>` +
                self.rawdata.unit +
                `</label></div></div>`
              return data2
            },
          }
        }
      }
      if (this.rawdata === null) {
        this.rawdata.value = 'No Data'
        this.rawdata.unit = ''
      }
    },
    preapareGaugeData(data) {
      if (data.result) {
        this.aggParam = data.result.aggregateOpperator
          ? this.legendEnamMap[data.result.aggregateOpperator]
          : 3
        let gaugeMeta = this.perepareGaugeMetaData(data.result)
        let gaugeValue = this.prepareGaugeValue(data.result, data.widget)
        console.log('*********** gauge data', gaugeMeta, gaugeValue)
        this.gaugeData = {
          meta: gaugeMeta,
          value: gaugeValue,
          raw: data.result,
        }
      }
    },
    perepareGaugeMetaData(result) {
      if (result) {
        let data = null
        if (result.result) {
          if (result.result.val1 === null && result.result.constant === null) {
            this.error = true
            return null
          } else {
            result = this.$helpers.cloneObject(result.result)
          }
        }
        if (result.val1 === null || result.constant === null) {
          this.error = true
          return null
        }
        if (result.val1 && result.constant && result.percent) {
          data = {
            max: Number(result.constant),
            percent: Number(result.percent),
            value: Number(result.val1),
          }
        } else if (result.val1 && !result.constant && result.percent) {
          data = {
            max: Number(result.val1),
            percent: Number(result.percent),
            value: Number(result.val1),
          }
        } else if (!result.val1 && result.constant && result.percent) {
          data = {
            max: Number(result.constant),
            percent: Number(result.percent),
            value: Number(result.constant),
          }
        }
        if (result.maxPercentage > -1) {
          data.maxPercentage = Number(result.maxPercentage) / 100
        }
        if (result.maxConstant > -1) {
          data.maxConstant = Number(result.maxConstant)
        }
        if (result.isPlus && result.isPlus === 'true') {
          data.isPlus = true
        }
        data.color = '#f866a0,#ecb0c8'
        return data
      }
    },
    prepareGaugeValue(result, widget) {
      if (widget && widget.metaJson) {
        let meta = JSON.parse(widget.metaJson)
        this.bgcolor = meta.background
        this.fontcolor = meta.fontcolor
        this.title = meta.title
        this.layout = meta.mode || 2
        this.gaugeLayout.needMax = meta.needGaugeMax
        this.gaugeLayout.startColor = meta.startColor
        this.gaugeLayout.endColor = meta.endColor
        this.gaugeLayout.maxColor = meta.maxColor
        this.gaugeLayout.parentName1 = meta.parentName1 || ''
        this.gaugeLayout.parentName2 = meta.parentName2 || ''
        this.gaugeLayout.enableCenterText1 =
          meta['enableCenterText1'] !== undefined
            ? meta.enableCenterText1
            : true
        this.colorMap = meta.colorMap ? meta.colorMap : false
        this.colorMapConfig = meta.colorMapConfig ? meta.colorMapConfig : {}
        this.readings = meta.readings ? meta.readings : null
      }
      if (widget && widget.paramsJson) {
        this.paramsJson = widget.paramsJson
        this.period = this.getdateOperators().find(
          rt => rt.value === widget.paramsJson.dateOperator
        ).label
      }
      if (result.val1 && result.unit && result.unit.symbol) {
        result.constant = result.constant
          ? Number(result.constant).toFixed(2)
          : result.constant
        result.val1 = result.val1 ? Number(result.val1).toFixed(2) : result.val1
        this.gaugeLayout.centerText = [
          {
            title: this.gaugeLayout.parentName1,
            label: formatter.formatCardValue(
              result.val1,
              result.unit.symbol,
              result.unit.symbol.toLowerCase()
            ).value,
            unit: formatter.formatCardValue(
              result.val1,
              result.unit.symbol,
              result.unit.symbol.toLowerCase()
            ).unit,
            unitLeft: result.unit.isLeft || false,
            enable: this.gaugeLayout.enableCenterText1,
          },
          {
            label:
              this.aggParam === 6
                ? this.getGaugePeriod('Last value')
                : this.period,
          },
          {
            title: this.gaugeLayout.parentName2
              ? this.gaugeLayout.parentName2
              : 'Target',
            label:
              result.unit1 && result.unit1.symbol
                ? formatter.formatCardValue(
                    result.constant,
                    result.unit1.symbol,
                    result.unit1.symbol.toLowerCase()
                  ).value +
                  ' ' +
                  formatter.formatCardValue(
                    result.constant,
                    result.unit1.symbol,
                    result.unit1.symbol.toLowerCase()
                  ).unit
                : result.constant
                ? formatter.formatCardValue(
                    result.constant,
                    result.unit.symbol,
                    result.unit.symbol.toLowerCase()
                  ).value +
                  ' ' +
                  formatter.formatCardValue(
                    result.constant,
                    result.unit.symbol,
                    result.unit.symbol.toLowerCase()
                  ).unit
                : '',
          },
          {
            label:
              result.unit1 && result.unit1.symbol
                ? result.unit1.symbol
                : result.unit && result.unit.symbol
                ? result.unit.symbol
                : '',
            label2: result.constant
              ? formatter.formatCardValue(
                  result.constant,
                  result.unit.symbol,
                  result.unit.symbol.toLowerCase()
                ).value
              : '',
          },
        ]
      } else if (result.val1) {
        this.gaugeLayout.centerText = [
          {
            label: Math.round(result.val1),
          },
          {
            label: this.period,
          },
        ]
      }
      return this.gaugeLayout
    },
  },
}
</script>
<style>
.readingcard-header {
  font-size: 1vw;
  font-weight: bold;
  letter-spacing: 1.3px;
  text-align: center;
  color: #171619;
  text-transform: uppercase;
  text-overflow: ellipsis;
  padding-left: 15px;
  padding-right: 15px;
  line-height: 1.1rem;
  padding-top: 10px;
}
.readingcard-value {
  font-size: 1.3vw;
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0px;
  color: #171619;
}
.readingcard-unit {
  font-size: 1.25vw;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.1px;
  color: #171619;
}
.readingcard-time {
  font-size: 0.8vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.3px;
  text-align: center;
  /* color: #fff !important; */
  text-transform: uppercase;
  padding: 5%;
  opacity: 0.5;
  padding-top: 5px !important;
  padding-bottom: 0px;
}
.readingcard-edit {
  position: absolute;
  left: 10px;
  top: 18px;
  font-size: 18px;
  z-index: 8;
  color: #868686;
  display: none;
  cursor: pointer;
}
.dragabale-card.readingcard:hover .readingcard-edit {
  display: block !important;
}
.readingcard .el-color-picker {
  position: absolute;
  left: 10px;
  top: 15px;
  display: none;
}
.dragabale-card.readingcard:hover .el-color-picker {
  display: block !important;
}
.card-theame .readingcard-header,
.card-theame .readingcard-unit,
.card-theame .readingcard-value {
  color: #fff;
}
.readingcard .el-switch {
  position: absolute;
  left: 10px;
  bottom: 10px;
}
.card-theame .readingcard-time {
  font-size: 12px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.3px;
  text-align: center;
  color: #ffffff;
}
.readingcard {
  height: 100%;
  box-shadow: 0 7px 6px 0 rgba(192, 192, 192, 0.5);
  border-radius: 3px;
  border: solid 1px #eaeaec;
}
.reading-card-color-picker {
  position: absolute;
  top: 16px;
  right: 33px;
  color: #000;
  opacity: 0.6;
  font-weight: 500;
  font-size: 18px;
  cursor: pointer;
  z-index: 10;
}
.reading-card-edit-icon {
  position: absolute;
  top: 16px;
  left: 20px;
  color: #000;
  opacity: 0.6;
  font-weight: 500;
  font-size: 18px;
  cursor: pointer;
  z-index: 10;
}
.color-picker-conatiner {
  display: inline-grid;
}
.color-box {
  height: 20px;
  width: 20px;
  margin: 2px;
  border: 1px solid #f4f4f4;
  cursor: pointer;
}
.color-picker-section {
}
.reading-color-picker {
  padding: 20px;
}
.c-picker-label {
  font-size: 11px;
  font-weight: 400;
  text-transform: uppercase;
  padding: 10px;
  padding-left: 2px;
  letter-spacing: 1px;
}
.color-choose-icon {
  display: none;
}
.dragabale-card.readingcard:hover .color-choose-icon {
  display: block !important;
}
.editmode .dragabale-card.readingcard:hover .readingcard-header {
  max-width: 65%;
  margin: auto;
  overflow: hidden;
  white-space: nowrap;
  margin-top: 0;
  color: #171619 !important;
  margin-bottom: 0;
  letter-spacing: 0.9px;
}
.readingcard-data {
  max-width: 90%;
  margin: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 0px;
  margin-bottom: 0px;
  padding-top: 10px;
  cursor: pointer;
  display: inline;
}
.readingcard-section {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  height: 100%;
  width: 100%;
  max-width: 100%;
  max-height: 100%;
  overflow: auto;
}
.layout2 .readingcard-time,
.layout2 .readingcard-data {
  padding-top: 5px;
  padding-bottom: 3px;
  padding: 0;
}
.layout2 .readingcard-header {
  padding-top: 0;
  padding-bottom: 5px;
}
.layout2 .readingcard-value {
  font-size: 1.7vw;
}
.layout3 .readingcard-data {
  padding-top: 0 !important;
  position: relative;
  bottom: 15px;
}
.layout3 .readingcard-time {
  font-size: 0.74vw;
  position: relative;
  bottom: 2px;
  padding-top: 3px !important;
  text-transform: capitalize;
  font-weight: 500;
}

.layout3 .sparkline-wrap {
  position: absolute;
  bottom: -4px;
  margin-left: -1px;
  width: 100%;
}
.sparkline-wrap svg text {
  text-transform: uppercase;
}
.layout3 {
  overflow: hidden;
}
.layout3 .readingcard-header {
  position: relative;
  bottom: 40px;
  font-size: 1.1vw;
  font-weight: normal;
  color: #171619;
}
.reading-card-border {
  width: 100%;
  height: 2.5px;
  background: #fff;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
}
.layout3 .readingcard-value,
.layout3 .readingcard-unit {
  font-size: 1.3vw;
  font-weight: 500;
  line-height: 1.4;
  letter-spacing: 0.3px;
  text-align: center;
  color: #171619 !important;
}
.layout3 .card3-measure-txt {
  bottom: 16px;
}
.layout3 .reading-card-time2 {
  color: #67646c !important;
  opacity: 1 !important;
  bottom: 6px;
  text-transform: uppercase;
}
.layout3 .reading-card-time3 {
  color: #171619;
  opacity: 0.6 !important;
}
@media only screen and (min-width: 1024px) {
  .layout3 .readingcard-value,
  .layout3 .readingcard-unit {
    font-size: 1.3vw !important;
  }
  .readingcard-header {
    font-size: 1vw;
  }
  .layout1 .readingcard-header {
    padding-top: 0px;
  }
  .layout1 .readingcard-data {
    padding-top: 15px;
    padding-bottom: 10px;
  }

  .layout2 .readingcard-value {
    font-size: 1.7vw !important;
  }
  .layout2.height3 .readingcard-time {
    padding-top: 15px;
    padding-bottom: 10px;
  }
  .layout2.height3 .readingcard-data {
    padding-bottom: 10px;
    padding-top: 15px;
  }
  .layout3 .readingcard-header {
    font-size: 0.8vw;
  }
  .layout3 .readingcard-header {
    bottom: 40px;
  }
}
.layout4 .readingcard-header {
  font-weight: 500 !important;
}
.dark-chart-tooltip {
  position: absolute;
  padding: 10px !important;
  background-color: #4a4444 !important;
  color: #fff !important;
  font-size: 12px !important;
}
.dark-chart-tooltip .axis-label {
  font-weight: 400;
  padding-right: 8px;
  color: #fff !important;
  font-size: 10px !important;
  letter-spacing: 0.5px !important;
}
.readingcard-btn-set.row .s-cancel,
.readingcard-btn-set.row .s-set {
  width: 50%;
  margin: 0;
  border-radius: 0;
  padding-top: 15px;
  padding-bottom: 15px;
}
.readingcard-btn-set.row .s-cancel {
  border-right: 1px;
  border-left: 0px;
  border-bottom: 0px;
  margin-bottom: -1px;
  margin-left: 0;
}
.readingcard-btn-set.row .s-set {
  border-right: 0px;
  border-bottom: 0px;
  background: #38b3c1;
  color: #fff;
  margin-right: -1px;
  margin-bottom: -1px;
  border-top: 0;
}
.readingcard-setcard-popup {
  padding: 0;
  border: 0;
}

.reading-setcard-body {
  padding: 10px;
}
.reading-setcard-body .el-input__inner {
  border: 1px solid #d8dce5;
  height: 40px;
  margin-top: 21px;
  padding: 10px;
}
.readingset-card-header {
  text-transform: uppercase;
  font-weight: 500;
  font-size: 13px;
}
.readingcard-time.reading-set-button {
  background: white;
  border: 1px solid #cacacd;
  padding-left: 10px;
  padding-right: 10px;
  font-size: 9px;
  margin-top: 10px;
  padding-bottom: 3px;
  padding-top: 3px !important;
  border-radius: 3px;
  cursor: pointer;
  z-index: 10;
}
.mobile-set-dialog .el-dialog__header {
  text-align: left;
}
.mobile-set-dialog .reading-setcard-body {
  padding-left: 0;
}
.mobile-set-dialog .el-dialog__footer {
  padding: 0;
}
.mobile-set-dialog button.el-button.el-button--default {
  width: 50%;
  border-radius: 0;
  padding-top: 15px;
  padding-bottom: 15px;
}
.mobile-set-dialog button.el-button.el-button--primary {
  width: 50%;
  border-radius: 0;
  margin: 0px;
  padding-top: 15px;
  padding-bottom: 15px;
}
.mobile-set-dialog span.dialog-footer {
  width: 100%;
  display: inline-flex;
}
.mobile-set-dialog input.el-input__inner {
  border-radius: 0px;
}
.mobile-set-dialog .el-dialog__body {
  padding-top: 0;
}
.readingcard .mobile-set-switch .el-switch {
  position: relative;
  padding-left: 10px;
  padding-right: 10px;
  top: 0px;
  left: 0px;
}
.mobile-set-dialog .el-dialog__header .el-dialog__title {
  font-size: 15px;
  text-transform: uppercase;
}
.no-value {
  font-size: 0.7em;
  opacity: 0.5;
}
</style>
