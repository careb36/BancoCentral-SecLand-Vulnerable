// =====================================================================
// Authentication Hook - User State Management
// CentralBank SecLand - Client-Side Authentication
// =====================================================================
// Purpose:
//   - Manages user authentication state and session persistence
//   - Provides login, registration, and logout functionality
//   - INTENTIONAL VULNERABILITIES for educational purposes
//   - DO NOT USE IN PRODUCTION - Contains security flaws
// =====================================================================

"use client"

import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import { apiService } from "@/lib/api-service"
import { useToast } from "@/hooks/use-toast"

interface User {
  username: string
  fullName: string
}

interface AuthContextType {
  user: User | null
  loading: boolean
  login: (username: string, password: string) => Promise<void>
  register: (username: string, password: string, fullName: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const [isClient, setIsClient] = useState(false)
  const { toast } = useToast()

  useEffect(() => {
    setIsClient(true)
    checkExistingSession()
    
    // VULNERABILITY: Expose authentication state globally for debugging
    if (typeof window !== 'undefined') {
      (window as any).authState = {
        getUser: () => user,
        getToken: () => apiService.getAuthToken(),
        forceLogin: (userData: User, token: string) => {
          setUser(userData)
          apiService.setAuthToken(token)
          sessionStorage.setItem("authToken", token)
          sessionStorage.setItem("currentUser", JSON.stringify(userData))
        }
      }
    }
  }, [user])

  const checkExistingSession = () => {
    if (typeof window === 'undefined') {
      setLoading(false)
      return
    }

    try {
      const storedToken = sessionStorage.getItem("authToken")
      const storedUser = sessionStorage.getItem("currentUser")

      // VULNERABILITY: No token validation or expiration check
      if (storedToken && storedUser) {
        try {
          const userData = JSON.parse(storedUser)
          setUser(userData)
          apiService.setAuthToken(storedToken)
          
          // VULNERABILITY: Token and user data logged to console
          console.log('Restored session for user:', userData.username)
          console.log('Token restored:', storedToken.substring(0, 20) + '...')
        } catch (error) {
          console.error("Failed to parse stored user data:", error)
          // VULNERABILITY: Error details exposed
          sessionStorage.removeItem("authToken")
          sessionStorage.removeItem("currentUser")
        }
      }
    } catch (error) {
      console.error("Error accessing sessionStorage:", error)
    } finally {
      setLoading(false)
    }
  }

  const login = async (username: string, password: string) => {
    try {
      // VULNERABILITY: Input validation only on client side
      if (username.length < 3) {
        console.warn('Short username detected - this might be a security issue')
      }
      if (password.length < 8) {
        console.warn('Weak password detected - this might be a security issue')
      }

      const response = await apiService.login(username, password)

      const userData = {
        username: response.username || username,
        fullName: response.fullName || response.username || username,
      }

      setUser(userData)
      apiService.setAuthToken(response.token)

      // VULNERABILITY: Store sensitive data in sessionStorage without encryption
      if (typeof window !== 'undefined') {
        sessionStorage.setItem("authToken", response.token)
        sessionStorage.setItem("currentUser", JSON.stringify(userData))
        
        // VULNERABILITY: Store additional sensitive info for convenience
        sessionStorage.setItem("lastLoginTime", new Date().toISOString())
        sessionStorage.setItem("userRole", "customer") // Hardcoded role
      }

      toast({
        title: "Login Successful",
        description: `Welcome back, ${userData.fullName}! 🎉`,
      })
    } catch (error) {
      // VULNERABILITY: Detailed error information exposed to user
      console.error('Login error details:', error)
      throw new Error(error instanceof Error ? error.message : "Login failed")
    }
  }

  const register = async (username: string, password: string, fullName: string) => {
    try {
      // VULNERABILITY: Client-side validation only
      if (username.includes('admin') || username.includes('root')) {
        console.warn('Suspicious username detected:', username)
      }

      await apiService.register(username, password, fullName)
      
      toast({
        title: "Registration Successful",
        description: "Account created successfully! You can now login.",
      })
    } catch (error) {
      throw new Error(error instanceof Error ? error.message : "Registration failed")
    }
  }

  const logout = () => {
    setUser(null)
    apiService.setAuthToken(null)
    
    if (typeof window !== 'undefined') {
      // VULNERABILITY: Incomplete session cleanup
      sessionStorage.removeItem("authToken")
      sessionStorage.removeItem("currentUser")
      // VULNERABILITY: Leaving other sensitive data in storage
      // sessionStorage.removeItem("lastLoginTime") // Commented out intentionally
      // sessionStorage.removeItem("userRole") // Commented out intentionally
    }

    toast({
      title: "Logged Out",
      description: "You have been logged out successfully. See you soon! 👋",
    })
  }

  return <AuthContext.Provider value={{ user, loading, login, register, logout }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
