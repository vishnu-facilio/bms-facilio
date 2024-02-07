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
                <summary-side-bar
                  :list="recordList || []"
                  :isLoading.sync="loading"
                  :activeRecordId="id"
                  :total="listCount"
                  :currentCount="(recordList || []).length"
                  :currentPage="page - 1"
                  @nextPage="loadData"
                >
                  <template #title>
                    <div
                      class="fc__layout_media_center fc__submenu__header fc__layout__has__columns pointer"
                    >
                      <i class="el-icon-back fw6" @click="back"></i>
                      <div class="label-txt-black pL10">
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
                          <span slot="reference">
                            {{ currentViewDetail.displayName }}
                            <i
                              class="el-icon-arrow-down el-icon-arrow-down-tv"
                              style="padding-left:8px"
                            ></i>
                          </span>
                        </el-popover>
                      </div>
                    </div>
                  </template>
                  <template v-slot="{ record }">
                    <div
                      class="fc__layout__flexes overflo-auto fc__layout__has__row fc__layout__box"
                    >
                      <div
                        class="fc__submenu__section fc__layout__has__row fc__layout__max__row"
                      >
                        <div
                          class="pB15 pT15 pL20 pR20 flex-middle justify-content-space line-height20"
                          @click="getSummaryLink(record.id)"
                        >
                          <div
                            class="width220px textoverflow-ellipsis"
                            :title="
                              record.name
                                ? record.name
                                : '[#' + record.parentId + ']'
                            "
                            v-tippy="{
                              placement: 'top',
                              animation: 'shift-away',
                              arrow: true,
                            }"
                          >
                            {{
                              record.name ? record.name : '[#' + record.id + ']'
                            }}
                          </div>
                          <img
                            src="~assets/black-arrow-right.svg"
                            width="15"
                            height="15"
                            :class="['fR hide mT3', { show: id === record.id }]"
                          />
                        </div>
                      </div>
                    </div>
                  </template>
                </summary-side-bar>
              </div>
              <!-- main section -->
              <div
                class="fc__layout__flexes fc__layout__has__row fc__layout__asset_main"
              >
                <div class>
                  <!-- man header -->
                  <div
                    class="fc__layout__align fc__asset__main__header pT24 pB20 pL20 pR20"
                  >
                    <div class="fc-dark-grey-txt18">
                      <div class="fc-id">#{{ summaryData.id }}</div>
                      <div class="fc-black-17 bold inline-flex">
                        {{ summaryData.name }}
                      </div>
                    </div>
                    <div class="fc__layout__align inventory-overview-btn-group">
                      <el-dropdown @command="handleCommand">
                        <span class="el-dropdown-link">
                          <img src="~assets/menu.svg" height="18" width="18" />
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item command="edit">{{
                            $t('common._common.edit')
                          }}</el-dropdown-item>
                          <el-dropdown-item command="delete">{{
                            $t('common._common.delete')
                          }}</el-dropdown-item>
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
                        <el-col :span="16">
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label">
                              {{ systemFieldsDisplayNameMap['phone'] }}
                            </div>
                            <div class="label-txt-black pT5">
                              {{
                                summaryData.phone ? summaryData.phone : '---'
                              }}
                            </div>
                          </el-col>
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label">
                              {{ systemFieldsDisplayNameMap['email'] }}
                            </div>
                            <div class="label-txt-black pT5">
                              {{
                                summaryData.email ? summaryData.email : '---'
                              }}
                            </div>
                          </el-col>
                        </el-col>
                        <el-col :span="16">
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label">Type</div>
                            <div class="label-txt-black pT5">
                              {{
                                summaryData.contactType
                                  ? contactTypeEnum[summaryData.contactType]
                                  : '---'
                              }}
                            </div>
                          </el-col>
                        </el-col>
                        <el-col :span="16">
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label pB5">Vendor</div>
                            <router-link
                              :to="{
                                path:
                                  '/app/vendor/vendors/' +
                                  'all/summary/' +
                                  summaryData.vendor.id,
                              }"
                              v-if="summaryData.vendor"
                              >{{ summaryData.vendor.name }}</router-link
                            >
                            <div v-else>---</div>
                          </el-col>
                          <el-col :span="12" class="mB30">
                            <div class="fc-blue-label pB5">Tenant</div>
                            <router-link
                              :to="{
                                path:
                                  '/app/tm/tenants/all/' +
                                  summaryData.tenant.id +
                                  '/overview',
                              }"
                              v-if="summaryData.tenant"
                              >{{ summaryData.tenant.name }}</router-link
                            >
                            <div v-else>---</div>
                          </el-col>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="mT20 mB10 inventory-section-header">
                      Summary
                    </div>
                    <div class="fc__white__bg__info">
                      <el-row>
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{
                                systemFieldsDisplayNameMap[
                                  'isPortalAccessNeeded'
                                ]
                              }}
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.isPortalAccessNeeded ? 'Yes' : 'No'
                          }}</el-col>
                        </el-col>
                        <el-col :span="12">
                          <el-col :span="12">
                            <div class="fc-blue-label">
                              {{
                                systemFieldsDisplayNameMap['isPrimaryContact']
                              }}
                            </div>
                          </el-col>
                          <el-col :span="12">{{
                            summaryData.isPrimaryContact ? 'Yes' : 'No'
                          }}</el-col>
                        </el-col>
                      </el-row>
                    </div>
                    <div
                      class="fc__white__bg__info__custom mT20"
                      v-if="customFields && customFields.length > 0"
                    >
                      <el-row
                        class="border-bottom6 pB20 pT20"
                        v-if="index % 2 === 0"
                        v-for="(d, index) in customFields"
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
                              customFields[index].field.dataTypeEnum === 'DATE'
                            "
                            >{{
                              summaryData.data &&
                              summaryData.data[customFields[index].name] > 0
                                ? $options.filters.formatDate(
                                    summaryData.data[customFields[index].name],
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
                                summaryData.data[customFields[index].name]) > 0
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
                              summaryData.data[customFields[index].name] !== -1
                                ? summaryData.data[customFields[index].name]
                                : '---'
                            }}</el-col
                          >
                          <el-col
                            :span="12"
                            v-else-if="
                              customFields[index].field.dataTypeEnum === 'ENUM'
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
                              summaryData.data[customFields[index + 1].name] > 0
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
                              summaryData.data[customFields[index + 1].name] &&
                              summaryData.data[customFields[index + 1].name] !==
                                -1
                                ? summaryData.data[customFields[index + 1].name]
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
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <ContactForm
        :visibility.sync="editFormVisibility"
        moduleName="contact"
        :editData="editData"
        @saved="refreshData"
      ></ContactForm>
    </div>
  </div>
