<script>
import BaseCard from 'pages/card-builder/cards/common/BaseCard'
import { isEmpty } from '@facilio/utils/validation'
import cardLoading from 'pages/card-builder/components/CardLoading'

export default {
  extends: BaseCard,
  components: { cardLoading },
  created() {
    this.constructGaugeData()
  },
  computed: {
    subscriptionParams() {
      let { cardParams, formatReadingsForPubSub: format } = this

      if (isEmpty(cardParams)) return []
      return { readings: format([cardParams.reading]) }
    },
  },
  watch: {
    cardData: {
      handler: function() {
        this.constructGaugeData()
      },
      deep: true,
    },
    cardStyle: {
      handler: function() {
        this.constructGaugeData()
      },
      deep: true,
    },
  },
}
</script>
