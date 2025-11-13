"use client";

import { useEffect, useState } from "react";
import { Clock, AlertCircle } from "lucide-react";

interface PaymentCountdownProps {
  dueDate: string;
  status?: string;
}

export function PaymentCountdown({ dueDate, status }: PaymentCountdownProps) {
  const [timeRemaining, setTimeRemaining] = useState<{
    days: number;
    hours: number;
    minutes: number;
    seconds: number;
    isOverdue: boolean;
  } | null>(null);

  useEffect(() => {
    const calculateTimeRemaining = () => {
      const now = new Date().getTime();
      const due = new Date(dueDate).getTime();
      const difference = due - now;

      if (difference <= 0) {
        // Payment is overdue
        const overdue = Math.abs(difference);
        const days = Math.floor(overdue / (1000 * 60 * 60 * 24));
        const hours = Math.floor((overdue % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((overdue % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((overdue % (1000 * 60)) / 1000);

        setTimeRemaining({
          days,
          hours,
          minutes,
          seconds,
          isOverdue: true,
        });
      } else {
        // Payment is not yet due
        const days = Math.floor(difference / (1000 * 60 * 60 * 24));
        const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((difference % (1000 * 60)) / 1000);

        setTimeRemaining({
          days,
          hours,
          minutes,
          seconds,
          isOverdue: false,
        });
      }
    };

    // Calculate immediately
    calculateTimeRemaining();

    // Update every second
    const interval = setInterval(calculateTimeRemaining, 1000);

    return () => clearInterval(interval);
  }, [dueDate]);

  // Don't show countdown if invoice is already paid
  if (status === "PAID" || !timeRemaining) {
    return null;
  }

  const { days, hours, minutes, seconds, isOverdue } = timeRemaining;

  // Determine styling based on time remaining
  const getColorClasses = () => {
    if (isOverdue) {
      return "bg-red-500/20 border-red-500/50 text-red-300";
    }
    if (days === 0) {
      return "bg-orange-500/20 border-orange-500/50 text-orange-300";
    }
    if (days <= 3) {
      return "bg-yellow-500/20 border-yellow-500/50 text-yellow-300";
    }
    return "bg-blue-500/20 border-blue-500/50 text-blue-300";
  };

  const formatTime = () => {
    if (isOverdue) {
      if (days > 0) {
        return `${days}d ${hours}h ${minutes}m overdue`;
      }
      if (hours > 0) {
        return `${hours}h ${minutes}m ${seconds}s overdue`;
      }
      if (minutes > 0) {
        return `${minutes}m ${seconds}s overdue`;
      }
      return `${seconds}s overdue`;
    }

    if (days > 0) {
      return `${days}d ${hours}h ${minutes}m`;
    }
    if (hours > 0) {
      return `${hours}h ${minutes}m ${seconds}s`;
    }
    if (minutes > 0) {
      return `${minutes}m ${seconds}s`;
    }
    return `${seconds}s`;
  };

  return (
    <div
      className={`flex items-center gap-2 px-4 py-3 rounded-lg border ${getColorClasses()} print:hidden`}
    >
      {isOverdue ? (
        <AlertCircle className="h-5 w-5 flex-shrink-0" />
      ) : (
        <Clock className="h-5 w-5 flex-shrink-0" />
      )}
      <div className="flex-1">
        <div className="text-sm font-medium">
          {isOverdue ? "Payment Overdue" : "Time Remaining"}
        </div>
        <div className="text-lg font-bold">{formatTime()}</div>
      </div>
    </div>
  );
}



