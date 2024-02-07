<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog connection-dialog"
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
            <p class="fc-input-label-txt fc-input-space-vertical pT0">
              Agent Type
            </p>
            <el-form-item prop="agentType">
              <el-select
                :disabled="!isNew"
                v-model="newAgent.agentType"
                class="width100"
              >
                <el-option
                  v-for="(label, value) in agentTypes"
                  :key="value"
                  :label="label"
                  :value="parseInt(value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div class="setup-input-block pT20" v-if="newAgent.agentType === 2">
            <el-row>
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  Host Id
                </p>
                <el-form-item prop="agent">
                  <el-input
                    :disabled="!isNew"
                    v-model="newAgent.agent"
                    placeholder="Enter host id"
                    class="fc-input-txt width100"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <el-row>
            <el-col :span="24">
              <div class="setup-input-block">
                <p class="fc-input-label-txt fc-input-space-vertical">
                  Name
                </p>
                <el-form-item prop="displayName">
                  <el-input
                    :disabled="$validation.isEmpty(newAgent.agentType)"
                    v-model="newAgent.displayName"
                    placeholder="Enter Name"
                    class="fc-input-txt width100"
                  ></el-input>
                </el-form-item>
              </div>
            </el-col>
          </el-row>
          <div>
            <el-row :gutter="20" class="mR20">
              <el-col v-if="showInterval" :span="12">
                <p class="fc-input-label-txt fc-input-space-vertical">
                  Interval
                </p>
                <el-form-item prop="interval">
                  <el-select
                    :disabled="$validation.isEmpty(newAgent.agentType)"
                    placeholder="Enter Interval"
                    v-model="newAgent.interval"
                    class="fc-input-select"
                  >
                    <el-option
                      v-for="interval in intervals"
                      :label="interval.label"
                      :value="interval.key"
                      :key="interval.key"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="showInterval ? 12 : 24">
                <p class="fc-input-label-txt fc-input-space-vertical">Site</p>
                <el-form-item prop="siteId">
                  <FLookupFieldWrapper
                    v-model="newAgent.siteId"
                    :field="{
                      lookupModule: {
                        name: 'site',
                      },
                      multiple: false,
                    }"
                    :disabled="$validation.isEmpty(newAgent.agentType) || disableSiteId"
                    :hideLookupIcon="false"
                  ></FLookupFieldWrapper>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row v-if="isMqttType" :gutter="20" class="mR20">
              <el-col>
                <p class="fc-input-label-txt fc-input-space-vertical">
                  {{ $t('agent.agent.connection') }}
                </p>
                <div class="d-flex align-center width100">
                  <el-select
                    :disabled="
                      !isNew || $validation.isEmpty(newAgent.agentType)
                    "
                    v-model="newAgent.messageSourceId"
                    class="fc-input-select connection-select width100"
                  >
                    <el-option
                      v-for="source in sources"
                      :key="source.id"
                      :label="source.name"
                      :value="source.id"
                    ></el-option>
                  </el-select>
                  <div
                    class="d-flex align-center connection-icon"
                    @click="showConnectionFORM()"
                    v-if="isNew"
                  >
                    <inline-svg
                      src="add-pink"
                      iconClass="icon icon-sm-md fc-lookup-icon mL15 mR15 mB10 mT9"
                    ></inline-svg>
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-row v-if="isMqttType" :gutter="20" class="mR20">
              <el-col>
                <p class="fc-input-label-txt fc-input-space-vertical">
                  {{ $t('agent.agent.subscribeTopic') }}
                </p>
                <el-form-item prop="subscribeTopics">
                  <el-input
                    :disabled="
                      !isNew || $validation.isEmpty(newAgent.agentType)
                    "
                    v-model="newAgent.subscribeTopics"
                    placeholder="Enter Subscribe Topic"
                    class="fc-input-txt width100"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <!-- rdm agent -->
          <div class="rdm-sec" v-if="newAgent.agentType === 6 && isNew">
            <div class="setup-input-block">
              <p class="fc-input-label-txt pT10">URL</p>
              <el-form-item prop="url">
                <el-input
                  type="text"
                  v-model="newAgent.url"
                  placeholder="Enter the URL"
                  class="fc-input-txt fc-desc-input"
                ></el-input>
              </el-form-item>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <p class="fc-input-label-txt pT10">User Name</p>
                <el-form-item prop="userName">
                  <el-input
                    type="text"
                    v-model="newAgent.userName"
                    placeholder="Enter the Username"
                    class="fc-input-txt fc-desc-input"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <p class="fc-input-label-txt pT10">Password</p>
                <el-form-item prop="password">
                  <el-input
                    type="text"
                    v-model="newAgent.password"
                    placeholder="Enter the password"
                    class="fc-input-txt fc-desc-input"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div class="e2-sec" v-if="newAgent.agentType === 9 && isNew">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item prop="e2AgentIpAddress" label="IP Address">
                  <el-input
                    type="text"
                    v-model="newAgent.e2AgentIpAddress"
                    placeholder="Enter the IP Adress"
                    class="fc-input-txt fc-desc-input"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="e2AgentPort" label="Port">
                  <el-input
                    type="text"
                    v-model="newAgent.e2AgentPort"
                    placeholder="Enter the Port"
                    class="fc-input-txt fc-desc-input"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div class="setup-input-block"></div>
          <!-- <div class="mT20">
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
          </div>-->
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel"
            >CANCEL</el-button
          >
          <el-button
            type="primary"
            @click="submitForm('agentForm')"
            :loading="saving"
            class="modal-btn-save"
            >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
          >
        </div>
      </el-form>
    </div>
    <ConnectionForm
      v-if="showConnectionForm"
      :onClose="() => (showConnectionForm = false)"
      @save="fetchSources"
    ></ConnectionForm>
  </el-dialog>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import ConnectionForm from './ConnectionForm.vue'
