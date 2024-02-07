<template>
  <el-dialog
    :title="$t('common.products.long_description')"
    :visible="true"
    width="50%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="cancel"
  >
    <div class="pB70">
      <RichTextArea v-model="description" :disabled="disabled" :isEdit="true" />
    </div>

    <div v-if="!disabled" class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button class="modal-btn-save" type="primary" @click="save"
        >{{ $t('common.roles.save') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import RichTextArea from '@/forms/RichTextArea'
export default {
  props: ['content', 'disabled'],

  components: {
    RichTextArea,
  },
  data() {
    return {
      description: null,
    }
  },
  created() {
    if (this.content) {
      this.description = this.content
    }
  },
  methods: {
    cancel() {
      this.$emit('onClose')
    },
    save() {
      this.$emit('onSave', this.description)
    },
  },
}
</script>
