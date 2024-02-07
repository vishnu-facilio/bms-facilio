<template>
  <div class="fc-url-reuslt-block">
    <div class="fc-attach-using-url">
      <div class="fc-black2-14 bold">
        Attach files using url
      </div>

      <div class="fc-url-add-block">
        <div class="pL20 pR20 fc-url-template-block pB20 border-bottom24">
          <div class="fc-url-grey-title opacity-100" style="color: #000;">
            Url title
          </div>
          <el-input
            placeholder="Add url"
            v-model="templateUrlData.urlString"
            class="width100 fc-input-full-border2 mT10"
            type="url"
            :class="{
              inActiveUrlClass: getInputValidateClass,
            }"
            @change="validateUrl(templateUrlData.urlString)"
          ></el-input>
          <div class="fc-url-alert-txt pT5" v-if="getInputValidateClass">
            Invalid url
          </div>
          <el-button
            class="fc-template-grey-btn mT10"
            @click="addTemplateUrl"
            :disabled="getInputValidateClass"
            :class="{ activeEnableBtn: templateUrlData.urlString }"
            >Add</el-button
          >
        </div>
        <!-- list render -->
        <div>
          <el-row
            class="pL20 pB10 pT10 flex-middle justify-content-space pR20"
            v-for="(iex, inx) in urlObjects"
            :key="inx"
          >
            <el-col :span="21">
              <el-input
                v-if="iex.edit"
                v-model="iex.urlString"
                class="width100 fc-template-edit-input"
                :class="{
                  inActiveUrlClass: getInputValidateClass,
                }"
                @blur="
                  iex.edit = false
                  $emit('update')
                "
                @keyup.enter="
                  iex.edit = false
                  $emit('update')
                "
                v-focus
              ></el-input>
              <div v-else>
                <label
                  @click="iex.edit = true"
                  class="fc-url-blue-txt break-word pR20"
                >
                  {{ iex.urlString }}
                </label>
              </div>
            </el-col>
            <el-col :span="3">
              <div class="pL20" v-if="iex.edit">
                <div @click="showUrlFieldEditClose(inx)">
                  <inline-svg
                    src="svgs/tick"
                    iconClass="icon icon-md"
                    class="pointer"
                  ></inline-svg>
                </div>
              </div>
              <div class="flex-middle justify-content-end" v-if="!iex.edit">
                <div class="pointer" @click="showUrlFieldEdit(inx)">
                  <i class="el-icon-edit f14 fc-setup-list-edit"></i>
                </div>
                <div class="pointer" @click="removeUrlItem(inx)">
                  <i
                    class="el-icon-delete f14 pointer fc-setup-list-delete"
                  ></i>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    urlAttachments: {
      type: Array,
      default: () => {
        return []
      },
    },
  },
  data() {
    return {
      editedUrlField: null,
      templateUrlData: [
        {
          urlString: '',
          type: 3,
          edit: false,
        },
      ],
      attachmentsDataSetEdit: [],
      showUrlAttachment: true,
      showUrlEditAttachment: false,
      showUrlAttachmentInput: false,
    }
  },
  computed: {
    getInputValidateClass() {
      if (this.templateUrlData.urlString) {
        let regex = /^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/
        if (!this.templateUrlData.urlString.match(regex)) {
          return true
        }
      }
      return false
    },
    urlObjects() {
      return this.templateUrlData.filter(element => {
        if (Object.keys(element.urlString).length !== 0) {
          return true
        }
        return false
      })
    },
  },
  mounted() {
    this.templateUrlData = this.formattedUrldata(this.urlAttachments)
  },
  directives: {
    focus: {
      inserted(el) {
        el.focus()
      },
    },
  },
  methods: {
    formattedUrldata(urlObjects) {
      let object = []
      urlObjects.forEach(rt => {
        this.$set(rt, 'edit', false)
        object.push(rt)
      })
      return urlObjects
    },
    removeUrlItem(inx) {
      this.templateUrlData.splice(inx, 1)
    },
    showUrlFieldEdit(index) {
      this.urlObjects[index].edit = true
      this.showUrlAttachment = false
      // this.showUrlEditAttachment = true
      this.showUrlAttachmentInput = true
    },
    showUrlFieldEditClose(index) {
      this.urlObjects[index].edit = false

      this.showUrlEditAttachment = false
      this.showUrlAttachment = true
      this.showUrlAttachmentInput = false
    },
    addTemplateUrl() {
      if (this.templateUrlData.urlString) {
        this.templateUrlData.push({
          urlString: this.templateUrlData.urlString,
          type: this.templateUrlData.type,
          edit: this.templateUrlData.edit,
        })
        this.templateUrlData.urlString = null
        this.$emit('upadte:urlAttachments', this.urlObjects)
      } else {
        this.$message.error('Please, add your Url')
      }
    },
    validateUrl() {
      let regex = /^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/
      if (!this.templateUrlData.urlString.match(regex)) {
        this.inActiveUrlClass = true
        return false
      }
    },
  },
}
</script>
