<template>
  <div class="p30">
    <div class="header cards-config-header ">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'Web Card' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20 mB60">
      <div class="section">
        <el-form
          :model="cardDataObj"
          :ref="`${this.cardLayout}_form`"
          :label-position="'top'"
        >
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="title" class="mB10">
                <p class="fc-input-label-txt pB5">Title</p>
                <el-input
                  :autofocus="isNew"
                  v-model="cardDataObj.title"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="mB10" :gutter="20">
            <el-col :span="12">
              <el-form-item prop="siteId" class="mB10">
                <p class="fc-input-label-txt pB5">Sites</p>
                <el-select
                  v-model="cardDataObj.siteId"
                  filterable
                  clearable
                  collapse-tags
                  placeholder="Sites"
                  class="db-filter fc-tag width100"
                  v-tippy
                >
                  <el-option
                    v-for="(site, index) in sites"
                    :key="index"
                    :label="site.name"
                    :value="site.id"
                  >
                  </el-option>
                  <el-option :key="-1" :label="'All Sites'" :value="-1">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="floorId" class="mB10">
                <p class="fc-input-label-txt pB5">Floor</p>
                <el-select
                  v-model="cardDataObj.floorId"
                  filterable
                  clearable
                  collapse-tags
                  placeholder="Floor"
                  class="db-filter fc-tag width100"
                  v-tippy
                >
                  <el-option
                    v-for="(floor, index) in floors"
                    :key="index"
                    :label="floor.name"
                    :value="floor.id"
                  >
                  </el-option>
                  <el-option :key="-1" :label="'All Floor'" :value="-1">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="floorPlanId" class="mB10">
                <p class="fc-input-label-txt pB5">Floor Plan</p>
                <el-select
                  v-model="cardDataObj.floorPlanId"
                  filterable
                  clearable
                  collapse-tags
                  placeholder="Floor Plan"
                  class="db-filter fc-tag width100"
                  v-tippy
                >
                  <el-option
                    v-for="(floorplan, index) in filteredFloorPLanList"
                    :key="index"
                    :label="floorplan.name"
                    :value="floorplan.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="viewMode" class="mB10">
                <p class="fc-input-label-txt pB5">Select mode</p>
                <el-select
                  v-model="cardDataObj.viewMode"
                  filterable
                  collapse-tags
                  placeholder="Mode"
                  class="db-filter fc-tag width100"
                  v-tippy
                >
                  <el-option
                    v-for="(view, index) in viewModes"
                    :key="index"
                    :label="view.label"
                    :value="view.value"
                  >
                  </el-option>
                  <el-option
                    :key="213"
                    label="VAV readings"
                    value="vavreadings"
                    v-if="$account.org.id === 343 || $account.org.id === 155"
                  ></el-option>
                  <el-option
                    :key="213"
                    label="Custom script"
                    value="customscript"
                    v-if="$account.org.id === 410"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row
            class="mB10"
            :gutter="20"
            v-if="cardDataObj.viewMode === 'readings'"
          >
            <el-col :span="12">
              <el-form-item prop="viewMode" class="mB10">
                <p class="fc-input-label-txt pB5">Asset Category</p>
                <el-select
                  filterable
                  @change="loadAssetFields()"
                  v-model="cardDataObj.viewParams.assetCategoryId"
                  placeholder="Select"
                  class=" fc-input-full-border2 width100"
                >
                  <el-option
                    v-for="(category, index) in assetCategory"
                    :key="index"
                    :label="category.name"
                    :value="category.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="viewMode" class="mB10">
                <p class="fc-input-label-txt pB5">Asset Field Name</p>
                <el-select
                  filterable
                  @change="setReadingFieldModuleName()"
                  v-model="cardDataObj.viewParams.readingFieldName"
                  placeholder="Select"
                  class=" fc-input-full-border2 width100"
                >
                  <el-option
                    v-for="(field, index) in assetFields"
                    :key="index"
                    :label="field.displayName"
                    :value="field.name"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :cardDataObj="cardDataObj"
            :cardData="previewData"
            :cardState="previewState"
            :isLoading="isPreviewLoading"
          ></Card>
        </div>
      </div>
    </div>
    <div class="d-flex mT-auto form-action-btn">
      <el-button
        class="form-btn f13 bold secondary text-center text-uppercase"
        @click="onGoBack()"
        >Cancel</el-button
      >
      <el-button
        type="primary"
        class="form-btn f13 bold primary m0 text-center text-uppercase"
        @click="save()"
        >Save</el-button
      >
    </div>
  </div>
