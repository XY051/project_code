 export const roles = [
   {
     label: "管理员",
     value: "admin",
   },
   {
     label: "用户",
     value: "user",
   },
 ];
 
 export const menuList = [
   {
     roleFlag: "admin",
     name: "管理员",
     backMenu: [
       {
         name: "首页",
         path: "/home",
         icon: "HomeFilled",
         tableName: "",
         buttons: [],
       },
       {
         name: "数据统计",
         path: "/chart",
         icon: "Histogram",
       },
       {
         name: "系统管理",
         path: "/system",
         icon: "Setting",
         children: [
           {
             name: "轮播图管理",
             path: "/banner",
             tableName: "banner",
             buttons: ["新增", "编辑", "删除"],
           },
            {
              name: "实训公告管理",
              path: "/notice",
              tableName: "notice",
              buttons: ["新增", "编辑", "删除"],
            },
          ],
        },
        {
          name: "信息管理",
          path: "/info",
          icon: "Document",
          children: [
            {
              name: "分类管理",
              path: "/ichType",
              tableName: "ichType",
              buttons: ["新增", "编辑", "删除"],
            },
            {
              name: "仿真资源管理",
              path: "/culturalHeritage",
              tableName: "culturalHeritage",
              buttons: ["新增", "编辑", "删除"],
            },
            {
              name: "虚拟仿真管理",
              path: "/video",
              tableName: "video",
              buttons: ["新增", "编辑", "删除"],
            },
            {
              name: "实训资讯管理",
              path: "/article",
              tableName: "article",
              buttons: ["新增", "编辑", "删除"],
            },
            {
              name: "反馈管理",
              path: "/feedback",
              tableName: "feedback",
              buttons: ["删除"],
            },
            {
              name: "收藏管理",
              path: "/collect",
              tableName: "collect",
              buttons: ["删除"],
            },
            {
              name: "评论管理",
              path: "/comment",
              tableName: "comment",
              buttons: ["删除"],
            },
            {
              name: "实训项目管理",
              path: "/activity",
              tableName: "activity",
              buttons: ["新增", "编辑", "删除"],
            },
            {
              name: "实验记录管理",
              path: "/activityApplication",
              tableName: "activityApplication",
              buttons: ["删除", "审核"],
            },
           {
             name: '实验室管理',
             path: "/lab",
             tableName: "lab",
             buttons: ["新增", "编辑", "删除"],
           },
           {
             name: '实验室预约管理',
             path: "/labBooking",
             tableName: "labBooking",
             buttons: ["新增", "编辑", "删除"],
           },
           {
             name: '设备管理',
             path: "/equipment",
             tableName: "equipment",
             buttons: ["新增", "编辑", "删除"],
           },
           {
             name: '设备维护管理',
             path: "/equipmentMaintenance",
             tableName: "equipmentMaintenance",
             buttons: ["新增", "编辑", "删除"],
           },
         ],
       },
       {
         name: "账号管理",
         path: "/account",
         icon: "User",
         children: [
           {
             name: "个人信息",
             path: "/person",
             icon: "",
             tableName: "person",
             buttons: [],
           },
           {
             name: "用户管理",
             path: "/user",
             tableName: "user",
             buttons: ["新增", "编辑", "删除"],
           },
         ],
       },
     ],
   },
 ];
 
