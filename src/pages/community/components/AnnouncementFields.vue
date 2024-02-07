<template>
  <div :class="['asset-details-widget', widget.title && 'pT0']">
    <template v-if="widget.title">
      <div class="widget-topbar">
        <div class="widget-title mL0">{{ widget.title }}</div>
      </div>
    </template>

    <div v-if="loading" :class="['container', widget.title && 'pL0']">
      <div v-for="index in [1, 2, 3]" :key="index" class="field">
        <el-row>
          <el-col :span="12">
            <span
              :class="['lines loading-shimmer', widget.title && 'm0']"
            ></span>
          </el-col>

          <el-col :span="12">
            <span
              :class="['lines loading-shimmer', widget.title && 'm0']"
            ></span>
          </el-col>
        </el-row>
      </div>

      <div class="field">
        <el-row>
          <el-col :span="12"></el-col>
          <el-col :span="12"></el-col>
        </el-row>
      </div>
    </div>

    <div v-else-if="emptyState" class="mT20" style="color: #8ca1ad;">
      <div class="d-flex flex-direction-column">
        <inline-svg
          src="svgs/emptystate/notes"
          class="vertical-middle self-center"
          iconClass="icon icon-xxxlg mR5"
        ></inline-svg>
        <div class="f14 pL10 self-center">
          No Fields Available
        </div>
      </div>
    </div>

    <div
      v-else
      :class="['container', widget.title && 'pL0 pB15']"
      ref="content-container"
    >
      <div
        v-for="(field, index) in fieldsList"
        :key="field.name + index"
        class="field"
      >
        <el-row v-if="field.displayTypeEnum === 'TEXTAREA'">
          <el-col :span="6" class="field-label">{{ field.displayName }}</el-col>
          <el-col :span="16" class="field-value line-height22">{{
            field.displayValue
          }}</el-col>
        </el-row>
        <el-row v-if="$getProperty(field, 'field.dataTypeEnum') === 'FILE'">
          <el-col :span="6" class="field-label">{{ field.displayName }}</el-col>

          <FilePreviewColumn
            :field="field"
            :record="details"
            :isV3="true"
          ></FilePreviewColumn>
        </el-row>

        <el-row v-else>
          <el-col :span="12" class="field-label">{{
            field.displayName
          }}</el-col>

          <el-col
            :span="12"
            v-if="isLookUpRedirectApplicable(field)"
            class="field-value"
          >
            <div
              class="bluetxt pointer"
              @click="redirectToLookUpFieldSummary(field)"
            >
              {{ field.displayValue }}
            </div>
          </el-col>
          <el-col
            :span="12"
            v-if="field.name === 'sysCreatedBy'"
            class="field-value line-height22"
          >
            {{ getSystemCreatedBy }}
          </el-col>
          <el-col
            :span="12"
            v-else-if="field.name === 'sysModifiedBy'"
            class="field-value line-height22"
          >
            {{ getSystemModifiedBy }}
          </el-col>
          <el-col
            :span="12"
            v-else-if="systemFields.includes(field.name)"
            class="field-value line-height22"
          >
            {{ getSysFieldValue(field) }}
          </el-col>
          <el-col v-else :span="12" class="field-value line-height22">
            {{ field.displayValue }}
          </el-col>
        </el-row>
      </div>

      <div v-if="additionalField" class="field">
        <el-row>
          <el-col :span="12" class="field-label"></el-col>
          <el-col :span="12" class="field-value"></el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import FieldDetails from 'src/components/page/widget/common/field-details/HorizontalFieldDetails.vue'
import FilePreviewColumn from '@/list/FilePreviewColumn'

export default {
  extends: FieldDetails,
  created() {
    this.$store.dispatch('loadUsers')
  },
  components: {
    FilePreviewColumn,
  },
  computed: {
    getSystemCreatedBy() {
      let { details } = this
      let { sysCreatedBy } = details
      let { name } = sysCreatedBy
      return name || '---'
    },
    getSystemModifiedBy() {
      let { details } = this
      let { sysModifiedBy } = details
      let { name } = sysModifiedBy
      return name || '---'
    },
  },
  methods: {
    autoResize() {
      // Overriden to prevent autoresize
    },
  },
}
</script>
