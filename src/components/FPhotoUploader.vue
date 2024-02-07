<template>
  <f-image-editor
    @onupload="onUpload"
    @close="closedialog"
    :visibility.sync="dialogVisible"
    :module="module"
    :conf="widget"
    :editwidget="editwidget"
    @onupdate="onUpdate"
    :uploadLoading="loading"
  ></f-image-editor>
</template>
<script>
import FImageEditor from '@/FImageEditor'
export default {
  props: ['dialogVisible', 'module', 'widget', 'editwidget'],
  components: {
    FImageEditor,
  },
  data() {
    return {
      loading: false,
    }
  },
  methods: {
    closedialog() {
      this.$emit('update:dialogVisible', false)
      this.$emit('input', false)
    },

    onUpload(imageObj) {
      let self = this
      self.loading = true

      let formdata = new FormData()
      formdata.append('avatar', imageObj.file)

      self.$http
        .post('/widget/addPhoto', formdata)
        .then(function(response) {
          self.loading = false
          self.$emit('update:dialogVisible', false)

          let respData = {
            photoId: response.data.photoId,
            url: response.data.url,
            options: {
              imageMeta: imageObj.imageMeta,
              areas: imageObj.areas,
            },
          }
          self.$emit('upload-done', respData)
          self.closeDialog = true
        })
        .catch(function(error) {
          self.$emit('upload-failed', error)
        })
    },
    onUpdate(data) {
      let self = this
      self.loading = true
      self.$emit('update', data)
      self.closeDialog = true
    },
  },
}
</script>
<style>
.f-photo-uploader {
  width: 100%;
  height: 200px;
  opacity: 0;
}
.f-upload-outer-box {
  border: 1px dashed #e4e4e4;
}
.empty-upload-content {
  position: absolute;
  top: 30%;
  text-align: center;
  width: 60%;
  display: block;
  padding: 15px;
  font-size: 14px;
  color: #666;
  line-height: 30px;
  margin: auto;
  margin-left: 12%;
}
.f-photo-container {
  width: 100%;
  height: 200px;
}
.el-dialog.f-photo-dialog {
  width: 20% !important;
}
.f-photo-dialog .dialog-footer {
  width: 100%;
}
.f-photo-dialog .dialog-footer .el-button.el-button--primary {
  background: #00b5c5 !important;
  color: #fff !important;
}
.f-photo-dialog .dialog-footer .el-button {
  border-radius: 0 !important;
  font-size: 13px !important;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  border: 0;
  color: #606266 !important;
  width: 50% !important;
  margin: 0 !important;
  height: 50px !important;
}
.f-photo-dialog .el-dialog__footer {
  width: 100%;
  padding: 0px !important;
}
.f-photo-dialog .dialog-footer {
  display: inline-table;
  width: 100%;
  align-items: center;
  left: 0;
  bottom: 0;
}
.f-photo-dialog i.el-dialog__close.el-icon.el-icon-close {
  position: absolute;
  right: -50px;
  font-size: 20px;
  display: block;
  width: 30px;
  height: 30px;
  cursor: pointer;
  transition: transform 0.18s;
  transform: rotate(0);
  color: #fff;
  font-weight: 900;
  top: -40px;
}
.f-photo-dialog .el-dialog__header {
  display: none;
}
.f-photo-dialog .loading-conatiner {
  text-align: center;
  width: calc(100% - 40px);
  height: 200px;
  position: absolute;
}
.f-photo-dialog .loading-conatiner > i {
  position: absolute;
  color: #fff;
  font-size: 3rem;
  left: 40%;
  top: 40%;
  text-align: center;
}
.f-photo-dialog .overlay {
  opacity: 0.6;
  width: calc(100% - 40px);
  height: 200px;
  background: #ffffff;
  position: absolute;
}
.f-photo-uploader:hover {
  cursor: pointer;
}
.f-upload-outer-box:hover {
  background: #efefef;
  cursor: pointer !important;
}
</style>
