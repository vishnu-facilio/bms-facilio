<script>
import http from 'util/http'
import Spinner from '@/Spinner'

import { isEmpty } from '@facilio/utils/validation'

let moduleNameHash = {
  workorder: 'workorder',
  submitrequest: 'workorder',
}

export default {
  components: {
    Spinner,
  },
  data() {
    return {
      forms: [],
      isLoading: false,
      selectedForm: {},
      formObj: {},
      isSaving: false,
    }
  },
  created() {
    let { module } = this
    let moduleName = moduleNameHash[module]
    let url = `/v2/forms?moduleName=${moduleName}`
    this.isLoading = true
    http
      .get(url)
      .then(({ data: { message, responseCode, result = {} } }) => {
        if (responseCode === 0) {
          let { forms = [] } = result
          this.forms = forms
          let defaultForm = forms.find(form => form.name.startsWith('default_'))
          // This will trigger form details api via selectedForm computed property
          this.$set(this, 'selectedForm', defaultForm)
        } else {
          throw new Error(message)
        }
      })
      .catch(({ message }) => {
        this.$message.error(message)
      })
  },
  watch: {
    selectedForm: {
      handler() {
        this.loadFormData()
      },
      deep: true,
    },
  },
  methods: {
    loadFormData() {
      let { selectedForm } = this
      let { id, name } = selectedForm
      let url =
        id === -1
          ? `/v2/forms/workorder?formName=${name}`
          : `/v2/forms/workorder?formId=${id}`
      this.isLoading = true
      let promise = http
        .get(url)
        .then(({ data: { message, responseCode, result = {} } }) => {
          if (responseCode === 0) {
            let { form } = result
            if (!isEmpty(form)) {
              form.primaryBtnLabel = 'SAVE'
              form.secondaryBtnLabel = 'CANCEL'
              this.formObj = form
            }
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      Promise.all([promise]).finally(() => (this.isLoading = false))
    },
  },
}
</script>
