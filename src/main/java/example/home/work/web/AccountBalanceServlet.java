package example.home.work.web;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.home.work.domain.AccountRepository;
import example.home.work.web.model.AccountDetails;
import example.home.work.web.model.Transaction;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/balance")
public class AccountBalanceServlet extends HttpServlet {

    private AccountRepository accountRepository;
    private ObjectMapper objectMapper;

    //ToDo: initialize servlet's dependencies
    @Override
    public void init() {
        try {
            accountRepository = new AccountRepository();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        objectMapper = new ObjectMapper();
    }

    //ToDo: implement "get" method that will return all available user balances.
    // Check accountRepository to see what DB communication methods can be used
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        List<AccountDetails> allAccountDetails = accountRepository.getAllAccountDetails();
        String allAccountDetailsAsJson = objectMapper.
        writeValueAsString(allAccountDetails);
        writer.write(allAccountDetailsAsJson);
        writer.close();
    }

    //ToDo: implement "post" method that will update account balance.
    // Check accountRepository to see what DB communication methods can be used
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Transaction transaction = objectMapper.readValue(req.getInputStream(), Transaction.class);
        PrintWriter writer = resp.getWriter();
        try {
            AccountDetails accountDetails = accountRepository.updateBalance(transaction);
            String resultInJson = objectMapper.writeValueAsString(accountDetails);
            writer.write(resultInJson);
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            writer.close();
        }
    }

}