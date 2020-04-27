package me.unc.ldms.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 用户权限
 * @Date 2020/2/7 17:09
 * @author LZS
 * @version v1.0
 */
@Data
public class Power implements Serializable {

    private List<String> powerList;

}
