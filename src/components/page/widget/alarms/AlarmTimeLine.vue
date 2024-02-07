<template>
  <div class="timeline-widget">
    <div class="container flex">
      <el-timeline>
        <el-timeline-item
          type="primary"
          :color="activity.color"
          v-for="(activity, index) in timeLineArray"
          :key="index"
          class="pT5"
        >
          <div
            v-if="activity.type === 'workorder'"
            class="pointer"
            @click="openWorkorder(activity.id)"
          >
            <div>
              <span @click="openWorkorder(activity.id)" class="fc-id pointer"
                >#{{ activity.id + ' | ' }}</span
              >
              <span class="timeline-date">
                {{ activity.createdTime | formatDate() }}
              </span>
            </div>
            <div>
              <span class="timeline-primary">{{ 'Work order ' }}</span>
              <span class="timeline-secondary">{{ 'generated with ' }}</span>
              <span class="timeline-secondary">{{ 'Priority as ' }}</span>
              <span
                class="pL5"
                v-if="activity.priority && activity.priority.id > 0"
              >
                <i
                  class="fa fa-circle f9"
                  v-bind:style="{ color: woPriority.colour }"
                  aria-hidden="true"
                ></i>
                <span class="timeline-primary">
                  {{ ' ' + woPriority.priority ? woPriority.priority : '---' }}
                </span>
              </span>
              <span class="timeline-secondary">{{ ' , Status as ' }}</span>
              <span v-if="activity.status && activity.status.id > 0">
                <span class="timeline-primary">
                  {{ ticketStatus ? ticketStatus.displayName : '---' }}
                </span>
              </span>
              <span></span>
            </div>
          </div>
          <div v-else style="margin-top: -4px">
            <div>
              <span class="timeline-date">
                {{ activity.createdTime | formatDate() }}
              </span>
            </div>
            <div>
              <span class="timeline-secondary">{{
                moduleName === 'newreadingalarm' ? 'Fault was ' : ' Alarm was '
              }}</span>
              <span class="timeline-primary">{{ 'acknowledged ' }}</span>
              <span class="timeline-secondary">{{ 'by' }}</span>
              <user-avatar
                size="sm"
                :user="getUser(activity.id)"
                :showPopover="true"
                :showLabel="false"
                moduleName="alarm"
                class="pL10 pR3 pointer"
              ></user-avatar>
              <span></span>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>
<script>
import AlarmMixin from '@/mixins/AlarmMixin'
import UserAvatar from '@/avatar/User'
import { mapGetters } from 'vuex'

export default {
  components: {
    UserAvatar,
  },
  props: [
    'widget',
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
  ],
  mixins: [AlarmMixin],
  data() {
    return {
      woPriority: null,
      ticketStatus: null,
      timeLineArray: [],
      timeLineObject: null,
    }
  },
  computed: {
    ...mapGetters(['getTicketStatus', 'getUser', 'getTicketPriority']),
  },
  watch: {},
  mounted() {
    this.formatObject()
  },
  methods: {
    formatObject() {
      if (this.widget && this.widget.relatedList) {
        this.timeLineObject = this.widget.relatedList
      }
      if (this.timeLineObject) {
        this.timeLineArray = []
        Object.keys(this.timeLineObject).forEach(d => {
          if (d === 'workOrder') {
            let type = 'workorder'
            let workOrder = this.timeLineObject.workOrder
            workOrder.type = type
            this.woPriority = this.getTicketPriority(workOrder.priority.id)
            this.ticketStatus = this.getTicketStatus(
              workOrder.status.id,
              'workorder'
            )
            this.timeLineArray.push(workOrder)
          } else if (d === 'acknowledge') {
            let type = 'acknowledge'
            let acknowledge = this.timeLineObject.acknowledge
            acknowledge.type = type
            this.timeLineArray.push(acknowledge)
          }
        })
      }
    },
  },
}
</script>
<style scoped lang="scss">
.timeline-widget {
  padding: 20px 20px 20px 0px;
  text-align: left;
  height: 100%;
  display: -webkit-box;
  display: flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  flex-direction: column;
  -webkit-box-pack: justify;
  justify-content: space-between;
}

.timeline-primary {
  font-size: 13px;
  letter-spacing: 0.5px;
  font-weight: 500;
  color: #324056;
}

.timeline-secondary {
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #324056;
}

.timeline-duration {
  font-style: italic;
  font-size: 13px;
  color: #39b2c2;
  padding-left: 5px;
  letter-spacing: 0.4px;
  font-weight: 400;
}

.timeline-date {
  font-style: italic;
  font-size: 13px;
  padding-left: 5px;
  letter-spacing: 0.4px;
  font-weight: 300;
}
</style>
