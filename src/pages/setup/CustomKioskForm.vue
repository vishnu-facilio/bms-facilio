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
          {{ $t('common._common.vendor_kiosk') }}
        </div>
      </div>
    </div>
    <div class="new-body-modal">
      <el-form ref="customKioskForm" :rules="rules" :model="formModel">
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
          :label="$t('common._common.button')"
          prop="customDeviceButton"
          class="mB10"
        >
          <el-select
            v-model="formModel.customDeviceButton"
            class="fc-input-full-border-select2 width100"
            :multiple="true"
            filterable
            collapse-tags
            collapse-tags-tooltip
          >
            <el-option
              v-for="custombutton in this.customkioskbutton"
              :key="custombutton.id"
              :label="custombutton.label"
              :value="custombutton.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('setup.users_management.site')"
          prop="siteId"
          class="custom-kiosk-site mB10"
          :required="true"
        >
          <Lookup
            v-model="formModel.siteId"
            :field="fields.site"
            :hideLookupIcon="true"
            :isEdit="isEdit"
            :disabled="siteDisable"
            @recordSelected="setSelectedValue"
            @showLookupWizard="showLookupWizardSite"
            @handleSiteSwitch="siteSwitched"
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
            @change="canShowLookupWizard = true"
            v-model="spaceDisplayName"
            style="width:100%"
            type="text"
            :placeholder="$t('common._common.to_search_type')"
            class="fc-input-full-border-select2"
          >
            <i
              @click="canShowLookupWizard = true"
              slot="suffix"
              style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
              class="el-input__icon el-icon-search"
            ></i>
          </el-input>
          <div v-if="canShowLookupWizard">
            <FLookupFieldWizard
              :canShowLookupWizard.sync="canShowLookupWizard"
              :selectedLookupField="SpaceField"
              @setLookupFieldValue="setSpace"
              :showFilters="true"
              :siteId="selectedSiteId"
              :disableTabSwitch="true"
            ></FLookupFieldWizard>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <div class="modal-dialog-footer">
      <el-button
        @click="$emit('update:drawerVisibility', false)"
        class="modal-btn-cancel"
        >{{ $t('common.roles.cancel') }}</el-button
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
import FSiteField from '@/FSiteField'
import { areValuesEmpty, isEmpty } from '@facilio/utils/validation'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { Lookup } from '@facilio/ui/forms'
import { API } from '@facilio/api'

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
    FLookupFieldWizard,
  },
  data() {
    return {
      siteDisable: false,
      fields,
      customkioskbutton: [],
      selectedSiteId: null,
      formModel: {
        name: '',
        description: '',
        customDeviceButton: [],
        siteId: null,
        associatedResource: {
          id: null,
        },
      },
      SpaceField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'basespace',
        selectedItems: [],
        displayTypeEnum: 'WOASSETSPACECHOOSER',
        displayTypeVal: 'wo-asset-space-chooser',
        field: {
          lookupModule: {
            name: 'resource',
          },
        },
      },
      canShowLookupWizard: false,
      spaceDisplayName: '',
      quickSearchQuery: '',
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
  async mounted() {
    await this.loadButton()
    if (this.isEdit && this.kioskContext?.id) {
      this.loadSummary()
    }
  },
  methods: {
    async loadSummary() {
      let param = { moduleName: 'customkiosk', id: this.kioskContext.id }

      let { data } = await API.get('/v3/modules/data/summary', param)
      if (data?.customkiosk) {
        this.$set(this, 'formModel', data.customkiosk)
        this.formModel.siteId = this.formModel.associatedResource.siteId
        this.spaceDisplayName = this.formModel.associatedResource.name
        this.filter.site = this.formModel.associatedResource.siteId
        this.selectedSiteId = this.formModel.associatedResource.siteId
        this.siteDisable = true
      }
    },
    async loadButton() {
      let queryParam = {
        moduleName: 'customkioskbutton',
      }

      let { data } = await API.get('/v3/modules/data/list', queryParam)
      this.customkioskbutton = data?.customkioskbutton
        ? data.customkioskbutton
        : []
    },
    showLookupWizardSite(field, canShow) {
      canShow = false
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    setSelectedValue(selectedValues, field) {
      this.selectedSiteId = selectedValues.value
      field
    },
    setSpace(space) {
      this.formModel.associatedResource.id = this.$getProperty(
        space,
        'field.selectedItems.0.value'
      )
      this.spaceDisplayName = this.$getProperty(
        space,
        'field.selectedItems.0.label'
      )
    },
    siteSwitched() {
      this.formModel.associatedResource.id = null
      this.quickSearchQuery = ''
      this.spaceDisplayName = ''
    },

    saveRecord() {
      this.$refs['customKioskForm'].validate(valid => {
        if (valid) {
          this.isSaving = true
          let { formModel } = this
          let { customDeviceButton } = formModel || {}
          if (this.isEdit) {
            this.formModel.customDeviceButton = customDeviceButton.map(ele => {
              return { id: ele.id ? ele.id : ele }
            })
          } else {
            this.formModel.customDeviceButton = customDeviceButton.map(ele => {
              return { id: ele }
            })
          }

          this.$emit('save', { formModel: this.formModel })
        }
      })
    },
  },
}
</script>

<style>
.el-select__tags-text {
  font-size: 14px;
  color: black;
}
.custom-kiosk-site {
  .f-ui-forms .f-ui-select .el-input__inner[disabled='disabled'] {
    color: black;
  }
}
</style>
