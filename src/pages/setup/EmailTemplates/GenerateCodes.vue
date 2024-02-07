<template>
  <el-dialog
    class="fc-dialog-center-container fc-dialog-center-body-p0 fc-html-container"
    :append-to-body="true"
    title="Generator"
    :visible="true"
    width="85%"
    :before-close="closeDialog"
  >
    <div class="height100 pB100">
      <el-tabs
        v-model="activeName"
        @tab-click="handleClick"
        class="fc-generator-tabs"
      >
        <el-tab-pane label="Button" name="button">
          <el-row class="mB20 flex-middle">
            <el-col :span="4">URL:</el-col>
            <el-col :span="10">
              <el-input
                v-model="urlInput"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">Text:</el-col>
            <el-col :span="10">
              <el-input
                v-model="urlInputText"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">Title:</el-col>
            <el-col :span="10">
              <el-input
                v-model="urlInputTitle"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">Background Color:</el-col>
            <el-col :span="10">
              <el-color-picker
                v-model="urlBgPicker"
                class="editor-color-picker"
              ></el-color-picker>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">Font Color:</el-col>
            <el-col :span="10">
              <el-color-picker
                v-model="urlFontColorPicker"
                class="editor-color-picker"
              ></el-color-picker>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">Padding:</el-col>
            <el-col :span="10">
              <el-input
                v-model="codePadding"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">Border radius:</el-col>
            <el-col :span="10">
              <el-input
                v-model="codeBorderRadius"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">Font Weight:</el-col>
            <el-col :span="10">
              <el-input
                v-model="codeFontWeight"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <div class="fwBold">
            Output:
          </div>
          <div class="mT20">
            <a
              target="_blank"
              :href="urlInputText"
              :title="urlInputTitle"
              :style="{
                color: urlFontColorPicker,
                background: urlBgPicker,
                padding: codePadding,
                borderRadius: codeBorderRadius,
                fontWeight: codeFontWeight,
              }"
              >{{ urlInputText }}</a
            >
            <span
              class="pL10 pointer fc-italic fc-blue-txt4-12 text-left"
              @click="copyBtnCode"
              >Copy the code</span
            >
          </div>
        </el-tab-pane>
        <el-tab-pane label="image" name="img">
          <el-row class="mB20 flex-middle">
            <el-col :span="4">
              Source
            </el-col>
            <el-col :span="10">
              <el-input
                v-model="imgSource"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">
              Width
            </el-col>
            <el-col :span="10">
              <el-input
                v-model="imgWidth"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">
              Height
            </el-col>
            <el-col :span="10">
              <el-input
                v-model="imgHeight"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>

          <div class="fwBold">
            Output:
          </div>

          <div class="mT20">
            <img
              :src="imgSource"
              :style="{ width: imgWidth, height: imgHeight }"
            />
            <div
              class="pointer fc-italic fc-blue-txt4-12 text-left mT15"
              @click="copyImgCode"
            >
              Copy the code
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Link" name="link">
          <el-row class="mB20 flex-middle">
            <el-col :span="4">
              URL:
            </el-col>
            <el-col :span="10">
              <el-input
                v-model="linkUrl"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">
              Text:
            </el-col>
            <el-col :span="10">
              <el-input
                v-model="linkText"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20 flex-middle">
            <el-col :span="4">
              Title:
            </el-col>
            <el-col :span="10">
              <el-input
                v-model="linkTitle"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
          <div class="fwBold">
            Output:
          </div>
          <div class="mT20">
            <a target="_blank" :href="linkUrl" :title="linkTitle">{{
              linkText
            }}</a>
            <span
              class="pL10 pointer fc-italic fc-blue-txt4-12 text-left"
              @click="copyLink"
              >Copy the code</span
            >
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-dialog>
</template>
<script>
export default {
  data() {
    return {
      activeName: 'button',
      urlInput: 'https://',
      urlInputText: 'Sample',
      urlInputTitle: 'Sample',
      urlBgPicker: '#ee518f',
      urlFontColorPicker: '#fff',
      codePadding: '10px',
      codeBorderRadius: '4px',
      codeFontWeight: 'normal',
      imgSource: 'https://facilio.com/images/facilio-blue-logo.svg',
      imgHeight: '100px',
      imgWidth: '100px',
      imgStyle: '',
      linkUrl: '',
      linkText: 'Sample',
      linkTitle: '',
      numberColumns: null,
      numberRows: null,
    }
  },
  methods: {
    closeDialog() {
      this.$emit('onCloseGenerator')
    },
    handleClick() {
      console.log('Generator Tag clicked')
    },
    async copyBtnCode() {
      let copy = `<a
              target="_blank"
              href="${this.urlInputText}"
              title="${this.urlInputTitle}"
              style="
                color: ${this.urlFontColorPicker};
                background: ${this.urlBgPicker};
                padding: ${this.codePadding};
                font-weight: ${this.codeFontWeight};
                border-radius: ${this.codeBorderRadius};
              "
              >${this.urlInputText}</a
            >`
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Code copied',
        type: 'success',
      })
    },
    async copyImgCode() {
      let copyImg = `<img
            src="${this.imgSource}"
            style="width: ${this.imgWidth}; height: ${this.imgHeight}"
          />`
      await navigator.clipboard.writeText(copyImg)
      this.$message({
        message: 'Code copied',
        type: 'success',
      })
    },
    async copyLink() {
      let copyLink = `  <a target="_blank"
              href="${this.linkUrl}"
              title="${this.linkTitle}"
              >${this.linkText}</a
            >`
      await navigator.clipboard.writeText(copyLink)
      this.$message({
        message: 'Code copied',
        type: 'success',
      })
    },
    async copyTable() {
      let copyTableList = ''
      await navigator.clipboard.writeText(copyTableList)
      this.$message({
        message: 'Code copied',
        type: 'success',
      })
    },
  },
}
</script>
<style lang="scss">
.fc-generator-tabs {
  .el-tabs__nav {
    padding-left: 30px;
  }
  .el-tabs__active-bar {
    left: 30px;
  }
  .el-tabs__content {
    padding: 10px 30px;
  }
}
.editor-color-picker {
  .el-color-picker__color,
  .el-color-picker__trigger {
    border: 1px solid #dde1e4;
  }
}
</style>
