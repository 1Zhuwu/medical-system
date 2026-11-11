package org.example.spr_demo1.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医生表 doctor
 */
@Data
@TableName("doctor")
public class Doctor {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "医生工号不能为空")
    @Size(max = 20, message = "医生工号长度不能超过 20")
    private String doctorNo;

    @NotBlank(message = "医生姓名不能为空")
    @Size(max = 50, message = "医生姓名长度不能超过 50")
    private String doctorName;

    /** 1 男 2 女 */
    @Pattern(regexp = "^[12]$", message = "性别只能是 1（男）或 2（女）")
    private String gender;

    /** 手机号：11 位，1 开头，第二位 3-9 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 身份证号：18 位，最后一位可以是 X */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String idCard;

    @NotNull(message = "所属科室不能为空")
    private Long deptId;

    /** 主任医师 / 副主任医师 / 主治医师 / 住院医师 */
    private String title;

    private String specialty;

    @Size(max = 500, message = "个人简介不能超过 500 字")
    private String introduction;

    @DecimalMin(value = "0.00", message = "挂号费不能为负数")
    private BigDecimal regFee;

    /** 1 出诊 0 停诊 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT, select = false)
    private Integer deleted;

    /** 非数据库字段：列表页展示所属科室名称（关联查询时填充） */
    @TableField(exist = false)
    private String deptName;
}
