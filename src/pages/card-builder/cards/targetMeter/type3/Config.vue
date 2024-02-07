<script>
import Config from '../type1/Config'
import { isEmpty } from '@facilio/utils/validation'
import Card from './Card'
import FAddDataPoint from 'pages/energy/analytics/components/FAddDataPoint'

export default {
  extends: Config,
  components: { Card, FAddDataPoint },
  data() {
    return {
      cardLayout: 'gauge_layout_3',
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
            title: 'Gauge Card 3',
            value: { unit: 'KwH', value: 35 },
          }
    },
  },
  methods: {
    serializeState() {
      let { styles, ...props } = this.cardStateObj
      return {
        ...props,
        styles: {
          tickColor: styles.tickColor.hex,
          colors: styles.colors.map(color => color.hex),
          backgroundColors: styles.backgroundColors.map(color => color.hex),
        },
      }
    },
    serializeConditionalState(state) {
      let { styles } = state
      let { ...props } = this.cardStateObj
      let style = {
        colors: styles.colors.map(color => {
          if (color.hex) {
            return color.hex
          } else {
            return color
          }
        }),
        backgroundColors: styles.backgroundColors.map(color => {
          if (color.hex) {
            return color.hex
          } else {
            return color
          }
        }),
        tickColor:
          styles.tickColor && styles.tickColor.hex
            ? styles.tickColor.hex
            : '#000000',
      }
      return {
        ...props,
        styles: {
          ...styles,
          ...style,
        },
      }
    },
    deserializeState(cardState) {
      this.cardStateObj = {
        ...this.cardStateObj,
        styles: {
          tickColor: {
            id: 0,
            hex:
              cardState.styles.tickColor && cardState.styles.tickColor.hex
                ? cardState.styles.tickColor.hex
                : '#969aa2',
          },
          colors: cardState.styles.colors.map((hex, index) => ({
            id: index,
            hex: hex,
          })),
          backgroundColors: cardState.styles.backgroundColors.map(
            (hex, index) => ({
              id: index,
              hex: hex,
            })
          ),
        },
      }
    },
  },
}
</script>
