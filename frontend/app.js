// =====================================================================
// CentralBank SecLand - Frontend Application Logic
// =====================================================================
// Purpose:
//   - Handles all client-side interactions for the banking application
//   - Manages authentication, account operations, and UI state
//   - Communicates with the Spring Boot backend API
//   - Provides enhanced UX with clear messaging and feedback
// =====================================================================

// Global state management
let currentUser = null;
let authToken = null;
let userAccounts = [];
let allTransactions = [];

// API Base URL - auto-detects environment
const API_BASE = window.location.hostname === 'localhost' && window.location.port === '' 
    ? '/api'  // Docker/Nginx setup
    : 'http://localhost:8080/api';  // Local development

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
    
    // Show welcome message for new users
    showWelcomeMessage();
});

// =====================================================================
// EVENT LISTENERS SETUP
// =====================================================================

function setupEventListeners() {
    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
        // Auto-focus on username field
        const usernameField = document.getElementById('username');
        if (usernameField) {
            usernameField.focus();
        }
    }
    
    // Registration form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
        // Auto-focus on username field
        const regUsernameField = document.getElementById('regUsername');
        if (regUsernameField) {
            regUsernameField.focus();
        }
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
    
    // Add keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + R to refresh accounts
        if ((e.ctrlKey || e.metaKey) && e.key === 'r') {
            e.preventDefault();
            if (currentUser) {
                refreshAccounts();
            }
        }
        // Escape to close modals
        if (e.key === 'Escape') {
            hideAllModals();
        }
    });
}

// =====================================================================
// AUTHENTICATION FUNCTIONS
// =====================================================================

async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        showMessage('Por favor ingresa tu nombre de usuario y contraseña', 'error');
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
            
            showMessage(`¡Bienvenido de vuelta, ${currentUser.fullName}! 🎉`, 'success');
            
            // Load dashboard
            await loadDashboard();
        } else {
            const errorMsg = data.message || 'Credenciales incorrectas. Por favor verifica tu usuario y contraseña.';
            showMessage(errorMsg, 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showMessage('Error de conexión. Por favor verifica tu conexión a internet e intenta nuevamente.', 'error');
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
        showMessage('Por favor completa todos los campos requeridos', 'error');
        return;
    }
    
    if (password.length < 8) {
        showMessage('La contraseña debe tener al menos 8 caracteres', 'error');
        return;
    }
    
    if (username.length < 3) {
        showMessage('El nombre de usuario debe tener al menos 3 caracteres', 'error');
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
            showMessage(`¡Cuenta creada exitosamente! Ya puedes iniciar sesión con tu nuevo usuario.`, 'success');
            showLogin();
            
            // Pre-fill login form
            document.getElementById('username').value = username;
            document.getElementById('username').focus();
        } else {
            const errorMsg = data.message || 'Error al crear la cuenta. Por favor intenta con un nombre de usuario diferente.';
            showMessage(errorMsg, 'error');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showMessage('Error de conexión. Por favor verifica tu conexión a internet e intenta nuevamente.', 'error');
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
    
    showMessage('Sesión cerrada exitosamente. ¡Hasta pronto! 👋', 'info');
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
        userFullNameElement.textContent = currentUser.fullName;
    }
    
    // Load user accounts
    await loadUserAccounts();
    
    // Show welcome message for returning users
    if (userAccounts.length > 0) {
        const totalBalance = userAccounts.reduce((sum, account) => sum + parseFloat(account.balance), 0);
        showMessage(`¡Bienvenido! Tienes ${userAccounts.length} cuenta(s) con un balance total de $${totalBalance.toLocaleString('en-US', {minimumFractionDigits: 2})}`, 'info');
    } else {
        showMessage('¡Bienvenido! Crea tu primera cuenta para comenzar a usar el banco.', 'info');
    }
}

