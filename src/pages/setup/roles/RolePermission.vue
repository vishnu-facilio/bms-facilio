<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog60 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <el-form :model="newrole" :label-position="'top'" ref="ruleForm">
      <div id="newrole">
        <div
          class="form-header inactiveheader"
          v-bind:class="{ activeformheader: focusheader }"
        >
          <div class="roletitle">
            {{
              isNew
                ? $t('common.products.new_role')
                : $t('common.header.edit_role')
            }}
          </div>
          <div v-if="!focusheader" style="float:right;padding-right:20px;">
            <el-button
              class="primarybutton btn-modal-fill"
              type="primary"
              :loading="saving"
              @click="newRole('ruleForm')"
              >{{
                saving
                  ? $t('common._common._saving')
                  : $t('common._common._save')
              }}</el-button
            >
            <el-button class="btn-modal-border mR20" @click="closeDialog()">{{
              $t('common._common.cancel')
            }}</el-button>
          </div>
          <div style="width:60%">
            <el-form-item prop="name" style="margin-bottom: 0 !important;">
              <div class="form-input">
                <el-input
                  autofocus
                  id="rolename"
                  ref="input1"
                  required
                  class="required"
                  v-model="newrole.name"
                  type="text"
                  autocomplete="off"
                  :placeholder="$t('common._common.enter_role_name')"
                />
              </div>
            </el-form-item>
            <div class="form-input">
              <el-input
                id="descriptionbox"
                @focus="activeheader()"
                type="textarea"
                class="required"
                v-model="newrole.description"
                autocomplete="off"
                :placeholder="
                  $t('common.wo_report.description_for_the_current_role')
                "
              ></el-input>
            </div>
          </div>
        </div>
        <form method="post" @submit.prevent="newRole" class="fc-form">
          <div
            id="row"
            class="row"
            style="position: fixed;width: 60%;"
            v-bind:class="{ fixedrow: focusheader }"
            @click="hideheader()"
          >
            <div
              style="width:30%;display:inline-block;border-right:1px solid #e1e9ed;border-top:1px solid #edeeef"
            >
              <div
                id="list"
                class="overflow-y-scroll side-list-height"
                style="background-color: #f7f8f9;"
              >
                <ul class="pT20">
                  <li
                    class="pointer"
                    data-section="pointer"
                    v-bind:class="{
                      activelist: active == 'space',
                      activecircle: active == 'space',
                      inactivelist: spacepermission == false,
                    }"
                    @click="option('space')"
                    v-if="this.$helpers.isLicenseEnabled('NEW_LAYOUT')"
                  >
                    {{ $t('common.header.portfolio') }}
                  </li>
                  <a
                    href="#"
                    v-if="this.$helpers.isLicenseEnabled('NEW_LAYOUT')"
                  >
                    <li
                      class="pointer prograss-section"
                      id="list1"
                      data-section="dashboard"
                      v-bind:class="{
                        activelist: active === 'dashboard',
                        activecircle: active == 'dashboard',
                        inactivelist: workorderpermission == false,
                      }"
                      @click="option('dashboard')"
                    >
                      {{ $t('common.header.dashboard') }}
                    </li>
                  </a>
                  <a href="#">
                    <li
                      class="pointer prograss-section"
                      id="list1"
                      data-section="wo"
                      v-bind:class="{
                        activelist: active == 'wo',
                        activecircle: active == 'wo',
                        inactivelist: workorderpermission == false,
                      }"
                      @click="option('wo')"
                    >
                      {{ $t('common._common.workorder') }}
                    </li>
                  </a>
                  <a href="#" class="prograss-section">
                    <li
                      class="pointer"
                      data-section="pm"
                      v-bind:class="{
                        activelist: active == 'pm',
                        activecircle: active == 'pm',
                        inactivelist: pmpermission == false,
                      }"
                      @click="option('pm')"
                    >
                      {{ $t('common.header.planned_maintenance') }}
                    </li>
                  </a>
                  <li
                    class="pointer"
                    data-section="alarm"
                    v-bind:class="{
                      activelist: active == 'alarm',
                      activecircle: active == 'alarm',
                      inactivelist: alarmpermission == false,
                    }"
                    @click="option('alarm')"
                  >
                    {{ $t('alarm.sensor_alarm.alarms') }}
                  </li>
                  <li
                    class="pointer"
                    data-section="alarmRules"
                    v-bind:class="{
                      activelist: active == 'alarmRules',
                      activecircle: active == 'alarmRules',
                      inactivelist: alarmRulespermission == false,
                    }"
                    @click="option('alarmRules')"
                  >
                    {{ $t('setup.setup.alarmrules') }}
                  </li>
                  <li
                    class="pointer"
                    data-section="energy"
                    v-bind:class="{
                      activelist: active == 'energy',
                      activecircle: active == 'energy',
                      inactivelist: energypermission == false,
                    }"
                    @click="option('energy')"
                  >
                    {{ $t('common.products.energy') }}
                  </li>
                  <li
                    class="pointer"
                    data-section="pointer"
                    v-bind:class="{
                      activelist: active == 'space',
                      activecircle: active == 'space',
                      inactivelist: spacepermission == false,
                    }"
                    @click="option('space')"
                    v-if="!this.$helpers.isLicenseEnabled('NEW_LAYOUT')"
                  >
                    {{ $t('setup.users_management.space') }}
                  </li>
                  <li
                    class="pointer"
                    data-section="asset"
                    v-bind:class="{
                      activelist: active == 'asset',
                      activecircle: active == 'asset',
                      inactivelist: assetpermission == false,
                    }"
                    @click="option('asset')"
                  >
                    {{ $t('common._common.asset') }}
                  </li>
                  <li
                    class="pointer"
                    data-section="inventory"
                    v-bind:class="{
                      activelist: active == 'inventory',
                      activecircle: active == 'inventory',
                      inactivelist: inventorypermission == false,
                    }"
                    @click="option('inventory')"
                    v-if="this.$helpers.isLicenseEnabled('INVENTORY')"
                  >
                    {{ $t('common.products.inventory') }}
                  </li>
                  <li
                    class="pointer"
                    data-section="inventoryRequest"
                    v-bind:class="{
                      activelist: active == 'inventoryRequest',
                      activecircle: active == 'inventoryRequest',
                      inactivelist: inventorypermission == false,
                    }"
                    @click="option('inventoryRequest')"
                    v-if="this.$helpers.isLicenseEnabled('INVENTORY')"
                  >
                    {{ $t('common.header.inventory_request') }}
                  </li>

                  <li
                    class="pointer"
                    data-section="tenant"
                    v-bind:class="{
                      activelist: active == 'tenant',
                      activecircle: active == 'tenant',
                      inactivelist: tenantpermission == false,
                    }"
                    @click="option('tenant')"
                    v-if="this.$helpers.isLicenseEnabled('TENANTS')"
                  >
                    {{ $t('common.products.tenants') }}
                  </li>
                  <li
                    class="pointer"
                    data-section="purchaseorder"
                    v-bind:class="{
                      activelist: active == 'purchaseorder',
                      activecircle: active == 'purchaseorder',
                      inactivelist: purchaseorderpermission == false,
                    }"
                    @click="option('purchaseorder')"
                    v-if="this.$helpers.isLicenseEnabled('PURCHASE')"
                  >
                    {{ $t('common.products.purchase_orders') }}
                  </li>
                  <li
                    class="pointer"
                    data-section="contract"
                    v-bind:class="{
                      activelist: active == 'contract',
                      activecircle: active == 'contract',
                      inactivelist: contractpermission == false,
                    }"
                    @click="option('contract')"
                    v-if="this.$helpers.isLicenseEnabled('CONTRACT')"
                  >
                    {{ $t('common.products.Contract') }}
                  </li>
                  <li
                    class="pointer"
                    v-for="(moduleDetails, index) in modulesListBasedOnLicense"
                    :key="`module-list=${index}`"
                    :data-section="moduleDetails.name"
                    v-bind:class="{
                      activelist: active == moduleDetails.name,
                      activecircle: active == moduleDetails.name,
                      inactivelist: moduleDetails.permission == false,
                    }"
                    @click="option(moduleDetails.name)"
                  >
                    {{ moduleDetails.displayName }}
                  </li>
                  <li
                    class="pointer"
                    data-section="admin"
                    v-bind:class="{
                      activelist: active == 'admin',
                      activecircle: active == 'admin',
                      inactivelist: adminpermission == false,
                    }"
                    @click="option('admin')"
                  >
                    {{ $t('common.products.admin') }}
                  </li>
                </ul>
              </div>
            </div>
            <div id="container" class="container_class">
              <div
                id="space"
                v-if="this.$helpers.isLicenseEnabled('NEW_LAYOUT')"
              >
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('common.header.portfolio_permissions') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="spacepermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="spacepermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('spacepermission', $event)"
                      v-model="spacepermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="CREATESPACE"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.import') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="536870912"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="IMPORTSPACE"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="SPACE_READ"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1024"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="SPACE_UPDATE"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup.add_reading') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="33554432"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="SPACE_READING"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup_profile.photo_delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="SPACE_DELETE"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>
              <div
                id="dashboard"
                v-if="this.$helpers.isLicenseEnabled('NEW_LAYOUT')"
              >
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('common.dashboard.dashboard_permissions') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="dashboardpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="dashboardpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('dashboardpermission', $event)"
                      v-model="dashboardpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="dashboardpermission == false"
                        active-color="#39b2c2"
                        v-model="CREATE_DASHBOARD"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('common._common.view') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="dashboardpermission == false"
                        active-color="#39b2c2"
                        v-model="READ_DASHBOARD"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('common._common.edit') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1024"
                        :inactive-value="0"
                        :disabled="dashboardpermission == false"
                        active-color="#39b2c2"
                        v-model="UPDATE_DASHBOARD"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="dashboardpermission == false"
                        active-color="#39b2c2"
                        v-model="DELETE_DASHBOARD"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.share_dashboard') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="34359738368"
                        :inactive-value="0"
                        :disabled="dashboardpermission == false"
                        active-color="#39b2c2"
                        v-model="SHARE_DASHBOARD"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>
              <div id="wo" class="pB30">
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('common.header.workorder_permissions') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="workorderpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="workorderpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('workorderpermission', $event)"
                      v-model="workorderpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="workorderpermission == false"
                        active-color="#39b2c2"
                        v-model="createpermission"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="workorderpermission == false"
                        active-color="#39b2c2"
                        v-model="readpermission"
                      ></el-switch>
                    </div>
                    <div
                      style="display:inline-block;padding-left:15px"
                      v-if="readpermission"
                    >
                      <el-radio-group v-model="readpermissionValue">
                        <el-radio
                          v-for="perm in readPerm"
                          :key="perm.value"
                          :label="perm.value"
                          >{{ perm.label }}</el-radio
                        >
                      </el-radio-group>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="workorderpermission == false"
                        active-color="#39b2c2"
                        v-model="updatepermission"
                      ></el-switch>
                    </div>
                    <div
                      v-if="updatepermission"
                      style="display:inline-block;padding-left:15px"
                    >
                      <el-radio-group v-model="updatepermissionValue">
                        <el-radio
                          v-for="perm in updatePerm"
                          :key="perm.value"
                          :label="perm.value"
                          >{{ perm.label }}</el-radio
                        >
                      </el-radio-group>
                    </div>
                    <div v-if="updatepermission" style="padding-bottom: 15px;">
                      <div>
                        <div class="suboperation">
                          {{ $t('common._common.change_ownership') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="524288"
                            :inactive-value="0"
                            :disabled="workorderpermission == false"
                            v-model="WO_CHANGE"
                            active-color="#39b2c2"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('common.header.close_workorder') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="1048576"
                            :inactive-value="0"
                            :disabled="workorderpermission == false"
                            v-model="WO_CLOSE"
                            active-color="#39b2c2"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('common._common.add_task') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="2097152"
                            :inactive-value="0"
                            :disabled="workorderpermission == false"
                            active-color="#39b2c2"
                            v-model="WO_TASK"
                          ></el-switch>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="workorderpermission == false"
                        active-color="#39b2c2"
                        v-model="deletepermission"
                      ></el-switch>
                    </div>
                    <div
                      style="display:inline-block;padding-left:15px"
                      v-if="deletepermission"
                    >
                      <el-radio-group
                        :disabled="workorderpermission == false"
                        v-model="deletepermissionValue"
                      >
                        <el-radio
                          v-for="perm in deletePerm"
                          :key="perm.value"
                          :label="perm.value"
                          >{{ perm.label }}</el-radio
                        >
                      </el-radio-group>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.general') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="workorderpermission == false"
                        v-model="woGeneral"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                    <div v-if="woGeneral">
                      <div v-if="!newlayout">
                        <div class="suboperation">
                          {{ $t('common._common.view_dashboards') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="34359738368"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="WO_VIEW_DASHBOARDS"
                          ></el-switch>
                        </div>
                      </div>
                      <div v-if="!newlayout">
                        <div class="suboperation">
                          {{ $t('common.header.create_edit_dashboards') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="4194304"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="WO_CREATE_EDIT_DASHBOARD"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('setup.setup.view_reports') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="8388608"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="WO_VIEW_REPORTS"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('common.header.create_edit_reports') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="16777216"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="WO_CREATE_EDIT_REPORT"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('setup.setup.export_reports') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="67108864"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="WO_EXPORT_REPORT"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('common.header.calender') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="134217728"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="WO_CALENDAR"
                          ></el-switch>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.approvalprocess.approval') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="
                          workorderpermission === false ||
                            readpermission === false
                        "
                        :active-value="VIEWAPPROVAL"
                        :inactive-value="0"
                        active-color="#39b2c2"
                        v-model="WO_VIEW_APPROVAL"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div id="pm">
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('setup.setup.planned_maintenance_permission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="pmpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span class="headerswitch" v-if="pmpermission == false">{{
                      $t('common._common.disabled')
                    }}</span>
                    <el-switch
                      @change="enablePermissions('pmpermission', $event)"
                      v-model="pmpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="pmpermission == false"
                        v-model="pmcreatepermission"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="pmpermission == false"
                        v-model="pmreadpermission"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                    <div
                      style="display:inline-block;padding-left:15px "
                      v-if="pmreadpermission"
                    >
                      <el-radio-group v-model="pmreadpermissionValue">
                        <el-radio
                          v-for="perm in readPerm"
                          :key="perm.value"
                          :label="perm.value"
                          >{{ perm.label }}</el-radio
                        >
                      </el-radio-group>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="pmpermission == false"
                        v-model="pmupdatepermission"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                    <div
                      style="display:inline-block;padding-left:15px"
                      v-if="pmupdatepermission"
                    >
                      <el-radio-group v-model="pmupdatepermissionValue">
                        <el-radio
                          v-for="perm in updatePerm"
                          :key="perm.value"
                          :label="perm.value"
                          >{{ perm.label }}</el-radio
                        >
                      </el-radio-group>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup.pm_calendar') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="134217728"
                        :inactive-value="0"
                        v-model="PM_CALENDER"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup.pm_planner') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1073741824"
                        :inactive-value="0"
                        v-model="PM_PLANNER"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="pmpermission == false"
                        v-model="pmdeletepermission"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                    <div
                      style="display:inline-block;padding-left:15px"
                      v-if="pmdeletepermission"
                    >
                      <el-radio-group v-model="pmdeletepermissionValue">
                        <el-radio
                          v-for="perm in deletePerm"
                          :key="perm.value"
                          :label="perm.value"
                          >{{ perm.label }}</el-radio
                        >
                      </el-radio-group>
                    </div>
                  </div>
                </div>
              </div>

              <div id="alarm">
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('setup.setup.alarm_permission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="alarmpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="alarmpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('alarmpermission', $event)"
                      v-model="alarmpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="alarmpermission == false"
                        active-color="#39b2c2"
                        v-model="alarmreadpermission"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup.acknowledge_alarm') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2147483648"
                        :inactive-value="0"
                        :disabled="alarmpermission == false"
                        active-color="#39b2c2"
                        v-model="ACK_ALARM"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup.clear_alarm') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4294967296"
                        :inactive-value="0"
                        :disabled="alarmpermission == false"
                        active-color="#39b2c2"
                        v-model="CLEAR_ALARM"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="alarmpermission == false"
                        active-color="#39b2c2"
                        v-model="alarmdeletepermission"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.general') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="alarmpermission == false"
                        v-model="generalalarm"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                    <div v-if="generalalarm">
                      <div>
                        <div class="suboperation">
                          {{ $t('common._common.create_workorder') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="17179869184"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="ALM_CREATE_WO"
                          ></el-switch>
                        </div>
                      </div>
                      <div v-if="!newlayout">
                        <div class="suboperation">
                          {{ $t('common._common.view_dashboards') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="34359738368"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="ALM_VIEW_DASHBOARDS"
                          ></el-switch>
                        </div>
                      </div>
                      <div v-if="!newlayout">
                        <div class="suboperation">
                          {{ $t('common.header.create_edit_dashboards') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="4194304"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="ALM_CREATE_EDIT_DASHBOARD"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('setup.setup.view_reports') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="8388608"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="ALM_VIEW_REPORTS"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('common.header.create_edit_reports') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="16777216"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="ALM_CREATE_EDIT_REPORT"
                          ></el-switch>
                        </div>
                      </div>
                      <div>
                        <div class="suboperation">
                          {{ $t('setup.setup.export_reports') }}
                        </div>
                        <div style="display:inline-block">
                          <el-switch
                            :active-value="67108864"
                            :inactive-value="0"
                            active-color="#39b2c2"
                            v-model="ALM_EXP_REPORT"
                          ></el-switch>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div id="alarmRules">
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('setup.setup.alarm_rules_permission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="alarmRulespermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="alarmRulespermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="
                        enablePermissions('alarmRulespermission', $event)
                      "
                      v-model="alarmRulespermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="alarmRulespermission == false"
                        active-color="#39b2c2"
                        v-model="CREATE_ALARM_RULES"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="alarmRulespermission == false"
                        active-color="#39b2c2"
                        v-model="READ_ALARM_RULES"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1024"
                        :inactive-value="0"
                        :disabled="alarmRulespermission == false"
                        v-model="UPDATE_ALARM_RULES"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="alarmRulespermission == false"
                        active-color="#39b2c2"
                        v-model="DELETE_ALARM_RULES"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div id="energy">
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('setup.setup.energy_permission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="energypermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="energypermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('energypermission', $event)"
                      v-model="energypermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter" v-if="!newlayout">
                    <div class="operation">
                      {{ $t('common._common.view_dashboards') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="34359738368"
                        :inactive-value="0"
                        :disabled="energypermission == false"
                        active-color="#39b2c2"
                        v-model="ENR_VIEW_DASHBOARDS"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter" v-if="!newlayout">
                    <div class="operation">
                      {{ $t('common.header.create_edit_dashboards') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4194304"
                        :inactive-value="0"
                        :disabled="energypermission == false"
                        active-color="#39b2c2"
                        v-model="ENR_CREATE_EDIT_DASHBOARD"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup.view_reports') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="8388608"
                        :inactive-value="0"
                        :disabled="energypermission == false"
                        active-color="#39b2c2"
                        v-model="ENR_VIEW_REPORTS"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.header.create_edit_reports') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="16777216"
                        :inactive-value="0"
                        :disabled="energypermission == false"
                        active-color="#39b2c2"
                        v-model="ENR_CREATE_EDIT_REPORT"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup.export_reports') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="67108864"
                        :inactive-value="0"
                        :disabled="energypermission == false"
                        active-color="#39b2c2"
                        v-model="ENR_EXPORT_REPORT"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div
                id="space"
                v-if="!this.$helpers.isLicenseEnabled('NEW_LAYOUT')"
              >
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('common.products.space_permissions') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="spacepermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="spacepermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('spacepermission', $event)"
                      v-model="spacepermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="CREATESPACE"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.import') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="536870912"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="IMPORTSPACE"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="SPACE_READ"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1024"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="SPACE_UPDATE"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.add_reading') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="33554432"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="SPACE_READING"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="spacepermission == false"
                        active-color="#39b2c2"
                        v-model="SPACE_DELETE"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div id="asset">
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('setup.setup.asset_permission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="assetpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="assetpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('assetpermission', $event)"
                      v-model="assetpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="assetpermission == false"
                        active-color="#39b2c2"
                        v-model="CREATEASSET"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.import') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="536870912"
                        :inactive-value="0"
                        :disabled="assetpermission == false"
                        active-color="#39b2c2"
                        v-model="IMPORTASSET"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="assetpermission == false"
                        active-color="#39b2c2"
                        v-model="ASSET_READ"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1024"
                        :inactive-value="0"
                        :disabled="assetpermission == false"
                        active-color="#39b2c2"
                        v-model="ASSET_UPDATE"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.add_reading') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="33554432"
                        :inactive-value="0"
                        :disabled="assetpermission == false"
                        active-color="#39b2c2"
                        v-model="ASSET_READING"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.control') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="CONTROL"
                        :inactive-value="0"
                        :disabled="assetpermission == false"
                        active-color="#39b2c2"
                        v-model="ASSET_CONTROL"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="assetpermission == false"
                        active-color="#39b2c2"
                        v-model="ASSET_DELETE"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div
                id="inventory"
                v-if="this.$helpers.isLicenseEnabled('INVENTORY')"
              >
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('common.header.inventory_permission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="inventorypermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="inventorypermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('inventorypermission', $event)"
                      v-model="inventorypermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="inventorypermission == false"
                        active-color="#39b2c2"
                        v-model="CREATE_INVENTORY"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="inventorypermission == false"
                        active-color="#39b2c2"
                        v-model="inventoryreadownpermission"
                      ></el-switch>
                    </div>
                    <div
                      style="display:inline-block;padding-left:15px"
                      v-if="inventoryreadownpermission"
                    >
                      <el-radio-group v-model="READ_INVENTORY">
                        <template v-for="perm in readPerm">
                          <el-radio
                            :key="perm.value"
                            :label="perm.value"
                            v-if="perm.label !== 'Team'"
                            >{{ perm.label }}</el-radio
                          >
                        </template>
                      </el-radio-group>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="inventorypermission == false"
                        v-model="inventoryupatepermission"
                        active-color="#39b2c2"
                      ></el-switch>
                    </div>
                    <div
                      style="display:inline-block;padding-left:15px"
                      v-if="inventoryupatepermission"
                    >
                      <el-radio-group v-model="UPDATE_INVENTORY">
                        <template v-for="perm in updatePerm">
                          <el-radio
                            :key="perm.value"
                            :label="perm.value"
                            v-if="perm.label !== 'Team'"
                            >{{ perm.label }}</el-radio
                          >
                        </template>
                      </el-radio-group>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.approvalprocess.approval') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="WRAPPREJ"
                        :inactive-value="0"
                        :disabled="inventorypermission == false"
                        active-color="#39b2c2"
                        v-model="APPROVE_INVENTORY"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="inventorypermission == false"
                        active-color="#39b2c2"
                        v-model="DELETE_INVENTORY"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div
                id="inventoryRequest"
                v-if="this.$helpers.isLicenseEnabled('INVENTORY')"
              >
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('common.header.inventory_request_permission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span
                      class="headerswitch"
                      v-if="inventoryRequestpermission"
                      >{{ $t('common._common.enabled') }}</span
                    >
                    <span
                      class="headerswitch"
                      v-if="inventoryRequestpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="
                        enablePermissions('inventoryRequestpermission', $event)
                      "
                      v-model="inventoryRequestpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="inventoryRequestpermission == false"
                        active-color="#39b2c2"
                        v-model="CREATE_INV_REQ"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="inventoryRequestpermission == false"
                        active-color="#39b2c2"
                        v-model="invreqreadpermission"
                      ></el-switch>
                    </div>
                    <div
                      style="display:inline-block;padding-left:15px"
                      v-if="invreqreadpermission"
                    >
                      <el-radio-group v-model="invreqreadpermissionValue">
                        <el-radio
                          v-for="perm in readPerm"
                          :key="perm.value"
                          :label="perm.value"
                          >{{ perm.label }}</el-radio
                        >
                      </el-radio-group>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :disabled="inventoryRequestpermission == false"
                        active-color="#39b2c2"
                        v-model="invrequpdatepermission"
                      ></el-switch>
                    </div>
                    <div
                      v-if="invrequpdatepermission"
                      style="display:inline-block;padding-left:15px"
                    >
                      <el-radio-group v-model="invrequpdatepermissionValue">
                        <el-radio
                          v-for="perm in updatePerm"
                          :key="perm.value"
                          :label="perm.value"
                          >{{ perm.label }}</el-radio
                        >
                      </el-radio-group>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="inventoryRequestpermission == false"
                        active-color="#39b2c2"
                        v-model="DELETE_INV_REQ"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.approvalprocess.approval') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="WRAPPREJ"
                        :inactive-value="0"
                        :disabled="inventoryRequestpermission == false"
                        active-color="#39b2c2"
                        v-model="APPROVE_INV_REQ"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div id="tenant" v-if="this.$helpers.isLicenseEnabled('TENANTS')">
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('setup.setup.tenantpermission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="tenantpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="tenantpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('tenantpermission', $event)"
                      v-model="tenantpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="tenantpermission == false"
                        active-color="#39b2c2"
                        v-model="CREATE_TENANT"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="tenantpermission == false"
                        active-color="#39b2c2"
                        v-model="READ_TENANT"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1024"
                        :inactive-value="0"
                        :disabled="tenantpermission == false"
                        active-color="#39b2c2"
                        v-model="UPDATE_TENANT"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="tenantpermission == false"
                        active-color="#39b2c2"
                        v-model="DELETE_TENANT"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>
              <div
                id="purchaseorder"
                v-if="this.$helpers.isLicenseEnabled('PURCHASE')"
              >
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('common.header.purchase_order_permissions') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="purchaseorderpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="purchaseorderpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="
                        enablePermissions('purchaseorderpermission', $event)
                      "
                      v-model="purchaseorderpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="purchaseorderpermission == false"
                        active-color="#39b2c2"
                        v-model="CREATE_PO"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="purchaseorderpermission == false"
                        active-color="#39b2c2"
                        v-model="READ_PO"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1024"
                        :inactive-value="0"
                        :disabled="purchaseorderpermission == false"
                        active-color="#39b2c2"
                        v-model="UPDATE_PO"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="purchaseorderpermission == false"
                        active-color="#39b2c2"
                        v-model="DELETE_PO"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.approval_complete_po') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="WRAPPREJ"
                        :inactive-value="0"
                        :disabled="purchaseorderpermission == false"
                        active-color="#39b2c2"
                        v-model="APPROVE_PO"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div
                id="contract"
                v-if="this.$helpers.isLicenseEnabled('CONTRACT')"
              >
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('common.header.contract_permissions') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="contractpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="contractpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('contractpermission', $event)"
                      v-model="contractpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2048"
                        :inactive-value="0"
                        :disabled="contractpermission == false"
                        active-color="#39b2c2"
                        v-model="CREATE_CONTRACT"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="512"
                        :inactive-value="0"
                        :disabled="contractpermission == false"
                        active-color="#39b2c2"
                        v-model="READ_CONTRACT"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1024"
                        :inactive-value="0"
                        :disabled="contractpermission == false"
                        active-color="#39b2c2"
                        v-model="UPDATE_CONTRACT"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4096"
                        :inactive-value="0"
                        :disabled="contractpermission == false"
                        active-color="#39b2c2"
                        v-model="DELETE_CONTRACT"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.approvalprocess.approval') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="WRAPPREJ"
                        :inactive-value="0"
                        :disabled="contractpermission == false"
                        active-color="#39b2c2"
                        v-model="APPROVE_CONTRACT"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>

              <div
                :id="moduleDetails.name"
                v-for="(moduleDetails, index) in modulesListBasedOnLicense"
                :key="`permissions-${index}`"
              >
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ moduleDetails.permissionsDisplayName }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span
                      class="headerswitch"
                      v-if="moduleDetails.permission"
                      >{{ $t('common._common.enabled') }}</span
                    >
                    <span class="headerswitch" v-else>{{
                      $t('common._common.disabled')
                    }}</span>
                    <el-switch
                      @change="enableAllPermissionsv2(moduleDetails, $event)"
                      v-model="moduleDetails.permission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div
                    v-if="moduleDetails.values.hasOwnProperty('CREATE')"
                    class="splitter"
                  >
                    <div class="operation">
                      {{ $t('common.products.create') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="CREATE"
                        :inactive-value="0"
                        :disabled="moduleDetails.permission == false"
                        active-color="#39b2c2"
                        v-model="moduleDetails.values.CREATE"
                      ></el-switch>
                    </div>
                  </div>
                  <div
                    v-if="moduleDetails.values.hasOwnProperty('READ')"
                    class="splitter"
                  >
                    <div class="operation">{{ $t('setup.setup.read') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="READ"
                        :inactive-value="0"
                        :disabled="moduleDetails.permission == false"
                        active-color="#39b2c2"
                        v-model="moduleDetails.values.READ"
                      ></el-switch>
                    </div>
                  </div>
                  <div
                    v-if="moduleDetails.values.hasOwnProperty('UPDATE')"
                    class="splitter"
                  >
                    <div class="operation">
                      {{ $t('common._common.update') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="UPDATE"
                        :inactive-value="0"
                        :disabled="moduleDetails.permission == false"
                        active-color="#39b2c2"
                        v-model="moduleDetails.values.UPDATE"
                      ></el-switch>
                    </div>
                  </div>
                  <div
                    v-if="moduleDetails.values.hasOwnProperty('DELETE')"
                    class="splitter"
                  >
                    <div class="operation">
                      {{ $t('common._common.delete') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="DELETE"
                        :inactive-value="0"
                        :disabled="moduleDetails.permission == false"
                        active-color="#39b2c2"
                        v-model="moduleDetails.values.DELETE"
                      ></el-switch>
                    </div>
                  </div>
                  <div
                    v-if="moduleDetails.values.hasOwnProperty('EXPORT')"
                    class="splitter"
                  >
                    <div class="operation">
                      {{ $t('setup.setup.export') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="EXPORT"
                        :inactive-value="0"
                        :disabled="moduleDetails.permission == false"
                        active-color="#39b2c2"
                        v-model="moduleDetails.values.EXPORT"
                      ></el-switch>
                    </div>
                  </div>
                </div>
              </div>
              <div id="admin" class="newheight">
                <div class="permission">
                  <div
                    class="roletitle"
                    style="display:inline-block;padding: 17px 40px;"
                  >
                    {{ $t('setup.setup.admin_permission') }}
                  </div>
                  <div
                    class="float-right"
                    style="display:inline-block;padding: 13px 40px;"
                  >
                    <span class="headerswitch" v-if="adminpermission">{{
                      $t('common._common.enabled')
                    }}</span>
                    <span
                      class="headerswitch"
                      v-if="adminpermission == false"
                      >{{ $t('common._common.disabled') }}</span
                    >
                    <el-switch
                      @change="enablePermissions('adminpermission', $event)"
                      v-model="adminpermission"
                      active-color="#ef4f8f"
                    ></el-switch>
                  </div>
                </div>
                <div style="padding-left:40px">
                  <div class="splitter">
                    <div class="operation">{{ $t('setup.setup.general') }}</div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="1"
                        :inactive-value="0"
                        :disabled="adminpermission == false"
                        active-color="#39b2c2"
                        v-model="Category"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.users_management.user_management') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="2"
                        :inactive-value="0"
                        :disabled="adminpermission == false"
                        active-color="#39b2c2"
                        v-model="UserManagement"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.header.workorder_settings') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="4"
                        :inactive-value="0"
                        :disabled="adminpermission == false"
                        active-color="#39b2c2"
                        v-model="WorkOrderSettings"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.header.alarm_settings') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="8"
                        :inactive-value="0"
                        :disabled="adminpermission == false"
                        active-color="#39b2c2"
                        v-model="AlarmsSettings"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common._common.space_asset_settings') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="8589934592"
                        :inactive-value="0"
                        :disabled="adminpermission == false"
                        active-color="#39b2c2"
                        v-model="AssetsSettings"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.controller') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="32"
                        :inactive-value="0"
                        :disabled="adminpermission == false"
                        active-color="#39b2c2"
                        v-model="Controller"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('common.products.energyAnalytics') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="64"
                        :inactive-value="0"
                        :disabled="adminpermission == false"
                        active-color="#39b2c2"
                        v-model="EnergyAnalytics"
                      ></el-switch>
                    </div>
                  </div>
                  <div class="splitter">
                    <div class="operation">
                      {{ $t('setup.setup.tenantbilling') }}
                    </div>
                    <div style="display:inline-block">
                      <el-switch
                        :active-value="256"
                        :inactive-value="0"
                        :disabled="adminpermission == false"
                        active-color="#39b2c2"
                        v-model="Tenantbilling"
                      ></el-switch>
                    </div>
                  </div>
                  <div></div>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
import $ from 'jquery'
import ErrorBanner from '@/ErrorBanner'
import { API } from '@facilio/api'
export default {
  props: ['isNew', 'role', 'appId'],
  data() {
    return {
      focusheader: false,
      errorText: '',
      error: false,
      dashboardpermission: false,
      workorderpermission: false,
      pmpermission: false,
      alarmpermission: false,
      energypermission: false,
      spacepermission: false,
      assetpermission: false,
      inventorypermission: false,
      alarmRulespermission: false,
      inventoryRequestpermission: false,
      tenantpermission: false,
      purchaseorderpermission: false,
      contractpermission: false,
      adminpermission: false,
      woGeneral: false,
      active: '',
      WO_CHANGE: false,
      WO_CLOSE: false,
      WO_TASK: false,
      WO_UPDATE_TASK: false,
      WO_DELETE_TASK: false,
      WO_ADD_COMMENT: false,
      woGeneralPerm: [],
      WO_VIEW_DASHBOARDS: false,
      WO_CREATE_EDIT_DASHBOARD: false,
      WO_VIEW_REPORTS: false,
      WO_CREATE_EDIT_REPORT: false,
      WO_EXPORT_REPORT: false,
      WO_CALENDAR: false,
      WO_VIEW_APPROVAL: false,
      pmPermGeneral: false,
      PM_CALENDER: false,
      PM_PLANNER: false,
      alarmPerm: [],
      ACK_ALARM: false,
      CLEAR_ALARM: false,
      generalalarm: false,
      ALM_CREATE_WO: false,
      ALM_VIEW_DASHBOARDS: false,
      ALM_CREATE_EDIT_DASHBOARD: false,
      ALM_VIEW_REPORTS: false,
      ALM_CREATE_EDIT_REPORT: false,
      ALM_EXP_REPORT: false,
      ENR_VIEW_DASHBOARDS: false,
      ENR_CREATE_EDIT_DASHBOARD: false,
      ENR_VIEW_REPORTS: false,
      ENR_CUS_DASHBOARD: false,
      ENR_CREATE_EDIT_REPORT: false,
      ENR_EXPORT_REPORT: false,
      CREATESPACE: false,
      IMPORTSPACE: false,
      SPACE_READ: false,
      SPACE_UPDATE: false,
      SPACE_READING: false,
      SPACE_DELETE: false,
      CREATEASSET: false,
      IMPORTASSET: false,
      ASSET_READ: false,
      ASSET_UPDATE: false,
      ASSET_READING: false,
      ASSET_DELETE: false,
      ASSET_CONTROL: false,
      SETUP_GROUPS: false,
      SETUP_LINKS: false,
      CREATE_INVENTORY: false,
      READ_INVENTORY: false,
      UPDATE_INVENTORY: false,
      DELETE_INVENTORY: false,
      CREATE_ALARM_RULES: false,
      READ_ALARM_RULES: false,
      UPDATE_ALARM_RULES: false,
      DELETE_ALARM_RULES: false,
      CREATE_INV_REQ: false,
      READ_INV_REQ: false,
      UPDATE_INV_REQ: false,
      DELETE_INV_REQ: false,
      APPROVE_INV_REQ: false,
      APPROVE_INVENTORY: false,
      CREATE_TENANT: false,
      READ_TENANT: false,
      UPDATE_TENANT: false,
      DELETE_TENANT: false,
      CREATE_DASHBOARD: false,
      READ_DASHBOARD: false,
      UPDATE_DASHBOARD: false,
      DELETE_DASHBOARD: false,
      SHARE_DASHBOARD: false,
      CREATE_PO: false,
      READ_PO: false,
      UPDATE_PO: false,
      DELETE_PO: false,
      APPROVE_PO: false,
      CREATE_CONTRACT: false,
      READ_CONTRACT: false,
      UPDATE_CONTRACT: false,
      DELETE_CONTRACT: false,
      APPROVE_CONTRACT: false,
      Category: false,
      UserManagement: false,
      WorkOrderSettings: false,
      AlarmsSettings: false,
      AssetsSettings: false,
      SpaceSettings: false,
      Controller: false,
      EnergyAnalytics: false,
      DataAdministration: false,
      Tenantbilling: false,
      readpermissionValue: null,
      updatepermissionValue: null,
      deletepermissionValue: null,
      invreqreadpermissionValue: null,
      invrequpdatepermissionValue: null,
      invreqdeletepermissionValue: null,
      pmreadpermissionValue: null,
      pmupdatepermissionValue: null,
      pmdeletepermissionValue: null,
      createpermission: false,
      deletepermission: false,
      updatepermission: false,
      readpermission: false,
      invrequpdatepermission: false,
      invreqreadpermission: false,
      pmcreatepermission: false,
      pmdeletepermission: false,
      pmupdatepermission: false,
      pmreadpermission: false,
      alarmreadpermission: false,
      alarmdeletepermission: false,
      inventoryreadownpermission: false,
      inventoryupatepermission: false,
      any: false,
      team: false,
      own: false,
      CREATE: 2048,
      READ: 512,
      READ_OWN: 16384,
      READ_TEAM: 8192,
      UPDATE: 1024,
      UPDATE_OWN: 65536,
      UPDATE_TEAM: 32768,
      DELETE: 4096,
      DELETE_TEAM: 131072,
      DELETE_OWN: 262144,
      ENRVIEWDASHBOARD: 8388608,
      CEDASHBOARD: 4194304,
      ENRVIEWREPORTS: 8388608,
      ENRCREATEEDITREPORT: 16777216,
      WOEXPORTREPORT: 67108864,
      ENREXPORTREPORT: 67108864,
      WOCHANGE: 524288,
      WOCLOSE: 1048576,
      WOTASK: 2097152,
      WOVIEWDASHBOARDS: 34359738368,
      WOCREATEEDITDASHBOARD: 4194304,
      WOVIEWREPORTS: 8388608,
      WOCREATEEDITREPORT: 16777216,
      WOCALENDAR: 134217728,
      WRAPPREJ: 268435456,
      PMCALENDER: 134217728,
      PMPLANNER: 1073741824,
      VIEWAPPROVAL: 68719476736,
      ACKALARM: 2147483648,
      CLEARALARM: 4294967296,
      ALMCREATEWO: 17179869184,
      ALMVIEWDASHBOARD: 34359738368,
      ALMCREATEEDITDASHBOARD: 4194304,
      ALMVIEWREPORTS: 8388608,
      ALMCREATEEDITREPORT: 16777216,
      ALMEXPREPORT: 67108864,
      SPACECREATE: 2048,
      SPACEIMPORT: 536870912,
      SPACEREAD: 512,
      SPACEUPDATE: 1024,
      SPACEREADING: 33554432,
      SPACEDELETE: 4096,
      ASSETCREATE: 2048,
      ASSETIMPORT: 536870912,
      ASSETREAD: 512,
      ASSETUPDATE: 1024,
      ASSETREADING: 33554432,
      ASSETDELETE: 4096,
      AdminCategory: 1,
      AdminUserManagement: 2,
      AdminWorkOrderSettings: 4,
      AdminAlarmsSettings: 8,
      AdminSpaceSettings: 16,
      AdminController: 32,
      AdminEnergyAnalytics: 64,
      AdminTenantbilling: 256,
      EXPORT: 137438953472,
      CONTROL: 4398046511104,
      readPerm: [],
      updatePerm: [],
      deletePerm: [],
      menu: [],
      wopermissionvalue: [],
      newrole: {
        name: null,
        description: null,
      },
      saving: false,
      permissions: [],
      woPermTotal: 0,
      pmPermTotal: 0,
      alamrPermTotal: 0,
      EnergyPermTotal: 0,
      spacePermTotal: 0,
      assetPermTotal: 0,
      adminPermTotal: 0,
      inventoryPermTotal: 0,
      alarmRulesPermTotal: 0,
      tenantPermTotal: 0,
      poPermTotal: 0,
      contractPermTotal: 0,
      invReqPermTotal: 0,
      newModulesList: [
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          displayName: 'Vendor',
          permissionsDisplayName: 'VENDOR PERMISSIONS',
          name: 'vendors',
          license: 'VENDOR',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          displayName: 'Quote',
          permissionsDisplayName: 'QUOTE PERMISSIONS',
          name: 'quote',
          license: 'QUOTATION',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          displayName: 'Client',
          permissionsDisplayName: 'CLIENT PERMISSIONS',
          name: 'client',
          license: 'CLIENT',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'PURCHASE REQUEST PERMISSIONS',
          displayName: 'Purchase Request',
          name: 'purchaserequest',
          license: 'PURCHASE',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'TERMS AND CONDITIONS PERMISSIONS',
          displayName: 'Terms And Conditions',
          name: 'termsandconditions',
          license: 'PURCHASE',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'TRANSFER REQUEST PERMISSIONS',
          displayName: 'Transfer Request',
          name: 'transferrequest',
          license: 'TRANSFER_REQUEST',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'REQUEST FOR QUOTATION PERMISSIONS',
          displayName: 'Request For Quotation',
          name: 'requestForQuotation',
          license: 'PURCHASE',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'TENANT UNIT PERMISSIONS',
          displayName: 'Tenant Unit',
          name: 'tenantunit',
          license: 'TENANTS',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'VISITS PERMISSIONS',
          displayName: 'Visits',
          name: 'visitorlog',
          license: 'VISITOR',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'INVITES PERMISSIONS',
          displayName: 'Invites',
          name: 'invitevisitor',
          license: 'VISITOR',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'VISITOR PERMISSIONS',
          displayName: 'Visitor',
          name: 'visitor',
          license: 'VISITOR',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'WATCHLIST PERMISSIONS',
          displayName: 'Watchlist',
          name: 'watchlist',
          license: 'VISITOR',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'SERVICE REQUESTS PERMISSIONS',
          displayName: 'Service Requests',
          name: 'serviceRequest',
          license: 'SERVICE_REQUEST',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'CONTACT DIRECTORY PERMISSIONS',
          displayName: 'Contact Directory',
          name: 'contactdirectory',
          license: 'COMMUNITY',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'DOCUMENTS PERMISSIONS',
          displayName: 'Documents',
          name: 'admindocuments',
          license: 'COMMUNITY',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'AUDIENCE PERMISSIONS',
          displayName: 'Audience',
          name: 'audience',
          license: 'COMMUNITY',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'ANNOUNCEMENTS PERMISSIONS',
          displayName: 'Announcements',
          name: 'announcement',
          license: 'COMMUNITY',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'NEIGHBOURHOOD PERMISSIONS',
          displayName: 'Neighbourhood',
          name: 'neighbourhood',
          license: 'COMMUNITY',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'DEALS AND OFFERS PERMISSIONS',
          displayName: 'Deals and Offers',
          name: 'dealsandoffers',
          license: 'COMMUNITY',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'NEWS AND INFORMATION PERMISSIONS',
          displayName: 'News and Information',
          name: 'newsandinformation',
          license: 'COMMUNITY',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'BUDGET PERMISSIONS',
          displayName: 'Budget',
          name: 'budget',
          license: 'BUDGET_MONITORING',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
          },
          permission: false,
          permissionsDisplayName: 'FACILITY PERMISSIONS',
          displayName: 'Facility',
          name: 'facility',
          license: 'FACILITY_BOOKING',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
          },
          permission: false,
          permissionsDisplayName: 'BOOKING PERMISSIONS',
          displayName: 'Booking',
          name: 'facilitybooking',
          license: 'FACILITY_BOOKING',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'INSPECTIONS PERMISSIONS',
          displayName: 'Inspections',
          name: 'inspectionResponse',
          license: 'INSPECTION',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'INSPECTION TEMPLATE PERMISSIONS',
          displayName: 'Inspection Template',
          name: 'inspectionTemplate',
          license: 'INSPECTION',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'INDUCTIONS PERMISSIONS',
          displayName: 'Inductions',
          name: 'inductionResponse',
          license: 'INDUCTION',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'INDUCTION TEMPLATE PERMISSIONS',
          displayName: 'Induction Template',
          name: 'inductionTemplate',
          license: 'INDUCTION',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'EMPLOYEE PERMISSIONS',
          displayName: 'Employee',
          name: 'employee',
          license: 'PEOPLE',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'TENANT CONTACTS PERMISSIONS',
          displayName: 'Tenant Contacts',
          name: 'tenantcontact',
          license: 'PEOPLE_CONTACTS',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'VENDOR CONTACTS PERMISSIONS',
          displayName: 'Vendor Contacts',
          name: 'vendorcontact',
          license: 'VENDOR',
        },
        {
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          permissionsDisplayName: 'INSURANCE PERMISSIONS',
          displayName: 'Insurance',
          name: 'insurance',
          license: 'VENDOR',
        },
      ],
    }
  },
  mounted() {
    Promise.all([this.loadCustomModulesList()]).then(() => {
      this.loadPermissionVal()
      this.preparePermissions()
      $('#container').on('scroll', this.onScroll)
      this.hideheader()
      this.active = this.$helpers.isLicenseEnabled('NEW_LAYOUT')
        ? 'space'
        : 'wo'
    })

    // Temporary permission for ConnectedApp grp icon for Investa(17), SanityTest(659) and icd(1)
    if ([17, 659, 1].includes(this.$org.id)) {
      this.newModulesList.push({
        values: { READ: 512 },
        permission: false,
        displayName: 'Connected Apps',
        permissionsDisplayName: 'CONNECTED APPS PERMISSIONS',
        name: 'connectedapps',
      })
    }
  },
  components: {
    ErrorBanner,
  },
  computed: {
    newlayout() {
      return this.$helpers.isLicenseEnabled('NEW_LAYOUT') ? true : false
    },
    modulesListBasedOnLicense() {
      return (
        this.newModulesList.filter(record => {
          if (record.license) {
            return this.$helpers.isLicenseEnabled(record.license)
          } else {
            return true
          }
        }) || []
      )
    },
  },
  watch: {
    role() {
      this.loadPermissionVal()
    },
  },
  methods: {
    hideheader() {
      this.focusheader = false
    },
    closeDialog() {
      this.$emit('onClose')
    },
    enablePermissions(mainPermission, enable) {
      if (mainPermission === 'workorderpermission') {
        this.createpermission = enable ? this.CREATE : 0
        this.readpermission = enable
        this.readpermissionValue = this.readPerm[0].value
        this.updatepermission = enable ? this.UPDATE : 0
        this.updatepermission = enable
        this.updatepermissionValue = this.updatePerm[0].value
        this.WO_CHANGE = enable ? this.WOCHANGE : 0
        this.WO_CLOSE = enable ? this.WOCLOSE : 0
        this.WO_TASK = enable ? this.WOTASK : 0
        this.deletepermission = enable
        this.deletepermissionValue = this.deletePerm[0].value
        this.woGeneral = enable
        this.WO_VIEW_DASHBOARDS = enable ? this.WOVIEWDASHBOARDS : 0
        this.WO_CREATE_EDIT_DASHBOARD = enable ? this.WOCREATEEDITDASHBOARD : 0
        this.WO_VIEW_REPORTS = enable ? this.WOVIEWREPORTS : 0
        this.WO_CREATE_EDIT_REPORT = enable ? this.WOCREATEEDITREPORT : 0
        this.WO_EXPORT_REPORT = enable ? this.WOEXPORTREPORT : 0
        this.WO_CALENDAR = enable ? this.WOCALENDAR : 0
        this.WO_VIEW_APPROVAL = enable ? this.VIEWAPPROVAL : 0
      } else if (mainPermission === 'pmpermission') {
        this.pmcreatepermission = enable ? this.CREATE : 0
        this.pmreadpermission = enable
        this.pmreadpermissionValue = this.readPerm[0].value
        this.pmupdatepermission = enable
        this.pmupdatepermissionValue = this.updatePerm[0].value
        this.PM_CALENDER = enable ? this.PMCALENDER : 0
        this.PM_PLANNER = enable ? this.PMPLANNER : 0
        this.pmdeletepermission = enable
        this.pmdeletepermissionValue = this.deletePerm[0].value
      } else if (mainPermission === 'alarmpermission') {
        this.alarmreadpermission = enable ? this.READ : 0
        this.ACK_ALARM = enable ? this.ACKALARM : 0
        this.CLEAR_ALARM = enable ? this.CLEARALARM : 0
        this.alarmdeletepermission = enable ? this.DELETE : 0
        this.generalalarm = enable
        this.ALM_CREATE_WO = enable ? this.ALMCREATEWO : 0
        this.ALM_VIEW_DASHBOARDS = enable ? this.ALMVIEWDASHBOARD : 0
        this.ALM_CREATE_EDIT_DASHBOARD = enable
          ? this.ALMCREATEEDITDASHBOARD
          : 0
        this.ALM_VIEW_REPORTS = enable ? this.ALMVIEWREPORTS : 0
        this.ALM_CREATE_EDIT_REPORT = enable ? this.ALMCREATEEDITREPORT : 0
        this.ALM_EXP_REPORT = enable ? this.ALMEXPREPORT : 0
      } else if (mainPermission === 'energypermission') {
        this.ENR_VIEW_DASHBOARDS = enable ? this.ENRVIEWDASHBOARD : 0
        this.ENR_CREATE_EDIT_DASHBOARD = enable ? this.CEDASHBOARD : 0
        this.ENR_VIEW_REPORTS = enable ? this.ENRVIEWREPORTS : 0
        this.ENR_CREATE_EDIT_REPORT = enable ? this.ENRCREATEEDITREPORT : 0
        this.ENR_EXPORT_REPORT = enable ? this.WOEXPORTREPORT : 0
      } else if (mainPermission === 'spacepermission') {
        this.CREATESPACE = enable ? this.SPACECREATE : 0
        this.IMPORTSPACE = enable ? this.SPACEIMPORT : 0
        this.SPACE_READ = enable ? this.SPACEREAD : 0
        this.SPACE_UPDATE = enable ? this.SPACEUPDATE : 0
        this.SPACE_READING = enable ? this.SPACEREADING : 0
        this.SPACE_DELETE = enable ? this.SPACEDELETE : 0
      } else if (mainPermission === 'assetpermission') {
        this.CREATEASSET = enable ? this.ASSETCREATE : 0
        this.IMPORTASSET = enable ? this.ASSETIMPORT : 0
        this.ASSET_READ = enable ? this.ASSETREAD : 0
        this.ASSET_UPDATE = enable ? this.ASSETUPDATE : 0
        this.ASSET_READING = enable ? this.ASSETREADING : 0
        this.ASSET_DELETE = enable ? this.ASSETDELETE : 0
        this.ASSET_CONTROL = enable ? this.CONTROL : 0
      } else if (mainPermission === 'inventorypermission') {
        this.CREATE_INVENTORY = enable ? this.CREATE : 0
        this.READ_INVENTORY = enable ? this.READ_INVENTORY : 0
        this.UPDATE_INVENTORY = enable ? this.UPDATE_INVENTORY : 0
        this.DELETE_INVENTORY = enable ? this.DELETE_INVENTORY : 0
        this.APPROVE_INVENTORY = enable ? this.WRAPPREJ : 0
      } else if (mainPermission === 'dashboardpermission') {
        this.CREATE_DASHBOARD = enable ? this.CREATE : 0
        this.READ_DASHBOARD = enable ? this.READ : 0
        this.UPDATE_DASHBOARD = enable ? this.UPDATE : 0
        this.DELETE_DASHBOARD = enable ? this.DELETE : 0
        this.SHARE_DASHBOARD = enable ? this.SHARE_DASHBOARD : 0
      } else if (mainPermission === 'tenantpermission') {
        this.CREATE_TENANT = enable ? this.CREATE : 0
        this.READ_TENANT = enable ? this.READ : 0
        this.UPDATE_TENANT = enable ? this.UPDATE : 0
        this.DELETE_TENANT = enable ? this.DELETE : 0
      } else if (mainPermission == 'purchaseorderpermission') {
        this.CREATE_PO = enable ? this.CREATE : 0
        this.READ_PO = enable ? this.READ : 0
        this.UPDATE_PO = enable ? this.UPDATE : 0
        this.DELETE_PO = enable ? this.DELETE : 0
        this.APPROVE_PO = enable ? this.WRAPPREJ : 0
      } else if (mainPermission == 'contractpermission') {
        this.CREATE_CONTRACT = enable ? this.CREATE : 0
        this.READ_CONTRACT = enable ? this.READ : 0
        this.UPDATE_CONTRACT = enable ? this.UPDATE : 0
        this.DELETE_CONTRACT = enable ? this.DELETE : 0
        this.APPROVE_CONTRACT = enable ? this.WRAPPREJ : 0
      } else if (mainPermission == 'alarmRulespermission') {
        this.CREATE_ALARM_RULES = enable ? this.CREATE : 0
        this.READ_ALARM_RULES = enable ? this.READ : 0
        this.UPDATE_ALARM_RULES = enable ? this.UPDATE : 0
        this.DELETE_ALARM_RULES = enable ? this.DELETE : 0
      } else if (mainPermission == 'inventoryRequestpermission') {
        this.invreqreadpermission = enable
        this.invreqreadpermissionValue = this.readPerm[0].value
        this.invrequpdatepermission = enable ? this.UPDATE : 0
        this.invrequpdatepermission = enable
        this.invrequpdatepermissionValue = this.updatePerm[0].value
        this.CREATE_INV_REQ = enable ? this.CREATE : 0
        this.DELETE_INV_REQ = enable ? this.DELETE : 0
        this.APPROVE_INV_REQ = enable ? this.WRAPPREJ : 0
      } else if (mainPermission === 'adminpermission') {
        this.Category = enable ? this.AdminCategory : 0
        this.UserManagement = enable ? this.AdminUserManagement : 0
        this.WorkOrderSettings = enable ? this.AdminWorkOrderSettings : 0
        this.AlarmsSettings = enable ? this.AdminAlarmsSettings : 0
        this.SpaceSettings = enable ? this.AdminSpaceSettings : 0
        this.Controller = enable ? this.AdminController : 0
        this.EnergyAnalytics = enable ? this.AdminEnergyAnalytics : 0
        this.Tenantbilling = enable ? this.AdminTenantbilling : 0
      }
    },
    enableAllPermissionsv2(moduleDetails, permission) {
      Object.keys(moduleDetails.values).forEach(key => {
        moduleDetails.values[key] = permission
          ? this.getDefaultTrueValue(key)
          : 0
      })
    },
    getDefaultTrueValue(key) {
      return {
        CREATE: this.CREATE,
        READ: this.READ,
        UPDATE: this.UPDATE,
        DELETE: this.DELETE,
        EXPORT: this.EXPORT,
      }[key]
    },
    loadPermissionVal() {
      this.newrole.name = this.role.name
      this.newrole.description = this.role.description
      if (!this.isNew) {
        let modulePermissions = {
          workorderrequest: 0,
          workorder: 0,
          space: 0,
          alarm: 0,
          energy: 0,
          asset: 0,
          planned: 0,
          setup: 0,
          inventory: 0,
          tenant: 0,
          purchaseorder: 0,
          contract: 0,
          dashboard: 0,
          alarmrules: 0,
          inventoryrequest: 0,
        }
        for (let perm in this.role.permissions) {
          modulePermissions[
            this.role.permissions[perm].moduleName
          ] = this.role.permissions[perm].permission
        }
        if (this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
          this.setDashboardPermission(modulePermissions.dashboard)
        }
        this.setWorkOrderPermissions(modulePermissions.workorder)
        this.setplannedPermission(modulePermissions.planned)
        this.setAssetPermission(modulePermissions.asset)
        this.setSpacePermission(modulePermissions.space)
        this.setEnergyPermission(modulePermissions.energy)
        this.setSetupPermission(modulePermissions.setup)
        this.setAlarmPermission(modulePermissions.alarm)
        this.setInventoryPermission(modulePermissions.inventory)
        this.setTenantPermission(modulePermissions.tenant)
        this.setPurhaseOrderPermission(modulePermissions.purchaseorder)
        this.setContractPermission(modulePermissions.contract)
        this.setAlarmRulesPermission(modulePermissions.alarmrules)
        this.setInventoryRequestPermission(modulePermissions.inventoryrequest)
        this.setNewModulesPermission(modulePermissions)
      } else {
        this.resetPermissions()
      }
    },
    setNewModulesPermission(modulePermissions) {
      this.newModulesList.forEach(moduleDetails => {
        let permission = modulePermissions[moduleDetails.name] || 0
        moduleDetails.permission = permission > 0
        Object.keys(moduleDetails.values).forEach(key => {
          moduleDetails.values[key] = this.andOperatorOnLong(
            this.$getProperty(this.$account, `appProps.permissions.${key}`, 0),
            permission
          )
        })
      })
    },
    setWorkOrderPermissions(permission) {
      this.workorderpermission = permission > 0
      this.createpermission =
        this.$account.appProps.permissions.CREATE & permission
      this.readpermissionValue =
        this.$account.appProps.permissions.READ & permission ||
        this.$account.appProps.permissions.READ_OWN & permission ||
        this.READ_TEAM & permission
      this.readpermission = this.readpermissionValue > 0
      this.updatepermissionValue =
        this.$account.appProps.permissions.UPDATE & permission ||
        this.$account.appProps.permissions.UPDATE_OWN & permission ||
        this.$account.appProps.permissions.UPDATE_TEAM & permission
      this.WO_CHANGE =
        this.$account.appProps.permissions.CHANGE_OWNERSHIP & permission
      this.WO_TASK =
        this.$account.appProps.permissions.ADD_UPDATE_DELETE_TASK & permission
      this.WO_CLOSE =
        this.$account.appProps.permissions.CLOSE_WORK_ORDER & permission
      this.updatepermission =
        this.updatepermissionValue > 0 ||
        this.WO_CHANGE > 0 ||
        this.WO_CLOSE > 0 ||
        this.WO_TASK > 0
      this.deletepermissionValue =
        this.$account.appProps.permissions.DELETE & permission ||
        this.$account.appProps.permissions.DELETE_TEAM & permission ||
        this.$account.appProps.permissions.DELETE_OWN & permission
      this.deletepermission = this.deletepermissionValue > 0
      this.WO_VIEW_DASHBOARDS = this.andOperatorOnLong(
        this.$account.appProps.permissions.VIEW_DASHBOARDS,
        permission
      )
      this.WO_CREATE_EDIT_DASHBOARD =
        this.$account.appProps.permissions.CREATE_EDIT_DASHBOARD & permission
      this.WO_VIEW_REPORTS =
        this.$account.appProps.permissions.VIEW_REPORTS & permission
      this.WO_CREATE_EDIT_REPORT =
        this.$account.appProps.permissions.CREATE_EDIT_REPORTS & permission
      this.WO_EXPORT_REPORT =
        this.$account.appProps.permissions.EXPORT_REPORTS & permission
      this.WO_CALENDAR =
        this.$account.appProps.permissions.CALENDAR & permission
      this.WO_VIEW_APPROVAL = this.andOperatorOnLong(
        this.$account.appProps.permissions.VIEW_APPROVAL,
        permission
      )
      this.woGeneral =
        this.WO_VIEW_DASHBOARDS > 0 ||
        this.WO_CREATE_EDIT_DASHBOARD > 0 ||
        this.WO_VIEW_REPORTS > 0 ||
        this.WO_CREATE_EDIT_REPORT > 0 ||
        this.WO_EXPORT_REPORT > 0 ||
        this.WO_CALENDAR > 0
    },
    setplannedPermission(permission) {
      this.pmpermission = permission > 0
      this.pmcreatepermission =
        this.$account.appProps.permissions.CREATE & permission
      this.pmreadpermissionValue =
        this.$account.appProps.permissions.READ & permission ||
        this.$account.appProps.permissions.READ_OWN & permission ||
        this.$account.appProps.permissions.READ_TEAM & permission
      this.pmreadpermission = this.pmreadpermissionValue > 0
      this.pmupdatepermissionValue =
        this.$account.appProps.permissions.UPDATE & permission ||
        this.$account.appProps.permissions.UPDATE_OWN & permission ||
        this.$account.appProps.permissions.UPDATE_TEAM & permission
      this.pmupdatepermission = this.pmupdatepermissionValue > 0
      this.pmdeletepermissionValue =
        this.$account.appProps.permissions.DELETE & permission ||
        this.$account.appProps.permissions.DELETE_TEAM & permission ||
        this.$account.appProps.permissions.DELETE_OWN & permission
      this.pmdeletepermission = this.pmdeletepermissionValue > 0
      this.PM_PLANNER =
        this.$account.appProps.permissions.PM_PLANNER & permission
      this.PM_CALENDER =
        this.$account.appProps.permissions.CALENDAR & permission
      this.pmPermGeneral = this.PM_PLANNER > 0 || this.PM_CALENDER > 0
    },
    setAlarmPermission(permission) {
      this.alarmpermission = permission > 0
      this.alarmreadpermission =
        this.$account.appProps.permissions.READ & permission
      this.ACK_ALARM = this.andOperatorOnLong(
        this.$account.appProps.permissions.ACKNOWLEDGE_ALARM,
        permission
      )
      this.CLEAR_ALARM = this.andOperatorOnLong(
        this.$account.appProps.permissions.CLEAR_ALARM,
        permission
      )
      this.alarmdeletepermission =
        this.$account.appProps.permissions.DELETE & permission
      this.ALM_VIEW_DASHBOARDS = this.andOperatorOnLong(
        this.$account.appProps.permissions.VIEW_DASHBOARDS,
        permission
      )
      this.ALM_CREATE_EDIT_DASHBOARD =
        this.$account.appProps.permissions.CREATE_EDIT_DASHBOARD & permission
      this.ALM_CREATE_WO = this.andOperatorOnLong(
        this.$account.appProps.permissions.CREATE_WO,
        permission
      )
      this.ALM_VIEW_REPORTS =
        this.$account.appProps.permissions.VIEW_REPORTS & permission
      this.ALM_CREATE_EDIT_REPORT =
        this.$account.appProps.permissions.CREATE_EDIT_REPORTS & permission
      this.ALM_EXP_REPORT =
        this.$account.appProps.permissions.EXPORT_REPORTS & permission
      this.generalalarm =
        this.ALM_VIEW_DASHBOARDS > 0 ||
        this.ALM_CREATE_EDIT_DASHBOARD > 0 ||
        this.ALM_CREATE_WO > 0 ||
        this.ALM_VIEW_REPORTS > 0 ||
        this.ALM_CREATE_EDIT_REPORT > 0 ||
        this.ALM_EXP_REPORT > 0
    },
    setAssetPermission(permission) {
      this.assetpermission = permission > 0
      this.ASSET_READ = this.$account.appProps.permissions.READ & permission
      this.ASSET_UPDATE = this.$account.appProps.permissions.UPDATE & permission
      this.ASSET_READING =
        this.$account.appProps.permissions.ADD_READING & permission
      this.IMPORTASSET = this.$account.appProps.permissions.IMPORT & permission
      this.CREATEASSET = this.$account.appProps.permissions.CREATE & permission
      this.ASSET_DELETE = this.$account.appProps.permissions.DELETE & permission
      this.ASSET_CONTROL = this.andOperatorOnLong(
        this.$account.appProps.permissions.CONTROL,
        permission
      )
    },
    setSpacePermission(permission) {
      this.spacepermission = permission > 0
      this.SPACE_READ = this.$account.appProps.permissions.READ & permission
      this.SPACE_UPDATE = this.$account.appProps.permissions.UPDATE & permission
      this.SPACE_READING =
        this.$account.appProps.permissions.ADD_READING & permission
      this.IMPORTSPACE = this.$account.appProps.permissions.IMPORT & permission
      this.CREATESPACE = this.$account.appProps.permissions.CREATE & permission
      this.SPACE_DELETE = this.$account.appProps.permissions.DELETE & permission
    },
    setEnergyPermission(permission) {
      this.energypermission = permission > 0
      this.ENR_VIEW_DASHBOARDS = this.andOperatorOnLong(
        this.$account.appProps.permissions.VIEW_DASHBOARDS,
        permission
      )
      this.ENR_CREATE_EDIT_DASHBOARD =
        this.$account.appProps.permissions.CREATE_EDIT_DASHBOARD & permission
      this.ENR_VIEW_REPORTS =
        this.$account.appProps.permissions.VIEW_REPORTS & permission
      this.ENR_CREATE_EDIT_REPORT =
        this.$account.appProps.permissions.CREATE_EDIT_REPORTS & permission
      this.ENR_EXPORT_REPORT =
        this.$account.appProps.permissions.EXPORT_REPORTS & permission
    },
    setSetupPermission(permission) {
      this.adminpermission = permission > 0
      this.Category = this.$account.appProps.permissions.GENERAL & permission
      this.UserManagement =
        this.$account.appProps.permissions.USER_MANAGEMENT & permission
      this.WorkOrderSettings =
        this.$account.appProps.permissions.WORKORDER_SETTINGS & permission
      this.EnergyAnalytics =
        this.$account.appProps.permissions.ENERGY_ANALYTICS & permission
      this.SpaceSettings =
        this.$account.appProps.permissions.SPACE_SETTINGS & permission
      this.AssetsSettings = this.andOperatorOnLong(
        this.$account.appProps.permissions.ASSET_SETTINGS,
        permission
      )
      this.DataAdministration =
        this.$account.appProps.permissions.DATA_ADMINISTRATION & permission
      this.Tenantbilling =
        this.$account.appProps.permissions.TENANT_BILLING & permission
      this.AlarmsSettings =
        this.$account.appProps.permissions.ALARM_SETTINGS & permission
      this.Controller =
        this.$account.appProps.permissions.CONTROLLER & permission
    },
    setInventoryPermission(permission) {
      this.inventorypermission = permission > 0
      this.READ_INVENTORY =
        this.$account.appProps.permissions.READ & permission ||
        this.$account.appProps.permissions.READ_OWN & permission
      this.inventoryreadownpermission = this.READ_INVENTORY > 0
      this.UPDATE_INVENTORY =
        this.$account.appProps.permissions.UPDATE & permission ||
        this.$account.appProps.permissions.UPDATE_OWN & permission
      this.inventoryupatepermission = this.UPDATE_INVENTORY > 0
      this.CREATE_INVENTORY =
        this.$account.appProps.permissions.CREATE & permission
      this.DELETE_INVENTORY =
        this.$account.appProps.permissions.DELETE & permission
      this.APPROVE_INVENTORY =
        this.$account.appProps.permissions.APPROVE_REJECT_WORKREQUEST &
        permission
    },
    setDashboardPermission(permission) {
      this.dashboardpermission = permission > 0
      this.READ_DASHBOARD = this.$account.appProps.permissions.READ & permission
      this.UPDATE_DASHBOARD =
        this.$account.appProps.permissions.UPDATE & permission
      this.CREATE_DASHBOARD =
        this.$account.appProps.permissions.CREATE & permission
      this.DELETE_DASHBOARD =
        this.$account.appProps.permissions.DELETE & permission
      this.SHARE_DASHBOARD = this.andOperatorOnLong(
        this.$account.appProps.permissions.SHARE_DASHBOARD,
        permission
      )
    },
    setTenantPermission(permission) {
      this.tenantpermission = permission > 0
      this.READ_TENANT = this.$account.appProps.permissions.READ & permission
      this.UPDATE_TENANT =
        this.$account.appProps.permissions.UPDATE & permission
      this.CREATE_TENANT =
        this.$account.appProps.permissions.CREATE & permission
      this.DELETE_TENANT =
        this.$account.appProps.permissions.DELETE & permission
    },
    setPurhaseOrderPermission(permission) {
      this.purchaseorderpermission = permission > 0
      this.READ_PO = this.$account.appProps.permissions.READ & permission
      this.UPDATE_PO = this.$account.appProps.permissions.UPDATE & permission
      this.CREATE_PO = this.$account.appProps.permissions.CREATE & permission
      this.DELETE_PO = this.$account.appProps.permissions.DELETE & permission
      this.APPROVE_PO =
        this.$account.appProps.permissions.APPROVE_REJECT_WORKREQUEST &
        permission
    },
    setContractPermission(permission) {
      this.contractpermission = permission > 0
      this.READ_CONTRACT = this.$account.appProps.permissions.READ & permission
      this.UPDATE_CONTRACT =
        this.$account.appProps.permissions.UPDATE & permission
      this.CREATE_CONTRACT =
        this.$account.appProps.permissions.CREATE & permission
      this.DELETE_CONTRACT =
        this.$account.appProps.permissions.DELETE & permission
      this.APPROVE_CONTRACT =
        this.$account.appProps.permissions.APPROVE_REJECT_WORKREQUEST &
        permission
    },
    setAlarmRulesPermission(permission) {
      this.alarmRulespermission = permission > 0
      this.READ_ALARM_RULES =
        this.$account.appProps.permissions.READ & permission
      this.UPDATE_ALARM_RULES =
        this.$account.appProps.permissions.UPDATE & permission
      this.CREATE_ALARM_RULES =
        this.$account.appProps.permissions.CREATE & permission
      this.DELETE_ALARM_RULES =
        this.$account.appProps.permissions.DELETE & permission
    },
    setInventoryRequestPermission(permission) {
      this.inventoryRequestpermission = permission > 0
      this.invreqreadpermissionValue =
        this.$account.appProps.permissions.READ & permission ||
        this.$account.appProps.permissions.READ_OWN & permission ||
        this.READ_TEAM & permission
      this.invreqreadpermission = this.invreqreadpermissionValue > 0
      this.invrequpdatepermissionValue =
        this.$account.appProps.permissions.UPDATE & permission ||
        this.$account.appProps.permissions.UPDATE_OWN & permission ||
        this.$account.appProps.permissions.UPDATE_TEAM & permission
      this.invrequpdatepermission = this.invrequpdatepermissionValue > 0

      this.CREATE_INV_REQ =
        this.$account.appProps.permissions.CREATE & permission
      this.DELETE_INV_REQ =
        this.$account.appProps.permissions.DELETE & permission
      this.APPROVE_INV_REQ =
        this.$account.appProps.permissions.APPROVE_REJECT_WORKREQUEST &
        permission
    },
    andOperatorOnLong(permVal, totalPermVal) {
      return this.$helpers.andOperatorOnLong(permVal, totalPermVal)
    },
    BitwiseAndLarge(value1, value2) {
      const maxInt32Bits = 4294967296 // 2^32

      const value1_highBits = value1 / maxInt32Bits
      const value1_lowBits = value1 % maxInt32Bits
      const value2_highBits = value2 / maxInt32Bits
      const value2_lowBits = value2 % maxInt32Bits
      return (
        (value1_highBits & value2_highBits) * maxInt32Bits +
        (value1_lowBits & value2_lowBits)
      )
    },
    resetPermissions() {
      this.dashboardpermission = false
      this.workorderpermission = false
      this.pmpermission = false
      this.alarmpermission = false
      this.energypermission = false
      this.spacepermission = false
      this.assetpermission = false
      this.adminpermission = false
      this.inventorypermission = false
      this.tenantpermission = false
      this.alarmRulespermission = false
      this.inventoryRequestpermission = false
      this.woGeneral = false
      this.WO_CHANGE = false
      this.WO_CLOSE = false
      this.WO_TASK = false
      this.WO_UPDATE_TASK = false
      this.WO_DELETE_TASK = false
      this.WO_ADD_COMMENT = false
      this.WO_VIEW_DASHBOARDS = false
      this.WO_CREATE_EDIT_DASHBOARD = false
      this.WO_VIEW_REPORTS = false
      this.WO_CREATE_EDIT_REPORT = false
      this.WO_EXPORT_REPORT = false
      this.WO_CALENDAR = false
      this.pmPermGeneral = false
      this.PM_CALENDER = false
      this.PM_PLANNER = false
      this.WO_VIEW_APPROVAL = false
      this.ACK_ALARM = false
      this.CLEAR_ALARM = false
      this.generalalarm = false
      this.ALM_CREATE_WO = false
      this.ALM_VIEW_DASHBOARDS = false
      this.ALM_CREATE_EDIT_DASHBOARD = false
      this.ALM_VIEW_REPORTS = false
      this.ALM_CREATE_EDIT_REPORT = false
      this.ALM_EXP_REPORT = false
      this.ENR_VIEW_DASHBOARDS = false
      this.ENR_CREATE_EDIT_DASHBOARD = false
      this.ENR_VIEW_REPORTS = false
      this.ENR_CUS_DASHBOARD = false
      this.ENR_CREATE_EDIT_REPORT = false
      this.ENR_EXPORT_REPORT = false
      this.CREATESPACE = false
      this.IMPORTSPACE = false
      this.SPACE_READ = false
      this.SPACE_UPDATE = false
      this.SPACE_READING = false
      this.SPACE_DELETE = false
      this.CREATEASSET = false
      this.IMPORTASSET = false
      this.ASSET_READ = false
      this.ASSET_UPDATE = false
      this.ASSET_READING = false
      this.ASSET_DELETE = false
      this.ASSET_CONTROL = false
      this.CREATE_ALARM_RULES = false
      this.READ_ALARM_RULES = false
      this.UPDATE_ALARM_RULES = false
      this.DELETE_ALARM_RULES = false
      this.CREATE_INV_REQ = false
      this.READ_INV_REQ = false
      this.UPDATE_INV_REQ = false
      this.DELETE_INV_REQ = false
      this.APPROVE_INV_REQ = false

      this.Category = false
      this.UserManagement = false
      this.WorkOrderSettings = false
      this.AlarmsSettings = false
      this.AssetsSettings = false
      this.SpaceSettings = false
      this.Controller = false
      this.EnergyAnalytics = false
      this.DataAdministration = false
      this.Tenantbilling = false
      this.readpermissionValue = null
      this.updatepermissionValue = null
      this.deletepermissionValue = null
      this.pmreadpermissionValue = null
      this.pmupdatepermissionValue = null
      this.pmdeletepermissionValue = null
      this.createpermission = false
      this.deletepermission = false
      this.updatepermission = false
      this.readpermission = false
      this.pmcreatepermission = false
      this.pmdeletepermission = false
      this.pmupdatepermission = false
      this.pmreadpermission = false
      this.alarmreadpermission = false
      this.alarmdeletepermission = false
      ;(this.CREATE_INVENTORY = false),
        (this.READ_INVENTORY = false),
        (this.UPDATE_INVENTORY = false),
        (this.DELETE_INVENTORY = false),
        (this.APPROVE_INVENTORY = false),
        (this.CREATE_TENANT = false),
        (this.READ_TENANT = false),
        (this.UPDATE_TENANT = false),
        (this.DELETE_TENANT = false),
        (this.CREATE_DASHBOARD = false),
        (this.READ_DASHBOARD = false),
        (this.UPDATE_DASHBOARD = false),
        (this.DELETE_DASHBOARD = false),
        (this.SHARE_DASHBOARD = false),
        (this.inventoryupatepermission = false),
        (this.inventoryreadownpermission = false)

      this.newModulesList.forEach(moduleDetails => {
        moduleDetails.permission = false
        Object.keys(moduleDetails.values).forEach(key => {
          moduleDetails.values[key] = 0
        })
      })
    },
    scrollTo(elm) {
      this.$scrollTo(elm, 500, {
        container: '#container',
        duration: 500,
        easing: 'ease',
      })
    },
    option(option) {
      let scrolledTo = '#' + option
      this.scrollTo(scrolledTo)
    },
    onScroll() {
      if ($('#container').position().top - $('#wo').position().top >= 0) {
        this.active = 'wo'
      }
      if ($('#container').position().top - $('#pm').position().top >= 0) {
        this.active = 'pm'
      }
      if ($('#container').position().top - $('#alarm').position().top >= 0) {
        this.active = 'alarm'
      }
      if (
        $('#container').position().top - $('#alarmRules').position().top >=
        0
      ) {
        this.active = 'alarmRules'
      }
      if ($('#container').position().top - $('#energy').position().top >= 0) {
        this.active = 'energy'
      }
      if ($('#container').position().top - $('#space').position().top >= 0) {
        this.active = 'space'
      }
      if ($('#container').position().top - $('#asset').position().top >= -5) {
        this.active = 'asset'
      }
      if ($('#container').position().top - $('#admin').position().top >= -5) {
        this.active = 'admin'
      }
      if (
        $('#container').position().top - $('#inventory').position().top >=
        -5
      ) {
        this.active = 'inventory'
      }
      if ($('#container').position().top - $('#tenant').position().top >= -5) {
        this.active = 'tenant'
      }
      if (
        $('#container').position().top -
          $('#inventoryRequest').position().top >=
        -5
      ) {
        this.active = 'inventoryRequest'
      }
      this.modulesListBasedOnLicense.forEach(moduleDetails => {
        if (
          $('#container').position().top -
            $(`#${moduleDetails.name}`).position().top >=
          -5
        ) {
          this.active = moduleDetails.name
        }
      })
    },
    preparePermissions() {
      this.readPerm = [
        {
          value: this.READ,
          label: 'All',
        },
        {
          value: this.READ_TEAM,
          label: 'Team',
        },
        {
          value: this.READ_OWN,
          label: 'Self',
        },
      ]
      this.updatePerm = [
        {
          value: this.UPDATE,
          label: 'All',
        },
        {
          value: this.UPDATE_TEAM,
          label: 'Team',
        },
        {
          value: this.UPDATE_OWN,
          label: 'Self',
        },
      ]
      this.deletePerm = [
        {
          value: this.DELETE,
          label: 'All',
        },
        {
          value: this.DELETE_TEAM,
          label: 'Team',
        },
        {
          value: this.DELETE_OWN,
          label: 'Self',
        },
      ]
    },
    close() {
      this.$emit('canceled')
    },
    activeheader() {
      this.focusheader = true
    },
    getPermValue(permissionValue) {
      let self = this
      let readView = self.readPerm
      let readObj = readView.find(view => view.value === permissionValue)
      let permVal = readObj.label
      let updateView = self.updatePerm
      let deleteView = self.deletePerm
      if (permVal === 'ANY') {
        let updateObj = updateView.find(view => view.label === 'ANY')
        let deleteObj = deleteView.find(view => view.label === 'ANY')
        updateObj.disabled = false
        deleteObj.disabled = false
        updateObj = updateView.find(view => view.label === 'TEAM')
        deleteObj = deleteView.find(view => view.label === 'TEAM')
        updateObj.disabled = false
        deleteObj.disabled = false
      } else if (permVal === 'TEAM') {
        let updateObj = updateView.find(view => view.label === 'ANY')
        let deleteObj = deleteView.find(view => view.label === 'ANY')
        updateObj.disabled = true
        deleteObj.disabled = true
        updateObj = updateView.find(view => view.label === 'TEAM')
        deleteObj = deleteView.find(view => view.label === 'TEAM')
        updateObj.disabled = false
        deleteObj.disabled = false
      } else if (permVal === 'OWN') {
        let updateobj = updateView.find(view => view.label === 'ANY')
        let deleteObj = deleteView.find(view => view.label === 'ANY')
        updateobj.disabled = true
        deleteObj.disabled = true
        updateobj = updateView.find(view => view.label === 'TEAM')
        deleteObj = deleteView.find(view => view.label === 'TEAM')
        updateobj.disabled = true
        deleteObj.disabled = true
      }
    },
    inactiveWo() {
      if (this.workorderpermission === false) {
        let elem = document.getElementById('wo')
        elem.classList.add('ínactivelist')
      }
    },
    cancel() {
      this.resetPermissions()
      this.$emit('canceled')
    },
    validation(rule) {
      this.error = false
      this.errorText = ''
      if (!rule.name) {
        this.errorText = 'Please enter the Name'
        this.error = true
      } else {
        this.errorText = ''
        this.error = false
      }
    },
    async loadCustomModulesList() {
      if (this.$constants.isCustomModulePermissionsEnabled(this.$org.id)) {
        let { data, error } = await API.get('/v2/module/list')
        if (error) {
          this.$message.error(
            error.message || 'Error occured while fetching custom modules list'
          )
        } else {
          this.initPermissionsForCustomModules(data?.moduleList || [])
        }
      } else {
        return
      }
    },
    initPermissionsForCustomModules(modules = []) {
      modules.forEach(record => {
        this.newModulesList.push({
          values: {
            CREATE: 0,
            READ: 0,
            UPDATE: 0,
            DELETE: 0,
            EXPORT: 0,
          },
          permission: false,
          displayName: record.displayName,
          permissionsDisplayName:
            record.displayName.toUpperCase() + ' PERMISSIONS',
          name: record.name,
        })
      })
    },
    newRole() {
      let self = this
      this.validation(self.newrole)
      if (this.error) {
        return
      }
      self.saving = true
      if (self.workorderpermission) {
        self.woPermTotal = self.createpermission
        if (self.readpermission) {
          self.woPermTotal += self.readpermissionValue
        }
        if (self.updatepermission) {
          self.woPermTotal +=
            self.updatepermissionValue +
            self.WO_CHANGE +
            self.WO_CLOSE +
            self.WO_TASK
        }
        if (self.deletepermission) {
          self.woPermTotal += self.deletepermissionValue
        }
        if (self.woGeneral) {
          self.woPermTotal +=
            self.WO_VIEW_DASHBOARDS +
            self.WO_CREATE_EDIT_DASHBOARD +
            self.WO_VIEW_REPORTS +
            self.WO_EXPORT_REPORT +
            self.WO_CREATE_EDIT_REPORT +
            self.WO_CALENDAR
        }
        if (self.WO_VIEW_APPROVAL) {
          self.woPermTotal += self.VIEWAPPROVAL
        }
        if (self.woPermTotal > 0) {
          self.permissions.push({
            moduleName: 'workorder',
            permission: self.woPermTotal,
          })
        }
      }
      if (self.pmpermission) {
        self.pmPermTotal = self.pmcreatepermission
        if (self.pmreadpermission) {
          self.pmPermTotal += self.pmreadpermissionValue
        }
        if (self.pmupdatepermission) {
          self.pmPermTotal += self.pmupdatepermissionValue
        }
        if (self.pmdeletepermission) {
          self.pmPermTotal += self.pmdeletepermissionValue
        }
        if (self.PM_PLANNER) {
          self.pmPermTotal += self.PMPLANNER
        }
        if (self.PM_CALENDER) {
          self.pmPermTotal += self.PMCALENDER
        }
        if (self.pmPermTotal > 0) {
          self.permissions.push({
            moduleName: 'planned',
            permission: self.pmPermTotal,
          })
        }
      }
      if (self.alarmpermission) {
        self.alamrPermTotal +=
          self.alarmreadpermission +
          self.alarmdeletepermission +
          self.ACK_ALARM +
          self.CLEAR_ALARM
        if (self.generalalarm) {
          self.alamrPermTotal +=
            self.ALM_CREATE_WO +
            self.ALM_VIEW_DASHBOARDS +
            self.ALM_CREATE_EDIT_DASHBOARD +
            self.ALM_VIEW_REPORTS +
            self.ALM_CREATE_EDIT_REPORT +
            self.ALM_EXP_REPORT
        }
        if (self.alamrPermTotal > 0) {
          self.permissions.push({
            moduleName: 'alarm',
            permission: self.alamrPermTotal,
          })
        }
      }
      if (self.energypermission) {
        self.EnergyPermTotal =
          self.ENR_VIEW_DASHBOARDS +
          self.ENR_CREATE_EDIT_DASHBOARD +
          self.ENR_VIEW_REPORTS +
          self.ENR_CREATE_EDIT_REPORT +
          self.ENR_EXPORT_REPORT
        if (self.EnergyPermTotal > 0) {
          self.permissions.push({
            moduleName: 'energy',
            permission: self.EnergyPermTotal,
          })
        }
      }
      if (self.spacepermission) {
        self.spacePermTotal =
          self.CREATESPACE +
          self.IMPORTSPACE +
          self.SPACE_READ +
          self.SPACE_READING +
          self.SPACE_UPDATE +
          self.SPACE_DELETE
        if (self.spacePermTotal > 0) {
          self.permissions.push({
            moduleName: 'space',
            permission: self.spacePermTotal,
          })
        }
      }
      if (self.assetpermission) {
        self.assetPermTotal =
          self.CREATEASSET +
          self.IMPORTASSET +
          self.ASSET_READ +
          self.ASSET_UPDATE +
          self.ASSET_READING +
          self.ASSET_DELETE +
          self.ASSET_CONTROL
        if (self.assetPermTotal > 0) {
          self.permissions.push({
            moduleName: 'asset',
            permission: self.assetPermTotal,
          })
        }
      }
      if (self.adminpermission) {
        self.adminPermTotal =
          self.Category +
          self.UserManagement +
          self.WorkOrderSettings +
          self.AlarmsSettings +
          self.AssetsSettings +
          self.SpaceSettings +
          self.Controller +
          self.EnergyAnalytics +
          self.DataAdministration +
          self.Tenantbilling
        if (self.adminPermTotal > 0) {
          self.permissions.push({
            moduleName: 'setup',
            permission: self.adminPermTotal,
          })
        }
      }
      if (self.inventorypermission) {
        self.inventoryPermTotal =
          self.CREATE_INVENTORY +
          self.READ_INVENTORY +
          self.UPDATE_INVENTORY +
          self.DELETE_INVENTORY +
          self.APPROVE_INVENTORY
        if (self.inventoryPermTotal > 0) {
          self.permissions.push({
            moduleName: 'inventory',
            permission: self.inventoryPermTotal,
          })
        }
      }
      if (self.alarmRulespermission) {
        self.alarmRulesPermTotal =
          self.CREATE_ALARM_RULES +
          self.READ_ALARM_RULES +
          self.UPDATE_ALARM_RULES +
          self.DELETE_ALARM_RULES
        if (self.alarmRulesPermTotal > 0) {
          self.permissions.push({
            moduleName: 'alarmrules',
            permission: self.alarmRulesPermTotal,
          })
        }
      }
      if (self.inventoryRequestpermission) {
        if (self.invreqreadpermission) {
          self.invReqPermTotal += self.invreqreadpermissionValue
        }
        if (self.invrequpdatepermission) {
          self.invReqPermTotal += self.invrequpdatepermissionValue
        }
        self.invReqPermTotal +=
          self.CREATE_INV_REQ + self.DELETE_INV_REQ + self.APPROVE_INV_REQ

        if (self.invReqPermTotal > 0) {
          self.permissions.push({
            moduleName: 'inventoryrequest',
            permission: self.invReqPermTotal,
          })
        }
      }
      if (self.dashboardpermission) {
        self.dashboardpermission =
          self.CREATE_DASHBOARD +
          self.READ_DASHBOARD +
          self.UPDATE_DASHBOARD +
          self.DELETE_DASHBOARD +
          self.SHARE_DASHBOARD
        if (self.dashboardpermission > 0) {
          self.permissions.push({
            moduleName: 'dashboard',
            permission: self.dashboardpermission,
          })
        }
      }
      if (self.tenantpermission) {
        self.tenantPermTotal =
          self.CREATE_TENANT +
          self.READ_TENANT +
          self.UPDATE_TENANT +
          self.DELETE_TENANT
        if (self.tenantPermTotal > 0) {
          self.permissions.push({
            moduleName: 'tenant',
            permission: self.tenantPermTotal,
          })
        }
      }
      if (self.purchaseorderpermission) {
        self.poPermTotal =
          self.CREATE_PO +
          self.READ_PO +
          self.UPDATE_PO +
          self.DELETE_PO +
          self.APPROVE_PO
        if (self.poPermTotal > 0) {
          self.permissions.push({
            moduleName: 'purchaseorder',
            permission: self.poPermTotal,
          })
        }
      }
      if (self.contractpermission) {
        self.contractPermTotal =
          self.CREATE_CONTRACT +
          self.READ_CONTRACT +
          self.UPDATE_CONTRACT +
          self.DELETE_CONTRACT +
          self.APPROVE_CONTRACT
        if (self.contractPermTotal > 0) {
          self.permissions.push({
            moduleName: 'contract',
            permission: self.contractPermTotal,
          })
        }
      }
      this.modulesListBasedOnLicense.forEach(moduleDetails => {
        if (moduleDetails.permission) {
          let totalValue = 0
          Object.keys(moduleDetails.values).forEach(key => {
            totalValue += moduleDetails.values[key]
          })
          if (totalValue > 0) {
            this.permissions.push({
              moduleName: moduleDetails.name,
              permission: totalValue,
            })
          }
        }
      })
      let formData = {}

      if (this.isNew) {
        formData = {
          role: {
            description: self.newrole.description,
            name: self.newrole.name,
          },
          permissions: self.permissions,
        }

        API.post('/setup/addRole', formData).then(({ error }) => {
          if (error) {
            this.$message.error('Error Occurred')
          } else {
            this.$message.success(' Role created successfully')
            this.resetPermissions()
            this.$emit('saved')
            this.closeDialog()
          }
          this.saving = false
        })

        this.$dialog.notify('Successfully New role created')
      } else {
        formData = {
          role: {
            description: self.newrole.description,
            name: self.newrole.name,
            roleId: self.role.roleId,
          },
          roleApp: [{ applicationId: this.appId }],
          permissions: self.permissions,
        }
        if (!this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
          let data = {
            moduleName: 'dashboard',
            permission: 1048064,
          }
          self.permissions.push(data)
        }
        API.post('/setup/updaterole', formData).then(({ error }) => {
          if (error) {
            this.$message.error('Error Occurred')
          } else {
            this.$message.success(' Role updated successfully')
            this.resetPermissions()
            this.$emit('saved')
            this.closeDialog()
          }
          this.saving = false
        })
      }
    },
  },
}
</script>
<style>
.styled-select select {
  background: transparent;
  border: none;
  font-size: 14px;
  height: 29px;
  padding: 5px;
  width: 100%;
}
.rounded {
  -webkit-border-radius: 20px;
  -moz-border-radius: 20px;
  border-radius: 20px;
}
.green {
  background-color: #f2fbeb;
}
.fc-dashed-line {
  border-top: 1px dashed #dce0e0;
  width: 100%;
}
.container_class {
  width: 70%;
  display: inline-block;
  overflow-y: scroll;
  height: 100vh;
  border-top: 1px solid #edeeef;
}
.fc-perm-label {
  padding: 5px;
}
.roletitle {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.8px;
  color: #ef4f8f;
}
.fc-model-close {
  display: none !important;
}
#newrole .el-input .el-input__inner,
#newrole .el-textarea .el-textarea__inner {
  border: none !important;
  padding-left: 0 !important;
}
#newrole .primarybutton.el-button {
  width: 125px;
  height: 40px;
  border-radius: 4px;
  background-color: #39b2c2;
  border-radius: 4px;
  float: right;
}

#newrole ul {
  list-style-type: none;
  margin-top: 0px;
}
#list li {
  font-size: 14px;
  line-height: 2.71;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
  padding-left: 5px;
}
#list li.activelist {
  font-size: 14px;
  font-weight: bold;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
  margin-left: -11px;
}
#list li.inactivelist {
  font-size: 14px;
  line-height: 2.71;
  letter-spacing: 0.5px;
  text-align: left;
  color: #8e8e8e;
}
#list li.activecircle::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #ef4f8f;
}
.permission {
  height: 50px;
  background-color: #fcfcfc;
  width: 100%;
  border-bottom: 1px solid #edeeef;
  border-top: 1px solid #edeeef;
  margin-top: -1px;
}
.operation {
  font-size: 14px;
  font-weight: 500;
  line-height: 2.71;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
  width: 40%;
  display: inline-block;
  padding: 7px;
}
.suboperation {
  font-size: 14px;
  line-height: 2.71;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
  width: 40%;
  display: inline-block;
  padding-left: 30px;
}
#newrole .splitter {
  border-bottom: solid 1px #e1e9ed;
  width: 93%;
}
#newrole .splitter:last-child {
  border-bottom: none;
  width: 93%;
  padding-bottom: 15px;
}
#newrole .loopsplitter {
  border-bottom: solid 1px #e1e9ed;
  width: 93%;
  padding-bottom: 15px;
}
#newrole .el-radio__input.is-checked .el-radio__inner {
  border-color: #39b2c2;
  background: #ffffff;
}
#newrole .el-radio__inner::after {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #39b2c2;
}
#newrole .el-radio__inner {
  border: 1px solid #d8dce5;
  border-radius: 100%;
  width: 18px;
  height: 18px;
  background-color: #fff;
  cursor: pointer;
  box-sizing: border-box;
}
#newrole .el-radio__label {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
  font-weight: normal;
}
#updatelist div {
  display: inline-block;
}
.pB30 {
  padding-bottom: 30px;
}
.headerswitch {
  padding-right: 7px;
  color: #666666;
  letter-spacing: 0.4px;
  font-size: 13px;
}
#descriptionbox.el-textarea__inner {
  display: block;
  resize: none;
  line-height: 1.5;
  width: 100%;
  color: #666666;
  background-color: #fff;
  letter-spacing: 0.5px;
  font-size: 14px;
  border-bottom: 1px solid transparent !important;
  border: none !important;
  padding-left: 0 !important;
}
.inactiveheader {
  margin-top: 0px;
  padding-top: 26px;
  padding-left: 39px;
}
.activeformheader #descriptionbox.el-textarea__inner {
  min-height: 80px !important;
}
#rolename.el-input__inner {
  background-color: #fff;
  color: #000000;
  display: inline-block;
  line-height: 1;
  letter-spacing: 0.6px;
  font-size: 18px;
}
.activeformheader {
  width: 834px;
  height: 171px;
  background-color: #ffffff;
  box-shadow: 0 2px 19px 0 rgba(0, 0, 0, 0.5);
  margin-top: 0px;
  padding-top: 26px;
  padding-left: 39px;
  position: absolute;
  z-index: 1;
  width: 100%;
}
.create-record.modal-content {
  position: relative;
  background: #fff;
  box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2),
    0 24px 38px 3px rgba(0, 0, 0, 0.14), 0 9px 46px 8px rgba(0, 0, 0, 0.12);
  overflow-y: hidden !important;
  min-width: 280px;
  max-height: 80vh;
}
.create-record {
  width: 60% !important;
  height: 100% !important;
  max-height: 100% !important;
}
.fixedrow {
  margin-top: 107px;
}
.newheight {
  height: 100vh;
}
.side-list-height {
  height: calc(100vh - 100px);
}
</style>
