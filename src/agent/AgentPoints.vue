<template>
  <div class="page-width-cal agent-point-page">
    <el-header class="fc-agent-main-header" height="80">
      <div class="flex-middle justify-content-space">
        <div class="fc-agent-black-26">{{ $t('agent.agent.points') }}</div>

        <div class="flex-middle flex-row">
          <transition name="fade">
            <el-button
              v-if="!isNiagaraAgent"
              class="fc-agent-add-btn mR20"
              @click="showAddPoint()"
            >
              <i class="el-icon-plus pR5 fwBold"></i>Add Points
            </el-button>
          </transition>
          <div class="agent-point-search flex-middle">
            <div
              class="row"
              style="margin-right: 20px"
              v-if="showQuickSearches"
            >
              <div class="fc-list-search">
                <div
                  class="fc-list-search-wrapper fc-list-search-wrapper-reading relative"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 32 32"
                    class="search-icon3"
                  >
                    <title>search</title>
                    <path
                      d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                    ></path>
                  </svg>
                  <input
                    ref="querySearch"
                    autofocus
                    type="text"
                    v-model="querySearch"
                    @keyup.enter="quickSearches()"
                    placeholder="Search"
                    class="quick-search-input6"
                  />
                  <svg
                    @click="closeSearches"
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 32 32"
                    class="close-icon6"
                    aria-hidden="true"
                  >
                    <title>close</title>
                    <path
                      d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                    ></path>
                  </svg>
                </div>
              </div>
            </div>
            <!-- <div
              class="pointer"
              @click="toggleQuickSearches"
              v-show="!showQuickSearches"
            >
              <span class="">
                <i
                  class="fa fa-search"
                  aria-hidden="true"
                  style="font-size: 14px;"
                ></i>
              </span>
            </div> -->
            <el-tooltip
              effect="dark"
              :content="$t('common._common.search')"
              placement="left"
            >
              <AdvancedSearch
                key="point-search"
                moduleName="points"
                moduleDisplayName="Point"
              >
              </AdvancedSearch>
            </el-tooltip>
            <span class="separator">|</span>

            <pagination
              :total="listCount"
              :perPage="perPage"
              ref="f-page"
            ></pagination>
            <span class="separator" v-if="listCount">|</span>
            <div
              class="flex-middle"
              @click="listRefresh"
              v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
              content="Refresh"
            >
              <i class="el-icon-refresh fwBold f16"></i>
            </div>
          </div>

          <div></div>
          <agent-point-form
            v-if="showDialog"
            :isNew="isNew"
            :visibility.sync="showDialog"
          ></agent-point-form>
        </div>
      </div>
    </el-header>
    <div>
      <FTags
        :key="moduleName"
        :moduleName="moduleName"
      >
      </FTags>
    </div>

    <div class="flex-middle mL15 justify-content-space mR20">
      <!-- agent filter -->
      <div class="flex-middle flex-no-wrap width100 position-relative pT10">
        <div>
          <SelectAgent @onAgentFilter="updateAgentId"></SelectAgent>
        </div>

        <div class="mL20">
          <div class="f12 fc-pink pB5 bold">Protocol</div>
          <el-select
            v-model="selectedType"
            filterable
            placeholder="Select Controller Type"
            class="fc-input-full-border-select2"
            @change="onTypeChange"
          >
            <template v-for="(label, value) in $constants.ControllerTypes">
              <el-option
                v-if="availableTypes.includes(parseInt(value))"
                :key="value"
                :label="label"
                :value="parseInt(value)"
              ></el-option>
            </template>
          </el-select>
        </div>
        <div class="mL20" v-if="lookupModuleName != null">
          <div class="f12 fc-pink pB5 bold">Controller</div>
          <FLookupFieldWrapper
            v-model="selectedControllerIds"
            :key="lookupModuleName"
            :field="{
              lookupModule: {
                name: lookupModuleName,
              },
              multiple: true,
            }"
            :filterConstruction="constructAgentFilter"
            :hideLookupIcon="false"
            :disabled="false"
            class="width250px"
            @recordSelected="onControllerDevice"
          ></FLookupFieldWrapper>
        </div>
        <div class="mL20 flex-middle pT20">
          <div class="cmd-btn">
            <div
              v-if="
                  this.agentPointsTab === 'unconfigured' &&
                    (agentType === 1 || agentType == 2 || agentType == 9)
                "
            >
              <el-dropdown
                :disabled="configureDisabled"
                split-button
                trigger="click"
                class="adv-configure"
                @click="configureInterval"
                @command="configureAll"
                :loading="configuring"
              >
              {{ configuring ? 'Configuring..' : "CONFIGURE" }}
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-tooltip
                      :disabled="showConfigureAllPointsTooltip"
                      content="Apply filter"
                      placement="bottom"
                    >
                      <span>
                        <el-dropdown-item
                        :disabled="!allowConfigureAllPoints"
                        command="all"
                        >
                          Configure All Points ({{ configurePointsCount }})
                        </el-dropdown-item>
                    </span>
                    </el-tooltip>
                  </el-dropdown-menu>
                </template>
            </el-dropdown>
            </div>
            <el-button
              :disabled="configureDisabled"
              type="button"
              class="fc-white-btn"
              v-if="
                this.agentPointsTab === 'configured' &&
                  !(
                    this.isNiagaraAgent === false &&
                    (this.selectedType == 5 || this.selectedType == 4)
                  )
              "
              @click="unConfigurePoint"
              :loading="unconfiguring"
              >{{
                unconfiguring ? 'Unconfiguring..' : 'UNCONFIGURE'
              }}</el-button
            >

            <el-button
              :disabled="configureDisabled"
              v-if="
                agentPointsTab === 'unconfigured' &&
                  (this.isNiagaraAgent === true ||
                    (this.selectedType != 3 &&
                      this.selectedType != 4 &&
                      this.selectedType != 5 &&
                      this.isNiagaraAgent === false))
              "
              type="button"
              class="fc-white-btn"
              @click="onSubscribeClicked"
              :loading="subscribing"
              >{{ subscribing ? 'Subscribing..' : 'SUBSCRIBE' }}
            </el-button>
            <el-button
              :disabled="configureDisabled"
              type="button"
              class="fc-white-btn"
              v-if="this.agentPointsTab === 'subscribed'"
              @click="unSubscribed"
              :loading="unsubscribing"
              >{{ unsubscribing ? 'Unsubscribing..' : 'UNSUBSCRIBE' }}
            </el-button>
          </div>
          <!-- <el-button type="button" class="fc-white-btn mL20" @click="addPoints"
              >Add Points</el-button
          >
          <el-tooltip
            content="Select atleast one controller"
            placement="bottom"
            :disabled="!$validation.isEmpty(selectedControllerIds)"
          >
            <div>
              <el-button
                :disabled="$validation.isEmpty(selectedControllerIds)"
                v-if="
                  selectedType != null &&
                    canDiscover(agentType, selectedType) &&
                    agentPointsTab == 'unconfigured'
                "
                type="button"
                class="fc-white-btn mL20"
                @click="discoverPoint"
                :loading="discovering"
                >{{ discovering ? 'Discovering...' : 'DISCOVER' }}
              </el-button>
            </div>
          </el-tooltip> -->

          <el-dropdown
            :disabled="$validation.isEmpty(points)"
            @command="exportPoints($event)"
            class="mL10 fc-btn-ico-lg pointer"
            style="padding: 2px 10px 3px;"
          >
            <span class="el-dropdown-link">
              <inline-svg
                src="svgs/new-download"
                iconClass="icon export-icon icon-sm-md"
              ></inline-svg>
            </span>
            <el-dropdown-menu slot="dropdown" class="controller-dropdown-item">
              <el-dropdown-item command="1">{{
                $t('common.wo_report.export_csv')
              }}</el-dropdown-item>
              <el-dropdown-item command="2">{{
                $t('common.wo_report.export_xcl')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <!-- agent filter -->
    </div>
    <!-- actions -->

    <div class="white-bg mL20 mR20 mT10">
      <el-tabs
        v-model="agentPointsTab"
        class="agent-tabs"
        @tab-click="tabSwitch()"
      >
        <el-tab-pane
          v-for="tab in tabs"
          :key="tab.key"
          :label="tab.label"
          :name="tab.key"
        >
          <div
            v-if="loading"
            class="flex-middle fc-empty-white m10 fc-agent-empty-state fc-agent-empty-state-points"
          >
            <spinner :show="loading" size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(points) && !loading"
            class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column fc-agent-table m10 fc-agent-empty-state fc-agent-empty-state-points"
          >
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="q-item-label nowo-label">
              {{ $t('agent.empty.no_points') }}
            </div>
          </div>
          <div
            class="fc-agent-table scrollbar-style fc-list-table-container"
            :class="pointsTableHeight()"
            v-if="!loading && !$validation.isEmpty(points)"
          >
            <el-table
              ref="multipleSelection"
              :data="points"
              height="auto"
              style="width: 100%"
              class="fc-list-view height100vh fc-table-td-height fc-table-viewchooser pB100 fc-table-th-pLalign-reduce"
              @selection-change="selectPoint"
            >
              <el-table-column
                type="selection"
                width="75"
                fixed="left"
              ></el-table-column>
              <el-table-column label="Name" width="280" fixed="left">
                <template v-slot="point">
                  <div
                    v-if="isNiagaraAgent && point.row.displayName"
                    class="bold"
                  >
                    {{ point.row.displayName }}
                  </div>
                  <div v-else class="bold">
                    {{ point.row.name }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="controller" width="200">
                <template v-slot="point">
                  {{ point.row.deviceName }}
                </template>
              </el-table-column>
              <el-table-column
                label="description"
                v-if="
                  selectedType === 1 &&
                    agentType === 1 &&
                    agentPointsTab != 'commissioned'
                "
                width="300"
              >
                <template v-slot="point">{{ point.row.description }}</template>
              </el-table-column>
              <el-table-column
                label="actual Unit"
                v-if="
                  selectedType === 1 &&
                    ( agentType === 1 || agentType === 2 ) &&
                    agentPointsTab != 'commissioned'
                "
                width="200"
              >
                <template v-slot="point">{{ point.row.actualUnit }}</template>
              </el-table-column>
              <el-table-column
                label="Link Name"
                width="200"
                v-if="agentType === 2"
              >
                <template v-slot="point">{{ point.row.name }}</template>
              </el-table-column>
              <el-table-column
                label="Instance"
                v-if="selectedType === 1"
                width="200"
              >
                <template v-slot="point">{{
                  point.row.instanceNumber
                }}</template>
              </el-table-column>
              <el-table-column
                label="register Type"
                v-if="selectedType === 4 || selectedType === 5"
                width="200"
              >
                <template v-slot="point">{{
                  $constants.AgentModbusRegisterType[point.row.registerType]
                }}</template>
              </el-table-column>
              <el-table-column
                label="register Number"
                v-if="selectedType === 4 || selectedType === 5"
                width="200"
              >
                <template v-slot="point">{{
                  point.row.registerNumber
                }}</template>
              </el-table-column>
              <el-table-column
                label="instance Type"
                v-if="selectedType === 1"
                width="200"
              >
                <template v-slot="point">{{
                  $constants.AgentInstancePoint[point.row.instanceType]
                }}</template>
              </el-table-column>
              <el-table-column
                label="Group Name"
                v-if="selectedType === 14"
                width="200"
              >
                <template v-slot="point">{{ point.row.groupName }}</template>
              </el-table-column>
              <el-table-column
                label="Prop Name"
                v-if="selectedType === 14"
                width="200"
              >
                <template v-slot="point">{{ point.row.propName }}</template>
              </el-table-column>
              <el-table-column
                label="path"
                v-if="selectedType === 6"
                width="250"
              >
                <template v-slot="point">{{ point.row.path }}</template>
              </el-table-column>
              <el-table-column
                label="Interval"
                v-if="
                  agentType === 1 &&
                    (agentPointsTab === 'configured' ||
                      agentPointsTab === 'commissioned')
                "
                width="200"
              >
                <template v-slot="point">
                  <el-tooltip
                    :content="selectedAgentInterval()"
                    placement="bottom"
                    :disabled="isAgentInterval(point.row)"
                  >
                    <span>
                      {{
                        point.row.interval > 0
                          ? `${point.row.interval} mins`
                          : 'Agent interval'
                      }}
                    </span>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column
                label="Category"
                v-if="agentPointsTab === 'commissioned'"
                width="200"
              >
                <template v-slot="point">
                  {{ getAssetCategory(point.row.categoryId).displayName }}
                </template>
              </el-table-column>
              <el-table-column
                label="Asset"
                v-if="agentPointsTab === 'commissioned'"
                width="200"
              >
                <template v-slot="point">
                  {{ getAsset(point) }}
                </template>
              </el-table-column>
              <el-table-column
                label="Reading"
                v-if="agentPointsTab === 'commissioned'"
                width="200"
              >
                <template v-slot="point">
                  {{ getReading(point) }}
                </template>
              </el-table-column>
              <el-table-column
                label="Unit"
                v-if="agentPointsTab === 'commissioned'"
                width="200"
              >
                <template v-slot="point">
                  {{ getUnit(point) }}
                </template>
              </el-table-column>
              <el-table-column label="created Time" width="200">
                <template v-slot="point">
                  {{ point.row.createdTime | formatDate(true) }}
                </template>
              </el-table-column>
              <el-table-column
                v-if="agentPointsTab === 'commissioned'"
                label="Mapped Time"
                width="200"
              >
                <template v-slot="point">
                  <span v-if="!$validation.isEmpty(point.row.mappedTime)">
                    {{ point.row.mappedTime | formatDate }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column
                label="Last Recorded Time"
                v-if="agentPointsTab != 'unconfigured'"
                width="200"
              >
                <template v-slot="point">
                  <el-tooltip
                    effect="dark"
                    :content="getLastRecordedTime(point)"
                    placement="bottom"
                  >
                    <span
                      v-if="!$validation.isEmpty(point.row.lastRecordedTime)"
                    >
                      {{ point.row.lastRecordedTime | fromNow }}
                    </span>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column
                label="Last Recorded value"
                width="200"
                v-if="agentPointsTab != 'unconfigured'"
              >
                <template v-slot="point">
                  {{ point.row.lastRecordedValue }}</template
                >
              </el-table-column>
              <el-table-column label="writable" width="200">
                <template v-slot="point">
                  <el-switch
                    v-model="point.row.writable"
                    :disabled="agentPointsTab == 'unconfigured'"
                    @change="toggleWritable(point.row, point.row.writable)"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#df5d5d"
                  ></el-switch>
                </template>
              </el-table-column>
              <el-table-column
                width="100"
                class="visibility-visible-actions"
                v-if="selectedType == 1 && (agentType == 1 || agentType == 2)"
              >
                <template v-slot="point">
                  <div class="visibility-hide-actions text-right mR30">
                    <el-dropdown @command="onOptionsSelect($event, point.row)">
                      <span class="el-dropdown-link">
                        <i class="el-icon-more controller-more"></i>
                      </span>
                      <el-dropdown-menu
                        slot="dropdown"
                        class="controller-dropdown-item"
                      >
                        <el-dropdown-item command="properties">{{
                          $t('agent.agent.properties')
                        }}</el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="help-icon-position pointer" @click="helpDialogOpen()">
      <inline-svg
        src="svgs/question"
        class="vertical-middle"
        iconClass="icon icon-xxlll"
      ></inline-svg>
    </div>
    <agent-help
      v-if="showHelpDialog"
      :visibility.sync="showHelpDialog"
    ></agent-help>
    <f-dialog
      v-if="showThresholdDialog"
      :visible.sync="showThresholdDialog"
      title="Set Threshold Values"
      width="40%"
      maxHeight="500px"
      @save="subscribed"
      customClass="threshold-value-dialog"
      confirmTitle="Subscribe"
      :stayOnSave="true"
    >
      <div class="pT10 pB10">
        <el-row :gutter="10" class="pB20 threshold-value-item">
          <el-col :span="10">Name</el-col>
          <!-- <el-col :span="6">Operator</el-col> -->
          <el-col :span="10">Threshold</el-col>
          <!-- <el-col :span="6">Step Count</el-col> -->
        </el-row>
        <el-row
          :gutter="10"
          class="pB20 threshold-value-item"
          v-for="instance in selectedInstances.filter(
            inst => (inst.dataType != 4 && inst.dataType != 8)
          )"
          :key="instance.id"
        >
          <el-col :span="10">{{ instance.name }}</el-col>
          <!-- <el-col :span="6">
            <el-select
              v-model="instance.operatorValue"
              style="width: 130px;"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-col> -->
          <el-col :span="10">
            <el-input
              type="number"
              v-model="instance.threshold"
              class="el-input-textbox-full-border"
              :min="5"
            >
              <template slot="append">(%)</template>
            </el-input>
          </el-col>
          <!-- <el-col :span="6" v-else>
            <el-input
              type="number"
              v-model="instance.threshold"
              class="el-input-textbox-full-border"
              :min="5"
            >
            </el-input>
          </el-col> -->
          <!-- <el-col :span="6">
            <el-input
              type="number"
              v-model="instance.stepCountVal"
              class="el-input-textbox-full-border"
              :min="5"
            ></el-input>
          </el-col> -->
        </el-row>
      </div>
    </f-dialog>

    <div>
      <el-dialog
        :title="$t('agent.agent.set_interval')"
        :visible.sync="intervalDialog"
        width="30%"
        :append-to-body="true"
        :before-close="closeIntervalDialog"
        class="fc-dialog-center-container"
        style="z-index: 999999"
      >
        <div class="height150">
          <el-select
            v-model="interval"
            placeholder="Select"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="interval in intervalTiming"
              :key="interval.value"
              :label="interval.label"
              :value="interval.value"
            >
            </el-option>
          </el-select>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-cancel f13"
            @click="closeIntervalDialog"
            >{{ $t('agent.agent.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save f13"
            @click="configurePoints(interval)"
            >{{ $t('agent.agent.set_interval') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <div>
      <el-dialog
        title="Properties"
        :visible.sync="showProperties"
        width="40%"
        :append-to-body="true"
        style="z-index: 9999999999;"
        class="agents-dialog fc-dialog-center-container"
      >
        <div class="label-txt-black line-height24">
          <div v-if="!$validation.isEmpty(pointProps)">
            <div class="fc-json-view bold ">States :</div>
            <div
              class="fc-json-view mL50"
              v-for="(label, value) in pointProps"
              :key="value"
            >
              {{ label }}
            </div>
          </div>
          <div v-else>
            <div class="bold ">No Properties</div>
          </div>
        </div>
      </el-dialog>
    </div>
    <iframe v-if="exportUrl" :src="exportUrl" style="display: none;"></iframe>
  </div>
</template>
<script>
import FDialog from '@/FDialogNew'
import Pagination from 'src/components/list/FPagination'
import AgentHelp from 'agent/AgentHelperDocs/HelpPoints'
import AgentPointForm from 'agent/components/AgentAddPointForm'
import ControllerFilter from 'pages/setup/agents/ControllerFilter'
import agentmixin from '@/mixins/AgentMixin'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import { mapGetters } from 'vuex'
import { isEmpty, isNull } from '@facilio/utils/validation'
import { formatDate, fromNow } from 'src/util/filters'
import { isNullOrUndefined } from '@facilio/utils/validation'
import SelectAgent from 'src/agent/components/SelectAgent.vue'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import Constants from 'util/constant'
import { API } from '@facilio/api'

export default {
  title() {
    return 'Agent Points'
  },
  data() {
    return {
      points: [],
      loading: false,
      agentPointsTab: null,
      selectedAgent: null,
      resourceMap: [],
      fieldMap: [],
      unitMap: [],
      tabs: [
        {
          key: 'unconfigured',
          label: 'Unconfigured',
        },
        {
          key: 'configured',
          label: 'Configured',
        },
        {
          key: 'commissioned',
          label: 'Commissioned',
        },
        {
          key: 'subscribed',
          label: 'Subscribed',
        },
      ],
      listCount: null,
      agent: null,
      selectedAgentId: null,
      selectedType: null,
      controllers: [],
      controller: null,
      selectedControllerIds: [],
      selectedPoint: [],
      realPoints: [],
      showHelpDialog: false,
      selectedPoints: [],
      allPointsSelected: false,
      perPage: 50,
      showDialog: false,
      isNew: true,
      pointDisabled: false,
      querySearch: null,
      showThresholdDialog: false,
      logical: false,
      discovering: false,
      configuring: false,
      unconfiguring: false,
      subscribing: false,
      unsubscribing: false,
      options: [
        {
          value: 5,
          label: 'Greater than',
        },
        {
          value: 6,
          label: 'Less than',
        },
        {
          value: 7,
          label: 'Greater than or equals',
        },
        {
          value: 8,
          label: 'Less than or equals',
        },
        {
          value: 9,
          label: 'Equals',
        },
        {
          value: 10,
          label: 'Precentage',
        },
      ],
      intervalTiming: [
        {
          value: 0,
          label: 'Agent interval',
        },
        {
          value: 2,
          label: '2 Mins',
        },
        {
          value: 5,
          label: '5 Mins',
        },
        {
          value: 10,
          label: '10 Mins',
        },
        {
          value: 15,
          label: '15 Mins',
        },
      ],
      interval: 0,
      configureWithCriteria : false,
      configureWithControllerIds: false,
      value: -1,
      canSubscribe: false,
      showQuickSearches: false,
      isNiagaraAgent: false,
      intervalDialog: false,
      moduleName: 'points',
      lookupModuleName: null,
      exportUrl: null,
      showProperties: false,
      pointProps: {},
    }
  },
  mixins: [agentmixin],
  computed: {
    ...mapGetters(['getAssetCategory']),

    configurePointsCount() {
      return  Math.min(this.listCount, 5000);
    },
    availableTypes() {
      return (
        this.controllers &&
        this.controllers.map(controller => controller.controllerType)
      )
    },
    filterControllers() {
      return (
        this.controllers &&
        this.controllers.filter(con => con.controllerType === this.selectedType)
      )
    },
    page() {
      return this.$route.query.page || 1
    },
    selectedInstances() {
      if (this.points && this.selectedPoints) {
        return this.points.filter(inst => this.selectedPoints.includes(inst.id))
      }
      return []
    },
    showConfigureAllPointsTooltip(){
      if(this.selectedPoints.length == this.points.length){
        return this.allowConfigureAllPoints
      }
      return true
    },
    allowConfigureAllPoints(){
      if(this.selectedPoints.length == this.points.length){
        if(!isNullOrUndefined(this.filters)){
          return true
        }
        /* else if(!isEmpty(this.selectedControllerIds) && (this.agentType == 2 || (this.agentType == 1 && this.selectedType == 1))){
          return true
        } */
      }
      return false
    },
    configureDisabled() {
      if (!this.selectedPoints.length) {
        return true
      }
      return false
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
  },
  mounted() {
    this.setAgentPointsTab()
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  watch: {
    page(val) {
      if (val != 1) {
        this.getPoints(true)
      } else {
        this.getPoints()
      }
    },
    filters: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.getPoints()
        }
      },
    },
    selectedControllerId() {
      let query = this.$route.query
      let { controllerId } = this.$route.query
      if (controllerId) {
        this.$router.replace({
          query: {
            ...query,
            controllerId: this.selectedControllerId,
          },
        })
      }
    },
  },
  components: {
    AgentHelp,
    Pagination,
    AgentPointForm,
    FDialog,
    AdvancedSearch,
    FTags,
    SelectAgent,
    FLookupFieldWrapper,
  },
  methods: {
    getConfigureAllPointsContent(){
      if(this.agentType!=null && ((this.agentType == 1 && this.selectedType == 1) || this.agentType == 2)){
        return "Select at least one controller or apply filter"
      }
      return "Apply filter"
    },
    onOptionsSelect(command, point) {
      switch (command) {
        case 'properties':
          this.showPointProperties(point)
          break
      }
    },
    async showPointProperties(point) {
      let { id } = point
      let url = `/v2/commissioning/inputValues?pointId=${id}`
      let { data } = await API.get(url)
      let { inputValues } = data
      if (!isEmpty(inputValues)) {
        let listOfLabels = []
        inputValues.forEach(item => {
          if(!isEmpty(item.inputLabel)){
            let value = item.inputLabel
            listOfLabels.push(value)
          }
        })
        this.pointProps = listOfLabels
      } else {
        this.pointProps = {}
      }
      this.showProperties = true
    },
    getDisplayName(displayName) {
      if (displayName.length) {
        let val = displayName.substr(displayName.lastIndexOf('/') + 1)
        if (val.length) {
          return val
        }
      }
      return null
    },
    selectedAgentInterval() {
      return this.selectedAgent.interval + ' mins'
    },
    isAgentInterval(value) {
      let { interval } = value
      return !isEmpty(interval)
    },
    /* onOptionsSelect(command, point) {
       if (command === 'set') {
         this.pointSetValue(point.row)
       }
     }, */
    helpDialogOpen() {
      this.showHelpDialog = true
    },
    selectPoint(selectedPo) {
      this.selectedPoints = selectedPo.map(value => value.id)
    },
    unConfigurePoint() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      this.loading = true
      this.unconfiguring = true
      let params = {
        recordIds: this.selectedPoints,
        controllerType: this.selectedType,
      }
      this.$http.post('/v2/point/unconfigure', params).then(response => {
        if (response.data.responseCode === 200) {
          let self = this
          self.$message.info('Points will be unconfigured in a while.')
          self.loading = false
          self.unconfiguring = false
          self.getPoints()
        } else {
          this.unconfiguring = false
          this.loading = false
          this.$message.error(response.data.message)
        }
      })
      this.selectedPoints = []
    },
    showAddPoint() {
      this.showDialog = true
    },
    onSubscribeClicked() {
      let booleanAndEnumInstances = this.selectedInstances.filter(
        inst => ( inst.dataType == 4 || inst.dataType == 8)
      )
      if (booleanAndEnumInstances.length == this.selectedInstances.length) {
        this.subscribed()
      } else {
        this.showThresholdDialog = true
      }
    },
    subscribed() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      if (!this.validateThreshold()) {
        return
      }
      if (!this.validateLogicalPoint()) {
        return
      }
      this.showThresholdDialog = false
      this.subscribing = true
      this.loading = true
      let instances = this.selectedInstances.map(instance => {
        let inst = {
          id: instance.id,
        }
        if (instance.threshold) {
          inst.thresholdJson = JSON.stringify({
            operator: 10,
            threshold: instance.threshold,
            stepCount: instance.stepCountVal,
          })
        }
        return inst
      })
      this.$http
        .post('/v2/point/subscribe', {
          instances: instances,
          controllerType: this.selectedType,
        })
        .then(response => {
          if (response.data.responseCode === 200) {
            let self = this
            self.$message.info('Points will be subscribed in a while.')
            self.loading = false
            self.subscribing = false
            self.getPoints()
          } else {
            this.subscribing = false
            this.loading = false
            this.$message.error(response.data.message)
          }
        })
      this.selectedPoints = []
    },
    validateThreshold() {
      let lessThreshold = this.selectedInstances.some(
        inst => inst.threshold < 5
      )
      if (lessThreshold) {
        this.$message.error(
          'The difference should be greater than or equal to 5 percentage'
        )
        return false
      }
      return true
    },
    validateLogicalPoint() {
      if (this.selectedInstances[0].logical == true) {
        let validate = this.selectedInstances.every(
          inst =>
            inst.controllerId === this.selectedInstances[0].controllerId &&
            inst.logical === this.selectedInstances[0].logical
        )
        if (validate) {
          this.logical = true
          return true
        } else {
          this.$message.error('Please select same Controller Points.')
          this.logical = false
          return false
        }
      } else {
        return true
      }
    },
    unSubscribed() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      if (!this.validateLogicalPoint()) {
        return
      }
      this.loading = true
      this.unsubscribing = true
      let instances = this.selectedInstances.map(instance => {
        let inst = {
          id: instance.id,
        }
        if (instance.threshold) {
          inst.thresholdJson = JSON.stringify({
            operator: instance.operatorValue,
            threshold: instance.threshold,
            stepCount: instance.stepCountVal,
          })
        }
        return inst
      })
      this.$http
        .post('/v2/point/unsubscribe', {
          instances: instances,
          controllerType: this.selectedType,
        })
        .then(response => {
          if (response.data.responseCode === 200) {
            let self = this
            self.$message.info('Points will be unsubscribed in a while.')
            self.loading = false
            self.unsubscribing = false
            self.getPoints()
          } else {
            this.$message.error(response.data.message)
            this.loading = false
            this.unsubscribing = false
          }
        })
      this.selectedPoints = []
    },
    discoverPoint() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      this.loading = true
      this.discovering = true
      let params = {
        recordIds: this.selectedControllerIds,
      }
      this.$http.post('/v2/device/discoverPoints', params).then(response => {
        if (response.data.responseCode === 200) {
          let self = this
          self.$message.info('Points will be discovered in a while.')
          self.loading = false
          self.discovering = false
          self.getPoints()
        } else {
          this.loading = false
          this.discovering = false
          this.$message.error(response.data.message)
        }
      })
    },
    configureInterval() {
      if (this.agentType === 1) {
        // Temp. Will be supported for all
        this.intervalDialog = true
      } else {
        this.configurePoints()
      }
    },
    configureAll(command){
      if(command == "all"){
        if(!isNullOrUndefined(this.filters)){
          this.configureWithCriteria = true
          this.configureInterval()
        }
        /* else {
          this.configureWithControllerIds = true
          this.configureInterval()
        } */
      }
    },
    configurePointsWithControllerIds(){
      let params = {
        recordIds: this.selectedControllerIds,
        controllerType: this.selectedType,
        agentId: this.selectedAgentId
      }
      if (this.interval > 0) {
        params.interval = this.interval
      }
      this.$http.post('/v2/point/configureAll', params).then(response => {
        if (response.data.responseCode === 200) {
          let self = this
          self.$message.info('Points will be configured in a while.')
          self.loading = false
          self.configuring = false
          this.intervalDialog = false
          self.getPoints()
        } else {
          this.loading = false
          this.configuring = false
          this.$message.error(response.data.message)
          this.intervalDialog = false
        }
      })
      this.configureWithControllerIds = false
      this.selectedPoints = []
    },
    configurePointsUsingFilterOrPointIds(){
      if (!this.validateLogicalPoint()) {
        return
      }
      let params = {
        recordIds: this.selectedPoints,
        controllerType: this.selectedType,
        logical: this.logical,
        agentId: this.selectedAgentId,
        controllerIds: this.selectedControllerIds,
      }
      if (this.interval > 0) {
        params.interval = this.interval
      }
      if(this.configureWithCriteria){
        params.filters = JSON.stringify(this.filters)
        params.recordIds = []
      }

      this.$http.post('/v2/point/configure', params).then(response => {
        if (response.data.responseCode === 200) {
          let self = this
          self.$message.info('Points will be configured in a while.')
          self.loading = false
          self.configuring = false
          this.intervalDialog = false
          self.getPoints()
        } else {
          this.loading = false
          this.configuring = false
          this.$message.error(response.data.message)
          this.intervalDialog = false
        }
      })
      this.selectedPoints = []
      this.configureWithCriteria = false
    },
    configurePoints() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      this.loading = true
      this.configuring = true

      /* if(this.configureWithControllerIds){
         this.configurePointsWithControllerIds()
         return;
      } */
      this.configurePointsUsingFilterOrPointIds()
    },
    toggleWritable(point) {
      if (!point.agentWritable) {
        this.$message.error('Selected point is Read only')
        return
      }
      let { id, writable: isActive } = point
      let url = isActive
        ? '/v2/point/makeWritable'
        : '/v2/point/disableWritable'
      return this.$http
        .post(url, {
          recordIds: [id],
        })
        .then(({ data }) => {
          if (data.responseCode === 200) {
            this.$message.success(
              isActive
                ? 'Point marked as writable'
                : 'Point marked as not writable'
            )
          } else {
            throw new Error()
          }
        })
        .catch(() => {
          this.$set(point, 'active', !isActive)
          this.$message('Could not update settings')
        })
    },
    async getPoints(loadMore) {
      this.lookupModuleName = Constants.ControllerModuleName[this.selectedType]
      if (this.selectedType == null) {
        return
      }
      let url = '/v3/point/list'
      let params = this.getPointsUrl()
      if (!loadMore) {
        this.loadCount()
      }
      this.loading = true
      let response = await API.post(url, params)
      let { error, data } = response
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.points = data.data ? data.data : []
        this.resourceMap = data.resourceMap ? data.resourceMap : []
        this.fieldMap = data.fieldMap ? data.fieldMap : []
        this.unitMap = data.unitMap ? data.unitMap : []
        this.loading = false
      }
    },
    async loadCount() {
      let url = '/v3/point/count'
      let params = this.getPointsUrl(true)
      let { error, data } = await API.post(url, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.listCount = data.count || 0
      }
    },
    getPointsUrl(isCount) {
      let status
      switch (this.agentPointsTab) {
        case 'unconfigured':
          status = 'UNCONFIGURED'
          break
        case 'configured':
          status = 'CONFIGURED'
          break
        case 'commissioned':
          status = 'COMMISSIONED'
          break
        case 'subscribed':
          status = 'SUBSCRIBED'
          break
      }
      let queryObj = {
        controllerType: this.selectedType,
        status: status,
        agentId: this.selectedAgentId,
      }
      if (!isEmpty(this.selectedControllerIds)) {
        queryObj.controllerIds = this.selectedControllerIds
      }
      if (!isCount) {
        queryObj['page'] = this.page
        queryObj['perPage'] = this.perPage
      }
      let { filters } = this
      if (!isEmpty(filters)) queryObj['filters'] = JSON.stringify(filters)
      return queryObj
    },
    selectAgent(selectedSh) {
      this.selectedPoint = selectedSh.map(value => value.id)
    },

    loadControllers() {
      this.isConnected = this.selectedAgent.connected
      this.setLoading(true)
      this.setResetFields()
      this.$http
        .get(`/v2/controller/getFilter?agentId=${this.selectedAgentId}`)
        .then(response => {
          this.controllers = response.data.result.data
            ? response.data.result.data.filter(c => c.controllerType > -1)
            : []
          if (this.controllers.length) {
            this.controllers = response.data.result.data
            this.selectedType = isNullOrUndefined(
              this.$route.query.controllerType
            )
              ? this.controllers[0].controllerType
              : parseInt(this.$route.query.controllerType)

            let id = this.$route.query.controllerId
            if (!isEmpty(id) && id != null) {
              this.selectedControllerIds.push(id)
              const query = Object.assign({}, this.$route.query)
              delete query.controllerId
              delete query.controllerType
              this.$router.replace({ query })
            }
            this.getPoints()
          }
          this.setLoading(false)
        })
        .catch(() => {
          this.setLoading(false)
          this.controllers = []
        })
    },
    setControllers(controllers) {
      this.controllers = controllers
    },
    onControllerDevice(controllers) {
      if (!isEmpty(this.selectedPoints)) {
        this.selectedPoints = []
      }

      this.selectedControllerIds = controllers.map(
        controller => controller.value
      )
      let page = this.$route.query.page
      if (page) {
        const query = Object.assign({}, this.$route.query)
        delete query.page
        this.$router.replace({ query })
      }
      this.getPoints()
    },
    setLoading(isLoading) {
      this.$emit('update:loading', isLoading)
    },
    onTypeChange() {
      this.selectedPoints = []
      if (this.filterControllers.length) {
        this.selectedControllerIds = []
        let page = this.$route.query.page
        const query = Object.assign({}, this.$route.query)
        if (page) delete query.page
        query.controllerType = this.selectedType
        this.$router.replace({ query })
        this.getPoints()
      }
    },
    listRefresh() {
      this.getPoints()
    },
    quickSearches() {
      let string = this.querySearch
      if (string) {
        this.getPoints(true)
        this.loadCount()
      }
    },
    toggleQuickSearches() {
      this.showQuickSearches = !this.showQuickSearches
      if (this.showQuickSearches) {
        this.$nextTick(() => {
          this.$refs.querySearch.focus()
        })
      }
    },
    closeSearches() {
      this.toggleQuickSearches()
      this.querySearch = null
      this.getPoints(true)
      this.loadCount()
    },
    setResetFields() {
      ;(this.selectedType = null),
        (this.selectedControllerIds = []),
        (this.points = []),
        (this.listCount = 0)
    },
    closeIntervalDialog() {
      this.intervalDialog = false
    },
    getAsset(point) {
      let resourceId = this.$getProperty(point, 'row.resourceId')
      let assetname = this.resourceMap[resourceId]
      return assetname
    },
    getReading(point) {
      let fieldId = this.$getProperty(point, 'row.fieldId')
      let reading = this.fieldMap[fieldId].name
      //let name = reading.name
      return reading
    },
    getUnit(point) {
      let unitId = this.$getProperty(point, 'row.unit')
      let unit = this.unitMap[unitId]
      return unit
    },
    getLastRecordedTime(point) {
      let lastRecordedTime = this.$getProperty(point, 'row.lastRecordedTime')
      this.lastRecordedTime = formatDate(lastRecordedTime)
      return this.lastRecordedTime
    },
    setAgentPointsTab() {
      let agentPointsTab = this.$getProperty(
        this.$route.query,
        'agentPointsTab'
      )
      if (!isEmpty(agentPointsTab)) {
        this.agentPointsTab = agentPointsTab
      } else {
        this.agentPointsTab = 'unconfigured'
      }
    },
    updateAgentId(selectedAgent) {
      this.selectedAgent = selectedAgent
      this.selectedAgentId = selectedAgent.id
      this.agentType = selectedAgent.agentType
      this.isNiagaraAgent = selectedAgent.agentType === 2 ? true : false
      this.lookupModuleName = null

      let page = this.$route.query.page
      let type = this.$route.query.controllerType
      const query = Object.assign({}, this.$route.query)
      if (page) delete query.page
      if (type) delete query.controllerType
      query.agentId = this.selectedAgentId
      this.$router.replace({ query })
      this.selectedPoints = []
      this.loadControllers()
    },
    constructAgentFilter() {
      return { agentId: { operatorId: 9, value: [`${this.selectedAgentId}`] } }
    },
    tabSwitch() {
      if (!isEmpty(this.selectedPoints)) {
        this.selectedPoints = []
      }
      let page = this.$route.query.page
      const query = Object.assign({}, this.$route.query)
      if (page) delete query.page
      query.agentPointsTab = this.agentPointsTab
      this.$router.replace({ query })
      this.getPoints()
    },
    pointsTableHeight() {
      let { filters } = this
      return isNull(filters) ? 'agent-list-scroll' : 'filter-applied'
    },
    async exportPoints(command) {
      let url = '/v3/point/export'
      let params = this.getPointsUrl()
      params['type'] = command

      this.$message.success({
        message: 'Downloading',
        type: 'success',
        showClose: true,
      })
      let { data, error } = await API.post(url, params)
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.exportUrl = data.fileUrl
      }
    },
  },
}
</script>
<style lang="scss">
.agent-point-page {
  .agent-list-scroll .el-table {
    height: calc(100vh - 285px) !important;
    padding-bottom: 100px;
  }
  .filter-applied .el-table {
    height: calc(100vh - 370px) !important;
    padding-bottom: 100px;
  }

  .agent-point-search {
    .el-icon-search {
      font-weight: bold;
    }
  }

  .commissioning-search-pagination .search-show-hide .fa-search {
    position: relative;
    top: 0px;
    right: 0px;
  }

  .cmd-btn{
    gap: 10px;
    display: flex;
  }

  .adv-configure {
    .el-button{
      color: #324056;
      letter-spacing: 1px;
      line-height: normal;
      font-weight: 500;
      font-size: 12px;
      &:hover,
      &:focus {
        background-color: #2e5bff;
        color: #fff;
        transition: 0.2s all;
      }
      &:disabled{
        color: #C0C4CC;
        &:hover,
        &:focus{
          background-color: #fff;
          color: #C0C4CC;
        }
      }
    }

  }
}
</style>
