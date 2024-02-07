<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      selectedCardData: null,
      selectedWidgetConfig: null,
      isDuplicate: false,
    }
  },
  methods: {
    openNewCardBuilder() {
      this.showCardBuilderSetup = true
    },

    onCardSave(data, layout = null) {
      this.addCardToDashboard(data, layout)

      this.isDraging = false
      this.showCardBuilderSetup = false
      this.isDuplicate = false
    },
    onCardDuplicate(data, layout = null) {
      this.duplicateCardToDashboard(data, layout)

      this.isDraging = false
      this.showCardBuilderSetup = false
      this.isDuplicate = false
    },

    onCardUpdate(data) {
      let { selectedCardData } = this
      this.updateCardInDashboard(selectedCardData.id, data.cardContext)
      this.onBuilderClosed()
      this.isDuplicate = false
    },

    onBuilderClosed() {
      this.selectedCardData = null
      this.cancelBuilding()
    },

    deleteCard(data) {
      this.isDuplicate = false
      this.removeChartList.push(data)

      let index = this.dashboardLayout.findIndex(
        r => r.widget.id === data.widgetId
      )
      if (!isEmpty(index)) {
        this.dashboardLayout.splice(index, 1)
      }
    },

    editCard(widget, widgetConfig) {
      this.selectedCardData = widget
      this.selectedWidgetConfig = widgetConfig
      this.showCardBuilderSetup = true
      this.isDuplicate = false
    },
    duplicate(widget, widgetConfig) {
      this.isDuplicate = false
      this.selectedCardData = widget
      this.selectedWidgetConfig = widgetConfig
      this.showCardBuilderSetup = true
      this.isDuplicate = true
    },

    duplicateCardToDashboard(data, layoutParams) {
      this.dashboardlength = this.dashboardlength + 1
      let layoutProp = this.selectedWidgetConfig
      let layout = {
        i: this.dashboardlength + '',
        x: layoutProp.x,
        y: layoutProp.y + layoutProp.h,
        w: layoutProp.w,
        h: layoutProp.h,
        minW: layoutProp.minW,
        minH: layoutProp.minH,
        widget: {
          dataOptions: data.cardContext,
          type: 'card',
          id: -1,
        },
      }
      this.dashboardLayout.push(layout)
    },
    addCardToDashboard(data, layoutParams) {
      this.dashboardlength = this.dashboardlength + 1

      if (isEmpty(layoutParams)) {
        layoutParams = {
          w: 24,
          h: 12,
        }
      }

      let layout = {
        i: this.dashboardlength + '',
        x: 0,
        y: 0,
        w: layoutParams.w,
        h: layoutParams.h,
        minW: layoutParams.w,
        minH: layoutParams.h,
        widget: {
          dataOptions: data.cardContext,
          type: 'card',
          id: -1,
        },
      }
      this.dashboardLayout.push(layout)
    },

    updateCardInDashboard(id, cardContext) {
      let selectedCard = null
      if (id < 0 && !isEmpty(this.selectedWidgetConfig)) {
        selectedCard = this.dashboardLayout.find(
          gridItem => gridItem.i === this.selectedWidgetConfig.i
        )
      } else {
        selectedCard = this.dashboardLayout.find(
          gridItem => gridItem.widget.id === id
        )
      }

      if (!isEmpty(selectedCard)) {
        let {
          widget: { dataOptions },
        } = selectedCard

        this.$set(selectedCard.widget, 'dataOptions', {
          ...dataOptions,
          ...cardContext,
          hasEdited: true,
        })
      }
    },

    canResizeCard(cardContext) {
      if (!cardContext) return false

      let { cardState } = cardContext

      if (!isEmpty(cardState) && cardState.hasOwnProperty('canResize')) {
        return cardState.canResize
      } else {
        return false
      }
    },
  },
}
</script>
