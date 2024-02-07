<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right newtemplatesize"
    :before-close="handleclose"
  >
    <el-form>
      <!-- <div class="bill-container"> -->
      <div class="new-header-container">
        <div class="new-header-modal">
          <div class="new-header-text">
            <div class="setup-modal-title ">New Template</div>
          </div>
        </div>
      </div>
      <!--  q-select v-model="moduleName" float-label="Modules" :options="modulesList"/> -->
      <!--div class="row">
            <div v-if="moduleName === 'energydata'" class="col-md-6">
              <q-select v-model="assetName" float-label="Assets" :options="assetList" />
            </div>
            </div -->
      <!-- </div> -->
      <div class="new-body-modal">
        <el-row>
          <el-col :span="19">
            <p class="new-label-text pB10">Template Name</p>
            <el-input
              v-model="templateName"
              class="form-item setting-form-title f14 fc-input-full-border2"
              placeholder="Enter the template name"
            />
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="19" class="mT30">
            <div v-if="upload">
              <attachments
                ref="uploadData"
                :customupload="true"
                @onupload="uploadExcel"
                :module="moduleName"
                @uploadResponse="processMetainfo"
              ></attachments>
            </div>
            <div v-if="mapping">
              <div class="fc-form-title">Column Mapping</div>
              <mapping
                :asset="assetName"
                :metainfo="metainfo"
                @mappingResponse="processResult"
              ></mapping>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
import Attachments from '@/UploadData'
import Mapping from '@/ColumnMapping'

export default {
  props: ['visibility'],
  data() {
    return {
      upload: true,
      mapping: false,
      moduleName: 'Energy',
      mapperName: 'Sami',
      uploadProgress: false,
      assetName: '0',
      metainfo: [],
      modulesList: [
        {
          label: 'Energy Data',
          value: 'energydata',
        },
        {
          label: 'Building',
          value: 'building',
        },
      ],
      assetList: [],
    }
  },

  components: {
    Attachments,
    Mapping,
  },
  methods: {
    uploadExcel(file) {
      let self = this
      const formData = new FormData()
      formData.append('templateName', this.templateName)
      formData.append('excelFile', file)
      self.$http
        .post('/tenantbilling/upload', formData)
        .then(function(response) {
          self.$emit('onsave', {
            name: self.templateName,
            id: response.data.excelTemplate.id,
          })
          self.templateName = null
          self.$refs['uploadData'].reset()
          self.$message({
            message: 'File successfully imported...',
            type: 'success',
          })
        })
      this.$emit('visibility', this.$helpers.cloneObject(this.templateName))
      this.$emit('update:visibility', false)
    },
    handleclose() {
      this.$emit('update:visibility', false)
    },
    processMetainfo(metainfo) {
      this.metainfo = metainfo
      this.upload = false
      this.mapping = true
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    processResult(data) {
      console.log(data)
      this.upload = true
      this.mapping = false
      const alert1 = Alert.create({
        html: 'Data imported successfully!',
        color: 'positive',
        position: 'top-center',
      })
      setTimeout(function() {
        alert1.dismiss()
      }, 1500)
    },
  },
}
</script>

<style type="text/css">
.newtemplatesize {
  width: 40% !important;
}
/* .bill-upload{
      margin-left: 10px;
    } */
/* .bill-container{
      padding-left: 20px;
      padding-top: 20px;
    } */
</style>
