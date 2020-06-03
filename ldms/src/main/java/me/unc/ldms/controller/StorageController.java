package me.unc.ldms.controller;

import me.unc.ldms.dto.Storage;
import me.unc.ldms.response.Result;
import me.unc.ldms.response.ResultBuilder;
import me.unc.ldms.service.StorageService;
import me.unc.ldms.utils.StateType;
import me.unc.ldms.websocket.StorageWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 仓储服务控制层
 * @Date 2020/2/10 18:33
 * @author LZS
 * @version v1.0
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;
    @Autowired
    private StorageWebSocket webSocket;

    /**
     * 入库操作接口
     * @param oid 订单id
     * @param wid 仓库id
     * @return 统一请求回应
     */
    @PostMapping("/inBound")
    public Result inBound(String oid, String wid, String createBy) {
        try {
            if (storageService.storage(wid, oid, createBy)) {
                return ResultBuilder.successResultOnly("入库成功");
            } else {
                return ResultBuilder.failResult("入库失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "入库失败", null);
        }
    }

    /**
     * 出库操作接口
     * @param oid 订单id
     * @param wid 仓库id
     * @return 统一请求回应
     */
    @PostMapping("/outBound")
    public Result outBound(String oid, String wid, String tid, String uid, String createBy) {
        try {
            if (storageService.outbound(wid, oid, tid, uid, createBy)) {
                return ResultBuilder.successResultOnly("出库成功");
            } else {
                return ResultBuilder.failResult("出库失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "出库失败", null);
        }
    }

    /**
     * 根据仓储点获取仓储订单
     * @param wid 仓储点id
     * @return 仓储信息集合
     */
    @GetMapping("/getOrdersByWid")
    public List<Storage> getOrdersByWid(String wid) {
        return storageService.selectWHsStorageMsg(wid);
    }

    @GetMapping("/testwebsocket")
    public Result testWebSocket(String msg) {
        webSocket.broadcast(msg);
        return ResultBuilder.successResultOnly("测试成功");
    }

}
