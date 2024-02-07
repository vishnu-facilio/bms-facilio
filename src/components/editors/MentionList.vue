<script>
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import router from 'src/router'
import PlaceholderPopover from '@/placeholder/PlaceholderPopover.vue'
import { isEmpty, isFunction } from '@facilio/utils/validation'
export default {
  props: {
    command: {
      type: Function,
      required: true,
    },
    query: {
      type: String,
      required: false,
      default: () => {
        return ''
      },
    },
  },
  render() {
    return (
      <div class="suggestion-datas">
        {this.loading ? (
          <div class="mention-list-spinner">
            <Spinner show={true} size={80} />
          </div>
        ) : (
          <div class="flex flex-row">
            <PlaceholderPopover
              {...{
                on: {
                  itemClick: this.emitOptions,
                  openPopover: this.openPopover,
                },
              }}
              options={this.filtredOption}
            />
            {this.childrenPopovers}
          </div>
        )}
      </div>
    )
  },
  data() {
    return {
      showPopover: true,
      value: [],
      selectedIndex: 0,
      moduleFields: [],
      loading: true,
      items: [],
      searchQuery: null,
      showSubMenuItem: false,
      placeholder: null,
      options: [],
      selectedData: null,
      childrenPopovers: [],
    }
  },
  watch: {
    items() {
      this.selectedIndex = 0
    },
  },
  components: {
    Spinner,
    PlaceholderPopover,
  },
  computed: {
    filtredOption() {
      if (this.query === '') {
        return this.options
      } else {
        return this.options.filter(option => {
          if (
            option.name.toLowerCase().indexOf(this.query.toLowerCase()) >= 0
          ) {
            return option
          }
        })
      }
    },
    getIndex() {
      if (this.query === '') {
        return 0
      } else {
        return this.options.findIndex(option => {
          if (
            option.name.toLowerCase().indexOf(this.query.toLowerCase()) >= 0
          ) {
            return option
          }
        })
      }
    },
    mentionList() {
      return this.items
    },
    resultQuery() {
      if (this.searchQuery) {
        return this.mentionList.filter(item => {
          return this.searchQuery
            .toLowerCase()
            .split(' ')
            .every(v => item.displayName.toLowerCase().includes(v))
        })
      } else {
        return this.mentionList
      }
    },
    moduleName() {
      return this.$getProperty(router.currentRoute, 'params.moduleName')
    },
  },
  async created() {
    await this.getModuleFields()
    await this.loadPlaceholders()
  },
  methods: {
    emitOptions(items) {
      let { option } = items
      if (option) {
        this.selectItem(option)
      }
    },
    handleChildClick(items) {
      let { option, options } = items
      if (option.name && options.name) {
        let name = { name: `${options.name}.${option.name}` }
        this.selectItem(name)
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
              itemClick: this.handleChildClick,
            },
          }}
          options={options}
        />
      )
    },
    onKeyDown({ event }) {
      if (event.key === 'ArrowUp') {
        this.upHandler()
        return true
      }

      if (event.key === 'ArrowDown') {
        this.downHandler()
        return true
      }

      if (event.key === 'Enter') {
        this.enterHandler()
        return true
      }
      return false
    },

    upHandler() {
      this.selectedIndex =
        (this.selectedIndex + this.items.length - 1) % this.items.length
    },

    downHandler() {
      this.selectedIndex = (this.selectedIndex + 1) % this.items.length
    },

    enterHandler() {
      let item = this.resultQuery[this.selectedIndex]
      this.selectItem(item)
    },

    selectItem(item) {
      if (item) {
        this.command({
          id:
            '{' +
            this.$getProperty(router.currentRoute, 'params.moduleName') +
            '.' +
            item.name +
            '}',
        })
      }
    },
    async getModuleFields() {
      this.loading = true
      let { error, data } = await API.get(
        `v2/modules/fields/fields?moduleName=${this.moduleName}`
      )
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.items = data.fields
      }
      this.loading = false
    },
    showMenuList() {
      this.showSubMenuItem = true
    },
    async loadPlaceholders() {
      if (this.options.length) {
        return
      }
      if (
        !isEmpty(this.$getProperty(router.currentRoute, 'params.moduleName'))
      ) {
        this.loading = true
        let url = `v3/placeholders/${this.$getProperty(
          router.currentRoute,
          'params.moduleName'
        )}`
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

    handleChange(value) {
      this.loadPlaceholders()
    },
  },
}
</script>