async function loadUserAccounts() {
    if (!authToken) {
        showMessage('Por favor inicia sesión primero', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE}/accounts`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            userAccounts = await response.json();
            displayAccounts(userAccounts);
            populateTransferAccountOptions();
            populateAccountFilter();
            
            if (userAccounts.length === 0) {
                showMessage('No tienes cuentas aún. ¡Crea tu primera cuenta para comenzar!', 'info');
            }
        } else if (response.status === 401) {
            showMessage('Tu sesión ha expirado. Por favor inicia sesión nuevamente.', 'error');
            logout();
        } else {
            showMessage('Error al cargar las cuentas. Por favor intenta nuevamente.', 'error');
        }
    } catch (error) {
        console.error('Error loading accounts:', error);
        showMessage('Error de conexión al cargar las cuentas. Verifica tu conexión a internet.', 'error');
    } finally {
        showLoading(false);
    }
}

function displayAccounts(accounts) {
    const accountsList = document.getElementById('accountsList');
    if (!accountsList) return;
    
    if (accounts.length === 0) {
        accountsList.innerHTML = `
            <div class="no-accounts">
                <p>No tienes cuentas aún</p>
                <button class="btn btn-primary" onclick="showCreateAccountForm()">Crear mi primera cuenta</button>
            </div>
        `;
        return;
    }
    
    accountsList.innerHTML = accounts.map(account => `
        <div class="account-card">
            <div class="account-header">
            <h4>${account.accountType} Account</h4>
                <span class="account-number">${account.accountNumber}</span>
            </div>
            <div class="balance">
                <span class="balance-label">Balance:</span>
                <span class="balance-amount">$${parseFloat(account.balance).toLocaleString('en-US', {minimumFractionDigits: 2})}</span>
            </div>
            <div class="account-actions">
                <button class="btn btn-sm btn-primary" onclick="showDepositForm(${account.id})">
                    💰 Depositar
                </button>
                <button class="btn btn-sm btn-danger" onclick="deleteAccount(${account.id})">
                    🗑️ Eliminar
                </button>
            </div>
        </div>
    `).join('');
}

function populateTransferAccountOptions() {
    const fromAccountSelect = document.getElementById('fromAccount');
    if (!fromAccountSelect) return;
    
    fromAccountSelect.innerHTML = '<option value="">Selecciona cuenta origen</option>';
    
    userAccounts.forEach(account => {
        const option = document.createElement('option');
        option.value = account.id;
        option.textContent = `${account.accountType} - ${account.accountNumber} ($${parseFloat(account.balance).toLocaleString('en-US', {minimumFractionDigits: 2})})`;
        fromAccountSelect.appendChild(option);
    });
}

function populateAccountFilter() {
    const accountFilter = document.getElementById('accountFilter');
    if (!accountFilter) return;
    
    accountFilter.innerHTML = '<option value="">Todas las cuentas</option>';
    
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
        showMessage('Por favor completa todos los campos requeridos', 'error');
        return;
    }
    
    if (amount <= 0) {
        showMessage('El monto debe ser mayor a $0', 'error');
        return;
    }
    
    if (amount > 1000000) {
        showMessage('El monto máximo permitido es $1,000,000', 'error');
        return;
    }
    
    // Find source account to check balance
    const sourceAccount = userAccounts.find(acc => acc.id == fromAccountId);
    if (sourceAccount && amount > sourceAccount.balance) {
        showMessage(`Fondos insuficientes. Tu balance es $${parseFloat(sourceAccount.balance).toLocaleString('en-US', {minimumFractionDigits: 2})}`, 'error');
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
                description: description || 'Transferencia'
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage(`¡Transferencia completada exitosamente! Se transfirieron $${amount.toLocaleString('en-US', {minimumFractionDigits: 2})}`, 'success');
            
            // Reset form
            document.getElementById('transferForm').reset();
            hideTransferForm();
            
            // Refresh accounts
            await loadUserAccounts();
        } else {
            const errorMsg = data.message || 'Error al realizar la transferencia. Verifica los datos e intenta nuevamente.';
            showMessage(errorMsg, 'error');
        }
    } catch (error) {
        console.error('Transfer error:', error);
        showMessage('Error de conexión durante la transferencia. Verifica tu conexión a internet.', 'error');
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
        showMessage('Por favor selecciona un tipo de cuenta', 'error');
        return;
    }
    if (isNaN(initialDeposit) || initialDeposit < 0) {
        showMessage('Por favor ingresa un depósito inicial válido (0 o más)', 'error');
        return;
    }
    if (initialDeposit > 1000000) {
        showMessage('El depósito inicial máximo permitido es $1,000,000', 'error');
        return;
    }
    if (!currentUser || !currentUser.username) {
        showMessage('Por favor inicia sesión primero', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE}/accounts/create`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
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
            const depositText = initialDeposit > 0 ? ` con depósito inicial de $${initialDeposit.toLocaleString('en-US', {minimumFractionDigits: 2})}` : '';
            showMessage(`¡Nueva cuenta ${accountType} creada exitosamente${depositText}! 🎉`, 'success');
            
            // Reset form
            document.getElementById('createAccountForm').reset();
            hideCreateAccountForm();
            
            // Refresh accounts
            await loadUserAccounts();
        } else {
            let data = {};
            try { data = await response.json(); } catch {}
            const errorMsg = data.message || 'Error al crear la cuenta. Por favor intenta nuevamente.';
            showMessage(errorMsg, 'error');
        }
    } catch (error) {
        console.error('Create account error:', error);
        showMessage('Error de conexión al crear la cuenta. Verifica tu conexión a internet.', 'error');
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
                <h3>💰 Depositar en cuenta</h3>
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
    document.getElementById('depositAmount').focus();
    
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
            if (amount > 1000000) {
                showMessage('El monto máximo permitido es $1,000,000', 'error');
                return;
            }
            cleanup();
            await handleDeposit(accountId, amount);
        } else {
            showMessage('Por favor ingresa un monto válido mayor a $0', 'error');
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
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ amount: amount })
        });
        
        if (response.ok) {
            const updatedAccount = await response.json();
            showMessage(`¡Depósito exitoso! Se depositaron $${amount.toLocaleString('en-US', {minimumFractionDigits: 2})} en tu cuenta`, 'success');
            
            // Refresh accounts
            await loadUserAccounts();
        } else {
            let data = {};
            try { data = await response.json(); } catch {}
            const errorMsg = data.message || 'Error al realizar el depósito. Por favor intenta nuevamente.';
            showMessage(errorMsg, 'error');
        }
    } catch (error) {
        console.error('Deposit error:', error);
        showMessage('Error de conexión durante el depósito. Verifica tu conexión a internet.', 'error');
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
        showMessage('Cuenta no encontrada', 'error');
        return;
    }
    const confirmMessage = `¿Estás seguro de que quieres eliminar esta cuenta ${account.accountType} (${account.accountNumber})? Esta acción no se puede deshacer.`;
    showConfirmModal(confirmMessage, async () => {
        showLoading(true);
        try {
            const response = await fetch(`${API_BASE}/accounts/${accountId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json',
                }
            });
            if (response.ok) {
                showMessage('Cuenta eliminada exitosamente', 'success');
                await loadUserAccounts();
            } else {
                let data = {};
                try { data = await response.json(); } catch {}
                const errorMsg = data.message || 'Error al eliminar la cuenta. Por favor intenta nuevamente.';
                showMessage(errorMsg, 'error');
            }
        } catch (error) {
            console.error('Delete account error:', error);
            showMessage('Error de conexión al eliminar la cuenta. Verifica tu conexión a internet.', 'error');
        } finally {
            showLoading(false);
        }
    });
}

// =====================================================================
// TRANSACTION HISTORY FUNCTIONS
// =====================================================================

async function showTransactionHistory() {
    if (!authToken || !userAccounts || userAccounts.length === 0) {
        showMessage('No hay cuentas disponibles para mostrar transacciones', 'error');
        return;
    }
    
    showTransactionHistorySection();
    await loadTransactionHistory();
}

async function loadTransactionHistory() {
    if (!authToken || !userAccounts || userAccounts.length === 0) {
        showMessage('No hay cuentas disponibles', 'error');
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
        
        if (allTransactions.length === 0) {
            showMessage('No hay transacciones para mostrar', 'info');
        } else {
            showMessage(`Cargadas ${allTransactions.length} transacciones`, 'info');
        }
    } catch (error) {
        console.error('Error loading transaction history:', error);
        showMessage('Error de conexión al cargar las transacciones. Verifica tu conexión a internet.', 'error');
    } finally {
        showLoading(false);
    }
}

function displayTransactionHistory(transactions) {
    const transactionList = document.getElementById('transactionHistoryList');
    if (!transactionList) return;
    
    if (transactions.length === 0) {
        transactionList.innerHTML = '<p class="no-transactions">No hay transacciones para mostrar</p>';
        return;
    }
    
    transactionList.innerHTML = transactions.map(transaction => {
        const amount = parseFloat(transaction.amount);
        const isPositive = transaction.transactionType === 'DEPOSIT' || transaction.transactionType === 'RECEIVED';
        const amountClass = isPositive ? 'positive' : 'negative';
        
        return `
            <div class="transaction-item">
                <div class="transaction-header">
                    <span class="transaction-type">${transaction.transactionType}</span>
                    <span class="transaction-date">${new Date(transaction.transactionDate).toLocaleString()}</span>
                </div>
                <div class="transaction-details">
                    <div class="transaction-accounts">
                        ${transaction.sourceAccountNumber ? `<div class="from-account">De: ${transaction.sourceAccountNumber}</div>` : ''}
                        ${transaction.destinationAccountNumber ? `<div class="to-account">Para: ${transaction.destinationAccountNumber}</div>` : ''}
                    </div>
                    <div class="transaction-amount ${amountClass}">
                        ${isPositive ? '+' : '-'}$${amount.toLocaleString('en-US', {minimumFractionDigits: 2})}
                    </div>
                </div>
                ${transaction.description ? `<div class="transaction-description">${transaction.description}</div>` : ''}
            </div>
        `;
    }).join('');
}

function filterTransactions() {
    const accountFilter = document.getElementById('accountFilter');
    const selectedAccountId = accountFilter.value;
    
    if (!selectedAccountId) {
        displayTransactionHistory(allTransactions);
        return;
    }
    
    const filteredTransactions = allTransactions.filter(transaction => 
        transaction.sourceAccountId == selectedAccountId || 
        transaction.destinationAccountId == selectedAccountId
    );
    
    displayTransactionHistory(filteredTransactions);
}

function hideTransactionHistory() {
    hideTransactionHistorySection();
}

// =====================================================================
// UI NAVIGATION FUNCTIONS
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
    document.getElementById('loginSection').style.display = 'none';
    document.getElementById('registerSection').style.display = 'none';
    document.getElementById('dashboardSection').style.display = 'none';
}

function showTransferForm() {
    hideTransferForm();
    document.getElementById('transferSection').style.display = 'block';
    document.getElementById('transferSection').scrollIntoView({ behavior: 'smooth' });
}

function hideTransferForm() {
    document.getElementById('transferSection').style.display = 'none';
}

function showCreateAccountForm() {
    hideCreateAccountForm();
    document.getElementById('createAccountSection').style.display = 'block';
    document.getElementById('createAccountSection').scrollIntoView({ behavior: 'smooth' });
}

function hideCreateAccountForm() {
    document.getElementById('createAccountSection').style.display = 'none';
}

function showTransactionHistorySection() {
    document.getElementById('transactionHistorySection').style.display = 'block';
    document.getElementById('transactionHistorySection').scrollIntoView({ behavior: 'smooth' });
}

function hideTransactionHistorySection() {
    document.getElementById('transactionHistorySection').style.display = 'none';
}

function hideAllModals() {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => modal.classList.add('hidden'));
}

// =====================================================================
// LOADING AND MESSAGE SYSTEM
// =====================================================================

function showLoading(show) {
    const loadingSpinner = document.getElementById('loadingSpinner');
    if (loadingSpinner) {
        loadingSpinner.style.display = show ? 'flex' : 'none';
    }
}

function showMessage(message, type = 'info') {
    const messageContainer = document.getElementById('messageContainer');
    if (!messageContainer) return;
    
    const messageElement = document.createElement('div');
    messageElement.className = `message ${type}`;
    messageElement.innerHTML = `
        <div class="message-content">
            <span class="message-icon">${getMessageIcon(type)}</span>
            <span class="message-text">${message}</span>
            <button class="message-close" onclick="this.parentElement.parentElement.remove()">×</button>
        </div>
    `;
    
    messageContainer.appendChild(messageElement);
    
    // Auto-remove after 6 seconds for success/info, 8 seconds for errors
    const autoRemoveTime = type === 'error' ? 8000 : 6000;
    setTimeout(() => {
        if (messageElement.parentNode) {
            messageElement.parentNode.removeChild(messageElement);
        }
    }, autoRemoveTime);
}

function getMessageIcon(type) {
    switch (type) {
        case 'success': return '✅';
        case 'error': return '❌';
        case 'warning': return '⚠️';
        case 'info': return 'ℹ️';
        default: return '💬';
    }
}

function showWelcomeMessage() {
    // Show welcome message only for new visitors
    if (!sessionStorage.getItem('hasVisited')) {
        setTimeout(() => {
            showMessage('¡Bienvenido a CentralBank SecLand! 🏦 Usa las credenciales de demo para comenzar.', 'info');
            sessionStorage.setItem('hasVisited', 'true');
        }, 1000);
    }
}

// =====================================================================
// UTILITY FUNCTIONS
// =====================================================================

async function refreshAccounts() {
    await loadUserAccounts();
    showMessage('Cuentas actualizadas', 'info');
}

// =====================================================================
// ERROR HANDLING
// =====================================================================

window.addEventListener('error', function(event) {
    console.error('Global error:', event.error);
    showMessage('Ha ocurrido un error inesperado. Por favor recarga la página.', 'error');
});

// Handle unhandled promise rejections
window.addEventListener('unhandledrejection', function(event) {
    console.error('Unhandled promise rejection:', event.reason);
    showMessage('Ha ocurrido un error inesperado. Por favor recarga la página.', 'error');
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
