(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-b98c7a08"],{"0222":function(t,a,s){"use strict";var e=s("63d2"),i=s.n(e);i.a},"63d2":function(t,a,s){},bf11:function(t,a,s){"use strict";s.r(a);var e=function(){var t=this,a=t.$createElement,s=t._self._c||a;return s("div",[s("div",{staticClass:"banner",style:t.cover},[s("h1",{staticClass:"banner-title"},[t._v("标签")])]),s("v-card",{staticClass:"blog-container"},[s("div",{staticClass:"tag-cloud-title"},[t._v("标签 - "+t._s(t.count))]),s("div",{staticClass:"tag-cloud"},[s("svg",{attrs:{width:t.width,height:t.height},on:{mousemove:function(a){return t.listener(a)}}},t._l(t.tags,(function(a,e){return s("router-link",{key:a.id,attrs:{to:"/tags/"+a.id}},[s("text",{attrs:{x:a.x,y:a.y,"font-size":600/(600-a.z)*20,"fill-opacity":(400+a.z)/600,fill:t.colors[e]}},[t._v(" "+t._s(a.tagName)+" ")])])})),1)])])],1)},i=[],n=(s("4160"),s("159b"),s("b85c")),o={created:function(){var t=this;this.listTags(),this.changeColors();var a=[];this.axios.get("/api/tags").then((function(s){console.log(s.data),console.log(s.data.data);for(var e=0;e<s.data.data.recordList.length;e++){console.log(s.data.data.recordList[e]);var i={},n=(2*(e+1)-1)/s.data.data.recordList.length-1,o=Math.acos(n),r=o*Math.sqrt(s.data.data.recordList.length*Math.PI);i.tagName=s.data.data.recordList[e].tagName,i.x=t.CX+t.RADIUS*Math.sin(o)*Math.cos(r),i.y=t.CY+t.RADIUS*Math.sin(o)*Math.sin(r),i.z=t.RADIUS*Math.cos(o),a.push(i)}})),console.log(a),this.tags=a},data:function(){return{width:460,height:460,RADIUS:230,speedX:Math.PI/360,speedY:Math.PI/360,tags:[],colors:[],tagList:[],count:0}},methods:{listTags:function(){var t=this;this.axios.get("/api/tags").then((function(a){var s=a.data;t.tagList=s.data.recordList,t.count=s.data.count}))},rotateX:function(t){var a,s=Math.cos(t),e=Math.sin(t),i=Object(n["a"])(this.tags);try{for(i.s();!(a=i.n()).done;){var o=a.value,r=(o.y-this.CY)*s-o.z*e+this.CY,c=o.z*s+(o.y-this.CY)*e;o.y=r,o.z=c}}catch(h){i.e(h)}finally{i.f()}},rotateY:function(t){var a,s=Math.cos(t),e=Math.sin(t),i=Object(n["a"])(this.this.tags);try{for(i.s();!(a=i.n()).done;){var o=a.value,r=(o.x-this.CX)*s-o.z*e+this.CX,c=o.z*s+(o.x-this.CX)*e;o.x=r,o.z=c}}catch(h){i.e(h)}finally{i.f()}},listener:function(t){var a=t.clientX-this.CX,s=t.clientY-this.CY;this.speedX=1e-4*a>0?Math.min(2e-5*this.RADIUS,1e-4*a):Math.max(2e-5*-this.RADIUS,1e-4*a),this.speedY=1e-4*s>0?Math.min(2e-5*this.RADIUS,1e-4*s):Math.max(2e-5*-this.RADIUS,1e-4*s)},changeColors:function(){for(var t=0;t<30;t++){var a=Math.floor(256*Math.random()),s=Math.floor(256*Math.random()),e=Math.floor(256*Math.random());this.colors[t]="rgb("+a+","+s+","+e+")"}}},mounted:function(){var t=this;setInterval((function(){t.rotateX(t.speedX),t.rotateY(t.speedY)}),17)},computed:{CX:function(){return this.width/2},CY:function(){return this.height/2},cover:function(){var t="";return this.$store.state.blogInfo.pageList.forEach((function(a){"tag"==a.pageLabel&&(t=a.pageCover)})),"background: url("+t+") center center / cover no-repeat"}}},r=o,c=(s("0222"),s("2877")),h=s("6544"),l=s.n(h),d=s("b0af"),u=Object(c["a"])(r,e,i,!1,null,"7373b218",null);a["default"]=u.exports;l()(u,{VCard:d["a"]})}}]);