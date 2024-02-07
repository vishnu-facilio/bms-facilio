import { isEmpty } from '@facilio/utils/validation'
import store from '../../store'

export default {
  path: '/:app/pdf',
  component: () => import('pages/pdfHome'),
  meta: {
    requiresAuth: true,
  },
  beforeEnter: (to, from, next) => {
    let { query } = to || {}
    let { currentTabId, currentGroupId } = query || {}

    if (!isEmpty(currentGroupId) && !isEmpty(currentTabId)) {
      store.dispatch('webtabs/setTabGroup', parseInt(currentGroupId))
      store.dispatch('webtabs/setTab', parseInt(currentTabId))
    }
    next()
  },
  children: [
    {
      path: 'billing',
      component: () => import('pages/pdf/Billing'),
    },
    {
      path: 'summarydownloadreport',
      component: () => import('pages/pdf/WoSummaryReportDownload'),
    },
    {
      path: 'quotationpdf',
      component: () => import('pages/pdf/QuotationPdf'),
    },
    {
      path: 'popdf',
      component: () => import('pages/pdf/PoPdf'),
    },
    {
      path: 'prpdf',
      component: () => import('pages/pdf/PrPdf'),
    },
    {
      path: 'rfqpdf',
      component: () => import('pages/pdf/RfqPdf'),
    },
    {
      path: 'vendorQuoteRfqPdf',
      component: () => import('pages/pdf/VendorQuoteRfqPdf'),
    },
    {
      path: 'workpermit',
      component: () => import('pages/pdf/WorkPermitPdf'),
    },
    {
      path: 'tenantbilling',
      component: () => import('pages/pdf/TenantBillingPdf'),
    },
    {
      path: 'tenantbillings',
      component: () => import('pages/pdf/TenantBillingDirectControl'),
    },
    {
      path: 'paymentmemopdf',
      component: () => import('pages/pdf/paymentmemopdf'),
    },
    {
      path: 'pmplanner',
      component: () => import('pages/pdf/PmPlannerpdf'),
    },
    {
      path: 'dashboardpdf',
      component: () => import('pages/pdf/DashboardSinglePdf'),
    },
    {
      path: 'mv/:id',
      component: () => import('pages/pdf/MVSummaryPdf.vue'),
    },
    {
      path: 'visitorinvite/:inviteid/:visitorid',
      component: () => import('pages/pdf/VisitorInvitePdf.vue'),
    },
    {
      path: 'visitorbadge',
      component: () => import('pages/pdf/VisitorBadgePdf.vue'),
    },
    {
      path: 'visitornda/:id',
      component: () => import('pages/pdf/VisitorNdaPdf.vue'),
    },
    {
      path: 'widget/:appname/:widgetname',
      component: () => import('pages/connectedapps/ConnectedAppWidgetPdf'),
    },
    {
      path: 'inspectionpdf',
      component: () => import('pages/pdf/QandaPdf'),
      props: () => ({
        moduleName: 'inspectionResponse',
      }),
    },
    {
      path: 'inductionPdf',
      component: () => import('pages/pdf/QandaPdf'),
      props: () => ({
        moduleName: 'inductionResponse',
      }),
    },
    {
      path: 'surveyPdf',
      component: () => import('pages/pdf/QandaPdf'),
      props: () => ({
        moduleName: 'inductionResponse',
      }),
    },
    {
      path: 'utilityBillspdf',
      component: () => import('src/pages/pdf/UtilityBillPdf.vue'),
    },
  ],
}
