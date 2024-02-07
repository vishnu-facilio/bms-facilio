<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div>
      <el-form
        :model="newAgent"
        :rules="rules"
        :label-position="'top'"
        ref="agentForm"
        label-width="120px"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ isNew ? 'New' : 'Edit' }} Agent
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <div class="setup-input-block">
            <p class="fc-input-label-txt">Agent Name</p>
            <el-form-item prop="name">
              <el-input
                :disabled="!isNew"
                v-model="newAgent.agent"
                placeholder="Enter Agent Name"
                class="fc-input-txt fc-name-input"
              ></el-input>
            </el-form-item>
          </div>
          <div class="setup-input-block">
            <p class="fc-input-label-txt fc-input-space-vertical">
              Display Name
            </p>
            <el-form-item prop="name">
              <el-input
                v-model="newAgent.displayName"
                placeholder="Enter Display Name"
                class="fc-input-txt fc-name-input"
              ></el-input>
            </el-form-item>
          </div>
          <div class="setup-input-block">
            <p class="fc-input-label-txt fc-input-space-vertical">Agent Type</p>
            <el-form-item prop="type">
              <el-select
                :disabled="!isNew && model.type > 0"
                v-model="newAgent.type"
              >
                <el-option
                  v-for="(label, value) in $constants.ControllerTypes"
                  :key="value"
                  :label="label"
                  :value="parseInt(value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div>
            <p class="fc-input-label-txt fc-input-space-vertical">Interval</p>
            <el-form-item prop="interval">
              <el-select
                placeholder="Enter Interval"
                v-model="newAgent.interval"
                class="fc-input-select"
              >
                <el-option
                  v-if="newAgent.type === 13"
                  label="2 mins"
                  :value="2"
                ></el-option>
                <el-option label="5 mins" :value="5"></el-option>
                <el-option label="10 mins" :value="10"></el-option>
                <el-option label="15 mins" :value="15"></el-option>
                <el-option label="20 mins" :value="20"></el-option>
                <el-option label="30 mins" :value="30"></el-option>
                <el-option label="1 hour" :value="60"></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div class="setup-input-block">
            <p class="fc-input-label-txt fc-input-space-vertical">Site</p>
            <el-select
              :disabled="!isNew"
              v-model="newAgent.siteId"
              class="fc-input-select"
            >
              <el-option
                v-for="site in sites"
                :key="site.id"
                :label="site.name"
                :value="site.id"
              ></el-option>
            </el-select>
          </div>
          <div class="mT20">
            <p class="fc-input-label-txt fc-input-space-vertical inline mR20">
              Is Writable
            </p>
            <el-switch
              v-model="newAgent.writable"
              class="Notification-toggle"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
            ></el-switch>
          </div>
          <div>
            <p class="fc-input-label-txt fc-input-space-vertical inline mR20">
              Is Active
            </p>
            <el-switch
              v-model="newAgent.state"
              class="Notification-toggle"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
              :active-value="1"
              :inactive-value="0"
            ></el-switch>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">
            CANCEL</el-button
          >
          <el-button
            type="primary"
            @click="submitForm('agentForm')"
            :loading="saving"
            class="modal-btn-save"
            >{{ saving ? 'Saving...' : 'SAVE' }}
          </el-button>
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>

<script>
import { mapState } from 'vuex'

export default {
  props: ['visibility', 'isNew', 'model'],
  data() {
    return {
      saving: false,
      newAgent: {
        agent: '',
        siteId: null,
        type: null,
        interval: null,
        writable: true,
        state: 0,
        displayName: '',
      },
      rules: {
        agent: [
          { required: true, message: 'Please enter a name', trigger: 'blur' },
        ],
        type: [
          {
            required: this.isNew,
            message: 'Please enter a type',
            trigger: 'blur',
          },
        ],
        siteId: [
          { required: true, message: 'Site is mandatory', trigger: 'blur' },
        ],
      },
      buildings: [],
    }
  },
  created() {
    this.$store.dispatch('loadSite')
  },
  mounted() {
    if (!this.isNew) {
      let {
        name,
        siteId,
        type,
        interval,
        writable,
        state,
        displayName,
      } = this.model
      Object.assign(this.newAgent, {
        agent: name,
        siteId,
        type,
        interval,
        writable,
        state,
        displayName,
      })

      let nullableFields = ['siteId', 'interval', 'type']
      nullableFields.forEach(fieldName => {
        if (this.newAgent[fieldName] === -1) {
          this.newAgent[fieldName] = null
        }
      })
    } else {
      this.newAgent.type = 1
      this.newAgent.interval = 15
    }
  },

  computed: {
    ...mapState({
      sites: state => state.site,
    }),
  },

  methods: {
    submitForm(agentForm) {
      this.$refs[agentForm].validate(valid => {
        if (valid) {
          this.saving = true
          let data = { agentContext: this.$helpers.cloneObject(this.newAgent) }
          if (!data.agentContext.interval) {
            data.agentContext.interval = 15
          }
          if (this.isNew) {
            this.$http.post('/v2/setup/agent/create', data).then(response => {
              if (response.status === 200) {
                this.$message('New Agent created.')
                this.saving = false
                this.resetForm()
                this.$emit('saved')
                this.$emit('update:visibility', false)
              }
            })
          } else {
            this.$http.post('/v2/setup/agent/edit', data).then(response => {
              if (response.status === 200) {
                this.$message('Agent Settings updated.')
                this.saving = false
                this.resetForm()
                this.$emit('saved')
                this.$emit('update:visibility', false)
              }
            })
          }
        }
      })
    },
    resetForm() {
      this.$refs['agentForm'].resetFields()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    cancel() {
      this.$emit('canceled')
    },
  },
}
</script>
