<template>
  <el-dialog
    :title="isEdit ? 'EDIT TEMPLATE' : 'NEW TEMPLATE'"
    :visible.sync="canShowFormCreation"
    width="30%"
    class="fc-dialog-center-container new-form-container"
    :append-to-body="true"
    :before-close="beforeClose"
  >
    <ErrorBanner
      :error.sync="errVal"
      :errorMessage.sync="errMsgVal"
    ></ErrorBanner>
    <div v-bind:class="canShowSite(formList) ? 'height450' : 'height350'">
      <el-form ref="formList" :rules="rules" :model="formList">
        <el-form-item
          :label="$t('common.roles.name')"
          prop="displayName"
          class="mB10"
          :required="true"
        >
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="formList.displayName"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('maintenance._workorder.description')"
          prop="description"
          class="mB10"
        >
          <el-input
            :placeholder="$t('common._common.enter_desc')"
            v-model="formList.description"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('setup.setup.stateflow')"
          prop="stateFlowId"
          class="mB20"
        >
          <el-select
            :placeholder="$t('common._common.select')"
            clearable
            v-model="formList.stateFlowId"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="stateFlow in stateFlowList"
              :key="stateFlow.id"
              :label="stateFlow.name"
              :value="stateFlow.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item
          :label="$t('space.sites._site')"
          prop="siteIds"
          class="mB20"
          v-if="canShowSite(formList)"
        >
          <el-select
            :placeholder="$t('common._common.select')"
            :disabled="!$validation.isEmpty(currentSiteId) && !isEdit"
            v-model="formList.siteIds"
            multiple
            class="fc-input-full-border-select2 width100"
            collapse-tags
          >
            <el-option
              v-for="site in sites"
              :key="site.id"
              :label="site.label"
              :value="site.value"
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
        :loading="saving"
        class="modal-btn-save"
        @click="addNewForm('formList')"
        >{{ $t('panel.dashboard.confirm') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { getFieldOptions } from 'util/picklist'
import ErrorBanner from '@/ErrorBanner'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: {
    showFormCreation: {
      type: Boolean,
    },
    error: {
      type: Boolean,
    },
    formList: {
      type: Object,
    },
    isEdit: {
      type: Boolean,
    },
    errorMessage: {
      type: String,
    },
    stateFlowList: {
      type: Array,
    },
    moduleName: {
      type: String,
      required: true,
    },
    apps: {
      type: Array,
    },
    saving: {
      type: Boolean,
    },
  },
  components: {
    ErrorBanner,
  },
  created() {
    this.$store.dispatch('loadSite')
    this.loadSites()
  },
  data() {
    return {
      rules: {
        displayName: [
          {
            required: true,
            message: 'Please input Form name',
            trigger: 'change',
          },
        ],
      },
      sites: [],
    }
  },
  computed: {
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
    currentSiteId() {
      let { isEdit } = this
      let siteId = Number(this.$cookie.get('fc.currentSite'))
      if (!isEmpty(siteId) && !isEdit && siteId > 0) {
        this.formList.siteIds = [siteId]
      }
      return siteId || null
    },
  },
  methods: {
    async loadSites() {
      let params = {
        field: { lookupModuleName: 'site' },
      }

      let { error, options } = await getFieldOptions(params)

      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.sites = options
      }
    },
    canShowSite(formList) {
      let isDefault = true
      if (!isEmpty(formList) && !isEmpty(formList.name) && this.isEdit) {
        isDefault = !formList.name.startsWith('default_')
      }
      return isDefault
    },
    addNewForm(formList) {
      this.$emit('setFormValid')
      this.$refs[formList].validate(valid => {
        if (valid) {
          let { formList } = this
          let data = {
            form: formList,
          }
          this.$emit('saveRecord', data)
        }
      })
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

<style lang="scss">
.new-form-container {
  .el-select .el-tag {
    border-radius: 100px !important;
    background-color: #f8feff !important;
    border: solid 1px #39b1c1 !important;
    color: #39b1c1;
  }
  .el-select .el-tag__close.el-icon-close {
    background-color: rgba(0, 0, 0, 0) !important;
    right: -7px;
    top: 0px;
    color: #219fb0;
    font-size: 14px;
  }
}
</style>
