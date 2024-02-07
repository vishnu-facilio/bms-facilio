<template>
  <div class="permalink-container">
    <main class="d-flex flex-direction-column">
      <router-view :token="token"></router-view>
    </main>
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
  },
  computed: {
    token() {
      return this.$route.query.token
    },
  },
}
</script>
<style lang="scss">
html,
body,
#q-app,
.permalink-container {
  height: 100%;
}
main {
  padding: 0;
  min-height: 100%;
}
</style>

<style lang="scss" scoped>
.facilio-logo {
  height: 23px;
  padding: 0;
  position: relative;
  width: 60px;
  height: 25px;
  margin-right: 5px;
}
.footer-power {
  display: flex;
  align-items: center;
  position: fixed;
  right: 0;
  bottom: 5px;
  font-size: 12px;
  color: #909090;
  margin-right: 5px;
}
</style>
