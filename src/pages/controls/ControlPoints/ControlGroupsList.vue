<template>
  <div class="height100 control-group-list-page">
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100 block"
      v-if="openGroupId === -1"
      :class="$route.query.search ? 'fc-list-table-search-scroll' : ''"
    >
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else>
        <el-table
          :data="controlgroupload"
          width="100%"
          height="auto"
          type="index"
          :index="indexMethod"
          :fit="true"
        >
          <template slot="empty">
            <img
              class="mT50"
              src="~statics/noData-light.png"
              width="100"
              height="100"
            />
            <div class="mT10 label-txt-black f14 op6">
              {{ $t('common._products.no_control_groups_available') }}
            </div>
          </template>
          <el-table-column fixed label="Name" width="300" prop="name">
            <template v-slot="scope">
              <div
                class="max-width300px textoverflow-ellipsis"
                :title="scope.row.name"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ scope.row.name }}
              </div>
            </template>
          </el-table-column>

          <el-table-column
            :label="$t('common._common._asset_category')"
            width="250"
            prop=""
          >
            <template v-slot="scope">
              {{ scope.row.assetCategoryContext.displayName }}
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common._common._space_asset')"
            width="200"
            prop=""
          >
            <template v-slot="scope">
              {{ getResourceName(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common.tabs._reading')"
            width="200"
            prop=""
          >
            <template v-slot="scope">
              {{ scope.row.field.displayName }}
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common._common.is_sandbox')"
            width="150"
            prop=""
          >
            <template v-slot="scope">
              <el-switch
                v-model="scope.row.mode"
                :active-value="1"
                :inactive-value="2"
                class="Notification-toggle"
                @change="changestatus(scope.row, scope.row.mode)"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column
            label=""
            width="180"
            prop=""
            class="visibility-visible-actions"
          >
            <template v-slot="scope">
              <button
                class="fc__border__btn visibility-hide-actions"
                style="padding: 5px 16px;"
                @click="setdialogShow(scope.row)"
              >
                {{ $t('common.header._command') }}
              </button>
            </template>
          </el-table-column>
          <el-table-column
            prop
            label
            width="100"
            class="visibility-visible-actions"
          >
            <template v-slot="scope">
              <div class="text-center">
                <i
                  class="el-icon-edit pointer visibility-hide-actions"
                  @click="editControlGroup(scope.row)"
                ></i>
                <i
                  class="el-icon-delete pointer visibility-hide-actions mL10"
                  @click="deleteControlGroup(scope.row)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <SetReadingPopup
      v-if="controlPopup.visible"
      :saveAction="resetControlValue"
      :groupId="controlPopup.reading.id"
      :reading="controlPopup.reading"
      :closeAction="resetControlValue"
      class="setReadingDialog"
    ></SetReadingPopup>
    <new-group-form
      v-if="controlGroupVisible"
      :controlgroup="selectedControlGroup"
      :visibility.sync="controlGroupVisible"
    ></new-group-form>
  </div>
</template>
<script>
import NewGroupForm from 'pages/controls/ControlPoints/NewControlGroupForm'
import SetReadingPopup from '@/readings/SetReadingValue'
import { mapState } from 'vuex'
export default {
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  data() {
    return {
      loading: true,
      controlgroupload: [],
      controlGroupVisible: false,
      selectedControlGroup: null,
      selectedScope: null,
      controlPopup: {
        visible: false,
        reading: null,
      },
    }
  },
  computed: {
    ...mapState({
      count: state => state.rule.count,
    }),

    openGroupId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    assetCategoryList() {
      return this.$store.state.assetCategory
    },
    rules: state => state.rule.rules,
  },
  components: {
    NewGroupForm,
    SetReadingPopup,
  },
  mounted: function() {
    this.loadGroupList()
  },
  methods: {
    loadGroupList() {
      let self = this
      self.loading = true
      let url = '/v2/controlAction/getControlGroups'
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          this.controlgroupload = response.data.result.controlActionGroups
            ? response.data.result.controlActionGroups
            : []
          this.loading = false
        }
      })
    },
    getCategoryName(categoryId) {
      if (categoryId > 0 && this.assetCategoryList) {
        let category = this.assetCategoryList.find(
          category => category.id === categoryId
        )
        if (category) {
          return category.name
        }
      }
    },
    getResourceName(rule) {
      if (rule.assetCategoryId > 0) {
        let message
        let isIncluded = rule.includedResources && rule.includedResources.length
        let selectedCount
        if (isIncluded) {
          selectedCount = rule.includedResources.length
        } else if (rule.excludedResources && rule.excludedResources.length) {
          selectedCount = rule.excludedResources.length
        }
        let categoryName = this.getCategoryName(rule.assetCategoryId)
        if (selectedCount) {
          message =
            (isIncluded ? selectedCount : 'Some') +
            ' ' +
            categoryName +
            (!isIncluded || selectedCount > 1 ? 's' : '')
        } else {
          message = 'All ' + categoryName + 's'
        }
        return message
      } else if (rule.resourceId > 0) {
        return rule.matchedResources[Object.keys(rule.matchedResources)[0]].name
      }
      return '---'
    },
    resetControlValue() {
      this.controlPopup = {
        visible: false,
        reading: null,
      }
    },
    changestatus(scopeRow, mode) {
      let jsonParams = {
        controlGroup: {
          id: scopeRow.id,
          mode: mode,
        },
      }
      let self = this
      self.$http
        .post(`/v2/controlAction/updateControlGroup`, jsonParams)
        .then(response => {
          let res = response.data.result
          if (res && res.controlGroup) {
            self.$message.success(
              'Switched to ' +
                (res.controlGroup.mode === 1 ? 'Sand Box' : 'Live') +
                ' status successfully.'
            )
          }
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    indexMethod(index) {
      return index * 2
    },
    editControlGroup(controlData) {
      this.controlGroupVisible = true
      this.selectedControlGroup = controlData
    },
    setdialogShow(value) {
      this.controlPopup.visible = true
      this.controlPopup.reading = value
    },
    deleteControlGroup(scopeRow) {
      let queryparams = {
        controlGroup: {
          id: scopeRow.id,
        },
      }

      let self = this
      self.$dialog
        .confirm({
          title: this.$t('common.header.delete_control_logic'),
          message: this.$t('common._common.are_you_want_delete_control_logic'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post(`/v2/controlAction/deleteControlGroup`, queryparams)
              .then(function(response) {
                if (response.data.responseCode === 0) {
                  self.$message.success(
                    this.$t(
                      'common.products.control_group_deleted_successfully'
                    )
                  )
                } else {
                  self.$message.error(response.data.message)
                }
              })
          }
        })
    },
  },
}
</script>
<style lang="scss">
.setReadingDialog .f-dialog-content .control-action-reading-field {
  width: 100% !important;
}
</style>
