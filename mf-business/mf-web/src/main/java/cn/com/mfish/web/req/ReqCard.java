package cn.com.mfish.web.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 卡片
 * @author: mfish
 * @date: 2024-05-02
 * @version: V1.3.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "卡片请求参数")
public class ReqCard {
    @Schema(description = "是否是最新大版本(1=是;0=否)")
    private Integer latest;
    @Schema(description = "是否已过时(1=是;0=否)")
    private Integer deprecated;
    @Schema(description = "母版ID")
    private String parentId;
}
