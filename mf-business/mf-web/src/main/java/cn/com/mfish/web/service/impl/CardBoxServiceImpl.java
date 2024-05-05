package cn.com.mfish.web.service.impl;

import cn.com.mfish.web.entity.Box;
import cn.com.mfish.web.entity.Card;
import cn.com.mfish.web.entity.CardBox;
import cn.com.mfish.web.mapper.CardBoxMapper;
import cn.com.mfish.web.service.BoxService;
import cn.com.mfish.web.service.CardBoxService;
import cn.com.mfish.web.service.CardService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @description: 卡片盒子中间表
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
@Service
public class CardBoxServiceImpl extends ServiceImpl<CardBoxMapper, CardBox> implements CardBoxService {

    @Resource
    private BoxService boxService;
    @Resource
    private CardService cardService;

    @Override
    public List<Box> listBoxByCardId(String cardId) {
        // 构建查询 wrapper
        QueryWrapper<CardBox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_id", cardId);
        // 查询
        List<CardBox> cardBoxList = this.list(queryWrapper);
        // 盒子ID列表
        List<String> list = cardBoxList.stream().map(CardBox::getBoxId).toList();
        // 查询盒子列表
        return boxService.listByIds(list);
    }

    @Override
    public List<Card> listCardByBoxId(String boxId) {
        // 构建查询 wrapper
        QueryWrapper<CardBox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("box_id", boxId);
        // 查询
        List<CardBox> cardBoxList = this.list(queryWrapper);
        // 卡片ID列表
        List<String> list = cardBoxList.stream().map(CardBox::getCardId).toList();
        // 查询卡片列表
        return cardService.listByIds(list);
    }
}
