<template>
  <div>
    <el-form
      ref="new-stateflow-form"
      :model="state"
      :rules="rules"
      :label-position="'top'"
      @submit.native.prevent
    >
      <error-banner
        :error.sync="error"
        :errorMessage="errorMessage"
      ></error-banner>

      <div class="new-header-container pL20 pR30 state-transition-header">
        <div class="d-flex">
          <span @click="cancel" class="mR10 pointer" title="Go Back" v-tippy>
            <inline-svg
              src="left-arrow"
              iconClass="icon vertical-middle"
            ></inline-svg>
          </span>
          <div class="stateflow-setup-title">
            {{ isNew ? 'New State' : 'Edit State' }}
          </div>
        </div>

        <div
          v-if="canShowDelete"
          v-tippy
          :title="$t('common._common.remove_state')"
          @click="remove(state)"
        >
          <InlineSvg
            src="remove-icon"
            class="pointer pT3"
            iconClass="icon icon-md mR5"
          ></InlineSvg>
        </div>
      </div>

      <div
        v-if="showProgressBar"
        :class="['sidebar-loader', saving && 'on']"
      ></div>

      <div class="new-body-modal new-stateflow-modal">
        <el-row>
          <el-col :span="24">
            <el-form-item prop="displayName">
              <p class="fc-input-label-txt">{{ $t('setup.stateflow.name') }}</p>
              <el-input
                v-model="state.displayName"
                @change="autoSave()"
                class="pR20"
                :placeholder="$t('setup.stateflow.name_placeholder')"
                :autofocus="true"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item prop="typeCode">
              <p class="fc-input-label-txt">
                {{ $t('maintenance.wr_list.type') }}
              </p>
              <el-radio-group v-model="state.typeCode" @change="autoSave()">
                <el-radio :label="1" class="fc-radio-btn">
                  {{ typeHash['1'] }}
                </el-radio>
                <el-radio :label="2" class="fc-radio-btn">
                  {{ typeHash['2'] }}
                </el-radio>
                <el-radio :label="6" class="fc-radio-btn">
                  {{ typeHash['6'] }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mT20">
          <el-col :span="22">
            <div class="d-flex justify-content-space">
              <p class="fc-input-label-txt inline">
                {{ $t('setup.setupLabel.locked') }}
              </p>
              <el-switch
                v-model="state.recordLocked"
                @change="autoSave()"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </div>
          </el-col>
        </el-row>
        <hr class="separator-line mR20" />
        <el-row>
          <el-col :span="22">
            <div class="d-flex justify-content-space">
              <p class="fc-input-label-txt inline">
                {{ $t('setup.setupLabel.timer_enabled') }}
              </p>
              <el-switch
                v-model="state.timerEnabled"
                @change="autoSave()"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </div>
          </el-col>
        </el-row>
      </div>

      <div v-if="!isAutoSaveNeeded" class="dialog-footer">
        <el-button @click="cancel" class="modal-btn-cancel">Cancel</el-button>
        <el-button
          type="primary"
          class="modal-btn-save shadow-none"
          @click="save"
          :loading="saving"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>
<script>
// TODO:  Update ticketStatus  when state is modified
import ErrorBanner from '@/ErrorBanner'
import debounce from 'lodash/debounce'

export default {
  props: [
    'isNew',
    'stateObj',
    'module',
    'canShowDelete',
    'isAutoSaveNeeded',
    'showProgressBar',
  ],
  components: { ErrorBanner },

  created() {
    this.state = { ...this.state, ...this.stateObj }
  },

  computed: {
    ticketStatus() {
      return this.$store.state.ticketStatus[this.module]
    },
  },
  data() {
    return {
      showDeleteDialog: false,
      saving: false,
      error: false,
      errorMessage: '',
      state: {
        displayName: null,
        typeCode: 1,
        recordLocked: false,
        requestedState: false,
        timerEnabled: false,
      },
      typeHash: {
        '1': 'Open',
        '2': 'Closed',
        '6': 'Skipped',
      },
      rules: {
        display: [
          {
            required: true,
            message: 'Please enter a name for this state',
            trigger: 'blur',
          },
        ],
        typeCode: [
          {
            required: true,
            message: 'Please select a state type',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  methods: {
    save() {
      let data = {}
      data['facilioStatus'] = { ...this.state }
      data['parentModuleName'] = this.module

      let form = this.$refs['new-stateflow-form']

      if (!form) return

      form.validate().then(isValid => {
        if (!isValid) return
        this.saving = true
        this.$emit('onSaving')

        let url = `/v2/state/${this.isNew ? 'add' : 'update'}`
        this.$http
          .post(url, data)
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.$store.dispatch('loadTicketStatus', {
                moduleName: this.module,
                forceUpdate: true,
              })

              if (this.isNew) {
                this.$emit('onStateCreate', data.result.status)
              } else {
                this.$emit(
                  'onStateUpdate',
                  data.result.status,
                  data.result.message
                )
              }
            } else {
              throw new Error(data.message)
            }
          })
          .catch(error => {
            this.$emit('onError', error)
          })
          .finally(() => (this.saving = false))
      })
    },
    remove(state) {
      // TODO: Implement delete functionality
      this.$emit('onDelete', state.id)
    },
    cancel() {
      this.$emit('onClose')
    },
    autoSave: debounce(function() {
      if (this.isAutoSaveNeeded) {
        this.save()
      }
    }, 2 * 1000),
  },
}
</script>
<style lang="scss" scoped>
.new-stateflow-modal {
  &.new-body-modal {
    padding-bottom: 0;
    margin-top: 0;
    padding-top: 20px;
  }
}
.dialog-footer {
  display: flex;
  > * {
    margin: 0;
  }
}
</style>
