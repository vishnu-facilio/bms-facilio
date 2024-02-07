<template>
  <div class="dashboard-action-config">
    <el-row class="pT20 pB20">
      <el-col :span="10">
        <p class="details-Heading">{{ name }}</p>
        <p class="small-description-txt2 width65 break-word">
          {{ description }}
        </p>
      </el-col>
      <el-col :span="10" class="flex items-center">
        <div
          v-if="isActionConfigure"
          class="pR10 configured-green f13 border-right2"
        >
          {{ $t('forms.rules.configured') }}
        </div>
        <el-button
          v-else
          type="button"
          @click="configureAction"
          class="small-border-btn"
          >{{ isActionConfigure ? 'Configured' : 'Configure' }}</el-button
        >

        <span v-if="isActionConfigure" class="mL10">
          <i
            class="el-icon-edit pointer"
            @click="configureAction"
            :title="$t('forms.rules.edit_actions')"
            v-tippy
          ></i>
          <span class="mL10 reset-txt pointer" @click="reset">{{
            $t('forms.rules.reset')
          }}</span>
        </span>
      </el-col>
    </el-row>
    <el-dialog
      v-if="opendiaglog"
      :visible.sync="opendiaglog"
      title="ACTIONS"
      class="fc-dialog-center-container "
      custom-class="action-configure-dialog"
      :append-to-body="true"
      width="70%"
    >
      <DashboardUrl
        v-if="openUrl"
        @actions="actions"
        @closeUrl="closeDialog"
        :presentAction="presentAction"
      ></DashboardUrl>
      <ShowHideSection
        v-if="openSection"
        @closeSection="closeDialog"
        @actions="actions"
        :presentAction="presentAction"
        :sectionsList="sectionsList"
      ></ShowHideSection>
      <DashboardScript
        v-if="isScriptEditor"
        :script_action="presentAction"
        @closeScript="closeDialog"
        @actions="actions"
      ></DashboardScript>
      <DashboardActionFilter
        v-if="isFilter"
        :widgets="widgets"
        @actions="actions"
        @closeFilter="closeDialog"
        :presentAction="presentAction"
        :preExistingRule="preExistingRule"
        :triggerWidgetPlaceholders="triggerWidgetPlaceholders"
      ></DashboardActionFilter>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import DashboardUrl from './DashboardActionUrl'
import ShowHideSection from './DashboardActionShowHideSection'
import DashboardScript from './DashboardActionScript'
import DashboardActionFilter from './DashboardActionFilter.vue'

const ACTION_TYPE_HASH = {
  1: 'Filter',
  2: 'Support Url',
  3: 'Show Section/Hide Section',
  5: 'Execute Script',
}

