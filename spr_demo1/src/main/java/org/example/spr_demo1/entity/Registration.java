package org.example.spr_demo1.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号记录表 registration
 * 实验二只建表 + 建实体，挂号业务放在实验三用 @Transactional 实现。
 */
@Data
@TableName("registration")
public class Registration {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 挂号单号，业务唯一 */
    private String regNo;

    private Long patientId;

    private Long doctorId;

    private Long deptId;

    private LocalDate visitDate;

    /** 上午 / 下午 / 08:00-09:00 */
    private String timeSlot;

    private BigDecimal regFee;

    /** 0 待就诊 1 已就诊 2 已取消 3 爽约 */
    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
