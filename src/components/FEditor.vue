<template>
  <div
    :id="
      tid
        ? tid
        : Math.random()
            .toString(36)
            .substr(2, 9)
    "
    ref="editor"
  ></div>
</template>

<script>
import Quill from 'quill'

export default {
  props: [
    'value',
    'placeholder',
    'editorModules',
    'disabled',
    'tid',
    'registerCustomModules',
  ],
  data() {
    return {
      quill: null,
    }
  },

  mounted() {
    this.registerPrototypes()
    this.setupQuillEditor()
    this.handleInitialContent()
    this.registerEditorEventListeners()
    this.$emit('ready', this.quill)
  },

  watch: {
    value(val) {
      if (val !== this.quill.root.innerHTML && !this.quill.hasFocus()) {
        this.quill.root.innerHTML = val
      }
    },
    disabled(status) {
      this.quill.enable(!status)
    },
  },

  beforeDestroy() {
    this.quill = null
    delete this.quill
  },

  methods: {
    setupQuillEditor() {
      let modules = {}
      if (this.editorModules) {
        modules = this.editorModules
      }

      let editorConfig = {
        debug: false,
        modules: modules,
        theme: 'snow',
        placeholder: this.placeholder ? this.placeholder : '',
        readOnly: this.disabled ? this.disabled : false,
      }
      this.quill = new Quill(this.$el, editorConfig)
    },

    registerPrototypes() {
      Quill.prototype.getHTML = function() {
        return this.container.querySelector('.ql-editor').innerHTML
      }
      Quill.prototype.getWordCount = function() {
        return this.container.querySelector('.ql-editor').innerText.length
      }

      let Block = Quill.import('blots/block')
      class Div extends Block {}
      Div.tagName = 'div'
      Div.blotName = 'div'
      Div.allowedChildren = Block.allowedChildren
      Div.allowedChildren.push(Block)
      Quill.register(Div, false)

      if (this.registerCustomModules) {
        this.registerCustomModules(Quill)
      }
    },

    registerEditorEventListeners() {
      this.quill.on('text-change', this.handleTextChange)
      this.quill.on('selection-change', this.handleSelectionChange)
      this.listenForEditorEvent('text-change')
      this.listenForEditorEvent('selection-change')
      this.listenForEditorEvent('editor-change')
    },

    listenForEditorEvent(type) {
      this.quill.on(type, (...args) => {
        this.$emit(type, ...args)
      })
    },

    handleInitialContent() {
      if (this.value) this.quill.root.innerHTML = this.value
    },

    handleSelectionChange(range, oldRange) {
      if (!range && oldRange) this.$emit('blur', this.quill)
      else if (range && !oldRange) this.$emit('focus', this.quill)
    },

    handleTextChange() {
      let editorContent =
        this.quill.getHTML() === '<div><br></div>' ? '' : this.quill.getHTML()
      this.$emit('input', editorContent)
    },

    removeFormat() {
      this.quill.removeFormat(0, this.quill.getLength())
    },
  },
}
</script>