</template>

<script>
import Config from '../base/Config'
import Card from './Card'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
export default {
  name: 'WebCard1',
  extends: Config,
  props: [
    'isNew',
    'cardType',
    'onClose',
    'onGoBack',
    'onCardSave',
    'onCardUpdate',
    'closePopup',
  ],
  components: {
    Card,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSites')
    this.loadFloors()
    this.getFloorPlanList()
  },
  mounted() {
    if (
      this.cardDataObj.viewParams &&
      this.cardDataObj.viewParams.assetCategoryId
    ) {
      this.loadAssetFields(true)
    }
  },
  computed: {
    ...mapState({
      sites: state => state.sites,
      assetCategory: state => state.assetCategory,
    }),
    filteredFloorPLanList() {
      if (this.cardDataObj.siteId > 0 && this.cardDataObj.floorId) {
        return this.floorPlanList.filter(floorPLan => {
          if (
            floorPLan.siteId === this.cardDataObj.siteId &&
            floorPLan.floorId === this.cardDataObj.floorId
          ) {
            return floorPLan
          }
        })
      } else if (this.cardDataObj.siteId > 0) {
        return this.floorPlanList.filter(floorPLan => {
          if (floorPLan.siteId === this.cardDataObj.siteId) {
            return floorPLan
          }
        })
      }
      return this.floorPlanList
    },
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: null,
            value: null,
          }
    },
  },
  data() {
    return {
      cardLayout: `floorplan_layout_1`,
      isPreviewLoading: false,
      floors: [],
      assetFields: [],
      resourceProps: [
        'title',
        'floorPlanId',
        'siteId',
        'floorId',
        'viewMode',
        'viewParams',
      ],
      cardDataObj: {
        title: '',
        siteId: -1,
        floorPlanId: null,
        floorId: -1,
        viewMode: 'default',
        viewParams: {
          assetCategoryId: null,
          readingFieldName: null,
          readingModule: null,
        },
      },
      layout: {
        w: 48,
        h: 24,
      },
      cardStateObj: {
        canResize: true,
        styles: {
          hideHeader: true,
        },
      },
      result: null,
      floorPlanList: [],
      viewModes: [
        {
          label: 'Default',
          value: 'default',
        },
        {
          label: 'Maintenance',
          value: 'maintenance',
        },
        {
          label: 'Control points',
          value: 'control_points',
        },
        {
          label: 'Readings',
          value: 'readings',
        },
        {
          label: 'Desk Manager',
          value: 'employee',
        },
      ],
    }
  },
  methods: {
    validateProperty() {
      return {
        siteId: () => false,
        floorId: () => false,
        viewMode: () => false,
        viewParams: data => {
          if (data.viewMode === 'readings' && data.viewParams) {
            return this.validateReadingModeFields(data.viewParams)
          }
          return false
        },
        scriptModeInt: () => false,
      }
    },
    validateReadingModeFields(viewParams) {
      let { assetCategoryId, readingFieldName, readingModule } = viewParams
      let error = false
      if (isEmpty(readingFieldName)) {
        error = true
      } else if (isEmpty(assetCategoryId)) {
        error = true
      } else if (isEmpty(readingModule)) {
        error = true
      }
      return error
    },
    loadFloors() {
      this.$http
        .get('/v2/module/data/list?moduleName=floor')
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.floors = data.result.moduleDatas
          }
        })
    },
    getFloorPlanList() {
      let self = this
      self.$http.get('/v2/floorPlan/getall').then(({ data }) => {
        if (data.responseCode === 0) {
          this.floorPlanList = data.result.floorPlans
        }
      })
    },
    setReadingFieldModuleName() {
      if (this.cardDataObj.viewParams.readingFieldName) {
        let fieldname = this.cardDataObj.viewParams.readingFieldName
        let fieldobj = this.assetFields.find(rt => rt.name === fieldname)
        this.cardDataObj.viewParams.readingModule = fieldobj.module.name
      }
    },
    loadAssetFields(edit) {
      if (!edit) {
        this.cardDataObj.viewParams.readingFieldName = null
      }
      this.$util
        .loadAssetReadingFields(-1, this.cardDataObj.viewParams.assetCategoryId)
        .then(fields => {
          this.assetFields = fields
        })
    },
  },
}
</script>

<style scoped lang="scss">
.card-builder-popup .container .section.preview-panel {
  flex-basis: 80%;
}
.card-wrapper {
  width: 500px;
  height: 400px;
}
</style>
