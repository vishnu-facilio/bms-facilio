<template>
  <shimmer-loading v-if="loading" class="card-shimmer"> </shimmer-loading>
  <div class="dragabale-card h100 energy-mini" v-else>
    <div class="h100">
      <div class="h100">
        <div
          class="db-container energy_background energy-card2 h100 energy-card"
        >
          <div class="energy-card-H">{{ $t('panel.layout.e_cost') }}</div>
          <div class="thismonth-section">
            <center v-if="result">
              <div class="energy-card-readigdata">
                <span
                  class="meter-formula-txt pR5 card-min-unit"
                  v-if="$currency.includes('$')"
                  >{{ $currency }}</span
                >
                <span
                  class="selfcenter align readings-result-txt"
                  :style="getFontcolor(result.result.currentVal + '')"
                  >{{
                    result.result && result.result.currentVal
                      ? formatValue(result.result.currentVal)
                      : '---'
                  }}</span
                >
                <span
                  class="meter-formula-txt pL5 card-min-unit"
                  v-if="!$currency.includes('$')"
                  >{{ $currency }}</span
                >
              </div>
            </center>
            <center
              class="thismonth fwBold uppercase"
              style="padding-top: 10px;"
            >
              {{ getPeriod() }}
            </center>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
import shimmerLoading from '@/ShimmerLoading'
import * as d3 from 'd3'
export default {
  props: ['widget', 'config'],
  mixins: [DateHelper],
  data() {
    return {
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
      data: {
        style: {
          color: 'red',
          bgcolor: 'pink',
        },
        unitString: '',
        workflowV2String: '',
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
    shimmerLoading,
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
    getStyle() {
      return 'background:' + this.style.bgcolor + ';color:' + this.style.color
    },
  },
  methods: {
    formatValue(value) {
      return d3.format(',')(value)
    },
    getPeriod() {
      if (
        this.getdateOperators().find(rt => rt.value === this.data.operatorId) &&
        this.getdateOperators().find(rt => rt.value === this.data.operatorId)
          .label
      ) {
        return this.getdateOperators().find(
          rt => rt.value === this.data.operatorId
        ).label
      }
      return ''
    },
    refresh() {
      this.updateCard()
    },
    loadCardData() {
      let self = this
      this.getParams()
      let params = null
      if (this.widget && this.widget.id > -1) {
        params = {
          widgetId: self.widget.id,
        }
      } else {
        params = {
          workflow: {
            isV2Script: true,
            workflowV2String: this.data.workflowV2String,
          },
          staticKey: 'kpiCard',
        }
      }

      this.loading = true
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
    updateCard() {
      let self = this
      let params = null
      self.getParams()
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
    getData(data) {
      if (data.hasOwnProperty('result')) {
        this.result = data
      }
    },
    getFontcolor(title) {
      if (title) {
        let font = 2
        if (title.length < 7) {
          font = 2.4
        } else if (title.length < 8) {
          font = 2.2
        } else if (title.length < 12) {
          font = 2
        } else if (title.length > 12) {
          font = 1.8
        }
        return 'font-size:' + font + 'rem'
      }
    },
  },
}
</script>
<style>
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
.kpi-container:hover .color-choose-icon {
  display: block !important;
}
</style>
