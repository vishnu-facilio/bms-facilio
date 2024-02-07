<template>
  <div class="signature-container">
    <div v-if="!imgUrl" class="btn-container">
      <el-dropdown v-if="canShowColorPalette">
        <div class="el-dropdown-link color-palette" placement="bottom-start">
          <InlineSvg
            src="svgs/painter-palette"
            class="vertical-middle pointer d-flex"
            :iconClass="getIconClassName()"
          ></InlineSvg>
        </div>
        <el-dropdown-menu slot="dropdown" class="color-palette-dropdown">
          <div
            v-for="(color, index) in colorsArr"
            :key="index"
            class="color-item"
            :class="getClassName(color)"
            @click="changePenColor(color)"
          ></div>
        </el-dropdown-menu>
      </el-dropdown>
      <div
        v-if="canShowRestore"
        class="restore-btn pR5 self-center"
        @click="restoreSignature"
      >
        {{ restoreBtnTxt }}
      </div>
      <div class="clear-btn self-center" @click="clearSignature">
        {{ clearBtnTxt }}
      </div>
    </div>
    <div v-if="disabled" class="signature-disabled"></div>
    <div v-else-if="imgUrl" class="preview-container">
      <div class="remove-sign" @click="clearSignature">
        <inline-svg
          src="svgs/delete"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
      </div>
      <img :src="imgUrl" class="width100 height-100" />
    </div>
    <VueSignaturePad
      v-else-if="!isLoading"
      height="100px"
      :ref="refKey"
      :saveType="`image/${extension}`"
      :customStyle="styleObj"
      :options="{
        backgroundColor: `transparent`,
        penColor: `#${penColor}`,
        onEnd,
      }"
    ></VueSignaturePad>
    <div v-if="isUploading" class="mT10">
      {{ $t('common._common.uploading') }}
    </div>
  </div>
</template>
<script>
import VueSignaturePad from 'vue-signature-pad'
import { isEmpty } from '@facilio/utils/validation'
import { v4 as uuid } from 'uuid'
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'

const colorsArr = ['000000', '332AC7', 'EA5151', '1A925E', '46D0E1', 'C848A3']

