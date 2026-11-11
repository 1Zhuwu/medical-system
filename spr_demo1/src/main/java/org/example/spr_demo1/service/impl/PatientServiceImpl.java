package org.example.spr_demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.example.spr_demo1.common.BusinessException;
import org.example.spr_demo1.entity.Patient;
import org.example.spr_demo1.entity.Registration;
import org.example.spr_demo1.mapper.PatientMapper;
import org.example.spr_demo1.mapper.RegistrationMapper;
import org.example.spr_demo1.service.PatientService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Resource
    private RegistrationMapper registrationMapper;

    @Override
    public IPage<Patient> pageQuery(long pageNum, long pageSize, String keyword, String gender) {
        Page<Patient> page = new Page<>(pageNum < 1 ? 1 : pageNum, pageSize < 1 ? 10 : Math.min(pageSize, 100));
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(Patient::getPatientName, kw)
                    .or().like(Patient::getPatientNo, kw)
                    .or().like(Patient::getPhone, kw));
        }
        wrapper.eq(StringUtils.hasText(gender), Patient::getGender, gender);
        wrapper.orderByDesc(Patient::getId);

        IPage<Patient> result = this.page(page, wrapper);
        // 列表页对敏感字段脱敏（实验四的安全要求，这里先做掉，避免前端拿到明文手机号/身份证）
        desensitize(result.getRecords());
        return result;
    }

    @Override
    public Patient getDetail(Long id) {
        Patient patient = this.getById(id);
        if (patient == null) {
            throw new BusinessException(404, "患者不存在或已删除：id=" + id);
        }
        return patient;
    }

    @Override
    public void create(Patient patient) {
        checkUnique(patient, null);
        patient.setId(null);
        this.save(patient);
    }

    @Override
    public void modify(Long id, Patient patient) {
        getDetail(id);
        checkUnique(patient, id);
        patient.setId(id);
        patient.setCreateTime(null);
        this.updateById(patient);
    }

    @Override
    public void remove(Long id) {
        getDetail(id);
        Long regCount = registrationMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getPatientId, id));
        if (regCount != null && regCount > 0) {
            throw new BusinessException("该患者存在 " + regCount + " 条挂号记录，不能删除（医疗数据需保留可追溯）");
        }
        this.removeById(id);
    }

    private void checkUnique(Patient patient, Long excludeId) {
        assertNoDuplicate(Patient::getPatientNo, patient.getPatientNo(), excludeId, "患者档案号已存在：");
        assertNoDuplicate(Patient::getPhone, patient.getPhone(), excludeId, "手机号已被其他患者使用：");
        assertNoDuplicate(Patient::getIdCard, patient.getIdCard(), excludeId, "身份证号已被其他患者使用：");
    }

    private void assertNoDuplicate(SFunction<Patient, ?> column, Object value, Long excludeId, String messagePrefix) {
        if (value == null) {
            return;
        }
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<Patient>().eq(column, value);
        if (excludeId != null) {
            wrapper.ne(Patient::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(messagePrefix + value);
        }
    }

    /** 手机号 138****8001；身份证 3301**********1234 */
    private void desensitize(List<Patient> patients) {
        if (patients == null) {
            return;
        }
        patients.forEach(p -> {
            p.setPhone(maskPhone(p.getPhone()));
            p.setIdCard(maskIdCard(p.getIdCard()));
            p.setEmergencyPhone(maskPhone(p.getEmergencyPhone()));
        });
    }

    private String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String maskIdCard(String idCard) {
        if (!StringUtils.hasText(idCard) || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }
}
