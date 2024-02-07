<template>
  <div class="mT10 p20">
    <div class="fc-id11 text-uppercase">
      {{ $t('maintenance._workorder.total_cost') }}
    </div>
    <div class="fc-black-17 pT5">
      <span
        v-if="$currency === '$' || $currency === '₹'"
        v-html="$currency"
        class="f15"
      ></span>
      {{ workorder.totalCost > -1 ? $d3.format(',')(workorder.totalCost) : 0 }}
      <span
        class="f15"
        v-if="$currency !== '$' && $currency !== '₹'"
        v-html="$currency"
      ></span>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'

export default {
  name: 'TotalCost',
  props: ['moduleName', 'details'],

  data() {
    return {}
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Total Cost'
    },
    workorder() {
      return this.details.workorder
    },
  },
  methods: {},
}
</script>
