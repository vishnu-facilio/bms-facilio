<template>
  <div
    class="rule-basic-info-container rule-basic-info-container d-flex flex-direction-column"
  >
    <div class="header f12 bold mT30 mL70 d-flex">
      <div>
        <div class="text-uppercase">{{ $t('rule.create.actions') }}</div>
        <div class="fc-heading-border-width43 mT15"></div>
      </div>
    </div>
    <div class="position-relative">
      <div class="rule-info-form">
        <div class="rca-rule-table" v-if="actions.length > 0">
          <div class="flex-middle justify-content-space pT20">
            <div class="fc-text-pink text-uppercase letter-spacing1">
              {{ $t('common.header.alaram_notification_list') }}
            </div>
            <div class="pointer pT10" @click="toggle = true">
              <inline-svg
                src="svgs/add-circled"
                class="vertical-middle"
                iconClass="icon icon-sm icon-add mR5"
              ></inline-svg>
              <span class="btn-text f12 bold green-txt-13 text-uppercase">{{
                $t('common.header.add_actions')
              }}</span>
            </div>
          </div>
          <el-table
            :data="actions"
            style="width: 100%"
            :fit="true"
            class="impact-form-table rule-actions-form-table pT10"
          >
            <el-table-column :label="$t('common._common._rule_name')">
              <template v-slot="action">
                <div class="line-height20">{{ action.row.name }}</div>
              </template>
            </el-table-column>
            <el-table-column :label="$t('common._common.execute_on')">
              <template v-slot="action">
                <div class="line-height20">
                  {{ checkActivityType(action.row.event.activityType) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('common.products._actions')"
              width="250"
            >
              <template v-slot="action">
                <div
                  v-if="action.row.actions"
                  style="display: flex;position: relative;"
                >
                  <div
                    v-for="(actionText, idx) in $helpers.setActionText(
                      action.row.actions
                    )"
                    :key="idx"
                    class="fc-tag mR10"
                  >
                    <el-tag>{{ actionText }}</el-tag>
                  </div>
                </div>
                <div v-else>---</div>
              </template>
            </el-table-column>
            <el-table-column>
              <template>
                <div class="text-right">
                  <span>
                    <inline-svg
                      src="svgs/edit"
                      class="edit-icon-color visibility-hide-actions"
                      iconClass="icon icon-sm mR5 icon-edit"
                    ></inline-svg>
                  </span>
                  <span>
                    <inline-svg
                      src="svgs/delete"
                      class="pointer edit-icon-color visibility-hide-actions mL10"
                      iconClass="icon icon-sm icon-remove fill-red"
                    ></inline-svg>
                  </span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="rule-basic-info-content" v-else>
          <div class="d-flex flex-direction-column text-center">
            <inline-svg
              src="svgs/emptystate/data-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="mT10 empty-text f15 bold">
              {{ $t('common.products.no_actions_available_here') }}
            </div>
            <div class="mT5 empty-text-desc f13">
              {{ $t('common.wo_report.you_havent_configured_action_yet') }}
            </div>
          </div>
        </div>
        <div class="inline-block mT20 text-center">
          <el-popover
            placement="bottom"
            width="250"
            v-model="toggle"
            popper-class="popover-height asset-popover "
            trigger="click"
            visible-arrow="true"
          >
            <ul>
              <li @click="openActionForm('severityAction', 'Severity Change')">
                {{ $t('common._common.severity_change') }}
              </li>
              <li @click="openActionForm('notification', 'Notification')">
                {{ $t('common.header.notification') }}
              </li>
            </ul>
            <el-button
              slot="reference"
              class="pT10 pB10 small-border-btn text-uppercase pL15 pR15"
            >
              <span class="btn-label mL5 f12 bold">{{
                $t('common.header.add_actions')
              }}</span>
            </el-button>
          </el-popover>
        </div>
        <div class="header f12 bold mT30 d-flex">
          <div>
            <div class="text-uppercase">
              {{ $t('common.header.alarm_settings') }}
            </div>
            <div class="fc-heading-border-width43 mT15"></div>
          </div>
        </div>
        <div class="mT30">
          <el-checkbox v-model="sharedData.createWo">{{
            $t('rule.create.create_wo_alarm_creation')
          }}</el-checkbox>
          <el-checkbox
            v-if="sharedData.createWo"
            v-model="sharedData.autoCloseWo"
            >{{ $t('common.header.auto_close_wo_alarm') }}</el-checkbox
          >
        </div>
        <div v-if="showControlPoints">
          <div class="rca-rule-table" v-if="pointsList.length > 0">
            <el-row class="pT40">
              <el-col :span="20">
                <div class="label-txt-black">
                  {{ $t('common.header.control_points') }}
                </div>
              </el-col>
              <el-col :span="4">
                <div
                  class="text-right pointer"
                  @click="showCnfPntsDialog = true"
                >
                  <inline-svg
                    src="svgs/add-circled"
                    class="vertical-middle"
                    iconClass="icon icon-sm icon-add mR5"
                  ></inline-svg>
                  <span class="btn-text f12 bold green-txt-13 text-uppercase">{{
                    $t('home.dashboard.add_points')
                  }}</span>
                </div>
              </el-col>
            </el-row>
            <el-table
              :data="pointsList"
              style="width: 100%"
              :fit="true"
              class="impact-form-table rule-actions-form-table pT30"
            >
              <el-table-column label="ASSET NAME" width="700">
                <template v-slot="point">
                  <div class="line-height20">{{ point.row.asset.name }}</div>
                </template>
              </el-table-column>
              <el-table-column>
                <template>
                  <div class="text-center">
                    <span>
                      <inline-svg
                        src="svgs/edit"
                        class="edit-icon-color visibility-hide-actions"
                        iconClass="icon icon-sm mR5 icon-edit"
                      ></inline-svg>
                    </span>
                    <span>
                      <inline-svg
                        src="svgs/delete"
                        class="pointer edit-icon-color visibility-hide-actions mL10"
                        iconClass="icon icon-sm icon-remove fill-red"
                      ></inline-svg>
                    </span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="rule-basic-info-content" v-else>
            <div class="d-flex flex-direction-column text-center">
              <inline-svg
                src="svgs/emptystate/data-empty"
                iconClass="icon text-center icon-xxxxlg"
              ></inline-svg>
              <div class="mT10 empty-text f15 bold">
                {{ $t('common.products.no_control_point_configured_yet') }}
              </div>
              <div class="mT5 empty-text-desc f13">
                {{ $t('common.wo_report.you_havent_configured_point_yet') }}
              </div>
              <div class="inline-block mT20">
                <el-button
                  class="pT10 pB10 small-border-btn text-uppercase pL20 pR20"
                  @click="showCnfPntsDialog = true"
                  >{{ $t('common.header.add_point') }}</el-button
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <action-form-dialog
      isNew="true"
      :title="dialogTitle"
      :module="'newreadingalarm'"
      :actionType="actionType"
      v-if="showActionForm"
      :visibilityshow.sync="showActionForm"
      @actionSaved="actionOnSave"
    ></action-form-dialog>
    <CnfPntDialog
      :visibility.sync="showCnfPntsDialog"
      @points="addPoints"
    ></CnfPntDialog>
    <div class="modal-dialog-footer" style="position: relative;">
      <el-button @click="goToPrevious" type="button" class="modal-btn-cancel">{{
        $t('common._common.previous')
      }}</el-button>
      <el-button
        @click="saveRule"
        type="button"
        class="modal-btn-save"
        :loading="isSaving"
      >
        {{ $t('common._common.save_rule') }}
      </el-button>
    </div>
  </div>
</template>

<script>
import ActionFormDialog from 'pages/alarm/rule/component/ActionFormDialog'
import CnfPntDialog from 'pages/alarm/rule/component/CnfPntDialog'
export default {
  components: {
    CnfPntDialog,
    ActionFormDialog,
  },
  props: {
    sharedData: {
      type: Object,
    },
  },
  created() {},
  data() {
    return {
      controlPoints: [],
      showCnfPntsDialog: false,
      showControlPoints: false,
      activityTypes: [
        {
          label: this.$t('common.products.create'),
          value: 1,
        },
        {
          label: this.$t('common._common.severity_change'),
          value: 1024,
        },
        {
          label: this.$t('common.header.create_and_severity_change'),
          value: 1025,
        },
        {
          label: this.$t('common.header.clear'),
          value: 2048,
        },
        {
          label: this.$t('common.header.on_date'),
          value: 524288,
        },
        {
          label: this.$t('common.wo_report.field_change'),
          value: 1048576,
        },
        {
          label: this.$t('common._common.delete'),
          value: 4,
        },
      ],
      actionType: null,
      dialogTitle: null,
      showActionForm: false,
      createWo: false,
      module: 'readingRule',
      isSaving: false,
      isLoading: false,
      pointsList: [],
      actions: [],
      autoCloseWo: false,
    }
  },
  methods: {
    checkActivityType(value) {
      let { activityTypes } = this
      return activityTypes.find(type => type.value === value).label
    },
    saveRule() {
      if (this.actions.length > 0) {
        this.sharedData.actions = this.actions
      }
      this.isSaving = true
      this.$emit('generateFinalSharedData', this.sharedData)
    },
    goToPrevious() {
      this.$emit('goToPreviousStep', null)
    },
    actionOnSave(action) {
      this.actions.push(action)
    },
    openActionForm(type, title) {
      this.showActionForm = true
      this.dialogTitle = title
      this.actionType = type
    },
    addPoints(points) {
      this.pointsList.push(points)
    },
  },
}
</script>

<style lang="scss">
.f-webform-container {
  &.reporting-container {
    border: 1px solid #ebedf4;

    .section-container {
      padding: 0 100px 30px 50px;
      border: none;
    }
  }
}
.rule-actions-form-table th > .cell {
  padding-left: 20px;
  padding-right: 20px;
}
.rule-actions-form-table th {
  border-top: 1px solid #f2f5f6;
}

.rule-actions-form-table .el-table__body tr:hover > td {
  background: #f9feff;
}
</style>
