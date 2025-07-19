"use client"

import type React from "react"

import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { LoadingSpinner } from "@/components/ui/loading-spinner"
import type { Account } from "@/types/account"

interface DepositDialogProps {
  account: Account
  onDeposit: (accountId: number, amount: number) => void
  onClose: () => void
  loading: boolean
}

export function DepositDialog({ account, onDeposit, onClose, loading }: DepositDialogProps) {
  const [amount, setAmount] = useState("")

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const amountNum = Number.parseFloat(amount)
    if (amountNum > 0 && amountNum <= 1000000) {
      onDeposit(account.id, amountNum)
    }
  }

  return (
    <Dialog open={true} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>💰 Deposit to Account</DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          <div className="p-4 bg-gray-50 rounded-lg">
            <p className="text-sm text-gray-600">Account: {account.accountType}</p>
            <p className="text-sm text-gray-600">Number: {account.accountNumber}</p>
            <p className="text-sm text-gray-600">
              Current Balance: $
              {Number.parseFloat(account.balance.toString()).toLocaleString("en-US", { minimumFractionDigits: 2 })}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="depositAmount">Amount to Deposit</Label>
              <Input
                id="depositAmount"
                type="number"
                step="0.01"
                min="0.01"
                max="1000000"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="e.g., 100.00"
                disabled={loading}
                autoFocus
              />
            </div>

            <div className="flex space-x-4">
              <Button type="submit" className="flex-1" disabled={loading || !amount || Number.parseFloat(amount) <= 0}>
                {loading ? <LoadingSpinner className="mr-2" /> : null}
                Deposit
              </Button>
              <Button type="button" variant="outline" onClick={onClose} disabled={loading}>
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </DialogContent>
    </Dialog>
  )
}
