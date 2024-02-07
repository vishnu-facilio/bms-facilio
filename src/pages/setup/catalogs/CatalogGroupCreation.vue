<template>
  <el-dialog
    :title="title"
    :visible.sync="canShowDialog"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="beforeClose"
  >
    <ErrorBanner
      :error.sync="error"
      :errorMessage.sync="errorMessage"
    ></ErrorBanner>
    <div class="height350">
      <el-form
        ref="newServiceCatalogGroup"
        :rules="rules"
        :model="serviceCatalogGroup"
      >
        <el-form-item
          :label="$t('common._common.name')"
          prop="name"
          class="mB10"
          :required="true"
        >
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="serviceCatalogGroup.name"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('common.wo_report.report_description')"
          prop="description"
          class="mB10"
        >
          <el-input
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4 }"
            :placeholder="$t('common._common.enter_desc')"
            v-model="serviceCatalogGroup.description"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialogBox" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        :loading="isSaving"
        type="primary"
        class="modal-btn-save"
        @click="addNewCatalogGroup()"
        >{{ $t('common._common.confirm') }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['canAddCatalogGroup', 'isEdit', 'serviceCatalogGroup'],
  components: {
    ErrorBanner,
  },
  data() {
    return {
      rules: {
        name: [
          {
            required: true,
            message: 'Please enter name',
            trigger: 'blur',
          },
        ],
      },
      error: '',
      errorMessage: '',
      isSaving: false,
    }
  },
  computed: {
    canShowDialog: {
      get() {
        return this.canAddCatalogGroup
      },
      set(value) {
        this.$emit('update:canAddCatalogGroup', value)
      },
    },
    title() {
      let { isEdit } = this
      let title = this.$t('servicecatalog.setup.group')
      return isEdit ? `Edit ${title}` : `New ${title}`
    },
  },
  methods: {
    setFormValid() {
      this.error = false
      this.errorMessage = ''
    },
    setFormInvalid(msg) {
      this.error = true
      this.errorMessage = msg
    },
    addNewCatalogGroup() {
      let { serviceCatalogGroup, $refs, isEdit } = this
      if (!isEmpty($refs['newServiceCatalogGroup'])) {
        this.setFormValid()
        this.$refs['newServiceCatalogGroup'].validate(valid => {
          if (valid) {
            let { name, description, id } = serviceCatalogGroup
            let sucessMsg = 'Service Category created successfully'
            let data = {
              serviceCatalogGroup: {
                name,
                description,
              },
            }
            if (isEdit) {
              data.serviceCatalogGroup.id = id
              sucessMsg = 'Service Category updated successfully'
            }
            let url = `v2/servicecataloggroup/addOrUpdate`
            this.$set(this, 'isSaving', true)
            let promise = this.$http
              .post(url, data)
              .then(({ data: { message, responseCode, result } }) => {
                if (responseCode === 0) {
                  let { serviceCatalogGroup } = result
                  this.$emit('updateCatalogGroup', serviceCatalogGroup)
                  this.closeDialogBox()
                  this.$message.success(sucessMsg)
                } else {
                  throw new Error(message)
                }
              })
              .catch(({ message }) => {
                this.setFormInvalid(message)
              })
            Promise.all([promise]).finally(() =>
              this.$set(this, 'isSaving', false)
            )
          }
        })
      }
    },
    closeDialogBox() {
      this.canShowDialog = false
    },
    beforeClose(done) {
      this.$set(this, 'error', null)
      this.$set(this, 'errorMessage', false)
      done()
    },
  },
}
</script>
