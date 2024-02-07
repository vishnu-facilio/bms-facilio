<template>
  <div class="width100">
    <div class="page-width-cal agent-table-scroll-tab">
      <el-header class="fc-agent-main-header" height="80">
        <div class="flex-middle justify-content-space">
          <div class="fc-agent-black-26">Controllers</div>
          <new-add-Controller
            v-if="showDialog"
            :model="device"
            isNew="isNew"
            :visibility.sync="showDialog"
            @saved="onControllerSaved"
          ></new-add-Controller>
          <el-button
            class="fc-agent-add-btn"
            v-if="canAddController()"
            @click="showController()"
          >
            <i class="el-icon-plus pR5 fwBold"></i>
            Add Controller
          </el-button>
        </div>
      </el-header>
      <!-- select -->
      <div class="flex-middle mL20 justify-content-space pT10 mR20">
        <div class="flex-middle">
          <div>
            <SelectAgent @onAgentFilter="updateAgentId"></SelectAgent>
          </div>
          <div class="mL20">
            <div class="f12 fc-pink bold pB5">Protocol</div>
            <el-select
              v-model="selectedType"
              filterable
              placeholder="Select Type"
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
          <div class="adv-discover mL20">
            <el-button
              v-if="canDiscover(agentType, selectedType)"
              class="fc-white-btn-b"
              @click="discoverOrOpenDialog(false)"
              :loading="discovering"
              >{{
                discovering ? 'Discovering..' : 'DISCOVER CONTROLLER'
              }}</el-button
            >
            <div
              v-if="supportAdvancedDiscover(agentType, selectedType)"
              class="separator"
            ></div>
            <el-button
              v-if="supportAdvancedDiscover(agentType, selectedType)"
              class="fc-white-btn-b mL0 small-btn"
              @click="discoverOrOpenDialog(true)"
            >
              <inline-svg
                src="lookup"
                iconClass="icon icon-sm-md fc-lookup-icon"
              ></inline-svg>
            </el-button>
          </div>
          <div class="adv-discover mL20">
            <el-button
            class="fc-white-btn-b"
            :disabled="$validation.isEmpty(selectedControllerReset)"
            @click="discoverPoints()"
            :loading="discoveringPoints"
            >{{
              discoveringPoints ? 'Discovering..' : 'DISCOVER POINTS'
            }}</el-button>
            <div
              v-if="supportsAdvanceDiscoverPoints(agentType, selectedType)"
              class="separator"
            ></div>
            <el-button
              v-if="supportsAdvanceDiscoverPoints(agentType, selectedType)"
              class="fc-white-btn-b mL0 small-btn"
              :disabled="$validation.isEmpty(selectedControllerReset)"
              @click="advanceDiscoverPoints()"
            >
              <inline-svg
                src="lookup"
                iconClass="icon icon-sm-md fc-lookup-icon"
              ></inline-svg>
            </el-button>
          </div>
          <!-- Configure All Points -->
          <!-- <div class="configure-btn mL10" v-if="!$validation.isEmpty(this.selectedControllerReset) && supportsConfigureAllPoints(agentType, selectedType)">
            <el-button
              class="fc-white-btn"
              @click="configureInterval()"
              :loading="configuringAllPoints"
              >{{
                configuringAllPoints ? 'Configuring All Points..' : 'CONFIGURE ALL POINTS'
              }}</el-button
            >
          </div> -->
        </div>
        <div class="flex-middle pT10">
          <div class="row" style="margin-right: 20px" v-if="showQuickSearches">
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
          <div
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
          </div>
          <span class="separator">|</span>
          <pagination :total="listCount" :perPage="perPage"></pagination>
          <span
            class="pointer fwBold pR10 f16"
            @click="listRefresh"
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            content="Refresh"
          >
            <i class="el-icon-refresh fwBold f16"></i>
          </span>
        </div>
      </div>
      <!-- tab -->
      <div class="white-bg m20">
        <div>
          <div
            v-if="loading"
            class="height-calc200 flex-middle fc-empty-white m10 fc-agent-empty-state"
          >
            <spinner :show="loading" size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(realControllers) && !loading"
            class="fc-empty-white height-calc200 flex-middle justify-content-center flex-direction-column fc-agent-table m10 fc-agent-empty-state"
          >
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="q-item-label nowo-label">
              No Controller available
            </div>
          </div>

          <div
            class="fc-agent-table agent-list-scroll fc-list-table-container"
            v-if="!loading && !$validation.isEmpty(realControllers)"
          >
            <el-table
              ref="multipleSelection"
              :data="realControllers"
              height="auto"
              @selection-change="selectControllerReset"
              style="width: 100%"
              class="fc-list-view pT0 height100vh fc-table-td-height fc-table-viewchooser pB100 fc-table-th-minus"
            >
              <el-table-column
                type="selection"
                width="75"
                fixed="left"
                v-model="selectall"
                v-if="canDiscover(agentType, selectedType)"
              ></el-table-column>
              <el-table-column label="NAME" width="300" fixed="left">
                <template v-slot="controller">
                  <div class="controller-active-section flex-middle">
                    <div
                      :title="controller.row.active ? 'Active' : 'Inactive'"
                      v-tippy
                      :class="[
                        'dot-10',
                        !isConnected
                          ? 'color-inactive'
                          : controller.row.active
                          ? 'color-active'
                          : 'color-inactive',
                      ]"
                    ></div>
                    <div class="label-txt3-14 mL10 bold">
                      {{ controller.row.name ? controller.row.name : '---' }}
                    </div>
                    <!-- <div class="fc-badge mL10 inline middle nowrap">
                  {{
                    $constants.ControllerTypes[controller.row.controllerType]
                      ? $constants.ControllerTypes[
                          controller.row.controllerType
                        ]
                      : '---'
                  }}
                </div>-->
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                label="Created Time"
                width="160"
                sortable
                prop="controller"
              >
                <template v-slot="controller">
                  <div v-if="controller.row.createdTime">
                    {{ controller.row.createdTime | formatDate(true) }}
                  </div>
                  <div v-else>---</div>
                </template>
              </el-table-column>
              <el-table-column
                label="URL"
                width="250"
                v-if="selectedType === 6 || selectedType === 7"
              >
                <template v-slot="controller">
                  {{ controller.row.url ? controller.row.url : '---' }}
                </template>
              </el-table-column>
              <el-table-column
                label="Security Mode"
                width="150"
                v-if="selectedType === 7"
              >
                <template v-slot="controller">
                  {{ $constants.SecurityMode[controller.row.securityMode] }}
                </template>
              </el-table-column>
              <el-table-column
                label="Security Policy"
                width="150"
                v-if="selectedType === 7"
              >
                <template v-slot="controller">
                  {{ $constants.SecurityPolicy[controller.row.securityPolicy] }}
                </template>
              </el-table-column>
              <el-table-column
                label="Ip Address"
                width="200"
                v-if="
                  selectedType === 1 ||
                    selectedType === 4 ||
                    selectedType === 3 ||
                    selectedType === 14
                "
              >
                <template v-slot="controller">
                  {{
                    controller.row.ipAddress ? controller.row.ipAddress : '---'
                  }}
                </template>
              </el-table-column>
              <el-table-column
                label="Instance"
                width="120"
                v-if="selectedType === 1"
              >
                <template v-slot="controller">
                  {{
                    controller.row.instanceNumber
                      ? controller.row.instanceNumber
                      : '---'
                  }}
                </template>
              </el-table-column>
              <el-table-column
                label="Network Number"
                width="200"
                v-if="selectedType === 1"
              >
                <template v-slot="controller">
                  {{
                    controller.row.networkNumber
                  }}
                </template>
              </el-table-column>
              <el-table-column
                label="Slave Id"
                width="150"
                v-if="selectedType === 4 || selectedType === 5"
              >
                <template v-slot="controller">{{
                  controller.row.slaveId ? controller.row.slaveId : '---'
                }}</template>
              </el-table-column>
              <el-table-column
                label="Port Name"
                width="150"
                v-if="selectedType === 5"
              >
                <template v-slot="controller">{{
                  controller.row.comPort ? controller.row.comPort : '---'
                }}</template>
              </el-table-column>
              <el-table-column
                label="Port"
                width="100"
                v-if="selectedType === 4 || selectedType === 14"
              >
                <template v-slot="controller">{{
                  controller.row.port ? controller.row.port : '---'
                }}</template>
              </el-table-column>
              <el-table-column
                label="Device Id"
                width="120"
                v-if="selectedType === 14"
              >
                <template v-slot="controller">{{
                  controller.row.deviceId ? controller.row.deviceId : '---'
                }}</template>
              </el-table-column>
              <el-table-column label="Configured/Total Points" width="250">
                <template v-slot="controller">
                  <span
                    :class="{ 'main-field-column': controller.row.configured }"
                    @click="
                      controller.row.configured
                        ? redirectoPointsPage(controller.row, 'configured')
                        : null
                    "
                  >
                    {{
                      controller.row.configured
                        ? controller.row.configured
                        : '---'
                    }}
                    /
                    {{
                      controller.row.points
                        ? controller.row.availablePoints ||
                          controller.row.points
                        : '---'
                    }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column
                label="Subscribed"
                width="160"
                v-if="selectedType === 1 || selectedType === 3"
              >
                <template v-slot="controller">
                  <span
                    :class="{ 'main-field-column': controller.row.subscribed }"
                    @click="
                      controller.row.subscribed
                        ? redirectoPointsPage(controller.row, 'subscribed')
                        : null
                    "
                  >
                    {{
                      controller.row.subscribed
                        ? controller.row.subscribed
                        : '---'
                    }}
                  </span>
                </template>
              </el-table-column>

              <el-table-column label width="100">
                <template v-slot="controller">
                  <el-dropdown
                    @command="onOptionsSelect($event, controller.row)"
                  >
                    <span class="el-dropdown-link">
                      <i
                        class="el-icon-more controller-more visibility-hide-actions"
                      ></i>
                    </span>
                    <el-dropdown-menu
                      slot="dropdown"
                      class="controller-dropdown-item"
                    >
                      <el-dropdown-item command="reset">Reset</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
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
    <el-dialog
      :title="$t('agent.agent.discover_controllers')"
      :visible.sync="discoverControllerDialog"
      width="30%"
      style="z-index: 999999"
      :append-to-body="true"
      :before-close="closeDialogAndResetValues"
      class="fc-dialog-center-container"
    >
      <div :class="discoverControllersDialogHeight">
        <el-form>
          <el-form-item label="Agent" :required="true">
            <el-select
              disabled
              v-model="selectedAgent.displayName"
              class="fc-input-full-border-select2  width100"
            >
              <el-option
                v-for="(agent, index) in selectedAgent"
                :key="index"
                :label="agent.displayName"
                :value="agent.id"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="Protocol" :required="true">
            <el-select
              :required="true"
              v-model="selectedType"
              class="fc-input-full-border-select2  width100"
            >
              <div
                v-if="
                  selectedAgentId != null &&
                    selectedAgentId > 0 &&
                    agentType == 1
                "
              >
                <el-option
                  v-for="item in facilioJavaOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </div>
              <div
                v-else-if="
                  selectedAgentId != null &&
                    selectedAgentId > 0 &&
                    agentType == 2
                "
              >
                <el-option
                  v-for="item in niagaraOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </div>
              <div
                v-else-if="
                  selectedAgentId != null &&
                    selectedAgentId > 0 &&
                    agentType == 9
                "
              >
                <el-option
                  v-for="item in e2Options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </div>
              <div
                v-else-if="
                  selectedAgentId != null &&
                    selectedAgentId > 0 &&
                    agentType == 6
                "
              >
                <el-option
                  v-for="item in rdmOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </div>
            </el-select>
          </el-form-item>
          <div
            v-if="
              selectedAgentId != null &&
                selectedAgentId > 0 &&
                selectedType == 14
            "
          >
            <el-form-item label="IP Address" :required="true">
              <el-input
                type="text"
                v-model="controllerIpAddress"
                placeholder="Enter IP Address"
                class="fc-input-full-border2 width100"
              ></el-input>
            </el-form-item>

            <el-form-item label="Port" :required="true">
              <el-input
                type="number"
                v-model="controllerPort"
                placeholder="Enter Port"
                class="fc-input-full-border2 width100"
              ></el-input>
            </el-form-item>
          </div>
          <div
            class="pB10"
            v-if="
              selectedType == 1 &&
                this.agentType == 1 &&
                isAdvancedDiscoveryEnabled
            "
          >
            <el-form-item :required="true" label="UDP Port">
              <el-select
                v-model="bacnetPort"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="item in bacnetPorts"
                  :key="item"
                  :label="item"
                  :value="item"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :required="false" label="Timeout (Mins)">
              <el-select
                v-model="timeout_sec"
                class="fc-input-full-border-select2  width100"
              >
                <el-option
                  v-for="item in discoverControllersTimeout"
                  :key="item"
                  :label="item"
                  :value="item"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              :required="false"
              label="Instance Number Range"
              style="width: 100%;"
            >
              <br />
              <el-radio label="all" v-model="IsfullRange">
                <span class="radio-button-padding">All the devices</span>
              </el-radio>
              <br />
              <el-radio
                lable="btwrange"
                v-model="IsfullRange"
                class="radio-lable"
              >
                <div class="d-flex width100">
                  <span class="instance-radio-lable">From</span>
                  <el-input
                    type="number"
                    v-model="instanceNumberFrom"
                    class="fc-input-full-border2 width100"
                  ></el-input>
                  <span class="instance-radio-lable">To</span>
                  <el-input
                    type="number"
                    v-model="instanceNumberTo"
                    class="fc-input-full-border2 width100"
                  ></el-input>
                </div>
              </el-radio>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialogAndResetValues">
          {{ $t('agent.agent.cancel') }}
        </el-button>
        <el-button
          :disabled="handleDisable"
          type="primary"
          class="modal-btn-save"
          @click="discoverController"
          >{{ $t('agent.agent.discover') }}
        </el-button>
      </div>
    </el-dialog>
    <div>
      <el-dialog
        :title="$t('agent.agent.discover_points')"
        :visible.sync="timeoutDialog"
        width="30%"
        :append-to-body="true"
        :before-close="closeTimeoutDialog"
        class="fc-dialog-center-container"
        style="z-index: 999999"
      >
        <el-form>
          <div class="height380">
            <el-form-item label="Agent">
              <el-select
                disabled
                v-model="selectedAgent.displayName"
                class="fc-input-full-border-select2  width100"
              >
                <el-option
                  v-for="(agent, index) in selectedAgent"
                  :key="index"
                  :label="agent.displayName"
                  :value="agent.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <div v-if="lookupModuleName != null">
                <div class="f12 pB5 bold">Controller</div>
                  <FLookupFieldWrapper
                    v-model="selectedControllerReset"
                    :key="lookupModuleName"
                    :field="{
                      lookupModule: {
                        name: lookupModuleName,
                      },
                      multiple: true,
                    }"
                    :filterConstruction="constructAgentFilter"
                    :disabled="true"
                    :hideLookupIcon="true"
                    class="width100"
                  ></FLookupFieldWrapper>
              </div>
            </el-form-item>
            <el-form-item label="Timeout" :required="true">
              <el-select
                v-model="timeoutInterval"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="interval in timeoutInMin"
                  :key="interval.value"
                  :label="interval.label"
                  :value="interval.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </div>
          <div class="modal-dialog-footer">
            <el-button
              class="modal-btn-cancel f13"
              @click="closeTimeoutDialog"
              >{{ $t('agent.agent.cancel') }}</el-button
            >
            <el-button
              class="modal-btn-save f13"
              @click="discoverPoints"
              >{{ $t('agent.agent.discover_points') }}</el-button
            >
          </div>
        </el-form>
      </el-dialog>
    </div>
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
            v-model="configurePointsInterval"
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
            @click="configurePoints()"
            >{{ $t('agent.agent.set_interval') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import Pagination from 'src/components/list/FPagination'
import AgentHelp from 'agent/AgentHelperDocs/HelpDevice'
import NewAddController from 'agent/components/ControllerAddForm'
import agentmixin from '@/mixins/AgentMixin'
import { isEmpty, isNull, isNullOrUndefined } from '@facilio/utils/validation'
import SelectAgent from 'src/agent/components/SelectAgent.vue'
import Constants from 'util/constant'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

export default {
  title() {
    return 'Agent Controllers'
  },
  data() {
    return {
      isAdvancedDiscoveryEnabled: false,
      IsfullRange: 'all',
      showSelectedType: false,
      loading: false,
      controllerPort: null,
      controllerIpAddress: null,
      controllers: [],
      selectedAgentId: null,
      controller: null,
      selectedAgent: [],
      controllerTypes: [],
      agent: null,
      showHelpDialog: false,
      selectedType: null,
      listCount: 0,
      selectedControllerId: null,
      selectedControllerReset: {},
      selectall: false,
      realControllers: [],
      perPage: 50,
      showDialog: false,
      querySearch: null,
      showQuickSearches: false,
      dialogVisible: false,
      isNiagaraAgent: false,
      discovering: false,
      configuringAllPoints: false,
      agentTypes: [],
      selectedControllerCheck: null,
      discoveringPoints: false,
      discoverControllerDialog: false,
      instanceNumberFrom: 0,
      instanceNumberTo: 4194302,
      bacnetPort: 47808,
      timeout_sec: 1,
      wholeRange: null,
      lookupModuleName: null,
      resetSelectedType : false,
      bacnetPorts: [
        47808,
        47809,
        47810,
        47811,
        47812,
        47813,
        47814,
        47815,
        47816,
        47817,
      ],
      timeoutInterval : 5,
      configurePointsInterval: 0,
      intervalDialog: false,
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
      timeoutDialog: false,
      discoverControllersTimeout: [1, 2, 3, 5, 8, 10, 15],
      facilioJavaOptions: [
        {
          value: 1,
          label: 'BACnet Ip',
        },
        {
          value: 12,
          label: 'System',
        },
      ],
      e2Options: [
        {
          value: 14,
          label: 'E2',
        },
      ],
      rdmOptions: [
        {
          value: 13,
          label: 'RDM',
        },
      ],
      value: -1,
      niagaraOptions: [
        {
          value: 0,
          label: 'Misc',
        },
        {
          value: 1,
          label: 'BACnet Ip',
        },
        {
          value: 3,
          label: 'Fox',
        },
        {
          value: 5,
          label: 'Modbus_Rtu',
        },
        {
          value: 8,
          label: 'Lon Works',
        },
      ],
      timeoutInMin: [
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
        {
          value: 30,
          label: '30 Mins',
        },
        {
          value: 60,
          label: '60 Mins',
        }
      ],
    }
  },
  mixins: [agentmixin],
  components: {
    Pagination,
    AgentHelp,
    NewAddController,
    SelectAgent,
    FLookupFieldWrapper,
  },
  watch: {
    selectedAgentId(newVal, oldVal) {
      if (oldVal != null) {
        let page = this.$route.query.page
        const query = Object.assign({}, this.$route.query)
        if (page) delete query.page
        query.agentId = newVal
        this.$router.replace({ query })
      }
    },
    page() {
      this.loadControllers()
      this.loadCount(true)
    },
    selectAll(val) {
      if (val) {
        this.selectedControllerCheck = []
        this.controllers.filter(controller => {
          this.selectedControllerCheck.push(controller.id)
        })
      } else {
        if (this.selectedControllerCheck.length === this.controller.length) {
          this.selectedControllerCheck = []
        }
      }
    },
  },
  computed: {
    discoverControllersDialogHeight() {
      return this.selectedType === 14
        ? 'height500'
        : this.isAdvancedDiscoveryEnabled && this.selectedType == 1
        ? 'height635'
        : 'height300'
    },
    handleDisable() {
      if (this.selectedType == null) {
        return true
      } else if (this.selectedType == 14) {
        return !(
          this.controllerIpAddress != null && this.controllerPort != null
        )
      }
    },
    selectedController() {
      if (this.selectedControllerId) {
        return this.controllers.find(
          controller => controller.id === this.selectedControllerId
        )
      }
      return null
    },
    availableTypes() {
      return this.controllerTypes.map(controller => controller.controllerType)
    },

    page() {
      return this.$route.query.page || 1
    },
    agentFilter() {
      return this.agents.filter(agent => agent.id === this.selectedAgentId)
    },
  },
  methods: {
    configureInterval() {
      if (this.agentType === 1) {
        // Temp. Will be supported for all
        this.intervalDialog = true
      } else {
        this.configurePoints()
      }
    },
    configurePoints() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      this.loading = true
      this.configuringAllPoints = true

      let params = {
        controllerType: this.selectedType,
        recordIds: this.selectedControllerReset,
        agentId: this.selectedAgentId,
      }
      if (this.configurePointsInterval > 0) {
        params.interval = this.configurePointsInterval
      }

      this.$http.post('/v2/point/configureAll', params).then(response => {
        if (response.data.responseCode === 200) {
          let self = this
          self.$message.info('Points for the selected controllers will be configured in a while.')
          self.loading = false
          self.configuringAllPoints = false
          this.intervalDialog = false
        } else {
          this.loading = false
          this.configuringAllPoints = false
          this.$message.error(response.data.message)
          this.intervalDialog = false
        }
      })
      this.resetSelectedControllers()
    },
    closeIntervalDialog() {
      this.intervalDialog = false
    },
    constructAgentFilter() {
      return { id: { operatorId: 9, value: this.selectedControllerReset.map(String) } }
    },
    resetSelectedControllers(){
      this.selectedControllerReset = {}
    },
    canAddController() {
      return (
        this.agentType != 2 && this.agentType != 0 && this.selectedType != 13
      )
    },
    listRefresh() {
      this.loadControllers()
      this.loadCount()
    },
    helpDialogOpen() {
      this.showHelpDialog = true
    },
    redirectoPointsPage(controller, tab) {
      let query = {
        agentId: this.selectedAgentId,
        agentPointsTab: tab,
        controllerType: controller.controllerType,
        controllerId: controller.id,
      }

      let path = '/iot/agent/points'
      this.$router.push({
        path,
        query,
      })
    },

    loadControllerType() {
      this.isConnected = this.selectedAgent.connected
      this.setLoading(true)
      let url = `/v2/controller/getFilter?agentId=${this.selectedAgentId}`
      this.$http(url)
        .then(response => {
          this.controllerTypes = response.data.result.data
            ? response.data.result.data
            : []
          if (this.controllerTypes.length) {
            this.selectedType =
              parseInt(this.$route.params.controllerType) ||
              this.controllerTypes[0].controllerType
            this.loadControllers()
            this.loadCount()
          } else {
            this.selectedType = null
            this.loadControllers()
            this.loadCount()
            this.setLoading(false)
          }
        })
        .catch(() => {
          this.selectedAgent = []
          this.setLoading(false)
        })
    },
    setLoading(isLoading) {
      this.$emit('update:loading', isLoading)
    },
    onTypeChange() {
      this.resetSelectedControllers()
      this.loadControllers()
      this.loadCount()
    },
    loadCount() {
      if (!(this.selectedAgentId && this.selectedType != null)) {
        this.listCount = 0
        return
      }
      let url = `/v2/controller/controllers?agentId=${this.selectedAgentId}&controllerType=${this.selectedType}&count=true`
      this.$http(url).then(response => {
        if (response.data.responseCode === 200) {
          this.listCount = response.data.result.data
        }
      })
    },
    loadControllers() {
      if (!(this.selectedAgentId && this.selectedType != null)) {
        this.realControllers = []
        return
      }
      this.loading = true
      let url = `/v2/controller/controllers?agentId=${this.selectedAgentId}&controllerType=${this.selectedType}`
      url += '&page=' + this.page + '&perPage=' + this.perPage
      this.$http(url)
        .then(response => {
          this.loading = false
          this.realControllers = response.data.result.data
            ? response.data.result.data
            : []
          this.setLoading(false)
        })
        .catch(() => {
          this.realControllers = []
          this.loading = false
          this.setLoading(false)
        })
    },
    selectControllerReset(selectedRs) {
      this.selectedControllerReset = selectedRs.map(value => value.id)
    },
    discoverController() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      let discoverControllersData = {
        agentId: this.selectedAgentId,
        controllerType: this.selectedType,
      }
      if (this.selectedType === 14) {
        discoverControllersData.ipAddress = this.controllerIpAddress,
        discoverControllersData.port = this.controllerPort
      }
      if(this.agentType == 6){
        discoverControllersData.controllerType = 13
      }
      if(this.agentType == 9){
        discoverControllersData.controllerType = 14
      }
      if (this.selectedType === 1 && this.agentType == 1) {
        if (this.IsfullRange != 'all') {
          let range = {}
          range.low = parseInt(this.instanceNumberFrom),
          range.high = parseInt(this.instanceNumberTo),
          discoverControllersData.range = range
        }
        discoverControllersData.bacnetPort = this.bacnetPort,
        discoverControllersData.timeout_sec = this.timeout_sec * 60
      }
      this.loading = true
      this.discovering = true
      this.$http
        .post('/v2/controller/discover', discoverControllersData)
        .then(response => {
          if (response.data.responseCode === 200) {
            let self = this
            self.$message.info('Controllers will be discovered in a while.')
            self.loading = false
            self.discovering = false
            self.listRefresh()
          } else {
            this.loading = false
            this.discovering = false
            this.$message.error(response.data.message)
          }
        })
      // this.closeDiscoverControllersDialog()
      if (this.discoverControllerDialog) {
        this.closeDialogAndResetValues()
      }
    },
    closeTimeoutDialog() {
      this.timeoutDialog = false
      this.lookupModuleName = null
    },
    isFacilioAgentAndBACnetOrOPCUa(){
      return this.agentType == 1 && (this.selectedType == 1 || this.selectedType == 7);
    },
    advanceDiscoverPoints() {
      if(this.isFacilioAgentAndBACnetOrOPCUa()){
        this.lookupModuleName = Constants.ControllerModuleName[this.selectedType]
        this.timeoutDialog = true
      } else {
        this.discoverPoints()
      }
    },
    discoverPoints() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      this.timeoutDialog = false
      this.loading = true
      this.discoveringPoints = true
      let params = {
          agentId: this.selectedAgentId,
          controllerType: this.selectedType,
          recordIds: this.selectedControllerReset,
      }
      if(this.isFacilioAgentAndBACnetOrOPCUa()){
        params.timeoutInSeconds = this.timeoutInterval * 60
      }
      this.$http
        .post('v2/device/discoverPoints', params)
        .then(response => {
          if (response.data.responseCode === 200) {
            this.$message.info('Points will be discovered in a while.')
            this.resetDiscoverPointsFields()
            this.listRefresh()
          } else {
            this.resetDiscoverPointsFields()
            this.$message.error(response.data.message)
          }
        })
    },
    showController() {
      this.showDialog = true
    },
    resetDiscoverPointsFields(){
      this.loading = false
      this.discoveringPoints = false
      this.timeoutInterval = 5
      this.resetSelectedControllers()
      this.lookupModuleName = null
    },
    onOptionsSelect(command, controller) {
      if (command === 'reset') {
        this.agentReset(controller)
      }
    },
    agentReset(controller) {
      this.loading = true
      this.$http
        .post('/v2/controller/reset', {
          controllerId: controller.id,
        })
        .then(response => {
          if (response.data.responseCode === 200) {
            this.$message.success('Reset updated successfully.')
            this.loading = false
          } else {
            this.$message.error(response.data.message)
            this.loading = false
          }
        })
    },
    quickSearches() {
      let string = this.querySearch
      if (string) {
        this.loadSearch()
        this.loadSearchCount()
      }
    },
    loadSearch() {
      if (
        !(
          this.selectedAgentId &&
          this.selectedType != null &&
          this.querySearch != null
        )
      ) {
        this.realControllers = []
        return
      }
      this.loading = true
      let url = `/v2/controller/controllers?agentId=${this.selectedAgentId}&querySearch=${this.querySearch}&controllerType=${this.selectedType}`
      url += '&page=' + this.page + '&perPage=' + this.perPage
      if (url) {
        this.$http(url)
          .then(response => {
            this.loading = false
            this.realControllers = response.data.result.data
              ? response.data.result.data
              : []
          })
          .catch(() => {
            this.loading = false
            this.realControllers = []
          })
      }
    },
    loadSearchCount() {
      if (
        !(
          this.selectedAgentId &&
          this.selectedType != null &&
          this.querySearch != null
        )
      ) {
        this.listCount = 0
        return
      }
      let url = `/v2/controller/controllers?agentId=${this.selectedAgentId}&querySearch=${this.querySearch}&controllerType=${this.selectedType}&count=true`
      if (url) {
        this.$http(url).then(response => {
          this.loading = false
          if (response.data.responseCode === 200) {
            this.listCount = response.data.result.data
          }
        })
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
      this.loadControllers()
      this.loadCount()
    },
    updateAgentId(selectedAgent) {
      this.resetSelectedControllers()
      this.selectedAgent = selectedAgent
      this.selectedAgentId = selectedAgent.id
      this.agentType = selectedAgent.agentType
      this.isNiagaraAgent = selectedAgent.agentType === 2 ? true : false
      this.loadControllerType()
    },
    discoverOrOpenDialog(isAdvancedDiscoverControllers) {
      if ( this.selectedType == null ? this.agentType == 1 || this.agentType == 2 : (this.selectedType == 1 && this.agentType == 1 && isAdvancedDiscoverControllers)) {
        this.resetSelectedType = this.selectedType == null ? true : false
        this.isAdvancedDiscoveryEnabled = isAdvancedDiscoverControllers
        this.discoverControllerDialog = true
      } else {
        this.discoverController()
      }
    },
    closeDialogAndResetValues() {
      this.discoverControllerDialog = false
      this.controllerPort = null
      this.controllerIpAddress = null
      if(this.resetSelectedType){
        this.selectedType = null
      }
      this.IsfullRange = 'all',
      this.instanceNumberFrom = 0,
      this.instanceNumberTo = 4194302,
      this.timeout_sec = 1,
      this.bacnetPort = 47808
      this.isAdvancedDiscoveryEnabled = false
    },
  },
}
</script>

<style lang="scss">
.agents-dialog .el-dialog__body {
  height: 300px;
  padding: 10px 20px 5px 20px;
}

.height635 {
  height: 625px;
}

.configure-btn {
  margin-top: 18px;
  display: flex;
  height: 40px;
}

.adv-discover {
  border-radius: 3px;
  border: 1px solid #d0d9e2;
  margin-top: 18px;
  display: flex;
  height: 40px;
  .separator {
    padding: 0px !important;
    width: 1px;
    border-right: 1px solid #d0d9e2;
  }
  .small-btn {
    width: 40px !important;
    justify-content: center;
    align-items: center;
    display: flex;
  }
  .fc-white-btn-b {
    border: none !important;
    background-color: #ffffff;
    padding: 9.5px 22px;
    font-size: 12px;
    font-weight: 500;
    text-transform: uppercase;
    line-height: normal;
    letter-spacing: 1px;
    text-align: center;
    color: #324056;
  }
}

.radio-lable {
  width: 100% !important;
  display: flex;
  .el-radio__label {
    width: 100% !important;
  }
}

.instance-radio-lable {
  display: flex;
  align-items: center;
  padding: 10px;
}

.radio-button-padding {
  padding: 8px !important;
}
</style>
