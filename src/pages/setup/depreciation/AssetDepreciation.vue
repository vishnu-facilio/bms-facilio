<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title text-capitalize">
          {{ $t('setup.setup.asset_depreciation') }}
        </div>

        <div class="heading-description">
          {{ $t('common._common.list_of_depreciation_schedule') }}
        </div>
      </div>

      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="openAddOrUpdateForm()"
        >
          {{ $t('common.products.add_depreciation_schedule') }}
        </el-button>
      </div>
    </div>

    <div class="container-scroll mT15" style="height: calc(100vh - 250px);">
      <div class="setting-Rlayout setting-list-view-table">
        <el-row class="header-row">
          <el-col :span="6" class="setting-table-th setting-th-text">
            {{ $t('common.products.name') }}
          </el-col>

          <el-col :span="5" class="setting-table-th setting-th-text">
            {{ $t('common.products.period_type') }}
          </el-col>

          <el-col
            :span="5"
            class="setting-table-th setting-th-text text-center"
          >
            {{ $t('common._common.type') }}
          </el-col>

          <el-col
            :span="4"
            class="setting-table-th setting-th-text text-center"
          >
            {{ $t('common.products.status') }}
          </el-col>

          <el-col :span="4" class="setting-table-th setting-th-text">
            &nbsp;
          </el-col>
        </el-row>

        <el-row v-if="isLoading">
          <el-col :span="24" class="text-center">
            <spinner :show="isLoading" size="80"></spinner>
          </el-col>
        </el-row>

        <el-row v-else-if="$validation.isEmpty(depreciationList)">
          <el-col class="body-row-cell text-center" :span="24">
            {{ $t('common.products.no_depreciation_schedule_found') }}
          </el-col>
        </el-row>

        <template v-else>
          <el-row
            class="body-row tablerow"
            v-for="depreciation in depreciationList"
            :key="depreciation.id"
          >
            <el-col class="body-row-cell d-flex" :span="6">
              <div @click="openAddOrUpdateForm(depreciation)" class="pointer">
                {{ depreciation.name }}
              </div>
            </el-col>

            <el-col class="body-row-cell" :span="5">
              {{ frequencyTypes[`${depreciation.frequencyType}`] }}
            </el-col>

            <el-col :span="5" class="body-row-cell text-center text-capitalize">
              {{ depreciationTypes[`${depreciation.depreciationType}`] }}
            </el-col>

            <el-col :span="4" class="body-row-cell text-center">
              <el-switch
                v-model="depreciation.active"
                @change="changeStatus(depreciation)"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </el-col>

            <el-col class="body-row-cell" :span="4">
              <div class="text-left actions mT0 text-center d-flex">
                <div class="mR15" @click="openAddOrUpdateForm(depreciation)">
                  <i class="el-icon-edit pointer"></i>
                </div>

                <div
                  v-if="canShowDelete"
                  @click="deleteDepreciation(depreciation)"
                >
                  <inline-svg
                    src="svgs/delete"
                    class="f-delete pointer"
                    iconClass="icon icon-sm icon-remove"
                  ></inline-svg>
                </div>
              </div>
            </el-col>
          </el-row>
        </template>
      </div>
    </div>
    <new-asset-depreciation
      v-if="showAddOrUpdateForm"
      :id="depreciationId"
      @onRecordSaved="loadDepreciation()"
      @onClose="showAddOrUpdateForm = false"
    ></new-asset-depreciation>
  </div>
</template>
<script>
import NewAssetDepreciation from './NewAssetDepreciation'
import { API } from '@facilio/api'
import { DEPRECIATION_TYPES, FREQUENCY_TYPES } from './DepreciationConstant'

export default {
  data() {
    return {
      depreciationList: [],
      isLoading: false,
      showAddOrUpdateForm: false,
      depreciationId: null,
    }
  },

  components: { NewAssetDepreciation },

  created() {
    this.loadDepreciation()
    this.depreciationTypes = DEPRECIATION_TYPES
  },

  computed: {
    canShowDelete() {
      return process.env.NODE_ENV === 'development'
    },

    frequencyTypes() {
      return Object.values(FREQUENCY_TYPES).reduce((res, freq) => {
        res[freq.id] = freq.label

        return res
      }, {})
    },
  },

  methods: {
    loadDepreciation() {
      let data = { page: 1, perPage: 50 }

      this.isLoading = true
      API.fetchAll('assetdepreciation', data).then(({ list, error }) => {
        if (error) {
          this.$message.error(error.messsage || 'Error Occured')
        } else {
          this.depreciationList = list
        }

        this.isLoading = false
        this.depreciationId = null
      })
    },

    changeStatus(depreciation) {
      let { id, active } = depreciation
      let param = { id, data: { active } }

      API.updateRecord('assetdepreciation', param).then(
        ({ assetdepreciation, error }) => {
          if (error) {
            this.$message.error(error.messsage || 'Error Occured')
            this.$set(depreciation, 'active', false)
          } else {
            let message = active
              ? 'Depreciation Schedule marked as active'
              : 'Depreciation Schedule marked as inactive'

            this.$message.success(message)

            let index = this.depreciationList.findIndex(
              depreciation => depreciation.id === assetdepreciation.id
            )
            this.depreciationList.splice(index, 1, assetdepreciation)
          }
        }
      )
    },

    openAddOrUpdateForm(depreciation = {}) {
      this.depreciationId = depreciation.id
      this.showAddOrUpdateForm = true
    },

    deleteDepreciation({ id }) {
      let param = { id }

      API.deleteRecord('assetdepreciation', param).then(({ error }) => {
        if (error) {
          this.$message.error(error.messsage || 'Error Occured')
        } else {
          this.loadDepreciation()
        }
      })
    },
  },
}
</script>
<style scoped>
.setting-list-view-table .setting-table-th {
  vertical-align: middle;
  background-color: #fff;
  border: none;
}
.body-row {
  border: 1px solid transparent;
  border-bottom: 1px solid #f2f5f6;
  background-color: #fff;
}
.body-row:hover {
  background-color: #fff;
  border: 1px solid #b0dbe1;
  z-index: 1;
}
.body-row:first-of-type {
  border-top: 1px solid #f2f5f6;
}
.body-row:first-of-type:hover {
  border: 1px solid #b0dbe1;
}
.body-row-cell {
  border-top: none;
  border-left: none;
  border-right: none;
  color: #333;
  font-size: 14px;
  border-collapse: separate;
  padding: 15px 30px;
  letter-spacing: 0.6px;
  font-weight: 400;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.body-row-cell:first-of-type {
  padding-left: 30px;
}
.body-row .actions {
  visibility: hidden;
}
.body-row:hover .actions {
  visibility: visible;
}
</style>