import { isEmpty } from '@facilio/utils/validation'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

export default {
  props: ['visibility', 'isNew', 'model'],
  components: { ConnectionForm, FLookupFieldWrapper },
  data() {
    return {
      saving: false,
      sources: null,
      showConnectionForm: false,
      newAgent: {
        id: 0,
        agent: '',
        siteId: null,
        type: null,
        interval: null,
        writable: true,
        state: 0,
        displayName: '',
        agentType: null,
        messageSourceId: null,
        subscribeTopics: null,
        url: null,
        userName: null,
        password: null,
        e2AgentIpAddress: null,
        e2AgentPort: null,
      },
      rules: {
        displayName: [
          {
            required: true,
            message: 'Please enter a name',
            trigger: 'blur',
          },
        ],
        agentType: [
          {
            required: this.isNew,
            message: 'Please select Agent type',
            trigger: 'blur',
          },
        ],
        siteId: [
          {
            required: true,
            message: 'Site is mandatory',
            trigger: 'blur',
          },
        ],
        agent:[
          {
            required: true,
            message: 'Please enter a Host Id',
            trigger: 'blur',
          }
        ],
        e2AgentIpAddress:[
          {
            required:true,
            message: 'IP Address is mandatory',
            trigger: 'blur'
          }
        ],
        e2AgentPort:[
          {
            required: true,
            message: 'Port is mandatory',
            trigger: 'blur'
          }
        ],
        url:[
          {
            required: true,
            message: 'URL is mandatory',
            trigger: 'blur'
          }
        ],
        userName:[
          {
            required: true,
            message: 'User name is mandatory',
            trigger: 'blur'
          }
        ],
        password:[
          {
            required: true,
            message: 'Password is mandatory',
            trigger: 'blur'
          }
        ],
        subscribeTopics:[
          {
            required: true,
            message:'Subscribe Topic is mandatory',
            trigger: 'blur'
          }
        ],
        messageSourceId:[
          {
            required: true,
            message:'Connection is mandatory',
            trigger:'blur'
          }
        ]


      },
      buildings: [],
      agentServiceTypes: [6, 7, 8, 9],
      disableSiteId: null,
    }
  },
  created() {
    this.$store.dispatch('loadSite')
  },
  mounted() {
    if (!this.isNew) {
      this.fetchAgentDetails()
      // let {
      //   id,
      //   name,
      //   siteId,
      //   type,
      //   interval,
      //   writable,
      //   state,
      //   displayName,
      //   agentType,
      //   messageSourceId,
      //   subscribeTopics,
      // } = this.model
      // console.log(this.model)
      // Object.assign(this.newAgent, {
      //   id,
      //   agent: name,
      //   siteId,
      //   type,
      //   interval,
      //   writable,
      //   state,
      //   displayName,
      //   agentType,
      //   messageSourceId,
      //   subscribeTopics,
      // })

      let nullableFields = ['siteId', 'interval', 'agentType']
      nullableFields.forEach(fieldName => {
        if (this.newAgent[fieldName] === -1) {
          this.newAgent[fieldName] = null
        }
      })
    } else {
      this.newAgent.interval = 15
    }
  },
  watch: {
    isMqttType(val) {
      if (val) {
        this.fetchSources()
      }
    },
  },

  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    ...mapGetters(['getCurrentUser']),
    agentTypes() {
      let types = this.$constants.AgentTypes
      if (!this.$helpers.isLicenseEnabled('CLOUD_AGENT_SERVICE')) {
        this.agentServiceTypes.forEach(type => delete types[type])
      }
      return types
    },
    isMqttType() {
      let { newAgent } = this || {}
      let { agentType } = newAgent || {}
      return agentType == 8
    },
    intervals() {
      const isAgentIntervalEnabled = this.$helpers.isLicenseEnabled('AGENT_INTERVAL');
      let mins = isAgentIntervalEnabled? [1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30] : [10,12,15,20,30]

      mins = mins.map(min => {
        return { key: min, label: min + ' min' + (min === 1 ? '' : 's') }
      })

      mins.push({ key: 60, label: '1 hour' })

      if (
        [3, 7].includes(this.newAgent.agentType) ||
        (this.model && this.model.interval === 1440)
      ) {
        mins.push({ key: 1440, label: '1 day' })
      }

      return mins
    },
    showInterval() {
      if (!isEmpty(this.newAgent.agentType)) {
        return (
          !this.isNew ||
          (this.newAgent.agentType !== 1 && this.newAgent.agentType !== 2)
        )
      }
      return false
    },
  },

  methods: {
    async fetchSources(id) {
      let url = '/v3/agent/connections'
      let { data, error } = await API.get(url)
      if (error) {
        this.$message.error(error.message || 'Error occured')
      } else {
        let { messageSources } = data || {}
        this.sources = messageSources
        if (!isEmpty(id)) {
          this.newAgent.messageSourceId = id
        }
      }
    },
    async fetchAgentDetails() {
      let { model } = this
      let { id } = model
      let url = '/v3/agent/' + id
      let { data, error } = await API.get(url)
      if (error) {
        this.$message.error(error.message || 'Error occured')
      } else {
        let currentAgent = data.agent
        let {
          id,
          name,
          siteId,
          type,
          interval,
          writable,
          state,
          displayName,
          agentType,
          params,
        } = currentAgent || {}

        let { subscribeTopics, messageSourceId } = params || {}

        this.disableSiteId = !this.isNew && siteId
        this.newAgent = {
          id,
          agent: name,
          siteId: siteId || null,
          type,
          interval,
          writable,
          state,
          displayName,
          agentType,
          messageSourceId,
          subscribeTopics,
        }
      }
    },
    showConnectionFORM() {
      this.showConnectionForm = !this.showConnectionForm
    },
    submitForm(agentForm) {
      this.$refs[agentForm].validate(valid => {
        if (valid) {
          this.saving = true
          let data = {
            agentContext: this.$helpers.cloneObject(this.newAgent),
          }
          if (!data.agentContext.interval) {
            data.agentContext.interval = 15
          }

          let agentData = {
            agentName: this.newAgent.agent,
            dataInterval: this.newAgent.interval,
            siteId: this.newAgent.siteId,
            agentType: this.newAgent.agentType,
            displayName: this.newAgent.displayName,
            orgUserId: this.getCurrentUser().ouid,
            messageSourceId: this.newAgent.messageSourceId,
            subscribeTopics: this.newAgent.subscribeTopics,
            userName: this.newAgent.userName,
            password: this.newAgent.password,
            url: this.newAgent.url,
            port: this.newAgent.e2AgentPort,
            ipAddress: this.newAgent.e2AgentIpAddress,
          }
          let editAgent = {
            agentId: this.newAgent.id,
            toUpdate: {
              interval: this.newAgent.interval,
              siteId: this.newAgent.siteId,
              writable: this.newAgent.writable,
              displayName: this.newAgent.displayName,
            },
          }
          if (this.isNew) {
            this.$http.post('/v2/agent/create', agentData).then(response => {
              this.onSubmit(response, 'New Agent created.')
            })
          } else {
            this.$http.post('/v2/agent/edit', editAgent).then(response => {
              this.onSubmit(response, 'Agent Settings updated.')
            })
          }
        }
      })
    },
    onSubmit(response, successMsg) {
      if (response.data.responseCode !== 500) {
        this.$message.success(successMsg)
        this.resetForm()
        this.closeDialog()
        this.$emit('saved')
        this.saving = false
      } else {
        this.$message.error(response.data.result.exception)
        this.saving = false
      }
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
<style lang="scss">
.connection-dialog {
  .connection-select .el-input .el-input__inner {
    width: 100%;
  }

  .connection-icon {
    border-color: rgb(213, 213, 213);
    border-style: solid;
    border-width: 1px;
    border-radius: 1%;
    border-left: none;
    background-color: #f5f7fa;
    cursor: pointer;
    &:hover {
      opacity: 0.6;
    }
  }
}
</style>
<style scoped>
.width250 {
  width: 250px;
}
</style>
