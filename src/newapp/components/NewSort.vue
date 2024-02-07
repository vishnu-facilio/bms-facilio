<template>
  <el-popover
    v-model="visible"
    placement="bottom"
    trigger="click"
    popper-class="portal-sort-container"
    :visible-arrow="true"
  >
    <InlineSvg
      slot="reference"
      src="new-sortby"
      class="d-flex pointer user-select-none pR0"
      iconClass="icon self-center mR5"
    ></InlineSvg>

    <el-input
      type="text"
      v-model="search"
      :placeholder="$t('common._common.sort_search')"
    />
    <div class="sort-list-container">
      <div class="sort-field-list">
        <div
          v-for="(sortField, index) in filteredSortList"
          :key="index"
          @click.stop="updateOrder(sortField.name, 'orderBy')"
          :class="['list-item', orderBy === sortField.name && 'active']"
        >
          {{ sortField.displayName }}

          <i v-if="orderBy === sortField.name" class="el-icon-check"></i>
        </div>
      </div>
      <div class="seperator"></div>
      <div
        v-for="(type, name) in orderTypeList"
        :key="name"
        class="list-item"
        :class="[
          'list-item',
          orderType === name && orderBy !== null && 'active',
        ]"
        :disabled="orderBy === null"
        @click.stop="updateOrder(name, 'orderType')"
      >
        {{ type }}
      </div>
    </div>
  </el-popover>
</template>

<script>
import { isEmpty, isObject } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { API } from '@facilio/api'

const NONE_FIELD = {
  displayName: 'None',
  id: null,
  mainField: false,
  name: null,
}

export default {
  props: ['moduleName'],
  data() {
    return {
      visible: false,
      search: '',
      sortConfig: {
        orderBy: null,
        orderType: null,
      },
      sortFieldLists: [],
      orderTypeList: {
        asc: this.$t('common.wo_report.ascending'),
        desc: this.$t('common.wo_report.descending'),
      },
    }
  },
  created() {
    this.loadModuleMeta()
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
    }),
    orderBy() {
      let { orderBy } = this.sortConfig
      return isObject(orderBy) ? orderBy.value : orderBy
    },
    orderType() {
      let { orderType, orderBy } = this.sortConfig
      return !isEmpty(orderBy) && isEmpty(orderType) ? 'asc' : orderType
    },
    filteredSortList() {
      let { sortFieldLists, search } = this

      return sortFieldLists.filter(field => {
        let { displayName } = field || {}

        if (!isEmpty(search)) {
          return displayName.toLowerCase().includes(search.toLowerCase()) || ''
        }
        return true
      })
    },
    currentView() {
      return this.$route.params.viewname
    },
  },
  watch: {
    viewDetail: {
      handler() {
        let { sortFields = [] } = this.viewDetail || {}

        if (!isEmpty(sortFields)) {
          let { name } = this.$getProperty(sortFields[0], 'sortField', {})
          let { isAscending } = this.$getProperty(sortFields, '0', {})

          this.sortConfig = {
            orderType: isAscending ? 'asc' : 'desc',
            orderBy: name ? name : '',
          }
        } else {
          this.sortConfig = {
            orderBy: null,
            orderType: null,
          }
        }
      },
      immediate: true,
    },
  },
  methods: {
    async updateOrder(orderName, type) {
      this.$set(this.sortConfig, type, orderName)

      let { orderBy, orderType } = this
      orderType = !isEmpty(orderBy) ? orderType : null
      let sortObj = {
        orderBy,
        orderType,
      }
      this.$emit('onSortChange', sortObj)
      this.visible = false
    },

    async loadModuleMeta() {
      let { moduleName } = this
      let { data, error } = await API.get(`/v2/fields/sortable/${moduleName}`)

      if (!error) {
        let { fields } = data || {}
        if (!isEmpty(fields)) {
          this.sortFieldLists = [...fields, NONE_FIELD]
        }
      }
    },
  },
}
</script>
<style lang="scss">
.portal-sort-container {
  padding: 0px;
  cursor: pointer;

  .el-input__inner {
    padding: 20px 20px;
  }
  .sort-list-container {
    max-height: 400px;

    .sort-field-list {
      max-height: 200px;
      overflow: scroll;
    }

    .list-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 20px;

      &.active {
        font-weight: bold;
        color: #39b2c2;
      }
      .el-icon-check {
        font-weight: 700;
      }
    }
    .seperator {
      margin: 10px 0;
      border-top: 1px solid #f1f1f1;
    }
  }
}
</style>
