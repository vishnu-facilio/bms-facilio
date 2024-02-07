<template>
  <el-dialog
    custom-class="f-image-editor fc-dialog-center-container  fp-adder indoorfp-dialog fp-image-adder"
    v-if="visibility"
    :visible.sync="visibility"
    :modal-append-to-body="false"
    :before-close="closeDialog"
    :width="'35%'"
    title="UPLOAD MARKER"
  >
    <div class="height100">
      <div class="flex-container height100">
        <div class="infp-left-section height100 p30 width100 pT20">
          <el-form
            ref="form"
            :rules="rules"
            :model="markertype"
            label-width="120px"
          >
            <el-row class="mB20">
              <el-col :span="24">
                <el-form-item prop="name" class="">
                  <p class="fc-input-label-txt pB5">Marker name</p>
                  <el-input
                    v-model="markertype.name"
                    class="width100 fc-input-full-border2"
                    placeholder="Enter the name"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="">
              <el-col :span="24">
                <el-form-item prop="description" class="mB20 m0">
                  <p class="fc-input-label-txt pB5">Description</p>
                  <el-input
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 2 }"
                    resize="none"
                    v-model="markertype.description"
                    class="width100 fc-input-full-border-select2"
                    placeholder="Description"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="mB20">
              <el-col :span="24">
                <el-form-item prop="description" class="">
                  <p class="fc-input-label-txt pB5">Module</p>
                  <el-select
                    filterable
                    clearable
                    v-model="markertype.recordModuleId"
                    placeholder="Select Module"
                    class="width100 el-input-textbox-full-border"
                  >
                    <el-option
                      v-for="(mod, index) in modules"
                      :key="index"
                      :label="mod.displayName"
                      :value="mod.moduleId"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <!-- <el-col :span="6">
                <el-form-item prop="description" class="mB20 m0 pL20">
                  <p class="fc-input-label-txt pB5">Auto Create</p>
                  <el-checkbox v-model="markertype.isAutoCreate"></el-checkbox>
                </el-form-item>
              </el-col> -->
            </el-row>

            <el-row class="mB20">
              <el-col :span="24">
                <el-form-item prop="fileId" class="">
                  <p class="fc-input-label-txt pB5">Marker Icon</p>
                  <div
                    class="infp-right-section height100 width100 marker-upload-conatiner pT0"
                  >
                    <ImageUpload
                      v-model="markertype['fileId']"
                      class="flex  upload-section"
                    ></ImageUpload>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>
      <div slot="footer" class="modal-dialog-footer row">
        <el-button class="col-6 modal-btn-cancel" @click="closeDialog"
          >CANCEL</el-button
        >
        <el-button
          :loading="uploading"
          type="primary"
          class="col-6 modal-btn-save"
          @click="handleSave"
        >
          {{ uploading ? 'Saving...' : 'SAVE' }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import ImageUpload from 'pages/card-builder/components/ImageUpload'
export default {
  props: ['visibility'],
  components: {
    ImageUpload,
  },
  data() {
    return {
      modules: [],
      markertype: {
        name: null,
        description: null,
        fileId: null,
        recordModuleId: null,
        isAutoCreate: false,
      },
      uploading: false,
      rules: {
        name: [
          {
            required: true,
            message: 'Please input Floor Plan name',
            trigger: 'blur',
          },
        ],
        fileId: [{ required: true, message: 'Icon required', trigger: 'blur' }],
      },
      floorList: [],
    }
  },
  created() {
    this.loadModules()
  },
  methods: {
    validData() {
      if (this.markertype.name != null && this.markertype.fileId != null) {
        return true
      }
      return false
    },
    handleSave() {
      if (this.validData()) {
        this.save()
      } else {
        this.$message.error('Mandatory Fields Missing')
      }
    },
    async loadModules() {
      let url = '/v3/modules/list/all'
      let { error, data } = await API.get(url)

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        Object.values(data).forEach(value => {
          this.modules.push(...value)
        })
      }
    },
    closeDialog() {
      this.$emit('close')
      this.$emit('update:visibility', false)
    },
    async save() {
      let url = 'v3/modules/data/create'
      let params = {
        moduleName: 'markertype',
        data: this.markertype,
      }
      this.uploading = true
      let { error } = await API.post(url, params)

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.closeDialog()
        this.uploading = false
      }
    },
  },
}
</script>

<style>
.marker-upload-conatiner .card-img-upload {
  max-width: 100%;
  max-height: 140px;
  height: 140px;
  width: 100%;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-pack: center;
  -ms-flex-pack: center;
  justify-content: center;
  margin-right: 0 !important;
}

.upload-section {
  width: 100%;
  height: 100%;
  align-items: center;
  display: flex;
  justify-content: center;
}

.indoorfp-dialog .el-dialog__body {
  padding: 0px;
  height: 580px;
}

.infp-left-section {
  border-right: 1px solid #eee;
}

.infp-left-section .el-form-item__content {
  margin: 0px !important;
}

.marker-upload-conatiner .card-image-preview {
  max-width: 200px;
  max-height: 200px;
  height: 300px;
  width: 400px;
}
.fp-image-adder .el-form-item {
  margin: 0px;
}
</style>
