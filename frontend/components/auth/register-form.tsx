"use client"

import type React from "react"

import { useState } from "react"
import { useAuth } from "@/hooks/use-auth"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { LoadingSpinner } from "@/components/ui/loading-spinner"
import { UserPlus } from "lucide-react"

interface RegisterFormProps {
  onSwitchToLogin: () => void
}

export function RegisterForm({ onSwitchToLogin }: RegisterFormProps) {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [fullName, setFullName] = useState("")
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState(false)
  const { register } = useAuth()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setSuccess(false)

    if (!username.trim() || !password || !fullName.trim()) {
      setError("Please complete all required fields")
      return
    }

    if (password.length < 8) {
      setError("Password must be at least 8 characters long")
      return
    }

    if (username.length < 3) {
      setError("Username must be at least 3 characters long")
      return
    }

    setLoading(true)
    try {
      await register(username.trim(), password, fullName.trim())
      setSuccess(true)
      setTimeout(() => {
        onSwitchToLogin()
      }, 2000)
    } catch (err) {
      setError(err instanceof Error ? err.message : "Registration failed")
    } finally {
      setLoading(false)
    }
  }

  if (success) {
    return (
      <Card className="w-full max-w-md glass-card">
        <CardContent className="pt-6">
          <div className="text-center">
            <div className="text-6xl mb-4">✅</div>
            <h3 className="text-xl font-semibold text-green-600 mb-2">Account Created Successfully!</h3>
            <p className="text-gray-600">You can now login with your new credentials.</p>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="w-full max-w-md glass-card">
      <CardHeader className="text-center">
        <div className="flex justify-center mb-4">
          <UserPlus className="h-12 w-12 text-blue-600" />
        </div>
        <CardTitle className="text-2xl">📝 Create Account</CardTitle>
        <CardDescription>Join our secure banking platform</CardDescription>
      </CardHeader>

      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="regUsername">Username</Label>
            <Input
              id="regUsername"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Choose a username"
              autoComplete="username"
              disabled={loading}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="regPassword">Password</Label>
            <Input
              id="regPassword"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Choose a password"
              autoComplete="new-password"
              disabled={loading}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="regFullName">Full Name</Label>
            <Input
              id="regFullName"
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="Enter your full name"
              autoComplete="name"
              disabled={loading}
            />
          </div>

          {error && (
            <Alert variant="destructive">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? <LoadingSpinner className="mr-2" /> : null}
            Register
          </Button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-sm text-gray-600">
            Already have an account?{" "}
            <button type="button" onClick={onSwitchToLogin} className="text-blue-600 hover:text-blue-500 font-medium">
              Login here
            </button>
          </p>
        </div>
      </CardContent>
    </Card>
  )
}
