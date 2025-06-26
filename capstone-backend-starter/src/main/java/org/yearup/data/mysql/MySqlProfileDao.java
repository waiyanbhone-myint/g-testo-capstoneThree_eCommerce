package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao {

    public MySqlProfileDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();
            return profile;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ NEW: Get profile by user ID
    @Override
    public Profile getByUserId(int userId) {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting profile", e);
        }

        return null;
    }

    // ✅ NEW: Update profile
    @Override
    public void update(int userId, Profile profile) {
        String sql = "UPDATE profiles SET first_name = ?, last_name = ?, phone = ?, " +
                "email = ?, address = ?, city = ?, state = ?, zip = ? WHERE user_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, profile.getFirstName());
            statement.setString(2, profile.getLastName());
            statement.setString(3, profile.getPhone());
            statement.setString(4, profile.getEmail());
            statement.setString(5, profile.getAddress());
            statement.setString(6, profile.getCity());
            statement.setString(7, profile.getState());
            statement.setString(8, profile.getZip());
            statement.setInt(9, userId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating profile", e);
        }
    }

    // ✅ Helper method to map ResultSet to Profile
    private Profile mapRow(ResultSet rs) throws SQLException {
        Profile profile = new Profile();
        profile.setUserId(rs.getInt("user_id"));
        profile.setFirstName(rs.getString("first_name"));
        profile.setLastName(rs.getString("last_name"));
        profile.setPhone(rs.getString("phone"));
        profile.setEmail(rs.getString("email"));
        profile.setAddress(rs.getString("address"));
        profile.setCity(rs.getString("city"));
        profile.setState(rs.getString("state"));
        profile.setZip(rs.getString("zip"));
        return profile;
    }
}