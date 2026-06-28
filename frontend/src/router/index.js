 import { createRouter, createWebHistory } from "vue-router";
 
 const routes = [
   {
     path: "/",
     name: "Layout",
     redirect: "/chart",
     component: () => import("../layouts/index.vue"),
     meta: {
       title: "入口",
     },
     children: [
       {
         path: "/home",
         name: "Home",
         component: () => import("@/views/home/home.vue"),
         meta: {
           title: "后台首页",
         },
       },
       {
         path: "/user",
         name: "User",
         component: () => import("@/views/system/user.vue"),
         meta: {
           title: "用户管理",
         },
       },
        {
          path: "/notice",
          name: "Notice",
          component: () => import("@/views/system/notice.vue"),
          meta: {
            title: "新闻公告",
          },
        },
        {
          path: "/banner",
          name: "Banner",
          component: () => import("@/views/system/banner.vue"),
          meta: {
            title: "轮播图管理",
          },
        },
        {
          path: "/person",
          name: "Person",
          component: () => import("@/views/person.vue"),
          meta: {
            title: "个人中心",
          },
        },
        {
          path: "/ichType",
          name: "IchType",
          component: () => import("@/views/system/ichType.vue"),
          meta: {
            title: "分类管理",
          },
        },
        {
          path: "/culturalHeritage",
          name: "CulturalHeritage",
          component: () => import("@/views/system/culturalHeritage.vue"),
          meta: {
            title: "实训实验",
          },
        },
        {
          path: "/video",
          name: "Video",
          component: () => import("@/views/system/video.vue"),
          meta: {
            title: "虚拟仿真管理",
          },
        },
        {
          path: "/article",
          name: "Article",
          component: () => import("@/views/system/article.vue"),
          meta: {
            title: "实训资讯管理",
          },
        },
        {
          path: "/feedback",
          name: "Feedback",
          component: () => import("@/views/system/feedback.vue"),
          meta: {
            title: "反馈管理",
          },
        },
        {
          path: "/collect",
          name: "Collect",
          component: () => import("@/views/system/collect.vue"),
          meta: {
            title: "收藏管理",
          },
        },
        {
          path: "/comment",
          name: "Comment",
          component: () => import("@/views/system/comment.vue"),
          meta: {
            title: "评论管理",
          },
        },
        {
          path: "/activity",
          name: "Activity",
          component: () => import("@/views/system/activity.vue"),
          meta: {
            title: "课程管理",
          },
        },
        {
          path: "/activityApplication",
          name: "ActivityApplication",
          component: () => import("@/views/system/activityApplication.vue"),
          meta: {
            title: "过程结果",
          },
        },
        {
          path: "/chart",
          name: "Chart",
          component: () => import("@/views/system/chart-page.vue"),
          meta: {
            title: "概览统计",
          },
        },
        {
          path: "/efficiency",
          name: "Efficiency",
          component: () => import("@/views/system/efficiency.vue"),
          meta: {
            title: "实训分析",
          },
        },
       {
         path: "/lab",
         name: "Lab",
         component: () => import("../views/system/lab.vue"),
         meta: {
           title: "实验室",
         },
       },
       {
         path: "/labBooking",
         name: "LabBooking",
         component: () => import("../views/system/labBooking.vue"),
         meta: {
           title: "实验室申请",
         },
       },
       {
         path: "/equipment",
         name: "Equipment",
         component: () => import("../views/system/equipment.vue"),
         meta: {
           title: "仿真设备",
         },
       },
       {
         path: "/equipmentMaintenance",
         name: "EquipmentMaintenance",
         component: () => import("../views/system/equipmentMaintenance.vue"),
         meta: {
           title: "设备监控",
         },
       },
       {
         path: "/trainingResource",
         name: "TrainingResource",
         component: () => import("../views/system/trainingResource.vue"),
         meta: {
           title: "实训资源",
         },
       },
       {
         path: "/resourceCategory",
         name: "ResourceCategory",
         component: () => import("../views/system/resourceCategory.vue"),
         meta: {
           title: "资源分类",
         },
       },
       {
         path: "/openApplication",
         name: "OpenApplication",
         component: () => import("../views/system/openApplication.vue"),
         meta: {
           title: "资源开放申请",
         },
       },
       {
         path: "/reservation",
         name: "Reservation",
         component: () => import("../views/system/reservation.vue"),
         meta: {
           title: "预约管理",
         },
       },
       {
         path: "/admin",
         name: "Admin",
         component: () => import("../views/system/admin.vue"),
         meta: {
           title: "系统用户",
         },
       },
       {
         path: "/dataExport",
         name: "DataExport",
         component: () => import("../views/system/placeholder.vue"),
         meta: {
           title: "数据导出",
         },
       },
       {
         path: "/teacherManage",
         name: "TeacherManage",
         component: () => import("../views/system/placeholder.vue"),
         meta: {
           title: "师资管理",
         },
       },
       {
         path: "/teachingPlan",
         name: "TeachingPlan",
         component: () => import("../views/system/placeholder.vue"),
         meta: {
           title: "教学计划",
         },
       },
       {
         path: "/resourceMonitor",
         name: "ResourceMonitor",
         component: () => import("../views/system/placeholder.vue"),
         meta: {
           title: "资源监控",
         },
       },
       {
         path: "/teachingMonitor",
         name: "TeachingMonitor",
         component: () => import("../views/system/placeholder.vue"),
         meta: {
           title: "教学监控",
         },
       },
       {
         path: "/platformSetting",
         name: "PlatformSetting",
         component: () => import("../views/system/placeholder.vue"),
         meta: {
           title: "平台设置",
         },
       },
       {
         path: "/systemLog",
         name: "SystemLog",
         component: () => import("../views/system/placeholder.vue"),
         meta: {
           title: "系统日志",
         },
       },
     ],
   },
   {
     path: "/front",
     name: "Front",
     component: () => import("../views/front-layout/index.vue"),
     redirect: "/front/home",
     meta: {
       title: "前台",
     },
     children: [
       {
         path: "/front/home",
         name: "FrontHome",
         component: () => import("../views/front/home.vue"),
         meta: {
           title: "首页",
         },
       },
       {
         path: "/front/resources",
         name: "FrontResources",
         component: () => import("../views/front/resources.vue"),
         meta: {
           title: "资源中心",
         },
       },
       {
         path: "/front/resourceDetail",
         name: "FrontResourceDetail",
         component: () => import("../views/front/resource-detail.vue"),
         meta: {
           title: "资源详情",
         },
       },
       {
         path: "/front/openShare",
         name: "FrontOpenShare",
         component: () => import("../views/front/open-share.vue"),
         meta: {
           title: "共享开放",
         },
       },
       {
         path: "/front/resourceApply",
         name: "FrontResourceApply",
         component: () => import("../views/front/resource-apply.vue"),
         meta: {
           title: "资源开放申请",
         },
       },
       {
         path: "/front/labReserve",
         name: "FrontLabReserve",
         component: () => import("../views/front/lab-reserve.vue"),
         meta: {
           title: "实训室预约",
         },
       },
       {
         path: "/front/myOpenShare",
         name: "FrontMyOpenShare",
         component: () => import("../views/front/my-open-share.vue"),
         meta: {
           title: "我的共享开放",
         },
       },
       {
         path: "/front/heritage",
         name: "FrontHeritage",
         component: () => import("../views/front/heritage.vue"),
         meta: {
           title: "虚拟仿真资源",
         },
       },
       {
         path: "/front/heritageDetail",
         name: "FrontHeritageDetail",
         component: () => import("../views/front/heritage-detail.vue"),
         meta: {
           title: "资源详情",
         },
       },
       {
         path: "/front/activity",
         name: "FrontActivity",
         component: () => import("../views/front/activity.vue"),
         meta: {
           title: "实训项目",
         },
       },
       {
         path: "/front/activityDetail",
         name: "FrontActivityDetail",
         component: () => import("../views/front/activity-detail.vue"),
         meta: {
           title: "实训项目详情",
         },
       },
       {
         path: "/front/news",
         name: "FrontNews",
         component: () => import("../views/front/news.vue"),
         meta: {
           title: "新闻公告",
         },
       },
       {
         path: "/front/newsDetail",
         name: "FrontNewsDetail",
         component: () => import("../views/front/news-detail.vue"),
         meta: {
           title: "公告详情",
         },
       },
       {
         path: "/front/videos",
         name: "FrontVideos",
         component: () => import("../views/front/videos.vue"),
         meta: {
           title: "宣传视频",
         },
       },
       {
         path: "/front/videosDetail",
         name: "FrontVideosDetail",
         component: () => import("../views/front/videos-detail.vue"),
         meta: {
           title: "虚拟仿真详情",
         },
       },
       {
         path: "/front/feedback",
         name: "FrontFeedback",
         component: () => import("../views/front/feedback.vue"),
         meta: {
           title: "反馈",
         },
       },
       {
         path: "/front/profile",
         name: "FrontProfile",
         component: () => import("../views/front/profile.vue"),
         meta: {
           title: "个人信息",
         },
       },
       {
         path: "/front/myApplications",
         name: "FrontMyApplications",
         component: () => import("../views/front/my-applications.vue"),
         meta: {
           title: "我的报名",
         },
       },
       {
         path: "/front/myCollect",
         name: "FrontMyCollect",
         component: () => import("../views/front/my-collect.vue"),
         meta: {
           title: "我的收藏",
         },
       },
       {
         path: "/front/labBooking",
         name: "FrontLabBooking",
         component: () => import("../views/front/lab-booking.vue"),
         meta: {
           title: "实验室",
         },
       },
     ],
   },
   {
     path: "/login",
     name: "Login",
     component: () => import("../views/login.vue"),
     meta: {
       title: "登录",
     },
   },
   {
     path: "/register",
     name: "Register",
     component: () => import("../views/register.vue"),
     meta: {
       title: "注册",
     },
   },
   {
     path: "/:pathMatch(.*)*",
     name: "NotFound",
     component: () => import("../views/notFound.vue"),
     meta: {
       title: "404",
     },
   },
 ];
 
 const router = createRouter({
   history: createWebHistory(),
   routes,
 });
 
 // 全局前置守卫
 router.beforeEach((to, from, next) => {
   // 设置页面标题
   document.title = to.meta.title || "Vue App";
   next();
 });
 
 export default router;
 
