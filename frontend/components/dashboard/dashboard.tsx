"use client"

import { useAccounts } from "@/hooks/use-accounts"
import { AccountsList } from "./accounts-list"
import { QuickActions } from "./quick-actions"
import { TransferForm } from "./transfer-form"
import { CreateAccountForm } from "./create-account-form"
import { TransactionHistory } from "./transaction-history"
import { WelcomeBanner } from "./welcome-banner"
import { LoadingSpinner } from "@/components/ui/loading-spinner"
import { useState } from "react"

export function Dashboard() {
  const { accounts, loading, refreshAccounts } = useAccounts()
  const [showTransfer, setShowTransfer] = useState(false)
  const [showCreateAccount, setShowCreateAccount] = useState(false)
  const [showTransactions, setShowTransactions] = useState(false)

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <LoadingSpinner />
      </div>
    )
  }

  return (
    <div className="max-w-6xl mx-auto space-y-8">
      <WelcomeBanner accounts={accounts} />

      <AccountsList accounts={accounts} onRefresh={refreshAccounts} />

      <QuickActions
        onTransfer={() => setShowTransfer(true)}
        onCreateAccount={() => setShowCreateAccount(true)}
        onViewTransactions={() => setShowTransactions(true)}
        onRefresh={refreshAccounts}
      />

      {showTransfer && (
        <TransferForm accounts={accounts} onClose={() => setShowTransfer(false)} onSuccess={refreshAccounts} />
      )}

      {showCreateAccount && (
        <CreateAccountForm onClose={() => setShowCreateAccount(false)} onSuccess={refreshAccounts} />
      )}

      {showTransactions && <TransactionHistory accounts={accounts} onClose={() => setShowTransactions(false)} />}
    </div>
  )
}
