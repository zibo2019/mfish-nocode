package cn.com.mfish.web.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.web.entity.Box;
import cn.com.mfish.web.entity.Card;
import cn.com.mfish.web.entity.CardBox;
import cn.com.mfish.web.req.ReqCardBox;
import cn.com.mfish.web.service.CardBoxService;
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
 * @description: 卡片盒子中间表
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
@Slf4j
@Tag(name = "卡片盒子中间表")
@RestController
@RequestMapping("/cardBox")
public class CardBoxController {

    @Resource
    private CardBoxService cardBoxService;

    /**
     * 分页列表查询
     *
     * @param reqCardBox 卡片盒子中间表请求参数
     * @param reqPage    分页参数
     * @return 返回卡片盒子中间表-分页列表
     */
    @Operation(summary = "卡片盒子中间表-分页列表查询", description = "卡片盒子中间表-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:cardBox:query")
    public Result<PageResult<CardBox>> queryPageList(ReqCardBox reqCardBox, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqCardBox, reqPage)), "卡片盒子中间表-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqCardBox 卡片盒子中间表请求参数
     * @param reqPage    分页参数
     * @return 返回卡片盒子中间表-分页列表
     */
    private List<CardBox> queryList(ReqCardBox reqCardBox, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<CardBox> lambdaQueryWrapper = new LambdaQueryWrapper<CardBox>()
            .eq(!StringUtils.isEmpty(reqCardBox.getCardId()), CardBox::getCardId, reqCardBox.getCardId())
            .eq(!StringUtils.isEmpty(reqCardBox.getBoxId()), CardBox::getBoxId, reqCardBox.getBoxId());
        return cardBoxService.list(lambdaQueryWrapper);
    }

    /**
     * 添加
     *
     * @param cardBox 卡片盒子中间表对象
     * @return 返回卡片盒子中间表-添加结果
     */
    @Log(title = "卡片盒子中间表-添加", operateType = OperateType.INSERT)
    @Operation(summary = "卡片盒子中间表-添加")
    @PostMapping
    @RequiresPermissions("sys:cardBox:insert")
    public Result<CardBox> add(@RequestBody CardBox cardBox) {
        if (cardBoxService.save(cardBox)) {
            return Result.ok(cardBox, "卡片盒子中间表-添加成功!");
        }
        return Result.fail(cardBox, "错误:卡片盒子中间表-添加失败!");
    }

    /**
     * 编辑
     *
     * @param cardBox 卡片盒子中间表对象
     * @return 返回卡片盒子中间表-编辑结果
     */
    @Log(title = "卡片盒子中间表-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "卡片盒子中间表-编辑")
    @PutMapping
    @RequiresPermissions("sys:cardBox:update")
    public Result<CardBox> edit(@RequestBody CardBox cardBox) {
        if (cardBoxService.updateById(cardBox)) {
            return Result.ok(cardBox, "卡片盒子中间表-编辑成功!");
        }
        return Result.fail(cardBox, "错误:卡片盒子中间表-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回卡片盒子中间表-删除结果
     */
    @Log(title = "卡片盒子中间表-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "卡片盒子中间表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:cardBox:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (cardBoxService.removeById(id)) {
            return Result.ok(true, "卡片盒子中间表-删除成功!");
        }
        return Result.fail(false, "错误:卡片盒子中间表-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回卡片盒子中间表-删除结果
     */
    @Log(title = "卡片盒子中间表-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "卡片盒子中间表-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("sys:cardBox:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        if (this.cardBoxService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "卡片盒子中间表-批量删除成功!");
        }
        return Result.fail(false, "错误:卡片盒子中间表-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回卡片盒子中间表对象
     */
    @Operation(summary = "卡片盒子中间表-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:cardBox:query")
    public Result<CardBox> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        CardBox cardBox = cardBoxService.getById(id);
        return Result.ok(cardBox, "卡片盒子中间表-查询成功!");
    }

    /**
     * 根据盒子ID查询卡片列表
     *
     * @param boxId 盒子ID
     * @return 卡片列表
     */
    @Operation(summary = "盒子-根据盒子ID查询卡片列表")
    @GetMapping("/card/{boxId}")
    @RequiresPermissions("sys:box:query")
    public Result<List<Card>> queryCardListByBoxId(@Parameter(name = "boxId", description = "盒子ID") @PathVariable String boxId) {
        return Result.ok(cardBoxService.listCardByBoxId(boxId), "盒子-查询成功!");
    }

    /**
     * 根据卡片ID查询盒子列表
     *
     * @param cardId 卡片ID
     * @return 盒子列表
     */
    @Operation(summary = "卡片-根据卡片ID查询盒子列表")
    @GetMapping("/box/{cardId}")
    @RequiresPermissions("sys:card:query")
    public Result<List<Box>> listBoxByCardId(@Parameter(name = "cardId", description = "卡片ID") @PathVariable String cardId) {
        return Result.ok(cardBoxService.listBoxByCardId(cardId), "卡片-根据卡片ID查询盒子列表成功!");
    }

    /**
     * 导出
     *
     * @param reqCardBox 卡片盒子中间表请求参数
     * @param reqPage    分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "导出卡片盒子中间表", description = "导出卡片盒子中间表")
    @GetMapping("/export")
    @RequiresPermissions("sys:cardBox:export")
    public void export(ReqCardBox reqCardBox, ReqPage reqPage) throws IOException {
        // swagger调用会用问题，使用postman测试
        ExcelUtils.write("CardBox", queryList(reqCardBox, reqPage));
    }
}
