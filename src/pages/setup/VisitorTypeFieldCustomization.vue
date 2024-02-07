<template>
  <div class="visitor-setting-con-width mT20" v-if="!loading">
    <div class="non-customizable-fields">
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="16">
            <div class="d-flex justify-start align-center">
              <InlineSvg
                src="svgs/drag-and-drop"
                iconClass="icon icon-md fc-grey-svg-color"
                class="mR15 hide field-reorder-icon opacity-25"
              ></InlineSvg>
              <div class="field-details">
                <div class="fc-black-15 fwBold">Name</div>
                <div class="fc-grey4-13 pT5">TEXTBOX</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6" class="opacity-0">
            <div class="flex-middle justify-content-end align-center">
              <div class="">
                <el-switch v-model="value1"></el-switch>
              </div>
              <div class="label-txt-black pL10 fw6">
                Mandatory
              </div>
            </div>
          </el-col>
          <el-col :span="2" class="text-right"></el-col>
        </el-row>
      </div>
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="16">
            <div class="d-flex justify-start align-center">
              <InlineSvg
                src="svgs/drag-and-drop"
                iconClass="icon icon-md fc-grey-svg-color"
                class="cursor-drag   mR15 hide field-reorder-icon opacity-25"
              ></InlineSvg>
              <div class="field-details">
                <div class="fc-black-15 fwBold">Email</div>
                <div class="fc-grey4-13 pT5">TEXTBOX</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6" class="opacity-0">
            <div class="flex-middle justify-content-end">
              <div class="">
                <el-switch v-model="value1"></el-switch>
              </div>
              <div class="label-txt-black pL10 fw6">
                Mandatory
              </div>
            </div>
          </el-col>
          <el-col :span="2" class="text-right"> </el-col>
        </el-row>
      </div>
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="16">
            <div class="d-flex justify-start align-center">
              <InlineSvg
                src="svgs/drag-and-drop"
                iconClass="icon icon-md fc-grey-svg-color"
                class="cursor-drag   mR15 hide field-reorder-icon opacity-25"
              ></InlineSvg>
              <div class="field-details">
                <div class="fc-black-15 fwBold">Phone</div>
                <div class="fc-grey4-13 pT5">TEXTBOX</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6" class="opacity-0">
            <div class="flex-middle justify-content-end">
              <div class="">
                <el-switch v-model="value1"></el-switch>
              </div>
              <div class="label-txt-black pL10 fw6">
                Mandatory
              </div>
            </div>
          </el-col>
          <el-col :span="2" class="text-right"> </el-col>
        </el-row>
      </div>
    </div>
    <draggable v-model="formFields" @change="saveForm">
      <template v-for="(formField, index) in formFields">
        <div
          class="visitor-hor-card scale-up-left customizable-field"
          v-if="
            formField.name != 'avatar' &&
              formField.name != 'host' &&
              formField.name != 'visitor'
          "
          :key="index"
        >
          <el-row class="flex-middle">
            <el-col :span="16">
              <div class="d-flex justify-start align-center">
                <InlineSvg
                  src="svgs/drag-and-drop"
                  iconClass="icon icon-md fc-grey-svg-color"
                  class="cursor-drag   mR15 hide field-reorder-icon"
                ></InlineSvg>
                <div class="field-details">
                  <div class="fc-black-15 fwBold">
                    {{ formField.displayName }}
                  </div>
                  <div class="fc-grey4-13 pT5">
                    {{ formField.displayTypeEnum }}
                  </div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="flex-middle justify-content-end">
                <div class="">
                  <el-switch
                    v-model="formField.required"
                    @change="saveForm"
                  ></el-switch>
                </div>
                <div class="label-txt-black pL10 fw6">
                  Mandatory
                </div>
              </div>
            </el-col>
            <el-col :span="2" class="text-right">
              <el-dropdown
                trigger="click"
                class="pointer"
                @command="handleCommand($event, formField)"
              >
                <span class="el-dropdown-link">
                  <i
                    class="el-icon-more visitor-type-card-more text-right"
                    style="transform: rotate(0deg);"
                  ></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="delete"> Delete</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-col>
          </el-row>
        </div>
      </template>
    </draggable>

    <el-popover
      placement="top-start"
      width="278"
      class="fc-popover-p02 position-relative"
      trigger="click"
      v-model="showAddFieldPopOver"
      :visible-arrow="false"
    >
      <div class="pB60 max-height400px overflow-scroll">
        <el-row
          class="visibility-visible-actions pT8  pB8"
          v-for="(unUsedField, index) in allUnusedFields"
          :key="index"
        >
          <el-col :span="22">
            <div class="label-txt-black pL10">
              {{ unUsedField.displayName }}
            </div>
          </el-col>
          <el-col :span="2">
            <div class="visibility-hide-actions pR10">
              <img
                @click="addFieldToForm(unUsedField)"
                src="~assets/add.svg"
                width="12"
                height="12"
                class="pointer"
              />
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="visitor-dropdown-btn">
        <el-button
          class="fc-pink-btn-full-width fwBold"
          @click="openCustomDialog"
        >
          <i class="el-icon-plus pR5 fwBold pointer"></i>
          Custom
        </el-button>
      </div>
      <el-button slot="reference" class="setup-el-btn mT20" @click="addBtnClick"
        >Add Field</el-button
      >
    </el-popover>

    <new-visitor-field
      v-if="showNewCustomFieldDialog"
      :visibility.sync="showNewCustomFieldDialog"
      :isNew="isNew"
      @onsave="newCustomFieldAdded"
      :module="module"
    ></new-visitor-field>
  </div>
