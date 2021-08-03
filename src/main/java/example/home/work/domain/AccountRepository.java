package example.home.work.domain;

import example.home.work.web.model.AccountDetails;
import example.home.work.web.model.Transaction;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;




public class AccountRepository {

    //ToDo: use these credentials to access the embedded in-memory database. No additional configuration required.
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/test";
    private Connection connection;


    public AccountRepository() throws SQLException {
        init();
    }

    //ToDo: add implementation to create DB table on application startup
    private void init() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_URL);
        connection = dataSource.getConnection();

        String createTableScript = "CREATE TABLE IF NOT EXISTS accounts " +
                "(userId VARCHAR(255) not NULL, " +
                "balance INTEGER, " +
                "PRIMARY KEY ( userId ))";
        try(Statement statement = connection.createStatement()) {
            statement.execute(createTableScript);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //ToDo: add implementation to retrieve all available account balances from DB (or empty list if there are no entries)
    public List<AccountDetails> getAllAccountDetails() {
        String query = "SELECT * FROM accounts";
        List<AccountDetails> detailsList = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            while(result.next()) {
                String userId = result.getString("userId");
                int balance = result.getInt("balance");
                AccountDetails accountDetails = new AccountDetails(userId, balance);
                detailsList.add(accountDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        detailsList = sortAccountDetails(detailsList);
        return detailsList;
    }


    public AccountDetails createUser(Transaction transactionRequest) {
        String insertQuery = "INSERT INTO accounts(userId, balance)" +
                "VALUES(?, ?);";
        String selectQuery = "SELECT * FROM accounts WHERE userId = ?" + ";";
        AccountDetails accountDetails = new AccountDetails("", 0);
        try(PreparedStatement statementForInsert = connection.prepareStatement(insertQuery);
            PreparedStatement statementForSelect = connection.prepareStatement(selectQuery)) {
            statementForInsert.setString(1, transactionRequest.getUserId());
            statementForInsert.setInt(2, transactionRequest.getAmount());
            statementForSelect.setString(1, transactionRequest.getUserId());
            statementForInsert.executeUpdate();
            ResultSet result = statementForSelect.executeQuery();
            if(result.next()) {
                String userId = result.getString("userId");
                int balance = result.getInt("balance");
                accountDetails.setUserId(userId);
                accountDetails.setBalance(balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountDetails;
    }

    //ToDo: add implementation to add amount from transactionRequest to player balance, or create a new DB entry if
    // userId from transactionRequest not exists in the DB. It should return AccountDetails with updated balance
    public AccountDetails updateBalance(Transaction transactionRequest) throws SQLException {
        if (!isUserExists(transactionRequest.getUserId())) return createUser(transactionRequest);

        String queryTransfer = "UPDATE accounts SET balance = balance + " + transactionRequest.getAmount()
                + " WHERE userId = ?" + ";";
        String selectQuery = "SELECT * FROM accounts WHERE userId = ?" + ";";
        AccountDetails accountDetails = new AccountDetails("", 0);
        try(PreparedStatement statementForSelect = connection.prepareStatement(selectQuery);
            PreparedStatement statementForUpdate = connection.prepareStatement(queryTransfer)) {
            statementForUpdate.setString(1, transactionRequest.getUserId());
            statementForUpdate.execute();
            statementForSelect.setString(1, transactionRequest.getUserId());
            ResultSet result = statementForSelect.executeQuery();
            if(result.next()) {
                String userId = result.getString("userId");
                int balance = result.getInt("balance");
                accountDetails.setUserId(userId);
                accountDetails.setBalance(balance);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return accountDetails;
    }

    private boolean isUserExists(String userId) {
        boolean isExist = false;
        String query = "SELECT * FROM accounts WHERE userId = ?" + ";";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId);
            ResultSet result = statement.executeQuery();
            isExist = result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isExist;
    }

    private List<AccountDetails> sortAccountDetails(List<AccountDetails> detailsList) {
        return detailsList.stream().sorted(new Comparator<AccountDetails>() {
            @Override
            public int compare(AccountDetails o1, AccountDetails o2) {
                return o2.getBalance() - o1.getBalance();
            }
        }).collect(Collectors.toList());
    }

    private List<AccountDetails> convertResultSetToList(ResultSet resultSet) throws SQLException {
        List<AccountDetails> result = new ArrayList<>();
        while(resultSet.next()) {
            String id = resultSet.getString("userId");
            int balance = resultSet.getInt("balance");
            AccountDetails accountDetails = new AccountDetails(id, balance);
            result.add(accountDetails);
        }
        return result;
    }
}