<template>
  <div>
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <template v-else>
      <div class="header-label pB15">
        {{ $t('common.wo_report.before') }}
      </div>

      <!-- before image section -->
      <div class="flex-middle">
        <!-- show uploaded image -->
        <div v-if="beforeImage">
          <img
            class="custom-file-upload"
            width="100"
            style="padding: 0px;max-height : 80px"
            :src="beforeImage"
            @click="imgaeBefore(1)"
          />
        </div>
        <!-- show placeholder image -->
        <div v-else class="inputover">
          <el-tooltip
            effect="dark"
            :content="$t('common._common.you_dont_have_attachment_permission')"
            placement="right"
            :disabled="!disabled"
          >
            <label
              for="file-upload-before"
              class="custom-upload"
              :class="isNotAllowed"
            >
              <i class="fa fa-camera" />
            </label>
          </el-tooltip>
          <input
            id="file-upload-before"
            name="before"
            type="file"
            accept="image/*"
            @change="uploadBeforeImage($event.target)"
            :disabled="disabled"
          />
        </div>
        <!-- image timestamp -->
        <div v-if="beforeImage && beforeImageDetail.createdTime" class="mL20">
          <div class="f11 text-fc-pink bold">
            {{ $t('common._common.uploaded_time') }}
          </div>
          <div class="mT6 fL fc-italic f11">
            {{ beforeImageDetail.createdTime | formatDate() }}
          </div>
        </div>
      </div>

      <!-- after image  section -->
      <div class="header-label pB15 mT20" style="padding-left: 0px">
        {{ $t('common.wo_report.after') }}
      </div>
      <div class="flex-middle">
        <!-- show uploaded image -->
        <div v-if="afterImage">
          <img
            class="custom-file-upload"
            width="100"
            style="padding: 0px;max-height : 80px"
            :src="afterImage"
            @click="imgaeBefore(2)"
          />
        </div>
        <!-- show placeholder image -->
        <div v-else class="inputover">
          <el-tooltip
            effect="dark"
            :content="$t('common._common.you_dont_have_attachment_permission')"
            placement="right"
            :disabled="!disabled"
          >
            <label
              for="file-upload-after"
              class="custom-upload"
              :class="isNotAllowed"
            >
              <i class="fa fa-camera" />
            </label>
          </el-tooltip>
          <input
            id="file-upload-after"
            name="after"
            type="file"
            accept="image/*"
            @change="uploadAfterImage($event.target)"
            :disabled="disabled"
          />
        </div>
        <!-- image timestamp -->
        <div v-if="afterImage && afterImageDetail.createdTime" class="mL20">
          <div class="f11 text-fc-pink bold">
            {{ $t('common._common.uploaded_time') }}
          </div>
          <div class="mT6 fL fc-italic f11">
            {{ afterImageDetail.createdTime | formatDate() }}
          </div>
        </div>
      </div>
    </template>
    <preview-file
      :visibility.sync="visibility"
      v-if="visibility && selectedAttachment"
      showDelete="true"
      :isLockedState="isLockedState"
      :isTaskClosed="isTaskClosed"
      :previewFile="selectedAttachment"
      :files="attachments"
      @onDelete="taskPhotoDletion"
    ></preview-file>
  </div>
</template>
<script>
import MobileAttachment from '@/MobileAttachment2'

export default {
  props: ['isLockedState', 'isTaskClosed'],
  extends: MobileAttachment,
  computed: {
    isNotAllowed() {
      let { disabled } = this
      return disabled ? 'cursor-not-allowed' : 'pointer'
    },
  },
  methods: {
    uploadAfterImage(event) {
      this.filesChange(event.files, 2)
    },
    uploadBeforeImage(event) {
      this.filesChange(event.files, 1)
    },
  },
}
</script>
<style scoped lang="scss">
.custom-upload {
  border-radius: 5px;
  background-color: #f2f2f2;
  display: inline-block;
  padding: 24px;
  border: 1px solid #f2f2f2;
  margin-top: 5px;
}
</style>
