<template>
  <div>
    <div v-if="!summaryData">
      <spinner :show="!summaryData"></spinner>
    </div>
    <div v-else class="fc__layout__flexes fc__layout__has__row fc__layout__box">
      <div class="fc__layout__flexes fc__layout__has__row fc__layout__box">
        <div
          class="fc__layout__flexes fc__layout__has__columns fc__layout__box"
        >
          <div class="fc__layout__flexes fc__layout__has__columns">
            <div class="fc__layout__has__row fc__layout__box">
              <!--sub header -->
              <div
                class="fc__layout__has__row fc__submenu__left fc__layout__box"
              >
                <div
                  class="fc__layout_media_center fc__submenu__header fc__layout__has__columns pointer"
                >
                  <i class="el-icon-back fw6" @click="back"></i>
                  <div v-if="showRevisionHistory" class="label-txt-black pL10">
                    {{ $t('common.wo_report.revision_history') }}
                    {{ revisionHistoryObj ? revisionHistoryObj.name : '---' }}
                  </div>
                  <div v-else class="label-txt-black pL10">
                    <el-popover
                      placement="bottom"
                      width="170"
                      v-model="toggle"
                      popper-class="popover-height inventory-list-popover"
                      trigger="click"
                    >
                      <ul>
                        <li
                          @click="switchCategory(view)"
                          v-for="(view, index) in views"
                          :key="index"
                          :class="{
                            active:
                              currentViewDetail.displayName ===
                              view.displayName,
                          }"
                        >
                          {{ view.displayName }}
                        </li>
                      </ul>
                      <span slot="reference"
                        >{{ currentViewDetail.displayName
                        }}<i
                          class="el-icon-arrow-down el-icon-arrow-down-tv"
                          style="padding-left:8px"
                        ></i
                      ></span>
                    </el-popover>
                  </div>
                  <div
                    v-if="!showRevisionHistory"
                    class="pointer"
                    @click="showQuickSearch = !showQuickSearch"
                  >
                    <i
                      class="fa fa-search asset__search__icon"
                      aria-hidden="true"
                    ></i>
                  </div>
                  <div class="row" v-if="showQuickSearch">
                    <div class="col-12 fc-list-search">
                      <div
                        class="fc-list-search-wrapper asset__inventory_search__con"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="32"
                          height="32"
                          viewBox="0 0 32 32"
                          class="search-icon-asset hide"
                        >
                          <title>{{ $t('common._common.search') }}</title>
                          <path
                            d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                          ></path>
                        </svg>
                        <input
                          ref="quickSearchQuery"
                          autofocus
                          type="text"
                          v-model="quickSearchQuery"
                          @keyup.enter="quickSearch"
                          :placeholder="$t('common._common.search')"
                          class="quick-search-input-asset asset__inventory__search"
                        />
                        <svg
                          @click="closeSearch"
                          xmlns="http://www.w3.org/2000/svg"
                          width="32"
                          height="32"
                          viewBox="0 0 32 32"
                          class="asset__inventory__close"
                          aria-hidden="true"
                        >
                          <title>{{ $t('common._common.close') }}</title>
                          <path
                            d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                          ></path>
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- sub section -->
                <div
                  class="fc__layout__flexes overflo-auto fc__layout__has__row fc__layout__box layout__box__height"
                >
                  <div
                    class="fc__submenu__section fc__layout__has__row fc__layout__max__row"
                  >
                    <div v-if="!showRevisionHistory" class="pT10">
                      <div
                        class="label-txt-black pb20 flex-middle justify-content-space"
                        @click="getSummaryLink(labCont.id)"
                        v-for="labCont in labourContracts"
                        :key="labCont.id"
                        v-bind:class="{ fcactivelist: id === labCont.id }"
                      >
                        <div
                          class="width220px textoverflow-ellipsis"
                          :title="
                            labCont.name
                              ? labCont.name
                              : '[#' + labCont.parentId + ']'
                          "
                          v-tippy="{
                            placement: 'top',
                            animation: 'shift-away',
                            arrow: true,
                          }"
                        >
                          {{
                            labCont.name
                              ? labCont.name
                              : '[#' + labCont.id + ']'
                          }}
                        </div>
                        <img
                          src="~assets/black-arrow-right.svg"
                          class="fR hide mT3"
                          width="15"
                          height="15"
                          v-bind:class="{ show: id === labCont.id }"
                        />
                      </div>
                    </div>
                    <div v-else-if="showRevisionHistory" class="pT10">
                      <div
                        class="label-txt-black pb20 flex-middle justify-content-space"
                        @click="getHistorySummaryLink(contract.id)"
                        v-for="contract in revisionHistoryJSON.list"
                        :key="contract.id"
                        v-bind:class="{ fcactivelist: id === contract.id }"
                      >
                        <div
                          class="width220px textoverflow-ellipsis"
                          :title="
                            contract.name
                              ? contract.name
                              : '[#' + contract.id + ']'
                          "
                          v-tippy="{
                            placement: 'top',
                            animation: 'shift-away',
                            arrow: true,
                          }"
                        >
                          {{
                            contract
                              ? contract.name + ' R-' + contract.revisionNumber
                              : '[#' + contract.parentId + ']'
                          }}
                        </div>
                        <img
                          src="~assets/black-arrow-right.svg"
                          class="fR hide mT3"
                          width="15"
                          height="15"
                          v-bind:class="{ show: id === contract.id }"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <!-- main section -->
              <div
                class="fc__layout__flexes fc__layout__has__row fc__layout__asset_main"
              >
                <div class="">
                  <!-- man header -->
                  <div
                    class="fc__layout__align fc__asset__main__header pT24 pB20 pL20 pR20"
                  >
                    <div class="fc-dark-grey-txt18">
                      <div class="fc-id">#{{ summaryData.parentId }}</div>
                      <div class="fc-black-17 bold inline-flex">
                        {{ summaryData.name }}
                        <div class="fc-newsummary-chip mL10">
                          {{ getContractStatus(summaryData.status) }}
                        </div>
                      </div>
                    </div>
                    <div class="fc__layout__align inventory-overview-btn-group">
                      <div
                        v-if="
                          $hasPermission(
                            'contract:APPROVE_REJECT_WORKREQUEST'
                          ) && summaryData.status !== 7
                        "
                      >
                        <el-popover
                          placement="top-start"
                          width="180"
                          trigger="hover"
                        >
                          <el-button slot="reference" class="fc__border__btn">
                            {{ $t('common.profile.change_status') }}
                            <i class="el-icon-arrow-down f14 text-right"></i
                          ></el-button>
                          <div
                            v-if="summaryData.status !== 2"
                            @click="
                              updateContractStatus(
                                2,
                                'Approved Successfully',
                                'labourcontract'
                              )
                            "
                            class="label-txt-black pT10 pB10 pointer pL10 list-hover"
                          >
                            {{ $t('common.header.approve') }}
                          </div>
                          <div
                            v-if="summaryData.status !== 3"
                            @click="
                              updateContractStatus(
                                3,
                                'Closed Successfully',
                                'labourcontract'
                              )
                            "
                            class="label-txt-black pT10 pB10 pointer pL10 list-hover"
                          >
                            {{ $t('common._common.close') }}
                          </div>
                          <div
                            v-if="summaryData.status !== 4"
                            @click="
                              updateContractStatus(
                                4,
                                'Cancelled Successfully',
                                'labourcontract'
                              )
                            "
                            class="label-txt-black pT10 pB10 pointer pL10 list-hover"
                          >
                            {{ $t('common._common.cancel') }}
                          </div>
                          <div
                            v-if="summaryData.status !== 5"
                            @click="
                              updateContractStatus(
                                5,
                                'Suspended Successfully',
                                'labourcontract'
                              )
                            "
                            class="label-txt-black pT10 pB10 pointer pL10 list-hover"
                          >
                            {{ $t('common.header.suspend') }}
                          </div>
                        </el-popover>
                      </div>
                      <el-dropdown
                        v-if="
                          isContractEditable(summaryData.status) ||
                            $hasPermission('contract:DELETE')
                        "
                        class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer"
                        @command="handleCommand"
                      >
                        <span class="el-dropdown-link">
                          <img src="~assets/menu.svg" height="18" width="18" />
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item
                            v-if="isContractEditable(summaryData.status)"
                            command="edit"
                            >{{ $t('common._common.edit') }}</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="
                              summaryData.status === 1 &&
                                $hasPermission('contract:DELETE')
                            "
                            command="delete"
                            >{{ $t('common._common.delete') }}</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="$hasPermission('contract:CREATE')"
                            command="duplicate"
                            >{{
                              $t('common.products.duplicate')
                            }}</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="
                              $hasPermission('contract:CREATE') &&
                                summaryData.status === 2
                            "
                            command="revise"
                            >{{
                              $t('common.products.revise')
                            }}</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="
                              summaryData.revisionNumber > 0 &&
                                summaryData.status !== 7
                            "
                            command="revisionhistory"
                            >{{
                              $t('common.header.show_revision_history')
                            }}</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="summaryData.status === 2"
                            command="preference"
                            >{{
                              $t('common.header.notification_preferences')
                            }}</el-dropdown-item
                          >
                          <el-dropdown-item
                            v-if="
                              summaryData.frequencyType > 0 &&
                                summaryData.status === 2
                            "
                            command="updatepayment"
                            >{{
                              $t('common.header.update_payment_status')
                            }}</el-dropdown-item
                          >
                        </el-dropdown-menu>
                      </el-dropdown>
                    </div>
                  </div>
                </div>
                <!-- main section -->
                <div
                  class="fc__layout__flexes fc__main__con__width"
                  v-if="summaryData !== null"
                >
                  <div class="fc__asset__main__scroll">
                    <div class="fc__white__bg__asset">
                      <el-row>
                        <el-col :span="16" class="mB30">
                          <el-col :span="24" class="pB30">
                            <div class="fc-blue-label">
                              {{ $t('common.wo_report.report_description') }}
                            </div>
                            <div
                              class="label-txt-black pT10 inventory-item-desc textoverflow-height-ellipsis2 height40"
                              v-if="summaryData.description"
                            >
                              {{ summaryData.description }}
                            </div>
                            <div
                              class="label-txt-black pT10 inventory-item-desc textoverflow-height-ellipsis2 height40 pointer f13"
                              @click="
                                addDescDialog = true
                                addDescriptionText = null
                              "
                              v-else
                            >
                              <span
                                :title="
                                  $t(
                                    'common.attachment_form.click_to_add_description'
                                  )
                                "
                                v-tippy="{ arrow: true }"
                                >{{ $t('common._common.nodescription') }}</span
                              >
                            </div>
                            <el-dialog
                              :visible.sync="addDescDialog"
                              :fullscreen="false"
                              :title="$t('common.header.add_description')"
                              open="top"
                              width="400px"
                              custom-class="inventory-dialog fc-dialog-center-container fc-web-form-dialog"
                              :append-to-body="true"
                            >
                              <div>
                                <div class="readingcard-setlayout">
                                  <div class="reading-setcard-body">
                                    <div style="margin-top: 7px;" class="pB10">
                                      <div
                                        class="inventory-select-item-con mT15 mB15"
                                      >
                                        <el-input
                                          type="textarea"
                                          placeholder="Enter Description"
                                          class="fc-input-full-border-textarea"
                                          v-model="addDescriptionText"
                                          :autosize="{ minRows: 3 }"
                                        ></el-input>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                              <div class="modal-dialog-footer">
                                <el-button
                                  class="modal-btn-cancel"
                                  @click="addDescDialog = false"
                                  >{{ $t('common._common.cancel') }}</el-button
                                >
                                <el-button
                                  class="modal-btn-save"
                                  type="primary"
                                  @click="addDescription()"
                                  >{{ $t('common._common.add') }}</el-button
                                >
                              </div>
                            </el-dialog>
                          </el-col>
                          <el-col :span="24" class="mB30">
                            <div class="fc-blue-label">
                              {{ $t('common._common.type') }}
                            </div>
                            <div class="label-txt-black pT5">
                              {{
                                summaryData.contractType
                                  ? contractTypeEnumFormatter(
                                      summaryData.contractType
                                    )
                                  : '---'
                              }}
                            </div>
                          </el-col>
                          <el-col :span="12" class="">
                            <div class="fc-blue-label">
                              {{ $t('common.products.vendor') }}
                            </div>
                            <div class="label-txt-black pT5">
                              <span v-if="!summaryData.vendor">---</span>
                              <router-link
                                v-else
                                :to="openVendorSummary(summaryData.vendor.id)"
                                >{{ summaryData.vendor.name }}</router-link
                              >
                            </div>
                          </el-col>
                        </el-col>
                      </el-row>
                    </div>

                    <div class="mT20 mB10 inventory-section-header">
                      {{ $t('common._common.summary') }}
                    </div>
                    <div class="fc__white__bg__info">
                      <el-row class="border-bottom6 pB20">
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common._common.valid_from') }}
                            </div>
                          </el-col>
                          <el-col :span="12">
                            {{
                              summaryData.fromDate > 0
                                ? $options.filters.formatDate(
                                    summaryData.fromDate,
                                    true
                                  )
                                : '---'
                            }}
                          </el-col>
                        </el-col>
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common._common.valid_till') }}
                            </div>
                          </el-col>
                          <el-col :span="12">
                            {{
                              summaryData.endDate > 0
                                ? $options.filters.formatDate(
                                    summaryData.endDate,
                                    true
                                  )
                                : '---'
                            }}
                          </el-col>
                        </el-col>
                      </el-row>
                      <el-row class="pT20">
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common._common.renewal_date') }}
                            </div>
                          </el-col>
                          <el-col :span="12">
                            {{
                              summaryData.renewalDate > 0
                                ? $options.filters.formatDate(
                                    summaryData.renewalDate,
                                    true
                                  )
                                : '---'
                            }}
                          </el-col>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="mT20 mB10 inventory-section-header">
                      {{ $t('common.header.payment_info') }}
                      <i
                        v-if="isContractEditable(summaryData.status)"
                        @click="handleCommand('edit')"
                        class="el-icon-edit pointer pL10"
                      ></i>
                    </div>
                    <div
                      v-if="summaryData.frequencyType < 0"
                      class="fc__white__bg__info height100px"
                    >
                      <el-row class="mT10">
                        <div class="payment-info-nodata">
                          {{ $t('inventory.contracts.no_payment_info') }}
                        </div>
                      </el-row>
                    </div>
                    <div v-else class="fc__white__bg__info">
                      <el-row class="border-bottom6 pB20">
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common.header.next_payment_date') }}
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.nextPaymentDate > 0
                              ? $options.filters.formatDate(
                                  summaryData.nextPaymentDate,
                                  true
                                )
                              : '---'
                          }}</el-col>
                        </el-col>
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common._common.frequency_type') }}
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.frequencyTypeEnum
                              ? summaryData.frequencyTypeEnum
                              : '---'
                          }}</el-col>
                        </el-col>
                      </el-row>
                      <el-row class="pT20 border-bottom6 pB20">
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common.header.payment_interval') }}
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.paymentInterval
                              ? summaryData.paymentInterval
                              : '---'
                          }}</el-col>
                        </el-col>
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common.header.schedule_time') }}
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.scheduleTime
                              ? convertMilliSecondsToTimeHHMM(
                                  summaryData.scheduleTime
                                )
                              : '---'
                          }}</el-col>
                        </el-col>
                      </el-row>
                      <el-row class="pT20 border-bottom6 pB20">
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common.header.schedule_day') }}
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.scheduleDay
                              ? summaryData.scheduleDay
                              : '---'
                          }}</el-col>
                        </el-col>
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{ $t('common.header.schedule_month') }}
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.scheduleMonth
                              ? summaryData.scheduleMonth
                              : '---'
                          }}</el-col>
                        </el-col>
                      </el-row>
                    </div>

                    <div
                      class="fc__white__bg__info__custom mT20"
                      v-if="customFields && customFields.length > 0"
                    >
                      <template v-for="(d, index) in customFields">
                        <el-row
                          class="border-bottom6 pB20 pT20"
                          v-if="index % 2 === 0"
                          :key="index"
                        >
                          <el-col :span="12">
                            <el-col :span="12">
                              <div class="fc-blue-label">
                                {{ customFields[index].displayName }}
                              </div>
                            </el-col>
                            <el-col
                              :span="12"
                              v-if="
                                customFields[index].field.dataTypeEnum ===
                                  'DATE'
                              "
                              >{{
                                summaryData.data &&
                                summaryData.data[customFields[index].name] > 0
                                  ? $options.filters.formatDate(
                                      summaryData.data[
                                        customFields[index].name
                                      ],
                                      true
                                    )
                                  : '---'
                              }}</el-col
                            >
                            <el-col
                              :span="12"
                              v-else-if="
                                customFields[index].field.dataTypeEnum ===
                                  'DATE_TIME'
                              "
                              >{{
                                (summaryData.data &&
                                  summaryData.data[customFields[index].name]) >
                                0
                                  ? $options.filters.formatDate(
                                      summaryData.data[customFields[index].name]
                                    )
                                  : '---'
                              }}</el-col
                            >
                            <el-col
                              :span="12"
                              v-else-if="
                                [2, 3].includes(
                                  customFields[index].field.dataType
                                )
                              "
                              >{{
                                summaryData.data &&
                                summaryData.data[customFields[index].name] &&
                                summaryData.data[customFields[index].name] !==
                                  -1
                                  ? summaryData.data[customFields[index].name]
                                  : '---'
                              }}</el-col
                            >
                            <el-col
                              :span="12"
                              v-else-if="
                                customFields[index].field.dataTypeEnum ===
                                  'ENUM'
                              "
                              >{{
                                summaryData.data &&
                                summaryData.data[customFields[index + 1].name]
                                  ? customFields[index + 1].field.enumMap[
                                      parseInt(
                                        summaryData.data[
                                          customFields[index + 1].name
                                        ]
                                      )
                                    ]
                                  : '---'
                              }}</el-col
                            >
                            <el-col :span="12" v-else>{{
                              summaryData.data &&
                              summaryData.data[customFields[index].name]
                                ? summaryData.data[customFields[index].name]
                                : '---'
                            }}</el-col>
                          </el-col>
                          <el-col
                            v-if="customFields.length > index + 1"
                            :span="12"
                          >
                            <el-col :span="12">
                              <div class="fc-blue-label">
                                {{ customFields[index + 1].displayName }}
                              </div>
                            </el-col>
                            <el-col
                              :span="12"
                              v-if="
                                customFields[index + 1].field.dataTypeEnum ===
                                  'DATE'
                              "
                              >{{
                                summaryData.data &&
                                summaryData.data[customFields[index + 1].name] >
                                  0
                                  ? $options.filters.formatDate(
                                      summaryData.data[
                                        customFields[index + 1].name
                                      ],
                                      true
                                    )
                                  : '---'
                              }}</el-col
                            >
                            <el-col
                              :span="12"
                              v-else-if="
                                customFields[index + 1].field.dataTypeEnum ===
                                  'DATE_TIME'
                              "
                              >{{
                                (summaryData.data &&
                                  summaryData.data[
                                    customFields[index + 1].name
                                  ]) > 0
                                  ? $options.filters.formatDate(
                                      summaryData.data[
                                        customFields[index + 1].name
                                      ]
                                    )
                                  : '---'
                              }}</el-col
                            >
                            <el-col
                              :span="12"
                              v-else-if="
                                [2, 3].includes(
                                  customFields[index + 1].field.dataType
                                )
                              "
                              >{{
                                summaryData.data &&
                                summaryData.data[
                                  customFields[index + 1].name
                                ] &&
                                summaryData.data[
                                  customFields[index + 1].name
                                ] !== -1
                                  ? summaryData.data[
                                      customFields[index + 1].name
                                    ]
                                  : '---'
                              }}</el-col
                            >
                            <el-col
                              :span="12"
                              v-else-if="
                                customFields[index + 1].field.dataTypeEnum ===
                                  'ENUM'
                              "
                              >{{
                                summaryData.data &&
                                summaryData.data[customFields[index + 1].name]
                                  ? customFields[index + 1].field.enumMap[
                                      parseInt(
                                        summaryData.data[
                                          customFields[index + 1].name
                                        ]
                                      )
                                    ]
                                  : '---'
                              }}</el-col
                            >
                            <el-col :span="12" v-else>{{
                              summaryData.data &&
                              summaryData.data[customFields[index + 1].name]
                                ? summaryData.data[customFields[index + 1].name]
                                : '---'
                            }}</el-col>
                          </el-col>
                        </el-row>
                      </template>
                    </div>
                    <!-- line items -->
                    <div class="fc__white__bg__p0 mT20">
                      <div
                        class="pull-right self-center table-btn"
                        v-if="
                          activeName1 === 'first' &&
                            isContractEditable(summaryData.status)
                        "
                      >
                        <el-button
                          @click="addLineItemsDialog"
                          icon="el-icon-plus"
                          class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add  sp-sh-btn-sm"
                          type="text"
                          :title="'Add Labor'"
                          v-tippy
                          data-size="small"
                        ></el-button>
                      </div>
                      <el-tabs
                        v-model="activeName1"
                        @tab-click="handleClick"
                        class="fc-tab-hide"
                      >
                        <el-tab-pane
                          :label="$t('common.header.labour')"
                          name="first"
                        >
                          <el-table
                            :data="summaryData.lineItems"
                            :empty-text="
                              $t('common._common.no_labours_available')
                            "
                            :default-sort="{
                              prop: 'summaryData.lineItems.name',
                              order: 'descending',
                            }"
                            class="width100 inventory-inner-table mT20"
                          >
                            <el-table-column
                              prop="labour.name"
                              :label="$t('common.header.labour_name')"
                            >
                            </el-table-column>
                            <el-table-column :label="$t('common.tabs._cost')">
                              <template v-slot="scope">
                                <div
                                  v-if="isContractEditable(summaryData.status)"
                                >
                                  <span v-if="$currency === '$'">{{
                                    $currency
                                  }}</span>
                                  <el-input
                                    placeholder=""
                                    :min="0"
                                    v-model="scope.row.cost"
                                    type="number"
                                    @change="
                                      updateContractLineItem(
                                        'Labor Cost edited Successfully',
                                        'labourcontract'
                                      )
                                    "
                                    class="fc-input-full-border2 width50px inventory-input-width text-right  pr-summary"
                                  ></el-input>
                                  <span v-if="$currency !== '$'">{{
                                    $currency
                                  }}</span>
                                </div>
                                <currency
                                  v-else
                                  :value="scope.row.cost"
                                ></currency>
                              </template>
                            </el-table-column>
                            <el-table-column>
                              <template v-slot="scope">
                                <div
                                  class="visibility-hide-actions export-dropdown-menu"
                                >
                                  <i
                                    class="el-icon-delete pointer edit-icon-color"
                                    v-if="
                                      isContractEditable(summaryData.status)
                                    "
                                    style="padding-left: 18px"
                                    data-arrow="true"
                                    :title="$t('common._common.delete_labour')"
                                    v-tippy
                                    @click="
                                      remove(
                                        summaryData.lineItems[scope.$index],
                                        scope.$index
                                      )
                                    "
                                  ></i>
                                </div>
                              </template>
                            </el-table-column>
                          </el-table>
                        </el-tab-pane>
                      </el-tabs>
                    </div>
                    <div class="fc__white__bg__p0 mT20">
                      <div
                        class="pull-right self-center table-btn"
                        v-if="section2 === 'tandc' && summaryData.status === 1"
                      >
                        <el-button
                          @click="addTandCVisibility = true"
                          icon="el-icon-plus"
                          class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add sp-sh-btn-sm"
                          type="text"
                          :title="'Add T&C'"
                          v-tippy
                          data-size="small"
                        ></el-button>
                      </div>
                      <el-tabs v-model="section2" @tab-click="handleClick">
                        <el-tab-pane
                          :label="$t('common._common.terms_conditions')"
                          name="tandc"
                        >
                          <contract-terms-and-conditions
                            :summaryData="summaryData"
                            :type="'contract'"
                            @refreshList="loadSummary"
                            :addTandCVisibility.sync="addTandCVisibility"
                          ></contract-terms-and-conditions>
                        </el-tab-pane>
                      </el-tabs>
                    </div>
                    <!-- notes & attachements -->
                    <div class="fc__white__bg__p0 mT20" :key="summaryData.id">
                      <el-tabs v-model="activeName" @tab-click="handleClick">
                        <el-tab-pane
                          :label="$t('common._common.notes')"
                          name="first"
                        >
                          <div
                            v-if="
                              activeName === 'first' &&
                                summaryData &&
                                summaryData.id
                            "
                            class="inventory-comments"
                          >
                            <comments
                              module="contractnotes"
                              :placeholder="$t('common._common.add_note')"
                              :btnLabel="$t('common._common.add_note')"
                              :record="summaryData"
                              :notify="false"
                            ></comments>
                          </div>
                        </el-tab-pane>
                        <el-tab-pane
                          :label="$t('common._common.documents')"
                          name="second"
                        >
                          <div v-if="activeName === 'second'">
                            <attachments
                              module="contractattachments"
                              v-if="summaryData"
                              :record="summaryData"
                            ></attachments>
                          </div>
                        </el-tab-pane>
                      </el-tabs>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        v-if="addLineItemsVisibility"
        class="position-relative"
        :key="summaryData.id"
      >
        <el-dialog
          :visible.sync="addLineItemsVisibility"
          :fullscreen="false"
          :key="summaryData.id"
          :title="$t('common.header.add_labours')"
          width="40%"
          open="top"
          custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
          :append-to-body="true"
        >
          <table class="setting-list-view-table store-table">
            <thead class="setup-dialog-thead">
              <tr>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 240px;"
                >
                  {{ $t('common.header._labour') }}
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 240px;"
                >
                  {{ $t('common.products.rate_hour') }}
                </th>
                <th></th>
              </tr>
            </thead>
            <tbody :key="summaryData.id">
              <tr
                v-for="(field, lineindex) in lineItemsFormObject.lineItems"
                :key="lineindex"
                class="visibility-visible-actions"
              >
                <td class="module-builder-td">
                  <el-select
                    filterable
                    clearable
                    v-model="lineItemsFormObject.lineItems[lineindex].labour.id"
                    @change="
                      loadRateForLabour(
                        lineItemsFormObject.lineItems[lineindex]
                      )
                    "
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option
                      v-for="(labour, index) in labours"
                      :key="index"
                      :label="labour.name"
                      :value="labour.id"
                    ></el-option>
                  </el-select>
                </td>
                <td class="module-builder-td">
                  <el-input
                    :placeholder="$t('common.products.rate_hour')"
                    type="number"
                    v-model="lineItemsFormObject.lineItems[lineindex].cost"
                    class="fc-input-full-border-select2 duration-input"
                  ></el-input>
                </td>
                <td>
                  <div class="visibility-hide-actions export-dropdown-menu">
                    <span
                      @click="
                        addLabourEntry(lineItemsFormObject.lineItems, lineindex)
                      "
                      :title="$t('common._common.add')"
                      v-tippy
                    >
                      <img src="~assets/add-icon.svg" class="mR10 mT10" />
                    </span>
                    <span
                      v-if="lineItemsFormObject.lineItems.length > 1"
                      @click="
                        removeLabourEntry(
                          lineItemsFormObject.lineItems,
                          lineindex
                        )
                      "
                      :title="$t('common._common.remove')"
                      v-tippy
                    >
                      <img src="~assets/remove-icon.svg" class="mT10" />
                    </span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="modal-dialog-footer">
            <el-button
              class="modal-btn-cancel"
              @click="addLineItemsVisibility = false"
              >{{ $t('common._common.cancel') }}</el-button
            >
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="addLineLabour()"
              >{{ $t('common._common.add') }}</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>
    <div v-if="preferenceVisibility">
      <el-dialog
        :visible.sync="preferenceVisibility"
        :title="$t('common.header.notification_preference')"
        :fullscreen="false"
        :before-close="cancelNotificationPreference"
        :append-to-body="true"
        key="1"
        custom-class="fc-animated slideInRight fc-dialog-form contract-notification-form-heading fc-dialog-right width50"
      >
        <notification-preference
          :moduleName="'labourcontracts'"
          :recordId="summaryData.id"
          :visibility.sync="preferenceVisibility"
        ></notification-preference>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import Attachments from '@/relatedlist/Attachments'
