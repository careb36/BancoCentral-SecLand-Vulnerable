"use client"

import { useAuth } from "@/hooks/use-auth"
import { Button } from "@/components/ui/button"
import { LogOut } from "lucide-react"

export function Header() {
  const { user, logout } = useAuth()

  return (
    <header className="glass-header sticky top-0 z-50">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <span className="text-2xl">🏦</span>
            <div>
              <h1 className="text-xl font-bold text-gray-800">CentralBank SecLand</h1>
              <p className="text-sm text-gray-600">Secure Banking Portal</p>
            </div>
          </div>

          {user && (
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-600">Welcome, {user.fullName}</span>
              <Button
                variant="outline"
                size="sm"
                onClick={logout}
                className="flex items-center space-x-2 bg-transparent"
              >
                <LogOut className="h-4 w-4" />
                <span>Logout</span>
              </Button>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}
