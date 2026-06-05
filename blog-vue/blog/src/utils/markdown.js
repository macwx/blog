import MarkdownIt from "markdown-it";
import hljs from "./highlight";
import markdownItSub from "markdown-it-sub";
import markdownItSup from "markdown-it-sup";
import markdownItMark from "markdown-it-mark";
import markdownItAbbr from "markdown-it-abbr";
import markdownItContainer from "markdown-it-container";
import markdownItDeflist from "markdown-it-deflist";
import { full as markdownItEmoji } from "markdown-it-emoji";
import markdownItFootnote from "markdown-it-footnote";
import markdownItIns from "markdown-it-ins";
import markdownItKatexExternal from "markdown-it-katex-external";
import markdownItTaskLists from "markdown-it-task-lists";

export default function markdownToHtml(content) {
  const md = new MarkdownIt({
    html: true,
    linkify: true,
    typographer: true,
    breaks: true,
    highlight: function (str, lang) {
      var d = new Date().getTime();
      if (window.performance && typeof window.performance.now === "function") {
        d += performance.now();
      }
      const codeIndex = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
        /[xy]/g,
        function (c) {
          var r = (d + Math.random() * 16) % 16 | 0;
          d = Math.floor(d / 16);
          return (c == "x" ? r : (r & 0x3) | 0x8).toString(16);
        }
      );
      let html = `<button class="copy-btn iconfont iconfuzhi" type="button" data-clipboard-action="copy" data-clipboard-target="#copy${codeIndex}"></button>`;
      const linesLength = str.split(/\n/).length - 1;
      let linesNum = '<span aria-hidden="true" class="line-numbers-rows">';
      for (let index = 0; index < linesLength; index++) {
        linesNum = linesNum + "<span></span>";
      }
      linesNum += "</span>";
      if (lang == null) {
        lang = "html";
      }
      if (lang && hljs.getLanguage(lang)) {
        const preCode = hljs.highlight(str, { language: lang, ignoreIllegals: true }).value;
        html = html + preCode;
        if (linesLength) {
          html += '<b class="name">' + lang + "</b>";
        }
        return `<pre class="hljs"><code>${html}</code>${linesNum}</pre><textarea style="position: absolute;top: -9999px;left: -9999px;z-index: -9999;" id="copy${codeIndex}">${str.replace(
          /<\/textarea>/g,
          "</textarea>"
        )}</textarea>`;
      }
    },
  })
    .use(markdownItSub)
    .use(markdownItSup)
    .use(markdownItMark)
    .use(markdownItAbbr)
    .use(markdownItContainer)
    .use(markdownItDeflist)
    .use(markdownItEmoji)
    .use(markdownItFootnote)
    .use(markdownItIns)
    .use(markdownItKatexExternal)
    .use(markdownItTaskLists);

  return md.render(content);
}
