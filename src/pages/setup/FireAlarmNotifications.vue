<template>
  <div class="notification-layout row">
    <!-- <div class="notificationHeader">Alarm Notifications:</div> -->
    <div class="setting-header">
      <div class="setting-title-block">
        <div class="setting-form-title">Alarm Notifications</div>
        <div class="heading-description">List of all Alarm notifications</div>
      </div>
    </div>
    <div class="container-scroll">
      <div class="setting-Rlayout mT20">
        <!-- <div class="n-header row mT30">
      <div class="col-12">
        <div class="row tabel-selector-row m0" style="position:relative;">
          <div class="tabel-selector" @click="selectSubSection({name: h})" v-for="(h, index) in headerJson" :key="index">{{h}}&nbsp;<span class=""></span><div v-bind:class="{active: subSection === h}" ></div></div>
        </div>
      </div>
     </div> -->
        <div class="notificationDiv">
          <table border="0" cellpadding="0" cellspacing="0">
            <thead>
              <tr>
                <th style="width: 25%">Notification</th>
                <th style="width: 20%">Status</th>
                <th style="width: 55%">Notify To</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="rule in rules" :key="rule.id">
                <td v-if="rule.name === 'Create Alarm - EMail'">Email</td>
                <td v-else>SMS</td>
                <td>
                  <q-toggle v-model="rule.status" @input="changeStatus(rule)" />
                </td>
                <td v-if="rule.name === 'Create Alarm - EMail'">
                  <div>
                    <span
                      v-for="(emailId, index) in emails"
                      style="padding:1px;"
                      :key="index"
                      ><avatar size="md" :user="{ name: emailId }"></avatar
                    ></span>
                    <span style="padding:1px;">
                      <div
                        class="fc-avatar fc-avatar-md"
                        style="background-color: #e2e2e2; color: grey;"
                      >
                        <i class="fa fa-plus"></i>
                        <q-tooltip
                          anchor="top middle"
                          self="bottom middle"
                          :offset="[10, 10]"
                          >Notify user</q-tooltip
                        >
                        <q-popover style="min-width: 350px;" ref="emailPopover">
                          <q-list link class="no-border">
                            <q-item tag="label">
                              <q-item-main>
                                <q-input
                                  float-label="Email"
                                  v-model="email"
                                  type="textarea"
                                />
                              </q-item-main>
                            </q-item>
                            <q-item tag="label" class="catagoryFooter">
                              <q-item-side>
                                <q-btn
                                  outline
                                  color="primary"
                                  class="catagoryFooterBtn"
                                  @click="addEmailTemplate()"
                                  style="font-weight:500;font-size:13px;"
                                  small
                                  >Add</q-btn
                                >
                              </q-item-side>
                            </q-item>
                          </q-list>
                        </q-popover>
                      </div>
                    </span>
                  </div>
                </td>
                <td v-else>
                  <div>
                    <span
                      v-for="(phoneNo, index) in phoneNumbers"
                      style="padding:1px;"
                      :key="index"
                      ><avatar size="md" :user="{ name: phoneNo }"></avatar
                    ></span>
                    <span style="padding:1px;">
                      <div
                        class="fc-avatar fc-avatar-md"
                        style="background-color: #e2e2e2; color: grey;"
                      >
                        <i class="fa fa-plus"></i>
                        <q-tooltip
                          anchor="top middle"
                          self="bottom middle"
                          :offset="[10, 10]"
                          >Notify user</q-tooltip
                        >
                        <q-popover style="min-width: 350px;" ref="phPopover">
                          <q-list link class="no-border">
                            <q-item tag="label">
                              <q-item-main>
                                <q-input
                                  float-label="Mobile Number"
                                  v-model="phone"
                                  type="textarea"
                                />
                              </q-item-main>
                            </q-item>
                            <q-item tag="label" class="catagoryFooter">
                              <q-item-side>
                                <q-btn
                                  outline
                                  color="primary"
                                  class="catagoryFooterBtn"
                                  @click="addSMSTemplate()"
                                  style="font-weight:500;font-size:13px;"
                                  small
                                  >Add</q-btn
                                >
                              </q-item-side>
                            </q-item>
                          </q-list>
                        </q-popover>
                      </div>
                    </span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import {
  QTooltip,
  QPopover,
  QList,
  QItem,
  QItemMain,
  QInput,
  QBtn,
  QItemSide,
  QToggle,
} from 'quasar'
export default {
  title() {
    return 'New Alarm Notifications'
  },
  components: {
    QTooltip,
    QPopover,
    QList,
    QItem,
    QItemMain,
    QInput,
    QBtn,
    QItemSide,
    QToggle,
    Avatar,
  },
  data() {
    return {
      rules: [],
      emails: [],
      phoneNumbers: [],
      email: null,
      phone: null,
      headerJson: ['Workorder', 'Alarm'],
    }
  },
  mounted: function() {
    this.loadNotifications()
  },
  methods: {
    loadNotifications: function() {
      let self = this
      self.$http
        .get('/setup/alarmrules')
        .then(function(response) {
          console.log(response['data'].alarmCreationRules)
          self.rules = response['data'].alarmCreationRules
          self.emails = response['data'].emails
          self.phoneNumbers = response['data'].phoneNumbers
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    changeStatus: function(rule) {
      let url
      if (rule.status) {
        url = 'turnonrule'
      } else {
        url = 'turnoffrule'
      }
      this.$http
        .post('/setup/' + url, { workflowId: rule.id })
        .then(function(response) {})
        .catch(function(error) {
          console.log(error)
        })
    },
    selectSubSection(section) {
      this.subSection = section.name
      if (this.subSection === 'Workorder') {
        this.loadWorkFlowRules('workorder')
      } else if (this.subSection === 'Alarm') {
        // this.loadWorkFlowRules('alarm')
        this.$router.push({ path: '/app/setup/general/alarmnotifications' })
      }
    },
    addEmailTemplate: function() {
      let self = this
      self.$http
        .post('/setup/addalarmemail', { email: this.email })
        .then(function(response) {
          self.emails.push(self.email)
          self.$refs.emailPopover.close()
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    addSMSTemplate: function() {
      let self = this
      self.$http
        .post('/setup/addalarmsms', { phone: this.phone })
        .then(function(response) {
          self.phoneNumbers.push(self.phone)
          self.$refs.phPopover.close()
        })
        .catch(function(error) {
          console.log(error)
        })
    },
  },
}
</script>
<style>
.notificationHeader {
  font-size: 19px;
}
.notificationDiv {
  width: 800px;
  padding-top: 10px;
}
.notificationDiv table {
  width: 100%;
  border: 1px solid #eee;
  border-bottom: none;
}
.notificationDiv th {
  text-align: left;
  background: #f2f2f2;
  color: #666;
  padding: 10px;
  padding-left: 20px;
  font-size: 15px;
}
.notificationDiv td {
  text-align: left;
  color: #666;
  padding: 10px;
  padding-left: 20px;
  font-size: 15px;
  border-bottom: 1px solid #eee;
}
</style>
