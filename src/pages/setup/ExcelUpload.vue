<template>
  <div class="layout-padding">
    <div v-if="upload">
      <div class="fc-form-title">Upload Excel Template</div>
      <div class="row">
        <div class="col-md-4">
          <!--  q-select
              v-model="moduleName"
              float-label="Modules"
              :options="modulesList"
              / -->
          <q-input
            v-model="templateName"
            class="form-item"
            float-label="Template Name"
          />
        </div>
      </div>
      <!--div class="row">
            <div v-if="moduleName === 'energydata'" class="col-md-6">
              <q-select
              v-model="assetName"
              float-label="Assets"
              :options="assetList"
              />
            </div>
            </div -->
      <div class="row">
        <attachments
          ref="uploadData"
          :customupload="true"
          @onupload="uploadExcel"
          class="col-md-4"
          :module="moduleName"
          @uploadResponse="processMetainfo"
        ></attachments>
      </div>
    </div>

    <div v-if="mapping">
      <div class="fc-form-title">Column Mapping</div>
      <mapping
        :asset="assetName"
        :metainfo="metainfo"
        @mappingResponse="processResult"
      ></mapping>
    </div>
  </div>
</template>

<script>
import { QInput } from 'quasar'

import Attachments from '@/UploadData'
import Mapping from '@/ColumnMapping'

export default {
  data() {
    return {
      upload: true,
      mapping: false,
      moduleName: 'Energy',
      mapperName: 'Sami',
      uploadProgress: false,
      assetName: '0',
      templateName: '',
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
  title() {
    return 'Upload Excel Template'
  },
  components: {
    Attachments,
    Mapping,
    QInput,
  },

  created() {
    this.loadAssetsList()
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
          self.templateName = null
          self.$refs['uploadData'].reset()
          self.$message({
            message: 'File successfully imported...',
            type: 'success',
          })
        })
    },

    processMetainfo(metainfo) {
      this.metainfo = metainfo
      this.upload = false
      this.mapping = true
    },

    processResult(data) {
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
.Cell {
  display: table-cell;
  border-width: thin;
  padding-top: 5px;
  padding-bottom: 5px;
}
</style>
