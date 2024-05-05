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
 * @description: 卡片盒子中间表
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
@Data
@TableName("t_card_box")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "t_card_box对象 卡片盒子中间表")
public class CardBox extends BaseEntity<String> {

    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    // 这是卡片的母版ID
    @ExcelProperty("卡片ID")
    @Schema(description = "卡片ID")
    private String cardId;
    @ExcelProperty("盒子ID")
    @Schema(description = "盒子ID")
    private String boxId;
}
