<template>
  <div class="height100 user-layout">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Space Readings</div>
        <div class="heading-description">List of all Space Readings</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click="showDialog = true"
          class="setup-el-btn"
          >New Space Readings</el-button
        >
        <!-- <el-button type="primary" @click="showDialog = true" class="setup-el-btn">New Space</el-button> -->
        <new-asset-reading
          :isNew="true"
          resourceType="Space"
          @saved="aftersave"
          :visibility.sync="showDialog"
        ></new-asset-reading>
        <!-- <new-space-reading resourceType="Space" @saved="aftersave" :visibility.sync="showDialog"></new-space-reading> -->
      </div>
    </div>
    <subheader
      :menu="subheaderMenu"
      parent="app/setup/assetsettings/"
    ></subheader>
    <div class="container-scroll">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table mT70">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">SPACE CATEGORY</th>
                <th class="setting-table-th setting-th-text">FIELD NAME</th>
                <th class="setting-table-th setting-th-text">FIELD TYPE</th>
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
            <tbody v-else-if="spaceReadingFields.length === 0">
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
                  {{
                    field.categoryId == -1
                      ? 'All Spaces'
                      : $store.getters.getSpaceCategoryPickList()[
                          field.categoryId
                        ]
                  }}
                </td>
                <td>
                  {{ field.displayName }}
                </td>
                <td>
                  {{ field.dataTypeEnum }}
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
    <edit-site-reading
      v-if="editshowDialog"
      :model="model"
      :visibility.sync="editshowDialog"
      :unitDetails="metricsUnits"
      @saved="aftersave"
      resourceType="space"
    ></edit-site-reading>
  </div>
</template>
<script>
import NewAssetReading from 'pages/setup/NewAssetReading'
import Subheader from '@/Subheader'
import EditSiteReading from 'pages/setup/EditSiteReading'
export default {
  title() {
    return 'Space Reading'
  },
  components: {
    NewAssetReading,
    Subheader,
    EditSiteReading,
  },
  data() {
    return {
      metricsUnits: null,
      loading: true,
      spaceCategories: [],
      spaceReadingFields: [],
      loadForm: false,
      model: {},
      editshowDialog: false,
      showDialog: false,
      subheaderMenu: [
        {
          label: 'Asset Readings',
          path: { path: '/app/setup/assetsettings/readings' },
        },
        {
          label: 'Site',
          path: { path: '/app/setup/assetsettings/sitereadings' },
        },
        {
          label: 'Building',
          path: { path: '/app/setup/assetsettings/buildingreadings' },
        },
        {
          label: 'Floor',
          path: { path: '/app/setup/assetsettings/floorreadings' },
        },
        {
          label: 'Space',
          path: { path: '/app/setup/assetsettings/spacereadings' },
        },
      ],
    }
  },
  mounted: function() {
    this.loadDefaultMetricUnits()
    this.getAllSpaceReadings()
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
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
      this.$http.get('/reading/getallspacereadings').then(function(response) {
        self.spaceCategories = response.data
        for (let categoryId in response.data) {
          let moduleJson = response.data[categoryId]
          for (let moduleIndex in moduleJson) {
            let readingFields = moduleJson[moduleIndex].fields
            for (let fieldIndex in readingFields) {
              let field = readingFields[fieldIndex]
              field['categoryId'] = categoryId
              self.spaceReadingFields.push(field)
            }
          }
        }
        console.log(
          'fff moduleJson33' + JSON.stringify(self.spaceReadingFields)
        )
        self.loading = false
      })
    },
    newSpaceReadingDialog() {
      this.$refs.newSpaceReading.open()
    },
    editSpaceCategoryDialog(data) {
      this.$refs.editSpaceCategory.open(data)
    },
    deleteCategories(data, index, id) {
      console.log('data : ', data)
      console.log('delete data', { skill: data })
      let that = this
      that.$http
        .post('/spacecategory/delete', { spaceCategoryIds: [id] })
        .then(function(response) {
          console.log(
            '########### delete spaceCategory',
            response.data.resultAction
          )
          if (response.data.resultAction) {
            that.spaceCategories.splice(index, 1)
            that.$message.success(response.data.resultMessage)
          } else {
            that.$message.error(response.data.resultMessage)
          }
        })
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
  },
}
</script>
<style>
.setting-header .add-btn {
  position: fixed;
  right: 0;
  z-index: 111;
}
.user-layout .setting-page-btn {
  top: 7.5rem !important;
}
.user-layout .setting-Rlayout {
  padding: 1rem 1.7rem !important;
}
.add-btn {
  position: relative;
  right: 21px;
  top: -47px;
  z-index: 1111;
}
table.dataTable > tbody > tr > td {
  padding: 10px;
  vertical-align: middle;
  border-spacing: 0;
  border-collapse: collapse;
}

