<template>
  <div class="main-container d-flex flex-direction-column flex-grow">
    <div
      class="d-flex flex-direction-column flex-grow width100 transition-permalink-container"
    >
      <div v-if="org.name" class="pB10 text-center header-section mB30">
        <img :src="org.logoUrl" :alt="org.name" class="org-logo" />
      </div>

      <div v-if="canShowTransitionBlock" class="mB40">
        <div class="form-container" v-if="canShowForm && formId">
          <TransitionForm
            :moduleName="transition.moduleName"
            :recordId="details.recordId"
            :formId="formId"
            :btnLabel="transition.name || 'Proceed'"
            :saveAction="transitionToState"
            :closeAction="setFailureState"
            :isV3="isV3Module"
          ></TransitionForm>
        </div>

        <div
          v-else-if="!isTransitionInProgress && isTransitionComplete"
          class="f22 bold text-center d-flex flex-direction-column"
        >
          <inline-svg
            src="svgs/permalink/success-icon"
            class="mB20 complete-badge vertical-middle"
            iconClass="icon"
          ></inline-svg>
          <span>
            Status
            <span style="color: #6c63ff;">Updated</span>
          </span>
        </div>

        <div v-else class="text-center">
          <Spinner size="80" :show="true"></Spinner>
        </div>
      </div>

      <div
        v-else-if="hasTransitionFailed"
        class="f22 bold text-center d-flex flex-direction-column mT60 mB30"
      >
        <inline-svg
          src="svgs/permalink/failed-icon"
          class="mB20 error-badge vertical-middle"
          iconClass="icon"
        ></inline-svg>
        <span v-if="errorMessage">{{ errorMessage }}</span>
        <span v-else>
          <span style="color: #d75a4a;">Error</span>
          Occurred
        </span>
        <p v-if="errorSubText" class="f10" style="color: #8e8c97;">
          {{ errorSubText }}
        </p>
      </div>

      <div v-else class="mT-auto mB40">
        <div class="f30 pT20 text-center">Oops!</div>
        <div class="f16 pT10 text-center">
          It looks like this link has expired.
        </div>
      </div>

      <div class="footer mT-auto pT25 pB10 f11 text-center">
        <inline-svg
          src="svgs/home/facilio-logo-dark"
          iconClass="facilio-logo"
        ></inline-svg>
        <div class="text-uppercase mT5">
          © 2021 facilio.com | All Rights Reserved
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import InlineSvg from '@/InlineSvg'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import TransitionForm from './TransitionForm'
import moment from 'moment-timezone'
import { API } from '@facilio/api'

