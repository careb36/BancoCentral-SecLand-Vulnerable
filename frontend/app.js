// =====================================================================
// CentralBank SecLand - Frontend Application Logic
// =====================================================================
// Purpose:
//   - Handles all client-side interactions for the banking application
//   - Manages authentication, account operations, and UI state
//   - Communicates with the Spring Boot backend API
// =====================================================================

// Global state management
let currentUser = null;
let authToken = null;
let userAccounts = [];
let allTransactions = [];

// API Base URL - will be proxied through Nginx
const API_BASE = '/api';

// =====================================================================
// INITIALIZATION
// =====================================================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🏦 CentralBank SecLand - Frontend Initialized');
    
    // Check for existing session
    checkExistingSession();
    
    // Setup event listeners
    setupEventListeners();
    
    // Initialize UI
    showLogin();
});

// =====================================================================
// EVENT LISTENERS SETUP
// =====================================================================

function setupEventListeners() {
    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    // Registration form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
    
    // Transfer form
    const transferForm = document.getElementById('transferForm');
    if (transferForm) {
        transferForm.addEventListener('submit', handleTransfer);
    }

    // Create account form
    const createAccountForm = document.getElementById('createAccountForm');
    if (createAccountForm) {
        createAccountForm.addEventListener('submit', handleCreateAccount);
    }
}

// =====================================================================
// AUTHENTICATION FUNCTIONS
// =====================================================================

