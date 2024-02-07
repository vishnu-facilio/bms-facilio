<template>
  <div class="height100 setup-readings">
    <div class="fc-setup-header" style="height: 183px;">
      <div class="setting-title-block fL">
        <div class="setting-form-title">Space Readings</div>
        <div class="heading-description">List of all space readings</div>
      </div>
      <div class="action-btn setting-page-btn fR">
        <el-button
          type="primary"
          @click="showDialog = true"
          class="setup-el-btn"
          >New Space Readings</el-button
        >
        <new-asset-reading
          :isNew="true"
          resourceType="Space"
          @saved="aftersave"
          :visibility.sync="showDialog"
        >
        </new-asset-reading>
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
          <el-select
            v-model="categoryId"
            filterable
            :placeholder="$t('setup.setup.select_category')"
            class="fc-input-full-border-select2 mL20"
            @change="categoryChange"
          >
            <el-option label="All Categories" :value="-1"></el-option>
            <el-option
              v-for="category in spaceCategories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            >
            </el-option>
          </el-select>
        </div>
        <div>
          <f-search
            v-model="spaceReadingFields"
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
                          Reading NAME
                        </th>
                        <th class="setting-table-th setting-th-text">
                          {{ $t('setup.setupLabel.link_name') }}
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
                    <tbody
                      v-if="$validation.isEmpty(spaceReadingFields) && !loading"
                    >
                      <tr>
                        <td colspan="100%" style="text-align:center;">
                          NO DATA
                        </td>
                      </tr>
                    </tbody>
                    <tbody v-else>
                      <tr
                        class="tablerow"
                        v-for="(field, index) in spaceReadingFields"
                        :key="index"
                      >
                        <td>
                          {{ field.displayName }}
                        </td>
                        <td>
                          {{ field.name }}
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
      resourceType="space"
    ></edit-reading>
  </div>
</template>
<script>
import NewAssetReading from 'src/pages/setup/v1/NewAssetReading'
import EditReading from 'pages/setup/new/v1/EditAssetReadingDialog'
import { mapState } from 'vuex'
import FSearch from '@/FSearch'
import ReadingsHelper from './ReadingsHelper'
export default {
  title() {
    return 'Space Reading'
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
      loading: true,
      spaceReadingFields: [],
      loadForm: false,
      model: {},
      readinglist: 'spacereadings',
      editshowDialog: false,
      showDialog: false,
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
      readingFilter: 'space',
      categoryId: -1,
    }
  },
  mounted: function() {
    this.loadDefaultMetricUnits()
    this.getAllSpaceReadings()
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
  },
  computed: {
    ...mapState({
      spaceCategories: state => state.spaceCategory,
    }),
  },
  methods: {
    aftersave: function() {
      // this.$refs.newAssetReadingDialog.close()
      this.spaceReadingFields = []
      this.getAllSpaceReadings()
    },
    showNewReading: function() {
      this.$refs.newAssetReadingDialog.open()
      this.loadForm = true
    },
    getAllSpaceReadings() {
      let self = this
      this.loading = true
      self.spaceReadingFields = []
      this.$http
        .get(
          `/v2/readings/spacecategory?id=${this.categoryId}&readingType=${this.selectedtab}`
        )
        .then(function(response) {
          self.spaceReadingFields = response.data.result.readings
          self.loading = false
        })
    },
    newSpaceReadingDialog() {
      this.$refs.newSpaceReading.open()
    },
    editSpaceCategoryDialog(data) {
      this.$refs.editSpaceCategory.open(data)
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
      this.getAllSpaceReadings()
    },
    categoryChange() {
      this.getAllSpaceReadings()
    },
  },
}
</script>
