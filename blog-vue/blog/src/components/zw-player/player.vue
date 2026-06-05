<template>
  <div>
    <transition name="dis_list">
      <div class="list_box" v-if="listIsDis">
        <transition name="music_alert">
          <span class="music_alert" v-if="musicAlertState">{{
            musicAlertVal
          }}</span>
        </transition>
        <div class="list_close" @click="DisList">x</div>
        <div class="music_list">
          <div class="list_l">
            <ul class="music_type">
              <li
                v-for="(item, index) in musicTypeList"
                :key="index"
                @click="_getMusicType(item.id)"
                :class="{ type_active: item.id == thisMusicType }"
              >
                {{ item.name }}
              </li>
            </ul>
            <div class="list_title">
              <span style="font-size: 14px;">歌曲列表</span>
              <img
                :src="musicStateButton"
                alt=""
                class="music_state"
                @click="MusicStateChange"
              />
              <div class="music_search_box">
                <input
                  type="text"
                  class="music_search"
                  v-model="musicSearchVal"
                  placeholder="搜索歌曲"
                />
                <transition name="music_search">
                  <ul class="search_list" v-if="musicSearchVal != ''">
                    <li
                      v-for="(item, index) in musicSearchList"
                      :key="index"
                      @click="ListAdd(item)"
                    >
                      <span class="music_search_name">{{ item.name }}</span>
                      <span class="music_search_ar">{{
                        item.artists[0].name
                      }}</span>
                    </li>
                  </ul>
                </transition>
              </div>
            </div>
            <div class="music_ul_title">
              <span>歌曲</span><span>歌手</span><span>专辑</span>
            </div>
            <ul class="list">
              <li
                v-for="(item, index) in thisMusicList"
                :key="index"
                @mouseover="ButtonActive(index)"
                @dblclick="ListPlay((thisListPage - 1) * 10 + index)"
              >
                <div
                  class="this_music_shlter"
                  v-if="(thisListPage - 1) * 10 + index == thisMusicIndex"
                ></div>
                <span>{{ item.name }}</span
                ><span>{{ item.ar[0].name }}</span
                ><span>{{ item.al.name }}</span>
                <transition name="list_button">
                  <div
                    class="music_button"
                    v-if="listButtonActiveIndex == index"
                  >
                    <div
                      class="list_play"
                      title="播放这首歌"
                      :style="{ backgroundImage: 'url(' + listPlay + ')' }"
                      @click="ListPlay((thisListPage - 1) * 10 + index)"
                    ></div>
                    <div
                      class="list_play"
                      title="添加到 My Songs"
                      :style="{ backgroundImage: 'url(' + add + ')' }"
                      @click="ListAdd(item)"
                      v-if="thisMusicType != -1"
                    ></div>
                  </div>
                </transition>
              </li>
            </ul>
            <div class="list_page">
              <div
                class="page_last"
                v-if="thisListPage != 1"
                @click="ListChange(true)"
              >
                &lt;
              </div>
              <div
                class="page_next"
                v-if="thisListPage != Math.ceil(musicList.length / 10)"
                @click="ListChange(false)"
              >
                >
              </div>
            </div>
          </div>
          <div class="list_r">
            <img class="music_list_bg" :src="musicImg" />
            <div
              class="music_list_shlter"
              :style="{ backgroundImage: 'url(' + shlter + ')' }"
            ></div>
            <ul class="music_talk_list">
              <li v-for="(item, index) in hotTalkList" :key="index">
                <div class="talk_head">
                  <img
                    :src="item.user.avatarUrl"
                    alt=""
                    class="talk_head_img"
                  />
                  <span class="talk_head_name">{{ item.user.nickname }}</span>
                </div>
                <p class="talk_content">
                  <img class="talk_icon_l" :src="talkicon1" />
                  {{ item.content }}
                  <img class="talk_icon_r" :src="talkicon2" />
                </p>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </transition>
    <div class="bbox" :class="{ bbox_active: disActive }">
      <div
        class="pan"
        :style="{ backgroundImage: 'url(' + pan + ')' }"
        :class="{ pan_active: disActive }"
        @click="DisActive"
      >
        <img :src="musicImg" alt="" class="pan_c" />
      </div>
      <div
        class="box"
        :style="{ backgroundImage: 'url(' + musicImg + ')' }"
        :class="{ box_active: disActive }"
        @dblclick="DisList"
      >
        <div
          class="music_shlter_2"
          :style="{ backgroundImage: 'url(' + musicImg + ')' }"
          :class="{ shlter_active: disActive }"
        ></div>
        <div
          class="music_shlter"
          :style="{ backgroundImage: 'url(' + musicImg + ')' }"
          :class="{ shlter_active: disActive }"
        ></div>
        <div class="music_shlter_3"></div>
        <div class="music_dis">
          <div class="dis_list" @click="DisList">...</div>
          <p class="music_title">{{ musicTitle }}</p>
          <p class="music_intro">歌手: {{ musicName }}</p>
          <ul class="music_words" ref="musicWordsRef">
            <div class="music_words_box" :style="{ top: wordsTop + 'px' }">
              <li
                v-for="(item, index) in musicWords"
                :key="index"
                class="music_word"
                :class="{ word_highlight: wordIndex == index }"
                :ref="el => { if (el) wordEls[index] = el }"
              >
                {{ item }}
              </li>
            </div>
          </ul>
        </div>
        <div class="control_box">
          <div class="control_button">
            <img :src="playIcon" alt="" class="control_icon" ref="controlIconRef" @click="togglePlay" />
          </div>
          <div class="progress" ref="progressRef">
            <div class="progress_c" :style="{ width: currentProgress }">
              <div class="progress_circle">
                <div class="progress_circle_c"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <video
        ref="musicEl"
        autoplay="autoplay"
        :src="musicUrl"
        name="media"
      ></video>
    </div>
  </div>