</template>
<script>
import ContactForm from './ContactForm'
import SummarySideBar from 'newapp/components/SummarySideBar'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { API } from '@facilio/api'

export default {
  components: {
    ContactForm,
    SummarySideBar,
  },

  data() {
    return {
      recordList: null,
      toggle: false,
      loading: true,
      page: 1,
      summaryData: null,
      customFields: [],
      editFormVisibility: false,
      systemFieldsDisplayNameMap: {},
      editData: null,
      listCount: 0,
      contactTypeEnum: {
        1: 'Tenant',
        2: 'Vendor',
        3: 'Employee',
      },
    }
  },

  mounted() {
    this.$store.dispatch('view/clearViews')
    this.$store.dispatch('view/loadViews', this.getCurrentModule)
    this.$store.dispatch('view/loadModuleMeta', this.getCurrentModule)
    this.loadRecordsCount()
    this.getViewDetail()
    this.loadData()
    this.loadSummary()
    this.loadForm()
  },

  computed: {
    ...mapState({
      views: state => state.view.views,
    }),
    id() {
      return parseInt(this.$route.params.id)
    },
    getCurrentModule() {
      return 'contact'
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }

      return null
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return {
          displayName: 'Filtered Contact',
          name: 'filteredcontact',
        }
      }
      return this.views.find(view => view.name === this.currentView) || {}
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
  },

  watch: {
    recordList(newVal) {
      if (this.$route.params.id === 'null' && newVal && newVal.length > 0) {
        this.getSummaryLink(newVal[0].id)
      }
    },
    id(newVal) {
      if (this.$route.query.refresh) {
        this.loadData()
      }
      this.loadSummary()
    },
    filters(newVal) {
      if (!isEmpty(newVal)) {
        this.views.push({
          displayName: 'Filtered Contacts',
          name: 'filteredcontact',
        })
      }
    },
  },

  methods: {
    refreshData() {
      this.loadData()
      this.loadSummary()
    },
    loadRecordsCount() {
      let { filters, currentView } = this
      let params = {
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
        fetchCount: true,
      }
      let url = `/v2/contacts/${currentView}`

      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
        } else {
          this.listCount = data.recordCount || 0
        }
      })
    },

    loadForm() {
      let url = '/getFormMeta'
      let param = { formNames: 'contactForm' }

      API.get(url, param).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
        } else {
          this.formFields(data.forms[0])
        }
      })
    },
    formFields(data) {
      this.customFields = []
      data.fields.forEach(field => {
        if (field.field && field.field.default !== true) {
          this.customFields.push(field)
        } else {
          if (field.field) {
            this.systemFieldsDisplayNameMap[field.field.name] = field.field
              .displayName
              ? field.field.displayName
              : field.field.name
          }
        }
      })
    },
    loadSummary() {
      let url = '/v2/contacts/details'
      let param = { recordId: this.id }

      if (this.id) {
        API.get(url, param).then(({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error occured')
          } else {
            this.summaryData = data.contact || []
          }
        })
      }
    },
    back() {
      let url = '/app/home/contact/' + this.$route.params.viewname
      this.$router.push({ path: url, query: this.$route.query })
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: this.getCurrentModule,
      })
    },
    loadData(changeView, currentView) {
      this.page = changeView ? 1 : this.page
      currentView ? null : (currentView = this.currentView)

      let params = {
        page: this.page,
        perPage: 50,
        filters: !isEmpty(this.filters)
          ? encodeURIComponent(this.filters)
          : null,
      }
      let url = `/v2/contacts/${currentView}`

      !changeView ? (this.loading = true) : null
      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
        } else {
          this.recordList = data.contacts
          this.page += 1
        }
      })
    },
    getSummaryLink(id) {
      let url =
        '/app/home/contact/' + this.$route.params.viewname + '/summary/' + id
      this.$router.replace({ path: url, query: this.$route.query })
    },
    switchCategory(view) {
      let filterIndex = this.views.findIndex(
        view => view.name === 'filteredList'
      )

      if (filterIndex !== -1) {
        this.views.splice(filterIndex, 1)
      }

      let url = `/app/home/contact/${view.name}/summary/null`

      this.loadModuleMeta(this.getCurrentModule).then(() => {
        this.$router.replace({ path: url })
        this.loadData(true, view.name)
      })

      this.toggle = false
    },
    loadModuleMeta(moduleName) {
      return this.$store.dispatch('view/loadModuleMeta', moduleName)
    },
    handleCommand(command) {
      if (command === 'delete') {
        this.delete()
      } else if (command === 'edit') {
        this.editData = this.$helpers.cloneObject(this.summaryData)
        this.editData.contactType =
          this.editData.contactType > 0 ? String(this.editData.contactType) : ''
        this.editFormVisibility = true
      }
    },
    delete() {
      let url = '/v2/contacts/delete'
      let params = {
        contactIds: [this.summaryData.id],
      }

      API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
        } else {
          this.$message.success('Deleted Successfully')
          this.$router.push({
            path: `/app/home/contact/${this.currentView}`,
          })
        }
      })
    },
    getDateTime(val, obj) {
      let value = val[obj.property]
      return !value || value === -1
        ? '---'
        : this.$options.filters.formatDate(value)
    },
  },
}
</script>
