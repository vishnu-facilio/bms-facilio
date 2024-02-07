<template>
  <div v-if="visibility">
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="false"
      title="Long Description"
      width="50%"
      open="top"
      :before-close="cancelEdit"
      custom-class="fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
      :append-to-body="true"
    >
      <vue-editor
        v-model="vueEditorData"
        :disabled="disabled"
        class="height250 pT15"
      ></vue-editor>
      <div v-if="!disabled" class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="cancelEdit"
          >CANCEL</el-button
        >
        <el-button class="modal-btn-save" type="primary" @click="saveEdit"
          >SAVE</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { VueEditor } from 'vue2-editor'
export default {
  props: {
    content: String,
    visibility: Boolean,
    disabled: { type: Boolean, default: false },
  },
  components: {
    VueEditor,
  },
  data() {
    return {
      vueEditorData: null,
    }
  },
  mounted() {
    if (this.content) {
      this.vueEditorData = this.content
    }
  },
  methods: {
    cancelEdit() {
      this.$emit('update:visibility', false)
    },
    saveEdit() {
      this.$emit('saved', this.vueEditorData)
    },
  },
}
</script>
