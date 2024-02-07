<template>
  <div class="pm-creation-container">
    <div class="header">
      <div class="header-text">{{ $t('maintenance.pm.new_pm') }}</div>

      <div class="stepper-container">
        <FormStepper
          :steps="steps"
          :active="activePage"
          @onStepClick="redirectToStep"
        />
      </div>
      <el-button class="discard-button" @click="redirectToList">{{
        cancelButtonText
      }}</el-button>
    </div>
    <div class="page-container">
      <component
        ref="pm-creation-page"
        :is="activePage"
        :pmProps="pmProps"
        :isSaving.sync="isSaving"
        :disableButton.sync="disableButton"
      ></component>
      <div class="footer-container mT25">
        <div class="actions-container">
          <el-button
            v-if="currentPageObject.cancelText"
            class="cancel-button"
            @click="onCancelClick"
            ><i class="el-icon-arrow-left mR5" />{{
              $t(currentPageObject.cancelText)
            }}</el-button
          >
          <el-button
            v-if="currentPageObject.saveText"
            class="save-button"
            :class="[disableButton && 'disabled-pm-save']"
            @click="onSuccessClick"
            :loading="isSaving"
            :disabled="disableButton"
            >{{ $t(currentPageObject.saveText)
            }}<i class="el-icon-arrow-right mL5"
          /></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FormStepper from './FormStepper'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['isEdit', 'id'],
  components: {
    FormStepper,
    configuration: () => import('./pages/WorkorderConfiguration'),
    publish: () => import('./pages/Publish.vue'),
    planner: () => import('./pages/Planner.vue'),
  },
  created() {
    let { isEdit } = this || {}
    if (isEdit) {
      this.deactivatePm()
    }
  },
  data: () => ({ isSaving: false, disableButton: false }),
  watch: {
    activePage: {
      async handler(newVal) {
        if (!isEmpty(newVal)) this.disableButton = false
      },
      immediate: true,
    },
  },
  computed: {
    pmProps() {
      let id = this.$getProperty(this, '$route.query.id')
      id = !isEmpty(id) ? parseInt(id) : null
      return { id }
    },
    moduleName() {
      return 'plannedmaintenance'
    },
    activePage() {
      let { $route } = this || {}
      let { query } = $route || {}
      let { tab } = query || {}
      return tab || 'configuration'
    },
    currentPageObject() {
      let { activePage, steps } = this || {}
      return (steps || {}).find(page => page.name === activePage)
    },
    cancelButtonText() {
      let { activePage } = this

      return activePage === 'configuration'
        ? this.$t('maintenance.pm.discard')
        : this.$t('maintenance.pm.exit')
    },
    steps() {
      return [
        {
          name: 'configuration',
          displayName: 'Configuration',
          saveText: 'maintenance.pm.next',
        },
        {
          name: 'planner',
          displayName: 'Planner',
          saveText: 'maintenance.pm.next',
          cancelText: 'maintenance.wr_list.previous',
        },
        {
          name: 'publish',
          displayName: 'Publish',
          saveText: 'maintenance.pm.publish',
          cancelText: 'maintenance.wr_list.previous',
        },
      ]
    },
  },
  methods: {
    redirectToStep(currentStep) {
      let { name } = currentStep || {}
      let query = this.$getProperty(this, '$route.query')
      this.$router.push({ path: '', query: { ...query, tab: name } })
    },
    async deactivatePm() {
      let { id: pmId } = this || {}
      let { error } = await API.post('/v3/plannedmaintenance/deactivate', {
        pmId,
      })
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      }
    },
    redirectToList() {
      let { viewname } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'pm-list',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    async onCancelClick() {
      let pageComponent = this.$refs['pm-creation-page']
      if (!isEmpty(pageComponent)) {
        this.isSaving = true
        let { onCancel } = pageComponent || {}
        if (!isEmpty(onCancel)) await pageComponent.onCancel()
        this.isSaving = false
      }
    },
    async onSuccessClick() {
      let pageComponent = this.$refs['pm-creation-page']
      if (!isEmpty(pageComponent)) {
        this.isSaving = true
        let { onSave } = pageComponent || {}
        if (!isEmpty(onSave)) await pageComponent.onSave()
        this.isSaving = false
      }
    },
  },
}
</script>

<style scoped lang="scss">
.header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 10px 15px;
  background-color: #ffffff;
  align-items: center;
  height: 60px;
}
.header-text {
  font-size: 16px;
  color: #324056;
  font-weight: 500;
}
.discard-button {
  border-radius: 3px;
  box-shadow: 0 2px 4px 0 rgb(230 230 230 / 50%) !important;
  border: solid 1px #39b2c2;
  background-color: #ffffff;
  padding: 12px 25px;
  cursor: pointer;
  text-transform: capitalize;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.6px;
  text-align: center;
  color: #39b2c2;
  &:hover {
    color: #ffffff;
    background-color: #39b2c2;
  }
}
.save-button,
.cancel-button {
  width: 100%;
  border-radius: 3px;
  padding: 12px 30px;
  cursor: pointer;
  text-transform: uppercase;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.6px;
  text-align: center;
}
.save-button {
  box-shadow: 0 2px 4px 0 rgb(230 230 230 / 50%) !important;
  border: solid 1px #39b2c2;
  color: #ffffff;
  background-color: #39b2c2;
}
.cancel-button {
  background-color: #f4f4f4;
  color: #8f8f8f;
}
.page-container {
  background-color: #ffffff;
  height: 100%;
  margin: 5px;
  display: flex;
  align-items: center;
  flex-direction: column;
}
.stepper-parent-container {
  border-bottom: solid 1px #eaeaea;
  width: 100%;
  display: flex;

  justify-content: center;
}
.stepper-container {
  padding: 10px 20px;
  width: 40%;
}
.footer-container {
  border-top: solid 1px #eaeaea;
  width: 100%;
  justify-content: center;
  display: flex;
}
.actions-container {
  padding: 10px 0px;
  width: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
<style lang="scss">
.pm-creation-container {
  .section-sub-heading {
    font-size: 16px;
    font-weight: bold;
    color: #ff3184;
  }
  .section-heading {
    color: #324056;
    font-size: 18px;
    font-weight: bold;
    letter-spacing: 1.1px;
  }
  .el-form-item__label {
    display: flex;
    letter-spacing: 0.5px !important;
    color: #324056;
  }
  .el-form-item.is-required .el-form-item__label {
    display: flex;
    letter-spacing: 0.5px !important;
    color: #324056 !important;
  }
  .el-form-item.is-required .el-form-item__label:after {
    display: block !important;
  }
  .section-description {
    color: #324056;
    font-size: 14px;
    line-height: 20px;
  }
  .pm-green-dot {
    width: 10px;
    height: 10px;
    background-color: #38b2c2;
    border-radius: 5px;
  }
  .pm-content-width {
    width: 50%;
  }
  .disabled-pm-save {
    pointer-events: none !important;
  }
}
</style>
