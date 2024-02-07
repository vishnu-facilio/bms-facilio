if (window.webpackPublicPath) {
  let publicPath = window.webpackPublicPath.trim()
  publicPath = publicPath.endsWith('/') ? publicPath : publicPath + '/'
  /* eslint-disable no-undef */
  __webpack_public_path__ = publicPath
}

// do all 3rd party style imports here, so that those rules are applied first
import './themes/app.mat.styl'
import './assets/styles/agfont.css'

import 'quasar-extras/material-icons'
import 'quasar-extras/fontawesome'
import 'element-ui/lib/theme-chalk/index.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.bubble.css'
import 'accessible-nprogress/dist/accessible-nprogress.css'
import 'mapbox-gl/dist/mapbox-gl.css'
import '@mapbox/mapbox-gl-draw/dist/mapbox-gl-draw.css'

import 'styles/common.css'
import 'styles/white-theme.css'
import 'styles/black-theme.scss'
import 'styles/form.css'
import 'styles/colors.css'
import 'styles/avatar.css'
import 'styles/leed.css'
import 'styles/firealarm.css'
import 'styles/energy.css'
import 'styles/space.css'
import 'styles/element.css'
import 'styles/summary.css'
import 'styles/filter.css'
import 'styles/all-page.scss'
import 'styles/components.scss'
import 'styles/helper.scss'
import 'src/charts/styles/chart.css'
import 'styles/dashboard.css'
import 'styles/c3.css'
import 'styles/custom-icon.css'
import 'styles/animation.css'
import 'styles/mobile-app.scss'
import 'styles/workorder.scss'
import 'styles/alarm.css'
import 'styles/setup.scss'
import 'styles/calendar.css'
import 'styles/markdown-style.css'
import 'styles/historical.scss'
import 'styles/svgs.scss'
import 'styles/print.scss'
import 'styles/forms.scss'
import 'styles/alarm.scss'
import 'styles/page.scss'
import 'styles/stateflow.scss'
import 'styles/floorplan.scss'
import 'styles/inventory.scss'
import 'styles/pmplanner.scss'
import 'styles/bookingplanner.scss'
import 'styles/analytics.scss'
import 'styles/asset.scss'
import 'styles/mv.scss'
import 'styles/attendance.scss'
import 'styles/media-query.scss'
import 'styles/table.scss'
import 'styles/custom-modules.scss'
import 'styles/import.scss'
import 'styles/tabularReport.scss'
import 'styles/cardbuilder.scss'
import 'styles/catalog.scss'
import 'src/assets/styles/report.scss'
import 'src/assets/styles/dashboardWidget.scss'
import 'src/assets/styles/hottable-wrapper.scss'
import 'src/assets/styles/fc-bulk-form.scss'
import 'src/assets/styles/site.scss'
import 'src/assets/styles/Responsive.scss'
import 'styles/commissioning.scss'
import 'styles/login-invite.scss'
import 'styles/energy-analytics.scss'
import 'styles/quotation.scss'
import 'styles/etisalat.scss'
import 'styles/chart-shapes.scss'
import 'styles/login.scss'
import 'styles/pm.scss'
import 'styles/digest.scss'
import 'styles/form-customization.scss'
import 'styles/v1/setup.scss'
import 'styles/employeeportal.scss'
import 'src/pdf/style/pdf.scss'

import Vue from 'vue'
import App from 'App'
import Quasar from 'quasar'
import ElementUI from 'element-ui'
import locale from 'element-ui/lib/locale/lang/en'
import router from './router'
import i18n from './translations'
import store from './store'
import VueTippy from 'vue-tippy'
import titleMixin from './util/title'
import AutofocusMixin from './util/autofocus'
import PubSubMixin from './components/mixins/PubSubMixin'
import VueCookie from 'vue-cookie'
import VueScrollTo from 'vue-scrollto'
import VueQriously from 'vue-qriously'
import PortalVue from 'portal-vue'
import errorHandler from 'lib/error-handler'
import InlineSvg from '@/InlineSvg'
import * as GmapVue from 'gmap-vue'
import resize from 'vue-element-resize-detector'
import ReadMore from 'vue-read-more'
import VueGtag from 'vue-gtag'

// eslint-disable-next-line no-unused-vars
import IntersectionObserverPolyfill from 'intersection-observer'
//import is not unused, polyfil has a IIFE that adds intersection observer to window

import VueObserveVisibility from 'vue-observe-visibility'

import { setInstance } from '@facilio/api'
import http from 'util/http'
import loadWebTabs from './webtabs'
import isCookieEnabled from 'lib/check-cookie'
import { defineCustomElements as fcIcon } from '@facilio/icons/dist/loader'
fcIcon(window)

Vue.mixin(titleMixin)
Vue.mixin(AutofocusMixin)
Vue.mixin(PubSubMixin)
Vue.component('InlineSvg', InlineSvg)

Vue.use(VueQriously)
Vue.use(Quasar)
Vue.use(ElementUI, {
  locale,
})
Vue.use(VueTippy, {
  allowHTML: false,
  delay: [800, 0],
})
Vue.use(VueCookie)
Vue.use(VueScrollTo)
Vue.use(PortalVue)
Vue.use(ReadMore)
Vue.use(resize)
Vue.use(GmapVue, {
  load: {
    key: 'AIzaSyAJ9myxeE3qCem4Yx-ksyp0TVR7jvBAHFA',
    libraries: 'places,visualization',
    v: 3.42,
  },
  installComponents: true,
})
Vue.use(VueObserveVisibility)

Vue.config.productionTip = false
if (process.env.NODE_ENV === 'production') {
  Vue.config.errorHandler = errorHandler
}

setInstance(http)

loadWebTabs().then(lang => {
  i18n(lang).then(langConfig => {
    if (!isCookieEnabled()) {
      store.state.isCookiesDisabled = true
    }

    // Enable analytics

    if (window?.isGoogleAnalytics) {
      Vue.use(
        VueGtag,
        {
          config: { id: 'G-DRM4R5SBLQ' },
          bootstrap: false,
        },
        router
      )
    }

    new Vue({
      el: '#q-app',
      router,
      store,
      i18n: langConfig,
      render: h => h(App),
    })
  })
})
