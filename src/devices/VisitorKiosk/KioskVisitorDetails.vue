<template>
  <div class="kiosk-visitor-form">
    <portal to="kiosk-top-right">
      <el-button
        @click="submitForm"
        v-if="showProceed"
        ref="proceedBtn"
        class="proceed-btn"
        :class="[{ 'form-valid': formValid }]"
      >
        Proceed
      </el-button>
    </portal>
    <div class="kiosk fc-kiosk-form">
      <div class="fc-kiosk-form-con">
        <div
          class="fc-kiosk-form position-relative"
          style="align-items: initial;"
        >
          <div
            class="kiosk-input-details pB30"
            v-for="(formField, index) in allFieldList"
            :key="index"
            :class="[{ 'is-focused': formField.focus }]"
          >
            <div class="flex-middle">
              <div class="fc-kiosk-pink-dot mR10"></div>
              <div class="kiosk-input-label-txt">
                {{ formField.displayName }}
              </div>
            </div>
            <div class="flex-middle">
              <el-input
                v-if="
                  formField.displayTypeEnum == 'NUMBER' ||
                    formField.displayTypeEnum == 'EMAIL' ||
                    formField.displayTypeEnum == 'TEXTBOX'
                "
                v-model="formModel[formField.name]"
                :type="formField.displayTypeVal"
                class="fc-kiosk-input-label mL20 kiosk-input-border-remove"
                :ref="'formField-' + index"
                @keyup.native.enter="formFieldSubmit(index)"
                @focus="focus(index)"
              >
              </el-input>
              <el-select
                filterable
                v-model="formModel[formField.name]"
                class="fc-kiosk-input mT10 mL20 kiosk-input-border-remove"
                :ref="'formField-' + index"
                v-else-if="
                  formField.displayTypeEnum == 'USER' ||
                    formField.displayTypeEnum == 'LOOKUP_SIMPLE'
                "
                @focus="focus(index)"
                @change="formFieldSubmit(index)"
                automatic-dropdown
                :disabled="disableHost"
              >
                <el-option
                  v-for="contact in contactList"
                  :key="contact.id"
                  :value="contact.id"
                  :label="contact.name"
                >
                </el-option>
              </el-select>
              <el-switch
                v-else-if="formField.displayTypeEnum == 'DECISION_BOX'"
                class="mT10 mL20"
                v-model="formModel[formField.name]"
              ></el-switch>

              <el-button
                v-if="
                  formField.displayTypeEnum == 'NUMBER' ||
                    formField.displayTypeEnum == 'EMAIL' ||
                    formField.displayTypeEnum == 'TEXTBOX'
                "
                class="fc-kiosk-input-next mT0"
                @click="formFieldSubmit(index)"
              >
                <img src="~assets/kiosk-forward.svg" height="45" width="45" />
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import getProperty from 'dlv'
// COMBINE VISITOR AND VISITOR LOG FORMS
export default {
  props: [
    'visitorFormFields',
    'visitorLogFormFields',
    'sharedData',
    'hostSettings',
  ],
  activated() {
    this.showProceed = true
    //when comp comes into view inside stepper
    let prefilledFormFields = getProperty(this.sharedData, 'preFill')
    if (!prefilledFormFields || !Object.keys(prefilledFormFields).length) {
      this.focus(0)
    }
  },
  deactivated() {
    //when comp comes into view inside stepper
    this.showProceed = false
  },

  created() {
    // on create is run only first time , if prefilled field exists(returning visitor set formmodel to those vals)
    this.loadHostOptions()

    this.processFields()
  },
  mounted() {
    let prefilledFormFields = getProperty(this.sharedData, 'preFill')
    if (!prefilledFormFields || !Object.keys(prefilledFormFields).length) {
      this.focus(0)
    }
  },
  computed: {
    ...mapGetters(['currentAccount']),
  },

  data() {
    return {
      formModel: {},
      showProceed: false,
      allFieldList: [], //field list for UI purpose
      loading: true,
      disableHost: false,
      formValid: false,
      contactList: [],
    }
  },
  methods: {
    async loadHostOptions() {
      let url = 'v2/contacts/all?'

      let hostType = this.hostSettings.hostType

      let queryParamsObj = {
        page: 1,
        perPage: 50,
        filters: JSON.stringify({
          contactType: {
            operatorId: 9,
            value: ['' + hostType],
          },
        }),
      }

      let queryString = new URLSearchParams(queryParamsObj).toString()
      url = url + '?' + queryString

      let { data } = await this.$http.get(url)

      if (this.$getProperty(data, 'result.contacts', null)) {
        this.contactList = data.result.contacts
      }
    },
    processFields() {
      this.allFieldList = [
        ...this.visitorFormFields,
        ...this.visitorLogFormFields,
      ]

      this.allFieldList.forEach(formField => {
        this.$set(formField, 'focus', false)
      })
      let prefilledFormFields = getProperty(this.sharedData, 'preFill')
      if (prefilledFormFields && prefilledFormFields.host) {
        this.disableHost = true
      }

      //prefil if exists else set null
      this.allFieldList.forEach(formField => {
        if (prefilledFormFields) {
          this.$set(
            this.formModel,
            formField.name,
            prefilledFormFields[formField.name]
          )
        } else {
          this.$set(this.formModel, formField.name, null)
        }
      })
    },
    focus(index) {
      //set focus state  of focuced element to true
      this.unfocusAll()
      if (this.$refs['formField-' + index]) {
        this.$refs['formField-' + index][0].focus()
        this.allFieldList[index].focus = true
      }
    },
    unfocusAll() {
      this.allFieldList.forEach(field => {
        field.focus = false
      })
    },
    // enter click and button click
    formFieldSubmit(fieldIndex) {
      this.focus(fieldIndex + 1)

      if (fieldIndex + 1 == this.allFieldList.length) {
        //last element entered , validate and submit
        this.unfocusAll()
        this.formValid = true
        this.$refs['proceedBtn'].$el.focus()
      }
    },
    submitForm() {
      this.$emit('nextStep', this.getFormModel())
    },
    getFormModel() {
      //for user output should be a contex object :{id:213123}, for primitive types just key:'value'
      if (this.formModel.host) {
        this.formModel.host = { id: this.formModel.host }
      }
      return {
        formFields: {
          visitor: this.getVisitorModel(),
          visit: this.getVisitModel(),
        },
      }
      //to do need to handle custom fields etc
    },
    getVisitorModel() {
      let model = {}
      this.visitorFormFields.forEach(formField => {
        model[formField.name] = this.formModel[formField.name]
      })
      return model
    },
    getVisitModel() {
      let model = {}
      model.data = {}
      this.visitorLogFormFields.forEach(formField => {
        if (formField.field.default) {
          model[formField.name] = this.formModel[formField.name]
        } else {
          model.data[formField.name] = this.formModel[formField.name]
        }
      })
      return model
    },
  },
}
</script>
<style lang="scss">
.kiosk-visitor-form {
  .fc-kiosk-input-next {
    display: none;
  }
  .is-focused {
    .fc-kiosk-input-next {
      display: block;

      border-color: #ff3184 !important;
    }
  }

  .form-valid {
    color: #ff3184;
  }

  .el-switch.is-checked .el-switch__core {
    background: #ff3184;
  }
}
</style>
