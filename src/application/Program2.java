package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.util.List;
import java.util.Scanner;

public class Program2 {

    public static void main(String[] args) {

        DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TEST 1: department findById ===");
        Department department1 = departmentDao.findById(1);
        System.out.println(department1);

        System.out.println("\n=== TEST 2: department findAll ===");
        List<Department> departments = departmentDao.findAll();
        departments.forEach(System.out::println);

        System.out.println("\n=== TEST 3: department insert ===");
        Department department = new Department(null, "New Department");
        departmentDao.insert(department);
        System.out.println("Department inserted, Id: " + department.getId());

        System.out.println("\n=== TEST 4: department update ===");
        department = departmentDao.findById(1);
        department.setName("updated department");
        departmentDao.update(department);
        System.out.println("Updated completed!");

        System.out.println("\n=== TEST 5: department delete ===");
        System.out.print("Enter id for delete: ");
        departmentDao.deleteById(scanner.nextInt());
        System.out.println("Delete completed!");
    }
}
