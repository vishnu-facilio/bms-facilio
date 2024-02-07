<template>
  <div class="block mL30 mT20">
    <spinner :size="80" :show="loading" v-if="loading"></spinner>
    <el-timeline v-else class="wo-activity">
      <div v-if="$validation.isEmpty(activities)">
        <div
          class="flex-middle width100  justify-center shadow-none white-bg-block flex-direction-column"
        >
          <InlineSvg
            src="svgs/emptystate/history"
            iconClass="icon icon-xxxxlg mR10"
          ></InlineSvg>
          <div class="nowo-label text-center pT10">
            {{ $t('asset.history.no_history_available') }}
          </div>
        </div>
      </div>
      <template v-else v-for="(activity, index) in activities">
        <el-timeline-item
          type="primary"
          :color="activity.color"
          :timestamp="activity.ttime | formatDate"
          :key="index"
          v-if="getMessage(activity).message !== null"
        >
          <div
            v-if="
              [83, 3].includes(activity.type) &&
                getMessage(activity).message !== null &&
                activity.info.assigned
            "
          >
            <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
            {{ activity.doneBy.name }}
            <template v-if="activity.info.assigned.assignedTo">
              <template v-if="activity.info.assigned.assignedTo > 0">
                <span v-html="getMessage(activity).message"></span>
                {{ ' ' }}
                <avatar
                  size="sm"
                  :user="{
                    name: getUserName(
                      activity.info.assigned.assignedTo,
                      activity
                    ),
                  }"
                ></avatar>
                {{ ' ' }}
                <i>{{
                  getUserName(activity.info.assigned.assignedTo, activity)
                }}</i>
              </template>
              <template v-else>
                <b>removed the staff</b>
              </template>
            </template>
            <template v-else>
              <template v-if="activity.info.assigned.assignmentGroup > 0">
                <span v-html="getMessage(activity).message"></span> team
                <i>{{
                  getGroup(activity.info.assigned.assignmentGroup, activity)
                    .name
                }}</i>
              </template>
              <template v-else>
                <b>removed the team</b>
              </template>
            </template>
          </div>
          <div
            v-else-if="
              activity.type === 24 && getMessage(activity).message !== null
            "
          >
            <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
            {{ activity.doneBy.name }}
            <span v-html="getMessage(activity).message"></span>
            <i>
              <span
                @click="openPm(activity.info.addPMWO[0].pmid)"
                class="fc-id pointer"
                ># {{ activity.info.addPMWO[0].pmid }}</span
              >
            </i>
          </div>
          <div
            v-else-if="
              activity.type === 68 && getMessage(activity).message !== null
            "
          >
            <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
            {{ activity.doneBy.name }}
            <span v-html="getMessage(activity).message"></span>
            <span
              @click="openQuotationPreview(activity.info.quotationId)"
              class="f12 fc-dark-blue3-13 bold pointer mL10"
              ><i class="el-icon-view mR3 f13"></i> Preview</span
            >
          </div>
          <div v-else-if="getMessage(activity).message !== null">
            <avatar size="sm" :user="{ name: activity.doneBy.name }"></avatar>
            {{ activity.doneBy.name }}
            <span v-html="getMessage(activity).message"></span>
          </div>
        </el-timeline-item>
      </template>
    </el-timeline>
  </div>
