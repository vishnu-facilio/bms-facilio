<template>
  <div>
    <div v-if="loading">
      <Spinner :show="loading"></Spinner>
    </div>
    <div v-else class="height100 width100">
      <div class="setting-header2">
        <div class="setting-title-block">
          <div class="setting-form-title">
            {{ connectedApp.name }}
            <el-tooltip
              class="item"
              effect="dark"
              :content="
                $t('common._common.link_name_connectedapp', {
                  connectedApp: connectedApp.linkName,
                })
              "
              placement="bottom"
            >
              <i class="el-icon-info capp-linkname"></i>
            </el-tooltip>
          </div>
          <div class="heading-description">{{ connectedApp.description }}</div>
        </div>
        <div class="action-btn setting-page-btn controller-right-actions">
          <el-button
            v-if="activeTabName === 'widgets'"
            type="primary"
            class="setup-el-btn"
            @click="openWidgetDialog()"
            >{{ $t('common._common.add_widget') }}</el-button
          >
          <el-button
            v-else-if="activeTabName === 'variables'"
            type="primary"
            class="setup-el-btn"
            @click="openVariableDialog()"
            >{{ $t('common.products.add_variable') }}</el-button
          >

          <el-button
            v-else-if="activeTabName === 'connectors'"
            type="primary"
            class="setup-el-btn"
            @click="showCreateNewConnector = true"
            >{{ $t('common.products.add_connector') }}</el-button
          >

          <el-button
            v-else
            type="primary"
            class="setup-el-btn"
            @click="showCreateNewDialog = true"
            >{{ $t('common.header.edit_connected_app') }}</el-button
          >
        </div>
      </div>
      <div class="container-scroll">
        <div class="row setting-Rlayout">
          <el-tabs v-model="activeTabName" class="width100">
            <el-tab-pane
              :label="$t('common.products.preferences')"
              name="details"
            >
              <div class="visitor-setting-con-width mT20">
                <div class="visitor-hor-card scale-up-left">
                  <el-row class="flex-middle">
                    <el-col :span="22">
                      <div class="fc-black-15 bold">
                        {{ $t('common.header.base_url') }}
                      </div>
                      <div class="fc-grey4-13 pT5">
                        {{ connectedApp.productionBaseUrl }}
                      </div>
                    </el-col>
                    <el-col :span="2" class="text-right">
                      <div>
                        <div class=""></div>
                      </div>
                    </el-col>
                  </el-row>
                </div>

                <div class="visitor-hor-card scale-up-left">
                  <el-row class="flex-middle">
                    <el-col :span="22">
                      <div class="fc-black-15 bold">
                        {{ $t('common.header.show_in_launcher') }}
                      </div>
                      <div class="fc-grey4-13 pT5">
                        {{
                          $t(
                            'common.header.show_this_connected_app_application'
                          )
                        }}
                      </div>
                    </el-col>
                    <el-col :span="2" class="text-right">
                      <div>
                        <div class="">
                          <el-switch
                            v-model="connectedApp.showInLauncher"
                            @change="updateConnectedAppShowInLauncher"
                          ></el-switch>
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </div>

                <div class="visitor-hor-card scale-up-left">
                  <el-row class="flex-middle">
                    <el-col :span="22">
                      <div class="fc-black-15 bold">
                        {{ $t('common.wo_report.app_status') }}
                      </div>
                      <div class="fc-grey4-13 pT5">
                        {{
                          $t(
                            'common.wo_report.activate_deactivate_this_connected_app'
                          )
                        }}
                      </div>
                    </el-col>
                    <el-col :span="2" class="text-right">
                      <div>
                        <div class="">
                          <el-switch
                            v-model="connectedApp.isActive"
                            @change="updateConnectedAppIsActive"
                          ></el-switch>
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </div>

                <!-- <div class="visitor-hor-card scale-up-left">
                  <el-row class="flex-middle">
                    <el-col :span="22">
                      <div class="fc-black-15 bold">
                        {{ $t('common.wo_report.app_access') }}
                      </div>
                      <div class="fc-grey4-13 pT5">
                        {{
                          $t(
                            'common._common.set_users_roles_that_should_have_access'
                          )
                        }}
                      </div>
                    </el-col>
                    <el-col :span="2" class="text-right">
                      <div>
                        <div class="">
                          <el-button
                            type="primary"
                            class="el-button setup-el-btn el-button--primary scale-up-left"
                            >{{ $t('common._common.edit') }}</el-button
                          >
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </div> -->

                <div class="visitor-hor-card scale-up-left">
                  <el-row class="flex-middle">
                    <el-col :span="18">
                      <div class="fc-black-15 bold">
                        {{ $t('common._common.saml') }}
                      </div>
                      <div class="fc-grey4-13 pT5">
                        {{
                          $t('common._common.to_enable_single_signon_facilio')
                        }}
                      </div>
                    </el-col>
                    <el-col :span="6" class="text-right">
                      <div>
                        <div class="">
                          <el-button
                            v-if="!connectedApp.connectedAppSAML"
                            type="primary"
                            class="el-button setup-el-btn el-button--primary scale-up-left"
                            @click="openSamlConfigure"
                            >{{
                              $t('common._common.configure_saml')
                            }}</el-button
                          >
                          <div v-else>
                            <i
                              class="el-icon-edit pointer"
                              @click="openSamlConfigure"
                            ></i>
                            &nbsp;&nbsp;
                            <i
                              class="el-icon-delete pointer"
                              @click="deleteSaml"
                            ></i>
                          </div>
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane
              :label="$t('common.dashboard._widgets')"
              name="widgets"
            >
              <div class="">
                <div
                  class="visitor-hor-card scale-up-left"
                  v-if="
                    !connectedApp.connectedAppWidgetsList ||
                      connectedApp.connectedAppWidgetsList.length === 0
                  "
                >
                  <div class="text-center">
                    {{ $t('common.products.no_widget_available') }}
                  </div>
                </div>

                <div v-else class="mT20">
                  <div
                    class="visitor-hor-card scale-up-left"
                    v-for="widget in connectedApp.connectedAppWidgetsList"
                    :key="widget.id"
                  >
                    <el-row class="flex-middle">
                      <el-col :span="10">
                        <div
                          class="agent-active-section flex-middle"
                          style="align-items: flex-start;"
                        >
                          <div class="mL10">
                            <div class="label-txt3-14">
                              {{ widget.widgetName }}
                              <el-tooltip
                                class="item"
                                effect="dark"
                                :content="
                                  $t(
                                    'common._common.link_name_widget_linkname',
                                    { widget: widget.linkName }
                                  )
                                "
                                placement="bottom"
                              >
                                <i class="el-icon-info capp-linkname"></i>
                              </el-tooltip>
                            </div>
                            <div class="label-txt3-12 mT10 flex-middle">
                              {{
                                widget.resourcePath
                                  ? widget.resourcePath
                                  : '---'
                              }}
                            </div>
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="8">
                        <div class="label-txt3-14">
                          {{
                            widget.entityTypeEnum
                              ? entityTypes.find(
                                  et => et.value === widget.entityTypeEnum
                                ).label
                              : '---'
                          }}
                        </div>
                      </el-col>
                      <el-col>
                        <i
                          class="el-icon-edit pointer"
                          @click="openWidgetDialog(widget)"
                        ></i>
                        &nbsp;&nbsp;
                        <i
                          class="el-icon-delete pointer"
                          @click="deleteWidget(widget)"
                        ></i>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane
              :label="$t('common._common._variables')"
              name="variables"
            >
              <div class="">
                <div
                  class="visitor-hor-card scale-up-left"
                  v-if="
                    !connectedApp.variablesList ||
                      connectedApp.variablesList.length === 0
                  "
                >
                  <div class="text-center">
                    {{ $t('common.products.no_variables_available') }}
                  </div>
                </div>

                <div v-else class="mT20">
                  <div
                    class="visitor-hor-card scale-up-left"
                    v-for="variable in connectedApp.variablesList"
                    :key="variable.id"
                  >
                    <el-row class="flex-middle">
                      <el-col :span="6">
                        <div
                          class="agent-active-section flex-middle"
                          style="align-items: flex-start;"
                        >
                          <div class="mL10">
                            <div class="label-txt3-14">
                              {{ variable.name }}
                            </div>
                            <!-- <div class="label-txt3-12 mT10 flex-middle">
                            {{
                              widget.resourcePath ? widget.resourcePath : '---'
                            }}
                          </div> -->
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="14">
                        <div class="label-txt3-14">
                          {{ variable.value ? variable.value : '---' }}
                        </div>
                      </el-col>
                      <el-col :span="4">
                        <i
                          class="el-icon-edit pointer"
                          @click="openVariableDialog(variable)"
                        ></i>
                        &nbsp;&nbsp;
                        <i
                          class="el-icon-delete pointer"
                          @click="deleteVariable(variable)"
                        ></i>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane
              :label="$t('common.products._connectors')"
              name="connectors"
            >
              <div class="">
                <div
                  class="visitor-hor-card scale-up-left"
                  v-if="
                    !connectedApp.connectedAppConnectorsList ||
                      connectedApp.connectedAppConnectorsList.length === 0
                  "
                >
                  <div class="text-center">
                    {{ $t('common.products.no_connectors_available') }}
                  </div>
                </div>

                <div v-else class="mT20">
                  <div
                    class="visitor-hor-card scale-up-left"
                    v-for="connector in connectedApp.connectedAppConnectorsList"
                    :key="connector.id"
                  >
                    <el-row class="flex-middle">
                      <el-col :span="10">
                        <div
                          class="agent-active-section flex-middle"
                          style="align-items: flex-start;"
                        >
                          <div class="mL10">
                            <div class="label-txt3-14">
                              {{ connector.connection.serviceName }}
                            </div>
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="10">
                        <div
                          class="agent-active-section flex-middle"
                          style="align-items: flex-start;"
                        >
                          <div class="mL10">
                            <div class="label-txt3-14">
                              {{ connector.connection.name }}
                            </div>
                          </div>
                        </div>
                      </el-col>
                      <el-col>
                        <!-- <i
                        class="el-icon-edit pointer"
                        @click="openVariableDialog(variable)"
                      ></i>
                      &nbsp;&nbsp; -->
                        <i
                          class="el-icon-delete pointer"
                          @click="deleteConnector(connector)"
                        ></i>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      <div v-if="showSAMLConfig">
        <el-dialog
          :visible.sync="showSAMLConfig"
          :fullscreen="false"
          :title="$t('common._common.configure_saml')"
          width="50%"
          open="top"
          custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
          :append-to-body="true"
        >
          <div class="mT20 mB20">
            <el-form
              ref="configureSAMLForm"
              :model="connectedAppSAML"
              v-if="connectedAppSAML"
            >
              <el-row>
                <el-col :span="24" class="pB10">
                  <el-form-item prop="loginUrl">
                    <p class="grey-text2">
                      {{ $t('common.products.idp_entity_id_login') }}
                    </p>
                    <el-input
                      class="fc-input-full-border2"
                      v-model="idpLoginURL"
                      readonly
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="24" class="pB10">
                  <el-form-item prop="logoutUrl">
                    <p class="grey-text2">
                      {{ $t('common.header.logout_url') }}
                    </p>
                    <el-input
                      class="fc-input-full-border2"
                      v-model="idpLogoutURL"
                      readonly
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="24" class="pB30">
                  <a :href="idpCertificateURL" target="_blank">{{
                    $t('common._common.download_idp_certificate')
                  }}</a>
                  <a :href="idpMetadataURL" class="mL15" target="_blank">{{
                    $t('common._common.download_idp_metadata')
                  }}</a>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24" class="pB10">
                  <el-form-item prop="spEntityId">
                    <p class="grey-text2">
                      {{ $t('common._common.sp_entity_id') }}
                    </p>
                    <el-input
                      class="fc-input-full-border2"
                      placeholder="This is an URI that uniquely identifies your Service Provider(SP)"
                      v-model="connectedAppSAML.spEntityId"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="24" class="pB10">
                  <el-form-item prop="spAcsUrl">
                    <p class="grey-text2">
                      {{ $t('common._common.sp_acs_url') }}
                    </p>
                    <el-input
                      class="fc-input-full-border2"
                      placeholder="Assertion Consumer Service URL"
                      v-model="connectedAppSAML.spAcsUrl"
                    >
                      <template slot="prepend">{{
                        connectedApp.productionBaseUrl
                      }}</template>
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="24" class="pB10">
                  <el-form-item prop="subjectType">
                    <p class="grey-text2">
                      {{ $t('common._common.subject_type') }}
                    </p>
                    <el-select
                      class="fc-input-full-border-select2 width100"
                      v-model="connectedAppSAML.subjectTypeEnum"
                      :placeholder="$t('common._common.choose_subject_type')"
                    >
                      <el-option
                        v-for="option in subjectTypes"
                        :value="option.value"
                        :label="option.label"
                        :key="option.label"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="24" class="pB10">
                  <el-form-item prop="nameIdFormat">
                    <p class="grey-text2">
                      {{ $t('common.wo_report.name_id_format') }}
                    </p>
                    <el-select
                      class="fc-input-full-border-select2 width100"
                      v-model="connectedAppSAML.nameIdFormatEnum"
                      :placeholder="$t('common._common.choose_name_id_format')"
                    >
                      <el-option
                        v-for="option in nameIdFormats"
                        :value="option.value"
                        :label="option.label"
                        :key="option.label"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelSAMLForm()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveSAMLConfig()"
              :loading="samlConfigSaving"
              >{{
                samlConfigSaving
                  ? $t('common._common.submitting')
                  : $t('common._common._save')
              }}</el-button
            >
          </div>
        </el-dialog>
      </div>

      <div v-if="showWidgetDialog">
        <el-dialog
          :visible.sync="showWidgetDialog"
          :fullscreen="false"
          :title="$t('common._common.add_widget')"
          width="50%"
          open="top"
          custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
          :append-to-body="true"
        >
          <div class="mT20 mB20">
            <el-form
              ref="configureWidgetForm"
              :model="connectedAppWidget"
              v-if="connectedAppWidget"
            >
              <el-row>
                <el-col :span="12" class="pB10">
                  <el-form-item prop="name">
                    <p class="grey-text2">{{ $t('common.products.name') }}</p>
                    <el-input
                      class="fc-input-full-border2"
                      v-model="connectedAppWidget.widgetName"
                      :placeholder="$t('common._common.enter_widget_name')"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12" class="pB10">
                  <el-form-item prop="entityType">
                    <p class="grey-text2">
                      {{ $t('common.products.widget_type') }}
                    </p>
                    <el-select
                      class="fc-input-full-border-select2 width100"
                      v-model="connectedAppWidget.entityTypeEnum"
                      :placeholder="$t('common._common.choose_widget_type')"
                    >
                      <el-option
                        v-for="option in entityTypes"
                        :value="option.value"
                        :label="option.label"
                        :key="option.label"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col
                  v-if="
                    connectedAppWidget.entityTypeEnum === 'SUMMARY_PAGE' ||
                      connectedAppWidget.entityTypeEnum === 'RELATED_LIST' ||
                      connectedAppWidget.entityTypeEnum === 'CREATE_RECORD' ||
                      connectedAppWidget.entityTypeEnum === 'EDIT_RECORD'
                  "
                  :span="12"
                  class="pB10"
                >
                  <el-form-item prop="entity">
                    <p class="grey-text2">
                      {{ $t('common.wo_report.module') }}
                    </p>
                    <el-select
                      class="fc-input-full-border-select2 width100"
                      v-model="connectedAppWidget.entityId"
                      :placeholder="$t('setup.formulaBuilder.choose_module')"
                      @change="loadModuleMeta"
                    >
                      <el-option
                        v-for="option in getModules(
                          connectedAppWidget.entityTypeEnum
                        )"
                        :value="option.moduleId"
                        :label="option.displayName"
                        :key="option.moduleId"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="
                    specialFormTypes.includes(connectedAppWidget.entityTypeEnum)
                  "
                  :span="12"
                  class="pB10"
                >
                  <el-form-item prop="entity">
                    <p class="grey-text2">
                      {{ $t('common.wo_report.module') }}
                    </p>
                    <el-select
                      class="fc-input-full-border-select2 width100"
                      v-model="moduleName"
                      :placeholder="$t('setup.formulaBuilder.choose_module')"
                      @change="loadModuleMetafromModuleName(moduleName)"
                    >
                      <el-option
                        v-for="option in getModules(
                          connectedAppWidget.entityTypeEnum
                        )"
                        :value="option.name"
                        :label="option.displayName"
                        :key="option.moduleId"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="
                    specialFormTypes.includes(connectedAppWidget.entityTypeEnum)
                  "
                  :span="12"
                  class="pB10"
                >
                  <el-form-item prop="entity">
                    <template
                      v-if="
                        connectedAppWidget.entityTypeEnum === 'VIEW_BACKGROUND'
                      "
                    >
                      <p class="grey-text2">
                        {{ $t('common.wo_report.view') }}
                      </p>
                      <el-select
                        class="fc-input-full-border-select2 width100"
                        v-model="connectedAppWidget.entityId"
                        :placeholder="$t('common.products.select_form')"
                      >
                        <el-option-group
                          v-for="(form, index) in viewList"
                          :key="index"
                          :label="form.displayName"
                          :value="form.id"
                        >
                          <el-option
                            v-for="item in form.views"
                            :key="item.id"
                            :label="item.displayName"
                            :value="item.id"
                          >
                          </el-option>
                        </el-option-group>
                      </el-select>
                    </template>
                    <template v-else>
                      <p class="grey-text2">
                        {{ $t('common.wo_report.forms') }}
                      </p>
                      <el-select
                        class="fc-input-full-border-select2 width100"
                        v-model="connectedAppWidget.entityId"
                        :placeholder="$t('common.products.select_form')"
                      >
                        <el-option
                          v-for="(form, index) in forms"
                          :value="form.id"
                          :label="form.displayName"
                          :key="index"
                        >
                        </el-option>
                      </el-select>
                    </template>
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="connectedAppWidget.entityTypeEnum === 'TOPBAR'"
                  :span="12"
                  class="pB10"
                >
                  <el-form-item prop="entity">
                    <p class="grey-text2">
                      {{ $t('common.placeholders.select_app') }}
                    </p>
                    <el-select
                      class="fc-input-full-border-select2 width100"
                      v-model="connectedAppWidget.entityId"
                      :placeholder="$t('common.products.select_form')"
                    >
                      <el-option
                        v-for="(app, index) in apps"
                        :key="index"
                        :label="app.name"
                        :value="app.id"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="pB10">
                <el-col :span="18">
                  <el-form-item prop="path">
                    <p class="grey-text2">
                      {{ $t('common.header.resource_path') }}
                    </p>
                    <el-input
                      class="fc-input-full-border2"
                      v-model="connectedAppWidget.resourcePath"
                      :placeholder="$t('common._common.enter_resource_path')"
                    >
                      <template slot="prepend">{{
                        connectedApp.productionBaseUrl
                      }}</template>
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="5" class="mL10">
                  <el-form-item
                    prop="insertfield"
                    v-if="
                      connectedAppWidget.entityTypeEnum === 'SUMMARY_PAGE' ||
                        connectedAppWidget.entityTypeEnum === 'RELATED_LIST' ||
                        connectedAppWidget.entityTypeEnum === 'CREATE_RECORD' ||
                        connectedAppWidget.entityTypeEnum === 'EDIT_RECORD' ||
                        connectedAppWidget.entityTypeEnum ===
                          'CREATE_RECORD_SIDEBAR'
                    "
                  >
                    <p class="grey-text2">
                      {{ $t('common.products.insert_fields') }}
                    </p>
                    <el-select
                      :placeholder="$t('common.products.select_fields')"
                      class="fc-input-full-border-select2"
                      v-if="moduleMeta"
                      :filterable="true"
                      v-model="selectedInsertField"
                      @change="insertFieldPlaceHolder"
                    >
                      <el-option
                        :label="$t('common.wo_report.none')"
                        :value="null"
                      ></el-option>
                      <el-option
                        v-for="(field, index) in moduleMeta.fields"
                        :key="index"
                        :label="field.displayName"
                        :value="field.name"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col
                  v-if="
                    connectedAppWidget.entityTypeEnum === 'SUMMARY_PAGE' ||
                      connectedAppWidget.entityTypeEnum === 'RELATED_LIST' ||
                      connectedAppWidget.entityTypeEnum === 'CREATE_RECORD' ||
                      connectedAppWidget.entityTypeEnum === 'EDIT_RECORD' ||
                      connectedAppWidget.entityTypeEnum ===
                        'CREATE_RECORD_SIDEBAR'
                  "
                  :span="24"
                  class="pB10"
                >
                  <el-button type="text" @click="showHideWidgetCriteria()"
                    ><i
                      :class="
                        showWidgetCriteria ? 'el-icon-minus' : 'el-icon-plus'
                      "
                    ></i>
                    {{
                      showWidgetCriteria ? 'Remove Criteria' : 'Set Criteria'
                    }}</el-button
                  >
                  <el-form-item
                    prop="criteriaBuilder"
                    v-if="showWidgetCriteria"
                  >
                    <p class="grey-text2">
                      {{ $t('common._common._criteria') }}
                    </p>
                    <CriteriaBuilder
                      v-model="connectedAppWidget.criteria"
                      :moduleName="moduleName"
                      ref="criteria-builder"
                      @input="assignCriteria"
                    ></CriteriaBuilder>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
          <div class="modal-dialog-footer" slot="footer">
            <el-button class="modal-btn-cancel" @click="cancelWigetForm()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveWidget()"
              :loading="widgetSaving"
              >{{
                widgetSaving
                  ? $t('common._common.submitting')
                  : $t('common._common._save')
              }}</el-button
            >
          </div>
        </el-dialog>
      </div>

      <div v-if="showVariableDialog">
        <el-dialog
          :visible.sync="showVariableDialog"
          :fullscreen="false"
          :title="$t('common.products.add_variable')"
          width="50%"
          open="top"
          custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
          :append-to-body="true"
        >
          <div class="mT20 mB20">
            <el-form
              ref="configureVariableForm"
              :model="connectedAppVariable"
              v-if="connectedAppVariable"
            >
              <el-row>
                <el-col :span="12" class="pB10">
                  <el-form-item prop="name">
                    <p class="grey-text2">{{ $t('common.products.name') }}</p>
                    <el-input
                      class="fc-input-full-border2"
                      v-model="connectedAppVariable.name"
                      :placeholder="$t('common._common.enter_variable_name')"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12" class="pB10">
                  <el-form-item prop="value">
                    <p class="grey-text2">{{ $t('common.header.value') }}</p>
                    <el-input
                      class="fc-input-full-border2"
                      v-model="connectedAppVariable.value"
                      :placeholder="$t('common._common.enter_variable_value')"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12" class="pB10">
                  <el-form-item prop="check">
                    <p class="grey-text2">
                      {{ $t('common.products.checkbox') }}
                    </p>
                    <el-checkbox
                      class="fc-input-full-border2"
                      v-model="connectedAppVariable.checkbox"
                    ></el-checkbox>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelVariableForm()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveVariable()"
              :loading="variableSaving"
              >{{
                variableSaving
                  ? $t('common._common.submitting')
                  : $t('common._common._save')
              }}</el-button
            >
          </div>
        </el-dialog>
      </div>

      <div v-if="showCreateNewDialog">
        <el-dialog
          :visible.sync="showCreateNewDialog"
          :fullscreen="true"
          :append-to-body="true"
          custom-class="fc-dialog-form fc-dialog-right setup-dialog100 setup-dialog assetaddvaluedialog fc-web-form-dialog fc-item-type-summary-dialog"
          style="z-index: 999999;"
        >
          <ConnectedAppForm
            :isNew="isNew"
            :connectedAppData="connectedApp"
            @onClose="closeForm"
            @onSave="loadConnectedApp"
            class="facilio-inventory-web-form-body"
          >
          </ConnectedAppForm>
        </el-dialog>
      </div>
      <new-connection
        v-if="showCreateNewConnector"
        :visibility.sync="showCreateNewConnector"
        :connectionObj="connection"
        :isNew="showCreateNewConnector"
        @saved="saved"
      ></new-connection>
      <new-credentials
        v-if="showCredentials"
        :isNew="false"
        :visibility.sync="showCredentials"
        :connectionObj="connection"
      ></new-credentials>
    </div>
  </div>
