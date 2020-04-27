package me.unc.ldms.service;

import me.unc.ldms.dto.Storage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * @Description 仓储服务业务逻辑接口
 * @Date 2020/2/10 18:31
 * @author LZS
 * @version v1.0
 */
public interface StorageService {

    void routePlanning(ConsumerRecord<String, String> record);

    void collectionNotify(ConsumerRecord<String, String> record);

    boolean storage(String wid, String oid);

    boolean outbound(String wid, String oid, String tid);

    boolean modifyStorageMsg(Storage storage);

    void selectWareHouse(String oid);

    List<String> selectWHsOrder(String wid);

    List<Storage> selectWHsStorageMsg(String wid);

    Storage selectOne(String oid, String wid);

    List<Storage> selectByOid(String oid);

}
