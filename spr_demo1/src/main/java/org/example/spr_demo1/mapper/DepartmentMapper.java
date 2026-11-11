package org.example.spr_demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.spr_demo1.entity.Department;

/**
 * 科室 Mapper：继承 BaseMapper 即拥有单表 CRUD，无需手写 SQL。
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
