<template>
  <el-form>
    <p class="subHeading-pink-txt bold pB0 text-fc-pink text-uppercase">
      {{ $t('setup.customButton.permissions') }}
    </p>
    <p class="fc-sub-title-desc">
      {{ $t('setup.customButton.permissions_subtext') }}
    </p>
    <el-row
      v-for="(approver, key) in activeApprovers"
      class="criteria-condition-block pT5 pB8 pL0"
      :key="key"
    >
      <el-col :span="8">
        <el-select
          v-model="approver.type"
          placeholder="Select"
          @change="approver.approverId = null"
          class="fc-input-full-border-select2 width100"
          filterable
        >
          <el-option
            v-for="(field, index) in approverTypes"
            :key="`${field.id}_${index}`"
            :label="field.name"
            :value="field.id"
          >
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="18" class="pL10">
        <el-select
          v-model="approver.approverId"
          @change="serialize"
          placeholder="Select"
          class="fc-input-full-border-select2 width100"
          filterable
        >
          <el-option
            v-for="(option, index) in approverOptionMap[approver.type]"
            :key="`${option.id}_${index}`"
            :label="option.displayName || option.name"
            :value="option.id"
          >
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="3" class="mT5 mL5 border-left action">
        <img
          src="~assets/add-icon.svg"
          v-if="key + 1 === activeApprovers.length"
          style="height:18px;width:18px;"
          class="delete-icon"
          @click="addApprover"
        />
        <img
          src="~assets/remove-icon.svg"
          v-if="isEmptyApprover(key)"
          style="height:18px;width:18px;margin-right: 3px;"
          class="delete-icon mL5"
          @click="deleteApprovers(key)"
        />
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { mapGetters, mapState } from 'vuex'
import {
  approverTypes,
  getApproverOptions,
  deserializeApprover,
  serializeApprover,
} from 'newapp/utils/approverUtils'

export default {
  name: 'CustomButtonPermissions',
  props: ['module', 'customButtonObj'],
  data() {
    return {
      approverTypes,
      approverOptionMap: [],
      moduleFieldsMeta: [],
      activeApprovers: [],
    }
  },
  async created() {
    await this.getModuleFields()

    let { approvers } = this.customButtonObj || {}

    if (!isEmpty(approvers)) {
      this.activeApprovers = approvers.map(approver =>
        deserializeApprover(approver)
      )
    } else {
      this.addApprover()
    }
  },
  computed: {
    ...mapGetters('automationSetup', ['getAutomationModulesList']),
    ...mapState({
      users: state => state.users,
      teams: state => state.groups,
      roles: state => state.roles,
    }),
    approverTypeOptions() {
      return {
        users: [
          ...this.users,
          {
            name: 'Requester',
            id: 'requester',
          },
        ],
        roles: this.roles,
        teams: this.teams,
        fields: this.moduleFields,
      }
    },
    moduleFields() {
      return this.moduleFieldsMeta || null
    },
  },
  watch: {
    approverTypeOptions: {
      handler: async function(approverOptions) {
        this.approverOptionMap = await getApproverOptions(approverOptions)
      },
      immediate: true,
    },
  },
  methods: {
    serialize() {
      let { activeApprovers, moduleFields } = this

      if (isEmpty(activeApprovers)) return

      let approvers = activeApprovers
        .map(approver => serializeApprover(approver, { fields: moduleFields }))
        .filter(approver => !isEmpty(approver))

      this.$emit('setProperties', { approvers })
    },
    async getModuleFields() {
      let { error, data } = await API.get(
        `/module/metafields?moduleName=${this.module}`
      )

      if (!error) {
        this.moduleFieldsMeta = data.meta.fields
      }
    },
    addApprover() {
      this.activeApprovers.push({ type: null, approverId: null })
    },
    deleteApprovers(key) {
      let approver = this.activeApprovers[key]

      if (
        this.activeApprovers.length === 1 &&
        (!isEmpty(approver.approverId) || !isEmpty(approver.type))
      ) {
        this.addApprover()
      }
      this.activeApprovers.splice(key, 1)
      this.serialize()
    },
    isEmptyApprover(index) {
      let approver = this.activeApprovers[index]
      if (
        !isEmpty(approver.type) ||
        !isEmpty(approver.approverId) ||
        this.activeApprovers.length > 1
      )
        return true
      return false
    },
  },
}
</script>
