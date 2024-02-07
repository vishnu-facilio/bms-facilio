<template>
  <div class="f-img-container">
    <div v-if="imgUrl && !isFileType" class="photo-container">
      <img :src="imgUrl" class="f-img" />
      <div
        v-if="!isDisabled && !isUploading"
        class="f-img-delete"
        @click="removeFile(field)"
      >
        <inline-svg
          src="svgs/delete"
          class="f-delete"
          iconClass="icon icon-sm"
        ></inline-svg>
      </div>
    </div>
    <div v-else-if="isFileType && fileObject" class="fc-attachments">
      <div class="fc-attachment-row d-flex">
        <div class="attached-files">
          <div class="mB10">{{ fileObject.name }}</div>
          <div
            v-if="!$validation.isEmpty(fileObject.size) && !preview"
            class="attached-file-size"
          >
            {{ fileObject.size | prettyBytes }}
          </div>
        </div>
        <div class="d-flex mL-auto">
          <spinner v-if="isUploading" :show="isUploading" size="30"></spinner>
          <i
            v-if="!isDisabled && !isUploading"
            class="el-icon-delete pointer attachment-delete"
            @click="removeFile(field)"
          ></i>
        </div>
      </div>
    </div>
    <div v-else class="f-img-empty">
      <div
        v-if="showWebCamPhoto && !isFileType"
        class="f-add-photo"
        @click="openPhoto"
      >
        <inline-svg
          src="svgs/camera"
          class="camera-icon self-center"
          iconClass="icon icon-xxl"
        ></inline-svg>
        <div class="self-center">
          <p class="help-text">{{ $t('common.attachment_form.add_photo') }}</p>
        </div>
      </div>
      <div
        v-if="showWebCamPhoto && !isFileType"
        class="f-img-separator justify-center d-flex"
      >
        <span class="self-center">{{ $t('common.text.or') }}</span>
      </div>
      <el-upload
        class="f-img-upload"
        :action="''"
        :show-file-list="false"
        :http-request="event => uploadFile(field, event)"
        :on-remove="removeFile"
        :before-upload="beforeUpload"
        :disabled="isDisabled"
        drag
      >
        <slot v-if="isPlaceHolderAvailable" name="placeHolderSlot"></slot>
        <div v-else class="d-flex flex-direction-column">
          <inline-svg
            src="svgs/folder"
            class="folder-icon self-center"
            iconClass="icon icon-xxl"
          ></inline-svg>
          <div class="self-center">
            <p class="help-text">
              {{ $t('common.attachment_form.drag_drop_text') }}
              {{ $t('common.attachment_form.click_to_browse') }}
            </p>
          </div>
        </div>
      </el-upload>
    </div>
    <div v-if="isUploading && !isFileType" class="mT10">
      {{ $t('common._common.uploading') }}
    </div>
    <div v-if="showPhotoContainer">
      <el-dialog
        class="f-img-webcam"
        :visible.sync="showPhotoContainer"
        width="650px"
        :append-to-body="true"
        :show-close="false"
        :before-close="closeDialogBox"
      >
        <div class="height400">
          <video ref="player" autoplay></video>
          <canvas
            ref="canvas"
            width="320"
            height="240"
            style="display: none"
          ></canvas>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialogBox" class="modal-btn-cancel"
            >Cancel</el-button
          >
          <el-button type="primary" class="modal-btn-save" @click="captureImg()"
            >Save Photo</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { isEmpty, isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import { v4 as uuid } from 'uuid'

export default {
  name: 'FFileUpload',
  components: {
    Spinner,
  },
  props: [
    'field',
    'showWebCamPhoto',
    'isDisabled',
    'imgContentUrl',
    'isFileType',
    'fileObj',
    'additionalFileFormat',
    'isV3Api',
    'isSaveBtnDisabled',
    'addImgFile',
    'preview',
  ],
  data() {
    return {
      showPhotoContainer: false,
      authorizeImageFormats: ['image/jpeg', 'image/png', 'image/jpg'],
      isUploading: false,
      skipDefault: false,
    }
  },
  computed: {
    imgUrl: {
      get() {
        return this.imgContentUrl
      },
      set(value) {
        this.$emit('update:imgContentUrl', value)
      },
    },
    fileObject: {
      get() {
        let { fileObj, defaultFileObject, skipDefault } = this
        if (!isEmpty(defaultFileObject) && !skipDefault) {
          return defaultFileObject
        } else return fileObj
      },
      set(value) {
        this.$emit('update:fileObj', value)
      },
    },
    defaultFileObject() {
      let { field } = this
      let { value } = field || {}

      if (!isEmpty(value)) {
        if (!isArray(value)) {
          try {
            value = JSON.parse(value)
          } catch (e) {
            this.$message.warning(this.$t('common._common.error_parsing_value'))
          }
        }
        let defaultFileObj = value[0] || {}
        let { fileName: name } = defaultFileObj || {}
        if (!isEmpty(name)) return { name }
        return null
      }
      return null
    },
    isPlaceHolderAvailable() {
      let { $slots } = this
      return !isEmpty($slots.placeHolderSlot)
    },
  },
  methods: {
    beforeUpload(file) {
      let { authorizeImageFormats, isFileType, additionalFileFormat } = this
      if (!isFileType) {
        if (!isEmpty(additionalFileFormat)) {
          authorizeImageFormats = authorizeImageFormats.concat(
            additionalFileFormat
          )
        }
        const isFileTypeAccessible = authorizeImageFormats.includes(file.type)
        if (!isFileTypeAccessible) {
          this.$message.error(
            'Picture must be under any of these (jpeg/png/jpg) format!'
          )
        }
        return isFileTypeAccessible
      }
      return true
    },
    openPhoto() {
      if (this.hasGetUserMedia()) {
        this.showPhotoContainer = true
        this.$nextTick(() => {
          let player = this.$refs.player
          const constraints = {
            video: { width: { exact: 600 }, height: { exact: 350 } },
          }
          navigator.mediaDevices.getUserMedia(constraints).then(stream => {
            player.srcObject = stream
          })
        })
      } else {
        let errMsg = this.$t('common.errorMsg.no_browser_support')
        this.$message.error(errMsg)
      }
      this.$set(this, 'showPhotoContainer', true)
    },
    hasGetUserMedia() {
      return !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia)
    },
    removeFile(field) {
      let { value } = field || {}
      this.imgUrl = null
      this.fileObject = null
      if (!isEmpty(value)) this.skipDefault = true
      this.$emit('removeImgFile', field)
    },
    async uploadFile(field, event) {
      let { file } = event
      let { isFileType } = this
      let fileReader = new FileReader()
      if (isFileType) {
        this.fileObject = file
      } else {
        fileReader.addEventListener('load', event => {
          this.imgUrl = event.target.result
        })
      }
      fileReader.readAsDataURL(file)

      let fileOrId = await this.getFileId(file)
      this.addImgFile(fileOrId, field)
    },
    closeDialogBox() {
      let player = this.$refs.player
      if (player.srcObject) {
        player.srcObject.getVideoTracks().forEach(track => track.stop())
      }
      this.showPhotoContainer = false
    },
    captureImg() {
      let { field } = this
      let canvasElement = this.$refs.canvas
      let player = this.$refs.player
      let canvasContext = canvasElement.getContext('2d')
      canvasContext.drawImage(
        player,
        0,
        0,
        canvasElement.width,
        canvasElement.height
      )
      this.$nextTick(() => {
        let dataUrl = canvasElement.toDataURL('image/png')
        this.imgUrl = dataUrl
        fetch(dataUrl)
          .then(res => res.blob())
          .then(async blob => {
            blob.name = blob.name || `file ${uuid()}`
            let fileOrId = await this.getFileId(blob)
            this.addImgFile(fileOrId, field)
          })
        player.srcObject.getVideoTracks().forEach(track => track.stop())
        this.showPhotoContainer = false
      })
    },
    async getFileId(file) {
      let fileId

      this.$emit('update:isSaveBtnDisabled', true)
      this.isUploading = true
      let { ids, error } = await API.uploadFiles([file])
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        fileId = isEmpty(ids) ? null : ids[0]
      }
      this.isUploading = false
      this.$emit('update:isSaveBtnDisabled', false)

      return fileId
    },
  },
}
</script>
<style lang="scss">
.f-img-webcam {
  .el-dialog__body {
    padding: 0px 30px 10px 30px;
  }
}
.f-img-container {
  .photo-container {
    display: flex;
    width: fit-content;
    .f-img-delete {
      position: absolute;
      bottom: 8px;
      left: 120px;
      padding: 4px;
      display: flex;
      background: #324056;
      border-radius: 50%;
      display: none;
      .f-delete {
        display: flex;
        cursor: pointer;
        .icon {
          fill: #fff;
        }
      }
    }
    .f-img {
      width: 150px;
      height: 150px;
      border: solid 1px #d0d9e2;
    }
    &:hover {
      .f-img-delete {
        display: block;
      }
    }
  }
  .f-img-empty {
    display: flex;
    .f-img-separator {
      flex: 1 1 10%;
      color: #324056;
    }
    .f-add-photo,
    .f-img-upload {
      display: flex;
      flex-direction: column;
      justify-content: center;
      flex: 1 1 45%;
      border-radius: 3px;
      border: dashed 1px #d0d9e2;
      background-color: #ffffff;
      padding: 20px;
      cursor: pointer;
      .camera-icon,
      .folder-icon {
        padding: 10px;
        display: flex;
        border: 1px solid #d0d9e2;
        border-radius: 50%;
        margin-bottom: 5px;
        .icon {
          fill: #8ca1ad;
        }
      }
      .el-upload-dragger {
        border: none;
        width: auto;
        height: auto;
      }
      .help-text {
        line-height: 15px;
        font-size: 10px;
        text-align: center;
        margin: 0;
      }
      &:hover {
        .camera-icon,
        .folder-icon {
          border: 1px solid #9ed6dd;
          .icon {
            fill: #39b2c2;
          }
        }
        .help-text {
          color: #39b2c2;
        }
        .el-upload-dragger {
          background-color: #f9feff;
        }
        border: dashed 1px #9ed6dd;
        background-color: #f9feff;
      }
    }
  }
}
</style>
