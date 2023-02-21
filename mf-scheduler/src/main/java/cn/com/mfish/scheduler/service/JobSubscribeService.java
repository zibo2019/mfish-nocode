package cn.com.mfish.scheduler.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description: 任务订阅表
 * @author: mfish
 * @date: 2023-02-20
 * @version: V1.0.0
 */
public interface JobSubscribeService extends IService<JobSubscribe> {
    Result<JobSubscribe> insertJobSubscribe(JobSubscribe jobSubscribe);

    Result<List<JobSubscribe>> insertJobSubscribes(List<JobSubscribe> jobSubscribeList);

}