<script>
import Config from '../type1/Config'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: Config,
  data() {
    return {
      cardLayout: 'gauge_layout_2',
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
            title: 'Gauge Card 2',
            value: { unit: 'KwH', value: 65 },
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
          colors: [color, color, color, color],
          backgroundColors: [color, color],
        },
      }
    },
    deserializeState(cardState) {
      this.cardStateObj = {
        ...this.cardStateObj,
        styles: {
          color: { id: 0, hex: cardState.styles.colors[0] },
        },
      }
    },
  },
}
</script>
