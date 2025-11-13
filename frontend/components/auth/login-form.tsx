"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { loginSchema, type LoginFormData } from "@/lib/validations/auth";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { useAuth } from "@/contexts/AuthContext";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useToast } from "@/hooks/use-toast";

export function LoginForm() {
  const { login } = useAuth();
  const router = useRouter();
  const { toast } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      identifier: "",
      password: "",
    },
  });

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);
    try {
      await login(data.identifier, data.password);
      
      // Get userType from localStorage to determine redirect
      const storedUser = localStorage.getItem("auth_user");
      if (storedUser) {
        const user = JSON.parse(storedUser);
        toast({
          title: "Success",
          description: "Logged in successfully",
        });
        
        // Store customer ID in sessionStorage for portal compatibility if customer
        if (user.userType === "CUSTOMER" && user.customerId) {
          if (typeof window !== "undefined") {
            sessionStorage.setItem("portal_customer_id", user.customerId);
            sessionStorage.setItem("portal_customer_email", user.email || "");
          }
        }
        
        // Redirect based on user type
        if (user.userType === "CUSTOMER") {
          router.replace("/portal"); // Customer dashboard
        } else {
          router.replace("/"); // Admin dashboard
        }
      } else {
        router.push("/");
      }
    } catch (error: any) {
      console.error("Login error:", error);
      const errorMessage = 
        error.response?.data?.message || 
        error.response?.data?.errors ? 
          Object.values(error.response.data.errors).join(", ") : 
          error.message || 
          "Invalid username/email or password";
      toast({
        title: "Error",
        description: errorMessage,
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
      <div className="space-y-2">
        <Label htmlFor="identifier" className="text-sm font-medium">Username or Email</Label>
        <Input
          id="identifier"
          {...register("identifier")}
          placeholder="admin or your.email@example.com"
          disabled={isLoading}
          autoComplete="username"
          className="h-11"
        />
        {errors.identifier && (
          <p className="text-sm text-destructive mt-1">{errors.identifier.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="password" className="text-sm font-medium">Password</Label>
        <Input
          id="password"
          type="password"
          {...register("password")}
          placeholder="Enter your password"
          disabled={isLoading}
          autoComplete="current-password"
          className="h-11"
        />
        {errors.password && (
          <p className="text-sm text-destructive mt-1">{errors.password.message}</p>
        )}
      </div>

      <Button 
        type="submit" 
        className="w-full h-11 text-base font-semibold" 
        disabled={isLoading}
      >
        {isLoading ? (
          <>
            <LoadingSpinner size="sm" className="mr-2" />
            Signing in...
          </>
        ) : (
          "Sign In"
        )}
      </Button>
    </form>
  );
}

