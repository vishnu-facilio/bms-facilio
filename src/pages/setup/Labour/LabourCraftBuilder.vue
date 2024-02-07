<template>
  <div>
    <div>
      <el-row :gutter="10" class="margin-bottom: 10px">
        <el-col :span="3">
          <p class="labourcraft-label">
            {{ $t('setup.labour.labourcraftbuilder.default') }}
          </p>
        </el-col>
        <el-col :span="6">
          <p class="labourcraft-label">
            {{ $t('setup.labour.labourcraftbuilder.craft') }}
          </p>
        </el-col>
        <el-col :span="6">
          <p class="labourcraft-label">
            {{ $t('setup.labour.labourcraftbuilder.skill') }}
          </p>
        </el-col>
        <el-col :span="6">
          <p class="labourcraft-label">
            {{ $t('setup.labour.labourcraftbuilder.rateperhour') }}
          </p>
        </el-col>
      </el-row>
    </div>
    <div v-for="(craftAndSkill, index) in labourCraftSkillList" :key="index">
      <div>
        <el-row :gutter="10" class="mB10">
          <el-col :span="3">
            <el-radio
              v-model="defaultId"
              :label="index"
              class="fc-radio-btn mT10 labourcraft-radio-button-colour setup-labour-craft-radio"
            >
              {{ '' }}
            </el-radio>
          </el-col>
          <el-col :span="6">
            <el-select
              v-model="craftAndSkill.craftId"
              class="width100 fc-input-full-border2"
              autofocus
              type="text"
              filterable
              clearable
              :remote="true"
              :remote-method="remoteSearchCrafts"
              :placeholder="$t('setup.labour.labourcraftbuilder.select_craft')"
              @change="fetchSkillforCraft(index)"
            >
              <el-option
                v-for="craft in craftlist"
                :key="craft.id"
                :label="craft.name"
                :value="craft.id"
              >
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select
              v-model="craftAndSkill.skillId"
              class="width100 fc-input-full-border2"
              autofocus
              clearable
              type="text"
              :disabled="!craftAndSkill.craftId"
              :loading="craftAndSkill.skillOptionLoading"
              :placeholder="$t('setup.labour.labourcraftbuilder.select_skill')"
              @change="fetchSkillRate(index, craftAndSkill.skillId)"
            >
              <el-option
                v-for="skill in craftAndSkill.skillOptions"
                :key="skill.id"
                :label="skill.name"
                :value="skill.id"
              >
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-input
              v-model="craftAndSkill.rate"
              disabled
              class="width100 fc-input-full-border2"
              autofocus
              type="number"
              :placeholder="$t('setup.labour.labourcraftbuilder.enter_rate')"
            />
          </el-col>
          <el-col :span="1">
            <div @click="addlabourCraft">
              <inline-svg
                src="add-icon"
                class="delete-icon m10"
                iconClass="icon icon-md"
              />
            </div>
          </el-col>

          <el-col :span="1">
            <div
              v-if="labourCraftSkillList.length > 1"
              @click="subtractCraft(craftAndSkill)"
            >
              <inline-svg
                src="remove-icon"
                class="delete-icon m10"
                iconClass="icon icon-md"
              />
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
import { isNullOrUndefined, isEmpty } from '@facilio/utils/validation'
import debounce from 'lodash/debounce'

const craftAndSkillModel = {
  craftId: null,
  skillId: null,
  rate: null,
  skillOptions: [],
  isDefault: false,
  skillOptionLoading: false,
}

