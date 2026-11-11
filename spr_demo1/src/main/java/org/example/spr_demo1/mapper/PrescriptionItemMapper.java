package org.example.spr_demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.spr_demo1.entity.PrescriptionItem;

@Mapper
public interface PrescriptionItemMapper extends BaseMapper<PrescriptionItem> {
}
