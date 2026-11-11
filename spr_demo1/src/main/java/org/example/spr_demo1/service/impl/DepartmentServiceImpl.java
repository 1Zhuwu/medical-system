package org.example.spr_demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.spr_demo1.common.BusinessException;
import org.example.spr_demo1.entity.Department;
import org.example.spr_demo1.entity.Doctor;
import org.example.spr_demo1.mapper.DepartmentMapper;
import org.example.spr_demo1.mapper.DoctorMapper;
import org.example.spr_demo1.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 科室业务实现。
 * 说明：所有条件均通过 LambdaQueryWrapper 生成 "#{...}" 占位符，
 * 由 MyBatis 交给 PreparedStatement 预编译，天然防 SQL 注入；
 * 严禁用字符串拼接拼 SQL。
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Resource
    private DoctorMapper doctorMapper;

    @Override
    public IPage<Department> pageQuery(long pageNum, long pageSize, String keyword, Integer status) {
        Page<Department> page = new Page<>(normalizePageNum(pageNum), normalizePageSize(pageSize));
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            // 组合条件：科室名称 like OR 科室编码 like —— 注意嵌套，避免 OR 破坏其他条件
            wrapper.and(w -> w.like(Department::getDeptName, kw)
                    .or()
                    .like(Department::getDeptCode, kw));
        }
        wrapper.eq(status != null, Department::getStatus, status);
        wrapper.orderByAsc(Department::getSortNo).orderByDesc(Department::getId);
        return this.page(page, wrapper);
    }

    @Override
    public Department getDetail(Long id) {
        Department department = this.getById(id);
        if (department == null) {
            throw new BusinessException(404, "科室不存在或已删除：id=" + id);
        }
        return department;
    }

    @Override
    public void create(Department department) {
        checkCodeUnique(department.getDeptCode(), null);
        if (department.getStatus() == null) {
            department.setStatus(1);
        }
        if (department.getSortNo() == null) {
            department.setSortNo(0);
        }
        department.setId(null);
        this.save(department);
    }

    @Override
    public void modify(Long id, Department department) {
        getDetail(id);
        checkCodeUnique(department.getDeptCode(), id);
        // 主键以路径参数为准，避免请求体里带 id 改到别的记录
        department.setId(id);
        department.setCreateTime(null);
        this.updateById(department);
    }

    @Override
    public void remove(Long id) {
        getDetail(id);
        Long doctorCount = doctorMapper.selectCount(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getDeptId, id));
        if (doctorCount != null && doctorCount > 0) {
            // 外键 ON DELETE RESTRICT 的友好提示：先在业务层拦，比抛数据库异常体验好
            throw new BusinessException("该科室下还有 " + doctorCount + " 位医生，请先转移或删除医生后再删除科室");
        }
        this.removeById(id);
    }

    /** 科室编码唯一性校验；excludeId 用于修改时排除自身 */
    private void checkCodeUnique(String deptCode, Long excludeId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<Department>()
                .eq(Department::getDeptCode, deptCode);
        if (excludeId != null) {
            wrapper.ne(Department::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException("科室编码已存在：" + deptCode);
        }
    }

    private long normalizePageNum(long pageNum) {
        return pageNum < 1 ? 1 : pageNum;
    }

    private long normalizePageSize(long pageSize) {
        if (pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}