</template>

<script setup>
import {
  getWords,
  getMusicInfo,
  getMusicUrl,
  getHotMusic,
  getSearchSuggest,
  getHotTalk
} from "./api/music";
import pan from "./img/pan.png";
import play from "./img/play.png";
import pause from "./img/pause.png";
import add from "./img/add.png";
import shlter from "./img/list_pan.png";
import listPlay from "./img/list_play_hover.png";
import state0 from "./img/state_0.png";
import state1 from "./img/state_1.png";
import talkicon1 from "./img/talkicon1.png";
import talkicon2 from "./img/talkicon2.png";
import { ref, reactive, computed, watch, onMounted, onUnmounted } from "vue";

// Template refs
const musicEl = ref(null);
const controlIconRef = ref(null);
const progressRef = ref(null);
const musicWordsRef = ref(null);
const wordEls = reactive({});

const o = ref(0);
const top = ref(0);
const playState = ref(true);
const playIconRef = ref(pause);
const musicImg = ref("");
const musicUrl = ref("");
const musicWords = ref([]);
const musicTitle = ref("");
const musicName = ref("");
const wordsTime = ref([]);
const wordsTop = ref(0);
const wordIndex = ref(0);
const currentProgress = ref("0%");
const musicList = ref([]);
const myMusicList = ref([]);
const thisMusicIndex = ref(1);
const disActive = ref(false);
const listIsDis = ref(false);
const listButtonActiveIndex = ref(-1);
const thisListPage = ref(1);
const musicTypeList = ref([
  { name: "热歌榜", id: 3778678 },
  { name: "新歌榜", id: 3779629 },
  { name: "飙升榜", id: 19723756 },
  { name: "嘻哈榜", id: 991319590 },
  { name: "My Songs", id: -1 }
]);
const thisMusicType = ref(-1);
const notPlay = ref([]);
const musicState = ref(0);
const musicStateButtonRef = ref(state1);
const musicSearchVal = ref("");
const musicSearchList = ref([]);
const musicAlertVal = ref("");
const musicAlertState = ref(false);
let musicAlertTimer = "";
const hotTalkList = ref([]);

// Internal state for Player lifecycle
let playerTimer = null;
let bodyClickHandler = null;
let docMouseMoveHandler = null;
let docMouseUpHandler = null;

