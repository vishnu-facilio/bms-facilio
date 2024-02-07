/* eslint-disable vue/valid-template-root */
<template>
  <div v-if="isRichText" class="richtext-area">
    <RichText
      v-model="content"
      :readOnly="disabled"
      :placeholder="placeholder"
      :hideImgTool="hideImgTool"
      :hideUnorderedList="hideUnorderedList"
      :hideOrderedList="hideOrderedList"
      :secondaryToolbar="secondaryToolbar"
      :istoolbarPositionBottom="istoolbarPositionBottom"
      :isMoreOption="isMoreOption"
      :customToolOrder="customToolOrder"
      :iconColor="iconColor"
      :style="{ minHeight, maxHeight, overflow }"
    >
      <template v-if="canShowModeToggle" #editorMode>
        <div
          class="self-center marginL-auto mR15 cursor-pointer mode-btn"
          @click="changeMode()"
        >
          {{ $t('forms.builder.plain_text_mode') }}
        </div>
      </template>
    </RichText>
  </div>
  <div v-else class="richtext-area plain-text-mode">
    <div class="richtext-editor">
      <div class="richtext-toolbar">
        <div
          class="toolbar-button marginL-auto cursor-pointer mode-btn"
          @click="changeMode()"
        >
          {{ $t('forms.builder.switch_to_richtext') }}
        </div>
      </div>
      <el-input
        type="textarea"
        resize="none"
        v-model="content"
        :placeholder="placeholder"
        class="richtext-content"
        :key="`plain-text`"
      ></el-input>
    </div>
  </div>
</template>
<script>
import { RichText } from '@facilio/ui/forms'
import { htmlToText } from '@facilio/utils/filters'

export default {
  props: {
    value: {
      type: String,
    },
    field: {
      type: Object,
    },
    rows: {
      type: Number,
      default: 10,
    },
    maxRows: {
      type: Number,
      default: 12,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
      default: 'Enter text here',
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
    hideImgTool: {
      type: Boolean,
      default: false,
    },
    hideOrderedList: {
      type: Boolean,
      default: false,
    },
    hideUnorderedList: {
      type: Boolean,
      default: false,
    },
    isRichTextMode: {
      type: Boolean,
      default: true,
    },
    canShowModeToggle: {
      type: Boolean,
      default: false,
    },
    istoolbarPositionBottom: {
      type: Boolean,
    },
    customToolOrder: {
      type: Array,
    },
    isMoreOption: {
      type: Boolean,
    },
    secondaryToolbar: {
      type: Boolean,
    },
    iconColor: {
      type: String,
    },
  },
  components: { RichText },
  computed: {
    content: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      },
    },
    isRichText: {
      get() {
        return this.isRichTextMode
      },
      set(value) {
        this.$emit('update:isRichTextMode', value)
      },
    },
    // Assuming a <p></p> tag inside editor
    // has fonts-size 1rem and margin-bottom 1rem
    minHeight() {
      return `${this.rows * 2}rem`
    },
    maxHeight() {
      return `${this.maxRows * 2}rem`
    },
    overflow() {
      return 'hidden'
    },
  },
  methods: {
    changeMode() {
      let { isRichText, value } = this
      if (isRichText) {
        this.showConfirmModeSwitch()
      } else {
        let htmlContent = `<p>${value}</p>`
        this.content = htmlContent
        this.proceedChangingModes()
      }
    },
    showConfirmModeSwitch() {
      let dialogObj = {
        htmlMessage: `${this.$t('forms.builder.switch_alert')}`,
        rbDanger: true,
        rbLabel: 'Confirm',
      }
      this.$dialog.confirm(dialogObj).then(canProceed => {
        if (canProceed) {
          let { value } = this
          let textContent = htmlToText(value)
          this.content = textContent
          this.proceedChangingModes()
        }
      })
    },
    proceedChangingModes() {
      let { isRichText } = this
      this.isRichText = !isRichText
    },
  },
}
</script>
<style lang="scss">
.richtext-area {
  position: relative;
  &.plain-text-mode {
    .richtext-editor {
      height: 16em;
      .richtext-content {
        &.el-textarea {
          .el-textarea__inner {
            height: 100%;
            border: 0px;
          }
        }
      }
    }
  }
  // make toolbar sticky
  .richtext-toolbar {
    position: sticky;
    top: 0;
    z-index: 1;
  }
  // make content area scrollablle
  .richtext-editor {
    position: relative;
    overflow: scroll;
    display: flex;
    flex-direction: column;
  }
  .richtext-content {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    & > div {
      flex-grow: 1;
    }
  }
}
</style>
