<template>
  <el-dialog
    :title="title"
    :visible="true"
    width="35%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height200">
      <el-form ref="folderCreationForm" :rules="rules" :model="folderObj">
        <el-form-item label="Folder Name" prop="name" :required="true">
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="folderObj.name"
            type="text"
            :placeholder="$t('common.dashboard.enter_the_folder_name')"
          />
        </el-form-item>
      </el-form>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>

        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="saveFolderInfo"
        >
          {{ $t('common._common._save') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapActions } from 'vuex'

export default {
  props: ['groupDetail', 'moduleName', 'appId'],
  data() {
    return {
      folderObj: { name: null },
      saving: false,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
      },
    }
  },
  created() {
    this.init()
  },
  computed: {
    title() {
      let { groupDetail } = this
      return !isEmpty(groupDetail)
        ? this.$t('viewsmanager.folder.edit_folder')
        : this.$t('viewsmanager.folder.add_new_folder')
    },
  },
  methods: {
    ...mapActions({
      addFolder: 'view/addFolder',
      updateFolder: 'view/updateFolder',
    }),
    init() {
      let { groupDetail } = this
      if (!isEmpty(groupDetail)) {
        let { displayName } = groupDetail
        this.folderObj.name = displayName
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
    async saveFolderInfo() {
      let validate = await this.$refs['folderCreationForm'].validate()
      if (!validate) return

      let { groupDetail, folderObj, moduleName, appId } = this
      let { id, name, moduleId, sequenceNumber } = groupDetail || {}
      let params
      if (isEmpty(groupDetail)) {
        params = {
          viewGroup: {
            displayName: folderObj.name,
            appId,
          },
          moduleName,
        }
        this.saving = true
        let { error, data } = await this.addFolder(params)
        if (error) {
          this.$message.error(
            error.message || this.$t('viewsmanager.folder.error_creation')
          )
        } else {
          this.$message.success(this.$t('viewsmanager.folder.success_creation'))

          this.$emit('onSave', data.viewGroup)
          this.closeDialog()
        }
        this.saving = false
      } else {
        let params
        params = {
          viewGroup: {
            displayName: folderObj.name,
            id,
            name,
            moduleId,
            sequenceNumber,
            appId,
          },
        }
        this.saving = true
        let { error } = await this.updateFolder(params)

        if (error) {
          this.$message.error(
            error.message || this.$t('viewsmanager.folder.error_creation')
          )
        } else {
          if (isEmpty(groupDetail) && !isEmpty(appId)) {
            this.$emit('onSave', appId)
          } else {
            this.$emit('onSave')
          }
          this.$message.success(this.$t('viewsmanager.folder.edit_creation'))
        }
        this.closeDialog()
        this.saving = false
      }
    },
  },
}
</script>

<style>
.folder-dialog-content {
  padding: 15px 25px 120px;
}
</style>
