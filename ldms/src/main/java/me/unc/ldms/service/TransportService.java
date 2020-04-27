package me.unc.ldms.service;

import me.unc.ldms.dto.Transport;
import me.unc.ldms.dto.WareHouse;

import java.util.List;

/**
 * @Description 运输服务业务逻辑层接口
 * @Date 2020/4/18 17:49
 * @author LZS
 * @version v1.0
 */
public interface TransportService {

    boolean addTransportPlan(Transport transport);

    boolean updateTransportPlan(Transport transport);

    List<Transport> listAll();

    List<WareHouse> listWHs();

    List<Transport> listWhsPlan(String wid);

}
