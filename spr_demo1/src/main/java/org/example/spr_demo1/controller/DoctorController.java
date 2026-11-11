package org.example.spr_demo1.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.spr_demo1.common.Result;
import org.example.spr_demo1.entity.Doctor;
import org.example.spr_demo1.service.DoctorService;
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
 * 医生管理接口
 * GET    /api/doctors?pageNum=1&pageSize=10&keyword=张&deptId=1&title=主任医师&status=1
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Resource
    private DoctorService doctorService;

    @GetMapping
    public Result<IPage<Doctor>> page(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Long deptId,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) Integer status) {
        return Result.success(doctorService.pageQuery(pageNum, pageSize, keyword, deptId, title, status));
    }

    @GetMapping("/{id}")
    public Result<Doctor> detail(@PathVariable Long id) {
        return Result.success(doctorService.getDetail(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid Doctor doctor) {
        doctorService.create(doctor);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> modify(@PathVariable Long id, @RequestBody @Valid Doctor doctor) {
        doctorService.modify(id, doctor);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        doctorService.remove(id);
        return Result.success();
    }
}
