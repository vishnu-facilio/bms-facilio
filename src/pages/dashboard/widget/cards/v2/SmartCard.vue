<template>
  <div class="dragabale-card readingcard height100">
    <shimmer-loading v-if="loading" class="map-shimmer"> </shimmer-loading>
    <div v-else class="height100 smart-table-wrapper">
      <el-popover
        placement="right"
        width="300"
        popper-class="reading-color-picker"
        trigger="click"
        v-if="mode"
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
                @click="
                  data.style.bgColor = color
                  setParams()
                "
                v-bind:class="{ active: data.style.bgColor === color }"
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
                @click="
                  data.style.color = color
                  setParams()
                "
                v-bind:class="{ active: data.style.color === color }"
              ></div>
            </div>
          </div>
        </div>
        <div slot="reference" class="color-choose-icon">
          <i class="fa fa-font reading-card-color-picker" v-if="mode"></i>
        </div>
      </el-popover>
      <div class="sm-card-body kpi-container" v-if="result" :style="getStyle">
        <div class="sm-card-label kpi-sections">{{ result.label }}</div>
        <div class="sm-card-value kpi-data kpi-count pointer">
          {{ result.value
          }}<span class="f18" v-if="result.unit">{{ result.unit }}</span>
        </div>
        <div
          class="sm-card-sublabel kpi-label kpi-period"
          v-if="result.percentage"
        >
          {{ result.percentage }}%
        </div>
        <div
          class="sm-card-sublabel kpi-label kpi-period"
          v-if="result.sublabel"
        >
          {{ result.sublabel }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import shimmerLoading from '@/ShimmerLoading'
import colors from 'charts/helpers/colors'
export default {
  props: ['widget', 'config'],
  mixins: [colors],
  data() {
    return {
      predefineColors: colors.readingcardColors,
      loading: true,
      result: null,
      data: null,
    }
  },
  mounted() {
    this.getParams()
    this.loadCardData()
  },
  components: {
    shimmerLoading,
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
    getStyle() {
      if (this.data.style) {
        let style = this.data.style
        return `background:${style.bgColor};color:${style.color}`
      }
      return `background:#fff;color:#000`
    },
  },
  methods: {
    refresh() {
      this.getParams()
      this.loadCardData()
    },
    perpareElTableResult(result) {
      if (result.data) {
        return result.data
      }
      return []
    },
    getData(result) {
      this.result = result.result
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
</style>
