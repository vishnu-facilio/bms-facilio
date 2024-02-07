<template>
  <el-drawer
    v-bind:append-to-body="false"
    :visible.sync="drawerVisibility"
    direction="rtl"
    size="40%"
    class="fc-drawer-hide-header"
    v-bind:destroy-on-close="true"
    @close="$emit('update:drawerVisibility', false)"
  >
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{ $t('common._common.feedback_kiosk') }}
        </div>
      </div>
    </div>
    <div class="new-body-modal">
      <el-form ref="feedbackkioskForm" :rules="rules" :model="formModel">
        <el-form-item
          :label="$t('common._common.name')"
          prop="name"
          class="mb15"
          :required="true"
        >
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="formModel.name"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('common.roles.description')"
          prop="description"
          class="mb15"
        >
          <el-input
            :placeholder="$t('common._common.enter_desc')"
            v-model="formModel.description"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('common._common.feedback_types')"
          prop="feedbackTypeId"
          class="mB10"
          :required="true"
        >
          <el-select
            :placeholder="$t('common.wo_report.select_feedback_types')"
            v-model="formModel.feedbackTypeId"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="feedbackType in feedbackTypes"
              :key="feedbackType.id"
              :label="feedbackType.name"
              :value="feedbackType.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('setup.users_management.site')"
          prop="siteId"
          class="mB10"
          :required="true"
        >
          <Lookup
            v-model="formModel.siteId"
            :field="fields.site"
            :hideLookupIcon="true"
            @recordSelected="setSelectedValue"
            @showLookupWizard="showLookupWizardSite"
          >
          </Lookup>
        </el-form-item>

        <el-form-item
          :label="$t('common.header.associated_space')"
          prop="associatedResource"
          class="mB10"
          :required="true"
        >
          <el-input
            @change="
              quickSearchQuery = spaceDisplayName
              chooserVisibility = true
            "
            v-model="spaceDisplayName"
            style="width:100%"
            type="text"
            :placeholder="$t('common._common.to_search_type')"
            class="fc-input-full-border-select2"
          >
            <i
              @click="chooserVisibility = true"
              slot="suffix"
              style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
              class="el-input__icon el-icon-search"
            ></i>
          </el-input>
          <WoSpaceAssetChooser
            @associate="setSpace"
            :visibility.sync="chooserVisibility"
            :query="quickSearchQuery"
            :appendToBody="false"
            :filter="filter"
            picktype="space"
          ></WoSpaceAssetChooser>
        </el-form-item>
      </el-form>
    </div>

    <div class="modal-dialog-footer">
      <el-button
        @click="$emit('update:drawerVisibility', false)"
        class="modal-btn-cancel"
        >{{ $t('common.roles.cancel') }}</el-button
      >
      <!-- <el-button type="primary" @click="addVisitorType" class="modal-btn-save"
            >Save</el-button > -->
      <el-button
        :loading="isSaving"
        type="primary"
        class="modal-btn-save"
        @click="saveRecord"
        >{{ $t('common._common.confirm') }}</el-button
      >
    </div>
  </el-drawer>
</template>

<script>
import FSiteField from '@/FSiteField'
import { areValuesEmpty, isEmpty } from '@facilio/utils/validation'
import WoSpaceAssetChooser from '@/SpaceAssetChooser'
import { Lookup } from '@facilio/ui/forms'
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: false,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
}

export default {
  props: ['drawerVisibility', 'isEdit', 'kioskContext'],
  components: {
    Lookup,
    FSiteField,
    WoSpaceAssetChooser,
  },
  data() {
    return {
      fields,
      formModel: {
        name: '',
        description: '',
        feedbackTypeId: null,
        siteId: null,

        associatedResource: {
          id: null,
        },
      },

      chooserVisibility: false,
      spaceDisplayName: '',
      quickSearchQuery: '',
      filter: { site: null },
      feedbackTypes: [],
      isFeedbackTypeLoading: true,
      isSaving: false,
      rules: {
        name: [
          {
            required: true,
            message: this.$t('common._common.please_input_kiosk_name'),
            trigger: 'blur',
          },
        ],
        feedbackTypeId: [
          {
            required: true,
            message: this.$t('common._common.please_choose_feedback_type'),
            trigger: 'change',
          },
        ],
        siteId: [
          {
            required: true,
            message: this.$t('common._common.please_choose_site'),
            trigger: 'change',
          },
        ],
        associatedResource: [
          {
            validator: (rule, value, callback) => {
              if (areValuesEmpty(value)) {
                callback(
                  new Error(this.$t('common._common.please_choose_space'))
                )
              } else {
                callback()
              }
            },
            // required: true,
            message: this.$t('common._common.please_choose_space'),
            trigger: 'change',
          },
        ],
      },
    }
  },
  created() {
    this.loadFeedbackTypes()
    if (this.isEdit) {
      this.setFormModel(this.kioskContext)
    }
    this.$store.dispatch('loadSite')
  },
  methods: {
    setFormModel(kioskContext) {
      Object.keys(this.formModel).forEach(key => {
        if (!isEmpty(kioskContext[key])) {
          this.$set(this.formModel, key, kioskContext[key])
        }
      })
      if (this.formModel.associatedResource.id) {
        this.spaceDisplayName = this.formModel.associatedResource.name
      }
      this.filter.site = this.formModel.siteId
    },
    showLookupWizardSite(field, canShow) {
      canShow = false
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    setSpace(space) {
      this.chooserVisibility = false
      this.formModel.associatedResource.id = space.id
      this.spaceDisplayName = space.name
    },
    siteSwitched() {
      this.formModel.associatedResource.id = null
      this.quickSearchQuery = ''
      this.spaceDisplayName = ''
    },
    saveRecord() {
      this.$refs['feedbackkioskForm'].validate(valid => {
        if (valid) {
          this.isSaving = true

          this.$emit('save', { formModel: this.formModel })
        }
      })
    },

    async loadFeedbackTypes() {
      // eslint-disable-next-line @facilio/no-http
      let resp = await this.$http.get('v2/feedbackType/list')
      this.feedbackTypes = resp.data.result.feedbackTypes
      this.isFeedbackTypeLoading = false
    },
  },
}
</script>

<style></style>
