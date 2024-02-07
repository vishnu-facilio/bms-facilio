<template>
  <div class="planner-container">
    <div class="planner-header pm-content-width">
      <div class="section-heading mB10 mT20">
        {{ $t('maintenance.pm.planner') }}
      </div>
      <div class="section-description">
        {{ getFormattedText($t('maintenance.pm.planner_desc'), true) }}
      </div>
    </div>
    <div v-if="isPlannerListLoading || isPmLoading" class="width100">
      <div
        class="d-flex mT50 planner-body"
        :class="
          isPlannerListLoading || isPmLoading ? 'planner-body-loading' : ''
        "
      >
        <Spinner :show="isPlannerListLoading || isPmLoading"></Spinner>
      </div>
    </div>
    <div
      v-else-if="$validation.isEmpty(plannerList)"
      class="planner-empty-state pm-content-width"
    >
      <inline-svg
        :src="`svgs/emptystate/readings-empty`"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="empty-state-txt">
        {{ $t('maintenance.pm.no_planner') }}
      </div>
      <el-button
        @click="addNewPlanner"
        :loading="isSavingPlanner"
        class="save-button"
        >{{ $t('maintenance.pm.add_planner') }}</el-button
      >
    </div>
    <div v-else class="width100">
      <div class="planner-tab-container">
        <div class="pm-content-width flex">
          <div
            class="planner-tab"
            v-for="planner in filteredPlannerList"
            :class="
              planner.id === selectedPlanner ? 'selected-planner-tab' : ''
            "
            :key="planner.id"
            @click="(...args) => switchPlannerTab(args, planner)"
          >
            <div>{{ planner.name }}</div>
            <el-dropdown
              @command="(...args) => handlePlannerActions(args, planner)"
              trigger="click"
            >
              <div class="more-icon">
                <fc-icon
                  group="default"
                  name="ellipsis-horizontal"
                  class="fwBold"
                ></fc-icon>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="editPlanner" class="d-flex pT10">
                  <fc-icon
                    group="default"
                    name="edit-solid"
                    size="16"
                    class="mR2"
                  ></fc-icon>
                  <span class="option-text">
                    {{ $t('common._common.edit') }}
                  </span>
                </el-dropdown-item>
                <el-dropdown-item command="deletePlanner" class="d-flex pT10">
                  <fc-icon
                    group="default"
                    name="trash-can-solid"
                    size="16"
                    class="mR2"
                  ></fc-icon>
                  <span class="option-text">
                    {{ $t('common._common.delete') }}
                  </span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

          <el-dropdown
            v-if="!$validation.isEmpty(dropDownPlannerList)"
            @command="moveTabSelection"
            trigger="click"
          >
            <div class="flex align-center pR20 pL20 cursor-pointer height100">
              <i class="el-icon-more fwBold" />
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="planner in dropDownPlannerList"
                :command="planner.id"
                :key="planner.id"
              >
                {{ planner.name }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <div
            class="flex align-center pR20 pL20 cursor-pointer"
            @click="addNewPlanner"
          >
            <Spinner
              size="20"
              v-if="isSavingPlanner"
              :show="isSavingPlanner"
            ></Spinner>
            <i v-else class="el-icon-plus fwBold" />
          </div>
          <div></div>
        </div>
      </div>
      <div class="width100 flex justify-center planner-body">
        <div v-if="isPlannerLoading" class="d-flex mT50 planner-body">
          <Spinner :show="isPlannerLoading"></Spinner>
        </div>
        <div v-else class="pm-content-width mT20">
          <TriggerSection
            :pmProps="pmProps"
            :planner="selectedPlannerRecord"
            :reloadPlanner="loadPlanner"
            :trigger="trigger"
            :pmRecord="pmRecord"
            :showTaskButton="isJobPlanEmpty"
            @openTaskDialog="openTaskDialog"
          />
          <div class="section-divider"></div>
          <TaskSection
            :showTaskDialog.sync="showTaskDialog"
            :closeDialog="() => (showTaskDialog = false)"
            :reloadPlanner="loadPlanner"
            :pmRecord="pmRecord"
            :planner="selectedPlannerRecord"
          />

          <div class="section-divider" v-if="!isJobPlanEmpty"></div>
          <ResourcePlanner
            :pmRecord="pmRecord"
            :planner="selectedPlannerRecord"
          />
          <div class="section-divider"></div>
        </div>
      </div>
    </div>
    <EditPlanner
      v-if="showPlannerEdit"
      :planner="selectedPlannerAction"
      :closeDialog="() => (showPlannerEdit = false)"
      :onSave="loadPlanners"
    />
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import TriggerSection from './planner/TriggerSection'
import ResourcePlanner from './planner/ResourcePlanner'
import EditPlanner from './planner/EditPlanner'
import TaskSection from './planner/tasks/TaskSection'
import {
  getPlaceholderText,
  getResourcePlaceholder,
} from '../utils/pm-utils.js'

const MAX_TABS = 4
export default {
  props: ['pmProps'],
  created() {
    this.init()
  },
  components: {
    Spinner,
    TriggerSection,
    ResourcePlanner,
    EditPlanner,
    TaskSection,
  },
  data: () => ({
    moduleName: 'pmPlanner',
    plannerList: [],
    isSavingPlanner: false,
    isPlannerListLoading: false,
    selectedPlanner: null,
    selectedPlannerAction: null,
    selectedPlannerRecord: null,
    isPlannerLoading: false,
    showPlannerEdit: false,
    pmRecord: {},
    showTaskDialog: false,
    isPmLoading: false,
  }),
  watch: {
    pmProps: {
      handler() {
        this.loadPm()
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    filteredPlannerList() {
      let { plannerList } = this || {}

      return (plannerList || []).filter((_, index) => index < MAX_TABS)
    },
    dropDownPlannerList() {
      let { plannerList } = this || {}
      return (plannerList || []).filter((_, index) => index >= MAX_TABS)
    },
    trigger() {
      let { selectedPlannerRecord } = this || {}
      let { trigger } = selectedPlannerRecord || {}
      return trigger
    },
    isJobPlanEmpty() {
      let { selectedPlannerRecord } = this || {}
      let { jobPlan } = selectedPlannerRecord || {}
      let { id } = jobPlan || {}
      return isEmpty(id)
    },
  },
  methods: {
    async init() {
      this.isPlannerListLoading = true
      await this.loadPlanners({ skipLoading: true })
      let { plannerList } = this || {}
      if (isEmpty(plannerList)) {
        await this.addNewPlanner({ skipLoading: true, hideMessage: true })
      }
      this.isPlannerListLoading = false
    },
    async loadPm() {
      this.isPmLoading = true
      let moduleName = 'plannedmaintenance'
      let { pmProps } = this || {}
      let { id } = pmProps
      let { [moduleName]: data, error } = await API.fetchRecord(
        moduleName,
        {
          id,
        },
        { force: true }
      )
      if (isEmpty(error)) {
        this.pmRecord = data
      } else {
        this.$message.error(
          error.message || 'Error occured while fetching planned maintenance'
        )
      }
      this.isPmLoading = false
    },
    async loadPlanners(props) {
      // To decided if we have to load the planners list,
      // not needed while adding new planner.
      let { skipLoading } = props || {}
      if (!skipLoading) {
        this.isPlannerListLoading = true
      }
      let { moduleName, pmProps, selectedPlanner } = this || {}
      let { id } = pmProps || {}
      let params = {}
      if (!isEmpty(id)) {
        let filters = JSON.stringify({
          pmId: { operatorId: 9, value: [`${id}`] },
        })
        params = { ...params, filters }
      }
      let { list, error } = await API.fetchAll(moduleName, params, {
        force: true,
      })
      if (isEmpty(error)) {
        this.plannerList = list
        let isIdSelected = list.some(planner => planner.id === selectedPlanner)
        // No need to set selected planner if we have an already selected planner
        if (!isIdSelected && !isEmpty(list)) {
          let [initPlanner] = list || []
          let { id } = initPlanner || {}
          this.selectedPlanner = id
          await this.loadPlanner()
        }
      }
      if (!skipLoading) {
        this.isPlannerListLoading = false
      }
    },
    async loadPlanner() {
      this.isPlannerLoading = true
      let { moduleName, selectedPlanner: id } = this || {}
      let { [moduleName]: record, error } = await API.fetchRecord(
        moduleName,
        {
          id,
        },
        { force: true }
      )
      if (isEmpty(error)) this.selectedPlannerRecord = record
      this.isPlannerLoading = false
    },

    getFormattedText(text, isUpperCase, removePlural) {
      let { pmRecord } = this || {}
      let placeholder = getResourcePlaceholder(pmRecord, isUpperCase)
      let formattedPlaceholder = placeholder.slice(0, -1)
      let formattedText = getPlaceholderText({
        pmRecord,
        text,
        isUpperCase,
        removePlural,
      })
      return formattedText.replace(placeholder, formattedPlaceholder)
    },
    async addNewPlanner(props) {
      let { skipLoading, hideMessage } = props || {}
      this.isSavingPlanner = true
      let { moduleName, pmProps, plannerList } = this || {}
      let { length } = plannerList || []
      let { id: pmId } = pmProps || {}
      let data = { pmId, name: `Planner ${length + 1}` }
      let { [moduleName]: record, error } = await API.createRecord(moduleName, {
        data,
      })
      if (isEmpty(error)) {
        await this.loadPlanners({ skipLoading })
        if (!hideMessage)
          this.$message.success(this.$t('maintenance.pm.planner_added'))
        let { id } = record || {}
        if (length >= MAX_TABS) {
          this.moveTabSelection(id)
        } else {
          this.selectedPlanner = id
          this.loadPlanner()
        }
      }
      this.isSavingPlanner = false
    },
    switchPlannerTab(args, planner) {
      // This method will be triggered even when the more icon is clicked,
      // to avoid that we are checking if it belongs to that div.
      let clickedElementClass = this.$getProperty(args, '0.target.className')
      let { id } = planner || {}
      let { selectedPlanner } = this || {}
      if (
        !['more-icon', 'el-icon-more fwBold'].includes(clickedElementClass) &&
        selectedPlanner !== id
      ) {
        this.selectedPlanner = id
        this.loadPlanner()
      }
    },
    moveTabSelection(plannerId) {
      let { plannerList } = this || {}
      let currentPlanner = plannerList.find(planner => planner.id === plannerId)
      plannerList = plannerList.filter(planner => planner.id !== plannerId)
      plannerList.splice(MAX_TABS - 1, 0, currentPlanner)

      this.$set(this, 'plannerList', plannerList)
      this.selectedPlanner = plannerId
      this.loadPlanner()
    },
    async handlePlannerActions(args, currPlanner) {
      let { moduleName, selectedPlanner } = this || {}
      let command = this.$getProperty(args, '0')
      let { id } = currPlanner || {}
      if (command === 'editPlanner') {
        this.selectedPlannerAction = currPlanner
        this.showPlannerEdit = true
      } else if (command === 'deletePlanner') {
        let dialogObj = {
          title: this.$t('maintenance.pm.planner_delete'),
          message: this.$t('maintenance.pm.planner_delete_prompt'),
          rbLabel: this.$t('common._common.delete'),
          rbClass: 'pmv2-delete-dialog-btn',
          className: 'pmv2-delete-dialog',
        }
        let canDelete = await this.$dialog.confirm(dialogObj)
        if (canDelete) {
          let { error } = await API.deleteRecord(moduleName, [id])
          if (isEmpty(error)) {
            this.$message.success(
              this.$t('maintenance.pm.planner_delete_message')
            )
            if (id === selectedPlanner) {
              this.selectedPlanner = null
              this.selectedPlannerRecord = {}
            }
            this.loadPlanners()
          } else {
            this.$message.error(error.message || 'Error Occured')
          }
        }
      }
    },
    openTaskDialog() {
      this.showTaskDialog = true
    },
    onSave() {
      let { pmRecord } = this || {}
      let { id } = pmRecord || {}
      if (isEmpty(this.plannerList)) {
        this.$message.warning('Please create a planner to publish it.')
      } else {
        this.$router.push({ path: '', query: { id: id, tab: 'publish' } })
      }
    },
    onCancel() {
      let { $route } = this || {}
      let { query } = $route || {}
      this.$router.push({ path: '', query: { ...query, tab: 'configuration' } })
    },
  },
}
</script>

<style scoped lang="scss">
.planner-container {
  width: 100%;
  height: calc(100vh - 210px);
  display: flex;
  flex-direction: column;
  align-items: center;
}
.planner-empty-state {
  height: calc(100vh - 205px);
  display: flex;
  flex-direction: column;
  padding-top: 40px;
  overflow: scroll;
}
.planner-body {
  height: calc(100vh - 370px);
  overflow: scroll;
  width: 100%;
}
.planner-body-loading {
  height: calc(100vh - 360px);
}
.empty-state-txt {
  font-weight: 500;
  font-size: 18px;
  color: #324056;
  letter-spacing: 0.5px;
  display: flex;
  justify-content: center;
}

.save-button {
  border-radius: 3px;
  padding: 12px 30px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.6px;
  text-align: center;
  box-shadow: 0 2px 4px 0 rgb(230 230 230 / 50%) !important;
  border: solid 1px #39b2c2;
  color: #ffffff;
  background-color: #39b2c2;
  margin-top: 10px;
  width: 30%;
  margin-left: 35%;
}
.planner-tab-container {
  border-top: solid 1px #dfe5eb;
  border-bottom: solid 1px #dfe5eb;
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.planner-tab {
  padding: 10px;
  background-color: #f9f9f9;
  border-top: solid 2px transparent;
  cursor: pointer;
  display: flex;
  min-width: 120px;
  justify-content: space-between;
  &:hover {
    background-color: #f4f2f2;
    .more-icon {
      visibility: visible;
      color: #615e88;
    }
  }
}
.planner-tab {
  border-right: solid 1px #d9d9d9;
}
.planner-tab:first-child {
  border-left: solid 1px #d9d9d9;
}
.more-icon {
  padding: 0px 5px 0px 10px;
  margin-left: 5px;
  visibility: hidden;
}
.selected-planner-tab {
  border-top: solid 2px #39b2c2;
  background-color: #f3f3f3;
}
.section-divider {
  border-top: solid 1px #dfe5eb;
  width: 100%;
  margin: 20px 0px;
}
.option-text {
  font-size: 14px;
  font-weight: 500;
  color: #4f4f4f;
  margin-left: 10px;
  margin-top: -10px;
}
</style>
<style lang="scss">
.planner-container {
  .inline {
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
.pmv2-delete-dialog {
  .f-dialog-header {
    display: none;
  }
  .f-dialog-content {
    padding-top: 15px;
    padding-left: 30px;
    padding-right: 10px;
    min-height: 170px;
    width: 650px;
    text-align: justify;
  }
  .f-dialog-body {
    padding: 0px;
  }
  .del-cancel-btn {
    width: 50%;
  }
  .pmv2-delete-dialog-btn {
    width: 50%;
    background-color: #39b2c2 !important;
    border: transparent;
    margin-left: 0;
    padding-top: 20px;
    padding-bottom: 20px;
    border-radius: 0;
    font-size: 13px;
    font-weight: bold;
    letter-spacing: 1.1px;
    text-align: center;
    color: #ffffff;
    &:hover {
      background-color: #3cbfd0 !important;
    }
  }
}
</style>
