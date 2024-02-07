<template>
  <el-dialog
    :visible="true"
    class="fc-dialog-header-hide overflow-y cardBuilder-dialog"
    custom-class="card-builder-popup scale-up-center"
    :width="popupWidth"
  >
    <div v-if="loading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <CardTypes
      v-else-if="showCardTypes"
      :cardTypes="cardTypes"
      :onSelect="onCardSelect"
      :onClose="onClose"
      ref="cardTypePicker"
    ></CardTypes>
    <component
      v-else-if="componentName"
      :is="componentName"
      :isNew="!isEdit"
      :cardMeta="selectedCard"
      :savedCardData="cardData && cardData.dataOptions"
      :cardType="selectedCard.cardType"
      :onCardSave="onSave"
      :onCardUpdate="onUpdate"
      :onGoBack="data => onGoBack(selectedCard.cardType, data)"
      :onClose="onClose"
      :isDuplicate="isDuplicate"
      :onCardDuplicate="onDuplicate"
    ></component>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import CardTypes from './CardTypes'
import CardLayoutMixin from './CardLayoutMixin'

export default {
  props: ['cardData', 'isDuplicate'],
  mixins: [CardLayoutMixin],
  components: {
    CardTypes,
  },
  data() {
    return {
      loading: false,
      showCardTypes: true,
      showCardConfig: true,
      selectedCard: null,
      isEdit: false,
    }
  },
  created() {
    if (!isEmpty(this.cardData)) this.init()
  },
  computed: {
    componentName() {
      if (this.showCardTypes || isEmpty(this.selectedCard)) return
      return this.selectedCard.component
    },
    popupWidth() {
      if (this.showCardTypes || isEmpty(this.selectedCard)) return '64%'
      else if (this.selectedCard.styles && this.selectedCard.styles.popupWidth)
        return this.selectedCard.styles.popupWidth
      else return '70%'
    },
  },
  methods: {
    init() {
      let { cardData } = this
      if (isEmpty(cardData.dataOptions)) return
      else {
        let {
          dataOptions: { cardLayout },
        } = cardData

        let layouts = this.cardTypes
          .map(type =>
            type.layouts.map(layout => ({ ...layout, parentType: type.type }))
          )
          .flat()

        let selectedCard = layouts.find(
          layout => layout.component === cardLayout
        )

        if (isEmpty(selectedCard)) return

        this.isEdit = true
        this.onCardSelect(selectedCard.parentType, selectedCard)
      }
    },
    onCardSelect(cardType, layoutObj) {
      this.showCardTypes = false

      this.selectedCard = {
        cardType,
        ...layoutObj,
      }
    },
    onGoBack(selectedCardType = null) {
      this.selectedCard = null
      this.showCardTypes = true

      if (this.isEdit) this.onClose()

      let hasMultiLayout =
        (
          (this.cardTypes.find(c => c.type === selectedCardType) || {})
            .layouts || []
        ).length > 1

      if (selectedCardType && hasMultiLayout) {
        this.$nextTick(() => {
          let cardTypePicker = this.$refs['cardTypePicker']
          cardTypePicker && cardTypePicker.openLayoutView(selectedCardType)
        })
      }
    },
    onSave(data, layout) {
      this.$emit('save', data, layout)
    },
    onClose() {
      this.isEdit = false
      this.$emit('close')
    },
    onDuplicate({ cardContext: duplicateCardContext }) {
      this.$emit('duplicate', duplicateCardContext)
    },

    onUpdate({ cardContext: updatedCardContext }) {
      this.isEdit = false
      this.$emit('update', updatedCardContext)
    },
  },
}
</script>
<style lang="scss">
.cardBuilder-dialog {
  .el-dialog {
    margin-top: 3vh !important;
    .container {
      height: 100%;
      // padding-bottom: 100px;
    }
  }
}
</style>