</template>

<script>
import NewConnection from 'pages/setup/new/NewConnection'
import NewCredentials from 'pages/setup/new/NewConnectionCredentials'
import Spinner from '@/Spinner'
import ConnectedAppForm from 'pages/setup/connectedapps/ConnectedAppForm'
import { CriteriaBuilder } from '@facilio/criteria'
import { getBaseURL } from 'util/baseUrl'
import { API } from '@facilio/api'
import { loadApps } from 'util/appUtil'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    NewConnection,
    NewCredentials,
    Spinner,
    CriteriaBuilder,
    ConnectedAppForm,
  },
  computed: {
    connectedAppId() {
      return this.$route.params.connectedAppId
    },
  },
  created() {
    this.loadModules()
    this.loadConnectedApp()

    loadApps().then(response => {
      this.apps = response.data
    })
  },
  data() {
    return {
      forms: [],
      viewList: null,
      apps: [],
      isNew: false,
      showCredentials: false,
      showCreateNewConnector: false,
      modules: [],
      specialFormTypes: [
        'CREATE_RECORD_SIDEBAR',
        'FORM_BACKGROUND',
        'VIEW_BACKGROUND',
      ],
      entityTypes: [
        { value: 'WEB_TAB', label: 'Web Tab' },
        { value: 'DIALER', label: 'Dialer' },
        { value: 'SUMMARY_PAGE', label: 'Summary Page' },
        { value: 'DASHBOARD_WIDGET', label: 'Dashboard Widget' },
        { value: 'RELATED_LIST', label: 'Related List' },
        // { value: 'DIALER', label: 'Dialer' }, // to be supported later
        { value: 'CREATE_RECORD', label: 'Create Record Page' },
        { value: 'EDIT_RECORD', label: 'Edit Record Page' },
        {
          value: 'CREATE_RECORD_SIDEBAR',
          label: 'Form - Sidebar',
        },
        {
          value: 'FORM_BACKGROUND',
          label: 'Form - Background',
        },
        {
          value: 'CUSTOM_BUTTON',
          label: 'Custom Button Widget',
        },
        {
          value: 'VIEW_BACKGROUND',
          label: 'View - Background',
        },
        {
          value: 'TOPBAR',
          label: 'Topbar',
        },
      ],
      subjectTypes: [{ value: 'USERID_EMAIL', label: 'User Id Email' }],
      nameIdFormats: [
        { value: 'UNSPECIFIED', label: 'Unspecified' },
        { value: 'EMAIL_ADDRESS', label: 'Email Address' },
      ],
      connectedApp: null,
      loading: true,
      activeTabName: 'details',
      showSAMLConfig: false,
      idpLoginURL: null,
      idpLogoutURL: null,
      idpCertificateURL: null,
      idpMetadataURL: null,
      samlConfigSaving: false,
      widgetSaving: false,
      variableSaving: false,
      showWidgetDialog: false,
      showVariableDialog: false,
      showCreateNewDialog: false,
      connectedAppWidget: {
        widgetName: null,
        linkName: null,
        connectedAppId: null,
        resourcePath: null,
        criteriaId: null,
        criteria: null,
        entityTypeEnum: null,
        entityId: null,
      },
      connectedAppVariable: {
        connectorId: null,
        connectedAppId: null,
        connection: null,
      },
      moduleMeta: null,
      showWidgetCriteria: false,
      selectedInsertField: null,
      saving: false,
      connectedAppSAML: null,
      connection: {
        name: '',
        id: null,
        authType: 1,
        serviceName: '',
        clientId: null,
        clientSecretId: null,
        scope: null,
        authorizeUrl: '',
        accessTokenUrl: '',
        refreshTokenUrl: '',
        revokeTokenUrl: '',
        accessToken: '',
        authCode: '',
        refreshToken: '',
        callBackURL: '',
      },
      moduleName: null,
    }
  },
  methods: {
    async getFormsList(moduleName) {
      if (moduleName) {
        let { data } = await API.get(`/v2/forms?moduleName=${moduleName}`)

        if (data) {
          this.forms = (data.forms || []).filter(form => form.id > 0)
        }
      }
    },
    async loadViewDetails(moduleName) {
      let viewDetailUrl = `/v2/views/viewList?moduleName=${moduleName}`
      let { data, error } = await API.get(viewDetailUrl)

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.viewList = data.groupViews
      }
    },
    saved(data, isNew) {
      if (isNew) {
        this.$nextTick(() => {
          this.addConnector(data)
          if (data.authType === 1) {
            this.updateCredentials(data)
          }
        })
      }
    },
    getModules(entityTypeEnum) {
      if (
        entityTypeEnum === 'CREATE_RECORD' ||
        entityTypeEnum === 'EDIT_RECORD'
      ) {
        return this.modules.filter(m => m.custom)
      } else {
        return this.modules
      }
    },
    updateCredentials(connection) {
      this.showCreateNewConnector = false
      this.connection = connection
      this.showCredentials = true
    },
    async addConnector(data) {
      let param = {
        connectedAppConnector: {
          connectorId: data.id,
          connectedAppId: this.connectedApp.id,
          connection: data,
        },
      }
      let url = '/v2/connectedApps/addOrUpdateConnector'

      let { error } = await API.post(url, param)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        if (!this.connectedApp.connectedAppConnectorsList) {
          this.connectedApp.connectedAppConnectorsList = []
        }
        this.connectedApp.connectedAppConnectorsList.push(
          data.connectedAppConnector
        )
      }
    },
    async loadModules() {
      let { data } = await API.get(`/v2/connectedApps/modules`)

      if (data) {
        this.modules = data.modules
      }
    },
    assignCriteria(val) {
      this.connectedAppWidget.criteria = val
      this.$forceUpdate()
    },
    async loadConnectedApp() {
      this.loading = true

      let { data } = await API.get(
        `/v2/connectedApps/get?connectedAppId=${this.connectedAppId}`
      )

      if (data) {
        this.connectedApp = data.connectedApp
      }
      this.loading = false
    },
    insertFieldPlaceHolder() {
      if (this.selectedInsertField) {
        let placeholder = '${' + this.selectedInsertField + '}'
        if (this.connectedAppWidget) {
          if (!this.connectedAppWidget.resourcePath) {
            this.connectedAppWidget.resourcePath = placeholder
          } else {
            this.connectedAppWidget.resourcePath =
              this.connectedAppWidget.resourcePath + placeholder
          }
        }
      }
    },
    async loadModuleMeta(moduleId) {
      this.moduleName = this.modules.find(m => m.moduleId === moduleId).name
      this.getFormsList(this.moduleName)
      let { data } = await API.get(`/module/meta?moduleName=${this.moduleName}`)

      if (data) {
        this.moduleMeta = data.meta
      }
    },
    async loadModuleMetafromModuleName(moduleName) {
      this.getFormsList(moduleName)
      let { data } = await API.get(`/module/meta?moduleName=${moduleName}`)

      if (data) {
        this.moduleMeta = data.meta
      }
      this.loadViewDetails(moduleName)
    },
    openWidgetDialog(widget) {
      this.showWidgetDialog = true
      this.showWidgetCriteria = false
      this.selectedInsertField = null
      this.moduleName = null
      if (!widget) {
        widget = {
          widgetName: null,
          linkName: null,
          connectedAppId: null,
          resourcePath: null,
          criteriaId: null,
          criteria: null,
          entityTypeEnum: null,
          entityId: null,
        }
      } else {
        if (
          this.specialFormTypes.includes(widget.entityTypeEnum) &&
          widget.entityId > 0
        ) {
          this.loadformData(widget.entityId)
        }
        if (widget.criteria) {
          this.showWidgetCriteria = true
        }
      }
      this.connectedAppWidget = widget
    },
    async loadformData(formId) {
      let { error, data } = await API.get(`v2/forms/null?formId=${formId}`)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let form = data.form
        if (form && form.module && form.module.name) {
          this.moduleName = form.module.name
          this.loadModuleMetafromModuleName(this.moduleName)
        }
      }
    },
    openVariableDialog(variable) {
      this.showVariableDialog = true
      if (!variable) {
        variable = {
          name: null,
          value: null,
          connectedAppId: null,
        }
      }
      this.connectedAppVariable = variable
    },
    showHideWidgetCriteria() {
      this.showWidgetCriteria = !this.showWidgetCriteria
      if (!this.showWidgetCriteria) {
        this.connectedAppWidget.criteria = null
      }
    },
    openSamlConfigure() {
      this.showSAMLConfig = true
      this.idpLoginURL =
        window.location.origin +
        '/app/connectedapp/' +
        this.connectedApp.linkName
      this.idpLogoutURL = window.location.origin + '/auth/logout'
      this.idpCertificateURL =
        getBaseURL() + '/v2/connectedApps/download/certificate'
      this.idpMetadataURL =
        getBaseURL() +
        '/v2/connectedApps/download/metadata?connectedAppId=' +
        this.connectedApp.id
      if (!this.connectedApp.connectedAppSAML) {
        this.connectedAppSAML = {
          connectedAppId: this.connectedApp.id,
        }
      } else {
        this.connectedAppSAML = this.connectedApp.connectedAppSAML
      }
    },
    async updateConnectedAppShowInLauncher() {
      let param = {
        connectedApp: {
          id: this.connectedApp.id,
          showInLauncher: this.connectedApp.showInLauncher,
        },
      }
      let url = '/v2/connectedApps/update'
      let { error } = await API.post(url, param)
      if (error) {
        this.$message.error('Error Occurred')
      }
    },
    async updateConnectedAppIsActive() {
      let param = {
        connectedApp: {
          id: this.connectedApp.id,
          isActive: this.connectedApp.isActive,
        },
      }
      let url = '/v2/connectedApps/update'
      let { error } = await API.post(url, param)
      if (error) {
        this.$message.error('Error Occurred')
      }
    },
    cancelSAMLForm() {
      this.showSAMLConfig = false
    },
    cancelWigetForm() {
      this.showWidgetDialog = false
    },
    cancelVariableForm() {
      this.showVariableDialog = false
    },
    deleteWidget(widget) {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_widget'),
          message: this.$t('common._common.are_you_sure_want_delete'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.deletWidgetCall(widget)
          }
        })
    },
    async deletWidgetCall(widget) {
      let url = '/v2/connectedApps/deleteWidget'
      let param = {
        connectedAppWidget: {
          id: widget.id,
          criteriaId: widget.criteriaId,
        },
      }
      let { error } = await API.post(url, param)

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let widgetIndex = this.connectedApp.connectedAppWidgetsList.findIndex(
          w => w.id === widget.id
        )
        this.connectedApp.connectedAppWidgetsList.splice(widgetIndex, 1)
        this.$message.success(
          this.$t('common.products.widget_deleted_successfully')
        )
      }
    },
    deleteVariable(variable) {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_variable'),
          message: this.$t('common._common.are_you_want_delete_this_variable'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.deleteVariableCall(variable)
          }
        })
    },
    async deleteVariableCall(variable) {
      let url = '/v2/connectedApps/deleteVariable'
      let param = {
        connectedAppId: this.connectedApp.id,
        name: variable.name,
      }
      let { error } = await API.post(url, param)

      if (error) {
        let { message } = error

        this.$message.error(message)
      } else {
        let index = this.connectedApp.variablesList.findIndex(
          w => w.id === variable.id
        )
        this.connectedApp.variablesList.splice(index, 1)
        this.$message.success(
          this.$t('common._common.variable_deleted_successfully')
        )
      }
    },
    deleteConnector(connector) {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_connector'),
          message: this.$t('common._common.are_you_want_delete_this_connector'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.deleteConnectorCall(connector)
          }
        })
    },
    async deleteConnectorCall(connector) {
      let url = '/v2/connectedApps/deleteConnector'
      let param = {
        connectedAppConnector: {
          id: connector.id,
        },
      }

      let { error } = await API.post(url, param)

      if (error) {
        let { message } = error

        this.$message.error(message)
      } else {
        let index = this.connectedApp.connectedAppConnectorsList.findIndex(
          w => w.id === connector.id
        )
        this.connectedApp.connectedAppConnectorsList.splice(index, 1)
        this.$message.success(
          this.$t('common.products.connector_deleted_successfully')
        )
      }
    },
    deleteSaml() {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_saml'),
          message: this.$t('common.header.are_you_want_delete_saml'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.deleteSamalCall()
          }
        })
    },
    async deleteSamalCall() {
      let url = '/v2/connectedApps/deleteSAML'
      let param = {
        connectedAppId: this.connectedApp.id,
      }
      let { error } = await API.post(url, param)

      if (error) {
        let { message } = error

        this.$message.error(message)
      } else {
        this.connectedApp.connectedAppSAML = null
        this.$message.success(
          this.$t('common._common.saml_deleted_successfully')
        )
      }
    },
    async saveSAMLConfig() {
      let url = '/v2/connectedApps/addOrUpdateSAML'
      this.connectedApp.connectedAppSAML = this.connectedAppSAML
      let param = {
        connectedAppSAML: this.connectedApp.connectedAppSAML,
      }
      this.samlConfigSaving = true
      let { error } = await API.post(url, param)

      if (error) {
        let { message } = error

        this.$message.error(message)
      } else {
        this.$message.success(
          this.$t('common._common.saml_configured_successfully')
        )
      }
      this.cancelSAMLForm()
    },
    async saveWidget() {
      this.connectedAppWidget.connectedAppId = this.connectedApp.id
      if (
        this.connectedAppWidget.entityTypeEnum !== 'SUMMARY_PAGE' &&
        this.connectedAppWidget.entityTypeEnum !== 'RELATED_LIST' &&
        this.connectedAppWidget.entityTypeEnum !== 'CREATE_RECORD' &&
        this.connectedAppWidget.entityTypeEnum !== 'EDIT_RECORD' &&
        this.connectedAppWidget.entityTypeEnum !== 'CREATE_RECORD_SIDEBAR' &&
        this.connectedAppWidget.entityTypeEnum !== 'VIEW_BACKGROUND' &&
        this.connectedAppWidget.entityTypeEnum !== 'FORM_BACKGROUND' &&
        this.connectedAppWidget.entityTypeEnum !== 'TOPBAR'
      ) {
        this.connectedAppWidget.entityId = -99
        this.connectedAppWidget.criteria = null
      }
      let criteriaValidate = true
      if (
        this.connectedAppWidget.criteria &&
        !isEmpty(this.$refs['criteria-builder'])
      ) {
        criteriaValidate = this.$refs['criteria-builder']?.validate()
      }
      if (criteriaValidate) {
        let url = '/v2/connectedApps/addOrUpdateWidget'
        let param = {
          connectedAppWidget: this.connectedAppWidget,
        }
        this.widgetSaving = true

        let { error, data } = await API.post(url, param)
        this.widgetSaving = false

        if (error) {
          let { message } = error

          this.$message.error(message)
        } else {
          let connectedAppWidget = data.connectedAppWidget
          if (!this.connectedAppWidget.id) {
            if (!this.connectedApp.connectedAppWidgetsList) {
              this.connectedApp.connectedAppWidgetsList = []
            }
            this.connectedApp.connectedAppWidgetsList.push(connectedAppWidget)
            this.$message.success(
              this.$t('common._common.widget_added_successfully')
            )
          } else {
            let widgetIndex = this.connectedApp.connectedAppWidgetsList.findIndex(
              w => w.id === connectedAppWidget.id
            )
            this.connectedApp.connectedAppWidgetsList[
              widgetIndex
            ] = connectedAppWidget
            this.$message.success(
              this.$t('common.products.widget_updated_successfully')
            )
          }
        }
      }
      this.cancelWigetForm()
    },
    async saveVariable() {
      if (
        this.connectedApp.variablesList &&
        this.connectedApp.variablesList.find(
          v =>
            v.name === this.connectedAppVariable.name &&
            v.id !== this.connectedAppVariable.id
        )
      ) {
        //  this.cancelVariableForm()
        this.$message.error(
          this.$t('common.header.variable_name_already_exists') +
            this.connectedAppVariable.name
        )
      } else {
        this.connectedAppVariable.connectedAppId = this.connectedApp.id

        let url = '/v2/connectedApps/addOrUpdateVariable'
        let param = {
          variable: this.connectedAppVariable,
        }
        this.variableSaving = true

        let { error, data } = await API.post(url, param)
        this.variableSaving = false
        if (error) {
          let { message } = error
          this.$message.error(message)
        } else {
          let connectedAppVariable = data.variable
          if (!this.connectedAppVariable.id) {
            if (!this.connectedApp.variablesList) {
              this.connectedApp.variablesList = []
            }
            this.connectedApp.variablesList.push(connectedAppVariable)
            this.$message.success(
              this.$t('common._common.variable_added_successfully')
            )
          } else {
            let index = this.connectedApp.variablesList.findIndex(
              v => v.id === connectedAppVariable.id
            )
            this.connectedApp.variablesList[index] = connectedAppVariable
            this.$message.success(
              this.$t('common._common.variable_updated_successfully')
            )
          }
        }
        this.cancelVariableForm()
      }
    },
    closeForm() {
      this.showCreateNewDialog = false
    },
  },
}
</script>
<style>
.capp-linkname {
  font-size: 14px;
  margin: 0 2px;
  opacity: 0.3;
}
.capp-linkname:hover {
  opacity: 1;
}
</style>