export default {
  props: [
    'preExistingRule',
    'description',
    'type',
    'widgets',
    'dashboardRules',
    'triggerWidgetPlaceholders'
  ],
  components: {
    DashboardUrl,
    ShowHideSection,
    DashboardScript,
    DashboardActionFilter,
  },
  data() {
    return {
      sectionsList: [],
      name: null,
      isActionConfigure: false,
      openUrl: false,
      opendiaglog: false,
      openSection: false,
      isScriptEditor: false,
      actionType: ACTION_TYPE_HASH,
      isFilter: false,
      presentAction: null,
    }
  },
  created() {
    this.getSections()
    this.name = this.actionType[this.type]
    this.existingAction()
  },
  computed: {
    dashboardLinkName() {
      const {
        params: { dashboardlink: dashboardLinkName },
      } = this.$route ?? {}
      return dashboardLinkName
    },
  },
  methods: {
    async getSections() {
      const { dashboardLinkName } = this ?? {}
      let {
        data: {
          dashboardJson: [{ children: widgets }],
        },
      } = await API.get('v3/dashboard/' + dashboardLinkName)
      this.sectionsList = widgets.map(({ widget }) => {
        if (widget.type == 'section') {
          const { name, desc: description, id } = widget ?? {}
          return {
            sectionName: name,
            description,
            id,
          }
        } else {
          return false
        }
      })
    },
    existingAction() {
      if (!isEmpty(this.dashboardRules)) {
        // this.presentAction = this.dashboardRules.find(action => {
        //   if (action.type === this.type) {
        //     this.isActionConfigure = true
        //     return action
        //   } else return false
        // })
        let showType = 0
        let hideType = 0
        if (this.type === 3) {
          showType = 3
          hideType = 4
          this.presentAction = {
            showSection: {},
            hideSection: {},
          }
        }

        this.dashboardRules.forEach(action => {
          if (action.type === showType) {
            this.presentAction.showSection = action
            this.isActionConfigure = true
          } else if (action.type === hideType) {
            this.presentAction.hideSection = action
            this.isActionConfigure = true
          } else if (action.type === this.type) {
            this.presentAction = action
            this.isActionConfigure = true
          } else this.presentAction = null
        })
      }
    },
    configureAction() {
      // this.isActionConfigure = true
      this.opendiaglog = true
      if (this.type == 2) this.openUrl = true
      else if (this.type == 3) this.openSection = true
      else if (this.type == 5) this.isScriptEditor = true
      else if (this.type == 1) this.isFilter = true
    },
    reset() {
      if (this.type === 1) {
        this.presentAction.target_widgets = null
      } else if (this.type === 2) {
        this.presentAction.action_meta.action_detail.url = null
      } else if (this.type === 3) {
        if (
          !isEmpty(this.presentAction.showSection) &&
          !isEmpty(this.presentAction.hideSection)
        ) {
          this.presentAction.showSection.action_meta.action_detail.section_ids = null
          this.presentAction.hideSection.action_meta.action_detail.section_ids = null
          this.$emit(
            'actions',
            this.presentAction.showSection,
            this.presentAction.hideSection
          )
        }
        if (!isEmpty(this.presentAction.showSection)) {
          this.presentAction.showSection.action_meta.action_detail.section_ids = null
          this.$emit('actions', this.presentAction.showSection, {})
        }
        if (!isEmpty(this.presentAction.hideSection)) {
          this.presentAction.hideSection.action_meta.action_detail.section_ids = null
          this.$emit('actions', {}, this.presentAction.hideSection)
        }
      }else if(this.type === 5){
        this.presentAction.action_meta.script = null
      }

      this.isActionConfigure = false
      if (this.type !== 3) this.$emit('actions', this.presentAction, {})
    },

    actions(val, val1) {
      this.presentAction = val
      // if(this.presentAction.id === undefined){
      //   this.presentAction.id = -1
      // }
      console.log(val, val1)
      let value
      if (this.type === 1) {
        value = val.target_widgets
      } else if (this.type === 2) {
        value = val.action_meta.action_detail.url
      } else if (this.type === 3 && !isEmpty(val)) {
        value = val.action_meta.action_detail.section_ids
      } else if (this.type === 3 && !isEmpty(val1)) {
        value = val1.action_meta.action_detail.section_ids
      } else if(this.type === 5 && !isEmpty(val)){
        value = val.action_meta.script
      }
      else value = null
      this.isActionConfigure=true
      if (isEmpty(value)) {
        this.isActionConfigure = false
      }
      if (this.type === 3) {
        this.presentAction = {}
        this.presentAction.showSection = val
        this.presentAction.hideSection = val1
      }
      this.$emit('actions', val, val1)
      this.opendiaglog = false
    },
    closeDialog(val) {
      this.opendiaglog = false
      this.isActionConfigure = val
    },
    // close() {
    //   this.isActionConfigure = false
    //   this.isScriptEditor = false
    // },
  },
}
</script>
<style lang="scss">
.action-configure-dialog {
  height: 500px !important;
  .el-dialog__headerbtn {
    display: none;
  }
  .el-dialog__body {
    min-height: 393px;
  }
}
.configured-green {
  color: #5bc293;
}
</style>
