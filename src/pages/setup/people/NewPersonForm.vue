<template>
  <el-dialog
    :visible.sync="showPersonDialog"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog45 setup-dialog show-right-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form
      :model="person"
      :rules="rules"
      ref="personForm"
      :label-position="'top'"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ formTitle }}
          </div>
        </div>
      </div>

      <div class="new-body-modal">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item
              prop="name"
              :label="$t('setup.users_management._name')"
              :required="true"
            >
              <el-input
                :autofocus="true"
                v-model="person.name"
                :placeholder="$t('setup.users_management.enter_name')"
                class="fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item
              prop="email"
              :label="$t('setup.users_management.email')"
              :required="true"
            >
              <el-input
                v-model="person.email"
                type="text"
                autocomplete="off"
                :disabled="!isNew"
                :placeholder="$t('setup.users_management._enter_email')"
                class="fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="mT20" :gutter="20">
          <el-col :span="12">
            <el-form-item
              :label="$t('setup.users_management.phone_number')"
              prop="phone"
            >
              <el-input
                v-model="person.phone"
                type="number"
                autocomplete="off"
                :placeholder="$t('setup.users_management.enter_phone_number')"
                class="fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="savePerson"
          :loading="isSaving"
        >
          {{ saveBtn }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'

export default {
  name: 'NewPerson',
  data() {
    return {
      person: { name: '', email: '', phone: '' },
      isSaving: false,
      rules: {
        name: [
          {
            required: true,
            message: this.$t('setup.users_management.please_enter_name'),
            trigger: 'blur',
          },
        ],
        email: [
          {
            required: true,
            validator: (rule, email, callback) => {
              if (isEmpty(email)) {
                callback(
                  new Error(this.$t('setup.users_management._enter_email'))
                )
              } else {
                email = email.trim()
                let emailRegx = /^(([^<>()\\[\]\\.,;:\s@"]+(\.[^<>()\\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
                if (!emailRegx.test(email) && this.isNew) {
                  callback(
                    new Error(this.$t('setup.users_management._valid_email'))
                  )
                } else {
                  callback()
                }
              }
            },
            trigger: 'blur',
          },
        ],
      },
    }
  },
  props: ['isNew', 'selectedPerson', 'showPersonDialog'],
  created() {
    let { isNew, selectedPerson } = this
    if (!isNew && !isEmpty(selectedPerson)) {
      this.person = cloneDeep(selectedPerson)
    }
  },
  computed: {
    saveBtn() {
      let { isSaving } = this
      return isSaving
        ? this.$t('setup.users_management.saving')
        : this.$t('setup.users_management.save')
    },
    formTitle() {
      let { isNew } = this
      return isNew
        ? this.$t('setup.users_management.new_person')
        : this.$t('setup.users_management.edit_person')
    },
    moduleName() {
      return 'people'
    },
  },
  methods: {
    closeDialog() {
      this.$emit('closeDialog', true)
    },
    savePerson() {
      this.$refs['personForm'].validate(async valid => {
        this.isSaving = true
        if (!valid) return
        let { person, moduleName, isNew } = this
        let promise
        if (isNew) {
          promise = await API.createRecord(moduleName, {
            data: person,
          })
        } else {
          let { id } = person || {}
          promise = await API.updateRecord(moduleName, { id, data: person })
        }

        let { error } = promise || {}
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$emit('savePerson')
          if (isNew) {
            this.$message.success(
              this.$t('setup.users_management.people_created')
            )
          } else {
            this.$message.success(
              this.$t('setup.users_management.people_updated')
            )
          }
        }
      })
      this.isSaving = false
    },
  },
}
</script>
