<template>
  <div v-if="hasImpact" class="p30 d-flex flex-direction-column">
    <div v-if="isLoading" class="mT25">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else>
      <div
        class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
      >
        {{ title }}
      </div>
      <div class="d-flex pT20">
        <div class="items-baseline width50 pR8">
          <div class="flex-middle">
            <div class="f18 bold">{{ thisMonthImpact }}</div>
            <fc-icon
              group="navigation"
              name="up"
              :class="`${getTrendClasses()} mL5`"
            ></fc-icon>
          </div>
          <div class="secondary-text pT5">
            {{ $t('common.date_picker.this_month') }}
          </div>
        </div>
        <div class="items-baseline width50 pL15 border-left2">
          <div class="flex-middle">
            <div class="f18 bold">{{ lastMonthImpact }}</div>
          </div>
          <div class="letter-spacing0_5 fc-blue-label f12 text-capitalize pT5">
            {{ $t('common.date_picker.last_month') }}
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="flex-center-vH">
    <div class="text-center flex-center-vH align-center">
      <InlineSvg
        src="svgs/emptystate/alarmEmpty"
        iconClass="icon text-center icon-60 emptystate-icon-size"
      ></InlineSvg>
      <div class="pT5 fc-black-dark f15 fw4">
        {{ $t('common.products.no_impact') }}!
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
export default {
  components: {
    Spinner,
  },
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
    'isCostImpact',
  ],
  data() {
    return {
      thisMonthImpact: 0,
      lastMonthImpact: 0,
      isLoading: false,
    }
  },
  created() {
    this.loadRecords()
  },
  computed: {
    title() {
      let { isCostImpact } = this
      return isCostImpact ? 'Cost Impact' : 'Energy Impact (KWh)'
    },
    hasImpact() {
      let { details } = this
      let { alarm } = details || {}
      let { rule } = alarm || {}
      return rule.hasOwnProperty('impact')
    },
  },
  methods: {
    getTrendClasses() {
      let { thisMonthImpact, lastMonthImpact } = this
      if (thisMonthImpact === 0 && lastMonthImpact === 0) return 'hide'
      return parseFloat(thisMonthImpact) > parseFloat(lastMonthImpact)
        ? 'fill-green'
        : 'fill-red rotate-bottom'
    },
    async loadRecords() {
      let { details, isCostImpact } = this
      let { alarm } = details || {}
      let { rule, resource } = alarm || {}
      let { id: ruleId } = rule || {}
      let { id: parentId } = resource || {}
      let params = {
        ruleId,
        parentId,
        isCostImpact,
      }
      this.isLoading = true
      let { data, error } = await API.get('/v3/readingrule/fetchImpact', params)
      this.isLoading = false
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let { result } = data || {}
        let { lastMonth, thisMonth } = result
        this.$setProperty(this, 'thisMonthImpact', thisMonth)
        this.$setProperty(this, 'lastMonthImpact', lastMonth)
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.fill-green {
  fill: #2cb988;
}

.fill-red {
  fill: #e87171;
}

.rotate-bottom {
  transform: rotate(180deg);
}
</style>
