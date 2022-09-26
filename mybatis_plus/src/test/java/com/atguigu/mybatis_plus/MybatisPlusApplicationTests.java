package com.atguigu.mybatis_plus;

import com.atguigu.mybatis_plus.mapper.StudentMapper;
import com.atguigu.mybatis_plus.pojo.Student;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class MybatisPlusApplicationTests {

    @Autowired
    private StudentMapper studentMapper;

    //查询student表所有数据
    @Test
    public void findAll() {
//        System.out.println("hello");
        List<Student> students = studentMapper.selectList(null);
        System.out.println(students);
    }

    @Test
    public void addStudent() {
        Student student = new Student();
        student.setPhone("13567489");
        student.setSex("男");
        student.setStudentno("EW465654");
        student.setSname("蔡徐坤");
        int insert = studentMapper.insert(student);
        System.out.println(insert);
    }

    //修改操作
    @Test
    public void updateStudent(){
        Student student = new Student();
        student.setId(5L);
        student.setSex("nn");
        int i = studentMapper.updateById(student);
        System.out.println("update:" +i );
    }

    //测试自动填充
    @Test
    public void addAuto(){
        Student student = new Student();
        student.setPhone("13547459");
        student.setSex("男");
        student.setStudentno("Edwwd54");
        student.setSname("蔡uiji坤");
        int insert = studentMapper.insert(student);
        System.out.println(insert);
    }

    //测试乐观锁
    @Test
    public void testOption(){
        //先查询
        Student student = studentMapper.selectById(5L);
        //进行修改
        student.setSex("ka");
        studentMapper.updateById(student);
    }

    //查询多人id
    @Test
    public void testSelectDemo(){
        List<Student> students = studentMapper.selectBatchIds(Arrays.asList(1L, 2L, 3L));
        System.out.println(students);
    }

    //条件查询
    @Test
    public void testSelectMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("sname","张明");
        map.put("entrance",789);
        List<Student> students = studentMapper.selectByMap(map);
        System.out.println(students);
    }

    //分页查询
    @Test
    public void testPage(){
        //1. 创建Page对象
        //传入两个参数：当前页和每页记录数
        Page<Student> page = new Page<>(1,3);
        //调用分页查询的方法:（参数）Page对象和条件
        //所有分页数据会被封装到page对象中
        studentMapper.selectPage(page, null);

        //通过page对象获得分页数据
        System.out.println(page.getCurrent());  //当前页
        System.out.println(page.getRecords());  //每页数据list集合
        System.out.println(page.getPages());    //总页数
        System.out.println(page.getSize());     //每页显示记录数
        System.out.println(page.getTotal());    //总记录数
        System.out.println(page.hasNext());     //是否有下一页
        System.out.println(page.hasPrevious()); //是否有上一页
    }

    //删除操作
    @Test
    public void testDeleteById(){
        int i = studentMapper.deleteById(1L);
        System.out.println(i);
    }

    //批量删除
    @Test
    public void testDeleteMore(){
        int result = studentMapper.deleteBatchIds(Arrays.asList(1L, 2L, 3L));
        System.out.println(result);
    }

    //逻辑删除(配置好后，就是物理删除的代码)
    @Test
    public void testDelete(){
        int i = studentMapper.deleteById(5L);
        System.out.println(i);
    }

    //复杂查询
    @Test
    public void testMid(){
        //创建QueryWrapper对象
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        //通过querywrapper设置查询条件
        //ge 大于等于，gt 大于，le 小于等于，lt小于
//        wrapper.ge("entrance",100);
//        wrapper.lt("entrance",50);
        //eq等于，ne不等于
//        wrapper.eq("sname","蔡徐坤");
//        wrapper.ne("sname","蔡徐坤");
        //between位于之间，notBetween不位于之间
        wrapper.between("entrance",25,800);
//        wrapper.notBetween("entrance",25,800);
        //like模糊查询，notlike,likeLeft,likeRight
//        wrapper.like("sname","蔡");
//        wrapper.likeLeft("sname","明");
//        wrapper.likeRight("sname","许");
        //orderBy,orderByDesc升序排列,orderByAsc降序排列
        wrapper.orderByDesc("id");
//        wrapper.orderByAsc("id");
        //last拼接部分语句到sql最后
//        wrapper.last("limit 1"); //limit n : 查n条语句
        //查询指定列
        wrapper.select("id","sname");
        //输出结果
        List<Student> students = studentMapper.selectList(wrapper);
        System.out.println(students);
        //allEq

    }

}
