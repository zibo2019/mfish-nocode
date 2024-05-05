package cn.com.mfish.web.service.impl;

import cn.com.mfish.web.entity.Box;
import cn.com.mfish.web.mapper.BoxMapper;
import cn.com.mfish.web.service.BoxService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @description: 盒子
 * @author: mfish
 * @date: 2024-05-05
 * @version: V1.3.0
 */
@Service
public class BoxServiceImpl extends ServiceImpl<BoxMapper, Box> implements BoxService {
}
