<template>
  <div v-if="loading">
    <spinner :show="true"></spinner>
  </div>
  <div
    v-else-if="energyConsumption === null"
    class="d-flex justify-content-center flex-direction-column align-center"
  >
    <div>
      <inline-svg
        src="svgs/cardNodata"
        class="vertical-middle'"
        iconClass="icon icon-80"
      ></inline-svg>
    </div>
    <div class="fc-black3-16 self-center bold">
      {{ $t('space.sites.no_energy_meter') }}
    </div>
  </div>
  <div
    v-else-if="energyConsumption < 0"
    class="d-flex justify-content-center flex-direction-column align-center"
  >
    <div>
      <inline-svg
        src="svgs/cardNodata"
        class="vertical-middle'"
        iconClass="icon icon-80"
      ></inline-svg>
    </div>
    <div class="fc-black3-16 self-center bold">
      No Energy Data Available
    </div>
  </div>
  <div v-else class="p30 text-center">
    <div class="fc-black-12 text-uppercase bold">
      {{ $t('space.sites.energy_consumption') }}
    </div>
    <div class="fc-black-com f24 pT20">
      {{ `${energyConsumption} ${unit}` }}
    </div>
    <div class="fc-grey2 text-uppercase f11 bold mT5">
      {{ $t('space.sites.this_month') }}
    </div>
  </div>
</template>
<script>
import { isNull } from '@facilio/utils/validation'
export default {
  props: ['details', 'moduleName'],
  data() {
    return {
      energyConsumption: null,
      loading: false,
      unit: '',
    }
  },
  computed: {
    recordId() {
      return (this.details || {}).id || -1
    },
  },
  created() {
    this.getEnergyData()
  },
  methods: {
    getEnergyData() {
      let params = {
        staticKey: 'energyDataCard',
        paramsJson: {
          dateOperator: 28,
          baseSpaceId: parseInt(this.recordId),
          dateValue: null,
          moduleName: 'energyData',
          fieldName: 'totalEnergyConsumptionDelta',
        },
      }
      this.loading = true
      this.$http
        .post('dashboard/getCardData', params)
        .then(({ data: { cardResult = {} }, status }) => {
          if (status === 200) {
            let { result } = cardResult
            let { unit } = cardResult || {}
            let { symbol } = unit || {}
            if (!isNull(result)) {
              result = parseInt(result)
              if (symbol === 'kWh') {
                if (result > 10000) {
                  result = result / 1000
                  symbol = 'MWh'
                }
              }
              this.$set(this, 'energyConsumption', result)
              this.$set(this, 'unit', symbol)
            }
            this.loading = false
          }
        })
        .catch(({ message = 'Error Occurred while fetching energy data' }) => {
          this.$message.error(message)
          this.loading = false
        })
    },
  },
}
</script>
