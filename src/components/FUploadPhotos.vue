<template>
  <el-dialog
    title="Upload Photos"
    class="upload-photos-dialog fc-dialog-center-container"
    :visible.sync="showDialog"
    @before-close="closeDialog"
    :append-to-body="true"
  >
    <el-upload
      v-if="showDialog"
      multiple
      list-type="picture-card"
      :file-list="photos"
      action=""
      accept="image/*"
      :http-request="onUpload"
      :on-success="onUploadSuccess"
      :on-error="onError"
      :on-remove="onRemove"
    >
      <i class="el-icon-plus"></i>
    </el-upload>
    <span slot="footer" class="dialog-footer modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel"
        >Cancel</el-button
      >
      <el-button type="primary" @click="done" class="modal-btn-save"
        >Done</el-button
      >
    </span>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from './page/widget/utils/eventBus'
export default {
  props: ['module', 'record', 'list'],
  data() {
    return {
      photos: [],
      showDialog: false,
      isChanged: false,
    }
  },
  methods: {
    open() {
      this.photos = !isEmpty(this.list) ? this.list : []
      this.showDialog = true
    },
    onUpload({ file, onError }) {
      let data = {
        parentId: this.record.id,
        module: this.module,
      }

      let formData = new FormData()
      formData.append('file', file, file.name)
      for (let key in data) {
        this.$helpers.setFormData(key, data[key], formData)
      }

      return this.$http.post(`v2/photos/upload`, formData)
    },
    onUploadSuccess(response) {
      let {
        data: { responseCode, result },
      } = response

      if (responseCode === 0) {
        this.list.push(result.photos[0])
        this.isChanged = true
      }
    },
    onError() {
      this.$message.error(this.$t('setup.users_management.Invalid_image'))
    },
    onRemove(file) {
      this.$http
        .post(`/v2/photos/delete`, {
          id: file.id,
          photoId: file.photoId,
          module: this.module,
          parentId: this.record.id,
        })
        .then(response => {
          if (response.data.responseCode === 0) {
            let list = [...this.list]
            let index = list.findIndex(f => f.id === file.id)
            list.splice(index, 1)
            this.$emit('updatePhotos', list)
            this.isChanged = true
          }
        })
        .catch(() => {})
    },
    done() {
      this.showDialog = false
      this.photos = []
      if (this.isChanged) {
        eventBus.$emit('refreshDetails')
        this.isChanged = false
      }
    },
    closeDialog() {
      if (this.isChanged) {
        eventBus.$emit('refreshDetails')
        this.isChanged = false
      }
      this.showDialog = false
    },
  },
}
</script>
<style>
.upload-photos-dialog .el-dialog__body {
  max-height: 400px;
  overflow-y: scroll;
  padding-bottom: 50px;
}
</style>
