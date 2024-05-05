package cn.com.mfish.web.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 卡片盒子中间表
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "卡片盒子中间表请求参数")
public class ReqCardBox {
    @Schema(description = "卡片ID")
    private String cardId;
    @Schema(description = "盒子ID")
    private String boxId;
}
