package model.dao.impl;

import db.DB;
import db.DBException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

    private Connection connection;

    public SellerDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Seller seller) {

    }

    @Override
    public void update(Seller seller) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Seller findById(Integer id) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName " +
                            "FROM seller " +
                            "INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "WHERE seller.id = ?"
            );
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Department department = instantiateDepartment(resultSet);
                return instantiateSeller(resultSet, department);
            }

            return null;

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }

        finally {
            DB.closeStatement(preparedStatement);
            DB.closeResultSet(resultSet);
        }
    }

    @Override
    public List<Seller> findAll() {
        return List.of();
    }

    @Override
    public List<Seller> findByDepartment(Department department) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName " +
                            "FROM seller " +
                            "INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "WHERE Department.id = ? " +
                            "ORDER BY Name"
            );
            preparedStatement.setInt(1, department.getId());
            resultSet = preparedStatement.executeQuery();

            List<Seller> sellers = new ArrayList<>();
            Map<Integer, Department> departmentMap = new HashMap<>();

            while (resultSet.next()) {

                Department dep = departmentMap.get(resultSet.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instantiateDepartment(resultSet);
                    departmentMap.put(resultSet.getInt("DepartmentId"), dep);
                }

                sellers.add(instantiateSeller(resultSet, dep));
            }

            return sellers;

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }

        finally {
            DB.closeStatement(preparedStatement);
            DB.closeResultSet(resultSet);
        }
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
        return new Department(
                resultSet.getInt("DepartmentId"),
                resultSet.getString("DepName")
        );
    }

    private Seller instantiateSeller(ResultSet resultSet, Department department) throws SQLException {
        return new Seller(
                resultSet.getInt("Id"),
                resultSet.getString("Name"),
                resultSet.getString("Email"),
                resultSet.getDate("BirthDate"),
                resultSet.getDouble("BaseSalary"),
                department
        );
    }
}
