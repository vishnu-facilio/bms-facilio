<script>
import CommonConfig from 'pages/card-builder/cards/common/BaseConfig'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: CommonConfig,
  methods: {
    setReading(prop, reading) {
      if (reading) {
        let { cardDataObj } = this

        this.$set(this, 'cardDataObj', { ...cardDataObj, [prop]: reading })
        this.setConditionalVariables(reading)
      }
    },
    setTitleFromReading(reading) {
      let {
        cardDataObj,
        cardDataObj: { title: cardName },
      } = this
      cardDataObj.title = !isEmpty(cardName) ? cardName : reading.readingName
    },
  },
}
</script>
