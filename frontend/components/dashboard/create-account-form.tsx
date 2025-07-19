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

interface CreateAccountFormProps {
  onClose: () => void
  onSuccess: () => void
}

export function CreateAccountForm({ onClose, onSuccess }: CreateAccountFormProps) {
  const [accountType, setAccountType] = useState("")
  const [initialDeposit, setInitialDeposit] = useState("")
  const [loading, setLoading] = useState(false)
  const { toast } = useToast()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!accountType) {
      toast({
        title: "Validation Error",
        description: "Please select an account type",
        variant: "destructive",
      })
      return
    }

    const depositAmount = Number.parseFloat(initialDeposit) || 0
    if (depositAmount < 0) {
      toast({
        title: "Invalid Deposit",
        description: "Initial deposit must be 0 or greater",
        variant: "destructive",
      })
      return
    }

    if (depositAmount > 1000000) {
      toast({
        title: "Deposit Too Large",
        description: "Maximum initial deposit is $1,000,000",
        variant: "destructive",
      })
      return
    }

    setLoading(true)
    try {
      await apiService.createAccount({
        accountType,
        initialDeposit: depositAmount,
      })

      const depositText =
        depositAmount > 0
          ? ` with initial deposit of $${depositAmount.toLocaleString("en-US", { minimumFractionDigits: 2 })}`
          : ""

      toast({
        title: "Account Created",
        description: `New ${accountType} account created successfully${depositText}! 🎉`,
      })

      onSuccess()
      onClose()
    } catch (error) {
      toast({
        title: "Account Creation Failed",
        description: error instanceof Error ? error.message : "Failed to create account",
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
          <span>➕</span>
          <span>Create New Account</span>
        </CardTitle>
        <Button variant="ghost" size="sm" onClick={onClose}>
          <X className="h-4 w-4" />
        </Button>
      </CardHeader>

      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="accountType">Account Type</Label>
            <Select value={accountType} onValueChange={setAccountType}>
              <SelectTrigger>
                <SelectValue placeholder="Select account type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="Savings">Savings Account</SelectItem>
                <SelectItem value="Checking">Checking Account</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="initialDeposit">Initial Deposit ($)</Label>
            <Input
              id="initialDeposit"
              type="number"
              step="0.01"
              min="0"
              value={initialDeposit}
              onChange={(e) => setInitialDeposit(e.target.value)}
              placeholder="Enter initial deposit amount"
              disabled={loading}
            />
          </div>

          <div className="flex space-x-4 pt-4">
            <Button type="submit" className="flex-1" disabled={loading}>
              {loading ? <LoadingSpinner className="mr-2" /> : null}
              Create Account
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
