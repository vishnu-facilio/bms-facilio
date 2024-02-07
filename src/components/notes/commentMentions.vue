<template>
  <div class="mention-popover">
    <div v-if="isLoading" class="mention-list-spinner">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <el-scrollbar v-else-if="isPeopleSelection" class="fc-suggestions-picker">
      <div
        v-for="(people, index) in recordsList"
        :key="index"
        :class="[
          'el-dropdown-menu__item fc-mention-item wo-assigned-avatar',
          selectedIndex === index && 'selected-Mention-item',
        ]"
        @click="selectItem(index)"
      >
        <el-tooltip
          placement="top-end"
          offset="-30"
          popper-class="max-width300px z-index9999 f13"
          :open-delay="500"
          :disabled="!people.fields"
        >
          <div v-if="people.fields" slot="content">
            <div v-for="field in people.fields" :key="`tooltip~${field}`">
              {{ field }} <br />
            </div>
          </div>
          <div class="pL3 pR6 d-flex align-center">
            <tenant-avatar
              :name="false"
              class="pT6 pR3"
              size="md"
              :tenant="people"
            ></tenant-avatar>
            <div
              class="fc__layout__has__row justify-around leading-none height80"
            >
              <span class="mention-title">
                {{ people.name || '---' }}
              </span>

              <span v-if="people.fields" class="mention-subtitle">
                {{ people.subtitle }}
              </span>
            </div>
          </div>
        </el-tooltip>
      </div>
    </el-scrollbar>
    <el-scrollbar v-else class="fc-suggestions-picker">
      <div
        v-for="(item, index) in recordsList"
        :key="`recordsList-${index}`"
        :class="[
          'el-dropdown-menu__item fc-mention-item',
          selectedIndex === index && 'selected-Mention-item',
        ]"
        @mouseover="selectedIndex = index"
        @click="selectItem(index)"
      >
        <!-- need to check this z-index issue -->
        <el-tooltip
          placement="top-end"
          offset="-30"
          popper-class="max-width300px z-index9999 f13"
          :open-delay="1500"
          :content="item.name"
          :disabled="!item.name"
        >
          <div class="font-normal record-mention-content">
            <span class="text-fc-blue fc-italic"> {{ `${item.id}` }}</span>
            <span class="break-word"> {{ ` - ${item.name}` }}</span>
          </div>
        </el-tooltip>
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
import { getFieldOptions } from 'util/picklist'
import debounce from 'lodash/debounce'
import TenantAvatar from '@/avatar/Tenant'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'
import { getApp } from '@facilio/router'
import { API } from '@facilio/api'

