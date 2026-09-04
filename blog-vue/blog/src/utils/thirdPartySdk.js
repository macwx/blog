// 第三方 SDK 按需加载工具
// 首页资源瀑布实测：QQ 互联的跨域代理 iframe 约 800ms、验证码 SDK 约 250ms，
// 且微博 wb.js 会持续探测已停服的 api.weibo.com 报连接错误。
// 这些 SDK 只在登录/注册/验证码等低频操作时才需要，改为用到时再注入。
const loadedScripts = {};

function loadScript(src, attrs = {}) {
  if (!loadedScripts[src]) {
    loadedScripts[src] = new Promise((resolve, reject) => {
      const script = document.createElement("script");
      script.src = src;
      Object.keys(attrs).forEach(key => script.setAttribute(key, attrs[key]));
      script.onload = resolve;
      script.onerror = () => {
        delete loadedScripts[src];
        reject(new Error("SDK 加载失败: " + src));
      };
      document.head.appendChild(script);
    });
  }
  return loadedScripts[src];
}

export function loadTencentCaptcha() {
  return loadScript("https://ssl.captcha.qq.com/TCaptcha.js");
}

export function loadQQSdk() {
  return loadScript("https://connect.qq.com/qc_jssdk.js", {
    "data-appid": "102025913",
    "data-redirecturi": "https://www.macw.cc/oauth/login/qq"
  });
}
