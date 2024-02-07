<template>
  <div class="height100 user-layout">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common._common.asset_types') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_asset_types') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" @click="newType" class="setup-el-btn">{{
          $t('common._common.add_type')
        }}</el-button>
        <new-asset-type
          v-if="showDialog"
          :isNew="isNew"
          :assetType="selectedType"
          :visibility.sync="showDialog"
          @reloadList="reloadList"
        ></new-asset-type>
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
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.products._name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common._common.is_movable') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="$validation.isEmpty(assetType)">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('common._common.no_asset_types_created') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(type, index) in assetType"
                :key="index"
              >
                <td style="padding-top: 18px;padding-bottom: 18px;">
                  {{ type.name }}
                </td>
                <td>{{ type.movable ? 'Yes' : 'No' }}</td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editType(index, type)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteGroup(index, type)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <el-dialog
      :title="$t('common.dashboard.cannot_delete_asset_type')"
      :visible.sync="showCannotDeleteDialog"
      width="40%"
    >
      <h6>{{ $t('common._common.asset_associated_following_entities') }}</h6>
      <ul>
        <li v-for="(moduleName, index) in associatedModules" :key="index">
          {{ moduleName }}
        </li>
      </ul>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="showCannotDeleteDialog = false">{{
          $t('common._common.ok')
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import NewAssetType from '@/NewAssetType'
import Subheader from '@/Subheader'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
export default {
  components: {
    NewAssetType,
    Subheader,
  },
  data() {
    return {
      selectedType: null,
      isNew: false,
      showDialog: false,
      associatedModules: null,
      showCannotDeleteDialog: false,
      assetType: [],
      currentModuleName: 'assettype',
    }
  },
  created() {
    this.listAssetType()
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
  methods: {
    newType() {
      this.selectedType = null
      this.isNew = true
      this.showDialog = true
    },
    editType(index, type) {
      this.selectedType = { index, value: type }
      this.isNew = false
      this.showDialog = true
    },
    openCannotDelDialog(modules) {
      this.associatedModules = modules
      this.showCannotDeleteDialog = true
    },
    deleteGroup(index, type) {
      let self = this
      this.$dialog
        .confirm({
          title: this.$t('common.wo_report.delete_asset_type_title'),
          message: this.$t('common.wo_report.are_you_want_delete_asset_type'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(async function(value) {
          if (value) {
            // Delete a single record
            let { error } = await API.deleteRecord(
              self.currentModuleName,
              type.id
            )

            if (!error) {
              self.$message.success(
                self.$t('common._common.asset_type_deleted_sucess')
              )
              self.reloadList()
            } else {
              self.openCannotDelDialog(error.response.data.relatedModules)
              self.$message.error(
                self.$t('common._common.failed_to_delete_asset_type')
              )
            }
          }
        })
    },
    async listAssetType() {
      // do api call
      let { list, error } = await API.fetchAll(this.currentModuleName, {
        page: 1,
        perPage: 5000,
        withCount: true,
      })
      if (!error) {
        // if no error, update assetType data variable
        this.assetType = list
      } else {
        // if error, add the error message
        if (error.status === 400) {
          self.$message.error(this.$t('common._common.could_not_fetch_module'))
        }
      }
    },
    reloadList() {
      this.listAssetType()
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
.user-layout .setting-Rlayout {
  padding: 1rem 1.7rem !important;
}
</style>
