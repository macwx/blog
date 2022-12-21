<template>
  <v-footer app padless absolute v-if="!this.isMessage">
    <div class="footer-wrap">
      <div>{{ english }}</div>
      <div>{{ chinese }}</div>
      <div>
        ©{{ blogInfo.websiteConfig.websiteCreateTime | year }} -
        {{ new Date().getFullYear() }} By
        {{ blogInfo.websiteConfig.websiteAuthor }}

      <a href="https://beian.miit.gov.cn/" target="_blank">
        {{ blogInfo.websiteConfig.websiteRecordNo }}
      </a>
      </div>

    </div>
  </v-footer>
</template>

<script>
import axios from "axios";

export default {
  data: function() {
    return {
      chinese: "",
      english: ""
    };
  },
  mounted: function() {
    axios
      .get("https://api.vvhan.com/api/en", {
        params: {
          type: "sj"
        }
      })
      .then(({ data }) => {
        this.chinese = data.data.zh;
        this.english = data.data.en;
      });
  },
  computed: {
    isMessage() {
      return this.$route.path === "/message";
    },
    blogInfo() {
      return this.$store.state.blogInfo;
    }
  }
};
</script>

<style scoped>
.footer-wrap {
  width: 100%;
  margin-top: 10px;
  line-height: 1.5;
  position: relative;
  padding: 20px 20px;
  color: #eee;
  font-size: 14px;
  text-align: center;
  background: linear-gradient(-45deg, #ee7752, #ce3e75, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: Gradient 10s ease infinite;
}
.footer-wrap a {
  color: #eee !important;
}
@keyframes Gradient {
  0% {
    background-position: 0 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0 50%;
  }
}
</style>
