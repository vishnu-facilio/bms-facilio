<template>
  <div class="floorplan-add">
    <div>
      <p class="fc-input-label-txt pB5">Floor Plan Name</p>
      <el-input
        class="el-select fc-input-select fc-input-full-border2 width50"
        v-model="floorPlan.name"
      ></el-input>
    </div>
    <div class="pT20">
      <el-row>
        <el-col :span="6">
          <p class="fc-input-label-txt pB5">Make Default</p>
          <el-checkbox v-model="makedefault"></el-checkbox>
        </el-col>
        <el-col :span="6">
          <p class="fc-input-label-txt pB5">Fit to Screen</p>
          <el-checkbox v-model="fitToscreen"></el-checkbox>
        </el-col>
      </el-row>
    </div>
    <div class="pT20">
      <p class="fc-input-label-txt pB5">Floor Plan Image</p>
      <div class="imgeditor-upload-area pT20">
        <div v-if="imageUrl" class="fp-image-upload-area">
          <input
            id="img"
            @change="handleFileChange($event.target.files[0], $event)"
            type="file"
          />
          <img
            id="floorPlanImg"
            class="custom-file-upload"
            style="width: 100%;height: 100%;"
            :src="imageUrl"
          />
        </div>
        <div v-else class="fp-image-upload-area">
          <input
            id="img"
            @change="handleFileChange($event.target.files[0], $event)"
            type="file"
          />
          <img src="~assets/picture.svg" width="150" height="150" />
          <div>
            {{
              $t('common.attachment_form.drag_and_drop_files') +
                ' ' +
                $t('common.attachment_form.click_to_browse')
            }}
          </div>
        </div>
      </div>
    </div>
    <span slot="footer" class="modal-dialog-footer row">
      <el-button class="col-6 modal-btn-cancel" @click="closeDialog"
        >CANCEL</el-button
      >
      <el-button
        type="primary"
        class="col-6 modal-btn-save"
        @click="saveFloorPlan"
        >SAVE</el-button
      >
    </span>
  </div>
</template>

<script>
// import floorPlanDataMixin from 'pages/floorPlan/mixins/FloorPlanDataMixin'
import { isEmpty } from '@facilio/utils/validation'
export default {
  data() {
    return {
      makedefault: false,
      fitToscreen: true,
      imageUrl: null,
      file: null,
      fileInfo: null,
      floorPlan: {
        name: null,
        leagend: {},
      },
    }
  },
  mounted() {
    this.defaultData()
  },
  methods: {
    defaultData() {
      let { params } = this.$route
      if (!isEmpty(params)) {
        this.$set(this.floorPlan, 'siteId', Number(params.siteid))
        this.$set(this.floorPlan, 'floorId', Number(params.floorid))
      }
    },
    handleFileChange(file, event) {
      this.file = file
      console.log('file event:', event)
      let reader = new FileReader()
      reader.addEventListener('load', event => {
        this.findImageMeta(event)
        console.log('======>', this)
        let picFile = event.target
        this.imageUrl = picFile.result
      })
      reader.readAsDataURL(file)
    },
    findImageMeta() {
      let self = this
      let _URL = window.URL || window.webkitURL
      let file, img
      if ((file = this.file)) {
        img = new Image()
        let objectUrl = _URL.createObjectURL(file)

        img.onload = function() {
          let imageLayout = {
            width: this.width + 100,
            height: this.height + 100,
          }
          self.$set(self.floorPlan.leagend, 'imageLayout', imageLayout)
          console.log(this.width + ' ' + this.height)
          _URL.revokeObjectURL(objectUrl)
        }
        img.src = objectUrl
      }
    },
    closeDialog() {
      this.$emit('close', false)
    },
    uploadImage() {
      let { file } = this
      if (isEmpty(file)) return

      const formData = new FormData()
      formData.append('fileContent', file)

      return this.$http.post('/v2/files/add', formData).then(({ data }) => {
        this.$set(this.floorPlan, 'fileId', Number(data.result.fileInfo.fileId))
      })
    },
    saveFloorPlan() {
      this.uploadImage().then(() => {
        this.$set(this.floorPlan.leagend, 'fitToscreen', this.fitToscreen)
        let leagend = JSON.stringify(this.floorPlan.leagend)
        this.$set(this.floorPlan, 'leagend', leagend)
        this.$http
          .post('/v2/floorPlan/add', {
            floorPlan: this.floorPlan,
          })
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.floorPlan = data.result.floorPlan
              this.closeDialog()
              this.openFloorPlanEditor(this.floorPlan)
            }
          })
      })
    },
    openFloorPlanEditor() {
      this.$emit('makedefault', this.makedefault, this.floorPlan.id)
      this.$emit('openFloorPlanEditor', this.floorPlan)
    },
  },
}
</script>

<style>
.imgeditor-upload-area {
  width: 75%;
  padding: 100px;
  border: 1px solid #eee;
  border-style: dotted;
  border-radius: 4px;
  text-align: center;
  position: relative;
  top: 12%;
  background: #fafafa;
  cursor: pointer;
}

.imgeditor-upload-area input[type='file'] {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  z-index: 1;
  left: 0;
  top: 0;
}

.floorplan-add .f-image-editor .el-dialog__body {
  padding: 20px 30px 0;
}
</style>
