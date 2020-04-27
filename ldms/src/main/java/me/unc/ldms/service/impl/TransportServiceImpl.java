package me.unc.ldms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.unc.ldms.dto.Transport;
import me.unc.ldms.dto.WareHouse;
import me.unc.ldms.mapper.TransportMapper;
import me.unc.ldms.mapper.WareHouseMapper;
import me.unc.ldms.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description 运输服务业务逻辑层实现类
 * @Date 2020/4/18 17:51
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Service
public class TransportServiceImpl implements TransportService {

    @Autowired
    private TransportMapper transportMapper;
    @Autowired
    private WareHouseMapper wareHouseMapper;

    /**
     * 添加运输计划
     * @param transport 运输信息实体类
     * @return 是否添加成功
     */
    @Override
    public boolean addTransportPlan(Transport transport) {
        log.info("calling TransportService [addTransportPlan]");
        return transportMapper.insert(transport) == 1;
    }

    /**
     * 修改运输计划
     * @param transport 运输信息实体类
     * @return 是否修改成功
     */
    @Override
    public boolean updateTransportPlan(Transport transport) {
        log.info("calling TransportService [updateTransportPlan]");
        return transportMapper.updateById(transport) == 1;
    }

    /**
     * 查询全部运输信息
     * @return 运输信息列表
     */
    @Override
    public List<Transport> listAll() {
        log.info("calling TransportService [listAll]");
        return transportMapper.selectList(new QueryWrapper<Transport>());
    }

    /**
     * 查询全部仓储点信息
     * @return 仓储点列表
     */
    @Override
    public List<WareHouse> listWHs() {
        log.info("calling TransportService [listWHs]");
        return wareHouseMapper.selectList(new QueryWrapper<WareHouse>());
    }

    /**
     * 根据当前仓储点id列出运输计划
     * @return 运输信息列表
     */
    @Override
    public List<Transport> listWhsPlan(String wid) {
        log.info("calling TransportService [listWhsPlan]");
        return transportMapper.selectList(new QueryWrapper<Transport>().eq("wid", wid));
    }
}
