export interface Transaction {
  id: number
  transactionType: string
  amount: number
  description?: string
  transactionDate: string
  sourceAccountId?: number
  destinationAccountId?: number
  sourceAccountNumber?: string
  destinationAccountNumber?: string
}
