<template>
  <div class="richtext-editor">
    <div class="richtext-toolbar">
      <el-dropdown trigger="click" @command="setFontSize">
        <div class="toolbar-dropdown-trigger">
          {{ (textSize || {}).label }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(option, optionKey) in fontOptions"
            :key="`option-${optionKey}`"
            class="toolbar-dropdown-option"
            :command="option"
          >
            {{ option.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <div class="toolbar-separator"></div>
      <div
        class="toolbar-button toolbar-button-bold "
        :class="{ 'is-active': isActive('bold') }"
        @click="() => formatContent('bold')"
      >
        <inline-svg src="richtext-editor/bold" class="toolbar-icon" />
      </div>
      <div
        class="toolbar-button toolbar-button-italic"
        :class="{ 'is-active': isActive('italic') }"
        @click="() => formatContent('italic')"
      >
        <inline-svg src="richtext-editor/italic" class="toolbar-icon" />
      </div>
      <div
        class="toolbar-button toolbar-button-underline"
        :class="{ 'is-active': isActive('underline') }"
        @click="() => formatContent('underline')"
      >
        <inline-svg src="richtext-editor/underline" class="toolbar-icon" />
      </div>
      <div
        class="toolbar-button toolbar-button-strikethrough"
        :class="{ 'is-active': isActive('strike') }"
        @click="() => formatContent('strike')"
      >
        <inline-svg src="richtext-editor/strikethrough" class="toolbar-icon" />
      </div>
      <div
        v-if="!hideUnorderedList"
        class="toolbar-button toolbar-button-list"
        :class="{ 'is-active': isActive('bullet-list') }"
        @click="() => formatContent('bullet-list')"
      >
        <inline-svg src="richtext-editor/unordered-list" class="toolbar-icon" />
      </div>
      <div
        v-if="!hideOrderedList"
        class="toolbar-button toolbar-button-list"
        :class="{ 'is-active': isActive('ordered-list') }"
        @click="() => formatContent('ordered-list')"
      >
        <inline-svg src="richtext-editor/ordered-list" class="toolbar-icon" />
      </div>
      <div
        class="toolbar-button toolbar-button-link"
        :class="{ 'is-active': isActive('link') }"
        @click="showLinkPrompt"
      >
        <inline-svg src="richtext-editor/link" class="toolbar-icon" />
      </div>
      <ColorPicker @currentColor="setHighlight">
        <template #reference>
          <div
            class="toolbar-button toolbar-button-link"
            :class="{ 'is-active': isActive('backgroundColor') }"
          >
            <inline-svg src="richtext-editor/highlight" class="toolbar-icon" />
          </div>
        </template>
      </ColorPicker>
      <ColorPicker @currentColor="setColor">
        <template #reference>
          <div
            class="toolbar-button toolbar-button-link"
            :class="{ 'is-active': isActive('color') }"
          >
            <inline-svg src="richtext-editor/text-color" class="toolbar-icon" />
          </div>
        </template>
      </ColorPicker>
    </div>
    <EditorContent :editor="editor" class="richtext-content" />
  </div>
</template>
<script>
import { sanitize } from '@facilio/utils/sanitize'
import { htmlToText } from '@facilio/utils/filters'
import { isEmpty } from '@facilio/utils/validation'
import { Editor, EditorContent } from '@tiptap/vue-2'
import ColorPicker from './ColorPicker'

// extensions
import Document from '@tiptap/extension-document'
import Paragraph from '@tiptap/extension-paragraph'
import Text from '@tiptap/extension-text'
import Bold from '@tiptap/extension-bold'
import Italic from '@tiptap/extension-italic'
import Underline from '@tiptap/extension-underline'
import Strike from '@tiptap/extension-strike'
import ListItem from '@tiptap/extension-list-item'
import BulletList from '@tiptap/extension-bullet-list'
import OrderedList from '@tiptap/extension-ordered-list'
import Link from '@tiptap/extension-link'
import Heading from '@tiptap/extension-heading'
import TextStyle from '@tiptap/extension-text-style'
import Placeholder from '@tiptap/extension-placeholder'
import FontSize from './custom-extensions/fontsize.plugin.js'
import Color from './custom-extensions/color.plugin.js'
import BackgroundColor from './custom-extensions/bgcolor.plugin.js'

const FORMAT_CHAIN_HASH = {
  italic: 'toggleItalic',
  bold: 'toggleBold',
  underline: 'toggleUnderline',
  strike: 'toggleStrike',
  'bullet-list': 'toggleBulletList',
  'ordered-list': 'toggleOrderedList',
  heading: 'toggleHeading',
  link: 'setLink',
  unlink: 'unsetLink',
}

const FORMAT_COMMAND_HASH = {
  color: 'toggleColor',
  small: 'setFontSize',
  unsetSmall: 'unsetFontSize',
  highlight: 'toggleBackgroundColor',
}

const SMALL_FONT_SIZE = 12

export default {
  props: {
    value: {
      type: String,
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
      default: 'Enter text here',
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
  },
  components: {
    EditorContent,
    ColorPicker,
  },
  data() {
    return {
      editor: null,
      html: null,
      selectedBgColor: '',
      selectedTextColor: '',
    }
  },
  mounted() {
    this.init()
  },
  beforeDestroy() {
    this.destoryEditor()
  },
  watch: {
    readOnly(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.editor.setOptions({
          editable: !newVal,
        })
      }
    },
  },
  computed: {
    fontOptions() {
      return {
        h1: {
          label: 'Heading 1',
          level: 1,
          isActive: editor => {
            return editor.isActive('heading', { level: 1 })
          },
          isHeading: true,
        },
        h2: {
          label: 'Heading 2',
          level: 2,
          isActive: editor => {
            return editor.isActive('heading', { level: 2 })
          },
          isHeading: true,
        },
        h3: {
          label: 'Heading 3',
          level: 3,
          isActive: editor => {
            return editor.isActive('heading', { level: 3 })
          },
          isHeading: true,
        },
        normal: {
          label: 'Normal',
          isActive: editor => {
            return editor.isActive('paragraph')
          },
          isHeading: false,
        },
        small: {
          label: 'Small',
          isActive: editor => {
            let currentElementAttr = this.editor.getAttributes('textStyle')
            let { fontSize } = currentElementAttr || {}
            let currentFontSize = `${SMALL_FONT_SIZE}px`
            return editor.isActive('paragraph') && fontSize === currentFontSize
          },
          isHeading: false,
        },
      }
    },
    textSize() {
      let { fontOptions, editor } = this || {}
      let { normal: currentTextSize } = fontOptions || {}

      Object.values(fontOptions).forEach(option => {
        let { isActive } = option || {}
        if (!isEmpty(editor) && isActive(editor)) {
          currentTextSize = option
        }
      })

      return currentTextSize
    },
  },
  methods: {
    init() {
      let { readOnly, value, onUpdate, extensionsList } = this

      this.editor = new Editor({
        content: value || '',
        editable: !readOnly,
        extensions: extensionsList(),
        onUpdate,
      })
    },
    destoryEditor() {
      this.editor.destroy()
    },
    extensionsList() {
      let { placeholder } = this || {}
      return [
        Document,
        Paragraph,
        Text,
        Bold,
        Italic,
        Underline,
        Strike,
        ListItem,
        BulletList,
        OrderedList,
        Link,
        Heading.configure({ levels: [1, 2, 3] }),
        TextStyle,
        FontSize.configure({
          types: ['textStyle'],
        }),
        Color.configure({
          multicolor: true,
        }),
        BackgroundColor.configure({
          multicolor: true,
        }),
        Placeholder.configure({
          emptyEditorClass: 'is-editor-empty',
          emptyNodeClass: 'is-empty',
          placeholder,
          showOnlyWhenEditable: true,
          showOnlyCurrent: true,
        }),
      ]
    },
    isActive(type) {
      let { editor } = this || {}
      if (!isEmpty(editor) && !isEmpty(editor.isActive)) {
        return (editor || {}).isActive(type)
      }
      return false
    },
    formatContent(type, params = null) {
      let formatChainName = FORMAT_CHAIN_HASH[type] || {}
      let formatCommandName = FORMAT_COMMAND_HASH[type] || {}
      if (!isEmpty(formatChainName)) {
        // had to be chained
        this.editor
          .chain()
          .focus()
          [formatChainName](params)
          .run()
      } else if (!isEmpty(formatCommandName)) {
        this.editor.commands[formatCommandName](params)

        this.editor
          .chain()
          .focus()
          .run()
      }
    },
    onUpdate({ editor }) {
      let html = editor.getHTML()
      let isNotEmpty = !isEmpty(htmlToText(html))
      let hasChanged = sanitize(html) !== sanitize(this.html)

      if (isNotEmpty || hasChanged) {
        this.html = html
        this.$emit('input', isNotEmpty ? sanitize(html) : null)
      }
    },
    // format methods
    setFontSize(option) {
      let { level, isHeading, label } = option || {}
      let { textSize } = this
      if (isHeading) {
        // unset small font size
        this.formatContent('unsetSmall')
        this.formatContent('heading', { level })
      } else {
        let { level } = textSize || {}
        if (label === 'Small') {
          // toggle heading back
          this.formatContent('heading', { level })
          this.editor.commands.setFontSize(SMALL_FONT_SIZE)
        } else if (label === 'Normal') {
          // unset small font size
          this.formatContent('unsetSmall')
          // toggle heading back
          this.formatContent('heading', { level })
        }
      }
    },
    setColor({ color }) {
      let { selectedTextColor } = this || {}

      if (color === selectedTextColor) this.selectedTextColor = ''
      else this.selectedTextColor = color

      this.formatContent('color', { color })
    },
    setHighlight({ color }) {
      let { selectedBgColor } = this || {}

      if (color === selectedBgColor) this.selectedBgColor = ''
      else this.selectedBgColor = color

      this.formatContent('highlight', { color })
    },
    showLinkPrompt() {
      let { editor = {} } = this || {}
      const previousUrl = editor.getAttributes('link').href
      const url = window.prompt('URL', previousUrl)

      if (url !== null) {
        if (url === '') {
          this.formatContent('unlink')
          return
        } else {
          this.formatContent('link', { href: url })
        }
      }
    },
  },
}
</script>
<style lang="scss">
.richtext-editor {
  .richtext-content {
    padding: 10px;
    word-wrap: break-word;
    word-break: break-word;

    h1 {
      font-size: 2.5em;
      margin: 1.75em 0;
    }
    h2 {
      font-size: 1.85em;
      margin: 1.5em 0;
    }
    h3 {
      font-size: 1.5em;
      margin: 1em 0;
    }
    h1,
    h2,
    h3 {
      font-weight: 300;
    }
    p {
      font-size: 1em;
      margin: 0 0 1em;
      padding: 0;
      letter-spacing: 0;
      -webkit-font-smoothing: antialiased;
    }
  }
  .richtext-toolbar {
    background-color: #fafbff;

    display: flex;
    flex-direction: row;
    flex-wrap: wrap;

    padding: 5px;

    .toolbar-button {
      margin: 5px;
      padding: 5px;
      line-height: 24px;

      border-radius: 3px;
      cursor: pointer;
      display: flex;
      align-items: center;

      &.is-active {
        background-color: #efefef;
      }
      &:hover {
        background-color: #efefef;
      }

      .toolbar-icon {
        display: flex;

        svg {
          fill: #6b7e91;
          width: 12px;
          height: 12px;
        }
      }

      &.toolbar-button-underline .toolbar-icon svg,
      &.toolbar-button-align .toolbar-icon svg {
        width: 14px;
        height: 14px;
      }

      &.toolbar-button-strikethrough {
        padding: 0;
        .toolbar-icon svg {
          width: 24px;
          height: 24px;
        }
      }

      &.toolbar-button-list .toolbar-icon svg,
      &.toolbar-button-link .toolbar-icon svg,
      &.toolbar-button-image .toolbar-icon svg {
        width: 16px;
        height: 16px;
      }
    }

    .toolbar-dropdown {
      background-color: #fff;
      border: 1px solid #d0d9e2;
      border-radius: 3px;

      > .toolbar-dropdown-option {
        line-height: normal;
        cursor: pointer;
        padding: 10px;
        &:hover {
          background-color: #d0d9e2;
        }
      }
    }
    .toolbar-dropdown-trigger {
      color: #4e5b69;
      line-height: normal;
      cursor: pointer;
      width: 110px;
      padding: 10px;
    }

    .toolbar-separator {
      border-right: 1px solid #d0d9e2;
      margin: 7px 5px;
    }

    .mode-btn {
      padding: 5px 5px;
      background-color: #efefef;
      color: rgb(107, 126, 145);
    }
  }
  p.is-editor-empty:first-child::before {
    content: attr(data-placeholder);
    float: left;
    color: #aaa;
    pointer-events: none;
    height: 0;
    font-style: italic;
  }
}
</style>
