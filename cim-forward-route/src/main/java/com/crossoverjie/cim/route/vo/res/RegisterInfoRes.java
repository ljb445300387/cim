package com.crossoverjie.cim.route.vo.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 21:54
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterInfoRes implements Serializable {
    private Long userId;
    private String userName;
}
