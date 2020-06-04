package me.unc.ldms.controller;

import me.unc.ldms.dto.Distribution;
import me.unc.ldms.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description 配送服务控制层
 * @Date 2020/2/10 18:34
 * @author LZS
 * @version v1.0
 */
@RestController
@RequestMapping("/distribution")
public class DistributionController {

    @Autowired
    private DistributionService distributionService;

    @GetMapping("/trackingMsg")
    public List<Map<String, String>> getTrackingMsg(String oid) {
        return distributionService.distributionTracking(oid);
    }

    @GetMapping("/distributionMsg")
    public Map<String, Object> getDistributionMsg(String oid) {
        return distributionService.getDistributionMsg(oid);
    }

    @GetMapping("/orderDistributionMsg")
    public Distribution getOrderDistributionMsg(String oid) {
        return distributionService.getOrderDistributionMsg(oid);
    }

}
