<template>
  <div v-if="currentFile" v-show="show" class="preview-file">
    <div class="top-toolbar">
      <div class="a-top-toolbar">
        <div class="file-name word-break-keep">
          {{ currentFile.fileName }}
        </div>
        <div class="top-toolbar-icon-right flex align-center">
          <template v-if="currentFile.contentType.includes('image')">
            <div class="flex-middle pL15">
              <div
                class="fc-white-14 word-break-keep pointer"
                @click="showOriginalPhoto"
                v-if="showOriginalImage"
              >
                {{ $t('common._common.view_original') }}
              </div>
              <div
                class="fc-white-14 word-break-keep pointer"
                @click="showCompressedPhoto"
                v-if="showCompressedImage"
              >
                {{ $t('common._common.view_default') }}
              </div>
            </div>
            <div
              @click="downloadAttachment(currentFile)"
              class="download-icon round flex-middle pL15"
            >
              <i class="el-icon-download op8" style="color: #ffff"></i>
            </div>
          </template>
          <template v-if="currentFile.contentType.includes('pdf')">
            <div
              @click="downloadAttachment(currentFile)"
              class="download-icon round flex-middle pL15"
            >
              <i class="el-icon-download op8" style="color: #ffff"></i>
            </div>
          </template>
          <div
            v-if="showDeleteOption()"
            class="flex-middle pL15"
            @click="deletePreview()"
          >
            <inline-svg
              src="svgs/delete"
              class="pointer edit-icon-color close vertical-text-top"
              iconClass="icon icon-md"
            ></inline-svg>
          </div>
          <div class="pL15">
            <i class="el-icon-close close" @click="closePreview"></i>
          </div>
        </div>
      </div>
    </div>

    <a
      class="el-icon-arrow-left prev"
      v-if="currentFileIndex > 0"
      style="font-size: 30px"
      @click="plusSlides(-1)"
    ></a>
    <a
      class="el-icon-arrow-right next"
      v-if="currentFileIndex < files.length - 1"
      style="font-size: 30px"
      @click="plusSlides(1)"
    ></a>

    <div class="preview-content">
      <template v-if="currentFile.contentType.includes('image')">
        <img
          v-if="compressedImage"
          :src="$prependBaseUrl(currentFile.previewUrl)"
          class="content"
          style="box-shadow: none"
        />
        <img
          v-else-if="originalImage"
          :src="$prependBaseUrl(`${currentFile.previewUrl}?fetchOriginal=true`)"
          class="content original"
          style="box-shadow: none"
        />
      </template>
      <div
        v-else-if="currentFile.contentType.includes('pdf')"
        class="width100 height-100"
      >
        <iframe
          :src="$prependBaseUrl(currentFile.previewUrl)"
          height="100%"
          width="100%"
          class="fc-preview-pdf"
        ></iframe>
      </div>
      <div
        v-else-if="isVideoSupported(currentFile.contentType)"
        class="width100 height-100 fc-preview-video"
      >
        <video controls>
          <source
            :src="
              $prependBaseUrl($getProperty(currentFile, 'downloadUrl', null))
            "
            :type="$getProperty(currentFile, 'contentType')"
          />
          {{ $t('common._common.browser_not_supported') }}
        </video>
      </div>

      <div class="no-preview" v-else>
        <div class="flex justify-center pT24">
          <inline-svg
            :src="getFileIcon(currentFile)"
            class="d-flex"
            iconClass="icon icon-60 overlay"
          >
          </inline-svg>
        </div>
        <div class="f14 pT12 pB12">No preview available</div>

        <div class="download-button">
          <a
            style="color: #ffff"
            class="flex flex-row"
            @click="downloadAttachment(currentFile)"
          >
            <div>Download</div>
          </a>
        </div>
      </div>
    </div>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none"
    ></iframe>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: [
    'previewFile',
    'files',
    'visibility',
    'showDelete',
    'isLockedState',
    'isTaskClosed',
  ],
  data() {
    return {
      show: true,
      previewUrl: null,
      currentFileIndex: 0,
      currentFile: null,
      imageMargin: null,
      exportDownloadUrl: null,
      originalImage: false,
      compressedImage: true,
      showOriginalImage: true,
      showCompressedImage: false,
    }
  },
  destroyed() {
    window.removeEventListener('keyup', this.handler)
  },
  mounted() {
    window.addEventListener('keyup', this.handler)
    this.preloadImages()
  },
  watch: {},
  computed: {},
  methods: {
    handler() {
      // If escape key is pressed...
      if (event.keyCode === 27) {
        this.closePreview()
      } else if (event.keyCode === 37) {
        this.plusSlides(-1)
      } else if (event.keyCode === 39) {
        this.plusSlides(1)
      }
    },
    preloadImages() {
      this.currentFile = this.previewFile ? this.previewFile : this.files[0]
      this.currentFileIndex = this.files.indexOf(this.currentFile)
    },
    plusSlides(n) {
      let index = this.currentFileIndex + n
      if (index < 0) {
        index = 0
      }
      if (index >= this.files.length) {
        index = this.files.length - 1
      }

      this.currentFileIndex = index
      this.currentFile = this.files[index]
    },
    closePreview() {
      this.$emit('update:visibility', false)
    },
    deletePreview() {
      let { currentFile } = this
      this.$emit('onDelete', currentFile)
    },
    downloadAttachment(file) {
      let { downloadUrl: url } = file || {}

      if (this.exportDownloadUrl) {
        this.exportDownloadUrl = null
      }

      this.$nextTick(() => {
        this.exportDownloadUrl = url
      })
    },
    showOriginalPhoto() {
      this.originalImage = true
      this.showCompressedImage = true
      this.showOriginalImage = false
      this.compressedImage = false
    },
    showCompressedPhoto() {
      this.showCompressedImage = false
      this.originalImage = false
      this.showOriginalImage = true
      this.compressedImage = true
    },
    getFileIcon(attachment) {
      let { contentType } = attachment
      let { FILE_TYPE_ICONS } = this.$constants

      let selectedIndex = FILE_TYPE_ICONS.findIndex(icons => {
        let { fileTypes } = icons
        return fileTypes.some(type => contentType.includes(type))
      })

      if (isEmpty(selectedIndex)) return FILE_TYPE_ICONS[0].path
      else return FILE_TYPE_ICONS[selectedIndex].path
    },
    isVideoSupported(contentType) {
      contentType = contentType || ''
      let supportedTypes = ['video/webm', 'video/mp4', 'video/ogg']
      return supportedTypes.includes(contentType.trim().toLowerCase())
    },
    showDeleteOption() {
      let { showDelete, isLockedState, isTaskClosed } = this
      return showDelete && !isLockedState && !isTaskClosed
    },
  },
}
</script>
<style>
.preview-file {
  position: fixed;
  z-index: 900;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
}
.preview-content {
  background: transparent;
  box-shadow: none;
  top: 0;
  height: 100%;
  align-items: center;
  justify-content: center;
  display: flex;
  padding-bottom: 20px;
}
.preview-content img {
  max-width: 80%;
  max-height: 500px;
  height: 100%;
  /* display: flex;
  text-align: center;
  margin: 0 auto; */
  margin-top: -50px;
}
.close {
  color: #f1f1f1;
  font-size: 28px;
  font-weight: lighter;
  transition: 0.3s;
  cursor: pointer;
  opacity: 0.8;
  font-size: 25px;
}
.close:hover {
  opacity: 1;
}
.round {
  border-radius: 50%;
}
.prev {
  cursor: pointer;
  position: absolute;
  top: 50%;
  width: auto;
  padding: 8px;
  margin-top: -22px;
  color: white;
  font-weight: bold;
  font-size: 18px;
  transition: 0.6s ease;
  border-radius: 50%;
  user-select: none;
  z-index: 12345;
}
.next {
  cursor: pointer;
  position: absolute;
  top: 50%;
  width: auto;
  padding: 8px;
  margin-top: -22px;
  color: white;
  font-weight: bold;
  font-size: 18px;
  transition: 0.6s ease;
  border-radius: 50%;
  user-select: none;
  z-index: 12345;
}
.prev:hover {
  background-color: rgba(255, 255, 255, 0.5);
  color: black;
}
.next:hover {
  background-color: rgba(255, 255, 255, 0.5);
  color: black;
}
.next {
  right: 0;
  border-radius: 50%;
}
.content {
  vertical-align: middle;
}
.no-preview {
  height: 200px;
  width: 330px;
  position: inherit;
  text-align: center;
  color: white;
  font-size: 22px;
  border-radius: 5px;
  margin-bottom: 100px;
  background-color: #4c494c;
}
.download-button {
  background-color: #39b2c2;
  color: #fff;
  display: inline-block;
  font-size: 14px;
  font-weight: 400;
  text-align: center;
  line-height: 28px;
  margin-top: 16px;
  min-width: 54px;
  padding: 3px 40px;
  vertical-align: middle;
  white-space: nowrap;
  border-radius: 3%;
  cursor: pointer;
}
.file-name {
  box-sizing: border-box;
  color: #fff;
  padding-right: 10px;
  font-size: 19px;
  letter-spacing: 0.00625em;
}
.download-icon {
  color: #f1f1f1;
  font-size: 25px;
  cursor: pointer;
}
.download-hover {
  opacity: 0.8;
  font-size: 20px;
}
.download-hover:hover {
  opacity: 1;
  font-size: 20px;
}
.a-model-content {
  display: inline-block;
  margin: 0 auto;
  max-height: 100%;
  max-width: 100%;
  position: relative;
  vertical-align: middle;
}
.a-top-toolbar {
  width: 100%;
  padding: 12px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.fc-preview-pdf {
  width: 100%;
  height: 100%;
  background: #fff;
  margin: 0 auto;
  border: none;
}
.fc-preview-video {
  display: flex;
  align-items: center;
}
.fc-preview-video video {
  width: 90%;
  margin-left: auto;
  margin-right: auto;
  margin-top: -50px;
  max-height: 90%;
}
</style>
