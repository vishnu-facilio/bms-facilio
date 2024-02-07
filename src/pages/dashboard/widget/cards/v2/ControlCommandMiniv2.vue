<template>
  <shimmer-loading v-if="loading" class="card-shimmer"> </shimmer-loading>
  <div class="dragabale-card h200 energy-mini" v-else>
    <div class="h200">
      <div class="h2500" v-if="resource && resource.length && result.result">
        <div class="command-card">
          <div class="energy-card-H">{{ resource[0].name }}</div>
          <div class="f14 bold">{{ result.result.fieldObj.displayName }}</div>
          <div class="mT30 bold f18">
            <div
              v-html="
                $fieldUtils.getDisplayValue(
                  result.result.fieldObj,
                  result.result.currentVal
                )
              "
            ></div>
          </div>
          <el-button
            size="small"
            class="text-uppercase fc__border__btn mT35 mB0 pR30 pL30"
            style="max-width: 90px;"
            @click="
              showSetReadingDialog(
                result.result.fieldObj,
                result.result.currentVal
              )
            "
            >Set</el-button
          >
          <SetReadingPopup
            v-if="showSetDialog"
            :key="newReading.field.id"
            :reading="newReading"
            :saveAction="closePopup"
            :closeAction="closePopup"
            :recordId="resource[0].id"
            :recordName="resource[0].name"
          ></SetReadingPopup>
        </div>
      </div>
      <div v-else>
        no data
      </div>
    </div>
  </div>
</template>
<script>
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
import shimmerLoading from '@/ShimmerLoading'
import SetReadingPopup from '@/readings/SetReadingValue'

import * as d3 from 'd3'
export default {
  props: ['widget', 'config'],
  mixins: [DateHelper],
  data() {
    return {
      newReading: {
        value: null,
        ttime: null,
      },
      resource: null,
      showSetDialog: false,
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
    SetReadingPopup,
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
    showSetReadingDialog(field, value) {
      this.newReading.field = field
      this.newReading.value = value
      this.showSetDialog = true
    },
    getPeriod() {
      return this.getdateOperators().find(
        rt => rt.value === this.data.operatorId
      ).label
    },
    refresh() {
      this.updateCard()
    },
    closePopup() {
      this.updateCommadData()

      this.showSetDialog = false
    },
    updateCommadData() {
      // console.log(JSON.stringify(this.newReading))
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
          self.$util
            .loadResource([response.data.cardResult.result.parentId])
            .then(fields => {
              self.resource = fields
              self.loading = false
            })
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
          self.$util
            .loadResource([response.data.cardResult.result.parentId])
            .then(fields => {
              self.resource = fields
              self.loading = false
            })
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
