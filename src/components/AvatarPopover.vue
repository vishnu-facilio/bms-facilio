<template>
  <div class="avatar-popover-container">
    <el-popover
      v-model="popoverShown"
      ref="popover1"
      placement="top-start"
      trigger="hover"
      width="383"
      clas="avatar-popover"
      :popper-class="'avatar-popover'"
      :height="defaultheight"
      :open-delay="600"
      style="display:block;padding:0;"
    >
      <div class="popover-content-block">
        <div class="popover-avatar">
          <avatar
            :size="size"
            :user="user"
            :color="color"
            :showPopover="false"
          ></avatar>
          <!-- <user-avatar size="lg" :user="user" :showPopover="false"></user-avatar> -->
        </div>
        <div class="popover-avatar-det">
          <div class="avatar-name-txt">
            {{ user.name ? user.name : '----' }}
          </div>
          <div class="mail-block">
            <img
              src="~assets/message.svg"
              class="envelope-icon"
              style="height: 13px;vertical-align: middle;"
            />
            <span class="mail-txt"
              >{{ user.email ? user.email : '----' }}
            </span>
          </div>
          <div>
            <img
              src="~assets/smartphone1.svg"
              class="envelope-icon"
              style="height: 15px;"
            />
            <span class="popover-phone-number">
              {{ user.mobile ? user.mobile : '----' }}
            </span>
          </div>
        </div>
        <div class="popover-footer" v-if="moduleName === 'workorder'">
          <ul>
            <li class="popover-footer-list" @click="getOpen('open')">
              <div class="open-result-txt">
                {{ result.openWorkOrdersCount }}
              </div>
              <div class="result-txt-black">OPEN</div>
            </li>
            <li
              class="popover-footer-list border-RL"
              @click="getOpen('overdue')"
            >
              <div class="overdue-result-txt">
                {{ result.overDueWorkOrdersCount }}
              </div>
              <div class="result-txt-black">OVERDUE</div>
            </li>
            <li class="popover-footer-list" @click="getOpen('duetoday')">
              <div class="dueday-result-txt">
                {{ result.dueTodayWorkOrdersCount }}
              </div>
              <div class="result-txt-black">DUE TODAY</div>
            </li>
          </ul>
        </div>
      </div>
      <template slot="reference"><slot name="reference"></slot></template>
    </el-popover>
  </div>
</template>
<script>
import Avatar from './Avatar'
export default {
  props: ['size', 'user', 'color', 'name', 'moduleName'],
  data() {
    return {
      defaultheight: '230px',
      result: {},
      popoverShown: false,
      viewname: '',
    }
  },
  components: {
    Avatar,
  },
  mounted() {
    // this.showPopover = this.showpopoverFooter
    if (this.moduleName) {
      this.defaultheight = '160px'
    }
  },
  watch: {
    popoverShown(val) {
      if (val) {
        this.userOpenDatas()
      }
    },
  },
  methods: {
    userOpenDatas() {
      let self = this
      self.$http
        .post('/widget/getUserCardData', { ouid: this.user.id })
        .then(function(response) {
          if (response.status === 200) {
            self.result = response.data.result
          }
        })
    },
    getOpen(viewName) {
      let filter = {
        assignedTo: { operator: 'is', value: [this.user.id + ''] },
      }
      this.$router.push(
        '/app/wo/orders/' +
          viewName +
          '?search=' +
          encodeURIComponent(JSON.stringify(filter))
      )
      // return '/app/wo/orders/open?search=' + encodeURIComponent(JSON.stringify(filter))
    },
  },
}
</script>

<style>
.avatar-popover {
  float: left;
  width: 38px;
  padding: 0 !important;
}
.avatar-popover-container .el-popover--plain,
.avatar-popover-container .el-popover {
  max-width: 383px;
  padding-bottom: 30px;
  box-shadow: 0 8px 14px 0 rgba(0, 0, 0, 0.15);
  background-color: #ffffff;
  border: solid 1px #edeaea;
  transition-delay: 0.8s;
  -webkit-transition-delay: 0.8s;
  padding: 0;
}
.avatar-popover-container .el-popover--plain,
.avatar-popover-container .el-popover:hover {
  transition-delay: 0.2s;
  -webkit-transition-delay: 0.2s;
}
.popover-content-block {
  width: 100%;
  padding-top: 10px;
}
.popover-avatar {
  width: 30%;
  padding-left: 10px;
  float: left;
}
.popover-avatar-det {
  width: 70%;
  float: left;
  padding-right: 10px;
}
.avatar-name-txt {
  font-size: 18px;
  font-weight: 500;
  letter-spacing: 0.3px;
  text-align: left;
  margin-bottom: 10px;
  color: #333333;
}
.mail-txt {
  width: 300px;
  max-width: 300px;
  overflow-x: hidden;
  word-break: break-all;
  padding-right: 10px;
  font-size: 13px;
  letter-spacing: 0.9px;
  text-align: left;
  padding-left: 5px;
  color: #2b7ec7;
}
.popover-footer ul {
  width: 100%;
  list-style-type: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-left: 0px;
  text-align: center;
  padding-top: 0;
  padding-bottom: 0px;
  margin-top: 0px;
  margin-bottom: 0px;
}
.popover-phone-number {
  padding-right: 5px;
  font-size: 13px;
  letter-spacing: 0.8px;
  text-align: left;
  color: #333333;
}
.popover-footer-list {
  width: 33.3%;
  padding-top: 10px;
  padding-bottom: 10px;
  cursor: pointer;
}
.border-RL {
  border-right: 1px solid #edeaea;
  border-left: 1px solid #edeaea;
}
.popover-footer {
  width: 100%;
  clear: both;
  position: relative;
  border-top: 1px solid #edeaea;
  bottom: 0;
  left: 0;
  right: 0;
  line-height: 2;
}
.mail-block {
  display: inline-block;
  margin-bottom: 9px;
}
.popover-avatar-det {
  padding-top: 20px;
  padding-bottom: 30px;
}
.open-result-txt {
  font-size: 22px;
  font-weight: 500;
  letter-spacing: 1.5px;
  text-align: center;
  color: #5dc6d5;
}
.result-txt-black {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 1.2px;
  text-align: center;
  color: #333333;
}
.overdue-result-txt {
  font-size: 22px;
  font-weight: 500;
  letter-spacing: 1.5px;
  text-align: center;
  color: #e07575;
}
.dueday-result-txt {
  font-size: 22px;
  font-weight: 500;
  letter-spacing: 1.5px;
  text-align: center;
  color: #e4ba2d;
}

.popover-avatar .fc-avatar {
  width: 80px;
  height: 80px;
  border-radius: 100%;
  position: relative;
  top: 20px;
  text-align: center;
  left: 10px;
  font-size: 18px;
}
.avatar-popover-container .wo-team-txt {
  margin-left: 10px !important;
  max-width: 100px !important;
  overflow-x: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}
</style>