async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        showMessage('Please enter both username and password', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (response.ok && data.token) {
            // Store authentication data
            authToken = data.token;
            currentUser = { 
                username: data.username || username, 
                fullName: data.fullName || data.username || username 
            };
            
            // Store in sessionStorage for persistence
            sessionStorage.setItem('authToken', authToken);
            sessionStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showMessage('Login successful! Welcome back.', 'success');
            
            // Load dashboard
            await loadDashboard();
        } else {
            showMessage(data.message || 'Login failed. Please check your credentials.', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showMessage('Connection error. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

async function handleRegister(event) {
    event.preventDefault();
    
    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value;
    const fullName = document.getElementById('regFullName').value.trim();
    
    if (!username || !password || !fullName) {
        showMessage('Please fill in all fields', 'error');
        return;
    }
    
    if (password.length < 8) {
        showMessage('Password must be at least 8 characters long', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password, fullName })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('Registration successful! You can now login.', 'success');
            showLogin();
            
            // Pre-fill login form
            document.getElementById('username').value = username;
        } else {
            showMessage(data.message || 'Registration failed. Please try again.', 'error');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showMessage('Connection error. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

function logout() {
    // Clear stored data
    authToken = null;
    currentUser = null;
    userAccounts = [];
    allTransactions = [];
    
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('currentUser');
    
    showMessage('Logged out successfully', 'info');
    showLogin();
}

function checkExistingSession() {
    const storedToken = sessionStorage.getItem('authToken');
    const storedUser = sessionStorage.getItem('currentUser');
    
    if (storedToken && storedUser) {
        authToken = storedToken;
        currentUser = JSON.parse(storedUser);
        loadDashboard();
    }
}

// =====================================================================
// DASHBOARD FUNCTIONS
// =====================================================================

async function loadDashboard() {
    showDashboard();
    
    // Update user info
    const userFullNameElement = document.getElementById('userFullName');
    if (userFullNameElement && currentUser) {
        userFullNameElement.textContent = currentUser.fullName || currentUser.username;
    }
    
    // Load user accounts
    await loadUserAccounts();
}

async function loadUserAccounts() {
    if (!authToken) {
        showMessage('Please login first', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE}/accounts/user/${currentUser.username}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        if (response.ok) {
            userAccounts = await response.json();
            displayAccounts(userAccounts);
            populateTransferAccountOptions();
            populateAccountFilter();
        } else if (response.status === 401) {
            showMessage('Session expired. Please login again.', 'error');
            logout();
        } else {
            showMessage('Failed to load accounts', 'error');
        }
    } catch (error) {
        console.error('Error loading accounts:', error);
        showMessage('Connection error while loading accounts', 'error');
    } finally {
        showLoading(false);
    }
}

function displayAccounts(accounts) {
    const accountsList = document.getElementById('accountsList');
    if (!accountsList) return;
    
    if (!accounts || accounts.length === 0) {
        accountsList.innerHTML = '<p class="text-center">No accounts found</p>';
        return;
    }
    
    accountsList.innerHTML = accounts.map(account => `
        <div class="account-card">
            <h4>${account.accountType} Account</h4>
            <div class="account-number">Account: ${account.accountNumber}</div>
            <div class="balance">$${parseFloat(account.balance).toLocaleString('en-US', {minimumFractionDigits: 2})}</div>
            <div class="account-actions">
                <button onclick="showDepositForm(${account.id})" class="btn btn-primary btn-sm">Deposit</button>
                <button onclick="deleteAccount(${account.id})" class="btn btn-danger btn-sm">Delete</button>
            </div>
        </div>
    `).join('');
}

function populateTransferAccountOptions() {
    const fromAccountSelect = document.getElementById('fromAccount');
    if (!fromAccountSelect || !userAccounts) return;
    
    fromAccountSelect.innerHTML = '<option value="">Select source account</option>';
    
    userAccounts.forEach(account => {
        const option = document.createElement('option');
        option.value = account.id;
        option.textContent = `${account.accountType} - ${account.accountNumber} ($${parseFloat(account.balance).toLocaleString('en-US', {minimumFractionDigits: 2})})`;
        fromAccountSelect.appendChild(option);
    });
}

function populateAccountFilter() {
    const accountFilter = document.getElementById('accountFilter');
    if (!accountFilter || !userAccounts) return;
    
    accountFilter.innerHTML = '<option value="">All Accounts</option>';
    
    userAccounts.forEach(account => {
        const option = document.createElement('option');
        option.value = account.id;
        option.textContent = `${account.accountType} - ${account.accountNumber}`;
        accountFilter.appendChild(option);
    });
}

// =====================================================================
// TRANSFER FUNCTIONS
// =====================================================================

async function handleTransfer(event) {
    event.preventDefault();
    
    const fromAccountId = document.getElementById('fromAccount').value;
    const toAccountNumber = document.getElementById('toAccount').value.trim();
    const amount = parseFloat(document.getElementById('amount').value);
    const description = document.getElementById('description').value.trim();
    
    if (!fromAccountId || !toAccountNumber || !amount) {
        showMessage('Please fill in all required fields', 'error');
        return;
    }
    
    if (amount <= 0) {
        showMessage('Amount must be greater than 0', 'error');
        return;
    }
    
    // Find source account to check balance
    const sourceAccount = userAccounts.find(acc => acc.id == fromAccountId);
    if (sourceAccount && amount > sourceAccount.balance) {
        showMessage('Insufficient funds', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE}/accounts/transfer`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                fromAccountId: parseInt(fromAccountId),
                toAccountNumber,
                amount,
                description: description || 'Transfer'
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('Transfer completed successfully!', 'success');
            
            // Reset form
            document.getElementById('transferForm').reset();
            hideTransferForm();
            
            // Refresh accounts
            await loadUserAccounts();
        } else {
            showMessage(data.message || 'Transfer failed', 'error');
        }
    } catch (error) {
        console.error('Transfer error:', error);
        showMessage('Connection error during transfer', 'error');
    } finally {
        showLoading(false);
    }
}

// =====================================================================
// ACCOUNT CREATION FUNCTIONS
// =====================================================================

async function handleCreateAccount(event) {
    event.preventDefault();
    
    const accountType = document.getElementById('accountType').value;
    const initialDepositStr = document.getElementById('initialDeposit').value;
    const initialDeposit = parseFloat(initialDepositStr);
    
    if (!accountType) {
        showMessage('Please select an account type', 'error');
        return;
    }
    if (isNaN(initialDeposit) || initialDeposit < 0) {
        showMessage('Please enter a valid initial deposit (0 or more)', 'error');
        return;
    }
    if (!currentUser || !currentUser.username) {
        showMessage('Please login first', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE}/accounts/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                username: currentUser.username,
                accountType: accountType,
                initialDeposit: initialDeposit
            })
        });
        
        if (response.ok) {
            const newAccount = await response.json();
            showMessage(`New ${accountType} account created with $${initialDeposit.toLocaleString('en-US', {minimumFractionDigits: 2})} deposit!`, 'success');
            
            // Reset form
            document.getElementById('createAccountForm').reset();
            hideCreateAccountForm();
            
            // Refresh accounts
            await loadUserAccounts();
        } else {
            let data = {};
            try { data = await response.json(); } catch {}
            showMessage(data.message || 'Failed to create account', 'error');
        }
    } catch (error) {
        console.error('Create account error:', error);
        showMessage('Connection error while creating account', 'error');
    } finally {
        showLoading(false);
    }
}

// =====================================================================
// ACCOUNT MANAGEMENT FUNCTIONS
// =====================================================================

// Modal para depósito
function showDepositForm(accountId) {
    // Crear modal si no existe
    let modal = document.getElementById('depositModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'depositModal';
        modal.className = 'modal';
        modal.innerHTML = `
            <div class="modal-content">
                <h3>Depósito en cuenta</h3>
                <label for="depositAmount">Monto a depositar:</label>
                <input type="number" id="depositAmount" min="0.01" step="0.01" class="form-control" placeholder="Ej: 100.00">
                <div class="form-actions">
                    <button id="depositConfirmBtn" class="btn btn-primary">Depositar</button>
                    <button id="depositCancelBtn" class="btn btn-secondary">Cancelar</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    }
    modal.classList.remove('hidden');
    document.getElementById('depositAmount').value = '';
    
    // Handlers
    const confirmBtn = document.getElementById('depositConfirmBtn');
    const cancelBtn = document.getElementById('depositCancelBtn');
    function cleanup() {
        modal.classList.add('hidden');
        confirmBtn.removeEventListener('click', onConfirm);
        cancelBtn.removeEventListener('click', onCancel);
    }
    async function onConfirm() {
        const amount = parseFloat(document.getElementById('depositAmount').value);
        if (!isNaN(amount) && amount > 0) {
            cleanup();
            await handleDeposit(accountId, amount);
        } else {
            showMessage('Por favor ingresa un monto válido mayor a 0', 'error');
        }
    }
    function onCancel() {
        cleanup();
    }
    confirmBtn.addEventListener('click', onConfirm);
    cancelBtn.addEventListener('click', onCancel);
}

async function handleDeposit(accountId, amount) {
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE}/accounts/${accountId}/deposit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ amount: amount })
        });
        
        if (response.ok) {
            const updatedAccount = await response.json();
            showMessage(`Successfully deposited $${amount.toLocaleString('en-US', {minimumFractionDigits: 2})}`, 'success');
            
            // Refresh accounts
            await loadUserAccounts();
        } else {
            let data = {};
            try { data = await response.json(); } catch {}
            // Mejorar el mensaje para el caso de segundo depósito
            if (data.message && data.message.toLowerCase().includes('deposit amount must be greater than 0')) {
                showMessage('No puedes realizar un segundo depósito inicial. Usa la función de depósito normal.', 'error');
            } else {
                showMessage(data.message || 'Deposit failed', 'error');
            }
        }
    } catch (error) {
        console.error('Deposit error:', error);
        showMessage('Connection error during deposit', 'error');
    } finally {
        showLoading(false);
    }
}

