<template>
  <div class="feedback-type-list">
    <div class v-if="loading">
      <spinner :show="loading" size="80"></spinner>
    </div>

    <div
      class="text-center fc-empty-white flex-middle justify-center flex-col"
      v-else-if="feedbackTypes.length === 0"
    >
      <InlineSvg
        src="svgs/emptystate/approval"
        iconClass="icon icon-xxxlg vertical-middle mR10"
      >
      </InlineSvg>
      <div class="q-item-label nowo-label">No Feedback types created yet.</div>
    </div>

    <div v-else class="mT20">
      <div class="fc-feedback-type-container">
        <div class="fc-feedback-type-sidebar">
          <el-header class="pT20 pB20 pL30 pR30 border-bottom6" height="60">
            <div class="flex-middle justify-content-space">
              <div class="label-txt-black fwBold text-uppercase">Type List</div>
              <div class="pointer feedback-search">
                <f-search class="mL20" v-model="feedbackTypes"></f-search>
              </div>
            </div>
          </el-header>
          <div class="fc-feedback-type-sidebar-scroll">
            <div
              :class="{
                feedbacktypeActive: selectedFeedbackType.id == feedbackType.id,
              }"
              v-for="feedbackType in feedbackTypes"
              :key="feedbackType.id"
              v-loading="loading"
              class="label-txt-black pT15 pB15 pL30 pR30 feedback-type-list visibility-visible-actions"
              @click="selectedFeedbackType = feedbackType"
            >
              {{ feedbackType.name }}
              <i
                class="el-icon-edit feedbacktype-edit visibility-hide-actions"
                @click="showRenameFeedbackType"
              ></i>
            </div>
          </div>
        </div>
        <div class="fc-feedback-type-main">
          <div class="feedback-card-layout">
            <div
              class="flex-middle justify-content-center height400 flex-direction-column"
              v-if="$validation.isEmpty(selectedFeedbackType.catalogs)"
            >
              <img
                src="~assets/svgs/emptystate/task.svg"
                alt
                width="120"
                height="120"
              />
              <el-button
                @click="addCatalogForm"
                class="fc-create-btn mT10"
                style="padding: 15px 30px;"
              >
                {{ $t('setup.feedback.add_catalog') }}</el-button
              >
            </div>
            <div
              class="flex-middle justify-between pL30 pR70 pT20 pB10"
              v-else-if="!$validation.isEmpty(selectedFeedbackType.catalogs)"
            >
              <div class="label-txt-black fwBold">
                {{ $t('setup.feedback.catalogs') }}
              </div>
              <!-- <div
                class="flex-middle green-txt-13 pointer"
                @click="editCatalog"
              >
                <img src="~assets/add-icon.svg" class="mR10" />
                {{ $t('setup.feedback.add_catalog') }}
              </div> -->
              <div
                class="green-txt-13 pointer flex-middle"
                @click="addCatalogForm"
              >
                <img src="~assets/add-icon.svg" class="mR10" />
                {{ $t('setup.feedback.add_catalog') }}
              </div>
            </div>
            <el-row class="mL30 mR30 mT20">
              <draggable
                v-model="selectedFeedbackType.catalogs"
                @change="updateFeedbackType"
              >
                <el-col
                  :span="11"
                  class="feedback-card visibility-visible-actions scale-up-left"
                  v-for="(catalog, index) in selectedFeedbackType.catalogs"
                  :key="index"
                >
                  <el-row class="flex-middle">
                    <el-col :span="1">
                      <img src="~assets/drag.svg" class="mR10 mT8" />
                    </el-col>
                    <el-col :span="3" class="pL10">
                      <div class="catalog-border-img" v-if="catalog.photoUrl">
                        <img
                          :src="catalog.photoUrl"
                          width="30"
                          height="30"
                          class
                        />
                      </div>
                      <div class="catalog-border-img" v-else>
                        <img src="~assets/assets.svg" width="30" height="30" />
                      </div>
                    </el-col>
                    <el-col :span="20" class="pL40">
                      <div class="label-txt3-14 bold">{{ catalog.name }}</div>
                      <div class="label-txt3-13 pT5">
                        {{ catalog.description }}
                      </div>
                    </el-col>
                  </el-row>
                  <i
                    class="el-icon-edit visibility-hide-actions feedback-card-actions edit"
                    @click="editCatalog(catalog)"
                  ></i>
                  <i
                    class="el-icon-delete visibility-hide-actions feedback-card-actions delete"
                    @click="feedbackDelete(index, catalog.id)"
                  ></i>
                </el-col>
              </draggable>
            </el-row>
          </div>
        </div>
      </div>
    </div>
    <feedback-type-form
      v-if="showForm"
      :isEdit="isEdit"
      :kioskContext="selectedFeedbackType"
      :drawerVisibility.sync="showForm"
      @save="handleFormSubmit"
    ></feedback-type-form>

    <el-dialog
      :show-close="false"
      v-if="showCatalogDialog"
      title="Service Categories"
      :visible.sync="showCatalogDialog"
      width="50%"
      class="fc-dialog-center-container service-categories"
      v-bind:append-to-body="false"
    >
      <div class="height450 overflow-auto">
        <div class="container-scroll catalog-setup-container">
          <div class="row setting-Rlayout">
            <div class="col-lg-12 col-md-12 overflow-x">
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
                    <!-- <th class="setting-table-th setting-th-text"></th> -->
                  </tr>
                </thead>

                <tbody>
                  <tr
                    class="tablerow visibility-visible-actions"
                    v-for="(catalog, index) in catalogList"
                    :key="index"
                  >
                    <td>
                      <div>
                        <div class="mL10">
                          <div class="label-txt3-14">{{ catalog.name }}</div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div class="label-txt3-14">{{ catalog.description }}</div>
                    </td>
                    <!-- <td class="pR0" style="width: 20%">
                    <div
                      class="text-left actions text-center mL20"
                      style="margin-top:-3px;"
                    >
                      <i
                        class="el-icon-edit pointer"
                        @click="editCatalogGroup(catalogGroup)"
                      ></i>
                      &nbsp;&nbsp;
                      <i
                        class="el-icon-delete pointer"
                        @click="showConfirmDelete(catalogGroup)"
                      ></i>
                      &nbsp;&nbsp;
                    </div>
                    </td>-->
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- form edit -->
    <el-dialog
      title="Feedback Type"
      :visible.sync="feedbackTypeRename"
      class="fc-dialog-center-container"
      width="30%"
      v-if="selectedFeedbackType"
    >
      <div class="height150">
        <el-form
          @submit.native.prevent="typeRenamed"
          ref="editNameForm"
          :model="renameForm"
        >
          <el-form-item :required="true" prop="feedbackTypeName">
            <el-input
              placeholder="Please input name"
              :autofocus="true"
              class="fc-input-full-border2"
              v-model="renameForm.feedbackTypeName"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="feedbackTypeRename = false" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}</el-button
        >
        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="typeRenamed"
          >{{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-dialog>
    <!--  -->
    <CatalogItemCreation
      v-if="canAddCatalogItem"
      :isEdit="isEdit"
      :serviceCatalogItem="serviceCatalogItem"
      :canAddCatalogItem.sync="canAddCatalogItem"
      :catalogGroups="catalogGroups"
      :cataLogModulesList.sync="cataLogModulesList"
      @setActiveCatalog="setActiveCatalog"
    >
    </CatalogItemCreation>
  </div>
</template>

<script>
import feedbackTypeForm from 'pages/setup/FeedbackTypeForm'
import FSearch from '@/FSearch'
import draggable from 'vuedraggable'
import CatalogItemCreation from 'pages/setup/catalogs/CatalogItemCreation'
import { isEmpty } from '@facilio/utils/validation'

export default {
  title() {
    return 'Feedback Types'
  },
  components: {
    feedbackTypeForm,
    FSearch,
    draggable,
    CatalogItemCreation,
  },

  data() {
    return {
      renameForm: {
        feedbackTypeName: null,
      },

      nameValidation: {
        name: [
          {
            required: true,
            message: 'Name cannot be empty',
            trigger: 'blur',
          },
        ],
      },
      isEdit: false, //form mode , edit or new
      loading: true,
      feedbackTypes: [],
      showForm: false,
      feedbackType: null,
      showCatalogDialog: false,
      catalogList: [],
      input: '',
      selectedFeedbackType: null,
      EditselectedType: false,
      selectedTypename: true,
      feedbackTypeRename: false,
      saving: false,
      canAddCatalogItem: false,
      serviceCatalogItem: null,
      catalogGroups: [{ id: -1, name: 'Complaint' }],
      cataLogModulesList: [],
    }
  },
  created() {
    this.loadFeedbackTypeList().then(() => {
      this.selectedFeedbackType = this.feedbackTypes[0] //first time  ,select first item
    })
  },
  methods: {
    loadCatalogModulesList() {
      let url = `v2/servicecatalog/modules`
      this.$set(this, 'isModulesLoading', true)
      let promise = this.$http
        .get(url)
        .then(({ data: { message, responseCode, result } }) => {
          if (responseCode === 0) {
            let { modules } = result
            this.modulesList = modules
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      return Promise.all([promise]).finally(() =>
        this.$set(this, 'isModulesLoading', false)
      )
    },
    openCatalogDialog(feedbackType) {
      this.catalogList = feedbackType.catalogs
      this.showCatalogDialog = true
    },
    async loadFeedbackTypeList() {
      let resp = await this.$http.get('v2/feedbackType/list', {
        fillCatalogForm: true,
      })
      this.feedbackTypes = resp.data.result.feedbackTypes
      this.loading = false

      // this.selectedFeedbackType=this.feedbackTypes.find(e=>e.id==this.selectedFeedbackType.id)
      //mainting selection if catalogs edited in a type
    },

    openAddFeedbackDialog() {
      this.isEdit = false
      this.showForm = true
    },

    async handleFormSubmit(formModel) {
      if (this.isEdit) {
        formModel.id = this.selectedFeedbackType.id
        this.$http
          .post('v2/feedbackType/update', {
            feedbackType: formModel,
          })
          .then(() => {
            this.showForm = false
            //maintain current feedback type selection but refresh catalog list
            this.refetchCurrentType()
          })
      } else {
        let resp = await this.$http.post('v2/feedbackType/add', {
          feedbackType: formModel,
        }) //go to newly added type after selecting
        let newType = resp.data.result.feedbackType
        this.showForm = false
        this.loadFeedbackTypeList().then(() => {
          this.selectedFeedbackType = this.feedbackTypes.find(e => {
            return e.id == newType.id
          })
        })
      }
    },
    refetchCurrentType() {
      let currentlySelectedTypeId = this.selectedFeedbackType.id
      this.loadFeedbackTypeList().then(() => {
        this.selectedFeedbackType = this.feedbackTypes.find(e => {
          return e.id == currentlySelectedTypeId
        })
      })
    },

    updateFeedbackType() {
      this.$http.post('v2/feedbackType/update', {
        feedbackType: this.selectedFeedbackType,
      })
    },
    feedbackDelete(index, id) {
      let url = `v2/servicecatalog/delete`
      let data = {
        id,
      }
      this.$http
        .post(url, data)
        .then(({ data: { message, responseCode } }) => {
          if (responseCode === 0) {
            this.selectedFeedbackType.catalogs.splice(index, 1)
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },

    onOptionsSelect(command, feedbackType, index) {
      if (command === 'edit') {
        this.isEdit = true
        this.feedbackType = feedbackType
        this.showForm = true
      }
    },

    editCatalog(item) {
      this.serviceCatalogItem = this.$helpers.cloneObject(item)
      this.serviceCatalogItem.complaintType = true
      this.serviceCatalogItem.groupId = -1
      this.isEdit = true
      this.canAddCatalogItem = true
    },
    showRenameFeedbackType() {
      this.feedbackTypeRename = true
      this.renameForm.feedbackTypeName = this.selectedFeedbackType.name
    },

    typeRenamed() {
      this.$refs['editNameForm'].validate(valid => {
        if (valid) {
          this.updateFeedbackType()
          this.selectedFeedbackType.name = this.renameForm.feedbackTypeName
          this.feedbackTypeRename = false
        }
      })
    },
    addCatalogForm() {
      this.initCatalog()
      this.canAddCatalogItem = true
      this.isEdit = false
    },
    initCatalog() {
      this.serviceCatalogItem = {
        name: null,
        description: null,
        complaintType: true,
        groupId: -1,
      }
    },
    setActiveCatalog(serviceCatalog) {
      if (this.isEdit) {
        this.refetchCurrentType()
      } else {
        if (!this.selectedFeedbackType.catalogs) {
          this.selectedFeedbackType.catalogs = []
        }
        this.selectedFeedbackType.catalogs.push(serviceCatalog)
        this.handleFormSubmit(this.selectedFeedbackType)
      }
    },
  },
}
</script>
<style lang="scss">
.feedback-type-list {
  .service-categories {
    .el-dialog__body {
      padding: 0px;

      .container-scroll {
        position: relative;
        height: 450px;
        width: 100%;
        overflow: scroll;
        padding-bottom: 0px;

        .setting-Rlayout {
          padding: 0px !important;
        }
      }
    }
  }
}
</style>
