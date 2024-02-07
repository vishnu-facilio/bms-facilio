<template>
  <div class="height100 user-layout">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common._common.asset_category') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_asset_category') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" @click="newCategory()" class="setup-el-btn">{{
          $t('common._common.add_category')
        }}</el-button>
        <new-asset-category
          v-if="showDialog"
          :visibility.sync="showDialog"
          :isNew="isNew"
          :category="selectedCategory"
          @reloadList="reloadList"
        ></new-asset-category>
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
                  {{ $t('common._common.category_type') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common._common.parent_category') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody>
              <tr
                class="tablerow"
                v-for="(category, index) in assetCategory"
                :key="index"
              >
                <td style="padding-top: 18px;padding-bottom: 18px;">
                  {{ category.displayName }}
                </td>
                <td>{{ category.typeVal }}</td>
                <td>
                  {{
                    category.parentCategoryId
                      ? assetCategory.find(
                          assetCategory =>
                            assetCategory.id === category.parentCategoryId
                        ).displayName
                      : '---'
                  }}
                </td>
                <td>
                  <!-- disabled edit and delete should enable it after handling Asset Category display name  -->
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editCategory(index, category)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      v-if="!category.isDefault"
                      @click="deleteGroup(index, category.id)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <!-- <q-modal ref="createNewModel" position="right" content-classes="fc-create-record" @close="loadForm = false">
    <q-btn class="fc-model-close" flat @click="$refs.createNewModel.close()">
      <q-icon name="close" />
    </q-btn>
    <new-asset-category v-if="loadForm" :isNew="isNew" :category="selectedCategory" @onsave="newCategoryAdded" @canceled="$refs.createNewModel.close()"></new-asset-category>
    </q-modal>-->
    <el-dialog
      :title="$t('common.dashboard.cannot_delete_asset_category')"
      :visible.sync="showCannotDeleteDialog"
      width="40%"
    >
      <h6>{{ $t('common._common.asset_category_entities') }}</h6>
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
import NewAssetCategory from '@/NewAssetCategory'
import Subheader from '@/Subheader'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'

export default {
  components: {
    NewAssetCategory,
    Subheader,
  },
  data() {
    return {
      selectedCategory: null,
      isNew: false,
      loadForm: false,
      showDialog: false,
      showCannotDeleteDialog: false,
      associatedModules: null,
      assetCategory: [],
    }
  },
  created() {
    this.listAssetCategory()
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
    newCategory() {
      this.selectedCategory = null
      this.isNew = true
      this.loadForm = true
      this.showDialog = true
      // this.$refs.createNewModel.open()
    },
    editCategory(index, category) {
      this.selectedCategory = { index: index, value: category }
      this.isNew = false
      this.loadForm = true
      this.showDialog = true
      // this.$refs.createNewModel.open()
    },
    openCannotDelDialog(modules) {
      this.associatedModules = modules
      this.showCannotDeleteDialog = true
    },
    deleteGroup(index, id) {
      let self = this
      self.$dialog
        .confirm({
          title: this.$t('common.wo_report.delete_asset_category'),
          message: this.$t('common.wo_report.delete_asset_category_message'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(async function(value) {
          if (value) {
            // Delete a single record
            let { error } = await API.deleteRecord('assetcategory', id)

            if (!error) {
              self.$message.success(
                self.$t('common._common.asset_category_deleted_succesfully')
              )
              self.reloadList()
            } else {
              let { data, message } = error || {}
              let { relatedModules } = data || {}
              !isEmpty(relatedModules)
                ? self.openCannotDelDialog(relatedModules)
                : self.$message.error(message)
            }
          }
        })
    },

    async listAssetCategory() {
      // do api call
      let { list, error } = await API.fetchAll('assetcategory', {
        page: 1,
        perPage: 5000,
        withCount: true,
      })
      if (!error) {
        // if no error, update assetCategory data variable
        this.assetCategory = list
      } else {
        // if error, add the error message
        if (error.status === 400) {
          self.$message.error(this.$t('common._common.could_not_fetch_module'))
        }
      }
    },

    reloadList() {
      this.listAssetCategory()
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
