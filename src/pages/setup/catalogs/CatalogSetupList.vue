<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('servicecatalog.setup.service_catalogs') }}
        </div>
        <div class="heading-description">
          {{ $t('servicecatalog.setup.list_of_sc') }}
        </div>
      </div>
      <div class="setting-page-btn catalog-btn">
        <el-select
          @change="loadCatalogsList"
          v-model="appId"
          placeholder="Select App"
          filterable
          class="fc-input-full-border2 width200px pR15"
        >
          <el-option
            v-for="app in apps"
            :key="app.linkName"
            :label="app.name"
            :value="app.id"
          >
          </el-option>
        </el-select>

        <el-dropdown
          class="mL10 pointer stateflow-btn-wrapper"
          trigger="click"
          @command="catalogBtn => openCreationDialog({}, catalogBtn)"
        >
          <el-button type="primary">
            {{ $t('servicecatalog.setup.add_new')
            }}<i class="el-icon-arrow-down pL10 font-black"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(catalogBtn, index) in catalogBtnDropDwn"
              :key="index"
              :command="catalogBtn.value"
              >{{ getCatalogBtnLabel(catalogBtn.label) }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <div class="pL30 d-flex">
      <el-select
        placeholder="Select"
        class="fc-input-full-border-select2"
        v-model="activeCatalog"
        value-key="id"
        filterable
        @change="setActiveCatalog"
      >
        <el-option
          v-for="(catalog, index) in catalogGroups"
          :key="`${catalog.name} ${index}`"
          :label="catalog.name"
          :value="catalog"
        ></el-option>
      </el-select>
      <div class="pL15">
        <el-button
          class="setup-el-btn"
          @click="canShowGroupList = true"
          style="height: 40px !important;"
          >{{ $t('common.wo_report.show_all_categories') }}</el-button
        >
      </div>
    </div>
    <div class="container-scroll mT30 catalog-setup-container">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12 overflow-x">
          <v-infinite-scroll
            :loading="loadingMoreCatalogs"
            @bottom="loadCatalogsList({ loadMore: true })"
            :offset="10"
            class="catalog-scroll-container"
          >
            <table class="setting-list-view-table">
              <thead>
                <tr>
                  <th
                    class="setting-table-th setting-th-text"
                    style="width: 25%"
                  >
                    {{ $t('common._common.name') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('common.wo_report.report_description') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('common._common.form') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('home.reports.module') }}
                  </th>
                  <th class="setting-table-th setting-th-text"></th>
                </tr>
              </thead>
              <tbody v-if="isLoading || isCatalogLoading">
                <tr>
                  <td colspan="100%" class="text-center">
                    <spinner
                      :show="isLoading || isCatalogLoading"
                      size="80"
                    ></spinner>
                  </td>
                </tr>
              </tbody>
              <tbody v-else-if="filteredCatalogItems.length === 0">
                <tr>
                  <td colspan="100%" class="text-center">
                    {{ $t('servicecatalog.setup.emptytext') }}
                  </td>
                </tr>
              </tbody>
              <tbody v-else>
                <tr
                  class="tablerow visibility-visible-actions "
                  v-for="(catalogItem, index) in filteredCatalogItems"
                  :key="index"
                >
                  <td>
                    <div>
                      <div class="mL10">
                        <div class="label-txt3-14">
                          {{ catalogItem.name }}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div class="label-txt3-14">
                      {{ catalogItem.description }}
                    </div>
                  </td>
                  <td>
                    <div
                      class="label-txt3-14 blue-background-link"
                      @click="redirectToBuilder(catalogItem)"
                    >
                      {{
                        catalogItem.form && catalogItem.form.displayName
                          ? catalogItem.form.displayName
                          : '---'
                      }}
                    </div>
                  </td>
                  <td>
                    <div class="label-txt3-14">
                      {{ catalogItem.module.displayName }}
                    </div>
                  </td>
                  <td class="pR0">
                    <div class="d-flex self-center mL-auto">
                      <inline-svg
                        @click.native="openSharingPermissionDialog(catalogItem)"
                        src="svgs/share"
                        class="d-flex mR15 self-center"
                        iconClass="icon icon-xs fill-grey pointer"
                        :title="$t('common._common.share')"
                        v-tippy
                        data-position="top"
                        data-arrow="true"
                      ></inline-svg>
                      <inline-svg
                        @click.native="
                          openCreationDialog(
                            catalogItem,
                            'canAddCatalogItem',
                            true
                          )
                        "
                        src="svgs/edit-pencil"
                        class="d-flex mR15 self-center"
                        iconClass="icon icon-xs fill-grey pointer"
                      ></inline-svg>
                      <inline-svg
                        @click.native="showConfirmDelete(catalogItem)"
                        src="svgs/delete"
                        class="d-flex mR15 self-center"
                        iconClass="icon icon-sm icon-remove fill-grey pointer"
                      ></inline-svg>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="loadingMoreCatalogs" class="catalog-loading-more">
              <Spinner :show="loadingMoreCatalogs"></Spinner>
            </div>
          </v-infinite-scroll>
        </div>
      </div>
    </div>
    <!--
      New form dialog box
    -->
    <ViewSharingPermission
      v-if="showSharingPermissionDialog"
      :showDialog.sync="showSharingPermissionDialog"
      :currentUserId="currentUserId"
      :sharingDetails="selectedCatalogItem.sharing"
      :selectedAppId="appId"
      :selectedApp="apps.find(app => app.id === appId) || {}"
      @sharing="saveViewSharingPermission"
      :roles="roles"
    >
    </ViewSharingPermission>
    <CatalogGroupCreation
      v-if="canAddCatalogGroup"
      :serviceCatalogGroup="catalogGroupObj"
      :canAddCatalogGroup.sync="canAddCatalogGroup"
      :isEdit="isEdit"
      @updateCatalogGroup="updateCatalogGroup"
    ></CatalogGroupCreation>
    <CatalogItemCreation
      v-if="canAddCatalogItem"
      :isEdit="isEdit"
      :serviceCatalogItem="selectedCatalogItem"
      :appId="appId"
      :canAddCatalogItem.sync="canAddCatalogItem"
      :catalogGroups="filteredCatalogGroups"
      :cataLogModulesList.sync="cataLogModulesList"
      @setActiveCatalog="setActiveCatalog"
    >
    </CatalogItemCreation>
    <CatalogGroupList
      v-if="canShowGroupList"
      :canShowGroupList.sync="canShowGroupList"
      :catalogGroups="filteredCatalogGroups"
      @openCreationDialog="openCreationDialog"
    ></CatalogGroupList>
  </div>
</template>
<script>
import CatalogListMixin from '@/mixins/catalogs/CatalogsSetupMixin.vue'
import CatalogGroupCreation from 'pages/setup/catalogs/CatalogGroupCreation'
import CatalogItemCreation from 'pages/setup/catalogs/CatalogItemCreation'
import CatalogGroupList from 'pages/setup/catalogs/CatalogGroupList'
import Spinner from '@/Spinner'
import VInfiniteScroll from 'v-infinite-scroll'
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import { API } from '@facilio/api'
import ViewSharingPermission from './SharingPermission'
import { mapGetters } from 'vuex'

export default {
  mixins: [CatalogListMixin],
  components: {
    Spinner,
    VInfiniteScroll,
    CatalogGroupCreation,
    CatalogItemCreation,
    CatalogGroupList,
    ViewSharingPermission,
  },
  async created() {
    this.isLoading = true
    await this.loadCatalogsData()
    this.isLoading = false
  },
  props: ['isApp'],

  mounted() {
    this.availableApps()
  },

  data() {
    return {
      showSharingPermissionDialog: false,
      appId: null,
      apps: [],
      catalogGroups: [],
      filteredCatalogItems: [],
      activeCatalog: null,
      searchText: null,
      isLoading: false,
      isCatalogLoading: false,
      loadingMoreCatalogs: false,
      perPage: 50,
      page: 1,
      allCatalogsLoaded: false,
      isEmptySearch: false,
      catalogBtnDropDwn: [
        {
          label: 'group',
          value: 'canAddCatalogGroup',
        },
        {
          label: 'item',
          value: 'canAddCatalogItem',
        },
      ],
      catalogGroupObj: {
        name: null,
        description: null,
      },
      catalogItemObj: {
        name: null,
        description: null,
        groupId: null,
        moduleId: null,
        formId: null,
      },
      selectedCatalogItem: null,
      isEdit: false,
      canAddCatalogGroup: false,
      canAddCatalogItem: false,
      canShowGroupList: false,
      cataLogModulesList: [],
      roles: [],
    }
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
    filteredCatalogGroups() {
      let { catalogGroups } = this
      if (!isEmpty(catalogGroups)) {
        return catalogGroups.filter(catalogGroup => !isEmpty(catalogGroup.id))
      }
      return []
    },
    currentUserId() {
      let { getCurrentUser } = this
      let userDetails = getCurrentUser()
      let { id } = userDetails
      return id || null
    },
  },
  methods: {
    openSharingPermissionDialog(selectedObj) {
      this.$set(this, 'selectedCatalogItem', selectedObj)
      this.showSharingPermissionDialog = true
    },
    saveViewSharingPermission(sharing) {
      let { selectedCatalogItem } = this
      let { module } = selectedCatalogItem
      let { name } = module
      selectedCatalogItem.sharing = sharing
      let data = {
        moduleName: name,
        serviceCatalog: selectedCatalogItem,
      }
      let url = `v2/servicecatalog/addOrUpdate`
      API.post(url, data).then(({ error }) => {
        if (error) {
          this.$message.error(
            error.message || 'Error Occured while saving the permission'
          )
        } else {
          this.selectedCatalogItem.sharing = sharing
          this.$message.success(this.$t(`Saved Successfully`))
        }
      })
    },
    availableApps() {
      API.get(`v2/application/list`).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.apps = data.application
          let defaultApp =
            this.apps.find(app => app.linkName === 'newapp') || this.apps[0]
          this.appId = defaultApp.id
          this.loadCatalogsList()
        }
      })
    },
    redirectToBuilder(catalogItem) {
      let { form, module } = catalogItem
      let moduleName = module.name
      let { id } = form

      if (module.custom) {
        let currentPath = this.$router.resolve({
          name: 'modules-details',
          params: { moduleName },
        }).href

        this.$router.push({
          path: `${currentPath}/layouts/${id}/edit`,
        })
      }
    },
    openCreationDialog(selectedObj, canShow, isEdit = false) {
      if (canShow === 'canAddCatalogGroup') {
        let catalogGroupObj = this.getDefaultCatalogGroupObj()
        let obj = isEmpty(selectedObj)
          ? deepCloneObject(catalogGroupObj)
          : deepCloneObject(selectedObj)
        this.$set(this, 'catalogGroupObj', obj)
      } else {
        let catalogItemObj = this.getDefaultCatalogItemObj()
        let obj = isEmpty(selectedObj)
          ? deepCloneObject(catalogItemObj)
          : deepCloneObject(selectedObj)
        this.$set(this, 'selectedCatalogItem', obj)
      }
      this.$set(this, 'isEdit', isEdit)
      this.$set(this, canShow, true)
    },
    getCatalogBtnLabel(label) {
      return this.$t(`servicecatalog.setup.${label}`)
    },
    getDefaultCatalogGroupObj() {
      return {
        name: null,
        description: null,
      }
    },
    getDefaultCatalogItemObj() {
      let { appId } = this
      return {
        name: null,
        description: null,
        groupId: null,
        moduleId: null,
        formId: null,
        appId: appId,
      }
    },
    showConfirmDelete(serviceItem) {
      let dialogObj = {
        title: 'Delete Service Item',
        message: 'Are you sure you want to delete this service item?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.deleteServiceItem(serviceItem)
        }
      })
    },
    deleteServiceItem({ id }) {
      let url = `v2/servicecatalog/delete`
      let data = { id }

      this.$http
        .post(url, data)
        .then(({ data: { message, responseCode } }) => {
          if (responseCode === 0) {
            let serviceCatalogItems = this.filteredCatalogItems.filter(
              item => item.id !== id
            )
            this.$set(this, 'filteredCatalogItems', serviceCatalogItems)
            this.$message.success(
              this.$t('common.wo_report.successfully_deleted')
            )
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
    updateCatalogGroup(catalogGroup) {
      let { catalogGroups, isEdit } = this
      if (!isEmpty(catalogGroup)) {
        if (isEdit) {
          let selectedCatalogGroupIndex = (catalogGroups || []).findIndex(
            group => group.id === catalogGroup.id
          )
          catalogGroups.splice(selectedCatalogGroupIndex, 1, catalogGroup)
        } else {
          catalogGroups.push(catalogGroup)
        }
      }
      this.$set(this, 'catalogGroups', catalogGroups)
    },
    setActiveCatalog() {
      this.$set(this, 'page', 1)
      this.$set(this, 'allCatalogsLoaded', false)
      this.loadCatalogsList({})
      this.loadRoles()
    },
  },
}
</script>
<style lang="scss">
.catalog-btn {
  .stateflow-btn-wrapper {
    &.el-dropdown {
      .el-dropdown__caret-button {
        &::before {
          background: none;
        }
      }
    }
    .el-button-group {
      .el-button {
        &:active,
        &:focus {
          background-color: #39b2c2;
        }
      }
      .el-button--primary {
        box-shadow: none !important;
      }
    }
  }
}
</style>
