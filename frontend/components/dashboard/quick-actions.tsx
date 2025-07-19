"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ArrowRightLeft, RefreshCw, BarChart3, Plus } from "lucide-react"

interface QuickActionsProps {
  onTransfer: () => void
  onCreateAccount: () => void
  onViewTransactions: () => void
  onRefresh: () => void
}

export function QuickActions({ onTransfer, onCreateAccount, onViewTransactions, onRefresh }: QuickActionsProps) {
  return (
    <Card className="glass-card">
      <CardHeader>
        <CardTitle className="flex items-center space-x-2">
          <span>⚡</span>
          <span>Quick Actions</span>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Button onClick={onTransfer} className="h-16 flex flex-col items-center justify-center space-y-2">
            <ArrowRightLeft className="h-6 w-6" />
            <span>Transfer Money</span>
          </Button>

          <Button
            onClick={onRefresh}
            variant="outline"
            className="h-16 flex flex-col items-center justify-center space-y-2 bg-transparent"
          >
            <RefreshCw className="h-6 w-6" />
            <span>Refresh Accounts</span>
          </Button>

          <Button
            onClick={onViewTransactions}
            variant="outline"
            className="h-16 flex flex-col items-center justify-center space-y-2 bg-transparent"
          >
            <BarChart3 className="h-6 w-6" />
            <span>View Transactions</span>
          </Button>

          <Button
            onClick={onCreateAccount}
            variant="outline"
            className="h-16 flex flex-col items-center justify-center space-y-2 bg-transparent"
          >
            <Plus className="h-6 w-6" />
            <span>Create Account</span>
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}