export default {
  components: {
    Spinner,
    TenantAvatar,
  },
  props: {
    editor: {
      type: Object,
      required: false,
    },

    items: {
      type: Object,
      required: true,
    },
    query: {
      type: String,
      required: false,
    },

    command: {
      type: Function,
      required: true,
    },
  },

  data() {
    return {
      selectedIndex: 0,
      visible: true,
      isLoading: false,
      recordsList: [],
      clientContact: null,
    }
  },
  async created() {
    await this.init()
    this.loadRecords()
  },

  watch: {
    query() {
      this.searchRecords()
    },
  },
  computed: {
    moduleName() {
      // if (this.isPeopleSelection && this.getModuleNameFromPortal !== null) {
      //   return this.getModuleNameFromPortal
      // }
      return this.items.moduleName
    },
    isPeopleSelection() {
      return this.items.moduleName === 'people'
    },
    getAPPlinkname() {
      return getApp().linkName
    },
    getModuleNameFromPortal() {
      let { getAPPlinkname } = this
      if (getAPPlinkname === 'tenant') {
        return 'tenantcontact'
      } else if (getAPPlinkname === 'client') {
        return 'clientcontact'
      }
      return null
    },
    getPeopleTypeFromApplinkName() {
      let { getAPPlinkname } = this
      if (getAPPlinkname === 'tenant') {
        return '1'
      } else if (getAPPlinkname === 'vendor') {
        return '2'
      } else if (getAPPlinkname === 'employee') {
        return '3'
      } else if (getAPPlinkname === 'client') {
        return '4'
      } else if (getAPPlinkname === 'service') {
        return '5'
      }
      return null
    },
  },

  methods: {
    searchRecords: debounce(function() {
      this.loadRecords()
    }, 400),
    async init() {
      // this special handle for only get the client contacts which is related to the user contact
      if (this.getAPPlinkname === 'client') {
        await this.getClientContact()
      }
    },
    async getClientContact() {
      let { user } = this.$account
      if (user?.peopleId) {
        let id = user.peopleId
        if (id) {
          let url = `v3/modules/clientcontact/${id}?id=4&moduleName=clientcontact`
          let { data } = await API.get(url)
          if (data?.clientcontact) {
            this.clientContact = data.clientcontact
          }
        }
      }
    },
    async loadRecords() {
      let { query, moduleName, isPeopleSelection, items } = this
      let { peopleFromRecordFields } = items || []
      let peopleFromFields = []
      let recordsForIds = []
      let recordsFromSearch = []

      this.isLoading = true
      if (isPeopleSelection && !isEmpty(peopleFromRecordFields)) {
        peopleFromFields = this.searchPeopleFromFields(query)
      }
      if (query && !isNaN(query)) {
        recordsForIds = await this.getRecordsForIds(query, moduleName)
      }
      recordsFromSearch = await this.getRecordForQuery(query, moduleName)
      this.recordsList = this.getUniqueRecords(
        peopleFromFields,
        recordsForIds,
        recordsFromSearch
      )
      this.isLoading = false
    },

    async getRecordForQuery(query, moduleName) {
      // this menthod is used to fetch the mention people list and module records
      let { getPeopleTypeFromApplinkName, clientContact } = this
      let filters = {}
      if (!isEmpty(getPeopleTypeFromApplinkName)) {
        filters['peopleType'] = {
          operatorId: 36,
          value: [getPeopleTypeFromApplinkName],
        }
      }
      let field = {
        lookupModuleName: moduleName,
        filters,
      }
      if (clientContact?.client?.id) {
        field['lookupModuleName'] = 'clientcontact'
        filters['client'] = {
          operatorId: 36,
          value: ['' + clientContact.client.id],
        }
      }
      let { error, options } = await getFieldOptions({
        field,
        searchText: query,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        return []
      } else {
        let recordsFromSearch = options.map(mod => ({
          name: mod.label,
          id: mod.value,
        }))
        return recordsFromSearch
      }
    },
    async getRecordsForIds(query, moduleName) {
      let filters = {
        id: { operatorId: 36, value: [`${query}`] },
      }

      let field = {
        lookupModuleName: moduleName,
        filters,
      }

      let params = {
        field,
      }
      let { error, options } = await getFieldOptions(params)
      if (!error) {
        let recordsForIds = options.map(mod => ({
          name: mod.label,
          id: mod.value,
        }))
        return recordsForIds
      } else {
        return []
      }
    },
    searchPeopleFromFields(query) {
      let { items } = this
      let { peopleFromRecordFields } = items || []
      if (!peopleFromRecordFields || !peopleFromRecordFields.length) {
        return []
      }
      let people = peopleFromRecordFields.map(ppl => {
        if (ppl?.fields) {
          ppl.subtitle = this.constructSubtitle(ppl.fields)
        }
        return ppl
      })
      if (!isEmpty(query)) {
        return people.filter(ppl => ppl.name.startsWith(query.toLowerCase()))
      } else return people
    },

    constructSubtitle(fields, maxItems = 3) {
      const MAXCHAR = 45
      let subtitle

      if (fields.length > maxItems) {
        subtitle = fields.slice(0, maxItems).join(', ')
        let remainingCount = fields.length - maxItems
        subtitle = subtitle + ` + ${remainingCount}`
      } else {
        subtitle = fields.join(', ')
      }
      if (subtitle.length > MAXCHAR) {
        if (maxItems > 1) {
          return this.constructSubtitle(fields, maxItems - 1)
        }
      }
      return subtitle
    },
    getUniqueRecords(...recordLists) {
      const resultMap = new Map()
      recordLists.forEach(list => {
        list.forEach(obj => {
          if (!resultMap.has(obj.id)) {
            resultMap.set(obj.id, obj)
          }
        })
      })
      const result = Array.from(resultMap.values())
      return result
    },

    onKeyDown({ event }) {
      if (event.key === 'ArrowUp') {
        this.upHandler()
        return true
      }

      if (event.key === 'ArrowDown') {
        this.downHandler()
        return true
      }

      if (event.key === 'Enter') {
        this.enterHandler()
        return true
      }

      return false
    },

    upHandler() {
      let minIndex = 0
      if (this.selectedIndex - 1 < minIndex) {
        this.selectedIndex = minIndex
      } else {
        this.selectedIndex = this.selectedIndex - 1
      }
    },

    downHandler() {
      let maxIndex = this.recordsList.length - 1
      if (this.selectedIndex + 1 > maxIndex) {
        this.selectedIndex = maxIndex
      } else {
        this.selectedIndex = this.selectedIndex + 1
      }
    },

    enterHandler() {
      this.selectItem(this.selectedIndex)
    },

    selectItem(index) {
      let { moduleName } = this
      const item = this.recordsList[index]
      if (item) {
        if (this.isPeopleSelection) {
          this.command({
            id: `${moduleName}~${item.id}`,
            label: `${item.name}`,
          })
        } else {
          this.command({
            id: `${moduleName}~${item.id}`,
            label: `${item.id}`,
          })
        }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.record-mention-content {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  word-wrap: break-word;
}
.fc-suggestions-picker {
  max-height: 300px;
  overflow-y: scroll;
  top: 0;
  left: 0;
  margin: 5px 0;
  border-radius: 4px;
  color: #606266;
  line-height: 1.4;
  text-align: justify;
  font-size: 14px;
}
.suggestions-header {
  border-bottom: solid 1px #e5e5ea;
}
.mention-title {
  font-weight: 500;
}
.mention-subtitle {
  color: #90959c;
  font-size: 10px;
}
.popover-border-left {
  border-left: solid 0.5px #f8f9fa;
}

.fc-mention-item {
  width: 100%;
  color: #324056;
  font-size: 14px;
  padding: 5px 15px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin: 0;
  text-align: left;
  background: transparent;
  border: 1px solid transparent;
  cursor: pointer;
  border-radius: 0;
  &:hover {
    background-color: #edf2f8;
  }
}
.selected-Mention-item {
  background-color: #edf2f8;
}
.mention-popover {
  width: 300px;
  min-width: 150px;
  border-radius: 4px;
  padding: 5px 0;
  color: #606266;
  background: #fff;
  line-height: 1.4;
  text-align: justify;
  font-size: 14px;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
  word-break: break-all;
}
</style>
