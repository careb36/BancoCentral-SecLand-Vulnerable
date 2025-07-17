// This file contains the secure versions of frontend functionality
// for educational comparison with the vulnerable implementations

/**
 * Secure Authentication Functions
 */
function secureLogin(username, password) {
    // 1. Password complexity validation
    if (!validatePasswordComplexity(password)) {
        throw new Error("Password must meet complexity requirements");
    }
    
    // 2. Rate limiting check
    if (isRateLimited(username)) {
        throw new Error("Too many login attempts. Please try again later.");
    }
    
    // 3. CSRF token inclusion
    const csrfToken = document.querySelector('meta[name="csrf-token"]').content;
    
    // 4. Secure headers
    const headers = {
        'Content-Type': 'application/json',
        'X-CSRF-Token': csrfToken,
        'X-Requested-With': 'XMLHttpRequest'
    };
    
    // 5. Account lockout tracking
    trackLoginAttempt(username);
    
    return fetch('/api/auth/login', {
        method: 'POST',
        headers,
        credentials: 'same-origin',
        body: JSON.stringify({ username, password })
    });
}

/**
 * Secure Transaction Functions
 */
function secureTransfer(fromAccountId, toAccountNumber, amount, description) {
    // 1. Input sanitization
    const sanitizedDescription = DOMPurify.sanitize(description);
    
    // 2. Amount validation
    if (!validateTransactionAmount(amount)) {
        throw new Error("Invalid transaction amount");
    }
    
    // 3. Account ownership verification
    if (!verifyAccountOwnership(fromAccountId)) {
        throw new Error("Unauthorized account access");
    }
    
    // 4. Transaction limits check
    if (exceedsDailyLimit(fromAccountId, amount)) {
        throw new Error("Daily transaction limit exceeded");
    }
    
    // 5. Anti-CSRF token
    const csrfToken = document.querySelector('meta[name="csrf-token"]').content;
    
    return fetch('/api/accounts/transfer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-Token': csrfToken
        },
        body: JSON.stringify({
            fromAccountId,
            toAccountNumber,
            amount,
            description: sanitizedDescription
        })
    });
}

/**
 * Secure Display Functions
 */
function secureDisplayTransaction(transaction) {
    // 1. HTML Escaping
    const safeDescription = escapeHtml(transaction.description);
    const safeAccountNumber = escapeHtml(transaction.accountNumber);
    
    // 2. Safe amount formatting
    const safeAmount = Number(transaction.amount).toLocaleString(
        'en-US',
        { minimumFractionDigits: 2, maximumFractionDigits: 2 }
    );
    
    // 3. Safe date formatting
    const safeDate = new Date(transaction.date).toLocaleDateString();
    
    return `
        <div class="transaction">
            <span class="account">${safeAccountNumber}</span>
            <span class="amount">$${safeAmount}</span>
            <span class="date">${safeDate}</span>
            <p class="description">${safeDescription}</p>
        </div>
    `;
}

/**
 * Security Helper Functions
 */
function validatePasswordComplexity(password) {
    const minLength = 12;
    const hasUpper = /[A-Z]/.test(password);
    const hasLower = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecial = /[^A-Za-z0-9]/.test(password);
    
    return password.length >= minLength &&
           hasUpper && hasLower && hasNumber && hasSpecial;
}

function escapeHtml(unsafeStr) {
    const div = document.createElement('div');
    div.textContent = unsafeStr;
    return div.innerHTML;
}

function validateTransactionAmount(amount) {
    const num = Number(amount);
    return !isNaN(num) && 
           num > 0 && 
           num <= 1000000 && 
           num.toFixed(2) === num.toString();
}

/**
 * Security Utility Functions
 */
const SecurityUtils = {
    MAXIMUM_LOGIN_ATTEMPTS: 5,
    LOGIN_TIMEOUT_MINUTES: 15,
    
    loginAttempts: new Map(),
    
    isRateLimited(username) {
        const attempts = this.loginAttempts.get(username) || [];
        const recentAttempts = attempts.filter(timestamp => 
            Date.now() - timestamp < this.LOGIN_TIMEOUT_MINUTES * 60 * 1000
        );
        return recentAttempts.length >= this.MAXIMUM_LOGIN_ATTEMPTS;
    },
    
    trackLoginAttempt(username) {
        const attempts = this.loginAttempts.get(username) || [];
        attempts.push(Date.now());
        this.loginAttempts.set(username, attempts);
    },
    
    resetLoginAttempts(username) {
        this.loginAttempts.delete(username);
    }
};
