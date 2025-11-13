import { cn } from "@/lib/utils";

interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {
  className?: string;
}

export function Skeleton({ className, ...props }: SkeletonProps) {
  return (
    <div
      className={cn("animate-pulse rounded-md bg-slate-700/50", className)}
      {...props}
    />
  );
}

/**
 * Skeleton for table rows
 */
export function TableRowSkeleton({ cols = 4 }: { cols?: number }) {
  return (
    <tr>
      {Array.from({ length: cols }).map((_, i) => (
        <td key={i} className="px-4 py-3">
          <Skeleton className="h-4 w-full" />
        </td>
      ))}
    </tr>
  );
}

/**
 * Skeleton for table with multiple rows
 */
export function TableSkeleton({ rows = 5, cols = 4 }: { rows?: number; cols?: number }) {
  return (
    <div className="rounded-md border border-slate-700">
      <div className="p-4">
        <Skeleton className="h-6 w-48 mb-4" />
        <div className="space-y-3">
          {Array.from({ length: rows }).map((_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      </div>
    </div>
  );
}

/**
 * Skeleton for card components
 */
export function CardSkeleton({ className }: { className?: string }) {
  return (
    <div className={cn("rounded-lg border border-slate-700 p-6", className)}>
      <Skeleton className="h-6 w-32 mb-4" />
      <Skeleton className="h-8 w-24 mb-2" />
      <Skeleton className="h-4 w-48" />
    </div>
  );
}

/**
 * Skeleton for dashboard stats cards
 */
export function StatsCardSkeleton() {
  return (
    <div className="rounded-lg border border-slate-700 p-6 bg-slate-800/50">
      <Skeleton className="h-4 w-24 mb-2" />
      <Skeleton className="h-8 w-32 mb-1" />
      <Skeleton className="h-3 w-16" />
    </div>
  );
}

