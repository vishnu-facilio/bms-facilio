<<template>
  <div class="approve-form-item">
    <el-form-item prop="activityType" class="fc-tag p0 pL10 pR10">
      <p class="fc-input-label-txt">
        {{ $t('common.header.field') }}
        {{ selectedFields.length > 0 ? '(' + selectedFields.length + ')' : '' }}
      </p>
      <el-select
        :multiple-limit="3"
        v-model="selectedFields"
        @change="addFields"
        multiple
        collapse-tags
        placeholder="Select"
        class="fc-input-full-border-select2 width50"
      >
        <el-option
          v-for="(fld, index) in moduleFields"
          :key="index"
          :label="fld.displayName"
          :value="fld.id"
        >
        </el-option>
      </el-select>
      <el-button
        v-if="selectedFields.length < 3"
        slot="reference"
        class="all-rule-btn"
        ><img src="~assets/add-icon.svg" class="mL10"
      /></el-button>
    </el-form-item>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  data() {
    return {
      selectedFields: [],
      metaInfo: {},
    }
  },
  mounted() {
    this.getMetaFields()
  },
  created() {
    let { isEdit } = this || {}
    if (isEdit) {
      let { workflowRule } = this || {}
      let { fields } = workflowRule || {}
      if (!isEmpty(fields)) {
        if (fields) {
          this.selectedFieldObject = []
          fields.map(d => {
            this.selectedFields.push(parseInt(d.fieldId))
          })
          this.$emit('selectedFields', fields)
        }
      }
    }
  },
  props: ['workflowRule', 'module', 'isEdit'],
  computed: {
    moduleFields() {
      return this.$getProperty(this.metaInfo, 'fields', null)
    },
  },
  methods: {
    async getMetaFields() {
      let { module } = this
      if (module) {
        let url = `/module/meta?moduleName=${module}`
        let { data } = await API.get(url, {
          module,
        })
        this.metaInfo = data.meta
      }
    },
    addFields() {
      let { selectedFields } = this || {}
      let fields = []
      selectedFields.forEach(d => {
        fields.push({ fieldId: d })
      })
      this.$emit('selectedFields', fields)
    },
  },
}
</script>
