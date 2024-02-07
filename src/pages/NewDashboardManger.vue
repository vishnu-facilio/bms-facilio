<template>
  <div>
    <div
      v-if="!isPortalApp() && !$hasPermission(`dashboard:CREATE,UPDATE,DELETE`)"
      style="text-align: center;margin-top: 20%;font-size: 20px"
    >
      <span> You don't have dashboard manager access</span>
    </div>
    <div v-else class="views-manager-container">
      <div class="header-container">
        <div class="d-flex flex-direction-column">
          <div class="d-flex pointer" @click="goBack">
            <InlineSvg
              src="arrow-pointing-to-left2"
              iconClass="icon icon-xs self-center mR5"
            ></InlineSvg>

            <div class="text-fc-blue f11 letter-spacing0_5">
              {{ $t('viewsmanager.list.back') }}
            </div>
          </div>
          <div class="heading-black22 pT10">
            {{ 'Dashboard Manager' }}
          </div>
        </div>
        <div class="mL-auto" v-if="activeName === 'dashboard'">
          <div class="btn-container">
            <el-select
              @change="loadDashboardAndSetAppsType(true, appId)"
              v-model="appId"
              placeholder="Select App"
              filterable
              class="fc-input-full-border2 width200px pR15"
            >
              <el-option
                v-for="app in apps"
                :key="app.linkName"
                :label="app.name"
                :value="app.id"
              >
              </el-option>
            </el-select>
            <el-button
              v-if="hasCreatePermission"
              type="primary"
              class="manager-primary-btn"
              @click="addFolder()"
              ><span class="btn-label">{{
                $t('viewsmanager.list.add_folder')
              }}</span>
            </el-button>
            <el-button
              v-if="hasCreatePermission"
              type="primary"
              class="manager-secondary-btn"
              @click="redirectToDashboardCreation()"
              ><span class="btn-label">{{ 'Add dashboard' }}</span>
            </el-button>
          </div>
        </div>
        <div class="mL-auto" v-if="activeName === 'screens'">
          <portal-target name="screens"></portal-target>
        </div>
        <div class="mL-auto" v-if="activeName === 'remote-screens'">
          <portal-target name="remote-screens"></portal-target>
        </div>
      </div>
      <el-tabs class="manager-tabs-container" v-model="activeName">
        <el-tab-pane label="Dashboard" name="dashboard">
          <div
            class="height100 overflow-y-scroll pB50"
            style="height: calc(100vh - 200px);"
          >
            <div v-if="loading">
              <div
                v-for="index in 3"
                :key="index"
                class="fc__white__bg__asset d-flex width100 height50 mB10"
              >
                <div class="fL width85 self-center">
                  <div class="fc-animated-background p10 width140px"></div>
                </div>
                <div class="fR self-center">
                  <div class="fc-animated-background p10 width140px"></div>
                </div>
              </div>
            </div>
            <draggable
              v-else
              v-model="headerList"
              v-bind="dashboardFolderDragOptions"
              :group="`viewGroups`"
              @change="onFolderChange"
            >
              <div
                class="mB10"
                v-for="(dashboard, index) in headerList"
                :key="index"
              >
                <el-collapse v-model="activeScreen" class="folder-collapse">
                  <el-collapse-item
                    :name="dashboard.id"
                    class="manager-views-item visibility-visible-actions"
                  >
                    <template slot="title">
                      <div class="d-flex width100">
                        <inline-svg
                          src="svgs/drag-and-drop"
                          class="d-flex self-center mR30 cursor-drag"
                          iconClass="icon fill-lite-grey pointer"
                        ></inline-svg>
                        <div class="d-flex self-center">
                          <div class="pR5 f18 bold">
                            <i class="el-icon-folder icon-close" />
                            <i class="el-icon-folder-opened icon-open" />
                          </div>

                          <div class="text-uppercase">
                            {{ dashboard.label }}
                          </div>
                        </div>
                        <div class="d-flex visibility-hide-actions mL-auto">
                          <inline-svg
                            @click.native.stop="updateFolderName(dashboard)"
                            src="svgs/edit-pencil"
                            class="d-flex mR15 self-center"
                            iconClass="icon icon-xs fill-grey pointer"
                          ></inline-svg>
                          <inline-svg
                            v-if="
                              dashboard.childrens &&
                                dashboard.childrens.length === 0
                            "
                            @click.native.prevent="
                              deletedashboardFolder(dashboard, index)
                            "
                            src="svgs/delete"
                            class="d-flex mR15 self-center fill-grey pointer"
                            iconClass="icon icon-sm default icon-remove"
                          ></inline-svg>
                        </div>
                      </div>
                    </template>
                    <draggable
                      v-model="dashboard.childrens"
                      v-bind="dashboardDragOptions"
                      @change="onDashBoardChange"
                    >
                      <template v-for="(d, idx) in dashboard.childrens">
                        <el-collapse
                          v-model="activeScreen1"
                          :key="idx"
                          v-if="d.childrens && d.childrens.length"
                          class="dashboardmanager-collapse-two-header"
                        >
                          <el-collapse-item
                            title="inner collapse"
                            :name="d.id"
                            class="dashboardmanager-collapse-two-open"
                          >
                            <template slot="title">
                              <div class="dashboardmanager-collapse-two">
                                <el-row
                                  class="pointer visibility-visible-actions dashboardmanager-collapse-tw0-header views-item"
                                  :key="idx"
                                  v-if="
                                    dashboard.childrens &&
                                      dashboard.childrens.length
                                  "
                                >
                                  <el-col
                                    :span="15"
                                    class="flex-middle f12 bold letter-spacing1 self-center"
                                  >
                                    <img
                                      src="~assets/drag.svg"
                                      class="mR30 cursor-drag"
                                    />
                                    <img
                                      src="~assets/dashboard-grey.svg"
                                      class="mR10"
                                    />
                                    <div class="fc-black-13 fw4">
                                      {{ d.label }}
                                    </div>
                                  </el-col>
                                  <el-col :span="3" class="self-center">
                                    <div class="fc-black-12 text-left">
                                      <el-popover
                                        placement="right"
                                        width="200"
                                        trigger="click"
                                        v-if="
                                          d.rawdata.dashboardSharingContext &&
                                            getDashboardSharedInfo(
                                              d.rawdata.dashboardSharingContext
                                            ).shareTo === 'Specific'
                                        "
                                      >
                                        <div class="popover-content p10">
                                          <div class="header">USERS</div>
                                          <div
                                            v-for="(user,
                                            index) in getDashboardSharedInfo(
                                              d.rawdata.dashboardSharingContext
                                            ).data.users"
                                            :key="index"
                                            class="fc-black-13 text-left pT10"
                                          >
                                            <ol>
                                              <li v-if="user && user.name">
                                                {{ user.name }}
                                              </li>
                                            </ol>
                                          </div>
                                        </div>
                                        <div class="popover-content p10">
                                          <div class="header">ROLES</div>
                                          <div
                                            v-for="(role,
                                            index) in getDashboardSharedInfo(
                                              d.rawdata.dashboardSharingContext
                                            ).data.roles"
                                            :key="index"
                                            class="fc-black-13 text-left pT10"
                                          >
                                            <ol>
                                              <li v-if="role && role.name">
                                                {{ role.name }}
                                              </li>
                                            </ol>
                                          </div>
                                        </div>
                                        <div class="popover-content p10">
                                          <div class="header">GROUPS</div>
                                          <div
                                            v-for="(group,
                                            index) in getDashboardSharedInfo(
                                              d.rawdata.dashboardSharingContext
                                            ).data.groups"
                                            :key="index"
                                            class="fc-black-13 text-left pT10"
                                          >
                                            <ol>
                                              <li v-if="group && group.name">
                                                {{ group.name }}
                                              </li>
                                            </ol>
                                          </div>
                                        </div>
                                        <span slot="reference">
                                          <inline-svg
                                            src="svgs/share"
                                            class="mR14 self-center vertical-text-top fill-grey"
                                            iconClass="icon icon-sm pT3"
                                          ></inline-svg
                                          >Specific
                                        </span>
                                      </el-popover>
                                      <span
                                        v-else
                                        class="fw4 line-height-normal"
                                      >
                                        <inline-svg
                                          src="svgs/share"
                                          class="mR10 self-center vertical-text-top fill-grey"
                                          iconClass="icon icon-sm pT3"
                                        ></inline-svg>
                                        {{
                                          d.rawdata.dashboardSharingContext
                                            ? getDashboardSharedInfo(
                                                d.rawdata
                                                  .dashboardSharingContext
                                              ).shareTo
                                            : ''
                                        }}
                                      </span>
                                    </div>
                                  </el-col>
                                  <el-col
                                    :span="2"
                                    class="flex-middle"
                                    v-if="d.rawdata && d.rawdata.mobileEnabled"
                                  >
                                    <img
                                      src="~assets/mobile-blue.svg"
                                      class="mR15"
                                    />
                                    <div class="fc-black-12 fw4">
                                      Enabled
                                    </div>
                                  </el-col>
                                  <el-col
                                    :span="2"
                                    class="flex-middle pointer"
                                    v-else
                                  >
                                    <inline-svg
                                      src="svgs/mobile-grey"
                                      class="mR10 self-center vertical-text-top fill-grey"
                                      iconClass="icon icon-sm"
                                    ></inline-svg>
                                    <div class="fc-black-12 pointer fw4">
                                      Disabled
                                    </div>
                                  </el-col>
                                  <el-col
                                    :span="4"
                                    class="flex-middle justify-content-end text-right position-relative"
                                  >
                                    <el-dropdown>
                                      <span class="el-dropdown-link">
                                        <img
                                          src="~assets/menu.svg"
                                          width="12"
                                          height="12"
                                          class="mR10"
                                        />
                                      </span>
                                      <el-dropdown-menu slot="dropdown">
                                        <el-dropdown-item
                                          v-if="hasCreatePermission"
                                        >
                                          <div @click="openClonePopup(d)">
                                            {{
                                              $t(
                                                'common.header.clone_dashboard'
                                              )
                                            }}
                                          </div>
                                        </el-dropdown-item>
                                        <el-dropdown-item
                                          v-if="
                                            isShowCloneToAnotherApp &&
                                              hasCreatePermission
                                          "
                                        >
                                          <div @click="openClonePopup(d, true)">
                                            {{
                                              $t(
                                                'common.dashboard.clone_to_another_app'
                                              )
                                            }}
                                          </div>
                                        </el-dropdown-item>
                                        <el-dropdown-item
                                          v-if="hasCreatePermission"
                                        >
                                          <div @click="openMoveToPopup(d)">
                                            {{
                                              $t(
                                                'common.dashboard.move_to_another_app'
                                              )
                                            }}
                                          </div>
                                        </el-dropdown-item>
                                        <el-dropdown-item
                                          v-if="hasDashboardEditPermission"
                                        >
                                          <div
                                            @click="redirectToDashboardEdit(d)"
                                          >
                                            Edit
                                          </div>
                                        </el-dropdown-item>
                                        <el-dropdown-item
                                          v-if="
                                            d.id > -1 && hasDeletePermission
                                          "
                                        >
                                          <div
                                            @click="
                                              deletedashboard(d, index, idx)
                                            "
                                          >
                                            Delete
                                          </div>
                                        </el-dropdown-item>
                                        <el-dropdown-item
                                          v-if="hasDashboardEditPermission"
                                        >
                                          <div
                                            @click="
                                              EnableMobileDashboardInManager(
                                                d,
                                                querryModule
                                              )
                                            "
                                          >
                                            {{
                                              d.rawdata.mobileEnabled
                                                ? $t(
                                                    'home.dashboard.disable_mobile_dashboard'
                                                  )
                                                : $t(
                                                    'home.dashboard.enable_mobile_dashboard'
                                                  )
                                            }}
                                          </div>
                                        </el-dropdown-item>
                                      </el-dropdown-menu>
                                    </el-dropdown>

                                    <div
                                      class="visibility-hide-actions dashboard-manager-collapse-actions pB10 pT0"
                                    >
                                      <inline-svg
                                        v-if="hasSharePermission"
                                        src="svgs/share"
                                        class="mR10 self-center vertical-text-top fill-grey"
                                        iconClass="icon icon-sm default share-icon"
                                        @click="sharedDashboard(d)"
                                        title="Share"
                                        v-tippy
                                        data-position="top"
                                        data-arrow="true"
                                      ></inline-svg>
                                      <inline-svg
                                        src="svgs/link"
                                        class="self-center vertical-text-top fill-grey default"
                                        iconClass="icon icon-sm-md default"
                                        title="Link"
                                        v-tippy
                                        data-position="top"
                                        data-arrow="true"
                                        @click="openDashboard(d)"
                                      ></inline-svg>
                                    </div>
                                  </el-col>
                                </el-row>
                              </div>
                            </template>
                            <draggable
                              v-model="d.childrens"
                              v-bind="dashboardChildrenOptions"
                              @change="onDashboardchildrenChange"
                              class="drag-folder"
                            >
                              <template v-for="(child, icx) in d.childrens">
                                <el-row
                                  class="pointer visibility-visible-actions dashboardmanager-collapse-two-bg views-item"
                                  :key="icx"
                                >
                                  <el-col :span="15" class="flex-middle mL40">
                                    <img
                                      src="~assets/drag.svg"
                                      class="mR30 cursor-drag"
                                    />
                                    <img
                                      src="~assets/dashboard-grey.svg"
                                      class="mR10"
                                    />
                                    <div class="fc-black-13">
                                      {{ child.label }}
                                    </div>
                                  </el-col>
                                  <el-col :span="3" class="self-center">
                                    <div class="fc-black-12 text-left">
                                      <el-popover
                                        placement="right"
                                        width="200"
                                        trigger="click"
                                        v-if="
                                          d.rawdata.dashboardSharingContext &&
                                            getDashboardSharedInfo(
                                              d.rawdata.dashboardSharingContext
                                            ).shareTo === 'Specific'
                                        "
                                      >
                                        <div class="popover-content p10">
                                          <div class="header">USERS</div>
                                          <template
                                            v-for="(user,
                                            index) in getDashboardSharedInfo(
                                              d.rawdata.dashboardSharingContext
                                            ).data.users"
                                          >
                                            <div
                                              :key="index"
                                              class="fc-black-13 text-left pT10"
                                              v-if="user && user.name"
                                            >
                                              {{ user.name }}
                                            </div>
                                          </template>
                                        </div>
                                        <div class="popover-content p10">
                                          <div class="header">ROLES</div>
                                          <template
                                            v-for="(role,
                                            index) in getDashboardSharedInfo(
                                              d.rawdata.dashboardSharingContext
                                            ).data.roles"
                                          >
                                            <div
                                              :key="index"
                                              class="fc-black-13 text-left pT10"
                                              v-if="role && role.name"
                                            >
                                              {{ role.name }}
                                            </div>
                                          </template>
                                        </div>
                                        <div class="popover-content p10">
                                          <div class="header">GROUPS</div>
                                          <template
                                            v-for="(group,
                                            index) in getDashboardSharedInfo(
                                              d.rawdata.dashboardSharingContext
                                            ).data.groups"
                                          >
                                            <div
                                              class="fc-black-13 text-left pT10"
                                              :key="index"
                                              v-if="group && group.name"
                                            >
                                              {{ group.name }}
                                            </div>
                                          </template>
                                        </div>
                                        <span slot="reference">
                                          <inline-svg
                                            src="svgs/share"
                                            class="mR14 self-center vertical-text-top fill-grey"
                                            iconClass="icon icon-sm pT3"
                                          ></inline-svg
                                          >Specific
                                        </span>
                                      </el-popover>
                                      <span v-else>
                                        <inline-svg
                                          src="svgs/share"
                                          class="mR10 self-center vertical-text-top fill-grey"
                                          iconClass="icon icon-sm pT3"
                                        ></inline-svg>
                                        {{
                                          d.rawdata.dashboardSharingContext
                                            ? getDashboardSharedInfo(
                                                d.rawdata
                                                  .dashboardSharingContext
                                              ).shareTo
                                            : ''
                                        }}
                                      </span>
                                    </div>
                                  </el-col>
                                  <el-col
                                    :span="2"
                                    class="flex-middle"
                                    v-if="
                                      child.rawdata &&
                                        child.rawdata.mobileEnabled
                                    "
                                  >
                                    <img
                                      src="~assets/mobile-blue.svg"
                                      class="mR15"
                                    />
                                    <div class="fc-black-12 fw4">
                                      Enabled
                                    </div>
                                  </el-col>
                                  <el-col
                                    :span="2"
                                    class="flex-middle pointer"
                                    v-else
                                  >
                                    <inline-svg
                                      src="svgs/mobile-grey"
                                      class="mR10 self-center vertical-text-top fill-grey"
                                      iconClass="icon icon-sm"
                                    ></inline-svg>
                                    <div class="fc-black-12 pointer">
                                      Disabled
                                    </div>
                                  </el-col>
                                  <el-col
                                    :span="4"
                                    class="flex-middle justify-content-end text-right position-relative"
                                  ></el-col>
                                </el-row>
                              </template>
                            </draggable>
                          </el-collapse-item>
                        </el-collapse>
                        <el-row
                          class="pointer visibility-visible-actions views-item"
                          :key="idx"
                          v-else
                        >
                          <el-col
                            :span="15"
                            class="flex-middle f12 bold letter-spacing1 self-center"
                          >
                            <img
                              src="~assets/drag.svg"
                              class="mR30 cursor-drag"
                            />
                            <img
                              src="~assets/dashboard-grey.svg"
                              class="mR10"
                            />
                            <div class="fc-black-13">
                              {{ d.label }}
                              {{
                                d.childrens && d.childrens.length
                                  ? '[' + (d.childrens.length - 1) + ']'
                                  : ''
                              }}
                            </div>
                          </el-col>
                          <el-col :span="3" class="self-center">
                            <div class="fc-black-12 text-left">
                              <el-popover
                                placement="right"
                                width="200"
                                trigger="click"
                                v-if="
                                  d.rawdata.dashboardSharingContext &&
                                    getDashboardSharedInfo(
                                      d.rawdata.dashboardSharingContext
                                    ).shareTo === 'Specific'
                                "
                              >
                                <div class="popover-content p10">
                                  <div class="header">USERS</div>
                                  <template
                                    v-for="(user,
                                    index) in getDashboardSharedInfo(
                                      d.rawdata.dashboardSharingContext
                                    ).data.users"
                                  >
                                    <div
                                      :key="index"
                                      class="fc-black-13 text-left pT10"
                                      v-if="user && user.name"
                                    >
                                      {{ user.name }}
                                    </div>
                                  </template>
                                </div>
                                <div class="popover-content p10">
                                  <div class="header">ROLES</div>
                                  <template
                                    v-for="(role,
                                    index) in getDashboardSharedInfo(
                                      d.rawdata.dashboardSharingContext
                                    ).data.roles"
                                  >
                                    <div
                                      :key="index"
                                      class="fc-black-13 text-left pT10"
                                      v-if="role && role.name"
                                    >
                                      {{ role.name }}
                                    </div>
                                  </template>
                                </div>
                                <div class="popover-content p10">
                                  <div class="header">GROUPS</div>
                                  <template
                                    v-for="(group,
                                    index) in getDashboardSharedInfo(
                                      d.rawdata.dashboardSharingContext
                                    ).data.groups"
                                  >
                                    <div
                                      class="fc-black-13 text-left pT10"
                                      :key="index"
                                      v-if="group && group.name"
                                    >
                                      {{ group.name }}
                                    </div>
                                  </template>
                                </div>
                                <span slot="reference">
                                  <inline-svg
                                    src="svgs/share"
                                    class="mR14 self-center vertical-text-top fill-grey"
                                    iconClass="icon icon-sm pT3"
                                  ></inline-svg
                                  >Specific
                                </span>
                              </el-popover>
                              <span v-else>
                                <inline-svg
                                  src="svgs/share"
                                  class="mR10 self-center vertical-text-top fill-grey"
                                  iconClass="icon icon-sm pT3"
                                ></inline-svg>
                                {{
                                  d.rawdata.dashboardSharingContext
                                    ? getDashboardSharedInfo(
                                        d.rawdata.dashboardSharingContext
                                      ).shareTo
                                    : ''
                                }}
                              </span>
                            </div>
                          </el-col>
                          <el-col
                            :span="2"
                            class="flex-middle"
                            v-if="d.rawdata && d.rawdata.mobileEnabled"
                          >
                            <img src="~assets/mobile-blue.svg" class="mR15" />
                            <div class="fc-black-12 fw4">Enabled</div>
                          </el-col>
                          <el-col :span="2" class="flex-middle pointer" v-else>
                            <inline-svg
                              src="svgs/mobile-grey"
                              class="mR10 self-center vertical-text-top fill-grey"
                              iconClass="icon icon-sm"
                            ></inline-svg>
                            <div class="fc-black-12 pointer">
                              Disabled
                            </div>
                          </el-col>
                          <el-col
                            :span="4"
                            class="flex-middle justify-content-end text-right position-relative"
                          >
                            <el-dropdown>
                              <span class="el-dropdown-link">
                                <img
                                  src="~assets/menu.svg"
                                  width="12"
                                  height="12"
                                  style="margin-right: 28px;"
                                />
                              </span>
                              <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item v-if="hasCreatePermission">
                                  <div @click="openClonePopup(d)">
                                    {{ $t('common.header.clone_dashboard') }}
                                  </div>
                                </el-dropdown-item>
                                <el-dropdown-item
                                  v-if="
                                    isShowCloneToAnotherApp &&
                                      hasCreatePermission
                                  "
                                >
                                  <div @click="openClonePopup(d, true)">
                                    {{
                                      $t(
                                        'common.dashboard.clone_to_another_app'
                                      )
                                    }}
                                  </div>
                                </el-dropdown-item>
                                <el-dropdown-item v-if="hasCreatePermission">
                                  <div @click="openMoveToPopup(d)">
                                    {{
                                      $t('common.dashboard.move_to_another_app')
                                    }}
                                  </div>
                                </el-dropdown-item>
                                <el-dropdown-item
                                  v-if="
                                    hasDashboardEditPermission &&
                                      hasEditPermission(d)
                                  "
                                >
                                  <div @click="redirectToDashboardEdit(d)">
                                    Edit
                                  </div>
                                </el-dropdown-item>
                                <el-dropdown-item
                                  v-if="d.id > -1 && hasDeletePermission"
                                >
                                  <div @click="deletedashboard(d, index, idx)">
                                    Delete
                                  </div>
                                </el-dropdown-item>
                                <el-dropdown-item
                                  v-if="hasDashboardEditPermission"
                                >
                                  <div
                                    @click="
                                      EnableMobileDashboardInManager(
                                        d,
                                        querryModule
                                      )
                                    "
                                  >
                                    {{
                                      d.rawdata.mobileEnabled
                                        ? $t(
                                            'home.dashboard.disable_mobile_dashboard'
                                          )
                                        : $t(
                                            'home.dashboard.enable_mobile_dashboard'
                                          )
                                    }}
                                  </div>
                                </el-dropdown-item>
                              </el-dropdown-menu>
                            </el-dropdown>
                            <div
                              class="visibility-hide-actions dashboard-manager-collapse-actions"
                            >
                              <div
                                v-if="hasSharePermission"
                                id="share"
                                @click="sharedDashboard(d)"
                              >
                                <inline-svg
                                  src="svgs/share"
                                  class="mR10 self-center vertical-text-top fill-grey"
                                  iconClass="icon icon-sm default share-icon"
                                  title="Share"
                                  v-tippy
                                  data-position="top"
                                  data-arrow="true"
                                ></inline-svg>
                              </div>
                              <div id="open" @click="openDashboard(d)">
                                <inline-svg
                                  src="svgs/link"
                                  class="self-center vertical-text-top fill-grey default"
                                  iconClass="icon icon-sm-md default"
                                  title="Link"
                                  v-tippy
                                  data-position="top"
                                  data-arrow="true"
                                ></inline-svg>
                              </div>
                            </div>
                          </el-col>
                        </el-row>
                      </template>
                    </draggable>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </draggable>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Screens" name="screens">
          <div class="pm-summary-inner-container">
            <div class>
              <div>
                <dashboard-screens
                  :module="module ? module.module : 'workorder'"
                  :activeName="activeName"
                ></dashboard-screens>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <!-- Resource planning -->
        <el-tab-pane label="Remote Screens" name="remote-screens">
          <div class="pm-summary-inner-container">
            <div class>
              <!-- first card card -->
              <div class>
                <dashboard-remote-screens
                  :activeName="activeName"
                ></dashboard-remote-screens>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div>
        <!-- header start-->

        <!-- header end -->
      </div>
      <ShareWith
        ref="dashboard-sharing-section"
        :appId="appId"
        :dashboard="selectedDashboard"
        :dialogVisible.sync="sharedialogVisible"
        v-if="sharedialogVisible && selectedDashboard"
      />
      <el-dialog
        :title="`${updateFolder ? 'Update' : 'Add'} Dashboard Folder`"
        :visible.sync="showAddDashboardfolder"
        width="30%"
        :before-close="closeAddDashboard"
      >
        <div class="height150">
          <el-input
            :autofocus="true"
            class="create-dashboard-input-title fc-input-full-border2"
            v-model="dashboardfolderName"
            placeholder="Enter the Folder Name"
          ></el-input>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="closeAddDashboard()"
            >Cancel</el-button
          >
          <el-button
            v-if="updateFolder"
            class="modal-btn-save"
            type="primary"
            @click="updateDashboardFolder()"
            >Update</el-button
          >
          <el-button
            v-else
            class="modal-btn-save"
            type="primary"
            @click="addDashbopardFolder()"
            >Save</el-button
          >
        </div>
      </el-dialog>
      <div class="dashboard-share dialog-box" v-if="isClone">
        <el-dialog
          title="Clone Dashboard"
          :visible.sync="isClone"
          width="35%"
          class="fc-dialog-center-container"
          :append-to-body="true"
        >
          <div>
            <div class="clonePopup">
              <el-form
                ref="cloneDashboardForm"
                class="fL"
                :rules="rules"
                :model="dashboardCloneObj"
              >
                <!-- <el-form-item label="Dashboard To Clone" class="mB10">
                  <el-input
                    v-model="dashboardCloneObj.dashboard.label"
                    class="width300px fc-input-full-border2 pR15" :disabled="true">
                  </el-input>
                </el-form-item> -->
                <el-form-item
                  label="Dashboard Name"
                  prop="name"
                  :required="true"
                  class="mB10"
                >
                  <el-input
                    v-model="dashboardCloneObj.name"
                    class="width300px fc-input-full-border2 pR15"
                  >
                  </el-input>
                </el-form-item>
                <el-form-item
                  v-if="isCloneToAnotherApp"
                  label="Target Application"
                  prop="name"
                  class="mB10"
                >
                  <el-select
                    @change="
                      loadDashboardFolders(dashboardCloneObj.selectedAppId)
                    "
                    v-model="dashboardCloneObj.selectedAppId"
                    filterable
                    class="fc-input-full-border2 width300px pR15"
                  >
                    <el-option
                      v-for="app in apps"
                      :key="app.linkName"
                      :label="app.name"
                      :value="app.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  v-if="isCloneToAnotherApp"
                  label="Target Folder"
                  prop="folder"
                  :required="true"
                  class="mB10"
                >
                  <el-select
                    v-model="dashboardCloneObj.folder"
                    filterable
                    :required="true"
                    class="fc-input-full-border2 width300px pR15"
                  >
                    <el-option
                      v-for="dashboardFolder in dashboardFoldersList"
                      :key="dashboardFolder.id"
                      :label="dashboardFolder.name"
                      :value="dashboardFolder.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </div>
          </div>
          <span class="dialog-footer row">
            <el-button
              @click="isClone = false"
              class="col-6 shareoverbtn shrbtn1"
            >
              {{ $t('common._common.cancel') }}
            </el-button>
            <el-button
              type="primary"
              class="modal-btn-save"
              :loading="dashboardSaving"
              @click="cloneDashboardToPortal(isCloneToAnotherApp)"
            >
              {{ $t('common._common._save') }}
            </el-button>
          </span>
        </el-dialog>
      </div>
      <div class="dashboard-share dialog-box" v-if="isMoveTo">
        <el-dialog
          title="Move Dashboard"
          :visible.sync="isMoveTo"
          width="35%"
          class="fc-dialog-center-container"
          :append-to-body="true"
        >
          <div>
            <div class="clonePopup">
              <el-form
                ref="moveToDashboardForm"
                class="fL"
                :rules="rules"
                :model="moveDashboardObj"
              >
                <el-form-item label="Target Application" class="mB10">
                  <el-select
                    @change="
                      loadDashboardFolders(moveDashboardObj.selectedAppId)
                    "
                    v-model="moveDashboardObj.selectedAppId"
                    filterable
                    class="fc-input-full-border2 width300px pR15"
                  >
                    <el-option
                      v-for="app in apps"
                      :key="app.linkName"
                      :label="app.name"
                      :value="app.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  label="Target Folder"
                  prop="folder"
                  :required="true"
                  class="mB10"
                >
                  <el-select
                    v-model="moveDashboardObj.folder"
                    filterable
                    :required="true"
                    class="fc-input-full-border2 width300px pR15"
                  >
                    <el-option
                      v-for="dashboardFolder in dashboardFoldersList"
                      :key="dashboardFolder.id"
                      :label="dashboardFolder.name"
                      :value="dashboardFolder.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </div>
          </div>
          <span class="dialog-footer row">
            <el-button
              @click="isMoveTo = false"
              class="col-6 shareoverbtn shrbtn1"
            >
              {{ $t('common._common.cancel') }}
            </el-button>
            <el-button
              type="primary"
              class="modal-btn-save"
              :loading="dashboardSaving"
              @click="moveDashboardToPortal()"
            >
              {{ $t('common._common._save') }}
            </el-button>
          </span>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import DashboardScreens from 'pages/dashboard/components/DashboardScreens'
