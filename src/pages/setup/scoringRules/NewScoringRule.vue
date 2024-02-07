<template>
  <div class="formbuilder-fullscreen-popup scoring-rule-container">
    <div class="setting-header">
      <div v-if="!isNew">
        <div class="pointer fc-link fw-normal f13" @click="goBack">
          <inline-svg
            src="left-arrow"
            iconClass="icon icon-sm vertical-text-top mR5"
          ></inline-svg>
          {{ $t('setup.setupLabel.go_back') }}
        </div>
        <div class="mT10 mB5 f22 fw3 letter-spacing0_5">
          {{ $t('setup.setup.edit_scoring_rule') }}
        </div>
      </div>

      <div v-else class="mT10 mB10 f22 fw3 letter-spacing0_5">
        {{ $t('setup.setup.new_scoring_rule') }}
      </div>

      <div class="fR stateflow-btn-wrapper">
        <async-button buttonClass="action-btn" :clickAction="goBack">
          {{ $t('setup.users_management.cancel') }}
        </async-button>

        <async-button
          buttonType="primary"
          buttonClass="action-btn"
          :clickAction="save"
        >
          {{ $t('maintenance._workorder.save') }}
        </async-button>
      </div>
    </div>

    <div class="d-flex setup-grey-bg">
      <div class="scoring-rule-sidebar">
        <a
          id="details-link"
          @click="scrollTo('details')"
          class="scoring-rule-link active"
        >
          {{ $t('setup.setup.score_details') }}
        </a>
        <a
          id="weightage-link"
          @click="scrollTo('weightage')"
          class="scoring-rule-link"
        >
          {{ $t('setup.setup.score_weightage') }}
        </a>
        <a
          id="triggers-link"
          @click="scrollTo('triggers')"
          class="scoring-rule-link"
        >
          {{ $t('setup.setup.triggers') }}
        </a>
      </div>

      <div class="scroll-container">
        <div v-if="isLoading" class="mT20">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <DetailsForm
            id="details-section"
            ref="details-section"
            class="mB20"
            :details="scoringRule.scoreDetails"
            :isNew="isNew"
            :moduleName="moduleName"
          ></DetailsForm>
          <Weightage
            id="weightage-section"
            ref="weightage-section"
            class="mB20"
            :details="scoringRule"
            :isNew="isNew"
            :currentModule="modules"
            :moduleName="moduleName"
          ></Weightage>
          <Triggers
            id="triggers-section"
            ref="triggers-section"
            class="mB20"
            :details="scoringRule"
            :isNew="isNew"
            :moduleName="moduleName"
          ></Triggers>
        </template>
        <div class="mT50"></div>
      </div>
    </div>
  </div>
</template>
<script>
import AsyncButton from '@/AsyncButton'
import SidebarScrollMixin from 'pages/setup/sla/mixins/SidebarScrollMixin'
import DetailsForm from './DetailsSection'
import Weightage from './WeightageSection'
import Triggers from './TriggersSection'
import { isEmpty } from '@facilio/utils/validation'
import { ScoringRule } from './Models/ScoringRuleModel'

export default {
  props: ['id', 'moduleName', 'modules'],
  components: { AsyncButton, DetailsForm, Weightage, Triggers },
  mixins: [SidebarScrollMixin],

  data() {
    return {
      isLoading: false,
      scoringRule: {},
      rootElementForScroll: '.scroll-container',
      sidebarElements: ['#details-link', '#weightage-link', '#triggers-link'],
      sectionElements: [
        '#details-section',
        '#weightage-section',
        '#triggers-section',
      ],
    }
  },

  async created() {
    if (!this.isNew) {
      await this.loadScoringRuleDetails()
    } else {
      let { moduleName } = this
      this.scoringRule = new ScoringRule({ moduleName })
    }
    this.$nextTick(this.registerScrollHandler)
  },

  computed: {
    isNew() {
      return isEmpty(this.id)
    },
  },

  methods: {
    async loadScoringRuleDetails() {
      this.isLoading = true

      try {
        this.scoringRule = await ScoringRule.fetch(this.moduleName, this.id)
      } catch (error) {
        this.$message.error(error)
      }

      this.isLoading = false
    },
    goBack() {
      let { moduleName } = this
      this.$router.push({ name: 'scoringrules.list', moduleName })
    },
    async save() {
      let scoreDetailsValidate = await this.$refs['details-section'].validate()
      let weightageValidate = await this.$refs['weightage-section'].validate()

      if (!scoreDetailsValidate || !weightageValidate) return

      try {
        await this.scoringRule.save()

        let { moduleName } = this

        this.$message.success(
          this.$t('common.header.scoring_rule_saved_successfully')
        )
        this.$router.push({
          name: 'scoringrules.list',
          params: { moduleName },
        })
      } catch (error) {
        this.$message.error(error)
      }
    },
  },
}
</script>
<style lang="scss">
.scoring-rule-container {
  border-left: 1px solid #e3e7ed;
  margin-left: 60px;
  margin-top: 50px;

  .setting-header {
    box-shadow: none;
    border-bottom: 1px solid #e3e7ed;
  }
  .scoring-rule-sidebar {
    background-color: #fff;
    min-width: 300px;
    height: 100vh;
    padding-top: 10px;
  }
  .scroll-container {
    flex-grow: 1;
    margin: 20px;
    overflow-y: scroll;
    max-height: calc(100vh - 150px);
    position: relative;

    > * {
      background-color: #fff;
    }
  }
  .action-btn {
    height: 40px !important;
    line-height: 1;
    display: inline-block;
    letter-spacing: 0.7px !important;
    border-radius: 3px;
  }
  .section-header {
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1.6px;
    color: var(--fc-theme-color);
    text-transform: uppercase;
    margin: 0;
    padding: 28px 50px 20px;

    &.anchor-top {
      position: sticky;
      top: 0;
      width: 100%;
      background: #fff;
      z-index: 2;
      box-shadow: 0 2px 3px 0 rgba(233, 233, 226, 0.5);
    }
  }
  .scoring-rule-link {
    display: block;
    position: relative;
    padding: 11px 0px 11px 40px;
    margin: 0;
    color: #555;
    font-size: 14px;
    border-left: 3px solid transparent;
    letter-spacing: 0.2px;
    text-transform: capitalize;

    &.active {
      background: #f3f4f7;
    }
  }
  .el-form {
    width: 95%;
    max-width: 998px;
  }
  .el-table::before {
    background: initial;
  }
  .el-table th .cell {
    letter-spacing: 1.6px;
    color: #385571;
    padding-left: 0;
    padding-right: 0;
  }
  .el-table--border td:first-child .cell {
    padding-left: initial;
    margin-left: auto;
  }
  .el-table__empty-block {
    border-bottom: 1px solid #ebeef5;
  }
  .el-table__row .actions {
    visibility: hidden;
  }
  .el-table__row:hover .actions {
    visibility: visible;
  }
  .sla-criteria .fc-modal-sub-title {
    color: #385571;
  }
  .task-add-btn {
    padding: 10px 20px;
    border: 1px solid #39b2c2;
    background-color: #f7feff;
    min-height: 36px;
    margin: 20px 0px;

    &:hover {
      border: 1px solid #39b2c2;
      background-color: #f7feff;
    }
    .btn-label {
      font-size: 12px;
      font-weight: 500;
      color: #39b2c2;
      letter-spacing: 0.5px;
    }
    img {
      width: 9px;
    }
  }
}
</style>
