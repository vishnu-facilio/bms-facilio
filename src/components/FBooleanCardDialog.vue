<template>
  <div>
    <el-dialog
      custom-class="fcu-card-builder"
      :modal-append-to-body="false"
      :visible.sync="visibility"
      width="30%"
      top="0%"
      title="SELECT FCU CARD"
      :before-close="closedialog"
    >
      <el-form :model="data" ref="FCUCARD" :label-position="'top'">
        <el-form-item prop="category">
          <p class="grey-text2">NAME</p>
          <el-input
            style="width: 100%"
            :autofocus="true"
            class="addReading-title el-input-textbox-full-border"
            v-model="data.name"
            placeholder="Enter the title"
          ></el-input>
        </el-form-item>
        <el-form-item prop="category">
          <p class="grey-text2">ASSET</p>
          <el-input
            v-model="data.assetName"
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
        </el-form-item>
        <el-form-item prop="category">
          <p class="grey-text2">PERIOD</p>
          <el-select
            style="width: 100%"
            v-model="data.periodId"
            placeholder="Please select a building"
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
    <space-asset-chooser
      @associate="associate"
      :showAsset="true"
      :visibility.sync="openAssetChooser"
      :resourceType="[2]"
      :appendToBody="false"
    ></space-asset-chooser>
  </div>
</template>
<script>
import DateHelper from '@/mixins/DateHelper'
import SpaceAssetChooser from '@/SpaceAssetChooser'
export default {
  mixins: [DateHelper],
  props: ['visibility'],
  components: {
    SpaceAssetChooser,
  },
  data() {
    return {
      data: {},
      openAssetChooser: false,
    }
  },
  mounted() {},
  methods: {
    closedialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    handleIconClick() {
      this.openAssetChooser = true
    },
    associate(data) {
      this.data.asset = data
      this.data.assetName = data.name
      this.openAssetChooser = false
    },
    save() {
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
<style>
.fcu-card-builder {
  margin-top: 15% !important;
}
</style>