import Comments from '@/relatedlist/Comments'
import contractMixin from 'pages/contract/mixin/contractHelper'
import ContractTermsAndConditions from 'pages/contract/components/ContractTermsAndConditions'
import NotificationPreference from 'pages/contract/components/NotificationPreference'
import { isEmpty } from '@facilio/utils/validation'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
export default {
  components: {
    Comments,
    Attachments,
    ContractTermsAndConditions,
    NotificationPreference,
  },
  mixins: [contractMixin],
  data() {
    return {
      activeName: 'first',
      activeName1: 'first',
      activeName2: 'first',
      section2: 'tandc',
      status: {
        REQUESTED: 'REQUESTED',
        APPROVED: 'APPROVED',
        REJECTED: 'REJECTED',
        ORDERED: 'ORDERED',
        PARTIALLY_RECEIVED: 'PARTIALLY RECEIVED',
        RECEIVED: 'RECEIVED',
        COMPLETED: 'COMPLETED',
      },
      showQuickSearch: false,
      error: false,
      errorText: '',
      quickSearchQuery: null,
      toggle: false,
      selectindex: false,
      loading: true,
      page: 1,
      fetchingMore: false,
      summaryData: null,
      saving: false,
      customFields: null,
      addLineItemsVisibility: false,
      lineItemsFormObject: { lineItems: null },
      addDescDialog: false,
      addDescriptionText: null,
      labours: [],
      addTandCVisibility: false,
      preferenceVisibility: false,
    }
  },
  mounted() {
    this.$store.dispatch('view/clearViews')
    this.$store.dispatch('view/loadViews', this.getCurrentModule)
    this.$store.dispatch('view/loadModuleMeta', this.getCurrentModule)
    this.getViewDetail()
    this.loadData()
    this.loadSummary()
    this.loadForm()
  },
  computed: {
    id() {
      return parseInt(this.$route.params.id)
    },
    labourContracts() {
      return this.$store.state.labourcontract.labourContracts
    },
    getCurrentModule() {
      return 'labourcontracts'
    },
    canLoadMore() {
      return this.$store.state.labourcontract.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    currentView() {
      if (this.$route.query.search) {
        return 'Filtered Labor Contracts'
      }
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return {
          displayName: 'Filtered Labor Contracts',
          name: 'filteredlabourcontracts',
        }
      }
      return this.views.find(view => view.name === this.currentView) || {}
    },
    views() {
      return this.$store.state.view.views
    },
    searchQuery() {
      return this.$store.state.labourcontract.quickSearchQuery
    },
  },
  watch: {
    labourContracts(newVal) {
      if (this.$route.params.id === 'null' && newVal && newVal.length > 0) {
        this.getSummaryLink(newVal[0].id)
      }
    },
    id() {
      if (this.$route.query.refresh) {
        this.loadData()
      }
      this.loadSummary()
    },
    searchQuery() {
      this.loadData()
    },
    currentView(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.loadData()
      }
    },
    '$route.query.search': {
      handler(value) {
        let viewIndex = this.views.findIndex(
          view => view.name === 'filteredlabourcontracts'
        )

        if (value && isEmpty(viewIndex)) {
          this.views.push({
            displayName: 'Filtered Labor Contracts',
            name: 'filteredlabourcontracts',
          })
        }
      },
    },
  },
  methods: {
    loadForm() {
      let self = this
      let url = '/getFormMeta?formNames=labourContractForm'
      this.$http
        .get(url)
        .then(response => {
          self.formFields(response.data.forms[0])
        })
        .catch(() => {})
    },
    updateStatus(status, message) {
      if (status && status !== 0) {
        this.summaryData.status = status
      }
      let self = this
      let param = {
        labourContract: {
          id: this.summaryData.id,
          lineItems: this.summaryData.lineItems,
          status: status,
          vendor: this.summaryData.vendor,
        },
      }
      this.$http
        .post('v2/labourcontract/addOrUpdate', param)
        .then(function(response) {
          if (response.data.responseCode === 0) {
            self.$message.success(message)
            self.loadSummary()
          } else {
            self.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    addDescription() {
      this.addDescDialog = false
      if (this.addDescriptionText) {
        let self = this
        let param = {
          labourContract: {
            id: this.summaryData.id,
            description: this.addDescriptionText,
            lineItems: this.summaryData.lineItems,
            storeRoom: this.summaryData.storeRoom,
            vendor: this.summaryData.vendor,
          },
        }
        this.$http
          .post('v2/labourcontract/addOrUpdate', param)
          .then(function(response) {
            if (response.data.responseCode === 0) {
              self.$message.success('Description updated successfully.')
              self.summaryData.description = self.addDescriptionText
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(() => {})
      }
    },
    remove(data, index) {
      this.deleteContractLineItem(
        [this.summaryData.lineItems[index].id],
        'Labor Removed Successfully',
        'labourcontract'
      )
    },
    loadSummary() {
      let self = this
      let url = '/v2/labourcontract/getById'
      let params = {
        recordId: this.id,
      }
      if (this.id) {
        this.$http
          .post(url, params)
          .then(response => {
            self.summaryData =
              response.data.result && response.data.result.labourcontract
                ? response.data.result.labourcontract
                : []
            let title =
              '[#' + self.summaryData.id + '] ' + self.summaryData.name
            self.setTitle(title)
          })
          .catch(() => {})
      }
    },
    formFields(data) {
      this.customFields = data.fields.filter(
        field => field.field && field.field.default !== true
      )
    },
    handleClick() {},
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.quickSearch()
    },
    quickSearch() {
      this.$store.dispatch(
        'labourcontract/updateSearchQuery',
        this.quickSearchQuery
      )
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: this.getCurrentModule,
      })
    },
    loadData(loadMore) {
      let self = this
      this.page = loadMore ? this.page : 1
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: this.$route.query.search
          ? JSON.parse(this.$route.query.search)
          : this.filters,
        search: this.searchQuery,
        includeParentFilter: this.includeParentFilter,
      }
      loadMore ? (self.fetchingMore = true) : (self.loading = true)
      self.$store
        .dispatch('labourcontract/fetchLabourContracts', queryObj)
        .then(function() {
          self.loading = false
          self.fetchingMore = false
          self.page++
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
    },
    getSummaryLink(id) {
      let {
        getCurrentModule,
        $route: { query, params },
      } = this
      let { viewname } = params || {}

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(getCurrentModule, pageTypes.OVERVIEW) || {}
        if (name) {
          this.$router.push({ name, params: { id, viewname }, query })
        }
      } else {
        let url =
          '/app/ct/labourcontracts/' +
          this.$route.params.viewname +
          '/summary/' +
          id
        this.$router.push({ path: url, query: this.$route.query })
      }
    },
    switchCategory(index) {
      let indexs
      if (indexs && indexs !== -1) {
        this.views.splice(indexs, 1)
      }

      let { getCurrentModule } = this
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(getCurrentModule, pageTypes.OVERVIEW) || {}
        if (name) {
          this.$router.push({
            name,
            params: { id: 'null', viewname: index.name },
          })
        }
      } else {
        let url = '/app/ct/labourcontracts/' + index.name + '/summary/' + null
        this.$router.push({ path: url })
      }
      this.toggle = false
    },
    handleCommand(command) {
      if (['edit', 'duplicate'].includes(command)) {
        let { summaryData, getCurrentModule } = this
        let { id } = summaryData || {}
        let query = command === 'duplicate' ? { duplicate: true } : {}

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(getCurrentModule, pageTypes.EDIT) || {}
          if (name) {
            this.$router.push({ name, params: { id }, query })
          }
        } else {
          this.$router.push({
            path: '/app/ct/labourcontracts/edit/' + id,
            query,
          })
        }
      } else if (command === 'delete') {
        this.delete()
      } else if (command === 'revise') {
        this.reviseContract(this.summaryData, 'labourcontract')
      } else if (command === 'revisionhistory') {
        this.$router.push({
          query: {
            showRevisionHistory: JSON.stringify({
              id: this.summaryData.id,
              parentId: this.summaryData.parentId,
              name: this.summaryData.name,
            }),
          },
        })
      } else if (command === 'preference') {
        this.preferenceVisibility = true
      } else if (command === 'updatepayment') {
        this.updatePaymentActions(this.summaryData)
      }
    },
    delete() {
      let self = this
      let url = '/v2/labourcontract/delete'
      let params = {
        recordIds: [this.summaryData.id],
      }
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success(this.$t('common._common.delete_success'))

            if (isWebTabsEnabled()) {
              let {
                getCurrentModule,
                $route: { params },
              } = this
              let { viewname } = params || {}
              let { name } =
                findRouteForModule(getCurrentModule, pageTypes.LIST) || {}

              if (name) {
                this.$router.push({ name, params: { viewname } })
              }
            } else {
              this.$router.push({
                path: '/app/ct/labourcontracts/all',
              })
            }
          }
        })
        .catch(() => {})
    },
    getDateTime(val) {
      let value = val.sysCreatedTime
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value)
    },
    addLineItemsDialog() {
      this.loadLabours()
      this.addLineItemsVisibility = true
      this.lineItemsFormObject.lineItems = [
        {
          labour: {
            id: null,
          },
          cost: null,
        },
      ]
    },
    loadLabours() {
      let self = this
      self.$http
        .get('/v2/labour/labourList')
        .then(function(response) {
          self.labours = response.data.result.labours
        })
        .catch(() => {})
    },
    loadRateForLabour(labourData) {
      let selectedLabour = this.labours.filter(labour => {
        return labourData.labour.id === labour.id
      })
      if (selectedLabour.length === 1) {
        labourData.cost = selectedLabour[0].cost
      } else {
        labourData.cost = null
      }
    },
    addLabourEntry(field) {
      field.push({ labour: { id: null }, cost: null })
    },
    removeLabourEntry(list, index) {
      list.splice(index, 1)
    },
    isValid(val) {
      if (val !== null && val !== '' && val !== undefined) {
        return true
      }
    },
    addLineLabour() {
      for (let i = this.lineItemsFormObject.lineItems.length - 1; i >= 0; i--) {
        if (
          !this.isValid(this.lineItemsFormObject.lineItems[i].labour.id) ||
          !this.isValid(this.lineItemsFormObject.lineItems[i].cost)
        ) {
          this.lineItemsFormObject.lineItems.splice(i, 1)
        }
      }
      if (this.lineItemsFormObject.lineItems.length === 0) {
        this.addLineItemsVisibility = false
        return
      }
      for (let j in this.lineItemsFormObject.lineItems) {
        this.lineItemsFormObject.lineItems[j][
          'labourContractId'
        ] = this.summaryData.id
        // this.summaryData.lineItems.push(this.lineItemsFormObject.lineItems[j])
      }
      let param = {
        lineItems: this.lineItemsFormObject.lineItems,
      }
      let self = this
      this.$http
        .post('v2/labourcontract/addOrUpdateLineItem', param)
        .then(function(response) {
          if (response.data.responseCode === 0) {
            self.addLineItemsVisibility = false
            self.loadSummary()
            self.$message.success(
              this.$t('common._common.labor_added_successfully')
            )
          } else {
            self.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    openVendorSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('vendors', pageTypes.OVERVIEW) || {}
        if (name) {
          return { name, params: { viewname: 'all', id } }
        }
      } else {
        return {
          path: `/app/vendor/vendors/all/summary/${id}`,
        }
      }
    },
  },
}
</script>
