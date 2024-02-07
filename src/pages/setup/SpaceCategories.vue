<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common._common.space_categories') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_space_category') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click="redirectToFormNew()"
          class="setup-el-btn"
          >{{ $t('common.wo_report.new_category') }}</el-button
        >
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <spinner v-if="loading" :show="loading" size="80"></spinner>
          <template v-else>
            <el-table
              :data="spaceCategories"
              class="setting-list-view-table"
              :cell-style="{ padding: '12px 30px' }"
              :empty-text="$t('common._common.empty_text')"
              style="width: 100%"
              height="calc(100vh - 200px)"
            >
              <el-table-column
                prop="name"
                :label="$t('common.products._name')"
                width="200"
              >
              </el-table-column>
              <el-table-column
                prop="description"
                :label="$t('common.wo_report._description')"
                width="200"
              >
              </el-table-column>
              <el-table-column
                prop="commonArea"
                :label="$t('common._common.is_common_area')"
                width="200"
              >
                <template v-slot="spacecategory">
                  {{
                    spacecategory.row.commonArea
                      ? $t('common._common.true')
                      : $t('common._common.false')
                  }}
                </template>
              </el-table-column>
              <el-table-column width="150">
                <template v-slot="spacecategory">
                  <div class="d-flex text-center">
                    <i
                      class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.edit')"
                      @click="redirectToFormEdit(spacecategory.row.id)"
                      v-tippy
                    ></i>
                    <i
                      class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.delete')"
                      @click="deleteCategories(spacecategory.row.id)"
                      v-tippy
                    ></i>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </div>
      </div>
    </div>
    <edit-space-category ref="editSpaceCategory"></edit-space-category>
  </div>
</template>
<script>
import EditSpaceCategory from 'pages/setup/EditSpaceCategory'
import { API } from '@facilio/api'

export default {
  title() {
    return 'Space Category'
  },
  components: {
    EditSpaceCategory,
  },
  data() {
    return {
      loading: false,
      spaceCategories: [],
    }
  },
  created() {
    this.loadSpaceCategories()
  },
  methods: {
    redirectToFormNew() {
      this.$router.push({ name: 'space-categories-new' })
    },
    redirectToFormEdit(id) {
      this.$router.push({
        name: 'space-categories-edit',
        params: { id },
      })
    },
    async loadSpaceCategories() {
      this.loading = true
      let { list, error } = await API.fetchAll('spacecategory', {
        page: 1,
        perPage: 5000,
        withCount: true,
      })
      if (!error) {
        this.spaceCategories = list
      } else {
        this.$message.error(this.$t('common._common.could_not_fetch_module'))
      }
      this.loading = false
    },
    async deleteCategories(id) {
      let value = await this.$dialog.confirm({
        title: this.$t('common.wo_report.delete_space_category_title'),
        message: this.$t(
          'common.header.are_you_sure_you_want_to_delete_this_spacecategory'
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let { error } = await API.deleteRecord('spacecategory', id)
        if (!error) {
          let index = this.spaceCategories.findIndex(
            spaceCategory => spaceCategory.id === id
          )
          if (index >= 0) {
            this.spaceCategories.splice(index, 1)
          }
          this.$message.success(
            this.$t('common._common.spacecategory_deleted_successfully')
          )
        } else {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        }
      }
    },
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
