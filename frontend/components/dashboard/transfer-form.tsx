"use client"

import type React from "react"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useToast } from "@/hooks/use-toast"
import { apiService } from "@/lib/api-service"
import { LoadingSpinner } from "@/components/ui/loading-spinner"
import { X } from "lucide-react"
import type { Account } from "@/types/account"

interface TransferFormProps {
  accounts: Account[]
  onClose: () => void
  onSuccess: () => void
}

export function TransferForm({ accounts, onClose, onSuccess }: TransferFormProps) {
  const [fromAccountId, setFromAccountId] = useState("")
  const [toAccountNumber, setToAccountNumber] = useState("")
  const [amount, setAmount] = useState("")
  const [description, setDescription] = useState("")
  const [loading, setLoading] = useState(false)
  const { toast } = useToast()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!fromAccountId || !toAccountNumber.trim() || !amount) {
      toast({
        title: "Validation Error",
        description: "Please complete all required fields",
        variant: "destructive",
      })
      return
    }

    const amountNum = Number.parseFloat(amount)
    if (amountNum <= 0) {
      toast({
        title: "Invalid Amount",
        description: "Amount must be greater than $0",
        variant: "destructive",
      })
      return
    }

    if (amountNum > 1000000) {
      toast({
        title: "Amount Too Large",
        description: "Maximum transfer amount is $1,000,000",
        variant: "destructive",
      })
      return
    }

    const sourceAccount = accounts.find((acc) => acc.id.toString() === fromAccountId)
    if (sourceAccount && amountNum > Number.parseFloat(sourceAccount.balance.toString())) {
      toast({
        title: "Insufficient Funds",
        description: `Your balance is $${Number.parseFloat(sourceAccount.balance.toString()).toLocaleString("en-US", { minimumFractionDigits: 2 })}`,
        variant: "destructive",
      })
      return
    }

    setLoading(true)
    try {
      await apiService.transferMoney({
        fromAccountId: Number.parseInt(fromAccountId),
        toAccountNumber: toAccountNumber.trim(),
        amount: amountNum,
        description: description.trim() || "Transfer",
      })

      toast({
        title: "Transfer Successful",
        description: `$${amountNum.toLocaleString("en-US", { minimumFractionDigits: 2 })} transferred successfully`,
      })

      onSuccess()
      onClose()
    } catch (error) {
      toast({
        title: "Transfer Failed",
        description: error instanceof Error ? error.message : "Transfer failed",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card className="glass-card">
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle className="flex items-center space-x-2">
          <span>💸</span>
          <span>Transfer Money</span>
        </CardTitle>
        <Button variant="ghost" size="sm" onClick={onClose}>
          <X className="h-4 w-4" />
        </Button>
      </CardHeader>

      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="fromAccount">From Account</Label>
            <Select value={fromAccountId} onValueChange={setFromAccountId}>
              <SelectTrigger>
                <SelectValue placeholder="Select source account" />
              </SelectTrigger>
              <SelectContent>
                {accounts.map((account) => (
                  <SelectItem key={account.id} value={account.id.toString()}>
                    {account.accountType} - {account.accountNumber}
                    ($
                    {Number.parseFloat(account.balance.toString()).toLocaleString("en-US", {
                      minimumFractionDigits: 2,
                    })}
                    )
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="toAccount">To Account Number</Label>
            <Input
              id="toAccount"
              value={toAccountNumber}
              onChange={(e) => setToAccountNumber(e.target.value)}
              placeholder="Enter destination account number"
              disabled={loading}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="amount">Amount</Label>
            <Input
              id="amount"
              type="number"
              step="0.01"
              min="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="Enter amount"
              disabled={loading}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Description (Optional)</Label>
            <Input
              id="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Optional description"
              disabled={loading}
            />
          </div>

          <div className="flex space-x-4 pt-4">
            <Button type="submit" className="flex-1" disabled={loading}>
              {loading ? <LoadingSpinner className="mr-2" /> : null}
              Transfer
            </Button>
            <Button type="button" variant="outline" onClick={onClose} disabled={loading}>
              Cancel
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
