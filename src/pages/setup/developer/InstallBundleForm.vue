<template>
  <el-dialog
    :title="
      isNew
        ? $t('setup.create.install_bundle')
        : $t('setup.create.install_edit_bundle')
    "
    :visible="true"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height300">
      <form
        enctype="multipart/form-data"
        novalidate
        class="upload-btn-wrapper"
        ref="uploadForm"
      >
        <div :class="{ uploadActiveClass: file }">
          <div class="upload-empty-txt">
            <input
              type="file"
              id="file"
              ref="file"
              @change="handleFileUpload()"
              accept=".zip"
              class="upload-input"
            />
            <div class="upload-img">
              <img
                src="~assets/svgs/file.svg"
                width="50"
                height="50"
                class="object-contain"
              />
            </div>
            <div class="upload-txt">
              <div>
                {{ $t('setup.setupLabel.upload_txt') }}
              </div>
              <div>
                {{ $t('setup.setupLabel.or_click_browse') }}
              </div>
            </div>
          </div>
        </div>
        <div class="flex-middle justify-content-space" v-if="file.name">
          <div class="fc-black-14 bold">
            {{ file.name }}
          </div>
          <!-- <div @click="resetForm()" class="pointer">
            <i class="el-icon-close z-50 fc-black-14"></i>
          </div> -->
        </div>
      </form>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>
        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="submitFile('file', $event.target.files)"
        >
          {{ $t('setup.setupLabel.upload') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import BaseAttachment from 'components/relatedlist/Attachments.vue'
export default {
  extends: BaseAttachment,
  props: ['isNew', 'bundleData'],
  data() {
    return {
      saving: false,
      file: '',
    }
  },
  created() {
    if (!this.isNew) {
      this.bundle = this.bundleData
    }
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    async submitFile() {
      this.saving = true
      let formData = new FormData()
      formData.append('bundleZip', this.file)
      formData.append(
        'bundleZipName',
        this.$refs.file && this.$refs.file.files[0].name
      )
      let url = 'v3/bundle/installBundle'
      let { error } = await API.post(url, formData)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.$message.success(
          this.$t('setup.setupLabel.install_bundle_sucessfully')
        )
      }
      this.closeDialog()
      this.saving = false
    },
    handleFileUpload() {
      if (this.$refs.file.files[0]) {
        this.file = this.$refs.file.files[0]
      }
    },
    resetForm() {
      this.$refs.uploadForm.reset()
    },
  },
}
</script>
<style lang="scss">
.upload-btn-wrapper {
  width: 100%;
  height: 200px;
  position: relative;
  input {
    height: 200px;
    width: 100%;
  }
}
.upload-input {
  position: absolute;
  top: 0;
  right: 0;
  margin: 0;
  padding: 0;
  font-size: 20px;
  cursor: pointer;
  opacity: 0;
  cursor: pointer;
  font-size: 10px;
  left: 0;
  z-index: 1;
}
.upload-txt {
  position: absolute;
  bottom: 50px;
  left: 22%;
  font-size: 14px;
  font-weight: normal;
  line-height: 1.14;
  letter-spacing: 0.32px;
  text-align: center;
  color: #324056;
  line-height: 20px;
}
.upload-img {
  position: absolute;
  top: 50px;
  left: 40%;
}
.file-upload-wrapper::after {
  content: attr(data-text);
  font-size: 18px;
  position: absolute;
  top: 0;
  left: 0;
  background: #fff;
  padding: 10px 15px;
  display: block;
  width: calc(100% - 40px);
  pointer-events: none;
  z-index: 20;
  height: 40px;
  line-height: 40px;
  color: #999;
  border-radius: 5px 10px 10px 5px;
  font-weight: 300;
}
.upload-empty-txt {
  width: 100%;
  height: 200px;
  background-image: url("data:image/svg+xml,%3csvg width='100%25' height='100%25' xmlns='http://www.w3.org/2000/svg'%3e%3crect width='100%25' height='100%25' fill='none' stroke='%23D0D9E2FF' stroke-width='2' stroke-dasharray='2%2c 10' stroke-dashoffset='0' stroke-linecap='square'/%3e%3c/svg%3e");
}
.uploadActiveClass {
  display: none;
}
</style>
