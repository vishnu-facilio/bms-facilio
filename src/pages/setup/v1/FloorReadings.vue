<template>
  <div class="height100 user-layout setup-readings">
    <div class="fc-setup-header" style="height: 183px;">
      <div class="setting-title-block fL">
        <div class="setting-form-title">Floor Readings</div>
        <div class="heading-description">List of all floor readings</div>
      </div>
      <div class="action-btn setting-page-btn fR">
        <el-button
          type="primary"
          @click="showDialog = true"
          class="setup-el-btn"
          >New Floor Reading</el-button
        >
        <!-- <el-button type="primary" @click="showNewReading = true" class="setup-el-btn">New Floor</el-button> -->
        <!-- <new-asset-reading resourceType="floor" @saved="aftersave" :visibility.sync="showDialog"></new-asset-reading> -->
        <new-floor-reading
          v-if="showDialog"
          @saved="aftersave"
          :visibility.sync="showDialog"
        ></new-floor-reading>
      </div>
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
            v-model="floorReadingFields"
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
              <div class="row">
                <div class="col-lg-12 col-md-12">
                  <table class="setting-list-view-table width100">
                    <thead>
                      <tr>
                        <th class="setting-table-th setting-th-text">
                          BUILDING NAME
                        </th>
                        <th class="setting-table-th setting-th-text">
                          FLOOR NAME
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
                    <tbody v-else-if="floorReadingFields.length === 0">
                      <tr>
                        <td colspan="100%" style="text-align:center;">
                          NO DATA
                        </td>
                      </tr>
                    </tbody>
                    <tbody v-else>
                      <tr
                        class="tablerow"
                        v-for="(field, index) in floorReadingFields"
                        :key="index"
                      >
                        <td>
                          {{ field.buildingName }}
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
      resourceType="floor"
    ></edit-reading>
  </div>
</template>
<script>
import NewFloorReading from 'src/pages/setup/v1/NewFloorReading'
import EditReading from 'pages/setup/new/v1/EditAssetReadingDialog'
import FSearch from '@/FSearch'
import ReadingsHelper from './ReadingsHelper'
export default {
  mixins: [ReadingsHelper],
  components: {
    NewFloorReading,
    EditReading,
    FSearch,
  },
  data() {
    return {
      metricsUnits: null,
      loading: true,
      model: {},
      floorReadingFields: [],
      loadForm: false,
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
      readingFilter: 'floor',
    }
  },
  mounted: function() {
    this.loadDefaultMetricUnits()
    this.getFloorReadings()
  },
  methods: {
    aftersave: function() {
      // this.$refs.newFloorReadingDialog.close()
      this.floorReadingFields = []
      this.getFloorReadings()
    },
    showNewReading: function() {
      this.$refs.newFloorReadingDialog.open()
      this.loadForm = true
    },
    getFloorReadings() {
      let self = this
      let type = 'Floors'
      this.loading = true
      self.floorReadingFields = []
      self.$http
        .post(
          `/reading/getallspacetypereadings?readingType=${this.selectedtab}`,
          {
            spaceType: type,
            readingType: this.selectedtab,
          }
        )
        .then(function(response) {
          for (let categoryId in response.data.moduleMap) {
            let moduleJson = response.data.moduleMap[categoryId]
            for (let moduleIndex in moduleJson) {
              let readingFields = moduleJson[moduleIndex].fields
              for (let fieldIndex in readingFields) {
                let field = readingFields[fieldIndex]
                if (categoryId === -1) {
                  field['spaceId'] = 'Default Readings'
                  field.buildingName = 'All'
                } else {
                  field['spaceId'] = response.data.spaces[categoryId]
                    ? response.data.spaces[categoryId].name
                    : ''
                  field.buildingName = response.data.spaces[categoryId]
                    ? response.data.spaces[categoryId].building.name
                    : ''
                }
                self.floorReadingFields.push(field)
              }
            }
          }
          self.loading = false
        })
    },
    newFloorReadingDialog() {
      this.$refs.NewFloorReading.open()
    },
    editReadingField(field) {
      this.model.fields = [field]
      this.editshowDialog = true
    },
    loadDefaultMetricUnits() {
      let self = this
      self.$http.get('/units/getDefaultMetricUnits').then(response => {
        self.metricsUnits = response.data
      })
    },
    readingsTabChange() {
      this.getFloorReadings()
    },
  },
}
</script>
