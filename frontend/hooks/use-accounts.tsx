"use client"

import { useState, useEffect } from "react"
import { apiService } from "@/lib/api-service"
import { useAuth } from "@/hooks/use-auth"
import { useToast } from "@/hooks/use-toast"
import type { Account } from "@/types/account"

export function useAccounts() {
  const [accounts, setAccounts] = useState<Account[]>([])
  const [loading, setLoading] = useState(true)
  const { user } = useAuth()
  const { toast } = useToast()

  useEffect(() => {
    if (user) {
      loadAccounts()
    }
  }, [user])

  const loadAccounts = async () => {
    if (!user) return

    setLoading(true)
    try {
      const userAccounts = await apiService.getUserAccounts()
      setAccounts(userAccounts)

      if (userAccounts.length === 0) {
        toast({
          title: "No Accounts",
          description: "Create your first account to get started!",
        })
      }
    } catch (error) {
      console.error("Failed to load accounts:", error)
      toast({
        title: "Load Failed",
        description: "Failed to load your accounts. Please try again.",
        variant: "destructive",
      })
    } finally {
      setLoading(false)
    }
  }

  const refreshAccounts = async () => {
    await loadAccounts()
  }

  return {
    accounts,
    loading,
    refreshAccounts,
  }
}
