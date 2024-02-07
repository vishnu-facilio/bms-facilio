<template>
  <div
    class="subheader-section"
    v-bind:class="{ filterclass: querry === true }"
  >
    <div class="subheader-div">
      <ul class="subheader-tabs pull-left">
        <slot name="prefix"></slot>
        <template v-for="(item, index) in activeMenuList">
          <li
            v-if="item.multilevel"
            :key="index"
            :class="{
              'router-link-exact-active active':
                getTabTitlenew(item) !== item.label ||
                ($route.params &&
                  $route.params.dashboardlink === 'chilleroverview'),
            }"
            class="sh-multilevel-header"
          >
            <el-popover
              placement="bottom"
              width="200"
              v-model="closepop"
              popper-class="sh-popup-tree"
              trigger="hover"
            >
              <div
                v-for="(d, index) in item.children"
                :key="index"
                class="p5 parent-sh"
                v-if="item.children"
              >
                <div
                  class="sh-pop-parent p5 pointer"
                  @click="getparent(d)"
                  v-bind:class="{ active: d.path.path === $route.path }"
                >
                  {{ d.label }}
                </div>
                <div v-if="d.children" class="p5 sh-con-header">
                  <div
                    v-for="(c, idx) in d.children"
                    :key="idx"
                    class="child-sh"
                    v-if="d.children"
                  >
                    <div
                      @click="getchild(c)"
                      class="sh-pop-child p5 pointer"
                      v-bind:class="{ active: c.path.path === $route.path }"
                    >
                      <i
                        class="fa fa-circle sh-indicater"
                        v-bind:class="{ active: c.path.path === $route.path }"
                      ></i
                      >{{ c.label }}
                    </div>
                  </div>
                </div>
              </div>
              <a slot="reference">
                {{ getTabTitlenew(item) }}
                <i
                  class="switch-icon-dropdown el-icon-arrow-down"
                  style="float:right;margin:5px;"
                ></i>
              </a>
            </el-popover>
            <div class="sh-selection-bar"></div>
          </li>
          <li
            v-else-if="item.disable"
            :key="index"
            :class="{
              'router-link-exact-active active':
                getTabTitle(item) !== item.label,
            }"
          >
            <el-dropdown @command="openChild">
              <a>
                {{ getTabTitle(item) }}
                <i
                  class="switch-icon-dropdown el-icon-arrow-down"
                  style="float:right;margin:5px;"
                ></i>
              </a>
              <div class="sh-selection-bar"></div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(child, childindex) in item.childrens"
                  :command="{ curentItem: child, parentItem: item }"
                  :key="childindex"
                  :disabled="child.disable"
                  >{{ child.label }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </li>
          <router-link
            v-else-if="
              item.childrens ||
                !showCurrentViewOnly ||
                currentView === item.name
            "
            :key="index"
            tag="li"
            :to="
              item.childrens &&
              item.childrens.find(cl => cl.path.path === $route.path)
                ? item.childrens.find(cl => cl.path.path === $route.path).path
                : item.path
            "
          >
            <a>{{ getTabTitle(item) }}</a>
            <div v-if="item.childrens" style="float:right;margin:3px;">
              <el-dropdown @command="openChild">
                <i class="switch-icon-dropdown el-icon-arrow-down"></i>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    v-for="(child, childindex) in item.childrens"
                    :command="{ curentItem: child, parentItem: item }"
                    :key="childindex"
                    :disabled="child.disable"
                    >{{ child.label }}</el-dropdown-item
                  >
                </el-dropdown-menu>
              </el-dropdown>
            </div>
            <div class="sh-selection-bar" v-if="!item.disable"></div>
          </router-link>
        </template>
        <li
          v-if="moreMenuList.length && !showCurrentViewOnly"
          class="pointer"
          v-bind:class="{ active: isMoreMenuActive }"
        >
          <div>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink"
              version="1.1"
              id="Capa_1"
              x="0px"
              y="0px"
              width="18px"
              height="18px"
              viewBox="0 0 408 408"
              style="enable-background:new 0 0 408 408;"
              xml:space="preserve"
            >
              <g>
                <g id="more-horiz">
                  <path
                    d="M51,153c-28.05,0-51,22.95-51,51s22.95,51,51,51s51-22.95,51-51S79.05,153,51,153z M357,153c-28.05,0-51,22.95-51,51    s22.95,51,51,51s51-22.95,51-51S385.05,153,357,153z M204,153c-28.05,0-51,22.95-51,51s22.95,51,51,51s51-22.95,51-51    S232.05,153,204,153z"
                    fill="#b1bdc9"
                  ></path>
                </g>
              </g>
            </svg>
          </div>
          <q-popover
            ref="shpopover"
            @open="moreMenuOpened"
            @close="moreMenuClosed"
          >
            <q-list link class="scroll q-list-item" style="min-width: 150px">
              <q-item
                @click="openSubTab(item, index)"
                v-for="(item, index) in moreMenuList"
                :key="index"
              >
                <q-item-main :label="item.label" style="font-size: 13px;" />
              </q-item>
              <q-item
                v-if="showRearrange"
                @click="rearrange"
                :menu="menu"
                class="rearrange-button"
              >
                <q-item-main
                  :label="$t('setup.users_management.rearrange')"
                  style="font-size: 13px;"
                ></q-item-main>
              </q-item>
            </q-list>
          </q-popover>
        </li>
      </ul>
      <slot name="suffix"></slot>
      <div v-bind:class="positionsheaders">
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<script>
import { QPopover, QList, QItem, QItemMain } from 'quasar'

export default {
  computed: {
    currentView() {
      return this.$route.params.viewname
    },
    querry() {
      if (this.$route.query.filters === '') {
        return true
      } else if (this.$route.query.filters) {
        return true
      } else {
        return false
      }
    },
  },
  props: [
    'menu',
    'filtersRetain',
    'newbtn',
    'type',
    'parent',
    'positionsheader',
    'showRearrange',
    'listCount',
    'showCurrentViewOnly',
    'maxVisibleMenu',
  ],
  components: {
    QPopover,
    QList,
    QItem,
    QItemMain,
  },
  data() {
    return {
      positionsheaders: 'pull-right',
      closepop: false,
      activeMenuList: [],
      moreMenuList: [],
      seletedvalue: null,
      isMoreMenuActive: false,
    }
  },
  watch: {
    $route(to, from) {
      if (this.filtersRetain && !(from.path === to.path)) {
        for (let i = 0; i < this.filtersRetain.length; i++) {
          let key1 = from.query.hasOwnProperty(this.filtersRetain[i])
          if (key1) {
            this.$router.push({ query: from.query })
          }
        }
      }
      this.setFirstMenu()
      this.$emit('tabChange')
    },
    menu: function(newVal) {
      this.multilevelheader()
    },
  },
  mounted() {
    this.multilevelheader()
  },
  methods: {
    getTabTitle(item) {
      if (item.path.path !== this.$route.path) {
        if (item.childrens && item.childrens.length) {
          let child = item.childrens.find(
            it => it.path.path === this.$route.path
          )
          if (child) {
            return child.label
          }
        }
      }
      return item.label
    },
    getTabTitlenew(item) {
      if (item.path.path !== this.$route.path) {
        if (item.children && item.children.length) {
          let child = item.children.find(
            it => it.path.path === this.$route.path
          )
          let ch = item.children.filter(it => it.children)
          if (child) {
            return child.label
          } else {
            for (let i = 0; i < ch.length; i++) {
              let c =
                ch[i].children &&
                ch[i].children.find(rt => rt.path.path === this.$route.path)
              if (c) {
                return c.label
              }
            }
          }
        }
      }
      return item.label
    },
    getparent(data) {
      if (data.path && data.path.path) {
        this.$router.push({ path: data.path.path })
        this.closepop = false
      }
    },
    getchild(data) {
      if (data.path && data.path.path) {
        this.$router.push({ path: data.path.path })
        this.closepop = false
      }
    },
    selectheader(value, event) {
      this.seletedvalue = value
      this.$router.push(value[value.length - 1])
    },
    multilevelheader() {
      this.seletedvalue = ['/app/em/newdashboard/chilleroverview/391']
      this.loadMenuList()
    },
    multiheaderClickEvent() {},
    callbackMethod(newVal) {},
    handleFcAfterDateBack(data, event) {},
    onClickChild(value) {},
    openChild(obj) {
      this.$router.push(obj.curentItem.path)
    },
    companyListSelected: function(label, id) {},
    loadMenuList() {
      let self = this
      self.positionsheaders = self.positionsheader || 'pull-right'
      self.activeMenuList = []
      self.moreMenuList = []
      let accessibleMenu = this.menu.filter(function(m) {
        if (typeof m.permission === 'undefined') {
          return m
        } else if (self.$hasPermission(m.permission)) {
          return m
        }
      })
      for (let idx in accessibleMenu) {
        if (parseInt(idx) < (this.maxVisibleMenu || 4)) {
          this.activeMenuList.push(accessibleMenu[idx])
        } else {
          this.moreMenuList.push(accessibleMenu[idx])
        }
      }
      this.setFirstMenu()
    },
    openSubTab(item, index, keepQuery) {
      if (item && item.disable && item.childrens.length) {
        let data = []
        data = item.childrens.filter(rt => rt.label !== item.label)
        if (data.length) {
          this.moreMenuList.splice(index, 1)
          let lastMenu = this.activeMenuList.pop()
          this.moreMenuList.splice(0, 0, lastMenu)
          this.activeMenuList.push(item)
          this.$router.push({ path: data[0].path.path })
          if (this.isMoreMenuActive) {
            this.$refs.shpopover.close()
          }
        }
      } else {
        this.moreMenuList.splice(index, 1)
        let lastMenu = this.activeMenuList.pop()
        this.moreMenuList.splice(0, 0, lastMenu)
        this.activeMenuList.push(item)

        if (keepQuery) {
          this.$router.replace({
            path: item.path.path,
            query: this.$route.query,
          })
        } else {
          this.$router.push(item.path)
        }
        if (this.isMoreMenuActive) {
          this.$refs.shpopover.close()
        }
      }
    },
    setFirstMenu() {
      if (
        this.parent &&
        (this.$route.path === this.parent ||
          this.$route.path + '/' === this.parent) &&
        this.activeMenuList.length
      ) {
        let primaryView = this.activeMenuList.find(menu => menu.primary)
        if (!primaryView) {
          primaryView = this.activeMenuList[0]
        }
        this.$router.replace({
          path: primaryView.path.path,
          query: this.$route.query,
          params: primaryView.path.params,
        })
      } else {
        this.checkAndSetMoreList(this.currentView)
      }
      if (
        ![...this.activeMenuList, ...this.moreMenuList].find(
          menu => menu.path.path === this.$route.path
        )
      ) {
        let portfolio = [
          ...this.activeMenuList,
          ...this.moreMenuList,
        ].find(menu => menu.path.path.endsWith('portfolio'))
        if (portfolio && portfolio?.childrens) {
          let currentBuilding = portfolio.childrens.find(
            menu => menu.path.path === this.$route.path
          )
          if (currentBuilding) {
            currentBuilding.childrens = portfolio.childrens
            if (
              this.activeMenuList.find(menu =>
                menu.path.path.endsWith('portfolio')
              )
            ) {
              this.activeMenuList = this.activeMenuList.map(ele => {
                if (ele.path.path === portfolio.path.path) {
                  return currentBuilding
                } else {
                  return ele
                }
              })
            } else {
              this.moreMenuList = this.moreMenuList.map(ele => {
                if (ele.path.path === portfolio.path.path) {
                  return currentBuilding
                } else {
                  return ele
                }
              })
            }
          }
        } else {
          if (this.menu.length && this.$route.path === '/app/em/newdashboard') {
            if (
              this.menu[0].disable &&
              this.menu[0].childrens &&
              this.menu[0].childrens.length > 1 &&
              this.menu[0].childrens[0].disable
            ) {
              this.$router.push(this.menu[0].childrens[1].path.path)
            } else {
              this.$router.push(this.menu[0].path.path)
            }
          }
        }
      }
    },
    moreMenuOpened() {
      this.isMoreMenuActive = true
    },
    moreMenuClosed() {
      this.isMoreMenuActive = false
    },
    checkAndSetMoreList(newView) {
      if (newView) {
        let index
        let menu = this.moreMenuList.find(function(menu, idx) {
          if (menu.name === newView) {
            index = idx
            return true
          }
        })
        if (menu) {
          this.openSubTab(menu, index, true)
        }
      }
    },
    rearrange() {
      this.$refs.shpopover.close()
      this.$emit('rearrange')
    },
  },
}
</script>
<style>
.filterclass {
  box-shadow: none !important;
  border-bottom: 1px solid #ededed;
}
.subheader-section {
  height: 56px;
  position: relative;
  font-size: 14px;
  z-index: 100;
  box-sizing: border-box;
  padding-top: 19px;
  padding-left: 15px;
  padding-bottom: 18px;
  padding-right: 8px;
  border-bottom: 1px solid rgb(0 0 0 / 10%);
}
.subheader-section ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background-color: transparent;
}
ul.subheader-tabs li {
  float: left;
  padding: 0px 15px;
}
ul.subheader-tabs li.active a {
  font-weight: 500;
}
ul.subheader-tabs li.active .sh-selection-bar {
  border-right: 0px solid #e0e0e0;
  border-left: 0px solid #e0e0e0;
  border-color: var(--fc-theme-color);
}
ul.subheader-tabs li .sh-selection-bar {
  border: 1px solid transparent;
  width: 25px;
  margin-top: 7px;
  position: absolute;
}
ul.subheader-tabs li.active .sh-subheader-count {
  display: contents;
}
ul.subheader-tabs li .sh-subheader-count {
  border: 1px solid transparent;
  width: 25px;
  margin-top: 5px;
  display: none;
  position: absolute;
}
ul.subheader-tabs li:last-child {
  border-right: 0px;
}
.sub-header-section ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background-color: transparent;
  max-height: 50px;
}
.sub-header-section ul.active {
  background-color: #f8f9fd;
}
.sub-header-section li a {
  color: #90909a;
  font-size: 13px;
}
.sub-header-section li.active a {
  color: #fff;
  font-weight: 500;
}
.el-badge__content {
  letter-spacing: 0.1px;
  font-size: 10px;
  font-weight: 600;
}
.sub-header-section li {
  float: left;
  display: block;
  color: #2f2e49;
  text-align: center;
  padding: 17px;
  text-decoration: none;
}
.sub-header-section .sub-header-tabs li:hover,
.sub-header-section .sub-header-tabs a:hover {
  color: #fff;
  background-color: #25243e;
}
.sub-header-section .sub-header-tabs li:hover #more-horiz path {
  fill: #fff;
}
.sub-header-section .sub-header-tabs li.active #more-horiz path {
  fill: #fff;
}
.sub-header-section .sub-header-tabs li.active {
  background-color: #25243e;
}
.sub-header-section .sub-header-tabs li.focus {
  background-color: #c3d7e2;
}
.sub-header-section {
  border-bottom: 1px solid #d5dbed;
  background: #2f2e49;
  font-size: 14px;
  width: 100%;
  z-index: 1000;
}
.sub-header-section li.router-link-exact-active.active a {
  color: #fff;
  font-weight: 500;
}
.sub-header-section .sub-header-tabs li .sh-selection-bar {
  border: 1px solid transparent;
  width: 25px;
  margin-top: 5px;
  position: absolute;
}
.sub-header-section .sub-header-tabs li.active .sh-selection-bar {
  border-right: 0px solid #e0e0e0;
  border-left: 0px solid #e0e0e0;
  border-color: #5bbaa7;
}
.sub-header-section .sub-header-tabs li .sh-subheader-count {
  border: 1px solid transparent;
  width: 25px;
  margin-top: 5px;
  position: absolute;
}
.sub-header-section .sub-header-tabs li.active .sh-subheader-count {
  border-right: 0px solid #e0e0e0;
  border-left: 0px solid #e0e0e0;
  border-color: #5bbaa7;
}
.shadow-12 {
  box-shadow: -4px 4px 8px rgba(250, 75, 146, 0.2);
}
.sub-header-section .sub-header-tabs .q-chip {
  height: 10px !important;
  padding: 0 10px;
  min-height: 0;
  font-size: 12px;
  font-weight: 900;
  border: #e0e0e0;
  border-radius: 2rem;
  cursor: default;
  position: relative;
  left: 6px;
  bottom: 8px;
  white-space: nowrap;
  vertical-align: super;
  background-color: #ff2d81 !important;
  padding-left: 7px;
  padding-right: 7px;
}
.sh-button-add i.q-icon.material-icons:hover {
  font-weight: 900;
}
.subheader-section .disable:disabled {
  opacity: 0.7;
}
.sh-subheader-count .el-badge__content {
  line-height: 15px !important;
}
.sh-multilevel-header .el-cascader {
  line-height: normal !important;
}
.sh-multilevel-header .el-input .el-input__inner,
.sh-multilevel-header .el-textarea .el-textarea__inner {
  border-bottom: 0 !important;
  visibility: hidden !important;
}
.sh-multilevel-header .el-input__prefix,
.sh-multilevel-header .el-input__suffix {
  top: -10px;
}
.sh-multilevel-header .is-opened .el-input__prefix,
.sh-multilevel-header .is-opened .el-input__suffix {
  top: 0px;
}
.sh-multilevel-header .el-cascader__label {
  padding: 0px !important;
}
.sh-pop-parent {
  font-weight: 500;
  letter-spacing: 0.5px;
}
.sh-pop-child.p5.pointer {
  font-size: 13px;
  letter-spacing: 0.5px;
  white-space: nowrap;
}
.sh-popup-tree {
  padding: 0px !important;
  max-height: 90vh;
  overflow: scroll;
}
.sh-pop-parent.p5.pointer:hover,
.child-sh:hover {
  background: #b9f3f1;
}
.child-sh {
  padding-top: 5px;
}
.sh-con-header {
  padding-top: 0px;
  padding-bottom: 0px;
}
.sh-pop-parent.active {
  background: #b9f3f1;
}
.sh-pop-child.active {
  background: transparent;
}
.sh-pop-child.active {
  color: #333;
  font-weight: 500;
}
.sh-indicater {
  font-size: 7px;
  padding: 0px;
  margin: auto;
  position: relative;
  bottom: 2px;
  right: 2px;
  padding-right: 5px;
}
.sh-indicater.active {
  /*color: #ee518f;*/
  color: var(--fc-theme-color);
}
.rearrange-button {
  text-align: center;
  background: #39b2c2;
  font-size: 14px;
  color: #fff;
  font-weight: 600;
  margin-top: 10px;
}
.rearrange-button:hover {
  background: #33a6b5 !important;
  color: #fff;
}
.q-list-item {
  padding-bottom: 0 !important;
}
</style>
