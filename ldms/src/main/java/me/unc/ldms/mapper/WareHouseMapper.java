package me.unc.ldms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.unc.ldms.dto.WareHouse;
import org.apache.ibatis.annotations.Select;

/**
 * @Description 仓库信息映射mapper
 * @Date 2020/2/11 11:52
 * @author LZS
 * @version v1.0
 */
public interface WareHouseMapper extends BaseMapper<WareHouse> {

    @Select("select name from warehouse where wid = #{wid}")
    String getNameByWid(String wid);

}
