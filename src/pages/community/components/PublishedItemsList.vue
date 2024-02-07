<template>
  <el-dialog
    :visible.sync="showDialogBox"
    width="70%"
    class="fc-dialog-center-container f-lookup-wizard scale-up-center fc-wizard-table-width100"
    :append-to-body="true"
    :show-close="false"
    :before-close="handleClose"
  >
    <template slot="title">
      <div class="header d-flex justify-between">
        <div class="el-dialog__title self-center">
          {{ sharingTypeName + ' List' }}
        </div>
        <div class="width50">
          <el-dropdown @command="action => handlecommand(action)" class="fL">
            <el-button
              class="height40px filter-key width100px fc-bordrer-btn-grey flex-middle justify-between"
            >
              {{ capitalizeFirstLetter(filterFieldValue) }}
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="(key, index) in fieldList"
                :key="index"
                :command="key"
                >{{ capitalizeFirstLetter(key) }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
          <div class="width70 fL">
            <el-input
              v-if="filterFieldValue == 'name'"
              type="text"
              v-model="searchQuery"
              clearable
              @clear="searchQuery = null"
              class="el-input-textbox-full-border width100 name-input"
              :min="5"
            >
            </el-input>
            <template v-for="(key, index) in filterList">
              <FLookupField
                v-if="filterFieldValue == key"
                :hideLookupIcon="true"
                :key="index"
                :model.sync="space"
                :fetchOptionsOnLoad="true"
                :field="fields[key]"
              ></FLookupField>
            </template>
          </div>
          <div class="mT10">
            <span class="separator self-center">|</span>
            <span
              class="close-btn self-center cursor-pointer"
              @click="handleClose"
            >
              <i class="el-dialog__close el-icon el-icon-close"></i>
            </span>
          </div>
        </div>
      </div>
    </template>
    <el-table
      :data="showSearchItems"
      :fit="true"
      height="500"
      class="fc-table-widget-scroll"
    >
      <template slot="empty">
        <div class="width100 justify-center">
          <img
            class="mT100"
            src="~statics/noData-light.png"
            width="100"
            height="100"
          />
          <div class="mT10 label-txt-black f14">
            {{ $t('common._common.no_units_available') }}
          </div>
        </div>
      </template>
      <template v-for="(key, index) in listItems">
        <el-table-column v-if="key == 'id'" :key="key" :label="key">
          <template v-slot="item">
            <div class="mL30 height30px fc-id">
              {{ '#' + item.row.id }}
            </div>
          </template>
        </el-table-column>

        <el-table-column v-else :key="index" :label="key">
          <template v-slot="item">
            <div v-if="key == 'area'" class="pointer mL30 height30px">
              {{ item.row.space.area | positive }}
              {{ $t('common.header.sqft') }}
            </div>
            <div
              v-else-if="key === 'peopleType'"
              class="pointer mL30 height30px"
            >
              {{ item.row.peopleTypeEnum }}
            </div>
            <div
              v-else-if="key === 'site' || key == 'building' || key == 'floor'"
              class="pointer mL30 height30px"
            >
              {{ getLookUpValue(item.row, key) || '---' }}
            </div>
            <div v-else class="pointer mL30 height30px">
              {{ item.row[key] || '---' }}
            </div>
          </template>
        </el-table-column>
      </template>
    </el-table>
  </el-dialog>
</template>

<script>
import SpaceMixin from '@/mixins/SpaceMixin'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
import { isEmpty } from '@facilio/utils/validation'
import FLookupField from '@/forms/FLookupField'
const fields = {
  building: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'building',
    field: {
      lookupModule: {
        name: 'building',
        displayName: 'Buildings',
      },
    },
    forceFetchAlways: true,
    filters: {},
    multiple: false,
    isDisabled: false,
  },
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },
    forceFetchAlways: true,
    filters: {},
    multiple: false,
    isDisabled: false,
  },
  floor: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'floor',
    field: {
      lookupModule: {
        name: 'floor',
        displayName: 'Floors',
      },
    },
    forceFetchAlways: true,
    filters: {},
    multiple: false,
    isDisabled: false,
  },
}
const sharingTypes = {
  TENANT_UNIT: 1,
  ROLE: 2,
  PEOPLE: 3,
  BUILDING: 4,
}
export default {
  props: ['details', 'sharingModuleName', 'showDialog'],
  mixins: [SpaceMixin],
  components: { FLookupField },
  data() {
    return {
      items: [],
      fields,
      showDialogBox: false,
      sharingTypeName: '---',
      sharingType: null,
      listItems: [],
      searchQuery: null,
      space: null,
      fieldList: [],
      filterFieldValue: 'name',
    }
  },
  created() {
    this.initial(), (this.showDialogBox = this.showDialog)
  },
  computed: {
    filterList() {
      return ['site', 'building', 'floor']
    },
    showSearchItems() {
      let { searchQuery, items, space, filterFieldValue } = this

      if (searchQuery) {
        return (items || []).filter(item => {
          let { name } = item || {}
          let searchQueryUpperCase = searchQuery.toUpperCase()

          return (name || '').toUpperCase().includes(searchQueryUpperCase)
        })
      } else if (space) {
        return (items || []).filter(item => {
          let { id } = (item || {})[filterFieldValue] || {}
          return id == space
        })
      }
      return items || []
    },
  },
  methods: {
    handlecommand(action) {
      this.space = null
      this.searchQuery = null
      this.filterFieldValue = action
    },
    initial() {
      let { details, $getProperty, sharingModuleName } = this
      let publishedTo = $getProperty(details, `${sharingModuleName}`) || []
      if (!isEmpty(publishedTo)) {
        publishedTo.forEach(item => {
          if (item.sharingType == sharingTypes.TENANT_UNIT) {
            this.items.push(item['sharedToSpace'])
            this.sharingTypeName = 'Tenant Unit'
            this.listItems = ['id', 'name', 'site', 'building', 'floor']
            this.sharingType = sharingTypes.TENANT_UNIT
          } else if (item.sharingType == sharingTypes.ROLE) {
            this.items.push(item['sharedToRole'])
            this.sharingTypeName = 'Role'
            this.listItems = ['name', 'description']
            this.sharingType = sharingTypes.ROLE
          } else if (item.sharingType == sharingTypes.PEOPLE) {
            this.items.push(item['sharedToPeople'])
            this.sharingTypeName = 'People'
            this.listItems = ['id', 'name', 'peopleType', 'email', 'phone']
            this.sharingType = sharingTypes.PEOPLE
          } else if (item.sharingType == sharingTypes.BUILDING) {
            this.items.push(item['sharedToSpace'])
            this.sharingTypeName = 'Building'
            this.listItems = ['id', 'name', 'site']
            this.sharingType = sharingTypes.BUILDING
          }
        })
      }
      this.setFieldList()
    },
    setFieldList() {
      let { sharingType } = this
      switch (sharingType) {
        case sharingTypes.TENANT_UNIT:
          this.fieldList = ['name', 'site', 'building', 'floor']
          break
        case sharingTypes.ROLE:
          this.fieldList = ['name']
          break
        case sharingTypes.PEOPLE:
          this.fieldList = ['name']
          break
        case sharingTypes.BUILDING:
          this.fieldList = ['name', 'site']
          break
      }
    },
    handleClose() {
      this.showDialogBox = false
      eventBus.$emit('listDialogClose')
    },
    getLookUpValue(value, key) {
      if (!isEmpty(value) && !isEmpty(value[key])) {
        return value[key].name
      }
      return '---'
    },
    capitalizeFirstLetter(string) {
      return string.charAt(0).toUpperCase() + string.slice(1)
    },
  },
}
</script>
<style scoped>
.height40px {
  height: 40px;
}
.height30px {
  height: 30px;
}
.filter-key {
  background: rgba(234, 236, 238, 0.5) !important;
  color: rgb(107, 107, 107);
  font-weight: 400;
  border: 1px solid #d0d9e2 !important;
  font-size: 14px;
}
.close-icon {
  display: none;
  color: rgba(133, 153, 172, 1);
}

.name-input:hover .close-icon {
  display: block;
}
.name-input {
  border-radius: 100px;
}
</style>
