<template>
  <shimmer-loading v-if="loading" class="card-shimmer"></shimmer-loading>
  <div class="dragabale-card mini-cards" v-else>
    <div class="fc-b-card pT5 mobile-building-card">
      <div class="row pT10 pB10" v-if="data.building">
        <div class="col-5 p10 text-center">
          <img class="fc-avatar profilemini-avatar" v-bind:src="url" />
        </div>
        <div class="col-7 self-center mobile-building-card-right">
          <div class="row pB10 en">
            <div class="fvw18 bold text-left mobile-building-name">
              {{ data.building.name }}
            </div>
          </div>
          <div
            class="fvw13 bold sb-secondary-color text-left mobile-buidling-year"
          >
            {{ getPeriod() }}
          </div>
          <div class="row building mobile-building-row-val" v-if="result">
            <span
              class="fvw20 bold en mobile-building-val"
              style="padding-top: 1px;"
              >{{
                result.hasOwnProperty('result') &&
                result.result.hasOwnProperty('currentVal')
                  ? formatValue(result.result.currentVal)
                  : '---'
              }}</span
            >
            <div
              class="fvw14 secondary-color mobile-current-val"
              style="padding-top: 5px;"
            >
              &nbsp;
              {{
                result.hasOwnProperty('result') &&
                result.result.hasOwnProperty('currentVal')
                  ? formatUnit(result.result.currentVal)
                  : ''
              }}
            </div>
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
export default {
  props: ['widget', 'config'],
  mixins: [DateHelper],
  data() {
    return {
      url: null,
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
  created() {
    this.getParams()
    this.loadAvatarUrl()
  },
  methods: {
    formatValue(value) {
      if (value < 999) {
        return value
      } else {
        return (value / 1000).toFixed(2)
      }
    },
    formatUnit(value) {
      if (value < 999) {
        return 'kWh'
      } else {
        return 'MWh'
      }
    },
    getPeriod() {
      return this.getdateOperators().find(
        rt => rt.value === this.data.operatorId
      ).label
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
    loadAvatarUrl() {
      let self = this
      if (this.data && this.data.building && this.data.building.id) {
        self.$http
          .get(
            '/photos/get?module=basespacephotos&parentId=' +
              this.data.building.id
          )
          .then(function(response) {
            self.url =
              response.data.photos && response.data.photos.length
                ? response.data.photos[0].url
                : []
          })
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
