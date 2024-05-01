package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.req.ReqOrgUser;
import cn.com.mfish.common.oauth.req.ReqSsoOrg;
import cn.com.mfish.common.oauth.service.SsoOrgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.3.0
 */
@Slf4j
@Tag(name = "组织结构表")
@RestController
@RequestMapping("/org")
public class SsoOrgController {
    @Resource
    private SsoOrgService ssoOrgService;

    /**
     * 分页列表查询
     *
     * @param reqSsoOrg
     * @param reqPage
     * @return
     */
    @Operation(summary = "组织结构表-分页列表查询", description = "组织结构表-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:org:query")
    public Result<PageResult<SsoOrg>> queryPageList(ReqSsoOrg reqSsoOrg, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return Result.ok(new PageResult<>(ssoOrgService.list(new LambdaQueryWrapper<SsoOrg>()
                .eq(SsoOrg::getDelFlag, 0)
                .like(!StringUtils.isEmpty(reqSsoOrg.getOrgName()), SsoOrg::getOrgName, reqSsoOrg.getOrgName())
                .eq(!StringUtils.isEmpty(reqSsoOrg.getTenantId()), SsoOrg::getTenantId, reqSsoOrg.getTenantId())
                .like(!StringUtils.isEmpty(reqSsoOrg.getLeader()), SsoOrg::getLeader, reqSsoOrg.getLeader())
                .like(!StringUtils.isEmpty(reqSsoOrg.getPhone()), SsoOrg::getPhone, reqSsoOrg.getPhone())
                .eq(reqSsoOrg.getStatus() != null, SsoOrg::getStatus, reqSsoOrg.getStatus())
        )), "组织结构表-查询成功!");
    }

    @Operation(summary = "获取组织结构")
    @GetMapping("/tree")
    public Result<List<SsoOrg>> queryOrgTree(ReqSsoOrg reqSsoOrg) {
        List<SsoOrg> list = ssoOrgService.queryOrg(reqSsoOrg);
        List<SsoOrg> orgList = new ArrayList<>();
        TreeUtils.buildTree("", list, orgList, SsoOrg.class);
        return Result.ok(orgList, "组织结构表-查询成功!");
    }

    @Operation(summary = "获取组织的角色")
    @GetMapping("/roles/{orgIds}")
    @RequiresPermissions(value = {"sys:account:insert", "sys:tenantUser:insert"})
    public Result<List<UserRole>> queryOrgRoles(@Parameter(name = "orgIds", description = "组织ID") @PathVariable("orgIds") String orgIds) {
        if (StringUtils.isEmpty(orgIds)) {
            return Result.fail(new ArrayList<>(), "错误:组织ID不允许为空");
        }
        return Result.ok(ssoOrgService.getOrgRoles(orgIds.split(",")), "组织角色-查询成功!");
    }

    /**
     * 添加
     *
     * @param ssoOrg 组织
     * @return
     */
    @Log(title = "组织结构表-添加", operateType = OperateType.INSERT)
    @Operation(summary = "组织结构表-添加", description = "组织结构表-添加")
    @PostMapping
    @RequiresPermissions("sys:org:insert")
    public Result<SsoOrg> add(@RequestBody SsoOrg ssoOrg) {
        return ssoOrgService.insertOrg(ssoOrg);
    }

    /**
     * 编辑
     *
     * @param ssoOrg 组织
     * @return
     */
    @Log(title = "组织结构表-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "组织结构表-编辑", description = "组织结构表-编辑")
    @PutMapping
    @RequiresPermissions("sys:org:update")
    public Result<SsoOrg> edit(@RequestBody SsoOrg ssoOrg) {
        return ssoOrgService.updateOrg(ssoOrg);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "组织结构表-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "组织结构表-通过id删除", description = "组织结构表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:org:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return ssoOrgService.removeOrg(id);
    }

    /**
     * 通过id查询
     *
     * @param ids 多个ID逗号分隔
     * @return
     */
    @Operation(summary = "组织结构表-通过id查询", description = "组织结构表-通过id查询")
    @GetMapping("/{ids}")
    @RequiresPermissions("sys:org:query")
    public Result<List<SsoOrg>> queryByIds(@Parameter(name = "ids", description = "唯一性ID") @PathVariable("ids") String ids) {
        return ssoOrgService.queryByIds(ids);
    }

    @Operation(summary = "组织树-通过固定编码查询", description = "组织树-通过固定编码查询")
    @GetMapping("/code/{code}")
    @Parameters({
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true),
    })
    public Result<List<SsoOrg>> queryByFixCode(@Parameter(name = "code", description = "固定编码") @PathVariable("code") String code, @RequestParam String direction) {
        List<SsoOrg> list = ssoOrgService.queryOrgByCode(code, TreeDirection.getDirection(direction));
        return Result.ok(list, "组织结构表-查询成功!");
    }

    @Operation(summary = "获取组织及子组织下的所有用户-通过固定编码查询", description = "获取组织及子组织下的所有用户-通过固定编码查询")
    @GetMapping("/user/{code}")
    public Result<PageResult<UserInfo>> queryUserByCode(@Parameter(name = "code", description = "固定编码") @PathVariable("code") String code, ReqOrgUser reqOrgUser, ReqPage reqPage) {
        return Result.ok(ssoOrgService.queryUserByCode(code, reqOrgUser, reqPage), "组织下用户查询成功");
    }

    /**
     * 获取组织id-通过组织编码查询
     *
     * @param codes 组织编码，多个逗号分隔
     * @return
     */
    @Operation(summary = "获取组织id", description = "获取组织id-通过组织编码查询")
    @GetMapping("/ids")
    @Parameters({
            @Parameter(name = "tenantId", description = "租户ID"),
            @Parameter(name = "codes", description = "组织固定编码", required = true),
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true)
    })
    public Result<List<String>> getOrgIdsByFixCode(String tenantId, String codes, String direction) {
        return ssoOrgService.getOrgIdsByFixCode(tenantId, List.of(codes.split(",")), TreeDirection.getDirection(direction));
    }
}
