<template>
  <div
    class="f-quill ql-snow dragabale-card imagecard"
    v-bind:class="{ readonly: !mode }"
  >
    <f-editor
      v-model="content"
      :editorModules="quillEditorConfig"
      v-bind:class="{ fquillidel: !mode }"
      v-if="mode"
    ></f-editor>
    <div v-show="!mode" class="f-quill-view ql-editor">
      <div
        class="ql-container p0"
        v-if="data"
        v-html="widget.dataOptions.metaJson"
      ></div>
      <div
        class="ql-container p0"
        v-else
        v-html="widget.dataOptions.metaJson"
      ></div>
    </div>
  </div>
</template>
<script>
import colors from 'charts/helpers/colors'
import FEditor from '@/FEditor'
export default {
  props: ['widget', 'data'],
  components: {
    FEditor,
  },
  data() {
    return {
      content: null,
      quillEditorConfig: {
        toolbar: [
          [
            'bold',
            'italic',
            'underline',
            'link',
            { list: 'ordered' },
            { list: 'bullet' },
            { indent: '-1' },
            { indent: '+1' },
            { size: ['small', false, 'large', 'huge'] },
            { header: [1, 2, 3, 4, 5, 6, false] },
            { color: colors.textCardColors },
            { background: colors.textCardColors },
            { align: ['', 'right', 'center', 'justify'] },
          ],
        ],
      },
    }
  },
  mounted() {
    this.getCardData()
  },
  watch: {
    content: {
      handler(newData, oldData) {
        this.updatecontehnt(false)
      },
    },
    widget: {
      handler(newData, oldData) {
        this.getCardData()
      },
      deep: true,
    },
  },
  methods: {
    getCardData() {
      if (this.widget && this.widget.dataOptions.metaJson) {
        this.content = this.widget.dataOptions.metaJson
      }
    },
    updatecontehnt() {
      this.widget.dataOptions.metaJson = this.content
    },
  },
  computed: {
    mode() {
      // if (
      //   this.$route.query &&
      //   this.$route.query.create &&
      //   this.$route.query.create === 'edit'
      // ) {
      //   return true
      // } else if (
      //   this.$route.query &&
      //   this.$route.query.create &&
      //   this.$route.query.create === 'new'
      // ) {
      //   return true
      // } else {
      //   return false
      // }
      return false
    },
  },
}
</script>
<style lang="scss" scoped>
.f-quill-view {
  width: 100%;
}
</style>
<style>
.fquillidel {
  overflow: hidden;
  width: 0px;
  height: 0px !important;
  margin: 0;
  position: absolute;
  right: 0;
  top: 0;
}
.f-quill,
.editr {
  height: 100%;
  overflow: auto;
}
.readonly .ql-toolbar {
  display: none;
}
/* .readonly {
    padding: 20px;
  } */
p {
  font-size: 100%;
}
.dragabale-card.dragpoint {
  position: absolute;
  left: 0;
  top: 0;
  border-radius: 5px;
  width: 10px;
  height: 10px;
  background: #ff3e90;
}
h1,
h2,
h3,
h4,
h5,
h6 {
  line-height: normal;
  font-weight: unset;
}
</style>
