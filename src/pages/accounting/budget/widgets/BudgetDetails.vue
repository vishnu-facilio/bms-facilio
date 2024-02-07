<template>
  <div class="p20">
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
    <div ref="content-container" v-else>
      <el-row class="d-flex flex-wrap pT10">
        <el-col
          v-for="(field, index) in fieldsList"
          :key="field.name"
          :span="8"
          class="pT15 pB15 border-bottom20"
        >
          <el-col
            :span="8"
            :key="field.displayName"
            class="fc-grey-label1 text-uppercase"
            >{{ field.displayName }}</el-col
          >
          <el-col
            :span="8"
            v-if="isLookUpRedirectApplicable(field)"
            :key="index + 1 + '-field'"
            class="label-txt-black"
          >
            <div
              @click="redirectToLookUpFieldSummary(field)"
              class="f14"
              :class="[field.displayValue !== '---' && 'fc-id pointer']"
            >
              {{ field.displayValue }}
            </div>
          </el-col>
          <el-col
            v-else-if="isFileField(field)"
            :span="8"
            :key="index + 1 + '-field'"
            class="attachment-label pointer"
          >
            <iframe
              v-if="exportDownloadUrl"
              :src="exportDownloadUrl"
              style="display: none;"
            ></iframe>

            <a @click="downloadAttachment(field)">
              {{ getFileName(field) }}
            </a>
          </el-col>
          <el-col
            v-else
            :span="8"
            :key="index + 1 + '-field'"
            class="label-txt-black"
            >{{ field.displayValue }}</el-col
          >
        </el-col>
      </el-row>
      <div
        v-if="(fieldsList || []).length > 6"
        @click="toggleVisibility()"
        class="width100 pT20 text-center"
      >
        <div class="fc-dark-blue3-12 f14 text-center pointer">
          {{ showMoreLinkText }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FieldDetails from 'src/components/page/widget/common/field-details/HorizontalFieldDetails.vue'
export default {
  extends: FieldDetails,
}
</script>
