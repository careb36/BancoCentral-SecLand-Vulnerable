"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { useToast } from "@/hooks/use-toast"
import { apiService } from "@/lib/api-service"
import { DollarSign, Trash2 } from "lucide-react"
import type { Account } from "@/types/account"
import { useState } from "react"
import { DepositDialog } from "./deposit-dialog"
import { ConfirmDialog } from "@/components/ui/confirm-dialog"

interface AccountsListProps {
  accounts: Account[]
  onRefresh: () => void
}

export function AccountsList({ accounts, onRefresh }: AccountsListProps) {
  const { toast } = useToast()
  const [depositAccount, setDepositAccount] = useState<Account | null>(null)
  const [deleteAccount, setDeleteAccount] = useState<Account | null>(null)
  const [loading, setLoading] = useState(false)

  const handleDeposit = async (accountId: number, amount: number) => {
    setLoading(true)
    try {
      await apiService.depositToAccount(accountId, amount)
      toast({
        title: "Deposit Successful",
        description: `$${amount.toLocaleString("en-US", { minimumFractionDigits: 2 })} deposited successfully`,
      })
      onRefresh()
      setDepositAccount(null)
    } catch (error) {
      toast({
        title: "Deposit Failed",
        description: error instanceof Error ? error.message : "Failed to deposit funds",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (accountId: number) => {
    setLoading(true)
    try {
      await apiService.deleteAccount(accountId)
      toast({
        title: "Account Deleted",
        description: "Account deleted successfully",
      })
      onRefresh()
      setDeleteAccount(null)
    } catch (error) {
      toast({
        title: "Delete Failed",
        description: error instanceof Error ? error.message : "Failed to delete account",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  if (accounts.length === 0) {
    return (
      <Card className="glass-card">
        <CardContent className="p-8 text-center">
          <div className="text-6xl mb-4">🏦</div>
          <h3 className="text-xl font-semibold text-gray-700 mb-2">No Accounts Yet</h3>
          <p className="text-gray-600 mb-4">Create your first account to get started with banking</p>
        </CardContent>
      </Card>
    )
  }

  return (
    <>
      <Card className="glass-card">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <span>💰</span>
            <span>Your Accounts</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {accounts.map((account) => (
              <Card key={account.id} className="bg-white/80 hover:bg-white/90 transition-colors">
                <CardContent className="p-6">
                  <div className="flex justify-between items-start mb-4">
                    <h4 className="font-semibold text-gray-800">{account.accountType} Account</h4>
                    <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded font-mono">
                      {account.accountNumber}
                    </span>
                  </div>

                  <div className="text-center mb-4">
                    <p className="text-sm text-gray-600 mb-1">Balance</p>
                    <p className="text-2xl font-bold text-gray-800">
                      $
                      {Number.parseFloat(account.balance.toString()).toLocaleString("en-US", {
                        minimumFractionDigits: 2,
                      })}
                    </p>
                  </div>

                  <div className="flex space-x-2">
                    <Button size="sm" className="flex-1" onClick={() => setDepositAccount(account)} disabled={loading}>
                      <DollarSign className="h-4 w-4 mr-1" />
                      Deposit
                    </Button>
                    <Button
                      size="sm"
                      variant="destructive"
                      onClick={() => setDeleteAccount(account)}
                      disabled={loading}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </CardContent>
      </Card>

      {depositAccount && (
        <DepositDialog
          account={depositAccount}
          onDeposit={handleDeposit}
          onClose={() => setDepositAccount(null)}
          loading={loading}
        />
      )}

      {deleteAccount && (
        <ConfirmDialog
          title="Delete Account"
          description={`Are you sure you want to delete this ${deleteAccount.accountType} account (${deleteAccount.accountNumber})? This action cannot be undone.`}
          onConfirm={() => handleDelete(deleteAccount.id)}
          onCancel={() => setDeleteAccount(null)}
          loading={loading}
        />
      )}
    </>
  )
}
