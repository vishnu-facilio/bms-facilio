<template>
  <el-dialog
    :title="'SELECT VISITOR TYPE FORM'"
    :visible.sync="canShowFormCreation"
    width="30%"
    height="40%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="beforeClose"
  >
    <div v-bind:class="'height250'">
      <el-form ref="visitorType" :model="visitorType">
        <el-form-item
          :label="$t('Select Form')"
          prop="visitorFormId"
          class="mB20"
        >
          <el-select
            :placeholder="$t('common._common.select')"
            v-model="visitorType.visitorFormId"
            class="fc-input-full-border-select2 width100 height100 scrollable"
          >
            <el-option
              v-for="form in formList"
              :key="form.id"
              :label="form.displayName"
              :value="form.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialogBox" class="modal-btn-cancel">{{
        $t('setup.users_management.cancel')
      }}</el-button>
      <el-button
        type="primary"
        class="modal-btn-save"
        @click="addNewForm(visitorType)"
        >{{ $t('panel.dashboard.confirm') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { mapState } from 'vuex'

export default {
  props: {
    showFormCreation: {
      type: Boolean,
    },
    error: {
      type: Boolean,
    },
    formList: {
      type: Array,
    },
    visitorType: {
      type: Object,
    },
    isEdit: {
      type: Boolean,
    },
    errorMessage: {
      type: String,
    },
    moduleName: {
      type: String,
      required: true,
    },
    appId: {
      type: Number,
    },
  },
  data() {
    return {}
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    canShowFormCreation: {
      get() {
        return this.showFormCreation
      },
      set(value) {
        this.$emit('update:showFormCreation', value)
      },
    },
    errVal: {
      get() {
        return this.error
      },
      set(value) {
        this.$emit('update:error', value)
      },
    },
    errMsgVal: {
      get() {
        return this.errorMessage
      },
      set(value) {
        return this.$emit('update:errorMsg', value)
      },
    },
  },
  methods: {
    addNewForm(visitorType) {
      this.$emit('saveRecord', visitorType)
    },
    closeDialogBox() {
      this.canShowFormCreation = false
    },
    beforeClose(done) {
      this.$set(this, 'errMsgVal', null)
      this.$set(this, 'errVal', false)
      done()
    },
  },
}
</script>

<style></style>
