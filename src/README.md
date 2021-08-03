# HomeWork 8

"Account balance" application stores and updates clients' account balances in the database.

Homework objective is to implement balance update logic and make the app a servlet that processes HTTP requests.

Requirements:

1. Account balance servlet should have "/balance" url mapping.

2. Servlet should have two methods:

- "doGet" method: returns **all existing** account balances as a JSON list of account details sorted by amount in **descending** order, 
  or empty list if there are no accounts registered.
  Response example:
  [
  {
  "userId": "someuser@gmail.com",
  "balance": 100
  },
  {
  "userId": "otheruser@gmail.com",
  "balance": 50
  }
  ]
  Empty response example:
  []
  
- "doPost" method: receives transaction request with JSON request body like:
  {
  "userId": "newuser@gmail.com",
  "amount": 1234
  }
  And returns account details:
  {
  "userId": "newuser@gmail.com",
  "balance": 1535
  }
  If userId is not present in the database, a new DB entry should be created,
  otherwise incoming transaction amount should be added to existing balance.
  
Use Transaction class to deserialize "doPost" method request body and AccountDetails class to serialize "doPost" 
and "doGet" response bodies 

Skip all additional corner case scenarios and error handling

See ToDos in the project classes that might give you a clue on implementation details

Check integration tests in AccountBalanceServletTest class to see if implementation is correct

Project structure:
- domain.AccountRepository class: responsible for database communication. Contains methods to get balance list and
  create/update particular balance (these methods require implementation)
- web.AccountBalanceServlet class: the servlet class. Requires additional configuration
- web.model.AccountDetails class: model that should be used as response body data for get and post requests
- web.model.Transaction class: model that should be used to map request body data in post request 