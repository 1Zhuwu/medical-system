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
 * 电子处方表 prescription（与挂号记录 1:1）
 */
@Data
@TableName("prescription")
public class Prescription {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String prescNo;

    private Long regId;

    private Long patientId;

    private Long doctorId;

    private String diagnosis;

    private BigDecimal totalAmount;

    /** 0 待发药 1 已发药 2 已作废 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
