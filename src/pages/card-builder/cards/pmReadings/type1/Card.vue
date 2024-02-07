<template>
  <div
    class="dragabale-card height100 "
    v-bind:class="{ 'vue-grid-item': $mobile }"
  >
    <div class="d-flex flex-direction-column justify-center" v-if="cardloading">
      <spinner :show="true"></spinner>
    </div>
    <div v-else class="linear-gauge dragabale-card fc-widget height100">
      <div class="height100">
        <!-- <div class="pm-reading-table-overlay"></div> -->
        <f-pm-readingtable
          class="height100"
          :pmObject="pmObject"
          :pm="pm"
          :workorder="workorder"
          :config="data.config"
          :settings="settings"
        ></f-pm-readingtable>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import FPmReadingtable from '@/PmReadingsable'
import Card from '../base/Card'
import { dateOperators } from 'pages/card-builder/card-constants'
export default {
  extends: Card,
  props: ['widget', 'config', 'loading', 'cardDataObj'],
  data() {
    return {
      cardloading: false,
      result: null,
      settings: {},
      data: {
        config: {
          hidedatefilter: true,
        },
      },
      pm: null,
      workorder: null,
      pmObject: null,
      dateOperators,
    }
  },
  components: {
    FPmReadingtable,
  },
  mounted() {
    this.getCardData()
  },
  watch: {
    cardData: {
      deep: true,
      handler() {
        this.getCardData()
      },
    },
  },
  computed: {
    pmId() {
      if (this.cardParams && this.cardParams.pmId) {
        return this.cardParams.pmId
      } else if (this.cardDataObj && this.cardDataObj.pmId) {
        return this.cardDataObj.pmId
      }
      return null
    },
  },
  methods: {
    getClientProps() {
      this.$nextTick(() => {
        let height = 100
        if (this.$el) {
          height = this.$el.clientHeight - 67
        } else {
          height = 200
        }
        this.$set(this.settings, 'height', height)
      })
    },
    getCardData() {
      this.cardloading = true
      let operatorId = 22
      let dateValueString = null
      if (this.cardDataObj) {
        operatorId = this.dateOperators.find(
          rt => rt.value === this.cardDataObj.dateRange
        ).enumValue
      } else if (this.cardParams) {
        if (this.cardParams.cardFilters) {
          let cardFilters = this.cardParams.cardFilters
          operatorId = cardFilters.operatorId
          dateValueString = cardFilters.dateValueString
        } else {
          operatorId = this.dateOperators.find(
            rt => rt.value === this.cardParams.dateRange
          ).enumValue
        }
      }
      this.$set(this.data.config, 'operatorId', operatorId)
      this.$set(this.data.config, 'dateValueString', dateValueString)
      this.getClientProps()
      this.loadpm()
    },
    loadpm() {
      if (this.pmId) {
        this.$http
          .get(
            '/workorder/preventiveMaintenanceSummary?id=' + parseInt(this.pmId)
          )
          .then(response => {
            this.pm = response.data.preventivemaintenance
            this.workorder = response.data.workorder
            this.pmObject = response.data
            this.cardloading = false
          })
      }
    },
  },
}
</script>
<style lang="scss" scoped>
@media print {
  .linear-gauge {
    width: 100%;
  }
}
</style>
