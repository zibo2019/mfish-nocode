package cn.com.mfish.web.service;

import cn.com.mfish.web.entity.Box;
import cn.com.mfish.web.entity.Card;
import cn.com.mfish.web.entity.CardBox;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @description: 卡片盒子中间表
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
public interface CardBoxService extends IService<CardBox> {

    /**
     * 根据卡片ID查询盒子列表
     *
     * @param cardId 卡片ID
     * @return 盒子列表
     */
    List<Box> listBoxByCardId(String cardId);

    /**
     * 根据盒子ID查询卡片列表
     *
     * @param boxId 盒子ID
     * @return 卡片列表
     */
    List<Card> listCardByBoxId(String boxId);

}
