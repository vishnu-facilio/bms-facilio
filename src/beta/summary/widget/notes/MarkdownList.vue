<template>
  <FModal
    :title="$t('common.markdown.text')"
    :visible="markdownshow"
    :hideFooter="true"
    size="S"
    @cancel="cancelForm"
  >
    <div class="markdown-style">
      <table>
        <tr>
          <th>{{ $t('common.markdown.element') }}</th>
          <th>{{ $t('common.markdown.output') }}</th>
          <th>{{ $t('common.markdown.syntax') }}</th>
          <th>{{ $t('common.markdown.null') }}</th>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{ $t('common.markdown.bold') }}</span>
          </td>
          <td>
            <p>
              <strong>{{ $t('common.markdown.hithere') }}</strong>
            </p>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">**</span>{{ $t('common.markdown.hithere')
              }}<span class="">**</span>
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(bold)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{ $t('common.markdown.h1') }}</span>
          </td>
          <td>
            <h1>{{ $t('common.markdown.hithere') }}</h1>
          </td>
          <td>
            <span class="syntax-text">
              <span class=""># </span>{{ $t('common.markdown.hithere') }}
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(h1)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{ $t('common.markdown.h2') }}</span>
          </td>
          <td>
            <h2>{{ $t('common.markdown.hithere') }}</h2>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">## </span>{{ $t('common.markdown.hithere') }}
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(h2)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{
              $t('common.markdown.italics')
            }}</span>
          </td>
          <td>
            <em>{{ $t('common.markdown.hithere') }}</em>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">*</span>{{ $t('common.markdown.hithere')
              }}<span class="">*</span>
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(italics)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{ $t('common.markdown.strike') }}</span>
          </td>
          <td>
            <del>{{ $t('common.markdown.hithere') }}</del>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">{{ $t('common.markdown.strikesyntax') }}</span
              >{{ $t('common.markdown.hithere')
              }}<span class="">{{ $t('common.markdown.strikesyntax') }}</span>
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(strike)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{
              $t('common.markdown.underline')
            }}</span>
          </td>
          <td>
            <ins>{{ $t('common.markdown.hithere') }}</ins>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">++</span>{{ $t('common.markdown.hithere')
              }}<span class="">++</span>
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(underline)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{ $t('common.markdown.list') }}</span>
          </td>
          <td>
            <li>{{ $t('common.markdown.hithere') }}</li>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">- </span>{{ $t('common.markdown.hithere') }}
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(list)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{
              $t('common.markdown.blockquote')
            }}</span>
          </td>
          <td>
            <blockquote
              class="list-blockquote"
              style=" display: block; margin-top: 1px;
           margin-bottom: 1px;
           font-size: 13px;
           padding: 0px 0px;"
            >
              {{ $t('common.markdown.hithere') }}
            </blockquote>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">> </span>{{ $t('common.markdown.hithere') }}
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(blockquote)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{
              $t('common.markdown.hyperlink')
            }}</span>
          </td>
          <td>
            <a>{{ $t('common.markdown.hithere') }}</a>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">[</span>{{ $t('common.markdown.hithere')
              }}<span class="">]</span> <span class="">(</span
              >{{ $t('common.markdown.url') }}<span class="">)</span>
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(hyperlink)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{
              $t('common.markdown.highlight')
            }}</span>
          </td>
          <td>
            <code>{{ $t('common.markdown.hithere') }}</code>
          </td>
          <td>
            <span class="syntax-text">
              <span class="">{{ $t('common.markdown.highlightsyntax') }}</span
              >{{ $t('common.markdown.hithere')
              }}<span class="">{{
                $t('common.markdown.highlightsyntax')
              }}</span>
            </span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(highlight)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <span class="element-list">{{ $t('common.markdown.hr') }}</span>
          </td>
          <td><hr /></td>
          <td>
            <span class="syntax-text"> <span class="">---</span></span>
          </td>
          <td>
            <div class="syntax-copy">
              <button @click="copyLinkName(hr)">
                <img
                  src="~assets/copy.svg"
                  class="pull-right pointer"
                  style="height:25px;width:23px"
                />
              </button>
            </div>
          </td>
        </tr>
      </table>
    </div>
  </FModal>
</template>

<script>
import { FModal } from '@facilio/design-system'
export default {
  name: 'MarkdownList',
  props: ['markdownshow'],
  components: { FModal },
  data() {
    return {
      bold: '**Hi There**',
      h1: '# Hi There',
      h2: '## Hi There',
      italics: '*Hi There*',
      strike: '~~Hi There~~',
      underline: '++Hi There++',
      list: '- Hi There',
      blockquote: '> Hi There',
      hyperlink: '[Hi There](URL)',
      highlight: '`Hi There`',
      hr: '---',
    }
  },
  methods: {
    cancelForm() {
      this.$emit('update:markdownshow', false)
    },
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied to clipboard!',
        type: 'success',
      })
    },
  },
}
</script>
