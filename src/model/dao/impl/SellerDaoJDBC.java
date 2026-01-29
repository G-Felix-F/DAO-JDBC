package model.dao.impl;

import db.DB;
import db.DBException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
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

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO seller " +
                            "(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.setString(1, seller.getName());
            preparedStatement.setString(2, seller.getEmail());
            preparedStatement.setDate(3, new Date(seller.getBirthDate().getTime()));
            preparedStatement.setDouble(4, seller.getBaseSalary());
            preparedStatement.setInt(5, seller.getDepartment().getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    seller.setId(id);
                }
                DB.closeResultSet(resultSet);
            } else {
                throw new DBException("Expected error! No rows affected!");
            }

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }

        finally {
            DB.closeStatement(preparedStatement);
        }

    }

    @Override
    public void update(Seller seller) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(
                    "UPDATE seller " +
                            "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " +
                            "WHERE Id = ?",
                    Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.setString(1, seller.getName());
            preparedStatement.setString(2, seller.getEmail());
            preparedStatement.setDate(3, new Date(seller.getBirthDate().getTime()));
            preparedStatement.setDouble(4, seller.getBaseSalary());
            preparedStatement.setInt(5, seller.getDepartment().getId());
            preparedStatement.setInt(6, seller.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }

        finally {
            DB.closeStatement(preparedStatement);
        }
    }

    @Override
    public void deleteById(Integer id) {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = connection.prepareStatement(
                    "DELETE FROM seller " +
                            "WHERE Id = ?"
            );
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }

        finally {
            DB.closeStatement(preparedStatement);
        }
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

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName " +
                            "FROM seller " +
                            "INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "ORDER BY Name"
            );
            resultSet = preparedStatement.executeQuery();

            List<Seller> sellers = new ArrayList<>();
            Map<Integer, Department> departmentMap = new HashMap<>();

            while (resultSet.next()) {

                int departmentId = resultSet.getInt("DepartmentId");
                Department dep = departmentMap.get(departmentId);

                if (dep == null) {
                    dep = instantiateDepartment(resultSet);
                    departmentMap.put(departmentId, dep);
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

                int departmentId = resultSet.getInt("DepartmentId");
                Department dep = departmentMap.get(departmentId);

                if (dep == null) {
                    dep = instantiateDepartment(resultSet);
                    departmentMap.put(departmentId, dep);
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
