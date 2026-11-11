package org.example.spr_demo1.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 药品表 drug（实验三号源/库存扣减会用）
 */
@Data
@TableName("drug")
public class Drug {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String drugCode;

    private String drugName;

    private String spec;

    private String unit;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