</template>
<script>
import { mapGetters, mapState } from 'vuex'
import Avatar from '@/Avatar'
import ActivitiesMixin from './ActivitiesMixin'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['record', 'serviceportal', 'module', 'activityModule'],
  mixins: [ActivitiesMixin],
  components: {
    Avatar,
  },
  async created() {
    this.$store.dispatch('loadUsers')
    // this.$store.dispatch('loadTicketType')
    // this.$store.dispatch('loadTicketPriority')
    // this.$store.dispatch('loadSite')
    this.$store.dispatch('loadGroups')
    await this.loadActivities()
    this.$store.dispatch('view/loadModuleMeta', this.module)
    await this.loadusers()
    this.$nextTick(() => {
      this.$emit('autoResizeWidget')
    })
  },
  data() {
    return {
      activities: [],
      loading: true,
      portalUserList: [],
    }
  },
  computed: {
    ...mapState({
      // users: state => state.users,
      // sites: state => state.site,
      metaInfo: state => state.view.metaInfo,
    }),

    ...mapGetters([
      // 'getUser',
      'getGroup',
    ]),
    fieldsMap() {
      let { metaInfo } = this
      let map = {}
      if (!isEmpty(metaInfo)) {
        let fields = this.$getProperty(metaInfo, 'fields', [])
        fields.forEach(field => {
          this.$setProperty(map, `${field.name}`, field)
        })
      }
      return map
    },
  },
  watch: {
    record: async function() {
      await this.loadActivities()
      await this.loadusers()
      this.$nextTick(() => {
        this.$emit('autoResizeWidget')
      })
    },
  },
  methods: {
    openPm(id) {
      this.$router.replace({ path: '/app/wo/planned/summary/' + id })
    },
    async loadusers() {
      this.loading = true
      let { activities } = this
      // let ouids = [],
      let assignedToIds = []
      activities.forEach(activity => {
        let { $getProperty } = this
        // let ouid = $getProperty(activity, 'doneBy.ouid', null)
        let assignedToId = $getProperty(
          activity,
          'info.assigned.assignedTo',
          null
        )

        // !isEmpty(ouid) && ouids.push(`${ouid}`)
        if (!isEmpty(assignedToId) && assignedToId !== -99)
          assignedToIds.push(`${assignedToId}`)
      })

      let userIds = [...new Set([...assignedToIds])]
      if (!isEmpty(userIds)) {
        let params = {
          includeParentFilter: true,
          filters: JSON.stringify({ ouid: { operatorId: 36, value: userIds } }),
        }

        let { error, data } = await API.get('/setup/portalUsersList', params)
        if (!error) this.portalUserList = data.users || []
      }
      this.loading = false
    },
    async loadActivities() {
      if (this.activityModule) {
        let params = {}
        if (this.module === 'newreadingalarm') {
          params.occurrenceId = this.record.occurrence.id
        } else {
          params['parentId'] = this.record.id
        }
        this.loading = true

        let { error, data } = await API.post(
          '/v2/activity/' + this.activityModule,
          params
        )
        if (!error) {
          let activityList = data?.activity || []

          if (this.module === 'newreadingalarm') {
            activityList = activityList.map(activity => activity.data)
          }
          this.activities = activityList
        } else this.$message.error(error.message)
      }
      this.loading = false
    },
    // compare(str1, str2) {
    //   return !str1.localeCompare(str2)
    // },
    // getSiteName(siteId) {
    //   let site = this.sites.find(site => site.id === siteId)
    //   return site ? site.name : ''
    // },
    openQuotationPreview(id) {
      window.open(
        `${window.location.protocol}//${window.location.host}/app/pdf/quotationpdf?quotationId=${id}`
      )
    },
  },
}
</script>
<style lang="scss">
.wo-activity {
  .el-timeline-item__timestamp.is-bottom {
    margin-top: 8px;
    position: absolute;
    left: -70px;
    top: -1px;
  }
  .el-timeline-item__wrapper {
    position: relative;
    padding-left: 130px;
    top: -3px;
  }
  .el-timeline-item {
    position: relative;
    padding-bottom: 20px;
    margin-left: 40px;
  }
  .el-timeline-item__node--normal {
    left: 80px;
    width: 10px;
    height: 10px;
    top: 5px;
    letter-spacing: 0.3px;
    font-size: 12px;
    border: solid 2px #ff3184;
    background: #fff;
  }
  .el-timeline-item__tail {
    margin-left: 80px;
    margin-top: 10px;
  }
  .el-timeline-item__timestamp.is-bottom {
    color: #8ca1ad;
    line-height: 1;
    font-size: 12px;
  }
}
</style>
