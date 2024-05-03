package cn.com.mfish.web.service.impl;

import cn.com.mfish.web.entity.Card;
import cn.com.mfish.web.mapper.CardMapper;
import cn.com.mfish.web.service.CardService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @description: 卡片
 * @author: mfish
 * @date: 2024-05-02
 * @version: V1.3.0
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

}
