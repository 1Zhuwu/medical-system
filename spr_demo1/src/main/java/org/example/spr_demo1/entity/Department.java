package org.example.spr_demo1.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 科室表 department
 */
@Data
@TableName("department")
public class Department {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 科室编码，业务唯一标识 */
    @NotBlank(message = "科室编码不能为空")
    @Size(max = 20, message = "科室编码长度不能超过 20")
    private String deptCode;

    @NotBlank(message = "科室名称不能为空")
    @Size(max = 50, message = "科室名称长度不能超过 50")
    private String deptName;

    /** 门诊 / 住院 / 医技 / 急诊 */
    private String deptType;

    private String location;

    /** 科室电话，格式如 0571-88001001 */
    @Pattern(regexp = "^$|^[0-9-]{7,20}$", message = "科室电话格式不正确")
    private String phone;

    private String description;

    @Min(value = 0, message = "排序号不能为负数")
    private Integer sortNo;

    @Min(value = 0, message = "状态值只能是 0 或 1")
    @Max(value = 1, message = "状态值只能是 0 或 1")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT, select = false)
    private Integer deleted;
}
