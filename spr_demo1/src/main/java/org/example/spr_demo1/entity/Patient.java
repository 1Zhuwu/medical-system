package org.example.spr_demo1.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者表 patient
 * 注意：idCard / phone 属于个人敏感信息，列表接口返回时会被脱敏（见 PatientServiceImpl）。
 */
@Data
@TableName("patient")
public class Patient {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "患者档案号不能为空")
    @Size(max = 20, message = "患者档案号长度不能超过 20")
    private String patientNo;

    @NotBlank(message = "患者姓名不能为空")
    @Size(max = 50, message = "患者姓名长度不能超过 50")
    private String patientName;

    @Pattern(regexp = "^[12]$", message = "性别只能是 1（男）或 2（女）")
    private String gender;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String idCard;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Past(message = "出生日期必须早于今天")
    private LocalDate birthDate;

    private String address;

    private String emergencyContact;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "紧急联系人电话格式不正确")
    private String emergencyPhone;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT, select = false)
    private Integer deleted;
}
