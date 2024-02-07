<template>
  <div>
    <div v-for="(skill, index) in skills" :key="index">
      <div class="d-flex">
        <el-row :gutter="10" class="margin-bottom: 10px">
          <el-col :span="2">
            <div class="alphabet-circle m10">{{ index + 1 }}</div>
          </el-col>
          <el-col :span="5">
            <el-input
              v-model="skill.name"
              class="width100 fc-input-full-border2"
              autofocus
              type="text"
              :placeholder="$t('setup.skills.skillLevel')"
            />
          </el-col>
          <el-col :span="6">
            <el-input
              v-model="skill.description"
              class="width100 fc-input-full-border2"
              autofocus
              type="text"
              :placeholder="$t('setup.crafts.form.desc')"
            />
          </el-col>
          <el-col :span="4">
            <el-input
              v-model="skill.skillLevelRank"
              class="width100 fc-input-full-border2"
              autofocus
              type="text"
              :placeholder="$t('setup.skills.rank')"
            />
          </el-col>
          <el-col :span="4">
            <el-input
              v-model="skill.standardRate"
              class="width100 fc-input-full-border2"
              autofocus
              type="text"
              :placeholder="$t('setup.skills.Rate')"
            />
          </el-col>
          <el-col :span="1">
            <div @click="addSkills">
              <inline-svg
                src="add-icon"
                style="height: 18px; width: 18px; margin:10px"
                class="delete-icon"
                iconClass="icon icon-md"
              />
            </div>
          </el-col>

          <el-col :span="1">
            <div v-if="skills.length > 1" @click="subtractSkills(skill)">
              <inline-svg
                src="remove-icon"
                style="height: 18px; width: 18px; margin: 10px"
                class="delete-icon"
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
import cloneDeep from 'lodash/cloneDeep'

const skillModel = {
  name: '',
  description: '',
  standardRate: '',
  skillLevelRank: '',
}

export default {
  props: ['isNew', 'editSkills'],
  data() {
    return {
      deleteSkillsList: [],
    }
  },
  computed: {
    skills: {
      get() {
        return this.editSkills
      },
      set(val) {
        this.$emit('update:editSkills', val)
      },
    },
  },
  methods: {
    addSkills() {
      let skillModelObj = cloneDeep(skillModel)
      skillModelObj.name = ''
      skillModelObj.skillLevelRank = ''
      skillModelObj.standardRate = ''
      this.skills.push(skillModelObj)
    },
    subtractSkills(deletedSkill) {
      let index = this.skills.findIndex(skill => skill.id === deletedSkill.id)
      if (index != -1) {
        this.skills.splice(index, 1)
      }
      this.deleteSkillsList.push(deletedSkill.id)
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
.alphabet-circle {
  width: 26px;
  height: 26px;
  border: solid 1px #a3bdc0;
  border-radius: 100%;
  text-align: center;
  font-size: 14px;
  font-weight: bold;
  letter-spacing: 0.5px;
  line-height: 24.7px;
  color: #91b3b6;
}
</style>
