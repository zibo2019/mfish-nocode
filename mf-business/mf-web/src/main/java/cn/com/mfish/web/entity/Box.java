package cn.com.mfish.web.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 盒子
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
@Data
@TableName("t_box")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "t_box对象 盒子")
public class Box extends BaseEntity<String> {

    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("名称")
    @Schema(description = "名称")
    private String name;
    @ExcelProperty("笔记")
    @Schema(description = "笔记")
    private String note;
    @ExcelProperty("卡片数量")
    @Schema(description = "卡片数量")
    private Integer cardNumber;
    @ExcelProperty("盒子排序")
    @Schema(description = "盒子排序")
    private BigDecimal boxSort;
    @ExcelProperty("是否仅作为标题(1=是;0=否)")
    @Schema(description = "是否仅作为标题(1=是;0=否)")
    private Integer onlyTitle;
    @ExcelProperty("背景色")
    @Schema(description = "背景色")
    private String backgroundColor;
}
