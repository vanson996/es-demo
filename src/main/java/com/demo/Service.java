package com.demo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WYX
 * @date 2020/12/16
 */
public class Service {
    public static void main(String[] args) {
        SchoolManager manager = new SchoolManager();
        CollegeManager manager1 = new CollegeManager();
        manager.print(manager1);
    }
}

class Employee {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

class CollegeEmployee {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

class CollegeManager {
    public List<CollegeEmployee> getList() {
        List<CollegeEmployee> list1 = new ArrayList<CollegeEmployee>();
        for (int i = 0; i < 10; i++) {
            CollegeEmployee employee = new CollegeEmployee();
            employee.setId("学院员工 id=" + i);
            list1.add(employee);
        }
        return list1;
    }

    void print() {
        System.out.println("=================学院员工==============");
        List<CollegeEmployee> list = getList();
        for (CollegeEmployee employee : list) {
            System.out.println(employee.getId());
        }
    }
}

class SchoolManager {
    public List<Employee> getList() {
        List<Employee> list1 = new ArrayList<Employee>();
        for (int i = 0; i < 10; i++) {
            Employee employee = new Employee();
            employee.setId("学校总部员工 id=" + i);
            list1.add(employee);
        }
        return list1;
    }

    void print(CollegeManager sub) {
        sub.print();

        System.out.println("==============学校总部员工============");
        List<Employee> list = getList();
        for (Employee employee : list) {
            System.out.println(employee.getId());
        }
    }
}




