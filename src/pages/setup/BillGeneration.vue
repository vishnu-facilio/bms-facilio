<template>
  <div class="layout-padding">
    <div v-if="upload">
      <div class="fc-form-title">Generate Bill From Templates</div>
      <div class="row">
        <div class="col-md-6">
          <q-select
            v-model="billTemplate"
            float-label="Bill Templates"
            :options="excelList"
          />
        </div>
      </div>
      <div class="row">
        <q-datetime
          v-model="fromdate"
          type="date"
          float-label="From Date"
          class="col-md-2"
        />
        <q-datetime
          v-model="enddate"
          type="date"
          float-label="End Date"
          class="col-md-2"
        />
      </div>
      <div class="row">
        <div v-if="billTemplate === 'energydata'" class="col-md-6">
          <q-select
            v-model="assetName"
            float-label="Assets"
            :options="assetList"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { QSelect, QDatetime } from 'quasar'

export default {
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
  title() {
    return 'Generate Bill'
  },
  mounted() {
    console.log('Mounted Called ...')
    this.loadTemplates()
  },

  components: {
    QSelect,
    QDatetime,
  },

  created() {
    this.loadAssetsList()
  },

  methods: {
    generatebill(file) {
      let self = this
      // svar self = this
      let from = new Date(this.fromdate).getTime()
      let end = new Date(this.enddate).getTime()
      const formData = new FormData()
      formData.append('startTime', from)
      formData.append('endTime', end)
      formData.append('templateId', this.billTemplate)
      self.$http
        .post('/tenantbilling/generatebill', formData)
        .then(function(response) {
          self.loading = false
          self.downloadURL = response.data.downloadURL
        })
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
    processMetainfo(metainfo) {
      this.metainfo = metainfo
      console.log(this.metainfo.columnHeadings)
      this.upload = false
      this.mapping = true
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
.Cell {
  display: table-cell;
  border-width: thin;
  padding-top: 5px;
  padding-bottom: 5px;
}
.v-modal {
  z-index: 101 !important;
}
</style>
