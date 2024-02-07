<script>
import BaseCard from 'pages/card-builder/cards/common/BaseCard'
import { isEmpty } from '@facilio/utils/validation'
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
  },
}
</script>
