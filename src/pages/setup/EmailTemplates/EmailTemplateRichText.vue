<template>
  <div class="richtext-editor">
    <div class="richtext-toolbar" v-if="hideToolBar">
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

      <!-- font family latest -->
      <el-popover
        placement="top-start"
        width="180"
        trigger="click"
        popper-class="fc-text-align-popover"
      >
        <span slot="reference" class="fc-table-btn-popover flex-middle">
          <div class="rc-font-family text-left">
            {{ familyName }}
          </div>
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>

        <div class="popup-container-new">
          <button
            v-for="(label, key) in FontFamilyList"
            :key="key"
            :lable="label"
            @click="() => selectFamily(key)"
            class="fc-editor-fontFamily"
            :style="{ fontFamily: label }"
          >
            <div
              class="fc-editor-fontFamily"
              :class="{
                'is-active': selectedFontFamily === key,
              }"
            >
              {{ key }}
            </div>
          </button>
        </div>
      </el-popover>
      <!-- end -->

      <div class="toolbar-separator mL5"></div>

      <div
        class="toolbar-button toolbar-button-bold mT5"
        :class="{ 'is-active': isActive('bold') }"
        @click="() => formatContent('bold')"
        v-tippy="{ arrow: true, arrowType: 'round' }"
        content="Bold"
      >
        <inline-svg
          src="richtext-editor/bold1"
          class="toolbar-icon"
          iconClass="icon icon-xs"
        />
      </div>
      <div
        class="toolbar-button toolbar-button-italic mT5"
        :class="{ 'is-active': isActive('italic') }"
        @click="() => formatContent('italic')"
        v-tippy="{ arrow: true, arrowType: 'round' }"
        content="Italic"
      >
        <inline-svg
          src="richtext-editor/italic1"
          class="toolbar-icon"
          iconClass="icon icon-xs"
        />
      </div>
      <div
        class="toolbar-button mT5"
        :class="{ 'is-active': isActive('underline') }"
        @click="() => formatContent('underline')"
        v-tippy="{ arrow: true, arrowType: 'round' }"
        content="Underline"
      >
        <inline-svg
          src="richtext-editor/underline1"
          class="toolbar-icon"
          iconClass="icon icon-sm"
        />
      </div>
      <div
        class="toolbar-button mT5"
        :class="{ 'is-active': isActive('strike') }"
        @click="() => formatContent('strike')"
        v-tippy="{ arrow: true, arrowType: 'round' }"
        content="Strike through"
      >
        <inline-svg
          src="richtext-editor/strikethrough1"
          class="toolbar-icon"
          iconClass="icon icon-sm"
        />
      </div>
      <div class="toolbar-separator"></div>
      <div
        v-if="!hideUnorderedList"
        class="toolbar-button"
        :class="{ 'is-active': isActive('bullet-list') }"
        @click="() => formatContent('bullet-list')"
      >
        <inline-svg
          src="richtext-editor/ordered-list1"
          class="toolbar-icon"
          v-tippy="{ arrow: true, arrowType: 'round' }"
          content="Unordered list"
          iconClass="icon icon-sm-md"
        />
      </div>
      <div
        v-if="!hideOrderedList"
        class="toolbar-button"
        :class="{ 'is-active': isActive('ordered-list') }"
        @click="() => formatContent('ordered-list')"
      >
        <inline-svg
          src="richtext-editor/unordered-list1"
          clsdass="toolbar-icon"
          v-tippy="{ arrow: true, arrowType: 'round' }"
          content="order list"
          iconClass="icon icon-sm-md vertical-middle"
        />
      </div>
      <div
        class="toolbar-button toolbar-button-link mT5"
        :class="{ 'is-active': isActive('link') }"
        @click="showLinkPrompt"
        v-tippy="{ arrow: true, arrowType: 'round' }"
        content="Link"
      >
        <inline-svg
          src="richtext-editor/link1"
          class="toolbar-icon"
          iconClass="icon icon-sm-md"
        />
      </div>
      <div class="toolbar-separator"></div>
      <ColorPicker @currentColor="setHighlight">
        <template #reference>
          <div
            class="toolbar-button toolbar-button-link"
            :class="{ 'is-active': isActive('backgroundColor') }"
            v-tippy="{ arrow: true, arrowType: 'round' }"
            content="Highlight"
          >
            <inline-svg
              src="richtext-editor/highlight1"
              class="toolbar-icon"
              iconClass="icon icon-sm-md"
            />
          </div>
        </template>
      </ColorPicker>
      <ColorPicker @currentColor="setColor">
        <template #reference>
          <div
            class="toolbar-button toolbar-button-link"
            :class="{ 'is-active': isActive('color') }"
            v-tippy="{ arrow: true, arrowType: 'round' }"
            content="Text color"
          >
            <inline-svg
              src="richtext-editor/text-color1"
              class="toolbar-icon"
              iconClass="icon icon-sm-md"
            />
          </div>
        </template>
      </ColorPicker>
      <div class="toolbar-separator"></div>

      <el-popover
        placement="top-start"
        width="50"
        trigger="click"
        popper-class="fc-text-align-popover"
      >
        <el-button
          slot="reference"
          class="fc-table-btn-popover mT4"
          v-tippy="{ arrow: true, arrowType: 'round' }"
          content="Text align"
        >
          <inline-svg
            src="richtext-editor/left-align1"
            class="toolbar-icon"
            iconClass="icon icon-xxll"
          />
        </el-button>
        <button
          @click="
            editor
              .chain()
              .focus()
              .setTextAlign('left')
              .run()
          "
          :class="{ 'is-active': isActive({ textAlign: 'left' }) }"
          v-tippy
          title="Left"
        >
          <inline-svg
            src="richtext-editor/left-align"
            class="toolbar-icon"
            iconClass="icon icon-xs"
          />
        </button>
        <button
          @click="
            editor
              .chain()
              .focus()
              .setTextAlign('center')
              .run()
          "
          :class="{ 'is-active': isActive({ textAlign: 'center' }) }"
          v-tippy
          title="Center"
        >
          <inline-svg
            src="richtext-editor/center-align"
            class="toolbar-icon"
            iconClass="icon icon-xs"
          />
        </button>
        <button
          @click="
            editor
              .chain()
              .focus()
              .setTextAlign('right')
              .run()
          "
          :class="{ 'is-active': isActive({ textAlign: 'right' }) }"
          v-tippy
          title="Right"
        >
          <inline-svg
            src="richtext-editor/right-align"
            class="toolbar-icon"
            iconClass="icon icon-xs"
          />
        </button>
        <button
          @click="
            editor
              .chain()
              .focus()
              .unsetTextAlign()
              .run()
          "
          v-tippy
          title="Unset"
        >
          <inline-svg
            src="richtext-editor/unset-align"
            class="toolbar-icon"
            iconClass="icon icon-xs"
          />
        </button>
      </el-popover>

      <el-popover placement="top-start" width="300" trigger="click">
        <!-- table -->
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .insertTable({ rows: 3, cols: 3, withHeaderRow: true })
              .run()
          "
        >
          Insert table
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .addColumnBefore()
              .run()
          "
        >
          Add column before
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .addColumnAfter()
              .run()
          "
        >
          Add column after
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .deleteColumn()
              .run()
          "
        >
          Delete column
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .addRowBefore()
              .run()
          "
        >
          Add row before
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .addRowAfter()
              .run()
          "
        >
          Add row after
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .deleteRow()
              .run()
          "
        >
          Delete row
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .deleteTable()
              .run()
          "
        >
          Delete table
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .mergeCells()
              .run()
          "
        >
          Merge cells
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .splitCell()
              .run()
          "
        >
          Split cell
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .toggleHeaderColumn()
              .run()
          "
        >
          Toggle header column
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .toggleHeaderRow()
              .run()
          "
        >
          Toggle header row
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .toggleHeaderCell()
              .run()
          "
        >
          Toggle header cell
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .mergeOrSplit()
              .run()
          "
        >
          Merge or split
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .setCellAttribute('colspan', 2)
              .run()
          "
        >
          Set cell attribute
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .fixTables()
              .run()
          "
        >
          Fix tables
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .goToNextCell()
              .run()
          "
        >
          Goto next cell
        </button>
        <button
          class="fc-rich-text-btn"
          @click="
            editor
              .chain()
              .focus()
              .goToPreviousCell()
              .run()
          "
        >
          GoTo previous cell
        </button>
        <el-button
          slot="reference"
          class="fc-table-btn-popover mT5"
          v-tippy="{ arrow: true, arrowType: 'round' }"
          content="Table"
        >
          <inline-svg
            src="richtext-editor/table1"
            class="toolbar-icon"
            iconClass="icon icon-sm"
          />
        </el-button>
      </el-popover>
      <div class="toolbar-separator"></div>

      <div
        class="toolbar-button toolbar-button-link"
        :class="{ 'is-active': isActive('image') }"
        @click="addImage"
        v-tippy="{ arrow: true, arrowType: 'round' }"
        content="Image"
      >
        <inline-svg
          src="richtext-editor/image"
          class="toolbar-icon"
          iconClass="icon icon-sm"
        />
      </div>

      <div
        class="toolbar-button toolbar-button-link mL0"
        @click="
          editor
            .chain()
            .focus()
            .toggleBlockquote()
            .run()
        "
        :class="{ 'is-active': isActive('blockquote') }"
        v-tippy="{ arrow: true, arrowType: 'round' }"
        content="Blockquote"
      >
        <inline-svg
          src="richtext-editor/blockquote1"
          class="toolbar-icon"
          iconClass="icon icon-xs"
        />
      </div>
      <div class="flex-middle">
        <slot name="plainTextMode"></slot>
      </div>
    </div>
    <div>
      <slot name="emailSubject"></slot>
    </div>

    <EditorContent
      :editor="editor"
      class="richtext-content richtext-email-editor-container"
    />
  </div>
