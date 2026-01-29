package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) {

        SellerDao sellerDao = DaoFactory.createSellerDao();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TEST 1: seller findById ===");
        Seller seller = sellerDao.findById(3);
        System.out.println(seller);

        System.out.println("\n=== TEST 2: seller findByDepartment ===");
        Department department = new Department(2, null);
        List<Seller> sellers = sellerDao.findByDepartment(department);
        sellers.forEach(System.out::println);

        System.out.println("\n=== TEST 3: seller findAll ===");
        sellers = sellerDao.findAll();
        sellers.forEach(System.out::println);

        System.out.println("\n=== TEST 4: seller insert ===");
        Seller newSeller = new Seller(null, "Greg", "greg@gmail.com", new Date(), 3000.0, department);
        sellerDao.insert(newSeller);
        System.out.println("Seller inserted, Id: " + newSeller.getId());

        System.out.println("\n=== TEST 5: seller update ===");
        seller = sellerDao.findById(1);
        seller.setName("Martha Waine");
        sellerDao.update(seller);
        System.out.println("Updated completed!");

        System.out.println("\n=== TEST 6: seller delete ===");
        System.out.print("Enter id for delete: ");
        sellerDao.deleteById(scanner.nextInt());
        System.out.println("Delete completed!");

        scanner.close();
    }
}
