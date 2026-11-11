package org.example.spr_demo1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 处方明细表 prescription_item
 */
@Data
@TableName("prescription_item")
public class PrescriptionItem {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long prescId;

    private Long drugId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal amount;

    private String usageDesc;
}