<style lang="scss">
.fc-mention-list-item {
  width: 400px;
  height: 180px;
  position: relative;
  border-radius: 0.5rem;
  background: #fff;
  color: rgba(0, 0, 0, 0.8);
  overflow: hidden;
  font-size: 0.9rem;
  overflow-y: scroll;
  box-shadow: none;
  box-shadow: none;
  padding: 0;
  border: 1px solid #e1e1e1;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  .el-cascader-panel.is-bordered {
    border: none;
  }
  // .el-cascader-menu__list {
  //   border-right: 1px solid #e1e1e1;
  // }
}

.submenu {
  width: 230px;
  height: 180px;
  position: relative;
  border-radius: 0.5rem;
  background: #fff;
  color: rgba(0, 0, 0, 0.8);
  overflow: hidden;
  font-size: 0.9rem;
  overflow-y: scroll;
  box-shadow: none;
  box-shadow: none;
  padding: 0 0 10px 0;
  border: 1px solid #e1e1e1;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-left: none;
}

.fc-mention-list-block {
  display: flex;
}

.fc-mention-list-empty {
  width: 230px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.2rem;
  position: relative;
  border-radius: 0.5rem;
  background: #fff;
  color: rgba(0, 0, 0, 0.8);
  overflow: hidden;
  font-size: 0.9rem;
  overflow-y: scroll;
  box-shadow: none;
  box-shadow: none;
  padding: 10px 0 10px 0;
}

.fc-mention-item {
  width: 100%;
  font-size: 14px;
  display: block;
  margin: 0;
  margin-bottom: 5px;
  text-align: left;
  background: transparent;
  border: 1px solid transparent;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 0;
  &:hover {
    background: rgb(57 178 194 / 10%);
  }
  &.is-selected {
    border-color: none;
    border: inherit;
    outline: none;
    border-width: 0;
    background: rgb(57 178 194 / 10%);
  }
}
.fc-field-search {
  position: sticky;
  top: 0;
  background: #fff;
  z-index: 9;
  .el-input__inner {
    height: 40px;
    padding-left: 10px;
  }
}
.suggestion-datas {
  .fc-placeholder-picker {
    background: #fff;
    overflow-y: scroll;
    // box-shadow: 0 1.5px 3.5px 0 rgb(226 229 233 / 50%);
  }
  .el-scrollbar__view {
    background: #fff;
    // border-top: 1px solid #edf4fa;
    // border-right: 1px solid #edf4fa;
    // border-bottom: 1px solid #edf4fa;
    max-height: 200px;
    min-height: 200px;
    overflow-y: scroll;
    // border-bottom-left-radius: 4px;
    box-shadow: 0 1.5px 3.5px 0 rgb(226 229 233 / 50%);
    width: 150px;
    // border-left: none;
    // border-top-left-radius: 0;
    // border-bottom-left-radius: 0;
    border: 1px solid #e3eaed;
    z-index: 12323234423432443423;
    border-bottom: 1px solid #e3eaed;
    border-radius: 8px;
  }
  .fc-field-item {
    padding-top: 5px;
    padding-bottom: 5px;
    line-height: 20px;
    word-break: break-all;
    font-size: 13px;
  }
  .popover-border-left {
    position: relative;
    left: -5px;
    .el-scrollbar__view {
      border-top-left-radius: 0;
      border-bottom-left-radius: 0;
    }
  }
}
.mention-list-spinner {
  width: 150px;
  max-height: 200px;
  min-height: 200px;
  border: 1px solid #e3eaed;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1.5px 3.5px 0 rgb(226 229 233 / 50%);
}
</style>
