<template>
  <div>
    <div v-if="formVisibility" class="position-relative">
      <el-dialog
        :append-to-body="true"
        :visible="true"
        @close="cancelForm"
        custom-class="fc-dialog-center-container fc-quick-add-dialog fc-web-form-dialog"
      >
        <div class="fc-pm-main-content-H">
          {{ $t('common.tabs._graphics') }}
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
        <div v-if="loading.graphics || loading.graphicsFolder">
          <spinner
            :show="loading.graphics || loading.graphicsFolder"
            class="mT20"
            size="80"
          ></spinner>
        </div>
        <div v-else style="padding: 20px 40px;">
          <el-row class="mB20" :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt">{{ $t('common.products.name') }}</p>
              <el-input
                :placeholder="$t('common.products.name')"
                class="fc-input-full-border2 width100"
                v-model="graphicsData.name"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt">
                {{ $t('common.wo_report.report_description') }}
              </p>
              <el-input
                type="textarea"
                class="fc-input-full-border-textarea"
                :autosize="{ minRows: 4, maxRows: 4 }"
                v-model="graphicsData.description"
                :placeholder="$t('common.placeholders.enter_description')"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{
                  graphicsContext && !duplicateGraphics
                    ? $('common._common.asset_category')
                    : $t('common._common.asset')
                }}
              </p>
              <div
                v-if="graphicsContext && !duplicateGraphics"
                class="form-input fc-input-full-border-select2"
              >
                <el-input
                  v-model="
                    (getAssetCategory(graphicsData.assetCategoryId) || {})
                      .displayName
                  "
                  type="text"
                  class="el-input-textbox-full-border"
                  :readonly="true"
                >
                </el-input>
              </div>
              <div v-else class="form-input fc-input-full-border-select2">
                <el-input
                  v-model="graphicsData.assetName"
                  type="text"
                  :placeholder="$t('common._common.select_asset')"
                  class="el-input-textbox-full-border"
                  :readonly="true"
                >
                  <i
                    @click="openAssetChooser = true"
                    slot="suffix"
                    style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                    class="el-input__icon el-icon-search"
                  ></i>
                </el-input>
              </div>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('common.products.folder') }}
              </p>
              <el-select
                v-model="graphicsData.parentFolderId"
                :filterable="true"
                :allow-create="true"
                :placeholder="$t('common._common.enter_new_folder_name')"
                class="fc-input-full-border2 width100"
              >
                <el-option
                  v-for="(folder, id) in graphicsFolders"
                  :key="id"
                  :label="folder.name"
                  :value="folder.id"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>

        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            :loading="saving"
            @click="saveGraphicsActions"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <space-asset-chooser
      @associate="associate"
      :showAsset="true"
      :visibility.sync="openAssetChooser"
      :resourceType="[2]"
      :initialValues="assetChooserInitialValue"
      :appendToBody="false"
      :hideSidebar="true"
    ></space-asset-chooser>
  </div>
</template>
<script>
import SpaceAssetChooser from '@/SpaceAssetChooser'
import { mapGetters } from 'vuex'
export default {
  props: ['formVisibility', 'graphicsContext', 'duplicateGraphics'],
  data() {
    return {
      editData: null,
      saving: false,
      graphicsData: {
        name: null,
        description: null,
        assetId: null,
        assetCategoryId: null,
        assetName: null,
        parentFolderId: null,
      },
      openAssetChooser: false,
      graphicsFolders: null,
      loading: {
        graphicsFolder: false,
        graphics: false,
      },
      assetChooserInitialValue: null,
    }
  },
  components: {
    SpaceAssetChooser,
  },
  mounted() {
    if (this.graphicsContext) {
      this.fillEditObject()
    }
    this.loadGraphicsFolders()
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
  },
  methods: {
    loadAsset(assetId) {
      if (!assetId) {
        return
      }
      this.$http.get('/asset/summary/' + assetId).then(response => {
        this.graphicsData.assetName = response.data.asset.name
        this.graphicsData.assetCategoryId = response.data.asset.category
          ? response.data.asset.category.id
          : null
        if (this.duplicateGraphics) {
          this.assetChooserInitialValue = {
            assetCategory: this.graphicsData.assetCategoryId,
          }
        }
      })
    },
    fillEditObject() {
      this.graphicsData = this.$helpers.cloneObject(this.graphicsContext)
      if (this.graphicsData.assetId) {
        this.loadAsset(this.graphicsContext.assetId)
      }
    },
    cancelForm() {
      this.$emit('update:formVisibility', false)
    },
    associate(data) {
      this.graphicsData.assetId = data.id
      this.graphicsData.assetCategoryId = data.category
        ? data.category.id
        : null
      this.graphicsData.assetName = data.name
      this.openAssetChooser = false
    },
    saveGraphicsActions() {
      if (!this.graphicsData.parentFolderId) {
        this.$message.error(
          this.$t('common.header.please_choose_folder_enter_new_folder')
        )
      } else {
        let graphicsFolderId = -1
        try {
          graphicsFolderId = parseInt(this.graphicsData.parentFolderId)
        } catch (err) {
          console.log(err)
        }
        if (isNaN(graphicsFolderId) || graphicsFolderId < 0) {
          let folderParam = {
            graphicsFolder: { name: this.graphicsData.parentFolderId },
          }
          this.$http
            .post('/v2/graphicsFolder/add', folderParam)
            .then(response => {
              if (response.data.responseCode === 0) {
                this.graphicsData.parentFolderId =
                  response.data.result.graphicsFolder.id
                this.addGraphics()
              } else {
                throw new Error(response.data.message)
              }
            })
            .catch(error => {
              this.$message.error(error)
            })
        } else {
          this.addGraphics()
        }
      }
    },
    addGraphics() {
      let url = '/v2/graphics/add'
      let message = this.$t('common._common.graphics_created_successfully')
      if (this.graphicsContext && !this.duplicateGraphics) {
        url = '/v2/graphics/update'
        message = this.$t('common._common.graphics_updated_successfully')
      }
      if (this.duplicateGraphics) {
        message = this.$t('common._common.graphics_duplicated_successfully')
      }

      let temp = this.$helpers.cloneObject(this.graphicsData)
      delete temp.assetName
      let param = {
        graphics: temp,
      }
      this.saving = true
      this.$http
        .post(url, param)
        .then(response => {
          this.saving = false
          if (response.data.responseCode === 0) {
            this.$message.success(message)
            this.$emit('saved', response.data.result.graphics)
            this.cancelForm()
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(error => {
          this.saving = false
          this.$message.error(error)
        })
      this.emitForm = false
    },
    loadGraphicsFolders() {
      this.loading.graphicsFolder = true
      this.$http
        .get('/v2/graphicsFolder/list')
        .then(response => {
          if (response.data.responseCode === 0) {
            this.graphicsFolders = response.data.result.graphicsFolders
          } else {
            throw new Error(response.data.message)
          }
          this.loading.graphicsFolder = false
        })
        .catch(error => {
          this.$message.error(error)
          this.loading.graphicsFolder = false
        })
    },
  },
}
</script>
