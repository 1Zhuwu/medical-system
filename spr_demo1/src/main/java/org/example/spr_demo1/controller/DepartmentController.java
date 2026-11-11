package org.example.spr_demo1.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.spr_demo1.common.Result;
import org.example.spr_demo1.entity.Department;
import org.example.spr_demo1.service.DepartmentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 科室管理接口
 * GET    /api/departments          分页查询（keyword + status 组合条件）
 * GET    /api/departments/{id}     详情
 * POST   /api/departments          新增
 * PUT    /api/departments/{id}     修改
 * DELETE /api/departments/{id}     删除
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @GetMapping
    public Result<IPage<Department>> page(@RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Integer status) {
        return Result.success(departmentService.pageQuery(pageNum, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<Department> detail(@PathVariable Long id) {
        return Result.success(departmentService.getDetail(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid Department department) {
        departmentService.create(department);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> modify(@PathVariable Long id, @RequestBody @Valid Department department) {
        departmentService.modify(id, department);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        departmentService.remove(id);
        return Result.success();
    }
}
