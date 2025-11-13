import { QueryClient } from "@tanstack/react-query";
import { customerService } from "@/lib/services/CustomerService";
import { invoiceService } from "@/lib/services/InvoiceService";
import { itemsApi } from "@/lib/api/items";

/**
 * Prefetch data for a specific route to improve navigation performance.
 * This should be called on link hover or focus to preload data before navigation.
 */
export function prefetchRouteData(queryClient: QueryClient, route: string) {
  const staleTime = 5 * 60 * 1000; // 5 minutes

  switch (route) {
    case "/":
      // Prefetch dashboard data (minimal data for counts)
      queryClient.prefetchQuery({
        queryKey: ["customers", 0, 1],
        queryFn: () => customerService.getAllCustomers(0, 1),
        staleTime,
      });
      queryClient.prefetchQuery({
        queryKey: ["invoices", { page: 0, size: 1 }],
        queryFn: () => invoiceService.getAllInvoices({ page: 0, size: 1 }),
        staleTime,
      });
      queryClient.prefetchQuery({
        queryKey: ["invoices", { status: "PAID", page: 0, size: 10 }],
        queryFn: () => invoiceService.getAllInvoices({ status: "PAID", page: 0, size: 10 }),
        staleTime,
      });
      queryClient.prefetchQuery({
        queryKey: ["items", 0, 1],
        queryFn: () => itemsApi.getAll(0, 1),
        staleTime,
      });
      break;
    case "/customers":
      queryClient.prefetchQuery({
        queryKey: ["customers", 0, 20],
        queryFn: () => customerService.getAllCustomers(0, 20),
        staleTime,
      });
      break;
    case "/invoices":
      queryClient.prefetchQuery({
        queryKey: ["invoices", { page: 0, size: 20 }],
        queryFn: () => invoiceService.getAllInvoices({ page: 0, size: 20 }),
        staleTime,
      });
      break;
    case "/items":
      queryClient.prefetchQuery({
        queryKey: ["items", 0, 20],
        queryFn: () => itemsApi.getAll(0, 20),
        staleTime,
      });
      break;
    case "/payments":
      // Prefetch payments data if you have a payments endpoint
      queryClient.prefetchQuery({
        queryKey: ["invoices", { status: "PAID", page: 0, size: 20 }],
        queryFn: () => invoiceService.getAllInvoices({ status: "PAID", page: 0, size: 20 }),
        staleTime,
      });
      break;
    case "/portal":
      // Prefetch customer portal dashboard data
      queryClient.prefetchQuery({
        queryKey: ["customer-invoices", "prefetch"],
        queryFn: async () => {
          // This will be handled by the actual page component
          return null;
        },
        staleTime,
      });
      break;
    case "/portal/invoices":
      // Prefetch customer invoices list
      queryClient.prefetchQuery({
        queryKey: ["customer-invoices", "prefetch"],
        queryFn: async () => {
          // This will be handled by the actual page component
          return null;
        },
        staleTime,
      });
      break;
    default:
      // Handle dynamic routes like /invoices/[id] or /customers/[id]
      if (route.startsWith("/invoices/") && route !== "/invoices") {
        const invoiceId = route.split("/invoices/")[1];
        if (invoiceId) {
          queryClient.prefetchQuery({
            queryKey: ["invoices", invoiceId],
            queryFn: () => invoiceService.getInvoiceById(invoiceId),
            staleTime,
          });
        }
      } else if (route.startsWith("/customers/") && route !== "/customers") {
        const customerId = route.split("/customers/")[1];
        if (customerId) {
          queryClient.prefetchQuery({
            queryKey: ["customers", customerId],
            queryFn: () => customerService.getCustomerById(customerId),
            staleTime,
          });
        }
      } else if (route.startsWith("/portal/invoices/") && route !== "/portal/invoices") {
        const invoiceId = route.split("/portal/invoices/")[1];
        if (invoiceId) {
          queryClient.prefetchQuery({
            queryKey: ["invoices", invoiceId],
            queryFn: () => invoiceService.getInvoiceById(invoiceId),
            staleTime,
          });
        }
      }
      break;
  }
}

