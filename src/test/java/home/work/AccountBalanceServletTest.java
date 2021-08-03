package home.work;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import example.home.work.web.AccountBalanceServlet;
import example.home.work.web.model.AccountDetails;
import example.home.work.web.model.Transaction;
import org.h2.jdbc.JdbcSQLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.DelegatingServletInputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountBalanceServletTest {

    private AccountBalanceServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ObjectMapper mapper;

    @Before
    public void initTestData() throws Exception {
        cleanupTestData();
        servlet = new AccountBalanceServlet();
        servlet.init();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        mapper = new ObjectMapper();
    }

    @Test
    public void getBalanceListEmptyResponse() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);
        writer.flush();
        Assert.assertEquals("[]", stringWriter.toString());
    }

    @Test
    public void createAccountBalanceEntry() throws Exception {
        Transaction requestBody = new Transaction("john.doe@gmail.com", 400);
        mockTransactionRequest(requestBody);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);
        writer.flush();

        AccountDetails response = mapper.readValue(stringWriter.toString(), AccountDetails.class);

        Assert.assertEquals(400, response.getBalance());
    }

    @Test
    public void addingAccountBalance() throws Exception {
        Transaction initialRequest = new Transaction("john.davis@gmail.com", 150);
        mockTransactionRequest(initialRequest);

        servlet.doPost(request, response);

        Transaction additionalRequest = new Transaction("john.davis@gmail.com", 400);
        mockTransactionRequest(additionalRequest);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);
        writer.flush();

        AccountDetails response = mapper.readValue(stringWriter.toString(), AccountDetails.class);

        Assert.assertEquals(550, response.getBalance());
    }

    @Test
    public void gettingSingleEntryBalance() throws Exception {
        Transaction transactionRequest = new Transaction("richard.barton@gmail.com", 170);
        mockTransactionRequest(transactionRequest);
        servlet.doPost(request, response);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);
        writer.flush();

        List<AccountDetails> expectedResponse =
                Collections.singletonList(new AccountDetails(transactionRequest.getUserId(), transactionRequest.getAmount()));
        List<AccountDetails> actualResponse = Arrays.asList(mapper.readValue(stringWriter.toString(), AccountDetails[].class));

        Assert.assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void gettingBalanceList() throws Exception {
        Transaction peterRequest = new Transaction("peter.richardson@gmail.com", 90);
        mockTransactionRequest(peterRequest);
        servlet.doPost(request, response);

        Transaction danielRequest = new Transaction("daniel.lewis@gmail.com", 255);
        mockTransactionRequest(danielRequest);
        servlet.doPost(request, response);

        Transaction kennethRequest = new Transaction("kenneth.kane@gmail.com", 130);
        mockTransactionRequest(kennethRequest);
        servlet.doPost(request, response);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);
        writer.flush();

        AccountDetails danielLewisDetails = new AccountDetails(danielRequest.getUserId(), danielRequest.getAmount());
        AccountDetails kennethKaneDetails = new AccountDetails(kennethRequest.getUserId(), kennethRequest.getAmount());
        AccountDetails peterRichardsonDetails = new AccountDetails(peterRequest.getUserId(), peterRequest.getAmount());

        List<AccountDetails> expectedResponse = Arrays.asList(danielLewisDetails, kennethKaneDetails, peterRichardsonDetails);
        List<AccountDetails> actualResponse = Arrays.asList(mapper.readValue(stringWriter.toString(), AccountDetails[].class));

        Assert.assertEquals(actualResponse, expectedResponse);
    }

    private void mockTransactionRequest(Transaction requestBody) throws Exception {
        String requestBodyString = mapper.writeValueAsString(requestBody);
        InputStream inputStream = new ByteArrayInputStream(requestBodyString.getBytes());
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        when(request.getInputStream()).thenReturn(servletInputStream);
        when(request.getReader()).thenReturn(reader);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    private void cleanupTestData() throws Exception {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/test");
        Statement stmt = conn.createStatement();
        stmt.execute("DROP ALL OBJECTS");
        stmt.close();
        conn.close();
    }
}