const playIcon = computed(() => playIconRef.value);
const musicStateButton = computed(() => musicStateButtonRef.value);

const thisMusicList = computed(() => {
  return [...musicList.value].splice((thisListPage.value - 1) * 10, 10);
});

watch(musicSearchVal, (val) => {
  if (val == "") {
    musicSearchList.value = [];
  } else {
    getSearchSuggest(val).then(res => {
      if (res.data.result.songs == undefined) {
        musicSearchList.value = [];
      } else {
        musicSearchList.value = res.data.result.songs;
      }
    });
  }
});

function DisAuthorInfo() {
  console.log(
    "%c音乐播放器作者----仲威，博客地址：https://blogme.top",
    "background-color:rgb(30,30,30);border-radius:4px;font-size:12px;padding:4px;color:rgb(220,208,129);"
  );
}

function MusicAlert(val) {
  musicAlertState.value = true;
  musicAlertVal.value = val;
  clearTimeout(musicAlertTimer);
  musicAlertTimer = setTimeout(() => {
    musicAlertState.value = false;
    musicAlertVal.value = "";
  }, 2000);
}

function ListAdd(obj) {
  getMusicInfo(obj.id).then(res => {
    musicSearchVal.value = "";
    if (myMusicList.value.length == 0) {
      myMusicList.value = [res.data.songs[0]];
      _getMusicType(-1);
    } else {
      myMusicList.value.push(res.data.songs[0]);
    }
    MusicAlert("添加成功");
  });
}

function MusicStateChange() {
  if (musicState.value == 0) {
    musicState.value = 1;
    musicStateButtonRef.value = state0;
    MusicAlert("已切换为单曲循环模式");
  } else {
    musicState.value = 0;
    musicStateButtonRef.value = state1;
    MusicAlert("已切换为列表循环模式");
  }
}

function DisList() {
  listIsDis.value = !listIsDis.value;
}

function ListChange(isLast) {
  if (isLast) {
    thisListPage.value--;
  } else {
    thisListPage.value++;
  }
}

function ListPlay(id) {
  if (thisMusicIndex.value != id) {
    thisMusicIndex.value = id > musicList.value.length - 1 ? 0 : id;
    _getInfo();
    top.value = 0;
    o.value = 0;
    wordIndex.value = 0;
    wordsTop.value = 0;
    currentProgress.value = "0%";
    if (!playState.value) {
      togglePlay();
    }
  }
}

function ButtonActive(id) {
  listButtonActiveIndex.value = id;
}

function DisActive() {
  disActive.value = !disActive.value;
}

function _getMusicType(id) {
  if (thisMusicType.value != id) {
    notPlay.value = [];
    if (id == -1) {
      if (myMusicList.value.length != 0) {
        musicList.value = myMusicList.value;
        thisMusicType.value = id;
        thisMusicIndex.value = 0;
        thisListPage.value = 1;
        _getInfo();
        top.value = 0;
        o.value = 0;
        wordIndex.value = 0;
        wordsTop.value = 0;
        currentProgress.value = "0%";
        if (!playState.value) {
          togglePlay();
        }
      } else {
        MusicAlert("没有歌曲,需要自己添加");
      }
    } else {
      getHotMusic(id).then(res => {
        musicList.value = res.data.playlist.tracks.splice(0, 200);
        thisMusicType.value = id;
        thisMusicIndex.value = 0;
        thisListPage.value = 1;
        _getInfo();
        top.value = 0;
        o.value = 0;
        wordIndex.value = 0;
        wordsTop.value = 0;
        currentProgress.value = "0%";
        if (!playState.value) {
          togglePlay();
        }
      });
    }
  }
}

