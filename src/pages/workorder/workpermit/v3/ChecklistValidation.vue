<template>
  <div v-if="visibility">
    <el-dialog
      :visible.sync="visibility"
      :title="dialogTitle"
      :fullscreen="false"
      open="top"
      width="80%"
      :before-close="closeDialog"
      custom-class="fc-dialog-center-container"
      :append-to-body="true"
    >
      <div class="height500 pB100 overflow-scroll">
        <spinner
          v-if="loading"
          :show="true"
          :size="70"
          class="flex-middle justify-content-center height100"
        ></spinner>
        <div
          v-else-if="$validation.isEmpty(categories)"
          class="flex-middle justify-content-center flex-col height100"
        >
          <inline-svg
            src="svgs/emptystate/workorder"
            iconClass="icon text-center icon-xxxlg height-auto"
          ></inline-svg>
          <div class="line-height20 nowo-label mT20">
            No Checklist available.
          </div>
        </div>
        <div
          v-else
          v-for="(category, categoryIndex) in categories"
          :key="categoryIndex"
          class="pT20"
        >
          <div class="fc-grey7-12 bold text-left">
            {{ category.name }}
          </div>
          <div class="work-permit-table width100">
            <table class="setting-list-view-table width100">
              <thead>
                <tr>
                  <th
                    class="setting-table-th setting-th-text uppercase width100px"
                  >
                    S.no
                  </th>
                  <th
                    class="setting-table-th setting-th-text uppercase width250px text-center"
                  >
                    Items
                  </th>
                  <th
                    class="setting-table-th setting-th-text uppercase width100px text-center"
                  >
                    Required
                  </th>
                  <th
                    class="setting-table-th setting-th-text uppercase width250px text-center"
                  >
                    Remarks
                  </th>
                  <th
                    class="setting-table-th setting-th-text uppercase width100px text-center"
                  >
                    Reviewed
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(wpChecklist, checklistIndex) in category.checklist"
                  :key="checklistIndex"
                  class="tablerow"
                >
                  <td style="width100px text-center">
                    {{ checklistIndex + 1 }}
                  </td>
                  <td class="width250">
                    {{ $getProperty(wpChecklist, 'checklist.item', '') }}
                  </td>
                  <td class="width100px text-center">
                    {{ $getProperty(wpChecklist, 'requiredEnum', '') }}
                  </td>
                  <td class="width250px text-center">
                    {{ $getProperty(wpChecklist, 'remarks', '-') }}
                  </td>
                  <td class="width100px text-center">
                    <el-checkbox v-model="wpChecklist.isReviewed"></el-checkbox>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="updateChecklist()"
          :loading="saving"
          >{{ saveBtnText }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import PermitChecklistMixin from './PermitChecklistMixin'
import { API } from '@facilio/api'
import { isEmpty, isArray } from '@facilio/utils/validation'
export default {
  props: ['details', 'validationType', 'visibility'],
  mixins: [PermitChecklistMixin],
  data() {
    return {
      categories: [],
      saving: false,
      loading: true,
      workPermitFieldId: null,
    }
  },
  mounted() {
    Promise.all([this.loadWorkPermitChecklistModuleMeta()]).then(() => {
      this.loading = false
    })
    let categories = this.getChecklistforValidation(
      this.details.checklist,
      this.validationType
    )
    this.$set(this, 'categories', categories)
  },
  computed: {
    dialogTitle() {
      return this.validationType === 1
        ? 'Review Prerequisites'
        : 'Permit Closeout'
    },
    saveBtnText() {
      return this.validationType === 1 ? 'Mark as Reviewed' : 'Mark as Closed'
    },
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    async updateChecklist() {
      let checklist = []
      let { workPermitFieldId: fieldId } = this
      if (!isEmpty(this.categories) && isArray(this.categories)) {
        this.categories.forEach(category => {
          if (!isEmpty(category.checklist)) {
            checklist.push(...category.checklist)
          }
        })
      }
      let param = {
        id: this.details.id,
        data: {
          relations: {
            workpermitchecklist: [{ fieldId, data: checklist }],
          },
        },
      }
      if (this.validationType === 1) {
        this.$setProperty(param, 'data.isPreValidationDone', true)
      } else if (this.validationType === 2) {
        this.$setProperty(param, 'data.isPostValidationDone', true)
      }
      this.saving = true
      let { error } = await API.updateRecord(`workpermit`, param)
      if (error) {
        this.$message.error(error || 'Error Occurred while updating checklist')
      } else {
        this.$message.success('Checklist Updated Successfully')
        this.$emit('saved')
        this.closeDialog()
      }
      this.saving = false
    },
  },
}
</script>
