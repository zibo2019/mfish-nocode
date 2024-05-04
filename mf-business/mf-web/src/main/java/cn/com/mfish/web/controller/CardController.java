package cn.com.mfish.web.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.web.entity.Card;
import cn.com.mfish.web.req.ReqCard;
import cn.com.mfish.web.service.CardService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 卡片
 * @author: mfish
 * @date: 2024-05-02
 * @version: V1.3.0
 */
@Slf4j
@Tag(name = "卡片")
@RestController
@RequestMapping("/card")
public class CardController {

    @Resource
    private CardService cardService;

    /**
     * 分页列表查询
     *
     * @param reqCard 卡片请求参数
     * @param reqPage 分页参数
     * @return 返回卡片-分页列表
     */
    @Operation(summary = "卡片-分页列表查询", description = "卡片-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:card:query")
    public Result<PageResult<Card>> queryPageList(ReqCard reqCard, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqCard, reqPage)), "卡片-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqCard 卡片请求参数
     * @param reqPage 分页参数
     * @return 返回卡片-分页列表
     */
    @SuppressWarnings("all")
    private List<Card> queryList(ReqCard reqCard, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<Card> lambdaQueryWrapper = new LambdaQueryWrapper<Card>()
            .eq(null != reqCard.getLatest(), Card::getLatest, reqCard.getLatest())
            .eq(null != reqCard.getDeprecated(), Card::getDeprecated, reqCard.getDeprecated())
            .eq(StringUtils.isNotEmpty(reqCard.getParentId()), Card::getParentId, reqCard.getParentId());
        // 如果存在大版本号，则最早创建的在最上面
        if (null != reqCard.getLatest()) {
            lambdaQueryWrapper.orderByDesc(Card::getCreateTime);
        } else {
            lambdaQueryWrapper.orderByAsc(Card::getCreateTime);
        }
        return cardService.list(lambdaQueryWrapper);
    }

    /**
     * 添加
     *
     * @param card 卡片对象
     * @return 返回卡片-添加结果
     */
    @Log(title = "卡片-添加", operateType = OperateType.INSERT)
    @Operation(summary = "卡片-添加")
    @PostMapping
    @RequiresPermissions("sys:card:insert")
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("all")
    public Result<Card> add(@RequestBody Card card) {
        // 如果母版ID为空，则设置自己的ID为母版ID
        if (StringUtils.isBlank(card.getParentId())) {
            if (cardService.save(card)) {
                card.setParentId(card.getId());
                cardService.updateById(card);
                return Result.ok(card, "卡片-添加成功!");
            }
        } else {
            // 如果有母版ID
            // 如果是升级，则将之前的数据全部废弃
            if (card.getLatest() == 1) {
                LambdaQueryWrapper<Card> lambdaQueryWrapper = new LambdaQueryWrapper<Card>()
                    .eq(Card::getParentId, card.getParentId())
                    .lt(Card::getBigVersion, card.getBigVersion());
                List<Card> list = cardService.list(lambdaQueryWrapper);
                list.forEach(item -> {
                    // 设置为过时
                    item.setDeprecated(1);
                    // 设置为非最新
                    item.setLatest(0);
                });
                cardService.updateBatchById(list);
            }
            if (cardService.save(card)) {
                return Result.ok(card, "卡片-添加成功!");
            }
        }
        return Result.fail(card, "错误:卡片-添加失败!");
    }

    /**
     * 编辑
     *
     * @param card 卡片对象
     * @return 返回卡片-编辑结果
     */
    @Log(title = "卡片-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "卡片-编辑")
    @PutMapping
    @RequiresPermissions("sys:card:update")
    public Result<Card> edit(@RequestBody Card card) {
        if (cardService.updateById(card)) {
            return Result.ok(card, "卡片-编辑成功!");
        }
        return Result.fail(card, "错误:卡片-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回卡片-删除结果
     */
    @Log(title = "卡片-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "卡片-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:card:delete")
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        // 理论上讲，删除一个卡片，应该将所有的子卡片都删除
        // 通过 ID 查询出原数据
        Card card = cardService.getById(id);
        if (cardService.deleteByParentId(card.getParentId())) {
            return Result.ok(true, "卡片-删除成功!");
        }
        return Result.fail(false, "错误:卡片-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回卡片-删除结果
     */
    @Log(title = "卡片-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "卡片-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("sys:card:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        if (this.cardService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "卡片-批量删除成功!");
        }
        return Result.fail(false, "错误:卡片-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回卡片对象
     */
    @Operation(summary = "卡片-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:card:query")
    public Result<Card> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        Card card = cardService.getById(id);
        return Result.ok(card, "卡片-查询成功!");
    }

    /**
     * 导出
     *
     * @param reqCard 卡片请求参数
     * @param reqPage 分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "导出卡片", description = "导出卡片")
    @GetMapping("/export")
    @RequiresPermissions("sys:card:export")
    public void export(ReqCard reqCard, ReqPage reqPage) throws IOException {
        // swagger调用会用问题，使用postman测试
        ExcelUtils.write("Card", queryList(reqCard, reqPage));
    }
}