function _getInfo() {
  getMusicUrl(musicList.value[thisMusicIndex.value].id).then(res => {
    if (
      res.data.data[0].url == null ||
      res.data.data[0].url == "" ||
      res.data.data[0].url == undefined
    ) {
      if (notPlay.value.length != musicList.value.length) {
        let nextIndex = thisMusicIndex.value + 1;
        if (notPlay.value.indexOf(thisMusicIndex.value) == -1) {
          notPlay.value.push(thisMusicIndex.value);
        }
        MusicAlert(
          `${musicList.value[thisMusicIndex.value].name}因为一些原因不能播放`
        );
        ListPlay(nextIndex);
      } else {
        console.log("not");
        MusicAlert("此列表所有歌都不能播放");
      }
    } else {
      musicUrl.value = res.data.data[0].url.replace("http://", "https://");
      musicImg.value =
        musicList.value[thisMusicIndex.value].al.picUrl.replace(
          "http://",
          "https://"
        ) + "?param=300y300";
      musicTitle.value = musicList.value[thisMusicIndex.value].name;
      let name_arr = [];
      musicList.value[thisMusicIndex.value].ar.forEach(i => {
        name_arr.push(i.name);
      });
      musicName.value = name_arr.join("/");
      getWords(musicList.value[thisMusicIndex.value].id).then(res => {
        if (!res.data.nolyric) {
          let info = Cut(res.data.lrc.lyric);
          musicWords.value = info.wordArr;
          wordsTime.value = info.timeArr;
        }
      });
      getHotTalk(musicList.value[thisMusicIndex.value].id).then(res => {
        let c = 0;
        hotTalkList.value = res.data.hotComments.splice(0, 3);
        hotTalkList.value.forEach(e => {
          c += e.content.length;
          e.user.avatarUrl = e.user.avatarUrl + "?param=50y50";
        });
        if (c >= 200) {
          hotTalkList.value = hotTalkList.value.slice(0, 2);
        }
      });
    }
  });
}

function Ltrim(s) {
  return s.replace(/(^\s*)/g, "");
}

function Rtrim(s) {
  return s.replace(/(\s*$)/g, "");
}

function Cut(str) {
  let timeArr = [];
  let wordArr = [];
  timeArr = str.split("[");
  timeArr.splice(0, 1);
  for (let i = 0; i < timeArr.length; i++) {
    let cut = timeArr[i].split("]");
    let time = cut[0].split(".")[0].split(":");
    timeArr[i] = Number.parseInt(time[0]) * 60 + Number.parseInt(time[1]);
    timeArr[i] = isNaN(timeArr[i]) ? 0 : timeArr[i];
    wordArr[i] = Rtrim(Ltrim(cut[1]));
  }
  return { timeArr: timeArr, wordArr: wordArr };
}

/**
 * Get the height + marginTop of a lyric word element by index.
 * Replaces: $(".music_word").eq(i).height() + parseInt($(".music_word").eq(i).css("marginTop"))
 */
function getWordOffsetHeight(index) {
  const el = wordEls[index];
  if (!el) return 0;
  const height = el.offsetHeight;
  const marginTop = Number.parseInt(getComputedStyle(el).marginTop) || 0;
  return height + marginTop;
}

/**
 * Toggle play/pause. Replaces $(".control_icon").click() and $(".control_icon").on("click", ...)
 */
function togglePlay() {
  const player = musicEl.value;
  if (!player) return;
  if (playState.value) {
    player.pause();
    playState.value = false;
    playIconRef.value = play;
    clearInterval(playerTimer);
  } else {
    player.play();
    playState.value = true;
    playIconRef.value = pause;
    clearInterval(playerTimer);
    playerTimer = setInterval(timer, 1000);
  }
}

/** The shared timer function used by Player() */
function timer() {
  const player = musicEl.value;
  if (!player) return;
  currentProgress.value = `${(player.currentTime / player.duration) * 100}%`;
  if (player.currentTime >= wordsTime.value[o.value + 1]) {
    top.value += getWordOffsetHeight(o.value);
    const wordsHeight = musicWordsRef.value ? musicWordsRef.value.offsetHeight : 0;
    if (top.value >= wordsHeight / 2 - 11) {
      wordsTop.value += -getWordOffsetHeight(o.value);
    }
    wordIndex.value = o.value + 1;
    o.value++;
  }
  if (player.currentTime >= player.duration) {
    if (musicList.value.length != 1) {
      if (musicState.value == 0) {
        thisMusicIndex.value =
          thisMusicIndex.value >= musicList.value.length - 1
            ? 0
            : thisMusicIndex.value + 1;
        _getInfo();
      }
    }
    player.play();
    top.value = 0;
    o.value = 0;
    wordIndex.value = 0;
    wordsTop.value = 0;
    currentProgress.value = "0%";
  }
}

