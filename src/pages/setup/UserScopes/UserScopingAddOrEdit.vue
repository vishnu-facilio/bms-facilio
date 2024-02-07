<template>
  <el-dialog
    :visible="true"
    width="85%"
    custom-class="fc-setup-dialog-form fc-setup-dialog-form fc-setup-rightSide-dialog-scroll fc-user-scope-dialog"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <el-form ref="scopeDetails" :model="scopingContext" :label-position="'top'">
      <div class="form-header inactiveheader flex-middle justify-content-space">
        <div class="fc-black2-18 fw6" v-if="!isNew">
          Edit {{ scopingContext.scopeName }}
        </div>
        <div
          v-if="userScopeData.isDefault === true"
          class="text-center fc-text-blink fc-red-new bold letter-spacing07"
        >
          {{ $t('setup.userScoping.default_scoping_cannot_edit') }}
        </div>
        <div></div>
      </div>

      <div class="user-scope-criteria-con">
        <div class="user-scope-criteria-left">
          <el-form-item>
            <div class="fc-user-scope-criteria-search">
              <el-input
                v-model="searchQuery"
                class="fc-input-full-border2"
                placeholder="Search the module"
                clearable
              >
                <i slot="prefix" class="el-input__icon el-icon-search"></i>
              </el-input>
            </div>

            <div v-if="loading" class="fc-user-empty-creation">
              <spinner :show="loading" size="80"></spinner>
            </div>

            <div
              v-else-if="$validation.isEmpty(resultQuery) && !loading"
              class="fc-user-empty-creation"
            >
              <inline-svg
                src="svgs/floor-plan-empty"
                iconClass="icon text-center icon-xxxlg"
              ></inline-svg>
              <div class="fwBold fc-black-14 text-left">
                {{ $t('setup.userScoping.no_results') }}
              </div>
            </div>

            <div v-else>
              <div
                v-for="modules in resultQuery"
                :key="modules.moduleId"
                :class="{ active: selectedModule.moduleId == modules.moduleId }"
                @click="selectedModule = modules"
                class="module-link"
              >
                <div class="flex-middle justify-content-space line-height20">
                  <div class="width70 break-word">
                    <a class="">
                      {{ modules.displayName }}
                    </a>
                  </div>
                  <div
                    class="fc-criteria-enabled"
                    v-if="configuredList[modules.moduleId]"
                  >
                    Configured
                  </div>
                </div>
              </div>
            </div>
          </el-form-item>
        </div>
        <div class="user-scope-criteria-right">
          <div v-if="loading" class="fc-user-empty-creation">
            <spinner :show="loading" size="80"></spinner>
          </div>
          <div
            v-else-if="$validation.isEmpty(modulesList) && !loading"
            class="fc-user-empty-creation"
          >
            <inline-svg
              src="svgs/floor-plan-empty"
              iconClass="icon text-center icon-xxxlg"
            ></inline-svg>
            <div class="fwBold fc-black-14 text-left">
              {{ $t('setup.userScoping.no_results') }}
            </div>
          </div>

          <div v-else>
            <div class="fc-security-header">
              <div class="fc-modal-sub-title fw6 pB20 pL20">
                {{ selectedModule.displayName }}
              </div>
            </div>
            <el-form-item>
              <!-- {{ moduleIdVsCriteriaObj[selectedModule.moduleId] }} -->
              <UserScopeCriteria
                v-if="moduleIdVsCriteriaObj[selectedModule.moduleId]"
                ref="criteriaBuilder"
                v-model="
                  moduleIdVsCriteriaObj[selectedModule.moduleId].criteria
                "
                :exrule="
                  moduleIdVsCriteriaObj[selectedModule.moduleId].criteria
                "
                @condition="handleCondition"
                :module="selectedModule.name"
                :isRendering.sync="isCriteriaRendering"
                :key="selectedModule.moduleId"
              ></UserScopeCriteria>
            </el-form-item>
          </div>
        </div>
      </div>
    </el-form>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">
        {{ $t('setup.users_management.cancel') }}
      </el-button>
      <el-button
        type="primary"
        :loading="saving"
        class="modal-btn-save"
        @click="saveUserScope"
        :disabled="userScopeData.isDefault === true"
      >
        {{ $t('panel.dashboard.confirm') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import UserScopeCriteria from 'pages/setup/UserScopes/UserScopeCriteria'
import { mapGetters } from 'vuex'
import { API } from '@facilio/api'
import { isEmpty } from '../../../util/validation'
export default {
  props: ['isNew', 'userScopeData', 'applicationId'],
  data() {
    return {
      saving: false,
      loading: true,
      searchQuery: null,
      isCriteriaRendering: false,
      scopingContext: {},
      moduleFieldsCriteria: [],
      userScopeCriteriaData: [],
      selectedModule: null,
    }
  },
  components: {
    UserScopeCriteria,
  },
  computed: {
    ...mapGetters('automationSetup', ['getAutomationModulesList']),
    modulesList() {
      return this.getAutomationModulesList()
    },
    moduleIdVsCriteriaObj() {
      let data = {}
      let { scopingConfigList } = this.scopingContext || {}

      scopingConfigList.forEach(rt => {
        let obj = {}

        if (
          rt.criteria?.conditions[1] &&
          rt.criteria.conditions[1].operatorId &&
          rt.criteria.conditions[1].operatorId === 93
        ) {
          this.$set(rt.criteria['conditions'][1], 'operatorsDataType', {
            dataType: 'LOOKUP',
            displayType: 'LOOKUP_SIMPLE',
          })
        }
        this.$set(obj, 'criteria', rt.criteria)

        this.$set(data, rt.moduleId, obj)
      })

      return data
    },
    configuredList() {
      let data = {}
      let { scopingConfigList } = this.scopingContext || {}
      scopingConfigList.forEach(rt => {
        if (rt.criteria?.conditions[1] && rt.criteria.conditions[1].value) {
          this.$set(data, rt.moduleId, true)
        }
      })
      return data
    },
    resultQuery() {
      if (this.searchQuery) {
        return this.modulesList.filter(item => {
          return this.searchQuery
            .toLowerCase()
            .split(' ')
            .every(v => item.displayName.toLowerCase().includes(v))
        })
      } else {
        return this.modulesList
      }
    },
    criteriaObj: {
      get() {
        let { scopingConfigList } = this.scopingContext || {}
        let { criteria } =
          (scopingConfigList || []).find(
            rt => rt.moduleId === this.selectedModule.moduleId
          ) || {}
        return criteria || null
      },
      set(value) {
        let { scopingConfigList } = this.scopingContext || {}
        let index = scopingConfigList.findIndex(
          rt => rt.moduleId === this.selectedModule.moduleId
        )
        if (index !== -1) {
          let configObject = scopingConfigList[index]
          scopingConfigList.splice(index, 0, {
            ...configObject,
            criteria: value,
          })
          this.$set(this.scopingContext, 'scopingConfigList', scopingConfigList)
          this.clearEmpties(scopingConfigList)
        }
      },
    },
  },
  created() {
    if (!this.isNew) {
      this.scopingContext = {
        ...this.userScopeData,
      }
      this.getUserScopeCriteriaData()
    }
  },
  watch: {
    selectedModule(newValue) {
      if (!isEmpty(newValue)) {
        let { scopingConfigList } = this.scopingContext || {}
        let index = scopingConfigList.findIndex(
          rt => rt.moduleId === this.selectedModule.moduleId
        )
        if (index === -1) {
          scopingConfigList.push({
            moduleId: this.selectedModule.moduleId,
            criteria: null,
          })
          this.$set(this.scopingContext, 'scopingConfigList', scopingConfigList)
        }
      }
    },
  },
  methods: {
    handleCondition(criteria) {
      this.moduleIdVsCriteriaObj[
        this.selectedModule.moduleId
      ].criteria = criteria
    },
    closeDialog() {
      this.$emit('onClose')
    },
    getScopingContext(context) {
      let scopingContext = this.$helpers.cloneObject(context)
      if (
        scopingContext?.scopingConfigList &&
        scopingContext.scopingConfigList.length
      ) {
        let { scopingConfigList } = this.scopingContext || null

        let configList = []
        scopingConfigList.forEach(rt => {
          let critera = this.moduleIdVsCriteriaObj[rt.moduleId] || null
          if (critera || critera.criteria) {
            critera.moduleId = rt.moduleId
            if (critera.critera) {
              configList.push(critera.criteria)
            } else if (critera) {
              this.$set(critera, 'moduleId', rt.moduleId)
              configList.push(critera)
            }
          }
        })
        this.$set(scopingContext, 'scopingConfigList', configList)
      }
      return scopingContext
    },
    // validateScopingContext(scopingContext) {
    //   let validate = true
    //   if (
    //     scopingContext?.scopingConfigList &&
    //     scopingContext.scopingConfigList.length
    //   ) {
    //     let { scopingConfigList } = this.scopingContext || null

    //     scopingConfigList.forEach(rt => {
    //       let critera = this.moduleIdVsCriteriaObj[rt.moduleId] || null
    //       if (critera || critera.critera) {
    //         if (critera.critera) {
    //           validate = true
    //         } else if (critera) {
    //           validate = true
    //         } else {
    //           validate = false
    //         }
    //       }
    //     })
    //   }
    //   return validate
    // },
    saveUserScope() {
      this.$refs['scopeDetails'].validate(async valid => {
        // if (this.validateScopingContext(this.scopingContext)) {
        //   this.$message.error('please fill emplty critera object')
        //   return
        // }
        if (!valid) return false
        this.saving = true
        let url = 'v2/scoping/addOrUpdateConfig'
        // let { scopingContext } = this;
        let scopingContext = this.getScopingContext(this.scopingContext)
        let { error, data } = await API.post(url, { scopingContext })
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(
            this.$t('common._common.user_scope_saved_successfully')
          )
          this.$emit('onSave', data.scopingContext)
          this.closeDialog()
        }
        this.saving = false
        this.$forceUpdate()
      })
    },
    async getUserScopeCriteriaData() {
      this.loading = true
      let { error, data } = await API.get(
        `v2/scoping/scopingConfigList?scopingId=${this.userScopeData.id}`
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.scopingContext = data.scopingContext || []
        let { scopingConfigList } = this.scopingContext || {}
        if (!scopingConfigList) {
          this.scopingContext.scopingConfigList = []
        }
      }
      this.selectedModule = this.modulesList[0]

      this.loading = false
    },
    clearEmpties(obj) {
      for (let k in obj) {
        if (!obj[k] || typeof obj[k] !== 'object') {
          continue
        }
        if (Object.keys(obj[k]).length === 0) {
          delete obj[k]
        }
        return obj
      }
    },
  },
}
</script>
<style lang="scss">
.fc-user-scope-dialog {
  .fc-modal-sub-title {
    text-transform: capitalize;
    font-size: 18px;
    color: #385571;
    font-weight: 400;
    letter-spacing: 0.5px;
  }
  .fc-add-border-green-btn {
    background: #39b2c2;
    color: #fff;
  }

  .el-dialog__header {
    display: none;
  }
  .form-header {
    margin-top: 0px;
    padding: 20px;
    border-bottom: 1px solid #edeeef;
  }
  .el-dialog__body {
    padding: 0;
  }
  .user-scope-criteria-con {
    width: calc(100% - 200px);
    position: fixed;
    display: flex;
    flex-wrap: nowrap;
  }
  .user-scope-criteria-left {
    width: 300px;
    height: calc(100vh - 100px);
    overflow-y: scroll;
    display: inline-block;
    background-color: #fff;
    border-right: 1px solid #edeeef;
    padding-bottom: 100px;
  }
  .user-scope-criteria-right {
    width: calc(100% - 300px);
    height: calc(100vh - 100px);
    display: inline-block;
    overflow-y: scroll;
    background: #f8f9fa;
    border-top: 1px solid #edeeef;
    padding-bottom: 600px;
    padding: 20px 0 100px;
  }
  .fc-field-values-block {
    background: #fff;
    width: calc(100% - 100px) !important;
  }
  .module-link {
    cursor: pointer;
    display: block;
    position: relative;
    padding: 14px 20px;
    margin: 0;
    border-left: 3px solid transparent;
    letter-spacing: 0.2px;
    text-transform: capitalize;
    border-left: 3px solid transparent;
    border-bottom: 1px solid rgb(237 244 250 / 70%);
    &:hover {
      background: #f1f8fa;
    }
    a {
      color: #324056;
      font-size: 14px;
    }
  }
  .active {
    background: #f3f4f7;
    border-left: 3px solid #ef4f8f;
    font-weight: 500;
  }
  .fc-input-user-scope {
    .el-input__inner {
      font-size: 18px !important;
      font-weight: 500;
      border: 1px solid transparent !important;
      &:focus,
      &:hover {
        border: 1px solid #d8dce5 !important;
        border-color: #d8dce5 !important;
      }
      &:focus {
        border: 1px solid #39b2c2 !important;
        border-color: #39b2c2 !important;
      }
    }
  }
  .fc-user-scope-criteria-search {
    position: sticky;
    top: 0;
    z-index: 300;
    background: #fff;
    padding: 10px 20px;
    .el-input__inner {
      padding-left: 30px !important;
      border-radius: 20px !important;
    }
  }
  .fc-user-empty-creation {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    height: 50vh;
  }
  .fc-criteria-enabled {
    font-size: 11px;
    font-weight: 500;
    font-style: italic;
    color: #3478f6;
  }
}
</style>
