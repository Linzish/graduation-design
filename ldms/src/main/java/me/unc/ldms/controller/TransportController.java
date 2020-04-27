package me.unc.ldms.controller;

import me.unc.ldms.dto.Transport;
import me.unc.ldms.dto.WareHouse;
import me.unc.ldms.response.Result;
import me.unc.ldms.response.ResultBuilder;
import me.unc.ldms.service.TransportService;
import me.unc.ldms.utils.StateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 运输服务控制层
 * @Date 2020/4/18 17:55
 * @author LZS
 * @version v1.0
 */
@RestController
@RequestMapping("/transport")
public class TransportController {

    @Autowired
    private TransportService transportService;

    @GetMapping("/transports")
    public List<Transport> listAll() {
        return transportService.listAll();
    }

    @GetMapping("/whsTransports")
    public List<Transport> listByWhs(String wid) {
        return transportService.listWhsPlan(wid);
    }

    @PostMapping("/transport")
    public Result add(Transport transport) {
        try {
            if (transportService.addTransportPlan(transport)) {
                return ResultBuilder.successResultOnly("添加成功");
            } else {
                return ResultBuilder.failResult("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "添加失败", null);
        }
    }

    @PutMapping("/transport")
    public Result update(Transport transport) {
        try {
            if (transportService.updateTransportPlan(transport)) {
                return ResultBuilder.successResultOnly("修改成功");
            } else {
                return ResultBuilder.failResult("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "修改失败", null);
        }
    }

    @GetMapping("/wareHouses")
    public List<WareHouse> listWHs() {
        return transportService.listWHs();
    }

}