export default {
  props: ['editLabourCraftSkill'],
  data() {
    return {
      craftlist: [],
      loading: false,
      rate: '',
      skills: [],
      deleteLabourCraftList: [],
      currentCraftRate: null,
      defaultId: null,
    }
  },
  async created() {
    if (!isEmpty(this.editLabourCraftSkill)) {
      let defaultId = this.editLabourCraftSkill.findIndex(
        labour => labour.isDefault
      )

      if (defaultId !== -1) this.defaultId = defaultId
    }
    await this.fetchCraftList()
  },
  computed: {
    labourCraftSkillList: {
      get() {
        return this.editLabourCraftSkill
      },
      set(val) {
        this.$emit('update:editLabourCraftSkill', val)
      },
    },
  },
  watch: {
    defaultId(newVal) {
      if (!isNullOrUndefined(newVal)) {
        this.labourCraftSkillList.forEach((craftSkill, index) => {
          let isDefault = false

          if (index === parseInt(newVal)) isDefault = true
          this.$set(craftSkill, 'isDefault', isDefault)
        })
      }
    },
  },
  methods: {
    async fetchCraftList(props) {
      this.loading = true
      let { filters } = props || {}
      let params = {
        page: 1,
        perPage: 20,
        withCount: true,
        moduleName: 'crafts',
      }
      if (!isEmpty(filters)) {
        params = { ...params, filters: JSON.stringify(filters) }
      }
      let { list, error } = await API.fetchAll(`crafts`, params)
      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t(`setup.labour.labourcraftbuilder.fetch_craft_error`)
        )
      } else {
        this.craftlist = list || []
      }
      this.loading = false
    },
    remoteSearchCrafts: debounce(async function(searchText) {
      if (!isEmpty(searchText)) {
        let filters = { name: { operatorId: 5, value: [`${searchText}`] } }
        this.fetchCraftList({ filters })
      }
    }, 1000),
    async fetchSkillforCraft(index) {
      if (isEmpty(this.labourCraftSkillList[index].craftId)) {
        let labourCraftSkillList = this.labourCraftSkillList.map(
          (item, itemIndex) => {
            if (index === itemIndex)
              return { ...item, rate: null, skillId: null }
            else return item
          }
        )
        this.labourCraftSkillList = labourCraftSkillList
        this.currentCraftRate = null
      } else {
        let craftObj = this.labourCraftSkillList[index]

        craftObj.skillOptionLoading = true

        let { craftId } = craftObj || {}
        let { crafts, error } = await API.fetchRecord('crafts', {
          id: craftId,
        })
        if (error) {
          this.$message.error(
            error.message ||
              this.$t(`setup.labour.labourcraftbuilder.fetch_craft_error`)
          )
        } else {
          let { skills } = crafts || {}
          craftObj.skillOptions = skills
        }

        craftObj.skillOptionLoading = false
        let { standardRate } = crafts || {}
        this.currentCraftRate = standardRate
        let labourCraftSkillList = this.labourCraftSkillList.map(
          (item, itemIndex) => {
            if (index === itemIndex)
              return { ...item, rate: standardRate, skillId: null }
            else return item
          }
        )
        this.labourCraftSkillList = labourCraftSkillList
      }
    },
    fetchSkillRate(index, skillId) {
      let modifyRate = null
      if (isEmpty(skillId)) {
        let { currentCraftRate } = this
        modifyRate = currentCraftRate
      } else {
        let skillobj = this.$getProperty(
          this,
          `labourCraftSkillList.${index}.skillOptions`
        )
        skillobj = skillobj.filter(skill => {
          if (skill.id === skillId) return skill
        })
        modifyRate = this.$getProperty(skillobj, `0.standardRate`)
      }
      this.labourCraftSkillList = this.labourCraftSkillList.map(
        (item, itemIndex) => {
          if (index === itemIndex) return { ...item, rate: modifyRate }
          else return item
        }
      )
    },
    addlabourCraft() {
      let craftAndSkillModelObj = cloneDeep(craftAndSkillModel)
      craftAndSkillModelObj.craftId = null
      craftAndSkillModelObj.skillId = null
      craftAndSkillModelObj.rate = null
      craftAndSkillModelObj.skillOptions = []
      this.labourCraftSkillList.push(craftAndSkillModelObj)
    },
    subtractCraft(deletedCraft) {
      let index = this.labourCraftSkillList.findIndex(
        labourCraftSkill => labourCraftSkill.id === deletedCraft.id
      )
      if (index != -1) {
        this.labourCraftSkillList.splice(index, 1)
      }
      this.deleteLabourCraftList.push(deletedCraft.id)
    },
  },
}
</script>
<style lang="scss" scoped>
.delete-icon {
  position: relative;
  top: 0;
  left: 10px;
  margin-right: 5px;
}
.labourcraft-radio-button-colour {
  border: #39b2c2;
}
.labourcraft-label {
  font-weight: 500;
  font-size: 14px;
}
</style>
<style lang="scss">
.setup-labour-craft-radio .el-radio__inner {
  border-color: #39b2c2;
  border-width: 2px;
}
</style>