table.dataTable thead > tr > th {
  padding: 5px 10px 2px 10px;
  color: #6f7175;
  vertical-align: top;
  font-weight: 400;
  font-size: 13px;
  border-bottom: 0px;
}

table.dataTable tr.odd {
  background-color: '#fafafa';
}

table.dataTable tr.even {
  background-color: blue;
}

div.dataTables_info {
  padding-top: 8px;
  white-space: nowrap;
  padding-left: 10px;
}

div.dataTables_info,
div.dataTables_paginate {
  padding: 18px;
  white-space: nowrap;
}

div.row-title {
  font-weight: 400;
}

div.row-subtitle {
  font-weight: 400;
  color: #6f7175;
}

.dataTable tbody tr:hover {
  background: #fafafa;
  cursor: pointer;
}

.dataTable tr th .checkbox {
  padding-left: 17px !important;
}

.dataTable tbody tr:last-child td {
  border-bottom: 1px solid #e7e7e7 !important;
}

.dataTable > tbody > tr:first-child > td {
  border-top: 0px;
}

div.row.content-center {
  padding-top: 100px;
  padding-bottom: 144px;
}

table.dataTable.dtr-inline.collapsed > tbody > tr > td:first-child:before,
table.dataTable.dtr-inline.collapsed > tbody > tr > th:first-child:before {
  background-color: #50ca7c;
  font-size: 16px;
  line-height: 16px;
  display: none;
}
.no-screen-msg .row-title {
  font-size: 17px;
  color: #212121;
  padding: 10px 0;
}
.no-screen-msg .row-subtitle {
  font-size: 13px;
  padding: 1px 0px;
}

.dataTable tbody tr.selected {
  background: rgba(14, 153, 227, 0.1);
}
.record-list,
.record-summary {
  padding: 0;
  transition: all 0.3s;
}
.more-actions .dropdown-toggle {
  color: #d8d8d8;
  font-size: 18px;
}

.more-actions .dropdown-toggle:hover {
  color: #000000;
}

.more-actions .dropdown-menu {
  right: 0;
  left: initial;
}
.toggle-switch label {
  position: relative;
  display: block;
  height: 12px;
  width: 30px;
  background: #999;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.08s linear;
}

.toggle-switch label:after {
  position: absolute;
  left: 0;
  top: -2px;
  display: block;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #fafafa;
  box-shadow: rgba(0, 0, 0, 0.4) 0px 1px 3px 0px;
  content: '';
  transition: 0.08s linear;
}

.toggle-switch label:active:after {
  transform: scale(1.15, 0.85);
}

.toggle-switch .checkbox:checked ~ label {
  background: rgba(80, 202, 124, 0.5);
}

.toggle-switch .checkbox:checked ~ label:after {
  left: 14px;
  background: #50ca7c;
}

.toggle-switch .checkbox:disabled ~ label {
  background: #d5d5d5;
  cursor: not-allowed;
  pointer-events: none;
}

.toggle-switch .checkbox:disabled ~ label:after {
  background: #bcbdbc;
}
</style>
