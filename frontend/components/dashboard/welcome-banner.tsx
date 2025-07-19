"use client"

import { useAuth } from "@/hooks/use-auth"
import { Card, CardContent } from "@/components/ui/card"
import type { Account } from "@/types/account"

interface WelcomeBannerProps {
  accounts: Account[]
}

export function WelcomeBanner({ accounts }: WelcomeBannerProps) {
  const { user } = useAuth()

  const totalBalance = accounts.reduce((sum, account) => sum + Number.parseFloat(account.balance.toString()), 0)

  return (
    <Card className="glass-card">
      <CardContent className="p-8 text-center">
        <h2 className="text-3xl font-bold text-gray-800 mb-2">Welcome, {user?.fullName}! 👋</h2>
        <p className="text-gray-600 text-lg">Manage your accounts and transactions securely</p>
        {accounts.length > 0 && (
          <div className="mt-4 p-4 bg-blue-50 rounded-lg">
            <p className="text-sm text-blue-700">
              You have {accounts.length} account(s) with a total balance of{" "}
              <span className="font-bold">${totalBalance.toLocaleString("en-US", { minimumFractionDigits: 2 })}</span>
            </p>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
