package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.oauth.cache.temp.ClientTempCache;
import cn.com.mfish.oauth.entity.OAuthClient;
import cn.com.mfish.oauth.service.ClientService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * @author: mfish
 * @date: 2020/2/16 16:11
 */
@Service
public class ClientServiceImpl implements ClientService {
    @Resource
    ClientTempCache clientTempCache;

    /**
     * 根据客户端id从缓存和数据库中获取客户端信息
     *
     * @param clientId 客户端id
     * @return 返回客户端信息
     */
    @Override
    public OAuthClient getClientById(String clientId) {
        return clientTempCache.getFromCacheAndDB(clientId);
    }
}
