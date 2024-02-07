<template>
  <div class="fc__layout__has__row justify-content-space">
    <div class="notes-richtext-editor ">
      <EditorContent
        ref="NotesEditor"
        :editor="editor"
        class="notes-richtext-content"
      />
    </div>
    <div class="comment-input-footer d-flex justify-content-space wrap">
      <slot name="attachments"> </slot>
      <div
        class="d-flex justify-end items-end marginL-auto  flex-middle flex-shrink-0"
        v-if="editor && expandTextBox"
        :content="characterLimitTooltip"
        v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
      >
        <span class="comment-character-count f12"
          >{{ editor.storage.characterCount.characters() }}/{{ charLimit }}
          {{ $t('common._common.characters') }}</span
        >

        <br />
      </div>
    </div>
  </div>
</template>

<script>
import Mention from '@tiptap/extension-mention'
import StarterKit from '@tiptap/starter-kit'
import { Editor, EditorContent } from '@tiptap/vue-2'
import recordSuggestions from './recordSuggestions'
import { PluginKey } from 'prosemirror-state'
import Placeholder from '@tiptap/extension-placeholder'
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'
import Link from '@tiptap/extension-link'
import CharacterCount from '@tiptap/extension-character-count'

export default {
  props: {
    value: {
      type: String,
      default: '',
    },
    parentModule: {
      type: String,
    },
    placeholder: {
      type: String,
      default: 'Enter text here',
    },
    charLimit: {
      type: Number,
    },
    expandTextBox: {
      type: Boolean,
    },
    peopleFromRecordFields: {
      type: Array,
      default: () => {
        return []
      },
    },
  },
  components: {
    EditorContent,
  },

  watch: {
    isRichTextFocused(isRichTextFocused) {
      if (isRichTextFocused) {
        this.$emit('focus')
      }
    },
  },

  data() {
    return {
      editor: null,
    }
  },
  computed: {
    content: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      },
    },
    isRichTextFocused() {
      let focus = this.editor?.isFocused
      if (!focus) {
        return false
      } else {
        return focus
      }
    },
    atMention() {
      return Mention.extend({ name: 'atMention' }).configure({
        HTMLAttributes: {
          class: this.$constants.PEOPLE_MENTION_CLASS,
        },
        renderLabel({ node }) {
          return `@${node.attrs.label ?? node.attrs.id}`
        },

        suggestion: {
          ...recordSuggestions,
          char: '@',
          pluginKey: new PluginKey('atKey'),
          items: () => {
            return {
              moduleName: 'people',
              peopleFromRecordFields: this.peopleFromRecordFields,
            }
          },
        },
      })
    },
    slashMention() {
      return Mention.extend({ name: 'slashMention' }).configure({
        HTMLAttributes: {
          class: this.$constants.RECORD_MENTION_CLASS,
        },
        renderLabel({ node }) {
          return `/${node.attrs.label ?? node.attrs.id}`
        },

        suggestion: {
          ...recordSuggestions,
          char: '/',
          pluginKey: new PluginKey('slashKey'),
          items: () => {
            return {
              moduleName: this.parentModule,
            }
          },
        },
      })
    },
    characterLimitTooltip() {
      let { charLimit } = this
      return this.$t('common._common.character_limit_info') + charLimit
    },
  },

  mounted() {
    this.init()
  },

  beforeUnmount() {
    this.editor.destroy()
  },
  methods: {
    init() {
      let { content, placeholder, onUpdate, atMention, slashMention } = this
      this.editor = new Editor({
        extensions: [
          StarterKit,
          Link,
          Placeholder.configure({
            emptyEditorClass: 'is-editor-empty',
            emptyNodeClass: 'is-empty',
            placeholder,
            showOnlyWhenEditable: true,
            showOnlyCurrent: true,
          }),
          CharacterCount.configure({
            limit: this.charLimit,
          }),
          atMention,
          slashMention,
        ],
        content,
        onUpdate,
      })
    },
    onUpdate({ editor }) {
      let html = editor.getHTML()
      let isNotEmpty = !isEmpty(editor.getText)
      let hasChanged = sanitize(html) !== sanitize(this.content)

      if (isNotEmpty || hasChanged) {
        this.content = html
        this.$emit('input', isNotEmpty ? sanitize(html) : null)
      }
    },
    triggerPeopleMention() {
      this.editor
        .chain()
        .insertContent('@')
        .focus()
        .run()
    },
    triggerSlashMention() {
      this.editor
        .chain()
        .insertContent('/')
        .focus()
        .run()
    },
    setContent(content) {
      this.editor.setContent(content)
    },
    prefillMentionForReply(user) {
      if (!user || !user.peopleId || !user.name) {
        return
      }
      let moduleName = 'people'
      let props = {
        id: `${moduleName}~${user.peopleId}`,
        label: `${user.name}`,
      }
      this.editor
        .chain()
        .focus()
        .insertContent([
          {
            type: 'atMention',
            attrs: props,
          },
          {
            type: 'text',
            text: ' ',
          },
        ])
        .run()
    },
  },
}
</script>

<style lang="scss">
.notes-richtext-editor {
  padding: 10px 15px;

  .notes-richtext-content {
    position: relative;
    word-wrap: break-word;
    word-break: break-word;

    h1 {
      font-size: 2.5em;
    }
    h2 {
      font-size: 1.85em;
    }
    h3 {
      font-size: 1.5em;
    }
    h1,
    h2,
    h3 {
      font-weight: 300;
    }
    p {
      font-size: 1em;
      margin: 0;
      padding: 0;
      letter-spacing: 0;
      -webkit-font-smoothing: antialiased;
    }
  }
  p.is-editor-empty:first-child::before {
    content: attr(data-placeholder);
    float: left;
    color: #aaa;
    pointer-events: none;
    height: 0;
    font-style: italic;
  }
}
.comment-people-mention {
  color: #0a7aff;
  font-weight: 500;
}
.comment-record-mention {
  color: #0a7aff;
  font-weight: 500;
}
.comment-character-count {
  margin: 3px 5px;
  color: #90959c;
}
.comment-input-footer {
  padding: 3px 5px;
}
</style>
