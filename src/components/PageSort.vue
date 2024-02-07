<template>
  <div class="page-sort-popover p0">
    <outside-click :visibility.sync="visible">
      <el-popover
        v-model="visible"
        placement="left"
        class="sort-popover"
        ref="sortComponentPopover"
        popper-class="page-sort-popover"
        :visible-arrow="true"
      >
        <div link style="min-width: 180px" class="sort-popovernew">
          <el-input
            type="text"
            v-model="search"
            placeholder="Search..."
            class="tag-list-search"
          />
          <div
            v-for="(name, index) in sortList"
            :key="index"
            @click.stop="updateOrderBy(name), close()"
            class="position-relative sort-active-check"
          >
            <div
              v-if="
                name &&
                  viewDetail &&
                  viewDetail.fieldDisplayNames &&
                  viewDetail.fieldDisplayNames[name]
              "
            >
              <div v-if="selectedFields(name)">
                <div
                  v-if="
                    search === '' ||
                      viewDetail.fieldDisplayNames[name]
                        .toLowerCase()
                        .startsWith(search.toLowerCase())
                  "
                >
                  <div
                    :label="name"
                    :class="{
                      selected: orderBy === name,
                      'flex-middle': orderBy === name,
                    }"
                    class="fc-div-hover page-sort-list"
                  >
                    {{ viewDetail.fieldDisplayNames[name] }}
                  </div>
                </div>
              </div>
              <i :class="{ 'el-icon-check': orderBy === name }"></i>
            </div>
          </div>
          <div class="scr-sep"></div>
          <div
            class="q-item-bottom-p fc-div-hover page-sort-list"
            v-if="config.orderType"
            separator
            @click.stop="updateOrderType('asc'), close()"
          >
            <div :class="{ selected: orderType === 'asc' }">
              {{ $t('common._common.ascending') }}
            </div>
          </div>
          <div
            class="fc-div-hover page-sort-list"
            v-if="config.orderType"
            @click.stop="updateOrderType('desc'), close()"
          >
            <div :class="{ selected: orderType === 'desc' }">
              {{ $t('common._common.descending') }}
            </div>
          </div>
        </div>
      </el-popover>
    </outside-click>
  </div>
</template>

<script>
import OutsideClick from '@/OutsideClick'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
export default {
  props: ['config', 'sortList', 'excludeFields'],
  data() {
    return {
      visible: false,
      customLookupFieldsArray: [],
      search: '',
    }
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    orderBy() {
      return this.config.orderBy ? this.config.orderBy : null
    },
    orderType() {
      return this.config.orderType ? this.config.orderType : null
    },
    viewDetail() {
      return this.$store.state.view.currentViewDetail
    },
  },
  watch: {
    moduleMeta: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.toFilterCustomLookupFields()
      }
    },
  },
  components: {
    OutsideClick,
  },
  methods: {
    toFilterCustomLookupFields() {
      let { moduleMeta } = this
      if (!isEmpty(moduleMeta) && !isEmpty(moduleMeta.fields)) {
        moduleMeta.fields.forEach(field => {
          if (
            !field.default &&
            !isEmpty(field.displayType) &&
            field.displayType._name === 'LOOKUP_SIMPLE'
          ) {
            this.customLookupFieldsArray.push(field)
          }
        })
      }
    },
    open() {
      this.visible = true
    },
    close() {
      this.visible = false
    },
    selectedFields(label) {
      if (this.excludeFields || this.customLookupFieldsArray) {
        if (!isEmpty(this.excludeFields)) {
          let key1 = this.excludeFields.find(rt => rt === label)
          if (key1) {
            return false
          }
        }

        if (!isEmpty(this.customLookupFieldsArray)) {
          let customLookup = this.customLookupFieldsArray.find(
            rt => rt.name === label
          )
          if (customLookup) {
            return false
          }
        }
      }
      return true
    },
    updateOrderBy(orderBy) {
      this.config.orderBy = orderBy
      this.$emit('onchange', { orderBy: orderBy, orderType: this.orderType })
    },
    updateOrderType(orderType) {
      this.config.orderType = orderType
      this.$emit('onchange', { orderBy: this.orderBy, orderType: orderType })
    },
  },
}
</script>

<style scoped>
.sort-popover {
  margin-top: 8px;
}

.sort-popover .q-item-main.q-item-section {
  font-size: 13px;
}

.sort-popover .q-item-main.q-item-section.selected {
  font-weight: bold;
  color: #39b2c2;
}
.q-item-bottom-p {
  border-top: none !important;
}
.scr-sep {
  border-top: 1px solid #f1f1f1;
  margin-top: 10px;
  margin-bottom: 7px;
}
.sort-popover .el-icon-check {
  font-size: 14px;
  color: #39b2c2;
  font-weight: bold;
}
.sort-popover .el-icon-check:before {
  padding-right: 6px;
}
.page-sort-list {
  padding-top: 5px;
  padding-bottom: 5px;
  padding-left: 10px;
}
.selected {
  font-weight: bold;
  color: #39b2c2;
}
.sort-active-check .el-icon-check {
  position: absolute;
  right: 0;
  top: 14px;
}
.tag-list-search {
  width: 100%;
  border-top: none !important;
  border-left: none !important;
  border-right: none !important;
  border-radius: 0 !important;
  line-height: 25px !important;
}
.tag-list-search .el-input__inner {
  height: 40px;
  padding-left: 20px;
  padding-right: 20px;
  border-bottom: 1px solid #d0d9e2;
  border-top: none;
  border-left: none;
  border-right: none;
  margin-bottom: 0px;
}
.tag-list-body {
  padding-top: 10px;
  padding-bottom: 20px;
}
.sort-popovernew {
  padding: 0;
  max-height: 350px;
  overflow-y: scroll;
  overflow-x: hidden;
}
.sort-popovernew .page-sort-list {
  padding: 10px 20px !important;
}
</style>
<style>
.page-sort-popover {
  padding: 0;
}
.tag-list-search .el-input__inner {
  padding: 20px 20px;
}
</style>
