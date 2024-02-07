<script>
import { isEmpty, isFunction } from '@facilio/utils/validation'
import PlaceholderPopover from './PlaceholderPopover'
import { API } from '@facilio/api'

export default {
  props: ['placement', 'trigger', 'title', 'module', 'filter'],
  components: { PlaceholderPopover },
  data() {
    return {
      childrenPopovers: [],
      showPopover: false,
      placeholder: null,
      options: [],
      loading: false,
    }
  },
  created() {
    this.loadPlaceholders()
  },
  render() {
    return (
      <el-popover
        v-model={this.showPopover}
        placement={this.placement}
        trigger="click"
        popper-class="p0"
      >
        <span
          slot="reference"
          class="el-dropdown-link details-Heading text-fc-green"
          style={this.loading ? 'cursor: not-allowed' : 'cursor: pointer'}
        >
          {this.title}
        </span>
        <div class="flex flex-row">
          <PlaceholderPopover
            {...{
              on: {
                itemClick: this.emitOptions,
                openPopover: this.openPopover,
              },
            }}
            options={this.options}
          />
          {this.childrenPopovers}
        </div>
      </el-popover>
    )
  },
  methods: {
    async loadPlaceholders() {
      if (!isEmpty(this.module)) {
        this.loading = true
        let url = `v3/placeholders/${this.module}`
        let { data } = await API.get(url)
        let {
          placeholders: { fields: fields, moduleFields: moduleFields },
        } = data

        this.options = fields
        this.options.forEach((field, index) => {
          let { module } = field

          if (!isEmpty(module)) {
            let children = moduleFields[module]

            this.options[index] = { ...field, children }
          }
        })
        let { filter } = this

        if (isFunction(filter)) this.options = this.options.filter(filter)
        this.loading = false
      }
    },
    emitOptions({ option: childOption, options: parentOption }) {
      let { primaryField } = childOption

      if (isEmpty(childOption.children)) {
        this.showPopover = false
        let placeholderString

        if (!isEmpty(parentOption.name)) {
          placeholderString = `${parentOption.name}.${childOption.name}`

          this.placeholder = [parentOption.name, childOption.name]
        } else {
          if (!isEmpty(primaryField))
            placeholderString = `${childOption.name}.${primaryField}`
          else placeholderString = `${childOption.name}`

          this.placeholder = [childOption.name, primaryField]
        }
        let { module } = this

        placeholderString = `\${${module}.${placeholderString}:-}`
        this.$emit('change', {
          placeholderString,
          placeholder: this.placeholder,
        })
        this.childrenPopovers = []
      }
    },
    openPopover({ option: childOption, options: parentOption }) {
      if (!isEmpty(childOption.children)) {
        let visited = this.$getProperty(parentOption, 'visited')

        if (visited) {
          this.childrenPopovers.pop()
        } else {
          parentOption.visited = true
        }
        this.childrenPopovers.push(this.addChildPopover(childOption))
      } else {
        this.childrenPopovers.pop()
      }
    },
    addChildPopover(options) {
      return (
        <PlaceholderPopover
          class="popover-border-left"
          {...{
            on: {
              itemClick: this.emitOptions,
            },
          }}
          options={options}
        />
      )
    },
  },
}
</script>
