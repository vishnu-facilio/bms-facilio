<template>
  <div class="alarmbar-dialog">
    <el-dialog
      custom-class="fcu-card-builder"
      top="0%"
      :modal-append-to-body="false"
      :visible.sync="visibility"
      width="30%"
      title="ALARM BAR CARD"
      :before-close="closedialog"
      class="fc-dialog-center-container fcu-alarm-dialog"
    >
      <!-- <error-banner :error.sync="error" :errorMessage.sync="errorText" class="alarm-error-msg"></error-banner> -->
      <el-form :model="data" ref="FCUCARD" :label-position="'top'">
        <el-form-item prop="category">
          <p class="fc-input-label-txt">Name</p>
          <el-input
            style="width: 100%"
            :autofocus="true"
            class="addReading-title el-input-textbox-full-border"
            v-model="data.name"
            placeholder="Enter the title"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <p class="fc-input-label-txt">Category</p>
          <el-select
            v-model="data.assetCategoryId"
            filterable
            placeholder="Select"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="(category, index) in assetCategory"
              :key="index"
              :label="category.displayName"
              :value="category.id"
            ></el-option>
          </el-select>
          <span v-if="errorText.category"> {{ errorText.asset }}</span>
        </el-form-item>
        <el-form-item prop="category">
          <p class="fc-input-label-txt">Asset</p>
          <el-input
            v-model="resourceLabel"
            type="text"
            :placeholder="$t('common._common.to_search_type')"
            class="el-input-textbox-full-border"
            :disabled="true"
          >
            <i
              @click="openAssetChooser = true"
              slot="suffix"
              style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
              class="el-input__icon el-icon-search"
            ></i>
          </el-input>
          <span class="error-txt" v-if="errorText.asset">
            {{ errorText.asset }}</span
          >
        </el-form-item>
        <el-form-item prop="category">
          <p class="fc-input-label-txt">Period</p>
          <el-select
            style="width: 100%"
            v-model="data.periodId"
            placeholder="Please select a period"
            class="el-input-textbox-full-border"
          >
            <!-- <el-option label="Last 24 Hours" :value="42"></el-option> -->
            <el-option
              :label="dateRange.label"
              :value="dateRange.value"
              v-for="(dateRange, index) in getdateOperators()"
              :key="index"
              v-if="dateRange.label !== 'Range'"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="modal-dialog-footer row">
        <el-button class="col-6 modal-btn-cancel" @click="closedialog"
          >Cancel</el-button
        >
        <!-- <el-button type="primary" class="col-6 modal-btn-save" @click="update" v-if="assetcardEdit">Update</el-button> -->
        <el-button type="primary" class="col-6 modal-btn-save" @click="save"
          >Ok</el-button
        >
      </span>
    </el-dialog>
    <!-- <space-asset-chooser @associate="associate" :showAsset="true" :initialValues="resourceData" :visibility.sync="openAssetChooser" :resourceType = "[2]" :appendToBody="false" ></space-asset-chooser> -->
    <space-asset-multi-chooser
      @associate="associate"
      :visibility.sync="openAssetChooser"
      :initialValues="resourceData"
      :resourceType="[2]"
      :showAsset="true"
      :disable="true"
    ></space-asset-multi-chooser>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import DateHelper from '@/mixins/DateHelper'
// import SpaceAssetChooser from '@/SpaceAssetChooser'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'

export default {
  mixins: [DateHelper],
  props: ['visibility'],
  components: {
    SpaceAssetMultiChooser,
  },
  data() {
    return {
      data: {
        asset: [],
      },
      errorText: [],
      error: false,
      selectedResourceList: null,
      openAssetChooser: false,
    }
  },
  mounted() {},
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    resourceData() {
      return {
        assetCategory: this.data.assetCategoryId,
      }
    },
    resourceLabel() {
      if (this.data.assetCategoryId > 0) {
        let category = this.assetCategory.filter(
          d => d.id === this.data.assetCategoryId
        )
        let message
        let selectedCount = this.selectedResourceList
          ? this.selectedResourceList.length
          : null
        if (selectedCount) {
          let includeMsg = this.isIncludeResource ? 'included' : 'excluded'
          if (selectedCount === 1) {
            return this.selectedResourceList[0].name
          }
          message =
            selectedCount +
            ' ' +
            category[0].name +
            (selectedCount > 1 ? 's' : '') +
            ' ' +
            includeMsg
        } else {
          message = 'All ' + category[0].name + 's selected'
        }
        return message
      }
      return null
    },
  },
  methods: {
    closedialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    handleIconClick() {
      this.openAssetChooser = true
    },
    associate(data) {
      if (data.resourceList && data.resourceList.length) {
        this.selectedResourceList = data.resourceList
        this.isIncludeResource = data.isInclude
      }
      // this.data.assets = this.selectedResourceList.map(resource => resource.id)
      this.data.asset = this.selectedResourceList.map(resource => resource.id)
      this.data.assetName = data.resourceList[0].name
      this.openAssetChooser = false
    },
    save() {
      this.errorText = []
      this.error = false
      // if (this.data.assetCategoryId > 0 ) {
      //   this.errorText['category'] =  'Please select asset Category and  '
      //   this.error = true
      //   return false
      // }
      if (this.data.asset.length <= 0) {
        this.errorText['asset'] = ' * Please Select Asset '
        this.error = true
        return false
      }
      let data = {
        carddata: this.data,
        selectedObj: this.data.asset,
      }
      this.$emit('update:visibility', false)
      this.$emit('save', data)
    },
    selectlevel() {},
  },
}
</script>
<style lang="scss">
.alarmbar-dialog {
  .fcu-alarm-dialog .el-dialog__header {
    padding: 20px 20px 15px;
    display: flex;
    align-items: center;
  }

  .alarm-error-msg {
    .error-block p {
      margin-bottom: 0;
    }
  }
}
.error-txt {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  letter-spacing: 0.4px;
  text-align: center;
  color: #e16868;
}
.fcu-card-builder {
  margin-top: 15% !important;
}
</style>
