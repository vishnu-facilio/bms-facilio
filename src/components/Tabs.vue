<template>
  <div class="tab-section">
    <div class="sub-header-div" style="margin-left:22px;">
      <ul class="tabs">
        <router-link
          v-if="index < 5"
          tag="li"
          :to="item.path"
          v-for="(item, index) in accessibleMenu"
          :key="item.name"
        >
          <img class="tb-logo" :src="item.logo" />
          <a>{{ item.label }}</a>
        </router-link>
        <li v-if="accessibleMenu.length > 5" class="tb-picklist-downarrow">
          <span
            ><i
              aria-hidden="true"
              class="fa fa-ellipsis-h wl-icon-downarrow"
            ></i
          ></span>
          <q-popover ref="shpopover">
            <q-list link class="scroll" style="min-width: 150px">
              <q-item
                @click="$router.push(item.path), $refs.shpopover.close()"
                v-if="index > 4"
                v-for="(item, index) in accessibleMenu"
                :key="index"
              >
                <q-item-main :label="item.label" style="font-size: 13px;" />
              </q-item>
            </q-list>
          </q-popover>
        </li>
        <router-link
          tag="li"
          :to="{ query: { create: 'new' } }"
          append
          class="tb-add-button"
          style="padding:0;float:right"
          v-if="
            $hasPermission('workorder:CREATE') &&
              newbtn &&
              (type === 'workorder' || type === 'alarm') &&
              !$route.path.endsWith('/summary')
          "
        >
          <button class="tb-button tb-button-add button-add  shadow-12">
            <q-icon name="add" style="ffont-size: 20px;font-weight: 700;" />
          </button>
        </router-link>
        <router-link
          tag="li"
          :to="'new'"
          append
          class="tb-add-button"
          style="padding:0;float:right"
          v-else-if="newbtn && !$route.path.endsWith('/summary')"
        >
        </router-link>
      </ul>
    </div>
  </div>
</template>
<script>
import { QIcon, QPopover, QList, QItem, QItemMain } from 'quasar'

export default {
  props: ['menu', 'newbtn', 'type', 'parent'],
  components: {
    QIcon,
    QPopover,
    QList,
    QItem,
    QItemMain,
  },
  data() {
    return {
      accessibleMenu: this.getAccessibleMenu(),
    }
  },
  methods: {
    getAccessibleMenu() {
      let self = this
      let accessibleMenu = this.menu.filter(function(m) {
        if (typeof m.permission === 'undefined') {
          return m
        } else if (self.$hasPermission(m.permission)) {
          return m
        }
      })
      return accessibleMenu
    },
  },
  watch: {
    $route(to, from) {
      if (this.parent && to.path === this.parent) {
        this.$router.push(this.accessibleMenu[0].path)
      }
    },
    menu: function() {
      this.accessibleMenu = this.getAccessibleMenu()
    },
  },
  mounted() {
    if (this.parent && this.$route.path === this.parent) {
      this.$router.push(this.accessibleMenu[0].path)
    }
  },
}
</script>
<style>
.tb-picklist-downarrow {
  padding-left: 5px !important;
  padding-top: 16px !important;
  padding-right: 5px !important;
}
.tb-picklist-downarrow span {
  color: #adadad;
  font-size: 16px;
}
.tb-picklist-downarrow span i {
  position: initial !important;
}
.tab-section ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background-color: transparent;
  max-height: 50px;
}
.tab-section ul.active {
  background-color: #fff;
  border-bottom: 2px solid #fe64ac;
}
.tab-section li a {
  color: #25243e;
  font-size: 14px;
}
.tab-section li {
  float: left;
  display: block;
  color: #2f2e49;
  text-align: center;
  padding: 14px;
  padding-right: 20px;
  text-decoration: none;
}
.tab-section .tabs li:hover,
.tab-section .tabs a:hover {
  color: #000;
}
.tab-section .tabs li.active {
  background-color: #fff;
  border-bottom: 3px solid #fe64ac;
}
.tab-section .tabs li.focus {
  background-color: #c3d7e2;
}
.tab-section {
  background: #fff;
  font-size: 14px;
  z-index: 1000;
}
.tab-section li.router-link-exact-active.active a {
  font-weight: 500;
}
.tb-button {
  background-color: #fd4b92;
  border: 0;
  color: #fff;
  padding: 5px 9px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 10px 2px;
  cursor: pointer;
  outline: none;
}
.shadow-12 {
  box-shadow: -10px 4px 15px 2px rgba(255, 45, 129, 0.42),
    -2px 2px 17px 0px #7c2b57, 0 5px 22px 4px rgba(0, 0, 0, 0.12);
}
.tb-add-button {
  padding: 0;
  float: right;
  position: relative;
  right: 35px;
  outline: none;
}
.tb-button-add i.q-icon.material-icons:hover {
  font-weight: 900;
}
.tb-logo {
  height: 16px;
  margin-right: 2px;
  position: relative;
  top: 3px;
}
</style>
