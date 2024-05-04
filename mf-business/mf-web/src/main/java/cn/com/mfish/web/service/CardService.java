package cn.com.mfish.web.service;

import cn.com.mfish.web.entity.Card;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 卡片
 * @author: mfish
 * @date: 2024-05-02
 * @version: V1.3.0
 */
public interface CardService extends IService<Card> {

    /**
     * 根据母版ID删除所有卡片
     *
     * @param parentId 母版ID
     * @return 是否删除成功
     */
    boolean deleteByParentId(String parentId);

}