import DashboardRemoteScreens from 'pages/dashboard/components/DashboardRemoteScreens'
import dashboardLayoutHelper from 'pages/dashboard/helpers/newDashboardlayoutHelper'
import ShareWith from 'pages/dashboard/components/ShareDashboard'
import draggable from 'vuedraggable'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import newDashboardLayoutHelper from 'src/pages/new-dashboard/components/dashboard/DashboardlayoutHelper.js'
import DashboardPermission from 'src/pages/new-dashboard/utils/DashboardPermissions.js'

export default {
  props: ['module'],
  mixins: [
    dashboardLayoutHelper,
    newDashboardLayoutHelper,
    DashboardPermission,
  ],
  components: {
    DashboardScreens,
    DashboardRemoteScreens,
    draggable,
    ShareWith,
  },
  data() {
    return {
      dashboardfolderName: null,
      appId: null,
      apps: [],
      updateFolder: false,
      updateFolderData: null,
      showAddDashboardfolder: false,
      tabs: [
        {
          label: 'Screens',
          value: 'screens',
        },
        {
          label: 'Remote Screens',
          value: 'remote_screens',
        },
      ],
      selectedTab: null,
      activeScreen1: [],
      activeName: 'dashboard',
      sharedialogVisible: false,
      selectedDashboard: null,
      publishdialogVisible: false,
      dashboardlist: {
        workorder: {
          dashboards: [],
          displayName: 'Maintenance',
          key: 'workorder',
          buildings: [],
          license: 'MAINTENANCE',
          createLink: '/app/home/editdashboard/new?create=new',
        },
        alarm: {
          dashboards: [],
          displayName: 'Fault detection and diagnostics',
          key: 'alarm',
          buildings: [],
          license: 'ALARMS',
          createLink: '/app/home/editdashboard/new?create=new',
        },
        energydata: {
          dashboards: [],
          displayName: 'Building performance',
          key: 'energydata',
          buildings: [],
          license: 'ENERGY',
          createLink: '/app/home/editdashboard/new?create=new',
        },
      },
      isClone: false,
      isMoveTo: false,
      dashboardCloneObj: {
        folder: null,
        selectedAppId: null,
        dashboard: null,
        name: null,
      },
      moveDashboardObj: { folder: null, selectedAppId: null, dashboard: null },
      dashboardFoldersList: [],
      dashboardSaving: false,
      isCloneToAnotherApp: false,
      isShowCloneToAnotherApp: false,
      rules: {
        folder: {
          required: true,
          message: this.$t('common.placeholders.enter_folder_name'),
        },
        name: {
          required: true,
          message: this.$t('common.placeholders.enter_dashboard_name'),
          trigger: 'change',
        },
      },
      isPortalAppSelected: null,
    }
  },
  created() {
    this.$store.dispatch('loadRoles')
  },
  mounted() {
    this.availableApps()
  },
  computed: {
    querryModule() {
      if (this.$route.query.module) {
        return this.$route.query.module
      } else {
        return 'workorder'
      }
    },
    getLicenseEnabledModules() {
      return this.dashboardlist.filter(
        module => this.$helpers.isLicenseEnabled(module.license) === true
      )
    },
    dashboardChildrenOptions() {
      return {
        group: 'dashboardChildren',
        disabled: true,
      }
    },
    dashboardDragOptions() {
      return {
        group: 'dashboard',
      }
    },
    dashboardFolderDragOptions() {
      return {
        group: 'dashboardFolder',
      }
    },
  },
  methods: {
    availableApps() {
      API.get(`v2/application/list`).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.apps = data.application
          if (this?.apps?.length > 1) {
            this.isShowCloneToAnotherApp = true
          }
          this.appId = (getApp() || {}).id
          if (isEmpty(this.appId)) {
            let defaultApp =
              this.apps.find(app => app.linkName === 'newapp') || this.apps[0]
            this.appId = defaultApp.id
            this.dashboardCloneObj.selectedAppId = this.appId
          }
          this.dashboardCloneObj.selectedAppId = this.appId
          this.loadDashboards(true, this.appId)
        }
      })
    },
    loadDashboardAndSetAppsType(redirect, appId) {
      this.loadDashboards(redirect, appId)
      if (!isEmpty(this.apps)) {
        let {
          appCategory: { PORTALS },
        } = this.$constants
        let isPoralApp = this.apps.filter(app => {
          return app.id == this.appId && app.appCategory == PORTALS
        })
        this.isPortalAppSelected = !isEmpty(isPoralApp) ? true : false
      }
    },
    openClonePopup(d, isCloneToAnotherApp) {
      this.isClone = true
      this.dashboardCloneObj.dashboard = d
      this.dashboardCloneObj.name = d.label
      this.isCloneToAnotherApp = isCloneToAnotherApp
        ? isCloneToAnotherApp
        : false
    },
    openMoveToPopup(d) {
      this.isMoveTo = true
      this.moveDashboardObj.dashboard = d
    },
    updateFolderName(folder) {
      this.updateFolder = true
      this.dashboardfolderName = folder.label
      this.updateFolderData = folder.rawdata
      this.showAddDashboardfolder = true
    },
    updateDashboardFolder() {
      let { dashboardfolderName, updateFolderData } = this
      this.closeAddDashboard()
      let reportFolderObj = {
        name: dashboardfolderName,
        id: updateFolderData.id,
      }
      this.$http
        .post(`dashboard/updateDashboardFolder?moduleName=workorder`, {
          dashboardFolderContext: reportFolderObj,
        })
        .then(() => {
          this.updateFolderDataName()
          this.$message({
            message: 'Dashboard Folder updated',
            type: 'success',
          })
        })
        .catch(() => {
          this.$message({
            message: 'Dashboard Folder not saved',
            type: 'error',
          })
        })
    },
    updateFolderDataName() {
      let findIndex = this.headerList.findIndex(
        rt => rt.id === this.updateFolderData.id
      )
      this.headerList[findIndex].label = this.dashboardfolderName
      this.updateFolder = false
      this.dashboardfolderName = null
    },
    addDashbopardFolder() {
      let { dashboardfolderName, appId } = this
      this.closeAddDashboard()
      let reportFolderObj = {
        name: dashboardfolderName,
      }
      if (!isEmpty(appId)) {
        reportFolderObj.appId = appId
      }
      this.$http
        .post(`dashboard/addDashboardFolder?moduleName=workorder`, {
          dashboardFolderContext: reportFolderObj,
        })
        .then(response => {
          let dashboardFolderContext = response.data.dashboardFolderContext
          this.pushDashboardFolder(dashboardFolderContext)
          this.dashboardfolderName = null
          this.$message({
            message: 'Dashboard Folder added',
            type: 'success',
          })
        })
        .catch(() => {
          this.$message({
            message: 'Dashboard Folder not saved',
            type: 'error',
          })
        })
    },
    pushDashboardFolder(folder) {
      let data = {
        childrens: [],
        id: folder.id,
        label: folder.name,
        path: {},
        rawdata: folder,
      }
      data.rawdata.displayOrder = this.headerList.length + 1
      this.headerList.push(data)
    },
    closeAddDashboard() {
      this.showAddDashboardfolder = false
    },
    addFolder() {
      this.showAddDashboardfolder = true
    },
    publishDashboard(d) {
      this.sharedialogVisible = false
      this.selectedDashboard = d
      this.publishdialogVisible = true
    },
    sharedDashboard(d) {
      this.sharedialogVisible = true
      this.selectedDashboard = d
      this.publishdialogVisible = false
    },
    redirectToDashboardCreation() {
      let { appId } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_CREATION) || {}

        name &&
          this.$router.push({
            name,
            query: {
              create: 'new',
              appId: appId,
            },
          })
      } else {
        let { dashboardlist, querryModule } = this
        let link = dashboardlist[querryModule].createLink
        if (appId > 0) {
          link += `&appId=${appId}`
        }
        this.$router.push(link)
      }
    },
    redirectToDashboardEdit(dashboardObj) {
      let { path, linkName } = dashboardObj
      let { appId } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_EDITOR) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              dashboardlink: linkName,
            },
            query: {
              create: 'edit',
              appId: appId,
            },
          })
        }
      } else {
        let link = path.editPath
        if (appId > 0) {
          link += `&appId=${appId}`
        }
        this.$router.push({
          path: link,
        })
      }
    },
    openDashboard(dashboardObj) {
      let { path, linkName } = dashboardObj

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_VIEWER) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              dashboardlink: linkName,
            },
          })
        }
      } else {
        this.$router.push({
          path: path.path,
        })
      }
    },
    switchdashboard(command) {
      this.loadDashboardsList()
      this.$router.replace({
        query: {
          module: command,
        },
      })
    },
    loadDashboardsList() {
      if (this.querryModule === 'energydata') {
        this.loadAll = true
        let self = this
        self.managerloading = true
        let self2 = self
        Promise.all([
          this.loadBuildings({
            module: 'energydata',
          }),
          this.loadSite(),
        ])
          .then(() => {
            self.loadDashboardList(true)
            if (self2.dashboardlist[self2.querryModule].dashboards.length) {
              self2.activeScreen = [
                self2.dashboardlist[self2.querryModule].dashboards[0].id,
              ]
            }
          })
          .catch(() => {})
      } else if (this.querryModule === 'alarm') {
        this.loadAll = true
        let self = this
        self.managerloading = true
        let self2 = self
        Promise.all([
          this.loadBuildings({
            module: 'alarm',
          }),
          this.loadSite(),
        ])
          .then(() => {
            self.loadDashboardList(true)
            if (self2.dashboardlist[self2.querryModule].dashboards.length) {
              self2.activeScreen = [
                self2.dashboardlist[self2.querryModule].dashboards[0].id,
              ]
            }
          })
          .catch(() => {})
      } else {
        this.loadAll = true
        let self = this
        let self2 = self
        self.managerloading = true
        Promise.all([
          this.loadBuildings({
            module: 'alarm',
          }),
          this.loadSite(),
        ])
          .then(() => {
            self.loadDashboardList(true)
            if (self2.dashboardlist[self2.querryModule].dashboards.length) {
              self2.activeScreen = [
                self2.dashboardlist[self2.querryModule].dashboards[0].id,
              ]
            }
          })
          .catch(() => {})
      }
    },
    onDashboardchildrenChange() {},
    addDashboardfolder() {},
    updateDashboards(dashboards) {
      let self = this
      this.$http
        .post('dashboard/updateDashboard', {
          dashboards: dashboards,
        })
        .then(function() {
          self.$message.success('Dashboard list updated')
        })
        .catch(() => {})
    },
    updateDashboardFoler(dashboards) {
      let self = this
      this.$http
        .post('dashboard/updateDashboardFolder', {
          dashboardFolders: dashboards,
        })
        .then(function() {
          self.$message.success('Dashboard folder updated')
        })
        .catch(() => {})
    },
    onFolderChange(data) {
      if (data['moved'] && data['moved'].element) {
        let i = 1
        this.headerList.forEach(dashboard => {
          dashboard.displayOrder = i
          i++
        })
        this.updateDashboardFoler(this.headerList)
      }
    },
    onDashBoardChange(data) {
      let dashboardList = this.headerList
      if (data['moved'] && data['moved'].element) {
        for (let i = 0; i < dashboardList.length; i++) {
          let list = dashboardList[i]
          if (list.childrens.find(rl => rl.id === data['moved'].element.id)) {
            let listdata = []
            let j = 1
            list.childrens.forEach(dashboard => {
              listdata.push({
                id: dashboard.id,
                displayOrder: j,
                dashboardFolderId: list.id,
              })
              j++
            })
            this.updateDashboards(listdata)
          }
        }
      } else if (data['added'] && data['added'].element) {
        for (let i = 0; i < dashboardList.length; i++) {
          let list = dashboardList[i]
          if (list.childrens.find(rl => rl.id === data['added'].element.id)) {
            let listdata = []
            let j = 0
            list.childrens.forEach(dashboard => {
              listdata.push({
                id: dashboard.id,
                displayOrder: j,
                dashboardFolderId: list.id,
              })
              j++
            })
            this.updateDashboards(listdata)
          }
        }
      }
    },
  },
}
</script>

