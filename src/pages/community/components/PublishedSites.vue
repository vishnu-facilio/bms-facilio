<template>
  <div class="pL30 pR30 d-flex flex-direction-column">
    <div class="widget-topbar">
      <div class="widget-title mL0 flex-middle justify-between">
        {{ $t('common._common.published_to', { sharingType }) }}
      </div>
      <div
        class="pointer underline"
        v-if="spaces.length > threshold"
        @click="showMoreDialog"
      >
        {{ $t('common._common.view-all') }}
      </div>
    </div>
    <div
      class="d-flex mT10 mB30 flex-direction-row flex-wrap"
      ref="publishTo-container"
    >
      <div
        v-for="(space, index) in limitedItems"
        :key="`space-${space.id}-${index}`"
        class="tag"
      >
        {{ space.name }}
      </div>
      <div
        class="more flex-middle height20 text-fc-grey"
        v-if="spaces.length > threshold"
      >
        <i class="el-icon-more"></i>
      </div>
      <ItemsList
        v-if="showMore"
        :key="details.name"
        :details="details"
        :sharingModuleName="sharingModuleName"
        :showDialog="showMore"
      >
      </ItemsList>
    </div>
    <template v-if="hasRoleFilter">
      <div>
        <div class="widget-topbar">
          <div class="widget-title mL0 flex-middle justify-between">
            {{ $t('common._common.filter_by_roles') }}
          </div>
        </div>
        <div
          class="d-flex mT10 mB30 flex-direction-row flex-wrap"
          ref="publishTo-container"
        >
          <div
            v-for="(role, index) in getAppliedRolesForFilter"
            :key="`space-${role.id}-${index}`"
            class="tag"
          >
            {{ role.name }}
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import ItemsList from 'src/pages/community/components/PublishedItemsList.vue'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
const sharingTypes = {
  TENANT_UNIT: 1,
  ROLE: 2,
  PEOPLE: 3,
  BUILDING: 4,
}
export default {
  props: ['details', 'widget'],
  components: { ItemsList },
  data() {
    return {
      showMore: false,
      threshold: 10,
    }
  },
  mounted() {
    eventBus.$on('listDialogClose', this.closeDialog)
  },
  computed: {
    sharingModuleName() {
      return this.$getProperty(
        this.widget,
        'widgetParams.sharingInfoModuleName'
      )
    },
    limitedItems() {
      let { threshold } = this
      return this.spaces.slice(0, threshold)
    },
    hasRoleFilter() {
      let { sharingModuleName, details } = this
      if (sharingModuleName != 'audienceSharing') {
        let { audience } = details || {}
        let { filterSharingType } = audience || {}
        return filterSharingType
      } else {
        let { filterSharingType } = details || {}
        return filterSharingType
      }
    },
    getAppliedRolesForFilter() {
      let { details, $getProperty, sharingModuleName } = this
      let publishedTo = $getProperty(details, `${sharingModuleName}`) || []
      if (!isEmpty(publishedTo) && this.hasRoleFilter) {
        let rolesFilter = (
          (publishedTo || []).map(({ sharedToRole }) => {
            if (!isEmpty(sharedToRole)) {
              let { name, id } = sharedToRole || {}
              return { id, name }
            }
          }) || []
        ).filter(info => !isEmpty((info || {}).name))
        return rolesFilter
      }
      return []
    },
    spaces() {
      let { details, $getProperty, sharingModuleName } = this
      let publishedTo = $getProperty(details, `${sharingModuleName}`) || []
      if (!isEmpty(publishedTo)) {
        let publishedItem = publishedTo[0]
        let { sharedToSpace, sharedToRole, sharedToPeople } =
          publishedItem || {}
        if (
          !isEmpty(sharedToSpace) ||
          !isEmpty(sharedToRole) ||
          !isEmpty(sharedToPeople)
        ) {
          let spaces = (
            (publishedTo || []).map(
              ({ sharedToSpace, sharedToRole, sharedToPeople }) => {
                if (!isEmpty(sharedToSpace)) {
                  let { name, id } = sharedToSpace || {}
                  return { id, name }
                } else if (!isEmpty(sharedToRole) && !this.hasRoleFilter) {
                  let { name, id } = sharedToRole || {}
                  return { id, name }
                } else if (!isEmpty(sharedToPeople)) {
                  let { name, id } = sharedToPeople || {}
                  return { id, name }
                }
              }
            ) || []
          ).filter(info => !isEmpty((info || {}).name))
          return spaces
        } else {
          let sharingType = this.getSharingTypeWithAppliedFilter
          switch (sharingType) {
            case sharingTypes.TENANT_UNIT:
              return [{ id: -1, name: 'All Tenant Units' }]
            case sharingTypes.ROLE:
              return [{ id: -1, name: 'All Roles' }]
            case sharingTypes.PEOPLE:
              return [{ id: -1, name: 'All People' }]
            case sharingTypes.BUILDING:
              return [{ id: -1, name: 'All Building' }]
            default:
              return [{ id: -1, name: '---' }]
          }
        }
      }
      return []
    },
    getSharingTypeWithAppliedFilter() {
      let { details, $getProperty, sharingModuleName } = this
      let publishedTo = $getProperty(details, `${sharingModuleName}`) || []
      let { hasRoleFilter } = this
      if (hasRoleFilter) {
        let publishToInfo = publishedTo.filter(function(publishInfo) {
          return publishInfo.sharingType != sharingTypes.ROLE
        })
        return this.$getProperty(publishToInfo, '0.sharingType')
      }
      return this.$getProperty(publishedTo, '0.sharingType')
    },
    sharingType() {
      let { details, $getProperty, sharingModuleName } = this
      let publishedTo = $getProperty(details, `${sharingModuleName}`) || []
      let sharingTypeId = this.getSharingTypeWithAppliedFilter
      if (!isEmpty(publishedTo)) {
        let publishedItem = publishedTo[0]
        let { sharedToSpace, sharedToRole, sharedToPeople } = publishedItem
        let sharingPrefix =
          !isEmpty(sharedToSpace) ||
          !isEmpty(sharedToRole) ||
          !isEmpty(sharedToPeople)
            ? 'Selected'
            : 'All'
        switch (sharingTypeId) {
          case sharingTypes.TENANT_UNIT:
            return sharingPrefix + ' Tenant Units'
          case sharingTypes.ROLE:
            return sharingPrefix + ' Roles'
          case sharingTypes.PEOPLE:
            return sharingPrefix + ' Peoples'
          case sharingTypes.BUILDING:
            return sharingPrefix + ' Buildings'
          default:
            return '---'
        }
      }
      return '---'
    },
  },
  methods: {
    showMoreDialog() {
      this.showMore = true
    },
    closeDialog() {
      this.showMore = false
    },
  },
}
</script>
<style lang="scss" scoped>
.tag {
  font-size: 13px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: center;
  color: #3ab2c2;
  background-color: #f7feff;
  border: solid 1px #3ab2c2;
  border-radius: 3px;
  padding: 4px 12px;
  margin-right: 10px;
  margin-bottom: 15px;
}
.more {
  color: #3ab2c2;
  font-size: 16px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: center;
  margin-top: 2px;
}
</style>
