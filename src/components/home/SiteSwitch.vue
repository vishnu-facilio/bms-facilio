<template>
  <el-dropdown
    v-if="hasSites"
    @command="setCurrentSite"
    @click="setFocus()"
    trigger="click"
    class="siteDropdown"
    :tabindex="-1"
  >
    <div class="siteDropdownBtn">
      <div class="header__current__site__switch">
        <div
          class="header__current__site__display new-home-display p0 flex-middle"
        >
          <span
            v-tippy
            :title="$t('setup.decommission.decommissioned')"
            v-if="getCurrentSiteOptionIsDecommission(currentSiteObj)"
            class="pR5 pT4"
          >
            <fc-icon group="alert" name="decommissioning" size="15"></fc-icon>
          </span>
          <span v-else>
            <img
              src="~assets/building-white.svg"
              class="mR5 mT5"
              width="13"
              height="13"
              :alt="$t('common.products.sites')"
            />
          </span>
          <span>{{ currentSiteObj.label }}</span>
        </div>
        <i
          :class="{
            'arrow-push-bottom': getCurrentSiteOptionIsDecommission(
              currentSiteObj
            ),
          }"
          class="fa fa-sort-desc wl-icon-downarrow"
        ></i>
      </div>
    </div>
    <el-dropdown-menu slot="dropdown" class="p5">
      <el-input
        @input="getSitesList()"
        prefix-icon="el-icon-search"
        class="site_switch_search_box  p5"
        v-model="searchText"
        ref="site-switch-search-box"
        :placeholder="$t('common.header.search_by_site_name')"
      ></el-input>
      <el-dropdown-item
        class="dropdown-item pT20"
        v-if="currentSiteObj.value !== -1"
      >
        {{ $t('maintenance.wr_list.all') }}
        <span v-if="currentSiteObj.value === -1" class="success-icon pL5"
          ><i class="el-icon-success site_switch"></i
        ></span>
      </el-dropdown-item>
      <template v-for="(site, key) in sites">
        <el-dropdown-item
          :command="site"
          :key="key"
          class="dropdown-item"
          v-if="!$validation.isEmpty(sites)"
        >
          <span>{{ site.label }}</span>
          <span v-if="isCurrentSite(site)" class="success-icon pL5"
            ><i class="el-icon-success site_switch"></i
          ></span>
          <span
            v-if="getCurrentSiteOptionIsDecommission(site)"
            v-tippy
            :title="$t('setup.decommission.decommissioned')"
            class="success-icon pL5"
            ><fc-icon
              group="alert"
              class="fR pT10"
              name="decommissioning"
              size="16"
            ></fc-icon
          ></span>
        </el-dropdown-item>
      </template>
    </el-dropdown-menu>
  </el-dropdown>
</template>
<script>
import { getFieldOptions } from 'util/picklist'
import { isEmpty } from '@facilio/utils/validation'
import debounce from 'lodash/debounce'
import { API } from '@facilio/api'

export default {
  data() {
    return {
      searchText: '',
      sites: [],
      hasSites: false,
      currentSiteObj: {
        value: -1,
        label: 'All',
        fourthLabel: false,
      },
    }
  },
  async created() {
    let currentSiteId = Number(this.$cookie.get('fc.currentSite')) || -1
    await this.getSitePickList(currentSiteId)
  },
  methods: {
    getCurrentSiteOptionIsDecommission(currentSiteObj) {
      if (!isEmpty(currentSiteObj)) {
        let { fourthLabel } = currentSiteObj || {}
        return !isEmpty(fourthLabel) ? JSON.parse(fourthLabel) : false
      }
      return false
    },
    setFocus() {
      this.setAutofocus('site-switch-search-box')
    },
    isCurrentSite(site) {
      let { currentSiteObj } = this
      let { value } = currentSiteObj || {}
      let { value: currentSiteId } = site || {}
      return value === currentSiteId
    },
    setCurrentSite(site) {
      if (!this.isCurrentSite(site)) {
        this.$cookie.set(
          'fc.currentSite',
          this.$getProperty(site, 'value', null),
          {
            expires: '10Y',
            path: '/',
          }
        )
        this.$router.go()
      }
    },
    getSitesList: debounce(async function() {
      await this.getSitePickList()
    }, 500),
    async getSitePickList(currentSiteId) {
      let { searchText } = this
      let params = {
        field: {
          lookupModuleName: 'site',
          additionalParams: {
            isToFetchDecommissionedResource: true,
          },
        },
        apiOptions: {
          removeCurrentSiteFilter: true,
        },
      }
      if (!isEmpty(searchText)) {
        params.searchText = searchText
      }
      let { error, options } = await getFieldOptions(params)
      if (!error) {
        if (options.length >= 1) {
          this.hasSites = true
        }
        this.sites = options
      }
      if (currentSiteId > 0) {
        this.currentSiteObj = options.find(site => site.value === currentSiteId)
      }
    },
  },
}
</script>
<style scoped>
.arrow-push-bottom {
  bottom: 4px !important;
}
.success-icon {
  float: right;
  color: #67c23a;
}
.siteDropdownBtn {
  background-color: transparent;
  padding: 0;
  border: none;
}
.siteDropdownBtn:hover,
.siteDropdownBtn:focus {
  color: #409eff;
}

.site_switch_search_box:focus {
  border: 1px solid #39b2c2 !important;
}
.site_switch_search_box {
  border: 1px solid #d8dce5;
}
.mL1 {
  margin-left: 1px;
}
</style>
<style>
.site_switch_search_box .el-input__inner {
  border-bottom: none !important;
  padding-left: 25px;
}
i.site_switch.el-icon-success {
  margin-right: 0;
}
li.el-dropdown-menu__item.dropdown-item {
  padding: 0 10px;
}
</style>
