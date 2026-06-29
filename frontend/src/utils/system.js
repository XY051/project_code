import Config from "@/config";
import http from "./http";
import { menuList } from "./menu";
import useUserStore from "@/stores/userStore";

/** 获取图片的全路径 */
export const getImageUrl = (url) => {
  return Config.baseUrl + url;
};

/** 权限码缓存（Set<string>） */
let permissionSet = null;

/** 操作名到权限码后缀的映射 */
const actionMap = {
  "新增": "add",
  "编辑": "edit",
  "删除": "del",
  "查看": "list",
  "审核": "audit",
};

/**
 * 从后端加载当前用户的权限码列表，存入缓存
 */
export const loadPermissions = async () => {
  try {
    const res = await http.get("/permission/current");
    if (res.code === 200 && Array.isArray(res.data)) {
      permissionSet = new Set(res.data);
    }
  } catch (e) {
    console.error("权限加载失败:", e);
    permissionSet = null;
  }
};

/**
 * 清空权限缓存（登出时调用）
 */
export const clearPermissionCache = () => {
  permissionSet = null;
};

/**
 * 递归搜索菜单项
 */
const findMenuRecursive = (menus, tableName) => {
  for (const menu of menus) {
    if (menu.tableName === tableName) return menu;
    if (menu.children?.length) {
      const found = findMenuRecursive(menu.children, tableName);
      if (found) return found;
    }
  }
  return null;
};

/**
 * 检查当前角色是否具有指定菜单的操作权限
 * 优先使用后端返回的权限码集合，降级使用 menu.js 硬编码
 */
export const hasPermission = (tableName, value) => {
  // 优先使用从后端加载的权限码
  if (permissionSet && permissionSet.size > 0) {
    const action = actionMap[value] || value;
    const permCode = `${tableName}:${action}`;
    return permissionSet.has(permCode);
  }

  // 降级：使用 menu.js 硬编码权限
  const roleFlag = useUserStore().roleFlag;
  if (!roleFlag) return false;

  try {
    const currentRoleMenus =
      menuList.find((menu) => menu.roleFlag === roleFlag)?.backMenu || [];
    const targetMenu = findMenuRecursive(currentRoleMenus, tableName);
    if (!targetMenu) return false;
    return targetMenu.buttons?.includes(value) || false;
  } catch (error) {
    console.error("权限检查出错:", error);
    return false;
  }
};
