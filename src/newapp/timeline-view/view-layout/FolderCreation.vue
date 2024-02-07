<script>
import FolderCreation from 'src/newapp/viewmanager/FolderCreation.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: FolderCreation,
  methods: {
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
            groupType: 2,
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
          this.canShowDialog = false
        }
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
          this.canShowDialog = false
          if (isEmpty(groupDetail) && !isEmpty(appId)) {
            this.$emit('onSave', appId)
          } else {
            this.$emit('onSave')
          }
          this.$message.success(this.$t('viewsmanager.folder.edit_creation'))
          this.closeDialog()
        }
      }
      this.saving = false
    },
  },
}
</script>
