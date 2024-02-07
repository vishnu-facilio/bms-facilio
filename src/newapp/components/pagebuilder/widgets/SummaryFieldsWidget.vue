<script>
import SummaryFieldsWidget from 'src/components/page/widget/common/field-details/SummaryFieldsWidget.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: SummaryFieldsWidget,
  methods: {
    async fetchDetails() {
      let detailsLayout = null

      if (isEmpty(this.detailsLayoutProp)) {
        detailsLayout = {
          summaryFieldWidget: {
            groups: this.$getProperty(this.widget, 'widgetDetail.groups') || [],
          },
        }
      } else {
        detailsLayout = this.detailsLayoutProp
      }

      await this.serializeDetailsLayout(detailsLayout)
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['content-container']
        if (!container) return

        let height = this.$refs['content-container'].scrollHeight
        let width = this.$refs['content-container'].scrollWidth

        let dimensions = this.calculateDimensions({ height, width })

        if (isEmpty(dimensions)) return
        let { h } = dimensions || {}
        let params = {}
        if (h <= this.defaultWidgetHeight) {
          this.needsShowMore = false
          params = { height: height + 20, width }
        } else {
          let { defaultWidgetHeight = 24 } = this
          this.needsShowMore = h > defaultWidgetHeight ? true : false
          this.defaultWidgetHeight = defaultWidgetHeight
          this.needsShowMore
            ? (params = { h: defaultWidgetHeight })
            : (params = { h: h })
        }
        this.resizeWidget(params)
      })
    },
  },
}
</script>
<style lang=""></style>