<style>
#share,
#publish,
#open {
  display: inline;
}
.el-collapse-item__header.is-active .icon-open {
  display: inline-flex !important;
}
.el-collapse-item__header.is-active .icon-close {
  display: none !important;
}
.icon-open {
  display: none;
}
.clonePopup {
  min-height: 175px;
  overflow: auto;
  padding-bottom: 70px;
}
</style>
<style lang="scss">
.views-manager-container {
  .header-container {
    display: flex;
    padding: 20px 20px 25px;
    background: #fff;
  }

  .btn-container {
    .manager-primary-btn,
    .manager-secondary-btn {
      line-height: normal;
      padding: 11px 17px;

      .btn-label {
        text-transform: uppercase;
        font-size: 12px;
        letter-spacing: 1px;
        font-weight: bold;
        cursor: pointer;
        text-align: center;
      }
    }

    .manager-primary-btn {
      background-color: #fff;
      border: solid 1px #ee518f;

      .btn-label {
        color: #ef508f;
      }
    }

    .manager-secondary-btn {
      background-color: #ef508f;
      border: solid 1px transparent;

      .btn-label {
        color: #fff;
      }
    }
  }

  .manager-tabs-container {
    .el-tabs__header {
      background: #fff;
      padding: 0 20px;
      margin: 0px;
    }

    .el-tabs__content {
      padding: 15px 20px;

      .el-collapse {
        &.folder-collapse {
          border-radius: 5px;
          border: solid 1px #f0eff0;
        }
      }
    }

    .manager-views-item {
      &:hover {
        box-shadow: 0 2px 10px 4px rgba(215, 222, 229, 0.41);
      }

      .el-collapse-item__header {
        padding: 0 12px 0 20px;
      }

      .el-collapse-item__content {
        padding-bottom: 0px;

        .views-item {
          border-top: 1px solid #f0eff0;

          &:hover {
            background-color: #fcfcfc;
          }

          display: flex;
          height: 54px;
          padding-left: 50px;

          .icon {
            &.views-list {
              fill: #353f54;
            }
          }

          .shared-label {
            font-size: 13px;
            letter-spacing: 0.5px;
            color: #8ca0ad;
          }

          .shared-txt {
            font-size: 13px;
            color: #324056;
          }
        }
      }
    }
  }
  .dashboard-manager-collapse-actions {
    padding-bottom: 0px;
  }
}
</style>