function Player() {
  const player = musicEl.value;
  if (!player) return;

  playerTimer = setInterval(timer, 1000);

  // Autoplay on first body click (browser autoplay policy workaround)
  bodyClickHandler = () => {
    player.play();
    document.body.removeEventListener("click", bodyClickHandler);
    bodyClickHandler = null;
  };
  document.body.addEventListener("click", bodyClickHandler);

  // Progress bar drag interaction
  const progressEl = progressRef.value;
  if (progressEl) {
    progressEl.addEventListener("mousedown", onProgressMouseDown);
  }

  // Register togglePlay on the control icon (already handled via @click in template)
  // but the original code also attached via jQuery .on(), so we ensure it works:
  const controlEl = controlIconRef.value;
  if (controlEl) {
    controlEl.addEventListener("click", togglePlay);
  }
}

function onProgressMouseDown(ev) {
  const player = musicEl.value;
  const progressEl = progressRef.value;
  if (!player || !progressEl) return;

  const e = ev || event;
  let pro =
    (e.clientX - progressEl.getBoundingClientRect().left) / progressEl.getBoundingClientRect().width;
  clearInterval(playerTimer);
  currentProgress.value = `${pro * 100}%`;

  docMouseMoveHandler = (ev) => {
    const e = ev || event;
    pro =
      (e.clientX - progressEl.getBoundingClientRect().left) / progressEl.getBoundingClientRect().width;
    currentProgress.value = `${pro * 100}%`;
  };

  docMouseUpHandler = () => {
    player.currentTime = player.duration * pro;
    let c_arr = [...wordsTime.value];
    c_arr.push(player.currentTime);
    c_arr.sort((l, r) => {
      return l - r;
    });
    let now_o = c_arr.indexOf(player.currentTime) - 1;
    let diff_h = 0;
    if (o.value < now_o) {
      for (let i = o.value; i < now_o; i++) {
        diff_h += -getWordOffsetHeight(i);
      }
    } else {
      for (let i = now_o; i < o.value; i++) {
        diff_h += getWordOffsetHeight(i);
      }
    }
    wordsTop.value += diff_h;
    wordIndex.value = o.value = now_o;
    clearInterval(playerTimer);
    playerTimer = setInterval(timer, 1000);
    playState.value = true;
    playIconRef.value = pause;
    if (player.currentTime >= player.duration) {
      top.value = 0;
      o.value = 0;
      wordIndex.value = 0;
      wordsTop.value = 0;
      currentProgress.value = "0%";
    }
    player.play();
    document.removeEventListener("mousemove", docMouseMoveHandler);
    document.removeEventListener("mouseup", docMouseUpHandler);
    docMouseMoveHandler = null;
    docMouseUpHandler = null;
  };

  document.addEventListener("mousemove", docMouseMoveHandler);
  document.addEventListener("mouseup", docMouseUpHandler);
}

onMounted(() => {
  Player();
  _getMusicType(3779629);
  DisAuthorInfo();
});

onUnmounted(() => {
  clearInterval(playerTimer);
  clearTimeout(musicAlertTimer);
  if (bodyClickHandler) {
    document.body.removeEventListener("click", bodyClickHandler);
    bodyClickHandler = null;
  }
  if (docMouseMoveHandler) {
    document.removeEventListener("mousemove", docMouseMoveHandler);
    docMouseMoveHandler = null;
  }
  if (docMouseUpHandler) {
    document.removeEventListener("mouseup", docMouseUpHandler);
    docMouseUpHandler = null;
  }
  const progressEl = progressRef.value;
  if (progressEl) {
    progressEl.removeEventListener("mousedown", onProgressMouseDown);
  }
  const controlEl = controlIconRef.value;
  if (controlEl) {
    controlEl.removeEventListener("click", togglePlay);
  }
});
</script>

<style scoped>
@import url("./player.css");
@import url("./playermobile.css");
</style>
