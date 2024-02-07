<template>
  <div
    class="catalog-container mT35 mL60 mR60 mB40"
    :class="isApp ? 'catalog-app-container' : ''"
  >
    <div v-if="isLoading">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else class="catalog-request-container">
      <div class="catalog-request-header">
        <div class="d-flex">
          <inline-svg
            v-if="$validation.isEmpty(serviceCatalog.photoUrl)"
            src="vehicle-delivery"
            class="vertical-middle catalog-icon-request"
            iconClass="icons"
          ></inline-svg>
          <img
            v-else
            :src="serviceCatalog.photoUrl"
            class="inline vertical-middle catalog-icon-request"
          />
          <div class="d-flex flex-direction-column catalog-header-content">
            <div class="catalog-nav">
              <div>
                <span class="catalog-text">{{
                  $t('common._common.service_catalog')
                }}</span>
              </div>
              <div class="mR10">
                <span class="catalog-text">></span>
              </div>
              <div>
                <span class="catalog-text">{{ serviceCatalogGroup.name }}</span>
              </div>
            </div>
            <div class="mB10">
              <span class="catalog-title">{{ serviceCatalog.name }}</span>
            </div>
            <div class="width60">
              <span class="catalog-title-desc">{{
                serviceCatalog.description
              }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="catalog-form-container">
        <div class="catalog-editable">
          <f-webform
            ref="service-catalog-creation"
            :form.sync="formObj"
            :module="module"
            :isEdit="false"
            :isSaving="isSaving"
            :customClass="getCustomClass()"
            :isV3Api="isV3Api"
            :canShowPrimaryBtn="true"
            :canShowSecondaryBtn="true"
            @save="submitRequest"
            @cancel="redirectToCatalogList(groupId)"
          ></f-webform>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import {
  isEmpty,
  isObject,
  areValuesEmpty,
  isFunction,
} from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import WebFormMixin from '@/mixins/forms/WebFormMixin'
import DetailsWidgetMixin from '@/page/widget/common/DetailsWidgetMixin'
import DataCreationMixin from '@/mixins/DataCreationMixin'
import FWebform from '@/FWebform'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
import TasksMixin from '@/mixins/tasks/TasksMixin'

export default {
  props: ['isApp'],
  mixins: [WebFormMixin, DetailsWidgetMixin, DataCreationMixin, TasksMixin],
  components: {
    Spinner,
    FWebform,
  },
  name: 'CatalogRequest',
  data() {
    return {
      serviceCatalog: {
        form: {
          sections: [],
        },
      },
      serviceCatalogGroup: {},
      isLoading: false,
      isSaving: false,
      formModel: {
        requester: {
          name: '',
          email: '',
        },
      },
      formObj: {},
      moduleMap: {
        workorder: {
          url: 'v2/workorders/add',
          dataKey: 'workorder',
          routerObj: {
            app: 'wosummarynew',
          },
          result: 'workorder',
        },
        vendors: {
          url: 'v2/vendors/add',
          dataKey: 'vendor',
          routerObj: {
            app: 'vendorsummary',
          },
          result: 'vendor',
          params: {
            viewname: 'all',
            vendorid: null,
          },
          paramKey: 'vendorid',
        },
        visitorlog: {
          routerObj: {
            app: 'visitorInviteList',
          },
          params: {
            viewname: 'all',
          },
        },
        workpermit: {
          url: 'v2/workpermit/add',
          dataKey: 'workPermit',
          routerObj: {
            app: 'workPermitSummary',
          },
          result: 'workPermitRecords',
          params: {
            viewname: 'all',
            id: null,
          },
          paramKey: 'id',
        },
        asset: {
          url: 'v2/assets/add',
          dataKey: 'asset',
          routerObj: {
            app: 'assetsummary',
          },
          result: 'asset',
          params: {
            viewname: 'all',
            assetid: null,
          },
          paramKey: 'assetid',
        },
        tenantcontact: {
          url: 'v2/tenantcontact/add',
          dataKey: 'tenantContacts',
          routerObj: {
            app: 'catalog-list',
          },
          result: 'tenantcontacts',
        },
        tenantunit: {
          url: 'v2/space/add',
          dataKey: 'tenantSpace',
          routerObj: {
            app: 'catalog-list',
          },
          result: 'spaceId',
        },
        rentalleasecontracts: {
          url: 'v2/rentalleasecontract/add',
          dataKey: 'rentalLeaseContract',
          routerObj: {
            app: 'catalog-list',
          },
          result: 'rentalleasecontract',
        },
        reservation: {
          url: 'v2/reservations/add',
          dataKey: 'reservation',
          routerObj: {
            app: 'catalog-list',
          },
          result: 'reservation',
        },
      },
    }
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
    this.loadCatalogDetail()
  },
  computed: {
    ...mapGetters(['getSpaceCategoryPickList']),
    isV3Api() {
      return true
    },
    spaceCategories() {
      return this.getSpaceCategoryPickList()
    },
    catalogId() {
      let { $route } = this
      let { params } = $route || {}
      if (!isEmpty(params)) {
        let { catalogId } = params
        return catalogId
      }
      return null
    },
    groupId() {
      let { serviceCatalog } = this
      if (!isEmpty(serviceCatalog)) {
        let { groupId } = serviceCatalog
        return groupId
      }
      return null
    },
    groupName() {
      let { $route } = this
      let { params } = $route || {}
      if (!isEmpty(params)) {
        let { groupName } = params
        return groupName
      }
      return null
    },
    module() {
      let { serviceCatalog } = this
      let { form } = serviceCatalog
      if (!isEmpty(form)) {
        let { module } = form
        let { name } = module || {}
        return name
      }
      return null
    },
    moduleObj() {
      let { serviceCatalog } = this
      let { form } = serviceCatalog
      if (!isEmpty(form)) {
        let { module } = form
        return module
      }
      return null
    },
    portalUserObj() {
      let { $portaluser } = this
      if (!isEmpty($portaluser)) {
        return $portaluser
      }
      return {}
    },
  },
  methods: {
    async loadCatalogDetail() {
      this.isLoading = true

      let { error, data } = await API.get(`v2/servicecatalog/detail`, {
        id: this.catalogId,
      })

      if (error) {
        this.$message.error(error.message || 'Could not fetch catalog')
      } else {
        let { serviceCatalog } = data
        let { form, groupId } = serviceCatalog
        if (!isEmpty(groupId)) {
          await this.loadCatalogGroupDetail(groupId)
        }
        if (!isEmpty(form)) {
          this.serviceCatalog = serviceCatalog
          let formObj = {
            ...form,
          }
          formObj.secondaryBtnLabel = 'CANCEL'
          formObj.primaryBtnLabel = 'SUBMIT'
          this.$set(this, 'formObj', formObj)
        }
        this.isLoading = false
      }
    },
    async loadCatalogGroupDetail(groupId) {
      let { error, data } = await API.get(`v2/servicecataloggroup/detail`, {
        id: groupId,
      })
      if (error) {
        this.$message.error(error.message)
      } else {
        let { serviceCatalogGroup } = data
        if (!isEmpty(serviceCatalogGroup)) {
          this.serviceCatalogGroup = serviceCatalogGroup
        }
      }
    },
    getCustomClass() {
      return `service-catalog-form`
    },
    afterSerializeHook({ data }) {
      const comp = this.$refs['service-catalog-creation']

      let tasksList = comp.getTasksList()
      let tasksSerializedData = {}
      let sequenceNumber = 1
      let sectionNameList = []

      // Handling for workorder: Adding taskString
      if (!isEmpty(tasksList)) {
        tasksList.forEach(task => {
          let { tasks, section } = task
          tasksSerializedData[section] = []
          tasks.forEach(task => {
            let _task = this.serializeTaskData(
              cloneDeep(task),
              sequenceNumber++
            )
            tasksSerializedData[section].push(_task)
          })
          if (!isEmpty(section)) {
            sectionNameList.push(section)
          }
        })

        data = {
          ...data,
          tasksString: tasksSerializedData,
          sectionNameList,
        }
      }
      return data
    },
    async submitRequest(dataModel) {
      let {
        serviceCatalog: { form },
        formModel,
        module,
        portalUserObj,
        afterSerializeHook,
        formObj,
      } = this

      let { requester } = formModel
      let _formModel = { ...formModel, ...dataModel }
      let { formId } = form || {}

      let serializedData = this.serializedData(form, _formModel)

      if (!isEmpty(afterSerializeHook) && isFunction(afterSerializeHook)) {
        serializedData = this.afterSerializeHook({
          data: serializedData,
          formModel: dataModel,
          formObj,
        })
      }

      if (isEmpty(formId)) {
        formId = (form || {}).id
      }
      serializedData.formId = formId || -1

      if (!isEmpty(portalUserObj)) {
        let { name, email, id } = portalUserObj
        // Please dont add further check here,
        if (module.includes('vendor')) {
          serializedData.requester = { name, email }
          serializedData.registeredBy = { id }
        } else if (module.includes('workpermit')) {
          serializedData.requestedBy = { id }
        } else if (module.includes('visitorlog')) {
          serializedData.requestedBy = { id }
        } else if (module.includes('tenantunit')) {
          let unitCategoryId = Object.keys(this.spaceCategories).find(
            key => this.spaceCategories[key] === 'Tenant Unit'
          )
          if (!isEmpty(unitCategoryId)) {
            serializedData['spaceCategory'] = {
              id: unitCategoryId,
            }
          }
        }
      } else if (!isEmpty(requester) && !areValuesEmpty(requester)) {
        serializedData.requester = requester
      }

      this.isSaving = true

      let { error, [module]: record } = await API.createRecord(module, {
        data: serializedData,
      })

      if (error) {
        this.$message.error(error.message)
      } else {
        this.$message.success('Request created successfully.')
        this.redirectToRecord(record)
      }
      this.isSaving = false
    },
    redirectToCatalogList(groupId) {
      this.$router.push({
        name: 'service-catalog-list',
        query: {
          groupId,
        },
      })
    },
    redirectToRecord(record) {
      // Remove all this when goToRoute support is available in @facilio/router
      let { moduleMap, module } = this
      let routerName = this.$getProperty(moduleMap[module], 'routerObj.app')
      let paramObj = this.$getProperty(moduleMap[module], 'params')

      let id
      if (Array.isArray(record)) {
        id = record[0].id
      } else if (isObject(record)) {
        id = record.id
      }
      if (!isEmpty(paramObj)) {
        let paramKey = this.$getProperty(moduleMap[module], 'paramKey')
        if (paramKey) paramObj[paramKey] = id
      } else {
        paramObj = { id }
      }
      this.$router.push({
        name: routerName,
        params: paramObj,
      })
    },
  },
}
</script>
<style lang="scss">
.catalog-icon-request {
  max-width: 120px;
  height: 120px;
  margin-right: 20px;
  width: 120px;
  background: #fff;
  svg {
    width: 120px;
    height: 120px;
  }
}
</style>
