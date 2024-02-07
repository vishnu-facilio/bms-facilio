<template>
  <el-dialog
    :title="`${isNew ? 'Add' : 'Edit'} Text`"
    :visible.sync="visibility"
    v-if="visibility"
    :append-to-body="true"
    :before-close="closeDialog"
    custom-class="floorplan-property-dialog graphic-object-dialog fc-dialog-center-container setup-dialog60 text-card-dialog"
  >
    <div class="text-editor-body">
      <div class="f-quill ql-snow">
        <f-editor v-model="data" :editorModules="quillEditorConfig"></f-editor>
      </div>
    </div>

    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog()"
        >CANCEL</el-button
      >
      <el-button
        v-if="data && this.widget && this.widget.widget"
        class="modal-btn-save"
        type="primary"
        @click="updateObjectProps"
        >{{ 'UPDATE' }}</el-button
      >
      <el-button v-else class="modal-btn-save" type="primary" @click="save">{{
        'SAVE'
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import colors from 'charts/helpers/colors'
import FEditor from '@/FEditor'
import cloneDeep from 'lodash/cloneDeep'
export default {
  props: ['visibility', 'widget'],
  components: {
    FEditor,
  },
  data() {
    return {
      data: null,
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
    this.getData()
  },
  computed: {
    isNew() {
      if (this.data && this.widget && this.widget.widget) {
        return false
      }
      return true
    },
  },
  methods: {
    getData() {
      if (
        this.widget &&
        this.widget.widget &&
        this.widget.widget.dataOptions.metaJson
      ) {
        this.data = cloneDeep(this.widget.widget.dataOptions.metaJson)
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
      this.$emit('close')
    },
    save() {
      this.$emit('add', this.data)
    },
    updateObjectProps() {
      this.$emit('update', {
        textData: this.data,
        id: cloneDeep(this.widget.id),
      })
    },
  },
}
</script>
<style>
.text-card-dialog .el-dialog__body {
  height: 500px;
}
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
.text-editor-body {
  height: 100%;
}
.text-card-dialog .ql-toolbar.ql-snow + .ql-container.ql-snow {
  height: 355px;
}
</style>
