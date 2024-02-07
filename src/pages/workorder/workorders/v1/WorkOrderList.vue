<template>
  <CommonListLayout
    :moduleName="currentModuleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => viewName"
    :hideSubHeader="isEmpty(selectedWorkorders)"
    :pathPrefix="'/app/wo/orders'"
    :recordCount="listCount"
    :recordLoading="loading"
    class="min-width0 min-height0 wo-list-container"
  >
    <template #header>
      <AdvancedSearchWrapper
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>
      <template v-if="!showSearch">
        <CustomButton
          class="custom-button-top"
          :moduleName="currentModuleName"
          :position="POSITION.LIST_TOP"
          :transformFn="transformFormData"
          updateUrl="/v2/workorders/update"
          @onSuccess="
            () => {
              loadWorkOrders()
            }
          "
          @onError="() => {}"
        />
        <button
          v-if="$hasPermission('workorder:CREATE')"
          class="fc-create-btn uppercase"
          @click="redirectToCreation()"
        >
          {{ $t('maintenance._workorder.newworkorder') }}
        </button>
      </template>
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(selectedWorkorders)">
        <pagination
          :total="listCount"
          :perPage="50"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="listCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="right"
        >
          <sort
            :key="currentModuleName + '-sort'"
            :config="sortConfig"
            :sortList="sortConfigLists"
            :excludeFields="excludedSortFields"
            @onchange="updateSort"
          ></sort>
        </el-tooltip>
        <span class="separator">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.export')"
          placement="left"
        >
          <f-export-settings
            :module="currentModuleName"
            :viewDetail="viewDetail"
            :showViewScheduler="true"
            :showMail="true"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
      </template>
    </template>

    <div class="height100 overflow-scroll-flex min-width0 min-height0">
      <div
        class="p10 pT0  pB20 position-relative overflow-scroll-flex min-width0 min-height0 height100"
      >
        <div
          class="full-layout-white fc-border-1 scrollbar-style overflow-scroll-flex min-width0 min-height0"
        >
          <div class="fc-overflow-y">
            <table
              class="min-width100 fc-list-view-table wo-table workorder-table-list scrollbar-style new-fc-list-view-table pB100"
            >
              <thead>
                <tr v-if="!selectedWorkorders.length">
                  <th class="width25px">
                    <el-checkbox
                      v-model="selectAll"
                      @change="selectAllWorkorders"
                      class="mL3"
                    ></el-checkbox>
                  </th>
                  <th class="text-left wo_td_sub_width">
                    {{ $t('common._common.id') }}
                  </th>
                  <th class="text-left wo_td_sub_width">
                    {{ $t('maintenance.wr_list.subject') }}
                  </th>
                  <template v-for="(field, index) in viewColumns">
                    <th
                      class="text-left min-width110px"
                      v-if="!isFixedColumn(field.name)"
                      :key="index"
                    >
                      <div>{{ field.displayName | upperCase }}</div>
                    </th>
                  </template>
                  <th
                    class="text-left"
                    v-if="
                      canShowColumn(
                        'noOfTasks',
                        'noOfNotes',
                        'noOfAttachments',
                        'attachmentPreview'
                      )
                    "
                  ></th>
                  <th class="text-left width40px"></th>
                  <th class="text-left"></th>

                  <th></th>
                </tr>
                <!-- actions btn start -->
                <tr v-else>
                  <th class="width25px">
                    <el-checkbox
                      v-model="selectAll"
                      @change="selectAllWorkorders"
                      class="mL3"
                    ></el-checkbox>
                  </th>
                  <th colspan="8" class="p8">
                    <div class="pull-left">
                      <div class="action-btn-slide btn-block flex">
                        <button
                          class="btn btn--tertiary"
                          :class="{ disabled: actions.assignLoading }"
                        >
                          <i
                            class="fa fa-circle-o-notch b-icon fa-spin"
                            aria-hidden="true"
                            v-if="actions.assignLoading"
                          ></i>
                          <i class="fa fa-user-o b-icon" v-else></i>
                          {{ $t('common._common.assign') }}
                          <q-popover
                            ref="globalAssignToPopover"
                            v-if="
                              !actions.assignLoading &&
                              hasWorkOrderClosePermission
                            "
                          >
                            <q-list link class="scroll min-width150">
                              <q-item
                                v-for="(user, usridx) in users"
                                @click="
                                  assignWorkOrder(selectedWorkorders, user),
                                    $refs.globalAssignToPopover.close()
                                "
                                :key="'user' + usridx"
                              >
                                <q-item-side
                                  icon="fa-user"
                                  class="assign-icon f11 secondary-color2 wo_assign-icon_user"
                                ></q-item-side>
                                <q-item-main
                                  :label="user.name"
                                  :sublabel="user.email"
                                />
                              </q-item>
                              <q-item
                                v-for="(group, grpidx) in groups"
                                @click="
                                  assignWorkOrder(
                                    selectedWorkorders,
                                    null,
                                    group
                                  ),
                                    $refs.globalAssignToPopover.close()
                                "
                                :key="'grpidx' + grpidx"
                              >
                                <q-item-side
                                  icon="fa-users"
                                  class="assign-icon f11 secondary-color2 wo_assign-icon_user"
                                ></q-item-side>
                                <q-item-main
                                  :label="group.name"
                                  :sublabel="
                                    group.description
                                      ? group.description
                                      : '---'
                                  "
                                />
                              </q-item>
                            </q-list>
                          </q-popover>
                        </button>
                        <button
                          class="btn btn--tertiary"
                          @click="closeWorkOrder(selectedWorkorders)"
                          :class="{ disabled: actions.closeLoading }"
                        >
                          <i
                            class="fa fa-circle-o-notch b-icon fa-spin"
                            aria-hidden="true"
                            v-if="actions.closeLoading"
                          ></i>
                          <i class="fa fa-check-circle-o b-icon" v-else></i>
                          {{ $t('common._common.close') }}
                        </button>
                        <button
                          class="btn btn--tertiary"
                          @click="bulkbool = true"
                        >
                          <i class="fa fa-refresh b-icon"></i>
                          {{ $t('common._common.bulk_update') }}
                        </button>
                        <button
                          class="btn btn--tertiary"
                          @click="deleteWorkOrder(selectedWorkorders)"
                          :class="{ disabled: actions.deleteLoading }"
                        >
                          <i
                            class="fa fa-circle-o-notch b-icon fa-spin"
                            aria-hidden="true"
                            v-if="actions.deleteLoading"
                          ></i>
                          <i class="fa fa-trash-o b-icon" v-else></i>
                          {{ $t('common._common.delete') }}
                        </button>

                        <button
                          class="btn btn--tertiary"
                          @click="openTaskOptions = true"
                        >
                          <i class="el-icon-printer"></i>
                          {{ $t('common._common.print') }}
                        </button>
                      </div>
                    </div>
                  </th>
                </tr>
                <!-- actions btn end -->
              </thead>
              <tbody v-if="loading">
                <tr>
                  <td colspan="100%" class="text-center">
                    <spinner :show="loading" size="80"></spinner>
                  </td>
                </tr>
              </tbody>
              <tbody v-else>
                <tr class="nowotd" v-if="!workorders.length">
                  <td colspan="100%" class="height80vh">
                    <div class="row container">
                      <div class="justify-center nowo">
                        <div>
                          <inline-svg
                            src="svgs/emptystate/workorder"
                            iconClass="icon text-center icon-xxxxlg"
                          ></inline-svg>
                          <div class="nowo-label">
                            {{ $t('maintenance._workorder.no_wo_title') }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>

                <tr
                  v-bind:class="{
                    selected: selectedWorkorders.indexOf(workorder.id) >= 0,
                  }"
                  class="tablerow workorder-row fc-new-table-row line-height24"
                  v-for="workorder in workorders"
                  :key="workorder.id"
                >
                  <td class="width25px">
                    <template
                      v-if="$getProperty(workorder, 'moduleState.id', null)"
                    >
                      <i
                        v-if="isRecordLocked(workorder)"
                        class="fa fa-lock locked-wo"
                        data-arrow="true"
                        :title="$t('maintenance.approval.waiting_for_approval')"
                        v-tippy
                      ></i>
                      <el-checkbox
                        v-else
                        :value="selectedWorkorders.indexOf(workorder.id) >= 0"
                        @change="toggleSelection(workorder.id)"
                        class="pL3"
                      ></el-checkbox>
                    </template>
                    <el-checkbox
                      v-else
                      :value="selectedWorkorders.indexOf(workorder.id) >= 0"
                      @change="toggleSelection(workorder.id)"
                      class="pL3"
                    ></el-checkbox>
                  </td>
                  <template v-if="workorder && workorder.id">
                    <td class="text-left min_width100">
                      <div class="q-item-division relative-position ellipsis">
                        <div class="q-item-main q-item-section">
                          <div class="flex-middle flex-no-wrap">
                            <div class="q-item-sublabel wl-id color-2">
                              #{{ workorder.serialNumber }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </td>
                    <td
                      class="text-left min_width100 main-field-column"
                      @click="openSummaryView(workorder.id)"
                    >
                      <div class="q-item-division relative-position ellipsis">
                        <div class="q-item-main q-item-section">
                          <div
                            v-tippy="{ placement: 'top' }"
                            small
                            :title="workorder.subject"
                            class="q-item-label ellipsis primary-field"
                          >
                            {{ workorder.subject }}
                          </div>
                          <div class="flex-middle flex-no-wrap">
                            <div
                              v-if="workorder.dueDate !== -1"
                              class="q-item-sublabel wl-duedate fc-due-date"
                              :title="$t('maintenance.wr_list.due_by_time')"
                              v-tippy
                            >
                              <i
                                class="fa fa-clock-o fc-due-date-clock"
                                aria-hidden="true"
                              ></i>
                              &nbsp;&nbsp;{{ workorder.dueDate | fromNow }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </td>
                  </template>
                  <template v-for="(field, index) in viewColumns">
                    <td
                      class="text-left wo-table-width min-width100"
                      v-if="!isFixedColumn(field.name)"
                      :key="index + field.name"
                    >
                      <div v-if="field.name === 'siteId'">
                        <span class="q-item-label">
                          {{
                            workorder.siteId && siteMap[workorder.siteId]
                              ? siteMap[workorder.siteId]
                              : '---'
                          }}
                        </span>
                      </div>

                      <div
                        v-else-if="
                          field.name === 'category' &&
                            field.parentFieldId === -1
                        "
                        class="min-width130"
                      >
                        <span
                          class="q-item-label"
                          :class="{ 'color-d': !workorder.category }"
                        >
                          {{
                            workorder.category
                              ? (getTicketCategory(workorder.category.id) || {})
                                  .displayName || '---'
                              : '---'
                          }}
                        </span>
                        <span
                          class="picklist-downarrow"
                          v-if="canEditRecord(workorder)"
                        >
                          <i
                            aria-hidden="true"
                            class="fa fa-sort-desc wl-icon-downarrow"
                          ></i>
                        </span>
                        <q-popover
                          ref="categorypopover"
                          v-if="canEditRecord(workorder)"
                        >
                          <q-list link class="scroll min-width150">
                            <q-item
                              v-for="category in ticketcategory"
                              :key="category.id"
                              @click="
                                updateWorkOrder([workorder.id], {
                                  category: {
                                    id: category.id,
                                    name: category.displayName,
                                  },
                                }),
                                  $refs.categorypopover[index].close()
                              "
                            >
                              <q-item-main :label="category.displayName" />
                            </q-item>
                          </q-list>
                        </q-popover>
                      </div>

                      <div
                        v-else-if="field.name === 'resource'"
                        class="q-item-division relative-position"
                        @click="canShowSpaceAssetChooser(workorder)"
                        style="min-width: 170px"
                      >
                        <div
                          v-if="
                            workorder.resource && workorder.resource.id !== -1
                          "
                        >
                          <div class="flLeft mR7">
                            <img
                              v-if="workorder.resource.resourceType === 1"
                              src="~statics/space/space-resource.svg"
                              style="height: 12px; width: 14px"
                            />
                            <img
                              v-else
                              src="~statics/space/asset-resource.svg"
                              style="height: 11px; width: 14px"
                            />
                          </div>
                          <span
                            class="flLeft q-item-label ellipsis max-width140px"
                            v-tippy
                            small
                            data-position="bottom"
                            :title="workorder.resource.name"
                            >{{ workorder.resource.name }}</span
                          >
                        </div>
                        <div v-else>
                          <span class="q-item-label secondary-color color-d">
                            ---{{ $t('maintenance.wr_list.space_asset') }}---
                          </span>
                        </div>
                      </div>

                      <div
                        v-else-if="field.name === 'assignedTo' || !field.name"
                        class="q-item-division relative-position wo_assignedto_avatarblock"
                      >
                        <div class="wo-assigned-avatar fL">
                          <user-avatar
                            size="md"
                            :user="workorder.assignedTo"
                            :group="workorder.assignmentGroup"
                            :showPopover="true"
                            :showLabel="true"
                            :moduleName="currentModuleName"
                          ></user-avatar>
                        </div>
                        <f-assignment
                          @assignactivity="onAssignWo"
                          v-if="canEditRecord(workorder)"
                          viewtype="view"
                          :record="workorder"
                          :siteId="$getProperty(workorder, 'siteId', null)"
                        ></f-assignment>
                      </div>
                      <div
                        v-else-if="field.name === 'status'"
                        class="width110px"
                      >
                        <span class="q-item-label">
                          {{
                            $getProperty(
                              workorder.moduleState,
                              'displayName',
                              '---'
                            )
                          }}
                        </span>
                      </div>
                      <div
                        v-else-if="field.name === 'sourceType'"
                        class="width110px"
                      >
                        <span
                          v-if="workorder.sourceType && workorder.sourceTypeVal"
                          class="q-item-label"
                        >
                          {{
                            workorder.sourceTypeVal
                              ? workorder.sourceTypeVal
                              : '---'
                          }}
                        </span>
                        <span v-else class="q-item-label">---</span>
                      </div>
                      <div
                        v-else-if="field.name === 'priority'"
                        class="width110px flex-middle"
                      >
                        <span class="q-item-label" v-if="workorder.priority">
                          <i
                            class="fa fa-circle prioritytag"
                            v-bind:style="{
                              color: getTicketPriority(workorder.priority.id)
                                .colour,
                            }"
                            aria-hidden="true"
                          ></i>
                          {{
                            workorder.priority.id > 0
                              ? getTicketPriority(workorder.priority.id)
                                  .displayName
                              : '---'
                          }}
                        </span>
                        <span v-else class="color-d">---</span>
                        <span
                          class="picklist-downarrow"
                          v-if="canEditRecord(workorder)"
                        >
                          <i
                            aria-hidden="true"
                            class="fa fa-sort-desc wl-icon-downarrow"
                          ></i>
                        </span>
                        <q-popover
                          ref="prioritypopover"
                          v-if="canEditRecord(workorder)"
                        >
                          <q-list link class="scroll min-width150">
                            <q-item
                              v-for="(priority, index) in ticketpriority"
                              @click="
                                updateWorkOrder([workorder.id], {
                                  priority: {
                                    id: priority.id,
                                    priority: priority.priority,
                                  },
                                }),
                                  $refs.prioritypopover[index].close()
                              "
                              :key="index"
                            >
                              <q-item-main :label="priority.displayName" />
                            </q-item>
                          </q-list>
                        </q-popover>
                      </div>

                      <div
                        v-else-if="
                          $getProperty(field, 'field.displayType') ===
                            'SIGNATURE'
                        "
                      >
                        <SignatureField
                          :field="(field || {}).field"
                          :record="workorder"
                        />
                      </div>

                      <div
                        v-else-if="field.name === 'type'"
                        class="q-item-division flex-middle width150px"
                      >
                        <div
                          v-tippy
                          small
                          :title="getColumnDisplayValue(field, workorder)"
                          class="q-item-label ellipsis table-field relative-position ellipsis"
                          :class="{
                            'color-d': !getColumnDisplayValue(field, workorder),
                          }"
                        >
                          {{ getColumnDisplayValue(field, workorder) || '---' }}
                        </div>
                        <el-popover
                          placement="bottom"
                          width="200"
                          trigger="click"
                          popper-class="fcPopover"
                        >
                          <div>
                            <ul>
                              <li
                                @click="
                                  updateWorkOrder([workorder.id], {
                                    data: { [field.name]: value },
                                  })
                                "
                                v-for="(value, key) in ticketType"
                                :key="key"
                                class="el-dropdown-menu__item"
                              >
                                {{ value.name }}
                              </li>
                            </ul>
                          </div>
                          <i
                            v-if="canEditRecord(workorder)"
                            slot="reference"
                            aria-hidden="true"
                            class="fa fa-sort-desc wl-icon-downarrow picklist-downarrow"
                          ></i>
                        </el-popover>
                      </div>

                      <div
                        v-else-if="
                          field.field && field.field.displayTypeInt === 33
                        "
                      >
                        <f-image-preview
                          :file="
                            $helpers.getFileObjectFromRecord(workorder, field)
                          "
                        ></f-image-preview>
                      </div>

                      <div
                        v-else-if="!field.isDefault"
                        class="q-item-division flex-middle width150px"
                      >
                        <div
                          v-tippy
                          small
                          :title="getColumnDisplayValue(field, workorder)"
                          class="q-item-label ellipsis table-field relative-position"
                          :class="{
                            'color-d': !getColumnDisplayValue(field, workorder),
                          }"
                        >
                          {{ getColumnDisplayValue(field, workorder) || '---' }}
                        </div>
                        <el-popover
                          placement="bottom"
                          width="200"
                          trigger="click"
                          popper-class="fcPopover"
                        >
                          <div>
                            <ul
                              v-if="
                                field.field &&
                                  field.field.dataTypeEnum === 'ENUM'
                              "
                            >
                              <li
                                @click="
                                  updateWorkOrder([workorder.id], {
                                    data: { [field.name]: value },
                                  })
                                "
                                v-for="(value, key) in field.field.enumMap"
                                :key="key"
                                :label="value"
                                :value="key"
                                class="el-dropdown-menu__item"
                              >
                                {{ value }}
                              </li>
                            </ul>
                            <ul
                              v-if="
                                field.field &&
                                  field.field.dataTypeEnum === 'BOOLEAN'
                              "
                            >
                              <li
                                @click="
                                  updateWorkOrder([workorder.id], {
                                    data: { [field.name]: true },
                                  })
                                "
                                :label="
                                  field.field.trueVal
                                    ? field.field.trueVal
                                    : 'Yes'
                                "
                                value="true"
                                class="el-dropdown-menu__item"
                              >
                                {{
                                  field.field.trueVal
                                    ? field.field.trueVal
                                    : 'Yes'
                                }}
                              </li>
                              <li
                                @click="
                                  updateWorkOrder([workorder.id], {
                                    data: { [field.name]: false },
                                  })
                                "
                                :label="
                                  field.field.falseVal
                                    ? field.field.falseVal
                                    : 'No'
                                "
                                value="false"
                                class="el-dropdown-menu__item"
                              >
                                {{
                                  field.field.falseVal
                                    ? field.field.falseVal
                                    : 'No'
                                }}
                              </li>
                            </ul>
                          </div>
                        </el-popover>
                      </div>
                      <div
                        v-else
                        class="q-item-division relative-position ellipsis"
                      >
                        <div class="q-item-main q-item-section">
                          <div
                            v-tippy
                            small
                            :title="getColumnDisplayValue(field, workorder)"
                            class="q-item-label ellipsis table-field"
                            :class="{
                              'color-d': !getColumnDisplayValue(
                                field,
                                workorder
                              ),
                            }"
                          >
                            {{
                              getColumnDisplayValue(field, workorder) || '---'
                            }}
                          </div>
                        </div>
                      </div>
                    </td>
                  </template>
                  <td
                    class="stats secondary-color2 wo_notes_count"
                    v-if="canShowColumn('noOfNotes')"
                    v-on:click.stop="
                      ;(workorderSelected = workorder), (showcomment = true)
                    "
                  >
                    <div
                      style="float: left"
                      v-if="canShowColumn('noOfNotes')"
                      v-on:click.stop="
                        ;(workorderSelected = workorder), (showcomment = true)
                      "
                    >
                      <span>
                        <img
                          src="~assets/comment.svg"
                          style="width: 14px; height: 12.8px"
                        />
                      </span>
                      <span class="width5px q-item-sublabel comment-counnt-txt">
                        {{
                          workorder.noOfNotes == -1 || workorder.noOfNotes == 0
                            ? '0'
                            : workorder.noOfNotes
                        }}
                      </span>
                    </div>
                  </td>
                  <td
                    class="stats secondary-color2"
                    v-if="canShowColumn('noOfTasks')"
                  >
                    <div class="fL" v-if="canShowColumn('noOfTasks')">
                      <q-icon
                        style="color: #ddd !important"
                        v-if="
                          workorder.noOfTasks != -1 && workorder.noOfNotes != 0
                        "
                        name="fa-tasks wl-table-icon"
                      />
                      <span class="q-item-sublabel pL5">
                        {{
                          workorder.noOfTasks == -1 || workorder.noOfNotes == 0
                            ? ''
                            : (workorder.noOfClosedTasks == -1
                                ? '0'
                                : workorder.noOfClosedTasks) +
                              '/' +
                              workorder.noOfTasks
                        }}
                      </span>
                    </div>
                    <div style="clear: both"></div>
                  </td>
                  <td
                    class="stats secondary-color2"
                    v-if="canShowColumn('noOfAttachments')"
                  >
                    <div
                      style="float: left"
                      v-if="
                        canShowColumn('noOfAttachments') &&
                          workorder.noOfAttachments &&
                          workorder.noOfAttachments > 0
                      "
                    >
                      <span>
                        <img
                          src="~assets/attachement-grey.svg"
                          v-on:click.stop="
                            ;(workorderSelected = workorder),
                              (showattachment = true)
                          "
                          style="width: 14px; height: 12.8px"
                        />
                      </span>
                      <span class="width5px q-item-sublabel comment-counnt-txt">
                        {{
                          workorder.noOfAttachments == -1 ||
                          workorder.noOfAttachments == 0
                            ? '0'
                            : workorder.noOfAttachments
                        }}
                      </span>
                    </div>
                  </td>
                  <td
                    class="secondary-color2"
                    v-if="canShowColumn('attachmentPreview')"
                  >
                    <div
                      class="text-align-center"
                      v-if="canShowColumn('attachmentPreview')"
                    >
                      <f-list-attachment-preview
                        module="ticketattachments"
                        :record="workorder"
                      ></f-list-attachment-preview>
                    </div>
                  </td>

                  <td>
                    <div class="d-flex flex-row mR3">
                      <div
                        v-if="showButtonsForListItems"
                        class="d-flex mL-auto justify-center width200px custom-button-container"
                      >
                        <CustomButton
                          :moduleName="currentModuleName"
                          :record="workorder"
                          :position="POSITION.LIST_ITEM"
                          :customButtonList="customButtonList"
                          :transformFn="transformFormData"
                          updateUrl="/v2/workorders/update"
                          class="custom-button"
                          @onSuccess="loadWorkOrders"
                          @onError="() => {}"
                        ></CustomButton>
                      </div>
                      <div class="mR3">
                        <i
                          class="fa fa-ellipsis-v secondary-color2"
                          aria-hidden="true"
                        ></i>

                        <q-popover
                          ref="morepopover"
                          width="200"
                          trigger="click"
                        >
                          <q-list>
                            <q-item
                              tag="label"
                              style="padding: 5px 15px; text-align: center"
                              @click="deleteWorkOrder([workorder.id])"
                            >
                              <q-item-main>Delete</q-item-main>
                            </q-item>
                          </q-list>
                        </q-popover>
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div
            class="view-column-chooser fc-new-view-column-chooser wo_fcnew_view_column_chooser"
            @click="showColumnSettings = true"
          >
            <img
              src="~assets/column-setting.svg"
              class="wo_fcnew_view_column_chooser-edit"
            />
          </div>
        </div>
      </div>
      <q-modal ref="basicModal">
        <div class="saveasview p20">
          <div class="saveasviewheader f20">
            {{ $t('maintenance._workorder.save_as') }}
          </div>
          <div></div>
          <q-btn small color="primary" @click="saveAsNewView">
            {{ $t('maintenance._workorder.save') }}
          </q-btn>
        </div>
      </q-modal>

      <bulk-update-viewer
        v-if="bulkbool"
        module="workorder"
        :fieldlist="bulkfieldlist"
        @submit="bulkAction"
        @closed="closeBulkUpdateDialog"
        :content-css="{
          padding: '0px',
          background: '#f7f8fa',
          Width: '10vw',
          Height: '30vh',
        }"
      ></bulk-update-viewer>
      <column-customization
        :visible.sync="showColumnSettings"
        :moduleName="currentModuleName"
        :columnConfig="columnConfig"
        :viewName="currentView"
      ></column-customization>
      <space-asset-chooser
        @associate="associate"
        v-if="chooserVisibility"
        :visibility.sync="chooserVisibility"
        :filter="filter"
      ></space-asset-chooser>
      <work-hours
        v-if="showWorkDurationDialog"
        :visible.sync="showWorkDurationDialog"
        :id="workorderSelected"
        :callback="workDurationCallBack"
      ></work-hours>
      <!-- comment dialog start -->
      <outside-click
        :visibility.sync="showcomment"
        class="comment-dialog"
        style="top: 50px; position: fixed"
      >
        <div>
          <div class="comment-dialog-header">
            <h3
              class="comment-dialog-heading"
              style="text-transform: uppercase"
            >
              {{ $t('maintenance.wr_list.comments') }}
              <span>
                {{
                  workorderSelected.noOfNotes > 0
                    ? '(' + workorderSelected.noOfNotes + ')'
                    : ''
                }}
              </span>
            </h3>
            <div class="comment-close">
              <el-tooltip
                class="item"
                effect="dark"
                content="Close"
                placement="bottom"
              >
                <i
                  class="el-icon-close"
                  aria-hidden="true"
                  v-on:click="showcomment = false"
                ></i>
              </el-tooltip>
            </div>
          </div>
          <div class="comment-dialog-body">
            <comments
              module="ticketnotes"
              parentModule="workorder"
              :record="workorderSelected"
            ></comments>
          </div>
        </div>
      </outside-click>
      <!-- comment dialog end -->
      <!-- attachment dialog start -->
      <outside-click :visibility.sync="showattachment" class="comment-dialog">
        <div>
          <div class="comment-dialog-header">
            <h3
              class="comment-dialog-heading"
              style="text-transform: uppercase"
            >
              {{ $t('maintenance.wr_list.attachments') }}
              <span>
                {{
                  workorderSelected.noOfAttachments > 0
                    ? '(' + workorderSelected.noOfAttachments + ')'
                    : ''
                }}
              </span>
            </h3>
            <div class="comment-close">
              <i
                class="el-icon-close"
                aria-hidden="true"
                v-on:click="showattachment = false"
              ></i>
            </div>
          </div>
          <div class="comment-dialog-body">
            <attachments
              module="ticketattachments"
              :record="workorderSelected"
              :diasbleAttachment="true"
            ></attachments>
          </div>
        </div>
      </outside-click>
      <!-- attachment dialog end -->

      <!-- workorder options start-->
      <workorder-options
        v-if="openTaskOptions"
        :workorders="selectedWorkorders"
        :visibility.sync="openTaskOptions"
      ></workorder-options>
      <portal to="view-manager-link">
        <router-link
          tag="div"
          :to="`/app/wo/workorder/viewmanager`"
          class="view-manager-btn"
        >
          <inline-svg
            src="svgs/hamburger-menu"
            class="d-flex"
            iconClass="icon icon-sm"
          ></inline-svg>
          <span class="label mL10 text-uppercase">
            {{ $t('viewsmanager.list.views_manager') }}
          </span>
        </router-link>
      </portal>
    </div>
  </CommonListLayout>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapActions, mapGetters, mapState } from 'vuex'
