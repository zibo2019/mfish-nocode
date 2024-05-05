package cn.com.mfish.web.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 盒子
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "盒子请求参数")
public class ReqBox {

    // 是否仅作为标题
    @Schema(description = "是否仅作为标题(1=是;0=否)")
    private Integer onlyTitle;

}
