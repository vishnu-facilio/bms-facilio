import '@testing-library/jest-dom'
import Vue from 'vue'
import ElementUI from 'element-ui'
import VueTippy from 'vue-tippy'
import * as Filters from 'src/util/filters'
import { isEmpty, isObject, isBoolean } from '@facilio/utils/validation'
import InlineSvg from 'src/components/InlineSvg'
import PortalVue from 'portal-vue'
import delve from 'dlv'

const initTestVue = () => {
  Vue.use(ElementUI, {
    locale: {},
  })

  Vue.component('InlineSvg', InlineSvg)
  Vue.use(PortalVue)

  Vue.use(VueTippy, {
    directive: 'tippy',
    component: 'tippy',
  })
  const prototype = {
    $dateformat: 'DD/MM/YYYY',
    $timeformat: 'hh:mm A',
    $timezone: 'Asia/Calcutta',
    $validation: {
      isEmpty,
      isObject,
      isBoolean,
    },
    $getProperty: delve,
  }
  Object.assign(Vue.prototype, { ...prototype })
  for (const [key, value] of Object.entries(Filters)) {
    Vue.filter(key, value)
  }
}

initTestVue()