import Spinner from '@/Spinner'
import Attachments from '@/relatedlist/SummaryAttachment'
import UserAvatar from '@/avatar/User'
import FAssignment from '@/FAssignment'
import ColumnCustomization from '@/ColumnCustomization'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import ViewMixinHelper from '@/mixins/ViewMixin'
import WorkHours from 'pages/workorder/widgets/dialogs/WorkHours'
import Comments from '@/relatedlist/Comments2'
import OutsideClick from '@/OutsideClick'
import BulkUpdateViewer from '@/BulkUpdateViewer'
import FListAttachmentPreview from '@/relatedlist/ListAttachmentPreview'
import FImagePreview from '@/relatedlist/ListImagePreview'
import workorderOptions from 'pages/workorder/workorders/v1/WorkOrderSummaryPdfOptions'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import {
  QList,
  QItem,
  QBtn,
  QItemMain,
  QItemSide,
  QIcon,
  QPopover,
  QModal,
} from 'quasar'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import SignatureField from '@/list/SignatureColumn'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'

export default {
  mixins: [ViewMixinHelper, workorderMixin],
  components: {
    QList,
    QItem,
    QBtn,
    QItemMain,
    QItemSide,
    QIcon,
    QPopover,
    QModal,
    UserAvatar,
    Spinner,
    FAssignment,
    ColumnCustomization,
    SpaceAssetChooser,
    Attachments,
    WorkHours,
    Comments,
    OutsideClick,
    BulkUpdateViewer,
    FListAttachmentPreview,
    FImagePreview,
    workorderOptions,
    CommonListLayout,
    FExportSettings,
    Sort,
    Pagination,
    CustomButton,
    SignatureField,
    AdvancedSearchWrapper,
  },
  data() {
    return {
      isEmpty,
      excludedSortFields: [
        'assignedTo',
        'assignedBy',
        'assignmentGroup',
        'createdBy',
        'requestedBy',
        'requester',
        'tenant',
      ],
      bulkfieldlist: [
        'requester',
        'assignedTo',
        'category',
        'dueDate',
        'priority',
        'type',
        'resource',
      ],
      loading: true,
      selectedWorkorders: [],
      selectAll: false,
      showcomment: false,
      showattachment: false,
      actions: {
        assignLoading: false,
        closeLoading: false,
        deleteLoading: false,
      },
      sortConfig: {
        orderBy: '',
        orderType: 'desc',
      },
      sortConfigLists: [],
      showColumnSettings: false,
      bulkbool: false,
      columnConfig: {
        fixedColumns: ['subject'],
        fixedSelectableColumns: [
          'noOfNotes',
          'noOfTasks',
          'noOfAttachments',
          'attachmentPreview',
        ],
        availableColumns: [
          'category',
          'description',
          'dueDate',
          'responseDueDate',
          'status',
          'sourceType',
          'assignedTo',
          'createdTime',
          'createdBy',
          'priority',
          'resource',
          'type',
          'modifiedTime',
          'actualWorkStart',
          'actualWorkEnd',
          'totalCost',
          'siteId',
          'vendor',
          'requester',
          this.$helpers.isLicenseEnabled('TENANTS') ? 'tenant' : '',
          this.$helpers.isLicenseEnabled('CLIENT') ? 'client' : '',
        ],
        showLookupColumns: false,
        lookupToShow: [
          'resource',
          this.$helpers.isLicenseEnabled('TENANTS') ? 'tenant' : '',
          this.$helpers.isLicenseEnabled('CLIENT') ? 'client' : '',
        ],
      },
      chooserVisibility: false,
      workorderSelected: -1,
      workOrder: -1,
      showWorkDurationDialog: false,
      workDurationCallBack: null,
      openTaskOptions: false,
      workorders: [],
      listCount: null,
      POSITION: POSITION_TYPE,
      filterConfig: {
        moduleName: 'workorder',
        includeParentCriteria: true,
        fetchModuleAction: 'workorder/fetchWorkOrders',
        path: '/app/wo/orders/',
        data: {
          subject: {
            label: 'Subject',
            displayType: 'string',
            value: [],
          },
          siteId: {
            label: 'Site',
            options: {},
            value: [],
            displayType: 'select',
            key: 'siteId',
          },
          category: {
            label: this.$t('maintenance.wr_list.category'),
            displayType: 'select',
            options: {},
            value: [],
          },
          resource: {
            label: 'Space / Asset',
            displayType: 'resourceType',
            options: {},
            value: [],
            multiSelect: true,
          },
          assignedTo: {
            label: this.$t('maintenance.wr_list.staff/team'),
            displayType: 'select',
            options: {},
            value: [],
          },
          ticketType: {
            label: this.$t('maintenance.wr_list.type'),
            displayType: 'select',
            options: {},
            value: [],
            key: 'type',
          },
          priority: {
            label: this.$t('maintenance.wr_list.priority'),
            displayType: 'select',
            options: {},
            value: [],
          },
          sourceType: {
            label: this.$t('maintenance.wr_list.source_type'),
            displayType: 'select',
            operatorId: 9,
            options: this.$constants.SourceType,
            value: [],
          },
          serialNumber: {
            label: 'ID',
            displayType: 'number',
            value: [],
            operator: 'NUMBER',
            operatorId: 9,
          },
          dueDate: {
            label: this.$t('maintenance.wr_list.duedate'),
            displayType: 'select',
            type: 'date',
            customdate: true,
            options: {
              72: 'Overdue',
              22: 'Today',
              25: 'Yesterday',
              26: 'Till Yesterday',
              23: 'Tomorrow',
              '8_41': 'Next 8 Hours',
            },
            value: [],
          },
          responseDueDate: {
            label: this.$t('maintenance.wr_list.responseDueDate'),
            displayType: 'select',
            type: 'date',
            customdate: true,
            options: {
              72: 'Overdue',
              25: 'Yesterday',
              26: 'Till Yesterday',
              22: 'Today',
              23: 'Tomorrow',
              '8_41': 'Next 8 Hours',
            },
            value: [],
          },
          createdTime: {
            label: this.$t('maintenance.wr_list.created_date'),
            displayType: 'select',
            single: true,
            type: 'date',
            customdate: true,
            options: {
              '1_40': 'Within 1 Hour',
              '12_40': 'Within 12 Hours',
              '24_40': 'Within 24 Hours',
              22: 'Today',
              25: 'Yesterday',
              31: 'This Week',
              28: 'This Month',
              27: 'Last Month',
              '2_39': 'Last 2 Months',
              '6_39': 'Last 6 Months',
            },
            value: '',
          },
          actualWorkEnd: {
            label: 'Resolved Time',
            displayType: 'select',
            single: true,
            type: 'date',
            customdate: true,
            options: {
              22: 'Today',
              25: 'Yesterday',
              31: 'This Week',
              28: 'This Month',
              27: 'Last Month',
              '2_39': 'Last 2 Months',
              '6_39': 'Last 6 Months',
            },
            value: '',
          },
        },
        showFilter: false,
        availableColumns: [
          'subject',
          'category',
          'resource',
          'dueDate',
          'responseDueDate',
          'sourceType',
          'status',
          'assignedTo',
          'createdTime',
          'comments',
          'tasks',
          'priority',
          'noOfNotes',
          'noOfTasks',
          'modifiedTime',
          'actualWorkStart',
          'actualWorkEnd',
          this.$helpers.isLicenseEnabled('TENANTS') ? 'tenant' : '',
          this.$helpers.isLicenseEnabled('CLIENT') ? 'client' : '',
        ],
        fixedCols: 'subject',
        saveView: true,
      },
      customButtonList: null,
      moduleDisplayName: `${this.$t('common._common.workorder')}`,
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadApprovalStatus')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadSites')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  mounted() {
    this.addFilterFields()
    this.metaFieldsLists()
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      siteList: state => state.sites,
      ticketpriority: state => state.ticketPriority,
      ticketType: state => state.ticketType,
      showSearch: state => state.search.active,
      viewDetail: state => state.view.currentViewDetail,
      hasWorkOrderChangeOwnerPermission() {
        if(isWebTabsEnabled()){
          return this.$hasPermission(`${this.moduleName}:UPDATE_CHANGE_OWNERSHIP`)
        }
      return this.$hasPermission(`${this.moduleName}:CHANGE_OWNERSHIP`)
    },
    }),
    ...mapGetters([
      'getTicketStatus',
      'getTicketStatusByLabel',
      'getTicketPriority',
      'getApprovalStatus',
      'isStatusLocked',
      'getTicketCategory',
      'getTicketType',
    ]),
    currentModuleName() {
      return this.$attrs.moduleName || 'workorder'
    },
    viewName() {
      let viewList = {
        open: 'Open Work Orders',
        overdue: 'Overdue Work Orders',
        duetoday: 'Due Today Work Orders',
        myopen: 'My Work Orders',
        myteamopen: 'My Team Work Orders',
        myoverdue: 'My Overdue Work Orders',
        myduetoday: 'My Due Today Work Orders',
        openfirealarms: 'Fire Alarm Work Orders',
        all: 'All Work Orders',
        unassigned: 'Unassigned Work Orders',
        closed: 'Closed Work Orders',
        unplanned: 'Unplanned Work Orders',
        planned: 'Planned Work Orders',
        mytodaysworkorder1: 'My Today Work Orders1',
        resolvedworkorder1: 'Resolved Work Orders 1',
        resolvedworkorder: 'resolved Work Orders',
        todaysplannedworkorders: 'Todays Planned Work Orders',
      }
      let title = this.currentView
        ? this.$getProperty(viewList, this.currentView)
        : null

      if (title) return title
      return 'Work Orders'
    },
    siteMap() {
      let { siteList } = this

      return (siteList || []).reduce((data, site) => {
        data[site.id] = site.name
        return data
      }, {})
    },
    currentView() {
      let viewName = this.$attrs.viewname || this.$route.params.viewname

      return viewName
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    page() {
      return this.$route.query.page || 1
    },
    filter() {
      let filter = {}
      let { siteId } = this.workOrder || {}

      if (!isEmpty(siteId)) {
        filter.site = Number(siteId)
      }

      return filter
    },
    showButtonsForListItems() {
      let { customButtonList, workorders } = this

      let evaluatedButtonIdSet = (workorders || []).reduce(
        (buttonIds, workorder) => {
          if (!isEmpty(workorder.evaluatedButtonIds)) {
            workorder.evaluatedButtonIds.forEach(id => buttonIds.add(id))
          }
          return buttonIds
        },
        new Set()
      )

      return (customButtonList || []).some(button =>
        evaluatedButtonIdSet.has(button.id)
      )
    },
  },

  watch: {
    currentView: {
      handler: function(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.loadWorkOrders()
          this.loadWOCounts()
        }
      },
      immediate: true,
    },
    filters(newVal, oldVal) {
      if (JSON.stringify(oldVal) !== JSON.stringify(newVal)) {
        this.loadWorkOrders()
        this.loadWOCounts()
      }
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadWorkOrders()
      }
    },
    viewDetail() {
      let { sortFields = [] } = this.viewDetail || {}

      if (!isEmpty(sortFields)) {
        let { name } = this.$getProperty(sortFields[0], 'sortField', {})
        this.sortConfig = {
          orderType: sortFields[0].isAscending ? 'asc' : 'desc',
          orderBy: name ? name : '',
        }
      }
    },
  },

  methods: {
    ...mapActions({
      saveNewView: 'view/saveNewView',
    }),
    toggleSelection(woId) {
      let idx = this.selectedWorkorders.findIndex(id => woId === id)

      if (idx === -1) {
        this.selectedWorkorders.push(woId)
      } else {
        this.selectedWorkorders.splice(idx, 1)
      }

      if (this.selectAll) {
        if (
          this.selectedWorkorders.length !==
          this.workorders.filter(wo => this.canEditRecord(wo)).length
        ) {
          this.selectAll = false
        }
      }
    },
    resetSelectAll() {
      this.selectedWorkorders = []
      this.selectAll = false
    },
    selectAllWorkorders() {
      let selectableWo = this.workorders.filter(wo => this.canEditRecord(wo))

      if (this.selectAll) {
        this.selectedWorkorders = selectableWo.map(wo => wo.id)
      } else {
        if (this.selectedWorkorders.length === selectableWo.length) {
          this.selectedWorkorders = []
        }
      }
    },
    onAssignWo(assignToObj) {
      if (!assignToObj) {
        return
      }

      let woIds = this.$getProperty(assignToObj, 'id', [])

      woIds.forEach(woId => {
        let workOrder = this.workorders.find(wo => wo.id === woId)

        if (!isEmpty(workOrder)) {
          let { assignedTo, assignmentGroup, status } = assignToObj
          let { status: wOStatus } = workOrder.status || {}
          let index = this.workorders.findIndex(wo => wo.id === woId)

          if (workOrder.id === woId) {
            if (!isEmpty(assignedTo)) {
              let { id } = assignedTo
              workOrder.assignedTo = id !== -1 ? assignedTo : null
            }

            if (!isEmpty(assignmentGroup)) {
              let { id } = assignmentGroup
              workOrder.assignmentGroup = id !== -1 ? assignmentGroup : null
            }

            if (wOStatus === 'Submitted' && status) {
              workOrder.status = status
            }
          }

          this.workorders.splice(index, 1, workOrder)
        }
      })
    },
    loadWorkOrders() {
      let url = `/v2/workorders/view/${this.currentView}`
      let { criteriaIds, fetchAllType } = this.$route.query || {}
      let { orderType, orderBy } = this.sortConfig || {}
      let params = {
        page: this.page,
        perPage: 50,
        filters: this.filters ? JSON.stringify(this.filters) : null,
        orderBy,
        orderType,
        criteriaIds: criteriaIds ? criteriaIds : null,
        fetchAllType: fetchAllType ? true : null,
        includeParentFilter: true,
      }

      Object.keys(params).forEach(key => {
        if (isEmpty(params[key])) delete params[key]
      })

      this.loading = true

      this.workorders = []
      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.workorders = data.workorders
          this.customButtonList = data.customButtons
        }
        this.loading = false
      })
    },
    loadWOCounts() {
      let url = '/workorder/workOrderCount'
      let { criteriaIds, fetchAllType } = this.$route.query || {}
      let params = {
        viewName: this.currentView,
        count: true,
        filters: this.filters ? JSON.stringify(this.filters) : null,
        criteriaIds: criteriaIds ? criteriaIds : null,
        fetchAllType: fetchAllType ? true : null,
        includeParentFilter: true,
      }

      Object.keys(params).forEach(key => {
        if (isEmpty(params[key])) delete params[key]
      })

      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.listCount = data.woCount
        }
      })
    },
    openSummaryView(id) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { currentView, currentModuleName } = this
          let { name } =
            findRouteForModule(currentModuleName, pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                viewname: currentView,
                id,
              },
            })
          }
        } else {
          this.$router.push({ path: '/app/wo/orders/summary/' + id })
        }
      }
    },
    assignWorkOrder(idList, assignedTo, assignmentGroup) {
      let workorder = {}

      if (assignedTo) {
        workorder = { assignedTo }
      }
      if (assignmentGroup) {
        workorder = { ...workorder, assignmentGroup }
      }

      let assignToObj = {
        id: idList,
        workorder,
      }

      this.actions.assignLoading = true
      API.post('/workorder/assign', assignToObj)
        .then(() => {
          this.loadWorkOrders()
          this.onAssignWo(assignToObj)
          this.$dialog.notify(
            this.$t('maintenance._workorder.wo_assigned_success')
          )
          this.resetSelectAll()
        })
        .finally(() => (this.actions.assignLoading = false))
    },
    doCloseWO(idList, actualDuration) {
      let paramObj = {
        id: idList,
      }

      if (actualDuration) {
        let { duration, workTimings } = actualDuration || {}

        if (duration !== -1) {
          paramObj.actualWorkDuration = duration
        }
        paramObj.actualTimings = workTimings
      }
      this.actions.closeLoading = true
      this.$store
        .dispatch('workorder/closeWorkOrder', paramObj)
        .then(() => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_closed_success')
          )
          this.loadWorkOrders()
        })
        .catch(() => {
          this.$message.error(this.$t('maintenance._workorder.wo_close_error'))
        })
        .finally(() => (this.actions.closeLoading = false))
    },
    closeWorkOrder(idList, actualDuration) {
      this.checkAndShowWorkDuration(idList, 'Closed', idList =>
        this.doCloseWO(idList)
      )
      if (!this.showWorkDurationDialog) {
        this.doCloseWO(idList, actualDuration)
        this.selectedWorkorders = []
      }
    },
    deleteWorkOrder(idList) {
      this.$dialog
        .confirm({
          title: this.$t('maintenance._workorder.delete_wo'),
          message: this.$t('maintenance._workorder.delete_wo_body'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.actions.deleteLoading = true
            this.$store
              .dispatch('workorder/deleteWorkOrder', { id: idList })
              .then(() => {
                this.$dialog.notify(
                  this.$t('maintenance._workorder.wo_delete_success')
                )
                this.resetSelectAll()
                this.loadWorkOrders()
                this.loadWOCounts()
              })
              .finally(() => (this.actions.deleteLoading = false))
          }
        })
    },
    bulkAction(actions) {
      let fields = {}

      actions.forEach(action => {
        let { field, valueArray, value, parseLabel, fieldObj } = action || {}
        if (field === 'category') {
          let { displayName } = this.getTicketCategory(parseInt(valueArray))

          fields[field] = {
            id: valueArray,
            name: displayName,
          }
        } else if (field === 'priority') {
          fields[field] = {
            id: parseInt(valueArray),
          }
        } else if (field === 'requester') {
          fields[field] = {
            id: parseInt(valueArray),
          }
        } else if (field === 'type') {
          fields[field] = {
            id: valueArray,
            name: this.getTicketType(parseInt(valueArray)).name,
          }
        } else if (field === 'assignedTo') {
          if (!isEmpty(value)) {
            fields[field] = { id: parseInt(value) }
          }
          if (!isEmpty(valueArray)) {
            fields.assignmentGroup = { id: parseInt(valueArray) }
          }
        } else if (field === 'resource') {
          fields[field] = { id: parseInt(parseLabel.id) }
        } else if (field === 'dueDate') {
          fields[field] = Date.parse(value)
        } else {
          if (!fields.data) {
            fields.data = {}
          }
          if (!isEmpty(fieldObj)) {
            let fieldObject = fieldObj[0]
            if (
              !isEmpty(fieldObject) &&
              !isEmpty(fieldObject.dataType) &&
              fieldObject.dataType === 7 &&
              !isEmpty(action.value.id)
            ) {
              action.value.id = parseInt(action.value.id)
              fields.data[field] = action.value
            }
          } else {
            fields.data[field] = action.value
          }
        }
      })

      this.updateWorkOrder(this.selectedWorkorders, fields)
      this.closeBulkUpdateDialog()
    },
    updateWorkOrder(idList, fields) {
      if (fields.status) {
        this.checkAndShowWorkDuration(idList, fields.status.status, idList =>
          this.doWorkOrderUpdate(idList, fields)
        )
        if (this.showWorkDurationDialog) {
          return
        }
      }
      this.doWorkOrderUpdate(idList, fields)
    },
    doWorkOrderUpdate(idList, fields, actualDuration) {
      let paramObj = { id: idList, fields: fields }

      if (actualDuration) {
        let { duration, workTimings } = actualDuration || {}

        if (duration !== -1) {
          fields.actualWorkDuration = duration
        }
        paramObj.actualTimings = workTimings
      }
      this.$store
        .dispatch('workorder/updateWorkOrder', paramObj)
        .then(() => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_update_success')
          )
          this.resetSelectAll()
          this.closeBulkUpdateDialog()
          this.loadWorkOrders()
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_update_failed')
          )
        })
    },
    checkAndShowWorkDuration(idList, status, cbk) {
      let showDuration = idList.some(id => {
        let workorder = this.workorders.find(wo => wo.id === id)
        let { isWorkDurationChangeAllowed, woStatus } = workorder || {}
        let { id: woStatusId } = woStatus || {}

        let getTicketStatusObj =
          (woStatusId && this.getTicketStatus(woStatusId, 'workorder')) || {}
        let closedOrResolvedState =
          status === 'Closed' && getTicketStatusObj.status !== 'Resolved'

        return (
          isWorkDurationChangeAllowed &&
          (status === 'Resolved' || closedOrResolvedState)
        )
      })
      if (showDuration) {
        if (this.$refs.statuspopover) {
          this.$refs.statuspopover.forEach(popover => popover.close())
        }
        this.workorderSelected = idList.length === 1 ? idList[0] : -1
        this.workDurationCallBack = cbk
        this.showWorkDurationDialog = true
      }
    },
    closeBulkUpdateDialog() {
      this.bulkbool = false
    },
    updateSort(sorting) {
      this.$store
        .dispatch('view/savesorting', {
          viewName: this.currentView,
          orderBy: sorting.orderBy,
          orderType: sorting.orderType,
          moduleName: 'workorder',
        })
        .then(() => this.loadWorkOrders())
    },
    associate(selectedObj) {
      this.chooserVisibility = false
      this.updateWorkOrder([this.workOrder.id], { resource: selectedObj })
      this.workOrder = -1
    },
    isRequestedState(workorder) {
      let { approvalStatus } = workorder || {}

      if (isEmpty(approvalStatus)) {
        return false
      } else {
        let statusObj = this.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
    },
    isRecordLocked(workorder) {
      let moduleState = this.$getProperty(workorder, 'moduleState.id', null)
      return moduleState && this.isStatusLocked(moduleState.id, 'workorder')
    },
    canEditRecord(workorder) {
      let isLocked = this.isRecordLocked(workorder)
      let isRequestedState = this.isRequestedState(workorder)

      return !isLocked && !isRequestedState
    },
    canShowSpaceAssetChooser(workorder) {
      if (this.canEditRecord(workorder)) {
        this.chooserVisibility = true
        this.workOrder = workorder
        this.workorderSelected = workorder.id
      }
    },
    saveAsNewView() {
      this.saveNewView({
        moduleName: 'workorder',
        view: { displayName: this.newViewName },
        filters: JSON.stringify(this.filters),
        parentView: 'open',
      }).then(() => {
        this.$refs.basicModal.close()
      })
    },
    metaFieldsLists() {
      this.$http
        .get('/module/metafields?moduleName=' + 'workorder')
        .then(({ data }) => {
          let { fields = [] } = data.meta || {}

          fields.forEach(field => {
            if (field.dataType !== 7 || field.default) {
              this.sortConfigLists.push(field.name)
            }
          })
          this.moduleMetaObject = data.meta
        })
    },
    addFilterFields() {
      let filterField = {
        label: '',
        displayType: 'lookup',
        options: {},
        value: [],
      }
      let filterObject = {}
      let { data } = this.filterConfig

      if (this.$helpers.isLicenseEnabled('TENANTS')) {
        filterObject['tenant'] = { ...filterField, label: 'Tenant' }
      }
      if (this.$helpers.isLicenseEnabled('VENDOR')) {
        filterObject['vendor'] = { ...filterField, label: 'Vendor' }
      }
      if (this.$helpers.isLicenseEnabled('CLIENT')) {
        filterObject['client'] = { ...filterField, label: 'Client' }
      }

      this.$set(this.filterConfig, 'data', { ...data, ...filterObject })
    },
    redirectToCreation() {
      if (isWebTabsEnabled()) {
        let { currentModuleName } = this
        let { name } =
          findRouteForModule(currentModuleName, pageTypes.CREATE) || {}

        name && this.$router.push({ name })
      } else {
        this.$router.push({ path: '/app/wo/create' })
      }
    },
  },
}
</script>
<style lang="scss">
.wo-list-container .common-content-container {
  display: flex;
  flex: 1 1 auto;
  flex-direction: column;
  min-width: 0;
  min-height: 0;
}
.custom-button-container {
  min-height: 25px;
}
.custom-button-top {
  padding-top: 1px;
  margin-bottom: auto;
  margin-top: -10px;
  padding-right: 10px;
}
.fc-list-view-table {
  &.wo-table {
    td:not(.main-field-column) {
      cursor: default;
    }
    td {
      &.main-field-column:hover {
        color: #46a2bf;
      }
    }
  }
}
</style>
