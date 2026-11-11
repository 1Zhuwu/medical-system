package org.example.spr_demo1.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.spr_demo1.common.Result;
import org.example.spr_demo1.entity.Patient;
import org.example.spr_demo1.service.PatientService;
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
 * 患者管理接口（列表返回的手机号、身份证已脱敏）
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Resource
    private PatientService patientService;

    @GetMapping
    public Result<IPage<Patient>> page(@RequestParam(defaultValue = "1") long pageNum,
                                       @RequestParam(defaultValue = "10") long pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String gender) {
        return Result.success(patientService.pageQuery(pageNum, pageSize, keyword, gender));
    }

    @GetMapping("/{id}")
    public Result<Patient> detail(@PathVariable Long id) {
        return Result.success(patientService.getDetail(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid Patient patient) {
        patientService.create(patient);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> modify(@PathVariable Long id, @RequestBody @Valid Patient patient) {
        patientService.modify(id, patient);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        patientService.remove(id);
        return Result.success();
    }
}
