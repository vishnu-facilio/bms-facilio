<template>
  <div class="newWorkorderForm">
    <el-form
      :model="model"
      :rules="rules"
      :label-position="'top'"
      ref="ruleForm"
    >
      <el-row :gutter="50" align="middle">
        <el-col
          v-bind:style="{ width: isPlannedWO || isTemplate ? '100%' : '65%' }"
          :span="18"
        >
          <div class="textcolor" style="color:#dc7171">
            {{ $t('maintenance.wr_list.subject') }}
            <el-input
              v-model="model.subject"
              placeholder=" "
              class="form-item"
              :maxlength="300"
            />
          </div>
        </el-col>
        <el-col v-if="!isPlannedWO && !isTemplate" style="width:35%" :span="8">
          <div class="textcolor">
            {{ $t('maintenance._workorder.requester') }}
          </div>
          <div>
            <el-select
              v-model="model.requester.email"
              filterable
              allow-create
              placeholder="example@domain.com"
              style="width:100%"
            >
              <el-option
                v-for="user in userlist"
                :key="user.email"
                :label="user.name"
                :value="user.email"
              >
                <span style="float: left">{{ user.email }}</span>
              </el-option>
            </el-select>
            <!--<el-select v-model="model.requester.email" placeholder=" " class="form-item" style="width:100%" /> -->
          </div>
        </el-col>
      </el-row>

      <el-row align="middle">
        <el-col :span="18">
          <div class="textcolor">
            {{ $t('maintenance._workorder.description') }}
            <el-input
              ref="textareaexpand"
              :autosize="{ minRows: 2, maxRows: 2 }"
              v-model="model.description"
              @keyup.native="handletextarea"
              class="form-item border-bottom-add"
              type="textarea"
              style="white-space: pre-wrap;"
            />
          </div>
        </el-col>
      </el-row>

      <el-row align="middle">
        <el-col style="width:62%" :span="18">
          <div class="textcolor">{{ $t('maintenance.wr_list.category') }}</div>
          <div>
            <el-select
              v-model="model.category.id"
              style="width:100%"
              class="form-item"
              placeholder=" "
            >
              <el-option
                v-for="category in ticketcategory"
                :key="category.id"
                :label="category.displayName"
                :value="parseInt(category.id)"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row align="middle">
        <el-col style="width:62%" :span="18">
          <div class="textcolor">
            {{ $t('maintenance.pm_list.maintenance_type') }}
          </div>
          <div>
            <el-select
              v-model="model.type.id"
              style="width:100%"
              class="form-item down-arrow-height-remove"
              placeholder=" "
            >
              <el-option
                v-for="(type, key) in tickettype"
                :key="key"
                :label="type"
                :value="parseInt(key)"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row v-if="!currentSiteId || currentSiteId <= 0" align="middle">
        <el-col style="width:62%" :span="18">
          <div class="textcolor" style="color:#dc7171">
            {{ $t('maintenance.wr_list.site') }}
          </div>
          <div>
            <el-select
              v-model="model.siteId"
              :disabled="currentSiteId && currentSiteId > 0"
              style="width:100%"
              class="form-item down-arrow-height-remove"
              placeholder=" "
            >
              <el-option
                v-for="s in site"
                :key="s.id"
                :label="s.name"
                :value="s.id"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row align="middle">
        <el-col style="width:62%" :span="18">
          <div class="textcolor">
            {{ $t('maintenance.wr_list.team_staff') }}
          </div>
          <div
            style="border-bottom: 1px solid #d8dce5; padding-bottom: 5px;padding-top: 5px;"
          >
            <span>{{ getTeamStaffLabel(model) }}</span>
            <span style="float: right;padding-right: 12px;">
              <img
                class="svg-icon team-down-icon"
                src="~assets/down-arrow.svg"
              />
            </span>
          </div>
          <f-assignment
            :model="model"
            :siteId="model.siteId"
            viewtype="form"
          ></f-assignment>
          <div></div>
        </el-col>
      </el-row>
      <el-row align="middle">
        <el-col style="width:62%" :span="18">
          <div class="textcolor">
            {{ $t('maintenance.wr_list.space_asset') }}
          </div>
          <div class="add">
            <el-input
              @change="
                quickSearchQuery = spaceAssetDisplayName
                showSpaceAssetChooser()
              "
              v-model="spaceAssetDisplayName"
              style="width:100%"
              type="text"
              :placeholder="$t('maintenance.wr_list.to_search_type')"
            >
              <i
                @click="visibility = true"
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                class="el-input__icon el-icon-search"
              ></i>
            </el-input>
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col style="width:62%" :span="18">
          <div class="textcolor">{{ $t('maintenance.wr_list.priority') }}</div>
          <div>
            <el-select
              v-model="model.priority.id"
              style="width:100%"
              class="form-item down-arrow-height-remove"
              placeholder=" "
            >
              <el-option
                v-for="(priority, key) in ticketpriority"
                :key="key"
                :label="priority.displayName"
                :value="parseInt(priority.id)"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <div v-if="isPlannedWO">
        <duration v-model="model.duration" labelClass="textcolor"></duration>
      </div>
      <el-row v-else>
        <el-col style="width:62%" :span="18">
          <div class="textcolor">{{ $t('maintenance.wr_list.duedate') }}</div>
          <f-date-picker
            v-model="model.dueDate"
            class="form-item date-icon-right"
            prefix-icon
            style="width:100%"
            float-label="Due Date"
            :type="'datetime'"
          ></f-date-picker>
        </el-col>
      </el-row>
      <el-row>
        <el-col style="width:62%" :span="18">
          <div class="textcolor">
            {{ $t('maintenance.wr_list.allow_user_wh') }}
          </div>
          <el-checkbox
            class="pT10"
            v-model="model.isWorkDurationChangeAllowed"
          ></el-checkbox>
        </el-col>
      </el-row>
      <el-row
        v-for="(customField, key) in customFields"
        :key="key"
        v-if="model.data"
      >
        <el-col style="width:62%" :span="18">
          <div class="textcolor">{{ customField.displayName }}</div>
          <el-input
            v-if="
              ['NUMBER', 'DECIMAL'].includes(customField.dataTypeEnum) ||
                ['NUMBER', 'DECIMAL'].includes(customField.dataTypeEnum._name)
            "
            type="number"
            v-model="model.data[customField.name]"
            placeholder=" "
            class="form-item"
          />
          <f-date-picker
            v-else-if="
              customField.dataTypeEnum === 'DATE' ||
                customField.dataTypeEnum._name === 'DATE'
            "
            v-model="model.data[customField.name]"
            :type="'date'"
            class="form-item"
            :value-format="'timestamp'"
          ></f-date-picker>
          <f-date-picker
            v-else-if="
              customField.dataTypeEnum === 'DATE_TIME' ||
                customField.dataTypeEnum._name === 'DATE_TIME'
            "
            v-model="model.data[customField.name]"
            :type="'datetime'"
            class="form-item"
            :value-format="'timestamp'"
          ></f-date-picker>
          <el-input
            v-else-if="customField.displayTypeInt === 2"
            v-model="model.data[customField.name]"
            type="textarea"
            :autosize="{ minRows: 1, maxRows: 3 }"
            class="form-item"
          ></el-input>
          <el-input
            v-else
            type="text"
            v-model="model.data[customField.name]"
            placeholder=" "
            class="form-item"
          />
        </el-col>
      </el-row>
      <wo-space-asset-chooser
        @associate="associate"
        :visibility.sync="visibility"
        :query="quickSearchQuery"
        :filter="filter"
      ></wo-space-asset-chooser>
    </el-form>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import FAssignment from '@/FAssignment'
