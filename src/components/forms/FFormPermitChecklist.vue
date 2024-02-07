<template>
  <div>
    <el-tabs
      v-model="checklistTypeTab"
      class="width100 fc-tab2 permit-checklist-tabs"
    >
      <el-tab-pane
        v-for="(mode, modeIndex) in tabModes"
        :key="modeIndex"
        :label="mode.displayName"
        :name="mode.name"
      >
        <div v-if="isLoading" class="flex-middle height200">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <div
          v-else-if="$validation.isEmpty(getCategoriesForForm(mode))"
          class="flex-middle justify-content-center flex-col height200"
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
          v-for="(category, categoryIndex) in getCategoriesForForm(mode)"
          :key="categoryIndex"
          class="pT20"
        >
          <div class="label-txt-black bold pB10">
            {{ category.name }}
          </div>
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
            class="flex-middle mT10"
            :key="checklistIndex"
            :gutter="20"
          >
            <el-col class="fc-grey8-14 f14 text-left" :span="1">
              {{ checklistIndex + 1 }}
            </el-col>
            <el-col class="label-txt-black" :span="10">
              {{ wpChecklist.checklist.item }}
            </el-col>
            <el-col :span="4" class="pL0">
              <el-select
                v-model="wpChecklist.required"
                placeholder="Permit Type"
                class="fc-input-full-border2 width100"
              >
                <el-option label="Yes" :value="1"></el-option>
                <el-option label="No" :value="2"></el-option>
                <el-option label="N/A" :value="3"></el-option>
              </el-select>
            </el-col>
            <el-col :span="9">
              <el-input
                v-model="wpChecklist.remarks"
                placeholder="Enter Remarks"
                class="fc-input-full-border2 width100"
              ></el-input>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import PermitChecklistMixin from 'src/pages/workorder/workpermit/v3/PermitChecklistMixin'
export default {
  props: ['model'],
  mixins: [PermitChecklistMixin],
  data() {
    return {}
  },
  created() {
    this.isLoading = true
    this.loadWorkPermitChecklistModuleMeta().then(() => {
      this.isLoading = false
    })
  },
}
</script>
