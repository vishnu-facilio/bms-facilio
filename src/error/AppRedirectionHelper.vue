<template>
  <div v-if="isLoading" class="mT40">
    <Spinner></Spinner>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { pageTypes } from '@facilio/router'
import getProperty from 'dlv'
import Spinner from 'src/components/Spinner'

const redirectionRequiredUrls = {
  workorder: {
    summaryRoute: 'app/wo/(orders)/summary/([0-9]+)',
    createRoute: 'app/wo/create',
    listRoute: 'app/wo/orders/([a-zA-Z0-9\-\_]+)',
    pdfRoute: 'app/pdf/summarydownloadreport',
  },
  asset: {
    summaryRoute: 'app/at/assets/([a-zA-Z0-9\-\_]+)/([0-9]+)/overview',
    listRoute: 'app/at/assets/([a-zA-Z0-9\-\_]+)',
  },
  serviceRequest: {
    summaryRoute: 'app/sr/serviceRequest/([a-zA-Z0-9\-\_]+)/([0-9]+)/overview',
    editRoute: 'app/sr/serviceRequest/edit/([0-9]+)',
    createRoute: 'app/sr/serviceRequest/new',
    listRoute: 'app/sr/serviceRequest/([a-zA-Z0-9\-\_]+)',
  },
  customModule: {
    summaryRoute:
      'app/ca/modules/(custom_.+)/([a-zA-Z0-9\-\_]+)/([0-9]+)/summary',
    editRoute: 'app/ca/modules/(custom_.+)/edit/([0-9]+)',
    createRoute: 'app/ca/modules/(custom_.+)/create',
    listRoute: 'app/ca/modules/(custom_.+)/([a-zA-Z0-9\-\_]+)',
  },

  purchaserequest: {
    summaryRoute: 'app/purchase/pr/([a-zA-Z0-9\-\_]+)/summary/([0-9]+)',
    editRoute: 'app/purchase/pr/new/([0-9]+)',
    createRoute: 'app/purchase/pr/new',
    listRoute: 'app/purchase/pr/([a-zA-Z0-9\-\_]+)',
    pdfRoute: 'app/pdf/prpdf',
  },
  purchaseorder: {
    summaryRoute: 'app/purchase/po/(([a-zA-Z0-9\-\_]+)/summary/([0-9]+)',
    createRoute: 'app/purchase/po/new',
  },
  receivable: {
    listRoute: 'app/purchase/rv/(([a-zA-Z0-9\-\_]+)',
  },
  tenant: {
    listRoute: 'app/tm/tenants/([a-zA-Z0-9\-\_]+)',
  },
  quote: {
    editRoute: 'app/tm/quotation/edit/([0-9]+)',
    createRoute: 'app/tm/quotation/new',
    pdfRoute: 'app/pdf/quotationpdf',
  },
  itemTypes: {
    createRoute: 'app/inventory/itemtypes/new',
  },
  toolTypes: {
    createRoute: 'app/inventory/tooltypes/new',
  },
  transferrequest: {
    createRoute: 'app/inventory/transferrequest/new',
  },
  //no need to handle create edit since they are dialogue
  item: {
    summaryRoute: 'app/inventory/item/([a-zA-Z0-9\-\_]+)/([0-9]+)/summary',
    listRoute: 'app/inventory/item/([a-zA-Z0-9\-\_]+)',
  },
  vendors: {
    createRoute: 'app/vendor/vendors/new',
  },
  client: {
    listRoute: 'app/cl/client/([a-zA-Z0-9\-\_]+)',
  },
  announcement: {
    createRoute: 'app/cy/announcements/new',
  },
  //there is no create and edit route
  inspectionResponse: {
    summaryRoute:
      'app/inspection/individual/([a-zA-Z0-9\-\_]+)/summary/([0-9]+)',
    listRoute: 'app/inspection/individual/([a-zA-Z0-9\-\_]+)',
  },
  facility: {
    summaryRoute: 'app/bk/facility/([a-zA-Z0-9\-\_]+)/([0-9]+)/overview',
    editRoute: 'app/bk/facility/([0-9]+)/edit',
    createRoute: 'app/bk/facility/new',
    listRoute: 'app/bk/facility/([a-zA-Z0-9\-\_]+)',
  },
  employee: {
    summaryRoute: 'app/pl/employee/([a-zA-Z0-9\-\_]+)/summary/([0-9]+)',
    editRoute: 'app/pl/employee/edit/([0-9]+)',
    createRoute: 'app/pl/employee/new',
    listRoute: 'app/pl/employee/([a-zA-Z0-9\-\_]+)',
  },
  facilitybooking: {
    summaryRoute: 'app/bk/facilitybooking/([a-zA-Z0-9\-\_]+)/([0-9]+)/overview',
    createRoute: 'app/bk/facilitybooking/new',
    listRoute: 'app/bk/facilitybooking/([a-zA-Z0-9\-\_]+)',
  },
  vendorcontact: {
    summaryRoute:
      'app/vendor/vendorcontact/([a-zA-Z0-9\-\_]+)/summary/([0-9]+)',
    editRoute: 'app/vendor/vendorcontact/edit/([0-9]+)',
    createRoute: 'app/vendor/vendorcontact/new',
    listRoute: 'app/vendor/vendorcontact/([a-zA-Z0-9\-\_]+)',
  },
  newreadingalarm: {
    summaryRoute: 'app/fa/faults/([a-zA-Z0-9\-\_]+)/newsummary/([0-9]+)',
    listRoute: 'app/fa/faults/([a-zA-Z0-9\-\_]+)',
  },
  portfolio: {
    siteSummaryRoute: 'app/home/portfolio/sites/all/site/([0-9]+)/overview',
    siteChildSummaryRoute:
      'app/home/portfolio/sites/all/site/([0-9]+)/([a-zA-Z0-9\-\_]+)/([0-9]+)',
  },
  dashboard: {
    dashboardRoute: 'app/home/dashboard/([a-zA-Z0-9\-\_]+)',
  },
  analytics: {
    analyticsRoute: 'app/em/analytics/([a-zA-Z0-9\-\_]+)',
  },
}

