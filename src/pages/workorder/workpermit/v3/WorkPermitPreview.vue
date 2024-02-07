<template>
  <div>
    <div v-if="loading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div v-else ref="preview-container" class="fc-workpermit-sum-con">
      <div class="fc-workpermit-sum-header">
        <div class="flex-col d-flex">
          <div v-if="$org.logoUrl" class="fc-quotation-logo">
            <img :src="$org.logoUrl" style="width: 100px;" />
          </div>
          <div class="label-txt-black bold pT10">
            {{ $account.org.name }}
          </div>
          <div class="pT5">
            <div class="label-txt-black line-height20">
              <span v-if="$account.org.street">
                {{ `${$account.org.street}${$account.org.city ? `,` : ``}` }}
              </span>
              <span v-if="$account.org.city">
                {{ `${$account.org.city}${$account.org.state ? `,` : ``}` }}
              </span>
            </div>
            <div class="label-txt-black line-height20">
              <span v-if="$account.org.state">
                {{ `${$account.org.state}${$account.org.country ? `,` : ``}` }}
              </span>
              <span v-if="$account.org.country">
                {{ `${$account.org.country}${$account.org.zip ? `,` : ``}` }}
              </span>
            </div>
            <div v-if="$account.org.zip" class="label-txt-black line-height20">
              {{ `${$account.org.zip}` }}
            </div>
          </div>
        </div>

        <div class="d-flex flex-col justify-content-end items-end">
          <div class="label-txt-black text-uppercase f23 fwBold line-height20">
            {{ $getProperty(details, 'workPermitType.type', '') }}
          </div>
          <div class="label-txt-black text-right line-height20 pT5 bold">
            {{ `PERMIT NO #${details.localId}` }}
          </div>
        </div>
      </div>

      <div class="pT30 border-bottom17">
        <div class="fc-pink text-uppercase text-left fwBold f12">
          {{ $t('home.workpermit.permit_receiver_details') }}
        </div>
        <el-row class="pT10">
          <el-col :span="12" class="pT15 pB15">
            <el-col :span="12">
              <div class="fc-black-11 text-uppercase fwBold">
                {{ $t('home.workpermit.vendor') }}
              </div>
            </el-col>
            <el-col :span="12">
              <div class="label-txt-black">
                {{ $getProperty(details, 'vendor.name', '---') }}
              </div>
            </el-col>
          </el-col>
          <el-col :span="12" class="pT15 pB15">
            <el-col :span="12">
              <div class="fc-black-11 text-uppercase fwBold">
                {{ $t('home.workpermit.vendor_contact_name') }}
              </div>
            </el-col>
            <el-col :span="12">
              <div class="label-txt-black">
                {{ $getProperty(details, 'people.name', '---') }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" class="pT15 pB15 border-top9">
            <el-col :span="12">
              <div class="fc-black-11 text-uppercase fwBold">
                CONTACT NUMBER
              </div>
            </el-col>
            <el-col :span="12">
              <div class="label-txt-black">
                {{ $getProperty(details, 'people.phone', '---') }}
              </div>
            </el-col>
          </el-col>
          <el-col :span="12" class="pT15 pB15 border-top9">
            <el-col :span="12">
              <div class="fc-black-11 text-uppercase fwBold">
                CONTACT EMAIL
              </div>
            </el-col>
            <el-col :span="12">
              <div class="label-txt-black">
                {{ $getProperty(details, 'people.email', '---') }}
              </div>
            </el-col>
          </el-col>
        </el-row>
      </div>

      <div class="pT30">
        <div class="fc-pink text-uppercase text-left fwBold f12">
          PERMIT VALIDITY AND WORK DETAILS
        </div>
        <div class="pT15 flex-middle">
          <div class="fc-black2-12 text-uppercase fwBold line-height20">
            {{ $getProperty(mainField, 'displayName', 'Subject') }} :
          </div>
          <div class="label-txt-black line-height20 pL20">
            {{ $getProperty(details, 'name', '---') || '---' }}
          </div>
        </div>

        <div class="pT10" v-if="!$validation.isEmpty(details.description)">
          <div
            v-html="sanitize(details.description)"
            class="label-txt-black line-height20"
          ></div>
        </div>
        <el-row class="d-flex flex-wrap pT10">
          <el-col
            v-for="(field, index) in displayFields"
            :key="field.name"
            :span="12"
            class="pT15 pB15 border-bottom17"
          >
            <el-col
              :span="12"
              :key="field.displayName"
              class="fc-black-11 text-uppercase fwBold"
              >{{ field.displayName }}</el-col
            >
            <el-col
              :span="12"
              v-if="isSummary && isLookUpRedirectApplicable(field)"
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
              :span="12"
              :key="index + 1 + '-field'"
              class="attachment-label pointer"
            >
              <iframe
                v-if="exportDownloadUrl[field.name]"
                :src="exportDownloadUrl[field.name]"
                style="display: none;"
              ></iframe>

              <a @click="downloadAttachment(field)">
                {{ getFileName(field) }}
              </a>
            </el-col>
            <el-col
              v-else
              :span="12"
              :key="index + 1 + '-field'"
              class="label-txt-black"
              >{{ field.displayValue }}</el-col
            >
          </el-col>
        </el-row>
        <div
          v-if="showViewMore"
          @click="toggleViewMore()"
          class="width100 pT20 text-center"
        >
          <div class="fc-dark-blue3-12 f14 text-center pointer">
            {{ showMoreLinkText }}
          </div>
        </div>
      </div>

      <div v-for="(mode, modeIndex) in tabModes" :key="modeIndex" class="pT30">
        <div
          v-if="!$validation.isEmpty(categories[mode.name])"
          class="fc-pink text-uppercase text-left fwBold f12"
        >
          {{ mode.displayName }}
        </div>
        <div
          v-for="(category, categoryIndex) in categories[mode.name]"
          :key="categoryIndex"
          class="pT20"
        >
          <div class="fc-black2-12 fwBold">
            {{ category.name }}
          </div>
          <div class="work-permit-table width100">
            <table class="setting-list-view-table width100">
              <thead>
                <tr>
                  <th
                    class="setting-table-th setting-th-text uppercase width100px"
                    style="width: 30px;padding-right: 10px;padding-left: 10px;"
                  >
                    S.no
                  </th>
                  <th
                    class="setting-table-th setting-th-text uppercase width300px text-center"
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
                  <td
                    class="width100px text-center"
                    style="width: 30px;padding-left: 10px;padding-right: 10px;"
                  >
                    {{ checklistIndex + 1 }}
                  </td>
                  <td class="width300px">
                    {{ $getProperty(wpChecklist, 'checklist.item', '') }}
                  </td>
                  <td class="width100px text-center">
                    {{ $getProperty(wpChecklist, 'requiredEnum', '') }}
                  </td>
                  <td class="width250px text-center">
                    {{ $getProperty(wpChecklist, 'remarks', '-') }}
                  </td>
                  <td class="width100px text-center">
                    {{
                      $getProperty(wpChecklist, 'isReviewed') === true
                        ? 'Yes'
                        : $getProperty(wpChecklist, 'isReviewed') === false
                        ? 'No'
                        : ''
                    }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PermitChecklistMixin from './PermitChecklistMixin'
import FieldDetails from 'src/components/page/widget/common/field-details/HorizontalFieldDetails.vue'
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'
export default {
  extends: FieldDetails,
  mixins: [PermitChecklistMixin],
  props: ['details', 'calculateDimensions', 'resizeWidget', 'isV3Api'],
  data() {
    return {
      categories: {},
      initialWidgetHeight: null,
    }
  },
  computed: {
    isSummary() {
      return this.$route.name === 'workPermitSummaryV3'
    },
    mainField() {
      let mainField = null
      if (!isEmpty(this.metaInfo)) {
        mainField = (this.$getProperty(this, 'metaInfo.fields', []) || []).find(
          field => {
            return (field || {}).mainField
          }
        )
      }
      return mainField || {}
    },
    displayFields() {
      let { allDisplayFields, isAllVisible } = this
      return isAllVisible ? allDisplayFields : allDisplayFields.slice(0, 6)
    },
    showViewMore() {
      return this.isSummary && (this.allDisplayFields || []).length > 6
    },
    allDisplayFields() {
      let fixedFieldsList = [
        'siteId',
        'space',
        'expectedStartTime',
        'expectedEndTime',
        'ticket',
      ]
      let list = []
      this.fieldsList.forEach(field => {
        if (
          fixedFieldsList.includes(field.name) ||
          this.$getProperty(field, 'field.default') === false
        ) {
          list.push(field)
        }
      })
      return list
    },
  },
  created() {
    this.categories = this.formatPermitChecklistData(this.details.checklist)
    this.sanitize = sanitize
  },
  methods: {
    toggleViewMore() {
      this.isAllVisible = !this.isAllVisible
      this.autoResize()
    },
    autoResize() {
      if (this.isSummary) {
        this.$nextTick(() => {
          let height = this.$refs['preview-container'].scrollHeight + 60
          let width = this.$refs['preview-container'].scrollWidth
          let { h } = this.calculateDimensions({ height, width })
          if (isEmpty(this.initialWidgetHeight)) {
            this.initialWidgetHeight = h
          }
          this.resizeWidget({
            h: this.isAllVisible ? h : this.initialWidgetHeight,
          })
        })
      }
    },
  },
}
</script>