</template>
<script>
// IMPORT ALL FROM FORM EDIT FOR NOW
import draggable from 'vuedraggable'
import newVisitorField from 'src/components/NewVisitorField'
import FormBuilderMixin from '@/mixins/forms/FormBuilderMixin'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [FormBuilderMixin],
  components: {
    newVisitorField,
    draggable,
  },
  props: ['formsList'],
  data() {
    return {
      isNew: true,
      showNewCustomFieldDialog: false,
      module: 'basevisit',
      value1: true,
      value2: false,
      loading: false,
      allUnusedFields: [],
      showAddFieldPopOver: false,
      formsListSection: [],
    }
  },
  created() {
    this.loading = true
    this.loadAll().then(() => {
      this.loading = false
    })
  },
  computed: {
    formFields: {
      get() {
        return this.formsListSection[0].fields
      },
      set(value) {
        this.formsListSection.forEach(section => {
          section.fields = value
        })
      },
    },
  },
  methods: {
    openCustomDialog() {
      this.showAddFieldPopOver = false
      this.showNewCustomFieldDialog = true
    },
    addBtnClick() {
      if (this.allUnusedFields.length == 0) {
        this.showNewCustomFieldDialog = true
        this.$nextTick(() => {
          this.showAddFieldPopOver = false
        })
      }
    },
    handleCommand(command, formField) {
      if (command === 'delete') {
        this.formsListSection.forEach(form => {
          let { fields } = form
          let idx = (fields || []).findIndex(f => f.id === formField.id)
          if (!isEmpty(idx)) {
            form.fields.splice(idx, 1)
          }
        })

        this.saveForm().then(() => {
          this.loadUnUsedFields()
        })
      }
    },
    newCustomFieldAdded(customField) {
      this.addFieldToForm(customField)
      this.showNewCustomFieldDialog = false
      this.showAddFieldPopOver = false
    },
    async loadAll() {
      this.formsListSection = this.formsList.map(f => {
        let section = this.$getProperty(f, 'sections.1', {})
        if (!section.fields) {
          section.fields = []
        }
        return section
      })

      await this.loadUnUsedFields()

      return
    },

    addFieldToForm(field) {
      this.formsListSection.forEach(form => {
        form.fields.push(field)
      })

      this.saveForm().then(() => {
        this.loadUnUsedFields()
      })
      this.showAddFieldPopOver = false
    },

    async saveForm() {
      //send only ID and serialized sections back to server
      //send active object used in UI builder to 2nd section in both forms

      this.formsList.forEach((form, index) => {
        form.sections[1] = this.formsListSection[index]
      })

      let formsListToSend = this.formsList.map(form => {
        let sections = this.serializeData(form.sections, true)
        return { id: form.id, sections }
      })

      await this.sendForms(formsListToSend)
    },

    async sendForms(data) {
      return API.post('/v2/visitorSettings/initForms', {
        forms: data,
      }).catch(() => {})
    },

    async loadUnUsedFields() {
      let fieldsToFilter = ['nda', 'avatar', 'host']
      let { id } = this.formsList[0]
      let { data, error } = await API.get('/v2/forms/fields/unusedlist', {
        moduleName: this.module,
        formId: id,
      })

      if (!error) {
        let unusedSystemFields = data.fields.systemFields
        let unusedCustomFields = data.fields.customFields

        this.allUnusedFields = [
          ...unusedSystemFields,
          ...unusedCustomFields,
        ].filter(formField => {
          return !fieldsToFilter.includes(formField.field.name)
        })
      }

      return
    },
  },
}
</script>
