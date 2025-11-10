"use client";

import React from "react";
import { motion } from "framer-motion";
import { CircularProgress } from "@/components/ui/circular-progress";

interface BounceCardProps {
  className?: string;
  children: React.ReactNode;
}

interface CardTitleProps {
  children: React.ReactNode;
}

const BounceCard = ({ className, children }: BounceCardProps) => {
  return (
    <motion.div
      whileHover={{ scale: 0.95, rotate: "-1deg" }}
      className={`group relative min-h-[300px] cursor-pointer overflow-hidden rounded-2xl bg-[#1e3a5f] p-8 ${className || ""}`}
    >
      {children}
    </motion.div>
  );
};

const CardTitle = ({ children }: CardTitleProps) => {
  return (
    <h3 className="mx-auto text-center text-3xl font-semibold text-slate-100">{children}</h3>
  );
};

interface CustomerDetailStatsProps {
  totalPaid: number;
  onTimePaymentPercentage: number;
  totalInvoices: number;
  isLoading?: boolean;
  customerName: string;
}

export function CustomerDetailStats({
  totalPaid,
  onTimePaymentPercentage,
  totalInvoices,
  isLoading = false,
  customerName,
}: CustomerDetailStatsProps) {
  return (
    <section className="mx-auto max-w-7xl px-4 py-12 text-slate-100">
      <div className="mb-8 flex flex-col items-start justify-between gap-4 md:flex-row md:items-end md:px-8">
        <h2 className="max-w-lg text-4xl font-bold md:text-5xl text-slate-100">
          {customerName}
          <span className="text-slate-400"> - Customer Overview</span>
        </h2>
      </div>
      <div className="mb-4 grid grid-cols-12 gap-4">
        <BounceCard className="col-span-12 md:col-span-4">
          <CardTitle>Total Paid</CardTitle>
          <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-violet-400 to-indigo-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
            <span className="block text-center font-semibold text-indigo-50 text-5xl">
              {isLoading ? "..." : `$${totalPaid.toFixed(2)}`}
            </span>
            <span className="block text-center text-sm text-indigo-100 mt-2">
              Total Amount Paid
            </span>
          </div>
        </BounceCard>
        <BounceCard className="col-span-12 md:col-span-8">
          <CardTitle>On-Time Payment Rate</CardTitle>
          <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-amber-400 to-orange-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg] flex items-center justify-center">
            {isLoading ? (
              <span className="text-6xl text-orange-50">...</span>
            ) : (
              <CircularProgress
                percentage={onTimePaymentPercentage}
                size={140}
                strokeWidth={10}
              />
            )}
          </div>
        </BounceCard>
      </div>
      <div className="grid grid-cols-12 gap-4">
        <BounceCard className="col-span-12 md:col-span-12">
          <CardTitle>Total Invoices</CardTitle>
          <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-green-400 to-emerald-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
            <span className="block text-center font-semibold text-emerald-50 text-6xl">
              {isLoading ? "..." : totalInvoices}
            </span>
            <span className="block text-center text-sm text-emerald-100 mt-2">
              Total Number of Invoices
            </span>
          </div>
        </BounceCard>
      </div>
    </section>
  );
}