import WoSpaceAssetChooser from '@/SpaceAssetChooser'
import Duration from '@/FDuration'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: ['model', 'customFields', 'isPlannedWO', 'isTemplate'],
  mixins: [TeamStaffMixin],
  components: {
    FAssignment,
    WoSpaceAssetChooser,
    Duration,
    FDatePicker,
  },
  data() {
    return {
      visibility: false,
      quickSearchQuery: '',
      spaceAssetDisplayName: '',
      userlist: [],
      error: '',
      asset: {
        subject: '',
        description: '',
      },
      rules: {
        subject: [
          {
            required: true,
            message: 'Please enter the subject',
            trigger: 'blur',
          },
        ],
        description: [{ required: true, message: ' ', trigger: 'blur' }],
      },
      currentSiteId: null,
      filter: {},
    }
  },
  created() {
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketstatus: state => state.ticketStatus.workorder,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
      site: state => state.site,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters(['getTicketTypePickList']),
    tickettype() {
      return this.getTicketTypePickList()
    },
    assignToList() {
      return this.users.concat(this.groups)
    },
  },
  mounted() {
    this.loadusers()
    if (this.model.resource) {
      this.spaceAssetDisplayName = this.model.resource.name
    }
    this.currentSiteId = Number(this.$cookie.get('fc.currentSite'))
    this.currentSiteId =
      this.currentSiteId && this.currentSiteId > 0 ? this.currentSiteId : null
    if (this.currentSiteId) {
      this.model.siteId = this.currentSiteId
    }
    if (this.site.length === 1) {
      this.model.siteId = this.site[0].id
      this.filter.siteId = this.model.siteId
    }
  },
  watch: {
    'model.resource'(val) {
      console.log('ttttgggg' + JSON.stringify(val))
      if (val) {
        this.spaceAssetDisplayName = val.name
      }
    },
    'model.description'(val) {
      setTimeout(() => {
        this.handletextarea()
      }, 50)
    },
    'model.siteId'(val) {
      this.filter.siteId = this.model.siteId
    },
  },
  methods: {
    handletextarea() {
      let el = this.$refs.textareaexpand
      setTimeout(() => {
        el.$refs.textarea.style.cssText = 'height:auto'
        el.$refs.textarea.style.cssText =
          'height:' + el.$refs.textarea.scrollHeight + 'px'
      }, 0)
    },
    showSpaceAssetChooser() {
      this.visibility = true
    },
    associate(selectedObj) {
      this.model.resource = selectedObj
      this.spaceAssetDisplayName = selectedObj.name
      this.visibility = false
    },
    loadusers: function() {
      let self = this
      self.$http
        .get('/setup/portalusers')
        .then(function(response) {
          self.userlist = response['data'].users
        })
        .catch(function(error) {
          console.log(error)
        })
    },
  },
}
</script>
<style>
.textcolor {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #6b7e91;
  padding-top: 25px;
}
.newWorkorderForm .el-input__suffix-inner .add {
  content: '\E619';
}
.newWorkorderForm .textcolor {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #6b7e91;
  /* padding-top: 25px; */
}

.newWorkorderForm .el-input.is-disabled .el-input__inner {
  background-color: transparent !important;
}
/* .date-icon-right .el-input__prefix{
  left: 0;
  right: 25px;
} */
.date-icon-right .el-icon-time {
  float: right;
}
.border-bottom-add .border-bottom1px {
  border-bottom: 1px solid #e2e8ee !important;
}
.border-bottom-add .el-textarea__inner {
  resize: both;
}
</style>
