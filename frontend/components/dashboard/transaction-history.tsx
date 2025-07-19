"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useToast } from "@/hooks/use-toast"
import { apiService } from "@/lib/api-service"
import { LoadingSpinner } from "@/components/ui/loading-spinner"
import { X } from "lucide-react"
import type { Account } from "@/types/account"
import type { Transaction } from "@/types/transaction"

interface TransactionHistoryProps {
  accounts: Account[]
  onClose: () => void
}

export function TransactionHistory({ accounts, onClose }: TransactionHistoryProps) {
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [filteredTransactions, setFilteredTransactions] = useState<Transaction[]>([])
  const [selectedAccountId, setSelectedAccountId] = useState("")
  const [loading, setLoading] = useState(true)
  const { toast } = useToast()

  useEffect(() => {
    loadTransactions()
  }, [accounts])

  useEffect(() => {
    if (!selectedAccountId) {
      setFilteredTransactions(transactions)
    } else {
      const filtered = transactions.filter(
        (t) =>
          t.sourceAccountId?.toString() === selectedAccountId ||
          t.destinationAccountId?.toString() === selectedAccountId,
      )
      setFilteredTransactions(filtered)
    }
  }, [transactions, selectedAccountId])

  const loadTransactions = async () => {
    if (accounts.length === 0) return

    setLoading(true)
    try {
      const allTransactions: Transaction[] = []

      for (const account of accounts) {
        try {
          const accountTransactions = await apiService.getAccountTransactions(account.id)
          allTransactions.push(...accountTransactions)
        } catch (error) {
          console.error(`Failed to load transactions for account ${account.id}:`, error)
        }
      }

      // Sort by date (newest first)
      allTransactions.sort((a, b) => new Date(b.transactionDate).getTime() - new Date(a.transactionDate).getTime())

      setTransactions(allTransactions)

      if (allTransactions.length === 0) {
        toast({
          title: "No Transactions",
          description: "No transactions found for your accounts",
        })
      }
    } catch (error) {
      toast({
        title: "Load Failed",
        description: "Failed to load transaction history",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  const getTransactionTypeColor = (type: string) => {
    switch (type) {
      case "DEPOSIT":
      case "RECEIVED":
        return "bg-green-100 text-green-800"
      case "TRANSFER":
      case "WITHDRAWAL":
        return "bg-red-100 text-red-800"
      default:
        return "bg-blue-100 text-blue-800"
    }
  }

  const getAmountDisplay = (transaction: Transaction) => {
    const amount = Number.parseFloat(transaction.amount.toString())
    const isPositive = transaction.transactionType === "DEPOSIT" || transaction.transactionType === "RECEIVED"
    return {
      amount,
      isPositive,
      display: `${isPositive ? "+" : "-"}$${amount.toLocaleString("en-US", { minimumFractionDigits: 2 })}`,
    }
  }

  return (
    <Card className="glass-card">
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle className="flex items-center space-x-2">
          <span>📊</span>
          <span>Transaction History</span>
        </CardTitle>
        <Button variant="ghost" size="sm" onClick={onClose}>
          <X className="h-4 w-4" />
        </Button>
      </CardHeader>

      <CardContent>
        <div className="space-y-4">
          <div className="flex items-center space-x-4">
            <Select value={selectedAccountId} onValueChange={setSelectedAccountId}>
              <SelectTrigger className="w-64">
                <SelectValue placeholder="All Accounts" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Accounts</SelectItem>
                {accounts.map((account) => (
                  <SelectItem key={account.id} value={account.id.toString()}>
                    {account.accountType} - {account.accountNumber}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {loading ? (
            <div className="flex items-center justify-center py-8">
              <LoadingSpinner />
            </div>
          ) : filteredTransactions.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              <p>No transactions to display</p>
            </div>
          ) : (
            <div className="space-y-3 max-h-96 overflow-y-auto">
              {filteredTransactions.map((transaction) => {
                const { amount, isPositive, display } = getAmountDisplay(transaction)

                return (
                  <div key={transaction.id} className="bg-white/80 rounded-lg p-4 border">
                    <div className="flex justify-between items-start mb-2">
                      <span
                        className={`px-2 py-1 rounded-full text-xs font-medium ${getTransactionTypeColor(transaction.transactionType)}`}
                      >
                        {transaction.transactionType}
                      </span>
                      <span className="text-sm text-gray-500">
                        {new Date(transaction.transactionDate).toLocaleString()}
                      </span>
                    </div>

                    <div className="flex justify-between items-center mb-2">
                      <div className="text-sm text-gray-600">
                        {transaction.sourceAccountNumber && <div>From: {transaction.sourceAccountNumber}</div>}
                        {transaction.destinationAccountNumber && <div>To: {transaction.destinationAccountNumber}</div>}
                      </div>
                      <div className={`text-lg font-bold ${isPositive ? "text-green-600" : "text-red-600"}`}>
                        {display}
                      </div>
                    </div>

                    {transaction.description && (
                      <div className="text-sm text-gray-500 italic">{transaction.description}</div>
                    )}
                  </div>
                )
              })}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}
