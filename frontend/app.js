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
            currentUser = data.user || { username, fullName: data.fullName || username };
            
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
        const response = await fetch(`${API_BASE}/accounts`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json',
            }
        });
        
        if (response.ok) {
            userAccounts = await response.json();
            displayAccounts(userAccounts);
            populateTransferAccountOptions();
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
    
    // Also log to console
    console.log(`[${type.toUpperCase()}] ${message}`);
}

// =====================================================================
// UTILITY FUNCTIONS
// =====================================================================

async function refreshAccounts() {
    showMessage('Refreshing accounts...', 'info');
    await loadUserAccounts();
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
