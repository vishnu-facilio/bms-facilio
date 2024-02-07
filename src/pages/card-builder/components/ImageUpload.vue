<template>
  <div>
    <el-upload
      v-if="$validation.isEmpty(value)"
      class="card-img-upload mR40"
      drag
      action=""
      accept="image/*"
      :show-file-list="false"
      :http-request="onUpload"
      :on-remove="onRemove"
      :on-success="onUploadSuccess"
    >
      <div class="d-flex flex-direction-column">
        <InlineSvg
          src="svgs/file-image-solid"
          class="d-flex justify-content-center folder-icon"
          iconClass="icon icon-xlg"
        ></InlineSvg>
        <div class="self-center">
          <p class="help-text">
            {{ $t('common.attachment_form.drag_drop_text') }}
            {{ $t('common.attachment_form.click_to_browse') }}
          </p>
        </div>
      </div>
    </el-upload>
    <div v-else class="inline line-height24">
      <el-image
        :src="previewUrl"
        fit="cover"
        class="fc-avatar-square card-image-preview"
      >
        <div slot="error" class="image-slot">
          <InlineSvg
            src="svgs/photo"
            iconClass="icon fill-grey icon-xxlll op5"
          ></InlineSvg>
        </div>
      </el-image>
      <a @click="onRemove" class="f12 block text-center">Remove</a>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { getBaseURL } from 'util/baseUrl'

export default {
  props: ['value'],
  data() {
    return {
      uploding: false,
    }
  },
  computed: {
    previewUrl() {
      let { value } = this
      return getBaseURL() + `/v2/files/preview/` + value
    },
  },
  methods: {
    async onUpload({ file, onError }) {
      this.uploding = true
      return API.uploadFiles([file])
    },
    onUploadSuccess(response) {
      let { ids, error } = response
      let [id] = ids
      this.$emit('input', id)

      this.uploding = false
    },
    onRemove() {
      // TODO delete file
      this.$emit('input', null)
    },
  },
}
</script>
<style lang="scss">
.card-img-upload {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1 1 45%;
  max-width: 250px;
  border-radius: 3px;
  border: dashed 1px #d0d9e2;
  background-color: #ffffff;
  padding: 20px;
  cursor: pointer;

  .folder-icon {
    padding: 10px;
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
    border: dashed 1px #9ed6dd;
    background-color: #f9feff;

    .folder-icon {
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
  }
}
.card-image-preview {
  height: 80px;
  width: 80px;
}
</style>
