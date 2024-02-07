<template>
  <el-dialog
    title="Add Control Points"
    :append-to-body="true"
    custom-class="control-command-card-dialog fc-dialog-center-container"
    :visible.sync="visibility"
    width="30%"
    :before-close="cancel"
  >
    <div class="height350">
      <div>
        <div class="reading-card-header fc-input-label-txt">
          Select Asset Category
        </div>
        <!-- <el-select class="fc-input-full-border-select2 width100" collapse-tags> -->
        <el-select
          v-model="points.assetCategoryId"
          filterable
          @change="loadThresholdFields(true, assetCategoryId)"
          placeholder="Select"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="(category, index) in assetCategory"
            :key="index"
            :label="category.displayName"
            :value="parseInt(category.id)"
          ></el-option>
        </el-select>
      </div>
      <div>
        <div class="reading-card-header fc-input-label-txt pT20">
          Select Asset
        </div>
        <el-input
          v-model="resourceLabel"
          disabled
          class="fc-border-select fc-input-full-border-select2 width100"
        >
          <i
            @click="chooserVisibility = true"
            slot="suffix"
            style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
            class="el-input__icon el-icon-search"
          ></i>
        </el-input>
      </div>
      <div>
        <div class="reading-card-header fc-input-label-txt pT20">
          Select Reading
        </div>

        <el-select
          filterable
          v-model="points.readingId"
          placeholder="Select"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="field in thresholdFields"
            :key="field.name + field.id"
            :label="field.displayName"
            :value="field.name"
          ></el-option>
        </el-select>
        <!-- <el-select
        v-model="modelData"
        placeholder="Please select a Field"
        class="fc-input-full-border2"
        filterable
        no-data-text="Loading..."
      >
        <el-option
          :label="reading.displayName"
          :value="reading.name"
          v-for="(reading, index) in assetReadings"
          :key="index"
        ></el-option>
      </el-select>-->
        <space-asset-chooser
          @associate="associateResource"
          :visibility.sync="chooserVisibility"
          :initialValues="{}"
          :query="resourceQuery"
          :showAsset="false"
          picktype="asset"
        ></space-asset-chooser>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="cancel">Cancel</el-button>
        <el-button class="modal-btn-save" @click="addPoints()">Save</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import RuleMixin from '@/mixins/RuleMixin'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import { mapState } from 'vuex'

export default {
  components: {
    SpaceAssetChooser,
  },
  mixins: [RuleMixin],
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    resourceLabel() {
      if (this.asset && this.asset.id > 0) {
        return this.asset.name
      }
      return null
    },
  },
  props: ['modelData', 'visibility'],
  data() {
    return {
      points: {
        assetCategoryId: null,
        asset: null,
        readingId: null,
      },
      resourceQuery: null,
      assetCategoryId: null,
      asset: null,
      readingId: null,
      controlCommand: {},
      chooserVisibility: false,
      thresholdFields: [],
      assetReadings: null,
      controlableAssets: null,
    }
  },
  watch: {},
  methods: {
    associateResource(selectedObj) {
      if (selectedObj) {
        this.points.asset = selectedObj
        this.asset = selectedObj
      }
      this.chooserVisibility = false
      this.resourceQuery = null
    },
    cancel() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    addPoints() {
      this.$emit('points', this.points)
      this.$emit('update:visibility', false)
    },
  },
  mounted() {
    // this.getControlableAssets()
  },
}
</script>
