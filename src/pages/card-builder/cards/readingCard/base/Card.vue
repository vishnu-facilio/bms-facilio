<script>
import BaseCard from 'pages/card-builder/cards/common/BaseCard'
import { isEmpty, isNumber } from '@facilio/utils/validation'
import cardLoading from 'pages/card-builder/components/CardLoading'
export default {
  extends: BaseCard,
  components: { cardLoading },
  computed: {
    subscriptionParams() {
      let { cardParams, formatReadingsForPubSub: format } = this

      if (isEmpty(cardParams)) return []
      return { readings: format([cardParams.reading]) }
    },
    variables() {
      let variables =
        this.result && this.result.data && this.result.data.variables
          ? this.result.data.variables
          : null
      if (variables) {
        return variables
      }
      return []
    },
    cardDataValue() {
      let {
        cardData: { value },
      } = this
      let decimalPlace =
        this.cardStyle && this.cardStyle.hasOwnProperty('decimalPlace')
          ? this.cardStyle.decimalPlace
          : -1
      let hasValue = value && isNumber(value.value)
      if (value.dataType && value.dataType === 'STRING') {
        return value.value
      } else if (hasValue) {
        if (decimalPlace > 0) {
          return this.formatDecimal(value.value, decimalPlace)
        } else if (decimalPlace > -1) {
          return this.formatDecimal(value.value, 0)
        } else {
          return this.formatDecimal(value.value)
        }
      } else if (value.value != null && value.dataType === 'BOOLEAN') {
        return value.value
      } else return '--'
    },
    unit() {
      let { cardData } = this
      if (cardData && cardData.value && cardData.value.unit) {
        return cardData.value.unit
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
}
</script>