export default {
  components: { Spinner },
  data() {
    return {
      isLoading: false,
    }
  },
  computed: {
    previousRoute() {
      let prevRoute = getProperty(
        this,
        '$route.params.currentRoute',
        window.location.href
      )
      return prevRoute
    },
    previousRouteEndPoint() {
      let { previousRoute = '' } = this
      let url = (previousRoute || '').split('//')
      let endPoint = ''
      if ((url || []).length > 1) {
        //this will give url without http:// or https://
        url = url[1]
        let baseUrl = (url || '').split('/', 1)
        endPoint = url.replace(baseUrl, '')
      }
      return endPoint
    },
    searchQuery() {
      let searchQuery = getProperty(this, '$route.params.searchQuery', '')
      return searchQuery
    },
  },
  created() {
    this.redirectionHandler()
  },
  methods: {
    async redirectionHandler() {
      this.isLoading = true
      let {
        previousRoute = '',
        previousRouteEndPoint = '',
        searchQuery = '',
      } = this
      let routeGroup = this.findGroupForRoute(previousRoute)
      if (routeGroup) {
        let {
          matched,
          moduleName,
          pageType,
          viewName,
          recordId,
          childModuleName,
          childRecordId,
        } = this.findDetailsFromRoute(previousRoute, routeGroup)
        if (matched) {
          let { data } = await API.get('v3/oldAppMigration/appRedirection', {
            moduleName,
            viewName,
            pageType,
            recordId,
            previousRoute: previousRouteEndPoint,
            childModuleName,
            childRecordId,
          })
          let { redirectUrl } = data || {}
          if (!isEmpty(redirectUrl) && redirectUrl != previousRouteEndPoint) {
            let url = window.location.origin + redirectUrl
            if (!isEmpty(searchQuery)) {
              url = url + searchQuery
            }
            window.location.href = url
            return
          }
        }
      }
      window.location.href = window.location.origin
    },
    findGroupForRoute(errorCausedRoute) {
      if (
        errorCausedRoute.includes('app/wo/create') ||
        errorCausedRoute.includes('app/wo/orders') ||
        errorCausedRoute.includes('app/pdf/summarydownloadreport')
      ) {
        return 'workorder'
      } else if (errorCausedRoute.includes('app/at/assets')) {
        return 'asset'
      } else if (errorCausedRoute.includes('app/sr/serviceRequest')) {
        return 'serviceRequest'
      } else if (errorCausedRoute.includes('app/ca')) {
        return 'customModule'
      } else if (
        errorCausedRoute.includes('app/purchase/pr') ||
        errorCausedRoute.includes('app/pdf/prpdf')
      ) {
        return 'purchaserequest'
      } else if (errorCausedRoute.includes('app/purchase/po')) {
        return 'purchaseorder'
      } else if (errorCausedRoute.includes('app/purchase/rv')) {
        return 'receivable'
      } else if (errorCausedRoute.includes('app/tm/tenants')) {
        return 'tenant'
      } else if (
        errorCausedRoute.includes('app/tm/quotation') ||
        errorCausedRoute.includes('app/pdf/quotationpdf')
      ) {
        return 'quote'
      } else if (errorCausedRoute.includes('app/inventory/itemtypes')) {
        return 'itemTypes'
      } else if (errorCausedRoute.includes('app/inventory/tooltypes')) {
        return 'toolTypes'
      } else if (errorCausedRoute.includes('app/inventory/transferrequest')) {
        return 'transferrequest'
      } else if (errorCausedRoute.includes('app/inventory/item')) {
        return 'item'
      } else if (errorCausedRoute.includes('app/vendor/vendors')) {
        return 'vendors'
      } else if (errorCausedRoute.includes('app/cl/client')) {
        return 'client'
      } else if (errorCausedRoute.includes('app/cy/announcements')) {
        return 'announcement'
      } else if (errorCausedRoute.includes('app/inspection/individual')) {
        return 'inspectionResponse'
      } else if (errorCausedRoute.includes('app/bk/facilitybooking')) {
        return 'facilitybooking'
      } else if (errorCausedRoute.includes('app/bk/facility')) {
        return 'facility'
      } else if (errorCausedRoute.includes('app/pl/employee')) {
        return 'employee'
      } else if (errorCausedRoute.includes('app/vendor/vendorcontact')) {
        return 'vendorcontact'
      } else if (errorCausedRoute.includes('app/fa/faults')) {
        return 'newreadingalarm'
      } else if (errorCausedRoute.includes('app/home/portfolio/sites/all')) {
        return 'portfolio'
      } else if (errorCausedRoute.includes('app/home/dashboard')) {
        return 'dashboard'
      } else if (errorCausedRoute.includes('app/em/analytics')) {
        return 'analytics'
      }
      return ''
    },
    findDetailsFromRoute(errorCausedRoute, routeGroup) {
      let matched = false
      let moduleName = ''
      let viewName = ''
      let recordId = -1
      let pageType = ''
      let childModuleName = ''
      let childRecordId = -1
      let redirectionRequiredRoutesInGroup = redirectionRequiredUrls[routeGroup]
      let {
        summaryRoute,
        createRoute,
        editRoute,
        listRoute,
        pdfRoute,
        dashboardRoute,
        analyticsRoute,
        siteSummaryRoute,
        siteChildSummaryRoute,
      } = redirectionRequiredRoutesInGroup || {}
      if (summaryRoute) {
        const rgx = new RegExp(summaryRoute)
        if (rgx.test(errorCausedRoute)) {
          let result = errorCausedRoute.match(rgx)
          matched = true
          viewName = result[1]
          recordId = result[2]
          moduleName = routeGroup
          pageType = pageTypes.OVERVIEW
          //since wo route has no view name in route
          if (routeGroup == 'workorder') {
            viewName = 'all'
          }
          if (routeGroup == 'customModule') {
            moduleName = result[1]
            viewName = result[2]
            recordId = result[3]
          }
        }
      }
      if (!matched && editRoute) {
        const rgx = new RegExp(editRoute)
        if (rgx.test(errorCausedRoute)) {
          let result = errorCausedRoute.match(rgx)
          matched = true
          recordId = result[1]
          moduleName = routeGroup
          pageType = pageTypes.EDIT
          //module name is dynamic will be extracted from route
          if (routeGroup == 'customModule') {
            // in matched group list - 1s group is moduleName and 2nd is recordId
            moduleName = result[1]
            recordId = result[2]
          }
        }
      }
      if (!matched && createRoute) {
        const rgx = new RegExp(createRoute)
        if (rgx.test(errorCausedRoute)) {
          let result = errorCausedRoute.match(rgx)
          matched = true
          moduleName = routeGroup
          pageType = pageTypes.CREATE
          if (routeGroup == 'customModule') {
            moduleName = result[1]
          }
        }
      }
      if (!matched && listRoute) {
        const rgx = new RegExp(listRoute)
        if (rgx.test(errorCausedRoute)) {
          let result = errorCausedRoute.match(rgx)
          matched = true
          moduleName = routeGroup
          pageType = pageTypes.LIST
          viewName = result[1]
          if (routeGroup == 'customModule') {
            moduleName = result[1]
            viewName = result[2]
          }
        }
      }
      if (!matched && pdfRoute) {
        const rgx = new RegExp(pdfRoute)
        if (rgx.test(errorCausedRoute)) {
          matched = true
          moduleName = routeGroup
          pageType = 'PRINT_PAGE'
        }
      }
      if (!matched && dashboardRoute) {
        const rgx = new RegExp(dashboardRoute)
        if (rgx.test(errorCausedRoute)) {
          let result = errorCausedRoute.match(rgx)
          matched = true
          moduleName = routeGroup
          pageType = 'DASHBOARD'
          viewName = result[1]
        }
      }
      if (!matched && analyticsRoute) {
        const rgx = new RegExp(analyticsRoute)
        if (rgx.test(errorCausedRoute)) {
          let result = errorCausedRoute.match(rgx)
          matched = true
          moduleName = routeGroup
          childModuleName = result[1]
          pageType = 'ANALYTICS'
        }
      }
      if (!matched && siteSummaryRoute) {
        const rgx = new RegExp(siteSummaryRoute)
        if (rgx.test(errorCausedRoute)) {
          let result = errorCausedRoute.match(rgx)
          matched = true
          moduleName = routeGroup
          recordId = result[1]
          pageType = 'SITE_SUMMARY'
        }
      }
      if (!matched && siteChildSummaryRoute) {
        const rgx = new RegExp(siteChildSummaryRoute)
        if (rgx.test(errorCausedRoute)) {
          let result = errorCausedRoute.match(rgx)
          matched = true
          moduleName = routeGroup
          recordId = result[1]
          childModuleName = result[2]
          childRecordId = result[3]
          pageType = 'SITE_CHILD_SUMMARY'
        }
      }
      return {
        matched,
        moduleName,
        viewName,
        recordId,
        pageType,
        childModuleName,
        childRecordId,
      }
    },
  },
}
</script>
