"use client";

import React from "react";
import Link from "next/link";
import { useQueryClient } from "@tanstack/react-query";
import { motion } from "framer-motion";
import { prefetchRouteData } from "@/lib/prefetch-enhanced";

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

interface CustomerDashboardCardsProps {
  totalInvoices?: number;
  totalPaid?: number;
  outstandingBalance?: number;
  overdueInvoices?: number;
  unpaidInvoices?: number;
  soonestDueInvoiceId?: string | null;
  isLoading?: boolean;
  customerName?: string;
}

export const CustomerDashboardCards = ({
  totalInvoices = 0,
  totalPaid = 0,
  outstandingBalance = 0,
  overdueInvoices = 0,
  unpaidInvoices = 0,
  soonestDueInvoiceId = null,
  isLoading = false,
  customerName = "Customer",
}: CustomerDashboardCardsProps) => {
  const queryClient = useQueryClient();

  return (
    <section className="mx-auto max-w-7xl px-4 py-12 text-slate-100">
      <div className="mb-8 flex flex-col items-start justify-between gap-4 md:flex-row md:items-end md:px-8">
        <h2 className="max-w-lg text-4xl font-bold md:text-5xl text-slate-100">
          Welcome back, {customerName}
          <span className="text-slate-400"> - Your Dashboard</span>
        </h2>
      </div>
      <div className="mb-4 grid grid-cols-12 gap-4">
        <Link 
          href="/portal/invoices" 
          prefetch={true} 
          onMouseEnter={() => prefetchRouteData(queryClient, "/portal/invoices")}
          className="col-span-12 md:col-span-4"
        >
          <BounceCard className="h-full">
            <CardTitle>Total Invoices</CardTitle>
            <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-violet-400 to-indigo-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
              <span className="block text-center font-semibold text-indigo-50 text-6xl">
                {isLoading ? "..." : totalInvoices}
              </span>
              <span className="block text-center text-sm text-indigo-100 mt-2">
                All Invoices
              </span>
            </div>
          </BounceCard>
        </Link>
        {soonestDueInvoiceId ? (
          <Link href={`/portal/invoices/${soonestDueInvoiceId}`} prefetch={true} className="col-span-12 md:col-span-4">
            <BounceCard className="h-full">
              <CardTitle>Pay Now</CardTitle>
              <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-blue-400 to-cyan-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
                <span className="block text-center font-semibold text-cyan-50 text-5xl">
                  {isLoading ? "..." : "→"}
                </span>
                <span className="block text-center text-sm text-cyan-100 mt-2">
                  Pay Soonest Due
                </span>
              </div>
            </BounceCard>
          </Link>
        ) : (
          <BounceCard className="col-span-12 md:col-span-4">
            <CardTitle>Pay Now</CardTitle>
            <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-slate-400 to-slate-500 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
              <span className="block text-center font-semibold text-slate-50 text-5xl">
                {isLoading ? "..." : "—"}
              </span>
              <span className="block text-center text-sm text-slate-100 mt-2">
                No Unpaid Invoices
              </span>
            </div>
          </BounceCard>
        )}
        <BounceCard className="col-span-12 md:col-span-4">
          <CardTitle>Total Paid</CardTitle>
          <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-amber-400 to-orange-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
            <span className="block text-center font-semibold text-orange-50 text-5xl">
              {isLoading ? "..." : `$${totalPaid.toFixed(2)}`}
            </span>
            <span className="block text-center text-sm text-orange-100 mt-2">
              Total Amount Paid
            </span>
          </div>
        </BounceCard>
      </div>
      <div className="grid grid-cols-12 gap-4">
        <BounceCard className="col-span-12 md:col-span-6">
          <CardTitle>Outstanding Balance</CardTitle>
          <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-green-400 to-emerald-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
            <span className="block text-center font-semibold text-emerald-50 text-5xl">
              {isLoading ? "..." : `$${outstandingBalance.toFixed(2)}`}
            </span>
            <span className="block text-center text-sm text-emerald-100 mt-2">
              Amount Owed
            </span>
          </div>
        </BounceCard>
        <BounceCard className="col-span-12 md:col-span-3">
          <CardTitle>Overdue</CardTitle>
          <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-pink-400 to-red-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
            <span className="block text-center font-semibold text-red-50 text-6xl">
              {isLoading ? "..." : overdueInvoices}
            </span>
            <span className="block text-center text-sm text-red-100 mt-2">
              Overdue Invoices
            </span>
          </div>
        </BounceCard>
        <BounceCard className="col-span-12 md:col-span-3">
          <CardTitle>Unpaid Invoices</CardTitle>
          <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-amber-400 to-orange-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
            <span className="block text-center font-semibold text-orange-50 text-6xl">
              {isLoading ? "..." : unpaidInvoices}
            </span>
            <span className="block text-center text-sm text-orange-100 mt-2">
              Unpaid Invoices
            </span>
          </div>
        </BounceCard>
      </div>
    </section>
  );
};

