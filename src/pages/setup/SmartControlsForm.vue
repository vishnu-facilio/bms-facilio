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
          {{ $t('common._common.smart_control_kiosk') }}
        </div>
      </div>
    </div>
    <div class="new-body-modal">
      <el-form ref="smartControlKioskForm" :rules="rules" :model="formModel">
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
          :label="$t('common.wo_report.report_description')"
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
          :label="$t('common.products.site')"
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
          ></Lookup>
        </el-form-item>

        <el-form-item
          :label="$t('common.products.space_type')"
          prop="spaceType"
          class="mB10"
          :required="true"
        >
          <el-select
            :placeholder="$t('common._common.select_space_type')"
            v-model="formModel.spaceType"
            class="fc-input-full-border-select2 width100"
            @change="changeResourcetype"
          >
            <el-option
              :label="$t('common.space_asset_chooser.space')"
              :value="4"
            ></el-option>
            <el-option
              :label="$t('common._common.floor')"
              :value="3"
            ></el-option>
          </el-select>
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
            :resourceType="resourceType"
            @associate="setSpace"
            :visibility.sync="chooserVisibility"
            :query="quickSearchQuery"
            :appendToBody="false"
            :filter="filter"
            picktype="space"
          ></WoSpaceAssetChooser>
        </el-form-item>
        <el-form-item
          :label="$t('common.header.tenant')"
          prop="tenantId"
          class="mB10"
          v-if="$helpers.isLicenseEnabled('TENANTS')"
        >
          <!-- TO DO , ADD filter in lookup to show only tenants in floor -->
          <FLookupField
            :hideLookupIcon="true"
            :model.sync="formModel.tenantId"
            :field="lookupFieldObj"
            :disabled="formModel.spaceType !== 3"
            :siteId="formModel.siteId"
          ></FLookupField>
        </el-form-item>
      </el-form>
    </div>

    <div class="modal-dialog-footer">
      <el-button
        @click="$emit('update:drawerVisibility', false)"
        class="modal-btn-cancel"
        >{{ $t('common._common.cancel') }}</el-button
      >

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
import FLookupField from 'src/components/forms/FLookupField'
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
    WoSpaceAssetChooser,
    FLookupField,
  },
  data() {
    return {
      lookupFieldObj: {
        isDataLoading: false,
        options: [],
        placeHolderText: 'Type to search',
        optionsCache: {},
        lookupModuleName: 'tenant',
        forceFetchAlways: true,
        filters: {},
      },
      formModel: {
        name: '',
        description: '',
        tenantId: null,
        siteId: null,
        spaceType: 4,

        associatedResource: {
          id: null,
        },
      },
      fields,
      chooserVisibility: false,
      spaceDisplayName: '',
      quickSearchQuery: '',
      resourceType: [4],
      filter: { site: null },

      isSaving: false,
      rules: {
        name: [
          {
            required: true,
            message: this.$t('common._common.please_input_kiosk_name'),
            trigger: 'blur',
          },
        ],

        siteId: [
          {
            required: true,
            message: this.$t('common._common.please_choose_a_site'),
            trigger: 'change',
          },
        ],
        spaceType: [
          {
            required: true,
            message: this.$t('common._common.select_space_type'),
            trigger: 'change',
          },
        ],
        associatedResource: [
          {
            validator: (rule, value, callback) => {
              if (areValuesEmpty(value)) {
                callback(
                  new Error(
                    this.$t('common._common.please_choose_a_space_or_floor')
                  )
                )
              } else {
                callback()
              }
            },
            // required: true,
            message: this.$t('common._common.please_choose_a_space_or_floor'),
            trigger: 'change',
          },
        ],
      },
    }
  },
  created() {
    if (this.isEdit) {
      this.setFormModel(this.kioskContext)
    }
    this.$store.dispatch('loadSite')
  },
  methods: {
    changeResourcetype(value) {
      this.resourceType = [value]
      this.formModel.tenantId = null
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
    setFormModel(kioskContext) {
      Object.keys(this.formModel).forEach(key => {
        if (!isEmpty(kioskContext[key])) {
          this.$set(this.formModel, key, kioskContext[key])
        }
      })
      if (this.formModel.associatedResource.id) {
        this.spaceDisplayName = this.formModel.associatedResource.name
      }
      if (this.formModel.tenantId > 0) {
        //avoid ID being shown in lookup field for edit
        this.lookupFieldObj.options.push({
          value: kioskContext.tenant.id,
          label: kioskContext.tenant.name,
        })
        kioskContext.tenant.associatedResource
      }
      this.filter.site = this.formModel.siteId
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
      this.$refs['smartControlKioskForm'].validate(valid => {
        if (valid) {
          this.isSaving = true
          if (!this.formModel.tenantId) {
            //should handle inside lookupitself
            this.formModel.tenantId = -99
          }
          this.$emit('save', { formModel: this.formModel })
        }
      })
    },
  },
}
</script>

<style></style>
