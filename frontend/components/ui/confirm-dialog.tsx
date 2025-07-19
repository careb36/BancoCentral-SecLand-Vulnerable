"use client"

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { LoadingSpinner } from "@/components/ui/loading-spinner"

interface ConfirmDialogProps {
  title: string
  description: string
  onConfirm: () => void
  onCancel: () => void
  loading?: boolean
}

export function ConfirmDialog({ title, description, onConfirm, onCancel, loading }: ConfirmDialogProps) {
  return (
    <Dialog open={true} onOpenChange={onCancel}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>

        <div className="flex space-x-4 pt-4">
          <Button variant="destructive" onClick={onConfirm} disabled={loading} className="flex-1">
            {loading ? <LoadingSpinner className="mr-2" /> : null}
            Confirm
          </Button>
          <Button variant="outline" onClick={onCancel} disabled={loading} className="flex-1 bg-transparent">
            Cancel
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}