function showConfirmModal(message, onConfirm) {
    const modal = document.getElementById('confirmModal');
    const msg = document.getElementById('confirmModalMessage');
    const yesBtn = document.getElementById('confirmModalYes');
    const noBtn = document.getElementById('confirmModalNo');
    msg.textContent = message;
    modal.classList.remove('hidden');

    function cleanup() {
        modal.classList.add('hidden');
        yesBtn.removeEventListener('click', onYes);
        noBtn.removeEventListener('click', onNo);
    }
    function onYes() {
        cleanup();
        onConfirm();
    }
    function onNo() {
        cleanup();
    }
    yesBtn.addEventListener('click', onYes);
    noBtn.addEventListener('click', onNo);
}

async function deleteAccount(accountId) {
    // Find the account to show its details in the confirmation
    const account = userAccounts.find(acc => acc.id == accountId);
    if (!account) {
        showMessage('Account not found', 'error');
        return;
    }
    const confirmMessage = `Are you sure you want to delete this ${account.accountType} account (${account.accountNumber})? This action cannot be undone.`;
    showConfirmModal(confirmMessage, async () => {
        showLoading(true);
        try {
            const response = await fetch(`${API_BASE}/accounts/${accountId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                }
            });
            if (response.ok) {
                showMessage('Account deleted successfully', 'success');
                await loadUserAccounts();
            } else {
                let data = {};
                try { data = await response.json(); } catch {}
                showMessage(data.message || 'Failed to delete account', 'error');
            }
        } catch (error) {
            console.error('Delete account error:', error);
            showMessage('Connection error while deleting account', 'error');
        } finally {
            showLoading(false);
        }
    });
}

// =====================================================================
// TRANSACTION HISTORY FUNCTIONS
// =====================================================================

async function showTransactionHistory() {
    const section = document.getElementById('transactionHistorySection');
    if (section) {
        section.style.display = 'block';
        section.scrollIntoView({ behavior: 'smooth' });
        
        // Load transaction history for all user accounts
        await loadTransactionHistory();
    }
}

async function loadTransactionHistory() {
    if (!authToken || !userAccounts || userAccounts.length === 0) {
        showMessage('No accounts available', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        allTransactions = [];
        
        // Load transactions for each account
        for (const account of userAccounts) {
            const response = await fetch(`${API_BASE}/accounts/${account.id}/transactions`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json',
                }
            });
            
            if (response.ok) {
                const transactions = await response.json();
                allTransactions.push(...transactions);
            }
        }
        
        // Sort transactions by date (newest first)
        allTransactions.sort((a, b) => new Date(b.transactionDate) - new Date(a.transactionDate));
        
        displayTransactionHistory(allTransactions);
    } catch (error) {
        console.error('Error loading transaction history:', error);
        showMessage('Connection error while loading transactions', 'error');
    } finally {
        showLoading(false);
    }
}

function displayTransactionHistory(transactions) {
    const transactionList = document.getElementById('transactionHistoryList');
    if (!transactionList) return;
    
    if (!transactions || transactions.length === 0) {
        transactionList.innerHTML = '<p class="text-center">No transactions found</p>';
        return;
    }
    
    transactionList.innerHTML = transactions.map(transaction => `
        <div class="transaction-item">
            <div class="transaction-header">
                <span class="transaction-type">${transaction.transactionType}</span>
                <span class="transaction-date">${new Date(transaction.transactionDate).toLocaleDateString()}</span>
            </div>
            <div class="transaction-details">
                <div class="transaction-accounts">
                    <span class="from-account">From: ${transaction.fromAccountNumber}</span>
                    <span class="to-account">To: ${transaction.toAccountNumber}</span>
                </div>
                <div class="transaction-amount">$${parseFloat(transaction.amount).toLocaleString('en-US', {minimumFractionDigits: 2})}</div>
            </div>
            ${transaction.description ? `<div class="transaction-description">${transaction.description}</div>` : ''}
        </div>
    `).join('');
}

function filterTransactions() {
    const accountFilter = document.getElementById('accountFilter');
    const selectedAccountId = accountFilter.value;
    
    if (!selectedAccountId) {
        displayTransactionHistory(allTransactions);
        return;
    }
    
    const filteredTransactions = allTransactions.filter(transaction => {
        const sourceAccount = userAccounts.find(acc => acc.id == selectedAccountId);
        return sourceAccount && transaction.fromAccountNumber === sourceAccount.accountNumber;
    });
    
    displayTransactionHistory(filteredTransactions);
}

function hideTransactionHistory() {
    const section = document.getElementById('transactionHistorySection');
    if (section) {
        section.style.display = 'none';
    }
}

// =====================================================================
// UI MANAGEMENT FUNCTIONS
// =====================================================================

function showLogin() {
    hideAllSections();
    document.getElementById('loginSection').style.display = 'block';
    document.getElementById('navigation').style.display = 'none';
}

function showRegister() {
    hideAllSections();
    document.getElementById('registerSection').style.display = 'block';
    document.getElementById('navigation').style.display = 'none';
}

function showDashboard() {
    hideAllSections();
    document.getElementById('dashboardSection').style.display = 'block';
    document.getElementById('navigation').style.display = 'block';
}

function hideAllSections() {
    const sections = ['loginSection', 'registerSection', 'dashboardSection'];
    sections.forEach(sectionId => {
        const section = document.getElementById(sectionId);
        if (section) {
            section.style.display = 'none';
        }
    });
}

function showTransferForm() {
    const transferSection = document.getElementById('transferSection');
    if (transferSection) {
        transferSection.style.display = 'block';
        transferSection.scrollIntoView({ behavior: 'smooth' });
    }
}

function hideTransferForm() {
    const transferSection = document.getElementById('transferSection');
    if (transferSection) {
        transferSection.style.display = 'none';
    }
}

function showCreateAccountForm() {
    const createAccountSection = document.getElementById('createAccountSection');
    if (createAccountSection) {
        createAccountSection.style.display = 'block';
        createAccountSection.scrollIntoView({ behavior: 'smooth' });
    }
}

function hideCreateAccountForm() {
    const createAccountSection = document.getElementById('createAccountSection');
    if (createAccountSection) {
        createAccountSection.style.display = 'none';
    }
}

function showLoading(show) {
    const loadingSpinner = document.getElementById('loadingSpinner');
    if (loadingSpinner) {
        loadingSpinner.style.display = show ? 'flex' : 'none';
    }
}

// =====================================================================
// MESSAGE SYSTEM
// =====================================================================

function showMessage(message, type = 'info') {
    const messageContainer = document.getElementById('messageContainer');
    if (!messageContainer) return;
    
    const messageElement = document.createElement('div');
    messageElement.className = `message ${type}`;
    messageElement.textContent = message;
    
    messageContainer.appendChild(messageElement);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (messageElement.parentNode) {
            messageElement.parentNode.removeChild(messageElement);
        }
    }, 5000);
}

// =====================================================================
// UTILITY FUNCTIONS
// =====================================================================

async function refreshAccounts() {
    await loadUserAccounts();
    showMessage('Accounts refreshed', 'info');
}

function viewTransactions() {
    showMessage('Transaction history feature coming soon!', 'info');
}

// =====================================================================
// ERROR HANDLING
// =====================================================================

window.addEventListener('error', function(event) {
    console.error('Global error:', event.error);
    showMessage('An unexpected error occurred', 'error');
});

// Handle unhandled promise rejections
window.addEventListener('unhandledrejection', function(event) {
    console.error('Unhandled promise rejection:', event.reason);
    showMessage('An unexpected error occurred', 'error');
});

// =====================================================================
// DEVELOPMENT HELPERS
// =====================================================================

// Expose some functions globally for debugging
window.bankingApp = {
    currentUser,
    authToken,
    userAccounts,
    refreshAccounts,
    logout,
    showMessage
};

console.log('🏦 CentralBank SecLand Frontend - Ready for secure banking operations!');
