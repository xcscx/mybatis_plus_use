package com.atguigu.mybatis_plus.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @TableId(type=IdType.AUTO)
    private Long id;

    private String studentno;

    private String sname;

    private String sex;

    @TableField(fill = FieldFill.INSERT)
    private Date birthdate;

    private int entrance;

    private String phone;

    private String Email;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    @TableLogic
    @TableField(fill=FieldFill.INSERT)
    private Integer deleted;

}
