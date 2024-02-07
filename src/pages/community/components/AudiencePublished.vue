<template>
  <div class="pL30 pR30 d-flex flex-direction-column">
    <div class="widget-topbar">
      <div class="widget-title mL0 flex-middle justify-between">
        {{ $t('common.products.audience') }}
      </div>
    </div>
    <div
      class="d-flex mT10 mB30 flex-direction-row flex-wrap"
      ref="publishTo-container"
    >
      <el-tooltip
        v-for="(audience, index) in getAudienceList"
        :key="`tooltip-${audience.id}-${index}`"
        placement="top"
        effect="dark"
      >
        <div slot="content">
          <div class="height-auto width150px align-center p8">
            <div style="font-size:10px">Audience</div>
            <div style="font-size:14px;font-weight:500">
              {{
                getSharingTypeDisplayString(
                  $getProperty(getGroupedSharing[audience.id], 'sharingType'),
                  $getProperty(
                    getGroupedSharing[audience.id],
                    'sharingTypeMeta'
                  )
                )
              }}
            </div>
            <div
              v-if="
                $getProperty(getGroupedSharing[audience.id], 'hasRoleFilter')
              "
            >
              <div style="font-size:10px;margin-top:15px">Filtered By</div>
              <div style="font-size:14px;font-weight:500">Selected Roles</div>
            </div>
          </div>
        </div>
        <div
          @click="redirectToAudience(audience.id)"
          :key="`audience-${audience.name}-${index}`"
          class="tag pointer"
        >
          {{ audience.name }}
        </div>
      </el-tooltip>
    </div>
  </div>
</template>
<script>
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

const sharingTypes = {
  TENANT_UNIT: 1,
  ROLE: 2,
  PEOPLE: 3,
  BUILDING: 4,
}

const sharingTypesMeta = {
  ALL: 1,
  SELECTED: 2,
}
export default {
  props: ['details', 'widget'],
  mixins: [FetchViewsMixin],
  computed: {
    getAudienceList() {
      let { details } = this
      let { audience } = details || {}
      return audience || []
    },
    sharingModuleName() {
      return this.$getProperty(
        this.widget,
        'widgetParams.sharingInfoModuleName'
      )
    },
    getGroupedSharing() {
      let { details, $getProperty, sharingModuleName } = this
      let publishedTo = $getProperty(details, `${sharingModuleName}`) || []

      return publishedTo.reduce((publishedObj, currentObj) => {
        let {
          sharedToSpace,
          sharedToRole,
          sharedToPeople,
          sharingType,
          audienceId,
        } = currentObj || {}
        let { ALL, SELECTED } = sharingTypesMeta
        let sharingTypeMeta = ALL
        let isSharingTypeSelected =
          !isEmpty(sharedToSpace) ||
          !isEmpty(sharedToRole) ||
          !isEmpty(sharedToPeople)
        let additionObj = {}

        if (isSharingTypeSelected) {
          sharingTypeMeta = SELECTED
        }

        audienceId = audienceId?.id

        if (audienceId in publishedObj) {
          let { ROLE } = sharingTypes
          let { sharingType: prevSharingType } = publishedObj[audienceId] || {}

          if (sharingType === ROLE && prevSharingType !== ROLE) {
            additionObj = {
              hasRoleFilter: true,
              sharingType: prevSharingType,
            }
          } else if (sharingType !== ROLE && prevSharingType === ROLE) {
            additionObj = { hasRoleFilter: true, sharingType }
          }
        }

        publishedObj[audienceId] = {
          sharingType,
          hasRoleFilter: false,
          sharingTypeMeta,
          ...additionObj,
        }

        return publishedObj
      }, {})
    },
  },
  methods: {
    async redirectToAudience(id) {
      let view = await this.fetchView('audience')
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: view,
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'audienceSummary',
          params: {
            viewname: view,
            id,
          },
        })
      }
    },
    getSharingTypeDisplayString(sharingType, sharingTypeMeta) {
      let prefix = ''
      if (sharingTypeMeta === sharingTypesMeta.ALL) {
        prefix = prefix + 'All'
      } else if (sharingTypeMeta === sharingTypesMeta.SELECTED) {
        prefix = prefix + 'Selected'
      }
      switch (sharingType) {
        case sharingTypes.TENANT_UNIT:
          return prefix + ' Tenant Units'
        case sharingTypes.ROLE:
          return prefix + ' Roles'
        case sharingTypes.PEOPLE:
          return prefix + ' Peoples'
        case sharingTypes.BUILDING:
          return prefix + ' Buildings'
        default:
          return '---'
      }
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
