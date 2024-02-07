<template>
  <el-form
    ref="newForm"
    @submit.prevent="save"
    :label-position="'top'"
    class="fc-form"
    :model="formModel"
    :rules="rules"
    v-if="isInlineForm"
  >
    <div class="form-content">
      <slot name="form" :formModel="formModel"></slot>
    </div>
  </el-form>
  <el-dialog
    v-else
    :visible.sync="visibilityState"
    :fullscreen="true"
    :custom-class="'fc-dialog-form ' + getDialogClass"
    :show-close="false"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <span slot="title">
      <el-row class="form-header">
        <slot name="header">
          <span class="form-title">
            {{ title }}
          </span>
        </slot>
      </el-row>
    </span>
    <el-form
      ref="newForm"
      @submit.prevent="save"
      :label-position="'top'"
      class="fc-form"
      :model="formModel"
      :rules="rules"
    >
      <div class="form-content">
        <slot name="form" :formModel="formModel"></slot>
      </div>
    </el-form>
    <div class="modal-dialog-footer">
      <slot name="footer">
        <button type="button" class="modal-btn-cancel" @click="close">
          <span>Cancel</span>
        </button>
        <button type="button" class="modal-btn-save" @click="save">
          <span>Save</span>
        </button>
      </slot>
    </div>
  </el-dialog>
</template>
<script>
const types = ['inline', 'dialog40', 'dialog50', 'dialog60']

export default {
  props: {
    title: {
      type: String,
      default: '',
    },
    model: Object,
    rules: Object,
    type: {
      type: String,
      default: 'dialog50',
      validator: val => types.includes(val),
    },
    size: String,
  },
  computed: {
    isInlineForm() {
      return this.type === 'inline'
    },
    getDialogClass() {
      if (this.isInlineForm) {
        return ''
      }
      return 'fc-' + this.type + '-form'
    },
  },
  data() {
    return {
      visibilityState: false,
      formModel: this.$helpers.cloneObject(this.model),
    }
  },
  methods: {
    open() {
      this.visibilityState = true
    },
    close() {
      this.visibilityState = false
    },
    handleClose() {
      this.reset()
      this.$emit('closed', true)
    },
    getFormModel() {
      return this.formModel
    },
    save() {
      this.$emit('save', this.getFormModel())
    },
    reset() {
      this.formModel = this.$helpers.cloneObject(this.model)
    },
  },
}
</script>
<style>
.el-dialog.fc-dialog-form {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  overflow-y: hidden;
  border-radius: 0px;
  transition: transform 0.25s ease;
}

.fc-dialog40-form {
  width: 40% !important;
}

.fc-dialog50-form {
  width: 50% !important;
}

.fc-dialog60-form {
  width: 60% !important;
}

.form-content {
  padding: 0px 30px;
}

.form-footer {
  position: absolute;
  bottom: 0px;
  width: 100%;
}

.fbtn {
  border: 0;
  border-radius: 0px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.8px;
  text-align: center;
  margin: 0;
  text-transform: uppercase;
  outline: none;
  padding: 18px;
  cursor: pointer;
}

.fbtn-secondary {
  background-color: #f4f4f4;
  color: #5f5f5f;
}

.fbtn-primary {
  color: white;
  background-color: #39b2c2;
}
</style>
