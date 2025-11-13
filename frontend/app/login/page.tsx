"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { LoginForm } from "@/components/auth/login-form";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { FileText, Shield } from "lucide-react";

export default function LoginPage() {
  const { isAuthenticated, isLoading, userType } = useAuth();
  const router = useRouter();
  const [mounted, setMounted] = useState(false);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  // Timeout fallback to show form if loading takes too long
  useEffect(() => {
    if (mounted && isLoading) {
      const timer = setTimeout(() => {
        setShowForm(true);
      }, 1000);
      return () => clearTimeout(timer);
    } else if (!isLoading) {
      setShowForm(true);
    }
  }, [mounted, isLoading]);

  useEffect(() => {
    if (mounted && !isLoading && isAuthenticated) {
      // Redirect based on user type - use replace to avoid adding to history
      if (userType === "CUSTOMER") {
        router.replace("/portal"); // Customer dashboard
      } else {
        router.replace("/"); // Admin dashboard
      }
    }
  }, [isAuthenticated, isLoading, userType, router, mounted]);

  // Show loading only briefly while mounting
  if (!mounted) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0f1e35]">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  // If still loading after mount, show form anyway after timeout to prevent infinite loading
  if (isLoading && !showForm) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0f1e35]">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  // Don't show anything if authenticated (will redirect)
  if (isAuthenticated) {
    return null;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#0f1e35] p-4 relative">
      <div className="w-full max-w-md space-y-6 relative z-10">
        <div className="text-center space-y-2">
          <div className="mx-auto w-16 h-16 bg-[#1e3a5f] rounded-xl flex items-center justify-center shadow-lg">
            <FileText className="h-8 w-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold tracking-tight text-slate-100">GimmeYoMoney</h1>
          <p className="text-slate-300">Sign in to your account</p>
        </div>
        
        <Card className="shadow-xl border border-slate-700 bg-[#1e3a5f]">
          <CardHeader className="space-y-1 pb-4">
            <div className="flex items-center justify-center mb-2">
              <Shield className="h-5 w-5 text-blue-400 mr-2" />
              <CardTitle className="text-2xl font-bold text-center text-slate-100">Sign In</CardTitle>
            </div>
            <CardDescription className="text-center text-slate-300">
              Enter your username/email and password to continue. Works for both admin and customer accounts.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <LoginForm />
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