export default {
  props: {
    model: {
      default: () => {},
    },
    customStyle: {
      type: Object,
    },
    disabled: {
      type: Boolean,
      default: () => false,
    },
    fileName: {
      type: String,
      default: () => 'signature',
    },
    isV3Api: {
      type: Boolean,
      default: () => false,
    },
    imgUrl: {
      default: () => null,
    },
    restoreBtnTxt: {
      default: () => 'restore',
    },
    clearBtnTxt: {
      default: () => 'clear',
    },
    canShowColorPalette: {
      type: Boolean,
    },
  },
  components: {
    VueSignaturePad,
  },
  data() {
    return {
      styleObj: {
        border: 'dashed 1px #e3e2e5',
      },
      isLoading: false,
      isUploading: false,
      initialImgUrl: null,
      initialImgValue: null,
      canShowRestore: false,
      refKey: '',
      penColor: '000',
      colorsArr: [],
      extension: 'png',
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      let { customStyle, styleObj, imgUrl, model } = this
      this.colorsArr = colorsArr
      this.initialImgUrl = imgUrl
      this.initialImgValue = model
      this.isLoading = true
      this.refKey = uuid()
      this.isLoading = false
      if (!isEmpty(customStyle)) {
        this.styleObj = {
          ...styleObj,
          ...customStyle,
        }
      }
    },
    getClassName(code = '') {
      return `label-${code}`
    },
    getIconClassName() {
      let { penColor } = this
      let className = this.getClassName(penColor)
      return `color-palette-icon ${className} icon icon-xs`
    },
    getSignatureInstance() {
      let { $refs, refKey } = this
      if (!isEmpty(refKey) && !isEmpty($refs[refKey])) {
        let instance = $refs[refKey]
        return instance
      }
      return null
    },

    clearSignature() {
      let { imgUrl } = this
      let signatureInstance = this.getSignatureInstance()
      if (imgUrl) {
        this.$emit('update:imgUrl', null)
        this.canShowRestore = true
      } else if (!isEmpty(signatureInstance)) {
        signatureInstance.clearSignature()
      }
      this.$emit('update:model', null)
      this.$emit('change', null)
    },
    restoreSignature() {
      let { initialImgUrl, initialImgValue } = this
      this.$emit('update:model', initialImgValue)
      this.$emit('change', initialImgValue)
      this.$emit('update:imgUrl', initialImgUrl)
      this.canShowRestore = false
    },
    onEnd: debounce(async function() {
      let { fileName, extension } = this
      fileName = fileName + `.${extension}`
      let signatureInstance = this.getSignatureInstance()
      if (!isEmpty(signatureInstance)) {
        let { isEmpty: isSignEmpty, data } = signatureInstance.saveSignature()
        let fileObj = this.$helpers.dataURLtoFile(data, fileName)
        let fileOrId = null

        if (!isSignEmpty) {
          fileOrId = await this.getFileId(fileObj)
        }
        this.$emit('update:model', fileOrId)
        this.$emit('change', fileOrId)
      }
    }, 400),
    async getFileId(file) {
      let fileId

      this.$emit('update:isSaveBtnDisabled', true)
      this.isUploading = true
      let { ids } = await API.uploadFiles([file])
      fileId = isEmpty(ids) ? null : ids[0]
      this.isUploading = false
      this.$emit('update:isSaveBtnDisabled', false)

      return fileId
    },
    changePenColor(color) {
      this.penColor = color
    },
  },
}
</script>
<style lang="scss">
.signature-container {
  position: relative;
  background-color: #fff;
  .preview-container {
    width: 150px;
    height: 150px;
    border: solid 1px #d0d9e2;
    position: relative;
    .remove-sign {
      position: absolute;
      right: 0;
      bottom: 0;
      padding: 5px;
      margin: 5px;
      cursor: pointer;
      background: #324056;
      border-radius: 50%;
      .icon {
        fill: #fff;
      }
    }
  }
  .signature-disabled {
    height: 100px;
    border: dashed 1px #e3e2e5;
    cursor: not-allowed;
  }
  .btn-container {
    display: flex;
    position: absolute;
    text-transform: uppercase;
    font-size: 11px;
    font-weight: 500;
    color: #727272;
    cursor: pointer;
    right: 15px;
    top: 10px;
    line-height: normal;
    .el-dropdown {
      + .restore-btn {
        border-left: 1px solid #727272;
        padding-left: 5px;
        margin-left: 5px;
      }
    }
    .restore-btn {
      border-right: 1px solid #727272;
    }
    .clear-btn {
      padding-left: 5px;
    }
  }
  .color-palette {
    width: 13px;
    height: 13px;
    border-radius: 50%;
    cursor: pointer;
  }
}
.color-palette-dropdown {
  display: flex;
  flex-wrap: wrap;
  max-width: 90px;
  padding-left: 10px;
  .color-item {
    height: 15px;
    width: 15px;
    flex: 0 0 15px;
    margin: 0px 10px 0px 0px;
    border-radius: 50%;
    cursor: pointer;
    &:nth-child(-n + 3) {
      margin-bottom: 10px;
    }
  }
}
.color-palette-dropdown .color-item,
.color-palette-icon {
  &.label-000000 {
    &:not(.icon) {
      background: #000000;
    }
    &.icon {
      fill: #000000;
    }
  }
  &.label-332AC7 {
    &:not(.icon) {
      background: #332ac7;
    }
    &.icon {
      fill: #332ac7;
    }
  }
  &.label-EA5151 {
    &:not(.icon) {
      background: #ea5151;
    }
    &.icon {
      fill: #ea5151;
    }
  }
  &.label-1A925E {
    &:not(.icon) {
      background: #1a925e;
    }
    &.icon {
      fill: #1a925e;
    }
  }
  &.label-46D0E1 {
    &:not(.icon) {
      background: #46d0e1;
    }
    &.icon {
      fill: #46d0e1;
    }
  }
  &.label-C848A3 {
    &:not(.icon) {
      background: #c848a3;
    }
    &.icon {
      fill: #c848a3;
    }
  }
}
</style>
