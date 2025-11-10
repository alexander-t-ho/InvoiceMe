import { Badge } from "@/components/ui/badge";
import type { InvoiceStatus } from "@/types/api";

interface InvoiceStatusBadgeProps {
  status: InvoiceStatus;
}

export function InvoiceStatusBadge({ status }: InvoiceStatusBadgeProps) {
  const variants: Record<InvoiceStatus, "default" | "secondary" | "outline"> = {
    DRAFT: "outline",
    SENT: "secondary",
    PAID: "default",
  };

  return (
    <Badge variant={variants[status]}>
      {status}
    </Badge>
  );
}

