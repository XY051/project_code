package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.PortalConfig;
import com.example.backend.service.IPortalConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description 门户配置控制层
 * @Version 1.0
 */
@Tag(name = "门户配置管理")
@RestController
@RequestMapping("/portalConfig")
public class PortalConfigController {

    @Resource
    private IPortalConfigService portalConfigService;

    @PostMapping("/add")
    @Operation(summary = "新增门户配置")
    public BaseResponse<Integer> add(@RequestBody PortalConfig request) {
        Integer res = portalConfigService.add(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    @Operation(summary = "删除门户配置")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = portalConfigService.del(id);
        return Result.success(res);
    }

    @PostMapping("/edit")
    @Operation(summary = "编辑门户配置")
    public BaseResponse<Boolean> edit(@RequestParam Integer id, @RequestBody PortalConfig request) {
        Boolean res = portalConfigService.edit(id, request);
        return Result.success(res);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询门户配置")
    public BaseResponse<PageResult> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String configKey) {
        PageResult res = portalConfigService.page(pageNum, pageSize, configKey);
        return Result.success(res);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有门户配置")
    public BaseResponse<List<PortalConfig>> list() {
        List<PortalConfig> res = portalConfigService.list();
        return Result.success(res);
    }

    @GetMapping("/map")
    @Operation(summary = "获取所有配置Key-Value映射（供前端门户首页使用）")
    public BaseResponse<Map<String, String>> map() {
        Map<String, String> res = portalConfigService.listAllConfigMap();
        return Result.success(res);
    }
}