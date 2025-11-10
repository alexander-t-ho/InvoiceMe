"use client";

import React from "react";
import { cn } from "@/lib/utils";

interface CircularProgressProps {
  percentage: number;
  size?: number;
  strokeWidth?: number;
  className?: string;
  showLabel?: boolean;
}

export function CircularProgress({
  percentage,
  size = 120,
  strokeWidth = 8,
  className,
  showLabel = true,
}: CircularProgressProps) {
  const radius = (size - strokeWidth) / 2;
  const circumference = radius * 2 * Math.PI;
  const offset = circumference - (percentage / 100) * circumference;

  // Determine color based on percentage
  const getColor = (percent: number) => {
    if (percent >= 80) return "text-green-400";
    if (percent >= 60) return "text-yellow-400";
    if (percent >= 40) return "text-orange-400";
    return "text-red-400";
  };

  const strokeColor = getColor(percentage);

  return (
    <div className={cn("relative inline-flex items-center justify-center", className)}>
      <svg
        width={size}
        height={size}
        className="transform -rotate-90"
      >
        {/* Background circle */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          stroke="currentColor"
          strokeWidth={strokeWidth}
          fill="none"
          className="text-slate-700"
        />
        {/* Progress circle */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          stroke="currentColor"
          strokeWidth={strokeWidth}
          fill="none"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          className={cn("transition-all duration-500 ease-in-out", strokeColor)}
        />
      </svg>
      {showLabel && (
        <div className="absolute inset-0 flex items-center justify-center">
          <span className={cn("text-2xl font-bold", strokeColor)}>
            {Math.round(percentage)}%
          </span>
        </div>
      )}
    </div>
  );
}

