"use client"

import { useAuth } from "@/hooks/use-auth"
import { LoginForm } from "@/components/auth/login-form"
import { RegisterForm } from "@/components/auth/register-form"
import { Dashboard } from "@/components/dashboard/dashboard"
import { Header } from "@/components/layout/header"
import { Footer } from "@/components/layout/footer"
import { LoadingSpinner } from "@/components/ui/loading-spinner"
import { useState } from "react"

export default function Home() {
  const { user, loading } = useAuth()
  const [showRegister, setShowRegister] = useState(false)

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <LoadingSpinner />
      </div>
    )
  }

  return (
    <div className="min-h-screen flex flex-col">
      <Header />

      <main className="flex-1 container mx-auto px-4 py-8">
        {user ? (
          <Dashboard />
        ) : (
          <div className="flex items-center justify-center min-h-[70vh]">
            {showRegister ? (
              <RegisterForm onSwitchToLogin={() => setShowRegister(false)} />
            ) : (
              <LoginForm onSwitchToRegister={() => setShowRegister(true)} />
            )}
          </div>
        )}
      </main>

      <Footer />
    </div>
  )
}