</template>
<script>
import { sanitize } from '@facilio/utils/sanitize'
import { htmlToText } from '@facilio/utils/filters'
import { isEmpty } from '@facilio/utils/validation'
import { Editor, EditorContent } from '@tiptap/vue-2'
import ColorPicker from 'components/editors/ColorPicker'
import { API } from '@facilio/api'

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
import Link from './plugins/link.plugin.js'
import Image from './plugins/image.plugin.js'
import TableHeaders from './plugins/tableHeader.plugin'
import TableCells from './plugins/tableCell.plugin.js'
import Heading from '@tiptap/extension-heading'
import TextStyle from '@tiptap/extension-text-style'
import Placeholder from '@tiptap/extension-placeholder'
import FontSize from 'components/editors/custom-extensions/fontsize.plugin.js'
import Color from 'components/editors/custom-extensions/color.plugin.js'
import BackgroundColor from 'components/editors/custom-extensions/bgcolor.plugin.js'
import Paragraphs from './plugins/paragraph.plugin.js'
import Mention from '@tiptap/extension-mention'
import suggestion from 'components/editors/custom-extensions/suggestions.plugin.js'
import Table from '@tiptap/extension-table'
import TableRow from '@tiptap/extension-table-row'
import TableCell from '@tiptap/extension-table-cell'
import TableHeader from '@tiptap/extension-table-header'
import TextAlign from '@tiptap/extension-text-align'
import Blockquote from '@tiptap/extension-blockquote'
import StarterKit from '@tiptap/starter-kit'
import FontFamily from '@tiptap/extension-font-family'
import Typography from '@tiptap/extension-typography'
import Gapcursor from '@tiptap/extension-gapcursor'
import Dropcursor from '@tiptap/extension-dropcursor'
import CodeBlock from '@tiptap/extension-code-block'

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

