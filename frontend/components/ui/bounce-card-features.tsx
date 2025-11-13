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

interface BouncyCardsFeaturesProps {
  totalCustomers?: number;
  totalInvoices?: number;
  totalPayments?: number;
  totalRevenue?: number;
  totalItems?: number;
  isLoading?: boolean;
}

export const BouncyCardsFeatures = ({
  totalCustomers = 0,
  totalInvoices = 0,
  totalPayments = 0,
  totalRevenue = 0,
  totalItems = 0,
  isLoading = false,
}: BouncyCardsFeaturesProps) => {
  const queryClient = useQueryClient();

  return (
    <section className="mx-auto max-w-7xl px-4 py-12 text-slate-100">
      <div className="mb-8 flex flex-col items-start justify-between gap-4 md:flex-row md:items-end md:px-8">
        <h2 className="max-w-lg text-4xl font-bold md:text-5xl text-slate-100">
          Welcome to GimmeYoMoney
          <span className="text-slate-400"> - Your ERP Dashboard</span>
        </h2>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="whitespace-nowrap rounded-lg bg-[#1e3a5f] px-4 py-2 font-medium text-white shadow-xl transition-colors hover:bg-[#2a4d75]"
        >
          Get Started
        </motion.button>
      </div>
      <div className="mb-4 grid grid-cols-12 gap-4">
        <Link 
          href="/customers" 
          prefetch={true} 
          onMouseEnter={() => prefetchRouteData(queryClient, "/customers")}
          className="col-span-12 md:col-span-4"
        >
          <BounceCard className="h-full">
            <CardTitle>Customers</CardTitle>
            <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-violet-400 to-indigo-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
              <span className="block text-center font-semibold text-indigo-50 text-6xl">
                {isLoading ? "..." : totalCustomers}
              </span>
              <span className="block text-center text-sm text-indigo-100 mt-2">
                Total Customers
              </span>
            </div>
          </BounceCard>
        </Link>
        <Link 
          href="/invoices" 
          prefetch={true} 
          onMouseEnter={() => prefetchRouteData(queryClient, "/invoices")}
          className="col-span-12 md:col-span-8"
        >
          <BounceCard className="h-full">
            <CardTitle>Invoices</CardTitle>
            <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-amber-400 to-orange-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
              <span className="block text-center font-semibold text-orange-50 text-6xl">
                {isLoading ? "..." : totalInvoices}
              </span>
              <span className="block text-center text-sm text-orange-100 mt-2">
                Total Invoices
              </span>
            </div>
          </BounceCard>
        </Link>
      </div>
      <div className="grid grid-cols-12 gap-4">
        <BounceCard className="col-span-12 md:col-span-6">
          <CardTitle>Revenue</CardTitle>
          <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-green-400 to-emerald-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
            <span className="block text-center font-semibold text-emerald-50 text-5xl">
              {isLoading ? "..." : `$${totalRevenue.toFixed(2)}`}
            </span>
            <span className="block text-center text-sm text-emerald-100 mt-2">
              Total Revenue
            </span>
          </div>
        </BounceCard>
        <Link 
          href="/items" 
          prefetch={true} 
          onMouseEnter={() => prefetchRouteData(queryClient, "/items")}
          className="col-span-12 md:col-span-6"
        >
          <BounceCard className="h-full">
            <CardTitle>Items</CardTitle>
            <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-blue-400 to-cyan-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
              <span className="block text-center font-semibold text-cyan-50 text-6xl">
                {isLoading ? "..." : totalItems}
              </span>
              <span className="block text-center text-sm text-cyan-100 mt-2">
                Total Items
              </span>
            </div>
          </BounceCard>
        </Link>
        <Link 
          href="/payments" 
          prefetch={true} 
          onMouseEnter={() => prefetchRouteData(queryClient, "/payments")}
          className="col-span-12 md:col-span-12"
        >
          <BounceCard className="h-full">
            <CardTitle>Payments</CardTitle>
            <div className="absolute bottom-0 left-4 right-4 top-32 translate-y-8 rounded-t-2xl bg-gradient-to-br from-pink-400 to-red-400 p-4 transition-transform duration-[250ms] group-hover:translate-y-4 group-hover:rotate-[2deg]">
              <span className="block text-center font-semibold text-red-50 text-5xl">
                {isLoading ? "..." : `$${totalPayments.toFixed(2)}`}
              </span>
              <span className="block text-center text-sm text-red-100 mt-2">
                Total Payments
              </span>
            </div>
          </BounceCard>
        </Link>
      </div>
    </section>
  );
};

