package org.example.spr_demo1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.spr_demo1.entity.Patient;

public interface PatientService extends IService<Patient> {

    /**
     * 多条件分页查询
     *
     * @param keyword 姓名 / 档案号 / 手机号 模糊匹配
     * @param gender  1 男 2 女
     */
    IPage<Patient> pageQuery(long pageNum, long pageSize, String keyword, String gender);

    Patient getDetail(Long id);

    void create(Patient patient);

    void modify(Long id, Patient patient);

    void remove(Long id);
}
