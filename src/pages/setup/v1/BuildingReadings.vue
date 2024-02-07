<template>
  <div class="height100 user-layout setup-readings">
    <div class="fc-setup-header" style="height: 183px;">
      <div class="setting-title-block fL">
        <div class="setting-form-title">Building Readings</div>
        <div class="heading-description">
          List of all building related readings
        </div>
      </div>
      <div class="action-btn setting-page-btn fR">
        <el-button
          type="primary"
          @click="showDialog = true"
          class="setup-el-btn"
          >New Building Reading</el-button
        >
        <new-building-reading
          v-if="showDialog"
          resourceType="Building"
          @saved="aftersave"
          :visibility.sync="showDialog"
        ></new-building-reading>
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
            v-model="buildingReadingFields"
            class="reading-search"
          ></f-search>
        </div>
      </div>
      <div class="mT15">
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
                          SITE NAME
                        </th>
                        <th class="setting-table-th setting-th-text">
                          BUILDING NAME
                        </th>
                        <th class="setting-table-th setting-th-text">
                          Reading NAME
                        </th>
                        <th class="setting-table-th setting-th-text">
                          Reading TYPE
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
                    <tbody v-else-if="buildingReadingFields.length === 0">
                      <tr>
                        <td colspan="100%" style="text-align:center;">
                          NO DATA
                        </td>
                      </tr>
                    </tbody>
                    <tbody v-else>
                      <tr
                        class="tablerow"
                        v-for="(field, index) in buildingReadingFields"
                        :key="index"
                      >
                        <td>
                          {{ field.siteName }}
                        </td>
                        <td>
                          {{ field.spaceId }}
                        </td>
                        <td>
                          {{ field.displayName }}
                        </td>
                        <td>
                          {{
                            field.counterField
                              ? 'Counter'
                              : $constants.dataType[field.dataType]
                          }}
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
      resourceType="buildings"
    ></edit-reading>
  </div>
</template>
<script>
import NewBuildingReading from 'src/pages/setup/v1/NewBuildingReading'
import EditReading from 'pages/setup/new/v1/EditAssetReadingDialog'
import FSearch from '@/FSearch'
import ReadingsHelper from './ReadingsHelper'
export default {
  title() {
    return 'Asset Reading'
  },
  mixins: [ReadingsHelper],
  components: {
    NewBuildingReading,
    EditReading,
    FSearch,
  },
  data() {
    return {
      metricsUnits: null,
      loading: true,
      model: {},
      buildingReadingFields: [],
      loadForm: false,
      // showNewReading: false,
      editshowDialog: false,
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
      showDialog: false,
      readinglist: 'spacereadings',
      readingFilter: 'building',
    }
  },
  mounted: function() {
    this.loadDefaultMetricUnits()
    this.getBuildingReadings()
  },
  methods: {
    aftersave: function() {
      this.buildingReadingFields = []
      this.getBuildingReadings()
    },
    getBuildingReadings() {
      let self = this
      let type = 'Buildings'
      this.loading = true
      self.buildingReadingFields = []
      self.$http
        .post(
          `/reading/getallspacetypereadings?readingType=${this.selectedtab}`,
          {
            spaceType: type,
            readingType: this.selectedtab,
          }
        )
        .then(function(response) {
          for (let buildingName in response.data.moduleMap) {
            let moduleJson = response.data.moduleMap[buildingName]
            for (let moduleIndex in moduleJson) {
              let readingFields = moduleJson[moduleIndex].fields
              for (let fieldIndex in readingFields) {
                let field = readingFields[fieldIndex]
                if (buildingName === '-1') {
                  field['spaceId'] = 'All Buildings'
                  field.siteName = '-'
                } else {
                  field['spaceId'] = response.data.spaces[buildingName].name
                  field.siteName = response.data.spaces[buildingName].site.name
                }
                self.buildingReadingFields.push(field)
              }
            }
          }
          self.loading = false
        })
    },
    editReadingField(field) {
      this.model.fields = [field]
      this.editshowDialog = true
    },
    newBuildingReadingDialog() {
      this.$refs.NewBuildingReading.open()
    },
    readingsTabChange() {
      this.getBuildingReadings()
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
