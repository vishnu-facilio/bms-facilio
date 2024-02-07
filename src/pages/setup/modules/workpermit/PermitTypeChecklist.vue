<template>
  <div class="height100 overflow-hidden fc-checklist-con">
    <el-header class="checklist-header-options width100 mT30">
      <div class="width50">
        <div class="f16 bold letter-spacing0_5 fc-black-color text-capitalize">
          Work Permit Type
        </div>
        <div class="flex-middle">
          <el-select
            v-model="model.workPermitType.id"
            placeholder="Select Work Permit Type"
            class="width50 fc-input-full-border-select2 mT10"
          >
            <el-option
              v-for="workpermitType in workpermitTypes"
              :key="workpermitType.id"
              :label="workpermitType.type"
              :value="workpermitType.id"
            >
            </el-option>
          </el-select>
          <div class="mL20">
            <el-button
              @click="openFormDialog('workpermittype')"
              class="btn-blue-fill f12 mT10 text-uppercase pL15 pR15 pT12 pB12"
            >
              Add Type
            </el-button>
          </div>
        </div>
      </div>
      <div
        v-if="!$validation.isEmpty($getProperty(model, 'workPermitType.id'))"
        @click="
          openFormDialog('workpermittypechecklistcategory', {
            workPermitType: { id: $getProperty(model, 'workPermitType.id') },
            validationType: checklistTypeTab === 'pre' ? 1 : 2,
          })
        "
        class="fc-text-pink14 bold f14 pointer pT30"
      >
        <i class="el-icon-circle-plus-outline fwBold f15"></i>
        Add Category
      </div>
    </el-header>
    <div class="checklist-tab-con mT30">
      <el-tabs type="card" v-model="checklistTypeTab" class="fc-tab-card-type">
        <el-tab-pane
          v-for="(mode, modeIndex) in tabModes"
          :key="modeIndex"
          :label="mode.displayName"
          :name="mode.name"
        >
          <div class="container-scroll" style="height: calc(100vh - 250px);">
            <div v-if="isLoading" class="flex-middle height200 fc-empty-white">
              <spinner :show="isLoading" size="80"></spinner>
            </div>
            <div
              v-else-if="
                $validation.isEmpty(
                  $getProperty(model, `checklist.${mode.name}`)
                )
              "
              class="flex-middle justify-content-center flex-col check-list-hor-card height200"
            >
              <inline-svg
                src="svgs/emptystate/workorder"
                iconClass="icon text-center icon-xxxlg height-auto"
              ></inline-svg>
              <div class="line-height20 nowo-label mT20">
                No Checklist Categories available.
              </div>
            </div>
            <div
              v-else
              v-for="(category, categoryIndex) in $getProperty(
                model,
                `checklist.${mode.name}`
              )"
              :key="categoryIndex"
              class="check-hor-card pL10 pR10 visibility-visible-actions pB10"
            >
              <div class="checklist-header-options">
                <div
                  class="label-txt-black bold f16 flex-middle visibility-visible-actions"
                >
                  {{ category.name }}
                </div>
                <div
                  @click="
                    openFormDialog('workpermittypechecklist', {
                      category: { id: category.id },
                    })
                  "
                  class="fc-row-add-icon bold pointer"
                >
                  <i class="el-icon-circle-plus-outline f16 fw6 pR5"></i>Add
                  Checklist
                </div>
              </div>
              <div class="check-list-hor-card mT10">
                <div class="">
                  <div
                    v-if="$validation.isEmpty(category.checklist)"
                    class="flex-middle justify-content-center flex-col height150"
                  >
                    <inline-svg
                      src="svgs/emptystate/task"
                      iconClass="icon text-center icon-xxlg height-auto"
                    ></inline-svg>
                    <div class="line-height20 mT20 label-txt-black bold f15">
                      No Checklist available under this category.
                    </div>
                  </div>
                  <el-row
                    v-else
                    v-for="(wpChecklist, checklistIndex) in category.checklist"
                    :key="checklistIndex"
                    class="flex-middle pT10 fc-check-visibility-visible-actions"
                  >
                    <el-col :span="1" style="width: 2%;">
                      <div class="dot-active-pink mL0"></div>
                    </el-col>
                    <el-col :span="23">
                      <div class="fc-black-14 text-left line-height20">
                        {{ wpChecklist.checklist.item }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="fc-category-delete visibility-hide-actions"></div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <WorkPermitSubModulesForm
      v-if="formVisibility"
      :visibility.sync="formVisibility"
      :appendDataMeta="formMeta"
      :moduleName="formModuleName"
      @saved="refreshData"
    ></WorkPermitSubModulesForm>
  </div>
</template>
<script>
import WorkPermitSubModulesForm from 'src/pages/workorder/workpermit/WorkPermitSubModulesForm'
import PermitChecklistMixin from 'src/pages/workorder/workpermit/v3/PermitChecklistMixin'
import { API } from '@facilio/api'
import { isEmpty, isArray } from '@facilio/utils/validation'
export default {
  components: { WorkPermitSubModulesForm },
  mixins: [PermitChecklistMixin],
  data() {
    return {
      workpermitTypes: [],
      formVisibility: false,
      formModuleName: null,
      formMeta: null,
      model: {
        workPermitType: { id: null },
        checklist: { pre: {}, post: {} },
      },
    }
  },
  created() {
    this.loadAllPermitTypes()
  },
  methods: {
    async loadAllPermitTypes(force) {
      let params = {
        viewName: 'all',
      }
      this.isLoading = true
      let { list, error } = await API.fetchAll(`workpermittype`, params, {
        force,
      })
      if (error) {
        let { message = 'Error Occured while fetching Permit Types' } = error
        this.$message.error(message)
      } else {
        this.workpermitTypes = list || []
        if (
          !isEmpty(list) &&
          isArray(list) &&
          isEmpty(this.$getProperty(this, 'model.workPermitType.id'))
        ) {
          this.$setProperty(
            this,
            'model.workPermitType.id',
            this.$getProperty(list, [0, 'id'], null)
          )
        }
      }
      this.isLoading = false
    },
    openFormDialog(moduleName, meta) {
      this.formVisibility = true
      this.formModuleName = moduleName
      this.formMeta = meta
    },
    refreshData() {
      if (this.formModuleName === 'workpermittype') {
        this.loadAllPermitTypes(true)
      } else {
        this.loadChecklist(true)
      }
    },
  },
}
</script>
