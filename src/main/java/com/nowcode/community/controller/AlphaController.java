package com.nowcode.community.controller;

import com.nowcode.community.Service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data1")
    @ResponseBody
    public String toFindData()
    {
        return alphaService.toFind();
    }


    //GET请求

    //第一种获取参数的方式
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current" , required = false ,defaultValue = "1") int current,
                              @RequestParam(name = "limit", required = false ,defaultValue = "1") int limit)
    {
        return "students";
    }

    //第二种获取参数的方式
    @RequestMapping(path = "/student/{id}" , method = RequestMethod.GET)
    @ResponseBody
    public int getStudent(@PathVariable("id") int id){
        return id;
    }

    //post请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应html
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age","33");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","西南科技大学");
        model.addAttribute("age","90");
        return "/demo/view";
    }

    //响应json
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","华为");
        emp.put("age","60");
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","华为");
        emp.put("age","60");
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","oppo");
        emp.put("age","50");
        list.add(emp);

        return list;
    }

}
