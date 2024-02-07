<script>
import Config from '../type2/Config'
import { isEmpty } from '@facilio/utils/validation'
import Card from '../type3/Card'
import FAddDataPoint from 'pages/energy/analytics/components/FAddDataPoint'

export default {
  extends: Config,
  components: { Card, FAddDataPoint },
  data() {
    return {
      cardLayout: 'gauge_layout_4',
    }
  },
  computed: {
    previewData() {
      let { result } = this
      let { data } = result || {}

      return !isEmpty(this.result)
        ? data
        : {
            period: 'Last Month',
            maxValue: { value: 100 },
            title: 'Gauge Card 4',
            value: { unit: 'KwH', value: 75 },
          }
    },
  },
  methods: {
    serializeState() {
      let { styles, ...props } = this.cardStateObj
      let color = styles.color.hex

      return {
        ...props,
        styles: {
          tickColor: styles.tickColor.hex,
          colors: [color, color, color, color],
          backgroundColors: [color, color],
        },
      }
    },
    deserializeState(cardState) {
      this.cardStateObj = {
        ...this.cardStateObj,
        styles: {
          tickColor: { id: 0, hex: cardState.styles.tickColor },
          color: { id: 0, hex: cardState.styles.colors[0] },
        },
      }
    },
  },
}
</script>
