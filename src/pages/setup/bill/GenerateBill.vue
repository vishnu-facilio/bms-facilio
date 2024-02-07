<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50"
    :before-close="handleclose"
    style="z-index: 999999"
  >
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">Generate Bill</div>
        <div class="fc-setup-modal-desc">Generate your Bill</div>
      </div>
    </div>

    <div class="new-body-modal">
      <el-row>
        <el-col :span="19">
          <p class="fc-input-label-txt">Template Name</p>
          <el-input
            type="text"
            disabled
            v-model="tempName.templateHeader"
            class="m10"
          ></el-input>
        </el-col>
      </el-row>

      <p class="fc-input-label-txt mT40">Pick the date range</p>
      <el-date-picker
        v-model="datevalue"
        type="daterange"
        range-separator="To"
        start-placeholder="Start date"
        end-placeholder="End date"
        class="mT10"
      >
      </el-date-picker>
      <div v-if="billTemplate === 'energydata'" class="col-md-6">
        <q-select
          v-model="assetName"
          float-label="Assets"
          :options="assetList"
        />
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">
        Cancel</el-button
      >
      <el-button
        type="primary"
        v-model="loading"
        class="modal-btn-save"
        @click="generatebill(tempName.tempId)"
        >Generate Bill</el-button
      >
    </div>

    <!-- <div  style="margin-left:33px"> -->
    <!-- <div class="pull-left form-header">Generate Bill</div> -->
    <!-- </div>
              <div style="margin-left:33px;width:75%" >
                <br> </br> -->
    <!-- <div class="temp">TemplateName</div> -->
    <!-- <el-input type="text" disabled v-model="tempName.templateHeader"></el-input>
                <br></br>
                <div >Pick the date range</div>
          <el-date-picker  v-model="datevalue" type="daterange" range-separator= "To" start-placeholder="Start date" end-placeholder="End date">
    </el-date-picker>
              </div>
            <div >
            <div v-if="billTemplate === 'energydata'" class ="col-md-6">
              <q-select v-model="assetName" float-label="Assets" :options="assetList" />
            </div>
            </div>
            <br></br> -->
    <!-- <div style="margin-left:33px;">
            <q-btn v-model="loading" loader color="primary" @click="generatebill"> Generate
            <span slot="loading">Generating</span>
            </q-btn>
          </div> -->
    <!-- </div>
    </div>

    </div>

  </div> -->
    <!-- <div class="form-footer row" style="position: absolute; bottom: 0px; width: 100%;">
      <button type="button" class="el-button col-6 uppercase ls9 f12 form-btn el-button--default" style="background-color: #f4f4f4;border: 0;border-radius: 0px;padding: 18px;font-size: 13px;font-weight: 500;letter-spacing: 0.8px;text-align: center;color: #5f5f5f;margin: 0;" @click="closeDialog" ><span>Cancel</span></button>
      <button v-model="loading" type="button" class="el-button col-6 uppercase ls9 f12 form-btn  el-button--default" style="margin-left: 0px;color: white;background-color: #39b2c2;border: 0;border-radius: 0px;font-size: 13px;font-weight: 500;letter-spacing: 0.8px;text-align: center;color: #ffffff;" @click="generatebill(tempName.tempId)" ><span>Generate Bill</span></button>
      </div> -->
  </el-dialog>
</template>

<script>
import { QSelect } from 'quasar'

export default {
  props: ['visibility', 'tempName'],
  data() {
    return {
      upload: true,
      loading: false,
      mapping: false,
      billTemplate: 'Energy',
      mapperName: 'Sami',
      downloadURL: null,
      assetName: '0',
      tenantName: '',
      datevalue: [],
      fromdate: null,
      enddate: null,
      metainfo: [],
      excelList: [
        {
          label: 'Select Template',
          value: -1,
        },
      ],
      assetList: [],
    }
  },
  created() {
    this.$store.dispatch('loadEnergyMeters')
  },
  mounted() {
    this.loadTemplates()
  },

  components: {
    QSelect,
  },
  methods: {
    generatebill(tempId) {
      let self = this
      let from = new Date(this.datevalue[0]).getTime()
      let end = new Date(this.datevalue[1]).getTime()
      const formData = new FormData()
      formData.append('startTime', from)
      formData.append('endTime', end)
      formData.append('templateId', tempId)
      self.$http
        .post('/tenantbilling/generatebill', formData)
        .then(function(response) {
          self.loading = false
          window.location.href = response.data.downloadURL
          self.closeDialog()
        })
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    loadTemplates() {
      let self = this
      self.$http.get('/tenantbilling/loadtemplates').then(function(response) {
        let template = response.data.excelTemplates
        for (let i = 0; i < template.length; i++) {
          let option = {}
          option.label = template[i].name
          option.value = template[i].id
          self.excelList.push(option)
        }
      })
    },
    handleclose() {
      this.$emit('update:visibility', false)
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
.generateBill {
  width: 46% !important;
}
.el-date-editor .el-range-separator {
  padding: 0 5px;
  line-height: 32px;
  width: 8% !important;
  color: #2d2f33;
}
.temp {
  font-size: 15px;
  letter-spacing: 0.5px;
  text-align: center;
  margin-top: 40px;
  color: #bdbdbd;
}
</style>
