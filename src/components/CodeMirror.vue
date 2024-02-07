<template>
  <div>
    <codemirror
      v-model="script"
      @input="onCmCodeChange"
      :options="cmOption"
      @ready="onCmReady"
      @focus="onCmFocus"
      :placeholder="placeholder"
    ></codemirror>
  </div>
</template>
<script>
import { codemirror } from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'
import 'codemirror/lib/codemirror.js'
import 'codemirror/addon/hint/show-hint'
import 'codemirror/addon/hint/show-hint.css'
import 'codemirror/addon/hint/javascript-hint.js'
import 'codemirror/addon/selection/active-line.js'
import 'codemirror/mode/javascript/javascript'
import 'codemirror/addon/display/placeholder.js'
export default {
  data() {
    return {
      script: null,
      cmOption: {
        tabSize: 4,
        styleActiveLine: true,
        mode: 'text/javascript',
        theme: 'default',
        lineNumbers: true,
        line: true,
        matchBrackets: true,
        foldGutter: true,
        placeholder: false,
        hintOptions: {
          completeSingle: false,
        },
        showCursorWhenSelecting: true,
      },
    }
  },
  props: ['value', 'options', 'placeholder'],
  components: {
    codemirror,
  },
  computed: {
    codemirror() {
      return this.$refs.myCm.codemirror
    },
  },
  watch: {
    value: {
      handler() {
        this.script = this.$helpers.cloneObject(this.value)
        if (this.options) {
          Object.assign(this.cmOption, this.options)
        }
      },
      immediate: true,
    },
  },
  methods: {
    onCmReady(cm) {},
    onCmFocus(cm) {},
    onCmCodeChange(newCode) {
      this.$emit('input', newCode)
    },
    addCode(code) {
      this.script = this.script += '\n' + code + '\n'
    },
    onCmScroll() {},
  },
}
</script>
<style lang="scss">
.CodeMirror {
  height: 350px;
}
</style>
