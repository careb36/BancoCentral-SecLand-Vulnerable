/**
 * Basic unit tests for the banking frontend
 * These tests check core functionality while preserving intended vulnerabilities
 */

describe('Banking Frontend Core Functionality', () => {
  beforeEach(() => {
    // Clear any mocked storage
    sessionStorage.clear();
    localStorage.clear();
    
    // Reset DOM
    document.body.innerHTML = `
      <div id="messageContainer"></div>
      <div id="loadingSpinner"></div>
      <div id="loginSection">
        <form id="loginForm">
          <input id="username" type="text">
          <input id="password" type="password">
        </form>
      </div>
    `;
    
    // Reset global state
    window.currentUser = null;
    window.authToken = null;
    window.userAccounts = [];
  });

  describe('Authentication', () => {
    test('should show error on empty login', async () => {
      // Import the showMessage function from app.js
      const { showMessage } = require('./app');
      
      const loginForm = document.getElementById('loginForm');
      const event = { preventDefault: jest.fn() };
      
      // Submit empty form
      await handleLogin(event);
      
      // Check error message
      const messages = document.querySelectorAll('.message.error');
      expect(messages.length).toBe(1);
      expect(messages[0].textContent).toContain('Please enter your username and password');
    });

    test('should store auth token on successful login', async () => {
      const mockResponse = {
        ok: true,
        json: () => Promise.resolve({
          token: 'fake-jwt-token',
          username: 'testuser',
          fullName: 'Test User'
        })
      };
      
      global.fetch = jest.fn().mockImplementation(() => Promise.resolve(mockResponse));
      
      const loginForm = document.getElementById('loginForm');
      document.getElementById('username').value = 'testuser';
      document.getElementById('password').value = 'password123';
      
      const event = { preventDefault: jest.fn() };
      await handleLogin(event);
      
      // Check storage
      expect(sessionStorage.getItem('authToken')).toBe('fake-jwt-token');
      expect(JSON.parse(sessionStorage.getItem('currentUser'))).toEqual({
        username: 'testuser',
        fullName: 'Test User'
      });
    });
  });

  describe('Account Management', () => {
    test('should prevent transfer with insufficient funds', async () => {
      // Setup mock user and accounts
      window.currentUser = { username: 'testuser' };
      window.userAccounts = [{
        id: 1,
        accountType: 'Savings',
        accountNumber: 'TEST-123',
        balance: 100.00
      }];
      
      document.body.innerHTML += `
        <form id="transferForm">
          <select id="fromAccount">
            <option value="1">TEST-123</option>
          </select>
          <input id="toAccount" value="TEST-456">
          <input id="amount" value="1000.00">
          <input id="description" value="Test transfer">
        </form>
      `;
      
      const event = { preventDefault: jest.fn() };
      await handleTransfer(event);
      
      // Check error message
      const messages = document.querySelectorAll('.message.error');
      expect(messages.length).toBe(1);
      expect(messages[0].textContent).toContain('Insufficient funds');
    });
  });

  describe('Security Vulnerabilities (Intentional)', () => {
    test('should allow XSS in transaction descriptions', () => {
      // VULN: No sanitization of description when displaying
      const transaction = {
        transactionType: 'TRANSFER',
        amount: 100.00,
        description: '<script>alert("XSS")</script>',
        transactionDate: new Date().toISOString()
      };
      
      displayTransactionHistory([transaction]);
      
      const transactionList = document.getElementById('transactionHistoryList');
      expect(transactionList.innerHTML).toContain('<script>alert("XSS")</script>');
    });

    test('should not validate origin account ownership', async () => {
      // VULN: No validation that user owns source account
      const mockResponse = { ok: true, json: () => Promise.resolve({ success: true }) };
      global.fetch = jest.fn().mockImplementation(() => Promise.resolve(mockResponse));
      
      const transferData = {
        fromAccountId: 999, // Account ID that user doesn't own
        toAccountNumber: 'TEST-456',
        amount: 100.00,
        description: 'Unauthorized transfer'
      };
      
      // Should allow transfer request without ownership check
      const response = await fetch('/api/accounts/transfer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(transferData)
      });
      
      expect(response.ok).toBe(true);
    });
  });

  describe('Error Handling', () => {
    test('should handle network errors gracefully', async () => {
      global.fetch = jest.fn().mockImplementation(() => Promise.reject(new Error('Network error')));
      
      const event = { preventDefault: jest.fn() };
      await handleLogin(event);
      
      const messages = document.querySelectorAll('.message.error');
      expect(messages.length).toBe(1);
      expect(messages[0].textContent).toContain('connection');
    });
  });
});
