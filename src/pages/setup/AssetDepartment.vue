<template>
  <div class="height100 user-layout">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common._common.asset_department') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_asset_department') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" @click="addDepartment" class="setup-el-btn">{{
          $t('common._common.add_department')
        }}</el-button>
        <NewAssetDepartment
          v-if="showDialog"
          :department="selectedDepartment"
          @onSave="loadDepartment"
          @onClose="showDialog = false"
        ></NewAssetDepartment>
      </div>
    </div>
    <subheader
      :menu="subheaderMenu"
      parent="app/setup/assetsettings/"
    ></subheader>
    <div
      class="d-flex"
      style="padding: 1rem 1.7rem;margin-top: 60px;height: calc(100vh - 200px);"
    >
      <spinner v-if="loading" :show="loading"></spinner>
      <el-table
        v-else
        :data="assetDepartment"
        :cell-style="{ padding: '12px 30px' }"
        :empty-text="$t('common._common.no_department_created_yet')"
        style="width: 100%"
        height="100%"
      >
        <el-table-column
          :label="$t('common._common.name')"
          prop="name"
        ></el-table-column>

        <el-table-column class="visibility-visible-actions" width="200px">
          <template v-slot="department">
            <div class="text-center flex-middle">
              <template>
                <i
                  class="el-icon-edit edit-icon visibility-hide-actions pR15"
                  data-arrow="true"
                  :title="$t('common.wo_report.edit_asset_department')"
                  v-tippy
                  @click="editDepartment(department.row)"
                ></i>
                <i
                  class="el-icon-delete fc-delete-icon visibility-hide-actions"
                  data-arrow="true"
                  :title="$t('common.wo_report.delete_asset_department_title')"
                  v-tippy
                  @click="deleteDepartment(department.row)"
                ></i>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import NewAssetDepartment from './NewAssetDepartment'
import Subheader from '@/Subheader'
import { AssetDepartment } from './AssetDepartmentModel'
import { getApp } from '@facilio/router'
export default {
  components: {
    NewAssetDepartment,
    Subheader,
  },
  data() {
    return {
      selectedDepartment: null,
      showDialog: false,
      assetDepartment: [],
      loading: false,
    }
  },
  computed: {
    subheaderMenu() {
      let { linkName } = getApp() || {}
      let subheaderMenu = [
        {
          label: this.$t('common._common.category'),
          path: { path: `/${linkName}/setup/assetsettings/category` },
        },
        {
          label: this.$t('common._common.department'),
          path: { path: `/${linkName}/setup/assetsettings/department` },
        },
        {
          label: this.$t('common._common.types'),
          path: { path: `/${linkName}/setup/assetsettings/types` },
        },
      ]

      return subheaderMenu
    },
  },
  async created() {
    this.loadDepartment()
  },
  methods: {
    addDepartment() {
      this.selectedDepartment = null
      this.showDialog = true
    },
    editDepartment(department) {
      this.selectedDepartment = department
      this.showDialog = true
    },
    async deleteDepartment({ id }) {
      let value = await this.$dialog.confirm({
        title: this.$t('common.wo_report.delete_asset_department_title'),
        htmlMessage: this.$t(
          'common.wo_report.delete_asset_department_message'
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.confirm'),
      })

      if (!value) return

      try {
        await AssetDepartment.delete({ id })
        let idx = this.assetDepartment.findIndex(t => t.id === id)

        if (idx > -1) {
          this.assetDepartment.splice(idx, 1)
        }
        this.$message.success(
          this.$t('common._common.asset_department_deleted_success')
        )
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    async loadDepartment() {
      try {
        this.loading = true
        this.assetDepartment = await AssetDepartment.fetchAll('assetdepartment')
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
      this.loading = false
    },
  },
}
</script>

<style>
.fc-create-record {
  width: 40% !important;
  height: 100% !important;
  max-height: 100% !important;
}
.el-icon-close:before {
  content: '';
}
/* .fc-dialog-form .el-dialog__header {
    padding: 0px;
} */
.more-actions:hover {
  background-color: #fafbfc;
  cursor: pointer;
}
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
</style>
