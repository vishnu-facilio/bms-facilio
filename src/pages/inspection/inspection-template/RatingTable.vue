<template>
  <div class="mcq-table-container justify-content-center d-flex">
    <el-table
      v-if="!$validation.isEmpty(mcqSummary)"
      :data="mcqSummary"
      style="width: 100%"
      class="mcq-table mT20"
    >
      <template v-if="isStarRating">
        <el-table-column :label="$t('qanda.template.stars')">
          <template v-slot="data">
            <div class="flex">
              <inline-svg
                v-for="stars in getCurrentOption(data)"
                :key="stars"
                src="svgs/star-yellow"
                class="vertical-middle fill-blue5"
                iconClass="icon icon-xxl"
              />
            </div>
          </template>
        </el-table-column>
      </template>
      <template v-else>
        <el-table-column :label="$t('qanda.template.emoji')">
          <template v-slot="data">
            <EmojiIconRenderer
              class="emoji-icon-layout mL_7"
              :rating="getCurrentOption(data)"
              :isActive="true"
            />
          </template>
        </el-table-column>
      </template>
      <el-table-column :label="$t('qanda.template.response_percent')">
        <template v-slot="data">
          <div>{{ (data.row || {}).percent || 0 }} %</div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('qanda.template.response_count')">
        <template v-slot="data">
          <div>{{ (data.row || {}).count || 0 }}</div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import MCQTable from './MCQTable'
import { EmojiIconRenderer } from '@facilio/survey'
export default {
  extends: MCQTable,
  components: { EmojiIconRenderer },
  computed: {
    emojiStartRating() {
      let { question } = this || {}
      let { ratingScale } = question

      let halfScal = ratingScale / 2

      return Math.round(5 - halfScal)
    },
    mcqSummary() {
      let { question, emojiStartRating, isStarRating } = this
      let { summary } = question || {}
      let deserializedSummary = []
      if (summary) {
        deserializedSummary = summary.map(currSummary => {
          let { option } = currSummary
          if (!isStarRating) option = Math.abs(option + emojiStartRating - 1)
          return { ...currSummary, option }
        })
      }
      return deserializedSummary
    },
    isStarRating() {
      let { question } = this || {}
      let { questionType } = question || {}
      return questionType === 'STAR_RATING'
    },
  },
  methods: {
    getCurrentOption(data) {
      return (data.row || {}).option
    },
  },
}
</script>

<style scoped>
.emoji-icon-layout {
  height: 3rem;
}
</style>
