package cn.com.mfish.web.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.web.entity.Box;
import cn.com.mfish.web.req.ReqBox;
import cn.com.mfish.web.service.BoxService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 盒子
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
@Slf4j
@Tag(name = "盒子")
@RestController
@RequestMapping("/box")
public class BoxController {

    @Resource
    private BoxService boxService;

    /**
     * 分页列表查询
     *
     * @param reqBox  盒子请求参数
     * @param reqPage 分页参数
     * @return 返回盒子-分页列表
     */
    @Operation(summary = "盒子-分页列表查询", description = "盒子-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:box:query")
    public Result<PageResult<Box>> queryPageList(ReqBox reqBox, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqBox, reqPage)), "盒子-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqBox  盒子请求参数
     * @param reqPage 分页参数
     * @return 返回盒子-分页列表
     */
    private List<Box> queryList(ReqBox reqBox, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<Box> lambdaQueryWrapper = new LambdaQueryWrapper<Box>()
            .eq(null != reqBox.getOnlyTitle(), Box::getOnlyTitle, reqBox.getOnlyTitle());
        lambdaQueryWrapper.orderByAsc(Box::getBoxSort);
        return boxService.list(lambdaQueryWrapper);
    }

    /**
     * 添加
     *
     * @param box 盒子对象
     * @return 返回盒子-添加结果
     */
    @Log(title = "盒子-添加", operateType = OperateType.INSERT)
    @Operation(summary = "盒子-添加")
    @PostMapping
    @RequiresPermissions("sys:box:insert")
    public Result<Box> add(@RequestBody Box box) {
        if (boxService.save(box)) {
            return Result.ok(box, "盒子-添加成功!");
        }
        return Result.fail(box, "错误:盒子-添加失败!");
    }

    /**
     * 编辑
     *
     * @param box 盒子对象
     * @return 返回盒子-编辑结果
     */
    @Log(title = "盒子-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "盒子-编辑")
    @PutMapping
    @RequiresPermissions("sys:box:update")
    public Result<Box> edit(@RequestBody Box box) {
        if (boxService.updateById(box)) {
            return Result.ok(box, "盒子-编辑成功!");
        }
        return Result.fail(box, "错误:盒子-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回盒子-删除结果
     */
    @Log(title = "盒子-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "盒子-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:box:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (boxService.removeById(id)) {
            return Result.ok(true, "盒子-删除成功!");
        }
        return Result.fail(false, "错误:盒子-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回盒子-删除结果
     */
    @Log(title = "盒子-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "盒子-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("sys:box:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        if (this.boxService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "盒子-批量删除成功!");
        }
        return Result.fail(false, "错误:盒子-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回盒子对象
     */
    @Operation(summary = "盒子-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:box:query")
    public Result<Box> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        Box box = boxService.getById(id);
        return Result.ok(box, "盒子-查询成功!");
    }

    /**
     * 导出
     *
     * @param reqBox  盒子请求参数
     * @param reqPage 分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "导出盒子", description = "导出盒子")
    @GetMapping("/export")
    @RequiresPermissions("sys:box:export")
    public void export(ReqBox reqBox, ReqPage reqPage) throws IOException {
        // swagger调用会用问题，使用postman测试
        ExcelUtils.write("Box", queryList(reqBox, reqPage));
    }
}
