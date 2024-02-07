<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Reading Fields Migration</div>
      </div>
    </div>
    <div class="new-body-modal pB20 pR20" style="height: calc(100vh - 220px);">
      <el-row>
        <el-col :span="10">
          <p class="label-txt2 pb10">Select Category</p>
          <div>
            <el-select
              v-model="categoryId"
              @change="loadReadingFields()"
              filterable
              placeholder="Select Category"
              style="width:80%"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="category in assetCategory"
                :key="category.id"
                :label="category.displayName"
                :value="category.id"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <p class="label-txt2 pb10">Select Custom Fields</p>
          <div>
            <el-select
              v-model="customFieldId"
              filterable
              @change="logMigration(customFieldId)"
              placeholder="Select Custom Field"
              style="width:80%"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="custom in customField"
                :key="custom.id"
                :label="custom.displayName"
                :value="custom.id"
              ></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="10">
          <p class="label-txt2 pb10">Select Default Fields</p>
          <div>
            <el-select
              v-model="defaultFieldId"
              filterable
              placeholder="Select Default Field"
              style="width:80%"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="defaultField in defaultFields"
                :key="defaultField.id"
                :label="defaultField.displayName"
                :value="defaultField.id"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row class="pT30">
        <el-col :span="10">
          <p class="label-txt2 pb10">Select Assets</p>
          <div>
            <el-select
              v-model="selectList"
              placeholder="Select Asset"
              style="width:80%"
              filterable
              multiple
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="resource in resourceList"
                :key="resource.resourceId"
                :label="resource.resourceId"
                :value="resource.resourceId"
              ></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col>
          <el-button @click="migrate"> Migrate</el-button>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  title() {
    return 'Space Category'
  },
  data() {
    return {
      categoryId: null,
      defaultFields: null,
      customField: null,
      selectList: [],
      defaultFieldId: null,
      customFieldId: null,
      resourceList: null,
    }
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },
  mounted: function() {
    this.$store.dispatch('loadAssetCategory')
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  methods: {
    loadReadingFields() {
      if (!isEmpty(this.categoryId)) {
        this.$util
          .loadAssetReadingCustomNDefaultFields(this.categoryId)
          .then(response => {
            this.customField = response[1]
            this.defaultFields = response[0]
          })
      }
    },
    migrate() {
      if (this.customFieldId && this.defaultFieldId) {
        let params = {}
        params.toField = this.customFieldId
        params.fromField = this.defaultFieldId
        params.assetsId = this.selectList
        this.$http.post('v2/fieldMigrate/migrate', params).then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(' Migrated Successfully.')
          } else if (response.data.responseCode === 1) {
            this.$message.console.error('Error Occurred ')
          }
        })
        // this.$message.success(' Data Migration on progress Successfully.')
      }
    },
    logMigration(fieldIds) {
      // if (this.customFieldId && this.defaultFieldId) {
      let params = {}
      params.toField = fieldIds
      params.categoryId = this.categoryId
      // params.fromField = this.defaultFieldId
      this.$http
        .post('v2/fieldMigrate/fieldDependency', params)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.resourceList = response.data.result.resourceList
            console.log('result' + response.data.result)
          }
        })
      //  this.$http.post('v2/fieldMigrate/migrate', params).then(response => {

      // })
    },
    // }
  },
}
</script>
<style scoped>
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
