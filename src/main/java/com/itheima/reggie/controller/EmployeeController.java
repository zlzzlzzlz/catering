package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService  employeeService;

    /*
    员工登录
    @param 登录
    @param 员工
    md5加密
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 6步
         */
        //1.将页面提交的密码做md5加密处理
        String password =employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交用户命username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp =employeeService.getOne(queryWrapper);

        //查询密码失败
        if (emp==null){
            return R.error("登录失败");
        }

        //4.密码比对
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //查看员工状态
        if(emp.getStatus()==0){
            return R.error("账号禁用");
        }

        //6.登录成功，将员工存入的session并保存，返回登录成功结果

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }
/*
员工退出
 */
    @PostMapping("/logout")

    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    /*
    新增员工
     */
    @PostMapping
    public R<String>save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (long)request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

//page不是随便来的，根据页面泛型来的
    /*
    员工信息分页查询
     */
    @GetMapping("page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize={},name ={}",page,pageSize,name );
        //构造分页配置器
        Page pageInfo =new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper =new LambdaQueryWrapper();
        //添加过滤条件
        if(name!=null){
        queryWrapper.like(Employee::getName,name);}
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

}
