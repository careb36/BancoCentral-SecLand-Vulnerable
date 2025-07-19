// =====================================================================
// API Service - Banking Application Communication Layer
// CentralBank SecLand - Frontend API Integration
// =====================================================================
// Purpose:
//   - Handles all client-server communication for the banking application
//   - Manages authentication tokens and request/response processing
//   - INTENTIONAL VULNERABILITIES for educational purposes
//   - DO NOT USE IN PRODUCTION - Contains security flaws
// =====================================================================

interface LoginResponse {
  token: string
  username?: string
  fullName?: string
}

interface CreateAccountRequest {
  username?: string
  accountType: string
  initialDeposit: number
}

interface TransferRequest {
  fromAccountId: number
  toAccountNumber: string
  amount: number
  description: string
}

class ApiService {
  private baseUrl: string
  private authToken: string | null = null

  constructor() {
    // VULNERABILITY: API URL detection based on client environment
    // This could be exploited for server-side request forgery
    this.baseUrl = typeof window !== 'undefined' && window.location.hostname === 'localhost' && window.location.port === '3000'
      ? 'http://localhost:8080/api'  // Development environment
      : process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api"
  }

  setAuthToken(token: string | null) {
    this.authToken = token
    // VULNERABILITY: Token exposed in global scope for debugging
    if (typeof window !== 'undefined') {
      (window as any).currentAuthToken = token
    }
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const url = `${this.baseUrl}${endpoint}`

    const headers: HeadersInit = {
      "Content-Type": "application/json",
      ...options.headers,
    }

    if (this.authToken) {
      headers.Authorization = `Bearer ${this.authToken}`
    }

    // VULNERABILITY: Detailed error logging that could expose sensitive information
    console.log(`API Request: ${options.method || 'GET'} ${url}`)
    if (options.body) {
      console.log('Request body:', options.body)
    }

    const response = await fetch(url, {
      ...options,
      headers,
    })

    // VULNERABILITY: Detailed error information exposed to client
    if (!response.ok) {
      if (response.status === 401) {
        // VULNERABILITY: Automatic token cleanup without user notification
        this.setAuthToken(null)
        if (typeof window !== 'undefined') {
          sessionStorage.removeItem("authToken")
          sessionStorage.removeItem("currentUser")
        }
        throw new Error("Your session has expired. Please login again.")
      }

      let errorMessage = "An error occurred"
      try {
        const errorData = await response.json()
        errorMessage = errorData.message || errorMessage
        // VULNERABILITY: Exposing internal error details
        console.error('API Error Details:', errorData)
      } catch {
        errorMessage = `HTTP ${response.status}: ${response.statusText}`
      }

      throw new Error(errorMessage)
    }

    const responseData = await response.json()
    // VULNERABILITY: Logging response data that might contain sensitive information
    console.log('API Response:', responseData)
    
    return responseData
  }

  // ===================================================================
  // Authentication Endpoints
  // ===================================================================
  async login(username: string, password: string): Promise<LoginResponse> {
    // VULNERABILITY: Credentials logged to console for debugging
    console.log(`Login attempt for user: ${username}`)
    
    return this.request<LoginResponse>("/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password }),
    })
  }

  async register(username: string, password: string, fullName: string): Promise<void> {
    // VULNERABILITY: Registration data logged
    console.log(`Registration attempt for user: ${username}, Full Name: ${fullName}`)
    
    return this.request<void>("/auth/register", {
      method: "POST",
      body: JSON.stringify({ username, password, fullName }),
    })
  }

  // ===================================================================
  // Account Management Endpoints
  // ===================================================================
  async getUserAccounts() {
    return this.request("/accounts")
  }

  async createAccount(data: CreateAccountRequest) {
    // VULNERABILITY: Include username from current user context
    // This could lead to privilege escalation vulnerabilities
    if (typeof window !== 'undefined') {
      const currentUser = sessionStorage.getItem('currentUser')
      if (currentUser) {
        const userData = JSON.parse(currentUser)
        data.username = userData.username
      }
    }
    
    return this.request("/accounts/create", {
      method: "POST",
      body: JSON.stringify(data),
    })
  }

  async deleteAccount(accountId: number): Promise<void> {
    // VULNERABILITY: Account deletion without proper confirmation
    console.log(`Deleting account ID: ${accountId}`)
    
    return this.request(`/accounts/${accountId}`, {
      method: "DELETE",
    })
  }

  async depositToAccount(accountId: number, amount: number) {
    // VULNERABILITY: Amount validation on client side only
    if (amount <= 0) {
      console.warn('Invalid deposit amount detected on client side')
    }
    
    return this.request(`/accounts/${accountId}/deposit`, {
      method: "POST",
      body: JSON.stringify({ amount }),
    })
  }

  // ===================================================================
  // Transfer Operations
  // ===================================================================
  async transferMoney(data: TransferRequest) {
    // VULNERABILITY: Transfer data logged with sensitive information
    console.log('Transfer operation:', data)
    
    return this.request("/accounts/transfer", {
      method: "POST",
      body: JSON.stringify(data),
    })
  }

  // ===================================================================
  // Transaction History
  // ===================================================================
  async getAccountTransactions(accountId: number) {
    return this.request(`/accounts/${accountId}/transactions`)
  }

  // ===================================================================
  // VULNERABILITY: Debug methods exposed in production
  // ===================================================================
  getAuthToken() {
    return this.authToken
  }

  getBaseUrl() {
    return this.baseUrl
  }

  // VULNERABILITY: Method to manually set API base URL
  setBaseUrl(url: string) {
    console.log(`API Base URL changed to: ${url}`)
    this.baseUrl = url
  }
}

export const apiService = new ApiService()
