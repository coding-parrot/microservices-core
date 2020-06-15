package io.interviewready.registry.database;


import com.mysql.cj.jdbc.MysqlDataSource;
import io.interviewready.registry.models.ServiceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class DBClient {
    private final Connection connection;
    private final Logger logger;

    @Autowired
    public DBClient() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost/registry");
        dataSource.setUser("gaurav");
        dataSource.setPassword("gaurav");
        connection = dataSource.getConnection();
        logger = LoggerFactory.getLogger(DBClient.class.getCanonicalName());
    }

    public CompletableFuture<Void> addNode(final ServiceNode node) {
        try {
            final PreparedStatement insertNode = connection.prepareStatement("insert into node(id,ip_address,service_name,port) values (?,?,?,?)");
            insertNode.setString(1, node.getId());
            insertNode.setString(2, node.getIpAddress());
            insertNode.setString(3, node.getServiceName());
            insertNode.setInt(4, node.getPort());
            return CompletableFuture.runAsync(() -> {
                try {
                    insertNode.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).whenComplete((__, throwable) -> {
                if (throwable != null) {
                    logger.error("exception inserting node record ", throwable);
                }
            });
        } catch (SQLException throwable) {
            logger.error("exception building insert statement ", throwable);
            return CompletableFuture.failedFuture(throwable);
        }
    }

    public CompletableFuture<Void> removeNode(final String id) {
        try {
            final PreparedStatement deleteNode = connection.prepareStatement("delete from node where id=?");
            deleteNode.setString(1, id);
            return CompletableFuture.runAsync(() -> {
                try {
                    deleteNode.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).whenComplete((__, throwable) -> {
                if (throwable != null) {
                    logger.error("exception inserting node record ", throwable);
                }
            });
        } catch (SQLException throwable) {
            logger.error("exception building delete statement ", throwable);
            return CompletableFuture.failedFuture(throwable);
        }
    }

    public CompletableFuture<List<ServiceNode>> getServiceNodes(final String methodName) {
        try {
            final PreparedStatement getServiceNodes = connection.prepareStatement("select * from node where service_name = (select service_name from registration where method_name = ?)");
            getServiceNodes.setString(1, methodName);
            return CompletableFuture.supplyAsync(() -> {
                final ResultSet rs;
                try {
                    rs = getServiceNodes.executeQuery();
                    final List<ServiceNode> serviceNodes = new ArrayList<>();
                    while (rs.next()) {
                        serviceNodes.add(new ServiceNode(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
                    }
                    return serviceNodes;
                } catch (SQLException throwable) {
                    logger.error("exception building query ", throwable);
                    throw new RuntimeException(throwable);
                }
            });
        } catch (SQLException throwable) {
            logger.error("exception building query ", throwable);
            return CompletableFuture.failedFuture(throwable);
        }
    }

    public CompletableFuture<List<ServiceNode>> getAllServiceNodes() {
        return CompletableFuture.supplyAsync(() -> {
            final ResultSet rs;
            try {
                rs = connection.prepareStatement("select * from node").executeQuery();
                final List<ServiceNode> serviceNodes = new ArrayList<>();
                while (rs.next()) {
                    serviceNodes.add(new ServiceNode(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
                }
                return serviceNodes;
            } catch (SQLException throwable) {
                logger.error("exception building query ", throwable);
                throw new RuntimeException(throwable);
            }
        });
    }

    public CompletableFuture<Void> register(final String serviceName, final String[] methodNames) {
        try {
            final PreparedStatement registerService = connection.prepareStatement("insert ignore into registration(method_name,service_name) values (?,?)");
            return CompletableFuture.runAsync(() -> {
                try {
                    for (final String methodName : methodNames) {
                        registerService.setString(1, methodName);
                        registerService.setString(2, serviceName);
                        registerService.addBatch();
                    }
                    registerService.executeBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).whenComplete((__, throwable) -> {
                if (throwable != null) {
                    logger.error("exception inserting node record ", throwable);
                }
            });
        } catch (SQLException throwable) {
            logger.error("exception building insert statement ", throwable);
            return CompletableFuture.failedFuture(throwable);
        }
    }
}
