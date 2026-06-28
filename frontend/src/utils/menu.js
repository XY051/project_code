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
        name: "数据概览",
        path: "/dashboard",
        icon: "DataAnalysis",
        children: [
          {
            name: "概览统计",
            path: "/chart",
            tableName: "",
            buttons: [],
          },
          {
            name: "实训分析",
            path: "/efficiency",
            tableName: "",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "数据导出",
            path: "/dataExport",
            tableName: "",
            buttons: [],
          },
        ],
      },
      {
        name: "资源管理",
        path: "/resourceManage",
        icon: "FolderOpened",
        children: [
          {
            name: "实验室",
            path: "/lab",
            tableName: "lab",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "仿真设备",
            path: "/equipment",
            tableName: "equipment",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "实训资源",
            path: "/trainingResource",
            tableName: "trainingResource",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "师资管理",
            path: "/teacherManage",
            tableName: "",
            buttons: [],
          },
          {
            name: "实验室申请",
            path: "/labBooking",
            tableName: "labBooking",
            buttons: ["新增", "编辑", "删除", "审核"],
          },
        ],
      },
      {
        name: "实训管理",
        path: "/trainingManage",
        icon: "Management",
        children: [
          {
            name: "课程管理",
            path: "/activity",
            tableName: "activity",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "实训实验",
            path: "/culturalHeritage",
            tableName: "culturalHeritage",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "教学计划",
            path: "/teachingPlan",
            tableName: "",
            buttons: [],
          },
          {
            name: "过程结果",
            path: "/activityApplication",
            tableName: "activityApplication",
            buttons: ["删除", "审核"],
          },
        ],
      },
      {
        name: "监控管理",
        path: "/monitorManage",
        icon: "Monitor",
        children: [
          {
            name: "设备监控",
            path: "/equipmentMaintenance",
            tableName: "equipmentMaintenance",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "资源监控",
            path: "/resourceMonitor",
            tableName: "",
            buttons: [],
          },
          {
            name: "教学监控",
            path: "/teachingMonitor",
            tableName: "",
            buttons: [],
          },
        ],
      },
      {
        name: "综合管理",
        path: "/generalManage",
        icon: "Operation",
        children: [
          {
            name: "新闻公告",
            path: "/notice",
            tableName: "notice",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "平台人员",
            path: "/user",
            tableName: "user",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "平台设置",
            path: "/platformSetting",
            tableName: "",
            buttons: [],
          },
        ],
      },
      {
        name: "系统信息",
        path: "/systemInfo",
        icon: "Setting",
        children: [
          {
            name: "个人中心",
            path: "/person",
            tableName: "person",
            buttons: [],
          },
          {
            name: "系统用户",
            path: "/admin",
            tableName: "admin",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "数据字典",
            path: "/ichType",
            tableName: "ichType",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "系统日志",
            path: "/systemLog",
            tableName: "",
            buttons: [],
          },
        ],
      },
    ],
  },
];
