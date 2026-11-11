package org.example.spr_demo1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.spr_demo1.entity.Department;

/**
 * 科室业务接口（实验二：多条件分页查询 + 新增 + 修改 + 删除）
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 多条件分页查询
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页条数
     * @param keyword  关键字，模糊匹配科室名称 / 科室编码
     * @param status   状态：1 启用 0 停用，null 表示不过滤
     */
    IPage<Department> pageQuery(long pageNum, long pageSize, String keyword, Integer status);

    Department getDetail(Long id);

    void create(Department department);

    void modify(Long id, Department department);

    void remove(Long id);
}
