<template>
  <div class="height100">
    <router-view></router-view>
  </div>
</template>
<script>
import Helpers from 'util/helpers.js'
import Vue from 'vue'
import { isEmpty, isObject, isBoolean } from '@facilio/utils/validation'
import constants from 'util/constant'
import http from 'util/http'
import delve from 'dlv'
import dset from 'dset'
import FDialog from '@/FDialog'
import {
  isBooleanField,
  isNumberField,
  isDecimalField,
  isEnumField,
  isDateField,
  isDateTimeField,
  isDateTypeField,
  isLookupField,
  getDisplayValue,
  isLookupDropDownField,
  isSiteField,
  isLookupSimple,
  isChooserTypeField,
  isSiteLookup,
} from 'util/field-utils'
import { Alert } from 'quasar'
import * as filters from 'util/filters'
import { htmlToText } from '@facilio/utils/filters'
export default {
  created() {
    Vue.prototype.$http = http
    Vue.prototype.$helpers = Helpers
    Vue.prototype.$constants = constants
    Vue.prototype.$validation = {
      isEmpty,
      isObject,
      isBoolean,
    }
    Vue.prototype.$getProperty = delve
    Vue.prototype.$setProperty = dset
    Vue.prototype.$fieldUtils = {
      isBooleanField,
      isNumberField,
      isDecimalField,
      isEnumField,
      isDateField,
      isDateTimeField,
      isDateTypeField,
      isLookupField,
      getDisplayValue,
      isLookupDropDownField,
      isSiteField,
      isLookupSimple,
      isChooserTypeField,
      isSiteLookup,
    }
    Object.keys(filters).forEach(key => {
      Vue.filter(key, filters[key])
    })
    Vue.filter('htmlToText', htmlToText)

    // register global utility variables and functions
    Object.defineProperties(Vue.prototype, {
      $dialog: {
        get() {
          return {
            alert(args = {}) {
              return fdialog('alert', args)
            },
            confirm(args = {}) {
              return fdialog('confirm', args)
            },
            prompt(args = {}) {
              return fdialog('prompt', args)
            },
            notify(args = {}) {
              let alertObj = {
                color: 'positive',
                position: 'top-center',
              }
              let autoclose = 1500
              if (typeof args === 'string') {
                alertObj.html = args
              } else {
                alertObj.html = args.message
                if (args.color) {
                  alertObj.color = args.color
                }
                if (args.position) {
                  alertObj.position = args.position
                }
                if (args.autoclose) {
                  autoclose = args.autoclose
                }
              }
              let alertInstance = Alert.create(alertObj)
              if (autoclose > 0) {
                setTimeout(function() {
                  alertInstance.dismiss()
                }, autoclose)
              }
              return alertInstance
            },
          }
        },
      },
    })

    const CONSTRUCTOR = Vue.extend(FDialog)
    const CACHE = {}
    const mergedOptions = Object.assign(
      FDialog.OPTIONS_TEMPLATE
      // options.dialog ? options.dialog : {}
    )
    const fdialog = (mode, args) => {
      if (typeof args === 'string') {
        let title = args
        args = {
          title,
        }
      }
      args.mode = mode || 'alert'
      switch (mode) {
        case 'alert':
          args.lbHide = true
          break
        case 'confirm':
          break
        case 'prompt':
          break
        default:
          args.lbHide = true
          break
      }
      let dialog =
        CACHE[mergedOptions.id] || (CACHE[mergedOptions.id] = new CONSTRUCTOR())
      if (!dialog.$el) {
        let vm = dialog.$mount()
        document.querySelector(args.parent || 'body').appendChild(vm.$el)
      }
      return dialog.enqueue(args)
    }
  },
}
</script>
