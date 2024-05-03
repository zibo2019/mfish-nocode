package cn.com.mfish.web.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 卡片
 * @author: mfish
 * @date: 2024-05-02
 * @version: V1.3.0
 */
@Data
@TableName("t_card")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "t_card对象 卡片")
public class Card extends BaseEntity<String> {

    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("标题")
    @Schema(description = "标题")
    private String title;
    @ExcelProperty("内容")
    @Schema(description = "内容")
    private String content;
    @ExcelProperty("背景色")
    @Schema(description = "背景色")
    private String backgroundColor;
    @ExcelProperty("类型(1=大版本;2=小版本)")
    @Schema(description = "类型(1=大版本;2=小版本)")
    private Integer type;
    @ExcelProperty("大版本号")
    @Schema(description = "大版本号")
    private Integer bigVersion;
    @ExcelProperty("小版本号")
    @Schema(description = "小版本号")
    private Integer smallVersion;
    @ExcelProperty("迭代次数")
    @Schema(description = "迭代次数")
    private Integer number;
    @ExcelProperty("是否是最新大版本(1=是;0=否)")
    @Schema(description = "是否是最新大版本(1=是;0=否)")
    private Integer latest;
    @ExcelProperty("是否已过时(1=是;0=否)")
    @Schema(description = "是否已过时(1=是;0=否)")
    private Integer deprecated;
    @ExcelProperty("母版卡片ID")
    @Schema(description = "母版卡片ID")
    private String parentId;
}