export default {
  props: ['token'],
  components: { InlineSvg, Spinner, TransitionForm },
  data() {
    return {
      org: {},
      isTransitionInProgress: true,
      isTransitionComplete: false,
      hasTransitionFailed: false,
      canShowForm: false,
      details: null,
      transition: null,
      formId: null,
      module: null,
      moduleHash: {
        workorder: {
          url: 'v2/workorders/update',
          key: 'workorder',
          transform: (context, data, formData) => {
            data = {
              ...data,
              id: [context.details.recordId],
              workorder: {},
            }

            if (formData) {
              if (!isEmpty(formData.ticketattachments)) {
                data.ticketattachments = formData.ticketattachments
                delete formData.ticketattachments
              }

              if (formData.comment) {
                data.comment = formData.comment
                delete formData.comment
              }
              data = { ...data, workorder: formData }
            }

            return data
          },
        },
        asset: {
          url: 'v2/assets/update',
          key: 'asset',
          transform: (context, data, formData) => {
            data = {
              ...data,
              id: [context.details.recordId],
              asset: {},
            }

            if (formData) {
              if (formData.comment) {
                data.comment = formData.comment
                delete formData.comment
              }

              data = { ...data, asset: formData }
            }

            return data
          },
        },
      },
      errorMessage: null,
      errorSubText: null,
    }
  },
  created() {
    if (!isEmpty(this.token)) {
      this.loadOrgInfo()
      this.loadData()
    } else {
      this.isTransitionInProgress = false
      this.isTransitionComplete = false
    }
  },
  computed: {
    canShowTransitionBlock() {
      return this.token && !this.hasTransitionFailed
    },
    isV3Module() {
      let { transition: { moduleName = null } = {}, moduleHash } = this
      return moduleName ? isEmpty(moduleHash[moduleName]) : false
    },
  },
  methods: {
    setFailureState(message = null, subText = null) {
      this.errorMessage = message
      this.errorSubText = subText

      this.isTransitionInProgress = false
      this.isTransitionComplete = false
      this.hasTransitionFailed = true
    },

    loadOrgInfo() {
      return API.get('/settings/company').then(({ error, data }) => {
        if (error) {
          this.setFailureState('Link Expired', 'ORG_FETCH_FAILED')
        } else {
          let { org } = data
          this.$set(this, 'org', org || {})

          if (!isEmpty(org.timezone)) {
            this.$helpers.getDateInOrg = function(date) {
              return moment.tz(
                moment(date).format('YYYY-MM-DD HH:mm:ss'),
                org.timezone
              )
            }
          }
        }
      })
    },

    loadData() {
      return API.get('v2/statetransition/detailsFromToken').then(
        ({ error, data }) => {
          if (error) {
            this.setFailureState('Link Expired', 'TRANSITION_FETCH_FAILED')
          } else {
            this.details = JSON.parse(data.session)
            this.transition = data.record

            return this.loadModule().then(() => {
              return !isEmpty(this.transition.formId)
                ? this.showTransitionForm()
                : this.transitionToState()
            })
          }
        }
      )
    },

    loadModule() {
      // DEPRECIATED. TO BE CHANGED ONCE V3 IS SUPPORTED FOR ALL MODULES
      return API.get(`v2/module/${this.details.moduleName}`).then(
        ({ error, data }) => {
          if (error) {
            console.warn(`Could not fetch module`, error)
          } else {
            this.module = data.module
          }
        }
      )
    },

    showTransitionForm() {
      this.formId = this.transition.formId
      this.canShowForm = true
    },

    getTransitionParam(transition) {
      return { stateTransitionId: transition.id }
    },

    transitionToState(formData = null) {
      let {
        transition,
        transition: { moduleName },
      } = this

      if (this.isV3Module) {
        let params = {
          id: this.details.recordId,
          ...this.getTransitionParam(transition),
          data: formData || {},
        }

        return API.updateRecord(moduleName, params).then(({ error }) => {
          if (error) {
            this.setFailureState('Update Failed')
          } else {
            this.isTransitionComplete = true
            this.isTransitionInProgress = false
            this.canShowForm = false
          }
        })
      } else {
        // DEPRECIATED. TO BE REMOVED ONCE V3 IS SUPPORTED FOR ALL MODULES
        let { url, transform } = this.moduleHash[moduleName]

        let data = transform(
          this,
          this.getTransitionParam(transition),
          formData
        )

        return API.post(url, data).then(({ error }) => {
          if (error) {
            this.setFailureState('Update Failed')
          } else {
            this.isTransitionComplete = true
            this.isTransitionInProgress = false
            this.canShowForm = false
          }
        })
      }
    },
  },
}
</script>
<style lang="scss">
.main-container {
  width: 100%;
  max-width: 768px;
  margin: 0 auto;
  padding: 0;

  @media screen and (max-width: 620px) {
    padding: 30px 20px 0;
  }
}

.complete-badge .icon {
  width: 250px;
  height: 250px;
}
.error-badge .icon {
  width: 75px;
  height: 75px;
}

.transition-permalink-container {
  color: #2f2e49;

  @media screen and (min-width: 480px) {
    padding: 30px 20px 0;

    .form-container {
      padding-left: 35px;
      padding-right: 35px;
    }
  }

  .header-section {
    border-bottom: 1px solid #eff4f7;
  }
  .footer {
    border-top: 1px solid #eff4f7;
    color: #8e8c97;
    letter-spacing: 0.38px;
  }
  .org-logo {
    width: 100px;
  }
  .facilio-logo {
    width: 85px;
    height: 30px;
  }

  // Overrides
  .form-btn.primary {
    background-color: #ff3184;
  }
  .fc-web-form-action-btn {
    margin-top: 35px;
  }
  .form-container .form-date-picker.el-input,
  .form-date-picker.el-date-editor--daterange.el-input__inner {
    width: 100%;
  }
  .form-container {
    .section-header {
      color: rgba(56, 85, 113, 0.68);
      font-weight: bold;
      text-transform: uppercase;
      font-size: 12px;
    }
    .fc-heading-border-width43 {
      margin-top: 5px !important;
    }
    .f-subform-wrapper {
      margin-top: 15px;
    }

    .fc-attachments {
      .attached-files {
        .attached-file-size {
          font-size: 12px;
          letter-spacing: 0.3px;
          color: #a3a2b1;
        }
      }
      .fc-attachment-row {
        &.d-flex {
          padding: 15px;
        }
        &:hover {
          .attachment-delete {
            opacity: 1;
          }
        }
      }
      .attachment-icon {
        height: 15px;
        width: 13px;
      }
      .attachment-delete {
        opacity: 0;
        color: #de7272;
        font-size: 16px;
      }
      .attached-files {
        font-size: 14px;
        letter-spacing: 0.5px;
        margin: 0;
        line-height: 1;
        color: #324056;
      }
    }
  }
}
</style>
