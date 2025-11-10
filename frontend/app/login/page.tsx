"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { LoginForm } from "@/components/auth/login-form";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { FileText, Shield } from "lucide-react";

export default function LoginPage() {
  const { isAuthenticated, isLoading, userType } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      // Redirect based on user type
      if (userType === "CUSTOMER") {
        router.push("/portal/invoices");
      } else {
        router.push("/"); // Admin dashboard
      }
    }
  }, [isAuthenticated, isLoading, userType, router]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0f1e35]">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (isAuthenticated) {
    return null; // Will redirect
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#0f1e35] p-4 relative">
      <div className="w-full max-w-md space-y-6 relative z-10">
        <div className="text-center space-y-2">
          <div className="mx-auto w-16 h-16 bg-[#1e3a5f] rounded-xl flex items-center justify-center shadow-lg">
            <FileText className="h-8 w-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold tracking-tight text-slate-100">InvoiceMe</h1>
          <p className="text-slate-300">Sign in to your account</p>
        </div>
        
        <Card className="shadow-xl border border-slate-700 bg-[#1e3a5f]">
          <CardHeader className="space-y-1 pb-4">
            <div className="flex items-center justify-center mb-2">
              <Shield className="h-5 w-5 text-blue-400 mr-2" />
              <CardTitle className="text-2xl font-bold text-center text-slate-100">Sign In</CardTitle>
            </div>
            <CardDescription className="text-center text-slate-300">
              Enter your username/email and password to continue
            </CardDescription>
          </CardHeader>
          <CardContent>
            <LoginForm />
          </CardContent>
        </Card>
        
        <p className="text-center text-sm text-slate-400">
          Customer portal? <a href="/portal" className="text-blue-400 hover:underline">Click here</a>
        </p>
      </div>
    </div>
  );
}

