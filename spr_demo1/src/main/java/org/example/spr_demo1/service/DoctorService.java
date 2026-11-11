package org.example.spr_demo1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.spr_demo1.entity.Doctor;

public interface DoctorService extends IService<Doctor> {

    /**
     * 多条件分页查询
     *
     * @param keyword 姓名 / 工号 / 手机号 模糊匹配
     * @param deptId  科室筛选
     * @param title   职称筛选
     * @param status  1 出诊 0 停诊
     */
    IPage<Doctor> pageQuery(long pageNum, long pageSize, String keyword, Long deptId, String title, Integer status);

    Doctor getDetail(Long id);

    void create(Doctor doctor);

    void modify(Long id, Doctor doctor);

    void remove(Long id);
}
