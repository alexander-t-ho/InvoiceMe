"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { useToast } from "@/hooks/use-toast";
import { customerService } from "@/lib/services/CustomerService";
import { FileText } from "lucide-react";

const loginSchema = z.object({
  email: z.string().email("Please enter a valid email address"),
  password: z.string().min(1, "Password is required"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function CustomerPortalPage() {
  const router = useRouter();
  const { toast } = useToast();
  const [isLoading, setIsLoading] = useState(false);
  const emailInputRef = useRef<HTMLInputElement | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const { ref: emailRef, ...emailRegister } = register("email");
  const { ref: passwordRef, ...passwordRegister } = register("password");

  // Handle client-side mounting and auto-focus
  useEffect(() => {
    if (emailInputRef.current) {
      emailInputRef.current.focus();
    }
  }, []);

  // Combined ref callback
  const setEmailRef = (element: HTMLInputElement | null) => {
    emailRef(element);
    emailInputRef.current = element;
  };

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);
    try {
      const customer = await customerService.authenticateCustomer(data.email, data.password);
      // Store customer ID in sessionStorage for the portal session
      if (typeof window !== "undefined") {
        sessionStorage.setItem("portal_customer_id", customer.id);
        sessionStorage.setItem("portal_customer_email", customer.email);
      }
      router.push("/portal/invoices");
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Invalid email or password. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#0f1e35] p-4 relative">
      <Card className="w-full max-w-md bg-[#1e3a5f] border-slate-700 relative z-10">
        <CardHeader className="text-center">
          <div className="mx-auto mb-4 w-12 h-12 bg-[#2a4d75] rounded-full flex items-center justify-center">
            <FileText className="h-6 w-6 text-blue-400" />
          </div>
          <CardTitle className="text-2xl text-slate-100">Customer Portal</CardTitle>
          <CardDescription className="text-slate-300">
            Enter your email and password to view your invoices and make payments
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email">Email Address</Label>
              <Input
                id="email"
                type="email"
                placeholder="your.email@example.com"
                {...emailRegister}
                disabled={isLoading}
                ref={setEmailRef}
              />
              <p className={`text-sm min-h-[1.25rem] ${errors.email ? 'text-destructive' : 'text-transparent'}`}>
                {errors.email?.message || '\u00A0'}
              </p>
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                placeholder="Enter your password"
                {...passwordRegister}
                disabled={isLoading}
                ref={passwordRef}
              />
              <p className={`text-sm min-h-[1.25rem] ${errors.password ? 'text-destructive' : 'text-transparent'}`}>
                {errors.password?.message || '\u00A0'}
              </p>
            </div>
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? (
                <>
                  <LoadingSpinner size="sm" className="mr-2" />
                  Loading...
                </>
              ) : (
                "View My Invoices"
              )}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

