<template>
  <div class="height100 user-layout setup-readings">
    <div class="fc-setup-header" style="height: 145px;">
      <div class="setting-title-block fL">
        <div class="setting-form-title">Weather Readings</div>
        <div class="heading-description">List of all weather readings</div>
      </div>
      <!-- tab section -->
      <div class="clearboth flex-middle pT20 justify-content-space width100">
        <div>
          <el-select
            v-model="readinglist"
            placeholder="Select Readings"
            class="fc-input-full-border2 space-reading-link"
            @change="readingSpaceAssetChoosing"
          >
            <el-option label="Asset Readings" value="assetreadings">
              Asset Readings
            </el-option>
            <el-option label="Space Readings" value="spacereadings">
              Space Readings
            </el-option>
            <el-option label="Weather Readings" value="weatherreadings">
              Weather Readings
            </el-option>
          </el-select>
        </div>
        <div>
          <f-search
            v-model="assetReadingFields"
            class="weather-reading-search"
          ></f-search>
        </div>
      </div>
      <div class="learboth mT35">
        <div class="container-scroll">
          <div class="">
            <div class="col-lg-12 col-md-12">
              <table class="setting-list-view-table width100">
                <thead>
                  <tr>
                    <th class="setting-table-th setting-th-text">
                      READING NAME
                    </th>
                    <th class="setting-table-th setting-th-text">
                      {{ $t('setup.setupLabel.link_name') }}
                    </th>
                    <th class="setting-table-th setting-th-text">
                      READING TYPE
                    </th>
                    <th></th>
                  </tr>
                </thead>
                <tbody v-if="loading">
                  <tr>
                    <td colspan="100%" class="text-center">
                      <spinner :show="loading" size="80"></spinner>
                    </td>
                  </tr>
                </tbody>
                <tbody v-else-if="assetReadingFields.length === 0">
                  <tr>
                    <td colspan="100%" style="text-align:center;">
                      NO DATA
                    </td>
                  </tr>
                </tbody>
                <tbody v-else>
                  <tr
                    class="tablerow"
                    v-for="(field, index) in assetReadingFields"
                    :key="index"
                  >
                    <td>
                      {{ field.displayName }}
                    </td>
                    <td>
                      {{ field.name }}
                    </td>
                    <td>
                      {{ dataType[field.dataType] }}
                    </td>
                    <td style="width: 30%;">
                      <div
                        class="text-left actions"
                        style="margin-top:-3px;margin-right: 15px;text-align:center;"
                      >
                        <i
                          class="el-icon-edit pointer"
                          title="Edit Reading"
                          v-tippy
                          @click="editReadingField(field)"
                        ></i>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <edit-site-reading
      v-if="editshowDialog"
      :model="model"
      :visibility.sync="editshowDialog"
      :unitDetails="metricsUnits"
      @saved="aftersave"
      resourceType="site"
    ></edit-site-reading>
  </div>
</template>
<script>
import EditSiteReading from 'pages/setup/EditSiteReading'
import FSearch from '@/FSearch'
import ReadingsHelper from './ReadingsHelper'
export default {
  title() {
    return 'Asset Reading'
  },
  mixins: [ReadingsHelper],
  components: {
    EditSiteReading,
    FSearch,
  },
  data() {
    return {
      metricsUnits: null,
      isNew: true,
      loading: true,
      assetReadingFields: [],
      loadForm: false,
      model: {},
      editshowDialog: false,
      readinglist: 'weatherreadings',
      readingFilter: 'site',
      dataType: {
        1: 'Text',
        2: 'Number',
        3: 'Decimal',
        4: 'Boolean',
        8: 'Pick List',
      },
    }
  },
  computed: {
    newSiteSummary() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return (
        isLicenseEnabled('NEW_SITE_SUMMARY') &&
        isLicenseEnabled('WEATHER_INTEGRATION')
      )
    },
  },
  mounted: function() {
    this.loadDefaultMetricUnits()
    this.getAllSpaceTypeReadings()
  },
  methods: {
    aftersave: function() {
      this.assetReadingFields = []
      this.getAllSpaceTypeReadings()
    },
    showNewReading: function() {
      this.$refs.newAssetReadingDialog.open()
      this.loadForm = true
    },
    getAllSpaceTypeReadings() {
      let self = this
      let type = 'Sites'
      if (this.newSiteSummary) type = 'Weather Station'
      this.loading = true
      self.assetReadingFields = []
      self.$http
        .post(`/reading/getallspacetypereadings`, {
          spaceType: type,
        })
        .then(function(response) {
          let modules = response.data.moduleMap[-1]
          if (modules) {
            modules.forEach(weatherModule => {
              if (self.$constants.weatherModules.includes(weatherModule.name)) {
                let readingFields = weatherModule.fields
                for (let fieldIndex in readingFields) {
                  let field = readingFields[fieldIndex]
                  field.module = {
                    name: weatherModule.name,
                    displayName: weatherModule.displayName,
                  }
                  field.moduleType = weatherModule.type
                  self.assetReadingFields.push(field)
                }
              }
            })
          }
          self.loading = false
        })
    },
    newAssetReadingDialog() {
      this.isNew = true
      this.$refs.newAssetReading.open()
    },
    editReadingField(field) {
      this.model.fields = [field]
      this.editshowDialog = true
    },
    readingsTabChange() {
      this.getAllSpaceTypeReadings()
    },
    loadDefaultMetricUnits() {
      let self = this
      self.$http.get('/units/getDefaultMetricUnits').then(response => {
        self.metricsUnits = response.data
      })
    },
  },
}
</script>
