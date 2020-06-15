package io.interviewready.profile.database;


import com.mysql.cj.jdbc.MysqlDataSource;
import io.interviewready.profile.models.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class DBClient {

    private final Connection connection;

    @Autowired
    public DBClient() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost/profile_db");
        dataSource.setUser("gaurav");
        dataSource.setPassword("gaurav");
        connection = dataSource.getConnection();
    }

    public void addProfile(final Profile profile) {
        try {
            final PreparedStatement insertProfile = connection.prepareStatement("insert into profiles(id,first_name,last_name,password,imageUrl) values (?,?,?,?,?)");
            insertProfile.setString(1, profile.getUserId());
            insertProfile.setString(2, profile.getFirstName());
            insertProfile.setString(3, profile.getLastName());
            insertProfile.setString(4, profile.getPassword());
            insertProfile.setString(5, profile.getImageUrl());
            insertProfile.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public Profile getProfile(final String id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from profiles where id=?");
            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return new Profile(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }
}
