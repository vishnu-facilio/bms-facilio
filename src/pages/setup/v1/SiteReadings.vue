<template>
  <div class="height100 user-layout setup-readings">
    <div class="fc-setup-header" style="height: 183px;">
      <div class="setting-title-block fL">
        <div class="setting-form-title">Site Readings</div>
        <div class="heading-description">List of all site readings</div>
      </div>
      <div class="action-btn setting-page-btn fR">
        <el-button
          type="primary"
          @click="showNewReading = true"
          class="setup-el-btn"
          >New Site Reading</el-button
        >
        <new-asset-reading
          v-if="showNewReading"
          resourceType="site"
          @saved="aftersave"
          :isNew="isNew"
          :visibility.sync="showNewReading"
        ></new-asset-reading>
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
          <el-select
            v-model="readingFilter"
            placeholder="Select Readings"
            class="fc-input-full-border2 space-reading-link mL20"
            @change="assetReadingChooser"
          >
            <el-option label="Site" value="site">
              Site
            </el-option>
            <el-option label="Building" value="building">
              Building
            </el-option>
            <el-option label="Floor" value="floor">
              Floor
            </el-option>
            <el-option label="Space" value="space">
              Space
            </el-option>
          </el-select>
        </div>
        <div>
          <f-search
            v-model="assetReadingFields"
            class="reading-search"
          ></f-search>
        </div>
      </div>
      <div class="flex-middle clearboth mT15">
        <el-tabs
          v-model="selectedtab"
          @tab-click="readingsTabChange"
          class="width100 setup-reading-tab"
        >
          <el-tab-pane
            v-for="tab in tabs"
            :key="tab.key"
            :label="tab.label"
            :name="tab.key"
          >
            <div class="container-scroll">
              <div class="">
                <div class="col-lg-12 col-md-12">
                  <table class="setting-list-view-table width100">
                    <thead>
                      <tr>
                        <th class="setting-table-th setting-th-text">
                          Reading NAME
                        </th>
                        <th class="setting-table-th setting-th-text">
                          {{ $t('setup.setupLabel.link_name') }}
                        </th>
                        <th class="setting-table-th setting-th-text">
                          SITE NAME
                        </th>
                        <th class="setting-table-th setting-th-text">
                          Reading TYPE
                        </th>
                        <!-- <th class="setting-table-th setting-th-text">TYPE</th> -->
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
                          {{ field.spaceId }}
                        </td>
                        <td>
                          {{
                            field.counterField
                              ? 'Counter'
                              : $constants.dataType[field.dataType]
                          }}
                        </td>
                        <!-- <td>
                        {{ field.default ? 'System' : 'Custom' }}
                      </td> -->
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
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <edit-reading
      v-if="editshowDialog"
      :model="model"
      :visibility.sync="editshowDialog"
      :unitDetails="metricsUnits"
      @saved="aftersave"
      resourceType="site"
    ></edit-reading>
  </div>
</template>
<script>
import NewAssetReading from 'src/pages/setup/v1/NewAssetReading'
import EditReading from 'pages/setup/new/v1/EditAssetReadingDialog'
import FSearch from '@/FSearch'
import ReadingsHelper from './ReadingsHelper'
export default {
  title() {
    return 'Asset Reading'
  },
  mixins: [ReadingsHelper],
  components: {
    NewAssetReading,
    EditReading,
    FSearch,
  },
  data() {
    return {
      metricsUnits: null,
      isNew: true,
      loading: true,
      assetCategories: [],
      assetReadingFields: [],
      loadForm: false,
      model: {},
      newAssetReadingDialog: false,
      showNewReading: false,
      editshowDialog: false,
      readinglist: 'spacereadings',
      readingFilter: 'site',
      selectedtab: 'connected',
      tabs: [
        {
          key: 'connected',
          label: 'Connected',
        },
        {
          key: 'formula',
          label: 'Formula',
        },
        {
          key: 'available',
          label: 'Available',
        },
      ],
    }
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
    showNewReadings: function() {
      this.$refs.AssetReadingDialog.open()
      this.loadForm = true
    },
    getAllSpaceTypeReadings() {
      let self = this
      let type = 'Sites'
      this.loading = true
      self.assetReadingFields = []
      self.$http
        .post(`/reading/getallspacetypereadings`, {
          spaceType: type,
          readingType: this.selectedtab,
        })
        .then(function(response) {
          self.assetCategories = response.data.moduleMap
          for (let categoryId in response.data.moduleMap) {
            let moduleJson = response.data.moduleMap[categoryId]
            for (let moduleIndex in moduleJson) {
              let module = moduleJson[moduleIndex]
              if (!self.$constants.weatherModules.includes(module.name)) {
                let readingFields = module.fields
                for (let fieldIndex in readingFields) {
                  let field = readingFields[fieldIndex]
                  if (categoryId === '-1') {
                    field['spaceId'] = 'All Sites'
                  } else {
                    field['spaceId'] = response.data.spaces[categoryId].name
                  }
                  field.module = {
                    name: module.name,
                    displayName: module.displayName,
                  }
                  field.moduleType = module.type
                  self.assetReadingFields.push(field)
                }
              }
            }
          }
          self.loading = false
        })
    },
    AssetReadingDialog() {
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
