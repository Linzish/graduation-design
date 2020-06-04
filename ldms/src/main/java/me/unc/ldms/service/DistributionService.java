package me.unc.ldms.service;

import me.unc.ldms.dto.Distribution;

import java.util.List;
import java.util.Map;

/**
 * @Description 配送服务业务逻辑接口
 * @Date 2020/2/10 18:29
 * @author LZS
 * @version v1.0
 */
public interface DistributionService {

    List<Map<String, String>> distributionTracking(String oid);

    void acceptOrder(String oidKey);

    Map<String, Object> getDistributionMsg(String oid);

    Distribution getOrderDistributionMsg(String oid);

}
