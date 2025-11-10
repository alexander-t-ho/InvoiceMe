"use client";

import { Button } from "@/components/ui/button";
import { customersApi } from "@/lib/api/customers";
import { useToast } from "@/hooks/use-toast";
import { useState } from "react";

/**
 * Test component to verify API connection from the browser.
 * This can be added to the dashboard temporarily for testing.
 */
export function TestApiButton() {
  const { toast } = useToast();
  const [loading, setLoading] = useState(false);

  const testConnection = async () => {
    setLoading(true);
    try {
      const result = await customersApi.getAll(0, 5);
      toast({
        title: "✅ API Connection Successful",
        description: `Retrieved ${result.content.length} customers. Total: ${result.totalElements}`,
      });
    } catch (error: any) {
      toast({
        title: "❌ API Connection Failed",
        description: error.message || "Could not connect to backend API",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Button onClick={testConnection} disabled={loading}>
      {loading ? "Testing..." : "Test API Connection"}
    </Button>
  );
}

