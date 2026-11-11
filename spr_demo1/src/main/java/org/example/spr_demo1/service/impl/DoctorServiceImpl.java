package org.example.spr_demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.spr_demo1.common.BusinessException;
import org.example.spr_demo1.entity.Department;
import org.example.spr_demo1.entity.Doctor;
import org.example.spr_demo1.entity.Registration;
import org.example.spr_demo1.mapper.DepartmentMapper;
import org.example.spr_demo1.mapper.DoctorMapper;
import org.example.spr_demo1.mapper.RegistrationMapper;
import org.example.spr_demo1.service.DoctorService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private RegistrationMapper registrationMapper;

    @Override
    public IPage<Doctor> pageQuery(long pageNum, long pageSize, String keyword, Long deptId, String title, Integer status) {
        Page<Doctor> page = new Page<>(pageNum < 1 ? 1 : pageNum, pageSize < 1 ? 10 : Math.min(pageSize, 100));
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(Doctor::getDoctorName, kw)
                    .or().like(Doctor::getDoctorNo, kw)
                    .or().like(Doctor::getPhone, kw));
        }
        wrapper.eq(deptId != null, Doctor::getDeptId, deptId);
        wrapper.eq(StringUtils.hasText(title), Doctor::getTitle, title);
        wrapper.eq(status != null, Doctor::getStatus, status);
        wrapper.orderByDesc(Doctor::getId);

        IPage<Doctor> result = this.page(page, wrapper);
        fillDeptName(result.getRecords());
        return result;
    }

    @Override
    public Doctor getDetail(Long id) {
        Doctor doctor = this.getById(id);
        if (doctor == null) {
            throw new BusinessException(404, "医生不存在或已删除：id=" + id);
        }
        Department department = departmentMapper.selectById(doctor.getDeptId());
        if (department != null) {
            doctor.setDeptName(department.getDeptName());
        }
        return doctor;
    }

    @Override
    public void create(Doctor doctor) {
        checkDeptExists(doctor.getDeptId());
        checkUnique(doctor, null);
        if (doctor.getStatus() == null) {
            doctor.setStatus(1);
        }
        doctor.setId(null);
        this.save(doctor);
    }

    @Override
    public void modify(Long id, Doctor doctor) {
        getDetail(id);
        checkDeptExists(doctor.getDeptId());
        checkUnique(doctor, id);
        doctor.setId(id);
        doctor.setCreateTime(null);
        doctor.setDeptName(null);
        this.updateById(doctor);
    }

    @Override
    public void remove(Long id) {
        getDetail(id);
        Long regCount = registrationMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getDoctorId, id));
        if (regCount != null && regCount > 0) {
            throw new BusinessException("该医生存在 " + regCount + " 条挂号记录，不能直接删除，请改为「停诊」");
        }
        this.removeById(id);
    }

    /** 批量补科室名称，避免列表循环里逐条查库（N+1 问题） */
    private void fillDeptName(List<Doctor> doctors) {
        if (doctors == null || doctors.isEmpty()) {
            return;
        }
        Set<Long> deptIds = doctors.stream()
                .map(Doctor::getDeptId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        if (deptIds.isEmpty()) {
            return;
        }
        Map<Long, String> deptNameMap = departmentMapper.selectBatchIds(deptIds).stream()
                .collect(Collectors.toMap(Department::getId, Department::getDeptName, (a, b) -> a));
        doctors.forEach(d -> d.setDeptName(deptNameMap.get(d.getDeptId())));
    }

    private void checkDeptExists(Long deptId) {
        if (deptId == null || departmentMapper.selectById(deptId) == null) {
            throw new BusinessException("所属科室不存在：deptId=" + deptId);
        }
    }

    /** 工号 / 手机号 / 身份证 三项唯一性校验，给出可读提示 */
    private void checkUnique(Doctor doctor, Long excludeId) {
        assertNoDuplicate(Doctor::getDoctorNo, doctor.getDoctorNo(), excludeId, "医生工号已存在：");
        assertNoDuplicate(Doctor::getPhone, doctor.getPhone(), excludeId, "手机号已被其他医生使用：");
        assertNoDuplicate(Doctor::getIdCard, doctor.getIdCard(), excludeId, "身份证号已被其他医生使用：");
    }

    private void assertNoDuplicate(com.baomidou.mybatisplus.core.toolkit.support.SFunction<Doctor, ?> column,
                                   Object value, Long excludeId, String messagePrefix) {
        if (value == null) {
            return;
        }
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<Doctor>().eq(column, value);
        if (excludeId != null) {
            wrapper.ne(Doctor::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(messagePrefix + value);
        }
    }
}
