<script>
import BaseCard from 'pages/card-builder/cards/common/BaseCard'
import cardLoading from 'pages/card-builder/components/CardLoading'
export default {
  extends: BaseCard,
  components: { cardLoading },
  computed: {
    unit() {
      let { cardData } = this
      if (cardData && cardData.value && cardData.value.unit) {
        return cardData.value.unit
      } else if (
        cardData.value &&
        cardData.value.kpi &&
        cardData.value.kpi.metric &&
        cardData.value.kpi.metric.unit
      ) {
        return cardData.value.kpi.metric.unit
      }
      return ''
    },
    localUnit() {
      let { cardStyle } = this
      if (cardStyle.unitConfig && cardStyle.unitConfig.unit) {
        return cardStyle.unitConfig
      } else if (cardStyle.unitConfig && cardStyle.unitConfig.unit !== '') {
        return cardStyle.unitConfig
      }
      return null
    },
  },
  mounted() {
    if (this.cardStyle && !this.cardStyle.unitConfig) {
      let data = {
        unit: '',
        position: 2,
      }
      this.$set(this.cardStyle, 'unitConfig', data)
    }
  },
}
</script>