const FONT_FAMILY = {
  Inter: 'Inter',
  'Comic Sans': 'Comic Sans MS, Comic Sans',
  Serif: 'serif',
  Monospace: 'monospace',
  Cursive: 'cursive',
  Garamond: 'Garamond',
  Georgia: 'georgia',
  'Sans-Serif': 'sans-serif',
  Tahoma: 'tahoma',
  'trebuchet ms': 'trebuchet ms',
  Verdana: 'verdana',
  unsetFontFamily: '',
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
    hideToolBar: {
      type: Boolean,
      default: true,
    },
  },
  components: {
    EditorContent,
    ColorPicker,
  },
  provide: {
    mentions: 'hello',
  },
  data() {
    return {
      editor: null,
      html: null,
      selectedBgColor: '',
      selectedTextColor: '',
      mentionsList: [],
      render: false,
      familyName: 'Sans-Serif',
      FontFamilyList: FONT_FAMILY,
      selectedFontFamily: 'Sans-Serif',
    }
  },
  mounted() {
    this.getMentionsList()
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
  },
  methods: {
    async getMentionsList() {
      let moduleName = this.$router?.currentRoute?.params?.moduleName || null
      let url = `v3/placeholders/${moduleName}`
      let { data } = await API.get(url)
      let {
        placeholders: { fields: fields, moduleFields: moduleFields },
      } = data

      let returnData = []

      fields.forEach((field, index) => {
        let { module } = field

        if (!isEmpty(module)) {
          let children = moduleFields[module]

          returnData[index] = { ...field, children }
        }
      })
      this.mentionsList = returnData
    },
    init() {
      let { readOnly, value, onUpdate, extensionsList } = this
      this.editor = new Editor({
        content: value || '',
        editable: !readOnly,
        extensions: extensionsList(),
        onUpdate,
      })

      this.$nextTick(() => {
        this.render = true
      })
    },
    selectFamily(key) {
      this.familyName = key
      this.selectedFontFamily = key
      let name = FONT_FAMILY[key]
      if (name == '') {
        this.editor
          .chain()
          .focus()
          .unsetFontFamily()
          .run()
      } else {
        this.editor
          .chain()
          .focus()
          .setFontFamily(`${name}`)
          .run()
      }
    },
    setContent(content) {
      this.editor.commands.setContent(content)
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
        Link.configure({
          HTMLAttributes: {
            class: 'my-custom-class',
          },
        }),
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
        Mention.configure({
          HTMLAttributes: {
            class: 'mention',
          },
          suggestion,
        }),
        Placeholder.configure({
          emptyEditorClass: 'is-editor-empty',
          emptyNodeClass: 'is-empty',
          placeholder,
          showOnlyWhenEditable: true,
          showOnlyCurrent: true,
        }),
        Table.configure({
          resizable: true,
        }),
        TableRow,
        TableHeader,
        TableCell,
        TextAlign.configure({
          types: ['heading', 'paragraph'],
        }),
        Blockquote,
        StarterKit,
        FontFamily,
        Typography,
        Gapcursor,
        Image,
        Dropcursor,
        CodeBlock,
        TableHeaders,
        Paragraphs,
        TableCells,
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
    addImage() {
      const url = window.prompt('URL')

      if (url) {
        this.editor
          .chain()
          .focus()
          .setImage({ src: url })
          .run()
      }
    },
  },
}
</script>
<style lang="scss">
.richtext-editor {
  .richtext-email-editor-container {
    height: 575px;
  }
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
    p {
      font-size: 1em;
      margin: 0 !important;
      padding: 0;
      line-height: 20px;
      letter-spacing: 0;
      -webkit-font-smoothing: antialiased;
    }
    blockquote {
      padding-left: 1rem;
      padding-top: 0;
      padding-bottom: 0;
      border-left: 3px solid rgba(13, 13, 13, 0.1);
    }
    img {
      width: 100%;
    }
  }
  .richtext-toolbar {
    background-color: #fafbff;
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    padding: 6px 5px;
    justify-content: space-between;

    .toolbar-button {
      margin: 5px;
      padding: 5px;
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

        // svg {
        //   fill: #2e2e49;
        //   width: 12px;
        //   height: 12px;
        //   path {
        //     fill: #2e2e49;
        //   }
        //   circle {
        //     fill: #2e2e49;
        //   }
        //   g {
        //     fill: #2e2e49 !important;
        //     path {
        //       fill: #2e2e49;
        //     }
        //   }
        // }
      }

      // &.toolbar-button-underline .toolbar-icon svg,
      // &.toolbar-button-align .toolbar-icon svg {
      //   width: 14px;
      //   height: 14px;
      // }

      // &.toolbar-button-strikethrough {
      //   padding: 0;
      //   .toolbar-icon svg {
      //     width: 24px;
      //     height: 24px;
      //   }
      // }

      // &.toolbar-button-list .toolbar-icon svg,
      // &.toolbar-button-link .toolbar-icon svg,
      // &.toolbar-button-image .toolbar-icon svg {
      //   width: 14px;
      //   height: 14px;
      // }
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
      color: #2e2e49;
      line-height: normal;
      cursor: pointer;
      max-width: 110px;
      padding: 10px;
    }

    .toolbar-separator {
      width: 1px;
      height: 16px;
      background: #ececec;
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

.ProseMirror {
  table {
    border-collapse: collapse;
    table-layout: fixed;
    width: 100%;
    margin: 0;
    overflow: hidden;

    td,
    th {
      min-width: 1em;
      border: 2px solid #ced4da;
      padding: 3px 5px;
      vertical-align: top;
      box-sizing: border-box;
      position: relative;

      > * {
        margin-bottom: 0 !important;
      }
      p {
        margin-bottom: 0;
      }
    }

    th {
      font-weight: bold;
      text-align: left;
      background-color: #f5f6f8;
    }

    .selectedCell:after {
      z-index: 2;
      position: absolute;
      content: '';
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      background: rgba(200, 200, 255, 0.4);
      pointer-events: none;
    }

    .column-resize-handle {
      position: absolute;
      right: -2px;
      top: 0;
      bottom: -2px;
      width: 4px;
      background-color: #adf;
      pointer-events: none;
    }

    p {
      margin: 0;
    }
  }
}

.tableWrapper {
  padding: 1rem 0;
  overflow-x: auto;
}

.resize-cursor {
  cursor: ew-resize;
  cursor: col-resize;
}
.fc-table-btn-popover {
  border: none;
  background: transparent;
  padding-left: 5px;
  padding-right: 5px;
  padding-top: 4px;
  padding-bottom: 4px;
  margin-left: 1px;
  &:hover {
    background: #efefef;
  }
}
.fc-rich-text-btn {
  font-size: 13px;
  margin-bottom: 10px;
  margin-right: 10px;
  background: rgb(57 178 194 / 20%);
  border: 1px solid #39b2c2;
  border-radius: 4px;
}
.fc-text-align-popover {
  min-width: 50px !important;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  button {
    border: none;
    background: none;
    padding: 5px;
    border-radius: 3px;
    &:hover {
      background: #efefef;
    }
  }
  .is-active {
    path {
      fill: #ee518f;
    }
  }
}
.fc-editor-bubble-menu-btn {
  font-size: 12px;
  background: #fff;
  border: 1px solid #000;
  padding: 5px 8px;
  margin-left: 5px;
  border-radius: 4px;
  text-transform: capitalize;
  font-weight: 500;
  cursor: pointer;
  &:hover {
    background: #efefef;
  }
}

.fc-editor-fontFamily {
  font-size: 14px;
  font-weight: 400;
  cursor: pointer;
}
.fc-table-btn-popover {
  border: none;
  background: transparent;
  padding-left: 5px;
  padding-right: 5px;
  padding-top: 4px;
  padding-bottom: 4px;
  margin-left: 1px;
  &:hover {
    background: #efefef;
  }
  .rc-font-family {
    width: 80px;
    font-size: 14px;
    overflow: hidden !important;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
.popup-container-new {
  height: 250px;
  overflow-y: scroll;
  padding-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  .fc-editor-fontFamily {
    font-size: 14px;
    font-weight: 400;
    cursor: pointer;
    width: 100%;
    display: flex;
    padding: 4px;
  }
}
</style>
